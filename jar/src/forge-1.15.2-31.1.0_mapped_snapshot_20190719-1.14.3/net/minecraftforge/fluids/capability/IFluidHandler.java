package net.minecraftforge.fluids.capability;

import javax.annotation.Nonnull;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidHandler {
   int getTanks();

   @Nonnull
   FluidStack getFluidInTank(int var1);

   int getTankCapacity(int var1);

   boolean isFluidValid(int var1, @Nonnull FluidStack var2);

   int fill(FluidStack var1, IFluidHandler.FluidAction var2);

   @Nonnull
   FluidStack drain(FluidStack var1, IFluidHandler.FluidAction var2);

   @Nonnull
   FluidStack drain(int var1, IFluidHandler.FluidAction var2);

   public static enum FluidAction {
      EXECUTE,
      SIMULATE;

      public boolean execute() {
         return this == EXECUTE;
      }

      public boolean simulate() {
         return this == SIMULATE;
      }
   }
}
