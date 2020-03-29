package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JSONUtils;

public class DamagePredicate {
   public static final DamagePredicate ANY = DamagePredicate.Builder.create().build();
   private final MinMaxBounds.FloatBound dealt;
   private final MinMaxBounds.FloatBound taken;
   private final EntityPredicate sourceEntity;
   private final Boolean blocked;
   private final DamageSourcePredicate type;

   public DamagePredicate() {
      this.dealt = MinMaxBounds.FloatBound.UNBOUNDED;
      this.taken = MinMaxBounds.FloatBound.UNBOUNDED;
      this.sourceEntity = EntityPredicate.ANY;
      this.blocked = null;
      this.type = DamageSourcePredicate.ANY;
   }

   public DamagePredicate(MinMaxBounds.FloatBound p_i49725_1_, MinMaxBounds.FloatBound p_i49725_2_, EntityPredicate p_i49725_3_, @Nullable Boolean p_i49725_4_, DamageSourcePredicate p_i49725_5_) {
      this.dealt = p_i49725_1_;
      this.taken = p_i49725_2_;
      this.sourceEntity = p_i49725_3_;
      this.blocked = p_i49725_4_;
      this.type = p_i49725_5_;
   }

   public boolean test(ServerPlayerEntity p_192365_1_, DamageSource p_192365_2_, float p_192365_3_, float p_192365_4_, boolean p_192365_5_) {
      if (this == ANY) {
         return true;
      } else if (!this.dealt.test(p_192365_3_)) {
         return false;
      } else if (!this.taken.test(p_192365_4_)) {
         return false;
      } else if (!this.sourceEntity.test(p_192365_1_, p_192365_2_.getTrueSource())) {
         return false;
      } else if (this.blocked != null && this.blocked != p_192365_5_) {
         return false;
      } else {
         return this.type.test(p_192365_1_, p_192365_2_);
      }
   }

   public static DamagePredicate deserialize(@Nullable JsonElement p_192364_0_) {
      if (p_192364_0_ != null && !p_192364_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_192364_0_, "damage");
         MinMaxBounds.FloatBound lvt_2_1_ = MinMaxBounds.FloatBound.fromJson(lvt_1_1_.get("dealt"));
         MinMaxBounds.FloatBound lvt_3_1_ = MinMaxBounds.FloatBound.fromJson(lvt_1_1_.get("taken"));
         Boolean lvt_4_1_ = lvt_1_1_.has("blocked") ? JSONUtils.getBoolean(lvt_1_1_, "blocked") : null;
         EntityPredicate lvt_5_1_ = EntityPredicate.deserialize(lvt_1_1_.get("source_entity"));
         DamageSourcePredicate lvt_6_1_ = DamageSourcePredicate.deserialize(lvt_1_1_.get("type"));
         return new DamagePredicate(lvt_2_1_, lvt_3_1_, lvt_5_1_, lvt_4_1_, lvt_6_1_);
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("dealt", this.dealt.serialize());
         lvt_1_1_.add("taken", this.taken.serialize());
         lvt_1_1_.add("source_entity", this.sourceEntity.serialize());
         lvt_1_1_.add("type", this.type.serialize());
         if (this.blocked != null) {
            lvt_1_1_.addProperty("blocked", this.blocked);
         }

         return lvt_1_1_;
      }
   }

   public static class Builder {
      private MinMaxBounds.FloatBound dealt;
      private MinMaxBounds.FloatBound taken;
      private EntityPredicate sourceEntity;
      private Boolean blocked;
      private DamageSourcePredicate type;

      public Builder() {
         this.dealt = MinMaxBounds.FloatBound.UNBOUNDED;
         this.taken = MinMaxBounds.FloatBound.UNBOUNDED;
         this.sourceEntity = EntityPredicate.ANY;
         this.type = DamageSourcePredicate.ANY;
      }

      public static DamagePredicate.Builder create() {
         return new DamagePredicate.Builder();
      }

      public DamagePredicate.Builder blocked(Boolean p_203968_1_) {
         this.blocked = p_203968_1_;
         return this;
      }

      public DamagePredicate.Builder type(DamageSourcePredicate.Builder p_203969_1_) {
         this.type = p_203969_1_.build();
         return this;
      }

      public DamagePredicate build() {
         return new DamagePredicate(this.dealt, this.taken, this.sourceEntity, this.blocked, this.type);
      }
   }
}
