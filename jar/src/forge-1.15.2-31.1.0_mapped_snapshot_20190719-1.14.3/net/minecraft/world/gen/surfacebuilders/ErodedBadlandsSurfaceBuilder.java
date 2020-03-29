package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState ORANGE_TERRACOTTA;
   private static final BlockState TERRACOTTA;

   public ErodedBadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51314_1_) {
      super(p_i51314_1_);
   }

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      double lvt_15_1_ = 0.0D;
      double lvt_17_1_ = Math.min(Math.abs(p_205610_7_), this.field_215435_c.func_215464_a((double)p_205610_4_ * 0.25D, (double)p_205610_5_ * 0.25D, false) * 15.0D);
      if (lvt_17_1_ > 0.0D) {
         double lvt_19_1_ = 0.001953125D;
         double lvt_21_1_ = Math.abs(this.field_215437_d.func_215464_a((double)p_205610_4_ * 0.001953125D, (double)p_205610_5_ * 0.001953125D, false));
         lvt_15_1_ = lvt_17_1_ * lvt_17_1_ * 2.5D;
         double lvt_23_1_ = Math.ceil(lvt_21_1_ * 50.0D) + 14.0D;
         if (lvt_15_1_ > lvt_23_1_) {
            lvt_15_1_ = lvt_23_1_;
         }

         lvt_15_1_ += 64.0D;
      }

      int lvt_19_2_ = p_205610_4_ & 15;
      int lvt_20_1_ = p_205610_5_ & 15;
      BlockState lvt_21_2_ = WHITE_TERRACOTTA;
      BlockState lvt_22_1_ = p_205610_3_.getSurfaceBuilderConfig().getUnder();
      int lvt_23_2_ = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      boolean lvt_24_1_ = Math.cos(p_205610_7_ / 3.0D * 3.141592653589793D) > 0.0D;
      int lvt_25_1_ = -1;
      boolean lvt_26_1_ = false;
      BlockPos.Mutable lvt_27_1_ = new BlockPos.Mutable();

      for(int lvt_28_1_ = Math.max(p_205610_6_, (int)lvt_15_1_ + 1); lvt_28_1_ >= 0; --lvt_28_1_) {
         lvt_27_1_.setPos(lvt_19_2_, lvt_28_1_, lvt_20_1_);
         if (p_205610_2_.getBlockState(lvt_27_1_).isAir() && lvt_28_1_ < (int)lvt_15_1_) {
            p_205610_2_.setBlockState(lvt_27_1_, p_205610_9_, false);
         }

         BlockState lvt_29_1_ = p_205610_2_.getBlockState(lvt_27_1_);
         if (lvt_29_1_.isAir()) {
            lvt_25_1_ = -1;
         } else if (lvt_29_1_.getBlock() == p_205610_9_.getBlock()) {
            if (lvt_25_1_ == -1) {
               lvt_26_1_ = false;
               if (lvt_23_2_ <= 0) {
                  lvt_21_2_ = Blocks.AIR.getDefaultState();
                  lvt_22_1_ = p_205610_9_;
               } else if (lvt_28_1_ >= p_205610_11_ - 4 && lvt_28_1_ <= p_205610_11_ + 1) {
                  lvt_21_2_ = WHITE_TERRACOTTA;
                  lvt_22_1_ = p_205610_3_.getSurfaceBuilderConfig().getUnder();
               }

               if (lvt_28_1_ < p_205610_11_ && (lvt_21_2_ == null || lvt_21_2_.isAir())) {
                  lvt_21_2_ = p_205610_10_;
               }

               lvt_25_1_ = lvt_23_2_ + Math.max(0, lvt_28_1_ - p_205610_11_);
               if (lvt_28_1_ >= p_205610_11_ - 1) {
                  if (lvt_28_1_ <= p_205610_11_ + 3 + lvt_23_2_) {
                     p_205610_2_.setBlockState(lvt_27_1_, p_205610_3_.getSurfaceBuilderConfig().getTop(), false);
                     lvt_26_1_ = true;
                  } else {
                     BlockState lvt_30_3_;
                     if (lvt_28_1_ >= 64 && lvt_28_1_ <= 127) {
                        if (lvt_24_1_) {
                           lvt_30_3_ = TERRACOTTA;
                        } else {
                           lvt_30_3_ = this.func_215431_a(p_205610_4_, lvt_28_1_, p_205610_5_);
                        }
                     } else {
                        lvt_30_3_ = ORANGE_TERRACOTTA;
                     }

                     p_205610_2_.setBlockState(lvt_27_1_, lvt_30_3_, false);
                  }
               } else {
                  p_205610_2_.setBlockState(lvt_27_1_, lvt_22_1_, false);
                  Block lvt_30_4_ = lvt_22_1_.getBlock();
                  if (lvt_30_4_ == Blocks.WHITE_TERRACOTTA || lvt_30_4_ == Blocks.ORANGE_TERRACOTTA || lvt_30_4_ == Blocks.MAGENTA_TERRACOTTA || lvt_30_4_ == Blocks.LIGHT_BLUE_TERRACOTTA || lvt_30_4_ == Blocks.YELLOW_TERRACOTTA || lvt_30_4_ == Blocks.LIME_TERRACOTTA || lvt_30_4_ == Blocks.PINK_TERRACOTTA || lvt_30_4_ == Blocks.GRAY_TERRACOTTA || lvt_30_4_ == Blocks.LIGHT_GRAY_TERRACOTTA || lvt_30_4_ == Blocks.CYAN_TERRACOTTA || lvt_30_4_ == Blocks.PURPLE_TERRACOTTA || lvt_30_4_ == Blocks.BLUE_TERRACOTTA || lvt_30_4_ == Blocks.BROWN_TERRACOTTA || lvt_30_4_ == Blocks.GREEN_TERRACOTTA || lvt_30_4_ == Blocks.RED_TERRACOTTA || lvt_30_4_ == Blocks.BLACK_TERRACOTTA) {
                     p_205610_2_.setBlockState(lvt_27_1_, ORANGE_TERRACOTTA, false);
                  }
               }
            } else if (lvt_25_1_ > 0) {
               --lvt_25_1_;
               if (lvt_26_1_) {
                  p_205610_2_.setBlockState(lvt_27_1_, ORANGE_TERRACOTTA, false);
               } else {
                  p_205610_2_.setBlockState(lvt_27_1_, this.func_215431_a(p_205610_4_, lvt_28_1_, p_205610_5_), false);
               }
            }
         }
      }

   }

   static {
      WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
      ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
      TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();
   }
}
