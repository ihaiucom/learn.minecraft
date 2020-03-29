package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class HopperContainer extends Container {
   private final IInventory hopperInventory;

   public HopperContainer(int p_i50078_1_, PlayerInventory p_i50078_2_) {
      this(p_i50078_1_, p_i50078_2_, new Inventory(5));
   }

   public HopperContainer(int p_i50079_1_, PlayerInventory p_i50079_2_, IInventory p_i50079_3_) {
      super(ContainerType.HOPPER, p_i50079_1_);
      this.hopperInventory = p_i50079_3_;
      assertInventorySize(p_i50079_3_, 5);
      p_i50079_3_.openInventory(p_i50079_2_.player);
      int lvt_4_1_ = true;

      int lvt_5_3_;
      for(lvt_5_3_ = 0; lvt_5_3_ < 5; ++lvt_5_3_) {
         this.addSlot(new Slot(p_i50079_3_, lvt_5_3_, 44 + lvt_5_3_ * 18, 20));
      }

      for(lvt_5_3_ = 0; lvt_5_3_ < 3; ++lvt_5_3_) {
         for(int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_) {
            this.addSlot(new Slot(p_i50079_2_, lvt_6_1_ + lvt_5_3_ * 9 + 9, 8 + lvt_6_1_ * 18, lvt_5_3_ * 18 + 51));
         }
      }

      for(lvt_5_3_ = 0; lvt_5_3_ < 9; ++lvt_5_3_) {
         this.addSlot(new Slot(p_i50079_2_, lvt_5_3_, 8 + lvt_5_3_ * 18, 109));
      }

   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return this.hopperInventory.isUsableByPlayer(p_75145_1_);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ < this.hopperInventory.getSizeInventory()) {
            if (!this.mergeItemStack(lvt_5_1_, this.hopperInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(lvt_5_1_, 0, this.hopperInventory.getSizeInventory(), false)) {
            return ItemStack.EMPTY;
         }

         if (lvt_5_1_.isEmpty()) {
            lvt_4_1_.putStack(ItemStack.EMPTY);
         } else {
            lvt_4_1_.onSlotChanged();
         }
      }

      return lvt_3_1_;
   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.hopperInventory.closeInventory(p_75134_1_);
   }
}
