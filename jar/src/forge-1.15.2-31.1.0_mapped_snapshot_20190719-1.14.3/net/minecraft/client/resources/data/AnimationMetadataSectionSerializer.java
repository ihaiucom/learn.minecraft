package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

@OnlyIn(Dist.CLIENT)
public class AnimationMetadataSectionSerializer implements IMetadataSectionSerializer<AnimationMetadataSection> {
   public AnimationMetadataSection deserialize(JsonObject p_195812_1_) {
      List<AnimationFrame> lvt_2_1_ = Lists.newArrayList();
      int lvt_3_1_ = JSONUtils.getInt(p_195812_1_, "frametime", 1);
      if (lvt_3_1_ != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)lvt_3_1_, "Invalid default frame time");
      }

      int lvt_5_2_;
      if (p_195812_1_.has("frames")) {
         try {
            JsonArray lvt_4_1_ = JSONUtils.getJsonArray(p_195812_1_, "frames");

            for(lvt_5_2_ = 0; lvt_5_2_ < lvt_4_1_.size(); ++lvt_5_2_) {
               JsonElement lvt_6_1_ = lvt_4_1_.get(lvt_5_2_);
               AnimationFrame lvt_7_1_ = this.parseAnimationFrame(lvt_5_2_, lvt_6_1_);
               if (lvt_7_1_ != null) {
                  lvt_2_1_.add(lvt_7_1_);
               }
            }
         } catch (ClassCastException var8) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + p_195812_1_.get("frames"), var8);
         }
      }

      int lvt_4_3_ = JSONUtils.getInt(p_195812_1_, "width", -1);
      lvt_5_2_ = JSONUtils.getInt(p_195812_1_, "height", -1);
      if (lvt_4_3_ != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)lvt_4_3_, "Invalid width");
      }

      if (lvt_5_2_ != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)lvt_5_2_, "Invalid height");
      }

      boolean lvt_6_2_ = JSONUtils.getBoolean(p_195812_1_, "interpolate", false);
      return new AnimationMetadataSection(lvt_2_1_, lvt_4_3_, lvt_5_2_, lvt_3_1_, lvt_6_2_);
   }

   private AnimationFrame parseAnimationFrame(int p_110492_1_, JsonElement p_110492_2_) {
      if (p_110492_2_.isJsonPrimitive()) {
         return new AnimationFrame(JSONUtils.getInt(p_110492_2_, "frames[" + p_110492_1_ + "]"));
      } else if (p_110492_2_.isJsonObject()) {
         JsonObject lvt_3_1_ = JSONUtils.getJsonObject(p_110492_2_, "frames[" + p_110492_1_ + "]");
         int lvt_4_1_ = JSONUtils.getInt(lvt_3_1_, "time", -1);
         if (lvt_3_1_.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)lvt_4_1_, "Invalid frame time");
         }

         int lvt_5_1_ = JSONUtils.getInt(lvt_3_1_, "index");
         Validate.inclusiveBetween(0L, 2147483647L, (long)lvt_5_1_, "Invalid frame index");
         return new AnimationFrame(lvt_5_1_, lvt_4_1_);
      } else {
         return null;
      }
   }

   public String getSectionName() {
      return "animation";
   }

   // $FF: synthetic method
   public Object deserialize(JsonObject p_195812_1_) {
      return this.deserialize(p_195812_1_);
   }
}
