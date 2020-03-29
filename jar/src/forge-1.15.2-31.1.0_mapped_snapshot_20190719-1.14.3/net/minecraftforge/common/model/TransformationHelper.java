package net.minecraftforge.common.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class TransformationHelper {
   private static final double THRESHOLD = 0.9995D;

   /** @deprecated */
   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public static TransformationMatrix toTransformation(ItemTransformVec3f transform) {
      return transform.equals(ItemTransformVec3f.DEFAULT) ? TransformationMatrix.func_227983_a_() : new TransformationMatrix(transform.translation, quatFromXYZ(transform.rotation, true), transform.scale, (Quaternion)null);
   }

   public static Quaternion quatFromXYZ(Vector3f xyz, boolean degrees) {
      return new Quaternion(xyz.getX(), xyz.getY(), xyz.getZ(), degrees);
   }

   public static Quaternion quatFromXYZ(float[] xyz, boolean degrees) {
      return new Quaternion(xyz[0], xyz[1], xyz[2], degrees);
   }

   public static Quaternion makeQuaternion(float[] values) {
      return new Quaternion(values[0], values[1], values[2], values[3]);
   }

   public static Vector3f lerp(Vector3f from, Vector3f to, float progress) {
      Vector3f res = from.func_229195_e_();
      res.func_229190_a_(to, progress);
      return res;
   }

   private static Quaternion slerp(Quaternion v0, Quaternion v1, float t) {
      float dot = v0.getX() * v1.getX() + v0.getY() * v1.getY() + v0.getZ() * v1.getZ() + v0.getW() * v1.getW();
      if (dot < 0.0F) {
         v1 = new Quaternion(-v1.getX(), -v1.getY(), -v1.getZ(), -v1.getW());
         dot = -dot;
      }

      float angle01;
      float angle0t;
      float sin0t;
      float sin01;
      if ((double)dot > 0.9995D) {
         angle01 = MathHelper.lerp(t, v0.getX(), v1.getX());
         angle0t = MathHelper.lerp(t, v0.getY(), v1.getY());
         sin0t = MathHelper.lerp(t, v0.getZ(), v1.getZ());
         sin01 = MathHelper.lerp(t, v0.getW(), v1.getW());
         return new Quaternion(angle01, angle0t, sin0t, sin01);
      } else {
         angle01 = (float)Math.acos((double)dot);
         angle0t = angle01 * t;
         sin0t = MathHelper.sin(angle0t);
         sin01 = MathHelper.sin(angle01);
         float sin1t = MathHelper.sin(angle01 - angle0t);
         float s1 = sin0t / sin01;
         float s0 = sin1t / sin01;
         return new Quaternion(s0 * v0.getX() + s1 * v1.getX(), s0 * v0.getY() + s1 * v1.getY(), s0 * v0.getZ() + s1 * v1.getZ(), s0 * v0.getW() + s1 * v1.getW());
      }
   }

   public static TransformationMatrix slerp(TransformationMatrix one, TransformationMatrix that, float progress) {
      return new TransformationMatrix(lerp(one.getTranslation(), that.getTranslation(), progress), slerp(one.func_227989_d_(), that.func_227989_d_(), progress), lerp(one.getScale(), that.getScale(), progress), slerp(one.getRightRot(), that.getRightRot(), progress));
   }

   public static boolean epsilonEquals(Vector4f v1, Vector4f v2, float epsilon) {
      return MathHelper.abs(v1.getX() - v2.getX()) < epsilon && MathHelper.abs(v1.getY() - v2.getY()) < epsilon && MathHelper.abs(v1.getZ() - v2.getZ()) < epsilon && MathHelper.abs(v1.getW() - v2.getW()) < epsilon;
   }

   public static class Deserializer implements JsonDeserializer<TransformationMatrix> {
      public TransformationMatrix deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            String transform = json.getAsString();
            if (transform.equals("identity")) {
               return TransformationMatrix.func_227983_a_();
            } else {
               throw new JsonParseException("TRSR: unknown default string: " + transform);
            }
         } else if (json.isJsonArray()) {
            return new TransformationMatrix(parseMatrix(json));
         } else if (!json.isJsonObject()) {
            throw new JsonParseException("TRSR: expected array or object, got: " + json);
         } else {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("matrix")) {
               TransformationMatrix ret = new TransformationMatrix(parseMatrix(obj.get("matrix")));
               obj.remove("matrix");
               if (obj.entrySet().size() != 0) {
                  throw new JsonParseException("TRSR: can't combine matrix and other keys");
               } else {
                  return ret;
               }
            } else {
               Vector3f translation = null;
               Quaternion leftRot = null;
               Vector3f scale = null;
               Quaternion rightRot = null;
               if (obj.has("translation")) {
                  translation = new Vector3f(parseFloatArray(obj.get("translation"), 3, "Translation"));
                  obj.remove("translation");
               }

               if (obj.has("rotation")) {
                  leftRot = parseRotation(obj.get("rotation"));
                  obj.remove("rotation");
               }

               if (obj.has("scale")) {
                  if (!obj.get("scale").isJsonArray()) {
                     try {
                        float s = obj.get("scale").getAsNumber().floatValue();
                        scale = new Vector3f(s, s, s);
                     } catch (ClassCastException var11) {
                        throw new JsonParseException("TRSR scale: expected number or array, got: " + obj.get("scale"));
                     }
                  } else {
                     scale = new Vector3f(parseFloatArray(obj.get("scale"), 3, "Scale"));
                  }

                  obj.remove("scale");
               }

               if (obj.has("post-rotation")) {
                  rightRot = parseRotation(obj.get("post-rotation"));
                  obj.remove("post-rotation");
               }

               if (!obj.entrySet().isEmpty()) {
                  throw new JsonParseException("TRSR: can either have single 'matrix' key, or a combination of 'translation', 'rotation', 'scale', 'post-rotation'");
               } else {
                  return new TransformationMatrix(translation, leftRot, scale, rightRot);
               }
            }
         }
      }

      public static Matrix4f parseMatrix(JsonElement e) {
         if (!e.isJsonArray()) {
            throw new JsonParseException("Matrix: expected an array, got: " + e);
         } else {
            JsonArray m = e.getAsJsonArray();
            if (m.size() != 3) {
               throw new JsonParseException("Matrix: expected an array of length 3, got: " + m.size());
            } else {
               float[] values = new float[16];

               for(int i = 0; i < 3; ++i) {
                  if (!m.get(i).isJsonArray()) {
                     throw new JsonParseException("Matrix row: expected an array, got: " + m.get(i));
                  }

                  JsonArray r = m.get(i).getAsJsonArray();
                  if (r.size() != 4) {
                     throw new JsonParseException("Matrix row: expected an array of length 4, got: " + r.size());
                  }

                  for(int j = 0; j < 4; ++j) {
                     try {
                        values[j * 4 + i] = r.get(j).getAsNumber().floatValue();
                     } catch (ClassCastException var7) {
                        throw new JsonParseException("Matrix element: expected number, got: " + r.get(j));
                     }
                  }
               }

               return new Matrix4f(values);
            }
         }
      }

      public static float[] parseFloatArray(JsonElement e, int length, String prefix) {
         if (!e.isJsonArray()) {
            throw new JsonParseException(prefix + ": expected an array, got: " + e);
         } else {
            JsonArray t = e.getAsJsonArray();
            if (t.size() != length) {
               throw new JsonParseException(prefix + ": expected an array of length " + length + ", got: " + t.size());
            } else {
               float[] ret = new float[length];

               for(int i = 0; i < length; ++i) {
                  try {
                     ret[i] = t.get(i).getAsNumber().floatValue();
                  } catch (ClassCastException var7) {
                     throw new JsonParseException(prefix + " element: expected number, got: " + t.get(i));
                  }
               }

               return ret;
            }
         }
      }

      public static Quaternion parseAxisRotation(JsonElement e) {
         if (!e.isJsonObject()) {
            throw new JsonParseException("Axis rotation: object expected, got: " + e);
         } else {
            JsonObject obj = e.getAsJsonObject();
            if (obj.entrySet().size() != 1) {
               throw new JsonParseException("Axis rotation: expected single axis object, got: " + e);
            } else {
               Entry entry = (Entry)obj.entrySet().iterator().next();

               try {
                  Quaternion ret;
                  if (((String)entry.getKey()).equals("x")) {
                     ret = Vector3f.field_229179_b_.func_229187_a_(((JsonElement)entry.getValue()).getAsNumber().floatValue());
                  } else if (((String)entry.getKey()).equals("y")) {
                     ret = Vector3f.field_229181_d_.func_229187_a_(((JsonElement)entry.getValue()).getAsNumber().floatValue());
                  } else {
                     if (!((String)entry.getKey()).equals("z")) {
                        throw new JsonParseException("Axis rotation: expected single axis key, got: " + (String)entry.getKey());
                     }

                     ret = Vector3f.field_229183_f_.func_229187_a_(((JsonElement)entry.getValue()).getAsNumber().floatValue());
                  }

                  return ret;
               } catch (ClassCastException var5) {
                  throw new JsonParseException("Axis rotation value: expected number, got: " + entry.getValue());
               }
            }
         }
      }

      public static Quaternion parseRotation(JsonElement e) {
         if (!e.isJsonArray()) {
            if (e.isJsonObject()) {
               return parseAxisRotation(e);
            } else {
               throw new JsonParseException("Rotation: expected array or object, got: " + e);
            }
         } else if (!e.getAsJsonArray().get(0).isJsonObject()) {
            if (e.isJsonArray()) {
               JsonArray array = e.getAsJsonArray();
               return array.size() == 3 ? TransformationHelper.quatFromXYZ(parseFloatArray(e, 3, "Rotation"), true) : TransformationHelper.makeQuaternion(parseFloatArray(e, 4, "Rotation"));
            } else {
               throw new JsonParseException("Rotation: expected array or object, got: " + e);
            }
         } else {
            Quaternion ret = Quaternion.field_227060_a_.func_227068_g_();
            Iterator var2 = e.getAsJsonArray().iterator();

            while(var2.hasNext()) {
               JsonElement a = (JsonElement)var2.next();
               ret.multiply(parseAxisRotation(a));
            }

            return ret;
         }
      }
   }
}
