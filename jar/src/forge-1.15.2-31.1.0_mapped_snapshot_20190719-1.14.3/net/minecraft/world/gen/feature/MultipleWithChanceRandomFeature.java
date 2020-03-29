package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class MultipleWithChanceRandomFeature extends Feature<MultipleRandomFeatureConfig> {
   public MultipleWithChanceRandomFeature(Function<Dynamic<?>, ? extends MultipleRandomFeatureConfig> p_i51447_1_) {
      super(p_i51447_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, MultipleRandomFeatureConfig p_212245_5_) {
      Iterator var6 = p_212245_5_.features.iterator();

      ConfiguredRandomFeatureList lvt_7_1_;
      do {
         if (!var6.hasNext()) {
            return p_212245_5_.defaultFeature.place(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
         }

         lvt_7_1_ = (ConfiguredRandomFeatureList)var6.next();
      } while(p_212245_3_.nextFloat() >= lvt_7_1_.chance);

      return lvt_7_1_.place(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
   }
}
