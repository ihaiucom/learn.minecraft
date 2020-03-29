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
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SingleItemRecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Advancement.Builder field_218652_d = Advancement.Builder.builder();
   private String field_218653_e;
   private final IRecipeSerializer<?> field_218654_f;

   public SingleItemRecipeBuilder(IRecipeSerializer<?> p_i50787_1_, Ingredient p_i50787_2_, IItemProvider p_i50787_3_, int p_i50787_4_) {
      this.field_218654_f = p_i50787_1_;
      this.result = p_i50787_3_.asItem();
      this.ingredient = p_i50787_2_;
      this.count = p_i50787_4_;
   }

   public static SingleItemRecipeBuilder func_218648_a(Ingredient p_218648_0_, IItemProvider p_218648_1_) {
      return new SingleItemRecipeBuilder(IRecipeSerializer.STONECUTTING, p_218648_0_, p_218648_1_, 1);
   }

   public static SingleItemRecipeBuilder func_218644_a(Ingredient p_218644_0_, IItemProvider p_218644_1_, int p_218644_2_) {
      return new SingleItemRecipeBuilder(IRecipeSerializer.STONECUTTING, p_218644_0_, p_218644_1_, p_218644_2_);
   }

   public SingleItemRecipeBuilder func_218643_a(String p_218643_1_, ICriterionInstance p_218643_2_) {
      this.field_218652_d.withCriterion(p_218643_1_, p_218643_2_);
      return this;
   }

   public void func_218645_a(Consumer<IFinishedRecipe> p_218645_1_, String p_218645_2_) {
      ResourceLocation lvt_3_1_ = Registry.ITEM.getKey(this.result);
      if ((new ResourceLocation(p_218645_2_)).equals(lvt_3_1_)) {
         throw new IllegalStateException("Single Item Recipe " + p_218645_2_ + " should remove its 'save' argument");
      } else {
         this.func_218647_a(p_218645_1_, new ResourceLocation(p_218645_2_));
      }
   }

   public void func_218647_a(Consumer<IFinishedRecipe> p_218647_1_, ResourceLocation p_218647_2_) {
      this.func_218646_a(p_218647_2_);
      this.field_218652_d.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", (ICriterionInstance)(new RecipeUnlockedTrigger.Instance(p_218647_2_))).withRewards(AdvancementRewards.Builder.recipe(p_218647_2_)).withRequirementsStrategy(IRequirementsStrategy.OR);
      p_218647_1_.accept(new SingleItemRecipeBuilder.Result(p_218647_2_, this.field_218654_f, this.field_218653_e == null ? "" : this.field_218653_e, this.ingredient, this.result, this.count, this.field_218652_d, new ResourceLocation(p_218647_2_.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + p_218647_2_.getPath())));
   }

   private void func_218646_a(ResourceLocation p_218646_1_) {
      if (this.field_218652_d.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_218646_1_);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation field_218620_a;
      private final String group;
      private final Ingredient field_218622_c;
      private final Item result;
      private final int count;
      private final Advancement.Builder field_218625_f;
      private final ResourceLocation field_218626_g;
      private final IRecipeSerializer<?> serializer;

      public Result(ResourceLocation p_i50574_1_, IRecipeSerializer<?> p_i50574_2_, String p_i50574_3_, Ingredient p_i50574_4_, Item p_i50574_5_, int p_i50574_6_, Advancement.Builder p_i50574_7_, ResourceLocation p_i50574_8_) {
         this.field_218620_a = p_i50574_1_;
         this.serializer = p_i50574_2_;
         this.group = p_i50574_3_;
         this.field_218622_c = p_i50574_4_;
         this.result = p_i50574_5_;
         this.count = p_i50574_6_;
         this.field_218625_f = p_i50574_7_;
         this.field_218626_g = p_i50574_8_;
      }

      public void serialize(JsonObject p_218610_1_) {
         if (!this.group.isEmpty()) {
            p_218610_1_.addProperty("group", this.group);
         }

         p_218610_1_.add("ingredient", this.field_218622_c.serialize());
         p_218610_1_.addProperty("result", Registry.ITEM.getKey(this.result).toString());
         p_218610_1_.addProperty("count", this.count);
      }

      public ResourceLocation getID() {
         return this.field_218620_a;
      }

      public IRecipeSerializer<?> getSerializer() {
         return this.serializer;
      }

      @Nullable
      public JsonObject getAdvancementJson() {
         return this.field_218625_f.serialize();
      }

      @Nullable
      public ResourceLocation getAdvancementID() {
         return this.field_218626_g;
      }
   }
}
