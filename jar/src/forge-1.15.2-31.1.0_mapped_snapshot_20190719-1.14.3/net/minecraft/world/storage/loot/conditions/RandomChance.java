package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class RandomChance implements ILootCondition {
   private final float chance;

   private RandomChance(float p_i46615_1_) {
      this.chance = p_i46615_1_;
   }

   public boolean test(LootContext p_test_1_) {
      return p_test_1_.getRandom().nextFloat() < this.chance;
   }

   public static ILootCondition.IBuilder builder(float p_216004_0_) {
      return () -> {
         return new RandomChance(p_216004_0_);
      };
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   RandomChance(float p_i51195_1_, Object p_i51195_2_) {
      this(p_i51195_1_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<RandomChance> {
      protected Serializer() {
         super(new ResourceLocation("random_chance"), RandomChance.class);
      }

      public void serialize(JsonObject p_186605_1_, RandomChance p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("chance", p_186605_2_.chance);
      }

      public RandomChance deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return new RandomChance(JSONUtils.getFloat(p_186603_1_, "chance"));
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
