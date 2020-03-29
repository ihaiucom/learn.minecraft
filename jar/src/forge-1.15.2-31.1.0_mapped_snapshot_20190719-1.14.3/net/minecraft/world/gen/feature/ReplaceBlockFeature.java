package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfig> {
   public ReplaceBlockFeature(Function<Dynamic<?>, ? extends ReplaceBlockConfig> p_i51444_1_) {
      super(p_i51444_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, ReplaceBlockConfig p_212245_5_) {
      if (p_212245_1_.getBlockState(p_212245_4_).getBlock() == p_212245_5_.target.getBlock()) {
         p_212245_1_.setBlockState(p_212245_4_, p_212245_5_.state, 2);
      }

      return true;
   }
}
