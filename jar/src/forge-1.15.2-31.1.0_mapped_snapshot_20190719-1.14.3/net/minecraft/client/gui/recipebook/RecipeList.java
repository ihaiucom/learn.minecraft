package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeList {
   private final List<IRecipe<?>> recipes = Lists.newArrayList();
   private final Set<IRecipe<?>> craftable = Sets.newHashSet();
   private final Set<IRecipe<?>> canFit = Sets.newHashSet();
   private final Set<IRecipe<?>> inBook = Sets.newHashSet();
   private boolean singleResultItem = true;

   public boolean isNotEmpty() {
      return !this.inBook.isEmpty();
   }

   public void updateKnownRecipes(RecipeBook p_194214_1_) {
      Iterator var2 = this.recipes.iterator();

      while(var2.hasNext()) {
         IRecipe<?> lvt_3_1_ = (IRecipe)var2.next();
         if (p_194214_1_.isUnlocked(lvt_3_1_)) {
            this.inBook.add(lvt_3_1_);
         }
      }

   }

   public void canCraft(RecipeItemHelper p_194210_1_, int p_194210_2_, int p_194210_3_, RecipeBook p_194210_4_) {
      for(int lvt_5_1_ = 0; lvt_5_1_ < this.recipes.size(); ++lvt_5_1_) {
         IRecipe<?> lvt_6_1_ = (IRecipe)this.recipes.get(lvt_5_1_);
         boolean lvt_7_1_ = lvt_6_1_.canFit(p_194210_2_, p_194210_3_) && p_194210_4_.isUnlocked(lvt_6_1_);
         if (lvt_7_1_) {
            this.canFit.add(lvt_6_1_);
         } else {
            this.canFit.remove(lvt_6_1_);
         }

         if (lvt_7_1_ && p_194210_1_.canCraft(lvt_6_1_, (IntList)null)) {
            this.craftable.add(lvt_6_1_);
         } else {
            this.craftable.remove(lvt_6_1_);
         }
      }

   }

   public boolean isCraftable(IRecipe<?> p_194213_1_) {
      return this.craftable.contains(p_194213_1_);
   }

   public boolean containsCraftableRecipes() {
      return !this.craftable.isEmpty();
   }

   public boolean containsValidRecipes() {
      return !this.canFit.isEmpty();
   }

   public List<IRecipe<?>> getRecipes() {
      return this.recipes;
   }

   public List<IRecipe<?>> getRecipes(boolean p_194208_1_) {
      List<IRecipe<?>> lvt_2_1_ = Lists.newArrayList();
      Set<IRecipe<?>> lvt_3_1_ = p_194208_1_ ? this.craftable : this.canFit;
      Iterator var4 = this.recipes.iterator();

      while(var4.hasNext()) {
         IRecipe<?> lvt_5_1_ = (IRecipe)var4.next();
         if (lvt_3_1_.contains(lvt_5_1_)) {
            lvt_2_1_.add(lvt_5_1_);
         }
      }

      return lvt_2_1_;
   }

   public List<IRecipe<?>> getDisplayRecipes(boolean p_194207_1_) {
      List<IRecipe<?>> lvt_2_1_ = Lists.newArrayList();
      Iterator var3 = this.recipes.iterator();

      while(var3.hasNext()) {
         IRecipe<?> lvt_4_1_ = (IRecipe)var3.next();
         if (this.canFit.contains(lvt_4_1_) && this.craftable.contains(lvt_4_1_) == p_194207_1_) {
            lvt_2_1_.add(lvt_4_1_);
         }
      }

      return lvt_2_1_;
   }

   public void add(IRecipe<?> p_192709_1_) {
      this.recipes.add(p_192709_1_);
      if (this.singleResultItem) {
         ItemStack lvt_2_1_ = ((IRecipe)this.recipes.get(0)).getRecipeOutput();
         ItemStack lvt_3_1_ = p_192709_1_.getRecipeOutput();
         this.singleResultItem = ItemStack.areItemsEqual(lvt_2_1_, lvt_3_1_) && ItemStack.areItemStackTagsEqual(lvt_2_1_, lvt_3_1_);
      }

   }

   public boolean hasSingleResultItem() {
      return this.singleResultItem;
   }
}
