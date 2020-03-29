package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class DepthStriderEnchantment extends Enchantment {
   public DepthStriderEnchantment(Enchantment.Rarity p_i46720_1_, EquipmentSlotType... p_i46720_2_) {
      super(p_i46720_1_, EnchantmentType.ARMOR_FEET, p_i46720_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return p_77321_1_ * 10;
   }

   public int getMaxEnchantability(int p_223551_1_) {
      return this.getMinEnchantability(p_223551_1_) + 15;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_) && p_77326_1_ != Enchantments.FROST_WALKER;
   }
}
