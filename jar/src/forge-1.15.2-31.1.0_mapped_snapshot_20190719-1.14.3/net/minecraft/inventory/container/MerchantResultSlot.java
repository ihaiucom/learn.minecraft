package net.minecraft.inventory.container;

import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.stats.Stats;

public class MerchantResultSlot extends Slot {
   private final MerchantInventory merchantInventory;
   private final PlayerEntity player;
   private int removeCount;
   private final IMerchant merchant;

   public MerchantResultSlot(PlayerEntity p_i1822_1_, IMerchant p_i1822_2_, MerchantInventory p_i1822_3_, int p_i1822_4_, int p_i1822_5_, int p_i1822_6_) {
      super(p_i1822_3_, p_i1822_4_, p_i1822_5_, p_i1822_6_);
      this.player = p_i1822_1_;
      this.merchant = p_i1822_2_;
      this.merchantInventory = p_i1822_3_;
   }

   public boolean isItemValid(ItemStack p_75214_1_) {
      return false;
   }

   public ItemStack decrStackSize(int p_75209_1_) {
      if (this.getHasStack()) {
         this.removeCount += Math.min(p_75209_1_, this.getStack().getCount());
      }

      return super.decrStackSize(p_75209_1_);
   }

   protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_) {
      this.removeCount += p_75210_2_;
      this.onCrafting(p_75210_1_);
   }

   protected void onCrafting(ItemStack p_75208_1_) {
      p_75208_1_.onCrafting(this.player.world, this.player, this.removeCount);
      this.removeCount = 0;
   }

   public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
      this.onCrafting(p_190901_2_);
      MerchantOffer lvt_3_1_ = this.merchantInventory.func_214025_g();
      if (lvt_3_1_ != null) {
         ItemStack lvt_4_1_ = this.merchantInventory.getStackInSlot(0);
         ItemStack lvt_5_1_ = this.merchantInventory.getStackInSlot(1);
         if (lvt_3_1_.func_222215_b(lvt_4_1_, lvt_5_1_) || lvt_3_1_.func_222215_b(lvt_5_1_, lvt_4_1_)) {
            this.merchant.onTrade(lvt_3_1_);
            p_190901_1_.addStat(Stats.TRADED_WITH_VILLAGER);
            this.merchantInventory.setInventorySlotContents(0, lvt_4_1_);
            this.merchantInventory.setInventorySlotContents(1, lvt_5_1_);
         }

         this.merchant.func_213702_q(this.merchant.getXp() + lvt_3_1_.func_222210_n());
      }

      return p_190901_2_;
   }
}
