package net.minecraft.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantInventory implements IInventory {
   private final IMerchant merchant;
   private final NonNullList<ItemStack> slots;
   @Nullable
   private MerchantOffer field_214026_c;
   private int currentRecipeIndex;
   private int field_214027_e;

   public MerchantInventory(IMerchant p_i50071_1_) {
      this.slots = NonNullList.withSize(3, ItemStack.EMPTY);
      this.merchant = p_i50071_1_;
   }

   public int getSizeInventory() {
      return this.slots.size();
   }

   public boolean isEmpty() {
      Iterator var1 = this.slots.iterator();

      ItemStack lvt_2_1_;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         lvt_2_1_ = (ItemStack)var1.next();
      } while(lvt_2_1_.isEmpty());

      return false;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return (ItemStack)this.slots.get(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      ItemStack lvt_3_1_ = (ItemStack)this.slots.get(p_70298_1_);
      if (p_70298_1_ == 2 && !lvt_3_1_.isEmpty()) {
         return ItemStackHelper.getAndSplit(this.slots, p_70298_1_, lvt_3_1_.getCount());
      } else {
         ItemStack lvt_4_1_ = ItemStackHelper.getAndSplit(this.slots, p_70298_1_, p_70298_2_);
         if (!lvt_4_1_.isEmpty() && this.inventoryResetNeededOnSlotChange(p_70298_1_)) {
            this.resetRecipeAndSlots();
         }

         return lvt_4_1_;
      }
   }

   private boolean inventoryResetNeededOnSlotChange(int p_70469_1_) {
      return p_70469_1_ == 0 || p_70469_1_ == 1;
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.slots, p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.slots.set(p_70299_1_, p_70299_2_);
      if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

      if (this.inventoryResetNeededOnSlotChange(p_70299_1_)) {
         this.resetRecipeAndSlots();
      }

   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      return this.merchant.getCustomer() == p_70300_1_;
   }

   public void markDirty() {
      this.resetRecipeAndSlots();
   }

   public void resetRecipeAndSlots() {
      this.field_214026_c = null;
      ItemStack lvt_1_2_;
      ItemStack lvt_2_2_;
      if (((ItemStack)this.slots.get(0)).isEmpty()) {
         lvt_1_2_ = (ItemStack)this.slots.get(1);
         lvt_2_2_ = ItemStack.EMPTY;
      } else {
         lvt_1_2_ = (ItemStack)this.slots.get(0);
         lvt_2_2_ = (ItemStack)this.slots.get(1);
      }

      if (lvt_1_2_.isEmpty()) {
         this.setInventorySlotContents(2, ItemStack.EMPTY);
         this.field_214027_e = 0;
      } else {
         MerchantOffers lvt_3_1_ = this.merchant.getOffers();
         if (!lvt_3_1_.isEmpty()) {
            MerchantOffer lvt_4_1_ = lvt_3_1_.func_222197_a(lvt_1_2_, lvt_2_2_, this.currentRecipeIndex);
            if (lvt_4_1_ == null || lvt_4_1_.func_222217_o()) {
               this.field_214026_c = lvt_4_1_;
               lvt_4_1_ = lvt_3_1_.func_222197_a(lvt_2_2_, lvt_1_2_, this.currentRecipeIndex);
            }

            if (lvt_4_1_ != null && !lvt_4_1_.func_222217_o()) {
               this.field_214026_c = lvt_4_1_;
               this.setInventorySlotContents(2, lvt_4_1_.func_222206_f());
               this.field_214027_e = lvt_4_1_.func_222210_n();
            } else {
               this.setInventorySlotContents(2, ItemStack.EMPTY);
               this.field_214027_e = 0;
            }
         }

         this.merchant.verifySellingItem(this.getStackInSlot(2));
      }
   }

   @Nullable
   public MerchantOffer func_214025_g() {
      return this.field_214026_c;
   }

   public void setCurrentRecipeIndex(int p_70471_1_) {
      this.currentRecipeIndex = p_70471_1_;
      this.resetRecipeAndSlots();
   }

   public void clear() {
      this.slots.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public int func_214024_h() {
      return this.field_214027_e;
   }
}
