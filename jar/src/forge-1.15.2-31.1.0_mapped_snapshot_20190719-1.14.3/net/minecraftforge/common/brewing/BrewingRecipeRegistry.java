package net.minecraftforge.common.brewing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

public class BrewingRecipeRegistry {
   private static List<IBrewingRecipe> recipes = new ArrayList();

   public static boolean addRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
      return addRecipe(new BrewingRecipe(input, ingredient, output));
   }

   public static boolean addRecipe(IBrewingRecipe recipe) {
      return recipes.add(recipe);
   }

   public static ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      if (!input.isEmpty() && input.getCount() == 1) {
         if (ingredient.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            Iterator var2 = recipes.iterator();

            ItemStack output;
            do {
               if (!var2.hasNext()) {
                  return ItemStack.EMPTY;
               }

               IBrewingRecipe recipe = (IBrewingRecipe)var2.next();
               output = recipe.getOutput(input, ingredient);
            } while(output.isEmpty());

            return output;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public static boolean hasOutput(ItemStack input, ItemStack ingredient) {
      return !getOutput(input, ingredient).isEmpty();
   }

   public static boolean canBrew(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes) {
      if (ingredient.isEmpty()) {
         return false;
      } else {
         int[] var3 = inputIndexes;
         int var4 = inputIndexes.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int i = var3[var5];
            if (hasOutput((ItemStack)inputs.get(i), ingredient)) {
               return true;
            }
         }

         return false;
      }
   }

   public static void brewPotions(NonNullList<ItemStack> inputs, ItemStack ingredient, int[] inputIndexes) {
      int[] var3 = inputIndexes;
      int var4 = inputIndexes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int i = var3[var5];
         ItemStack output = getOutput((ItemStack)inputs.get(i), ingredient);
         if (!output.isEmpty()) {
            inputs.set(i, output);
         }
      }

   }

   public static boolean isValidIngredient(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         Iterator var1 = recipes.iterator();

         IBrewingRecipe recipe;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            recipe = (IBrewingRecipe)var1.next();
         } while(!recipe.isIngredient(stack));

         return true;
      }
   }

   public static boolean isValidInput(ItemStack stack) {
      if (stack.getCount() != 1) {
         return false;
      } else {
         Iterator var1 = recipes.iterator();

         IBrewingRecipe recipe;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            recipe = (IBrewingRecipe)var1.next();
         } while(!recipe.isInput(stack));

         return true;
      }
   }

   public static List<IBrewingRecipe> getRecipes() {
      return Collections.unmodifiableList(recipes);
   }

   static {
      addRecipe(new VanillaBrewingRecipe());
   }
}
