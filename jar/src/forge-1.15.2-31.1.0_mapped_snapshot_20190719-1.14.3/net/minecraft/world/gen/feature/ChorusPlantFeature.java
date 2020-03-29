package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class ChorusPlantFeature extends Feature<NoFeatureConfig> {
   public ChorusPlantFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49902_1_) {
      super(p_i49902_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      if (p_212245_1_.isAirBlock(p_212245_4_.up()) && p_212245_1_.getBlockState(p_212245_4_).getBlock() == Blocks.END_STONE) {
         ChorusFlowerBlock.generatePlant(p_212245_1_, p_212245_4_.up(), p_212245_3_, 8);
         return true;
      } else {
         return false;
      }
   }
}
