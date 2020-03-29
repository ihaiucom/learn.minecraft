package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public interface ILiquidContainer {
   boolean canContainFluid(IBlockReader var1, BlockPos var2, BlockState var3, Fluid var4);

   boolean receiveFluid(IWorld var1, BlockPos var2, BlockState var3, IFluidState var4);
}
