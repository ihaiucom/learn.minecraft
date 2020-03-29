package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

@OnlyIn(Dist.CLIENT)
public class SoundListSerializer implements JsonDeserializer<SoundList> {
   public SoundList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
      JsonObject lvt_4_1_ = JSONUtils.getJsonObject(p_deserialize_1_, "entry");
      boolean lvt_5_1_ = JSONUtils.getBoolean(lvt_4_1_, "replace", false);
      String lvt_6_1_ = JSONUtils.getString(lvt_4_1_, "subtitle", (String)null);
      List<Sound> lvt_7_1_ = this.deserializeSounds(lvt_4_1_);
      return new SoundList(lvt_7_1_, lvt_5_1_, lvt_6_1_);
   }

   private List<Sound> deserializeSounds(JsonObject p_188733_1_) {
      List<Sound> lvt_2_1_ = Lists.newArrayList();
      if (p_188733_1_.has("sounds")) {
         JsonArray lvt_3_1_ = JSONUtils.getJsonArray(p_188733_1_, "sounds");

         for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.size(); ++lvt_4_1_) {
            JsonElement lvt_5_1_ = lvt_3_1_.get(lvt_4_1_);
            if (JSONUtils.isString(lvt_5_1_)) {
               String lvt_6_1_ = JSONUtils.getString(lvt_5_1_, "sound");
               lvt_2_1_.add(new Sound(lvt_6_1_, 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16));
            } else {
               lvt_2_1_.add(this.deserializeSound(JSONUtils.getJsonObject(lvt_5_1_, "sound")));
            }
         }
      }

      return lvt_2_1_;
   }

   private Sound deserializeSound(JsonObject p_188734_1_) {
      String lvt_2_1_ = JSONUtils.getString(p_188734_1_, "name");
      Sound.Type lvt_3_1_ = this.deserializeType(p_188734_1_, Sound.Type.FILE);
      float lvt_4_1_ = JSONUtils.getFloat(p_188734_1_, "volume", 1.0F);
      Validate.isTrue(lvt_4_1_ > 0.0F, "Invalid volume", new Object[0]);
      float lvt_5_1_ = JSONUtils.getFloat(p_188734_1_, "pitch", 1.0F);
      Validate.isTrue(lvt_5_1_ > 0.0F, "Invalid pitch", new Object[0]);
      int lvt_6_1_ = JSONUtils.getInt(p_188734_1_, "weight", 1);
      Validate.isTrue(lvt_6_1_ > 0, "Invalid weight", new Object[0]);
      boolean lvt_7_1_ = JSONUtils.getBoolean(p_188734_1_, "preload", false);
      boolean lvt_8_1_ = JSONUtils.getBoolean(p_188734_1_, "stream", false);
      int lvt_9_1_ = JSONUtils.getInt(p_188734_1_, "attenuation_distance", 16);
      return new Sound(lvt_2_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_3_1_, lvt_8_1_, lvt_7_1_, lvt_9_1_);
   }

   private Sound.Type deserializeType(JsonObject p_188732_1_, Sound.Type p_188732_2_) {
      Sound.Type lvt_3_1_ = p_188732_2_;
      if (p_188732_1_.has("type")) {
         lvt_3_1_ = Sound.Type.getByName(JSONUtils.getString(p_188732_1_, "type"));
         Validate.notNull(lvt_3_1_, "Invalid type", new Object[0]);
      }

      return lvt_3_1_;
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
      return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
   }
}
