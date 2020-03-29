package net.minecraft.client.renderer.chunk;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderCache implements ILightReader {
   protected final int chunkStartX;
   protected final int chunkStartZ;
   protected final BlockPos cacheStartPos;
   protected final int cacheSizeX;
   protected final int cacheSizeY;
   protected final int cacheSizeZ;
   protected final Chunk[][] chunks;
   protected final BlockState[] blockStates;
   protected final IFluidState[] fluidStates;
   protected final World world;

   @Nullable
   public static ChunkRenderCache generateCache(World p_212397_0_, BlockPos p_212397_1_, BlockPos p_212397_2_, int p_212397_3_) {
      int lvt_4_1_ = p_212397_1_.getX() - p_212397_3_ >> 4;
      int lvt_5_1_ = p_212397_1_.getZ() - p_212397_3_ >> 4;
      int lvt_6_1_ = p_212397_2_.getX() + p_212397_3_ >> 4;
      int lvt_7_1_ = p_212397_2_.getZ() + p_212397_3_ >> 4;
      Chunk[][] lvt_8_1_ = new Chunk[lvt_6_1_ - lvt_4_1_ + 1][lvt_7_1_ - lvt_5_1_ + 1];

      int lvt_10_2_;
      for(int lvt_9_1_ = lvt_4_1_; lvt_9_1_ <= lvt_6_1_; ++lvt_9_1_) {
         for(lvt_10_2_ = lvt_5_1_; lvt_10_2_ <= lvt_7_1_; ++lvt_10_2_) {
            lvt_8_1_[lvt_9_1_ - lvt_4_1_][lvt_10_2_ - lvt_5_1_] = p_212397_0_.getChunk(lvt_9_1_, lvt_10_2_);
         }
      }

      boolean lvt_9_2_ = true;

      for(lvt_10_2_ = p_212397_1_.getX() >> 4; lvt_10_2_ <= p_212397_2_.getX() >> 4; ++lvt_10_2_) {
         for(int lvt_11_1_ = p_212397_1_.getZ() >> 4; lvt_11_1_ <= p_212397_2_.getZ() >> 4; ++lvt_11_1_) {
            Chunk lvt_12_1_ = lvt_8_1_[lvt_10_2_ - lvt_4_1_][lvt_11_1_ - lvt_5_1_];
            if (!lvt_12_1_.isEmptyBetween(p_212397_1_.getY(), p_212397_2_.getY())) {
               lvt_9_2_ = false;
            }
         }
      }

      if (lvt_9_2_) {
         return null;
      } else {
         int lvt_10_3_ = true;
         BlockPos lvt_11_2_ = p_212397_1_.add(-1, -1, -1);
         BlockPos lvt_12_2_ = p_212397_2_.add(1, 1, 1);
         return new ChunkRenderCache(p_212397_0_, lvt_4_1_, lvt_5_1_, lvt_8_1_, lvt_11_2_, lvt_12_2_);
      }
   }

   public ChunkRenderCache(World p_i49840_1_, int p_i49840_2_, int p_i49840_3_, Chunk[][] p_i49840_4_, BlockPos p_i49840_5_, BlockPos p_i49840_6_) {
      this.world = p_i49840_1_;
      this.chunkStartX = p_i49840_2_;
      this.chunkStartZ = p_i49840_3_;
      this.chunks = p_i49840_4_;
      this.cacheStartPos = p_i49840_5_;
      this.cacheSizeX = p_i49840_6_.getX() - p_i49840_5_.getX() + 1;
      this.cacheSizeY = p_i49840_6_.getY() - p_i49840_5_.getY() + 1;
      this.cacheSizeZ = p_i49840_6_.getZ() - p_i49840_5_.getZ() + 1;
      this.blockStates = new BlockState[this.cacheSizeX * this.cacheSizeY * this.cacheSizeZ];
      this.fluidStates = new IFluidState[this.cacheSizeX * this.cacheSizeY * this.cacheSizeZ];

      BlockPos lvt_8_1_;
      Chunk lvt_11_1_;
      int lvt_12_1_;
      for(Iterator var7 = BlockPos.getAllInBoxMutable(p_i49840_5_, p_i49840_6_).iterator(); var7.hasNext(); this.fluidStates[lvt_12_1_] = lvt_11_1_.getFluidState(lvt_8_1_)) {
         lvt_8_1_ = (BlockPos)var7.next();
         int lvt_9_1_ = (lvt_8_1_.getX() >> 4) - p_i49840_2_;
         int lvt_10_1_ = (lvt_8_1_.getZ() >> 4) - p_i49840_3_;
         lvt_11_1_ = p_i49840_4_[lvt_9_1_][lvt_10_1_];
         lvt_12_1_ = this.getIndex(lvt_8_1_);
         this.blockStates[lvt_12_1_] = lvt_11_1_.getBlockState(lvt_8_1_);
      }

   }

   protected final int getIndex(BlockPos p_212398_1_) {
      return this.getIndex(p_212398_1_.getX(), p_212398_1_.getY(), p_212398_1_.getZ());
   }

   protected int getIndex(int p_217339_1_, int p_217339_2_, int p_217339_3_) {
      int lvt_4_1_ = p_217339_1_ - this.cacheStartPos.getX();
      int lvt_5_1_ = p_217339_2_ - this.cacheStartPos.getY();
      int lvt_6_1_ = p_217339_3_ - this.cacheStartPos.getZ();
      return lvt_6_1_ * this.cacheSizeX * this.cacheSizeY + lvt_5_1_ * this.cacheSizeX + lvt_4_1_;
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      return this.blockStates[this.getIndex(p_180495_1_)];
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      return this.fluidStates[this.getIndex(p_204610_1_)];
   }

   public WorldLightManager func_225524_e_() {
      return this.world.func_225524_e_();
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      return this.getTileEntity(p_175625_1_, Chunk.CreateEntityType.IMMEDIATE);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_212399_1_, Chunk.CreateEntityType p_212399_2_) {
      int lvt_3_1_ = (p_212399_1_.getX() >> 4) - this.chunkStartX;
      int lvt_4_1_ = (p_212399_1_.getZ() >> 4) - this.chunkStartZ;
      return this.chunks[lvt_3_1_][lvt_4_1_].getTileEntity(p_212399_1_, p_212399_2_);
   }

   public int func_225525_a_(BlockPos p_225525_1_, ColorResolver p_225525_2_) {
      return this.world.func_225525_a_(p_225525_1_, p_225525_2_);
   }
}
