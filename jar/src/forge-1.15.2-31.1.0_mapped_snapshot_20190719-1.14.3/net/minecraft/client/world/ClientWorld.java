package net.minecraft.client.world;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.particle.FireworkParticle;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.ColorCache;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.CubeCoordinateIterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.ITickList;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.world.WorldEvent;

@OnlyIn(Dist.CLIENT)
public class ClientWorld extends World {
   private final List<Entity> globalEntities = Lists.newArrayList();
   private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectOpenHashMap();
   private final ClientPlayNetHandler connection;
   private final WorldRenderer worldRenderer;
   private final Minecraft mc = Minecraft.getInstance();
   private final List<AbstractClientPlayerEntity> field_217431_w = Lists.newArrayList();
   private int ambienceTicks;
   private Scoreboard scoreboard;
   private final Map<String, MapData> field_217432_z;
   private int field_228314_A_;
   private final Object2ObjectArrayMap<ColorResolver, ColorCache> field_228315_B_;

   public ClientWorld(ClientPlayNetHandler p_i51056_1_, WorldSettings p_i51056_2_, DimensionType p_i51056_3_, int p_i51056_4_, IProfiler p_i51056_5_, WorldRenderer p_i51056_6_) {
      super(new WorldInfo(p_i51056_2_, "MpServer"), p_i51056_3_, (p_lambda$new$1_1_, p_lambda$new$1_2_) -> {
         return new ClientChunkProvider((ClientWorld)p_lambda$new$1_1_, p_i51056_4_);
      }, p_i51056_5_, true);
      this.ambienceTicks = this.rand.nextInt(12000);
      this.scoreboard = new Scoreboard();
      this.field_217432_z = Maps.newHashMap();
      this.field_228315_B_ = (Object2ObjectArrayMap)Util.make(new Object2ObjectArrayMap(3), (p_lambda$new$0_0_) -> {
         p_lambda$new$0_0_.put(BiomeColors.GRASS_COLOR, new ColorCache());
         p_lambda$new$0_0_.put(BiomeColors.FOLIAGE_COLOR, new ColorCache());
         p_lambda$new$0_0_.put(BiomeColors.WATER_COLOR, new ColorCache());
      });
      this.connection = p_i51056_1_;
      this.worldRenderer = p_i51056_6_;
      this.setSpawnPoint(new BlockPos(8, 64, 8));
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      this.gatherCapabilities(this.dimension.initCapabilities());
      MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(this));
   }

   public void tick(BooleanSupplier p_72835_1_) {
      this.getWorldBorder().tick();
      this.advanceTime();
      this.getProfiler().startSection("blocks");
      this.chunkProvider.tick(p_72835_1_);
      this.func_217426_j();
      this.getProfiler().endSection();
   }

   public Iterable<Entity> getAllEntities() {
      return Iterables.concat(this.entitiesById.values(), this.globalEntities);
   }

   public void tickEntities() {
      IProfiler iprofiler = this.getProfiler();
      iprofiler.startSection("entities");
      iprofiler.startSection("global");

      for(int i = 0; i < this.globalEntities.size(); ++i) {
         Entity entity = (Entity)this.globalEntities.get(i);
         this.func_217390_a((p_lambda$tickEntities$2_0_) -> {
            ++p_lambda$tickEntities$2_0_.ticksExisted;
            if (p_lambda$tickEntities$2_0_.canUpdate()) {
               p_lambda$tickEntities$2_0_.tick();
            }

         }, entity);
         if (entity.removed) {
            this.globalEntities.remove(i--);
         }
      }

      iprofiler.endStartSection("regular");
      ObjectIterator objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();

      while(objectiterator.hasNext()) {
         Entry<Entity> entry = (Entry)objectiterator.next();
         Entity entity1 = (Entity)entry.getValue();
         if (!entity1.isPassenger()) {
            iprofiler.startSection("tick");
            if (!entity1.removed) {
               this.func_217390_a(this::func_217418_a, entity1);
            }

            iprofiler.endSection();
            iprofiler.startSection("remove");
            if (entity1.removed) {
               objectiterator.remove();
               this.removeEntity(entity1);
            }

            iprofiler.endSection();
         }
      }

      iprofiler.endSection();
      this.func_217391_K();
      iprofiler.endSection();
   }

   public void func_217418_a(Entity p_217418_1_) {
      if (p_217418_1_ instanceof PlayerEntity || this.getChunkProvider().isChunkLoaded(p_217418_1_)) {
         p_217418_1_.func_226286_f_(p_217418_1_.func_226277_ct_(), p_217418_1_.func_226278_cu_(), p_217418_1_.func_226281_cx_());
         p_217418_1_.prevRotationYaw = p_217418_1_.rotationYaw;
         p_217418_1_.prevRotationPitch = p_217418_1_.rotationPitch;
         if (p_217418_1_.addedToChunk || p_217418_1_.isSpectator()) {
            ++p_217418_1_.ticksExisted;
            this.getProfiler().startSection(() -> {
               return Registry.ENTITY_TYPE.getKey(p_217418_1_.getType()).toString();
            });
            if (p_217418_1_.canUpdate()) {
               p_217418_1_.tick();
            }

            this.getProfiler().endSection();
         }

         this.func_217423_b(p_217418_1_);
         if (p_217418_1_.addedToChunk) {
            Iterator var2 = p_217418_1_.getPassengers().iterator();

            while(var2.hasNext()) {
               Entity entity = (Entity)var2.next();
               this.func_217420_a(p_217418_1_, entity);
            }
         }
      }

   }

   public void func_217420_a(Entity p_217420_1_, Entity p_217420_2_) {
      if (!p_217420_2_.removed && p_217420_2_.getRidingEntity() == p_217420_1_) {
         if (p_217420_2_ instanceof PlayerEntity || this.getChunkProvider().isChunkLoaded(p_217420_2_)) {
            p_217420_2_.func_226286_f_(p_217420_2_.func_226277_ct_(), p_217420_2_.func_226278_cu_(), p_217420_2_.func_226281_cx_());
            p_217420_2_.prevRotationYaw = p_217420_2_.rotationYaw;
            p_217420_2_.prevRotationPitch = p_217420_2_.rotationPitch;
            if (p_217420_2_.addedToChunk) {
               ++p_217420_2_.ticksExisted;
               p_217420_2_.updateRidden();
            }

            this.func_217423_b(p_217420_2_);
            if (p_217420_2_.addedToChunk) {
               Iterator var3 = p_217420_2_.getPassengers().iterator();

               while(var3.hasNext()) {
                  Entity entity = (Entity)var3.next();
                  this.func_217420_a(p_217420_2_, entity);
               }
            }
         }
      } else {
         p_217420_2_.stopRiding();
      }

   }

   public void func_217423_b(Entity p_217423_1_) {
      this.getProfiler().startSection("chunkCheck");
      int i = MathHelper.floor(p_217423_1_.func_226277_ct_() / 16.0D);
      int j = MathHelper.floor(p_217423_1_.func_226278_cu_() / 16.0D);
      int k = MathHelper.floor(p_217423_1_.func_226281_cx_() / 16.0D);
      if (!p_217423_1_.addedToChunk || p_217423_1_.chunkCoordX != i || p_217423_1_.chunkCoordY != j || p_217423_1_.chunkCoordZ != k) {
         if (p_217423_1_.addedToChunk && this.chunkExists(p_217423_1_.chunkCoordX, p_217423_1_.chunkCoordZ)) {
            this.getChunk(p_217423_1_.chunkCoordX, p_217423_1_.chunkCoordZ).removeEntityAtIndex(p_217423_1_, p_217423_1_.chunkCoordY);
         }

         if (!p_217423_1_.setPositionNonDirty() && !this.chunkExists(i, k)) {
            p_217423_1_.addedToChunk = false;
         } else {
            this.getChunk(i, k).addEntity(p_217423_1_);
         }
      }

      this.getProfiler().endSection();
   }

   public void onChunkUnloaded(Chunk p_217409_1_) {
      this.tileEntitiesToBeRemoved.addAll(p_217409_1_.getTileEntityMap().values());
      this.chunkProvider.getLightManager().func_215571_a(p_217409_1_.getPos(), false);
   }

   public void func_228323_e_(int p_228323_1_, int p_228323_2_) {
      this.field_228315_B_.forEach((p_lambda$func_228323_e_$4_2_, p_lambda$func_228323_e_$4_3_) -> {
         p_lambda$func_228323_e_$4_3_.func_228070_a_(p_228323_1_, p_228323_2_);
      });
   }

   public void func_228327_h_() {
      this.field_228315_B_.forEach((p_lambda$func_228327_h_$5_0_, p_lambda$func_228327_h_$5_1_) -> {
         p_lambda$func_228327_h_$5_1_.func_228069_a_();
      });
   }

   public boolean chunkExists(int p_217354_1_, int p_217354_2_) {
      return true;
   }

   private void func_217426_j() {
      if (this.mc.player != null) {
         if (this.ambienceTicks > 0) {
            --this.ambienceTicks;
         } else {
            BlockPos blockpos = new BlockPos(this.mc.player);
            BlockPos blockpos1 = blockpos.add(4 * (this.rand.nextInt(3) - 1), 4 * (this.rand.nextInt(3) - 1), 4 * (this.rand.nextInt(3) - 1));
            double d0 = blockpos.distanceSq(blockpos1);
            if (d0 >= 4.0D && d0 <= 256.0D) {
               BlockState blockstate = this.getBlockState(blockpos1);
               if (blockstate.isAir() && this.func_226659_b_(blockpos1, 0) <= this.rand.nextInt(8) && this.func_226658_a_(LightType.SKY, blockpos1) <= 0) {
                  this.playSound((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, 0.7F, 0.8F + this.rand.nextFloat() * 0.2F, false);
                  this.ambienceTicks = this.rand.nextInt(12000) + 6000;
               }
            }
         }
      }

   }

   public int func_217425_f() {
      return this.entitiesById.size();
   }

   public void addLightning(LightningBoltEntity p_217410_1_) {
      this.globalEntities.add(p_217410_1_);
   }

   public void addPlayer(int p_217408_1_, AbstractClientPlayerEntity p_217408_2_) {
      this.addEntityImpl(p_217408_1_, p_217408_2_);
      this.field_217431_w.add(p_217408_2_);
   }

   public void addEntity(int p_217411_1_, Entity p_217411_2_) {
      this.addEntityImpl(p_217411_1_, p_217411_2_);
   }

   private void addEntityImpl(int p_217424_1_, Entity p_217424_2_) {
      if (!MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(p_217424_2_, this))) {
         this.removeEntityFromWorld(p_217424_1_);
         this.entitiesById.put(p_217424_1_, p_217424_2_);
         this.getChunkProvider().getChunk(MathHelper.floor(p_217424_2_.func_226277_ct_() / 16.0D), MathHelper.floor(p_217424_2_.func_226281_cx_() / 16.0D), ChunkStatus.FULL, true).addEntity(p_217424_2_);
         p_217424_2_.onAddedToWorld();
      }
   }

   public void removeEntityFromWorld(int p_217413_1_) {
      Entity entity = (Entity)this.entitiesById.remove(p_217413_1_);
      if (entity != null) {
         entity.remove();
         this.removeEntity(entity);
      }

   }

   private void removeEntity(Entity p_217414_1_) {
      p_217414_1_.detach();
      if (p_217414_1_.addedToChunk) {
         this.getChunk(p_217414_1_.chunkCoordX, p_217414_1_.chunkCoordZ).removeEntity(p_217414_1_);
      }

      this.field_217431_w.remove(p_217414_1_);
      p_217414_1_.onRemovedFromWorld();
   }

   public void addEntitiesToChunk(Chunk p_217417_1_) {
      ObjectIterator var2 = this.entitiesById.int2ObjectEntrySet().iterator();

      while(var2.hasNext()) {
         Entry<Entity> entry = (Entry)var2.next();
         Entity entity = (Entity)entry.getValue();
         int i = MathHelper.floor(entity.func_226277_ct_() / 16.0D);
         int j = MathHelper.floor(entity.func_226281_cx_() / 16.0D);
         if (i == p_217417_1_.getPos().x && j == p_217417_1_.getPos().z) {
            p_217417_1_.addEntity(entity);
         }
      }

   }

   @Nullable
   public Entity getEntityByID(int p_73045_1_) {
      return (Entity)this.entitiesById.get(p_73045_1_);
   }

   public void invalidateRegionAndSetBlock(BlockPos p_195597_1_, BlockState p_195597_2_) {
      this.setBlockState(p_195597_1_, p_195597_2_, 19);
   }

   public void sendQuittingDisconnectingPacket() {
      this.connection.getNetworkManager().closeChannel(new TranslationTextComponent("multiplayer.status.quitting", new Object[0]));
   }

   public void animateTick(int p_73029_1_, int p_73029_2_, int p_73029_3_) {
      int i = true;
      Random random = new Random();
      boolean flag = false;
      if (this.mc.playerController.getCurrentGameType() == GameType.CREATIVE) {
         Iterator var7 = this.mc.player.getHeldEquipment().iterator();

         while(var7.hasNext()) {
            ItemStack itemstack = (ItemStack)var7.next();
            if (itemstack.getItem() == Blocks.BARRIER.asItem()) {
               flag = true;
               break;
            }
         }
      }

      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int j = 0; j < 667; ++j) {
         this.animateTick(p_73029_1_, p_73029_2_, p_73029_3_, 16, random, flag, blockpos$mutable);
         this.animateTick(p_73029_1_, p_73029_2_, p_73029_3_, 32, random, flag, blockpos$mutable);
      }

   }

   public void animateTick(int p_184153_1_, int p_184153_2_, int p_184153_3_, int p_184153_4_, Random p_184153_5_, boolean p_184153_6_, BlockPos.Mutable p_184153_7_) {
      int i = p_184153_1_ + this.rand.nextInt(p_184153_4_) - this.rand.nextInt(p_184153_4_);
      int j = p_184153_2_ + this.rand.nextInt(p_184153_4_) - this.rand.nextInt(p_184153_4_);
      int k = p_184153_3_ + this.rand.nextInt(p_184153_4_) - this.rand.nextInt(p_184153_4_);
      p_184153_7_.setPos(i, j, k);
      BlockState blockstate = this.getBlockState(p_184153_7_);
      blockstate.getBlock().animateTick(blockstate, this, p_184153_7_, p_184153_5_);
      IFluidState ifluidstate = this.getFluidState(p_184153_7_);
      if (!ifluidstate.isEmpty()) {
         ifluidstate.animateTick(this, p_184153_7_, p_184153_5_);
         IParticleData iparticledata = ifluidstate.getDripParticleData();
         if (iparticledata != null && this.rand.nextInt(10) == 0) {
            boolean flag = blockstate.func_224755_d(this, p_184153_7_, Direction.DOWN);
            BlockPos blockpos = p_184153_7_.down();
            this.spawnFluidParticle(blockpos, this.getBlockState(blockpos), iparticledata, flag);
         }
      }

      if (p_184153_6_ && blockstate.getBlock() == Blocks.BARRIER) {
         this.addParticle(ParticleTypes.BARRIER, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 0.0D, 0.0D, 0.0D);
      }

   }

   private void spawnFluidParticle(BlockPos p_211530_1_, BlockState p_211530_2_, IParticleData p_211530_3_, boolean p_211530_4_) {
      if (p_211530_2_.getFluidState().isEmpty()) {
         VoxelShape voxelshape = p_211530_2_.getCollisionShape(this, p_211530_1_);
         double d0 = voxelshape.getEnd(Direction.Axis.Y);
         if (d0 < 1.0D) {
            if (p_211530_4_) {
               this.spawnParticle((double)p_211530_1_.getX(), (double)(p_211530_1_.getX() + 1), (double)p_211530_1_.getZ(), (double)(p_211530_1_.getZ() + 1), (double)(p_211530_1_.getY() + 1) - 0.05D, p_211530_3_);
            }
         } else if (!p_211530_2_.isIn(BlockTags.IMPERMEABLE)) {
            double d1 = voxelshape.getStart(Direction.Axis.Y);
            if (d1 > 0.0D) {
               this.spawnParticle(p_211530_1_, p_211530_3_, voxelshape, (double)p_211530_1_.getY() + d1 - 0.05D);
            } else {
               BlockPos blockpos = p_211530_1_.down();
               BlockState blockstate = this.getBlockState(blockpos);
               VoxelShape voxelshape1 = blockstate.getCollisionShape(this, blockpos);
               double d2 = voxelshape1.getEnd(Direction.Axis.Y);
               if (d2 < 1.0D && blockstate.getFluidState().isEmpty()) {
                  this.spawnParticle(p_211530_1_, p_211530_3_, voxelshape, (double)p_211530_1_.getY() - 0.05D);
               }
            }
         }
      }

   }

   private void spawnParticle(BlockPos p_211835_1_, IParticleData p_211835_2_, VoxelShape p_211835_3_, double p_211835_4_) {
      this.spawnParticle((double)p_211835_1_.getX() + p_211835_3_.getStart(Direction.Axis.X), (double)p_211835_1_.getX() + p_211835_3_.getEnd(Direction.Axis.X), (double)p_211835_1_.getZ() + p_211835_3_.getStart(Direction.Axis.Z), (double)p_211835_1_.getZ() + p_211835_3_.getEnd(Direction.Axis.Z), p_211835_4_, p_211835_2_);
   }

   private void spawnParticle(double p_211834_1_, double p_211834_3_, double p_211834_5_, double p_211834_7_, double p_211834_9_, IParticleData p_211834_11_) {
      this.addParticle(p_211834_11_, MathHelper.lerp(this.rand.nextDouble(), p_211834_1_, p_211834_3_), p_211834_9_, MathHelper.lerp(this.rand.nextDouble(), p_211834_5_, p_211834_7_), 0.0D, 0.0D, 0.0D);
   }

   public void removeAllEntities() {
      ObjectIterator objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();

      while(objectiterator.hasNext()) {
         Entry<Entity> entry = (Entry)objectiterator.next();
         Entity entity = (Entity)entry.getValue();
         if (entity.removed) {
            objectiterator.remove();
            this.removeEntity(entity);
         }
      }

   }

   public CrashReportCategory fillCrashReport(CrashReport p_72914_1_) {
      CrashReportCategory crashreportcategory = super.fillCrashReport(p_72914_1_);
      crashreportcategory.addDetail("Server brand", () -> {
         return this.mc.player.getServerBrand();
      });
      crashreportcategory.addDetail("Server type", () -> {
         return this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
      });
      return crashreportcategory;
   }

   public void playSound(@Nullable PlayerEntity p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {
      PlaySoundAtEntityEvent event = ForgeEventFactory.onPlaySoundAtEntity(p_184148_1_, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_);
      if (!event.isCanceled() && event.getSound() != null) {
         p_184148_8_ = event.getSound();
         p_184148_9_ = event.getCategory();
         p_184148_10_ = event.getVolume();
         if (p_184148_1_ == this.mc.player) {
            this.playSound(p_184148_2_, p_184148_4_, p_184148_6_, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_, false);
         }

      }
   }

   public void playMovingSound(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
      PlaySoundAtEntityEvent event = ForgeEventFactory.onPlaySoundAtEntity(p_217384_1_, p_217384_3_, p_217384_4_, p_217384_5_, p_217384_6_);
      if (!event.isCanceled() && event.getSound() != null) {
         p_217384_3_ = event.getSound();
         p_217384_4_ = event.getCategory();
         p_217384_5_ = event.getVolume();
         if (p_217384_1_ == this.mc.player) {
            this.mc.getSoundHandler().play(new EntityTickableSound(p_217384_3_, p_217384_4_, p_217384_2_));
         }

      }
   }

   public void playSound(BlockPos p_184156_1_, SoundEvent p_184156_2_, SoundCategory p_184156_3_, float p_184156_4_, float p_184156_5_, boolean p_184156_6_) {
      this.playSound((double)p_184156_1_.getX() + 0.5D, (double)p_184156_1_.getY() + 0.5D, (double)p_184156_1_.getZ() + 0.5D, p_184156_2_, p_184156_3_, p_184156_4_, p_184156_5_, p_184156_6_);
   }

   public void playSound(double p_184134_1_, double p_184134_3_, double p_184134_5_, SoundEvent p_184134_7_, SoundCategory p_184134_8_, float p_184134_9_, float p_184134_10_, boolean p_184134_11_) {
      double d0 = this.mc.gameRenderer.getActiveRenderInfo().getProjectedView().squareDistanceTo(p_184134_1_, p_184134_3_, p_184134_5_);
      SimpleSound simplesound = new SimpleSound(p_184134_7_, p_184134_8_, p_184134_9_, p_184134_10_, (float)p_184134_1_, (float)p_184134_3_, (float)p_184134_5_);
      if (p_184134_11_ && d0 > 100.0D) {
         double d1 = Math.sqrt(d0) / 40.0D;
         this.mc.getSoundHandler().playDelayed(simplesound, (int)(d1 * 20.0D));
      } else {
         this.mc.getSoundHandler().play(simplesound);
      }

   }

   public void makeFireworks(double p_92088_1_, double p_92088_3_, double p_92088_5_, double p_92088_7_, double p_92088_9_, double p_92088_11_, @Nullable CompoundNBT p_92088_13_) {
      this.mc.particles.addEffect(new FireworkParticle.Starter(this, p_92088_1_, p_92088_3_, p_92088_5_, p_92088_7_, p_92088_9_, p_92088_11_, this.mc.particles, p_92088_13_));
   }

   public void sendPacketToServer(IPacket<?> p_184135_1_) {
      this.connection.sendPacket(p_184135_1_);
   }

   public RecipeManager getRecipeManager() {
      return this.connection.getRecipeManager();
   }

   public void setScoreboard(Scoreboard p_96443_1_) {
      this.scoreboard = p_96443_1_;
   }

   public void setDayTime(long p_72877_1_) {
      if (p_72877_1_ < 0L) {
         p_72877_1_ = -p_72877_1_;
         ((GameRules.BooleanValue)this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE)).set(false, (MinecraftServer)null);
      } else {
         ((GameRules.BooleanValue)this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE)).set(true, (MinecraftServer)null);
      }

      super.setDayTime(p_72877_1_);
   }

   public ITickList<Block> getPendingBlockTicks() {
      return EmptyTickList.get();
   }

   public ITickList<Fluid> getPendingFluidTicks() {
      return EmptyTickList.get();
   }

   public ClientChunkProvider getChunkProvider() {
      return (ClientChunkProvider)super.getChunkProvider();
   }

   @Nullable
   public MapData func_217406_a(String p_217406_1_) {
      return (MapData)this.field_217432_z.get(p_217406_1_);
   }

   public void func_217399_a(MapData p_217399_1_) {
      this.field_217432_z.put(p_217399_1_.getName(), p_217399_1_);
   }

   public int getNextMapId() {
      return 0;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public NetworkTagManager getTags() {
      return this.connection.getTags();
   }

   public void notifyBlockUpdate(BlockPos p_184138_1_, BlockState p_184138_2_, BlockState p_184138_3_, int p_184138_4_) {
      this.worldRenderer.notifyBlockUpdate(this, p_184138_1_, p_184138_2_, p_184138_3_, p_184138_4_);
   }

   public void func_225319_b(BlockPos p_225319_1_, BlockState p_225319_2_, BlockState p_225319_3_) {
      this.worldRenderer.func_224746_a(p_225319_1_, p_225319_2_, p_225319_3_);
   }

   public void markSurroundingsForRerender(int p_217427_1_, int p_217427_2_, int p_217427_3_) {
      this.worldRenderer.markSurroundingsForRerender(p_217427_1_, p_217427_2_, p_217427_3_);
   }

   public void sendBlockBreakProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_) {
      this.worldRenderer.sendBlockBreakProgress(p_175715_1_, p_175715_2_, p_175715_3_);
   }

   public void playBroadcastSound(int p_175669_1_, BlockPos p_175669_2_, int p_175669_3_) {
      this.worldRenderer.broadcastSound(p_175669_1_, p_175669_2_, p_175669_3_);
   }

   public void playEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {
      try {
         this.worldRenderer.playEvent(p_217378_1_, p_217378_2_, p_217378_3_, p_217378_4_);
      } catch (Throwable var8) {
         CrashReport crashreport = CrashReport.makeCrashReport(var8, "Playing level event");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Level event being played");
         crashreportcategory.addDetail("Block coordinates", (Object)CrashReportCategory.getCoordinateInfo(p_217378_3_));
         crashreportcategory.addDetail("Event source", (Object)p_217378_1_);
         crashreportcategory.addDetail("Event type", (Object)p_217378_2_);
         crashreportcategory.addDetail("Event data", (Object)p_217378_4_);
         throw new ReportedException(crashreport);
      }
   }

   public void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
      this.worldRenderer.addParticle(p_195594_1_, p_195594_1_.getType().getAlwaysShow(), p_195594_2_, p_195594_4_, p_195594_6_, p_195594_8_, p_195594_10_, p_195594_12_);
   }

   public void addParticle(IParticleData p_195590_1_, boolean p_195590_2_, double p_195590_3_, double p_195590_5_, double p_195590_7_, double p_195590_9_, double p_195590_11_, double p_195590_13_) {
      this.worldRenderer.addParticle(p_195590_1_, p_195590_1_.getType().getAlwaysShow() || p_195590_2_, p_195590_3_, p_195590_5_, p_195590_7_, p_195590_9_, p_195590_11_, p_195590_13_);
   }

   public void addOptionalParticle(IParticleData p_195589_1_, double p_195589_2_, double p_195589_4_, double p_195589_6_, double p_195589_8_, double p_195589_10_, double p_195589_12_) {
      this.worldRenderer.addParticle(p_195589_1_, false, true, p_195589_2_, p_195589_4_, p_195589_6_, p_195589_8_, p_195589_10_, p_195589_12_);
   }

   public void func_217404_b(IParticleData p_217404_1_, boolean p_217404_2_, double p_217404_3_, double p_217404_5_, double p_217404_7_, double p_217404_9_, double p_217404_11_, double p_217404_13_) {
      this.worldRenderer.addParticle(p_217404_1_, p_217404_1_.getType().getAlwaysShow() || p_217404_2_, true, p_217404_3_, p_217404_5_, p_217404_7_, p_217404_9_, p_217404_11_, p_217404_13_);
   }

   public List<AbstractClientPlayerEntity> getPlayers() {
      return this.field_217431_w;
   }

   public Biome func_225604_a_(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
      return Biomes.PLAINS;
   }

   public float func_228326_g_(float p_228326_1_) {
      float f = this.getCelestialAngle(p_228326_1_);
      float f1 = 1.0F - (MathHelper.cos(f * 6.2831855F) * 2.0F + 0.2F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      f1 = 1.0F - f1;
      f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(p_228326_1_) * 5.0F) / 16.0D));
      f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(p_228326_1_) * 5.0F) / 16.0D));
      return f1 * 0.8F + 0.2F;
   }

   public Vec3d func_228318_a_(BlockPos p_228318_1_, float p_228318_2_) {
      float f = this.getCelestialAngle(p_228318_2_);
      float f1 = MathHelper.cos(f * 6.2831855F) * 2.0F + 0.5F;
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      Biome biome = this.func_226691_t_(p_228318_1_);
      int i = biome.func_225529_c_();
      float f2 = (float)(i >> 16 & 255) / 255.0F;
      float f3 = (float)(i >> 8 & 255) / 255.0F;
      float f4 = (float)(i & 255) / 255.0F;
      f2 *= f1;
      f3 *= f1;
      f4 *= f1;
      float f5 = this.getRainStrength(p_228318_2_);
      float f9;
      float f11;
      if (f5 > 0.0F) {
         f9 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         f11 = 1.0F - f5 * 0.75F;
         f2 = f2 * f11 + f9 * (1.0F - f11);
         f3 = f3 * f11 + f9 * (1.0F - f11);
         f4 = f4 * f11 + f9 * (1.0F - f11);
      }

      f9 = this.getThunderStrength(p_228318_2_);
      if (f9 > 0.0F) {
         f11 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - f9 * 0.75F;
         f2 = f2 * f8 + f11 * (1.0F - f8);
         f3 = f3 * f8 + f11 * (1.0F - f8);
         f4 = f4 * f8 + f11 * (1.0F - f8);
      }

      if (this.field_228314_A_ > 0) {
         f11 = (float)this.field_228314_A_ - p_228318_2_;
         if (f11 > 1.0F) {
            f11 = 1.0F;
         }

         f11 *= 0.45F;
         f2 = f2 * (1.0F - f11) + 0.8F * f11;
         f3 = f3 * (1.0F - f11) + 0.8F * f11;
         f4 = f4 * (1.0F - f11) + 1.0F * f11;
      }

      return new Vec3d((double)f2, (double)f3, (double)f4);
   }

   public Vec3d func_228328_h_(float p_228328_1_) {
      float f = this.getCelestialAngle(p_228328_1_);
      float f1 = MathHelper.cos(f * 6.2831855F) * 2.0F + 0.5F;
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 1.0F;
      float f5 = this.getRainStrength(p_228328_1_);
      float f9;
      float f10;
      if (f5 > 0.0F) {
         f9 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
         f10 = 1.0F - f5 * 0.95F;
         f2 = f2 * f10 + f9 * (1.0F - f10);
         f3 = f3 * f10 + f9 * (1.0F - f10);
         f4 = f4 * f10 + f9 * (1.0F - f10);
      }

      f2 *= f1 * 0.9F + 0.1F;
      f3 *= f1 * 0.9F + 0.1F;
      f4 *= f1 * 0.85F + 0.15F;
      f9 = this.getThunderStrength(p_228328_1_);
      if (f9 > 0.0F) {
         f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
         float f8 = 1.0F - f9 * 0.95F;
         f2 = f2 * f8 + f10 * (1.0F - f8);
         f3 = f3 * f8 + f10 * (1.0F - f8);
         f4 = f4 * f8 + f10 * (1.0F - f8);
      }

      return new Vec3d((double)f2, (double)f3, (double)f4);
   }

   public Vec3d func_228329_i_(float p_228329_1_) {
      float f = this.getCelestialAngle(p_228329_1_);
      return this.dimension.getFogColor(f, p_228329_1_);
   }

   public float func_228330_j_(float p_228330_1_) {
      float f = this.getCelestialAngle(p_228330_1_);
      float f1 = 1.0F - (MathHelper.cos(f * 6.2831855F) * 2.0F + 0.25F);
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      return f1 * f1 * 0.5F;
   }

   public double func_228331_m_() {
      return this.worldInfo.getGenerator() == WorldType.FLAT ? 0.0D : 63.0D;
   }

   public int func_228332_n_() {
      return this.field_228314_A_;
   }

   public void func_225605_c_(int p_225605_1_) {
      this.field_228314_A_ = p_225605_1_;
   }

   public int func_225525_a_(BlockPos p_225525_1_, ColorResolver p_225525_2_) {
      ColorCache colorcache = (ColorCache)this.field_228315_B_.get(p_225525_2_);
      return colorcache.func_228071_a_(p_225525_1_, () -> {
         return this.func_228321_b_(p_225525_1_, p_225525_2_);
      });
   }

   public int func_228321_b_(BlockPos p_228321_1_, ColorResolver p_228321_2_) {
      int i = Minecraft.getInstance().gameSettings.biomeBlendRadius;
      if (i == 0) {
         return p_228321_2_.getColor(this.func_226691_t_(p_228321_1_), (double)p_228321_1_.getX(), (double)p_228321_1_.getZ());
      } else {
         int j = (i * 2 + 1) * (i * 2 + 1);
         int k = 0;
         int l = 0;
         int i1 = 0;
         CubeCoordinateIterator cubecoordinateiterator = new CubeCoordinateIterator(p_228321_1_.getX() - i, p_228321_1_.getY(), p_228321_1_.getZ() - i, p_228321_1_.getX() + i, p_228321_1_.getY(), p_228321_1_.getZ() + i);

         int j1;
         for(BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(); cubecoordinateiterator.hasNext(); i1 += j1 & 255) {
            blockpos$mutable.setPos(cubecoordinateiterator.getX(), cubecoordinateiterator.getY(), cubecoordinateiterator.getZ());
            j1 = p_228321_2_.getColor(this.func_226691_t_(blockpos$mutable), (double)blockpos$mutable.getX(), (double)blockpos$mutable.getZ());
            k += (j1 & 16711680) >> 16;
            l += (j1 & '\uff00') >> 8;
         }

         return (k / j & 255) << 16 | (l / j & 255) << 8 | i1 / j & 255;
      }
   }
}
