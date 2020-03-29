package net.minecraftforge.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ObjectHolder;

public class ConditionalRecipe {
   @ObjectHolder("forge:conditional")
   public static final IRecipeSerializer<IRecipe<?>> SERIALZIER = null;

   public static ConditionalRecipe.Builder builder() {
      return new ConditionalRecipe.Builder();
   }

   private static class Finished implements IFinishedRecipe {
      private final ResourceLocation id;
      private final List<ICondition[]> conditions;
      private final List<IFinishedRecipe> recipes;
      private final ResourceLocation advId;
      private final ConditionalAdvancement.Builder adv;

      private Finished(ResourceLocation id, List<ICondition[]> conditions, List<IFinishedRecipe> recipes, ResourceLocation advId, ConditionalAdvancement.Builder adv) {
         this.id = id;
         this.conditions = conditions;
         this.recipes = recipes;
         this.advId = advId;
         this.adv = adv;
      }

      public void serialize(JsonObject json) {
         JsonArray array = new JsonArray();
         json.add("recipes", array);

         for(int x = 0; x < this.conditions.size(); ++x) {
            JsonObject holder = new JsonObject();
            JsonArray conds = new JsonArray();
            ICondition[] var6 = (ICondition[])this.conditions.get(x);
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               ICondition c = var6[var8];
               conds.add(CraftingHelper.serialize(c));
            }

            holder.add("conditions", conds);
            holder.add("recipe", ((IFinishedRecipe)this.recipes.get(x)).getRecipeJson());
            array.add(holder);
         }

      }

      public ResourceLocation getID() {
         return this.id;
      }

      public IRecipeSerializer<?> getSerializer() {
         return ConditionalRecipe.SERIALZIER;
      }

      public JsonObject getAdvancementJson() {
         return this.adv == null ? null : this.adv.write();
      }

      public ResourceLocation getAdvancementID() {
         return this.advId;
      }

      // $FF: synthetic method
      Finished(ResourceLocation x0, List x1, List x2, ResourceLocation x3, ConditionalAdvancement.Builder x4, Object x5) {
         this(x0, x1, x2, x3, x4);
      }
   }

   public static class Builder {
      private List<ICondition[]> conditions = new ArrayList();
      private List<IFinishedRecipe> recipes = new ArrayList();
      private ResourceLocation advId;
      private ConditionalAdvancement.Builder adv;
      private List<ICondition> currentConditions = new ArrayList();

      public ConditionalRecipe.Builder addCondition(ICondition condition) {
         this.currentConditions.add(condition);
         return this;
      }

      public ConditionalRecipe.Builder addRecipe(Consumer<Consumer<IFinishedRecipe>> callable) {
         callable.accept(this::addRecipe);
         return this;
      }

      public ConditionalRecipe.Builder addRecipe(IFinishedRecipe recipe) {
         if (this.currentConditions.isEmpty()) {
            throw new IllegalStateException("Can not add a recipe with no conditions.");
         } else {
            this.conditions.add(this.currentConditions.toArray(new ICondition[this.currentConditions.size()]));
            this.recipes.add(recipe);
            this.currentConditions.clear();
            return this;
         }
      }

      public ConditionalRecipe.Builder setAdvancement(String namespace, String path, ConditionalAdvancement.Builder advancement) {
         return this.setAdvancement(new ResourceLocation(namespace, path), advancement);
      }

      public ConditionalRecipe.Builder setAdvancement(ResourceLocation id, ConditionalAdvancement.Builder advancement) {
         if (this.advId != null) {
            throw new IllegalStateException("Invalid ConditionalRecipeBuilder, Advancement already set");
         } else {
            this.advId = id;
            this.adv = advancement;
            return this;
         }
      }

      public void build(Consumer<IFinishedRecipe> consumer, String namespace, String path) {
         this.build(consumer, new ResourceLocation(namespace, path));
      }

      public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
         if (!this.currentConditions.isEmpty()) {
            throw new IllegalStateException("Invalid ConditionalRecipe builder, Orphaned conditions");
         } else if (this.recipes.isEmpty()) {
            throw new IllegalStateException("Invalid ConditionalRecipe builder, No recipes");
         } else {
            consumer.accept(new ConditionalRecipe.Finished(id, this.conditions, this.recipes, this.advId, this.adv));
         }
      }
   }

   public static class Serializer<T extends IRecipe<?>> implements IRecipeSerializer<T> {
      private ResourceLocation name;

      public IRecipeSerializer<?> setRegistryName(ResourceLocation name) {
         this.name = name;
         return this;
      }

      public ResourceLocation getRegistryName() {
         return this.name;
      }

      public Class<IRecipeSerializer<?>> getRegistryType() {
         return castClass(IRecipeSerializer.class);
      }

      private static <G> Class<G> castClass(Class<?> cls) {
         return cls;
      }

      public T read(ResourceLocation recipeId, JsonObject json) {
         JsonArray items = JSONUtils.getJsonArray(json, "recipes");
         int idx = 0;

         for(Iterator var5 = items.iterator(); var5.hasNext(); ++idx) {
            JsonElement ele = (JsonElement)var5.next();
            if (!ele.isJsonObject()) {
               throw new JsonSyntaxException("Invalid recipes entry at index " + idx + " Must be JsonObject");
            }

            if (CraftingHelper.processConditions(JSONUtils.getJsonArray(ele.getAsJsonObject(), "conditions"))) {
               return RecipeManager.deserializeRecipe(recipeId, JSONUtils.getJsonObject(ele.getAsJsonObject(), "recipe"));
            }
         }

         return null;
      }

      public T read(ResourceLocation recipeId, PacketBuffer buffer) {
         return null;
      }

      public void write(PacketBuffer buffer, T recipe) {
      }
   }
}
