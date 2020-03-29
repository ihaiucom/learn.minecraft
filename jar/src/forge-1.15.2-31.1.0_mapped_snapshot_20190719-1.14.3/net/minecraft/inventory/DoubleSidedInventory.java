package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DoubleSidedInventory implements IInventory {
   private final IInventory field_70477_b;
   private final IInventory field_70478_c;

   public DoubleSidedInventory(IInventory p_i50399_1_, IInventory p_i50399_2_) {
      if (p_i50399_1_ == null) {
         p_i50399_1_ = p_i50399_2_;
      }

      if (p_i50399_2_ == null) {
         p_i50399_2_ = p_i50399_1_;
      }

      this.field_70477_b = p_i50399_1_;
      this.field_70478_c = p_i50399_2_;
   }

   public int getSizeInventory() {
      return this.field_70477_b.getSizeInventory() + this.field_70478_c.getSizeInventory();
   }

   public boolean isEmpty() {
      return this.field_70477_b.isEmpty() && this.field_70478_c.isEmpty();
   }

   public boolean isPartOfLargeChest(IInventory p_90010_1_) {
      return this.field_70477_b == p_90010_1_ || this.field_70478_c == p_90010_1_;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return p_70301_1_ >= this.field_70477_b.getSizeInventory() ? this.field_70478_c.getStackInSlot(p_70301_1_ - this.field_70477_b.getSizeInventory()) : this.field_70477_b.getStackInSlot(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      return p_70298_1_ >= this.field_70477_b.getSizeInventory() ? this.field_70478_c.decrStackSize(p_70298_1_ - this.field_70477_b.getSizeInventory(), p_70298_2_) : this.field_70477_b.decrStackSize(p_70298_1_, p_70298_2_);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return p_70304_1_ >= this.field_70477_b.getSizeInventory() ? this.field_70478_c.removeStackFromSlot(p_70304_1_ - this.field_70477_b.getSizeInventory()) : this.field_70477_b.removeStackFromSlot(p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      if (p_70299_1_ >= this.field_70477_b.getSizeInventory()) {
         this.field_70478_c.setInventorySlotContents(p_70299_1_ - this.field_70477_b.getSizeInventory(), p_70299_2_);
      } else {
         this.field_70477_b.setInventorySlotContents(p_70299_1_, p_70299_2_);
      }

   }

   public int getInventoryStackLimit() {
      return this.field_70477_b.getInventoryStackLimit();
   }

   public void markDirty() {
      this.field_70477_b.markDirty();
      this.field_70478_c.markDirty();
   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      return this.field_70477_b.isUsableByPlayer(p_70300_1_) && this.field_70478_c.isUsableByPlayer(p_70300_1_);
   }

   public void openInventory(PlayerEntity p_174889_1_) {
      this.field_70477_b.openInventory(p_174889_1_);
      this.field_70478_c.openInventory(p_174889_1_);
   }

   public void closeInventory(PlayerEntity p_174886_1_) {
      this.field_70477_b.closeInventory(p_174886_1_);
      this.field_70478_c.closeInventory(p_174886_1_);
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      return p_94041_1_ >= this.field_70477_b.getSizeInventory() ? this.field_70478_c.isItemValidForSlot(p_94041_1_ - this.field_70477_b.getSizeInventory(), p_94041_2_) : this.field_70477_b.isItemValidForSlot(p_94041_1_, p_94041_2_);
   }

   public void clear() {
      this.field_70477_b.clear();
      this.field_70478_c.clear();
   }
}
