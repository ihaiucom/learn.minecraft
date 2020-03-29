package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class DamageSourceProperties implements ILootCondition {
   private final DamageSourcePredicate predicate;

   private DamageSourceProperties(DamageSourcePredicate p_i51205_1_) {
      this.predicate = p_i51205_1_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.POSITION, LootParameters.DAMAGE_SOURCE);
   }

   public boolean test(LootContext p_test_1_) {
      DamageSource lvt_2_1_ = (DamageSource)p_test_1_.get(LootParameters.DAMAGE_SOURCE);
      BlockPos lvt_3_1_ = (BlockPos)p_test_1_.get(LootParameters.POSITION);
      return lvt_3_1_ != null && lvt_2_1_ != null && this.predicate.func_217952_a(p_test_1_.getWorld(), new Vec3d(lvt_3_1_), lvt_2_1_);
   }

   public static ILootCondition.IBuilder builder(DamageSourcePredicate.Builder p_215966_0_) {
      return () -> {
         return new DamageSourceProperties(p_215966_0_.build());
      };
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   DamageSourceProperties(DamageSourcePredicate p_i51206_1_, Object p_i51206_2_) {
      this(p_i51206_1_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<DamageSourceProperties> {
      protected Serializer() {
         super(new ResourceLocation("damage_source_properties"), DamageSourceProperties.class);
      }

      public void serialize(JsonObject p_186605_1_, DamageSourceProperties p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.add("predicate", p_186605_2_.predicate.serialize());
      }

      public DamageSourceProperties deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         DamageSourcePredicate lvt_3_1_ = DamageSourcePredicate.deserialize(p_186603_1_.get("predicate"));
         return new DamageSourceProperties(lvt_3_1_);
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
