package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraftforge.common.IPlantable;

public abstract class HugeTreesFeature<T extends BaseTreeFeatureConfig> extends AbstractTreeFeature<T> {
   public HugeTreesFeature(Function<Dynamic<?>, ? extends T> p_i225810_1_) {
      super(p_i225810_1_);
   }

   protected int func_227256_a_(Random p_227256_1_, HugeTreeFeatureConfig p_227256_2_) {
      int i = p_227256_1_.nextInt(3) + p_227256_2_.field_227371_p_;
      if (p_227256_2_.field_227275_a_ > 1) {
         i += p_227256_1_.nextInt(p_227256_2_.field_227275_a_);
      }

      return i;
   }

   private boolean isSpaceAt(IWorldGenerationBaseReader p_175926_1_, BlockPos p_175926_2_, int p_175926_3_) {
      boolean flag = true;
      if (p_175926_2_.getY() >= 1 && p_175926_2_.getY() + p_175926_3_ + 1 <= p_175926_1_.getMaxHeight()) {
         for(int i = 0; i <= 1 + p_175926_3_; ++i) {
            int j = 2;
            if (i == 0) {
               j = 1;
            } else if (i >= 1 + p_175926_3_ - 2) {
               j = 2;
            }

            for(int k = -j; k <= j && flag; ++k) {
               for(int l = -j; l <= j && flag; ++l) {
                  if (p_175926_2_.getY() + i < 0 || p_175926_2_.getY() + i >= p_175926_1_.getMaxHeight() || !func_214587_a(p_175926_1_, p_175926_2_.add(k, i, l))) {
                     flag = false;
                  }
               }
            }
         }

         return flag;
      } else {
         return false;
      }
   }

   private boolean validSoil(IWorldGenerationReader p_validSoil_1_, BlockPos p_validSoil_2_, IPlantable p_validSoil_3_) {
      BlockPos blockpos = p_validSoil_2_.down();
      if (isSoil(p_validSoil_1_, blockpos, p_validSoil_3_) && p_validSoil_2_.getY() >= 2) {
         this.setDirtAt(p_validSoil_1_, blockpos, p_validSoil_2_);
         this.setDirtAt(p_validSoil_1_, blockpos.east(), p_validSoil_2_);
         this.setDirtAt(p_validSoil_1_, blockpos.south(), p_validSoil_2_);
         this.setDirtAt(p_validSoil_1_, blockpos.south().east(), p_validSoil_2_);
         return true;
      } else {
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   protected boolean func_203427_a(IWorldGenerationReader p_203427_1_, BlockPos p_203427_2_, int p_203427_3_) {
      return this.isSpaceAt(p_203427_1_, p_203427_2_, p_203427_3_) && this.validSoil(p_203427_1_, p_203427_2_, (IPlantable)Blocks.OAK_SAPLING);
   }

   protected boolean hasRoom(IWorldGenerationReader p_hasRoom_1_, BlockPos p_hasRoom_2_, int p_hasRoom_3_, BaseTreeFeatureConfig p_hasRoom_4_) {
      return this.isSpaceAt(p_hasRoom_1_, p_hasRoom_2_, p_hasRoom_3_) && this.validSoil(p_hasRoom_1_, p_hasRoom_2_, p_hasRoom_4_.getSapling());
   }

   protected void func_227255_a_(IWorldGenerationReader p_227255_1_, Random p_227255_2_, BlockPos p_227255_3_, int p_227255_4_, Set<BlockPos> p_227255_5_, MutableBoundingBox p_227255_6_, BaseTreeFeatureConfig p_227255_7_) {
      int i = p_227255_4_ * p_227255_4_;

      for(int j = -p_227255_4_; j <= p_227255_4_ + 1; ++j) {
         for(int k = -p_227255_4_; k <= p_227255_4_ + 1; ++k) {
            int l = Math.min(Math.abs(j), Math.abs(j - 1));
            int i1 = Math.min(Math.abs(k), Math.abs(k - 1));
            if (l + i1 < 7 && l * l + i1 * i1 <= i) {
               this.func_227219_b_(p_227255_1_, p_227255_2_, p_227255_3_.add(j, 0, k), p_227255_5_, p_227255_6_, p_227255_7_);
            }
         }
      }

   }

   protected void func_227257_b_(IWorldGenerationReader p_227257_1_, Random p_227257_2_, BlockPos p_227257_3_, int p_227257_4_, Set<BlockPos> p_227257_5_, MutableBoundingBox p_227257_6_, BaseTreeFeatureConfig p_227257_7_) {
      int i = p_227257_4_ * p_227257_4_;

      for(int j = -p_227257_4_; j <= p_227257_4_; ++j) {
         for(int k = -p_227257_4_; k <= p_227257_4_; ++k) {
            if (j * j + k * k <= i) {
               this.func_227219_b_(p_227257_1_, p_227257_2_, p_227257_3_.add(j, 0, k), p_227257_5_, p_227257_6_, p_227257_7_);
            }
         }
      }

   }

   protected void func_227254_a_(IWorldGenerationReader p_227254_1_, Random p_227254_2_, BlockPos p_227254_3_, int p_227254_4_, Set<BlockPos> p_227254_5_, MutableBoundingBox p_227254_6_, HugeTreeFeatureConfig p_227254_7_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = 0; i < p_227254_4_; ++i) {
         blockpos$mutable.setPos((Vec3i)p_227254_3_).move(0, i, 0);
         if (func_214587_a(p_227254_1_, blockpos$mutable)) {
            this.func_227216_a_(p_227254_1_, p_227254_2_, blockpos$mutable, p_227254_5_, p_227254_6_, p_227254_7_);
         }

         if (i < p_227254_4_ - 1) {
            blockpos$mutable.setPos((Vec3i)p_227254_3_).move(1, i, 0);
            if (func_214587_a(p_227254_1_, blockpos$mutable)) {
               this.func_227216_a_(p_227254_1_, p_227254_2_, blockpos$mutable, p_227254_5_, p_227254_6_, p_227254_7_);
            }

            blockpos$mutable.setPos((Vec3i)p_227254_3_).move(1, i, 1);
            if (func_214587_a(p_227254_1_, blockpos$mutable)) {
               this.func_227216_a_(p_227254_1_, p_227254_2_, blockpos$mutable, p_227254_5_, p_227254_6_, p_227254_7_);
            }

            blockpos$mutable.setPos((Vec3i)p_227254_3_).move(0, i, 1);
            if (func_214587_a(p_227254_1_, blockpos$mutable)) {
               this.func_227216_a_(p_227254_1_, p_227254_2_, blockpos$mutable, p_227254_5_, p_227254_6_, p_227254_7_);
            }
         }
      }

   }
}
