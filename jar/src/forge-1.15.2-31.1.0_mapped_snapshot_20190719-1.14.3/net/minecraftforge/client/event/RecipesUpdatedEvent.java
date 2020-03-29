package net.minecraftforge.client.event;

import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.Event;

public class RecipesUpdatedEvent extends Event {
   private final RecipeManager mgr;

   public RecipesUpdatedEvent(RecipeManager mgr) {
      this.mgr = mgr;
   }

   public RecipeManager getRecipeManager() {
      return this.mgr;
   }
}
