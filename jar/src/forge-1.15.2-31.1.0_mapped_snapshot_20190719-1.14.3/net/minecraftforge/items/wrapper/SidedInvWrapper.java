package net.minecraftforge.items.wrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class SidedInvWrapper implements IItemHandlerModifiable {
   protected final ISidedInventory inv;
   @Nullable
   protected final Direction side;

   public static LazyOptional<IItemHandlerModifiable>[] create(ISidedInventory inv, Direction... sides) {
      LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[sides.length];

      for(int x = 0; x < sides.length; ++x) {
         Direction side = sides[x];
         ret[x] = LazyOptional.of(() -> {
            return new SidedInvWrapper(inv, side);
         });
      }

      return ret;
   }

   public SidedInvWrapper(ISidedInventory inv, @Nullable Direction side) {
      this.inv = inv;
      this.side = side;
   }

   public static int getSlot(ISidedInventory inv, int slot, @Nullable Direction side) {
      int[] slots = inv.getSlotsForFace(side);
      return slot < slots.length ? slots[slot] : -1;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SidedInvWrapper that = (SidedInvWrapper)o;
         return this.inv.equals(that.inv) && this.side == that.side;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.inv.hashCode();
      result = 31 * result + (this.side == null ? 0 : this.side.hashCode());
      return result;
   }

   public int getSlots() {
      return this.inv.getSlotsForFace(this.side).length;
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      int i = getSlot(this.inv, slot, this.side);
      return i == -1 ? ItemStack.EMPTY : this.inv.getStackInSlot(i);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         int slot1 = getSlot(this.inv, slot, this.side);
         if (slot1 == -1) {
            return stack;
         } else {
            ItemStack stackInSlot = this.inv.getStackInSlot(slot1);
            int m;
            if (!stackInSlot.isEmpty()) {
               if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), this.getSlotLimit(slot))) {
                  return stack;
               } else if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                  return stack;
               } else if (this.inv.canInsertItem(slot1, stack, this.side) && this.inv.isItemValidForSlot(slot1, stack)) {
                  m = Math.min(stack.getMaxStackSize(), this.getSlotLimit(slot)) - stackInSlot.getCount();
                  ItemStack copy;
                  if (stack.getCount() <= m) {
                     if (!simulate) {
                        copy = stack.copy();
                        copy.grow(stackInSlot.getCount());
                        this.setInventorySlotContents(slot1, copy);
                     }

                     return ItemStack.EMPTY;
                  } else {
                     stack = stack.copy();
                     if (!simulate) {
                        copy = stack.split(m);
                        copy.grow(stackInSlot.getCount());
                        this.setInventorySlotContents(slot1, copy);
                        return stack;
                     } else {
                        stack.shrink(m);
                        return stack;
                     }
                  }
               } else {
                  return stack;
               }
            } else if (this.inv.canInsertItem(slot1, stack, this.side) && this.inv.isItemValidForSlot(slot1, stack)) {
               m = Math.min(stack.getMaxStackSize(), this.getSlotLimit(slot));
               if (m < stack.getCount()) {
                  stack = stack.copy();
                  if (!simulate) {
                     this.setInventorySlotContents(slot1, stack.split(m));
                     return stack;
                  } else {
                     stack.shrink(m);
                     return stack;
                  }
               } else {
                  if (!simulate) {
                     this.setInventorySlotContents(slot1, stack);
                  }

                  return ItemStack.EMPTY;
               }
            } else {
               return stack;
            }
         }
      }
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      int slot1 = getSlot(this.inv, slot, this.side);
      if (slot1 != -1) {
         this.setInventorySlotContents(slot1, stack);
      }

   }

   private void setInventorySlotContents(int slot, ItemStack stack) {
      this.inv.markDirty();
      this.inv.setInventorySlotContents(slot, stack);
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (amount == 0) {
         return ItemStack.EMPTY;
      } else {
         int slot1 = getSlot(this.inv, slot, this.side);
         if (slot1 == -1) {
            return ItemStack.EMPTY;
         } else {
            ItemStack stackInSlot = this.inv.getStackInSlot(slot1);
            if (stackInSlot.isEmpty()) {
               return ItemStack.EMPTY;
            } else if (!this.inv.canExtractItem(slot1, stackInSlot, this.side)) {
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
               ItemStack ret = this.inv.decrStackSize(slot1, m);
               this.inv.markDirty();
               return ret;
            }
         }
      }
   }

   public int getSlotLimit(int slot) {
      return this.inv.getInventoryStackLimit();
   }

   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      int slot1 = getSlot(this.inv, slot, this.side);
      return slot1 == -1 ? false : this.inv.isItemValidForSlot(slot1, stack);
   }
}
