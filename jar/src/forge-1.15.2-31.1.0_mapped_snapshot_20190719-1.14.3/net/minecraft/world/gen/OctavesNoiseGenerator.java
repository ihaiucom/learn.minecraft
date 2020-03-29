package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;

public class OctavesNoiseGenerator implements INoiseGenerator {
   private final ImprovedNoiseGenerator[] octaves;
   private final double field_227460_b_;
   private final double field_227461_c_;

   public OctavesNoiseGenerator(SharedSeedRandom p_i225878_1_, int p_i225878_2_, int p_i225878_3_) {
      this(p_i225878_1_, new IntRBTreeSet(IntStream.rangeClosed(-p_i225878_2_, p_i225878_3_).toArray()));
   }

   public OctavesNoiseGenerator(SharedSeedRandom p_i225879_1_, IntSortedSet p_i225879_2_) {
      if (p_i225879_2_.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int lvt_3_1_ = -p_i225879_2_.firstInt();
         int lvt_4_1_ = p_i225879_2_.lastInt();
         int lvt_5_1_ = lvt_3_1_ + lvt_4_1_ + 1;
         if (lvt_5_1_ < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            ImprovedNoiseGenerator lvt_6_1_ = new ImprovedNoiseGenerator(p_i225879_1_);
            int lvt_7_1_ = lvt_4_1_;
            this.octaves = new ImprovedNoiseGenerator[lvt_5_1_];
            if (lvt_4_1_ >= 0 && lvt_4_1_ < lvt_5_1_ && p_i225879_2_.contains(0)) {
               this.octaves[lvt_4_1_] = lvt_6_1_;
            }

            for(int lvt_8_1_ = lvt_4_1_ + 1; lvt_8_1_ < lvt_5_1_; ++lvt_8_1_) {
               if (lvt_8_1_ >= 0 && p_i225879_2_.contains(lvt_7_1_ - lvt_8_1_)) {
                  this.octaves[lvt_8_1_] = new ImprovedNoiseGenerator(p_i225879_1_);
               } else {
                  p_i225879_1_.skip(262);
               }
            }

            if (lvt_4_1_ > 0) {
               long lvt_8_2_ = (long)(lvt_6_1_.func_215456_a(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * 9.223372036854776E18D);
               SharedSeedRandom lvt_10_1_ = new SharedSeedRandom(lvt_8_2_);

               for(int lvt_11_1_ = lvt_7_1_ - 1; lvt_11_1_ >= 0; --lvt_11_1_) {
                  if (lvt_11_1_ < lvt_5_1_ && p_i225879_2_.contains(lvt_7_1_ - lvt_11_1_)) {
                     this.octaves[lvt_11_1_] = new ImprovedNoiseGenerator(lvt_10_1_);
                  } else {
                     lvt_10_1_.skip(262);
                  }
               }
            }

            this.field_227461_c_ = Math.pow(2.0D, (double)lvt_4_1_);
            this.field_227460_b_ = 1.0D / (Math.pow(2.0D, (double)lvt_5_1_) - 1.0D);
         }
      }
   }

   public double func_205563_a(double p_205563_1_, double p_205563_3_, double p_205563_5_) {
      return this.func_215462_a(p_205563_1_, p_205563_3_, p_205563_5_, 0.0D, 0.0D, false);
   }

   public double func_215462_a(double p_215462_1_, double p_215462_3_, double p_215462_5_, double p_215462_7_, double p_215462_9_, boolean p_215462_11_) {
      double lvt_12_1_ = 0.0D;
      double lvt_14_1_ = this.field_227461_c_;
      double lvt_16_1_ = this.field_227460_b_;
      ImprovedNoiseGenerator[] var18 = this.octaves;
      int var19 = var18.length;

      for(int var20 = 0; var20 < var19; ++var20) {
         ImprovedNoiseGenerator lvt_21_1_ = var18[var20];
         if (lvt_21_1_ != null) {
            lvt_12_1_ += lvt_21_1_.func_215456_a(maintainPrecision(p_215462_1_ * lvt_14_1_), p_215462_11_ ? -lvt_21_1_.yCoord : maintainPrecision(p_215462_3_ * lvt_14_1_), maintainPrecision(p_215462_5_ * lvt_14_1_), p_215462_7_ * lvt_14_1_, p_215462_9_ * lvt_14_1_) * lvt_16_1_;
         }

         lvt_14_1_ /= 2.0D;
         lvt_16_1_ *= 2.0D;
      }

      return lvt_12_1_;
   }

   @Nullable
   public ImprovedNoiseGenerator getOctave(int p_215463_1_) {
      return this.octaves[p_215463_1_];
   }

   public static double maintainPrecision(double p_215461_0_) {
      return p_215461_0_ - (double)MathHelper.lfloor(p_215461_0_ / 3.3554432E7D + 0.5D) * 3.3554432E7D;
   }

   public double func_215460_a(double p_215460_1_, double p_215460_3_, double p_215460_5_, double p_215460_7_) {
      return this.func_215462_a(p_215460_1_, p_215460_3_, 0.0D, p_215460_5_, p_215460_7_, false);
   }
}
