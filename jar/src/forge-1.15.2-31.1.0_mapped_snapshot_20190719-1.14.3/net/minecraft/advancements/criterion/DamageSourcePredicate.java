package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.damageType().build();
   private final Boolean isProjectile;
   private final Boolean isExplosion;
   private final Boolean bypassesArmor;
   private final Boolean bypassesInvulnerability;
   private final Boolean bypassesMagic;
   private final Boolean isFire;
   private final Boolean isMagic;
   private final Boolean field_217953_i;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(@Nullable Boolean p_i50810_1_, @Nullable Boolean p_i50810_2_, @Nullable Boolean p_i50810_3_, @Nullable Boolean p_i50810_4_, @Nullable Boolean p_i50810_5_, @Nullable Boolean p_i50810_6_, @Nullable Boolean p_i50810_7_, @Nullable Boolean p_i50810_8_, EntityPredicate p_i50810_9_, EntityPredicate p_i50810_10_) {
      this.isProjectile = p_i50810_1_;
      this.isExplosion = p_i50810_2_;
      this.bypassesArmor = p_i50810_3_;
      this.bypassesInvulnerability = p_i50810_4_;
      this.bypassesMagic = p_i50810_5_;
      this.isFire = p_i50810_6_;
      this.isMagic = p_i50810_7_;
      this.field_217953_i = p_i50810_8_;
      this.directEntity = p_i50810_9_;
      this.sourceEntity = p_i50810_10_;
   }

   public boolean test(ServerPlayerEntity p_193418_1_, DamageSource p_193418_2_) {
      return this.func_217952_a(p_193418_1_.getServerWorld(), p_193418_1_.getPositionVec(), p_193418_2_);
   }

   public boolean func_217952_a(ServerWorld p_217952_1_, Vec3d p_217952_2_, DamageSource p_217952_3_) {
      if (this == ANY) {
         return true;
      } else if (this.isProjectile != null && this.isProjectile != p_217952_3_.isProjectile()) {
         return false;
      } else if (this.isExplosion != null && this.isExplosion != p_217952_3_.isExplosion()) {
         return false;
      } else if (this.bypassesArmor != null && this.bypassesArmor != p_217952_3_.isUnblockable()) {
         return false;
      } else if (this.bypassesInvulnerability != null && this.bypassesInvulnerability != p_217952_3_.canHarmInCreative()) {
         return false;
      } else if (this.bypassesMagic != null && this.bypassesMagic != p_217952_3_.isDamageAbsolute()) {
         return false;
      } else if (this.isFire != null && this.isFire != p_217952_3_.isFireDamage()) {
         return false;
      } else if (this.isMagic != null && this.isMagic != p_217952_3_.isMagicDamage()) {
         return false;
      } else if (this.field_217953_i != null && this.field_217953_i != (p_217952_3_ == DamageSource.LIGHTNING_BOLT)) {
         return false;
      } else if (!this.directEntity.func_217993_a(p_217952_1_, p_217952_2_, p_217952_3_.getImmediateSource())) {
         return false;
      } else {
         return this.sourceEntity.func_217993_a(p_217952_1_, p_217952_2_, p_217952_3_.getTrueSource());
      }
   }

   public static DamageSourcePredicate deserialize(@Nullable JsonElement p_192447_0_) {
      if (p_192447_0_ != null && !p_192447_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_192447_0_, "damage type");
         Boolean lvt_2_1_ = optionalBoolean(lvt_1_1_, "is_projectile");
         Boolean lvt_3_1_ = optionalBoolean(lvt_1_1_, "is_explosion");
         Boolean lvt_4_1_ = optionalBoolean(lvt_1_1_, "bypasses_armor");
         Boolean lvt_5_1_ = optionalBoolean(lvt_1_1_, "bypasses_invulnerability");
         Boolean lvt_6_1_ = optionalBoolean(lvt_1_1_, "bypasses_magic");
         Boolean lvt_7_1_ = optionalBoolean(lvt_1_1_, "is_fire");
         Boolean lvt_8_1_ = optionalBoolean(lvt_1_1_, "is_magic");
         Boolean lvt_9_1_ = optionalBoolean(lvt_1_1_, "is_lightning");
         EntityPredicate lvt_10_1_ = EntityPredicate.deserialize(lvt_1_1_.get("direct_entity"));
         EntityPredicate lvt_11_1_ = EntityPredicate.deserialize(lvt_1_1_.get("source_entity"));
         return new DamageSourcePredicate(lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_, lvt_9_1_, lvt_10_1_, lvt_11_1_);
      } else {
         return ANY;
      }
   }

   @Nullable
   private static Boolean optionalBoolean(JsonObject p_192448_0_, String p_192448_1_) {
      return p_192448_0_.has(p_192448_1_) ? JSONUtils.getBoolean(p_192448_0_, p_192448_1_) : null;
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         this.addProperty(lvt_1_1_, "is_projectile", this.isProjectile);
         this.addProperty(lvt_1_1_, "is_explosion", this.isExplosion);
         this.addProperty(lvt_1_1_, "bypasses_armor", this.bypassesArmor);
         this.addProperty(lvt_1_1_, "bypasses_invulnerability", this.bypassesInvulnerability);
         this.addProperty(lvt_1_1_, "bypasses_magic", this.bypassesMagic);
         this.addProperty(lvt_1_1_, "is_fire", this.isFire);
         this.addProperty(lvt_1_1_, "is_magic", this.isMagic);
         this.addProperty(lvt_1_1_, "is_lightning", this.field_217953_i);
         lvt_1_1_.add("direct_entity", this.directEntity.serialize());
         lvt_1_1_.add("source_entity", this.sourceEntity.serialize());
         return lvt_1_1_;
      }
   }

   private void addProperty(JsonObject p_203992_1_, String p_203992_2_, @Nullable Boolean p_203992_3_) {
      if (p_203992_3_ != null) {
         p_203992_1_.addProperty(p_203992_2_, p_203992_3_);
      }

   }

   public static class Builder {
      private Boolean isProjectile;
      private Boolean isExplosion;
      private Boolean bypassesArmor;
      private Boolean bypassesInvulnerability;
      private Boolean bypassesMagic;
      private Boolean isFire;
      private Boolean isMagic;
      private Boolean field_217951_h;
      private EntityPredicate directEntity;
      private EntityPredicate sourceEntity;

      public Builder() {
         this.directEntity = EntityPredicate.ANY;
         this.sourceEntity = EntityPredicate.ANY;
      }

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder isProjectile(Boolean p_203978_1_) {
         this.isProjectile = p_203978_1_;
         return this;
      }

      public DamageSourcePredicate.Builder func_217950_h(Boolean p_217950_1_) {
         this.field_217951_h = p_217950_1_;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder p_203980_1_) {
         this.directEntity = p_203980_1_.build();
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.field_217951_h, this.directEntity, this.sourceEntity);
      }
   }
}
