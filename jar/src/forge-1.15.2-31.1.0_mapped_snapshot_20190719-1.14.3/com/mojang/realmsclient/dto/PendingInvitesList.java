package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
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
public class PendingInvitesList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List<PendingInvite> pendingInvites = Lists.newArrayList();

   public static PendingInvitesList parse(String p_parse_0_) {
      PendingInvitesList lvt_1_1_ = new PendingInvitesList();

      try {
         JsonParser lvt_2_1_ = new JsonParser();
         JsonObject lvt_3_1_ = lvt_2_1_.parse(p_parse_0_).getAsJsonObject();
         if (lvt_3_1_.get("invites").isJsonArray()) {
            Iterator lvt_4_1_ = lvt_3_1_.get("invites").getAsJsonArray().iterator();

            while(lvt_4_1_.hasNext()) {
               lvt_1_1_.pendingInvites.add(PendingInvite.parse(((JsonElement)lvt_4_1_.next()).getAsJsonObject()));
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse PendingInvitesList: " + var5.getMessage());
      }

      return lvt_1_1_;
   }
}
