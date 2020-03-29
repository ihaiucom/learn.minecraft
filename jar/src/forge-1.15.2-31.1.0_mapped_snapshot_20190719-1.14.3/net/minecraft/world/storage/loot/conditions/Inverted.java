package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.ValidationTracker;

public class Inverted implements ILootCondition {
   private final ILootCondition term;

   private Inverted(ILootCondition p_i51202_1_) {
      this.term = p_i51202_1_;
   }

   public final boolean test(LootContext p_test_1_) {
      return !this.term.test(p_test_1_);
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return this.term.getRequiredParameters();
   }

   public void func_225580_a_(ValidationTracker p_225580_1_) {
      ILootCondition.super.func_225580_a_(p_225580_1_);
      this.term.func_225580_a_(p_225580_1_);
   }

   public static ILootCondition.IBuilder builder(ILootCondition.IBuilder p_215979_0_) {
      Inverted lvt_1_1_ = new Inverted(p_215979_0_.build());
      return () -> {
         return lvt_1_1_;
      };
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   Inverted(ILootCondition p_i51203_1_, Object p_i51203_2_) {
      this(p_i51203_1_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<Inverted> {
      public Serializer() {
         super(new ResourceLocation("inverted"), Inverted.class);
      }

      public void serialize(JsonObject p_186605_1_, Inverted p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.add("term", p_186605_3_.serialize(p_186605_2_.term));
      }

      public Inverted deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         ILootCondition lvt_3_1_ = (ILootCondition)JSONUtils.deserializeClass(p_186603_1_, "term", p_186603_2_, ILootCondition.class);
         return new Inverted(lvt_3_1_);
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
