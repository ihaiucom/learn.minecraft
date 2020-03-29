package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ResourceLocation implements Comparable<ResourceLocation> {
   private static final SimpleCommandExceptionType INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("argument.id.invalid", new Object[0]));
   protected final String namespace;
   protected final String path;

   protected ResourceLocation(String[] p_i47923_1_) {
      this.namespace = org.apache.commons.lang3.StringUtils.isEmpty(p_i47923_1_[0]) ? "minecraft" : p_i47923_1_[0];
      this.path = p_i47923_1_[1];
      if (!func_217858_d(this.namespace)) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
      } else if (!func_217856_c(this.path)) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
      }
   }

   public ResourceLocation(String p_i1293_1_) {
      this(decompose(p_i1293_1_, ':'));
   }

   public ResourceLocation(String p_i1292_1_, String p_i1292_2_) {
      this(new String[]{p_i1292_1_, p_i1292_2_});
   }

   public static ResourceLocation create(String p_195828_0_, char p_195828_1_) {
      return new ResourceLocation(decompose(p_195828_0_, p_195828_1_));
   }

   @Nullable
   public static ResourceLocation tryCreate(String p_208304_0_) {
      try {
         return new ResourceLocation(p_208304_0_);
      } catch (ResourceLocationException var2) {
         return null;
      }
   }

   protected static String[] decompose(String p_195823_0_, char p_195823_1_) {
      String[] lvt_2_1_ = new String[]{"minecraft", p_195823_0_};
      int lvt_3_1_ = p_195823_0_.indexOf(p_195823_1_);
      if (lvt_3_1_ >= 0) {
         lvt_2_1_[1] = p_195823_0_.substring(lvt_3_1_ + 1, p_195823_0_.length());
         if (lvt_3_1_ >= 1) {
            lvt_2_1_[0] = p_195823_0_.substring(0, lvt_3_1_);
         }
      }

      return lvt_2_1_;
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public String toString() {
      return this.namespace + ':' + this.path;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation lvt_2_1_ = (ResourceLocation)p_equals_1_;
         return this.namespace.equals(lvt_2_1_.namespace) && this.path.equals(lvt_2_1_.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(ResourceLocation p_compareTo_1_) {
      int lvt_2_1_ = this.path.compareTo(p_compareTo_1_.path);
      if (lvt_2_1_ == 0) {
         lvt_2_1_ = this.namespace.compareTo(p_compareTo_1_.namespace);
      }

      return lvt_2_1_;
   }

   public static ResourceLocation read(StringReader p_195826_0_) throws CommandSyntaxException {
      int lvt_1_1_ = p_195826_0_.getCursor();

      while(p_195826_0_.canRead() && isValidPathCharacter(p_195826_0_.peek())) {
         p_195826_0_.skip();
      }

      String lvt_2_1_ = p_195826_0_.getString().substring(lvt_1_1_, p_195826_0_.getCursor());

      try {
         return new ResourceLocation(lvt_2_1_);
      } catch (ResourceLocationException var4) {
         p_195826_0_.setCursor(lvt_1_1_);
         throw INVALID_EXCEPTION.createWithContext(p_195826_0_);
      }
   }

   public static boolean isValidPathCharacter(char p_195824_0_) {
      return p_195824_0_ >= '0' && p_195824_0_ <= '9' || p_195824_0_ >= 'a' && p_195824_0_ <= 'z' || p_195824_0_ == '_' || p_195824_0_ == ':' || p_195824_0_ == '/' || p_195824_0_ == '.' || p_195824_0_ == '-';
   }

   private static boolean func_217856_c(String p_217856_0_) {
      return p_217856_0_.chars().allMatch((p_217857_0_) -> {
         return p_217857_0_ == 95 || p_217857_0_ == 45 || p_217857_0_ >= 97 && p_217857_0_ <= 122 || p_217857_0_ >= 48 && p_217857_0_ <= 57 || p_217857_0_ == 47 || p_217857_0_ == 46;
      });
   }

   private static boolean func_217858_d(String p_217858_0_) {
      return p_217858_0_.chars().allMatch((p_217859_0_) -> {
         return p_217859_0_ == 95 || p_217859_0_ == 45 || p_217859_0_ >= 97 && p_217859_0_ <= 122 || p_217859_0_ >= 48 && p_217859_0_ <= 57 || p_217859_0_ == 46;
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean func_217855_b(String p_217855_0_) {
      String[] lvt_1_1_ = decompose(p_217855_0_, ':');
      return func_217858_d(org.apache.commons.lang3.StringUtils.isEmpty(lvt_1_1_[0]) ? "minecraft" : lvt_1_1_[0]) && func_217856_c(lvt_1_1_[1]);
   }

   // $FF: synthetic method
   public int compareTo(Object p_compareTo_1_) {
      return this.compareTo((ResourceLocation)p_compareTo_1_);
   }

   public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
      public ResourceLocation deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return new ResourceLocation(JSONUtils.getString(p_deserialize_1_, "location"));
      }

      public JsonElement serialize(ResourceLocation p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return new JsonPrimitive(p_serialize_1_.toString());
      }

      // $FF: synthetic method
      public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return this.serialize((ResourceLocation)p_serialize_1_, p_serialize_2_, p_serialize_3_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
