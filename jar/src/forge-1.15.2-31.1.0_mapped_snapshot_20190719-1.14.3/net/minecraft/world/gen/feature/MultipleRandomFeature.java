package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class MultipleRandomFeature extends Feature<MultipleWithChanceRandomFeatureConfig> {
   public MultipleRandomFeature(Function<Dynamic<?>, ? extends MultipleWithChanceRandomFeatureConfig> p_i51453_1_) {
      super(p_i51453_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, MultipleWithChanceRandomFeatureConfig p_212245_5_) {
      int lvt_6_1_ = p_212245_3_.nextInt(5) - 3 + p_212245_5_.count;

      for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_; ++lvt_7_1_) {
         int lvt_8_1_ = p_212245_3_.nextInt(p_212245_5_.features.size());
         ConfiguredFeature<?, ?> lvt_9_1_ = (ConfiguredFeature)p_212245_5_.features.get(lvt_8_1_);
         lvt_9_1_.place(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
      }

      return true;
   }
}
