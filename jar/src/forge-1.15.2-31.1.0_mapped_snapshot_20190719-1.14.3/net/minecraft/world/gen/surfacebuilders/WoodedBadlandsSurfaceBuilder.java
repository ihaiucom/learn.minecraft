package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class WoodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState ORANGE_TERRACOTTA;
   private static final BlockState TERRACOTTA;

   public WoodedBadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51303_1_) {
      super(p_i51303_1_);
   }

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      int lvt_15_1_ = p_205610_4_ & 15;
      int lvt_16_1_ = p_205610_5_ & 15;
      BlockState lvt_17_1_ = WHITE_TERRACOTTA;
      BlockState lvt_18_1_ = p_205610_3_.getSurfaceBuilderConfig().getUnder();
      int lvt_19_1_ = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      boolean lvt_20_1_ = Math.cos(p_205610_7_ / 3.0D * 3.141592653589793D) > 0.0D;
      int lvt_21_1_ = -1;
      boolean lvt_22_1_ = false;
      int lvt_23_1_ = 0;
      BlockPos.Mutable lvt_24_1_ = new BlockPos.Mutable();

      for(int lvt_25_1_ = p_205610_6_; lvt_25_1_ >= 0; --lvt_25_1_) {
         if (lvt_23_1_ < 15) {
            lvt_24_1_.setPos(lvt_15_1_, lvt_25_1_, lvt_16_1_);
            BlockState lvt_26_1_ = p_205610_2_.getBlockState(lvt_24_1_);
            if (lvt_26_1_.isAir()) {
               lvt_21_1_ = -1;
            } else if (lvt_26_1_.getBlock() == p_205610_9_.getBlock()) {
               if (lvt_21_1_ == -1) {
                  lvt_22_1_ = false;
                  if (lvt_19_1_ <= 0) {
                     lvt_17_1_ = Blocks.AIR.getDefaultState();
                     lvt_18_1_ = p_205610_9_;
                  } else if (lvt_25_1_ >= p_205610_11_ - 4 && lvt_25_1_ <= p_205610_11_ + 1) {
                     lvt_17_1_ = WHITE_TERRACOTTA;
                     lvt_18_1_ = p_205610_3_.getSurfaceBuilderConfig().getUnder();
                  }

                  if (lvt_25_1_ < p_205610_11_ && (lvt_17_1_ == null || lvt_17_1_.isAir())) {
                     lvt_17_1_ = p_205610_10_;
                  }

                  lvt_21_1_ = lvt_19_1_ + Math.max(0, lvt_25_1_ - p_205610_11_);
                  if (lvt_25_1_ >= p_205610_11_ - 1) {
                     if (lvt_25_1_ > 86 + lvt_19_1_ * 2) {
                        if (lvt_20_1_) {
                           p_205610_2_.setBlockState(lvt_24_1_, Blocks.COARSE_DIRT.getDefaultState(), false);
                        } else {
                           p_205610_2_.setBlockState(lvt_24_1_, Blocks.GRASS_BLOCK.getDefaultState(), false);
                        }
                     } else if (lvt_25_1_ > p_205610_11_ + 3 + lvt_19_1_) {
                        BlockState lvt_27_3_;
                        if (lvt_25_1_ >= 64 && lvt_25_1_ <= 127) {
                           if (lvt_20_1_) {
                              lvt_27_3_ = TERRACOTTA;
                           } else {
                              lvt_27_3_ = this.func_215431_a(p_205610_4_, lvt_25_1_, p_205610_5_);
                           }
                        } else {
                           lvt_27_3_ = ORANGE_TERRACOTTA;
                        }

                        p_205610_2_.setBlockState(lvt_24_1_, lvt_27_3_, false);
                     } else {
                        p_205610_2_.setBlockState(lvt_24_1_, p_205610_3_.getSurfaceBuilderConfig().getTop(), false);
                        lvt_22_1_ = true;
                     }
                  } else {
                     p_205610_2_.setBlockState(lvt_24_1_, lvt_18_1_, false);
                     if (lvt_18_1_ == WHITE_TERRACOTTA) {
                        p_205610_2_.setBlockState(lvt_24_1_, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (lvt_21_1_ > 0) {
                  --lvt_21_1_;
                  if (lvt_22_1_) {
                     p_205610_2_.setBlockState(lvt_24_1_, ORANGE_TERRACOTTA, false);
                  } else {
                     p_205610_2_.setBlockState(lvt_24_1_, this.func_215431_a(p_205610_4_, lvt_25_1_, p_205610_5_), false);
                  }
               }

               ++lvt_23_1_;
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
