package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class DecoratedFeature extends Feature<DecoratedFeatureConfig> {
   public DecoratedFeature(Function<Dynamic<?>, ? extends DecoratedFeatureConfig> p_i49893_1_) {
      super(p_i49893_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, DecoratedFeatureConfig p_212245_5_) {
      return p_212245_5_.decorator.func_215093_a(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_, p_212245_5_.feature);
   }

   public String toString() {
      return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this));
   }
}
