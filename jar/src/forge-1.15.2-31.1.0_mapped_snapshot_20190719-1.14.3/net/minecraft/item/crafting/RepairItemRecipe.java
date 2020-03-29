package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RepairItemRecipe extends SpecialRecipe {
   public RepairItemRecipe(ResourceLocation p_i51524_1_) {
      super(p_i51524_1_);
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      List<ItemStack> list = Lists.newArrayList();

      for(int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
         ItemStack itemstack = p_77569_1_.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            list.add(itemstack);
            if (list.size() > 1) {
               ItemStack itemstack1 = (ItemStack)list.get(0);
               if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.isRepairable()) {
                  return false;
               }
            }
         }
      }

      return list.size() == 2;
   }

   public ItemStack getCraftingResult(CraftingInventory p_77572_1_) {
      List<ItemStack> list = Lists.newArrayList();

      ItemStack itemstack;
      for(int i = 0; i < p_77572_1_.getSizeInventory(); ++i) {
         itemstack = p_77572_1_.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            list.add(itemstack);
            if (list.size() > 1) {
               ItemStack itemstack1 = (ItemStack)list.get(0);
               if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.isRepairable()) {
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (list.size() == 2) {
         ItemStack itemstack3 = (ItemStack)list.get(0);
         itemstack = (ItemStack)list.get(1);
         if (itemstack3.getItem() == itemstack.getItem() && itemstack3.getCount() == 1 && itemstack.getCount() == 1 && itemstack3.isRepairable()) {
            Item item = itemstack3.getItem();
            int j = itemstack3.getMaxDamage() - itemstack3.getDamage();
            int k = itemstack3.getMaxDamage() - itemstack.getDamage();
            int l = j + k + itemstack3.getMaxDamage() * 5 / 100;
            int i1 = itemstack3.getMaxDamage() - l;
            if (i1 < 0) {
               i1 = 0;
            }

            ItemStack itemstack2 = new ItemStack(itemstack3.getItem());
            itemstack2.setDamage(i1);
            return itemstack2;
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.field_223550_o;
   }
}
