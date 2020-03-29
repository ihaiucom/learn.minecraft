package net.minecraft.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShapelessRecipe implements ICraftingRecipe {
   private final ResourceLocation id;
   private final String group;
   private final ItemStack recipeOutput;
   private final NonNullList<Ingredient> recipeItems;
   private final boolean isSimple;

   public ShapelessRecipe(ResourceLocation p_i48161_1_, String p_i48161_2_, ItemStack p_i48161_3_, NonNullList<Ingredient> p_i48161_4_) {
      this.id = p_i48161_1_;
      this.group = p_i48161_2_;
      this.recipeOutput = p_i48161_3_;
      this.recipeItems = p_i48161_4_;
      this.isSimple = p_i48161_4_.stream().allMatch(Ingredient::isSimple);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SHAPELESS;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.recipeItems;
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
      List<ItemStack> inputs = new ArrayList();
      int i = 0;

      for(int j = 0; j < p_77569_1_.getSizeInventory(); ++j) {
         ItemStack itemstack = p_77569_1_.getStackInSlot(j);
         if (!itemstack.isEmpty()) {
            ++i;
            if (this.isSimple) {
               recipeitemhelper.func_221264_a(itemstack, 1);
            } else {
               inputs.add(itemstack);
            }
         }
      }

      boolean var10000;
      label43: {
         if (i == this.recipeItems.size()) {
            if (this.isSimple) {
               if (recipeitemhelper.canCraft(this, (IntList)null)) {
                  break label43;
               }
            } else if (RecipeMatcher.findMatches(inputs, this.recipeItems) != null) {
               break label43;
            }
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   public ItemStack getCraftingResult(CraftingInventory p_77572_1_) {
      return this.recipeOutput.copy();
   }

   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= this.recipeItems.size();
   }

   public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessRecipe> {
      private static final ResourceLocation NAME = new ResourceLocation("minecraft", "crafting_shapeless");

      public ShapelessRecipe read(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
         String s = JSONUtils.getString(p_199425_2_, "group", "");
         NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(p_199425_2_, "ingredients"));
         if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (nonnulllist.size() > ShapedRecipe.MAX_WIDTH * ShapedRecipe.MAX_HEIGHT) {
            throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + ShapedRecipe.MAX_WIDTH * ShapedRecipe.MAX_HEIGHT);
         } else {
            ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(p_199425_2_, "result"));
            return new ShapelessRecipe(p_199425_1_, s, itemstack, nonnulllist);
         }
      }

      private static NonNullList<Ingredient> readIngredients(JsonArray p_199568_0_) {
         NonNullList<Ingredient> nonnulllist = NonNullList.create();

         for(int i = 0; i < p_199568_0_.size(); ++i) {
            Ingredient ingredient = Ingredient.deserialize(p_199568_0_.get(i));
            if (!ingredient.hasNoMatchingItems()) {
               nonnulllist.add(ingredient);
            }
         }

         return nonnulllist;
      }

      public ShapelessRecipe read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
         String s = p_199426_2_.readString(32767);
         int i = p_199426_2_.readVarInt();
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

         for(int j = 0; j < nonnulllist.size(); ++j) {
            nonnulllist.set(j, Ingredient.read(p_199426_2_));
         }

         ItemStack itemstack = p_199426_2_.readItemStack();
         return new ShapelessRecipe(p_199426_1_, s, itemstack, nonnulllist);
      }

      public void write(PacketBuffer p_199427_1_, ShapelessRecipe p_199427_2_) {
         p_199427_1_.writeString(p_199427_2_.group);
         p_199427_1_.writeVarInt(p_199427_2_.recipeItems.size());
         Iterator var3 = p_199427_2_.recipeItems.iterator();

         while(var3.hasNext()) {
            Ingredient ingredient = (Ingredient)var3.next();
            ingredient.write(p_199427_1_);
         }

         p_199427_1_.writeItemStack(p_199427_2_.recipeOutput);
      }
   }
}
