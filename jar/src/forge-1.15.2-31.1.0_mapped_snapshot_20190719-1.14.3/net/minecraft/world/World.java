package net.minecraft.world;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeWorld;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.server.timings.TimeTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public abstract class World extends CapabilityProvider<World> implements IWorld, AutoCloseable, IForgeWorld {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Direction[] FACING_VALUES = Direction.values();
   public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
   public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
   protected final List<TileEntity> addedTileEntityList = Lists.newArrayList();
   protected final Set<TileEntity> tileEntitiesToBeRemoved = Collections.newSetFromMap(new IdentityHashMap());
   private final Thread mainThread;
   private int skylightSubtracted;
   protected int updateLCG = (new Random()).nextInt();
   protected final int DIST_HASH_MAGIC = 1013904223;
   public float prevRainingStrength;
   public float rainingStrength;
   public float prevThunderingStrength;
   public float thunderingStrength;
   public final Random rand = new Random();
   public final Dimension dimension;
   protected final AbstractChunkProvider chunkProvider;
   protected final WorldInfo worldInfo;
   private final IProfiler profiler;
   public final boolean isRemote;
   protected boolean processingLoadedTiles;
   private final WorldBorder worldBorder;
   private final BiomeManager field_226689_w_;
   public boolean restoringBlockSnapshots = false;
   public boolean captureBlockSnapshots = false;
   public ArrayList<BlockSnapshot> capturedBlockSnapshots = new ArrayList();
   private double maxEntityRadius = 2.0D;

   protected World(WorldInfo p_i50005_1_, DimensionType p_i50005_2_, BiFunction<World, Dimension, AbstractChunkProvider> p_i50005_3_, IProfiler p_i50005_4_, boolean p_i50005_5_) {
      super(World.class);
      this.profiler = p_i50005_4_;
      this.worldInfo = p_i50005_1_;
      this.dimension = p_i50005_2_.create(this);
      this.chunkProvider = (AbstractChunkProvider)p_i50005_3_.apply(this, this.dimension);
      this.isRemote = p_i50005_5_;
      this.worldBorder = this.dimension.createWorldBorder();
      this.mainThread = Thread.currentThread();
      this.field_226689_w_ = new BiomeManager(this, p_i50005_5_ ? p_i50005_1_.getSeed() : WorldInfo.func_227498_c_(p_i50005_1_.getSeed()), p_i50005_2_.func_227176_e_());
   }

   public boolean isRemote() {
      return this.isRemote;
   }

   @Nullable
   public MinecraftServer getServer() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void setInitialSpawnLocation() {
      this.setSpawnPoint(new BlockPos(8, 64, 8));
   }

   public BlockState getGroundAboveSeaLevel(BlockPos p_184141_1_) {
      BlockPos blockpos;
      for(blockpos = new BlockPos(p_184141_1_.getX(), this.getSeaLevel(), p_184141_1_.getZ()); !this.isAirBlock(blockpos.up()); blockpos = blockpos.up()) {
      }

      return this.getBlockState(blockpos);
   }

   public static boolean isValid(BlockPos p_175701_0_) {
      return !isOutsideBuildHeight(p_175701_0_) && p_175701_0_.getX() >= -30000000 && p_175701_0_.getZ() >= -30000000 && p_175701_0_.getX() < 30000000 && p_175701_0_.getZ() < 30000000;
   }

   public static boolean isOutsideBuildHeight(BlockPos p_189509_0_) {
      return isYOutOfBounds(p_189509_0_.getY());
   }

   public static boolean isYOutOfBounds(int p_217405_0_) {
      return p_217405_0_ < 0 || p_217405_0_ >= 256;
   }

   public Chunk getChunkAt(BlockPos p_175726_1_) {
      return this.getChunk(p_175726_1_.getX() >> 4, p_175726_1_.getZ() >> 4);
   }

   public Chunk getChunk(int p_212866_1_, int p_212866_2_) {
      return (Chunk)this.getChunk(p_212866_1_, p_212866_2_, ChunkStatus.FULL);
   }

   public IChunk getChunk(int p_217353_1_, int p_217353_2_, ChunkStatus p_217353_3_, boolean p_217353_4_) {
      IChunk ichunk = this.chunkProvider.getChunk(p_217353_1_, p_217353_2_, p_217353_3_, p_217353_4_);
      if (ichunk == null && p_217353_4_) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return ichunk;
      }
   }

   public boolean setBlockState(BlockPos p_180501_1_, BlockState p_180501_2_, int p_180501_3_) {
      if (isOutsideBuildHeight(p_180501_1_)) {
         return false;
      } else if (!this.isRemote && this.worldInfo.getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         return false;
      } else {
         Chunk chunk = this.getChunkAt(p_180501_1_);
         Block block = p_180501_2_.getBlock();
         p_180501_1_ = p_180501_1_.toImmutable();
         BlockSnapshot blockSnapshot = null;
         if (this.captureBlockSnapshots && !this.isRemote) {
            blockSnapshot = BlockSnapshot.getBlockSnapshot(this, p_180501_1_, p_180501_3_);
            this.capturedBlockSnapshots.add(blockSnapshot);
         }

         BlockState old = this.getBlockState(p_180501_1_);
         int oldLight = old.getLightValue(this, p_180501_1_);
         int oldOpacity = old.getOpacity(this, p_180501_1_);
         BlockState blockstate = chunk.setBlockState(p_180501_1_, p_180501_2_, (p_180501_3_ & 64) != 0);
         if (blockstate == null) {
            if (blockSnapshot != null) {
               this.capturedBlockSnapshots.remove(blockSnapshot);
            }

            return false;
         } else {
            BlockState blockstate1 = this.getBlockState(p_180501_1_);
            if (blockstate1 != blockstate && (blockstate1.getOpacity(this, p_180501_1_) != oldOpacity || blockstate1.getLightValue(this, p_180501_1_) != oldLight || blockstate1.func_215691_g() || blockstate.func_215691_g())) {
               this.profiler.startSection("queueCheckLight");
               this.getChunkProvider().getLightManager().checkBlock(p_180501_1_);
               this.profiler.endSection();
            }

            if (blockSnapshot == null) {
               this.markAndNotifyBlock(p_180501_1_, chunk, blockstate, p_180501_2_, p_180501_3_);
            }

            return true;
         }
      }
   }

   public void markAndNotifyBlock(BlockPos p_markAndNotifyBlock_1_, @Nullable Chunk p_markAndNotifyBlock_2_, BlockState p_markAndNotifyBlock_3_, BlockState p_markAndNotifyBlock_4_, int p_markAndNotifyBlock_5_) {
      Block block = p_markAndNotifyBlock_4_.getBlock();
      BlockState blockstate1 = this.getBlockState(p_markAndNotifyBlock_1_);
      if (blockstate1 == p_markAndNotifyBlock_4_) {
         if (p_markAndNotifyBlock_3_ != blockstate1) {
            this.func_225319_b(p_markAndNotifyBlock_1_, p_markAndNotifyBlock_3_, blockstate1);
         }

         if ((p_markAndNotifyBlock_5_ & 2) != 0 && (!this.isRemote || (p_markAndNotifyBlock_5_ & 4) == 0) && (this.isRemote || p_markAndNotifyBlock_2_ == null || p_markAndNotifyBlock_2_.func_217321_u() != null && p_markAndNotifyBlock_2_.func_217321_u().isAtLeast(ChunkHolder.LocationType.TICKING))) {
            this.notifyBlockUpdate(p_markAndNotifyBlock_1_, p_markAndNotifyBlock_3_, p_markAndNotifyBlock_4_, p_markAndNotifyBlock_5_);
         }

         if (!this.isRemote && (p_markAndNotifyBlock_5_ & 1) != 0) {
            this.notifyNeighbors(p_markAndNotifyBlock_1_, p_markAndNotifyBlock_3_.getBlock());
            if (p_markAndNotifyBlock_4_.hasComparatorInputOverride()) {
               this.updateComparatorOutputLevel(p_markAndNotifyBlock_1_, block);
            }
         }

         if ((p_markAndNotifyBlock_5_ & 16) == 0) {
            int i = p_markAndNotifyBlock_5_ & -2;
            p_markAndNotifyBlock_3_.updateDiagonalNeighbors(this, p_markAndNotifyBlock_1_, i);
            p_markAndNotifyBlock_4_.updateNeighbors(this, p_markAndNotifyBlock_1_, i);
            p_markAndNotifyBlock_4_.updateDiagonalNeighbors(this, p_markAndNotifyBlock_1_, i);
         }

         this.func_217393_a(p_markAndNotifyBlock_1_, p_markAndNotifyBlock_3_, blockstate1);
      }

   }

   public void func_217393_a(BlockPos p_217393_1_, BlockState p_217393_2_, BlockState p_217393_3_) {
   }

   public boolean removeBlock(BlockPos p_217377_1_, boolean p_217377_2_) {
      IFluidState ifluidstate = this.getFluidState(p_217377_1_);
      return this.setBlockState(p_217377_1_, ifluidstate.getBlockState(), 3 | (p_217377_2_ ? 64 : 0));
   }

   public boolean func_225521_a_(BlockPos p_225521_1_, boolean p_225521_2_, @Nullable Entity p_225521_3_) {
      BlockState blockstate = this.getBlockState(p_225521_1_);
      if (blockstate.isAir(this, p_225521_1_)) {
         return false;
      } else {
         IFluidState ifluidstate = this.getFluidState(p_225521_1_);
         this.playEvent(2001, p_225521_1_, Block.getStateId(blockstate));
         if (p_225521_2_) {
            TileEntity tileentity = blockstate.hasTileEntity() ? this.getTileEntity(p_225521_1_) : null;
            Block.spawnDrops(blockstate, this, p_225521_1_, tileentity, p_225521_3_, ItemStack.EMPTY);
         }

         return this.setBlockState(p_225521_1_, ifluidstate.getBlockState(), 3);
      }
   }

   public boolean setBlockState(BlockPos p_175656_1_, BlockState p_175656_2_) {
      return this.setBlockState(p_175656_1_, p_175656_2_, 3);
   }

   public abstract void notifyBlockUpdate(BlockPos var1, BlockState var2, BlockState var3, int var4);

   public void notifyNeighbors(BlockPos p_195592_1_, Block p_195592_2_) {
      if (this.worldInfo.getGenerator() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.notifyNeighborsOfStateChange(p_195592_1_, p_195592_2_);
      }

   }

   public void func_225319_b(BlockPos p_225319_1_, BlockState p_225319_2_, BlockState p_225319_3_) {
   }

   public void notifyNeighborsOfStateChange(BlockPos p_195593_1_, Block p_195593_2_) {
      if (!ForgeEventFactory.onNeighborNotify(this, p_195593_1_, this.getBlockState(p_195593_1_), EnumSet.allOf(Direction.class), false).isCanceled()) {
         this.neighborChanged(p_195593_1_.west(), p_195593_2_, p_195593_1_);
         this.neighborChanged(p_195593_1_.east(), p_195593_2_, p_195593_1_);
         this.neighborChanged(p_195593_1_.down(), p_195593_2_, p_195593_1_);
         this.neighborChanged(p_195593_1_.up(), p_195593_2_, p_195593_1_);
         this.neighborChanged(p_195593_1_.north(), p_195593_2_, p_195593_1_);
         this.neighborChanged(p_195593_1_.south(), p_195593_2_, p_195593_1_);
      }
   }

   public void notifyNeighborsOfStateExcept(BlockPos p_175695_1_, Block p_175695_2_, Direction p_175695_3_) {
      EnumSet<Direction> directions = EnumSet.allOf(Direction.class);
      directions.remove(p_175695_3_);
      if (!ForgeEventFactory.onNeighborNotify(this, p_175695_1_, this.getBlockState(p_175695_1_), directions, false).isCanceled()) {
         if (p_175695_3_ != Direction.WEST) {
            this.neighborChanged(p_175695_1_.west(), p_175695_2_, p_175695_1_);
         }

         if (p_175695_3_ != Direction.EAST) {
            this.neighborChanged(p_175695_1_.east(), p_175695_2_, p_175695_1_);
         }

         if (p_175695_3_ != Direction.DOWN) {
            this.neighborChanged(p_175695_1_.down(), p_175695_2_, p_175695_1_);
         }

         if (p_175695_3_ != Direction.UP) {
            this.neighborChanged(p_175695_1_.up(), p_175695_2_, p_175695_1_);
         }

         if (p_175695_3_ != Direction.NORTH) {
            this.neighborChanged(p_175695_1_.north(), p_175695_2_, p_175695_1_);
         }

         if (p_175695_3_ != Direction.SOUTH) {
            this.neighborChanged(p_175695_1_.south(), p_175695_2_, p_175695_1_);
         }

      }
   }

   public void neighborChanged(BlockPos p_190524_1_, Block p_190524_2_, BlockPos p_190524_3_) {
      if (!this.isRemote) {
         BlockState blockstate = this.getBlockState(p_190524_1_);

         try {
            blockstate.neighborChanged(this, p_190524_1_, p_190524_2_, p_190524_3_, false);
         } catch (Throwable var8) {
            CrashReport crashreport = CrashReport.makeCrashReport(var8, "Exception while updating neighbours");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
            crashreportcategory.addDetail("Source block type", () -> {
               try {
                  return String.format("ID #%s (%s // %s)", p_190524_2_.getRegistryName(), p_190524_2_.getTranslationKey(), p_190524_2_.getClass().getCanonicalName());
               } catch (Throwable var2) {
                  return "ID #" + p_190524_2_.getRegistryName();
               }
            });
            CrashReportCategory.addBlockInfo(crashreportcategory, p_190524_1_, blockstate);
            throw new ReportedException(crashreport);
         }
      }

   }

   public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
      int i;
      if (p_201676_2_ >= -30000000 && p_201676_3_ >= -30000000 && p_201676_2_ < 30000000 && p_201676_3_ < 30000000) {
         if (this.chunkExists(p_201676_2_ >> 4, p_201676_3_ >> 4)) {
            i = this.getChunk(p_201676_2_ >> 4, p_201676_3_ >> 4).getTopBlockY(p_201676_1_, p_201676_2_ & 15, p_201676_3_ & 15) + 1;
         } else {
            i = 0;
         }
      } else {
         i = this.getSeaLevel() + 1;
      }

      return i;
   }

   public WorldLightManager func_225524_e_() {
      return this.getChunkProvider().getLightManager();
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      if (isOutsideBuildHeight(p_180495_1_)) {
         return Blocks.VOID_AIR.getDefaultState();
      } else {
         Chunk chunk = this.getChunk(p_180495_1_.getX() >> 4, p_180495_1_.getZ() >> 4);
         return chunk.getBlockState(p_180495_1_);
      }
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      if (isOutsideBuildHeight(p_204610_1_)) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         Chunk chunk = this.getChunkAt(p_204610_1_);
         return chunk.getFluidState(p_204610_1_);
      }
   }

   public boolean isDaytime() {
      return this.dimension.isDaytime();
   }

   public boolean func_226690_K_() {
      return this.dimension.getType() == DimensionType.OVERWORLD && !this.isDaytime();
   }

   public void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_) {
      this.playSound(p_184133_1_, (double)p_184133_2_.getX() + 0.5D, (double)p_184133_2_.getY() + 0.5D, (double)p_184133_2_.getZ() + 0.5D, p_184133_3_, p_184133_4_, p_184133_5_, p_184133_6_);
   }

   public abstract void playSound(@Nullable PlayerEntity var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9, float var10, float var11);

   public abstract void playMovingSound(@Nullable PlayerEntity var1, Entity var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

   public void playSound(double p_184134_1_, double p_184134_3_, double p_184134_5_, SoundEvent p_184134_7_, SoundCategory p_184134_8_, float p_184134_9_, float p_184134_10_, boolean p_184134_11_) {
   }

   public void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void addParticle(IParticleData p_195590_1_, boolean p_195590_2_, double p_195590_3_, double p_195590_5_, double p_195590_7_, double p_195590_9_, double p_195590_11_, double p_195590_13_) {
   }

   public void addOptionalParticle(IParticleData p_195589_1_, double p_195589_2_, double p_195589_4_, double p_195589_6_, double p_195589_8_, double p_195589_10_, double p_195589_12_) {
   }

   public void func_217404_b(IParticleData p_217404_1_, boolean p_217404_2_, double p_217404_3_, double p_217404_5_, double p_217404_7_, double p_217404_9_, double p_217404_11_, double p_217404_13_) {
   }

   public float getCelestialAngleRadians(float p_72929_1_) {
      float f = this.getCelestialAngle(p_72929_1_);
      return f * 6.2831855F;
   }

   public boolean addTileEntity(TileEntity p_175700_1_) {
      if (p_175700_1_.getWorld() != this) {
         p_175700_1_.func_226984_a_(this, p_175700_1_.getPos());
      }

      if (this.processingLoadedTiles) {
         LOGGER.error("Adding block entity while ticking: {} @ {}", new Supplier[]{() -> {
            return Registry.BLOCK_ENTITY_TYPE.getKey(p_175700_1_.getType());
         }, p_175700_1_::getPos});
         return this.addedTileEntityList.add(p_175700_1_);
      } else {
         boolean flag = this.loadedTileEntityList.add(p_175700_1_);
         if (flag && p_175700_1_ instanceof ITickableTileEntity) {
            this.tickableTileEntities.add(p_175700_1_);
         }

         p_175700_1_.onLoad();
         if (this.isRemote) {
            BlockPos blockpos = p_175700_1_.getPos();
            BlockState blockstate = this.getBlockState(blockpos);
            this.notifyBlockUpdate(blockpos, blockstate, blockstate, 2);
         }

         return flag;
      }
   }

   public void addTileEntities(Collection<TileEntity> p_147448_1_) {
      if (this.processingLoadedTiles) {
         p_147448_1_.stream().filter((p_lambda$addTileEntities$2_1_) -> {
            return p_lambda$addTileEntities$2_1_.getWorld() != this;
         }).forEach((p_lambda$addTileEntities$3_1_) -> {
            p_lambda$addTileEntities$3_1_.func_226984_a_(this, p_lambda$addTileEntities$3_1_.getPos());
         });
         this.addedTileEntityList.addAll(p_147448_1_);
      } else {
         Iterator var2 = p_147448_1_.iterator();

         while(var2.hasNext()) {
            TileEntity tileentity = (TileEntity)var2.next();
            this.addTileEntity(tileentity);
         }
      }

   }

   public void func_217391_K() {
      IProfiler iprofiler = this.getProfiler();
      iprofiler.startSection("blockEntities");
      this.processingLoadedTiles = true;
      if (!this.tileEntitiesToBeRemoved.isEmpty()) {
         this.tileEntitiesToBeRemoved.forEach((p_lambda$func_217391_K$4_0_) -> {
            p_lambda$func_217391_K$4_0_.onChunkUnloaded();
         });
         this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
         this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
         this.tileEntitiesToBeRemoved.clear();
      }

      Iterator iterator = this.tickableTileEntities.iterator();

      while(iterator.hasNext()) {
         TileEntity tileentity = (TileEntity)iterator.next();
         if (!tileentity.isRemoved() && tileentity.hasWorld()) {
            BlockPos blockpos = tileentity.getPos();
            if (this.chunkProvider.canTick(blockpos) && this.getWorldBorder().contains(blockpos)) {
               try {
                  TimeTracker.TILE_ENTITY_UPDATE.trackStart(tileentity);
                  iprofiler.startSection(() -> {
                     return String.valueOf(tileentity.getType().getRegistryName());
                  });
                  if (tileentity.getType().isValidBlock(this.getBlockState(blockpos).getBlock())) {
                     ((ITickableTileEntity)tileentity).tick();
                  } else {
                     tileentity.warnInvalidBlock();
                  }

                  iprofiler.endSection();
               } catch (Throwable var11) {
                  CrashReport crashreport = CrashReport.makeCrashReport(var11, "Ticking block entity");
                  CrashReportCategory crashreportcategory = crashreport.makeCategory("Block entity being ticked");
                  tileentity.addInfoToCrashReport(crashreportcategory);
                  if (!(Boolean)ForgeConfig.SERVER.removeErroringTileEntities.get()) {
                     throw new ReportedException(crashreport);
                  }

                  LogManager.getLogger().fatal("{}", crashreport.getCompleteReport());
                  tileentity.remove();
                  this.removeTileEntity(tileentity.getPos());
               } finally {
                  TimeTracker.TILE_ENTITY_UPDATE.trackEnd(tileentity);
               }
            }
         }

         if (tileentity.isRemoved()) {
            iterator.remove();
            this.loadedTileEntityList.remove(tileentity);
            if (this.isBlockLoaded(tileentity.getPos())) {
               Chunk chunk = this.getChunkAt(tileentity.getPos());
               if (chunk.getTileEntity(tileentity.getPos(), Chunk.CreateEntityType.CHECK) == tileentity) {
                  chunk.removeTileEntity(tileentity.getPos());
               }
            }
         }
      }

      this.processingLoadedTiles = false;
      iprofiler.endStartSection("pendingBlockEntities");
      if (!this.addedTileEntityList.isEmpty()) {
         for(int i = 0; i < this.addedTileEntityList.size(); ++i) {
            TileEntity tileentity1 = (TileEntity)this.addedTileEntityList.get(i);
            if (!tileentity1.isRemoved()) {
               if (!this.loadedTileEntityList.contains(tileentity1)) {
                  this.addTileEntity(tileentity1);
               }

               if (this.isBlockLoaded(tileentity1.getPos())) {
                  Chunk chunk = this.getChunkAt(tileentity1.getPos());
                  BlockState blockstate = chunk.getBlockState(tileentity1.getPos());
                  chunk.addTileEntity(tileentity1.getPos(), tileentity1);
                  this.notifyBlockUpdate(tileentity1.getPos(), blockstate, blockstate, 3);
               }
            }
         }

         this.addedTileEntityList.clear();
      }

      iprofiler.endSection();
   }

   public void func_217390_a(Consumer<Entity> p_217390_1_, Entity p_217390_2_) {
      try {
         TimeTracker.ENTITY_UPDATE.trackStart(p_217390_2_);
         p_217390_1_.accept(p_217390_2_);
      } catch (Throwable var9) {
         CrashReport crashreport = CrashReport.makeCrashReport(var9, "Ticking entity");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");
         p_217390_2_.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      } finally {
         TimeTracker.ENTITY_UPDATE.trackEnd(p_217390_2_);
      }

   }

   public boolean checkBlockCollision(AxisAlignedBB p_72829_1_) {
      int i = MathHelper.floor(p_72829_1_.minX);
      int j = MathHelper.ceil(p_72829_1_.maxX);
      int k = MathHelper.floor(p_72829_1_.minY);
      int l = MathHelper.ceil(p_72829_1_.maxY);
      int i1 = MathHelper.floor(p_72829_1_.minZ);
      int j1 = MathHelper.ceil(p_72829_1_.maxZ);
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var9 = null;

      boolean var27;
      try {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  BlockState blockstate = this.getBlockState(blockpos$pooledmutable.setPos(k1, l1, i2));
                  if (!blockstate.isAir(this, blockpos$pooledmutable)) {
                     boolean flag = true;
                     boolean var15 = flag;
                     return var15;
                  }
               }
            }
         }

         var27 = false;
      } catch (Throwable var25) {
         var9 = var25;
         throw var25;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var9 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var24) {
                  var9.addSuppressed(var24);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

      return var27;
   }

   public boolean isFlammableWithin(AxisAlignedBB p_147470_1_) {
      int i = MathHelper.floor(p_147470_1_.minX);
      int j = MathHelper.ceil(p_147470_1_.maxX);
      int k = MathHelper.floor(p_147470_1_.minY);
      int l = MathHelper.ceil(p_147470_1_.maxY);
      int i1 = MathHelper.floor(p_147470_1_.minZ);
      int j1 = MathHelper.ceil(p_147470_1_.maxZ);
      if (this.isAreaLoaded(i, k, i1, j, l, j1)) {
         BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
         Throwable var9 = null;

         boolean var27;
         try {
            for(int k1 = i; k1 < j; ++k1) {
               for(int l1 = k; l1 < l; ++l1) {
                  for(int i2 = i1; i2 < j1; ++i2) {
                     BlockState state = this.getBlockState(blockpos$pooledmutable.setPos(k1, l1, i2));
                     if (state.isBurning(this, blockpos$pooledmutable)) {
                        boolean flag = true;
                        boolean var15 = flag;
                        return var15;
                     }
                  }
               }
            }

            var27 = false;
         } catch (Throwable var25) {
            var9 = var25;
            throw var25;
         } finally {
            if (blockpos$pooledmutable != null) {
               if (var9 != null) {
                  try {
                     blockpos$pooledmutable.close();
                  } catch (Throwable var24) {
                     var9.addSuppressed(var24);
                  }
               } else {
                  blockpos$pooledmutable.close();
               }
            }

         }

         return var27;
      } else {
         return false;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public BlockState findBlockstateInArea(AxisAlignedBB p_203067_1_, Block p_203067_2_) {
      int i = MathHelper.floor(p_203067_1_.minX);
      int j = MathHelper.ceil(p_203067_1_.maxX);
      int k = MathHelper.floor(p_203067_1_.minY);
      int l = MathHelper.ceil(p_203067_1_.maxY);
      int i1 = MathHelper.floor(p_203067_1_.minZ);
      int j1 = MathHelper.ceil(p_203067_1_.maxZ);
      if (this.isAreaLoaded(i, k, i1, j, l, j1)) {
         BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
         Throwable var10 = null;

         try {
            for(int k1 = i; k1 < j; ++k1) {
               for(int l1 = k; l1 < l; ++l1) {
                  for(int i2 = i1; i2 < j1; ++i2) {
                     BlockState blockstate = this.getBlockState(blockpos$pooledmutable.setPos(k1, l1, i2));
                     if (blockstate.getBlock() == p_203067_2_) {
                        BlockState var15 = blockstate;
                        return var15;
                     }
                  }
               }
            }

            Object var27 = null;
            return (BlockState)var27;
         } catch (Throwable var25) {
            var10 = var25;
            throw var25;
         } finally {
            if (blockpos$pooledmutable != null) {
               if (var10 != null) {
                  try {
                     blockpos$pooledmutable.close();
                  } catch (Throwable var24) {
                     var10.addSuppressed(var24);
                  }
               } else {
                  blockpos$pooledmutable.close();
               }
            }

         }
      } else {
         return null;
      }
   }

   public boolean isMaterialInBB(AxisAlignedBB p_72875_1_, Material p_72875_2_) {
      int i = MathHelper.floor(p_72875_1_.minX);
      int j = MathHelper.ceil(p_72875_1_.maxX);
      int k = MathHelper.floor(p_72875_1_.minY);
      int l = MathHelper.ceil(p_72875_1_.maxY);
      int i1 = MathHelper.floor(p_72875_1_.minZ);
      int j1 = MathHelper.ceil(p_72875_1_.maxZ);
      BlockMaterialMatcher blockmaterialmatcher = BlockMaterialMatcher.forMaterial(p_72875_2_);
      return BlockPos.getAllInBox(i, k, i1, j - 1, l - 1, j1 - 1).anyMatch((p_lambda$isMaterialInBB$6_2_) -> {
         return blockmaterialmatcher.test(this.getBlockState(p_lambda$isMaterialInBB$6_2_));
      });
   }

   public Explosion createExplosion(@Nullable Entity p_217385_1_, double p_217385_2_, double p_217385_4_, double p_217385_6_, float p_217385_8_, Explosion.Mode p_217385_9_) {
      return this.createExplosion(p_217385_1_, (DamageSource)null, p_217385_2_, p_217385_4_, p_217385_6_, p_217385_8_, false, p_217385_9_);
   }

   public Explosion createExplosion(@Nullable Entity p_217398_1_, double p_217398_2_, double p_217398_4_, double p_217398_6_, float p_217398_8_, boolean p_217398_9_, Explosion.Mode p_217398_10_) {
      return this.createExplosion(p_217398_1_, (DamageSource)null, p_217398_2_, p_217398_4_, p_217398_6_, p_217398_8_, p_217398_9_, p_217398_10_);
   }

   public Explosion createExplosion(@Nullable Entity p_217401_1_, @Nullable DamageSource p_217401_2_, double p_217401_3_, double p_217401_5_, double p_217401_7_, float p_217401_9_, boolean p_217401_10_, Explosion.Mode p_217401_11_) {
      Explosion explosion = new Explosion(this, p_217401_1_, p_217401_3_, p_217401_5_, p_217401_7_, p_217401_9_, p_217401_10_, p_217401_11_);
      if (p_217401_2_ != null) {
         explosion.setDamageSource(p_217401_2_);
      }

      if (ForgeEventFactory.onExplosionStart(this, explosion)) {
         return explosion;
      } else {
         explosion.doExplosionA();
         explosion.doExplosionB(true);
         return explosion;
      }
   }

   public boolean extinguishFire(@Nullable PlayerEntity p_175719_1_, BlockPos p_175719_2_, Direction p_175719_3_) {
      p_175719_2_ = p_175719_2_.offset(p_175719_3_);
      if (this.getBlockState(p_175719_2_).getBlock() == Blocks.FIRE) {
         this.playEvent(p_175719_1_, 1009, p_175719_2_, 0);
         this.removeBlock(p_175719_2_, false);
         return true;
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public String getProviderName() {
      return this.chunkProvider.makeString();
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      if (isOutsideBuildHeight(p_175625_1_)) {
         return null;
      } else if (!this.isRemote && Thread.currentThread() != this.mainThread) {
         return null;
      } else {
         TileEntity tileentity = null;
         if (this.processingLoadedTiles) {
            tileentity = this.getPendingTileEntityAt(p_175625_1_);
         }

         if (tileentity == null) {
            tileentity = this.getChunkAt(p_175625_1_).getTileEntity(p_175625_1_, Chunk.CreateEntityType.IMMEDIATE);
         }

         if (tileentity == null) {
            tileentity = this.getPendingTileEntityAt(p_175625_1_);
         }

         return tileentity;
      }
   }

   @Nullable
   private TileEntity getPendingTileEntityAt(BlockPos p_189508_1_) {
      for(int i = 0; i < this.addedTileEntityList.size(); ++i) {
         TileEntity tileentity = (TileEntity)this.addedTileEntityList.get(i);
         if (!tileentity.isRemoved() && tileentity.getPos().equals(p_189508_1_)) {
            return tileentity;
         }
      }

      return null;
   }

   public void setTileEntity(BlockPos p_175690_1_, @Nullable TileEntity p_175690_2_) {
      if (!isOutsideBuildHeight(p_175690_1_)) {
         p_175690_1_ = p_175690_1_.toImmutable();
         if (p_175690_2_ != null && !p_175690_2_.isRemoved()) {
            if (this.processingLoadedTiles) {
               p_175690_2_.func_226984_a_(this, p_175690_1_);
               Iterator iterator = this.addedTileEntityList.iterator();

               while(iterator.hasNext()) {
                  TileEntity tileentity = (TileEntity)iterator.next();
                  if (tileentity.getPos().equals(p_175690_1_)) {
                     tileentity.remove();
                     iterator.remove();
                  }
               }

               this.addedTileEntityList.add(p_175690_2_);
            } else {
               Chunk chunk = this.getChunkAt(p_175690_1_);
               if (chunk != null) {
                  chunk.addTileEntity(p_175690_1_, p_175690_2_);
               }

               this.addTileEntity(p_175690_2_);
            }
         }
      }

   }

   public void removeTileEntity(BlockPos p_175713_1_) {
      TileEntity tileentity = this.getTileEntity(p_175713_1_);
      if (tileentity != null && this.processingLoadedTiles) {
         tileentity.remove();
         this.addedTileEntityList.remove(tileentity);
         if (!(tileentity instanceof ITickableTileEntity)) {
            this.loadedTileEntityList.remove(tileentity);
         }
      } else {
         if (tileentity != null) {
            this.addedTileEntityList.remove(tileentity);
            this.loadedTileEntityList.remove(tileentity);
            this.tickableTileEntities.remove(tileentity);
         }

         this.getChunkAt(p_175713_1_).removeTileEntity(p_175713_1_);
      }

      this.updateComparatorOutputLevel(p_175713_1_, this.getBlockState(p_175713_1_).getBlock());
   }

   public boolean isBlockPresent(BlockPos p_195588_1_) {
      return isOutsideBuildHeight(p_195588_1_) ? false : this.chunkProvider.chunkExists(p_195588_1_.getX() >> 4, p_195588_1_.getZ() >> 4);
   }

   public boolean func_217400_a(BlockPos p_217400_1_, Entity p_217400_2_) {
      if (isOutsideBuildHeight(p_217400_1_)) {
         return false;
      } else {
         IChunk ichunk = this.getChunk(p_217400_1_.getX() >> 4, p_217400_1_.getZ() >> 4, ChunkStatus.FULL, false);
         return ichunk == null ? false : ichunk.getBlockState(p_217400_1_).func_215682_a(this, p_217400_1_, p_217400_2_);
      }
   }

   public void calculateInitialSkylight() {
      double d0 = 1.0D - (double)(this.getRainStrength(1.0F) * 5.0F) / 16.0D;
      double d1 = 1.0D - (double)(this.getThunderStrength(1.0F) * 5.0F) / 16.0D;
      double d2 = 0.5D + 2.0D * MathHelper.clamp((double)MathHelper.cos(this.getCelestialAngle(1.0F) * 6.2831855F), -0.25D, 0.25D);
      this.skylightSubtracted = (int)((1.0D - d2 * d0 * d1) * 11.0D);
   }

   public void setAllowedSpawnTypes(boolean p_72891_1_, boolean p_72891_2_) {
      this.getChunkProvider().setAllowedSpawnTypes(p_72891_1_, p_72891_2_);
      this.getDimension().setAllowedSpawnTypes(p_72891_1_, p_72891_2_);
   }

   protected void calculateInitialWeather() {
      this.dimension.calculateInitialWeather();
   }

   public void calculateInitialWeatherBody() {
      if (this.worldInfo.isRaining()) {
         this.rainingStrength = 1.0F;
         if (this.worldInfo.isThundering()) {
            this.thunderingStrength = 1.0F;
         }
      }

   }

   public void close() throws IOException {
      this.chunkProvider.close();
   }

   @Nullable
   public IBlockReader func_225522_c_(int p_225522_1_, int p_225522_2_) {
      return this.getChunk(p_225522_1_, p_225522_2_, ChunkStatus.FULL, false);
   }

   public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity p_175674_1_, AxisAlignedBB p_175674_2_, @Nullable Predicate<? super Entity> p_175674_3_) {
      this.getProfiler().func_230035_c_("getEntities");
      List<Entity> list = Lists.newArrayList();
      int i = MathHelper.floor((p_175674_2_.minX - this.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((p_175674_2_.maxX + this.getMaxEntityRadius()) / 16.0D);
      int k = MathHelper.floor((p_175674_2_.minZ - this.getMaxEntityRadius()) / 16.0D);
      int l = MathHelper.floor((p_175674_2_.maxZ + this.getMaxEntityRadius()) / 16.0D);

      for(int i1 = i; i1 <= j; ++i1) {
         for(int j1 = k; j1 <= l; ++j1) {
            Chunk chunk = this.getChunkProvider().getChunk(i1, j1, false);
            if (chunk != null) {
               chunk.getEntitiesWithinAABBForEntity(p_175674_1_, p_175674_2_, list, p_175674_3_);
            }
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(@Nullable EntityType<T> p_217394_1_, AxisAlignedBB p_217394_2_, Predicate<? super T> p_217394_3_) {
      this.getProfiler().func_230035_c_("getEntities");
      int i = MathHelper.floor((p_217394_2_.minX - this.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.ceil((p_217394_2_.maxX + this.getMaxEntityRadius()) / 16.0D);
      int k = MathHelper.floor((p_217394_2_.minZ - this.getMaxEntityRadius()) / 16.0D);
      int l = MathHelper.ceil((p_217394_2_.maxZ + this.getMaxEntityRadius()) / 16.0D);
      List<T> list = Lists.newArrayList();

      for(int i1 = i; i1 < j; ++i1) {
         for(int j1 = k; j1 < l; ++j1) {
            Chunk chunk = this.getChunkProvider().getChunk(i1, j1, false);
            if (chunk != null) {
               chunk.func_217313_a(p_217394_1_, p_217394_2_, list, p_217394_3_);
            }
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_175647_1_, AxisAlignedBB p_175647_2_, @Nullable Predicate<? super T> p_175647_3_) {
      this.getProfiler().func_230035_c_("getEntities");
      int i = MathHelper.floor((p_175647_2_.minX - this.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.ceil((p_175647_2_.maxX + this.getMaxEntityRadius()) / 16.0D);
      int k = MathHelper.floor((p_175647_2_.minZ - this.getMaxEntityRadius()) / 16.0D);
      int l = MathHelper.ceil((p_175647_2_.maxZ + this.getMaxEntityRadius()) / 16.0D);
      List<T> list = Lists.newArrayList();
      AbstractChunkProvider abstractchunkprovider = this.getChunkProvider();

      for(int i1 = i; i1 < j; ++i1) {
         for(int j1 = k; j1 < l; ++j1) {
            Chunk chunk = abstractchunkprovider.getChunk(i1, j1, false);
            if (chunk != null) {
               chunk.getEntitiesOfTypeWithinAABB(p_175647_1_, p_175647_2_, list, p_175647_3_);
            }
         }
      }

      return list;
   }

   public <T extends Entity> List<T> func_225316_b(Class<? extends T> p_225316_1_, AxisAlignedBB p_225316_2_, @Nullable Predicate<? super T> p_225316_3_) {
      this.getProfiler().func_230035_c_("getLoadedEntities");
      int i = MathHelper.floor((p_225316_2_.minX - this.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.ceil((p_225316_2_.maxX + this.getMaxEntityRadius()) / 16.0D);
      int k = MathHelper.floor((p_225316_2_.minZ - this.getMaxEntityRadius()) / 16.0D);
      int l = MathHelper.ceil((p_225316_2_.maxZ + this.getMaxEntityRadius()) / 16.0D);
      List<T> list = Lists.newArrayList();
      AbstractChunkProvider abstractchunkprovider = this.getChunkProvider();

      for(int i1 = i; i1 < j; ++i1) {
         for(int j1 = k; j1 < l; ++j1) {
            Chunk chunk = abstractchunkprovider.func_225313_a(i1, j1);
            if (chunk != null) {
               chunk.getEntitiesOfTypeWithinAABB(p_225316_1_, p_225316_2_, list, p_225316_3_);
            }
         }
      }

      return list;
   }

   @Nullable
   public abstract Entity getEntityByID(int var1);

   public void markChunkDirty(BlockPos p_175646_1_, TileEntity p_175646_2_) {
      if (this.isBlockLoaded(p_175646_1_)) {
         this.getChunkAt(p_175646_1_).markDirty();
      }

   }

   public int getSeaLevel() {
      return this.getDimension().getSeaLevel();
   }

   public World getWorld() {
      return this;
   }

   public WorldType getWorldType() {
      return this.worldInfo.getGenerator();
   }

   public int getStrongPower(BlockPos p_175676_1_) {
      int i = 0;
      int i = Math.max(i, this.getStrongPower(p_175676_1_.down(), Direction.DOWN));
      if (i >= 15) {
         return i;
      } else {
         i = Math.max(i, this.getStrongPower(p_175676_1_.up(), Direction.UP));
         if (i >= 15) {
            return i;
         } else {
            i = Math.max(i, this.getStrongPower(p_175676_1_.north(), Direction.NORTH));
            if (i >= 15) {
               return i;
            } else {
               i = Math.max(i, this.getStrongPower(p_175676_1_.south(), Direction.SOUTH));
               if (i >= 15) {
                  return i;
               } else {
                  i = Math.max(i, this.getStrongPower(p_175676_1_.west(), Direction.WEST));
                  if (i >= 15) {
                     return i;
                  } else {
                     i = Math.max(i, this.getStrongPower(p_175676_1_.east(), Direction.EAST));
                     return i >= 15 ? i : i;
                  }
               }
            }
         }
      }
   }

   public boolean isSidePowered(BlockPos p_175709_1_, Direction p_175709_2_) {
      return this.getRedstonePower(p_175709_1_, p_175709_2_) > 0;
   }

   public int getRedstonePower(BlockPos p_175651_1_, Direction p_175651_2_) {
      BlockState blockstate = this.getBlockState(p_175651_1_);
      return blockstate.shouldCheckWeakPower(this, p_175651_1_, p_175651_2_) ? this.getStrongPower(p_175651_1_) : blockstate.getWeakPower(this, p_175651_1_, p_175651_2_);
   }

   public boolean isBlockPowered(BlockPos p_175640_1_) {
      if (this.getRedstonePower(p_175640_1_.down(), Direction.DOWN) > 0) {
         return true;
      } else if (this.getRedstonePower(p_175640_1_.up(), Direction.UP) > 0) {
         return true;
      } else if (this.getRedstonePower(p_175640_1_.north(), Direction.NORTH) > 0) {
         return true;
      } else if (this.getRedstonePower(p_175640_1_.south(), Direction.SOUTH) > 0) {
         return true;
      } else if (this.getRedstonePower(p_175640_1_.west(), Direction.WEST) > 0) {
         return true;
      } else {
         return this.getRedstonePower(p_175640_1_.east(), Direction.EAST) > 0;
      }
   }

   public int getRedstonePowerFromNeighbors(BlockPos p_175687_1_) {
      int i = 0;
      Direction[] var3 = FACING_VALUES;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction direction = var3[var5];
         int j = this.getRedstonePower(p_175687_1_.offset(direction), direction);
         if (j >= 15) {
            return 15;
         }

         if (j > i) {
            i = j;
         }
      }

      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public void sendQuittingDisconnectingPacket() {
   }

   public void setGameTime(long p_82738_1_) {
      this.worldInfo.setGameTime(p_82738_1_);
   }

   public long getSeed() {
      return this.dimension.getSeed();
   }

   public long getGameTime() {
      return this.worldInfo.getGameTime();
   }

   public long getDayTime() {
      return this.dimension.getWorldTime();
   }

   public void setDayTime(long p_72877_1_) {
      this.dimension.setWorldTime(p_72877_1_);
   }

   protected void advanceTime() {
      this.setGameTime(this.worldInfo.getGameTime() + 1L);
      if (this.worldInfo.getGameRulesInstance().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
         this.setDayTime(this.worldInfo.getDayTime() + 1L);
      }

   }

   public BlockPos getSpawnPoint() {
      BlockPos blockpos = this.dimension.getSpawnPoint();
      if (!this.getWorldBorder().contains(blockpos)) {
         blockpos = this.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
      }

      return blockpos;
   }

   public void setSpawnPoint(BlockPos p_175652_1_) {
      this.dimension.setSpawnPoint(p_175652_1_);
   }

   public boolean isBlockModifiable(PlayerEntity p_175660_1_, BlockPos p_175660_2_) {
      return this.dimension.canMineBlock(p_175660_1_, p_175660_2_);
   }

   public boolean canMineBlockBody(PlayerEntity p_canMineBlockBody_1_, BlockPos p_canMineBlockBody_2_) {
      return true;
   }

   public void setEntityState(Entity p_72960_1_, byte p_72960_2_) {
   }

   public AbstractChunkProvider getChunkProvider() {
      return this.chunkProvider;
   }

   public void addBlockEvent(BlockPos p_175641_1_, Block p_175641_2_, int p_175641_3_, int p_175641_4_) {
      this.getBlockState(p_175641_1_).onBlockEventReceived(this, p_175641_1_, p_175641_3_, p_175641_4_);
   }

   public WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   public GameRules getGameRules() {
      return this.worldInfo.getGameRulesInstance();
   }

   public float getThunderStrength(float p_72819_1_) {
      return MathHelper.lerp(p_72819_1_, this.prevThunderingStrength, this.thunderingStrength) * this.getRainStrength(p_72819_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setThunderStrength(float p_147442_1_) {
      this.prevThunderingStrength = p_147442_1_;
      this.thunderingStrength = p_147442_1_;
   }

   public float getRainStrength(float p_72867_1_) {
      return MathHelper.lerp(p_72867_1_, this.prevRainingStrength, this.rainingStrength);
   }

   @OnlyIn(Dist.CLIENT)
   public void setRainStrength(float p_72894_1_) {
      this.prevRainingStrength = p_72894_1_;
      this.rainingStrength = p_72894_1_;
   }

   public boolean isThundering() {
      if (this.dimension.hasSkyLight() && !this.dimension.isNether()) {
         return (double)this.getThunderStrength(1.0F) > 0.9D;
      } else {
         return false;
      }
   }

   public boolean isRaining() {
      return (double)this.getRainStrength(1.0F) > 0.2D;
   }

   public boolean isRainingAt(BlockPos p_175727_1_) {
      if (!this.isRaining()) {
         return false;
      } else if (!this.func_226660_f_(p_175727_1_)) {
         return false;
      } else if (this.getHeight(Heightmap.Type.MOTION_BLOCKING, p_175727_1_).getY() > p_175727_1_.getY()) {
         return false;
      } else {
         return this.func_226691_t_(p_175727_1_).getPrecipitation() == Biome.RainType.RAIN;
      }
   }

   public boolean isBlockinHighHumidity(BlockPos p_180502_1_) {
      return this.dimension.isHighHumidity(p_180502_1_);
   }

   @Nullable
   public abstract MapData func_217406_a(String var1);

   public abstract void func_217399_a(MapData var1);

   public abstract int getNextMapId();

   public void playBroadcastSound(int p_175669_1_, BlockPos p_175669_2_, int p_175669_3_) {
   }

   public int getActualHeight() {
      return this.dimension.getActualHeight();
   }

   public CrashReportCategory fillCrashReport(CrashReport p_72914_1_) {
      CrashReportCategory crashreportcategory = p_72914_1_.makeCategoryDepth("Affected level", 1);
      crashreportcategory.addDetail("All players", () -> {
         return this.getPlayers().size() + " total; " + this.getPlayers();
      });
      AbstractChunkProvider var10002 = this.chunkProvider;
      crashreportcategory.addDetail("Chunk stats", var10002::makeString);
      crashreportcategory.addDetail("Level dimension", () -> {
         return this.dimension.getType().toString();
      });

      try {
         this.worldInfo.addToCrashReport(crashreportcategory);
      } catch (Throwable var4) {
         crashreportcategory.addCrashSectionThrowable("Level Data Unobtainable", var4);
      }

      return crashreportcategory;
   }

   public abstract void sendBlockBreakProgress(int var1, BlockPos var2, int var3);

   @OnlyIn(Dist.CLIENT)
   public void makeFireworks(double p_92088_1_, double p_92088_3_, double p_92088_5_, double p_92088_7_, double p_92088_9_, double p_92088_11_, @Nullable CompoundNBT p_92088_13_) {
   }

   public abstract Scoreboard getScoreboard();

   public void updateComparatorOutputLevel(BlockPos p_175666_1_, Block p_175666_2_) {
      Direction[] var3 = Direction.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction direction = var3[var5];
         BlockPos blockpos = p_175666_1_.offset(direction);
         if (this.isBlockLoaded(blockpos)) {
            BlockState blockstate = this.getBlockState(blockpos);
            blockstate.onNeighborChange(this, blockpos, p_175666_1_);
            if (blockstate.isNormalCube(this, blockpos)) {
               blockpos = blockpos.offset(direction);
               blockstate = this.getBlockState(blockpos);
               if (blockstate.getWeakChanges(this, blockpos)) {
                  blockstate.neighborChanged(this, blockpos, p_175666_2_, p_175666_1_, false);
               }
            }
         }
      }

   }

   public DifficultyInstance getDifficultyForLocation(BlockPos p_175649_1_) {
      long i = 0L;
      float f = 0.0F;
      if (this.isBlockLoaded(p_175649_1_)) {
         f = this.getCurrentMoonPhaseFactor();
         i = this.getChunkAt(p_175649_1_).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), i, f);
   }

   public int getSkylightSubtracted() {
      return this.skylightSubtracted;
   }

   public void func_225605_c_(int p_225605_1_) {
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public void sendPacketToServer(IPacket<?> p_184135_1_) {
      throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
   }

   public Dimension getDimension() {
      return this.dimension;
   }

   public Random getRandom() {
      return this.rand;
   }

   public boolean hasBlockState(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
      return p_217375_2_.test(this.getBlockState(p_217375_1_));
   }

   public abstract RecipeManager getRecipeManager();

   public abstract NetworkTagManager getTags();

   public BlockPos func_217383_a(int p_217383_1_, int p_217383_2_, int p_217383_3_, int p_217383_4_) {
      this.updateLCG = this.updateLCG * 3 + 1013904223;
      int i = this.updateLCG >> 2;
      return new BlockPos(p_217383_1_ + (i & 15), p_217383_2_ + (i >> 16 & p_217383_4_), p_217383_3_ + (i >> 8 & 15));
   }

   public boolean isSaveDisabled() {
      return false;
   }

   public IProfiler getProfiler() {
      return this.profiler;
   }

   public BiomeManager func_225523_d_() {
      return this.field_226689_w_;
   }

   public double getMaxEntityRadius() {
      return this.maxEntityRadius;
   }

   public double increaseMaxEntityRadius(double p_increaseMaxEntityRadius_1_) {
      if (p_increaseMaxEntityRadius_1_ > this.maxEntityRadius) {
         this.maxEntityRadius = p_increaseMaxEntityRadius_1_;
      }

      return this.maxEntityRadius;
   }
}
