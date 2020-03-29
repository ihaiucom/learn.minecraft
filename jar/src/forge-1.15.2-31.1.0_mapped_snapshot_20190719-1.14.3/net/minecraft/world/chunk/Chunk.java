package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.ITickList;
import net.minecraft.world.SerializableTickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeChunk;
import net.minecraftforge.event.entity.EntityEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk extends CapabilityProvider<Chunk> implements IChunk, IForgeChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ChunkSection EMPTY_SECTION = null;
   private final ChunkSection[] sections;
   private BiomeContainer blockBiomeArray;
   private final Map<BlockPos, CompoundNBT> deferredTileEntities;
   private boolean loaded;
   private final World world;
   private final Map<Heightmap.Type, Heightmap> heightMap;
   private final UpgradeData upgradeData;
   private final Map<BlockPos, TileEntity> tileEntities;
   private final ClassInheritanceMultiMap<Entity>[] entityLists;
   private final Map<String, StructureStart> structureStarts;
   private final Map<String, LongSet> structureReferences;
   private final ShortList[] packedBlockPositions;
   private ITickList<Block> blocksToBeTicked;
   private ITickList<Fluid> fluidsToBeTicked;
   private boolean hasEntities;
   private long lastSaveTime;
   private volatile boolean dirty;
   private long inhabitedTime;
   @Nullable
   private Supplier<ChunkHolder.LocationType> field_217329_u;
   @Nullable
   private Consumer<Chunk> field_217330_v;
   private final ChunkPos pos;
   private volatile boolean field_217331_x;

   public Chunk(World p_i225780_1_, ChunkPos p_i225780_2_, BiomeContainer p_i225780_3_) {
      this(p_i225780_1_, p_i225780_2_, p_i225780_3_, UpgradeData.EMPTY, EmptyTickList.get(), EmptyTickList.get(), 0L, (ChunkSection[])null, (Consumer)null);
   }

   public Chunk(World p_i225781_1_, ChunkPos p_i225781_2_, BiomeContainer p_i225781_3_, UpgradeData p_i225781_4_, ITickList<Block> p_i225781_5_, ITickList<Fluid> p_i225781_6_, long p_i225781_7_, @Nullable ChunkSection[] p_i225781_9_, @Nullable Consumer<Chunk> p_i225781_10_) {
      super(Chunk.class);
      this.sections = new ChunkSection[16];
      this.deferredTileEntities = Maps.newHashMap();
      this.heightMap = Maps.newEnumMap(Heightmap.Type.class);
      this.tileEntities = Maps.newHashMap();
      this.structureStarts = Maps.newHashMap();
      this.structureReferences = Maps.newHashMap();
      this.packedBlockPositions = new ShortList[16];
      this.entityLists = new ClassInheritanceMultiMap[16];
      this.world = p_i225781_1_;
      this.pos = p_i225781_2_;
      this.upgradeData = p_i225781_4_;
      Heightmap.Type[] var11 = Heightmap.Type.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         Heightmap.Type heightmap$type = var11[var13];
         if (ChunkStatus.FULL.getHeightMaps().contains(heightmap$type)) {
            this.heightMap.put(heightmap$type, new Heightmap(this, heightmap$type));
         }
      }

      for(int i = 0; i < this.entityLists.length; ++i) {
         this.entityLists[i] = new ClassInheritanceMultiMap(Entity.class);
      }

      this.blockBiomeArray = p_i225781_3_;
      this.blocksToBeTicked = p_i225781_5_;
      this.fluidsToBeTicked = p_i225781_6_;
      this.inhabitedTime = p_i225781_7_;
      this.field_217330_v = p_i225781_10_;
      if (p_i225781_9_ != null) {
         if (this.sections.length == p_i225781_9_.length) {
            System.arraycopy(p_i225781_9_, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", p_i225781_9_.length, this.sections.length);
         }
      }

      this.gatherCapabilities();
   }

   public Chunk(World p_i49947_1_, ChunkPrimer p_i49947_2_) {
      this(p_i49947_1_, p_i49947_2_.getPos(), p_i49947_2_.func_225549_i_(), p_i49947_2_.getUpgradeData(), p_i49947_2_.getBlocksToBeTicked(), p_i49947_2_.getFluidsToBeTicked(), p_i49947_2_.getInhabitedTime(), p_i49947_2_.getSections(), (Consumer)null);
      Iterator var3 = p_i49947_2_.getEntities().iterator();

      while(var3.hasNext()) {
         CompoundNBT compoundnbt = (CompoundNBT)var3.next();
         EntityType.func_220335_a(compoundnbt, p_i49947_1_, (p_lambda$new$0_1_) -> {
            this.addEntity(p_lambda$new$0_1_);
            return p_lambda$new$0_1_;
         });
      }

      var3 = p_i49947_2_.getTileEntities().values().iterator();

      while(var3.hasNext()) {
         TileEntity tileentity = (TileEntity)var3.next();
         this.addTileEntity(tileentity);
      }

      this.deferredTileEntities.putAll(p_i49947_2_.getDeferredTileEntities());

      for(int i = 0; i < p_i49947_2_.getPackedPositions().length; ++i) {
         this.packedBlockPositions[i] = p_i49947_2_.getPackedPositions()[i];
      }

      this.setStructureStarts(p_i49947_2_.getStructureStarts());
      this.setStructureReferences(p_i49947_2_.getStructureReferences());
      var3 = p_i49947_2_.func_217311_f().iterator();

      while(var3.hasNext()) {
         Entry<Heightmap.Type, Heightmap> entry = (Entry)var3.next();
         if (ChunkStatus.FULL.getHeightMaps().contains(entry.getKey())) {
            this.func_217303_b((Heightmap.Type)entry.getKey()).setDataArray(((Heightmap)entry.getValue()).getDataArray());
         }
      }

      this.setLight(p_i49947_2_.hasLight());
      this.dirty = true;
   }

   public Heightmap func_217303_b(Heightmap.Type p_217303_1_) {
      return (Heightmap)this.heightMap.computeIfAbsent(p_217303_1_, (p_lambda$func_217303_b$1_1_) -> {
         return new Heightmap(this, p_lambda$func_217303_b$1_1_);
      });
   }

   public Set<BlockPos> getTileEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
      set.addAll(this.tileEntities.keySet());
      return set;
   }

   public ChunkSection[] getSections() {
      return this.sections;
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      int i = p_180495_1_.getX();
      int j = p_180495_1_.getY();
      int k = p_180495_1_.getZ();
      if (this.world.getWorldType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         BlockState blockstate = null;
         if (j == 60) {
            blockstate = Blocks.BARRIER.getDefaultState();
         }

         if (j == 70) {
            blockstate = DebugChunkGenerator.getBlockStateFor(i, k);
         }

         return blockstate == null ? Blocks.AIR.getDefaultState() : blockstate;
      } else {
         try {
            if (j >= 0 && j >> 4 < this.sections.length) {
               ChunkSection chunksection = this.sections[j >> 4];
               if (!ChunkSection.isEmpty(chunksection)) {
                  return chunksection.getBlockState(i & 15, j & 15, k & 15);
               }
            }

            return Blocks.AIR.getDefaultState();
         } catch (Throwable var8) {
            CrashReport crashreport = CrashReport.makeCrashReport(var8, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
            crashreportcategory.addDetail("Location", () -> {
               return CrashReportCategory.getCoordinateInfo(i, j, k);
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      return this.getFluidState(p_204610_1_.getX(), p_204610_1_.getY(), p_204610_1_.getZ());
   }

   public IFluidState getFluidState(int p_205751_1_, int p_205751_2_, int p_205751_3_) {
      try {
         if (p_205751_2_ >= 0 && p_205751_2_ >> 4 < this.sections.length) {
            ChunkSection chunksection = this.sections[p_205751_2_ >> 4];
            if (!ChunkSection.isEmpty(chunksection)) {
               return chunksection.getFluidState(p_205751_1_ & 15, p_205751_2_ & 15, p_205751_3_ & 15);
            }
         }

         return Fluids.EMPTY.getDefaultState();
      } catch (Throwable var7) {
         CrashReport crashreport = CrashReport.makeCrashReport(var7, "Getting fluid state");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
         crashreportcategory.addDetail("Location", () -> {
            return CrashReportCategory.getCoordinateInfo(p_205751_1_, p_205751_2_, p_205751_3_);
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_) {
      int i = p_177436_1_.getX() & 15;
      int j = p_177436_1_.getY();
      int k = p_177436_1_.getZ() & 15;
      ChunkSection chunksection = this.sections[j >> 4];
      if (chunksection == EMPTY_SECTION) {
         if (p_177436_2_.isAir()) {
            return null;
         }

         chunksection = new ChunkSection(j >> 4 << 4);
         this.sections[j >> 4] = chunksection;
      }

      boolean flag = chunksection.isEmpty();
      BlockState blockstate = chunksection.setBlockState(i, j & 15, k, p_177436_2_);
      if (blockstate == p_177436_2_) {
         return null;
      } else {
         Block block = p_177436_2_.getBlock();
         Block block1 = blockstate.getBlock();
         ((Heightmap)this.heightMap.get(Heightmap.Type.MOTION_BLOCKING)).update(i, j, k, p_177436_2_);
         ((Heightmap)this.heightMap.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).update(i, j, k, p_177436_2_);
         ((Heightmap)this.heightMap.get(Heightmap.Type.OCEAN_FLOOR)).update(i, j, k, p_177436_2_);
         ((Heightmap)this.heightMap.get(Heightmap.Type.WORLD_SURFACE)).update(i, j, k, p_177436_2_);
         boolean flag1 = chunksection.isEmpty();
         if (flag != flag1) {
            this.world.getChunkProvider().getLightManager().func_215567_a(p_177436_1_, flag1);
         }

         if (!this.world.isRemote) {
            blockstate.onReplaced(this.world, p_177436_1_, p_177436_2_, p_177436_3_);
         } else if (block1 != block && blockstate.hasTileEntity()) {
            this.world.removeTileEntity(p_177436_1_);
         }

         if (chunksection.getBlockState(i, j & 15, k).getBlock() != block) {
            return null;
         } else {
            TileEntity tileentity1;
            if (blockstate.hasTileEntity()) {
               tileentity1 = this.getTileEntity(p_177436_1_, Chunk.CreateEntityType.CHECK);
               if (tileentity1 != null) {
                  tileentity1.updateContainingBlockInfo();
               }
            }

            if (!this.world.isRemote) {
               p_177436_2_.onBlockAdded(this.world, p_177436_1_, blockstate, p_177436_3_);
            }

            if (p_177436_2_.hasTileEntity()) {
               tileentity1 = this.getTileEntity(p_177436_1_, Chunk.CreateEntityType.CHECK);
               if (tileentity1 == null) {
                  tileentity1 = p_177436_2_.createTileEntity(this.world);
                  this.world.setTileEntity(p_177436_1_, tileentity1);
               } else {
                  tileentity1.updateContainingBlockInfo();
               }
            }

            this.dirty = true;
            return blockstate;
         }
      }
   }

   @Nullable
   public WorldLightManager getWorldLightManager() {
      return this.world.getChunkProvider().getLightManager();
   }

   public void addEntity(Entity p_76612_1_) {
      this.hasEntities = true;
      int i = MathHelper.floor(p_76612_1_.func_226277_ct_() / 16.0D);
      int j = MathHelper.floor(p_76612_1_.func_226281_cx_() / 16.0D);
      if (i != this.pos.x || j != this.pos.z) {
         LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.pos.x, this.pos.z, p_76612_1_);
         p_76612_1_.removed = true;
      }

      int k = MathHelper.floor(p_76612_1_.func_226278_cu_() / 16.0D);
      if (k < 0) {
         k = 0;
      }

      if (k >= this.entityLists.length) {
         k = this.entityLists.length - 1;
      }

      MinecraftForge.EVENT_BUS.post(new EntityEvent.EnteringChunk(p_76612_1_, this.pos.x, this.pos.z, p_76612_1_.chunkCoordX, p_76612_1_.chunkCoordZ));
      p_76612_1_.addedToChunk = true;
      p_76612_1_.chunkCoordX = this.pos.x;
      p_76612_1_.chunkCoordY = k;
      p_76612_1_.chunkCoordZ = this.pos.z;
      this.entityLists[k].add(p_76612_1_);
      this.markDirty();
   }

   public void setHeightmap(Heightmap.Type p_201607_1_, long[] p_201607_2_) {
      ((Heightmap)this.heightMap.get(p_201607_1_)).setDataArray(p_201607_2_);
   }

   public void removeEntity(Entity p_76622_1_) {
      this.removeEntityAtIndex(p_76622_1_, p_76622_1_.chunkCoordY);
   }

   public void removeEntityAtIndex(Entity p_76608_1_, int p_76608_2_) {
      if (p_76608_2_ < 0) {
         p_76608_2_ = 0;
      }

      if (p_76608_2_ >= this.entityLists.length) {
         p_76608_2_ = this.entityLists.length - 1;
      }

      this.entityLists[p_76608_2_].remove(p_76608_1_);
      this.markDirty();
   }

   public int getTopBlockY(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
      return ((Heightmap)this.heightMap.get(p_201576_1_)).getHeight(p_201576_2_ & 15, p_201576_3_ & 15) - 1;
   }

   @Nullable
   private TileEntity createNewTileEntity(BlockPos p_177422_1_) {
      BlockState blockstate = this.getBlockState(p_177422_1_);
      Block block = blockstate.getBlock();
      return !blockstate.hasTileEntity() ? null : blockstate.createTileEntity(this.world);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      return this.getTileEntity(p_175625_1_, Chunk.CreateEntityType.CHECK);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_177424_1_, Chunk.CreateEntityType p_177424_2_) {
      TileEntity tileentity = (TileEntity)this.tileEntities.get(p_177424_1_);
      if (tileentity != null && tileentity.isRemoved()) {
         this.tileEntities.remove(p_177424_1_);
         tileentity = null;
      }

      if (tileentity == null) {
         CompoundNBT compoundnbt = (CompoundNBT)this.deferredTileEntities.remove(p_177424_1_);
         if (compoundnbt != null) {
            TileEntity tileentity1 = this.setDeferredTileEntity(p_177424_1_, compoundnbt);
            if (tileentity1 != null) {
               return tileentity1;
            }
         }
      }

      if (tileentity == null && p_177424_2_ == Chunk.CreateEntityType.IMMEDIATE) {
         tileentity = this.createNewTileEntity(p_177424_1_);
         this.world.setTileEntity(p_177424_1_, tileentity);
      }

      return tileentity;
   }

   public void addTileEntity(TileEntity p_150813_1_) {
      this.addTileEntity(p_150813_1_.getPos(), p_150813_1_);
      if (this.loaded || this.world.isRemote()) {
         this.world.setTileEntity(p_150813_1_.getPos(), p_150813_1_);
      }

   }

   public void addTileEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
      if (this.getBlockState(p_177426_1_).hasTileEntity()) {
         p_177426_2_.func_226984_a_(this.world, p_177426_1_);
         p_177426_2_.validate();
         TileEntity tileentity = (TileEntity)this.tileEntities.put(p_177426_1_.toImmutable(), p_177426_2_);
         if (tileentity != null && tileentity != p_177426_2_) {
            tileentity.remove();
         }
      }

   }

   public void addTileEntity(CompoundNBT p_201591_1_) {
      this.deferredTileEntities.put(new BlockPos(p_201591_1_.getInt("x"), p_201591_1_.getInt("y"), p_201591_1_.getInt("z")), p_201591_1_);
   }

   @Nullable
   public CompoundNBT func_223134_j(BlockPos p_223134_1_) {
      TileEntity tileentity = this.getTileEntity(p_223134_1_);
      CompoundNBT compoundnbt;
      if (tileentity != null && !tileentity.isRemoved()) {
         try {
            compoundnbt = tileentity.write(new CompoundNBT());
            compoundnbt.putBoolean("keepPacked", false);
            return compoundnbt;
         } catch (Exception var4) {
            LogManager.getLogger().error("A TileEntity type {} has thrown an exception trying to write state. It will not persist, Report this to the mod author", tileentity.getClass().getName(), var4);
            return null;
         }
      } else {
         compoundnbt = (CompoundNBT)this.deferredTileEntities.get(p_223134_1_);
         if (compoundnbt != null) {
            compoundnbt = compoundnbt.copy();
            compoundnbt.putBoolean("keepPacked", true);
         }

         return compoundnbt;
      }
   }

   public void removeTileEntity(BlockPos p_177425_1_) {
      if (this.loaded || this.world.isRemote()) {
         TileEntity tileentity = (TileEntity)this.tileEntities.remove(p_177425_1_);
         if (tileentity != null) {
            tileentity.remove();
         }
      }

   }

   public void func_217318_w() {
      if (this.field_217330_v != null) {
         this.field_217330_v.accept(this);
         this.field_217330_v = null;
      }

   }

   public void markDirty() {
      this.dirty = true;
   }

   public void getEntitiesWithinAABBForEntity(@Nullable Entity p_177414_1_, AxisAlignedBB p_177414_2_, List<Entity> p_177414_3_, @Nullable Predicate<? super Entity> p_177414_4_) {
      int i = MathHelper.floor((p_177414_2_.minY - this.world.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((p_177414_2_.maxY + this.world.getMaxEntityRadius()) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      label67:
      for(int k = i; k <= j; ++k) {
         if (!this.entityLists[k].isEmpty()) {
            Iterator var8 = this.entityLists[k].iterator();

            while(true) {
               Entity entity;
               do {
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label67;
                        }

                        entity = (Entity)var8.next();
                     } while(!entity.getBoundingBox().intersects(p_177414_2_));
                  } while(entity == p_177414_1_);

                  if (p_177414_4_ == null || p_177414_4_.test(entity)) {
                     p_177414_3_.add(entity);
                  }
               } while(!(entity instanceof EnderDragonEntity));

               EnderDragonPartEntity[] var10 = ((EnderDragonEntity)entity).func_213404_dT();
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  EnderDragonPartEntity enderdragonpartentity = var10[var12];
                  if (enderdragonpartentity != p_177414_1_ && enderdragonpartentity.getBoundingBox().intersects(p_177414_2_) && (p_177414_4_ == null || p_177414_4_.test(enderdragonpartentity))) {
                     p_177414_3_.add(enderdragonpartentity);
                  }
               }
            }
         }
      }

   }

   public <T extends Entity> void func_217313_a(@Nullable EntityType<?> p_217313_1_, AxisAlignedBB p_217313_2_, List<? super T> p_217313_3_, Predicate<? super T> p_217313_4_) {
      int i = MathHelper.floor((p_217313_2_.minY - this.world.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((p_217313_2_.maxY + this.world.getMaxEntityRadius()) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      label32:
      for(int k = i; k <= j; ++k) {
         Iterator var8 = this.entityLists[k].func_219790_a(Entity.class).iterator();

         while(true) {
            Entity entity;
            do {
               if (!var8.hasNext()) {
                  continue label32;
               }

               entity = (Entity)var8.next();
            } while(p_217313_1_ != null && entity.getType() != p_217313_1_);

            if (entity.getBoundingBox().intersects(p_217313_2_) && p_217313_4_.test(entity)) {
               p_217313_3_.add(entity);
            }
         }
      }

   }

   public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> p_177430_1_, AxisAlignedBB p_177430_2_, List<T> p_177430_3_, @Nullable Predicate<? super T> p_177430_4_) {
      int i = MathHelper.floor((p_177430_2_.minY - this.world.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((p_177430_2_.maxY + this.world.getMaxEntityRadius()) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      label33:
      for(int k = i; k <= j; ++k) {
         Iterator var8 = this.entityLists[k].func_219790_a(p_177430_1_).iterator();

         while(true) {
            Entity t;
            do {
               do {
                  if (!var8.hasNext()) {
                     continue label33;
                  }

                  t = (Entity)var8.next();
               } while(!t.getBoundingBox().intersects(p_177430_2_));
            } while(p_177430_4_ != null && !p_177430_4_.test(t));

            p_177430_3_.add(t);
         }
      }

   }

   public boolean isEmpty() {
      return false;
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_227073_a_(@Nullable BiomeContainer p_227073_1_, PacketBuffer p_227073_2_, CompoundNBT p_227073_3_, int p_227073_4_) {
      boolean flag = p_227073_1_ != null;
      Predicate<BlockPos> predicate = flag ? (p_lambda$func_227073_a_$4_0_) -> {
         return true;
      } : (p_lambda$func_227073_a_$5_1_) -> {
         return (p_227073_4_ & 1 << (p_lambda$func_227073_a_$5_1_.getY() >> 4)) != 0;
      };
      Stream var10000 = Sets.newHashSet(this.tileEntities.keySet()).stream().filter(predicate);
      World var10001 = this.world;
      var10000.forEach(var10001::removeTileEntity);
      Iterator var7 = this.tileEntities.values().iterator();

      TileEntity tileentity;
      while(var7.hasNext()) {
         tileentity = (TileEntity)var7.next();
         tileentity.updateContainingBlockInfo();
         tileentity.getBlockState();
      }

      for(int i = 0; i < this.sections.length; ++i) {
         ChunkSection chunksection = this.sections[i];
         if ((p_227073_4_ & 1 << i) == 0) {
            if (flag && chunksection != EMPTY_SECTION) {
               this.sections[i] = EMPTY_SECTION;
            }
         } else {
            if (chunksection == EMPTY_SECTION) {
               chunksection = new ChunkSection(i << 4);
               this.sections[i] = chunksection;
            }

            chunksection.read(p_227073_2_);
         }
      }

      if (p_227073_1_ != null) {
         this.blockBiomeArray = p_227073_1_;
      }

      Heightmap.Type[] var13 = Heightmap.Type.values();
      int var15 = var13.length;

      for(int var9 = 0; var9 < var15; ++var9) {
         Heightmap.Type heightmap$type = var13[var9];
         String s = heightmap$type.getId();
         if (p_227073_3_.contains(s, 12)) {
            this.setHeightmap(heightmap$type, p_227073_3_.getLongArray(s));
         }
      }

      var7 = this.tileEntities.values().iterator();

      while(var7.hasNext()) {
         tileentity = (TileEntity)var7.next();
         tileentity.updateContainingBlockInfo();
      }

   }

   public BiomeContainer func_225549_i_() {
      return this.blockBiomeArray;
   }

   public void setLoaded(boolean p_177417_1_) {
      this.loaded = p_177417_1_;
   }

   public World getWorld() {
      return this.world;
   }

   public Collection<Entry<Heightmap.Type, Heightmap>> func_217311_f() {
      return Collections.unmodifiableSet(this.heightMap.entrySet());
   }

   public Map<BlockPos, TileEntity> getTileEntityMap() {
      return this.tileEntities;
   }

   public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
      return this.entityLists;
   }

   public CompoundNBT getDeferredTileEntity(BlockPos p_201579_1_) {
      return (CompoundNBT)this.deferredTileEntities.get(p_201579_1_);
   }

   public Stream<BlockPos> func_217304_m() {
      return StreamSupport.stream(BlockPos.getAllInBoxMutable(this.pos.getXStart(), 0, this.pos.getZStart(), this.pos.getXEnd(), 255, this.pos.getZEnd()).spliterator(), false).filter((p_lambda$func_217304_m$6_1_) -> {
         return this.getBlockState(p_lambda$func_217304_m$6_1_).getLightValue(this.getWorld(), p_lambda$func_217304_m$6_1_) != 0;
      });
   }

   public ITickList<Block> getBlocksToBeTicked() {
      return this.blocksToBeTicked;
   }

   public ITickList<Fluid> getFluidsToBeTicked() {
      return this.fluidsToBeTicked;
   }

   public void setModified(boolean p_177427_1_) {
      this.dirty = p_177427_1_;
   }

   public boolean isModified() {
      return this.dirty || this.hasEntities && this.world.getGameTime() != this.lastSaveTime;
   }

   public void setHasEntities(boolean p_177409_1_) {
      this.hasEntities = p_177409_1_;
   }

   public void setLastSaveTime(long p_177432_1_) {
      this.lastSaveTime = p_177432_1_;
   }

   @Nullable
   public StructureStart getStructureStart(String p_201585_1_) {
      return (StructureStart)this.structureStarts.get(p_201585_1_);
   }

   public void putStructureStart(String p_201584_1_, StructureStart p_201584_2_) {
      this.structureStarts.put(p_201584_1_, p_201584_2_);
   }

   public Map<String, StructureStart> getStructureStarts() {
      return this.structureStarts;
   }

   public void setStructureStarts(Map<String, StructureStart> p_201612_1_) {
      this.structureStarts.clear();
      this.structureStarts.putAll(p_201612_1_);
   }

   public LongSet getStructureReferences(String p_201578_1_) {
      return (LongSet)this.structureReferences.computeIfAbsent(p_201578_1_, (p_lambda$getStructureReferences$7_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addStructureReference(String p_201583_1_, long p_201583_2_) {
      ((LongSet)this.structureReferences.computeIfAbsent(p_201583_1_, (p_lambda$addStructureReference$8_0_) -> {
         return new LongOpenHashSet();
      })).add(p_201583_2_);
   }

   public Map<String, LongSet> getStructureReferences() {
      return this.structureReferences;
   }

   public void setStructureReferences(Map<String, LongSet> p_201606_1_) {
      this.structureReferences.clear();
      this.structureReferences.putAll(p_201606_1_);
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long p_177415_1_) {
      this.inhabitedTime = p_177415_1_;
   }

   public void postProcess() {
      ChunkPos chunkpos = this.getPos();

      for(int i = 0; i < this.packedBlockPositions.length; ++i) {
         if (this.packedBlockPositions[i] != null) {
            ShortListIterator var3 = this.packedBlockPositions[i].iterator();

            while(var3.hasNext()) {
               Short oshort = (Short)var3.next();
               BlockPos blockpos = ChunkPrimer.unpackToWorld(oshort, i, chunkpos);
               BlockState blockstate = this.getBlockState(blockpos);
               BlockState blockstate1 = Block.getValidBlockForPosition(blockstate, this.world, blockpos);
               this.world.setBlockState(blockpos, blockstate1, 20);
            }

            this.packedBlockPositions[i].clear();
         }
      }

      this.func_222879_B();
      Iterator var8 = Sets.newHashSet(this.deferredTileEntities.keySet()).iterator();

      while(var8.hasNext()) {
         BlockPos blockpos1 = (BlockPos)var8.next();
         this.getTileEntity(blockpos1);
      }

      this.deferredTileEntities.clear();
      this.upgradeData.postProcessChunk(this);
   }

   @Nullable
   private TileEntity setDeferredTileEntity(BlockPos p_212815_1_, CompoundNBT p_212815_2_) {
      TileEntity tileentity;
      if ("DUMMY".equals(p_212815_2_.getString("id"))) {
         BlockState state = this.getBlockState(p_212815_1_);
         if (state.hasTileEntity()) {
            tileentity = state.createTileEntity(this.world);
         } else {
            tileentity = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", p_212815_1_, this.getBlockState(p_212815_1_));
         }
      } else {
         tileentity = TileEntity.create(p_212815_2_);
      }

      if (tileentity != null) {
         tileentity.func_226984_a_(this.world, p_212815_1_);
         this.addTileEntity(tileentity);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(p_212815_1_), p_212815_1_);
      }

      return tileentity;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public ShortList[] getPackedPositions() {
      return this.packedBlockPositions;
   }

   public void func_222879_B() {
      if (this.blocksToBeTicked instanceof ChunkPrimerTickList) {
         ((ChunkPrimerTickList)this.blocksToBeTicked).postProcess(this.world.getPendingBlockTicks(), (p_lambda$func_222879_B$9_1_) -> {
            return this.getBlockState(p_lambda$func_222879_B$9_1_).getBlock();
         });
         this.blocksToBeTicked = EmptyTickList.get();
      } else if (this.blocksToBeTicked instanceof SerializableTickList) {
         this.world.getPendingBlockTicks().func_219497_a(((SerializableTickList)this.blocksToBeTicked).func_219499_a());
         this.blocksToBeTicked = EmptyTickList.get();
      }

      if (this.fluidsToBeTicked instanceof ChunkPrimerTickList) {
         ((ChunkPrimerTickList)this.fluidsToBeTicked).postProcess(this.world.getPendingFluidTicks(), (p_lambda$func_222879_B$10_1_) -> {
            return this.getFluidState(p_lambda$func_222879_B$10_1_).getFluid();
         });
         this.fluidsToBeTicked = EmptyTickList.get();
      } else if (this.fluidsToBeTicked instanceof SerializableTickList) {
         this.world.getPendingFluidTicks().func_219497_a(((SerializableTickList)this.fluidsToBeTicked).func_219499_a());
         this.fluidsToBeTicked = EmptyTickList.get();
      }

   }

   public void func_222880_a(ServerWorld p_222880_1_) {
      if (this.blocksToBeTicked == EmptyTickList.get()) {
         this.blocksToBeTicked = new SerializableTickList(Registry.BLOCK::getKey, p_222880_1_.getPendingBlockTicks().func_223188_a(this.pos, true, false));
         this.setModified(true);
      }

      if (this.fluidsToBeTicked == EmptyTickList.get()) {
         this.fluidsToBeTicked = new SerializableTickList(Registry.FLUID::getKey, p_222880_1_.getPendingFluidTicks().func_223188_a(this.pos, true, false));
         this.setModified(true);
      }

   }

   public ChunkStatus getStatus() {
      return ChunkStatus.FULL;
   }

   public ChunkHolder.LocationType func_217321_u() {
      return this.field_217329_u == null ? ChunkHolder.LocationType.BORDER : (ChunkHolder.LocationType)this.field_217329_u.get();
   }

   public void func_217314_a(Supplier<ChunkHolder.LocationType> p_217314_1_) {
      this.field_217329_u = p_217314_1_;
   }

   public boolean hasLight() {
      return this.field_217331_x;
   }

   public void setLight(boolean p_217305_1_) {
      this.field_217331_x = p_217305_1_;
      this.setModified(true);
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public final CompoundNBT writeCapsToNBT() {
      return this.serializeCaps();
   }

   /** @deprecated */
   @Deprecated
   public final void readCapsFromNBT(CompoundNBT p_readCapsFromNBT_1_) {
      this.deserializeCaps(p_readCapsFromNBT_1_);
   }

   public World getWorldForge() {
      return this.getWorld();
   }

   public static enum CreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }
}
