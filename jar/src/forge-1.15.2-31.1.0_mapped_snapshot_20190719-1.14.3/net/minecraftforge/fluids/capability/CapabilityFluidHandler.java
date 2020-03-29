package net.minecraftforge.fluids.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CapabilityFluidHandler {
   @CapabilityInject(IFluidHandler.class)
   public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = null;
   @CapabilityInject(IFluidHandlerItem.class)
   public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = null;

   public static void register() {
      CapabilityManager.INSTANCE.register(IFluidHandler.class, new CapabilityFluidHandler.DefaultFluidHandlerStorage(), () -> {
         return new FluidTank(1000);
      });
      CapabilityManager.INSTANCE.register(IFluidHandlerItem.class, new CapabilityFluidHandler.DefaultFluidHandlerStorage(), () -> {
         return new FluidHandlerItemStack(new ItemStack(Items.BUCKET), 1000);
      });
   }

   private static class DefaultFluidHandlerStorage<T extends IFluidHandler> implements Capability.IStorage<T> {
      private DefaultFluidHandlerStorage() {
      }

      public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
         if (!(instance instanceof FluidTank)) {
            throw new RuntimeException("Cannot serialize to an instance that isn't the default implementation");
         } else {
            CompoundNBT nbt = new CompoundNBT();
            FluidTank tank = (FluidTank)instance;
            FluidStack fluid = tank.getFluid();
            fluid.writeToNBT(nbt);
            nbt.putInt("Capacity", tank.getCapacity());
            return nbt;
         }
      }

      public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
         if (!(instance instanceof FluidTank)) {
            throw new RuntimeException("Cannot deserialize to an instance that isn't the default implementation");
         } else {
            CompoundNBT tags = (CompoundNBT)nbt;
            FluidTank tank = (FluidTank)instance;
            tank.setCapacity(tags.getInt("Capacity"));
            tank.readFromNBT(tags);
         }
      }

      // $FF: synthetic method
      DefaultFluidHandlerStorage(Object x0) {
         this();
      }
   }
}
