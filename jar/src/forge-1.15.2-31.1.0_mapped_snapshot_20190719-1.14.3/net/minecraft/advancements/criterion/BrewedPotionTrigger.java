package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BrewedPotionTrigger extends AbstractCriterionTrigger<BrewedPotionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("brewed_potion");

   public ResourceLocation getId() {
      return ID;
   }

   public BrewedPotionTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Potion lvt_3_1_ = null;
      if (p_192166_1_.has("potion")) {
         ResourceLocation lvt_4_1_ = new ResourceLocation(JSONUtils.getString(p_192166_1_, "potion"));
         lvt_3_1_ = (Potion)Registry.POTION.getValue(lvt_4_1_).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion '" + lvt_4_1_ + "'");
         });
      }

      return new BrewedPotionTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_192173_1_, Potion p_192173_2_) {
      this.func_227070_a_(p_192173_1_.getAdvancements(), (p_226301_1_) -> {
         return p_226301_1_.test(p_192173_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final Potion potion;

      public Instance(@Nullable Potion p_i47398_1_) {
         super(BrewedPotionTrigger.ID);
         this.potion = p_i47398_1_;
      }

      public static BrewedPotionTrigger.Instance brewedPotion() {
         return new BrewedPotionTrigger.Instance((Potion)null);
      }

      public boolean test(Potion p_192250_1_) {
         return this.potion == null || this.potion == p_192250_1_;
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.potion != null) {
            lvt_1_1_.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return lvt_1_1_;
      }
   }
}
