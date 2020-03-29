package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChestContainer extends Container {
   private final IInventory lowerChestInventory;
   private final int numRows;

   private ChestContainer(ContainerType<?> p_i50091_1_, int p_i50091_2_, PlayerInventory p_i50091_3_, int p_i50091_4_) {
      this(p_i50091_1_, p_i50091_2_, p_i50091_3_, new Inventory(9 * p_i50091_4_), p_i50091_4_);
   }

   public static ChestContainer createGeneric9X1(int p_216986_0_, PlayerInventory p_216986_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X1, p_216986_0_, p_216986_1_, 1);
   }

   public static ChestContainer createGeneric9X2(int p_216987_0_, PlayerInventory p_216987_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X2, p_216987_0_, p_216987_1_, 2);
   }

   public static ChestContainer createGeneric9X3(int p_216988_0_, PlayerInventory p_216988_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X3, p_216988_0_, p_216988_1_, 3);
   }

   public static ChestContainer createGeneric9X4(int p_216991_0_, PlayerInventory p_216991_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X4, p_216991_0_, p_216991_1_, 4);
   }

   public static ChestContainer createGeneric9X5(int p_216989_0_, PlayerInventory p_216989_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X5, p_216989_0_, p_216989_1_, 5);
   }

   public static ChestContainer createGeneric9X6(int p_216990_0_, PlayerInventory p_216990_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X6, p_216990_0_, p_216990_1_, 6);
   }

   public static ChestContainer createGeneric9X3(int p_216992_0_, PlayerInventory p_216992_1_, IInventory p_216992_2_) {
      return new ChestContainer(ContainerType.GENERIC_9X3, p_216992_0_, p_216992_1_, p_216992_2_, 3);
   }

   public static ChestContainer createGeneric9X6(int p_216984_0_, PlayerInventory p_216984_1_, IInventory p_216984_2_) {
      return new ChestContainer(ContainerType.GENERIC_9X6, p_216984_0_, p_216984_1_, p_216984_2_, 6);
   }

   public ChestContainer(ContainerType<?> p_i50092_1_, int p_i50092_2_, PlayerInventory p_i50092_3_, IInventory p_i50092_4_, int p_i50092_5_) {
      super(p_i50092_1_, p_i50092_2_);
      assertInventorySize(p_i50092_4_, p_i50092_5_ * 9);
      this.lowerChestInventory = p_i50092_4_;
      this.numRows = p_i50092_5_;
      p_i50092_4_.openInventory(p_i50092_3_.player);
      int lvt_6_1_ = (this.numRows - 4) * 18;

      int lvt_7_3_;
      int lvt_8_2_;
      for(lvt_7_3_ = 0; lvt_7_3_ < this.numRows; ++lvt_7_3_) {
         for(lvt_8_2_ = 0; lvt_8_2_ < 9; ++lvt_8_2_) {
            this.addSlot(new Slot(p_i50092_4_, lvt_8_2_ + lvt_7_3_ * 9, 8 + lvt_8_2_ * 18, 18 + lvt_7_3_ * 18));
         }
      }

      for(lvt_7_3_ = 0; lvt_7_3_ < 3; ++lvt_7_3_) {
         for(lvt_8_2_ = 0; lvt_8_2_ < 9; ++lvt_8_2_) {
            this.addSlot(new Slot(p_i50092_3_, lvt_8_2_ + lvt_7_3_ * 9 + 9, 8 + lvt_8_2_ * 18, 103 + lvt_7_3_ * 18 + lvt_6_1_));
         }
      }

      for(lvt_7_3_ = 0; lvt_7_3_ < 9; ++lvt_7_3_) {
         this.addSlot(new Slot(p_i50092_3_, lvt_7_3_, 8 + lvt_7_3_ * 18, 161 + lvt_6_1_));
      }

   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return this.lowerChestInventory.isUsableByPlayer(p_75145_1_);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ < this.numRows * 9) {
            if (!this.mergeItemStack(lvt_5_1_, this.numRows * 9, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(lvt_5_1_, 0, this.numRows * 9, false)) {
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
      this.lowerChestInventory.closeInventory(p_75134_1_);
   }

   public IInventory getLowerChestInventory() {
      return this.lowerChestInventory;
   }

   @OnlyIn(Dist.CLIENT)
   public int getNumRows() {
      return this.numRows;
   }
}
