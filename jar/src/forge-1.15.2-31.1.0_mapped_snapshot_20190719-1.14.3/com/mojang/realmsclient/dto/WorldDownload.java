package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldDownload extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String downloadLink;
   public String resourcePackUrl;
   public String resourcePackHash;

   public static WorldDownload parse(String p_parse_0_) {
      JsonParser lvt_1_1_ = new JsonParser();
      JsonObject lvt_2_1_ = lvt_1_1_.parse(p_parse_0_).getAsJsonObject();
      WorldDownload lvt_3_1_ = new WorldDownload();

      try {
         lvt_3_1_.downloadLink = JsonUtils.func_225171_a("downloadLink", lvt_2_1_, "");
         lvt_3_1_.resourcePackUrl = JsonUtils.func_225171_a("resourcePackUrl", lvt_2_1_, "");
         lvt_3_1_.resourcePackHash = JsonUtils.func_225171_a("resourcePackHash", lvt_2_1_, "");
      } catch (Exception var5) {
         LOGGER.error("Could not parse WorldDownload: " + var5.getMessage());
      }

      return lvt_3_1_;
   }
}
