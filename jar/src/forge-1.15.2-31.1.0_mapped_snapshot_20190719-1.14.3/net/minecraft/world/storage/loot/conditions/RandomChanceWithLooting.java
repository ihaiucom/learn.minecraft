package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class RandomChanceWithLooting implements ILootCondition {
   private final float chance;
   private final float lootingMultiplier;

   private RandomChanceWithLooting(float p_i46614_1_, float p_i46614_2_) {
      this.chance = p_i46614_1_;
      this.lootingMultiplier = p_i46614_2_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.KILLER_ENTITY);
   }

   public boolean test(LootContext p_test_1_) {
      int i = p_test_1_.getLootingModifier();
      return p_test_1_.getRandom().nextFloat() < this.chance + (float)i * this.lootingMultiplier;
   }

   public static ILootCondition.IBuilder builder(float p_216003_0_, float p_216003_1_) {
      return () -> {
         return new RandomChanceWithLooting(p_216003_0_, p_216003_1_);
      };
   }

   // $FF: synthetic method
   RandomChanceWithLooting(float p_i51194_1_, float p_i51194_2_, Object p_i51194_3_) {
      this(p_i51194_1_, p_i51194_2_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<RandomChanceWithLooting> {
      protected Serializer() {
         super(new ResourceLocation("random_chance_with_looting"), RandomChanceWithLooting.class);
      }

      public void serialize(JsonObject p_186605_1_, RandomChanceWithLooting p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("chance", p_186605_2_.chance);
         p_186605_1_.addProperty("looting_multiplier", p_186605_2_.lootingMultiplier);
      }

      public RandomChanceWithLooting deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return new RandomChanceWithLooting(JSONUtils.getFloat(p_186603_1_, "chance"), JSONUtils.getFloat(p_186603_1_, "looting_multiplier"));
      }
   }
}
