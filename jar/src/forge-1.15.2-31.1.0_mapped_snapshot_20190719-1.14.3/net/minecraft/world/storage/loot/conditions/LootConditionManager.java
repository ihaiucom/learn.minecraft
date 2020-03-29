package net.minecraft.world.storage.loot.conditions;

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
import java.util.function.Predicate;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class LootConditionManager {
   private static final Map<ResourceLocation, ILootCondition.AbstractSerializer<?>> BY_NAME = Maps.newHashMap();
   private static final Map<Class<? extends ILootCondition>, ILootCondition.AbstractSerializer<?>> BY_CLASS = Maps.newHashMap();

   public static <T extends ILootCondition> void registerCondition(ILootCondition.AbstractSerializer<? extends T> p_186639_0_) {
      ResourceLocation lvt_1_1_ = p_186639_0_.getLootTableLocation();
      Class<T> lvt_2_1_ = p_186639_0_.getConditionClass();
      if (BY_NAME.containsKey(lvt_1_1_)) {
         throw new IllegalArgumentException("Can't re-register item condition name " + lvt_1_1_);
      } else if (BY_CLASS.containsKey(lvt_2_1_)) {
         throw new IllegalArgumentException("Can't re-register item condition class " + lvt_2_1_.getName());
      } else {
         BY_NAME.put(lvt_1_1_, p_186639_0_);
         BY_CLASS.put(lvt_2_1_, p_186639_0_);
      }
   }

   public static ILootCondition.AbstractSerializer<?> getSerializerForName(ResourceLocation p_186641_0_) {
      ILootCondition.AbstractSerializer<?> lvt_1_1_ = (ILootCondition.AbstractSerializer)BY_NAME.get(p_186641_0_);
      if (lvt_1_1_ == null) {
         throw new IllegalArgumentException("Unknown loot item condition '" + p_186641_0_ + "'");
      } else {
         return lvt_1_1_;
      }
   }

   public static <T extends ILootCondition> ILootCondition.AbstractSerializer<T> getSerializerFor(T p_186640_0_) {
      ILootCondition.AbstractSerializer<T> lvt_1_1_ = (ILootCondition.AbstractSerializer)BY_CLASS.get(p_186640_0_.getClass());
      if (lvt_1_1_ == null) {
         throw new IllegalArgumentException("Unknown loot item condition " + p_186640_0_);
      } else {
         return lvt_1_1_;
      }
   }

   public static <T> Predicate<T> and(Predicate<T>[] p_216305_0_) {
      switch(p_216305_0_.length) {
      case 0:
         return (p_216304_0_) -> {
            return true;
         };
      case 1:
         return p_216305_0_[0];
      case 2:
         return p_216305_0_[0].and(p_216305_0_[1]);
      default:
         return (p_216307_1_) -> {
            Predicate[] var2 = p_216305_0_;
            int var3 = p_216305_0_.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Predicate<T> lvt_5_1_ = var2[var4];
               if (!lvt_5_1_.test(p_216307_1_)) {
                  return false;
               }
            }

            return true;
         };
      }
   }

   public static <T> Predicate<T> or(Predicate<T>[] p_216306_0_) {
      switch(p_216306_0_.length) {
      case 0:
         return (p_216308_0_) -> {
            return false;
         };
      case 1:
         return p_216306_0_[0];
      case 2:
         return p_216306_0_[0].or(p_216306_0_[1]);
      default:
         return (p_216309_1_) -> {
            Predicate[] var2 = p_216306_0_;
            int var3 = p_216306_0_.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Predicate<T> lvt_5_1_ = var2[var4];
               if (lvt_5_1_.test(p_216309_1_)) {
                  return true;
               }
            }

            return false;
         };
      }
   }

   static {
      registerCondition(new Inverted.Serializer());
      registerCondition(new Alternative.Serializer());
      registerCondition(new RandomChance.Serializer());
      registerCondition(new RandomChanceWithLooting.Serializer());
      registerCondition(new EntityHasProperty.Serializer());
      registerCondition(new KilledByPlayer.Serializer());
      registerCondition(new EntityHasScore.Serializer());
      registerCondition(new BlockStateProperty.Serializer());
      registerCondition(new MatchTool.Serializer());
      registerCondition(new TableBonus.Serializer());
      registerCondition(new SurvivesExplosion.Serializer());
      registerCondition(new DamageSourceProperties.Serializer());
      registerCondition(new LocationCheck.Serializer());
      registerCondition(new WeatherCheck.Serializer());
      registerCondition(new Reference.Serializer());
      registerCondition(new TimeCheck.Serializer());
   }

   public static class Serializer implements JsonDeserializer<ILootCondition>, JsonSerializer<ILootCondition> {
      public ILootCondition deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = JSONUtils.getJsonObject(p_deserialize_1_, "condition");
         ResourceLocation lvt_5_1_ = new ResourceLocation(JSONUtils.getString(lvt_4_1_, "condition"));

         ILootCondition.AbstractSerializer lvt_6_2_;
         try {
            lvt_6_2_ = LootConditionManager.getSerializerForName(lvt_5_1_);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown condition '" + lvt_5_1_ + "'");
         }

         return lvt_6_2_.deserialize(lvt_4_1_, p_deserialize_3_);
      }

      public JsonElement serialize(ILootCondition p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         ILootCondition.AbstractSerializer<ILootCondition> lvt_4_1_ = LootConditionManager.getSerializerFor(p_serialize_1_);
         JsonObject lvt_5_1_ = new JsonObject();
         lvt_5_1_.addProperty("condition", lvt_4_1_.getLootTableLocation().toString());
         lvt_4_1_.serialize(lvt_5_1_, p_serialize_1_, p_serialize_3_);
         return lvt_5_1_;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((ILootCondition)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
