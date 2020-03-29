package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ArmorDyeRecipe extends SpecialRecipe {
   public ArmorDyeRecipe(ResourceLocation p_i48173_1_) {
      super(p_i48173_1_);
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      List<ItemStack> list = Lists.newArrayList();

      for(int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
         ItemStack itemstack1 = p_77569_1_.getStackInSlot(i);
         if (!itemstack1.isEmpty()) {
            if (itemstack1.getItem() instanceof IDyeableArmorItem) {
               if (!itemstack.isEmpty()) {
                  return false;
               }

               itemstack = itemstack1;
            } else {
               if (!(itemstack1.getItem() instanceof DyeItem)) {
                  return false;
               }

               list.add(itemstack1);
            }
         }
      }

      return !itemstack.isEmpty() && !list.isEmpty();
   }

   public ItemStack getCraftingResult(CraftingInventory p_77572_1_) {
      List<DyeItem> list = Lists.newArrayList();
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < p_77572_1_.getSizeInventory(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(i);
         if (!itemstack1.isEmpty()) {
            Item item = itemstack1.getItem();
            if (item instanceof IDyeableArmorItem) {
               if (!itemstack.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               itemstack = itemstack1.copy();
            } else {
               if (!(item instanceof DyeItem)) {
                  return ItemStack.EMPTY;
               }

               list.add((DyeItem)item);
            }
         }
      }

      return !itemstack.isEmpty() && !list.isEmpty() ? IDyeableArmorItem.func_219975_a(itemstack, list) : ItemStack.EMPTY;
   }

   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_ARMORDYE;
   }
}
