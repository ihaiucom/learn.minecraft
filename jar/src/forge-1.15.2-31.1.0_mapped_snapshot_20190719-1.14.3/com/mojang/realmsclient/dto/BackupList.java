package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BackupList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List<Backup> backups;

   public static BackupList parse(String p_parse_0_) {
      JsonParser lvt_1_1_ = new JsonParser();
      BackupList lvt_2_1_ = new BackupList();
      lvt_2_1_.backups = Lists.newArrayList();

      try {
         JsonElement lvt_3_1_ = lvt_1_1_.parse(p_parse_0_).getAsJsonObject().get("backups");
         if (lvt_3_1_.isJsonArray()) {
            Iterator lvt_4_1_ = lvt_3_1_.getAsJsonArray().iterator();

            while(lvt_4_1_.hasNext()) {
               lvt_2_1_.backups.add(Backup.parse((JsonElement)lvt_4_1_.next()));
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse BackupList: " + var5.getMessage());
      }

      return lvt_2_1_;
   }
}
