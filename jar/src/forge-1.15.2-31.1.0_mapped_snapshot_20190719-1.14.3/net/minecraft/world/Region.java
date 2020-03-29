package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunk;

public class Region implements IBlockReader, ICollisionReader {
   protected final int chunkX;
   protected final int chunkZ;
   protected final IChunk[][] chunks;
   protected boolean empty;
   protected final World world;

   public Region(World p_i50004_1_, BlockPos p_i50004_2_, BlockPos p_i50004_3_) {
      this.world = p_i50004_1_;
      this.chunkX = p_i50004_2_.getX() >> 4;
      this.chunkZ = p_i50004_2_.getZ() >> 4;
      int lvt_4_1_ = p_i50004_3_.getX() >> 4;
      int lvt_5_1_ = p_i50004_3_.getZ() >> 4;
      this.chunks = new IChunk[lvt_4_1_ - this.chunkX + 1][lvt_5_1_ - this.chunkZ + 1];
      AbstractChunkProvider lvt_6_1_ = p_i50004_1_.getChunkProvider();
      this.empty = true;

      int lvt_7_2_;
      int lvt_8_2_;
      for(lvt_7_2_ = this.chunkX; lvt_7_2_ <= lvt_4_1_; ++lvt_7_2_) {
         for(lvt_8_2_ = this.chunkZ; lvt_8_2_ <= lvt_5_1_; ++lvt_8_2_) {
            this.chunks[lvt_7_2_ - this.chunkX][lvt_8_2_ - this.chunkZ] = lvt_6_1_.func_225313_a(lvt_7_2_, lvt_8_2_);
         }
      }

      for(lvt_7_2_ = p_i50004_2_.getX() >> 4; lvt_7_2_ <= p_i50004_3_.getX() >> 4; ++lvt_7_2_) {
         for(lvt_8_2_ = p_i50004_2_.getZ() >> 4; lvt_8_2_ <= p_i50004_3_.getZ() >> 4; ++lvt_8_2_) {
            IChunk lvt_9_1_ = this.chunks[lvt_7_2_ - this.chunkX][lvt_8_2_ - this.chunkZ];
            if (lvt_9_1_ != null && !lvt_9_1_.isEmptyBetween(p_i50004_2_.getY(), p_i50004_3_.getY())) {
               this.empty = false;
               return;
            }
         }
      }

   }

   private IChunk func_226703_d_(BlockPos p_226703_1_) {
      return this.func_226702_a_(p_226703_1_.getX() >> 4, p_226703_1_.getZ() >> 4);
   }

   private IChunk func_226702_a_(int p_226702_1_, int p_226702_2_) {
      int lvt_3_1_ = p_226702_1_ - this.chunkX;
      int lvt_4_1_ = p_226702_2_ - this.chunkZ;
      if (lvt_3_1_ >= 0 && lvt_3_1_ < this.chunks.length && lvt_4_1_ >= 0 && lvt_4_1_ < this.chunks[lvt_3_1_].length) {
         IChunk lvt_5_1_ = this.chunks[lvt_3_1_][lvt_4_1_];
         return (IChunk)(lvt_5_1_ != null ? lvt_5_1_ : new EmptyChunk(this.world, new ChunkPos(p_226702_1_, p_226702_2_)));
      } else {
         return new EmptyChunk(this.world, new ChunkPos(p_226702_1_, p_226702_2_));
      }
   }

   public WorldBorder getWorldBorder() {
      return this.world.getWorldBorder();
   }

   public IBlockReader func_225522_c_(int p_225522_1_, int p_225522_2_) {
      return this.func_226702_a_(p_225522_1_, p_225522_2_);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      IChunk lvt_2_1_ = this.func_226703_d_(p_175625_1_);
      return lvt_2_1_.getTileEntity(p_175625_1_);
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      if (World.isOutsideBuildHeight(p_180495_1_)) {
         return Blocks.AIR.getDefaultState();
      } else {
         IChunk lvt_2_1_ = this.func_226703_d_(p_180495_1_);
         return lvt_2_1_.getBlockState(p_180495_1_);
      }
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      if (World.isOutsideBuildHeight(p_204610_1_)) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         IChunk lvt_2_1_ = this.func_226703_d_(p_204610_1_);
         return lvt_2_1_.getFluidState(p_204610_1_);
      }
   }
}
