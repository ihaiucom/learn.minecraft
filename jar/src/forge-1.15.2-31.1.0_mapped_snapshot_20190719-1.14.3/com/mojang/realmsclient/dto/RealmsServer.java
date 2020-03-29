package com.mojang.realmsclient.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServer extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long id;
   public String remoteSubscriptionId;
   public String name;
   public String motd;
   public RealmsServer.Status state;
   public String owner;
   public String ownerUUID;
   public List<PlayerInfo> players;
   public Map<Integer, RealmsWorldOptions> slots;
   public boolean expired;
   public boolean expiredTrial;
   public int daysLeft;
   public RealmsServer.ServerType worldType;
   public int activeSlot;
   public String minigameName;
   public int minigameId;
   public String minigameImage;
   public RealmsServerPing serverPing = new RealmsServerPing();

   public String getDescription() {
      return this.motd;
   }

   public String getName() {
      return this.name;
   }

   public String getMinigameName() {
      return this.minigameName;
   }

   public void setName(String p_setName_1_) {
      this.name = p_setName_1_;
   }

   public void setDescription(String p_setDescription_1_) {
      this.motd = p_setDescription_1_;
   }

   public void updateServerPing(RealmsServerPlayerList p_updateServerPing_1_) {
      StringBuilder lvt_2_1_ = new StringBuilder();
      int lvt_3_1_ = 0;
      Iterator var4 = p_updateServerPing_1_.players.iterator();

      while(true) {
         String lvt_5_1_;
         do {
            if (!var4.hasNext()) {
               this.serverPing.nrOfPlayers = String.valueOf(lvt_3_1_);
               this.serverPing.playerList = lvt_2_1_.toString();
               return;
            }

            lvt_5_1_ = (String)var4.next();
         } while(lvt_5_1_.equals(Realms.getUUID()));

         String lvt_6_1_ = "";

         try {
            lvt_6_1_ = RealmsUtil.func_225193_a(lvt_5_1_);
         } catch (Exception var8) {
            LOGGER.error("Could not get name for " + lvt_5_1_, var8);
            continue;
         }

         if (lvt_2_1_.length() > 0) {
            lvt_2_1_.append("\n");
         }

         lvt_2_1_.append(lvt_6_1_);
         ++lvt_3_1_;
      }
   }

   public static RealmsServer parse(JsonObject p_parse_0_) {
      RealmsServer lvt_1_1_ = new RealmsServer();

      try {
         lvt_1_1_.id = JsonUtils.func_225169_a("id", p_parse_0_, -1L);
         lvt_1_1_.remoteSubscriptionId = JsonUtils.func_225171_a("remoteSubscriptionId", p_parse_0_, (String)null);
         lvt_1_1_.name = JsonUtils.func_225171_a("name", p_parse_0_, (String)null);
         lvt_1_1_.motd = JsonUtils.func_225171_a("motd", p_parse_0_, (String)null);
         lvt_1_1_.state = getState(JsonUtils.func_225171_a("state", p_parse_0_, RealmsServer.Status.CLOSED.name()));
         lvt_1_1_.owner = JsonUtils.func_225171_a("owner", p_parse_0_, (String)null);
         if (p_parse_0_.get("players") != null && p_parse_0_.get("players").isJsonArray()) {
            lvt_1_1_.players = parseInvited(p_parse_0_.get("players").getAsJsonArray());
            sortInvited(lvt_1_1_);
         } else {
            lvt_1_1_.players = Lists.newArrayList();
         }

         lvt_1_1_.daysLeft = JsonUtils.func_225172_a("daysLeft", p_parse_0_, 0);
         lvt_1_1_.expired = JsonUtils.func_225170_a("expired", p_parse_0_, false);
         lvt_1_1_.expiredTrial = JsonUtils.func_225170_a("expiredTrial", p_parse_0_, false);
         lvt_1_1_.worldType = getWorldType(JsonUtils.func_225171_a("worldType", p_parse_0_, RealmsServer.ServerType.NORMAL.name()));
         lvt_1_1_.ownerUUID = JsonUtils.func_225171_a("ownerUUID", p_parse_0_, "");
         if (p_parse_0_.get("slots") != null && p_parse_0_.get("slots").isJsonArray()) {
            lvt_1_1_.slots = parseSlots(p_parse_0_.get("slots").getAsJsonArray());
         } else {
            lvt_1_1_.slots = getEmptySlots();
         }

         lvt_1_1_.minigameName = JsonUtils.func_225171_a("minigameName", p_parse_0_, (String)null);
         lvt_1_1_.activeSlot = JsonUtils.func_225172_a("activeSlot", p_parse_0_, -1);
         lvt_1_1_.minigameId = JsonUtils.func_225172_a("minigameId", p_parse_0_, -1);
         lvt_1_1_.minigameImage = JsonUtils.func_225171_a("minigameImage", p_parse_0_, (String)null);
      } catch (Exception var3) {
         LOGGER.error("Could not parse McoServer: " + var3.getMessage());
      }

      return lvt_1_1_;
   }

   private static void sortInvited(RealmsServer p_sortInvited_0_) {
      p_sortInvited_0_.players.sort((p_229951_0_, p_229951_1_) -> {
         return ComparisonChain.start().compareFalseFirst(p_229951_1_.getAccepted(), p_229951_0_.getAccepted()).compare(p_229951_0_.getName().toLowerCase(Locale.ROOT), p_229951_1_.getName().toLowerCase(Locale.ROOT)).result();
      });
   }

   private static List<PlayerInfo> parseInvited(JsonArray p_parseInvited_0_) {
      List<PlayerInfo> lvt_1_1_ = Lists.newArrayList();
      Iterator var2 = p_parseInvited_0_.iterator();

      while(var2.hasNext()) {
         JsonElement lvt_3_1_ = (JsonElement)var2.next();

         try {
            JsonObject lvt_4_1_ = lvt_3_1_.getAsJsonObject();
            PlayerInfo lvt_5_1_ = new PlayerInfo();
            lvt_5_1_.setName(JsonUtils.func_225171_a("name", lvt_4_1_, (String)null));
            lvt_5_1_.setUuid(JsonUtils.func_225171_a("uuid", lvt_4_1_, (String)null));
            lvt_5_1_.setOperator(JsonUtils.func_225170_a("operator", lvt_4_1_, false));
            lvt_5_1_.setAccepted(JsonUtils.func_225170_a("accepted", lvt_4_1_, false));
            lvt_5_1_.setOnline(JsonUtils.func_225170_a("online", lvt_4_1_, false));
            lvt_1_1_.add(lvt_5_1_);
         } catch (Exception var6) {
         }
      }

      return lvt_1_1_;
   }

   private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray p_parseSlots_0_) {
      Map<Integer, RealmsWorldOptions> lvt_1_1_ = Maps.newHashMap();
      Iterator var2 = p_parseSlots_0_.iterator();

      while(var2.hasNext()) {
         JsonElement lvt_3_1_ = (JsonElement)var2.next();

         try {
            JsonObject lvt_5_1_ = lvt_3_1_.getAsJsonObject();
            JsonParser lvt_6_1_ = new JsonParser();
            JsonElement lvt_7_1_ = lvt_6_1_.parse(lvt_5_1_.get("options").getAsString());
            RealmsWorldOptions lvt_4_2_;
            if (lvt_7_1_ == null) {
               lvt_4_2_ = RealmsWorldOptions.getDefaults();
            } else {
               lvt_4_2_ = RealmsWorldOptions.parse(lvt_7_1_.getAsJsonObject());
            }

            int lvt_8_1_ = JsonUtils.func_225172_a("slotId", lvt_5_1_, -1);
            lvt_1_1_.put(lvt_8_1_, lvt_4_2_);
         } catch (Exception var9) {
         }
      }

      for(int lvt_2_1_ = 1; lvt_2_1_ <= 3; ++lvt_2_1_) {
         if (!lvt_1_1_.containsKey(lvt_2_1_)) {
            lvt_1_1_.put(lvt_2_1_, RealmsWorldOptions.getEmptyDefaults());
         }
      }

      return lvt_1_1_;
   }

   private static Map<Integer, RealmsWorldOptions> getEmptySlots() {
      Map<Integer, RealmsWorldOptions> lvt_0_1_ = Maps.newHashMap();
      lvt_0_1_.put(1, RealmsWorldOptions.getEmptyDefaults());
      lvt_0_1_.put(2, RealmsWorldOptions.getEmptyDefaults());
      lvt_0_1_.put(3, RealmsWorldOptions.getEmptyDefaults());
      return lvt_0_1_;
   }

   public static RealmsServer parse(String p_parse_0_) {
      RealmsServer lvt_1_1_ = new RealmsServer();

      try {
         JsonParser lvt_2_1_ = new JsonParser();
         JsonObject lvt_3_1_ = lvt_2_1_.parse(p_parse_0_).getAsJsonObject();
         lvt_1_1_ = parse(lvt_3_1_);
      } catch (Exception var4) {
         LOGGER.error("Could not parse McoServer: " + var4.getMessage());
      }

      return lvt_1_1_;
   }

   private static RealmsServer.Status getState(String p_getState_0_) {
      try {
         return RealmsServer.Status.valueOf(p_getState_0_);
      } catch (Exception var2) {
         return RealmsServer.Status.CLOSED;
      }
   }

   private static RealmsServer.ServerType getWorldType(String p_getWorldType_0_) {
      try {
         return RealmsServer.ServerType.valueOf(p_getWorldType_0_);
      } catch (Exception var2) {
         return RealmsServer.ServerType.NORMAL;
      }
   }

   public int hashCode() {
      return (new HashCodeBuilder(17, 37)).append(this.id).append(this.name).append(this.motd).append(this.state).append(this.owner).append(this.expired).toHashCode();
   }

   public boolean equals(Object p_equals_1_) {
      if (p_equals_1_ == null) {
         return false;
      } else if (p_equals_1_ == this) {
         return true;
      } else if (p_equals_1_.getClass() != this.getClass()) {
         return false;
      } else {
         RealmsServer lvt_2_1_ = (RealmsServer)p_equals_1_;
         return (new EqualsBuilder()).append(this.id, lvt_2_1_.id).append(this.name, lvt_2_1_.name).append(this.motd, lvt_2_1_.motd).append(this.state, lvt_2_1_.state).append(this.owner, lvt_2_1_.owner).append(this.expired, lvt_2_1_.expired).append(this.worldType, this.worldType).isEquals();
      }
   }

   public RealmsServer clone() {
      RealmsServer lvt_1_1_ = new RealmsServer();
      lvt_1_1_.id = this.id;
      lvt_1_1_.remoteSubscriptionId = this.remoteSubscriptionId;
      lvt_1_1_.name = this.name;
      lvt_1_1_.motd = this.motd;
      lvt_1_1_.state = this.state;
      lvt_1_1_.owner = this.owner;
      lvt_1_1_.players = this.players;
      lvt_1_1_.slots = this.cloneSlots(this.slots);
      lvt_1_1_.expired = this.expired;
      lvt_1_1_.expiredTrial = this.expiredTrial;
      lvt_1_1_.daysLeft = this.daysLeft;
      lvt_1_1_.serverPing = new RealmsServerPing();
      lvt_1_1_.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
      lvt_1_1_.serverPing.playerList = this.serverPing.playerList;
      lvt_1_1_.worldType = this.worldType;
      lvt_1_1_.ownerUUID = this.ownerUUID;
      lvt_1_1_.minigameName = this.minigameName;
      lvt_1_1_.activeSlot = this.activeSlot;
      lvt_1_1_.minigameId = this.minigameId;
      lvt_1_1_.minigameImage = this.minigameImage;
      return lvt_1_1_;
   }

   public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> p_cloneSlots_1_) {
      Map<Integer, RealmsWorldOptions> lvt_2_1_ = Maps.newHashMap();
      Iterator var3 = p_cloneSlots_1_.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<Integer, RealmsWorldOptions> lvt_4_1_ = (Entry)var3.next();
         lvt_2_1_.put(lvt_4_1_.getKey(), ((RealmsWorldOptions)lvt_4_1_.getValue()).clone());
      }

      return lvt_2_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ServerType {
      NORMAL,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Status {
      CLOSED,
      OPEN,
      UNINITIALIZED;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerComparator implements Comparator<RealmsServer> {
      private final String field_223701_a;

      public ServerComparator(String p_i51687_1_) {
         this.field_223701_a = p_i51687_1_;
      }

      public int compare(RealmsServer p_compare_1_, RealmsServer p_compare_2_) {
         return ComparisonChain.start().compareTrueFirst(p_compare_1_.state.equals(RealmsServer.Status.UNINITIALIZED), p_compare_2_.state.equals(RealmsServer.Status.UNINITIALIZED)).compareTrueFirst(p_compare_1_.expiredTrial, p_compare_2_.expiredTrial).compareTrueFirst(p_compare_1_.owner.equals(this.field_223701_a), p_compare_2_.owner.equals(this.field_223701_a)).compareFalseFirst(p_compare_1_.expired, p_compare_2_.expired).compareTrueFirst(p_compare_1_.state.equals(RealmsServer.Status.OPEN), p_compare_2_.state.equals(RealmsServer.Status.OPEN)).compare(p_compare_1_.id, p_compare_2_.id).result();
      }

      // $FF: synthetic method
      public int compare(Object p_compare_1_, Object p_compare_2_) {
         return this.compare((RealmsServer)p_compare_1_, (RealmsServer)p_compare_2_);
      }
   }
}
