package com.mojang.realmsclient.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsError {
   private static final Logger field_224975_a = LogManager.getLogger();
   private String field_224976_b;
   private int field_224977_c;

   public RealmsError(String p_i51789_1_) {
      try {
         JsonParser lvt_2_1_ = new JsonParser();
         JsonObject lvt_3_1_ = lvt_2_1_.parse(p_i51789_1_).getAsJsonObject();
         this.field_224976_b = JsonUtils.func_225171_a("errorMsg", lvt_3_1_, "");
         this.field_224977_c = JsonUtils.func_225172_a("errorCode", lvt_3_1_, -1);
      } catch (Exception var4) {
         field_224975_a.error("Could not parse RealmsError: " + var4.getMessage());
         field_224975_a.error("The error was: " + p_i51789_1_);
      }

   }

   public String func_224973_a() {
      return this.field_224976_b;
   }

   public int func_224974_b() {
      return this.field_224977_c;
   }
}
