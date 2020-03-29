package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.EnderChestTileEntity;

public class EnderChestInventory extends Inventory {
   private EnderChestTileEntity associatedChest;

   public EnderChestInventory() {
      super(27);
   }

   public void setChestTileEntity(EnderChestTileEntity p_146031_1_) {
      this.associatedChest = p_146031_1_;
   }

   public void read(ListNBT p_70486_1_) {
      int lvt_2_2_;
      for(lvt_2_2_ = 0; lvt_2_2_ < this.getSizeInventory(); ++lvt_2_2_) {
         this.setInventorySlotContents(lvt_2_2_, ItemStack.EMPTY);
      }

      for(lvt_2_2_ = 0; lvt_2_2_ < p_70486_1_.size(); ++lvt_2_2_) {
         CompoundNBT lvt_3_1_ = p_70486_1_.getCompound(lvt_2_2_);
         int lvt_4_1_ = lvt_3_1_.getByte("Slot") & 255;
         if (lvt_4_1_ >= 0 && lvt_4_1_ < this.getSizeInventory()) {
            this.setInventorySlotContents(lvt_4_1_, ItemStack.read(lvt_3_1_));
         }
      }

   }

   public ListNBT write() {
      ListNBT lvt_1_1_ = new ListNBT();

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.getSizeInventory(); ++lvt_2_1_) {
         ItemStack lvt_3_1_ = this.getStackInSlot(lvt_2_1_);
         if (!lvt_3_1_.isEmpty()) {
            CompoundNBT lvt_4_1_ = new CompoundNBT();
            lvt_4_1_.putByte("Slot", (byte)lvt_2_1_);
            lvt_3_1_.write(lvt_4_1_);
            lvt_1_1_.add(lvt_4_1_);
         }
      }

      return lvt_1_1_;
   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      return this.associatedChest != null && !this.associatedChest.canBeUsed(p_70300_1_) ? false : super.isUsableByPlayer(p_70300_1_);
   }

   public void openInventory(PlayerEntity p_174889_1_) {
      if (this.associatedChest != null) {
         this.associatedChest.openChest();
      }

      super.openInventory(p_174889_1_);
   }

   public void closeInventory(PlayerEntity p_174886_1_) {
      if (this.associatedChest != null) {
         this.associatedChest.closeChest();
      }

      super.closeInventory(p_174886_1_);
      this.associatedChest = null;
   }
}
