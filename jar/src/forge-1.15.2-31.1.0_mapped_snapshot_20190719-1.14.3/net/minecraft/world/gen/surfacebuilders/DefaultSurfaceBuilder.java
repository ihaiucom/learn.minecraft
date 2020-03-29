package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class DefaultSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   public DefaultSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51315_1_) {
      super(p_i51315_1_);
   }

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      this.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_14_.getTop(), p_205610_14_.getUnder(), p_205610_14_.getUnderWaterMaterial(), p_205610_11_);
   }

   protected void buildSurface(Random p_206967_1_, IChunk p_206967_2_, Biome p_206967_3_, int p_206967_4_, int p_206967_5_, int p_206967_6_, double p_206967_7_, BlockState p_206967_9_, BlockState p_206967_10_, BlockState p_206967_11_, BlockState p_206967_12_, BlockState p_206967_13_, int p_206967_14_) {
      BlockState lvt_15_1_ = p_206967_11_;
      BlockState lvt_16_1_ = p_206967_12_;
      BlockPos.Mutable lvt_17_1_ = new BlockPos.Mutable();
      int lvt_18_1_ = -1;
      int lvt_19_1_ = (int)(p_206967_7_ / 3.0D + 3.0D + p_206967_1_.nextDouble() * 0.25D);
      int lvt_20_1_ = p_206967_4_ & 15;
      int lvt_21_1_ = p_206967_5_ & 15;

      for(int lvt_22_1_ = p_206967_6_; lvt_22_1_ >= 0; --lvt_22_1_) {
         lvt_17_1_.setPos(lvt_20_1_, lvt_22_1_, lvt_21_1_);
         BlockState lvt_23_1_ = p_206967_2_.getBlockState(lvt_17_1_);
         if (lvt_23_1_.isAir()) {
            lvt_18_1_ = -1;
         } else if (lvt_23_1_.getBlock() == p_206967_9_.getBlock()) {
            if (lvt_18_1_ == -1) {
               if (lvt_19_1_ <= 0) {
                  lvt_15_1_ = Blocks.AIR.getDefaultState();
                  lvt_16_1_ = p_206967_9_;
               } else if (lvt_22_1_ >= p_206967_14_ - 4 && lvt_22_1_ <= p_206967_14_ + 1) {
                  lvt_15_1_ = p_206967_11_;
                  lvt_16_1_ = p_206967_12_;
               }

               if (lvt_22_1_ < p_206967_14_ && (lvt_15_1_ == null || lvt_15_1_.isAir())) {
                  if (p_206967_3_.func_225486_c(lvt_17_1_.setPos(p_206967_4_, lvt_22_1_, p_206967_5_)) < 0.15F) {
                     lvt_15_1_ = Blocks.ICE.getDefaultState();
                  } else {
                     lvt_15_1_ = p_206967_10_;
                  }

                  lvt_17_1_.setPos(lvt_20_1_, lvt_22_1_, lvt_21_1_);
               }

               lvt_18_1_ = lvt_19_1_;
               if (lvt_22_1_ >= p_206967_14_ - 1) {
                  p_206967_2_.setBlockState(lvt_17_1_, lvt_15_1_, false);
               } else if (lvt_22_1_ < p_206967_14_ - 7 - lvt_19_1_) {
                  lvt_15_1_ = Blocks.AIR.getDefaultState();
                  lvt_16_1_ = p_206967_9_;
                  p_206967_2_.setBlockState(lvt_17_1_, p_206967_13_, false);
               } else {
                  p_206967_2_.setBlockState(lvt_17_1_, lvt_16_1_, false);
               }
            } else if (lvt_18_1_ > 0) {
               --lvt_18_1_;
               p_206967_2_.setBlockState(lvt_17_1_, lvt_16_1_, false);
               if (lvt_18_1_ == 0 && lvt_16_1_.getBlock() == Blocks.SAND && lvt_19_1_ > 1) {
                  lvt_18_1_ = p_206967_1_.nextInt(4) + Math.max(0, lvt_22_1_ - 63);
                  lvt_16_1_ = lvt_16_1_.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
               }
            }
         }
      }

   }
}
