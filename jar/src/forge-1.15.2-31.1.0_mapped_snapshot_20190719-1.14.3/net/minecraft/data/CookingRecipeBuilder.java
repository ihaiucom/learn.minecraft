package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CookingRecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
   private String group;
   private final CookingRecipeSerializer<?> recipeSerializer;

   private CookingRecipeBuilder(IItemProvider p_i50788_1_, Ingredient p_i50788_2_, float p_i50788_3_, int p_i50788_4_, CookingRecipeSerializer<?> p_i50788_5_) {
      this.result = p_i50788_1_.asItem();
      this.ingredient = p_i50788_2_;
      this.experience = p_i50788_3_;
      this.cookingTime = p_i50788_4_;
      this.recipeSerializer = p_i50788_5_;
   }

   public static CookingRecipeBuilder cookingRecipe(Ingredient p_218631_0_, IItemProvider p_218631_1_, float p_218631_2_, int p_218631_3_, CookingRecipeSerializer<?> p_218631_4_) {
      return new CookingRecipeBuilder(p_218631_1_, p_218631_0_, p_218631_2_, p_218631_3_, p_218631_4_);
   }

   public static CookingRecipeBuilder blastingRecipe(Ingredient p_218633_0_, IItemProvider p_218633_1_, float p_218633_2_, int p_218633_3_) {
      return cookingRecipe(p_218633_0_, p_218633_1_, p_218633_2_, p_218633_3_, IRecipeSerializer.BLASTING);
   }

   public static CookingRecipeBuilder smeltingRecipe(Ingredient p_218629_0_, IItemProvider p_218629_1_, float p_218629_2_, int p_218629_3_) {
      return cookingRecipe(p_218629_0_, p_218629_1_, p_218629_2_, p_218629_3_, IRecipeSerializer.SMELTING);
   }

   public CookingRecipeBuilder addCriterion(String p_218628_1_, ICriterionInstance p_218628_2_) {
      this.advancementBuilder.withCriterion(p_218628_1_, p_218628_2_);
      return this;
   }

   public void build(Consumer<IFinishedRecipe> p_218630_1_) {
      this.build(p_218630_1_, Registry.ITEM.getKey(this.result));
   }

   public void build(Consumer<IFinishedRecipe> p_218632_1_, String p_218632_2_) {
      ResourceLocation lvt_3_1_ = Registry.ITEM.getKey(this.result);
      ResourceLocation lvt_4_1_ = new ResourceLocation(p_218632_2_);
      if (lvt_4_1_.equals(lvt_3_1_)) {
         throw new IllegalStateException("Recipe " + lvt_4_1_ + " should remove its 'save' argument");
      } else {
         this.build(p_218632_1_, lvt_4_1_);
      }
   }

   public void build(Consumer<IFinishedRecipe> p_218635_1_, ResourceLocation p_218635_2_) {
      this.validate(p_218635_2_);
      this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", (ICriterionInstance)(new RecipeUnlockedTrigger.Instance(p_218635_2_))).withRewards(AdvancementRewards.Builder.recipe(p_218635_2_)).withRequirementsStrategy(IRequirementsStrategy.OR);
      p_218635_1_.accept(new CookingRecipeBuilder.Result(p_218635_2_, this.group == null ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.advancementBuilder, new ResourceLocation(p_218635_2_.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + p_218635_2_.getPath()), this.recipeSerializer));
   }

   private void validate(ResourceLocation p_218634_1_) {
      if (this.advancementBuilder.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_218634_1_);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final Ingredient ingredient;
      private final Item result;
      private final float experience;
      private final int cookingTime;
      private final Advancement.Builder advancementBuilder;
      private final ResourceLocation advancementId;
      private final IRecipeSerializer<? extends AbstractCookingRecipe> serializer;

      public Result(ResourceLocation p_i50605_1_, String p_i50605_2_, Ingredient p_i50605_3_, Item p_i50605_4_, float p_i50605_5_, int p_i50605_6_, Advancement.Builder p_i50605_7_, ResourceLocation p_i50605_8_, IRecipeSerializer<? extends AbstractCookingRecipe> p_i50605_9_) {
         this.id = p_i50605_1_;
         this.group = p_i50605_2_;
         this.ingredient = p_i50605_3_;
         this.result = p_i50605_4_;
         this.experience = p_i50605_5_;
         this.cookingTime = p_i50605_6_;
         this.advancementBuilder = p_i50605_7_;
         this.advancementId = p_i50605_8_;
         this.serializer = p_i50605_9_;
      }

      public void serialize(JsonObject p_218610_1_) {
         if (!this.group.isEmpty()) {
            p_218610_1_.addProperty("group", this.group);
         }

         p_218610_1_.add("ingredient", this.ingredient.serialize());
         p_218610_1_.addProperty("result", Registry.ITEM.getKey(this.result).toString());
         p_218610_1_.addProperty("experience", this.experience);
         p_218610_1_.addProperty("cookingtime", this.cookingTime);
      }

      public IRecipeSerializer<?> getSerializer() {
         return this.serializer;
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
