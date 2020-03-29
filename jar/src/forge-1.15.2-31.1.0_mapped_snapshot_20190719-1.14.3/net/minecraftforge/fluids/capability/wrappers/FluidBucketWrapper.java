package net.minecraftforge.fluids.capability.wrappers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidBucketWrapper implements IFluidHandlerItem, ICapabilityProvider {
   private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> {
      return this;
   });
   @Nonnull
   protected ItemStack container;

   public FluidBucketWrapper(@Nonnull ItemStack container) {
      this.container = container;
   }

   @Nonnull
   public ItemStack getContainer() {
      return this.container;
   }

   public boolean canFillFluidType(FluidStack fluid) {
      if (fluid.getFluid() != Fluids.WATER && fluid.getFluid() != Fluids.LAVA && !fluid.getFluid().getRegistryName().equals(new ResourceLocation("milk"))) {
         return fluid.getFluid().getAttributes().getBucket(fluid) != null;
      } else {
         return true;
      }
   }

   @Nonnull
   public FluidStack getFluid() {
      Item item = this.container.getItem();
      return item instanceof BucketItem ? new FluidStack(((BucketItem)item).getFluid(), 1000) : FluidStack.EMPTY;
   }

   protected void setFluid(@Nonnull FluidStack fluidStack) {
      if (fluidStack.isEmpty()) {
         this.container = new ItemStack(Items.BUCKET);
      } else {
         this.container = FluidUtil.getFilledBucket(fluidStack);
      }

   }

   public int getTanks() {
      return 1;
   }

   @Nonnull
   public FluidStack getFluidInTank(int tank) {
      return this.getFluid();
   }

   public int getTankCapacity(int tank) {
      return 1000;
   }

   public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
      return true;
   }

   public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
      if (this.container.getCount() == 1 && resource.getAmount() >= 1000 && !(this.container.getItem() instanceof MilkBucketItem) && this.getFluid().isEmpty() && this.canFillFluidType(resource)) {
         if (action.execute()) {
            this.setFluid(resource);
         }

         return 1000;
      } else {
         return 0;
      }
   }

   @Nonnull
   public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
      if (this.container.getCount() == 1 && resource.getAmount() >= 1000) {
         FluidStack fluidStack = this.getFluid();
         if (!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {
            if (action.execute()) {
               this.setFluid(FluidStack.EMPTY);
            }

            return fluidStack;
         } else {
            return FluidStack.EMPTY;
         }
      } else {
         return FluidStack.EMPTY;
      }
   }

   @Nonnull
   public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
      if (this.container.getCount() == 1 && maxDrain >= 1000) {
         FluidStack fluidStack = this.getFluid();
         if (!fluidStack.isEmpty()) {
            if (action.execute()) {
               this.setFluid(FluidStack.EMPTY);
            }

            return fluidStack;
         } else {
            return FluidStack.EMPTY;
         }
      } else {
         return FluidStack.EMPTY;
      }
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
      return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, this.holder);
   }
}
