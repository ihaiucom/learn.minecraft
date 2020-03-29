package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
   private final String name;
   private final List<LootEntry> lootEntries;
   private final List<ILootCondition> conditions;
   private final Predicate<LootContext> combinedConditions;
   private final ILootFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;
   private IRandomRange rolls;
   private RandomValueRange bonusRolls;
   private boolean isFrozen;

   private LootPool(LootEntry[] p_i230100_1_, ILootCondition[] p_i230100_2_, ILootFunction[] p_i230100_3_, IRandomRange p_i230100_4_, RandomValueRange p_i230100_5_, String p_i230100_6_) {
      this.isFrozen = false;
      this.name = p_i230100_6_;
      this.lootEntries = Lists.newArrayList(p_i230100_1_);
      this.conditions = Lists.newArrayList(p_i230100_2_);
      this.combinedConditions = LootConditionManager.and(p_i230100_2_);
      this.functions = p_i230100_3_;
      this.combinedFunctions = LootFunctionManager.combine(p_i230100_3_);
      this.rolls = p_i230100_4_;
      this.bonusRolls = p_i230100_5_;
   }

   private void generateRoll(Consumer<ItemStack> p_216095_1_, LootContext p_216095_2_) {
      Random random = p_216095_2_.getRandom();
      List<ILootGenerator> list = Lists.newArrayList();
      MutableInt mutableint = new MutableInt();
      Iterator var6 = this.lootEntries.iterator();

      while(var6.hasNext()) {
         LootEntry lootentry = (LootEntry)var6.next();
         lootentry.expand(p_216095_2_, (p_lambda$generateRoll$0_3_) -> {
            int k = p_lambda$generateRoll$0_3_.getEffectiveWeight(p_216095_2_.getLuck());
            if (k > 0) {
               list.add(p_lambda$generateRoll$0_3_);
               mutableint.add(k);
            }

         });
      }

      int i = list.size();
      if (mutableint.intValue() != 0 && i != 0) {
         if (i == 1) {
            ((ILootGenerator)list.get(0)).func_216188_a(p_216095_1_, p_216095_2_);
         } else {
            int j = random.nextInt(mutableint.intValue());
            Iterator var8 = list.iterator();

            while(var8.hasNext()) {
               ILootGenerator ilootgenerator = (ILootGenerator)var8.next();
               j -= ilootgenerator.getEffectiveWeight(p_216095_2_.getLuck());
               if (j < 0) {
                  ilootgenerator.func_216188_a(p_216095_1_, p_216095_2_);
                  return;
               }
            }
         }
      }

   }

   public void generate(Consumer<ItemStack> p_216091_1_, LootContext p_216091_2_) {
      if (this.combinedConditions.test(p_216091_2_)) {
         Consumer<ItemStack> consumer = ILootFunction.func_215858_a(this.combinedFunctions, p_216091_1_, p_216091_2_);
         Random random = p_216091_2_.getRandom();
         int i = this.rolls.generateInt(random) + MathHelper.floor(this.bonusRolls.generateFloat(random) * p_216091_2_.getLuck());

         for(int j = 0; j < i; ++j) {
            this.generateRoll(consumer, p_216091_2_);
         }
      }

   }

   public void func_227505_a_(ValidationTracker p_227505_1_) {
      int k;
      for(k = 0; k < this.conditions.size(); ++k) {
         ((ILootCondition)this.conditions.get(k)).func_225580_a_(p_227505_1_.func_227534_b_(".condition[" + k + "]"));
      }

      for(k = 0; k < this.functions.length; ++k) {
         this.functions[k].func_225580_a_(p_227505_1_.func_227534_b_(".functions[" + k + "]"));
      }

      for(k = 0; k < this.lootEntries.size(); ++k) {
         ((LootEntry)this.lootEntries.get(k)).func_225579_a_(p_227505_1_.func_227534_b_(".entries[" + k + "]"));
      }

   }

   public static LootPool.Builder builder() {
      return new LootPool.Builder();
   }

   public void freeze() {
      this.isFrozen = true;
   }

   public boolean isFrozen() {
      return this.isFrozen;
   }

   private void checkFrozen() {
      if (this.isFrozen()) {
         throw new RuntimeException("Attempted to modify LootPool after being frozen!");
      }
   }

   public String getName() {
      return this.name;
   }

   public IRandomRange getRolls() {
      return this.rolls;
   }

   public IRandomRange getBonusRolls() {
      return this.bonusRolls;
   }

   public void setRolls(RandomValueRange p_setRolls_1_) {
      this.checkFrozen();
      this.rolls = p_setRolls_1_;
   }

   public void setBonusRolls(RandomValueRange p_setBonusRolls_1_) {
      this.checkFrozen();
      this.bonusRolls = p_setBonusRolls_1_;
   }

   // $FF: synthetic method
   LootPool(LootEntry[] p_i230101_1_, ILootCondition[] p_i230101_2_, ILootFunction[] p_i230101_3_, IRandomRange p_i230101_4_, RandomValueRange p_i230101_5_, String p_i230101_6_, Object p_i230101_7_) {
      this(p_i230101_1_, p_i230101_2_, p_i230101_3_, p_i230101_4_, p_i230101_5_, p_i230101_6_);
   }

   public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {
      public LootPool deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "loot pool");
         LootEntry[] alootentry = (LootEntry[])JSONUtils.deserializeClass(jsonobject, "entries", p_deserialize_3_, LootEntry[].class);
         ILootCondition[] ailootcondition = (ILootCondition[])JSONUtils.deserializeClass(jsonobject, "conditions", new ILootCondition[0], p_deserialize_3_, ILootCondition[].class);
         ILootFunction[] ailootfunction = (ILootFunction[])JSONUtils.deserializeClass(jsonobject, "functions", new ILootFunction[0], p_deserialize_3_, ILootFunction[].class);
         IRandomRange irandomrange = RandomRanges.deserialize(jsonobject.get("rolls"), p_deserialize_3_);
         RandomValueRange randomvaluerange = (RandomValueRange)JSONUtils.deserializeClass(jsonobject, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), p_deserialize_3_, RandomValueRange.class);
         return new LootPool(alootentry, ailootcondition, ailootfunction, irandomrange, randomvaluerange, ForgeHooks.readPoolName(jsonobject));
      }

      public JsonElement serialize(LootPool p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (p_serialize_1_.name != null && !p_serialize_1_.name.startsWith("custom#")) {
            jsonobject.add("name", p_serialize_3_.serialize(p_serialize_1_.name));
         }

         jsonobject.add("rolls", RandomRanges.serialize(p_serialize_1_.rolls, p_serialize_3_));
         jsonobject.add("entries", p_serialize_3_.serialize(p_serialize_1_.lootEntries));
         if (p_serialize_1_.bonusRolls.getMin() != 0.0F && p_serialize_1_.bonusRolls.getMax() != 0.0F) {
            jsonobject.add("bonus_rolls", p_serialize_3_.serialize(p_serialize_1_.bonusRolls));
         }

         if (!p_serialize_1_.conditions.isEmpty()) {
            jsonobject.add("conditions", p_serialize_3_.serialize(p_serialize_1_.conditions));
         }

         if (!ArrayUtils.isEmpty((Object[])p_serialize_1_.functions)) {
            jsonobject.add("functions", p_serialize_3_.serialize(p_serialize_1_.functions));
         }

         return jsonobject;
      }
   }

   public static class Builder implements ILootFunctionConsumer<LootPool.Builder>, ILootConditionConsumer<LootPool.Builder> {
      private final List<LootEntry> entries = Lists.newArrayList();
      private final List<ILootCondition> conditions = Lists.newArrayList();
      private final List<ILootFunction> functions = Lists.newArrayList();
      private IRandomRange rolls = new RandomValueRange(1.0F);
      private RandomValueRange bonusRolls = new RandomValueRange(0.0F, 0.0F);
      private String name;

      public LootPool.Builder rolls(IRandomRange p_216046_1_) {
         this.rolls = p_216046_1_;
         return this;
      }

      public LootPool.Builder cast() {
         return this;
      }

      public LootPool.Builder addEntry(LootEntry.Builder<?> p_216045_1_) {
         this.entries.add(p_216045_1_.func_216081_b());
         return this;
      }

      public LootPool.Builder acceptCondition(ILootCondition.IBuilder p_212840_1_) {
         this.conditions.add(p_212840_1_.build());
         return this;
      }

      public LootPool.Builder acceptFunction(ILootFunction.IBuilder p_212841_1_) {
         this.functions.add(p_212841_1_.build());
         return this;
      }

      public LootPool.Builder name(String p_name_1_) {
         this.name = p_name_1_;
         return this;
      }

      public LootPool.Builder bonusRolls(float p_bonusRolls_1_, float p_bonusRolls_2_) {
         this.bonusRolls = new RandomValueRange(p_bonusRolls_1_, p_bonusRolls_2_);
         return this;
      }

      public LootPool build() {
         if (this.rolls == null) {
            throw new IllegalArgumentException("Rolls not set");
         } else {
            return new LootPool((LootEntry[])this.entries.toArray(new LootEntry[0]), (ILootCondition[])this.conditions.toArray(new ILootCondition[0]), (ILootFunction[])this.functions.toArray(new ILootFunction[0]), this.rolls, this.bonusRolls, this.name);
         }
      }
   }
}
