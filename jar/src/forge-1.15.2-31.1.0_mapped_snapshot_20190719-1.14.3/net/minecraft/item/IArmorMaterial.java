package net.minecraft.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IArmorMaterial {
   int getDurability(EquipmentSlotType var1);

   int getDamageReductionAmount(EquipmentSlotType var1);

   int getEnchantability();

   SoundEvent getSoundEvent();

   Ingredient getRepairMaterial();

   @OnlyIn(Dist.CLIENT)
   String getName();

   float getToughness();
}