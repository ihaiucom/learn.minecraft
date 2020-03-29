package net.minecraftforge.items;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotItemHandler extends Slot {
   private static IInventory emptyInventory = new Inventory(0);
   private final IItemHandler itemHandler;
   private final int index;

   public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
      super(emptyInventory, index, xPosition, yPosition);
      this.itemHandler = itemHandler;
      this.index = index;
   }

   public boolean isItemValid(@Nonnull ItemStack stack) {
      return stack.isEmpty() ? false : this.itemHandler.isItemValid(this.index, stack);
   }

   @Nonnull
   public ItemStack getStack() {
      return this.getItemHandler().getStackInSlot(this.index);
   }

   public void putStack(@Nonnull ItemStack stack) {
      ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(this.index, stack);
      this.onSlotChanged();
   }

   public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
   }

   public int getSlotStackLimit() {
      return this.itemHandler.getSlotLimit(this.index);
   }

   public int getItemStackLimit(@Nonnull ItemStack stack) {
      ItemStack maxAdd = stack.copy();
      int maxInput = stack.getMaxStackSize();
      maxAdd.setCount(maxInput);
      IItemHandler handler = this.getItemHandler();
      ItemStack currentStack = handler.getStackInSlot(this.index);
      if (handler instanceof IItemHandlerModifiable) {
         IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable)handler;
         handlerModifiable.setStackInSlot(this.index, ItemStack.EMPTY);
         ItemStack remainder = handlerModifiable.insertItem(this.index, maxAdd, true);
         handlerModifiable.setStackInSlot(this.index, currentStack);
         return maxInput - remainder.getCount();
      } else {
         ItemStack remainder = handler.insertItem(this.index, maxAdd, true);
         int current = currentStack.getCount();
         int added = maxInput - remainder.getCount();
         return current + added;
      }
   }

   public boolean canTakeStack(PlayerEntity playerIn) {
      return !this.getItemHandler().extractItem(this.index, 1, true).isEmpty();
   }

   @Nonnull
   public ItemStack decrStackSize(int amount) {
      return this.getItemHandler().extractItem(this.index, amount, false);
   }

   public IItemHandler getItemHandler() {
      return this.itemHandler;
   }
}
