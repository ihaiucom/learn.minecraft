package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class BinomialRange implements IRandomRange {
   private final int n;
   private final float p;

   public BinomialRange(int p_i51276_1_, float p_i51276_2_) {
      this.n = p_i51276_1_;
      this.p = p_i51276_2_;
   }

   public int generateInt(Random p_186511_1_) {
      int lvt_2_1_ = 0;

      for(int lvt_3_1_ = 0; lvt_3_1_ < this.n; ++lvt_3_1_) {
         if (p_186511_1_.nextFloat() < this.p) {
            ++lvt_2_1_;
         }
      }

      return lvt_2_1_;
   }

   public static BinomialRange func_215838_a(int p_215838_0_, float p_215838_1_) {
      return new BinomialRange(p_215838_0_, p_215838_1_);
   }

   public ResourceLocation func_215830_a() {
      return BINOMIAL;
   }

   public static class Serializer implements JsonDeserializer<BinomialRange>, JsonSerializer<BinomialRange> {
      public BinomialRange deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = JSONUtils.getJsonObject(p_deserialize_1_, "value");
         int lvt_5_1_ = JSONUtils.getInt(lvt_4_1_, "n");
         float lvt_6_1_ = JSONUtils.getFloat(lvt_4_1_, "p");
         return new BinomialRange(lvt_5_1_, lvt_6_1_);
      }

      public JsonElement serialize(BinomialRange p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject lvt_4_1_ = new JsonObject();
         lvt_4_1_.addProperty("n", p_serialize_1_.n);
         lvt_4_1_.addProperty("p", p_serialize_1_.p);
         return lvt_4_1_;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((BinomialRange)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
