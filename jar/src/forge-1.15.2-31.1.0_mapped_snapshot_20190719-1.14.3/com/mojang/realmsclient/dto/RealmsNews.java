package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsNews extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String newsLink;

   public static RealmsNews parse(String p_parse_0_) {
      RealmsNews lvt_1_1_ = new RealmsNews();

      try {
         JsonParser lvt_2_1_ = new JsonParser();
         JsonObject lvt_3_1_ = lvt_2_1_.parse(p_parse_0_).getAsJsonObject();
         lvt_1_1_.newsLink = JsonUtils.func_225171_a("newsLink", lvt_3_1_, (String)null);
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsNews: " + var4.getMessage());
      }

      return lvt_1_1_;
   }
}
