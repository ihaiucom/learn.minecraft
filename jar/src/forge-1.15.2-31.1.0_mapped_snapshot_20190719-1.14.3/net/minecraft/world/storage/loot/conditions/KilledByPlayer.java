package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class KilledByPlayer implements ILootCondition {
   private static final KilledByPlayer INSTANCE = new KilledByPlayer();

   private KilledByPlayer() {
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.LAST_DAMAGE_PLAYER);
   }

   public boolean test(LootContext p_test_1_) {
      return p_test_1_.has(LootParameters.LAST_DAMAGE_PLAYER);
   }

   public static ILootCondition.IBuilder builder() {
      return () -> {
         return INSTANCE;
      };
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<KilledByPlayer> {
      protected Serializer() {
         super(new ResourceLocation("killed_by_player"), KilledByPlayer.class);
      }

      public void serialize(JsonObject p_186605_1_, KilledByPlayer p_186605_2_, JsonSerializationContext p_186605_3_) {
      }

      public KilledByPlayer deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return KilledByPlayer.INSTANCE;
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
