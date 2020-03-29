package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.math.NumberUtils;

public class MathHelper {
   public static final float SQRT_2 = sqrt(2.0F);
   private static final float[] SIN_TABLE = (float[])Util.make(new float[65536], (p_203445_0_) -> {
      for(int lvt_1_1_ = 0; lvt_1_1_ < p_203445_0_.length; ++lvt_1_1_) {
         p_203445_0_[lvt_1_1_] = (float)Math.sin((double)lvt_1_1_ * 3.141592653589793D * 2.0D / 65536.0D);
      }

   });
   private static final Random RANDOM = new Random();
   private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
   private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
   private static final double[] ASINE_TAB = new double[257];
   private static final double[] COS_TAB = new double[257];

   public static float sin(float p_76126_0_) {
      return SIN_TABLE[(int)(p_76126_0_ * 10430.378F) & '\uffff'];
   }

   public static float cos(float p_76134_0_) {
      return SIN_TABLE[(int)(p_76134_0_ * 10430.378F + 16384.0F) & '\uffff'];
   }

   public static float sqrt(float p_76129_0_) {
      return (float)Math.sqrt((double)p_76129_0_);
   }

   public static float sqrt(double p_76133_0_) {
      return (float)Math.sqrt(p_76133_0_);
   }

   public static int floor(float p_76141_0_) {
      int lvt_1_1_ = (int)p_76141_0_;
      return p_76141_0_ < (float)lvt_1_1_ ? lvt_1_1_ - 1 : lvt_1_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static int fastFloor(double p_76140_0_) {
      return (int)(p_76140_0_ + 1024.0D) - 1024;
   }

   public static int floor(double p_76128_0_) {
      int lvt_2_1_ = (int)p_76128_0_;
      return p_76128_0_ < (double)lvt_2_1_ ? lvt_2_1_ - 1 : lvt_2_1_;
   }

   public static long lfloor(double p_76124_0_) {
      long lvt_2_1_ = (long)p_76124_0_;
      return p_76124_0_ < (double)lvt_2_1_ ? lvt_2_1_ - 1L : lvt_2_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static int absFloor(double p_207806_0_) {
      return (int)(p_207806_0_ >= 0.0D ? p_207806_0_ : -p_207806_0_ + 1.0D);
   }

   public static float abs(float p_76135_0_) {
      return Math.abs(p_76135_0_);
   }

   public static int abs(int p_76130_0_) {
      return Math.abs(p_76130_0_);
   }

   public static int ceil(float p_76123_0_) {
      int lvt_1_1_ = (int)p_76123_0_;
      return p_76123_0_ > (float)lvt_1_1_ ? lvt_1_1_ + 1 : lvt_1_1_;
   }

   public static int ceil(double p_76143_0_) {
      int lvt_2_1_ = (int)p_76143_0_;
      return p_76143_0_ > (double)lvt_2_1_ ? lvt_2_1_ + 1 : lvt_2_1_;
   }

   public static int clamp(int p_76125_0_, int p_76125_1_, int p_76125_2_) {
      if (p_76125_0_ < p_76125_1_) {
         return p_76125_1_;
      } else {
         return p_76125_0_ > p_76125_2_ ? p_76125_2_ : p_76125_0_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static long func_226163_a_(long p_226163_0_, long p_226163_2_, long p_226163_4_) {
      if (p_226163_0_ < p_226163_2_) {
         return p_226163_2_;
      } else {
         return p_226163_0_ > p_226163_4_ ? p_226163_4_ : p_226163_0_;
      }
   }

   public static float clamp(float p_76131_0_, float p_76131_1_, float p_76131_2_) {
      if (p_76131_0_ < p_76131_1_) {
         return p_76131_1_;
      } else {
         return p_76131_0_ > p_76131_2_ ? p_76131_2_ : p_76131_0_;
      }
   }

   public static double clamp(double p_151237_0_, double p_151237_2_, double p_151237_4_) {
      if (p_151237_0_ < p_151237_2_) {
         return p_151237_2_;
      } else {
         return p_151237_0_ > p_151237_4_ ? p_151237_4_ : p_151237_0_;
      }
   }

   public static double clampedLerp(double p_151238_0_, double p_151238_2_, double p_151238_4_) {
      if (p_151238_4_ < 0.0D) {
         return p_151238_0_;
      } else {
         return p_151238_4_ > 1.0D ? p_151238_2_ : lerp(p_151238_4_, p_151238_0_, p_151238_2_);
      }
   }

   public static double absMax(double p_76132_0_, double p_76132_2_) {
      if (p_76132_0_ < 0.0D) {
         p_76132_0_ = -p_76132_0_;
      }

      if (p_76132_2_ < 0.0D) {
         p_76132_2_ = -p_76132_2_;
      }

      return p_76132_0_ > p_76132_2_ ? p_76132_0_ : p_76132_2_;
   }

   public static int intFloorDiv(int p_76137_0_, int p_76137_1_) {
      return Math.floorDiv(p_76137_0_, p_76137_1_);
   }

   public static int nextInt(Random p_76136_0_, int p_76136_1_, int p_76136_2_) {
      return p_76136_1_ >= p_76136_2_ ? p_76136_1_ : p_76136_0_.nextInt(p_76136_2_ - p_76136_1_ + 1) + p_76136_1_;
   }

   public static float nextFloat(Random p_151240_0_, float p_151240_1_, float p_151240_2_) {
      return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
   }

   public static double nextDouble(Random p_82716_0_, double p_82716_1_, double p_82716_3_) {
      return p_82716_1_ >= p_82716_3_ ? p_82716_1_ : p_82716_0_.nextDouble() * (p_82716_3_ - p_82716_1_) + p_82716_1_;
   }

   public static double average(long[] p_76127_0_) {
      long lvt_1_1_ = 0L;
      long[] var3 = p_76127_0_;
      int var4 = p_76127_0_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         long lvt_6_1_ = var3[var5];
         lvt_1_1_ += lvt_6_1_;
      }

      return (double)lvt_1_1_ / (double)p_76127_0_.length;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean epsilonEquals(float p_180185_0_, float p_180185_1_) {
      return Math.abs(p_180185_1_ - p_180185_0_) < 1.0E-5F;
   }

   public static boolean epsilonEquals(double p_219806_0_, double p_219806_2_) {
      return Math.abs(p_219806_2_ - p_219806_0_) < 9.999999747378752E-6D;
   }

   public static int normalizeAngle(int p_180184_0_, int p_180184_1_) {
      return Math.floorMod(p_180184_0_, p_180184_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public static float positiveModulo(float p_188207_0_, float p_188207_1_) {
      return (p_188207_0_ % p_188207_1_ + p_188207_1_) % p_188207_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static double positiveModulo(double p_191273_0_, double p_191273_2_) {
      return (p_191273_0_ % p_191273_2_ + p_191273_2_) % p_191273_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public static int wrapDegrees(int p_188209_0_) {
      int lvt_1_1_ = p_188209_0_ % 360;
      if (lvt_1_1_ >= 180) {
         lvt_1_1_ -= 360;
      }

      if (lvt_1_1_ < -180) {
         lvt_1_1_ += 360;
      }

      return lvt_1_1_;
   }

   public static float wrapDegrees(float p_76142_0_) {
      float lvt_1_1_ = p_76142_0_ % 360.0F;
      if (lvt_1_1_ >= 180.0F) {
         lvt_1_1_ -= 360.0F;
      }

      if (lvt_1_1_ < -180.0F) {
         lvt_1_1_ += 360.0F;
      }

      return lvt_1_1_;
   }

   public static double wrapDegrees(double p_76138_0_) {
      double lvt_2_1_ = p_76138_0_ % 360.0D;
      if (lvt_2_1_ >= 180.0D) {
         lvt_2_1_ -= 360.0D;
      }

      if (lvt_2_1_ < -180.0D) {
         lvt_2_1_ += 360.0D;
      }

      return lvt_2_1_;
   }

   public static float wrapSubtractDegrees(float p_203302_0_, float p_203302_1_) {
      return wrapDegrees(p_203302_1_ - p_203302_0_);
   }

   public static float degreesDifferenceAbs(float p_203301_0_, float p_203301_1_) {
      return abs(wrapSubtractDegrees(p_203301_0_, p_203301_1_));
   }

   public static float func_219800_b(float p_219800_0_, float p_219800_1_, float p_219800_2_) {
      float lvt_3_1_ = wrapSubtractDegrees(p_219800_0_, p_219800_1_);
      float lvt_4_1_ = clamp(lvt_3_1_, -p_219800_2_, p_219800_2_);
      return p_219800_1_ - lvt_4_1_;
   }

   public static float approach(float p_203300_0_, float p_203300_1_, float p_203300_2_) {
      p_203300_2_ = abs(p_203300_2_);
      return p_203300_0_ < p_203300_1_ ? clamp(p_203300_0_ + p_203300_2_, p_203300_0_, p_203300_1_) : clamp(p_203300_0_ - p_203300_2_, p_203300_1_, p_203300_0_);
   }

   public static float approachDegrees(float p_203303_0_, float p_203303_1_, float p_203303_2_) {
      float lvt_3_1_ = wrapSubtractDegrees(p_203303_0_, p_203303_1_);
      return approach(p_203303_0_, p_203303_0_ + lvt_3_1_, p_203303_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public static int getInt(String p_82715_0_, int p_82715_1_) {
      return NumberUtils.toInt(p_82715_0_, p_82715_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public static int getInt(String p_82714_0_, int p_82714_1_, int p_82714_2_) {
      return Math.max(p_82714_2_, getInt(p_82714_0_, p_82714_1_));
   }

   @OnlyIn(Dist.CLIENT)
   public static double getDouble(String p_207805_0_, double p_207805_1_) {
      try {
         return Double.parseDouble(p_207805_0_);
      } catch (Throwable var4) {
         return p_207805_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static double getDouble(String p_207804_0_, double p_207804_1_, double p_207804_3_) {
      return Math.max(p_207804_3_, getDouble(p_207804_0_, p_207804_1_));
   }

   public static int smallestEncompassingPowerOfTwo(int p_151236_0_) {
      int lvt_1_1_ = p_151236_0_ - 1;
      lvt_1_1_ |= lvt_1_1_ >> 1;
      lvt_1_1_ |= lvt_1_1_ >> 2;
      lvt_1_1_ |= lvt_1_1_ >> 4;
      lvt_1_1_ |= lvt_1_1_ >> 8;
      lvt_1_1_ |= lvt_1_1_ >> 16;
      return lvt_1_1_ + 1;
   }

   private static boolean isPowerOfTwo(int p_151235_0_) {
      return p_151235_0_ != 0 && (p_151235_0_ & p_151235_0_ - 1) == 0;
   }

   public static int log2DeBruijn(int p_151241_0_) {
      p_151241_0_ = isPowerOfTwo(p_151241_0_) ? p_151241_0_ : smallestEncompassingPowerOfTwo(p_151241_0_);
      return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)p_151241_0_ * 125613361L >> 27) & 31];
   }

   public static int log2(int p_151239_0_) {
      return log2DeBruijn(p_151239_0_) - (isPowerOfTwo(p_151239_0_) ? 0 : 1);
   }

   public static int roundUp(int p_154354_0_, int p_154354_1_) {
      if (p_154354_1_ == 0) {
         return 0;
      } else if (p_154354_0_ == 0) {
         return p_154354_1_;
      } else {
         if (p_154354_0_ < 0) {
            p_154354_1_ *= -1;
         }

         int lvt_2_1_ = p_154354_0_ % p_154354_1_;
         return lvt_2_1_ == 0 ? p_154354_0_ : p_154354_0_ + p_154354_1_ - lvt_2_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static int rgb(float p_180183_0_, float p_180183_1_, float p_180183_2_) {
      return rgb(floor(p_180183_0_ * 255.0F), floor(p_180183_1_ * 255.0F), floor(p_180183_2_ * 255.0F));
   }

   @OnlyIn(Dist.CLIENT)
   public static int rgb(int p_180181_0_, int p_180181_1_, int p_180181_2_) {
      int lvt_3_1_ = (p_180181_0_ << 8) + p_180181_1_;
      lvt_3_1_ = (lvt_3_1_ << 8) + p_180181_2_;
      return lvt_3_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static float func_226164_h_(float p_226164_0_) {
      return p_226164_0_ - (float)floor(p_226164_0_);
   }

   public static double frac(double p_181162_0_) {
      return p_181162_0_ - (double)lfloor(p_181162_0_);
   }

   public static long getPositionRandom(Vec3i p_180186_0_) {
      return getCoordinateRandom(p_180186_0_.getX(), p_180186_0_.getY(), p_180186_0_.getZ());
   }

   public static long getCoordinateRandom(int p_180187_0_, int p_180187_1_, int p_180187_2_) {
      long lvt_3_1_ = (long)(p_180187_0_ * 3129871) ^ (long)p_180187_2_ * 116129781L ^ (long)p_180187_1_;
      lvt_3_1_ = lvt_3_1_ * lvt_3_1_ * 42317861L + lvt_3_1_ * 11L;
      return lvt_3_1_ >> 16;
   }

   public static UUID getRandomUUID(Random p_180182_0_) {
      long lvt_1_1_ = p_180182_0_.nextLong() & -61441L | 16384L;
      long lvt_3_1_ = p_180182_0_.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
      return new UUID(lvt_1_1_, lvt_3_1_);
   }

   public static UUID getRandomUUID() {
      return getRandomUUID(RANDOM);
   }

   public static double pct(double p_181160_0_, double p_181160_2_, double p_181160_4_) {
      return (p_181160_0_ - p_181160_2_) / (p_181160_4_ - p_181160_2_);
   }

   public static double atan2(double p_181159_0_, double p_181159_2_) {
      double lvt_4_1_ = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
      if (Double.isNaN(lvt_4_1_)) {
         return Double.NaN;
      } else {
         boolean lvt_6_1_ = p_181159_0_ < 0.0D;
         if (lvt_6_1_) {
            p_181159_0_ = -p_181159_0_;
         }

         boolean lvt_7_1_ = p_181159_2_ < 0.0D;
         if (lvt_7_1_) {
            p_181159_2_ = -p_181159_2_;
         }

         boolean lvt_8_1_ = p_181159_0_ > p_181159_2_;
         double lvt_9_2_;
         if (lvt_8_1_) {
            lvt_9_2_ = p_181159_2_;
            p_181159_2_ = p_181159_0_;
            p_181159_0_ = lvt_9_2_;
         }

         lvt_9_2_ = fastInvSqrt(lvt_4_1_);
         p_181159_2_ *= lvt_9_2_;
         p_181159_0_ *= lvt_9_2_;
         double lvt_11_1_ = FRAC_BIAS + p_181159_0_;
         int lvt_13_1_ = (int)Double.doubleToRawLongBits(lvt_11_1_);
         double lvt_14_1_ = ASINE_TAB[lvt_13_1_];
         double lvt_16_1_ = COS_TAB[lvt_13_1_];
         double lvt_18_1_ = lvt_11_1_ - FRAC_BIAS;
         double lvt_20_1_ = p_181159_0_ * lvt_16_1_ - p_181159_2_ * lvt_18_1_;
         double lvt_22_1_ = (6.0D + lvt_20_1_ * lvt_20_1_) * lvt_20_1_ * 0.16666666666666666D;
         double lvt_24_1_ = lvt_14_1_ + lvt_22_1_;
         if (lvt_8_1_) {
            lvt_24_1_ = 1.5707963267948966D - lvt_24_1_;
         }

         if (lvt_7_1_) {
            lvt_24_1_ = 3.141592653589793D - lvt_24_1_;
         }

         if (lvt_6_1_) {
            lvt_24_1_ = -lvt_24_1_;
         }

         return lvt_24_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static float func_226165_i_(float p_226165_0_) {
      float lvt_1_1_ = 0.5F * p_226165_0_;
      int lvt_2_1_ = Float.floatToIntBits(p_226165_0_);
      lvt_2_1_ = 1597463007 - (lvt_2_1_ >> 1);
      p_226165_0_ = Float.intBitsToFloat(lvt_2_1_);
      p_226165_0_ *= 1.5F - lvt_1_1_ * p_226165_0_ * p_226165_0_;
      return p_226165_0_;
   }

   public static double fastInvSqrt(double p_181161_0_) {
      double lvt_2_1_ = 0.5D * p_181161_0_;
      long lvt_4_1_ = Double.doubleToRawLongBits(p_181161_0_);
      lvt_4_1_ = 6910469410427058090L - (lvt_4_1_ >> 1);
      p_181161_0_ = Double.longBitsToDouble(lvt_4_1_);
      p_181161_0_ *= 1.5D - lvt_2_1_ * p_181161_0_ * p_181161_0_;
      return p_181161_0_;
   }

   @OnlyIn(Dist.CLIENT)
   public static float func_226166_j_(float p_226166_0_) {
      int lvt_1_1_ = Float.floatToIntBits(p_226166_0_);
      lvt_1_1_ = 1419967116 - lvt_1_1_ / 3;
      float lvt_2_1_ = Float.intBitsToFloat(lvt_1_1_);
      lvt_2_1_ = 0.6666667F * lvt_2_1_ + 1.0F / (3.0F * lvt_2_1_ * lvt_2_1_ * p_226166_0_);
      lvt_2_1_ = 0.6666667F * lvt_2_1_ + 1.0F / (3.0F * lvt_2_1_ * lvt_2_1_ * p_226166_0_);
      return lvt_2_1_;
   }

   public static int hsvToRGB(float p_181758_0_, float p_181758_1_, float p_181758_2_) {
      int lvt_3_1_ = (int)(p_181758_0_ * 6.0F) % 6;
      float lvt_4_1_ = p_181758_0_ * 6.0F - (float)lvt_3_1_;
      float lvt_5_1_ = p_181758_2_ * (1.0F - p_181758_1_);
      float lvt_6_1_ = p_181758_2_ * (1.0F - lvt_4_1_ * p_181758_1_);
      float lvt_7_1_ = p_181758_2_ * (1.0F - (1.0F - lvt_4_1_) * p_181758_1_);
      float lvt_8_7_;
      float lvt_9_7_;
      float lvt_10_7_;
      switch(lvt_3_1_) {
      case 0:
         lvt_8_7_ = p_181758_2_;
         lvt_9_7_ = lvt_7_1_;
         lvt_10_7_ = lvt_5_1_;
         break;
      case 1:
         lvt_8_7_ = lvt_6_1_;
         lvt_9_7_ = p_181758_2_;
         lvt_10_7_ = lvt_5_1_;
         break;
      case 2:
         lvt_8_7_ = lvt_5_1_;
         lvt_9_7_ = p_181758_2_;
         lvt_10_7_ = lvt_7_1_;
         break;
      case 3:
         lvt_8_7_ = lvt_5_1_;
         lvt_9_7_ = lvt_6_1_;
         lvt_10_7_ = p_181758_2_;
         break;
      case 4:
         lvt_8_7_ = lvt_7_1_;
         lvt_9_7_ = lvt_5_1_;
         lvt_10_7_ = p_181758_2_;
         break;
      case 5:
         lvt_8_7_ = p_181758_2_;
         lvt_9_7_ = lvt_5_1_;
         lvt_10_7_ = lvt_6_1_;
         break;
      default:
         throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + p_181758_0_ + ", " + p_181758_1_ + ", " + p_181758_2_);
      }

      int lvt_11_1_ = clamp((int)(lvt_8_7_ * 255.0F), 0, 255);
      int lvt_12_1_ = clamp((int)(lvt_9_7_ * 255.0F), 0, 255);
      int lvt_13_1_ = clamp((int)(lvt_10_7_ * 255.0F), 0, 255);
      return lvt_11_1_ << 16 | lvt_12_1_ << 8 | lvt_13_1_;
   }

   public static int hash(int p_188208_0_) {
      p_188208_0_ ^= p_188208_0_ >>> 16;
      p_188208_0_ *= -2048144789;
      p_188208_0_ ^= p_188208_0_ >>> 13;
      p_188208_0_ *= -1028477387;
      p_188208_0_ ^= p_188208_0_ >>> 16;
      return p_188208_0_;
   }

   public static int binarySearch(int p_199093_0_, int p_199093_1_, IntPredicate p_199093_2_) {
      int lvt_3_1_ = p_199093_1_ - p_199093_0_;

      while(lvt_3_1_ > 0) {
         int lvt_4_1_ = lvt_3_1_ / 2;
         int lvt_5_1_ = p_199093_0_ + lvt_4_1_;
         if (p_199093_2_.test(lvt_5_1_)) {
            lvt_3_1_ = lvt_4_1_;
         } else {
            p_199093_0_ = lvt_5_1_ + 1;
            lvt_3_1_ -= lvt_4_1_ + 1;
         }
      }

      return p_199093_0_;
   }

   public static float lerp(float p_219799_0_, float p_219799_1_, float p_219799_2_) {
      return p_219799_1_ + p_219799_0_ * (p_219799_2_ - p_219799_1_);
   }

   public static double lerp(double p_219803_0_, double p_219803_2_, double p_219803_4_) {
      return p_219803_2_ + p_219803_0_ * (p_219803_4_ - p_219803_2_);
   }

   public static double lerp2(double p_219804_0_, double p_219804_2_, double p_219804_4_, double p_219804_6_, double p_219804_8_, double p_219804_10_) {
      return lerp(p_219804_2_, lerp(p_219804_0_, p_219804_4_, p_219804_6_), lerp(p_219804_0_, p_219804_8_, p_219804_10_));
   }

   public static double lerp3(double p_219807_0_, double p_219807_2_, double p_219807_4_, double p_219807_6_, double p_219807_8_, double p_219807_10_, double p_219807_12_, double p_219807_14_, double p_219807_16_, double p_219807_18_, double p_219807_20_) {
      return lerp(p_219807_4_, lerp2(p_219807_0_, p_219807_2_, p_219807_6_, p_219807_8_, p_219807_10_, p_219807_12_), lerp2(p_219807_0_, p_219807_2_, p_219807_14_, p_219807_16_, p_219807_18_, p_219807_20_));
   }

   public static double perlinFade(double p_219801_0_) {
      return p_219801_0_ * p_219801_0_ * p_219801_0_ * (p_219801_0_ * (p_219801_0_ * 6.0D - 15.0D) + 10.0D);
   }

   public static int signum(double p_219802_0_) {
      if (p_219802_0_ == 0.0D) {
         return 0;
      } else {
         return p_219802_0_ > 0.0D ? 1 : -1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static float func_219805_h(float p_219805_0_, float p_219805_1_, float p_219805_2_) {
      return p_219805_1_ + p_219805_0_ * wrapDegrees(p_219805_2_ - p_219805_1_);
   }

   @Deprecated
   public static float func_226167_j_(float p_226167_0_, float p_226167_1_, float p_226167_2_) {
      float lvt_3_1_;
      for(lvt_3_1_ = p_226167_1_ - p_226167_0_; lvt_3_1_ < -180.0F; lvt_3_1_ += 360.0F) {
      }

      while(lvt_3_1_ >= 180.0F) {
         lvt_3_1_ -= 360.0F;
      }

      return p_226167_0_ + p_226167_2_ * lvt_3_1_;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public static float func_226168_l_(double p_226168_0_) {
      while(p_226168_0_ >= 180.0D) {
         p_226168_0_ -= 360.0D;
      }

      while(p_226168_0_ < -180.0D) {
         p_226168_0_ += 360.0D;
      }

      return (float)p_226168_0_;
   }

   static {
      for(int lvt_0_1_ = 0; lvt_0_1_ < 257; ++lvt_0_1_) {
         double lvt_1_1_ = (double)lvt_0_1_ / 256.0D;
         double lvt_3_1_ = Math.asin(lvt_1_1_);
         COS_TAB[lvt_0_1_] = Math.cos(lvt_3_1_);
         ASINE_TAB[lvt_0_1_] = lvt_3_1_;
      }

   }
}
