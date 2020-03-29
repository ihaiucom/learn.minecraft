package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class TableBonus implements ILootCondition {
   private final Enchantment enchantment;
   private final float[] chances;

   private TableBonus(Enchantment p_i51207_1_, float[] p_i51207_2_) {
      this.enchantment = p_i51207_1_;
      this.chances = p_i51207_2_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.TOOL);
   }

   public boolean test(LootContext p_test_1_) {
      ItemStack lvt_2_1_ = (ItemStack)p_test_1_.get(LootParameters.TOOL);
      int lvt_3_1_ = lvt_2_1_ != null ? EnchantmentHelper.getEnchantmentLevel(this.enchantment, lvt_2_1_) : 0;
      float lvt_4_1_ = this.chances[Math.min(lvt_3_1_, this.chances.length - 1)];
      return p_test_1_.getRandom().nextFloat() < lvt_4_1_;
   }

   public static ILootCondition.IBuilder builder(Enchantment p_215955_0_, float... p_215955_1_) {
      return () -> {
         return new TableBonus(p_215955_0_, p_215955_1_);
      };
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   TableBonus(Enchantment p_i51208_1_, float[] p_i51208_2_, Object p_i51208_3_) {
      this(p_i51208_1_, p_i51208_2_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<TableBonus> {
      public Serializer() {
         super(new ResourceLocation("table_bonus"), TableBonus.class);
      }

      public void serialize(JsonObject p_186605_1_, TableBonus p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("enchantment", Registry.ENCHANTMENT.getKey(p_186605_2_.enchantment).toString());
         p_186605_1_.add("chances", p_186605_3_.serialize(p_186605_2_.chances));
      }

      public TableBonus deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         ResourceLocation lvt_3_1_ = new ResourceLocation(JSONUtils.getString(p_186603_1_, "enchantment"));
         Enchantment lvt_4_1_ = (Enchantment)Registry.ENCHANTMENT.getValue(lvt_3_1_).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + lvt_3_1_);
         });
         float[] lvt_5_1_ = (float[])JSONUtils.deserializeClass(p_186603_1_, "chances", p_186603_2_, float[].class);
         return new TableBonus(lvt_4_1_, lvt_5_1_);
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
