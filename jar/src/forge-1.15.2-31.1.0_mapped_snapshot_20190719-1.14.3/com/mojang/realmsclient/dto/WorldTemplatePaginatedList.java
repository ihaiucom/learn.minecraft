package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldTemplatePaginatedList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List<WorldTemplate> templates;
   public int page;
   public int size;
   public int total;

   public WorldTemplatePaginatedList() {
   }

   public WorldTemplatePaginatedList(int p_i51733_1_) {
      this.templates = Collections.emptyList();
      this.page = 0;
      this.size = p_i51733_1_;
      this.total = -1;
   }

   public boolean isLastPage() {
      return this.page * this.size >= this.total && this.page > 0 && this.total > 0 && this.size > 0;
   }

   public static WorldTemplatePaginatedList parse(String p_parse_0_) {
      WorldTemplatePaginatedList lvt_1_1_ = new WorldTemplatePaginatedList();
      lvt_1_1_.templates = Lists.newArrayList();

      try {
         JsonParser lvt_2_1_ = new JsonParser();
         JsonObject lvt_3_1_ = lvt_2_1_.parse(p_parse_0_).getAsJsonObject();
         if (lvt_3_1_.get("templates").isJsonArray()) {
            Iterator lvt_4_1_ = lvt_3_1_.get("templates").getAsJsonArray().iterator();

            while(lvt_4_1_.hasNext()) {
               lvt_1_1_.templates.add(WorldTemplate.parse(((JsonElement)lvt_4_1_.next()).getAsJsonObject()));
            }
         }

         lvt_1_1_.page = JsonUtils.func_225172_a("page", lvt_3_1_, 0);
         lvt_1_1_.size = JsonUtils.func_225172_a("size", lvt_3_1_, 0);
         lvt_1_1_.total = JsonUtils.func_225172_a("total", lvt_3_1_, 0);
      } catch (Exception var5) {
         LOGGER.error("Could not parse WorldTemplatePaginatedList: " + var5.getMessage());
      }

      return lvt_1_1_;
   }
}
