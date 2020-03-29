package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class PiercingEnchantment extends Enchantment {
   public PiercingEnchantment(Enchantment.Rarity p_i50019_1_, EquipmentSlotType... p_i50019_2_) {
      super(p_i50019_1_, EnchantmentType.CROSSBOW, p_i50019_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 1 + (p_77321_1_ - 1) * 10;
   }

   public int getMaxEnchantability(int p_223551_1_) {
      return 50;
   }

   public int getMaxLevel() {
      return 4;
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_) && p_77326_1_ != Enchantments.MULTISHOT;
   }
}
