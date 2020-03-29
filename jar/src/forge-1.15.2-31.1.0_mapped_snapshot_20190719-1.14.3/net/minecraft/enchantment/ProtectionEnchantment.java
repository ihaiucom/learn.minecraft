package net.minecraft.enchantment;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment extends Enchantment {
   public final ProtectionEnchantment.Type protectionType;

   public ProtectionEnchantment(Enchantment.Rarity p_i46723_1_, ProtectionEnchantment.Type p_i46723_2_, EquipmentSlotType... p_i46723_3_) {
      super(p_i46723_1_, EnchantmentType.ARMOR, p_i46723_3_);
      this.protectionType = p_i46723_2_;
      if (p_i46723_2_ == ProtectionEnchantment.Type.FALL) {
         this.type = EnchantmentType.ARMOR_FEET;
      }

   }

   public int getMinEnchantability(int p_77321_1_) {
      return this.protectionType.getMinimalEnchantability() + (p_77321_1_ - 1) * this.protectionType.getEnchantIncreasePerLevel();
   }

   public int getMaxEnchantability(int p_223551_1_) {
      return this.getMinEnchantability(p_223551_1_) + this.protectionType.getEnchantIncreasePerLevel();
   }

   public int getMaxLevel() {
      return 4;
   }

   public int calcModifierDamage(int p_77318_1_, DamageSource p_77318_2_) {
      if (p_77318_2_.canHarmInCreative()) {
         return 0;
      } else if (this.protectionType == ProtectionEnchantment.Type.ALL) {
         return p_77318_1_;
      } else if (this.protectionType == ProtectionEnchantment.Type.FIRE && p_77318_2_.isFireDamage()) {
         return p_77318_1_ * 2;
      } else if (this.protectionType == ProtectionEnchantment.Type.FALL && p_77318_2_ == DamageSource.FALL) {
         return p_77318_1_ * 3;
      } else if (this.protectionType == ProtectionEnchantment.Type.EXPLOSION && p_77318_2_.isExplosion()) {
         return p_77318_1_ * 2;
      } else {
         return this.protectionType == ProtectionEnchantment.Type.PROJECTILE && p_77318_2_.isProjectile() ? p_77318_1_ * 2 : 0;
      }
   }

   public boolean canApplyTogether(Enchantment p_77326_1_) {
      if (p_77326_1_ instanceof ProtectionEnchantment) {
         ProtectionEnchantment lvt_2_1_ = (ProtectionEnchantment)p_77326_1_;
         if (this.protectionType == lvt_2_1_.protectionType) {
            return false;
         } else {
            return this.protectionType == ProtectionEnchantment.Type.FALL || lvt_2_1_.protectionType == ProtectionEnchantment.Type.FALL;
         }
      } else {
         return super.canApplyTogether(p_77326_1_);
      }
   }

   public static int getFireTimeForEntity(LivingEntity p_92093_0_, int p_92093_1_) {
      int lvt_2_1_ = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FIRE_PROTECTION, p_92093_0_);
      if (lvt_2_1_ > 0) {
         p_92093_1_ -= MathHelper.floor((float)p_92093_1_ * (float)lvt_2_1_ * 0.15F);
      }

      return p_92093_1_;
   }

   public static double getBlastDamageReduction(LivingEntity p_92092_0_, double p_92092_1_) {
      int lvt_3_1_ = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.BLAST_PROTECTION, p_92092_0_);
      if (lvt_3_1_ > 0) {
         p_92092_1_ -= (double)MathHelper.floor(p_92092_1_ * (double)((float)lvt_3_1_ * 0.15F));
      }

      return p_92092_1_;
   }

   public static enum Type {
      ALL("all", 1, 11),
      FIRE("fire", 10, 8),
      FALL("fall", 5, 6),
      EXPLOSION("explosion", 5, 8),
      PROJECTILE("projectile", 3, 6);

      private final String typeName;
      private final int minEnchantability;
      private final int levelCost;

      private Type(String p_i48839_3_, int p_i48839_4_, int p_i48839_5_) {
         this.typeName = p_i48839_3_;
         this.minEnchantability = p_i48839_4_;
         this.levelCost = p_i48839_5_;
      }

      public int getMinimalEnchantability() {
         return this.minEnchantability;
      }

      public int getEnchantIncreasePerLevel() {
         return this.levelCost;
      }
   }
}
