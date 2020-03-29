package net.minecraftforge.fluids.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class ItemFluidContainer extends Item {
   protected final int capacity;

   public ItemFluidContainer(Item.Properties properties, int capacity) {
      super(properties);
      this.capacity = capacity;
   }

   public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundNBT nbt) {
      return new FluidHandlerItemStack(stack, this.capacity);
   }
}
