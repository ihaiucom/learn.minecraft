package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;

public class WeatherCheck implements ILootCondition {
   @Nullable
   private final Boolean raining;
   @Nullable
   private final Boolean thundering;

   private WeatherCheck(@Nullable Boolean p_i51191_1_, @Nullable Boolean p_i51191_2_) {
      this.raining = p_i51191_1_;
      this.thundering = p_i51191_2_;
   }

   public boolean test(LootContext p_test_1_) {
      ServerWorld lvt_2_1_ = p_test_1_.getWorld();
      if (this.raining != null && this.raining != lvt_2_1_.isRaining()) {
         return false;
      } else {
         return this.thundering == null || this.thundering == lvt_2_1_.isThundering();
      }
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   WeatherCheck(Boolean p_i51192_1_, Boolean p_i51192_2_, Object p_i51192_3_) {
      this(p_i51192_1_, p_i51192_2_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<WeatherCheck> {
      public Serializer() {
         super(new ResourceLocation("weather_check"), WeatherCheck.class);
      }

      public void serialize(JsonObject p_186605_1_, WeatherCheck p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("raining", p_186605_2_.raining);
         p_186605_1_.addProperty("thundering", p_186605_2_.thundering);
      }

      public WeatherCheck deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         Boolean lvt_3_1_ = p_186603_1_.has("raining") ? JSONUtils.getBoolean(p_186603_1_, "raining") : null;
         Boolean lvt_4_1_ = p_186603_1_.has("thundering") ? JSONUtils.getBoolean(p_186603_1_, "thundering") : null;
         return new WeatherCheck(lvt_3_1_, lvt_4_1_);
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
