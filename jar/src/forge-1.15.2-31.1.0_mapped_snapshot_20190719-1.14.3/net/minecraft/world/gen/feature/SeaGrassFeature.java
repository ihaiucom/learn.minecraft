package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeaGrassBlock;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class SeaGrassFeature extends Feature<SeaGrassConfig> {
   public SeaGrassFeature(Function<Dynamic<?>, ? extends SeaGrassConfig> p_i51441_1_) {
      super(p_i51441_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, SeaGrassConfig p_212245_5_) {
      int lvt_6_1_ = 0;

      for(int lvt_7_1_ = 0; lvt_7_1_ < p_212245_5_.count; ++lvt_7_1_) {
         int lvt_8_1_ = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int lvt_9_1_ = p_212245_3_.nextInt(8) - p_212245_3_.nextInt(8);
         int lvt_10_1_ = p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_212245_4_.getX() + lvt_8_1_, p_212245_4_.getZ() + lvt_9_1_);
         BlockPos lvt_11_1_ = new BlockPos(p_212245_4_.getX() + lvt_8_1_, lvt_10_1_, p_212245_4_.getZ() + lvt_9_1_);
         if (p_212245_1_.getBlockState(lvt_11_1_).getBlock() == Blocks.WATER) {
            boolean lvt_12_1_ = p_212245_3_.nextDouble() < p_212245_5_.tallProbability;
            BlockState lvt_13_1_ = lvt_12_1_ ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();
            if (lvt_13_1_.isValidPosition(p_212245_1_, lvt_11_1_)) {
               if (lvt_12_1_) {
                  BlockState lvt_14_1_ = (BlockState)lvt_13_1_.with(TallSeaGrassBlock.field_208065_c, DoubleBlockHalf.UPPER);
                  BlockPos lvt_15_1_ = lvt_11_1_.up();
                  if (p_212245_1_.getBlockState(lvt_15_1_).getBlock() == Blocks.WATER) {
                     p_212245_1_.setBlockState(lvt_11_1_, lvt_13_1_, 2);
                     p_212245_1_.setBlockState(lvt_15_1_, lvt_14_1_, 2);
                  }
               } else {
                  p_212245_1_.setBlockState(lvt_11_1_, lvt_13_1_, 2);
               }

               ++lvt_6_1_;
            }
         }
      }

      return lvt_6_1_ > 0;
   }
}
