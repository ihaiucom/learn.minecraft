package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class LootFunctionManager {
   private static final Map<ResourceLocation, ILootFunction.Serializer<?>> NAME_TO_SERIALIZER_MAP = Maps.newHashMap();
   private static final Map<Class<? extends ILootFunction>, ILootFunction.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();
   public static final BiFunction<ItemStack, LootContext, ItemStack> IDENTITY = (p_216240_0_, p_216240_1_) -> {
      return p_216240_0_;
   };

   public static <T extends ILootFunction> void registerFunction(ILootFunction.Serializer<? extends T> p_186582_0_) {
      ResourceLocation lvt_1_1_ = p_186582_0_.getFunctionName();
      Class<T> lvt_2_1_ = p_186582_0_.getFunctionClass();
      if (NAME_TO_SERIALIZER_MAP.containsKey(lvt_1_1_)) {
         throw new IllegalArgumentException("Can't re-register item function name " + lvt_1_1_);
      } else if (CLASS_TO_SERIALIZER_MAP.containsKey(lvt_2_1_)) {
         throw new IllegalArgumentException("Can't re-register item function class " + lvt_2_1_.getName());
      } else {
         NAME_TO_SERIALIZER_MAP.put(lvt_1_1_, p_186582_0_);
         CLASS_TO_SERIALIZER_MAP.put(lvt_2_1_, p_186582_0_);
      }
   }

   public static ILootFunction.Serializer<?> getSerializerForName(ResourceLocation p_186583_0_) {
      ILootFunction.Serializer<?> lvt_1_1_ = (ILootFunction.Serializer)NAME_TO_SERIALIZER_MAP.get(p_186583_0_);
      if (lvt_1_1_ == null) {
         throw new IllegalArgumentException("Unknown loot item function '" + p_186583_0_ + "'");
      } else {
         return lvt_1_1_;
      }
   }

   public static <T extends ILootFunction> ILootFunction.Serializer<T> getSerializerFor(T p_186581_0_) {
      ILootFunction.Serializer<T> lvt_1_1_ = (ILootFunction.Serializer)CLASS_TO_SERIALIZER_MAP.get(p_186581_0_.getClass());
      if (lvt_1_1_ == null) {
         throw new IllegalArgumentException("Unknown loot item function " + p_186581_0_);
      } else {
         return lvt_1_1_;
      }
   }

   public static BiFunction<ItemStack, LootContext, ItemStack> combine(BiFunction<ItemStack, LootContext, ItemStack>[] p_216241_0_) {
      switch(p_216241_0_.length) {
      case 0:
         return IDENTITY;
      case 1:
         return p_216241_0_[0];
      case 2:
         BiFunction<ItemStack, LootContext, ItemStack> lvt_1_1_ = p_216241_0_[0];
         BiFunction<ItemStack, LootContext, ItemStack> lvt_2_1_ = p_216241_0_[1];
         return (p_216239_2_, p_216239_3_) -> {
            return (ItemStack)lvt_2_1_.apply(lvt_1_1_.apply(p_216239_2_, p_216239_3_), p_216239_3_);
         };
      default:
         return (p_216238_1_, p_216238_2_) -> {
            BiFunction[] var3 = p_216241_0_;
            int var4 = p_216241_0_.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               BiFunction<ItemStack, LootContext, ItemStack> lvt_6_1_ = var3[var5];
               p_216238_1_ = (ItemStack)lvt_6_1_.apply(p_216238_1_, p_216238_2_);
            }

            return p_216238_1_;
         };
      }
   }

   static {
      registerFunction(new SetCount.Serializer());
      registerFunction(new EnchantWithLevels.Serializer());
      registerFunction(new EnchantRandomly.Serializer());
      registerFunction(new SetNBT.Serializer());
      registerFunction(new Smelt.Serializer());
      registerFunction(new LootingEnchantBonus.Serializer());
      registerFunction(new SetDamage.Serializer());
      registerFunction(new SetAttributes.Serializer());
      registerFunction(new SetName.Serializer());
      registerFunction(new ExplorationMap.Serializer());
      registerFunction(new SetStewEffect.Serializer());
      registerFunction(new CopyName.Serializer());
      registerFunction(new SetContents.Serializer());
      registerFunction(new LimitCount.Serializer());
      registerFunction(new ApplyBonus.Serializer());
      registerFunction(new SetLootTable.Serializer());
      registerFunction(new ExplosionDecay.Serializer());
      registerFunction(new SetLore.Serializer());
      registerFunction(new FillPlayerHead.Serializer());
      registerFunction(new CopyNbt.Serializer());
      registerFunction(new CopyBlockState.Serializer());
   }

   public static class Serializer implements JsonDeserializer<ILootFunction>, JsonSerializer<ILootFunction> {
      public ILootFunction deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = JSONUtils.getJsonObject(p_deserialize_1_, "function");
         ResourceLocation lvt_5_1_ = new ResourceLocation(JSONUtils.getString(lvt_4_1_, "function"));

         ILootFunction.Serializer lvt_6_2_;
         try {
            lvt_6_2_ = LootFunctionManager.getSerializerForName(lvt_5_1_);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown function '" + lvt_5_1_ + "'");
         }

         return lvt_6_2_.deserialize(lvt_4_1_, p_deserialize_3_);
      }

      public JsonElement serialize(ILootFunction p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         ILootFunction.Serializer<ILootFunction> lvt_4_1_ = LootFunctionManager.getSerializerFor(p_serialize_1_);
         JsonObject lvt_5_1_ = new JsonObject();
         lvt_5_1_.addProperty("function", lvt_4_1_.getFunctionName().toString());
         lvt_4_1_.serialize(lvt_5_1_, p_serialize_1_, p_serialize_3_);
         return lvt_5_1_;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((ILootFunction)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
