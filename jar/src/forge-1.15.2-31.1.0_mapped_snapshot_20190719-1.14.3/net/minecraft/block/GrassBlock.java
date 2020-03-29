package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.server.ServerWorld;

public class GrassBlock extends SpreadableSnowyDirtBlock implements IGrowable {
   public GrassBlock(Block.Properties p_i48388_1_) {
      super(p_i48388_1_);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return p_176473_1_.getBlockState(p_176473_2_.up()).isAir();
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      BlockPos lvt_5_1_ = p_225535_3_.up();
      BlockState lvt_6_1_ = Blocks.GRASS.getDefaultState();

      label48:
      for(int lvt_7_1_ = 0; lvt_7_1_ < 128; ++lvt_7_1_) {
         BlockPos lvt_8_1_ = lvt_5_1_;

         for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_7_1_ / 16; ++lvt_9_1_) {
            lvt_8_1_ = lvt_8_1_.add(p_225535_2_.nextInt(3) - 1, (p_225535_2_.nextInt(3) - 1) * p_225535_2_.nextInt(3) / 2, p_225535_2_.nextInt(3) - 1);
            if (p_225535_1_.getBlockState(lvt_8_1_.down()).getBlock() != this || p_225535_1_.getBlockState(lvt_8_1_).func_224756_o(p_225535_1_, lvt_8_1_)) {
               continue label48;
            }
         }

         BlockState lvt_9_2_ = p_225535_1_.getBlockState(lvt_8_1_);
         if (lvt_9_2_.getBlock() == lvt_6_1_.getBlock() && p_225535_2_.nextInt(10) == 0) {
            ((IGrowable)lvt_6_1_.getBlock()).func_225535_a_(p_225535_1_, p_225535_2_, lvt_8_1_, lvt_9_2_);
         }

         if (lvt_9_2_.isAir()) {
            BlockState lvt_10_2_;
            if (p_225535_2_.nextInt(8) == 0) {
               List<ConfiguredFeature<?, ?>> lvt_11_1_ = p_225535_1_.func_226691_t_(lvt_8_1_).getFlowers();
               if (lvt_11_1_.isEmpty()) {
                  continue;
               }

               ConfiguredFeature<?, ?> lvt_12_1_ = ((DecoratedFeatureConfig)((ConfiguredFeature)lvt_11_1_.get(0)).config).feature;
               lvt_10_2_ = ((FlowersFeature)lvt_12_1_.feature).func_225562_b_(p_225535_2_, lvt_8_1_, lvt_12_1_.config);
            } else {
               lvt_10_2_ = lvt_6_1_;
            }

            if (lvt_10_2_.isValidPosition(p_225535_1_, lvt_8_1_)) {
               p_225535_1_.setBlockState(lvt_8_1_, lvt_10_2_, 3);
            }
         }
      }

   }
}
