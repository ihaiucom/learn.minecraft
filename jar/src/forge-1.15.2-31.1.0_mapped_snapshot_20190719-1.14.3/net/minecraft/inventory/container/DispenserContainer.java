package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class DispenserContainer extends Container {
   private final IInventory dispenserInventory;

   public DispenserContainer(int p_i50087_1_, PlayerInventory p_i50087_2_) {
      this(p_i50087_1_, p_i50087_2_, new Inventory(9));
   }

   public DispenserContainer(int p_i50088_1_, PlayerInventory p_i50088_2_, IInventory p_i50088_3_) {
      super(ContainerType.GENERIC_3X3, p_i50088_1_);
      assertInventorySize(p_i50088_3_, 9);
      this.dispenserInventory = p_i50088_3_;
      p_i50088_3_.openInventory(p_i50088_2_.player);

      int lvt_4_3_;
      int lvt_5_2_;
      for(lvt_4_3_ = 0; lvt_4_3_ < 3; ++lvt_4_3_) {
         for(lvt_5_2_ = 0; lvt_5_2_ < 3; ++lvt_5_2_) {
            this.addSlot(new Slot(p_i50088_3_, lvt_5_2_ + lvt_4_3_ * 3, 62 + lvt_5_2_ * 18, 17 + lvt_4_3_ * 18));
         }
      }

      for(lvt_4_3_ = 0; lvt_4_3_ < 3; ++lvt_4_3_) {
         for(lvt_5_2_ = 0; lvt_5_2_ < 9; ++lvt_5_2_) {
            this.addSlot(new Slot(p_i50088_2_, lvt_5_2_ + lvt_4_3_ * 9 + 9, 8 + lvt_5_2_ * 18, 84 + lvt_4_3_ * 18));
         }
      }

      for(lvt_4_3_ = 0; lvt_4_3_ < 9; ++lvt_4_3_) {
         this.addSlot(new Slot(p_i50088_2_, lvt_4_3_, 8 + lvt_4_3_ * 18, 142));
      }

   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return this.dispenserInventory.isUsableByPlayer(p_75145_1_);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ < 9) {
            if (!this.mergeItemStack(lvt_5_1_, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(lvt_5_1_, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (lvt_5_1_.isEmpty()) {
            lvt_4_1_.putStack(ItemStack.EMPTY);
         } else {
            lvt_4_1_.onSlotChanged();
         }

         if (lvt_5_1_.getCount() == lvt_3_1_.getCount()) {
            return ItemStack.EMPTY;
         }

         lvt_4_1_.onTake(p_82846_1_, lvt_5_1_);
      }

      return lvt_3_1_;
   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.dispenserInventory.closeInventory(p_75134_1_);
   }
}
