package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class ConstantRange implements IRandomRange {
   private final int value;

   public ConstantRange(int p_i51275_1_) {
      this.value = p_i51275_1_;
   }

   public int generateInt(Random p_186511_1_) {
      return this.value;
   }

   public ResourceLocation func_215830_a() {
      return CONSTANT;
   }

   public static ConstantRange of(int p_215835_0_) {
      return new ConstantRange(p_215835_0_);
   }

   public static class Serializer implements JsonDeserializer<ConstantRange>, JsonSerializer<ConstantRange> {
      public ConstantRange deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return new ConstantRange(JSONUtils.getInt(p_deserialize_1_, "value"));
      }

      public JsonElement serialize(ConstantRange p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return new JsonPrimitive(p_serialize_1_.value);
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((ConstantRange)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
