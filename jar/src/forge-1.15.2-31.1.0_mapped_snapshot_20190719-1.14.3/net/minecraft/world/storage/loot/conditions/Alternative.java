package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.ValidationTracker;

public class Alternative implements ILootCondition {
   private final ILootCondition[] field_215962_a;
   private final Predicate<LootContext> field_215963_b;

   private Alternative(ILootCondition[] p_i51209_1_) {
      this.field_215962_a = p_i51209_1_;
      this.field_215963_b = LootConditionManager.or(p_i51209_1_);
   }

   public final boolean test(LootContext p_test_1_) {
      return this.field_215963_b.test(p_test_1_);
   }

   public void func_225580_a_(ValidationTracker p_225580_1_) {
      ILootCondition.super.func_225580_a_(p_225580_1_);

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_215962_a.length; ++lvt_2_1_) {
         this.field_215962_a[lvt_2_1_].func_225580_a_(p_225580_1_.func_227534_b_(".term[" + lvt_2_1_ + "]"));
      }

   }

   public static Alternative.Builder builder(ILootCondition.IBuilder... p_215960_0_) {
      return new Alternative.Builder(p_215960_0_);
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   Alternative(ILootCondition[] p_i51210_1_, Object p_i51210_2_) {
      this(p_i51210_1_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<Alternative> {
      public Serializer() {
         super(new ResourceLocation("alternative"), Alternative.class);
      }

      public void serialize(JsonObject p_186605_1_, Alternative p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.add("terms", p_186605_3_.serialize(p_186605_2_.field_215962_a));
      }

      public Alternative deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         ILootCondition[] lvt_3_1_ = (ILootCondition[])JSONUtils.deserializeClass(p_186603_1_, "terms", p_186603_2_, ILootCondition[].class);
         return new Alternative(lvt_3_1_);
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }

   public static class Builder implements ILootCondition.IBuilder {
      private final List<ILootCondition> field_216303_a = Lists.newArrayList();

      public Builder(ILootCondition.IBuilder... p_i50046_1_) {
         ILootCondition.IBuilder[] var2 = p_i50046_1_;
         int var3 = p_i50046_1_.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ILootCondition.IBuilder lvt_5_1_ = var2[var4];
            this.field_216303_a.add(lvt_5_1_.build());
         }

      }

      public Alternative.Builder alternative(ILootCondition.IBuilder p_216297_1_) {
         this.field_216303_a.add(p_216297_1_.build());
         return this;
      }

      public ILootCondition build() {
         return new Alternative((ILootCondition[])this.field_216303_a.toArray(new ILootCondition[0]));
      }
   }
}
