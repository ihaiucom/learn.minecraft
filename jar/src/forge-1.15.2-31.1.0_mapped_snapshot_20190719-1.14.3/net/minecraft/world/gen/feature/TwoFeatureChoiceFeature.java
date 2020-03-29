package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class TwoFeatureChoiceFeature extends Feature<TwoFeatureChoiceConfig> {
   public TwoFeatureChoiceFeature(Function<Dynamic<?>, ? extends TwoFeatureChoiceConfig> p_i51457_1_) {
      super(p_i51457_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, TwoFeatureChoiceConfig p_212245_5_) {
      boolean lvt_6_1_ = p_212245_3_.nextBoolean();
      return lvt_6_1_ ? p_212245_5_.field_227285_a_.place(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_) : p_212245_5_.field_227286_b_.place(p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
   }
}
