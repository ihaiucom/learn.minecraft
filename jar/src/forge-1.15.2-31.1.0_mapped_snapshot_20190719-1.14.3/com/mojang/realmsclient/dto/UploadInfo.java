package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UploadInfo extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   @Expose
   private boolean worldClosed;
   @Expose
   private String token = "";
   @Expose
   private String uploadEndpoint = "";
   private int port;

   public static UploadInfo parse(String p_parse_0_) {
      UploadInfo lvt_1_1_ = new UploadInfo();

      try {
         JsonParser lvt_2_1_ = new JsonParser();
         JsonObject lvt_3_1_ = lvt_2_1_.parse(p_parse_0_).getAsJsonObject();
         lvt_1_1_.worldClosed = JsonUtils.func_225170_a("worldClosed", lvt_3_1_, false);
         lvt_1_1_.token = JsonUtils.func_225171_a("token", lvt_3_1_, (String)null);
         lvt_1_1_.uploadEndpoint = JsonUtils.func_225171_a("uploadEndpoint", lvt_3_1_, (String)null);
         lvt_1_1_.port = JsonUtils.func_225172_a("port", lvt_3_1_, 8080);
      } catch (Exception var4) {
         LOGGER.error("Could not parse UploadInfo: " + var4.getMessage());
      }

      return lvt_1_1_;
   }

   public String getToken() {
      return this.token;
   }

   public String getUploadEndpoint() {
      return this.uploadEndpoint;
   }

   public boolean isWorldClosed() {
      return this.worldClosed;
   }

   public void setToken(String p_setToken_1_) {
      this.token = p_setToken_1_;
   }

   public int getPort() {
      return this.port;
   }
}
