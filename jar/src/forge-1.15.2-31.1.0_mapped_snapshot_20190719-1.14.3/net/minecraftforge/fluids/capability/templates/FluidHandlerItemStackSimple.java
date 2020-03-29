package net.minecraftforge.fluids.capability.templates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidHandlerItemStackSimple implements IFluidHandlerItem, ICapabilityProvider {
   public static final String FLUID_NBT_KEY = "Fluid";
   private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> {
      return this;
   });
   @Nonnull
   protected ItemStack container;
   protected int capacity;

   public FluidHandlerItemStackSimple(@Nonnull ItemStack container, int capacity) {
      this.container = container;
      this.capacity = capacity;
   }

   @Nonnull
   public ItemStack getContainer() {
      return this.container;
   }

   @Nonnull
   public FluidStack getFluid() {
      CompoundNBT tagCompound = this.container.getTag();
      return tagCompound != null && tagCompound.contains("Fluid") ? FluidStack.loadFluidStackFromNBT(tagCompound.getCompound("Fluid")) : FluidStack.EMPTY;
   }

   protected void setFluid(FluidStack fluid) {
      if (!this.container.hasTag()) {
         this.container.setTag(new CompoundNBT());
      }

      CompoundNBT fluidTag = new CompoundNBT();
      fluid.writeToNBT(fluidTag);
      this.container.getTag().put("Fluid", fluidTag);
   }

   public int getTanks() {
      return 1;
   }

   @Nonnull
   public FluidStack getFluidInTank(int tank) {
      return this.getFluid();
   }

   public int getTankCapacity(int tank) {
      return this.capacity;
   }

   public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
      return true;
   }

   public int fill(@Nonnull FluidStack resource, IFluidHandler.FluidAction action) {
      if (this.container.getCount() == 1 && !resource.isEmpty() && this.canFillFluidType(resource)) {
         FluidStack contained = this.getFluid();
         if (contained.isEmpty()) {
            int fillAmount = Math.min(this.capacity, resource.getAmount());
            if (fillAmount == this.capacity) {
               if (action.execute()) {
                  FluidStack filled = resource.copy();
                  filled.setAmount(fillAmount);
                  this.setFluid(filled);
               }

               return fillAmount;
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   @Nonnull
   public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
      return this.container.getCount() == 1 && !resource.isEmpty() && resource.isFluidEqual(this.getFluid()) ? this.drain(resource.getAmount(), action) : FluidStack.EMPTY;
   }

   @Nonnull
   public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
      if (this.container.getCount() == 1 && maxDrain > 0) {
         FluidStack contained = this.getFluid();
         if (!contained.isEmpty() && this.canDrainFluidType(contained)) {
            int drainAmount = Math.min(contained.getAmount(), maxDrain);
            if (drainAmount == this.capacity) {
               FluidStack drained = contained.copy();
               if (action.execute()) {
                  this.setContainerToEmpty();
               }

               return drained;
            } else {
               return FluidStack.EMPTY;
            }
         } else {
            return FluidStack.EMPTY;
         }
      } else {
         return FluidStack.EMPTY;
      }
   }

   public boolean canFillFluidType(FluidStack fluid) {
      return true;
   }

   public boolean canDrainFluidType(FluidStack fluid) {
      return true;
   }

   protected void setContainerToEmpty() {
      this.container.removeChildTag("Fluid");
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
      return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, this.holder);
   }

   public static class SwapEmpty extends FluidHandlerItemStackSimple {
      protected final ItemStack emptyContainer;

      public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity) {
         super(container, capacity);
         this.emptyContainer = emptyContainer;
      }

      protected void setContainerToEmpty() {
         super.setContainerToEmpty();
         this.container = this.emptyContainer;
      }
   }

   public static class Consumable extends FluidHandlerItemStackSimple {
      public Consumable(ItemStack container, int capacity) {
         super(container, capacity);
      }

      protected void setContainerToEmpty() {
         super.setContainerToEmpty();
         this.container.shrink(1);
      }
   }
}
