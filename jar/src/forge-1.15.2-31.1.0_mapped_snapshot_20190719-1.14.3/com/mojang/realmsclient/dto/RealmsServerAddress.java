package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServerAddress extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String address;
   public String resourcePackUrl;
   public String resourcePackHash;

   public static RealmsServerAddress parse(String p_parse_0_) {
      JsonParser lvt_1_1_ = new JsonParser();
      RealmsServerAddress lvt_2_1_ = new RealmsServerAddress();

      try {
         JsonObject lvt_3_1_ = lvt_1_1_.parse(p_parse_0_).getAsJsonObject();
         lvt_2_1_.address = JsonUtils.func_225171_a("address", lvt_3_1_, (String)null);
         lvt_2_1_.resourcePackUrl = JsonUtils.func_225171_a("resourcePackUrl", lvt_3_1_, (String)null);
         lvt_2_1_.resourcePackHash = JsonUtils.func_225171_a("resourcePackHash", lvt_3_1_, (String)null);
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsServerAddress: " + var4.getMessage());
      }

      return lvt_2_1_;
   }
}
