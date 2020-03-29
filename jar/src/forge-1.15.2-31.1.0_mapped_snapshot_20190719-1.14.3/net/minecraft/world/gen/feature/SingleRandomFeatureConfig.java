package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class SingleRandomFeatureConfig extends Feature<SingleRandomFeature> {
   public SingleRandomFeatureConfig(Function<Dynamic<?>, ? extends SingleRandomFeature> p_i51436_1_) {
      super(p_i51436_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, SingleRandomFeature p_212245_5_) {
      int lvt_6_1_ = p_212245_3_.nextInt(p_212245_5_.features.size());
      ConfiguredFeature<?, ?> lvt_7_1_ = (ConfiguredFeature)p_212245_5_.features.get(lvt_6_1_);
      return lvt_7_1_.place(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
   }
}
