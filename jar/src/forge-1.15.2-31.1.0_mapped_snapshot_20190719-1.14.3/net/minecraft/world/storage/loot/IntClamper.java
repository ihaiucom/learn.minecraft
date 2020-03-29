package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;

public class IntClamper implements IntUnaryOperator {
   private final Integer field_215852_a;
   private final Integer field_215853_b;
   private final IntUnaryOperator field_215854_c;

   private IntClamper(@Nullable Integer p_i51273_1_, @Nullable Integer p_i51273_2_) {
      this.field_215852_a = p_i51273_1_;
      this.field_215853_b = p_i51273_2_;
      int lvt_3_1_;
      if (p_i51273_1_ == null) {
         if (p_i51273_2_ == null) {
            this.field_215854_c = (p_215845_0_) -> {
               return p_215845_0_;
            };
         } else {
            lvt_3_1_ = p_i51273_2_;
            this.field_215854_c = (p_215844_1_) -> {
               return Math.min(lvt_3_1_, p_215844_1_);
            };
         }
      } else {
         lvt_3_1_ = p_i51273_1_;
         if (p_i51273_2_ == null) {
            this.field_215854_c = (p_215846_1_) -> {
               return Math.max(lvt_3_1_, p_215846_1_);
            };
         } else {
            int lvt_4_1_ = p_i51273_2_;
            this.field_215854_c = (p_215847_2_) -> {
               return MathHelper.clamp(p_215847_2_, lvt_3_1_, lvt_4_1_);
            };
         }
      }

   }

   public static IntClamper func_215843_a(int p_215843_0_, int p_215843_1_) {
      return new IntClamper(p_215843_0_, p_215843_1_);
   }

   public static IntClamper func_215848_a(int p_215848_0_) {
      return new IntClamper(p_215848_0_, (Integer)null);
   }

   public static IntClamper func_215851_b(int p_215851_0_) {
      return new IntClamper((Integer)null, p_215851_0_);
   }

   public int applyAsInt(int p_applyAsInt_1_) {
      return this.field_215854_c.applyAsInt(p_applyAsInt_1_);
   }

   // $FF: synthetic method
   IntClamper(Integer p_i51274_1_, Integer p_i51274_2_, Object p_i51274_3_) {
      this(p_i51274_1_, p_i51274_2_);
   }

   public static class Serializer implements JsonDeserializer<IntClamper>, JsonSerializer<IntClamper> {
      public IntClamper deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = JSONUtils.getJsonObject(p_deserialize_1_, "value");
         Integer lvt_5_1_ = lvt_4_1_.has("min") ? JSONUtils.getInt(lvt_4_1_, "min") : null;
         Integer lvt_6_1_ = lvt_4_1_.has("max") ? JSONUtils.getInt(lvt_4_1_, "max") : null;
         return new IntClamper(lvt_5_1_, lvt_6_1_);
      }

      public JsonElement serialize(IntClamper p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject lvt_4_1_ = new JsonObject();
         if (p_serialize_1_.field_215853_b != null) {
            lvt_4_1_.addProperty("max", p_serialize_1_.field_215853_b);
         }

         if (p_serialize_1_.field_215852_a != null) {
            lvt_4_1_.addProperty("min", p_serialize_1_.field_215852_a);
         }

         return lvt_4_1_;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((IntClamper)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
