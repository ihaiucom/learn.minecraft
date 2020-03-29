package net.minecraft.world.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.merchant.IReputationTracking;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SSpawnGlobalEntityPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Explosion;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapIdTracker;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeWorldServer;
import net.minecraftforge.common.util.WorldCapabilityData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World implements IForgeWorldServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<Entity> globalEntities = Lists.newArrayList();
   private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap();
   private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   private final Queue<Entity> entitiesToAdd = Queues.newArrayDeque();
   private final List<ServerPlayerEntity> players = Lists.newArrayList();
   boolean tickingEntities;
   private final MinecraftServer server;
   private final SaveHandler saveHandler;
   public boolean disableLevelSaving;
   private boolean allPlayersSleeping;
   private int updateEntityTick;
   private final Teleporter worldTeleporter;
   private final ServerTickList<Block> pendingBlockTicks;
   private final ServerTickList<Fluid> pendingFluidTicks;
   private final Set<PathNavigator> navigations;
   protected final RaidManager raids;
   private final ObjectLinkedOpenHashSet<BlockEventData> blockEventQueue;
   private boolean insideTick;
   @Nullable
   private final WanderingTraderSpawner wanderingTraderSpawner;
   protected Set<ChunkPos> doneChunks;
   private WorldCapabilityData capabilityData;

   public ServerWorld(MinecraftServer p_i50703_1_, Executor p_i50703_2_, SaveHandler p_i50703_3_, WorldInfo p_i50703_4_, DimensionType p_i50703_5_, IProfiler p_i50703_6_, IChunkStatusListener p_i50703_7_) {
      super(p_i50703_4_, p_i50703_5_, (p_lambda$new$3_4_, p_lambda$new$3_5_) -> {
         return new ServerChunkProvider((ServerWorld)p_lambda$new$3_4_, p_i50703_3_.getWorldDirectory(), p_i50703_3_.getFixer(), p_i50703_3_.getStructureTemplateManager(), p_i50703_2_, p_lambda$new$3_4_.getWorldType().createChunkGenerator(p_lambda$new$3_4_), p_i50703_1_.getPlayerList().getViewDistance(), p_i50703_7_, () -> {
            return p_i50703_1_.getWorld(DimensionType.OVERWORLD).getSavedData();
         });
      }, p_i50703_6_, false);
      this.pendingBlockTicks = new ServerTickList(this, (p_lambda$new$0_0_) -> {
         return p_lambda$new$0_0_ == null || p_lambda$new$0_0_.getDefaultState().isAir();
      }, Registry.BLOCK::getKey, Registry.BLOCK::getOrDefault, this::tickBlock);
      this.pendingFluidTicks = new ServerTickList(this, (p_lambda$new$1_0_) -> {
         return p_lambda$new$1_0_ == null || p_lambda$new$1_0_ == Fluids.EMPTY;
      }, Registry.FLUID::getKey, Registry.FLUID::getOrDefault, this::tickFluid);
      this.navigations = Sets.newHashSet();
      this.blockEventQueue = new ObjectLinkedOpenHashSet();
      this.doneChunks = Sets.newHashSet();
      this.saveHandler = p_i50703_3_;
      this.server = p_i50703_1_;
      this.worldTeleporter = new Teleporter(this);
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      this.getWorldBorder().setSize(p_i50703_1_.getMaxWorldSize());
      this.raids = (RaidManager)this.getSavedData().getOrCreate(() -> {
         return new RaidManager(this);
      }, RaidManager.func_215172_a(this.dimension));
      if (!p_i50703_1_.isSinglePlayer()) {
         this.getWorldInfo().setGameType(p_i50703_1_.getGameType());
      }

      this.wanderingTraderSpawner = this.dimension.getType() == DimensionType.OVERWORLD ? new WanderingTraderSpawner(this) : null;
      this.initCapabilities();
   }

   public Biome func_225604_a_(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
      return this.getChunkProvider().getChunkGenerator().getBiomeProvider().func_225526_b_(p_225604_1_, p_225604_2_, p_225604_3_);
   }

   public void tick(BooleanSupplier p_72835_1_) {
      IProfiler iprofiler = this.getProfiler();
      this.insideTick = true;
      iprofiler.startSection("world border");
      this.getWorldBorder().tick();
      iprofiler.endStartSection("weather");
      boolean flag = this.isRaining();
      this.dimension.updateWeather(() -> {
         if (this.dimension.hasSkyLight()) {
            if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
               int i = this.worldInfo.getClearWeatherTime();
               int j = this.worldInfo.getThunderTime();
               int k = this.worldInfo.getRainTime();
               boolean flag1 = this.worldInfo.isThundering();
               boolean flag2 = this.worldInfo.isRaining();
               if (i > 0) {
                  --i;
                  j = flag1 ? 0 : 1;
                  k = flag2 ? 0 : 1;
                  flag1 = false;
                  flag2 = false;
               } else {
                  if (j > 0) {
                     --j;
                     if (j == 0) {
                        flag1 = !flag1;
                     }
                  } else if (flag1) {
                     j = this.rand.nextInt(12000) + 3600;
                  } else {
                     j = this.rand.nextInt(168000) + 12000;
                  }

                  if (k > 0) {
                     --k;
                     if (k == 0) {
                        flag2 = !flag2;
                     }
                  } else if (flag2) {
                     k = this.rand.nextInt(12000) + 12000;
                  } else {
                     k = this.rand.nextInt(168000) + 12000;
                  }
               }

               this.worldInfo.setThunderTime(j);
               this.worldInfo.setRainTime(k);
               this.worldInfo.setClearWeatherTime(i);
               this.worldInfo.setThundering(flag1);
               this.worldInfo.setRaining(flag2);
            }

            this.prevThunderingStrength = this.thunderingStrength;
            if (this.worldInfo.isThundering()) {
               this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
            } else {
               this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
            }

            this.thunderingStrength = MathHelper.clamp(this.thunderingStrength, 0.0F, 1.0F);
            this.prevRainingStrength = this.rainingStrength;
            if (this.worldInfo.isRaining()) {
               this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
            } else {
               this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
            }

            this.rainingStrength = MathHelper.clamp(this.rainingStrength, 0.0F, 1.0F);
         }

      });
      if (this.prevRainingStrength != this.rainingStrength) {
         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(7, this.rainingStrength), this.dimension.getType());
      }

      if (this.prevThunderingStrength != this.thunderingStrength) {
         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(8, this.thunderingStrength), this.dimension.getType());
      }

      if (flag != this.isRaining()) {
         if (flag) {
            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(2, 0.0F), this.dimension.getType());
         } else {
            this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(1, 0.0F), this.dimension.getType());
         }

         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(7, this.rainingStrength), this.dimension.getType());
         this.server.getPlayerList().sendPacketToAllPlayersInDimension(new SChangeGameStatePacket(8, this.thunderingStrength), this.dimension.getType());
      }

      if (this.getWorldInfo().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
         this.getWorldInfo().setDifficulty(Difficulty.HARD);
      }

      if (this.allPlayersSleeping && this.players.stream().noneMatch((p_lambda$tick$6_0_) -> {
         return !p_lambda$tick$6_0_.isSpectator() && !p_lambda$tick$6_0_.isPlayerFullyAsleep();
      })) {
         this.allPlayersSleeping = false;
         if (this.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            long l = this.getDayTime() + 24000L;
            this.setDayTime(ForgeEventFactory.onSleepFinished(this, l - l % 24000L, this.getDayTime()));
         }

         this.func_229856_ab_();
         if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
            this.resetRainAndThunder();
         }
      }

      this.calculateInitialSkylight();
      this.advanceTime();
      iprofiler.endStartSection("chunkSource");
      this.getChunkProvider().tick(p_72835_1_);
      iprofiler.endStartSection("tickPending");
      if (this.worldInfo.getGenerator() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.pendingBlockTicks.tick();
         this.pendingFluidTicks.tick();
      }

      iprofiler.endStartSection("raid");
      this.raids.tick();
      if (this.wanderingTraderSpawner != null) {
         this.wanderingTraderSpawner.tick();
      }

      iprofiler.endStartSection("blockEvents");
      this.sendQueuedBlockEvents();
      this.insideTick = false;
      iprofiler.endStartSection("entities");
      boolean flag3 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
      if (flag3) {
         this.resetUpdateEntityTick();
      }

      if (flag3 || this.updateEntityTick++ < 300) {
         this.dimension.tick();
         iprofiler.startSection("global");

         Entity entity2;
         for(int i1 = 0; i1 < this.globalEntities.size(); ++i1) {
            entity2 = (Entity)this.globalEntities.get(i1);
            this.func_217390_a((p_lambda$tick$7_0_) -> {
               ++p_lambda$tick$7_0_.ticksExisted;
               if (p_lambda$tick$7_0_.canUpdate()) {
                  p_lambda$tick$7_0_.tick();
               }

            }, entity2);
            if (entity2.removed) {
               this.globalEntities.remove(i1--);
            }
         }

         iprofiler.endStartSection("regular");
         this.tickingEntities = true;
         ObjectIterator objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();

         label117:
         while(true) {
            while(true) {
               if (!objectiterator.hasNext()) {
                  this.tickingEntities = false;

                  Entity entity1;
                  while((entity1 = (Entity)this.entitiesToAdd.poll()) != null) {
                     this.onEntityAdded(entity1);
                  }

                  iprofiler.endSection();
                  this.func_217391_K();
                  break label117;
               }

               Entry<Entity> entry = (Entry)objectiterator.next();
               entity2 = (Entity)entry.getValue();
               Entity entity3 = entity2.getRidingEntity();
               if (!this.server.getCanSpawnAnimals() && (entity2 instanceof AnimalEntity || entity2 instanceof WaterMobEntity)) {
                  entity2.remove();
               }

               if (!this.server.getCanSpawnNPCs() && entity2 instanceof INPC) {
                  entity2.remove();
               }

               iprofiler.startSection("checkDespawn");
               if (!entity2.removed) {
                  entity2.checkDespawn();
               }

               iprofiler.endSection();
               if (entity3 == null) {
                  break;
               }

               if (entity3.removed || !entity3.isPassenger(entity2)) {
                  entity2.stopRiding();
                  break;
               }
            }

            iprofiler.startSection("tick");
            if (!entity2.removed && !(entity2 instanceof EnderDragonPartEntity)) {
               this.func_217390_a(this::updateEntity, entity2);
            }

            iprofiler.endSection();
            iprofiler.startSection("remove");
            if (entity2.removed) {
               this.removeFromChunk(entity2);
               objectiterator.remove();
               this.removeEntityComplete(entity2, entity2 instanceof ServerPlayerEntity);
            }

            iprofiler.endSection();
         }
      }

      iprofiler.endSection();
   }

   private void func_229856_ab_() {
      ((List)this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList())).forEach((p_lambda$func_229856_ab_$8_0_) -> {
         p_lambda$func_229856_ab_$8_0_.func_225652_a_(false, false);
      });
   }

   public void func_217441_a(Chunk p_217441_1_, int p_217441_2_) {
      ChunkPos chunkpos = p_217441_1_.getPos();
      boolean flag = this.isRaining();
      int i = chunkpos.getXStart();
      int j = chunkpos.getZStart();
      IProfiler iprofiler = this.getProfiler();
      iprofiler.startSection("thunder");
      BlockPos blockpos2;
      if (this.dimension.canDoLightning(p_217441_1_) && flag && this.isThundering() && this.rand.nextInt(100000) == 0) {
         blockpos2 = this.adjustPosToNearbyEntity(this.func_217383_a(i, 0, j, 15));
         if (this.isRainingAt(blockpos2)) {
            DifficultyInstance difficultyinstance = this.getDifficultyForLocation(blockpos2);
            boolean flag1 = this.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && this.rand.nextDouble() < (double)difficultyinstance.getAdditionalDifficulty() * 0.01D;
            if (flag1) {
               SkeletonHorseEntity skeletonhorseentity = (SkeletonHorseEntity)EntityType.SKELETON_HORSE.create(this);
               skeletonhorseentity.setTrap(true);
               skeletonhorseentity.setGrowingAge(0);
               skeletonhorseentity.setPosition((double)blockpos2.getX(), (double)blockpos2.getY(), (double)blockpos2.getZ());
               this.addEntity(skeletonhorseentity);
            }

            this.addLightningBolt(new LightningBoltEntity(this, (double)blockpos2.getX() + 0.5D, (double)blockpos2.getY(), (double)blockpos2.getZ() + 0.5D, flag1));
         }
      }

      iprofiler.endStartSection("iceandsnow");
      if (this.dimension.canDoRainSnowIce(p_217441_1_) && this.rand.nextInt(16) == 0) {
         blockpos2 = this.getHeight(Heightmap.Type.MOTION_BLOCKING, this.func_217383_a(i, 0, j, 15));
         BlockPos blockpos3 = blockpos2.down();
         Biome biome = this.func_226691_t_(blockpos2);
         if (this.isAreaLoaded(blockpos2, 1) && biome.doesWaterFreeze(this, blockpos3)) {
            this.setBlockState(blockpos3, Blocks.ICE.getDefaultState());
         }

         if (flag && biome.doesSnowGenerate(this, blockpos2)) {
            this.setBlockState(blockpos2, Blocks.SNOW.getDefaultState());
         }

         if (flag && this.func_226691_t_(blockpos3).getPrecipitation() == Biome.RainType.RAIN) {
            this.getBlockState(blockpos3).getBlock().fillWithRain(this, blockpos3);
         }
      }

      iprofiler.endStartSection("tickBlocks");
      if (p_217441_2_ > 0) {
         ChunkSection[] var17 = p_217441_1_.getSections();
         int var19 = var17.length;

         for(int var21 = 0; var21 < var19; ++var21) {
            ChunkSection chunksection = var17[var21];
            if (chunksection != Chunk.EMPTY_SECTION && chunksection.needsRandomTickAny()) {
               int k = chunksection.getYLocation();

               for(int l = 0; l < p_217441_2_; ++l) {
                  BlockPos blockpos1 = this.func_217383_a(i, k, j, 15);
                  iprofiler.startSection("randomTick");
                  BlockState blockstate = chunksection.getBlockState(blockpos1.getX() - i, blockpos1.getY() - k, blockpos1.getZ() - j);
                  if (blockstate.ticksRandomly()) {
                     blockstate.func_227034_b_(this, blockpos1, this.rand);
                  }

                  IFluidState ifluidstate = blockstate.getFluidState();
                  if (ifluidstate.ticksRandomly()) {
                     ifluidstate.randomTick(this, blockpos1, this.rand);
                  }

                  iprofiler.endSection();
               }
            }
         }
      }

      iprofiler.endSection();
   }

   protected BlockPos adjustPosToNearbyEntity(BlockPos p_175736_1_) {
      BlockPos blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, p_175736_1_);
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), this.getHeight(), blockpos.getZ()))).grow(3.0D);
      List<LivingEntity> list = this.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb, (p_lambda$adjustPosToNearbyEntity$9_1_) -> {
         return p_lambda$adjustPosToNearbyEntity$9_1_ != null && p_lambda$adjustPosToNearbyEntity$9_1_.isAlive() && this.func_226660_f_(p_lambda$adjustPosToNearbyEntity$9_1_.getPosition());
      });
      if (!list.isEmpty()) {
         return ((LivingEntity)list.get(this.rand.nextInt(list.size()))).getPosition();
      } else {
         if (blockpos.getY() == -1) {
            blockpos = blockpos.up(2);
         }

         return blockpos;
      }
   }

   public boolean isInsideTick() {
      return this.insideTick;
   }

   public void updateAllPlayersSleepingFlag() {
      this.allPlayersSleeping = false;
      if (!this.players.isEmpty()) {
         int i = 0;
         int j = 0;
         Iterator var3 = this.players.iterator();

         while(var3.hasNext()) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var3.next();
            if (serverplayerentity.isSpectator()) {
               ++i;
            } else if (serverplayerentity.isSleeping()) {
               ++j;
            }
         }

         this.allPlayersSleeping = j > 0 && j >= this.players.size() - i;
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.server.getScoreboard();
   }

   private void resetRainAndThunder() {
      this.dimension.resetRainAndThunder();
   }

   @OnlyIn(Dist.CLIENT)
   public void setInitialSpawnLocation() {
      if (this.worldInfo.getSpawnY() <= 0) {
         this.worldInfo.setSpawnY(this.getSeaLevel() + 1);
      }

      int i = this.worldInfo.getSpawnX();
      int j = this.worldInfo.getSpawnZ();
      int k = 0;

      while(this.getGroundAboveSeaLevel(new BlockPos(i, 0, j)).isAir(this, new BlockPos(i, 0, j))) {
         i += this.rand.nextInt(8) - this.rand.nextInt(8);
         j += this.rand.nextInt(8) - this.rand.nextInt(8);
         ++k;
         if (k == 10000) {
            break;
         }
      }

      this.worldInfo.setSpawnX(i);
      this.worldInfo.setSpawnZ(j);
   }

   public void resetUpdateEntityTick() {
      this.updateEntityTick = 0;
   }

   private void tickFluid(NextTickListEntry<Fluid> p_205339_1_) {
      IFluidState ifluidstate = this.getFluidState(p_205339_1_.position);
      if (ifluidstate.getFluid() == p_205339_1_.getTarget()) {
         ifluidstate.tick(this, p_205339_1_.position);
      }

   }

   private void tickBlock(NextTickListEntry<Block> p_205338_1_) {
      BlockState blockstate = this.getBlockState(p_205338_1_.position);
      if (blockstate.getBlock() == p_205338_1_.getTarget()) {
         blockstate.func_227033_a_(this, p_205338_1_.position, this.rand);
      }

   }

   public void updateEntity(Entity p_217479_1_) {
      if (p_217479_1_ instanceof PlayerEntity || this.getChunkProvider().isChunkLoaded(p_217479_1_)) {
         p_217479_1_.func_226286_f_(p_217479_1_.func_226277_ct_(), p_217479_1_.func_226278_cu_(), p_217479_1_.func_226281_cx_());
         p_217479_1_.prevRotationYaw = p_217479_1_.rotationYaw;
         p_217479_1_.prevRotationPitch = p_217479_1_.rotationPitch;
         if (p_217479_1_.addedToChunk) {
            ++p_217479_1_.ticksExisted;
            IProfiler iprofiler = this.getProfiler();
            iprofiler.startSection(() -> {
               return p_217479_1_.getType().getRegistryName() == null ? p_217479_1_.getType().toString() : p_217479_1_.getType().getRegistryName().toString();
            });
            if (p_217479_1_.canUpdate()) {
               iprofiler.func_230035_c_("tickNonPassenger");
            }

            p_217479_1_.tick();
            iprofiler.endSection();
         }

         this.chunkCheck(p_217479_1_);
         if (p_217479_1_.addedToChunk) {
            Iterator var4 = p_217479_1_.getPassengers().iterator();

            while(var4.hasNext()) {
               Entity entity = (Entity)var4.next();
               this.func_217459_a(p_217479_1_, entity);
            }
         }
      }

   }

   public void func_217459_a(Entity p_217459_1_, Entity p_217459_2_) {
      if (!p_217459_2_.removed && p_217459_2_.getRidingEntity() == p_217459_1_) {
         if (p_217459_2_ instanceof PlayerEntity || this.getChunkProvider().isChunkLoaded(p_217459_2_)) {
            p_217459_2_.func_226286_f_(p_217459_2_.func_226277_ct_(), p_217459_2_.func_226278_cu_(), p_217459_2_.func_226281_cx_());
            p_217459_2_.prevRotationYaw = p_217459_2_.rotationYaw;
            p_217459_2_.prevRotationPitch = p_217459_2_.rotationPitch;
            if (p_217459_2_.addedToChunk) {
               ++p_217459_2_.ticksExisted;
               IProfiler iprofiler = this.getProfiler();
               iprofiler.startSection(() -> {
                  return Registry.ENTITY_TYPE.getKey(p_217459_2_.getType()).toString();
               });
               iprofiler.func_230035_c_("tickPassenger");
               p_217459_2_.updateRidden();
               iprofiler.endSection();
            }

            this.chunkCheck(p_217459_2_);
            if (p_217459_2_.addedToChunk) {
               Iterator var5 = p_217459_2_.getPassengers().iterator();

               while(var5.hasNext()) {
                  Entity entity = (Entity)var5.next();
                  this.func_217459_a(p_217459_2_, entity);
               }
            }
         }
      } else {
         p_217459_2_.stopRiding();
      }

   }

   public void chunkCheck(Entity p_217464_1_) {
      this.getProfiler().startSection("chunkCheck");
      int i = MathHelper.floor(p_217464_1_.func_226277_ct_() / 16.0D);
      int j = MathHelper.floor(p_217464_1_.func_226278_cu_() / 16.0D);
      int k = MathHelper.floor(p_217464_1_.func_226281_cx_() / 16.0D);
      if (!p_217464_1_.addedToChunk || p_217464_1_.chunkCoordX != i || p_217464_1_.chunkCoordY != j || p_217464_1_.chunkCoordZ != k) {
         if (p_217464_1_.addedToChunk && this.chunkExists(p_217464_1_.chunkCoordX, p_217464_1_.chunkCoordZ)) {
            this.getChunk(p_217464_1_.chunkCoordX, p_217464_1_.chunkCoordZ).removeEntityAtIndex(p_217464_1_, p_217464_1_.chunkCoordY);
         }

         if (!p_217464_1_.setPositionNonDirty() && !this.chunkExists(i, k)) {
            p_217464_1_.addedToChunk = false;
         } else {
            this.getChunk(i, k).addEntity(p_217464_1_);
         }
      }

      this.getProfiler().endSection();
   }

   public boolean isBlockModifiable(PlayerEntity p_175660_1_, BlockPos p_175660_2_) {
      return super.isBlockModifiable(p_175660_1_, p_175660_2_);
   }

   public boolean canMineBlockBody(PlayerEntity p_canMineBlockBody_1_, BlockPos p_canMineBlockBody_2_) {
      return !this.server.isBlockProtected(this, p_canMineBlockBody_2_, p_canMineBlockBody_1_) && this.getWorldBorder().contains(p_canMineBlockBody_2_);
   }

   public void createSpawnPosition(WorldSettings p_73052_1_) {
      if (!this.dimension.canRespawnHere()) {
         this.worldInfo.setSpawn(BlockPos.ZERO.up(this.getChunkProvider().getChunkGenerator().getGroundHeight()));
      } else if (this.worldInfo.getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.worldInfo.setSpawn(BlockPos.ZERO.up());
      } else {
         if (ForgeEventFactory.onCreateWorldSpawn(this, p_73052_1_)) {
            return;
         }

         BiomeProvider biomeprovider = this.getChunkProvider().getChunkGenerator().getBiomeProvider();
         List<Biome> list = biomeprovider.getBiomesToSpawnIn();
         Random random = new Random(this.getSeed());
         BlockPos blockpos = biomeprovider.func_225531_a_(0, this.getSeaLevel(), 0, 256, list, random);
         ChunkPos chunkpos = blockpos == null ? new ChunkPos(0, 0) : new ChunkPos(blockpos);
         if (blockpos == null) {
            LOGGER.warn("Unable to find spawn biome");
         }

         boolean flag = false;
         Iterator var8 = BlockTags.VALID_SPAWN.getAllElements().iterator();

         while(var8.hasNext()) {
            Block block = (Block)var8.next();
            if (biomeprovider.getSurfaceBlocks().contains(block.getDefaultState())) {
               flag = true;
               break;
            }
         }

         this.worldInfo.setSpawn(chunkpos.asBlockPos().add(8, this.getChunkProvider().getChunkGenerator().getGroundHeight(), 8));
         int i1 = 0;
         int j1 = 0;
         int i = 0;
         int j = -1;
         int k = true;

         for(int l = 0; l < 1024; ++l) {
            if (i1 > -16 && i1 <= 16 && j1 > -16 && j1 <= 16) {
               BlockPos blockpos1 = this.dimension.findSpawn(new ChunkPos(chunkpos.x + i1, chunkpos.z + j1), flag);
               if (blockpos1 != null) {
                  this.worldInfo.setSpawn(blockpos1);
                  break;
               }
            }

            if (i1 == j1 || i1 < 0 && i1 == -j1 || i1 > 0 && i1 == 1 - j1) {
               int k1 = i;
               i = -j;
               j = k1;
            }

            i1 += i;
            j1 += j;
         }

         if (p_73052_1_.isBonusChestEnabled()) {
            this.createBonusChest();
         }
      }

   }

   protected void createBonusChest() {
      ConfiguredFeature<?, ?> configuredfeature = Feature.BONUS_CHEST.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG);
      configuredfeature.place(this, this.getChunkProvider().getChunkGenerator(), this.rand, new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ()));
   }

   @Nullable
   public BlockPos getSpawnCoordinate() {
      return this.dimension.getSpawnCoordinate();
   }

   public void save(@Nullable IProgressUpdate p_217445_1_, boolean p_217445_2_, boolean p_217445_3_) throws SessionLockException {
      ServerChunkProvider serverchunkprovider = this.getChunkProvider();
      if (!p_217445_3_) {
         if (p_217445_1_ != null) {
            p_217445_1_.displaySavingString(new TranslationTextComponent("menu.savingLevel", new Object[0]));
         }

         this.saveLevel();
         if (p_217445_1_ != null) {
            p_217445_1_.displayLoadingString(new TranslationTextComponent("menu.savingChunks", new Object[0]));
         }

         MinecraftForge.EVENT_BUS.post(new WorldEvent.Save(this));
         serverchunkprovider.save(p_217445_2_);
      }

   }

   protected void saveLevel() throws SessionLockException {
      this.checkSessionLock();
      this.dimension.onWorldSave();
      this.getChunkProvider().getSavedData().save();
   }

   public List<Entity> getEntities(@Nullable EntityType<?> p_217482_1_, Predicate<? super Entity> p_217482_2_) {
      List<Entity> list = Lists.newArrayList();
      ServerChunkProvider serverchunkprovider = this.getChunkProvider();
      ObjectIterator var5 = this.entitiesById.values().iterator();

      while(true) {
         Entity entity;
         do {
            if (!var5.hasNext()) {
               return list;
            }

            entity = (Entity)var5.next();
         } while(p_217482_1_ != null && entity.getType() != p_217482_1_);

         if (serverchunkprovider.chunkExists(MathHelper.floor(entity.func_226277_ct_()) >> 4, MathHelper.floor(entity.func_226281_cx_()) >> 4) && p_217482_2_.test(entity)) {
            list.add(entity);
         }
      }
   }

   public List<EnderDragonEntity> getDragons() {
      List<EnderDragonEntity> list = Lists.newArrayList();
      ObjectIterator var2 = this.entitiesById.values().iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         if (entity instanceof EnderDragonEntity && entity.isAlive()) {
            list.add((EnderDragonEntity)entity);
         }
      }

      return list;
   }

   public List<ServerPlayerEntity> getPlayers(Predicate<? super ServerPlayerEntity> p_217490_1_) {
      List<ServerPlayerEntity> list = Lists.newArrayList();
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var3.next();
         if (p_217490_1_.test(serverplayerentity)) {
            list.add(serverplayerentity);
         }
      }

      return list;
   }

   @Nullable
   public ServerPlayerEntity getRandomPlayer() {
      List<ServerPlayerEntity> list = this.getPlayers(LivingEntity::isAlive);
      return list.isEmpty() ? null : (ServerPlayerEntity)list.get(this.rand.nextInt(list.size()));
   }

   public Object2IntMap<EntityClassification> countEntities() {
      Object2IntMap<EntityClassification> object2intmap = new Object2IntOpenHashMap();
      ObjectIterator objectiterator = this.entitiesById.values().iterator();

      while(true) {
         Entity entity;
         MobEntity mobentity;
         do {
            if (!objectiterator.hasNext()) {
               return object2intmap;
            }

            entity = (Entity)objectiterator.next();
            if (!(entity instanceof MobEntity)) {
               break;
            }

            mobentity = (MobEntity)entity;
         } while(mobentity.isNoDespawnRequired() || mobentity.preventDespawn());

         EntityClassification entityclassification = entity.getClassification(true);
         if (entityclassification != EntityClassification.MISC && this.getChunkProvider().func_223435_b(entity)) {
            object2intmap.mergeInt(entityclassification, 1, Integer::sum);
         }
      }
   }

   public boolean addEntity(Entity p_217376_1_) {
      return this.addEntity0(p_217376_1_);
   }

   public boolean summonEntity(Entity p_217470_1_) {
      return this.addEntity0(p_217470_1_);
   }

   public void func_217460_e(Entity p_217460_1_) {
      boolean flag = p_217460_1_.forceSpawn;
      p_217460_1_.forceSpawn = true;
      this.summonEntity(p_217460_1_);
      p_217460_1_.forceSpawn = flag;
      this.chunkCheck(p_217460_1_);
   }

   public void func_217446_a(ServerPlayerEntity p_217446_1_) {
      this.addPlayer(p_217446_1_);
      this.chunkCheck(p_217446_1_);
   }

   public void func_217447_b(ServerPlayerEntity p_217447_1_) {
      this.addPlayer(p_217447_1_);
      this.chunkCheck(p_217447_1_);
   }

   public void addNewPlayer(ServerPlayerEntity p_217435_1_) {
      this.addPlayer(p_217435_1_);
   }

   public void addRespawnedPlayer(ServerPlayerEntity p_217433_1_) {
      this.addPlayer(p_217433_1_);
   }

   private void addPlayer(ServerPlayerEntity p_217448_1_) {
      if (!MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(p_217448_1_, this))) {
         Entity entity = (Entity)this.entitiesByUuid.get(p_217448_1_.getUniqueID());
         if (entity != null) {
            LOGGER.warn("Force-added player with duplicate UUID {}", p_217448_1_.getUniqueID().toString());
            entity.detach();
            this.removePlayer((ServerPlayerEntity)entity);
         }

         this.players.add(p_217448_1_);
         this.updateAllPlayersSleepingFlag();
         IChunk ichunk = this.getChunk(MathHelper.floor(p_217448_1_.func_226277_ct_() / 16.0D), MathHelper.floor(p_217448_1_.func_226281_cx_() / 16.0D), ChunkStatus.FULL, true);
         if (ichunk instanceof Chunk) {
            ichunk.addEntity(p_217448_1_);
         }

         this.onEntityAdded(p_217448_1_);
      }
   }

   private boolean addEntity0(Entity p_72838_1_) {
      if (p_72838_1_.removed) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getKey(p_72838_1_.getType()));
         return false;
      } else if (this.hasDuplicateEntity(p_72838_1_)) {
         return false;
      } else if (MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(p_72838_1_, this))) {
         return false;
      } else {
         IChunk ichunk = this.getChunk(MathHelper.floor(p_72838_1_.func_226277_ct_() / 16.0D), MathHelper.floor(p_72838_1_.func_226281_cx_() / 16.0D), ChunkStatus.FULL, p_72838_1_.forceSpawn);
         if (!(ichunk instanceof Chunk)) {
            return false;
         } else {
            ichunk.addEntity(p_72838_1_);
            this.onEntityAdded(p_72838_1_);
            return true;
         }
      }
   }

   public boolean addEntityIfNotDuplicate(Entity p_217440_1_) {
      if (this.hasDuplicateEntity(p_217440_1_)) {
         return false;
      } else if (MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(p_217440_1_, this))) {
         return false;
      } else {
         this.onEntityAdded(p_217440_1_);
         return true;
      }
   }

   private boolean hasDuplicateEntity(Entity p_217478_1_) {
      Entity entity = (Entity)this.entitiesByUuid.get(p_217478_1_.getUniqueID());
      if (entity == null) {
         return false;
      } else {
         LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getKey(entity.getType()), p_217478_1_.getUniqueID().toString());
         return true;
      }
   }

   public void onChunkUnloading(Chunk p_217466_1_) {
      this.tileEntitiesToBeRemoved.addAll(p_217466_1_.getTileEntityMap().values());
      ClassInheritanceMultiMap<Entity>[] aclassinheritancemultimap = p_217466_1_.getEntityLists();
      int i = aclassinheritancemultimap.length;

      for(int j = 0; j < i; ++j) {
         Iterator var5 = aclassinheritancemultimap[j].iterator();

         while(var5.hasNext()) {
            Entity entity = (Entity)var5.next();
            if (!(entity instanceof ServerPlayerEntity)) {
               if (this.tickingEntities) {
                  throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("Removing entity while ticking!"));
               }

               this.entitiesById.remove(entity.getEntityId());
               this.onEntityRemoved(entity);
            }
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public void onEntityRemoved(Entity p_217484_1_) {
      this.removeEntityComplete(p_217484_1_, false);
   }

   public void removeEntityComplete(Entity p_removeEntityComplete_1_, boolean p_removeEntityComplete_2_) {
      if (p_removeEntityComplete_1_ instanceof EnderDragonEntity) {
         EnderDragonPartEntity[] var3 = ((EnderDragonEntity)p_removeEntityComplete_1_).func_213404_dT();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EnderDragonPartEntity enderdragonpartentity = var3[var5];
            enderdragonpartentity.remove(p_removeEntityComplete_2_);
         }
      }

      p_removeEntityComplete_1_.remove(p_removeEntityComplete_2_);
      this.entitiesByUuid.remove(p_removeEntityComplete_1_.getUniqueID());
      this.getChunkProvider().untrack(p_removeEntityComplete_1_);
      if (p_removeEntityComplete_1_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_removeEntityComplete_1_;
         this.players.remove(serverplayerentity);
      }

      this.getScoreboard().removeEntity(p_removeEntityComplete_1_);
      if (p_removeEntityComplete_1_ instanceof MobEntity) {
         this.navigations.remove(((MobEntity)p_removeEntityComplete_1_).getNavigator());
      }

      p_removeEntityComplete_1_.onRemovedFromWorld();
   }

   private void onEntityAdded(Entity p_217465_1_) {
      if (this.tickingEntities) {
         this.entitiesToAdd.add(p_217465_1_);
      } else {
         this.entitiesById.put(p_217465_1_.getEntityId(), p_217465_1_);
         if (p_217465_1_ instanceof EnderDragonEntity) {
            EnderDragonPartEntity[] var2 = ((EnderDragonEntity)p_217465_1_).func_213404_dT();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               EnderDragonPartEntity enderdragonpartentity = var2[var4];
               this.entitiesById.put(enderdragonpartentity.getEntityId(), enderdragonpartentity);
            }
         }

         this.entitiesByUuid.put(p_217465_1_.getUniqueID(), p_217465_1_);
         this.getChunkProvider().track(p_217465_1_);
         if (p_217465_1_ instanceof MobEntity) {
            this.navigations.add(((MobEntity)p_217465_1_).getNavigator());
         }
      }

      p_217465_1_.onAddedToWorld();
   }

   public void removeEntity(Entity p_217467_1_) {
      this.removeEntity(p_217467_1_, false);
   }

   public void removeEntity(Entity p_removeEntity_1_, boolean p_removeEntity_2_) {
      if (this.tickingEntities) {
         throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("Removing entity while ticking!"));
      } else {
         this.removeFromChunk(p_removeEntity_1_);
         this.entitiesById.remove(p_removeEntity_1_.getEntityId());
         this.removeEntityComplete(p_removeEntity_1_, p_removeEntity_2_);
      }
   }

   private void removeFromChunk(Entity p_217454_1_) {
      IChunk ichunk = this.getChunk(p_217454_1_.chunkCoordX, p_217454_1_.chunkCoordZ, ChunkStatus.FULL, false);
      if (ichunk instanceof Chunk) {
         ((Chunk)ichunk).removeEntity(p_217454_1_);
      }

   }

   public void removePlayer(ServerPlayerEntity p_217434_1_) {
      this.removePlayer(p_217434_1_, false);
   }

   public void removePlayer(ServerPlayerEntity p_removePlayer_1_, boolean p_removePlayer_2_) {
      p_removePlayer_1_.remove(p_removePlayer_2_);
      this.removeEntity(p_removePlayer_1_, p_removePlayer_2_);
      this.updateAllPlayersSleepingFlag();
   }

   public void addLightningBolt(LightningBoltEntity p_217468_1_) {
      this.globalEntities.add(p_217468_1_);
      this.server.getPlayerList().sendToAllNearExcept((PlayerEntity)null, p_217468_1_.func_226277_ct_(), p_217468_1_.func_226278_cu_(), p_217468_1_.func_226281_cx_(), 512.0D, this.dimension.getType(), new SSpawnGlobalEntityPacket(p_217468_1_));
   }

   public void sendBlockBreakProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_) {
      Iterator var4 = this.server.getPlayerList().getPlayers().iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var4.next();
         if (serverplayerentity != null && serverplayerentity.world == this && serverplayerentity.getEntityId() != p_175715_1_) {
            double d0 = (double)p_175715_2_.getX() - serverplayerentity.func_226277_ct_();
            double d1 = (double)p_175715_2_.getY() - serverplayerentity.func_226278_cu_();
            double d2 = (double)p_175715_2_.getZ() - serverplayerentity.func_226281_cx_();
            if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
               serverplayerentity.connection.sendPacket(new SAnimateBlockBreakPacket(p_175715_1_, p_175715_2_, p_175715_3_));
            }
         }
      }

   }

   public void playSound(@Nullable PlayerEntity p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {
      PlaySoundAtEntityEvent event = ForgeEventFactory.onPlaySoundAtEntity(p_184148_1_, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_);
      if (!event.isCanceled() && event.getSound() != null) {
         p_184148_8_ = event.getSound();
         p_184148_9_ = event.getCategory();
         p_184148_10_ = event.getVolume();
         this.server.getPlayerList().sendToAllNearExcept(p_184148_1_, p_184148_2_, p_184148_4_, p_184148_6_, p_184148_10_ > 1.0F ? (double)(16.0F * p_184148_10_) : 16.0D, this.dimension.getType(), new SPlaySoundEffectPacket(p_184148_8_, p_184148_9_, p_184148_2_, p_184148_4_, p_184148_6_, p_184148_10_, p_184148_11_));
      }
   }

   public void playMovingSound(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
      PlaySoundAtEntityEvent event = ForgeEventFactory.onPlaySoundAtEntity(p_217384_1_, p_217384_3_, p_217384_4_, p_217384_5_, p_217384_6_);
      if (!event.isCanceled() && event.getSound() != null) {
         p_217384_3_ = event.getSound();
         p_217384_4_ = event.getCategory();
         p_217384_5_ = event.getVolume();
         this.server.getPlayerList().sendToAllNearExcept(p_217384_1_, p_217384_2_.func_226277_ct_(), p_217384_2_.func_226278_cu_(), p_217384_2_.func_226281_cx_(), p_217384_5_ > 1.0F ? (double)(16.0F * p_217384_5_) : 16.0D, this.dimension.getType(), new SSpawnMovingSoundEffectPacket(p_217384_3_, p_217384_4_, p_217384_2_, p_217384_5_, p_217384_6_));
      }
   }

   public void playBroadcastSound(int p_175669_1_, BlockPos p_175669_2_, int p_175669_3_) {
      this.server.getPlayerList().sendPacketToAllPlayers(new SPlaySoundEventPacket(p_175669_1_, p_175669_2_, p_175669_3_, true));
   }

   public void playEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {
      this.server.getPlayerList().sendToAllNearExcept(p_217378_1_, (double)p_217378_3_.getX(), (double)p_217378_3_.getY(), (double)p_217378_3_.getZ(), 64.0D, this.dimension.getType(), new SPlaySoundEventPacket(p_217378_2_, p_217378_3_, p_217378_4_, false));
   }

   public void notifyBlockUpdate(BlockPos p_184138_1_, BlockState p_184138_2_, BlockState p_184138_3_, int p_184138_4_) {
      this.getChunkProvider().markBlockChanged(p_184138_1_);
      VoxelShape voxelshape = p_184138_2_.getCollisionShape(this, p_184138_1_);
      VoxelShape voxelshape1 = p_184138_3_.getCollisionShape(this, p_184138_1_);
      if (VoxelShapes.compare(voxelshape, voxelshape1, IBooleanFunction.NOT_SAME)) {
         Iterator var7 = this.navigations.iterator();

         while(var7.hasNext()) {
            PathNavigator pathnavigator = (PathNavigator)var7.next();
            if (!pathnavigator.canUpdatePathOnTimeout()) {
               pathnavigator.func_220970_c(p_184138_1_);
            }
         }
      }

   }

   public void setEntityState(Entity p_72960_1_, byte p_72960_2_) {
      this.getChunkProvider().sendToTrackingAndSelf(p_72960_1_, new SEntityStatusPacket(p_72960_1_, p_72960_2_));
   }

   public ServerChunkProvider getChunkProvider() {
      return (ServerChunkProvider)super.getChunkProvider();
   }

   public Explosion createExplosion(@Nullable Entity p_217401_1_, @Nullable DamageSource p_217401_2_, double p_217401_3_, double p_217401_5_, double p_217401_7_, float p_217401_9_, boolean p_217401_10_, Explosion.Mode p_217401_11_) {
      Explosion explosion = new Explosion(this, p_217401_1_, p_217401_3_, p_217401_5_, p_217401_7_, p_217401_9_, p_217401_10_, p_217401_11_);
      if (ForgeEventFactory.onExplosionStart(this, explosion)) {
         return explosion;
      } else {
         if (p_217401_2_ != null) {
            explosion.setDamageSource(p_217401_2_);
         }

         explosion.doExplosionA();
         explosion.doExplosionB(false);
         if (p_217401_11_ == Explosion.Mode.NONE) {
            explosion.clearAffectedBlockPositions();
         }

         Iterator var13 = this.players.iterator();

         while(var13.hasNext()) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var13.next();
            if (serverplayerentity.getDistanceSq(p_217401_3_, p_217401_5_, p_217401_7_) < 4096.0D) {
               serverplayerentity.connection.sendPacket(new SExplosionPacket(p_217401_3_, p_217401_5_, p_217401_7_, p_217401_9_, explosion.getAffectedBlockPositions(), (Vec3d)explosion.getPlayerKnockbackMap().get(serverplayerentity)));
            }
         }

         return explosion;
      }
   }

   public void addBlockEvent(BlockPos p_175641_1_, Block p_175641_2_, int p_175641_3_, int p_175641_4_) {
      this.blockEventQueue.add(new BlockEventData(p_175641_1_, p_175641_2_, p_175641_3_, p_175641_4_));
   }

   private void sendQueuedBlockEvents() {
      while(!this.blockEventQueue.isEmpty()) {
         BlockEventData blockeventdata = (BlockEventData)this.blockEventQueue.removeFirst();
         if (this.fireBlockEvent(blockeventdata)) {
            this.server.getPlayerList().sendToAllNearExcept((PlayerEntity)null, (double)blockeventdata.getPosition().getX(), (double)blockeventdata.getPosition().getY(), (double)blockeventdata.getPosition().getZ(), 64.0D, this.dimension.getType(), new SBlockActionPacket(blockeventdata.getPosition(), blockeventdata.getBlock(), blockeventdata.getEventID(), blockeventdata.getEventParameter()));
         }
      }

   }

   private boolean fireBlockEvent(BlockEventData p_147485_1_) {
      BlockState blockstate = this.getBlockState(p_147485_1_.getPosition());
      return blockstate.getBlock() == p_147485_1_.getBlock() ? blockstate.onBlockEventReceived(this, p_147485_1_.getPosition(), p_147485_1_.getEventID(), p_147485_1_.getEventParameter()) : false;
   }

   public ServerTickList<Block> getPendingBlockTicks() {
      return this.pendingBlockTicks;
   }

   public ServerTickList<Fluid> getPendingFluidTicks() {
      return this.pendingFluidTicks;
   }

   @Nonnull
   public MinecraftServer getServer() {
      return this.server;
   }

   public Teleporter getDefaultTeleporter() {
      return this.worldTeleporter;
   }

   public TemplateManager getStructureTemplateManager() {
      return this.saveHandler.getStructureTemplateManager();
   }

   public <T extends IParticleData> int spawnParticle(T p_195598_1_, double p_195598_2_, double p_195598_4_, double p_195598_6_, int p_195598_8_, double p_195598_9_, double p_195598_11_, double p_195598_13_, double p_195598_15_) {
      SSpawnParticlePacket sspawnparticlepacket = new SSpawnParticlePacket(p_195598_1_, false, p_195598_2_, p_195598_4_, p_195598_6_, (float)p_195598_9_, (float)p_195598_11_, (float)p_195598_13_, (float)p_195598_15_, p_195598_8_);
      int i = 0;

      for(int j = 0; j < this.players.size(); ++j) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)this.players.get(j);
         if (this.sendPacketWithinDistance(serverplayerentity, false, p_195598_2_, p_195598_4_, p_195598_6_, sspawnparticlepacket)) {
            ++i;
         }
      }

      return i;
   }

   public <T extends IParticleData> boolean spawnParticle(ServerPlayerEntity p_195600_1_, T p_195600_2_, boolean p_195600_3_, double p_195600_4_, double p_195600_6_, double p_195600_8_, int p_195600_10_, double p_195600_11_, double p_195600_13_, double p_195600_15_, double p_195600_17_) {
      IPacket<?> ipacket = new SSpawnParticlePacket(p_195600_2_, p_195600_3_, p_195600_4_, p_195600_6_, p_195600_8_, (float)p_195600_11_, (float)p_195600_13_, (float)p_195600_15_, (float)p_195600_17_, p_195600_10_);
      return this.sendPacketWithinDistance(p_195600_1_, p_195600_3_, p_195600_4_, p_195600_6_, p_195600_8_, ipacket);
   }

   private boolean sendPacketWithinDistance(ServerPlayerEntity p_195601_1_, boolean p_195601_2_, double p_195601_3_, double p_195601_5_, double p_195601_7_, IPacket<?> p_195601_9_) {
      if (p_195601_1_.getServerWorld() != this) {
         return false;
      } else {
         BlockPos blockpos = p_195601_1_.getPosition();
         if (blockpos.withinDistance(new Vec3d(p_195601_3_, p_195601_5_, p_195601_7_), p_195601_2_ ? 512.0D : 32.0D)) {
            p_195601_1_.connection.sendPacket(p_195601_9_);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getEntityByID(int p_73045_1_) {
      return (Entity)this.entitiesById.get(p_73045_1_);
   }

   @Nullable
   public Entity getEntityByUuid(UUID p_217461_1_) {
      return (Entity)this.entitiesByUuid.get(p_217461_1_);
   }

   @Nullable
   public BlockPos findNearestStructure(String p_211157_1_, BlockPos p_211157_2_, int p_211157_3_, boolean p_211157_4_) {
      return this.getChunkProvider().getChunkGenerator().findNearestStructure(this, p_211157_1_, p_211157_2_, p_211157_3_, p_211157_4_);
   }

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public NetworkTagManager getTags() {
      return this.server.getNetworkTagManager();
   }

   public void setGameTime(long p_82738_1_) {
      super.setGameTime(p_82738_1_);
      this.worldInfo.getScheduledEvents().run(this.server, p_82738_1_);
   }

   public boolean isSaveDisabled() {
      return this.disableLevelSaving;
   }

   public void checkSessionLock() throws SessionLockException {
      this.saveHandler.checkSessionLock();
   }

   public SaveHandler getSaveHandler() {
      return this.saveHandler;
   }

   public DimensionSavedDataManager getSavedData() {
      return this.getChunkProvider().getSavedData();
   }

   @Nullable
   public MapData func_217406_a(String p_217406_1_) {
      return (MapData)this.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().get(() -> {
         return new MapData(p_217406_1_);
      }, p_217406_1_);
   }

   public void func_217399_a(MapData p_217399_1_) {
      this.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().set(p_217399_1_);
   }

   public int getNextMapId() {
      return ((MapIdTracker)this.getServer().getWorld(DimensionType.OVERWORLD).getSavedData().getOrCreate(MapIdTracker::new, "idcounts")).func_215162_a();
   }

   public void setSpawnPoint(BlockPos p_175652_1_) {
      ChunkPos chunkpos = new ChunkPos(new BlockPos(this.worldInfo.getSpawnX(), 0, this.worldInfo.getSpawnZ()));
      super.setSpawnPoint(p_175652_1_);
      this.getChunkProvider().func_217222_b(TicketType.START, chunkpos, 11, Unit.INSTANCE);
      this.getChunkProvider().func_217228_a(TicketType.START, new ChunkPos(p_175652_1_), 11, Unit.INSTANCE);
   }

   public LongSet getForcedChunks() {
      ForcedChunksSaveData forcedchunkssavedata = (ForcedChunksSaveData)this.getSavedData().get(ForcedChunksSaveData::new, "chunks");
      return (LongSet)(forcedchunkssavedata != null ? LongSets.unmodifiable(forcedchunkssavedata.getChunks()) : LongSets.EMPTY_SET);
   }

   public boolean forceChunk(int p_217458_1_, int p_217458_2_, boolean p_217458_3_) {
      ForcedChunksSaveData forcedchunkssavedata = (ForcedChunksSaveData)this.getSavedData().getOrCreate(ForcedChunksSaveData::new, "chunks");
      ChunkPos chunkpos = new ChunkPos(p_217458_1_, p_217458_2_);
      long i = chunkpos.asLong();
      boolean flag;
      if (p_217458_3_) {
         flag = forcedchunkssavedata.getChunks().add(i);
         if (flag) {
            this.getChunk(p_217458_1_, p_217458_2_);
         }
      } else {
         flag = forcedchunkssavedata.getChunks().remove(i);
      }

      forcedchunkssavedata.setDirty(flag);
      if (flag) {
         this.getChunkProvider().forceChunk(chunkpos, p_217458_3_);
      }

      return flag;
   }

   public List<ServerPlayerEntity> getPlayers() {
      return this.players;
   }

   public void func_217393_a(BlockPos p_217393_1_, BlockState p_217393_2_, BlockState p_217393_3_) {
      Optional<PointOfInterestType> optional = PointOfInterestType.forState(p_217393_2_);
      Optional<PointOfInterestType> optional1 = PointOfInterestType.forState(p_217393_3_);
      if (!Objects.equals(optional, optional1)) {
         BlockPos blockpos = p_217393_1_.toImmutable();
         optional.ifPresent((p_lambda$func_217393_a$14_2_) -> {
            this.getServer().execute(() -> {
               this.func_217443_B().func_219140_a(blockpos);
               DebugPacketSender.func_218805_b(this, blockpos);
            });
         });
         optional1.ifPresent((p_lambda$func_217393_a$16_2_) -> {
            this.getServer().execute(() -> {
               this.func_217443_B().func_219135_a(blockpos, p_lambda$func_217393_a$16_2_);
               DebugPacketSender.func_218799_a(this, blockpos);
            });
         });
      }

   }

   public PointOfInterestManager func_217443_B() {
      return this.getChunkProvider().func_217231_i();
   }

   public boolean func_217483_b_(BlockPos p_217483_1_) {
      return this.func_217471_a(p_217483_1_, 1);
   }

   public boolean func_222887_a(SectionPos p_222887_1_) {
      return this.func_217483_b_(p_222887_1_.getCenter());
   }

   public boolean func_217471_a(BlockPos p_217471_1_, int p_217471_2_) {
      if (p_217471_2_ > 6) {
         return false;
      } else {
         return this.func_217486_a(SectionPos.from(p_217471_1_)) <= p_217471_2_;
      }
   }

   public int func_217486_a(SectionPos p_217486_1_) {
      return this.func_217443_B().func_219150_a(p_217486_1_);
   }

   public RaidManager getRaids() {
      return this.raids;
   }

   @Nullable
   public Raid findRaid(BlockPos p_217475_1_) {
      return this.raids.findRaid(p_217475_1_, 9216);
   }

   public boolean hasRaid(BlockPos p_217455_1_) {
      return this.findRaid(p_217455_1_) != null;
   }

   public void func_217489_a(IReputationType p_217489_1_, Entity p_217489_2_, IReputationTracking p_217489_3_) {
      p_217489_3_.func_213739_a(p_217489_1_, p_217489_2_);
   }

   public void func_225322_a(Path p_225322_1_) throws IOException {
      ChunkManager chunkmanager = this.getChunkProvider().chunkManager;
      Writer writer = Files.newBufferedWriter(p_225322_1_.resolve("stats.txt"));
      Throwable var4 = null;

      try {
         writer.write(String.format("spawning_chunks: %d\n", chunkmanager.func_219246_e().func_219358_b()));
         ObjectIterator var5 = this.countEntities().object2IntEntrySet().iterator();

         while(true) {
            if (!var5.hasNext()) {
               writer.write(String.format("entities: %d\n", this.entitiesById.size()));
               writer.write(String.format("block_entities: %d\n", this.loadedTileEntityList.size()));
               writer.write(String.format("block_ticks: %d\n", this.getPendingBlockTicks().func_225420_a()));
               writer.write(String.format("fluid_ticks: %d\n", this.getPendingFluidTicks().func_225420_a()));
               writer.write("distance_manager: " + chunkmanager.func_219246_e().func_225412_c() + "\n");
               writer.write(String.format("pending_tasks: %d\n", this.getChunkProvider().func_225314_f()));
               break;
            }

            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<EntityClassification> entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var5.next();
            writer.write(String.format("spawn_count.%s: %d\n", ((EntityClassification)entry.getKey()).func_220363_a(), entry.getIntValue()));
         }
      } catch (Throwable var164) {
         var4 = var164;
         throw var164;
      } finally {
         if (writer != null) {
            if (var4 != null) {
               try {
                  writer.close();
               } catch (Throwable var150) {
                  var4.addSuppressed(var150);
               }
            } else {
               writer.close();
            }
         }

      }

      CrashReport crashreport = new CrashReport("Level dump", new Exception("dummy"));
      this.fillCrashReport(crashreport);
      Writer writer1 = Files.newBufferedWriter(p_225322_1_.resolve("example_crash.txt"));
      Throwable var168 = null;

      try {
         writer1.write(crashreport.getCompleteReport());
      } catch (Throwable var158) {
         var168 = var158;
         throw var158;
      } finally {
         if (writer1 != null) {
            if (var168 != null) {
               try {
                  writer1.close();
               } catch (Throwable var151) {
                  var168.addSuppressed(var151);
               }
            } else {
               writer1.close();
            }
         }

      }

      Path path = p_225322_1_.resolve("chunks.csv");
      Writer writer2 = Files.newBufferedWriter(path);
      Throwable var171 = null;

      try {
         chunkmanager.func_225406_a(writer2);
      } catch (Throwable var157) {
         var171 = var157;
         throw var157;
      } finally {
         if (writer2 != null) {
            if (var171 != null) {
               try {
                  writer2.close();
               } catch (Throwable var148) {
                  var171.addSuppressed(var148);
               }
            } else {
               writer2.close();
            }
         }

      }

      Path path1 = p_225322_1_.resolve("entities.csv");
      Writer writer3 = Files.newBufferedWriter(path1);
      Throwable var7 = null;

      try {
         func_225320_a(writer3, this.entitiesById.values());
      } catch (Throwable var156) {
         var7 = var156;
         throw var156;
      } finally {
         if (writer3 != null) {
            if (var7 != null) {
               try {
                  writer3.close();
               } catch (Throwable var152) {
                  var7.addSuppressed(var152);
               }
            } else {
               writer3.close();
            }
         }

      }

      Path path2 = p_225322_1_.resolve("global_entities.csv");
      Writer writer4 = Files.newBufferedWriter(path2);
      Throwable var8 = null;

      try {
         func_225320_a(writer4, this.globalEntities);
      } catch (Throwable var155) {
         var8 = var155;
         throw var155;
      } finally {
         if (writer4 != null) {
            if (var8 != null) {
               try {
                  writer4.close();
               } catch (Throwable var153) {
                  var8.addSuppressed(var153);
               }
            } else {
               writer4.close();
            }
         }

      }

      Path path3 = p_225322_1_.resolve("block_entities.csv");
      Writer writer5 = Files.newBufferedWriter(path3);
      Throwable var9 = null;

      try {
         this.func_225321_a(writer5);
      } catch (Throwable var154) {
         var9 = var154;
         throw var154;
      } finally {
         if (writer5 != null) {
            if (var9 != null) {
               try {
                  writer5.close();
               } catch (Throwable var149) {
                  var9.addSuppressed(var149);
               }
            } else {
               writer5.close();
            }
         }

      }

   }

   private static void func_225320_a(Writer p_225320_0_, Iterable<Entity> p_225320_1_) throws IOException {
      CSVWriter csvwriter = CSVWriter.func_225428_a().func_225423_a("x").func_225423_a("y").func_225423_a("z").func_225423_a("uuid").func_225423_a("type").func_225423_a("alive").func_225423_a("display_name").func_225423_a("custom_name").func_225422_a(p_225320_0_);
      Iterator var3 = p_225320_1_.iterator();

      while(var3.hasNext()) {
         Entity entity = (Entity)var3.next();
         ITextComponent itextcomponent = entity.getCustomName();
         ITextComponent itextcomponent1 = entity.getDisplayName();
         csvwriter.func_225426_a(entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), entity.getUniqueID(), Registry.ENTITY_TYPE.getKey(entity.getType()), entity.isAlive(), itextcomponent1.getString(), itextcomponent != null ? itextcomponent.getString() : null);
      }

   }

   private void func_225321_a(Writer p_225321_1_) throws IOException {
      CSVWriter csvwriter = CSVWriter.func_225428_a().func_225423_a("x").func_225423_a("y").func_225423_a("z").func_225423_a("type").func_225422_a(p_225321_1_);
      Iterator var3 = this.loadedTileEntityList.iterator();

      while(var3.hasNext()) {
         TileEntity tileentity = (TileEntity)var3.next();
         BlockPos blockpos = tileentity.getPos();
         csvwriter.func_225426_a(blockpos.getX(), blockpos.getY(), blockpos.getZ(), Registry.BLOCK_ENTITY_TYPE.getKey(tileentity.getType()));
      }

   }

   @VisibleForTesting
   public void func_229854_a_(MutableBoundingBox p_229854_1_) {
      this.blockEventQueue.removeIf((p_lambda$func_229854_a_$17_1_) -> {
         return p_229854_1_.isVecInside(p_lambda$func_229854_a_$17_1_.getPosition());
      });
   }

   protected void initCapabilities() {
      ICapabilityProvider parent = this.dimension.initCapabilities();
      this.gatherCapabilities(parent);
      this.capabilityData = (WorldCapabilityData)this.getSavedData().getOrCreate(() -> {
         return new WorldCapabilityData(this.getCapabilities());
      }, "capabilities");
      this.capabilityData.setCapabilities(this.dimension, this.getCapabilities());
   }

   public Stream<Entity> getEntities() {
      return this.entitiesById.values().stream();
   }
}
