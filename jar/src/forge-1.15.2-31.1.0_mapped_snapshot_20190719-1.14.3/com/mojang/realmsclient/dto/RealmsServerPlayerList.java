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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServerPlayerList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final JsonParser jsonParser = new JsonParser();
   public long serverId;
   public List<String> players;

   public static RealmsServerPlayerList parse(JsonObject p_parse_0_) {
      RealmsServerPlayerList lvt_1_1_ = new RealmsServerPlayerList();

      try {
         lvt_1_1_.serverId = JsonUtils.func_225169_a("serverId", p_parse_0_, -1L);
         String lvt_2_1_ = JsonUtils.func_225171_a("playerList", p_parse_0_, (String)null);
         if (lvt_2_1_ != null) {
            JsonElement lvt_3_1_ = jsonParser.parse(lvt_2_1_);
            if (lvt_3_1_.isJsonArray()) {
               lvt_1_1_.players = parsePlayers(lvt_3_1_.getAsJsonArray());
            } else {
               lvt_1_1_.players = Lists.newArrayList();
            }
         } else {
            lvt_1_1_.players = Lists.newArrayList();
         }
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsServerPlayerList: " + var4.getMessage());
      }

      return lvt_1_1_;
   }

   private static List<String> parsePlayers(JsonArray p_parsePlayers_0_) {
      List<String> lvt_1_1_ = Lists.newArrayList();
      Iterator var2 = p_parsePlayers_0_.iterator();

      while(var2.hasNext()) {
         JsonElement lvt_3_1_ = (JsonElement)var2.next();

         try {
            lvt_1_1_.add(lvt_3_1_.getAsString());
         } catch (Exception var5) {
         }
      }

      return lvt_1_1_;
   }
}
