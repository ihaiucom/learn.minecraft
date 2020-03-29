package net.minecraftforge.items;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface IItemHandler {
   int getSlots();

   @Nonnull
   ItemStack getStackInSlot(int var1);

   @Nonnull
   ItemStack insertItem(int var1, @Nonnull ItemStack var2, boolean var3);

   @Nonnull
   ItemStack extractItem(int var1, int var2, boolean var3);

   int getSlotLimit(int var1);

   boolean isItemValid(int var1, @Nonnull ItemStack var2);
}
