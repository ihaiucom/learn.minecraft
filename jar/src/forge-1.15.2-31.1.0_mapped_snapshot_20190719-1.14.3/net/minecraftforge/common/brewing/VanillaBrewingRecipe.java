package net.minecraftforge.common.brewing;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionBrewing;

public class VanillaBrewingRecipe implements IBrewingRecipe {
   public boolean isInput(ItemStack stack) {
      Item item = stack.getItem();
      return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE;
   }

   public boolean isIngredient(ItemStack stack) {
      return PotionBrewing.isReagent(stack);
   }

   public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      if (!input.isEmpty() && !ingredient.isEmpty() && this.isIngredient(ingredient)) {
         ItemStack result = PotionBrewing.doReaction(ingredient, input);
         return result != input ? result : ItemStack.EMPTY;
      } else {
         return ItemStack.EMPTY;
      }
   }
}
