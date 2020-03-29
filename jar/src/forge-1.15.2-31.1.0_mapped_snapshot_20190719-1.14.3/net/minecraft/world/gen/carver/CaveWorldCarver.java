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

public class CaveWorldCarver extends WorldCarver<ProbabilityConfig> {
   public CaveWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49929_1_, int p_i49929_2_) {
      super(p_i49929_1_, p_i49929_2_);
   }

   public boolean shouldCarve(Random p_212868_1_, int p_212868_2_, int p_212868_3_, ProbabilityConfig p_212868_4_) {
      return p_212868_1_.nextFloat() <= p_212868_4_.probability;
   }

   public boolean func_225555_a_(IChunk p_225555_1_, Function<BlockPos, Biome> p_225555_2_, Random p_225555_3_, int p_225555_4_, int p_225555_5_, int p_225555_6_, int p_225555_7_, int p_225555_8_, BitSet p_225555_9_, ProbabilityConfig p_225555_10_) {
      int lvt_11_1_ = (this.func_222704_c() * 2 - 1) * 16;
      int lvt_12_1_ = p_225555_3_.nextInt(p_225555_3_.nextInt(p_225555_3_.nextInt(this.func_222724_a()) + 1) + 1);

      for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_12_1_; ++lvt_13_1_) {
         double lvt_14_1_ = (double)(p_225555_5_ * 16 + p_225555_3_.nextInt(16));
         double lvt_16_1_ = (double)this.generateCaveStartY(p_225555_3_);
         double lvt_18_1_ = (double)(p_225555_6_ * 16 + p_225555_3_.nextInt(16));
         int lvt_20_1_ = 1;
         float lvt_23_2_;
         if (p_225555_3_.nextInt(4) == 0) {
            double lvt_21_1_ = 0.5D;
            lvt_23_2_ = 1.0F + p_225555_3_.nextFloat() * 6.0F;
            this.func_227205_a_(p_225555_1_, p_225555_2_, p_225555_3_.nextLong(), p_225555_4_, p_225555_7_, p_225555_8_, lvt_14_1_, lvt_16_1_, lvt_18_1_, lvt_23_2_, 0.5D, p_225555_9_);
            lvt_20_1_ += p_225555_3_.nextInt(4);
         }

         for(int lvt_21_2_ = 0; lvt_21_2_ < lvt_20_1_; ++lvt_21_2_) {
            float lvt_22_1_ = p_225555_3_.nextFloat() * 6.2831855F;
            lvt_23_2_ = (p_225555_3_.nextFloat() - 0.5F) / 4.0F;
            float lvt_24_1_ = this.generateCaveRadius(p_225555_3_);
            int lvt_25_1_ = lvt_11_1_ - p_225555_3_.nextInt(lvt_11_1_ / 4);
            int lvt_26_1_ = false;
            this.func_227206_a_(p_225555_1_, p_225555_2_, p_225555_3_.nextLong(), p_225555_4_, p_225555_7_, p_225555_8_, lvt_14_1_, lvt_16_1_, lvt_18_1_, lvt_24_1_, lvt_22_1_, lvt_23_2_, 0, lvt_25_1_, this.func_222725_b(), p_225555_9_);
         }
      }

      return true;
   }

   protected int func_222724_a() {
      return 15;
   }

   protected float generateCaveRadius(Random p_222722_1_) {
      float lvt_2_1_ = p_222722_1_.nextFloat() * 2.0F + p_222722_1_.nextFloat();
      if (p_222722_1_.nextInt(10) == 0) {
         lvt_2_1_ *= p_222722_1_.nextFloat() * p_222722_1_.nextFloat() * 3.0F + 1.0F;
      }

      return lvt_2_1_;
   }

   protected double func_222725_b() {
      return 1.0D;
   }

   protected int generateCaveStartY(Random p_222726_1_) {
      return p_222726_1_.nextInt(p_222726_1_.nextInt(120) + 8);
   }

   protected void func_227205_a_(IChunk p_227205_1_, Function<BlockPos, Biome> p_227205_2_, long p_227205_3_, int p_227205_5_, int p_227205_6_, int p_227205_7_, double p_227205_8_, double p_227205_10_, double p_227205_12_, float p_227205_14_, double p_227205_15_, BitSet p_227205_17_) {
      double lvt_18_1_ = 1.5D + (double)(MathHelper.sin(1.5707964F) * p_227205_14_);
      double lvt_20_1_ = lvt_18_1_ * p_227205_15_;
      this.func_227208_a_(p_227205_1_, p_227205_2_, p_227205_3_, p_227205_5_, p_227205_6_, p_227205_7_, p_227205_8_ + 1.0D, p_227205_10_, p_227205_12_, lvt_18_1_, lvt_20_1_, p_227205_17_);
   }

   protected void func_227206_a_(IChunk p_227206_1_, Function<BlockPos, Biome> p_227206_2_, long p_227206_3_, int p_227206_5_, int p_227206_6_, int p_227206_7_, double p_227206_8_, double p_227206_10_, double p_227206_12_, float p_227206_14_, float p_227206_15_, float p_227206_16_, int p_227206_17_, int p_227206_18_, double p_227206_19_, BitSet p_227206_21_) {
      Random lvt_22_1_ = new Random(p_227206_3_);
      int lvt_23_1_ = lvt_22_1_.nextInt(p_227206_18_ / 2) + p_227206_18_ / 4;
      boolean lvt_24_1_ = lvt_22_1_.nextInt(6) == 0;
      float lvt_25_1_ = 0.0F;
      float lvt_26_1_ = 0.0F;

      for(int lvt_27_1_ = p_227206_17_; lvt_27_1_ < p_227206_18_; ++lvt_27_1_) {
         double lvt_28_1_ = 1.5D + (double)(MathHelper.sin(3.1415927F * (float)lvt_27_1_ / (float)p_227206_18_) * p_227206_14_);
         double lvt_30_1_ = lvt_28_1_ * p_227206_19_;
         float lvt_32_1_ = MathHelper.cos(p_227206_16_);
         p_227206_8_ += (double)(MathHelper.cos(p_227206_15_) * lvt_32_1_);
         p_227206_10_ += (double)MathHelper.sin(p_227206_16_);
         p_227206_12_ += (double)(MathHelper.sin(p_227206_15_) * lvt_32_1_);
         p_227206_16_ *= lvt_24_1_ ? 0.92F : 0.7F;
         p_227206_16_ += lvt_26_1_ * 0.1F;
         p_227206_15_ += lvt_25_1_ * 0.1F;
         lvt_26_1_ *= 0.9F;
         lvt_25_1_ *= 0.75F;
         lvt_26_1_ += (lvt_22_1_.nextFloat() - lvt_22_1_.nextFloat()) * lvt_22_1_.nextFloat() * 2.0F;
         lvt_25_1_ += (lvt_22_1_.nextFloat() - lvt_22_1_.nextFloat()) * lvt_22_1_.nextFloat() * 4.0F;
         if (lvt_27_1_ == lvt_23_1_ && p_227206_14_ > 1.0F) {
            this.func_227206_a_(p_227206_1_, p_227206_2_, lvt_22_1_.nextLong(), p_227206_5_, p_227206_6_, p_227206_7_, p_227206_8_, p_227206_10_, p_227206_12_, lvt_22_1_.nextFloat() * 0.5F + 0.5F, p_227206_15_ - 1.5707964F, p_227206_16_ / 3.0F, lvt_27_1_, p_227206_18_, 1.0D, p_227206_21_);
            this.func_227206_a_(p_227206_1_, p_227206_2_, lvt_22_1_.nextLong(), p_227206_5_, p_227206_6_, p_227206_7_, p_227206_8_, p_227206_10_, p_227206_12_, lvt_22_1_.nextFloat() * 0.5F + 0.5F, p_227206_15_ + 1.5707964F, p_227206_16_ / 3.0F, lvt_27_1_, p_227206_18_, 1.0D, p_227206_21_);
            return;
         }

         if (lvt_22_1_.nextInt(4) != 0) {
            if (!this.func_222702_a(p_227206_6_, p_227206_7_, p_227206_8_, p_227206_12_, lvt_27_1_, p_227206_18_, p_227206_14_)) {
               return;
            }

            this.func_227208_a_(p_227206_1_, p_227206_2_, p_227206_3_, p_227206_5_, p_227206_6_, p_227206_7_, p_227206_8_, p_227206_10_, p_227206_12_, lvt_28_1_, lvt_30_1_, p_227206_21_);
         }
      }

   }

   protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
      return p_222708_3_ <= -0.7D || p_222708_1_ * p_222708_1_ + p_222708_3_ * p_222708_3_ + p_222708_5_ * p_222708_5_ >= 1.0D;
   }
}
