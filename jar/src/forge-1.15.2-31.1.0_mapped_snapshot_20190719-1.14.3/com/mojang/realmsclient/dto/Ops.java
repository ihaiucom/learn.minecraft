package com.mojang.realmsclient.dto;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Ops extends ValueObject {
   public Set<String> ops = Sets.newHashSet();

   public static Ops parse(String p_parse_0_) {
      Ops lvt_1_1_ = new Ops();
      JsonParser lvt_2_1_ = new JsonParser();

      try {
         JsonElement lvt_3_1_ = lvt_2_1_.parse(p_parse_0_);
         JsonObject lvt_4_1_ = lvt_3_1_.getAsJsonObject();
         JsonElement lvt_5_1_ = lvt_4_1_.get("ops");
         if (lvt_5_1_.isJsonArray()) {
            Iterator var6 = lvt_5_1_.getAsJsonArray().iterator();

            while(var6.hasNext()) {
               JsonElement lvt_7_1_ = (JsonElement)var6.next();
               lvt_1_1_.ops.add(lvt_7_1_.getAsString());
            }
         }
      } catch (Exception var8) {
      }

      return lvt_1_1_;
   }
}
