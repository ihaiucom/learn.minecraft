package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.LogBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;

public class FancyTreeFeature extends AbstractTreeFeature<TreeFeatureConfig> {
   public FancyTreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> p_i225803_1_) {
      super(p_i225803_1_);
   }

   private void func_227233_a_(IWorldGenerationReader p_227233_1_, Random p_227233_2_, BlockPos p_227233_3_, float p_227233_4_, Set<BlockPos> p_227233_5_, MutableBoundingBox p_227233_6_, TreeFeatureConfig p_227233_7_) {
      int lvt_8_1_ = (int)((double)p_227233_4_ + 0.618D);

      for(int lvt_9_1_ = -lvt_8_1_; lvt_9_1_ <= lvt_8_1_; ++lvt_9_1_) {
         for(int lvt_10_1_ = -lvt_8_1_; lvt_10_1_ <= lvt_8_1_; ++lvt_10_1_) {
            if (Math.pow((double)Math.abs(lvt_9_1_) + 0.5D, 2.0D) + Math.pow((double)Math.abs(lvt_10_1_) + 0.5D, 2.0D) <= (double)(p_227233_4_ * p_227233_4_)) {
               this.func_227219_b_(p_227233_1_, p_227233_2_, p_227233_3_.add(lvt_9_1_, 0, lvt_10_1_), p_227233_5_, p_227233_6_, p_227233_7_);
            }
         }
      }

   }

   private float func_227231_a_(int p_227231_1_, int p_227231_2_) {
      if ((float)p_227231_2_ < (float)p_227231_1_ * 0.3F) {
         return -1.0F;
      } else {
         float lvt_3_1_ = (float)p_227231_1_ / 2.0F;
         float lvt_4_1_ = lvt_3_1_ - (float)p_227231_2_;
         float lvt_5_1_ = MathHelper.sqrt(lvt_3_1_ * lvt_3_1_ - lvt_4_1_ * lvt_4_1_);
         if (lvt_4_1_ == 0.0F) {
            lvt_5_1_ = lvt_3_1_;
         } else if (Math.abs(lvt_4_1_) >= lvt_3_1_) {
            return 0.0F;
         }

         return lvt_5_1_ * 0.5F;
      }
   }

   private float func_227230_a_(int p_227230_1_) {
      if (p_227230_1_ >= 0 && p_227230_1_ < 5) {
         return p_227230_1_ != 0 && p_227230_1_ != 4 ? 3.0F : 2.0F;
      } else {
         return -1.0F;
      }
   }

   private void func_227236_a_(IWorldGenerationReader p_227236_1_, Random p_227236_2_, BlockPos p_227236_3_, Set<BlockPos> p_227236_4_, MutableBoundingBox p_227236_5_, TreeFeatureConfig p_227236_6_) {
      for(int lvt_7_1_ = 0; lvt_7_1_ < 5; ++lvt_7_1_) {
         this.func_227233_a_(p_227236_1_, p_227236_2_, p_227236_3_.up(lvt_7_1_), this.func_227230_a_(lvt_7_1_), p_227236_4_, p_227236_5_, p_227236_6_);
      }

   }

   private int func_227235_a_(IWorldGenerationReader p_227235_1_, Random p_227235_2_, BlockPos p_227235_3_, BlockPos p_227235_4_, boolean p_227235_5_, Set<BlockPos> p_227235_6_, MutableBoundingBox p_227235_7_, TreeFeatureConfig p_227235_8_) {
      if (!p_227235_5_ && Objects.equals(p_227235_3_, p_227235_4_)) {
         return -1;
      } else {
         BlockPos lvt_9_1_ = p_227235_4_.add(-p_227235_3_.getX(), -p_227235_3_.getY(), -p_227235_3_.getZ());
         int lvt_10_1_ = this.func_227237_a_(lvt_9_1_);
         float lvt_11_1_ = (float)lvt_9_1_.getX() / (float)lvt_10_1_;
         float lvt_12_1_ = (float)lvt_9_1_.getY() / (float)lvt_10_1_;
         float lvt_13_1_ = (float)lvt_9_1_.getZ() / (float)lvt_10_1_;

         for(int lvt_14_1_ = 0; lvt_14_1_ <= lvt_10_1_; ++lvt_14_1_) {
            BlockPos lvt_15_1_ = p_227235_3_.add((double)(0.5F + (float)lvt_14_1_ * lvt_11_1_), (double)(0.5F + (float)lvt_14_1_ * lvt_12_1_), (double)(0.5F + (float)lvt_14_1_ * lvt_13_1_));
            if (p_227235_5_) {
               this.func_227217_a_(p_227235_1_, lvt_15_1_, (BlockState)p_227235_8_.field_227368_m_.func_225574_a_(p_227235_2_, lvt_15_1_).with(LogBlock.AXIS, this.func_227238_a_(p_227235_3_, lvt_15_1_)), p_227235_7_);
               p_227235_6_.add(lvt_15_1_);
            } else if (!func_214587_a(p_227235_1_, lvt_15_1_)) {
               return lvt_14_1_;
            }
         }

         return -1;
      }
   }

   private int func_227237_a_(BlockPos p_227237_1_) {
      int lvt_2_1_ = MathHelper.abs(p_227237_1_.getX());
      int lvt_3_1_ = MathHelper.abs(p_227237_1_.getY());
      int lvt_4_1_ = MathHelper.abs(p_227237_1_.getZ());
      if (lvt_4_1_ > lvt_2_1_ && lvt_4_1_ > lvt_3_1_) {
         return lvt_4_1_;
      } else {
         return lvt_3_1_ > lvt_2_1_ ? lvt_3_1_ : lvt_2_1_;
      }
   }

   private Direction.Axis func_227238_a_(BlockPos p_227238_1_, BlockPos p_227238_2_) {
      Direction.Axis lvt_3_1_ = Direction.Axis.Y;
      int lvt_4_1_ = Math.abs(p_227238_2_.getX() - p_227238_1_.getX());
      int lvt_5_1_ = Math.abs(p_227238_2_.getZ() - p_227238_1_.getZ());
      int lvt_6_1_ = Math.max(lvt_4_1_, lvt_5_1_);
      if (lvt_6_1_ > 0) {
         if (lvt_4_1_ == lvt_6_1_) {
            lvt_3_1_ = Direction.Axis.X;
         } else if (lvt_5_1_ == lvt_6_1_) {
            lvt_3_1_ = Direction.Axis.Z;
         }
      }

      return lvt_3_1_;
   }

   private void func_227232_a_(IWorldGenerationReader p_227232_1_, Random p_227232_2_, int p_227232_3_, BlockPos p_227232_4_, List<FancyTreeFeature.ExtendedPos> p_227232_5_, Set<BlockPos> p_227232_6_, MutableBoundingBox p_227232_7_, TreeFeatureConfig p_227232_8_) {
      Iterator var9 = p_227232_5_.iterator();

      while(var9.hasNext()) {
         FancyTreeFeature.ExtendedPos lvt_10_1_ = (FancyTreeFeature.ExtendedPos)var9.next();
         if (this.func_227239_b_(p_227232_3_, lvt_10_1_.func_227243_r_() - p_227232_4_.getY())) {
            this.func_227236_a_(p_227232_1_, p_227232_2_, lvt_10_1_, p_227232_6_, p_227232_7_, p_227232_8_);
         }
      }

   }

   private boolean func_227239_b_(int p_227239_1_, int p_227239_2_) {
      return (double)p_227239_2_ >= (double)p_227239_1_ * 0.2D;
   }

   private void func_227234_a_(IWorldGenerationReader p_227234_1_, Random p_227234_2_, BlockPos p_227234_3_, int p_227234_4_, Set<BlockPos> p_227234_5_, MutableBoundingBox p_227234_6_, TreeFeatureConfig p_227234_7_) {
      this.func_227235_a_(p_227234_1_, p_227234_2_, p_227234_3_, p_227234_3_.up(p_227234_4_), true, p_227234_5_, p_227234_6_, p_227234_7_);
   }

   private void func_227240_b_(IWorldGenerationReader p_227240_1_, Random p_227240_2_, int p_227240_3_, BlockPos p_227240_4_, List<FancyTreeFeature.ExtendedPos> p_227240_5_, Set<BlockPos> p_227240_6_, MutableBoundingBox p_227240_7_, TreeFeatureConfig p_227240_8_) {
      Iterator var9 = p_227240_5_.iterator();

      while(var9.hasNext()) {
         FancyTreeFeature.ExtendedPos lvt_10_1_ = (FancyTreeFeature.ExtendedPos)var9.next();
         int lvt_11_1_ = lvt_10_1_.func_227243_r_();
         BlockPos lvt_12_1_ = new BlockPos(p_227240_4_.getX(), lvt_11_1_, p_227240_4_.getZ());
         if (!lvt_12_1_.equals(lvt_10_1_) && this.func_227239_b_(p_227240_3_, lvt_11_1_ - p_227240_4_.getY())) {
            this.func_227235_a_(p_227240_1_, p_227240_2_, lvt_12_1_, lvt_10_1_, true, p_227240_6_, p_227240_7_, p_227240_8_);
         }
      }

   }

   public boolean func_225557_a_(IWorldGenerationReader p_225557_1_, Random p_225557_2_, BlockPos p_225557_3_, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox p_225557_6_, TreeFeatureConfig p_225557_7_) {
      Random lvt_8_1_ = new Random(p_225557_2_.nextLong());
      int lvt_9_1_ = this.func_227241_b_(p_225557_1_, p_225557_2_, p_225557_3_, 5 + lvt_8_1_.nextInt(12), p_225557_4_, p_225557_6_, p_225557_7_);
      if (lvt_9_1_ == -1) {
         return false;
      } else {
         this.func_214584_a(p_225557_1_, p_225557_3_.down());
         int lvt_10_1_ = (int)((double)lvt_9_1_ * 0.618D);
         if (lvt_10_1_ >= lvt_9_1_) {
            lvt_10_1_ = lvt_9_1_ - 1;
         }

         double lvt_11_1_ = 1.0D;
         int lvt_13_1_ = (int)(1.382D + Math.pow(1.0D * (double)lvt_9_1_ / 13.0D, 2.0D));
         if (lvt_13_1_ < 1) {
            lvt_13_1_ = 1;
         }

         int lvt_14_1_ = p_225557_3_.getY() + lvt_10_1_;
         int lvt_15_1_ = lvt_9_1_ - 5;
         List<FancyTreeFeature.ExtendedPos> lvt_16_1_ = Lists.newArrayList();
         lvt_16_1_.add(new FancyTreeFeature.ExtendedPos(p_225557_3_.up(lvt_15_1_), lvt_14_1_));

         for(; lvt_15_1_ >= 0; --lvt_15_1_) {
            float lvt_17_1_ = this.func_227231_a_(lvt_9_1_, lvt_15_1_);
            if (lvt_17_1_ >= 0.0F) {
               for(int lvt_18_1_ = 0; lvt_18_1_ < lvt_13_1_; ++lvt_18_1_) {
                  double lvt_19_1_ = 1.0D;
                  double lvt_21_1_ = 1.0D * (double)lvt_17_1_ * ((double)lvt_8_1_.nextFloat() + 0.328D);
                  double lvt_23_1_ = (double)(lvt_8_1_.nextFloat() * 2.0F) * 3.141592653589793D;
                  double lvt_25_1_ = lvt_21_1_ * Math.sin(lvt_23_1_) + 0.5D;
                  double lvt_27_1_ = lvt_21_1_ * Math.cos(lvt_23_1_) + 0.5D;
                  BlockPos lvt_29_1_ = p_225557_3_.add(lvt_25_1_, (double)(lvt_15_1_ - 1), lvt_27_1_);
                  BlockPos lvt_30_1_ = lvt_29_1_.up(5);
                  if (this.func_227235_a_(p_225557_1_, p_225557_2_, lvt_29_1_, lvt_30_1_, false, p_225557_4_, p_225557_6_, p_225557_7_) == -1) {
                     int lvt_31_1_ = p_225557_3_.getX() - lvt_29_1_.getX();
                     int lvt_32_1_ = p_225557_3_.getZ() - lvt_29_1_.getZ();
                     double lvt_33_1_ = (double)lvt_29_1_.getY() - Math.sqrt((double)(lvt_31_1_ * lvt_31_1_ + lvt_32_1_ * lvt_32_1_)) * 0.381D;
                     int lvt_35_1_ = lvt_33_1_ > (double)lvt_14_1_ ? lvt_14_1_ : (int)lvt_33_1_;
                     BlockPos lvt_36_1_ = new BlockPos(p_225557_3_.getX(), lvt_35_1_, p_225557_3_.getZ());
                     if (this.func_227235_a_(p_225557_1_, p_225557_2_, lvt_36_1_, lvt_29_1_, false, p_225557_4_, p_225557_6_, p_225557_7_) == -1) {
                        lvt_16_1_.add(new FancyTreeFeature.ExtendedPos(lvt_29_1_, lvt_36_1_.getY()));
                     }
                  }
               }
            }
         }

         this.func_227232_a_(p_225557_1_, p_225557_2_, lvt_9_1_, p_225557_3_, lvt_16_1_, p_225557_5_, p_225557_6_, p_225557_7_);
         this.func_227234_a_(p_225557_1_, p_225557_2_, p_225557_3_, lvt_10_1_, p_225557_4_, p_225557_6_, p_225557_7_);
         this.func_227240_b_(p_225557_1_, p_225557_2_, lvt_9_1_, p_225557_3_, lvt_16_1_, p_225557_4_, p_225557_6_, p_225557_7_);
         return true;
      }
   }

   private int func_227241_b_(IWorldGenerationReader p_227241_1_, Random p_227241_2_, BlockPos p_227241_3_, int p_227241_4_, Set<BlockPos> p_227241_5_, MutableBoundingBox p_227241_6_, TreeFeatureConfig p_227241_7_) {
      if (!isDirtOrGrassBlockOrFarmland(p_227241_1_, p_227241_3_.down())) {
         return -1;
      } else {
         int lvt_8_1_ = this.func_227235_a_(p_227241_1_, p_227241_2_, p_227241_3_, p_227241_3_.up(p_227241_4_ - 1), false, p_227241_5_, p_227241_6_, p_227241_7_);
         if (lvt_8_1_ == -1) {
            return p_227241_4_;
         } else {
            return lvt_8_1_ < 6 ? -1 : lvt_8_1_;
         }
      }
   }

   static class ExtendedPos extends BlockPos {
      private final int field_227242_b_;

      public ExtendedPos(BlockPos p_i225804_1_, int p_i225804_2_) {
         super(p_i225804_1_.getX(), p_i225804_1_.getY(), p_i225804_1_.getZ());
         this.field_227242_b_ = p_i225804_2_;
      }

      public int func_227243_r_() {
         return this.field_227242_b_;
      }
   }
}
