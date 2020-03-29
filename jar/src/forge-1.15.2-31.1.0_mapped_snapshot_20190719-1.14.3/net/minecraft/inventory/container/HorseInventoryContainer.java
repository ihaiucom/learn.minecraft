package net.minecraft.inventory.container;

import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HorseInventoryContainer extends Container {
   private final IInventory horseInventory;
   private final AbstractHorseEntity horse;

   public HorseInventoryContainer(int p_i50077_1_, PlayerInventory p_i50077_2_, IInventory p_i50077_3_, final AbstractHorseEntity p_i50077_4_) {
      super((ContainerType)null, p_i50077_1_);
      this.horseInventory = p_i50077_3_;
      this.horse = p_i50077_4_;
      int lvt_5_1_ = true;
      p_i50077_3_.openInventory(p_i50077_2_.player);
      int lvt_6_1_ = true;
      this.addSlot(new Slot(p_i50077_3_, 0, 8, 18) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return p_75214_1_.getItem() == Items.SADDLE && !this.getHasStack() && p_i50077_4_.canBeSaddled();
         }

         @OnlyIn(Dist.CLIENT)
         public boolean isEnabled() {
            return p_i50077_4_.canBeSaddled();
         }
      });
      this.addSlot(new Slot(p_i50077_3_, 1, 8, 36) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return p_i50077_4_.isArmor(p_75214_1_);
         }

         @OnlyIn(Dist.CLIENT)
         public boolean isEnabled() {
            return p_i50077_4_.wearsArmor();
         }

         public int getSlotStackLimit() {
            return 1;
         }
      });
      int lvt_7_3_;
      int lvt_8_2_;
      if (p_i50077_4_ instanceof AbstractChestedHorseEntity && ((AbstractChestedHorseEntity)p_i50077_4_).hasChest()) {
         for(lvt_7_3_ = 0; lvt_7_3_ < 3; ++lvt_7_3_) {
            for(lvt_8_2_ = 0; lvt_8_2_ < ((AbstractChestedHorseEntity)p_i50077_4_).getInventoryColumns(); ++lvt_8_2_) {
               this.addSlot(new Slot(p_i50077_3_, 2 + lvt_8_2_ + lvt_7_3_ * ((AbstractChestedHorseEntity)p_i50077_4_).getInventoryColumns(), 80 + lvt_8_2_ * 18, 18 + lvt_7_3_ * 18));
            }
         }
      }

      for(lvt_7_3_ = 0; lvt_7_3_ < 3; ++lvt_7_3_) {
         for(lvt_8_2_ = 0; lvt_8_2_ < 9; ++lvt_8_2_) {
            this.addSlot(new Slot(p_i50077_2_, lvt_8_2_ + lvt_7_3_ * 9 + 9, 8 + lvt_8_2_ * 18, 102 + lvt_7_3_ * 18 + -18));
         }
      }

      for(lvt_7_3_ = 0; lvt_7_3_ < 9; ++lvt_7_3_) {
         this.addSlot(new Slot(p_i50077_2_, lvt_7_3_, 8 + lvt_7_3_ * 18, 142));
      }

   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return this.horseInventory.isUsableByPlayer(p_75145_1_) && this.horse.isAlive() && this.horse.getDistance(p_75145_1_) < 8.0F;
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         lvt_3_1_ = lvt_5_1_.copy();
         int lvt_6_1_ = this.horseInventory.getSizeInventory();
         if (p_82846_2_ < lvt_6_1_) {
            if (!this.mergeItemStack(lvt_5_1_, lvt_6_1_, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(1).isItemValid(lvt_5_1_) && !this.getSlot(1).getHasStack()) {
            if (!this.mergeItemStack(lvt_5_1_, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.getSlot(0).isItemValid(lvt_5_1_)) {
            if (!this.mergeItemStack(lvt_5_1_, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (lvt_6_1_ <= 2 || !this.mergeItemStack(lvt_5_1_, 2, lvt_6_1_, false)) {
            int lvt_8_1_ = lvt_6_1_ + 27;
            int lvt_10_1_ = lvt_8_1_ + 9;
            if (p_82846_2_ >= lvt_8_1_ && p_82846_2_ < lvt_10_1_) {
               if (!this.mergeItemStack(lvt_5_1_, lvt_6_1_, lvt_8_1_, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= lvt_6_1_ && p_82846_2_ < lvt_8_1_) {
               if (!this.mergeItemStack(lvt_5_1_, lvt_8_1_, lvt_10_1_, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(lvt_5_1_, lvt_8_1_, lvt_8_1_, false)) {
               return ItemStack.EMPTY;
            }

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
      this.horseInventory.closeInventory(p_75134_1_);
   }
}
