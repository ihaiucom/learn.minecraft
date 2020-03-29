package net.minecraft.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
   private final int field_222178_t;
   private final CookingRecipeSerializer.IFactory<T> field_222179_u;

   public CookingRecipeSerializer(CookingRecipeSerializer.IFactory<T> p_i50025_1_, int p_i50025_2_) {
      this.field_222178_t = p_i50025_2_;
      this.field_222179_u = p_i50025_1_;
   }

   public T read(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
      String s = JSONUtils.getString(p_199425_2_, "group", "");
      JsonElement jsonelement = JSONUtils.isJsonArray(p_199425_2_, "ingredient") ? JSONUtils.getJsonArray(p_199425_2_, "ingredient") : JSONUtils.getJsonObject(p_199425_2_, "ingredient");
      Ingredient ingredient = Ingredient.deserialize((JsonElement)jsonelement);
      if (!p_199425_2_.has("result")) {
         throw new JsonSyntaxException("Missing result, expected to find a string or object");
      } else {
         ItemStack itemstack;
         if (p_199425_2_.get("result").isJsonObject()) {
            itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(p_199425_2_, "result"));
         } else {
            String s1 = JSONUtils.getString(p_199425_2_, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            itemstack = new ItemStack((IItemProvider)Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> {
               return new IllegalStateException("Item: " + s1 + " does not exist");
            }));
         }

         float f = JSONUtils.getFloat(p_199425_2_, "experience", 0.0F);
         int i = JSONUtils.getInt(p_199425_2_, "cookingtime", this.field_222178_t);
         return this.field_222179_u.create(p_199425_1_, s, ingredient, itemstack, f, i);
      }
   }

   public T read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
      String s = p_199426_2_.readString(32767);
      Ingredient ingredient = Ingredient.read(p_199426_2_);
      ItemStack itemstack = p_199426_2_.readItemStack();
      float f = p_199426_2_.readFloat();
      int i = p_199426_2_.readVarInt();
      return this.field_222179_u.create(p_199426_1_, s, ingredient, itemstack, f, i);
   }

   public void write(PacketBuffer p_199427_1_, T p_199427_2_) {
      p_199427_1_.writeString(p_199427_2_.group);
      p_199427_2_.ingredient.write(p_199427_1_);
      p_199427_1_.writeItemStack(p_199427_2_.result);
      p_199427_1_.writeFloat(p_199427_2_.experience);
      p_199427_1_.writeVarInt(p_199427_2_.cookTime);
   }

   interface IFactory<T extends AbstractCookingRecipe> {
      T create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4, float var5, int var6);
   }
}
