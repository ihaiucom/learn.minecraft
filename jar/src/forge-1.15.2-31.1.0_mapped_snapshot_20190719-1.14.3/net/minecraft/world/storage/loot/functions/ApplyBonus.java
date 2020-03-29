package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ApplyBonus extends LootFunction {
   private static final Map<ResourceLocation, ApplyBonus.IFormulaDeserializer> field_215875_a = Maps.newHashMap();
   private final Enchantment field_215876_c;
   private final ApplyBonus.IFormula field_215877_d;

   private ApplyBonus(ILootCondition[] p_i51246_1_, Enchantment p_i51246_2_, ApplyBonus.IFormula p_i51246_3_) {
      super(p_i51246_1_);
      this.field_215876_c = p_i51246_2_;
      this.field_215877_d = p_i51246_3_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.TOOL);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      ItemStack lvt_3_1_ = (ItemStack)p_215859_2_.get(LootParameters.TOOL);
      if (lvt_3_1_ != null) {
         int lvt_4_1_ = EnchantmentHelper.getEnchantmentLevel(this.field_215876_c, lvt_3_1_);
         int lvt_5_1_ = this.field_215877_d.func_216204_a(p_215859_2_.getRandom(), p_215859_1_.getCount(), lvt_4_1_);
         p_215859_1_.setCount(lvt_5_1_);
      }

      return p_215859_1_;
   }

   public static LootFunction.Builder<?> func_215870_a(Enchantment p_215870_0_, float p_215870_1_, int p_215870_2_) {
      return builder((p_215864_3_) -> {
         return new ApplyBonus(p_215864_3_, p_215870_0_, new ApplyBonus.BinomialWithBonusCountFormula(p_215870_2_, p_215870_1_));
      });
   }

   public static LootFunction.Builder<?> func_215869_a(Enchantment p_215869_0_) {
      return builder((p_215866_1_) -> {
         return new ApplyBonus(p_215866_1_, p_215869_0_, new ApplyBonus.OreDropsFormula());
      });
   }

   public static LootFunction.Builder<?> func_215871_b(Enchantment p_215871_0_) {
      return builder((p_215872_1_) -> {
         return new ApplyBonus(p_215872_1_, p_215871_0_, new ApplyBonus.UniformBonusCountFormula(1));
      });
   }

   public static LootFunction.Builder<?> func_215865_a(Enchantment p_215865_0_, int p_215865_1_) {
      return builder((p_215868_2_) -> {
         return new ApplyBonus(p_215868_2_, p_215865_0_, new ApplyBonus.UniformBonusCountFormula(p_215865_1_));
      });
   }

   // $FF: synthetic method
   ApplyBonus(ILootCondition[] p_i51247_1_, Enchantment p_i51247_2_, ApplyBonus.IFormula p_i51247_3_, Object p_i51247_4_) {
      this(p_i51247_1_, p_i51247_2_, p_i51247_3_);
   }

   static {
      field_215875_a.put(ApplyBonus.BinomialWithBonusCountFormula.field_216211_a, ApplyBonus.BinomialWithBonusCountFormula::func_216210_a);
      field_215875_a.put(ApplyBonus.OreDropsFormula.field_216206_a, ApplyBonus.OreDropsFormula::func_216205_a);
      field_215875_a.put(ApplyBonus.UniformBonusCountFormula.field_216208_a, ApplyBonus.UniformBonusCountFormula::func_216207_a);
   }

   public static class Serializer extends LootFunction.Serializer<ApplyBonus> {
      public Serializer() {
         super(new ResourceLocation("apply_bonus"), ApplyBonus.class);
      }

      public void serialize(JsonObject p_186532_1_, ApplyBonus p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.addProperty("enchantment", Registry.ENCHANTMENT.getKey(p_186532_2_.field_215876_c).toString());
         p_186532_1_.addProperty("formula", p_186532_2_.field_215877_d.func_216203_a().toString());
         JsonObject lvt_4_1_ = new JsonObject();
         p_186532_2_.field_215877_d.func_216202_a(lvt_4_1_, p_186532_3_);
         if (lvt_4_1_.size() > 0) {
            p_186532_1_.add("parameters", lvt_4_1_);
         }

      }

      public ApplyBonus deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         ResourceLocation lvt_4_1_ = new ResourceLocation(JSONUtils.getString(p_186530_1_, "enchantment"));
         Enchantment lvt_5_1_ = (Enchantment)Registry.ENCHANTMENT.getValue(lvt_4_1_).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + lvt_4_1_);
         });
         ResourceLocation lvt_6_1_ = new ResourceLocation(JSONUtils.getString(p_186530_1_, "formula"));
         ApplyBonus.IFormulaDeserializer lvt_7_1_ = (ApplyBonus.IFormulaDeserializer)ApplyBonus.field_215875_a.get(lvt_6_1_);
         if (lvt_7_1_ == null) {
            throw new JsonParseException("Invalid formula id: " + lvt_6_1_);
         } else {
            ApplyBonus.IFormula lvt_8_2_;
            if (p_186530_1_.has("parameters")) {
               lvt_8_2_ = lvt_7_1_.deserialize(JSONUtils.getJsonObject(p_186530_1_, "parameters"), p_186530_2_);
            } else {
               lvt_8_2_ = lvt_7_1_.deserialize(new JsonObject(), p_186530_2_);
            }

            return new ApplyBonus(p_186530_3_, lvt_5_1_, lvt_8_2_);
         }
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }

   static final class OreDropsFormula implements ApplyBonus.IFormula {
      public static final ResourceLocation field_216206_a = new ResourceLocation("ore_drops");

      private OreDropsFormula() {
      }

      public int func_216204_a(Random p_216204_1_, int p_216204_2_, int p_216204_3_) {
         if (p_216204_3_ > 0) {
            int lvt_4_1_ = p_216204_1_.nextInt(p_216204_3_ + 2) - 1;
            if (lvt_4_1_ < 0) {
               lvt_4_1_ = 0;
            }

            return p_216204_2_ * (lvt_4_1_ + 1);
         } else {
            return p_216204_2_;
         }
      }

      public void func_216202_a(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_) {
      }

      public static ApplyBonus.IFormula func_216205_a(JsonObject p_216205_0_, JsonDeserializationContext p_216205_1_) {
         return new ApplyBonus.OreDropsFormula();
      }

      public ResourceLocation func_216203_a() {
         return field_216206_a;
      }

      // $FF: synthetic method
      OreDropsFormula(Object p_i50982_1_) {
         this();
      }
   }

   static final class UniformBonusCountFormula implements ApplyBonus.IFormula {
      public static final ResourceLocation field_216208_a = new ResourceLocation("uniform_bonus_count");
      private final int bonusMultiplier;

      public UniformBonusCountFormula(int p_i50981_1_) {
         this.bonusMultiplier = p_i50981_1_;
      }

      public int func_216204_a(Random p_216204_1_, int p_216204_2_, int p_216204_3_) {
         return p_216204_2_ + p_216204_1_.nextInt(this.bonusMultiplier * p_216204_3_ + 1);
      }

      public void func_216202_a(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_) {
         p_216202_1_.addProperty("bonusMultiplier", this.bonusMultiplier);
      }

      public static ApplyBonus.IFormula func_216207_a(JsonObject p_216207_0_, JsonDeserializationContext p_216207_1_) {
         int lvt_2_1_ = JSONUtils.getInt(p_216207_0_, "bonusMultiplier");
         return new ApplyBonus.UniformBonusCountFormula(lvt_2_1_);
      }

      public ResourceLocation func_216203_a() {
         return field_216208_a;
      }
   }

   static final class BinomialWithBonusCountFormula implements ApplyBonus.IFormula {
      public static final ResourceLocation field_216211_a = new ResourceLocation("binomial_with_bonus_count");
      private final int extra;
      private final float probability;

      public BinomialWithBonusCountFormula(int p_i50983_1_, float p_i50983_2_) {
         this.extra = p_i50983_1_;
         this.probability = p_i50983_2_;
      }

      public int func_216204_a(Random p_216204_1_, int p_216204_2_, int p_216204_3_) {
         for(int lvt_4_1_ = 0; lvt_4_1_ < p_216204_3_ + this.extra; ++lvt_4_1_) {
            if (p_216204_1_.nextFloat() < this.probability) {
               ++p_216204_2_;
            }
         }

         return p_216204_2_;
      }

      public void func_216202_a(JsonObject p_216202_1_, JsonSerializationContext p_216202_2_) {
         p_216202_1_.addProperty("extra", this.extra);
         p_216202_1_.addProperty("probability", this.probability);
      }

      public static ApplyBonus.IFormula func_216210_a(JsonObject p_216210_0_, JsonDeserializationContext p_216210_1_) {
         int lvt_2_1_ = JSONUtils.getInt(p_216210_0_, "extra");
         float lvt_3_1_ = JSONUtils.getFloat(p_216210_0_, "probability");
         return new ApplyBonus.BinomialWithBonusCountFormula(lvt_2_1_, lvt_3_1_);
      }

      public ResourceLocation func_216203_a() {
         return field_216211_a;
      }
   }

   interface IFormulaDeserializer {
      ApplyBonus.IFormula deserialize(JsonObject var1, JsonDeserializationContext var2);
   }

   interface IFormula {
      int func_216204_a(Random var1, int var2, int var3);

      void func_216202_a(JsonObject var1, JsonSerializationContext var2);

      ResourceLocation func_216203_a();
   }
}
