package net.minecraft.client.renderer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.lang.reflect.Type;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** @deprecated */
@OnlyIn(Dist.CLIENT)
@Deprecated
public class ItemTransformVec3f {
   public static final ItemTransformVec3f DEFAULT = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
   public final Vector3f rotation;
   public final Vector3f translation;
   public final Vector3f scale;

   public ItemTransformVec3f(Vector3f p_i47622_1_, Vector3f p_i47622_2_, Vector3f p_i47622_3_) {
      this.rotation = p_i47622_1_.func_229195_e_();
      this.translation = p_i47622_2_.func_229195_e_();
      this.scale = p_i47622_3_.func_229195_e_();
   }

   public void func_228830_a_(boolean p_228830_1_, MatrixStack p_228830_2_) {
      if (this != DEFAULT) {
         float f = this.rotation.getX();
         float f1 = this.rotation.getY();
         float f2 = this.rotation.getZ();
         if (p_228830_1_) {
            f1 = -f1;
            f2 = -f2;
         }

         int i = p_228830_1_ ? -1 : 1;
         p_228830_2_.func_227861_a_((double)((float)i * this.translation.getX()), (double)this.translation.getY(), (double)this.translation.getZ());
         p_228830_2_.func_227863_a_(new Quaternion(f, f1, f2, true));
         p_228830_2_.func_227862_a_(this.scale.getX(), this.scale.getY(), this.scale.getZ());
      }

   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (this.getClass() != p_equals_1_.getClass()) {
         return false;
      } else {
         ItemTransformVec3f itemtransformvec3f = (ItemTransformVec3f)p_equals_1_;
         return this.rotation.equals(itemtransformvec3f.rotation) && this.scale.equals(itemtransformvec3f.scale) && this.translation.equals(itemtransformvec3f.translation);
      }
   }

   public int hashCode() {
      int i = this.rotation.hashCode();
      i = 31 * i + this.translation.hashCode();
      i = 31 * i + this.scale.hashCode();
      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemTransformVec3f> {
      public static final Vector3f ROTATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
      public static final Vector3f TRANSLATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
      public static final Vector3f SCALE_DEFAULT = new Vector3f(1.0F, 1.0F, 1.0F);

      public ItemTransformVec3f deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         Vector3f vector3f = this.parseVector(jsonobject, "rotation", ROTATION_DEFAULT);
         Vector3f vector3f1 = this.parseVector(jsonobject, "translation", TRANSLATION_DEFAULT);
         vector3f1.mul(0.0625F);
         vector3f1.clamp(-5.0F, 5.0F);
         Vector3f vector3f2 = this.parseVector(jsonobject, "scale", SCALE_DEFAULT);
         vector3f2.clamp(-4.0F, 4.0F);
         return new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
      }

      private Vector3f parseVector(JsonObject p_199340_1_, String p_199340_2_, Vector3f p_199340_3_) {
         if (!p_199340_1_.has(p_199340_2_)) {
            return p_199340_3_;
         } else {
            JsonArray jsonarray = JSONUtils.getJsonArray(p_199340_1_, p_199340_2_);
            if (jsonarray.size() != 3) {
               throw new JsonParseException("Expected 3 " + p_199340_2_ + " values, found: " + jsonarray.size());
            } else {
               float[] afloat = new float[3];

               for(int i = 0; i < afloat.length; ++i) {
                  afloat[i] = JSONUtils.getFloat(jsonarray.get(i), p_199340_2_ + "[" + i + "]");
               }

               return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
         }
      }
   }
}
