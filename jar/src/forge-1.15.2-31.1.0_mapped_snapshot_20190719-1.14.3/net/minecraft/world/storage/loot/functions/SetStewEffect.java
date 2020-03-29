package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.potion.Effect;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetStewEffect extends LootFunction {
   private final Map<Effect, RandomValueRange> field_215950_a;

   private SetStewEffect(ILootCondition[] p_i51215_1_, Map<Effect, RandomValueRange> p_i51215_2_) {
      super(p_i51215_1_);
      this.field_215950_a = ImmutableMap.copyOf(p_i51215_2_);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.getItem() == Items.SUSPICIOUS_STEW && !this.field_215950_a.isEmpty()) {
         Random lvt_3_1_ = p_215859_2_.getRandom();
         int lvt_4_1_ = lvt_3_1_.nextInt(this.field_215950_a.size());
         Entry<Effect, RandomValueRange> lvt_5_1_ = (Entry)Iterables.get(this.field_215950_a.entrySet(), lvt_4_1_);
         Effect lvt_6_1_ = (Effect)lvt_5_1_.getKey();
         int lvt_7_1_ = ((RandomValueRange)lvt_5_1_.getValue()).generateInt(lvt_3_1_);
         if (!lvt_6_1_.isInstant()) {
            lvt_7_1_ *= 20;
         }

         SuspiciousStewItem.addEffect(p_215859_1_, lvt_6_1_, lvt_7_1_);
         return p_215859_1_;
      } else {
         return p_215859_1_;
      }
   }

   public static SetStewEffect.Builder func_215948_b() {
      return new SetStewEffect.Builder();
   }

   // $FF: synthetic method
   SetStewEffect(ILootCondition[] p_i51216_1_, Map p_i51216_2_, Object p_i51216_3_) {
      this(p_i51216_1_, p_i51216_2_);
   }

   public static class Serializer extends LootFunction.Serializer<SetStewEffect> {
      public Serializer() {
         super(new ResourceLocation("set_stew_effect"), SetStewEffect.class);
      }

      public void serialize(JsonObject p_186532_1_, SetStewEffect p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         if (!p_186532_2_.field_215950_a.isEmpty()) {
            JsonArray lvt_4_1_ = new JsonArray();
            Iterator var5 = p_186532_2_.field_215950_a.keySet().iterator();

            while(var5.hasNext()) {
               Effect lvt_6_1_ = (Effect)var5.next();
               JsonObject lvt_7_1_ = new JsonObject();
               ResourceLocation lvt_8_1_ = Registry.EFFECTS.getKey(lvt_6_1_);
               if (lvt_8_1_ == null) {
                  throw new IllegalArgumentException("Don't know how to serialize mob effect " + lvt_6_1_);
               }

               lvt_7_1_.add("type", new JsonPrimitive(lvt_8_1_.toString()));
               lvt_7_1_.add("duration", p_186532_3_.serialize(p_186532_2_.field_215950_a.get(lvt_6_1_)));
               lvt_4_1_.add(lvt_7_1_);
            }

            p_186532_1_.add("effects", lvt_4_1_);
         }

      }

      public SetStewEffect deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         Map<Effect, RandomValueRange> lvt_4_1_ = Maps.newHashMap();
         if (p_186530_1_.has("effects")) {
            JsonArray lvt_5_1_ = JSONUtils.getJsonArray(p_186530_1_, "effects");
            Iterator var6 = lvt_5_1_.iterator();

            while(var6.hasNext()) {
               JsonElement lvt_7_1_ = (JsonElement)var6.next();
               String lvt_8_1_ = JSONUtils.getString(lvt_7_1_.getAsJsonObject(), "type");
               Effect lvt_9_1_ = (Effect)Registry.EFFECTS.getValue(new ResourceLocation(lvt_8_1_)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown mob effect '" + lvt_8_1_ + "'");
               });
               RandomValueRange lvt_10_1_ = (RandomValueRange)JSONUtils.deserializeClass(lvt_7_1_.getAsJsonObject(), "duration", p_186530_2_, RandomValueRange.class);
               lvt_4_1_.put(lvt_9_1_, lvt_10_1_);
            }
         }

         return new SetStewEffect(p_186530_3_, lvt_4_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }

   public static class Builder extends LootFunction.Builder<SetStewEffect.Builder> {
      private final Map<Effect, RandomValueRange> field_216078_a = Maps.newHashMap();

      protected SetStewEffect.Builder doCast() {
         return this;
      }

      public SetStewEffect.Builder func_216077_a(Effect p_216077_1_, RandomValueRange p_216077_2_) {
         this.field_216078_a.put(p_216077_1_, p_216077_2_);
         return this;
      }

      public ILootFunction build() {
         return new SetStewEffect(this.getConditions(), this.field_216078_a);
      }

      // $FF: synthetic method
      protected LootFunction.Builder doCast() {
         return this.doCast();
      }
   }
}
