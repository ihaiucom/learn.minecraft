package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FireworkStarFadeRecipe extends SpecialRecipe {
   private static final Ingredient INGREDIENT_FIREWORK_STAR;

   public FireworkStarFadeRecipe(ResourceLocation p_i48167_1_) {
      super(p_i48167_1_);
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      boolean flag = false;
      boolean flag1 = false;

      for(int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
         ItemStack itemstack = p_77569_1_.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem() instanceof DyeItem) {
               flag = true;
            } else {
               if (!INGREDIENT_FIREWORK_STAR.test(itemstack)) {
                  return false;
               }

               if (flag1) {
                  return false;
               }

               flag1 = true;
            }
         }
      }

      return flag1 && flag;
   }

   public ItemStack getCraftingResult(CraftingInventory p_77572_1_) {
      List<Integer> list = Lists.newArrayList();
      ItemStack itemstack = null;

      for(int i = 0; i < p_77572_1_.getSizeInventory(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(i);
         Item item = itemstack1.getItem();
         if (item instanceof DyeItem) {
            list.add(((DyeItem)item).getDyeColor().getFireworkColor());
         } else if (INGREDIENT_FIREWORK_STAR.test(itemstack1)) {
            itemstack = itemstack1.copy();
            itemstack.setCount(1);
         }
      }

      if (itemstack != null && !list.isEmpty()) {
         itemstack.getOrCreateChildTag("Explosion").putIntArray("FadeColors", (List)list);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_FIREWORK_STAR_FADE;
   }

   static {
      INGREDIENT_FIREWORK_STAR = Ingredient.fromItems(Items.FIREWORK_STAR);
   }
}
