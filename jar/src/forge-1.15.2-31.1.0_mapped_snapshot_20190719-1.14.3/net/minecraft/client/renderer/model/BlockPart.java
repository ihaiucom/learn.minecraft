package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockPart {
   public final Vector3f positionFrom;
   public final Vector3f positionTo;
   public final Map<Direction, BlockPartFace> mapFaces;
   public final BlockPartRotation partRotation;
   public final boolean shade;

   public BlockPart(Vector3f p_i47624_1_, Vector3f p_i47624_2_, Map<Direction, BlockPartFace> p_i47624_3_, @Nullable BlockPartRotation p_i47624_4_, boolean p_i47624_5_) {
      this.positionFrom = p_i47624_1_;
      this.positionTo = p_i47624_2_;
      this.mapFaces = p_i47624_3_;
      this.partRotation = p_i47624_4_;
      this.shade = p_i47624_5_;
      this.setDefaultUvs();
   }

   private void setDefaultUvs() {
      Iterator var1 = this.mapFaces.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<Direction, BlockPartFace> lvt_2_1_ = (Entry)var1.next();
         float[] lvt_3_1_ = this.getFaceUvs((Direction)lvt_2_1_.getKey());
         ((BlockPartFace)lvt_2_1_.getValue()).blockFaceUV.setUvs(lvt_3_1_);
      }

   }

   public float[] getFaceUvs(Direction p_178236_1_) {
      switch(p_178236_1_) {
      case DOWN:
         return new float[]{this.positionFrom.getX(), 16.0F - this.positionTo.getZ(), this.positionTo.getX(), 16.0F - this.positionFrom.getZ()};
      case UP:
         return new float[]{this.positionFrom.getX(), this.positionFrom.getZ(), this.positionTo.getX(), this.positionTo.getZ()};
      case NORTH:
      default:
         return new float[]{16.0F - this.positionTo.getX(), 16.0F - this.positionTo.getY(), 16.0F - this.positionFrom.getX(), 16.0F - this.positionFrom.getY()};
      case SOUTH:
         return new float[]{this.positionFrom.getX(), 16.0F - this.positionTo.getY(), this.positionTo.getX(), 16.0F - this.positionFrom.getY()};
      case WEST:
         return new float[]{this.positionFrom.getZ(), 16.0F - this.positionTo.getY(), this.positionTo.getZ(), 16.0F - this.positionFrom.getY()};
      case EAST:
         return new float[]{16.0F - this.positionTo.getZ(), 16.0F - this.positionTo.getY(), 16.0F - this.positionFrom.getZ(), 16.0F - this.positionFrom.getY()};
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockPart> {
      public BlockPart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
         Vector3f lvt_5_1_ = this.func_199330_e(lvt_4_1_);
         Vector3f lvt_6_1_ = this.func_199329_d(lvt_4_1_);
         BlockPartRotation lvt_7_1_ = this.parseRotation(lvt_4_1_);
         Map<Direction, BlockPartFace> lvt_8_1_ = this.parseFacesCheck(p_deserialize_3_, lvt_4_1_);
         if (lvt_4_1_.has("shade") && !JSONUtils.isBoolean(lvt_4_1_, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean lvt_9_1_ = JSONUtils.getBoolean(lvt_4_1_, "shade", true);
            return new BlockPart(lvt_5_1_, lvt_6_1_, lvt_8_1_, lvt_7_1_, lvt_9_1_);
         }
      }

      @Nullable
      private BlockPartRotation parseRotation(JsonObject p_178256_1_) {
         BlockPartRotation lvt_2_1_ = null;
         if (p_178256_1_.has("rotation")) {
            JsonObject lvt_3_1_ = JSONUtils.getJsonObject(p_178256_1_, "rotation");
            Vector3f lvt_4_1_ = this.func_199328_a(lvt_3_1_, "origin");
            lvt_4_1_.mul(0.0625F);
            Direction.Axis lvt_5_1_ = this.parseAxis(lvt_3_1_);
            float lvt_6_1_ = this.parseAngle(lvt_3_1_);
            boolean lvt_7_1_ = JSONUtils.getBoolean(lvt_3_1_, "rescale", false);
            lvt_2_1_ = new BlockPartRotation(lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
         }

         return lvt_2_1_;
      }

      private float parseAngle(JsonObject p_178255_1_) {
         float lvt_2_1_ = JSONUtils.getFloat(p_178255_1_, "angle");
         if (lvt_2_1_ != 0.0F && MathHelper.abs(lvt_2_1_) != 22.5F && MathHelper.abs(lvt_2_1_) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + lvt_2_1_ + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return lvt_2_1_;
         }
      }

      private Direction.Axis parseAxis(JsonObject p_178252_1_) {
         String lvt_2_1_ = JSONUtils.getString(p_178252_1_, "axis");
         Direction.Axis lvt_3_1_ = Direction.Axis.byName(lvt_2_1_.toLowerCase(Locale.ROOT));
         if (lvt_3_1_ == null) {
            throw new JsonParseException("Invalid rotation axis: " + lvt_2_1_);
         } else {
            return lvt_3_1_;
         }
      }

      private Map<Direction, BlockPartFace> parseFacesCheck(JsonDeserializationContext p_178250_1_, JsonObject p_178250_2_) {
         Map<Direction, BlockPartFace> lvt_3_1_ = this.parseFaces(p_178250_1_, p_178250_2_);
         if (lvt_3_1_.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return lvt_3_1_;
         }
      }

      private Map<Direction, BlockPartFace> parseFaces(JsonDeserializationContext p_178253_1_, JsonObject p_178253_2_) {
         Map<Direction, BlockPartFace> lvt_3_1_ = Maps.newEnumMap(Direction.class);
         JsonObject lvt_4_1_ = JSONUtils.getJsonObject(p_178253_2_, "faces");
         Iterator var5 = lvt_4_1_.entrySet().iterator();

         while(var5.hasNext()) {
            Entry<String, JsonElement> lvt_6_1_ = (Entry)var5.next();
            Direction lvt_7_1_ = this.parseEnumFacing((String)lvt_6_1_.getKey());
            lvt_3_1_.put(lvt_7_1_, p_178253_1_.deserialize((JsonElement)lvt_6_1_.getValue(), BlockPartFace.class));
         }

         return lvt_3_1_;
      }

      private Direction parseEnumFacing(String p_178248_1_) {
         Direction lvt_2_1_ = Direction.byName(p_178248_1_);
         if (lvt_2_1_ == null) {
            throw new JsonParseException("Unknown facing: " + p_178248_1_);
         } else {
            return lvt_2_1_;
         }
      }

      private Vector3f func_199329_d(JsonObject p_199329_1_) {
         Vector3f lvt_2_1_ = this.func_199328_a(p_199329_1_, "to");
         if (lvt_2_1_.getX() >= -16.0F && lvt_2_1_.getY() >= -16.0F && lvt_2_1_.getZ() >= -16.0F && lvt_2_1_.getX() <= 32.0F && lvt_2_1_.getY() <= 32.0F && lvt_2_1_.getZ() <= 32.0F) {
            return lvt_2_1_;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + lvt_2_1_);
         }
      }

      private Vector3f func_199330_e(JsonObject p_199330_1_) {
         Vector3f lvt_2_1_ = this.func_199328_a(p_199330_1_, "from");
         if (lvt_2_1_.getX() >= -16.0F && lvt_2_1_.getY() >= -16.0F && lvt_2_1_.getZ() >= -16.0F && lvt_2_1_.getX() <= 32.0F && lvt_2_1_.getY() <= 32.0F && lvt_2_1_.getZ() <= 32.0F) {
            return lvt_2_1_;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + lvt_2_1_);
         }
      }

      private Vector3f func_199328_a(JsonObject p_199328_1_, String p_199328_2_) {
         JsonArray lvt_3_1_ = JSONUtils.getJsonArray(p_199328_1_, p_199328_2_);
         if (lvt_3_1_.size() != 3) {
            throw new JsonParseException("Expected 3 " + p_199328_2_ + " values, found: " + lvt_3_1_.size());
         } else {
            float[] lvt_4_1_ = new float[3];

            for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.length; ++lvt_5_1_) {
               lvt_4_1_[lvt_5_1_] = JSONUtils.getFloat(lvt_3_1_.get(lvt_5_1_), p_199328_2_ + "[" + lvt_5_1_ + "]");
            }

            return new Vector3f(lvt_4_1_[0], lvt_4_1_[1], lvt_4_1_[2]);
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
