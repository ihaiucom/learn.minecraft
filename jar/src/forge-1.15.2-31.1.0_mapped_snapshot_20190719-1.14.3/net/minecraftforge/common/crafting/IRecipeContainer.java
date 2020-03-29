package net.minecraftforge.common.crafting;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;

public interface IRecipeContainer {
   CraftResultInventory getCraftResult();

   CraftingInventory getCraftMatrix();
}
