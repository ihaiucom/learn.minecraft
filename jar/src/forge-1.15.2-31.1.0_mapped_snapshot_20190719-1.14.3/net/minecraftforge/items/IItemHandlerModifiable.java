package net.minecraftforge.items;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface IItemHandlerModifiable extends IItemHandler {
   void setStackInSlot(int var1, @Nonnull ItemStack var2);
}
