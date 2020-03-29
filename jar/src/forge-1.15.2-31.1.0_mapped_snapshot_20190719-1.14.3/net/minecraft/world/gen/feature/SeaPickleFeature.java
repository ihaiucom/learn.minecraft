package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.CountConfig;

public class SeaPickleFeature extends Feature<CountConfig> {
   public SeaPickleFeature(Function<Dynamic<?>, ? extends CountConfig> p_i51442_1_) {
      super(p_i51442_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<?> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, CountConfig p_212245_5_) {
      int lvt_6_1_ = 0;

      for(int lvt_7_1_ = 0; lvt_7_1_ < p_212245_5_.count; ++lvt_7_1_) {
         int lvt_8_1_ = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int lvt_9_1_ = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int lvt_10_1_ = p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_212245_4_.getX() + lvt_8_1_, p_212245_4_.getZ() + lvt_9_1_);
         BlockPos lvt_11_1_ = new BlockPos(p_212245_4_.getX() + lvt_8_1_, lvt_10_1_, p_212245_4_.getZ() + lvt_9_1_);
         BlockState lvt_12_1_ = (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, p_212245_3_.nextInt(4) + 1);
         if (p_212245_1_.getBlockState(lvt_11_1_).getBlock() == Blocks.WATER && lvt_12_1_.isValidPosition(p_212245_1_, lvt_11_1_)) {
            p_212245_1_.setBlockState(lvt_11_1_, lvt_12_1_, 2);
            ++lvt_6_1_;
         }
      }

      return lvt_6_1_ > 0;
   }
}
