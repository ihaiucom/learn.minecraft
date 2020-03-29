package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class ScatteredStructure<C extends IFeatureConfig> extends Structure<C> {
   public ScatteredStructure(Function<Dynamic<?>, ? extends C> p_i51449_1_) {
      super(p_i51449_1_);
   }

   protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int lvt_7_1_ = this.getBiomeFeatureDistance(p_211744_1_);
      int lvt_8_1_ = this.getBiomeFeatureSeparation(p_211744_1_);
      int lvt_9_1_ = p_211744_3_ + lvt_7_1_ * p_211744_5_;
      int lvt_10_1_ = p_211744_4_ + lvt_7_1_ * p_211744_6_;
      int lvt_11_1_ = lvt_9_1_ < 0 ? lvt_9_1_ - lvt_7_1_ + 1 : lvt_9_1_;
      int lvt_12_1_ = lvt_10_1_ < 0 ? lvt_10_1_ - lvt_7_1_ + 1 : lvt_10_1_;
      int lvt_13_1_ = lvt_11_1_ / lvt_7_1_;
      int lvt_14_1_ = lvt_12_1_ / lvt_7_1_;
      ((SharedSeedRandom)p_211744_2_).setLargeFeatureSeedWithSalt(p_211744_1_.getSeed(), lvt_13_1_, lvt_14_1_, this.getSeedModifier());
      lvt_13_1_ *= lvt_7_1_;
      lvt_14_1_ *= lvt_7_1_;
      lvt_13_1_ += p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_);
      lvt_14_1_ += p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_);
      return new ChunkPos(lvt_13_1_, lvt_14_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ChunkPos lvt_7_1_ = this.getStartPositionForPosition(p_225558_2_, p_225558_3_, p_225558_4_, p_225558_5_, 0, 0);
      return p_225558_4_ == lvt_7_1_.x && p_225558_5_ == lvt_7_1_.z && p_225558_2_.hasStructure(p_225558_6_, this);
   }

   protected int getBiomeFeatureDistance(ChunkGenerator<?> p_204030_1_) {
      return p_204030_1_.getSettings().getBiomeFeatureDistance();
   }

   protected int getBiomeFeatureSeparation(ChunkGenerator<?> p_211745_1_) {
      return p_211745_1_.getSettings().getBiomeFeatureSeparation();
   }

   protected abstract int getSeedModifier();
}
