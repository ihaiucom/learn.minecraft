package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class SingleItemRecipe implements IRecipe<IInventory> {
   protected final Ingredient ingredient;
   protected final ItemStack result;
   private final IRecipeType<?> type;
   private final IRecipeSerializer<?> serializer;
   protected final ResourceLocation id;
   protected final String group;

   public SingleItemRecipe(IRecipeType<?> p_i50023_1_, IRecipeSerializer<?> p_i50023_2_, ResourceLocation p_i50023_3_, String p_i50023_4_, Ingredient p_i50023_5_, ItemStack p_i50023_6_) {
      this.type = p_i50023_1_;
      this.serializer = p_i50023_2_;
      this.id = p_i50023_3_;
      this.group = p_i50023_4_;
      this.ingredient = p_i50023_5_;
      this.result = p_i50023_6_;
   }

   public IRecipeType<?> getType() {
      return this.type;
   }

   public IRecipeSerializer<?> getSerializer() {
      return this.serializer;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getRecipeOutput() {
      return this.result;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.ingredient);
      return nonnulllist;
   }

   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return true;
   }

   public ItemStack getCraftingResult(IInventory p_77572_1_) {
      return this.result.copy();
   }

   public static class Serializer<T extends SingleItemRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
      final SingleItemRecipe.Serializer.IRecipeFactory<T> factory;

      protected Serializer(SingleItemRecipe.Serializer.IRecipeFactory<T> p_i50146_1_) {
         this.factory = p_i50146_1_;
      }

      public T read(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
         String s = JSONUtils.getString(p_199425_2_, "group", "");
         Ingredient ingredient;
         if (JSONUtils.isJsonArray(p_199425_2_, "ingredient")) {
            ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(p_199425_2_, "ingredient"));
         } else {
            ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(p_199425_2_, "ingredient"));
         }

         String s1 = JSONUtils.getString(p_199425_2_, "result");
         int i = JSONUtils.getInt(p_199425_2_, "count");
         ItemStack itemstack = new ItemStack((IItemProvider)Registry.ITEM.getOrDefault(new ResourceLocation(s1)), i);
         return this.factory.create(p_199425_1_, s, ingredient, itemstack);
      }

      public T read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
         String s = p_199426_2_.readString(32767);
         Ingredient ingredient = Ingredient.read(p_199426_2_);
         ItemStack itemstack = p_199426_2_.readItemStack();
         return this.factory.create(p_199426_1_, s, ingredient, itemstack);
      }

      public void write(PacketBuffer p_199427_1_, T p_199427_2_) {
         p_199427_1_.writeString(p_199427_2_.group);
         p_199427_2_.ingredient.write(p_199427_1_);
         p_199427_1_.writeItemStack(p_199427_2_.result);
      }

      interface IRecipeFactory<T extends SingleItemRecipe> {
         T create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4);
      }
   }
}
