package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class KilledTrigger extends AbstractCriterionTrigger<KilledTrigger.Instance> {
   private final ResourceLocation id;

   public KilledTrigger(ResourceLocation p_i47433_1_) {
      this.id = p_i47433_1_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public KilledTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return new KilledTrigger.Instance(this.id, EntityPredicate.deserialize(p_192166_1_.get("entity")), DamageSourcePredicate.deserialize(p_192166_1_.get("killing_blow")));
   }

   public void trigger(ServerPlayerEntity p_192211_1_, Entity p_192211_2_, DamageSource p_192211_3_) {
      this.func_227070_a_(p_192211_1_.getAdvancements(), (p_226846_3_) -> {
         return p_226846_3_.test(p_192211_1_, p_192211_2_, p_192211_3_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate entity;
      private final DamageSourcePredicate killingBlow;

      public Instance(ResourceLocation p_i47454_1_, EntityPredicate p_i47454_2_, DamageSourcePredicate p_i47454_3_) {
         super(p_i47454_1_);
         this.entity = p_i47454_2_;
         this.killingBlow = p_i47454_3_;
      }

      public static KilledTrigger.Instance playerKilledEntity(EntityPredicate.Builder p_203928_0_) {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, p_203928_0_.build(), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.Instance playerKilledEntity() {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.Instance playerKilledEntity(EntityPredicate.Builder p_203929_0_, DamageSourcePredicate.Builder p_203929_1_) {
         return new KilledTrigger.Instance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, p_203929_0_.build(), p_203929_1_.build());
      }

      public static KilledTrigger.Instance entityKilledPlayer() {
         return new KilledTrigger.Instance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.ANY);
      }

      public boolean test(ServerPlayerEntity p_192270_1_, Entity p_192270_2_, DamageSource p_192270_3_) {
         return !this.killingBlow.test(p_192270_1_, p_192270_3_) ? false : this.entity.test(p_192270_1_, p_192270_2_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("entity", this.entity.serialize());
         lvt_1_1_.add("killing_blow", this.killingBlow.serialize());
         return lvt_1_1_;
      }
   }
}
