package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapelessRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List<Ingredient> ingredients = Lists.newArrayList();
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   private String group;

   public ShapelessRecipeBuilder(IItemProvider p_i48260_1_, int p_i48260_2_) {
      this.result = p_i48260_1_.asItem();
      this.count = p_i48260_2_;
   }

   public static ShapelessRecipeBuilder shapelessRecipe(IItemProvider p_200486_0_) {
      return new ShapelessRecipeBuilder(p_200486_0_, 1);
   }

   public static ShapelessRecipeBuilder shapelessRecipe(IItemProvider p_200488_0_, int p_200488_1_) {
      return new ShapelessRecipeBuilder(p_200488_0_, p_200488_1_);
   }

   public ShapelessRecipeBuilder addIngredient(Tag<Item> p_203221_1_) {
      return this.addIngredient(Ingredient.fromTag(p_203221_1_));
   }

   public ShapelessRecipeBuilder addIngredient(IItemProvider p_200487_1_) {
      return this.addIngredient((IItemProvider)p_200487_1_, 1);
   }

   public ShapelessRecipeBuilder addIngredient(IItemProvider p_200491_1_, int p_200491_2_) {
      for(int lvt_3_1_ = 0; lvt_3_1_ < p_200491_2_; ++lvt_3_1_) {
         this.addIngredient(Ingredient.fromItems(p_200491_1_));
      }

      return this;
   }

   public ShapelessRecipeBuilder addIngredient(Ingredient p_200489_1_) {
      return this.addIngredient((Ingredient)p_200489_1_, 1);
   }

   public ShapelessRecipeBuilder addIngredient(Ingredient p_200492_1_, int p_200492_2_) {
      for(int lvt_3_1_ = 0; lvt_3_1_ < p_200492_2_; ++lvt_3_1_) {
         this.ingredients.add(p_200492_1_);
      }

      return this;
   }

   public ShapelessRecipeBuilder addCriterion(String p_200483_1_, ICriterionInstance p_200483_2_) {
      this.advancementBuilder.withCriterion(p_200483_1_, p_200483_2_);
      return this;
   }

   public ShapelessRecipeBuilder setGroup(String p_200490_1_) {
      this.group = p_200490_1_;
      return this;
   }

   public void build(Consumer<IFinishedRecipe> p_200482_1_) {
      this.build(p_200482_1_, Registry.ITEM.getKey(this.result));
   }

   public void build(Consumer<IFinishedRecipe> p_200484_1_, String p_200484_2_) {
      ResourceLocation lvt_3_1_ = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(p_200484_2_)).equals(lvt_3_1_)) {
         throw new IllegalStateException("Shapeless Recipe " + p_200484_2_ + " should remove its 'save' argument");
      } else {
         this.build(p_200484_1_, new ResourceLocation(p_200484_2_));
      }
   }

   public void build(Consumer<IFinishedRecipe> p_200485_1_, ResourceLocation p_200485_2_) {
      this.validate(p_200485_2_);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", (ICriterionInstance)(new RecipeUnlockedTrigger.Instance(p_200485_2_))).withRewards(AdvancementRewards.Builder.recipe(p_200485_2_)).withRequirementsStrategy(IRequirementsStrategy.OR);
      p_200485_1_.accept(new ShapelessRecipeBuilder.Result(p_200485_2_, this.result, this.count, this.group == null ? "" : this.group, this.ingredients, this.advancementBuilder, new ResourceLocation(p_200485_2_.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + p_200485_2_.getPath())));
   }

   private void validate(ResourceLocation p_200481_1_) {
      if (this.advancementBuilder.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_200481_1_);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation p_i48268_1_, Item p_i48268_2_, int p_i48268_3_, String p_i48268_4_, List<Ingredient> p_i48268_5_, Advancement.Builder p_i48268_6_, ResourceLocation p_i48268_7_) {
         this.id = p_i48268_1_;
         this.result = p_i48268_2_;
         this.count = p_i48268_3_;
         this.group = p_i48268_4_;
         this.ingredients = p_i48268_5_;
         this.advancementBuilder = p_i48268_6_;
         this.advancementId = p_i48268_7_;
      }

      public void serialize(JsonObject p_218610_1_) {
         if (!this.group.isEmpty()) {
            p_218610_1_.addProperty("group", this.group);
         }

         JsonArray lvt_2_1_ = new JsonArray();
         Iterator var3 = this.ingredients.iterator();

         while(var3.hasNext()) {
            Ingredient lvt_4_1_ = (Ingredient)var3.next();
            lvt_2_1_.add(lvt_4_1_.serialize());
         }

         p_218610_1_.add("ingredients", lvt_2_1_);
         JsonObject lvt_3_1_ = new JsonObject();
         lvt_3_1_.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         if (this.count > 1) {
            lvt_3_1_.addProperty("count", this.count);
         }

         p_218610_1_.add("result", lvt_3_1_);
      }

      public IRecipeSerializer<?> getSerializer() {
         return IRecipeSerializer.CRAFTING_SHAPELESS;
      }

      public ResourceLocation getID() {
         return this.id;
      }

      @Nullable
      public JsonObject getAdvancementJson() {
         return this.advancementBuilder.serialize();
      }

      @Nullable
      public ResourceLocation getAdvancementID() {
         return this.advancementId;
      }
   }
}
