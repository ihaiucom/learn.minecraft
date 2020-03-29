package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IRandomRange;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.RandomRanges;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class EnchantWithLevels extends LootFunction {
   private final IRandomRange randomLevel;
   private final boolean isTreasure;

   private EnchantWithLevels(ILootCondition[] p_i51236_1_, IRandomRange p_i51236_2_, boolean p_i51236_3_) {
      super(p_i51236_1_);
      this.randomLevel = p_i51236_2_;
      this.isTreasure = p_i51236_3_;
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Random lvt_3_1_ = p_215859_2_.getRandom();
      return EnchantmentHelper.addRandomEnchantment(lvt_3_1_, p_215859_1_, this.randomLevel.generateInt(lvt_3_1_), this.isTreasure);
   }

   public static EnchantWithLevels.Builder func_215895_a(IRandomRange p_215895_0_) {
      return new EnchantWithLevels.Builder(p_215895_0_);
   }

   // $FF: synthetic method
   EnchantWithLevels(ILootCondition[] p_i51237_1_, IRandomRange p_i51237_2_, boolean p_i51237_3_, Object p_i51237_4_) {
      this(p_i51237_1_, p_i51237_2_, p_i51237_3_);
   }

   public static class Serializer extends LootFunction.Serializer<EnchantWithLevels> {
      public Serializer() {
         super(new ResourceLocation("enchant_with_levels"), EnchantWithLevels.class);
      }

      public void serialize(JsonObject p_186532_1_, EnchantWithLevels p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.add("levels", RandomRanges.serialize(p_186532_2_.randomLevel, p_186532_3_));
         p_186532_1_.addProperty("treasure", p_186532_2_.isTreasure);
      }

      public EnchantWithLevels deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         IRandomRange lvt_4_1_ = RandomRanges.deserialize(p_186530_1_.get("levels"), p_186530_2_);
         boolean lvt_5_1_ = JSONUtils.getBoolean(p_186530_1_, "treasure", false);
         return new EnchantWithLevels(p_186530_3_, lvt_4_1_, lvt_5_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }

   public static class Builder extends LootFunction.Builder<EnchantWithLevels.Builder> {
      private final IRandomRange field_216060_a;
      private boolean field_216061_b;

      public Builder(IRandomRange p_i51494_1_) {
         this.field_216060_a = p_i51494_1_;
      }

      protected EnchantWithLevels.Builder doCast() {
         return this;
      }

      public EnchantWithLevels.Builder func_216059_e() {
         this.field_216061_b = true;
         return this;
      }

      public ILootFunction build() {
         return new EnchantWithLevels(this.getConditions(), this.field_216060_a, this.field_216061_b);
      }

      // $FF: synthetic method
      protected LootFunction.Builder doCast() {
         return this.doCast();
      }
   }
}
