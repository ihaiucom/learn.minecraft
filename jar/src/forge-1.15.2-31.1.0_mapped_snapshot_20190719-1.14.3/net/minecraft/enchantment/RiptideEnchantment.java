package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class RiptideEnchantment extends Enchantment {
   public RiptideEnchantment(Enchantment.Rarity p_i48784_1_, EquipmentSlotType... p_i48784_2_) {
      super(p_i48784_1_, EnchantmentType.TRIDENT, p_i48784_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 10 + p_77321_1_ * 7;
   }

   public int getMaxEnchantability(int p_223551_1_) {
      return 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      return super.canApplyTogether(p_77326_1_) && p_77326_1_ != Enchantments.LOYALTY && p_77326_1_ != Enchantments.CHANNELING;
   }
}
