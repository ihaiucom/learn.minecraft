package net.minecraft.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ShapedRecipe implements ICraftingRecipe, IShapedRecipe<CraftingInventory> {
   static int MAX_WIDTH = 3;
   static int MAX_HEIGHT = 3;
   private final int recipeWidth;
   private final int recipeHeight;
   private final NonNullList<Ingredient> recipeItems;
   private final ItemStack recipeOutput;
   private final ResourceLocation id;
   private final String group;

   public static void setCraftingSize(int p_setCraftingSize_0_, int p_setCraftingSize_1_) {
      if (MAX_WIDTH < p_setCraftingSize_0_) {
         MAX_WIDTH = p_setCraftingSize_0_;
      }

      if (MAX_HEIGHT < p_setCraftingSize_1_) {
         MAX_HEIGHT = p_setCraftingSize_1_;
      }

   }

   public ShapedRecipe(ResourceLocation p_i48162_1_, String p_i48162_2_, int p_i48162_3_, int p_i48162_4_, NonNullList<Ingredient> p_i48162_5_, ItemStack p_i48162_6_) {
      this.id = p_i48162_1_;
      this.group = p_i48162_2_;
      this.recipeWidth = p_i48162_3_;
      this.recipeHeight = p_i48162_4_;
      this.recipeItems = p_i48162_5_;
      this.recipeOutput = p_i48162_6_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SHAPED;
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

   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ >= this.recipeWidth && p_194133_2_ >= this.recipeHeight;
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      for(int i = 0; i <= p_77569_1_.getWidth() - this.recipeWidth; ++i) {
         for(int j = 0; j <= p_77569_1_.getHeight() - this.recipeHeight; ++j) {
            if (this.checkMatch(p_77569_1_, i, j, true)) {
               return true;
            }

            if (this.checkMatch(p_77569_1_, i, j, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean checkMatch(CraftingInventory p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
      for(int i = 0; i < p_77573_1_.getWidth(); ++i) {
         for(int j = 0; j < p_77573_1_.getHeight(); ++j) {
            int k = i - p_77573_2_;
            int l = j - p_77573_3_;
            Ingredient ingredient = Ingredient.EMPTY;
            if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
               if (p_77573_4_) {
                  ingredient = (Ingredient)this.recipeItems.get(this.recipeWidth - k - 1 + l * this.recipeWidth);
               } else {
                  ingredient = (Ingredient)this.recipeItems.get(k + l * this.recipeWidth);
               }
            }

            if (!ingredient.test(p_77573_1_.getStackInSlot(i + j * p_77573_1_.getWidth()))) {
               return false;
            }
         }
      }

      return true;
   }

   public ItemStack getCraftingResult(CraftingInventory p_77572_1_) {
      return this.getRecipeOutput().copy();
   }

   public int getWidth() {
      return this.recipeWidth;
   }

   public int getRecipeWidth() {
      return this.getWidth();
   }

   public int getHeight() {
      return this.recipeHeight;
   }

   public int getRecipeHeight() {
      return this.getHeight();
   }

   private static NonNullList<Ingredient> deserializeIngredients(String[] p_192402_0_, Map<String, Ingredient> p_192402_1_, int p_192402_2_, int p_192402_3_) {
      NonNullList<Ingredient> nonnulllist = NonNullList.withSize(p_192402_2_ * p_192402_3_, Ingredient.EMPTY);
      Set<String> set = Sets.newHashSet(p_192402_1_.keySet());
      set.remove(" ");

      for(int i = 0; i < p_192402_0_.length; ++i) {
         for(int j = 0; j < p_192402_0_[i].length(); ++j) {
            String s = p_192402_0_[i].substring(j, j + 1);
            Ingredient ingredient = (Ingredient)p_192402_1_.get(s);
            if (ingredient == null) {
               throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
            }

            set.remove(s);
            nonnulllist.set(j + p_192402_2_ * i, ingredient);
         }
      }

      if (!set.isEmpty()) {
         throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
      } else {
         return nonnulllist;
      }
   }

   @VisibleForTesting
   static String[] shrink(String... p_194134_0_) {
      int i = Integer.MAX_VALUE;
      int j = 0;
      int k = 0;
      int l = 0;

      for(int i1 = 0; i1 < p_194134_0_.length; ++i1) {
         String s = p_194134_0_[i1];
         i = Math.min(i, firstNonSpace(s));
         int j1 = lastNonSpace(s);
         j = Math.max(j, j1);
         if (j1 < 0) {
            if (k == i1) {
               ++k;
            }

            ++l;
         } else {
            l = 0;
         }
      }

      if (p_194134_0_.length == l) {
         return new String[0];
      } else {
         String[] astring = new String[p_194134_0_.length - l - k];

         for(int k1 = 0; k1 < astring.length; ++k1) {
            astring[k1] = p_194134_0_[k1 + k].substring(i, j + 1);
         }

         return astring;
      }
   }

   private static int firstNonSpace(String p_194135_0_) {
      int i;
      for(i = 0; i < p_194135_0_.length() && p_194135_0_.charAt(i) == ' '; ++i) {
      }

      return i;
   }

   private static int lastNonSpace(String p_194136_0_) {
      int i;
      for(i = p_194136_0_.length() - 1; i >= 0 && p_194136_0_.charAt(i) == ' '; --i) {
      }

      return i;
   }

   private static String[] patternFromJson(JsonArray p_192407_0_) {
      String[] astring = new String[p_192407_0_.size()];
      if (astring.length > MAX_HEIGHT) {
         throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
      } else if (astring.length == 0) {
         throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
      } else {
         for(int i = 0; i < astring.length; ++i) {
            String s = JSONUtils.getString(p_192407_0_.get(i), "pattern[" + i + "]");
            if (s.length() > MAX_WIDTH) {
               throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
            }

            if (i > 0 && astring[0].length() != s.length()) {
               throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }

            astring[i] = s;
         }

         return astring;
      }
   }

   private static Map<String, Ingredient> deserializeKey(JsonObject p_192408_0_) {
      Map<String, Ingredient> map = Maps.newHashMap();
      Iterator var2 = p_192408_0_.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, JsonElement> entry = (Entry)var2.next();
         if (((String)entry.getKey()).length() != 1) {
            throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
         }

         if (" ".equals(entry.getKey())) {
            throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
         }

         map.put(entry.getKey(), Ingredient.deserialize((JsonElement)entry.getValue()));
      }

      map.put(" ", Ingredient.EMPTY);
      return map;
   }

   public static ItemStack deserializeItem(JsonObject p_199798_0_) {
      String s = JSONUtils.getString(p_199798_0_, "item");
      Item var10000 = (Item)Registry.ITEM.getValue(new ResourceLocation(s)).orElseThrow(() -> {
         return new JsonSyntaxException("Unknown item '" + s + "'");
      });
      if (p_199798_0_.has("data")) {
         throw new JsonParseException("Disallowed data tag found");
      } else {
         int i = JSONUtils.getInt(p_199798_0_, "count", 1);
         return CraftingHelper.getItemStack(p_199798_0_, true);
      }
   }

   public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedRecipe> {
      private static final ResourceLocation NAME = new ResourceLocation("minecraft", "crafting_shaped");

      public ShapedRecipe read(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
         String s = JSONUtils.getString(p_199425_2_, "group", "");
         Map<String, Ingredient> map = ShapedRecipe.deserializeKey(JSONUtils.getJsonObject(p_199425_2_, "key"));
         String[] astring = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(JSONUtils.getJsonArray(p_199425_2_, "pattern")));
         int i = astring[0].length();
         int j = astring.length;
         NonNullList<Ingredient> nonnulllist = ShapedRecipe.deserializeIngredients(astring, map, i, j);
         ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(p_199425_2_, "result"));
         return new ShapedRecipe(p_199425_1_, s, i, j, nonnulllist, itemstack);
      }

      public ShapedRecipe read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
         int i = p_199426_2_.readVarInt();
         int j = p_199426_2_.readVarInt();
         String s = p_199426_2_.readString(32767);
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

         for(int k = 0; k < nonnulllist.size(); ++k) {
            nonnulllist.set(k, Ingredient.read(p_199426_2_));
         }

         ItemStack itemstack = p_199426_2_.readItemStack();
         return new ShapedRecipe(p_199426_1_, s, i, j, nonnulllist, itemstack);
      }

      public void write(PacketBuffer p_199427_1_, ShapedRecipe p_199427_2_) {
         p_199427_1_.writeVarInt(p_199427_2_.recipeWidth);
         p_199427_1_.writeVarInt(p_199427_2_.recipeHeight);
         p_199427_1_.writeString(p_199427_2_.group);
         Iterator var3 = p_199427_2_.recipeItems.iterator();

         while(var3.hasNext()) {
            Ingredient ingredient = (Ingredient)var3.next();
            ingredient.write(p_199427_1_);
         }

         p_199427_1_.writeItemStack(p_199427_2_.recipeOutput);
      }
   }
}
