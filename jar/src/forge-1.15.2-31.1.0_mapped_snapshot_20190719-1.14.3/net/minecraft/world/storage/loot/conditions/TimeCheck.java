package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;

public class TimeCheck implements ILootCondition {
   @Nullable
   private final Long field_227570_a_;
   private final RandomValueRange field_227571_b_;

   private TimeCheck(@Nullable Long p_i225898_1_, RandomValueRange p_i225898_2_) {
      this.field_227570_a_ = p_i225898_1_;
      this.field_227571_b_ = p_i225898_2_;
   }

   public boolean test(LootContext p_test_1_) {
      ServerWorld lvt_2_1_ = p_test_1_.getWorld();
      long lvt_3_1_ = lvt_2_1_.getDayTime();
      if (this.field_227570_a_ != null) {
         lvt_3_1_ %= this.field_227570_a_;
      }

      return this.field_227571_b_.isInRange((int)lvt_3_1_);
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   TimeCheck(Long p_i225899_1_, RandomValueRange p_i225899_2_, Object p_i225899_3_) {
      this(p_i225899_1_, p_i225899_2_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<TimeCheck> {
      public Serializer() {
         super(new ResourceLocation("time_check"), TimeCheck.class);
      }

      public void serialize(JsonObject p_186605_1_, TimeCheck p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("period", p_186605_2_.field_227570_a_);
         p_186605_1_.add("value", p_186605_3_.serialize(p_186605_2_.field_227571_b_));
      }

      public TimeCheck deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         Long lvt_3_1_ = p_186603_1_.has("period") ? JSONUtils.func_226161_m_(p_186603_1_, "period") : null;
         RandomValueRange lvt_4_1_ = (RandomValueRange)JSONUtils.deserializeClass(p_186603_1_, "value", p_186603_2_, RandomValueRange.class);
         return new TimeCheck(lvt_3_1_, lvt_4_1_);
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
