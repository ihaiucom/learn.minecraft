package net.minecraftforge.common.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;

public interface IShapedRecipe<T extends IInventory> extends IRecipe<T> {
   int getRecipeWidth();

   int getRecipeHeight();
}
