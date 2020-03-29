package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Subscription extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long startDate;
   public int daysLeft;
   public Subscription.Type type;

   public Subscription() {
      this.type = Subscription.Type.NORMAL;
   }

   public static Subscription parse(String p_parse_0_) {
      Subscription lvt_1_1_ = new Subscription();

      try {
         JsonParser lvt_2_1_ = new JsonParser();
         JsonObject lvt_3_1_ = lvt_2_1_.parse(p_parse_0_).getAsJsonObject();
         lvt_1_1_.startDate = JsonUtils.func_225169_a("startDate", lvt_3_1_, 0L);
         lvt_1_1_.daysLeft = JsonUtils.func_225172_a("daysLeft", lvt_3_1_, 0);
         lvt_1_1_.type = typeFrom(JsonUtils.func_225171_a("subscriptionType", lvt_3_1_, Subscription.Type.NORMAL.name()));
      } catch (Exception var4) {
         LOGGER.error("Could not parse Subscription: " + var4.getMessage());
      }

      return lvt_1_1_;
   }

   private static Subscription.Type typeFrom(String p_typeFrom_0_) {
      try {
         return Subscription.Type.valueOf(p_typeFrom_0_);
      } catch (Exception var2) {
         return Subscription.Type.NORMAL;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      NORMAL,
      RECURRING;
   }
}
