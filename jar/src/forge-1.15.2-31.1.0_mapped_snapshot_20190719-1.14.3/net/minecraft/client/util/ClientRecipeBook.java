package net.minecraft.client.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.inventory.container.BlastFurnaceContainer;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.SmokerContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientRecipeBook extends RecipeBook {
   private final RecipeManager recipeManager;
   private final Map<RecipeBookCategories, List<RecipeList>> recipesByCategory = Maps.newHashMap();
   private final List<RecipeList> allRecipes = Lists.newArrayList();

   public ClientRecipeBook(RecipeManager p_i48186_1_) {
      this.recipeManager = p_i48186_1_;
   }

   public void rebuildTable() {
      this.allRecipes.clear();
      this.recipesByCategory.clear();
      Table<RecipeBookCategories, String, RecipeList> lvt_1_1_ = HashBasedTable.create();
      Iterator var2 = this.recipeManager.getRecipes().iterator();

      while(var2.hasNext()) {
         IRecipe<?> lvt_3_1_ = (IRecipe)var2.next();
         if (!lvt_3_1_.isDynamic()) {
            RecipeBookCategories lvt_4_1_ = getCategory(lvt_3_1_);
            String lvt_5_1_ = lvt_3_1_.getGroup();
            RecipeList lvt_6_2_;
            if (lvt_5_1_.isEmpty()) {
               lvt_6_2_ = this.newRecipeList(lvt_4_1_);
            } else {
               lvt_6_2_ = (RecipeList)lvt_1_1_.get(lvt_4_1_, lvt_5_1_);
               if (lvt_6_2_ == null) {
                  lvt_6_2_ = this.newRecipeList(lvt_4_1_);
                  lvt_1_1_.put(lvt_4_1_, lvt_5_1_, lvt_6_2_);
               }
            }

            lvt_6_2_.add(lvt_3_1_);
         }
      }

   }

   private RecipeList newRecipeList(RecipeBookCategories p_202889_1_) {
      RecipeList lvt_2_1_ = new RecipeList();
      this.allRecipes.add(lvt_2_1_);
      ((List)this.recipesByCategory.computeIfAbsent(p_202889_1_, (p_202890_0_) -> {
         return Lists.newArrayList();
      })).add(lvt_2_1_);
      if (p_202889_1_ != RecipeBookCategories.FURNACE_BLOCKS && p_202889_1_ != RecipeBookCategories.FURNACE_FOOD && p_202889_1_ != RecipeBookCategories.FURNACE_MISC) {
         if (p_202889_1_ != RecipeBookCategories.BLAST_FURNACE_BLOCKS && p_202889_1_ != RecipeBookCategories.BLAST_FURNACE_MISC) {
            if (p_202889_1_ == RecipeBookCategories.SMOKER_FOOD) {
               this.func_216767_a(RecipeBookCategories.SMOKER_SEARCH, lvt_2_1_);
            } else if (p_202889_1_ == RecipeBookCategories.STONECUTTER) {
               this.func_216767_a(RecipeBookCategories.STONECUTTER, lvt_2_1_);
            } else if (p_202889_1_ == RecipeBookCategories.CAMPFIRE) {
               this.func_216767_a(RecipeBookCategories.CAMPFIRE, lvt_2_1_);
            } else {
               this.func_216767_a(RecipeBookCategories.SEARCH, lvt_2_1_);
            }
         } else {
            this.func_216767_a(RecipeBookCategories.BLAST_FURNACE_SEARCH, lvt_2_1_);
         }
      } else {
         this.func_216767_a(RecipeBookCategories.FURNACE_SEARCH, lvt_2_1_);
      }

      return lvt_2_1_;
   }

   private void func_216767_a(RecipeBookCategories p_216767_1_, RecipeList p_216767_2_) {
      ((List)this.recipesByCategory.computeIfAbsent(p_216767_1_, (p_216768_0_) -> {
         return Lists.newArrayList();
      })).add(p_216767_2_);
   }

   private static RecipeBookCategories getCategory(IRecipe<?> p_202887_0_) {
      IRecipeType<?> lvt_1_1_ = p_202887_0_.getType();
      if (lvt_1_1_ == IRecipeType.SMELTING) {
         if (p_202887_0_.getRecipeOutput().getItem().isFood()) {
            return RecipeBookCategories.FURNACE_FOOD;
         } else {
            return p_202887_0_.getRecipeOutput().getItem() instanceof BlockItem ? RecipeBookCategories.FURNACE_BLOCKS : RecipeBookCategories.FURNACE_MISC;
         }
      } else if (lvt_1_1_ == IRecipeType.BLASTING) {
         return p_202887_0_.getRecipeOutput().getItem() instanceof BlockItem ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
      } else if (lvt_1_1_ == IRecipeType.SMOKING) {
         return RecipeBookCategories.SMOKER_FOOD;
      } else if (lvt_1_1_ == IRecipeType.STONECUTTING) {
         return RecipeBookCategories.STONECUTTER;
      } else if (lvt_1_1_ == IRecipeType.CAMPFIRE_COOKING) {
         return RecipeBookCategories.CAMPFIRE;
      } else {
         ItemStack lvt_2_1_ = p_202887_0_.getRecipeOutput();
         ItemGroup lvt_3_1_ = lvt_2_1_.getItem().getGroup();
         if (lvt_3_1_ == ItemGroup.BUILDING_BLOCKS) {
            return RecipeBookCategories.BUILDING_BLOCKS;
         } else if (lvt_3_1_ != ItemGroup.TOOLS && lvt_3_1_ != ItemGroup.COMBAT) {
            return lvt_3_1_ == ItemGroup.REDSTONE ? RecipeBookCategories.REDSTONE : RecipeBookCategories.MISC;
         } else {
            return RecipeBookCategories.EQUIPMENT;
         }
      }
   }

   public static List<RecipeBookCategories> func_216769_b(RecipeBookContainer<?> p_216769_0_) {
      if (!(p_216769_0_ instanceof WorkbenchContainer) && !(p_216769_0_ instanceof PlayerContainer)) {
         if (p_216769_0_ instanceof FurnaceContainer) {
            return Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC});
         } else if (p_216769_0_ instanceof BlastFurnaceContainer) {
            return Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.BLAST_FURNACE_SEARCH, RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC});
         } else {
            return p_216769_0_ instanceof SmokerContainer ? Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.SMOKER_SEARCH, RecipeBookCategories.SMOKER_FOOD}) : Lists.newArrayList();
         }
      } else {
         return Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.SEARCH, RecipeBookCategories.EQUIPMENT, RecipeBookCategories.BUILDING_BLOCKS, RecipeBookCategories.MISC, RecipeBookCategories.REDSTONE});
      }
   }

   public List<RecipeList> getRecipes() {
      return this.allRecipes;
   }

   public List<RecipeList> getRecipes(RecipeBookCategories p_202891_1_) {
      return (List)this.recipesByCategory.getOrDefault(p_202891_1_, Collections.emptyList());
   }
}
