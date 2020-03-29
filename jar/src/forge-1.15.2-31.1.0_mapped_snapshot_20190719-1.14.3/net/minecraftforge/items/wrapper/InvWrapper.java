package net.minecraftforge.items.wrapper;

import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class InvWrapper implements IItemHandlerModifiable {
   private final IInventory inv;

   public InvWrapper(IInventory inv) {
      this.inv = inv;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         InvWrapper that = (InvWrapper)o;
         return this.getInv().equals(that.getInv());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getInv().hashCode();
   }

   public int getSlots() {
      return this.getInv().getSizeInventory();
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return this.getInv().getStackInSlot(slot);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stackInSlot = this.getInv().getStackInSlot(slot);
         int m;
         if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), this.getSlotLimit(slot))) {
               return stack;
            } else if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
               return stack;
            } else if (!this.getInv().isItemValidForSlot(slot, stack)) {
               return stack;
            } else {
               m = Math.min(stack.getMaxStackSize(), this.getSlotLimit(slot)) - stackInSlot.getCount();
               ItemStack copy;
               if (stack.getCount() <= m) {
                  if (!simulate) {
                     copy = stack.copy();
                     copy.grow(stackInSlot.getCount());
                     this.getInv().setInventorySlotContents(slot, copy);
                     this.getInv().markDirty();
                  }

                  return ItemStack.EMPTY;
               } else {
                  stack = stack.copy();
                  if (!simulate) {
                     copy = stack.split(m);
                     copy.grow(stackInSlot.getCount());
                     this.getInv().setInventorySlotContents(slot, copy);
                     this.getInv().markDirty();
                     return stack;
                  } else {
                     stack.shrink(m);
                     return stack;
                  }
               }
            }
         } else if (!this.getInv().isItemValidForSlot(slot, stack)) {
            return stack;
         } else {
            m = Math.min(stack.getMaxStackSize(), this.getSlotLimit(slot));
            if (m < stack.getCount()) {
               stack = stack.copy();
               if (!simulate) {
                  this.getInv().setInventorySlotContents(slot, stack.split(m));
                  this.getInv().markDirty();
                  return stack;
               } else {
                  stack.shrink(m);
                  return stack;
               }
            } else {
               if (!simulate) {
                  this.getInv().setInventorySlotContents(slot, stack);
                  this.getInv().markDirty();
               }

               return ItemStack.EMPTY;
            }
         }
      }
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (amount == 0) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stackInSlot = this.getInv().getStackInSlot(slot);
         if (stackInSlot.isEmpty()) {
            return ItemStack.EMPTY;
         } else if (simulate) {
            if (stackInSlot.getCount() < amount) {
               return stackInSlot.copy();
            } else {
               ItemStack copy = stackInSlot.copy();
               copy.setCount(amount);
               return copy;
            }
         } else {
            int m = Math.min(stackInSlot.getCount(), amount);
            ItemStack decrStackSize = this.getInv().decrStackSize(slot, m);
            this.getInv().markDirty();
            return decrStackSize;
         }
      }
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.getInv().setInventorySlotContents(slot, stack);
   }

   public int getSlotLimit(int slot) {
      return this.getInv().getInventoryStackLimit();
   }

   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return this.getInv().isItemValidForSlot(slot, stack);
   }

   public IInventory getInv() {
      return this.inv;
   }
}
