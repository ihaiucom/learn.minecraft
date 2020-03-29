package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class MatchTool implements ILootCondition {
   private final ItemPredicate predicate;

   public MatchTool(ItemPredicate p_i51193_1_) {
      this.predicate = p_i51193_1_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.TOOL);
   }

   public boolean test(LootContext p_test_1_) {
      ItemStack lvt_2_1_ = (ItemStack)p_test_1_.get(LootParameters.TOOL);
      return lvt_2_1_ != null && this.predicate.test(lvt_2_1_);
   }

   public static ILootCondition.IBuilder builder(ItemPredicate.Builder p_216012_0_) {
      return () -> {
         return new MatchTool(p_216012_0_.build());
      };
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<MatchTool> {
      protected Serializer() {
         super(new ResourceLocation("match_tool"), MatchTool.class);
      }

      public void serialize(JsonObject p_186605_1_, MatchTool p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.add("predicate", p_186605_2_.predicate.serialize());
      }

      public MatchTool deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         ItemPredicate lvt_3_1_ = ItemPredicate.deserialize(p_186603_1_.get("predicate"));
         return new MatchTool(lvt_3_1_);
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
