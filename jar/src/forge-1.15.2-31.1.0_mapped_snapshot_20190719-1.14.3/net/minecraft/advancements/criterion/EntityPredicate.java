package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class EntityPredicate {
   public static final EntityPredicate ANY;
   public static final EntityPredicate[] ANY_ARRAY;
   private final EntityTypePredicate type;
   private final DistancePredicate distance;
   private final LocationPredicate location;
   private final MobEffectsPredicate effects;
   private final NBTPredicate nbt;
   private final EntityFlagsPredicate flags;
   private final EntityEquipmentPredicate field_217995_i;
   private final PlayerPredicate field_226609_j_;
   @Nullable
   private final String field_226610_k_;
   @Nullable
   private final ResourceLocation field_217996_j;

   private EntityPredicate(EntityTypePredicate p_i225735_1_, DistancePredicate p_i225735_2_, LocationPredicate p_i225735_3_, MobEffectsPredicate p_i225735_4_, NBTPredicate p_i225735_5_, EntityFlagsPredicate p_i225735_6_, EntityEquipmentPredicate p_i225735_7_, PlayerPredicate p_i225735_8_, @Nullable String p_i225735_9_, @Nullable ResourceLocation p_i225735_10_) {
      this.type = p_i225735_1_;
      this.distance = p_i225735_2_;
      this.location = p_i225735_3_;
      this.effects = p_i225735_4_;
      this.nbt = p_i225735_5_;
      this.flags = p_i225735_6_;
      this.field_217995_i = p_i225735_7_;
      this.field_226609_j_ = p_i225735_8_;
      this.field_226610_k_ = p_i225735_9_;
      this.field_217996_j = p_i225735_10_;
   }

   public boolean test(ServerPlayerEntity p_192482_1_, @Nullable Entity p_192482_2_) {
      return this.func_217993_a(p_192482_1_.getServerWorld(), p_192482_1_.getPositionVec(), p_192482_2_);
   }

   public boolean func_217993_a(ServerWorld p_217993_1_, @Nullable Vec3d p_217993_2_, @Nullable Entity p_217993_3_) {
      if (this == ANY) {
         return true;
      } else if (p_217993_3_ == null) {
         return false;
      } else if (!this.type.test(p_217993_3_.getType())) {
         return false;
      } else {
         if (p_217993_2_ == null) {
            if (this.distance != DistancePredicate.ANY) {
               return false;
            }
         } else if (!this.distance.test(p_217993_2_.x, p_217993_2_.y, p_217993_2_.z, p_217993_3_.func_226277_ct_(), p_217993_3_.func_226278_cu_(), p_217993_3_.func_226281_cx_())) {
            return false;
         }

         if (!this.location.test(p_217993_1_, p_217993_3_.func_226277_ct_(), p_217993_3_.func_226278_cu_(), p_217993_3_.func_226281_cx_())) {
            return false;
         } else if (!this.effects.test(p_217993_3_)) {
            return false;
         } else if (!this.nbt.test(p_217993_3_)) {
            return false;
         } else if (!this.flags.test(p_217993_3_)) {
            return false;
         } else if (!this.field_217995_i.test(p_217993_3_)) {
            return false;
         } else if (!this.field_226609_j_.func_226998_a_(p_217993_3_)) {
            return false;
         } else {
            if (this.field_226610_k_ != null) {
               Team lvt_4_1_ = p_217993_3_.getTeam();
               if (lvt_4_1_ == null || !this.field_226610_k_.equals(lvt_4_1_.getName())) {
                  return false;
               }
            }

            return this.field_217996_j == null || p_217993_3_ instanceof CatEntity && ((CatEntity)p_217993_3_).getCatTypeName().equals(this.field_217996_j);
         }
      }
   }

   public static EntityPredicate deserialize(@Nullable JsonElement p_192481_0_) {
      if (p_192481_0_ != null && !p_192481_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_192481_0_, "entity");
         EntityTypePredicate lvt_2_1_ = EntityTypePredicate.deserialize(lvt_1_1_.get("type"));
         DistancePredicate lvt_3_1_ = DistancePredicate.deserialize(lvt_1_1_.get("distance"));
         LocationPredicate lvt_4_1_ = LocationPredicate.deserialize(lvt_1_1_.get("location"));
         MobEffectsPredicate lvt_5_1_ = MobEffectsPredicate.deserialize(lvt_1_1_.get("effects"));
         NBTPredicate lvt_6_1_ = NBTPredicate.deserialize(lvt_1_1_.get("nbt"));
         EntityFlagsPredicate lvt_7_1_ = EntityFlagsPredicate.deserialize(lvt_1_1_.get("flags"));
         EntityEquipmentPredicate lvt_8_1_ = EntityEquipmentPredicate.deserialize(lvt_1_1_.get("equipment"));
         PlayerPredicate lvt_9_1_ = PlayerPredicate.func_227000_a_(lvt_1_1_.get("player"));
         String lvt_10_1_ = JSONUtils.getString(lvt_1_1_, "team", (String)null);
         ResourceLocation lvt_11_1_ = lvt_1_1_.has("catType") ? new ResourceLocation(JSONUtils.getString(lvt_1_1_, "catType")) : null;
         return (new EntityPredicate.Builder()).type(lvt_2_1_).distance(lvt_3_1_).location(lvt_4_1_).effects(lvt_5_1_).nbt(lvt_6_1_).func_217987_a(lvt_7_1_).func_217985_a(lvt_8_1_).func_226613_a_(lvt_9_1_).func_226614_a_(lvt_10_1_).func_217988_b(lvt_11_1_).build();
      } else {
         return ANY;
      }
   }

   public static EntityPredicate[] deserializeArray(@Nullable JsonElement p_204849_0_) {
      if (p_204849_0_ != null && !p_204849_0_.isJsonNull()) {
         JsonArray lvt_1_1_ = JSONUtils.getJsonArray(p_204849_0_, "entities");
         EntityPredicate[] lvt_2_1_ = new EntityPredicate[lvt_1_1_.size()];

         for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_1_1_.size(); ++lvt_3_1_) {
            lvt_2_1_[lvt_3_1_] = deserialize(lvt_1_1_.get(lvt_3_1_));
         }

         return lvt_2_1_;
      } else {
         return ANY_ARRAY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("type", this.type.serialize());
         lvt_1_1_.add("distance", this.distance.serialize());
         lvt_1_1_.add("location", this.location.serialize());
         lvt_1_1_.add("effects", this.effects.serialize());
         lvt_1_1_.add("nbt", this.nbt.serialize());
         lvt_1_1_.add("flags", this.flags.serialize());
         lvt_1_1_.add("equipment", this.field_217995_i.serialize());
         lvt_1_1_.add("player", this.field_226609_j_.func_226995_a_());
         lvt_1_1_.addProperty("team", this.field_226610_k_);
         if (this.field_217996_j != null) {
            lvt_1_1_.addProperty("catType", this.field_217996_j.toString());
         }

         return lvt_1_1_;
      }
   }

   public static JsonElement serializeArray(EntityPredicate[] p_204850_0_) {
      if (p_204850_0_ == ANY_ARRAY) {
         return JsonNull.INSTANCE;
      } else {
         JsonArray lvt_1_1_ = new JsonArray();
         EntityPredicate[] var2 = p_204850_0_;
         int var3 = p_204850_0_.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EntityPredicate lvt_5_1_ = var2[var4];
            JsonElement lvt_6_1_ = lvt_5_1_.serialize();
            if (!lvt_6_1_.isJsonNull()) {
               lvt_1_1_.add(lvt_6_1_);
            }
         }

         return lvt_1_1_;
      }
   }

   // $FF: synthetic method
   EntityPredicate(EntityTypePredicate p_i225736_1_, DistancePredicate p_i225736_2_, LocationPredicate p_i225736_3_, MobEffectsPredicate p_i225736_4_, NBTPredicate p_i225736_5_, EntityFlagsPredicate p_i225736_6_, EntityEquipmentPredicate p_i225736_7_, PlayerPredicate p_i225736_8_, String p_i225736_9_, ResourceLocation p_i225736_10_, Object p_i225736_11_) {
      this(p_i225736_1_, p_i225736_2_, p_i225736_3_, p_i225736_4_, p_i225736_5_, p_i225736_6_, p_i225736_7_, p_i225736_8_, p_i225736_9_, p_i225736_10_);
   }

   static {
      ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NBTPredicate.ANY, EntityFlagsPredicate.ALWAYS_TRUE, EntityEquipmentPredicate.ANY, PlayerPredicate.field_226989_a_, (String)null, (ResourceLocation)null);
      ANY_ARRAY = new EntityPredicate[0];
   }

   public static class Builder {
      private EntityTypePredicate type;
      private DistancePredicate distance;
      private LocationPredicate location;
      private MobEffectsPredicate effects;
      private NBTPredicate nbt;
      private EntityFlagsPredicate field_217990_f;
      private EntityEquipmentPredicate field_217991_g;
      private PlayerPredicate field_226611_h_;
      private String field_226612_i_;
      private ResourceLocation field_217992_h;

      public Builder() {
         this.type = EntityTypePredicate.ANY;
         this.distance = DistancePredicate.ANY;
         this.location = LocationPredicate.ANY;
         this.effects = MobEffectsPredicate.ANY;
         this.nbt = NBTPredicate.ANY;
         this.field_217990_f = EntityFlagsPredicate.ALWAYS_TRUE;
         this.field_217991_g = EntityEquipmentPredicate.ANY;
         this.field_226611_h_ = PlayerPredicate.field_226989_a_;
      }

      public static EntityPredicate.Builder create() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder type(EntityType<?> p_203998_1_) {
         this.type = EntityTypePredicate.func_217999_b(p_203998_1_);
         return this;
      }

      public EntityPredicate.Builder func_217989_a(Tag<EntityType<?>> p_217989_1_) {
         this.type = EntityTypePredicate.func_217998_a(p_217989_1_);
         return this;
      }

      public EntityPredicate.Builder func_217986_a(ResourceLocation p_217986_1_) {
         this.field_217992_h = p_217986_1_;
         return this;
      }

      public EntityPredicate.Builder type(EntityTypePredicate p_209366_1_) {
         this.type = p_209366_1_;
         return this;
      }

      public EntityPredicate.Builder distance(DistancePredicate p_203997_1_) {
         this.distance = p_203997_1_;
         return this;
      }

      public EntityPredicate.Builder location(LocationPredicate p_203999_1_) {
         this.location = p_203999_1_;
         return this;
      }

      public EntityPredicate.Builder effects(MobEffectsPredicate p_209367_1_) {
         this.effects = p_209367_1_;
         return this;
      }

      public EntityPredicate.Builder nbt(NBTPredicate p_209365_1_) {
         this.nbt = p_209365_1_;
         return this;
      }

      public EntityPredicate.Builder func_217987_a(EntityFlagsPredicate p_217987_1_) {
         this.field_217990_f = p_217987_1_;
         return this;
      }

      public EntityPredicate.Builder func_217985_a(EntityEquipmentPredicate p_217985_1_) {
         this.field_217991_g = p_217985_1_;
         return this;
      }

      public EntityPredicate.Builder func_226613_a_(PlayerPredicate p_226613_1_) {
         this.field_226611_h_ = p_226613_1_;
         return this;
      }

      public EntityPredicate.Builder func_226614_a_(@Nullable String p_226614_1_) {
         this.field_226612_i_ = p_226614_1_;
         return this;
      }

      public EntityPredicate.Builder func_217988_b(@Nullable ResourceLocation p_217988_1_) {
         this.field_217992_h = p_217988_1_;
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(this.type, this.distance, this.location, this.effects, this.nbt, this.field_217990_f, this.field_217991_g, this.field_226611_h_, this.field_226612_i_, this.field_217992_h);
      }
   }
}
