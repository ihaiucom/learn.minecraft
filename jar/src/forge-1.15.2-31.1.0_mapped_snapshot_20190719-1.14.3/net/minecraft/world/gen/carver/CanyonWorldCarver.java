package net.minecraft.world.gen.carver;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class CanyonWorldCarver extends WorldCarver<ProbabilityConfig> {
   private final float[] field_202536_i = new float[1024];

   public CanyonWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49930_1_) {
      super(p_i49930_1_, 256);
   }

   public boolean shouldCarve(Random p_212868_1_, int p_212868_2_, int p_212868_3_, ProbabilityConfig p_212868_4_) {
      return p_212868_1_.nextFloat() <= p_212868_4_.probability;
   }

   public boolean func_225555_a_(IChunk p_225555_1_, Function<BlockPos, Biome> p_225555_2_, Random p_225555_3_, int p_225555_4_, int p_225555_5_, int p_225555_6_, int p_225555_7_, int p_225555_8_, BitSet p_225555_9_, ProbabilityConfig p_225555_10_) {
      int lvt_11_1_ = (this.func_222704_c() * 2 - 1) * 16;
      double lvt_12_1_ = (double)(p_225555_5_ * 16 + p_225555_3_.nextInt(16));
      double lvt_14_1_ = (double)(p_225555_3_.nextInt(p_225555_3_.nextInt(40) + 8) + 20);
      double lvt_16_1_ = (double)(p_225555_6_ * 16 + p_225555_3_.nextInt(16));
      float lvt_18_1_ = p_225555_3_.nextFloat() * 6.2831855F;
      float lvt_19_1_ = (p_225555_3_.nextFloat() - 0.5F) * 2.0F / 8.0F;
      double lvt_20_1_ = 3.0D;
      float lvt_22_1_ = (p_225555_3_.nextFloat() * 2.0F + p_225555_3_.nextFloat()) * 2.0F;
      int lvt_23_1_ = lvt_11_1_ - p_225555_3_.nextInt(lvt_11_1_ / 4);
      int lvt_24_1_ = false;
      this.func_227204_a_(p_225555_1_, p_225555_2_, p_225555_3_.nextLong(), p_225555_4_, p_225555_7_, p_225555_8_, lvt_12_1_, lvt_14_1_, lvt_16_1_, lvt_22_1_, lvt_18_1_, lvt_19_1_, 0, lvt_23_1_, 3.0D, p_225555_9_);
      return true;
   }

   private void func_227204_a_(IChunk p_227204_1_, Function<BlockPos, Biome> p_227204_2_, long p_227204_3_, int p_227204_5_, int p_227204_6_, int p_227204_7_, double p_227204_8_, double p_227204_10_, double p_227204_12_, float p_227204_14_, float p_227204_15_, float p_227204_16_, int p_227204_17_, int p_227204_18_, double p_227204_19_, BitSet p_227204_21_) {
      Random lvt_22_1_ = new Random(p_227204_3_);
      float lvt_23_1_ = 1.0F;

      for(int lvt_24_1_ = 0; lvt_24_1_ < 256; ++lvt_24_1_) {
         if (lvt_24_1_ == 0 || lvt_22_1_.nextInt(3) == 0) {
            lvt_23_1_ = 1.0F + lvt_22_1_.nextFloat() * lvt_22_1_.nextFloat();
         }

         this.field_202536_i[lvt_24_1_] = lvt_23_1_ * lvt_23_1_;
      }

      float lvt_24_2_ = 0.0F;
      float lvt_25_1_ = 0.0F;

      for(int lvt_26_1_ = p_227204_17_; lvt_26_1_ < p_227204_18_; ++lvt_26_1_) {
         double lvt_27_1_ = 1.5D + (double)(MathHelper.sin((float)lvt_26_1_ * 3.1415927F / (float)p_227204_18_) * p_227204_14_);
         double lvt_29_1_ = lvt_27_1_ * p_227204_19_;
         lvt_27_1_ *= (double)lvt_22_1_.nextFloat() * 0.25D + 0.75D;
         lvt_29_1_ *= (double)lvt_22_1_.nextFloat() * 0.25D + 0.75D;
         float lvt_31_1_ = MathHelper.cos(p_227204_16_);
         float lvt_32_1_ = MathHelper.sin(p_227204_16_);
         p_227204_8_ += (double)(MathHelper.cos(p_227204_15_) * lvt_31_1_);
         p_227204_10_ += (double)lvt_32_1_;
         p_227204_12_ += (double)(MathHelper.sin(p_227204_15_) * lvt_31_1_);
         p_227204_16_ *= 0.7F;
         p_227204_16_ += lvt_25_1_ * 0.05F;
         p_227204_15_ += lvt_24_2_ * 0.05F;
         lvt_25_1_ *= 0.8F;
         lvt_24_2_ *= 0.5F;
         lvt_25_1_ += (lvt_22_1_.nextFloat() - lvt_22_1_.nextFloat()) * lvt_22_1_.nextFloat() * 2.0F;
         lvt_24_2_ += (lvt_22_1_.nextFloat() - lvt_22_1_.nextFloat()) * lvt_22_1_.nextFloat() * 4.0F;
         if (lvt_22_1_.nextInt(4) != 0) {
            if (!this.func_222702_a(p_227204_6_, p_227204_7_, p_227204_8_, p_227204_12_, lvt_26_1_, p_227204_18_, p_227204_14_)) {
               return;
            }

            this.func_227208_a_(p_227204_1_, p_227204_2_, p_227204_3_, p_227204_5_, p_227204_6_, p_227204_7_, p_227204_8_, p_227204_10_, p_227204_12_, lvt_27_1_, lvt_29_1_, p_227204_21_);
         }
      }

   }

   protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
      return (p_222708_1_ * p_222708_1_ + p_222708_5_ * p_222708_5_) * (double)this.field_202536_i[p_222708_7_ - 1] + p_222708_3_ * p_222708_3_ / 6.0D >= 1.0D;
   }
}
