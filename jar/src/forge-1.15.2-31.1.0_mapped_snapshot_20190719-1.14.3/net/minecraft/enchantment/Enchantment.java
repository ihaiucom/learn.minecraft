package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class Enchantment extends ForgeRegistryEntry<Enchantment> {
   private final EquipmentSlotType[] applicableEquipmentTypes;
   private final Enchantment.Rarity rarity;
   @Nullable
   public EnchantmentType type;
   @Nullable
   protected String name;

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Enchantment getEnchantmentByID(int p_185262_0_) {
      return (Enchantment)Registry.ENCHANTMENT.getByValue(p_185262_0_);
   }

   protected Enchantment(Enchantment.Rarity p_i46731_1_, EnchantmentType p_i46731_2_, EquipmentSlotType[] p_i46731_3_) {
      this.rarity = p_i46731_1_;
      this.type = p_i46731_2_;
      this.applicableEquipmentTypes = p_i46731_3_;
   }

   public Map<EquipmentSlotType, ItemStack> getEntityEquipment(LivingEntity p_222181_1_) {
      Map<EquipmentSlotType, ItemStack> map = Maps.newEnumMap(EquipmentSlotType.class);
      EquipmentSlotType[] var3 = this.applicableEquipmentTypes;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EquipmentSlotType equipmentslottype = var3[var5];
         ItemStack itemstack = p_222181_1_.getItemStackFromSlot(equipmentslottype);
         if (!itemstack.isEmpty()) {
            map.put(equipmentslottype, itemstack);
         }
      }

      return map;
   }

   public Enchantment.Rarity getRarity() {
      return this.rarity;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return 1;
   }

   public int getMinEnchantability(int p_77321_1_) {
      return 1 + p_77321_1_ * 10;
   }

   public int getMaxEnchantability(int p_223551_1_) {
      return this.getMinEnchantability(p_223551_1_) + 5;
   }

   public int calcModifierDamage(int p_77318_1_, DamageSource p_77318_2_) {
      return 0;
   }

   public float calcDamageByCreature(int p_152376_1_, CreatureAttribute p_152376_2_) {
      return 0.0F;
   }

   public final boolean isCompatibleWith(Enchantment p_191560_1_) {
      return this.canApplyTogether(p_191560_1_) && p_191560_1_.canApplyTogether(this);
   }

   protected boolean canApplyTogether(Enchantment p_77326_1_) {
      return this != p_77326_1_;
   }

   protected String getDefaultTranslationKey() {
      if (this.name == null) {
         this.name = Util.makeTranslationKey("enchantment", Registry.ENCHANTMENT.getKey(this));
      }

      return this.name;
   }

   public String getName() {
      return this.getDefaultTranslationKey();
   }

   public ITextComponent getDisplayName(int p_200305_1_) {
      ITextComponent itextcomponent = new TranslationTextComponent(this.getName(), new Object[0]);
      if (this.isCurse()) {
         itextcomponent.applyTextStyle(TextFormatting.RED);
      } else {
         itextcomponent.applyTextStyle(TextFormatting.GRAY);
      }

      if (p_200305_1_ != 1 || this.getMaxLevel() != 1) {
         itextcomponent.appendText(" ").appendSibling(new TranslationTextComponent("enchantment.level." + p_200305_1_, new Object[0]));
      }

      return itextcomponent;
   }

   public boolean canApply(ItemStack p_92089_1_) {
      return this.canApplyAtEnchantingTable(p_92089_1_);
   }

   public void onEntityDamaged(LivingEntity p_151368_1_, Entity p_151368_2_, int p_151368_3_) {
   }

   public void onUserHurt(LivingEntity p_151367_1_, Entity p_151367_2_, int p_151367_3_) {
   }

   public boolean isTreasureEnchantment() {
      return false;
   }

   public boolean isCurse() {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack p_canApplyAtEnchantingTable_1_) {
      return p_canApplyAtEnchantingTable_1_.canApplyAtEnchantingTable(this);
   }

   public boolean isAllowedOnBooks() {
      return true;
   }

   public static enum Rarity {
      COMMON(10),
      UNCOMMON(5),
      RARE(2),
      VERY_RARE(1);

      private final int weight;

      private Rarity(int p_i47026_3_) {
         this.weight = p_i47026_3_;
      }

      public int getWeight() {
         return this.weight;
      }
   }
}
