package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class SimplexNoiseGenerator {
   protected static final int[][] field_215468_a = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
   private static final double SQRT_3 = Math.sqrt(3.0D);
   private static final double F2;
   private static final double G2;
   private final int[] p = new int[512];
   public final double xo;
   public final double yo;
   public final double zo;

   public SimplexNoiseGenerator(Random p_i45471_1_) {
      this.xo = p_i45471_1_.nextDouble() * 256.0D;
      this.yo = p_i45471_1_.nextDouble() * 256.0D;
      this.zo = p_i45471_1_.nextDouble() * 256.0D;

      int lvt_2_2_;
      for(lvt_2_2_ = 0; lvt_2_2_ < 256; this.p[lvt_2_2_] = lvt_2_2_++) {
      }

      for(lvt_2_2_ = 0; lvt_2_2_ < 256; ++lvt_2_2_) {
         int lvt_3_1_ = p_i45471_1_.nextInt(256 - lvt_2_2_);
         int lvt_4_1_ = this.p[lvt_2_2_];
         this.p[lvt_2_2_] = this.p[lvt_3_1_ + lvt_2_2_];
         this.p[lvt_3_1_ + lvt_2_2_] = lvt_4_1_;
      }

   }

   private int func_215466_a(int p_215466_1_) {
      return this.p[p_215466_1_ & 255];
   }

   protected static double func_215467_a(int[] p_215467_0_, double p_215467_1_, double p_215467_3_, double p_215467_5_) {
      return (double)p_215467_0_[0] * p_215467_1_ + (double)p_215467_0_[1] * p_215467_3_ + (double)p_215467_0_[2] * p_215467_5_;
   }

   private double func_215465_a(int p_215465_1_, double p_215465_2_, double p_215465_4_, double p_215465_6_, double p_215465_8_) {
      double lvt_12_1_ = p_215465_8_ - p_215465_2_ * p_215465_2_ - p_215465_4_ * p_215465_4_ - p_215465_6_ * p_215465_6_;
      double lvt_10_2_;
      if (lvt_12_1_ < 0.0D) {
         lvt_10_2_ = 0.0D;
      } else {
         lvt_12_1_ *= lvt_12_1_;
         lvt_10_2_ = lvt_12_1_ * lvt_12_1_ * func_215467_a(field_215468_a[p_215465_1_], p_215465_2_, p_215465_4_, p_215465_6_);
      }

      return lvt_10_2_;
   }

   public double getValue(double p_151605_1_, double p_151605_3_) {
      double lvt_5_1_ = (p_151605_1_ + p_151605_3_) * F2;
      int lvt_7_1_ = MathHelper.floor(p_151605_1_ + lvt_5_1_);
      int lvt_8_1_ = MathHelper.floor(p_151605_3_ + lvt_5_1_);
      double lvt_9_1_ = (double)(lvt_7_1_ + lvt_8_1_) * G2;
      double lvt_11_1_ = (double)lvt_7_1_ - lvt_9_1_;
      double lvt_13_1_ = (double)lvt_8_1_ - lvt_9_1_;
      double lvt_15_1_ = p_151605_1_ - lvt_11_1_;
      double lvt_17_1_ = p_151605_3_ - lvt_13_1_;
      byte lvt_19_2_;
      byte lvt_20_2_;
      if (lvt_15_1_ > lvt_17_1_) {
         lvt_19_2_ = 1;
         lvt_20_2_ = 0;
      } else {
         lvt_19_2_ = 0;
         lvt_20_2_ = 1;
      }

      double lvt_21_1_ = lvt_15_1_ - (double)lvt_19_2_ + G2;
      double lvt_23_1_ = lvt_17_1_ - (double)lvt_20_2_ + G2;
      double lvt_25_1_ = lvt_15_1_ - 1.0D + 2.0D * G2;
      double lvt_27_1_ = lvt_17_1_ - 1.0D + 2.0D * G2;
      int lvt_29_1_ = lvt_7_1_ & 255;
      int lvt_30_1_ = lvt_8_1_ & 255;
      int lvt_31_1_ = this.func_215466_a(lvt_29_1_ + this.func_215466_a(lvt_30_1_)) % 12;
      int lvt_32_1_ = this.func_215466_a(lvt_29_1_ + lvt_19_2_ + this.func_215466_a(lvt_30_1_ + lvt_20_2_)) % 12;
      int lvt_33_1_ = this.func_215466_a(lvt_29_1_ + 1 + this.func_215466_a(lvt_30_1_ + 1)) % 12;
      double lvt_34_1_ = this.func_215465_a(lvt_31_1_, lvt_15_1_, lvt_17_1_, 0.0D, 0.5D);
      double lvt_36_1_ = this.func_215465_a(lvt_32_1_, lvt_21_1_, lvt_23_1_, 0.0D, 0.5D);
      double lvt_38_1_ = this.func_215465_a(lvt_33_1_, lvt_25_1_, lvt_27_1_, 0.0D, 0.5D);
      return 70.0D * (lvt_34_1_ + lvt_36_1_ + lvt_38_1_);
   }

   public double func_227464_a_(double p_227464_1_, double p_227464_3_, double p_227464_5_) {
      double lvt_7_1_ = 0.3333333333333333D;
      double lvt_9_1_ = (p_227464_1_ + p_227464_3_ + p_227464_5_) * 0.3333333333333333D;
      int lvt_11_1_ = MathHelper.floor(p_227464_1_ + lvt_9_1_);
      int lvt_12_1_ = MathHelper.floor(p_227464_3_ + lvt_9_1_);
      int lvt_13_1_ = MathHelper.floor(p_227464_5_ + lvt_9_1_);
      double lvt_14_1_ = 0.16666666666666666D;
      double lvt_16_1_ = (double)(lvt_11_1_ + lvt_12_1_ + lvt_13_1_) * 0.16666666666666666D;
      double lvt_18_1_ = (double)lvt_11_1_ - lvt_16_1_;
      double lvt_20_1_ = (double)lvt_12_1_ - lvt_16_1_;
      double lvt_22_1_ = (double)lvt_13_1_ - lvt_16_1_;
      double lvt_24_1_ = p_227464_1_ - lvt_18_1_;
      double lvt_26_1_ = p_227464_3_ - lvt_20_1_;
      double lvt_28_1_ = p_227464_5_ - lvt_22_1_;
      byte lvt_30_2_;
      byte lvt_31_2_;
      byte lvt_32_2_;
      byte lvt_33_2_;
      byte lvt_34_2_;
      byte lvt_35_6_;
      if (lvt_24_1_ >= lvt_26_1_) {
         if (lvt_26_1_ >= lvt_28_1_) {
            lvt_30_2_ = 1;
            lvt_31_2_ = 0;
            lvt_32_2_ = 0;
            lvt_33_2_ = 1;
            lvt_34_2_ = 1;
            lvt_35_6_ = 0;
         } else if (lvt_24_1_ >= lvt_28_1_) {
            lvt_30_2_ = 1;
            lvt_31_2_ = 0;
            lvt_32_2_ = 0;
            lvt_33_2_ = 1;
            lvt_34_2_ = 0;
            lvt_35_6_ = 1;
         } else {
            lvt_30_2_ = 0;
            lvt_31_2_ = 0;
            lvt_32_2_ = 1;
            lvt_33_2_ = 1;
            lvt_34_2_ = 0;
            lvt_35_6_ = 1;
         }
      } else if (lvt_26_1_ < lvt_28_1_) {
         lvt_30_2_ = 0;
         lvt_31_2_ = 0;
         lvt_32_2_ = 1;
         lvt_33_2_ = 0;
         lvt_34_2_ = 1;
         lvt_35_6_ = 1;
      } else if (lvt_24_1_ < lvt_28_1_) {
         lvt_30_2_ = 0;
         lvt_31_2_ = 1;
         lvt_32_2_ = 0;
         lvt_33_2_ = 0;
         lvt_34_2_ = 1;
         lvt_35_6_ = 1;
      } else {
         lvt_30_2_ = 0;
         lvt_31_2_ = 1;
         lvt_32_2_ = 0;
         lvt_33_2_ = 1;
         lvt_34_2_ = 1;
         lvt_35_6_ = 0;
      }

      double lvt_36_1_ = lvt_24_1_ - (double)lvt_30_2_ + 0.16666666666666666D;
      double lvt_38_1_ = lvt_26_1_ - (double)lvt_31_2_ + 0.16666666666666666D;
      double lvt_40_1_ = lvt_28_1_ - (double)lvt_32_2_ + 0.16666666666666666D;
      double lvt_42_1_ = lvt_24_1_ - (double)lvt_33_2_ + 0.3333333333333333D;
      double lvt_44_1_ = lvt_26_1_ - (double)lvt_34_2_ + 0.3333333333333333D;
      double lvt_46_1_ = lvt_28_1_ - (double)lvt_35_6_ + 0.3333333333333333D;
      double lvt_48_1_ = lvt_24_1_ - 1.0D + 0.5D;
      double lvt_50_1_ = lvt_26_1_ - 1.0D + 0.5D;
      double lvt_52_1_ = lvt_28_1_ - 1.0D + 0.5D;
      int lvt_54_1_ = lvt_11_1_ & 255;
      int lvt_55_1_ = lvt_12_1_ & 255;
      int lvt_56_1_ = lvt_13_1_ & 255;
      int lvt_57_1_ = this.func_215466_a(lvt_54_1_ + this.func_215466_a(lvt_55_1_ + this.func_215466_a(lvt_56_1_))) % 12;
      int lvt_58_1_ = this.func_215466_a(lvt_54_1_ + lvt_30_2_ + this.func_215466_a(lvt_55_1_ + lvt_31_2_ + this.func_215466_a(lvt_56_1_ + lvt_32_2_))) % 12;
      int lvt_59_1_ = this.func_215466_a(lvt_54_1_ + lvt_33_2_ + this.func_215466_a(lvt_55_1_ + lvt_34_2_ + this.func_215466_a(lvt_56_1_ + lvt_35_6_))) % 12;
      int lvt_60_1_ = this.func_215466_a(lvt_54_1_ + 1 + this.func_215466_a(lvt_55_1_ + 1 + this.func_215466_a(lvt_56_1_ + 1))) % 12;
      double lvt_61_1_ = this.func_215465_a(lvt_57_1_, lvt_24_1_, lvt_26_1_, lvt_28_1_, 0.6D);
      double lvt_63_1_ = this.func_215465_a(lvt_58_1_, lvt_36_1_, lvt_38_1_, lvt_40_1_, 0.6D);
      double lvt_65_1_ = this.func_215465_a(lvt_59_1_, lvt_42_1_, lvt_44_1_, lvt_46_1_, 0.6D);
      double lvt_67_1_ = this.func_215465_a(lvt_60_1_, lvt_48_1_, lvt_50_1_, lvt_52_1_, 0.6D);
      return 32.0D * (lvt_61_1_ + lvt_63_1_ + lvt_65_1_ + lvt_67_1_);
   }

   static {
      F2 = 0.5D * (SQRT_3 - 1.0D);
      G2 = (3.0D - SQRT_3) / 6.0D;
   }
}
