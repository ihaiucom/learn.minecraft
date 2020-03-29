package net.minecraft.resources.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;

public class PackMetadataSectionSerializer implements IMetadataSectionSerializer<PackMetadataSection> {
   public PackMetadataSection deserialize(JsonObject p_195812_1_) {
      ITextComponent lvt_2_1_ = ITextComponent.Serializer.fromJson(p_195812_1_.get("description"));
      if (lvt_2_1_ == null) {
         throw new JsonParseException("Invalid/missing description!");
      } else {
         int lvt_3_1_ = JSONUtils.getInt(p_195812_1_, "pack_format");
         return new PackMetadataSection(lvt_2_1_, lvt_3_1_);
      }
   }

   public String getSectionName() {
      return "pack";
   }

   // $FF: synthetic method
   public Object deserialize(JsonObject p_195812_1_) {
      return this.deserialize(p_195812_1_);
   }
}
