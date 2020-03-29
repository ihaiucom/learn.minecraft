package net.minecraft.item.crafting;

import java.util.Iterator;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class MapExtendingRecipe extends ShapedRecipe {
   public MapExtendingRecipe(ResourceLocation p_i48164_1_) {
      super(p_i48164_1_, "", 3, 3, NonNullList.from(Ingredient.EMPTY, Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.FILLED_MAP), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER), Ingredient.fromItems(Items.PAPER)), new ItemStack(Items.MAP));
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      if (!super.matches(p_77569_1_, p_77569_2_)) {
         return false;
      } else {
         ItemStack lvt_3_1_ = ItemStack.EMPTY;

         for(int lvt_4_1_ = 0; lvt_4_1_ < p_77569_1_.getSizeInventory() && lvt_3_1_.isEmpty(); ++lvt_4_1_) {
            ItemStack lvt_5_1_ = p_77569_1_.getStackInSlot(lvt_4_1_);
            if (lvt_5_1_.getItem() == Items.FILLED_MAP) {
               lvt_3_1_ = lvt_5_1_;
            }
         }

         if (lvt_3_1_.isEmpty()) {
            return false;
         } else {
            MapData lvt_4_2_ = FilledMapItem.getMapData(lvt_3_1_, p_77569_2_);
            if (lvt_4_2_ == null) {
               return false;
            } else if (this.isExplorationMap(lvt_4_2_)) {
               return false;
            } else {
               return lvt_4_2_.scale < 4;
            }
         }
      }
   }

   private boolean isExplorationMap(MapData p_190934_1_) {
      if (p_190934_1_.mapDecorations != null) {
         Iterator var2 = p_190934_1_.mapDecorations.values().iterator();

         while(var2.hasNext()) {
            MapDecoration lvt_3_1_ = (MapDecoration)var2.next();
            if (lvt_3_1_.getType() == MapDecoration.Type.MANSION || lvt_3_1_.getType() == MapDecoration.Type.MONUMENT) {
               return true;
            }
         }
      }

      return false;
   }

   public ItemStack getCraftingResult(CraftingInventory p_77572_1_) {
      ItemStack lvt_2_1_ = ItemStack.EMPTY;

      for(int lvt_3_1_ = 0; lvt_3_1_ < p_77572_1_.getSizeInventory() && lvt_2_1_.isEmpty(); ++lvt_3_1_) {
         ItemStack lvt_4_1_ = p_77572_1_.getStackInSlot(lvt_3_1_);
         if (lvt_4_1_.getItem() == Items.FILLED_MAP) {
            lvt_2_1_ = lvt_4_1_;
         }
      }

      lvt_2_1_ = lvt_2_1_.copy();
      lvt_2_1_.setCount(1);
      lvt_2_1_.getOrCreateTag().putInt("map_scale_direction", 1);
      return lvt_2_1_;
   }

   public boolean isDynamic() {
      return true;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_MAPEXTENDING;
   }
}
