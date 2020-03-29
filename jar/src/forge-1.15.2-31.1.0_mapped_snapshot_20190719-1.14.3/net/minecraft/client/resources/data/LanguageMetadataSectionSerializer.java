package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.resources.Language;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageMetadataSectionSerializer implements IMetadataSectionSerializer<LanguageMetadataSection> {
   public LanguageMetadataSection deserialize(JsonObject p_195812_1_) {
      Set<Language> lvt_2_1_ = Sets.newHashSet();
      Iterator var3 = p_195812_1_.entrySet().iterator();

      String lvt_5_1_;
      String lvt_7_1_;
      String lvt_8_1_;
      boolean lvt_9_1_;
      do {
         if (!var3.hasNext()) {
            return new LanguageMetadataSection(lvt_2_1_);
         }

         Entry<String, JsonElement> lvt_4_1_ = (Entry)var3.next();
         lvt_5_1_ = (String)lvt_4_1_.getKey();
         if (lvt_5_1_.length() > 16) {
            throw new JsonParseException("Invalid language->'" + lvt_5_1_ + "': language code must not be more than " + 16 + " characters long");
         }

         JsonObject lvt_6_1_ = JSONUtils.getJsonObject((JsonElement)lvt_4_1_.getValue(), "language");
         lvt_7_1_ = JSONUtils.getString(lvt_6_1_, "region");
         lvt_8_1_ = JSONUtils.getString(lvt_6_1_, "name");
         lvt_9_1_ = JSONUtils.getBoolean(lvt_6_1_, "bidirectional", false);
         if (lvt_7_1_.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + lvt_5_1_ + "'->region: empty value");
         }

         if (lvt_8_1_.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + lvt_5_1_ + "'->name: empty value");
         }
      } while(lvt_2_1_.add(new Language(lvt_5_1_, lvt_7_1_, lvt_8_1_, lvt_9_1_)));

      throw new JsonParseException("Duplicate language->'" + lvt_5_1_ + "' defined");
   }

   public String getSectionName() {
      return "language";
   }

   // $FF: synthetic method
   public Object deserialize(JsonObject p_195812_1_) {
      return this.deserialize(p_195812_1_);
   }
}
