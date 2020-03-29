package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public interface ISidedInventory extends IInventory {
   int[] getSlotsForFace(Direction var1);

   boolean canInsertItem(int var1, ItemStack var2, @Nullable Direction var3);

   boolean canExtractItem(int var1, ItemStack var2, Direction var3);
}
