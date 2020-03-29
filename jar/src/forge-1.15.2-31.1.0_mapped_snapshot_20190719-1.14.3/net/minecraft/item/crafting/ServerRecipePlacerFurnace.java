package net.minecraft.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ServerRecipePlacerFurnace<C extends IInventory> extends ServerRecipePlacer<C> {
   private boolean matches;

   public ServerRecipePlacerFurnace(RecipeBookContainer<C> p_i50751_1_) {
      super(p_i50751_1_);
   }

   protected void tryPlaceRecipe(IRecipe<C> p_201508_1_, boolean p_201508_2_) {
      this.matches = this.recipeBookContainer.matches(p_201508_1_);
      int lvt_3_1_ = this.recipeItemHelper.getBiggestCraftableStack(p_201508_1_, (IntList)null);
      if (this.matches) {
         ItemStack lvt_4_1_ = this.recipeBookContainer.getSlot(0).getStack();
         if (lvt_4_1_.isEmpty() || lvt_3_1_ <= lvt_4_1_.getCount()) {
            return;
         }
      }

      int lvt_4_2_ = this.getMaxAmount(p_201508_2_, lvt_3_1_, this.matches);
      IntList lvt_5_1_ = new IntArrayList();
      if (this.recipeItemHelper.canCraft(p_201508_1_, lvt_5_1_, lvt_4_2_)) {
         if (!this.matches) {
            this.giveToPlayer(this.recipeBookContainer.getOutputSlot());
            this.giveToPlayer(0);
         }

         this.func_201516_a(lvt_4_2_, lvt_5_1_);
      }
   }

   protected void clear() {
      this.giveToPlayer(this.recipeBookContainer.getOutputSlot());
      super.clear();
   }

   protected void func_201516_a(int p_201516_1_, IntList p_201516_2_) {
      Iterator<Integer> lvt_3_1_ = p_201516_2_.iterator();
      Slot lvt_4_1_ = this.recipeBookContainer.getSlot(0);
      ItemStack lvt_5_1_ = RecipeItemHelper.unpack((Integer)lvt_3_1_.next());
      if (!lvt_5_1_.isEmpty()) {
         int lvt_6_1_ = Math.min(lvt_5_1_.getMaxStackSize(), p_201516_1_);
         if (this.matches) {
            lvt_6_1_ -= lvt_4_1_.getStack().getCount();
         }

         for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_; ++lvt_7_1_) {
            this.consumeIngredient(lvt_4_1_, lvt_5_1_);
         }

      }
   }
}
