package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class EfficiencyEnchantment extends Enchantment {
   protected EfficiencyEnchantment(Enchantment.Rarity p_i46732_1_, EquipmentSlotType... p_i46732_2_) {
      super(p_i46732_1_, EnchantmentType.DIGGER, p_i46732_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 1 + 10 * (p_77321_1_ - 1);
   }

   public int getMaxEnchantability(int p_223551_1_) {
      return super.getMinEnchantability(p_223551_1_) + 50;
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canApply(ItemStack p_92089_1_) {
      return p_92089_1_.getItem() == Items.SHEARS ? true : super.canApply(p_92089_1_);
   }
}
