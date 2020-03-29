package net.minecraftforge.fluids;

import javax.annotation.Nonnull;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidBlock {
   Fluid getFluid();

   int place(World var1, BlockPos var2, @Nonnull FluidStack var3, IFluidHandler.FluidAction var4);

   @Nonnull
   FluidStack drain(World var1, BlockPos var2, IFluidHandler.FluidAction var3);

   boolean canDrain(World var1, BlockPos var2);

   float getFilledPercentage(World var1, BlockPos var2);
}
