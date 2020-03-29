package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

public class ChangeDimensionTrigger extends AbstractCriterionTrigger<ChangeDimensionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("changed_dimension");

   public ResourceLocation getId() {
      return ID;
   }

   public ChangeDimensionTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DimensionType lvt_3_1_ = p_192166_1_.has("from") ? DimensionType.byName(new ResourceLocation(JSONUtils.getString(p_192166_1_, "from"))) : null;
      DimensionType lvt_4_1_ = p_192166_1_.has("to") ? DimensionType.byName(new ResourceLocation(JSONUtils.getString(p_192166_1_, "to"))) : null;
      return new ChangeDimensionTrigger.Instance(lvt_3_1_, lvt_4_1_);
   }

   public void trigger(ServerPlayerEntity p_193143_1_, DimensionType p_193143_2_, DimensionType p_193143_3_) {
      this.func_227070_a_(p_193143_1_.getAdvancements(), (p_226305_2_) -> {
         return p_226305_2_.test(p_193143_2_, p_193143_3_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      @Nullable
      private final DimensionType from;
      @Nullable
      private final DimensionType to;

      public Instance(@Nullable DimensionType p_i47475_1_, @Nullable DimensionType p_i47475_2_) {
         super(ChangeDimensionTrigger.ID);
         this.from = p_i47475_1_;
         this.to = p_i47475_2_;
      }

      public static ChangeDimensionTrigger.Instance changedDimensionTo(DimensionType p_203911_0_) {
         return new ChangeDimensionTrigger.Instance((DimensionType)null, p_203911_0_);
      }

      public boolean test(DimensionType p_193190_1_, DimensionType p_193190_2_) {
         if (this.from != null && this.from != p_193190_1_) {
            return false;
         } else {
            return this.to == null || this.to == p_193190_2_;
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.from != null) {
            lvt_1_1_.addProperty("from", DimensionType.getKey(this.from).toString());
         }

         if (this.to != null) {
            lvt_1_1_.addProperty("to", DimensionType.getKey(this.to).toString());
         }

         return lvt_1_1_;
      }
   }
}
