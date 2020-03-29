package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServerPlayerLists extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List<RealmsServerPlayerList> servers;

   public static RealmsServerPlayerLists parse(String p_parse_0_) {
      RealmsServerPlayerLists lvt_1_1_ = new RealmsServerPlayerLists();
      lvt_1_1_.servers = Lists.newArrayList();

      try {
         JsonParser lvt_2_1_ = new JsonParser();
         JsonObject lvt_3_1_ = lvt_2_1_.parse(p_parse_0_).getAsJsonObject();
         if (lvt_3_1_.get("lists").isJsonArray()) {
            JsonArray lvt_4_1_ = lvt_3_1_.get("lists").getAsJsonArray();
            Iterator lvt_5_1_ = lvt_4_1_.iterator();

            while(lvt_5_1_.hasNext()) {
               lvt_1_1_.servers.add(RealmsServerPlayerList.parse(((JsonElement)lvt_5_1_.next()).getAsJsonObject()));
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Could not parse RealmsServerPlayerLists: " + var6.getMessage());
      }

      return lvt_1_1_;
   }
}
