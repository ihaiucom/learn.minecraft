package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ShulkerBoxContainer extends Container {
   private final IInventory inventory;

   public ShulkerBoxContainer(int p_i50065_1_, PlayerInventory p_i50065_2_) {
      this(p_i50065_1_, p_i50065_2_, new Inventory(27));
   }

   public ShulkerBoxContainer(int p_i50066_1_, PlayerInventory p_i50066_2_, IInventory p_i50066_3_) {
      super(ContainerType.SHULKER_BOX, p_i50066_1_);
      assertInventorySize(p_i50066_3_, 27);
      this.inventory = p_i50066_3_;
      p_i50066_3_.openInventory(p_i50066_2_.player);
      int lvt_4_1_ = true;
      int lvt_5_1_ = true;

      int lvt_6_3_;
      int lvt_7_2_;
      for(lvt_6_3_ = 0; lvt_6_3_ < 3; ++lvt_6_3_) {
         for(lvt_7_2_ = 0; lvt_7_2_ < 9; ++lvt_7_2_) {
            this.addSlot(new ShulkerBoxSlot(p_i50066_3_, lvt_7_2_ + lvt_6_3_ * 9, 8 + lvt_7_2_ * 18, 18 + lvt_6_3_ * 18));
         }
      }

      for(lvt_6_3_ = 0; lvt_6_3_ < 3; ++lvt_6_3_) {
         for(lvt_7_2_ = 0; lvt_7_2_ < 9; ++lvt_7_2_) {
            this.addSlot(new Slot(p_i50066_2_, lvt_7_2_ + lvt_6_3_ * 9 + 9, 8 + lvt_7_2_ * 18, 84 + lvt_6_3_ * 18));
         }
      }

      for(lvt_6_3_ = 0; lvt_6_3_ < 9; ++lvt_6_3_) {
         this.addSlot(new Slot(p_i50066_2_, lvt_6_3_, 8 + lvt_6_3_ * 18, 142));
      }

   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return this.inventory.isUsableByPlayer(p_75145_1_);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ < this.inventory.getSizeInventory()) {
            if (!this.mergeItemStack(lvt_5_1_, this.inventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(lvt_5_1_, 0, this.inventory.getSizeInventory(), false)) {
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
      this.inventory.closeInventory(p_75134_1_);
   }
}
