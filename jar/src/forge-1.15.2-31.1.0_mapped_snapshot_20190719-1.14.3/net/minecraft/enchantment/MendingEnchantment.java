package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class MendingEnchantment extends Enchantment {
   public MendingEnchantment(Enchantment.Rarity p_i46725_1_, EquipmentSlotType... p_i46725_2_) {
      super(p_i46725_1_, EnchantmentType.BREAKABLE, p_i46725_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return p_77321_1_ * 25;
   }

   public int getMaxEnchantability(int p_223551_1_) {
      return this.getMinEnchantability(p_223551_1_) + 50;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public int getMaxLevel() {
      return 1;
   }
}
