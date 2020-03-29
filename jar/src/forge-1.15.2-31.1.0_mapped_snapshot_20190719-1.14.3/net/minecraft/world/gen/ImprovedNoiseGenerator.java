package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public final class ImprovedNoiseGenerator {
   private final byte[] permutations;
   public final double xCoord;
   public final double yCoord;
   public final double zCoord;

   public ImprovedNoiseGenerator(Random p_i45469_1_) {
      this.xCoord = p_i45469_1_.nextDouble() * 256.0D;
      this.yCoord = p_i45469_1_.nextDouble() * 256.0D;
      this.zCoord = p_i45469_1_.nextDouble() * 256.0D;
      this.permutations = new byte[256];

      int lvt_2_2_;
      for(lvt_2_2_ = 0; lvt_2_2_ < 256; ++lvt_2_2_) {
         this.permutations[lvt_2_2_] = (byte)lvt_2_2_;
      }

      for(lvt_2_2_ = 0; lvt_2_2_ < 256; ++lvt_2_2_) {
         int lvt_3_1_ = p_i45469_1_.nextInt(256 - lvt_2_2_);
         byte lvt_4_1_ = this.permutations[lvt_2_2_];
         this.permutations[lvt_2_2_] = this.permutations[lvt_2_2_ + lvt_3_1_];
         this.permutations[lvt_2_2_ + lvt_3_1_] = lvt_4_1_;
      }

   }

   public double func_215456_a(double p_215456_1_, double p_215456_3_, double p_215456_5_, double p_215456_7_, double p_215456_9_) {
      double lvt_11_1_ = p_215456_1_ + this.xCoord;
      double lvt_13_1_ = p_215456_3_ + this.yCoord;
      double lvt_15_1_ = p_215456_5_ + this.zCoord;
      int lvt_17_1_ = MathHelper.floor(lvt_11_1_);
      int lvt_18_1_ = MathHelper.floor(lvt_13_1_);
      int lvt_19_1_ = MathHelper.floor(lvt_15_1_);
      double lvt_20_1_ = lvt_11_1_ - (double)lvt_17_1_;
      double lvt_22_1_ = lvt_13_1_ - (double)lvt_18_1_;
      double lvt_24_1_ = lvt_15_1_ - (double)lvt_19_1_;
      double lvt_26_1_ = MathHelper.perlinFade(lvt_20_1_);
      double lvt_28_1_ = MathHelper.perlinFade(lvt_22_1_);
      double lvt_30_1_ = MathHelper.perlinFade(lvt_24_1_);
      double lvt_32_2_;
      if (p_215456_7_ != 0.0D) {
         double lvt_34_1_ = Math.min(p_215456_9_, lvt_22_1_);
         lvt_32_2_ = (double)MathHelper.floor(lvt_34_1_ / p_215456_7_) * p_215456_7_;
      } else {
         lvt_32_2_ = 0.0D;
      }

      return this.func_215459_a(lvt_17_1_, lvt_18_1_, lvt_19_1_, lvt_20_1_, lvt_22_1_ - lvt_32_2_, lvt_24_1_, lvt_26_1_, lvt_28_1_, lvt_30_1_);
   }

   private static double func_215457_a(int p_215457_0_, double p_215457_1_, double p_215457_3_, double p_215457_5_) {
      int lvt_7_1_ = p_215457_0_ & 15;
      return SimplexNoiseGenerator.func_215467_a(SimplexNoiseGenerator.field_215468_a[lvt_7_1_], p_215457_1_, p_215457_3_, p_215457_5_);
   }

   private int func_215458_a(int p_215458_1_) {
      return this.permutations[p_215458_1_ & 255] & 255;
   }

   public double func_215459_a(int p_215459_1_, int p_215459_2_, int p_215459_3_, double p_215459_4_, double p_215459_6_, double p_215459_8_, double p_215459_10_, double p_215459_12_, double p_215459_14_) {
      int lvt_16_1_ = this.func_215458_a(p_215459_1_) + p_215459_2_;
      int lvt_17_1_ = this.func_215458_a(lvt_16_1_) + p_215459_3_;
      int lvt_18_1_ = this.func_215458_a(lvt_16_1_ + 1) + p_215459_3_;
      int lvt_19_1_ = this.func_215458_a(p_215459_1_ + 1) + p_215459_2_;
      int lvt_20_1_ = this.func_215458_a(lvt_19_1_) + p_215459_3_;
      int lvt_21_1_ = this.func_215458_a(lvt_19_1_ + 1) + p_215459_3_;
      double lvt_22_1_ = func_215457_a(this.func_215458_a(lvt_17_1_), p_215459_4_, p_215459_6_, p_215459_8_);
      double lvt_24_1_ = func_215457_a(this.func_215458_a(lvt_20_1_), p_215459_4_ - 1.0D, p_215459_6_, p_215459_8_);
      double lvt_26_1_ = func_215457_a(this.func_215458_a(lvt_18_1_), p_215459_4_, p_215459_6_ - 1.0D, p_215459_8_);
      double lvt_28_1_ = func_215457_a(this.func_215458_a(lvt_21_1_), p_215459_4_ - 1.0D, p_215459_6_ - 1.0D, p_215459_8_);
      double lvt_30_1_ = func_215457_a(this.func_215458_a(lvt_17_1_ + 1), p_215459_4_, p_215459_6_, p_215459_8_ - 1.0D);
      double lvt_32_1_ = func_215457_a(this.func_215458_a(lvt_20_1_ + 1), p_215459_4_ - 1.0D, p_215459_6_, p_215459_8_ - 1.0D);
      double lvt_34_1_ = func_215457_a(this.func_215458_a(lvt_18_1_ + 1), p_215459_4_, p_215459_6_ - 1.0D, p_215459_8_ - 1.0D);
      double lvt_36_1_ = func_215457_a(this.func_215458_a(lvt_21_1_ + 1), p_215459_4_ - 1.0D, p_215459_6_ - 1.0D, p_215459_8_ - 1.0D);
      return MathHelper.lerp3(p_215459_10_, p_215459_12_, p_215459_14_, lvt_22_1_, lvt_24_1_, lvt_26_1_, lvt_28_1_, lvt_30_1_, lvt_32_1_, lvt_34_1_, lvt_36_1_);
   }
}
