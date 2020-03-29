package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class EffectsChangedTrigger extends AbstractCriterionTrigger<EffectsChangedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public EffectsChangedTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      MobEffectsPredicate lvt_3_1_ = MobEffectsPredicate.deserialize(p_192166_1_.get("effects"));
      return new EffectsChangedTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_193153_1_) {
      this.func_227070_a_(p_193153_1_.getAdvancements(), (p_226524_1_) -> {
         return p_226524_1_.test(p_193153_1_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final MobEffectsPredicate effects;

      public Instance(MobEffectsPredicate p_i47545_1_) {
         super(EffectsChangedTrigger.ID);
         this.effects = p_i47545_1_;
      }

      public static EffectsChangedTrigger.Instance forEffect(MobEffectsPredicate p_203917_0_) {
         return new EffectsChangedTrigger.Instance(p_203917_0_);
      }

      public boolean test(ServerPlayerEntity p_193195_1_) {
         return this.effects.test((LivingEntity)p_193195_1_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("effects", this.effects.serialize());
         return lvt_1_1_;
      }
   }
}
