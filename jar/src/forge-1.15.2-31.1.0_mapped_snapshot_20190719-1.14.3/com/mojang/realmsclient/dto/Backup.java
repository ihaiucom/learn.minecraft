package com.mojang.realmsclient.dto;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Backup extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String backupId;
   public Date lastModifiedDate;
   public long size;
   private boolean uploadedVersion;
   public Map<String, String> metadata = Maps.newHashMap();
   public Map<String, String> changeList = Maps.newHashMap();

   public static Backup parse(JsonElement p_parse_0_) {
      JsonObject lvt_1_1_ = p_parse_0_.getAsJsonObject();
      Backup lvt_2_1_ = new Backup();

      try {
         lvt_2_1_.backupId = JsonUtils.func_225171_a("backupId", lvt_1_1_, "");
         lvt_2_1_.lastModifiedDate = JsonUtils.func_225173_a("lastModifiedDate", lvt_1_1_);
         lvt_2_1_.size = JsonUtils.func_225169_a("size", lvt_1_1_, 0L);
         if (lvt_1_1_.has("metadata")) {
            JsonObject lvt_3_1_ = lvt_1_1_.getAsJsonObject("metadata");
            Set<Entry<String, JsonElement>> lvt_4_1_ = lvt_3_1_.entrySet();
            Iterator var5 = lvt_4_1_.iterator();

            while(var5.hasNext()) {
               Entry<String, JsonElement> lvt_6_1_ = (Entry)var5.next();
               if (!((JsonElement)lvt_6_1_.getValue()).isJsonNull()) {
                  lvt_2_1_.metadata.put(format((String)lvt_6_1_.getKey()), ((JsonElement)lvt_6_1_.getValue()).getAsString());
               }
            }
         }
      } catch (Exception var7) {
         LOGGER.error("Could not parse Backup: " + var7.getMessage());
      }

      return lvt_2_1_;
   }

   private static String format(String p_format_0_) {
      String[] lvt_1_1_ = p_format_0_.split("_");
      StringBuilder lvt_2_1_ = new StringBuilder();
      String[] var3 = lvt_1_1_;
      int var4 = lvt_1_1_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String lvt_6_1_ = var3[var5];
         if (lvt_6_1_ != null && lvt_6_1_.length() >= 1) {
            if ("of".equals(lvt_6_1_)) {
               lvt_2_1_.append(lvt_6_1_).append(" ");
            } else {
               char lvt_7_1_ = Character.toUpperCase(lvt_6_1_.charAt(0));
               lvt_2_1_.append(lvt_7_1_).append(lvt_6_1_.substring(1, lvt_6_1_.length())).append(" ");
            }
         }
      }

      return lvt_2_1_.toString();
   }

   public boolean isUploadedVersion() {
      return this.uploadedVersion;
   }

   public void setUploadedVersion(boolean p_setUploadedVersion_1_) {
      this.uploadedVersion = p_setUploadedVersion_1_;
   }
}
