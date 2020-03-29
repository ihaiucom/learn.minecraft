package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JsonUtils {
   public static String func_225171_a(String p_225171_0_, JsonObject p_225171_1_, String p_225171_2_) {
      JsonElement lvt_3_1_ = p_225171_1_.get(p_225171_0_);
      if (lvt_3_1_ != null) {
         return lvt_3_1_.isJsonNull() ? p_225171_2_ : lvt_3_1_.getAsString();
      } else {
         return p_225171_2_;
      }
   }

   public static int func_225172_a(String p_225172_0_, JsonObject p_225172_1_, int p_225172_2_) {
      JsonElement lvt_3_1_ = p_225172_1_.get(p_225172_0_);
      if (lvt_3_1_ != null) {
         return lvt_3_1_.isJsonNull() ? p_225172_2_ : lvt_3_1_.getAsInt();
      } else {
         return p_225172_2_;
      }
   }

   public static long func_225169_a(String p_225169_0_, JsonObject p_225169_1_, long p_225169_2_) {
      JsonElement lvt_4_1_ = p_225169_1_.get(p_225169_0_);
      if (lvt_4_1_ != null) {
         return lvt_4_1_.isJsonNull() ? p_225169_2_ : lvt_4_1_.getAsLong();
      } else {
         return p_225169_2_;
      }
   }

   public static boolean func_225170_a(String p_225170_0_, JsonObject p_225170_1_, boolean p_225170_2_) {
      JsonElement lvt_3_1_ = p_225170_1_.get(p_225170_0_);
      if (lvt_3_1_ != null) {
         return lvt_3_1_.isJsonNull() ? p_225170_2_ : lvt_3_1_.getAsBoolean();
      } else {
         return p_225170_2_;
      }
   }

   public static Date func_225173_a(String p_225173_0_, JsonObject p_225173_1_) {
      JsonElement lvt_2_1_ = p_225173_1_.get(p_225173_0_);
      return lvt_2_1_ != null ? new Date(Long.parseLong(lvt_2_1_.getAsString())) : new Date();
   }
}
