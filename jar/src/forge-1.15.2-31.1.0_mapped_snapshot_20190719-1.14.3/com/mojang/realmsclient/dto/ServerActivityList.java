package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerActivityList extends ValueObject {
   public long periodInMillis;
   public List<ServerActivity> serverActivities = Lists.newArrayList();

   public static ServerActivityList parse(String p_parse_0_) {
      ServerActivityList lvt_1_1_ = new ServerActivityList();
      JsonParser lvt_2_1_ = new JsonParser();

      try {
         JsonElement lvt_3_1_ = lvt_2_1_.parse(p_parse_0_);
         JsonObject lvt_4_1_ = lvt_3_1_.getAsJsonObject();
         lvt_1_1_.periodInMillis = JsonUtils.func_225169_a("periodInMillis", lvt_4_1_, -1L);
         JsonElement lvt_5_1_ = lvt_4_1_.get("playerActivityDto");
         if (lvt_5_1_ != null && lvt_5_1_.isJsonArray()) {
            JsonArray lvt_6_1_ = lvt_5_1_.getAsJsonArray();
            Iterator var7 = lvt_6_1_.iterator();

            while(var7.hasNext()) {
               JsonElement lvt_8_1_ = (JsonElement)var7.next();
               ServerActivity lvt_9_1_ = ServerActivity.parse(lvt_8_1_.getAsJsonObject());
               lvt_1_1_.serverActivities.add(lvt_9_1_);
            }
         }
      } catch (Exception var10) {
      }

      return lvt_1_1_;
   }
}
