package net.minecraftforge.fluids.capability.templates;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidTank implements IFluidHandler, IFluidTank {
   protected Predicate<FluidStack> validator;
   @Nonnull
   protected FluidStack fluid;
   protected int capacity;

   public FluidTank(int capacity) {
      this(capacity, (e) -> {
         return true;
      });
   }

   public FluidTank(int capacity, Predicate<FluidStack> validator) {
      this.fluid = FluidStack.EMPTY;
      this.capacity = capacity;
      this.validator = validator;
   }

   public FluidTank setCapacity(int capacity) {
      this.capacity = capacity;
      return this;
   }

   public FluidTank setValidator(Predicate<FluidStack> validator) {
      if (validator != null) {
         this.validator = validator;
      }

      return this;
   }

   public boolean isFluidValid(FluidStack stack) {
      return this.validator.test(stack);
   }

   public int getCapacity() {
      return this.capacity;
   }

   @Nonnull
   public FluidStack getFluid() {
      return this.fluid;
   }

   public int getFluidAmount() {
      return this.fluid.getAmount();
   }

   public FluidTank readFromNBT(CompoundNBT nbt) {
      FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
      this.setFluid(fluid);
      return this;
   }

   public CompoundNBT writeToNBT(CompoundNBT nbt) {
      this.fluid.writeToNBT(nbt);
      return nbt;
   }

   public int getTanks() {
      return 1;
   }

   @Nonnull
   public FluidStack getFluidInTank(int tank) {
      return this.getFluid();
   }

   public int getTankCapacity(int tank) {
      return this.getCapacity();
   }

   public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
      return this.isFluidValid(stack);
   }

   public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
      if (!resource.isEmpty() && this.isFluidValid(resource)) {
         if (action.simulate()) {
            if (this.fluid.isEmpty()) {
               return Math.min(this.capacity, resource.getAmount());
            } else {
               return !this.fluid.isFluidEqual(resource) ? 0 : Math.min(this.capacity - this.fluid.getAmount(), resource.getAmount());
            }
         } else if (this.fluid.isEmpty()) {
            this.fluid = new FluidStack(resource, Math.min(this.capacity, resource.getAmount()));
            this.onContentsChanged();
            return this.fluid.getAmount();
         } else if (!this.fluid.isFluidEqual(resource)) {
            return 0;
         } else {
            int filled = this.capacity - this.fluid.getAmount();
            if (resource.getAmount() < filled) {
               this.fluid.grow(resource.getAmount());
               filled = resource.getAmount();
            } else {
               this.fluid.setAmount(this.capacity);
            }

            if (filled > 0) {
               this.onContentsChanged();
            }

            return filled;
         }
      } else {
         return 0;
      }
   }

   @Nonnull
   public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
      return !resource.isEmpty() && resource.isFluidEqual(this.fluid) ? this.drain(resource.getAmount(), action) : FluidStack.EMPTY;
   }

   @Nonnull
   public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
      int drained = maxDrain;
      if (this.fluid.getAmount() < maxDrain) {
         drained = this.fluid.getAmount();
      }

      FluidStack stack = new FluidStack(this.fluid, drained);
      if (action.execute() && drained > 0) {
         this.fluid.shrink(drained);
      }

      if (drained > 0) {
         this.onContentsChanged();
      }

      return stack;
   }

   protected void onContentsChanged() {
   }

   public void setFluid(FluidStack stack) {
      this.fluid = stack;
   }

   public boolean isEmpty() {
      return this.fluid.isEmpty();
   }

   public int getSpace() {
      return Math.max(0, this.capacity - this.fluid.getAmount());
   }
}
