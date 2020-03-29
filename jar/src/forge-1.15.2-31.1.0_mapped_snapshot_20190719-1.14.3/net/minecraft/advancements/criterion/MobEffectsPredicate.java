package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class MobEffectsPredicate {
   public static final MobEffectsPredicate ANY = new MobEffectsPredicate(Collections.emptyMap());
   private final Map<Effect, MobEffectsPredicate.InstancePredicate> effects;

   public MobEffectsPredicate(Map<Effect, MobEffectsPredicate.InstancePredicate> p_i47538_1_) {
      this.effects = p_i47538_1_;
   }

   public static MobEffectsPredicate any() {
      return new MobEffectsPredicate(Maps.newHashMap());
   }

   public MobEffectsPredicate addEffect(Effect p_204015_1_) {
      this.effects.put(p_204015_1_, new MobEffectsPredicate.InstancePredicate());
      return this;
   }

   public boolean test(Entity p_193469_1_) {
      if (this == ANY) {
         return true;
      } else {
         return p_193469_1_ instanceof LivingEntity ? this.test(((LivingEntity)p_193469_1_).getActivePotionMap()) : false;
      }
   }

   public boolean test(LivingEntity p_193472_1_) {
      return this == ANY ? true : this.test(p_193472_1_.getActivePotionMap());
   }

   public boolean test(Map<Effect, EffectInstance> p_193470_1_) {
      if (this == ANY) {
         return true;
      } else {
         Iterator var2 = this.effects.entrySet().iterator();

         Entry lvt_3_1_;
         EffectInstance lvt_4_1_;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            lvt_3_1_ = (Entry)var2.next();
            lvt_4_1_ = (EffectInstance)p_193470_1_.get(lvt_3_1_.getKey());
         } while(((MobEffectsPredicate.InstancePredicate)lvt_3_1_.getValue()).test(lvt_4_1_));

         return false;
      }
   }

   public static MobEffectsPredicate deserialize(@Nullable JsonElement p_193471_0_) {
      if (p_193471_0_ != null && !p_193471_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_193471_0_, "effects");
         Map<Effect, MobEffectsPredicate.InstancePredicate> lvt_2_1_ = Maps.newHashMap();
         Iterator var3 = lvt_1_1_.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<String, JsonElement> lvt_4_1_ = (Entry)var3.next();
            ResourceLocation lvt_5_1_ = new ResourceLocation((String)lvt_4_1_.getKey());
            Effect lvt_6_1_ = (Effect)Registry.EFFECTS.getValue(lvt_5_1_).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown effect '" + lvt_5_1_ + "'");
            });
            MobEffectsPredicate.InstancePredicate lvt_7_1_ = MobEffectsPredicate.InstancePredicate.deserialize(JSONUtils.getJsonObject((JsonElement)lvt_4_1_.getValue(), (String)lvt_4_1_.getKey()));
            lvt_2_1_.put(lvt_6_1_, lvt_7_1_);
         }

         return new MobEffectsPredicate(lvt_2_1_);
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         Iterator var2 = this.effects.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<Effect, MobEffectsPredicate.InstancePredicate> lvt_3_1_ = (Entry)var2.next();
            lvt_1_1_.add(Registry.EFFECTS.getKey(lvt_3_1_.getKey()).toString(), ((MobEffectsPredicate.InstancePredicate)lvt_3_1_.getValue()).serialize());
         }

         return lvt_1_1_;
      }
   }

   public static class InstancePredicate {
      private final MinMaxBounds.IntBound amplifier;
      private final MinMaxBounds.IntBound duration;
      @Nullable
      private final Boolean ambient;
      @Nullable
      private final Boolean visible;

      public InstancePredicate(MinMaxBounds.IntBound p_i49709_1_, MinMaxBounds.IntBound p_i49709_2_, @Nullable Boolean p_i49709_3_, @Nullable Boolean p_i49709_4_) {
         this.amplifier = p_i49709_1_;
         this.duration = p_i49709_2_;
         this.ambient = p_i49709_3_;
         this.visible = p_i49709_4_;
      }

      public InstancePredicate() {
         this(MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, (Boolean)null, (Boolean)null);
      }

      public boolean test(@Nullable EffectInstance p_193463_1_) {
         if (p_193463_1_ == null) {
            return false;
         } else if (!this.amplifier.test(p_193463_1_.getAmplifier())) {
            return false;
         } else if (!this.duration.test(p_193463_1_.getDuration())) {
            return false;
         } else if (this.ambient != null && this.ambient != p_193463_1_.isAmbient()) {
            return false;
         } else {
            return this.visible == null || this.visible == p_193463_1_.doesShowParticles();
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("amplifier", this.amplifier.serialize());
         lvt_1_1_.add("duration", this.duration.serialize());
         lvt_1_1_.addProperty("ambient", this.ambient);
         lvt_1_1_.addProperty("visible", this.visible);
         return lvt_1_1_;
      }

      public static MobEffectsPredicate.InstancePredicate deserialize(JsonObject p_193464_0_) {
         MinMaxBounds.IntBound lvt_1_1_ = MinMaxBounds.IntBound.fromJson(p_193464_0_.get("amplifier"));
         MinMaxBounds.IntBound lvt_2_1_ = MinMaxBounds.IntBound.fromJson(p_193464_0_.get("duration"));
         Boolean lvt_3_1_ = p_193464_0_.has("ambient") ? JSONUtils.getBoolean(p_193464_0_, "ambient") : null;
         Boolean lvt_4_1_ = p_193464_0_.has("visible") ? JSONUtils.getBoolean(p_193464_0_, "visible") : null;
         return new MobEffectsPredicate.InstancePredicate(lvt_1_1_, lvt_2_1_, lvt_3_1_, lvt_4_1_);
      }
   }
}
