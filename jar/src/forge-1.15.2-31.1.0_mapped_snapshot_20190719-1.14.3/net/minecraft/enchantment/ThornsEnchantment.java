package net.minecraft.enchantment;

import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ThornsEnchantment extends Enchantment {
   public ThornsEnchantment(Enchantment.Rarity p_i46722_1_, EquipmentSlotType... p_i46722_2_) {
      super(p_i46722_1_, EnchantmentType.ARMOR_CHEST, p_i46722_2_);
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 10 + 20 * (p_77321_1_ - 1);
   }

   public int getMaxEnchantability(int p_223551_1_) {
      return super.getMinEnchantability(p_223551_1_) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApply(ItemStack p_92089_1_) {
      return p_92089_1_.getItem() instanceof ArmorItem ? true : super.canApply(p_92089_1_);
   }

   public void onUserHurt(LivingEntity p_151367_1_, Entity p_151367_2_, int p_151367_3_) {
      Random lvt_4_1_ = p_151367_1_.getRNG();
      Entry<EquipmentSlotType, ItemStack> lvt_5_1_ = EnchantmentHelper.func_222189_b(Enchantments.THORNS, p_151367_1_);
      if (shouldHit(p_151367_3_, lvt_4_1_)) {
         if (p_151367_2_ != null) {
            p_151367_2_.attackEntityFrom(DamageSource.causeThornsDamage(p_151367_1_), (float)getDamage(p_151367_3_, lvt_4_1_));
         }

         if (lvt_5_1_ != null) {
            ((ItemStack)lvt_5_1_.getValue()).damageItem(3, p_151367_1_, (p_222183_1_) -> {
               p_222183_1_.sendBreakAnimation((EquipmentSlotType)lvt_5_1_.getKey());
            });
         }
      } else if (lvt_5_1_ != null) {
         ((ItemStack)lvt_5_1_.getValue()).damageItem(1, p_151367_1_, (p_222182_1_) -> {
            p_222182_1_.sendBreakAnimation((EquipmentSlotType)lvt_5_1_.getKey());
         });
      }

   }

   public static boolean shouldHit(int p_92094_0_, Random p_92094_1_) {
      if (p_92094_0_ <= 0) {
         return false;
      } else {
         return p_92094_1_.nextFloat() < 0.15F * (float)p_92094_0_;
      }
   }

   public static int getDamage(int p_92095_0_, Random p_92095_1_) {
      return p_92095_0_ > 10 ? p_92095_0_ - 10 : 1 + p_92095_1_.nextInt(4);
   }
}
