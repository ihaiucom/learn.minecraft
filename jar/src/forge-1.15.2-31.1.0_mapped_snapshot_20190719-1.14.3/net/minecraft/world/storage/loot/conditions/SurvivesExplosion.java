package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class SurvivesExplosion implements ILootCondition {
   private static final SurvivesExplosion INSTANCE = new SurvivesExplosion();

   private SurvivesExplosion() {
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.EXPLOSION_RADIUS);
   }

   public boolean test(LootContext p_test_1_) {
      Float lvt_2_1_ = (Float)p_test_1_.get(LootParameters.EXPLOSION_RADIUS);
      if (lvt_2_1_ != null) {
         Random lvt_3_1_ = p_test_1_.getRandom();
         float lvt_4_1_ = 1.0F / lvt_2_1_;
         return lvt_3_1_.nextFloat() <= lvt_4_1_;
      } else {
         return true;
      }
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

   public static class Serializer extends ILootCondition.AbstractSerializer<SurvivesExplosion> {
      protected Serializer() {
         super(new ResourceLocation("survives_explosion"), SurvivesExplosion.class);
      }

      public void serialize(JsonObject p_186605_1_, SurvivesExplosion p_186605_2_, JsonSerializationContext p_186605_3_) {
      }

      public SurvivesExplosion deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return SurvivesExplosion.INSTANCE;
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
