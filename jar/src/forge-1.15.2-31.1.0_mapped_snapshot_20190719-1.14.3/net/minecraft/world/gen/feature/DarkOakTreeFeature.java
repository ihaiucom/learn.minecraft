package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;

public class DarkOakTreeFeature extends AbstractTreeFeature<HugeTreeFeatureConfig> {
   public DarkOakTreeFeature(Function<Dynamic<?>, ? extends HugeTreeFeatureConfig> p_i225800_1_) {
      super(p_i225800_1_);
   }

   public boolean func_225557_a_(IWorldGenerationReader p_225557_1_, Random p_225557_2_, BlockPos p_225557_3_, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox p_225557_6_, HugeTreeFeatureConfig p_225557_7_) {
      int i = p_225557_2_.nextInt(3) + p_225557_2_.nextInt(2) + p_225557_7_.field_227371_p_;
      int j = p_225557_3_.getX();
      int k = p_225557_3_.getY();
      int l = p_225557_3_.getZ();
      if (k >= 1 && k + i + 1 < p_225557_1_.getMaxHeight()) {
         BlockPos blockpos = p_225557_3_.down();
         if (!isSoil(p_225557_1_, blockpos, p_225557_7_.getSapling())) {
            return false;
         } else if (!this.func_214615_a(p_225557_1_, p_225557_3_, i)) {
            return false;
         } else {
            this.setDirtAt(p_225557_1_, blockpos, p_225557_3_);
            this.setDirtAt(p_225557_1_, blockpos.east(), p_225557_3_);
            this.setDirtAt(p_225557_1_, blockpos.south(), p_225557_3_);
            this.setDirtAt(p_225557_1_, blockpos.south().east(), p_225557_3_);
            Direction direction = Direction.Plane.HORIZONTAL.random(p_225557_2_);
            int i1 = i - p_225557_2_.nextInt(4);
            int j1 = 2 - p_225557_2_.nextInt(3);
            int k1 = j;
            int l1 = l;
            int i2 = k + i - 1;

            int l3;
            int k4;
            for(l3 = 0; l3 < i; ++l3) {
               if (l3 >= i1 && j1 > 0) {
                  k1 += direction.getXOffset();
                  l1 += direction.getZOffset();
                  --j1;
               }

               k4 = k + l3;
               BlockPos blockpos1 = new BlockPos(k1, k4, l1);
               if (isAirOrLeaves(p_225557_1_, blockpos1)) {
                  this.func_227216_a_(p_225557_1_, p_225557_2_, blockpos1, p_225557_4_, p_225557_6_, p_225557_7_);
                  this.func_227216_a_(p_225557_1_, p_225557_2_, blockpos1.east(), p_225557_4_, p_225557_6_, p_225557_7_);
                  this.func_227216_a_(p_225557_1_, p_225557_2_, blockpos1.south(), p_225557_4_, p_225557_6_, p_225557_7_);
                  this.func_227216_a_(p_225557_1_, p_225557_2_, blockpos1.east().south(), p_225557_4_, p_225557_6_, p_225557_7_);
               }
            }

            for(l3 = -2; l3 <= 0; ++l3) {
               for(k4 = -2; k4 <= 0; ++k4) {
                  int l4 = -1;
                  this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1 + l3, i2 + l4, l1 + k4), p_225557_5_, p_225557_6_, p_225557_7_);
                  this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(1 + k1 - l3, i2 + l4, l1 + k4), p_225557_5_, p_225557_6_, p_225557_7_);
                  this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1 + l3, i2 + l4, 1 + l1 - k4), p_225557_5_, p_225557_6_, p_225557_7_);
                  this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(1 + k1 - l3, i2 + l4, 1 + l1 - k4), p_225557_5_, p_225557_6_, p_225557_7_);
                  if ((l3 > -2 || k4 > -1) && (l3 != -1 || k4 != -2)) {
                     int l4 = 1;
                     this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1 + l3, i2 + l4, l1 + k4), p_225557_5_, p_225557_6_, p_225557_7_);
                     this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(1 + k1 - l3, i2 + l4, l1 + k4), p_225557_5_, p_225557_6_, p_225557_7_);
                     this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1 + l3, i2 + l4, 1 + l1 - k4), p_225557_5_, p_225557_6_, p_225557_7_);
                     this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(1 + k1 - l3, i2 + l4, 1 + l1 - k4), p_225557_5_, p_225557_6_, p_225557_7_);
                  }
               }
            }

            if (p_225557_2_.nextBoolean()) {
               this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1, i2 + 2, l1), p_225557_5_, p_225557_6_, p_225557_7_);
               this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1 + 1, i2 + 2, l1), p_225557_5_, p_225557_6_, p_225557_7_);
               this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1 + 1, i2 + 2, l1 + 1), p_225557_5_, p_225557_6_, p_225557_7_);
               this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1, i2 + 2, l1 + 1), p_225557_5_, p_225557_6_, p_225557_7_);
            }

            for(l3 = -3; l3 <= 4; ++l3) {
               for(k4 = -3; k4 <= 4; ++k4) {
                  if ((l3 != -3 || k4 != -3) && (l3 != -3 || k4 != 4) && (l3 != 4 || k4 != -3) && (l3 != 4 || k4 != 4) && (Math.abs(l3) < 3 || Math.abs(k4) < 3)) {
                     this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1 + l3, i2, l1 + k4), p_225557_5_, p_225557_6_, p_225557_7_);
                  }
               }
            }

            for(l3 = -1; l3 <= 2; ++l3) {
               for(k4 = -1; k4 <= 2; ++k4) {
                  if ((l3 < 0 || l3 > 1 || k4 < 0 || k4 > 1) && p_225557_2_.nextInt(3) <= 0) {
                     int i5 = p_225557_2_.nextInt(3) + 2;

                     int k5;
                     for(k5 = 0; k5 < i5; ++k5) {
                        this.func_227216_a_(p_225557_1_, p_225557_2_, new BlockPos(j + l3, i2 - k5 - 1, l + k4), p_225557_4_, p_225557_6_, p_225557_7_);
                     }

                     int l5;
                     for(k5 = -1; k5 <= 1; ++k5) {
                        for(l5 = -1; l5 <= 1; ++l5) {
                           this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1 + l3 + k5, i2, l1 + k4 + l5), p_225557_5_, p_225557_6_, p_225557_7_);
                        }
                     }

                     for(k5 = -2; k5 <= 2; ++k5) {
                        for(l5 = -2; l5 <= 2; ++l5) {
                           if (Math.abs(k5) != 2 || Math.abs(l5) != 2) {
                              this.func_227219_b_(p_225557_1_, p_225557_2_, new BlockPos(k1 + l3 + k5, i2 - 1, l1 + k4 + l5), p_225557_5_, p_225557_6_, p_225557_7_);
                           }
                        }
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean func_214615_a(IWorldGenerationBaseReader p_214615_1_, BlockPos p_214615_2_, int p_214615_3_) {
      int i = p_214615_2_.getX();
      int j = p_214615_2_.getY();
      int k = p_214615_2_.getZ();
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int l = 0; l <= p_214615_3_ + 1; ++l) {
         int i1 = 1;
         if (l == 0) {
            i1 = 0;
         }

         if (l >= p_214615_3_ - 1) {
            i1 = 2;
         }

         for(int j1 = -i1; j1 <= i1; ++j1) {
            for(int k1 = -i1; k1 <= i1; ++k1) {
               if (!func_214587_a(p_214615_1_, blockpos$mutable.setPos(i + j1, j + l, k + k1))) {
                  return false;
               }
            }
         }
      }

      return true;
   }
}
