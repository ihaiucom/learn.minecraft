package net.minecraftforge.fluids.capability.templates;

import javax.annotation.Nonnull;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class VoidFluidHandler implements IFluidHandler {
   public static final VoidFluidHandler INSTANCE = new VoidFluidHandler();

   public int getTanks() {
      return 1;
   }

   @Nonnull
   public FluidStack getFluidInTank(int tank) {
      return FluidStack.EMPTY;
   }

   public int getTankCapacity(int tank) {
      return Integer.MAX_VALUE;
   }

   public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
      return true;
   }

   public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
      return resource.getAmount();
   }

   @Nonnull
   public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
      return FluidStack.EMPTY;
   }

   @Nonnull
   public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
      return FluidStack.EMPTY;
   }
}
