package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Variant implements IModelTransform {
   private final ResourceLocation modelLocation;
   private final TransformationMatrix rotation;
   private final boolean uvLock;
   private final int weight;

   public Variant(ResourceLocation p_i226001_1_, TransformationMatrix p_i226001_2_, boolean p_i226001_3_, int p_i226001_4_) {
      this.modelLocation = p_i226001_1_;
      this.rotation = p_i226001_2_;
      this.uvLock = p_i226001_3_;
      this.weight = p_i226001_4_;
   }

   public ResourceLocation getModelLocation() {
      return this.modelLocation;
   }

   public TransformationMatrix func_225615_b_() {
      return this.rotation;
   }

   public boolean isUvLock() {
      return this.uvLock;
   }

   public int getWeight() {
      return this.weight;
   }

   public String toString() {
      return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Variant)) {
         return false;
      } else {
         Variant lvt_2_1_ = (Variant)p_equals_1_;
         return this.modelLocation.equals(lvt_2_1_.modelLocation) && Objects.equals(this.rotation, lvt_2_1_.rotation) && this.uvLock == lvt_2_1_.uvLock && this.weight == lvt_2_1_.weight;
      }
   }

   public int hashCode() {
      int lvt_1_1_ = this.modelLocation.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.rotation.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + Boolean.valueOf(this.uvLock).hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.weight;
      return lvt_1_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Variant> {
      public Variant deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
         ResourceLocation lvt_5_1_ = this.getStringModel(lvt_4_1_);
         ModelRotation lvt_6_1_ = this.parseModelRotation(lvt_4_1_);
         boolean lvt_7_1_ = this.parseUvLock(lvt_4_1_);
         int lvt_8_1_ = this.parseWeight(lvt_4_1_);
         return new Variant(lvt_5_1_, lvt_6_1_.func_225615_b_(), lvt_7_1_, lvt_8_1_);
      }

      private boolean parseUvLock(JsonObject p_188044_1_) {
         return JSONUtils.getBoolean(p_188044_1_, "uvlock", false);
      }

      protected ModelRotation parseModelRotation(JsonObject p_188042_1_) {
         int lvt_2_1_ = JSONUtils.getInt(p_188042_1_, "x", 0);
         int lvt_3_1_ = JSONUtils.getInt(p_188042_1_, "y", 0);
         ModelRotation lvt_4_1_ = ModelRotation.getModelRotation(lvt_2_1_, lvt_3_1_);
         if (lvt_4_1_ == null) {
            throw new JsonParseException("Invalid BlockModelRotation x: " + lvt_2_1_ + ", y: " + lvt_3_1_);
         } else {
            return lvt_4_1_;
         }
      }

      protected ResourceLocation getStringModel(JsonObject p_188043_1_) {
         return new ResourceLocation(JSONUtils.getString(p_188043_1_, "model"));
      }

      protected int parseWeight(JsonObject p_188045_1_) {
         int lvt_2_1_ = JSONUtils.getInt(p_188045_1_, "weight", 1);
         if (lvt_2_1_ < 1) {
            throw new JsonParseException("Invalid weight " + lvt_2_1_ + " found, expected integer >= 1");
         } else {
            return lvt_2_1_;
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
