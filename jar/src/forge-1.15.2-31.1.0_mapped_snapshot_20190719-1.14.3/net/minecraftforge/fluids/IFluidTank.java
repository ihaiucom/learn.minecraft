package net.minecraftforge.fluids;

import javax.annotation.Nonnull;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidTank {
   @Nonnull
   FluidStack getFluid();

   int getFluidAmount();

   int getCapacity();

   boolean isFluidValid(FluidStack var1);

   int fill(FluidStack var1, IFluidHandler.FluidAction var2);

   @Nonnull
   FluidStack drain(int var1, IFluidHandler.FluidAction var2);

   @Nonnull
   FluidStack drain(FluidStack var1, IFluidHandler.FluidAction var2);
}
