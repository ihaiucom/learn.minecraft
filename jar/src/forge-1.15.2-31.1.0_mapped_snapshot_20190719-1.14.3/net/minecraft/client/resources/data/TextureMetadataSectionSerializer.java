package net.minecraft.client.resources.data;

import com.google.gson.JsonObject;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureMetadataSectionSerializer implements IMetadataSectionSerializer<TextureMetadataSection> {
   public TextureMetadataSection deserialize(JsonObject p_195812_1_) {
      boolean lvt_2_1_ = JSONUtils.getBoolean(p_195812_1_, "blur", false);
      boolean lvt_3_1_ = JSONUtils.getBoolean(p_195812_1_, "clamp", false);
      return new TextureMetadataSection(lvt_2_1_, lvt_3_1_);
   }

   public String getSectionName() {
      return "texture";
   }

   // $FF: synthetic method
   public Object deserialize(JsonObject p_195812_1_) {
      return this.deserialize(p_195812_1_);
   }
}
