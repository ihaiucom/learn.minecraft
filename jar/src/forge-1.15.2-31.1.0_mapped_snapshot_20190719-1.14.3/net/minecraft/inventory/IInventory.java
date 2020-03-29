package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IInventory extends IClearable {
   int getSizeInventory();

   boolean isEmpty();

   ItemStack getStackInSlot(int var1);

   ItemStack decrStackSize(int var1, int var2);

   ItemStack removeStackFromSlot(int var1);

   void setInventorySlotContents(int var1, ItemStack var2);

   default int getInventoryStackLimit() {
      return 64;
   }

   void markDirty();

   boolean isUsableByPlayer(PlayerEntity var1);

   default void openInventory(PlayerEntity p_174889_1_) {
   }

   default void closeInventory(PlayerEntity p_174886_1_) {
   }

   default boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return true;
   }

   default int count(Item p_213901_1_) {
      int lvt_2_1_ = 0;

      for(int lvt_3_1_ = 0; lvt_3_1_ < this.getSizeInventory(); ++lvt_3_1_) {
         ItemStack lvt_4_1_ = this.getStackInSlot(lvt_3_1_);
         if (lvt_4_1_.getItem().equals(p_213901_1_)) {
            lvt_2_1_ += lvt_4_1_.getCount();
         }
      }

      return lvt_2_1_;
   }

   default boolean hasAny(Set<Item> p_213902_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < this.getSizeInventory(); ++lvt_2_1_) {
         ItemStack lvt_3_1_ = this.getStackInSlot(lvt_2_1_);
         if (p_213902_1_.contains(lvt_3_1_.getItem()) && lvt_3_1_.getCount() > 0) {
            return true;
         }
      }

      return false;
   }
}
