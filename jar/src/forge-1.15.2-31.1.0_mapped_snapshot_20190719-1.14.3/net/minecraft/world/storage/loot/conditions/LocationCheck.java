package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class LocationCheck implements ILootCondition {
   private final LocationPredicate predicate;
   private final BlockPos field_227564_b_;

   public LocationCheck(LocationPredicate p_i225895_1_, BlockPos p_i225895_2_) {
      this.predicate = p_i225895_1_;
      this.field_227564_b_ = p_i225895_2_;
   }

   public boolean test(LootContext p_test_1_) {
      BlockPos lvt_2_1_ = (BlockPos)p_test_1_.get(LootParameters.POSITION);
      return lvt_2_1_ != null && this.predicate.test(p_test_1_.getWorld(), (float)(lvt_2_1_.getX() + this.field_227564_b_.getX()), (float)(lvt_2_1_.getY() + this.field_227564_b_.getY()), (float)(lvt_2_1_.getZ() + this.field_227564_b_.getZ()));
   }

   public static ILootCondition.IBuilder builder(LocationPredicate.Builder p_215975_0_) {
      return () -> {
         return new LocationCheck(p_215975_0_.build(), BlockPos.ZERO);
      };
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<LocationCheck> {
      public Serializer() {
         super(new ResourceLocation("location_check"), LocationCheck.class);
      }

      public void serialize(JsonObject p_186605_1_, LocationCheck p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.add("predicate", p_186605_2_.predicate.serialize());
         if (p_186605_2_.field_227564_b_.getX() != 0) {
            p_186605_1_.addProperty("offsetX", p_186605_2_.field_227564_b_.getX());
         }

         if (p_186605_2_.field_227564_b_.getY() != 0) {
            p_186605_1_.addProperty("offsetY", p_186605_2_.field_227564_b_.getY());
         }

         if (p_186605_2_.field_227564_b_.getZ() != 0) {
            p_186605_1_.addProperty("offsetZ", p_186605_2_.field_227564_b_.getZ());
         }

      }

      public LocationCheck deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         LocationPredicate lvt_3_1_ = LocationPredicate.deserialize(p_186603_1_.get("predicate"));
         int lvt_4_1_ = JSONUtils.getInt(p_186603_1_, "offsetX", 0);
         int lvt_5_1_ = JSONUtils.getInt(p_186603_1_, "offsetY", 0);
         int lvt_6_1_ = JSONUtils.getInt(p_186603_1_, "offsetZ", 0);
         return new LocationCheck(lvt_3_1_, new BlockPos(lvt_4_1_, lvt_5_1_, lvt_6_1_));
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
