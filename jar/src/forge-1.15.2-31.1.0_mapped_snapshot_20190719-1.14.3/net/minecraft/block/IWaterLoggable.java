package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public interface IWaterLoggable extends IBucketPickupHandler, ILiquidContainer {
   default boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
      return !(Boolean)p_204510_3_.get(BlockStateProperties.WATERLOGGED) && p_204510_4_ == Fluids.WATER;
   }

   default boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, IFluidState p_204509_4_) {
      if (!(Boolean)p_204509_3_.get(BlockStateProperties.WATERLOGGED) && p_204509_4_.getFluid() == Fluids.WATER) {
         if (!p_204509_1_.isRemote()) {
            p_204509_1_.setBlockState(p_204509_2_, (BlockState)p_204509_3_.with(BlockStateProperties.WATERLOGGED, true), 3);
            p_204509_1_.getPendingFluidTicks().scheduleTick(p_204509_2_, p_204509_4_.getFluid(), p_204509_4_.getFluid().getTickRate(p_204509_1_));
         }

         return true;
      } else {
         return false;
      }
   }

   default Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, BlockState p_204508_3_) {
      if ((Boolean)p_204508_3_.get(BlockStateProperties.WATERLOGGED)) {
         p_204508_1_.setBlockState(p_204508_2_, (BlockState)p_204508_3_.with(BlockStateProperties.WATERLOGGED, false), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }
}
