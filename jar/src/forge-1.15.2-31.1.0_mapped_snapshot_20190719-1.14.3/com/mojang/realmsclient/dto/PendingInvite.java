package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PendingInvite extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String invitationId;
   public String worldName;
   public String worldOwnerName;
   public String worldOwnerUuid;
   public Date date;

   public static PendingInvite parse(JsonObject p_parse_0_) {
      PendingInvite lvt_1_1_ = new PendingInvite();

      try {
         lvt_1_1_.invitationId = JsonUtils.func_225171_a("invitationId", p_parse_0_, "");
         lvt_1_1_.worldName = JsonUtils.func_225171_a("worldName", p_parse_0_, "");
         lvt_1_1_.worldOwnerName = JsonUtils.func_225171_a("worldOwnerName", p_parse_0_, "");
         lvt_1_1_.worldOwnerUuid = JsonUtils.func_225171_a("worldOwnerUuid", p_parse_0_, "");
         lvt_1_1_.date = JsonUtils.func_225173_a("date", p_parse_0_);
      } catch (Exception var3) {
         LOGGER.error("Could not parse PendingInvite: " + var3.getMessage());
      }

      return lvt_1_1_;
   }
}
