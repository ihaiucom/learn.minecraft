package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IRecipe<C extends IInventory> {
   boolean matches(C var1, World var2);

   ItemStack getCraftingResult(C var1);

   boolean canFit(int var1, int var2);

   ItemStack getRecipeOutput();

   default NonNullList<ItemStack> getRemainingItems(C p_179532_1_) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(p_179532_1_.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack item = p_179532_1_.getStackInSlot(i);
         if (item.hasContainerItem()) {
            nonnulllist.set(i, item.getContainerItem());
         }
      }

      return nonnulllist;
   }

   default NonNullList<Ingredient> getIngredients() {
      return NonNullList.create();
   }

   default boolean isDynamic() {
      return false;
   }

   default String getGroup() {
      return "";
   }

   default ItemStack getIcon() {
      return new ItemStack(Blocks.CRAFTING_TABLE);
   }

   ResourceLocation getId();

   IRecipeSerializer<?> getSerializer();

   IRecipeType<?> getType();
}
