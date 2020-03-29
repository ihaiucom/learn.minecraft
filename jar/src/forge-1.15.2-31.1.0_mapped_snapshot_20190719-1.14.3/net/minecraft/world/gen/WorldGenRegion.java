package net.minecraft.world.gen;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldGenTickList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegion implements IWorld {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<IChunk> chunkPrimers;
   private final int mainChunkX;
   private final int mainChunkZ;
   private final int field_217380_e;
   private final ServerWorld world;
   private final long seed;
   private final int seaLevel;
   private final WorldInfo worldInfo;
   private final Random random;
   private final Dimension dimension;
   private final GenerationSettings chunkGenSettings;
   private final ITickList<Block> pendingBlockTickList = new WorldGenTickList((p_lambda$new$0_1_) -> {
      return this.getChunk(p_lambda$new$0_1_).getBlocksToBeTicked();
   });
   private final ITickList<Fluid> pendingFluidTickList = new WorldGenTickList((p_lambda$new$1_1_) -> {
      return this.getChunk(p_lambda$new$1_1_).getFluidsToBeTicked();
   });
   private final BiomeManager field_229862_o_;

   public WorldGenRegion(ServerWorld p_i50698_1_, List<IChunk> p_i50698_2_) {
      int i = MathHelper.floor(Math.sqrt((double)p_i50698_2_.size()));
      if (i * i != p_i50698_2_.size()) {
         throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("Cache size is not a square."));
      } else {
         ChunkPos chunkpos = ((IChunk)p_i50698_2_.get(p_i50698_2_.size() / 2)).getPos();
         this.chunkPrimers = p_i50698_2_;
         this.mainChunkX = chunkpos.x;
         this.mainChunkZ = chunkpos.z;
         this.field_217380_e = i;
         this.world = p_i50698_1_;
         this.seed = p_i50698_1_.getSeed();
         this.chunkGenSettings = p_i50698_1_.getChunkProvider().getChunkGenerator().getSettings();
         this.seaLevel = p_i50698_1_.getSeaLevel();
         this.worldInfo = p_i50698_1_.getWorldInfo();
         this.random = p_i50698_1_.getRandom();
         this.dimension = p_i50698_1_.getDimension();
         this.field_229862_o_ = new BiomeManager(this, WorldInfo.func_227498_c_(this.seed), this.dimension.getType().func_227176_e_());
      }
   }

   public int getMainChunkX() {
      return this.mainChunkX;
   }

   public int getMainChunkZ() {
      return this.mainChunkZ;
   }

   public IChunk getChunk(int p_212866_1_, int p_212866_2_) {
      return this.getChunk(p_212866_1_, p_212866_2_, ChunkStatus.EMPTY);
   }

   @Nullable
   public IChunk getChunk(int p_217353_1_, int p_217353_2_, ChunkStatus p_217353_3_, boolean p_217353_4_) {
      IChunk ichunk;
      if (this.chunkExists(p_217353_1_, p_217353_2_)) {
         ChunkPos chunkpos = ((IChunk)this.chunkPrimers.get(0)).getPos();
         int i = p_217353_1_ - chunkpos.x;
         int j = p_217353_2_ - chunkpos.z;
         ichunk = (IChunk)this.chunkPrimers.get(i + j * this.field_217380_e);
         if (ichunk.getStatus().isAtLeast(p_217353_3_)) {
            return ichunk;
         }
      } else {
         ichunk = null;
      }

      if (!p_217353_4_) {
         return null;
      } else {
         IChunk ichunk1 = (IChunk)this.chunkPrimers.get(0);
         IChunk ichunk2 = (IChunk)this.chunkPrimers.get(this.chunkPrimers.size() - 1);
         LOGGER.error("Requested chunk : {} {}", p_217353_1_, p_217353_2_);
         LOGGER.error("Region bounds : {} {} | {} {}", ichunk1.getPos().x, ichunk1.getPos().z, ichunk2.getPos().x, ichunk2.getPos().z);
         if (ichunk != null) {
            throw (RuntimeException)Util.func_229757_c_(new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", p_217353_3_, ichunk.getStatus(), p_217353_1_, p_217353_2_)));
         } else {
            throw (RuntimeException)Util.func_229757_c_(new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", p_217353_1_, p_217353_2_)));
         }
      }
   }

   public boolean chunkExists(int p_217354_1_, int p_217354_2_) {
      IChunk ichunk = (IChunk)this.chunkPrimers.get(0);
      IChunk ichunk1 = (IChunk)this.chunkPrimers.get(this.chunkPrimers.size() - 1);
      return p_217354_1_ >= ichunk.getPos().x && p_217354_1_ <= ichunk1.getPos().x && p_217354_2_ >= ichunk.getPos().z && p_217354_2_ <= ichunk1.getPos().z;
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      return this.getChunk(p_180495_1_.getX() >> 4, p_180495_1_.getZ() >> 4).getBlockState(p_180495_1_);
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      return this.getChunk(p_204610_1_).getFluidState(p_204610_1_);
   }

   @Nullable
   public PlayerEntity getClosestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, Predicate<Entity> p_190525_9_) {
      return null;
   }

   public int getSkylightSubtracted() {
      return 0;
   }

   public BiomeManager func_225523_d_() {
      return this.field_229862_o_;
   }

   public Biome func_225604_a_(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
      return this.world.func_225604_a_(p_225604_1_, p_225604_2_, p_225604_3_);
   }

   public WorldLightManager func_225524_e_() {
      return this.world.func_225524_e_();
   }

   public boolean func_225521_a_(BlockPos p_225521_1_, boolean p_225521_2_, @Nullable Entity p_225521_3_) {
      BlockState blockstate = this.getBlockState(p_225521_1_);
      if (blockstate.isAir(this, p_225521_1_)) {
         return false;
      } else {
         if (p_225521_2_) {
            TileEntity tileentity = blockstate.hasTileEntity() ? this.getTileEntity(p_225521_1_) : null;
            Block.spawnDrops(blockstate, this.world, p_225521_1_, tileentity, p_225521_3_, ItemStack.EMPTY);
         }

         return this.setBlockState(p_225521_1_, Blocks.AIR.getDefaultState(), 3);
      }
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      IChunk ichunk = this.getChunk(p_175625_1_);
      TileEntity tileentity = ichunk.getTileEntity(p_175625_1_);
      if (tileentity != null) {
         return tileentity;
      } else {
         CompoundNBT compoundnbt = ichunk.getDeferredTileEntity(p_175625_1_);
         if (compoundnbt != null) {
            if ("DUMMY".equals(compoundnbt.getString("id"))) {
               BlockState state = this.getBlockState(p_175625_1_);
               if (!state.hasTileEntity()) {
                  return null;
               }

               tileentity = state.createTileEntity(this.world);
            } else {
               tileentity = TileEntity.create(compoundnbt);
            }

            if (tileentity != null) {
               ichunk.addTileEntity(p_175625_1_, tileentity);
               return tileentity;
            }
         }

         if (ichunk.getBlockState(p_175625_1_).hasTileEntity()) {
            LOGGER.warn("Tried to access a block entity before it was created. {}", p_175625_1_);
         }

         return null;
      }
   }

   public boolean setBlockState(BlockPos p_180501_1_, BlockState p_180501_2_, int p_180501_3_) {
      IChunk ichunk = this.getChunk(p_180501_1_);
      BlockState blockstate = ichunk.setBlockState(p_180501_1_, p_180501_2_, false);
      if (blockstate != null) {
         this.world.func_217393_a(p_180501_1_, blockstate, p_180501_2_);
      }

      Block block = p_180501_2_.getBlock();
      if (p_180501_2_.hasTileEntity()) {
         if (ichunk.getStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
            ichunk.addTileEntity(p_180501_1_, p_180501_2_.createTileEntity(this));
         } else {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putInt("x", p_180501_1_.getX());
            compoundnbt.putInt("y", p_180501_1_.getY());
            compoundnbt.putInt("z", p_180501_1_.getZ());
            compoundnbt.putString("id", "DUMMY");
            ichunk.addTileEntity(compoundnbt);
         }
      } else if (blockstate != null && blockstate.hasTileEntity()) {
         ichunk.removeTileEntity(p_180501_1_);
      }

      if (p_180501_2_.blockNeedsPostProcessing(this, p_180501_1_)) {
         this.markBlockForPostprocessing(p_180501_1_);
      }

      return true;
   }

   private void markBlockForPostprocessing(BlockPos p_201683_1_) {
      this.getChunk(p_201683_1_).markBlockForPostprocessing(p_201683_1_);
   }

   public boolean addEntity(Entity p_217376_1_) {
      int i = MathHelper.floor(p_217376_1_.func_226277_ct_() / 16.0D);
      int j = MathHelper.floor(p_217376_1_.func_226281_cx_() / 16.0D);
      this.getChunk(i, j).addEntity(p_217376_1_);
      return true;
   }

   public boolean removeBlock(BlockPos p_217377_1_, boolean p_217377_2_) {
      return this.setBlockState(p_217377_1_, Blocks.AIR.getDefaultState(), 3);
   }

   public WorldBorder getWorldBorder() {
      return this.world.getWorldBorder();
   }

   public boolean isRemote() {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public ServerWorld getWorld() {
      return this.world;
   }

   public WorldInfo getWorldInfo() {
      return this.worldInfo;
   }

   public DifficultyInstance getDifficultyForLocation(BlockPos p_175649_1_) {
      if (!this.chunkExists(p_175649_1_.getX() >> 4, p_175649_1_.getZ() >> 4)) {
         throw new RuntimeException("We are asking a region for a chunk out of bound");
      } else {
         return new DifficultyInstance(this.world.getDifficulty(), this.world.getDayTime(), 0L, this.world.getCurrentMoonPhaseFactor());
      }
   }

   public AbstractChunkProvider getChunkProvider() {
      return this.world.getChunkProvider();
   }

   public long getSeed() {
      return this.seed;
   }

   public ITickList<Block> getPendingBlockTicks() {
      return this.pendingBlockTickList;
   }

   public ITickList<Fluid> getPendingFluidTicks() {
      return this.pendingFluidTickList;
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public Random getRandom() {
      return this.random;
   }

   public void notifyNeighbors(BlockPos p_195592_1_, Block p_195592_2_) {
   }

   public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
      return this.getChunk(p_201676_2_ >> 4, p_201676_3_ >> 4).getTopBlockY(p_201676_1_, p_201676_2_ & 15, p_201676_3_ & 15) + 1;
   }

   public void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_) {
   }

   public void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
   }

   public void playEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getSpawnPoint() {
      return this.world.getSpawnPoint();
   }

   public Dimension getDimension() {
      return this.dimension;
   }

   public boolean hasBlockState(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
      return p_217375_2_.test(this.getBlockState(p_217375_1_));
   }

   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_175647_1_, AxisAlignedBB p_175647_2_, @Nullable Predicate<? super T> p_175647_3_) {
      return Collections.emptyList();
   }

   public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity p_175674_1_, AxisAlignedBB p_175674_2_, @Nullable Predicate<? super Entity> p_175674_3_) {
      return Collections.emptyList();
   }

   public List<PlayerEntity> getPlayers() {
      return Collections.emptyList();
   }
}
