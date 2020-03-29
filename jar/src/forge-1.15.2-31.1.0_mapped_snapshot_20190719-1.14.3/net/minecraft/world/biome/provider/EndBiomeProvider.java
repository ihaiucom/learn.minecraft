package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.SimplexNoiseGenerator;

public class EndBiomeProvider extends BiomeProvider {
   private final SimplexNoiseGenerator generator;
   private final SharedSeedRandom random;
   private static final Set<Biome> field_226853_f_;

   public EndBiomeProvider(EndBiomeProviderSettings p_i48970_1_) {
      super(field_226853_f_);
      this.random = new SharedSeedRandom(p_i48970_1_.getSeed());
      this.random.skip(17292);
      this.generator = new SimplexNoiseGenerator(this.random);
   }

   public Biome func_225526_b_(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      int lvt_4_1_ = p_225526_1_ >> 2;
      int lvt_5_1_ = p_225526_3_ >> 2;
      if ((long)lvt_4_1_ * (long)lvt_4_1_ + (long)lvt_5_1_ * (long)lvt_5_1_ <= 4096L) {
         return Biomes.THE_END;
      } else {
         float lvt_6_1_ = this.func_222365_c(lvt_4_1_ * 2 + 1, lvt_5_1_ * 2 + 1);
         if (lvt_6_1_ > 40.0F) {
            return Biomes.END_HIGHLANDS;
         } else if (lvt_6_1_ >= 0.0F) {
            return Biomes.END_MIDLANDS;
         } else {
            return lvt_6_1_ < -20.0F ? Biomes.SMALL_END_ISLANDS : Biomes.END_BARRENS;
         }
      }
   }

   public float func_222365_c(int p_222365_1_, int p_222365_2_) {
      int lvt_3_1_ = p_222365_1_ / 2;
      int lvt_4_1_ = p_222365_2_ / 2;
      int lvt_5_1_ = p_222365_1_ % 2;
      int lvt_6_1_ = p_222365_2_ % 2;
      float lvt_7_1_ = 100.0F - MathHelper.sqrt((float)(p_222365_1_ * p_222365_1_ + p_222365_2_ * p_222365_2_)) * 8.0F;
      lvt_7_1_ = MathHelper.clamp(lvt_7_1_, -100.0F, 80.0F);

      for(int lvt_8_1_ = -12; lvt_8_1_ <= 12; ++lvt_8_1_) {
         for(int lvt_9_1_ = -12; lvt_9_1_ <= 12; ++lvt_9_1_) {
            long lvt_10_1_ = (long)(lvt_3_1_ + lvt_8_1_);
            long lvt_12_1_ = (long)(lvt_4_1_ + lvt_9_1_);
            if (lvt_10_1_ * lvt_10_1_ + lvt_12_1_ * lvt_12_1_ > 4096L && this.generator.getValue((double)lvt_10_1_, (double)lvt_12_1_) < -0.8999999761581421D) {
               float lvt_14_1_ = (MathHelper.abs((float)lvt_10_1_) * 3439.0F + MathHelper.abs((float)lvt_12_1_) * 147.0F) % 13.0F + 9.0F;
               float lvt_15_1_ = (float)(lvt_5_1_ - lvt_8_1_ * 2);
               float lvt_16_1_ = (float)(lvt_6_1_ - lvt_9_1_ * 2);
               float lvt_17_1_ = 100.0F - MathHelper.sqrt(lvt_15_1_ * lvt_15_1_ + lvt_16_1_ * lvt_16_1_) * lvt_14_1_;
               lvt_17_1_ = MathHelper.clamp(lvt_17_1_, -100.0F, 80.0F);
               lvt_7_1_ = Math.max(lvt_7_1_, lvt_17_1_);
            }
         }
      }

      return lvt_7_1_;
   }

   static {
      field_226853_f_ = ImmutableSet.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);
   }
}
