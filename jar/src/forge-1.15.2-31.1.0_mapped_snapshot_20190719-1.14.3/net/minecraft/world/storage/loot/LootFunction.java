package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootFunction implements ILootFunction {
   protected final ILootCondition[] conditions;
   private final Predicate<LootContext> combinedConditions;

   protected LootFunction(ILootCondition[] p_i51231_1_) {
      this.conditions = p_i51231_1_;
      this.combinedConditions = LootConditionManager.and(p_i51231_1_);
   }

   public final ItemStack apply(ItemStack p_apply_1_, LootContext p_apply_2_) {
      return this.combinedConditions.test(p_apply_2_) ? this.doApply(p_apply_1_, p_apply_2_) : p_apply_1_;
   }

   protected abstract ItemStack doApply(ItemStack var1, LootContext var2);

   public void func_225580_a_(ValidationTracker p_225580_1_) {
      ILootFunction.super.func_225580_a_(p_225580_1_);

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.conditions.length; ++lvt_2_1_) {
         this.conditions[lvt_2_1_].func_225580_a_(p_225580_1_.func_227534_b_(".conditions[" + lvt_2_1_ + "]"));
      }

   }

   protected static LootFunction.Builder<?> builder(Function<ILootCondition[], ILootFunction> p_215860_0_) {
      return new LootFunction.SimpleBuilder(p_215860_0_);
   }

   // $FF: synthetic method
   public Object apply(Object p_apply_1_, Object p_apply_2_) {
      return this.apply((ItemStack)p_apply_1_, (LootContext)p_apply_2_);
   }

   public abstract static class Serializer<T extends LootFunction> extends ILootFunction.Serializer<T> {
      public Serializer(ResourceLocation p_i50228_1_, Class<T> p_i50228_2_) {
         super(p_i50228_1_, p_i50228_2_);
      }

      public void serialize(JsonObject p_186532_1_, T p_186532_2_, JsonSerializationContext p_186532_3_) {
         if (!ArrayUtils.isEmpty(p_186532_2_.conditions)) {
            p_186532_1_.add("conditions", p_186532_3_.serialize(p_186532_2_.conditions));
         }

      }

      public final T deserialize(JsonObject p_212870_1_, JsonDeserializationContext p_212870_2_) {
         ILootCondition[] lvt_3_1_ = (ILootCondition[])JSONUtils.deserializeClass(p_212870_1_, "conditions", new ILootCondition[0], p_212870_2_, ILootCondition[].class);
         return this.deserialize(p_212870_1_, p_212870_2_, lvt_3_1_);
      }

      public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, ILootCondition[] var3);

      // $FF: synthetic method
      public ILootFunction deserialize(JsonObject p_212870_1_, JsonDeserializationContext p_212870_2_) {
         return this.deserialize(p_212870_1_, p_212870_2_);
      }
   }

   static final class SimpleBuilder extends LootFunction.Builder<LootFunction.SimpleBuilder> {
      private final Function<ILootCondition[], ILootFunction> function;

      public SimpleBuilder(Function<ILootCondition[], ILootFunction> p_i50229_1_) {
         this.function = p_i50229_1_;
      }

      protected LootFunction.SimpleBuilder doCast() {
         return this;
      }

      public ILootFunction build() {
         return (ILootFunction)this.function.apply(this.getConditions());
      }

      // $FF: synthetic method
      protected LootFunction.Builder doCast() {
         return this.doCast();
      }
   }

   public abstract static class Builder<T extends LootFunction.Builder<T>> implements ILootFunction.IBuilder, ILootConditionConsumer<T> {
      private final List<ILootCondition> conditions = Lists.newArrayList();

      public T acceptCondition(ILootCondition.IBuilder p_212840_1_) {
         this.conditions.add(p_212840_1_.build());
         return this.doCast();
      }

      public final T cast() {
         return this.doCast();
      }

      protected abstract T doCast();

      protected ILootCondition[] getConditions() {
         return (ILootCondition[])this.conditions.toArray(new ILootCondition[0]);
      }

      // $FF: synthetic method
      public Object cast() {
         return this.cast();
      }

      // $FF: synthetic method
      public Object acceptCondition(ILootCondition.IBuilder p_212840_1_) {
         return this.acceptCondition(p_212840_1_);
      }
   }
}
