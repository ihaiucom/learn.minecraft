package net.minecraft.world.biome;

import net.minecraft.util.FastRandom;

public enum FuzzedBiomeMagnifier implements IBiomeMagnifier {
   INSTANCE;

   public Biome func_225532_a_(long p_225532_1_, int p_225532_3_, int p_225532_4_, int p_225532_5_, BiomeManager.IBiomeReader p_225532_6_) {
      int lvt_7_1_ = p_225532_3_ - 2;
      int lvt_8_1_ = p_225532_4_ - 2;
      int lvt_9_1_ = p_225532_5_ - 2;
      int lvt_10_1_ = lvt_7_1_ >> 2;
      int lvt_11_1_ = lvt_8_1_ >> 2;
      int lvt_12_1_ = lvt_9_1_ >> 2;
      double lvt_13_1_ = (double)(lvt_7_1_ & 3) / 4.0D;
      double lvt_15_1_ = (double)(lvt_8_1_ & 3) / 4.0D;
      double lvt_17_1_ = (double)(lvt_9_1_ & 3) / 4.0D;
      double[] lvt_19_1_ = new double[8];

      int lvt_20_2_;
      int lvt_24_2_;
      int lvt_25_2_;
      for(lvt_20_2_ = 0; lvt_20_2_ < 8; ++lvt_20_2_) {
         boolean lvt_21_1_ = (lvt_20_2_ & 4) == 0;
         boolean lvt_22_1_ = (lvt_20_2_ & 2) == 0;
         boolean lvt_23_1_ = (lvt_20_2_ & 1) == 0;
         lvt_24_2_ = lvt_21_1_ ? lvt_10_1_ : lvt_10_1_ + 1;
         lvt_25_2_ = lvt_22_1_ ? lvt_11_1_ : lvt_11_1_ + 1;
         int lvt_26_1_ = lvt_23_1_ ? lvt_12_1_ : lvt_12_1_ + 1;
         double lvt_27_1_ = lvt_21_1_ ? lvt_13_1_ : lvt_13_1_ - 1.0D;
         double lvt_29_1_ = lvt_22_1_ ? lvt_15_1_ : lvt_15_1_ - 1.0D;
         double lvt_31_1_ = lvt_23_1_ ? lvt_17_1_ : lvt_17_1_ - 1.0D;
         lvt_19_1_[lvt_20_2_] = func_226845_a_(p_225532_1_, lvt_24_2_, lvt_25_2_, lvt_26_1_, lvt_27_1_, lvt_29_1_, lvt_31_1_);
      }

      lvt_20_2_ = 0;
      double lvt_21_2_ = lvt_19_1_[0];

      int lvt_23_2_;
      for(lvt_23_2_ = 1; lvt_23_2_ < 8; ++lvt_23_2_) {
         if (lvt_21_2_ > lvt_19_1_[lvt_23_2_]) {
            lvt_20_2_ = lvt_23_2_;
            lvt_21_2_ = lvt_19_1_[lvt_23_2_];
         }
      }

      lvt_23_2_ = (lvt_20_2_ & 4) == 0 ? lvt_10_1_ : lvt_10_1_ + 1;
      lvt_24_2_ = (lvt_20_2_ & 2) == 0 ? lvt_11_1_ : lvt_11_1_ + 1;
      lvt_25_2_ = (lvt_20_2_ & 1) == 0 ? lvt_12_1_ : lvt_12_1_ + 1;
      return p_225532_6_.func_225526_b_(lvt_23_2_, lvt_24_2_, lvt_25_2_);
   }

   private static double func_226845_a_(long p_226845_0_, int p_226845_2_, int p_226845_3_, int p_226845_4_, double p_226845_5_, double p_226845_7_, double p_226845_9_) {
      long lvt_11_1_ = FastRandom.func_226162_a_(p_226845_0_, (long)p_226845_2_);
      lvt_11_1_ = FastRandom.func_226162_a_(lvt_11_1_, (long)p_226845_3_);
      lvt_11_1_ = FastRandom.func_226162_a_(lvt_11_1_, (long)p_226845_4_);
      lvt_11_1_ = FastRandom.func_226162_a_(lvt_11_1_, (long)p_226845_2_);
      lvt_11_1_ = FastRandom.func_226162_a_(lvt_11_1_, (long)p_226845_3_);
      lvt_11_1_ = FastRandom.func_226162_a_(lvt_11_1_, (long)p_226845_4_);
      double lvt_13_1_ = func_226844_a_(lvt_11_1_);
      lvt_11_1_ = FastRandom.func_226162_a_(lvt_11_1_, p_226845_0_);
      double lvt_15_1_ = func_226844_a_(lvt_11_1_);
      lvt_11_1_ = FastRandom.func_226162_a_(lvt_11_1_, p_226845_0_);
      double lvt_17_1_ = func_226844_a_(lvt_11_1_);
      return func_226843_a_(p_226845_9_ + lvt_17_1_) + func_226843_a_(p_226845_7_ + lvt_15_1_) + func_226843_a_(p_226845_5_ + lvt_13_1_);
   }

   private static double func_226844_a_(long p_226844_0_) {
      double lvt_2_1_ = (double)((int)Math.floorMod(p_226844_0_ >> 24, 1024L)) / 1024.0D;
      return (lvt_2_1_ - 0.5D) * 0.9D;
   }

   private static double func_226843_a_(double p_226843_0_) {
      return p_226843_0_ * p_226843_0_;
   }
}
