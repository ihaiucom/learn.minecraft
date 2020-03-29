package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class BlockWithContextFeature extends Feature<BlockWithContextConfig> {
   public BlockWithContextFeature(Function<Dynamic<?>, ? extends BlockWithContextConfig> p_i51438_1_) {
      super(p_i51438_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockWithContextConfig p_212245_5_) {
      if (p_212245_5_.placeOn.contains(p_212245_1_.getBlockState(p_212245_4_.down())) && p_212245_5_.placeIn.contains(p_212245_1_.getBlockState(p_212245_4_)) && p_212245_5_.placeUnder.contains(p_212245_1_.getBlockState(p_212245_4_.up()))) {
         p_212245_1_.setBlockState(p_212245_4_, p_212245_5_.toPlace, 2);
         return true;
      } else {
         return false;
      }
   }
}
