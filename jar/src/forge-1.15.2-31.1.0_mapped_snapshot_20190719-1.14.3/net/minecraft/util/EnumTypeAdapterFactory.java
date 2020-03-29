package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {
   @Nullable
   public <T> TypeAdapter<T> create(Gson p_create_1_, TypeToken<T> p_create_2_) {
      Class<T> lvt_3_1_ = p_create_2_.getRawType();
      if (!lvt_3_1_.isEnum()) {
         return null;
      } else {
         final Map<String, T> lvt_4_1_ = Maps.newHashMap();
         Object[] var5 = lvt_3_1_.getEnumConstants();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            T lvt_8_1_ = var5[var7];
            lvt_4_1_.put(this.getName(lvt_8_1_), lvt_8_1_);
         }

         return new TypeAdapter<T>() {
            public void write(JsonWriter p_write_1_, T p_write_2_) throws IOException {
               if (p_write_2_ == null) {
                  p_write_1_.nullValue();
               } else {
                  p_write_1_.value(EnumTypeAdapterFactory.this.getName(p_write_2_));
               }

            }

            @Nullable
            public T read(JsonReader p_read_1_) throws IOException {
               if (p_read_1_.peek() == JsonToken.NULL) {
                  p_read_1_.nextNull();
                  return null;
               } else {
                  return lvt_4_1_.get(p_read_1_.nextString());
               }
            }
         };
      }
   }

   private String getName(Object p_151232_1_) {
      return p_151232_1_ instanceof Enum ? ((Enum)p_151232_1_).name().toLowerCase(Locale.ROOT) : p_151232_1_.toString().toLowerCase(Locale.ROOT);
   }
}
