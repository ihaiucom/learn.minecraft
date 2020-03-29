package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class RecipeWrapper implements IInventory {
   protected final IItemHandlerModifiable inv;

   public RecipeWrapper(IItemHandlerModifiable inv) {
      this.inv = inv;
   }

   public int getSizeInventory() {
      return this.inv.getSlots();
   }

   public ItemStack getStackInSlot(int slot) {
      return this.inv.getStackInSlot(slot);
   }

   public ItemStack decrStackSize(int slot, int count) {
      ItemStack stack = this.inv.getStackInSlot(slot);
      return stack.isEmpty() ? ItemStack.EMPTY : stack.split(count);
   }

   public void setInventorySlotContents(int slot, ItemStack stack) {
      this.inv.setStackInSlot(slot, stack);
   }

   public ItemStack removeStackFromSlot(int index) {
      ItemStack s = this.getStackInSlot(index);
      if (s.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.setInventorySlotContents(index, ItemStack.EMPTY);
         return s;
      }
   }

   public boolean isEmpty() {
      for(int i = 0; i < this.inv.getSlots(); ++i) {
         if (!this.inv.getStackInSlot(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public boolean isItemValidForSlot(int slot, ItemStack stack) {
      return this.inv.isItemValid(slot, stack);
   }

   public void clear() {
      for(int i = 0; i < this.inv.getSlots(); ++i) {
         this.inv.setStackInSlot(i, ItemStack.EMPTY);
      }

   }

   public int getInventoryStackLimit() {
      return 0;
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(PlayerEntity player) {
      return false;
   }

   public void openInventory(PlayerEntity player) {
   }

   public void closeInventory(PlayerEntity player) {
   }
}
