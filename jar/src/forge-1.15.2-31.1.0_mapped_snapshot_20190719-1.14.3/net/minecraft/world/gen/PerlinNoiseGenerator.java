package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.stream.IntStream;
import net.minecraft.util.SharedSeedRandom;

public class PerlinNoiseGenerator implements INoiseGenerator {
   private final SimplexNoiseGenerator[] noiseLevels;
   private final double field_227462_b_;
   private final double field_227463_c_;

   public PerlinNoiseGenerator(SharedSeedRandom p_i225880_1_, int p_i225880_2_, int p_i225880_3_) {
      this(p_i225880_1_, new IntRBTreeSet(IntStream.rangeClosed(-p_i225880_2_, p_i225880_3_).toArray()));
   }

   public PerlinNoiseGenerator(SharedSeedRandom p_i225881_1_, IntSortedSet p_i225881_2_) {
      if (p_i225881_2_.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int lvt_3_1_ = -p_i225881_2_.firstInt();
         int lvt_4_1_ = p_i225881_2_.lastInt();
         int lvt_5_1_ = lvt_3_1_ + lvt_4_1_ + 1;
         if (lvt_5_1_ < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            SimplexNoiseGenerator lvt_6_1_ = new SimplexNoiseGenerator(p_i225881_1_);
            int lvt_7_1_ = lvt_4_1_;
            this.noiseLevels = new SimplexNoiseGenerator[lvt_5_1_];
            if (lvt_4_1_ >= 0 && lvt_4_1_ < lvt_5_1_ && p_i225881_2_.contains(0)) {
               this.noiseLevels[lvt_4_1_] = lvt_6_1_;
            }

            for(int lvt_8_1_ = lvt_4_1_ + 1; lvt_8_1_ < lvt_5_1_; ++lvt_8_1_) {
               if (lvt_8_1_ >= 0 && p_i225881_2_.contains(lvt_7_1_ - lvt_8_1_)) {
                  this.noiseLevels[lvt_8_1_] = new SimplexNoiseGenerator(p_i225881_1_);
               } else {
                  p_i225881_1_.skip(262);
               }
            }

            if (lvt_4_1_ > 0) {
               long lvt_8_2_ = (long)(lvt_6_1_.func_227464_a_(lvt_6_1_.xo, lvt_6_1_.yo, lvt_6_1_.zo) * 9.223372036854776E18D);
               SharedSeedRandom lvt_10_1_ = new SharedSeedRandom(lvt_8_2_);

               for(int lvt_11_1_ = lvt_7_1_ - 1; lvt_11_1_ >= 0; --lvt_11_1_) {
                  if (lvt_11_1_ < lvt_5_1_ && p_i225881_2_.contains(lvt_7_1_ - lvt_11_1_)) {
                     this.noiseLevels[lvt_11_1_] = new SimplexNoiseGenerator(lvt_10_1_);
                  } else {
                     lvt_10_1_.skip(262);
                  }
               }
            }

            this.field_227463_c_ = Math.pow(2.0D, (double)lvt_4_1_);
            this.field_227462_b_ = 1.0D / (Math.pow(2.0D, (double)lvt_5_1_) - 1.0D);
         }
      }
   }

   public double func_215464_a(double p_215464_1_, double p_215464_3_, boolean p_215464_5_) {
      double lvt_6_1_ = 0.0D;
      double lvt_8_1_ = this.field_227463_c_;
      double lvt_10_1_ = this.field_227462_b_;
      SimplexNoiseGenerator[] var12 = this.noiseLevels;
      int var13 = var12.length;

      for(int var14 = 0; var14 < var13; ++var14) {
         SimplexNoiseGenerator lvt_15_1_ = var12[var14];
         if (lvt_15_1_ != null) {
            lvt_6_1_ += lvt_15_1_.getValue(p_215464_1_ * lvt_8_1_ + (p_215464_5_ ? lvt_15_1_.xo : 0.0D), p_215464_3_ * lvt_8_1_ + (p_215464_5_ ? lvt_15_1_.yo : 0.0D)) * lvt_10_1_;
         }

         lvt_8_1_ /= 2.0D;
         lvt_10_1_ *= 2.0D;
      }

      return lvt_6_1_;
   }

   public double func_215460_a(double p_215460_1_, double p_215460_3_, double p_215460_5_, double p_215460_7_) {
      return this.func_215464_a(p_215460_1_, p_215460_3_, true) * 0.55D;
   }
}
