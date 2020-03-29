package net.minecraftforge.common.brewing;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class BrewingRecipe implements IBrewingRecipe {
   @Nonnull
   private final Ingredient input;
   @Nonnull
   private final Ingredient ingredient;
   @Nonnull
   private final ItemStack output;

   public BrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
      this.input = input;
      this.ingredient = ingredient;
      this.output = output;
   }

   public boolean isInput(@Nonnull ItemStack stack) {
      return this.input.test(stack);
   }

   public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      return this.isInput(input) && this.isIngredient(ingredient) ? this.getOutput().copy() : ItemStack.EMPTY;
   }

   public Ingredient getInput() {
      return this.input;
   }

   public Ingredient getIngredient() {
      return this.ingredient;
   }

   public ItemStack getOutput() {
      return this.output;
   }

   public boolean isIngredient(ItemStack ingredient) {
      return this.ingredient.test(ingredient);
   }
}
