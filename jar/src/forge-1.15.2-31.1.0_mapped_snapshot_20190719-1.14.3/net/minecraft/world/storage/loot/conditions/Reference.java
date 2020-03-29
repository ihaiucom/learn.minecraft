package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.ValidationTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference implements ILootCondition {
   private static final Logger field_227561_a_ = LogManager.getLogger();
   private final ResourceLocation field_227562_b_;

   public Reference(ResourceLocation p_i225894_1_) {
      this.field_227562_b_ = p_i225894_1_;
   }

   public void func_225580_a_(ValidationTracker p_225580_1_) {
      if (p_225580_1_.func_227536_b_(this.field_227562_b_)) {
         p_225580_1_.func_227530_a_("Condition " + this.field_227562_b_ + " is recursively called");
      } else {
         ILootCondition.super.func_225580_a_(p_225580_1_);
         ILootCondition lvt_2_1_ = p_225580_1_.func_227541_d_(this.field_227562_b_);
         if (lvt_2_1_ == null) {
            p_225580_1_.func_227530_a_("Unknown condition table called " + this.field_227562_b_);
         } else {
            lvt_2_1_.func_225580_a_(p_225580_1_.func_227531_a_(".{" + this.field_227562_b_ + "}", this.field_227562_b_));
         }

      }
   }

   public boolean test(LootContext p_test_1_) {
      ILootCondition lvt_2_1_ = p_test_1_.func_227504_b_(this.field_227562_b_);
      if (p_test_1_.func_227501_a_(lvt_2_1_)) {
         boolean var3;
         try {
            var3 = lvt_2_1_.test(p_test_1_);
         } finally {
            p_test_1_.func_227503_b_(lvt_2_1_);
         }

         return var3;
      } else {
         field_227561_a_.warn("Detected infinite loop in loot tables");
         return false;
      }
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<Reference> {
      protected Serializer() {
         super(new ResourceLocation("reference"), Reference.class);
      }

      public void serialize(JsonObject p_186605_1_, Reference p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("name", p_186605_2_.field_227562_b_.toString());
      }

      public Reference deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         ResourceLocation lvt_3_1_ = new ResourceLocation(JSONUtils.getString(p_186603_1_, "name"));
         return new Reference(lvt_3_1_);
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
