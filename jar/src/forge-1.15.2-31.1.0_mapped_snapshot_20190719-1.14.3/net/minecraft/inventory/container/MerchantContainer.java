package net.minecraft.inventory.container;

import net.minecraft.entity.Entity;
import net.minecraft.entity.NPCMerchant;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantContainer extends Container {
   private final IMerchant merchant;
   private final MerchantInventory merchantInventory;
   @OnlyIn(Dist.CLIENT)
   private int field_217054_e;
   @OnlyIn(Dist.CLIENT)
   private boolean field_217055_f;
   @OnlyIn(Dist.CLIENT)
   private boolean field_223433_g;

   public MerchantContainer(int p_i50068_1_, PlayerInventory p_i50068_2_) {
      this(p_i50068_1_, p_i50068_2_, new NPCMerchant(p_i50068_2_.player));
   }

   public MerchantContainer(int p_i50069_1_, PlayerInventory p_i50069_2_, IMerchant p_i50069_3_) {
      super(ContainerType.MERCHANT, p_i50069_1_);
      this.merchant = p_i50069_3_;
      this.merchantInventory = new MerchantInventory(p_i50069_3_);
      this.addSlot(new Slot(this.merchantInventory, 0, 136, 37));
      this.addSlot(new Slot(this.merchantInventory, 1, 162, 37));
      this.addSlot(new MerchantResultSlot(p_i50069_2_.player, p_i50069_3_, this.merchantInventory, 2, 220, 37));

      int lvt_4_2_;
      for(lvt_4_2_ = 0; lvt_4_2_ < 3; ++lvt_4_2_) {
         for(int lvt_5_1_ = 0; lvt_5_1_ < 9; ++lvt_5_1_) {
            this.addSlot(new Slot(p_i50069_2_, lvt_5_1_ + lvt_4_2_ * 9 + 9, 108 + lvt_5_1_ * 18, 84 + lvt_4_2_ * 18));
         }
      }

      for(lvt_4_2_ = 0; lvt_4_2_ < 9; ++lvt_4_2_) {
         this.addSlot(new Slot(p_i50069_2_, lvt_4_2_, 108 + lvt_4_2_ * 18, 142));
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void func_217045_a(boolean p_217045_1_) {
      this.field_217055_f = p_217045_1_;
   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      this.merchantInventory.resetRecipeAndSlots();
      super.onCraftMatrixChanged(p_75130_1_);
   }

   public void setCurrentRecipeIndex(int p_75175_1_) {
      this.merchantInventory.setCurrentRecipeIndex(p_75175_1_);
   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return this.merchant.getCustomer() == p_75145_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217048_e() {
      return this.merchant.getXp();
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217047_f() {
      return this.merchantInventory.func_214024_h();
   }

   @OnlyIn(Dist.CLIENT)
   public void func_217052_e(int p_217052_1_) {
      this.merchant.func_213702_q(p_217052_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217049_g() {
      return this.field_217054_e;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_217043_f(int p_217043_1_) {
      this.field_217054_e = p_217043_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_223431_b(boolean p_223431_1_) {
      this.field_223433_g = p_223431_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_223432_h() {
      return this.field_223433_g;
   }

   public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
      return false;
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ == 2) {
            if (!this.mergeItemStack(lvt_5_1_, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
            this.func_223132_j();
         } else if (p_82846_2_ != 0 && p_82846_2_ != 1) {
            if (p_82846_2_ >= 3 && p_82846_2_ < 30) {
               if (!this.mergeItemStack(lvt_5_1_, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.mergeItemStack(lvt_5_1_, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(lvt_5_1_, 3, 39, false)) {
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

   private void func_223132_j() {
      if (!this.merchant.getWorld().isRemote) {
         Entity lvt_1_1_ = (Entity)this.merchant;
         this.merchant.getWorld().playSound(lvt_1_1_.func_226277_ct_(), lvt_1_1_.func_226278_cu_(), lvt_1_1_.func_226281_cx_(), this.merchant.func_213714_ea(), SoundCategory.NEUTRAL, 1.0F, 1.0F, false);
      }

   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.merchant.setCustomer((PlayerEntity)null);
      if (!this.merchant.getWorld().isRemote) {
         if (p_75134_1_.isAlive() && (!(p_75134_1_ instanceof ServerPlayerEntity) || !((ServerPlayerEntity)p_75134_1_).hasDisconnected())) {
            p_75134_1_.inventory.placeItemBackInInventory(p_75134_1_.world, this.merchantInventory.removeStackFromSlot(0));
            p_75134_1_.inventory.placeItemBackInInventory(p_75134_1_.world, this.merchantInventory.removeStackFromSlot(1));
         } else {
            ItemStack lvt_2_1_ = this.merchantInventory.removeStackFromSlot(0);
            if (!lvt_2_1_.isEmpty()) {
               p_75134_1_.dropItem(lvt_2_1_, false);
            }

            lvt_2_1_ = this.merchantInventory.removeStackFromSlot(1);
            if (!lvt_2_1_.isEmpty()) {
               p_75134_1_.dropItem(lvt_2_1_, false);
            }
         }

      }
   }

   public void func_217046_g(int p_217046_1_) {
      if (this.func_217051_h().size() > p_217046_1_) {
         ItemStack lvt_2_1_ = this.merchantInventory.getStackInSlot(0);
         if (!lvt_2_1_.isEmpty()) {
            if (!this.mergeItemStack(lvt_2_1_, 3, 39, true)) {
               return;
            }

            this.merchantInventory.setInventorySlotContents(0, lvt_2_1_);
         }

         ItemStack lvt_3_1_ = this.merchantInventory.getStackInSlot(1);
         if (!lvt_3_1_.isEmpty()) {
            if (!this.mergeItemStack(lvt_3_1_, 3, 39, true)) {
               return;
            }

            this.merchantInventory.setInventorySlotContents(1, lvt_3_1_);
         }

         if (this.merchantInventory.getStackInSlot(0).isEmpty() && this.merchantInventory.getStackInSlot(1).isEmpty()) {
            ItemStack lvt_4_1_ = ((MerchantOffer)this.func_217051_h().get(p_217046_1_)).func_222205_b();
            this.func_217053_c(0, lvt_4_1_);
            ItemStack lvt_5_1_ = ((MerchantOffer)this.func_217051_h().get(p_217046_1_)).func_222202_c();
            this.func_217053_c(1, lvt_5_1_);
         }

      }
   }

   private void func_217053_c(int p_217053_1_, ItemStack p_217053_2_) {
      if (!p_217053_2_.isEmpty()) {
         for(int lvt_3_1_ = 3; lvt_3_1_ < 39; ++lvt_3_1_) {
            ItemStack lvt_4_1_ = ((Slot)this.inventorySlots.get(lvt_3_1_)).getStack();
            if (!lvt_4_1_.isEmpty() && this.func_217050_b(p_217053_2_, lvt_4_1_)) {
               ItemStack lvt_5_1_ = this.merchantInventory.getStackInSlot(p_217053_1_);
               int lvt_6_1_ = lvt_5_1_.isEmpty() ? 0 : lvt_5_1_.getCount();
               int lvt_7_1_ = Math.min(p_217053_2_.getMaxStackSize() - lvt_6_1_, lvt_4_1_.getCount());
               ItemStack lvt_8_1_ = lvt_4_1_.copy();
               int lvt_9_1_ = lvt_6_1_ + lvt_7_1_;
               lvt_4_1_.shrink(lvt_7_1_);
               lvt_8_1_.setCount(lvt_9_1_);
               this.merchantInventory.setInventorySlotContents(p_217053_1_, lvt_8_1_);
               if (lvt_9_1_ >= p_217053_2_.getMaxStackSize()) {
                  break;
               }
            }
         }
      }

   }

   private boolean func_217050_b(ItemStack p_217050_1_, ItemStack p_217050_2_) {
      return p_217050_1_.getItem() == p_217050_2_.getItem() && ItemStack.areItemStackTagsEqual(p_217050_1_, p_217050_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public void func_217044_a(MerchantOffers p_217044_1_) {
      this.merchant.func_213703_a(p_217044_1_);
   }

   public MerchantOffers func_217051_h() {
      return this.merchant.getOffers();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_217042_i() {
      return this.field_217055_f;
   }
}
