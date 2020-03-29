package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RecipeUnlockedTrigger extends AbstractCriterionTrigger<RecipeUnlockedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

   public ResourceLocation getId() {
      return ID;
   }

   public RecipeUnlockedTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ResourceLocation lvt_3_1_ = new ResourceLocation(JSONUtils.getString(p_192166_1_, "recipe"));
      return new RecipeUnlockedTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_192225_1_, IRecipe<?> p_192225_2_) {
      this.func_227070_a_(p_192225_1_.getAdvancements(), (p_227018_1_) -> {
         return p_227018_1_.test(p_192225_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final ResourceLocation recipe;

      public Instance(ResourceLocation p_i48179_1_) {
         super(RecipeUnlockedTrigger.ID);
         this.recipe = p_i48179_1_;
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.addProperty("recipe", this.recipe.toString());
         return lvt_1_1_;
      }

      public boolean test(IRecipe<?> p_193215_1_) {
         return this.recipe.equals(p_193215_1_.getId());
      }
   }
}
