package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class LootingEnchantBonus extends LootFunction {
   private final RandomValueRange count;
   private final int limit;

   private LootingEnchantBonus(ILootCondition[] p_i47145_1_, RandomValueRange p_i47145_2_, int p_i47145_3_) {
      super(p_i47145_1_);
      this.count = p_i47145_2_;
      this.limit = p_i47145_3_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.KILLER_ENTITY);
   }

   private boolean func_215917_b() {
      return this.limit > 0;
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Entity entity = (Entity)p_215859_2_.get(LootParameters.KILLER_ENTITY);
      if (entity instanceof LivingEntity) {
         int i = p_215859_2_.getLootingModifier();
         if (i == 0) {
            return p_215859_1_;
         }

         float f = (float)i * this.count.generateFloat(p_215859_2_.getRandom());
         p_215859_1_.grow(Math.round(f));
         if (this.func_215917_b() && p_215859_1_.getCount() > this.limit) {
            p_215859_1_.setCount(this.limit);
         }
      }

      return p_215859_1_;
   }

   public static LootingEnchantBonus.Builder func_215915_a(RandomValueRange p_215915_0_) {
      return new LootingEnchantBonus.Builder(p_215915_0_);
   }

   // $FF: synthetic method
   LootingEnchantBonus(ILootCondition[] p_i51230_1_, RandomValueRange p_i51230_2_, int p_i51230_3_, Object p_i51230_4_) {
      this(p_i51230_1_, p_i51230_2_, p_i51230_3_);
   }

   public static class Serializer extends LootFunction.Serializer<LootingEnchantBonus> {
      protected Serializer() {
         super(new ResourceLocation("looting_enchant"), LootingEnchantBonus.class);
      }

      public void serialize(JsonObject p_186532_1_, LootingEnchantBonus p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.add("count", p_186532_3_.serialize(p_186532_2_.count));
         if (p_186532_2_.func_215917_b()) {
            p_186532_1_.add("limit", p_186532_3_.serialize(p_186532_2_.limit));
         }

      }

      public LootingEnchantBonus deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         int i = JSONUtils.getInt(p_186530_1_, "limit", 0);
         return new LootingEnchantBonus(p_186530_3_, (RandomValueRange)JSONUtils.deserializeClass(p_186530_1_, "count", p_186530_2_, RandomValueRange.class), i);
      }
   }

   public static class Builder extends LootFunction.Builder<LootingEnchantBonus.Builder> {
      private final RandomValueRange field_216073_a;
      private int field_216074_b = 0;

      public Builder(RandomValueRange p_i50932_1_) {
         this.field_216073_a = p_i50932_1_;
      }

      protected LootingEnchantBonus.Builder doCast() {
         return this;
      }

      public LootingEnchantBonus.Builder func_216072_a(int p_216072_1_) {
         this.field_216074_b = p_216072_1_;
         return this;
      }

      public ILootFunction build() {
         return new LootingEnchantBonus(this.getConditions(), this.field_216073_a, this.field_216074_b);
      }
   }
}
