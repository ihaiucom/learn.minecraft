package com.mojang.realmsclient.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import net.minecraft.realms.Realms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsClient {
   public static RealmsClient.Environment field_224944_a;
   private static boolean field_224945_b;
   private static final Logger field_224946_c;
   private final String field_224947_d;
   private final String field_224948_e;
   private static final Gson field_224949_f;

   public static RealmsClient func_224911_a() {
      String lvt_0_1_ = Realms.userName();
      String lvt_1_1_ = Realms.sessionId();
      if (lvt_0_1_ != null && lvt_1_1_ != null) {
         if (!field_224945_b) {
            field_224945_b = true;
            String lvt_2_1_ = System.getenv("realms.environment");
            if (lvt_2_1_ == null) {
               lvt_2_1_ = System.getProperty("realms.environment");
            }

            if (lvt_2_1_ != null) {
               if ("LOCAL".equals(lvt_2_1_)) {
                  func_224941_d();
               } else if ("STAGE".equals(lvt_2_1_)) {
                  func_224940_b();
               }
            }
         }

         return new RealmsClient(lvt_1_1_, lvt_0_1_, Realms.getProxy());
      } else {
         return null;
      }
   }

   public static void func_224940_b() {
      field_224944_a = RealmsClient.Environment.STAGE;
   }

   public static void func_224921_c() {
      field_224944_a = RealmsClient.Environment.PRODUCTION;
   }

   public static void func_224941_d() {
      field_224944_a = RealmsClient.Environment.LOCAL;
   }

   public RealmsClient(String p_i51790_1_, String p_i51790_2_, Proxy p_i51790_3_) {
      this.field_224947_d = p_i51790_1_;
      this.field_224948_e = p_i51790_2_;
      RealmsClientConfig.func_224896_a(p_i51790_3_);
   }

   public RealmsServerList func_224902_e() throws RealmsServiceException, IOException {
      String lvt_1_1_ = this.func_224926_c("worlds");
      String lvt_2_1_ = this.func_224938_a(Request.func_224953_a(lvt_1_1_));
      return RealmsServerList.parse(lvt_2_1_);
   }

   public RealmsServer func_224935_a(long p_224935_1_) throws RealmsServiceException, IOException {
      String lvt_3_1_ = this.func_224926_c("worlds" + "/$ID".replace("$ID", String.valueOf(p_224935_1_)));
      String lvt_4_1_ = this.func_224938_a(Request.func_224953_a(lvt_3_1_));
      return RealmsServer.parse(lvt_4_1_);
   }

   public RealmsServerPlayerLists func_224915_f() throws RealmsServiceException {
      String lvt_1_1_ = this.func_224926_c("activities/liveplayerlist");
      String lvt_2_1_ = this.func_224938_a(Request.func_224953_a(lvt_1_1_));
      return RealmsServerPlayerLists.parse(lvt_2_1_);
   }

   public RealmsServerAddress func_224904_b(long p_224904_1_) throws RealmsServiceException, IOException {
      String lvt_3_1_ = this.func_224926_c("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + p_224904_1_));
      String lvt_4_1_ = this.func_224938_a(Request.func_224960_a(lvt_3_1_, 5000, 30000));
      return RealmsServerAddress.parse(lvt_4_1_);
   }

   public void func_224900_a(long p_224900_1_, String p_224900_3_, String p_224900_4_) throws RealmsServiceException, IOException {
      RealmsDescriptionDto lvt_5_1_ = new RealmsDescriptionDto(p_224900_3_, p_224900_4_);
      String lvt_6_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(p_224900_1_)));
      String lvt_7_1_ = field_224949_f.toJson(lvt_5_1_);
      this.func_224938_a(Request.func_224959_a(lvt_6_1_, lvt_7_1_, 5000, 10000));
   }

   public Boolean func_224918_g() throws RealmsServiceException, IOException {
      String lvt_1_1_ = this.func_224926_c("mco/available");
      String lvt_2_1_ = this.func_224938_a(Request.func_224953_a(lvt_1_1_));
      return Boolean.valueOf(lvt_2_1_);
   }

   public Boolean func_224931_h() throws RealmsServiceException, IOException {
      String lvt_1_1_ = this.func_224926_c("mco/stageAvailable");
      String lvt_2_1_ = this.func_224938_a(Request.func_224953_a(lvt_1_1_));
      return Boolean.valueOf(lvt_2_1_);
   }

   public RealmsClient.CompatibleVersionResponse func_224939_i() throws RealmsServiceException, IOException {
      String lvt_1_1_ = this.func_224926_c("mco/client/compatible");
      String lvt_2_1_ = this.func_224938_a(Request.func_224953_a(lvt_1_1_));

      try {
         RealmsClient.CompatibleVersionResponse lvt_3_2_ = RealmsClient.CompatibleVersionResponse.valueOf(lvt_2_1_);
         return lvt_3_2_;
      } catch (IllegalArgumentException var5) {
         throw new RealmsServiceException(500, "Could not check compatible version, got response: " + lvt_2_1_, -1, "");
      }
   }

   public void func_224908_a(long p_224908_1_, String p_224908_3_) throws RealmsServiceException {
      String lvt_4_1_ = this.func_224926_c("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(p_224908_1_)).replace("$UUID", p_224908_3_));
      this.func_224938_a(Request.func_224952_b(lvt_4_1_));
   }

   public void func_224912_c(long p_224912_1_) throws RealmsServiceException {
      String lvt_3_1_ = this.func_224926_c("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224912_1_)));
      this.func_224938_a(Request.func_224952_b(lvt_3_1_));
   }

   public RealmsServer func_224910_b(long p_224910_1_, String p_224910_3_) throws RealmsServiceException, IOException {
      PlayerInfo lvt_4_1_ = new PlayerInfo();
      lvt_4_1_.setName(p_224910_3_);
      String lvt_5_1_ = this.func_224926_c("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224910_1_)));
      String lvt_6_1_ = this.func_224938_a(Request.func_224951_b(lvt_5_1_, field_224949_f.toJson(lvt_4_1_)));
      return RealmsServer.parse(lvt_6_1_);
   }

   public BackupList func_224923_d(long p_224923_1_) throws RealmsServiceException {
      String lvt_3_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_224923_1_)));
      String lvt_4_1_ = this.func_224938_a(Request.func_224953_a(lvt_3_1_));
      return BackupList.parse(lvt_4_1_);
   }

   public void func_224922_b(long p_224922_1_, String p_224922_3_, String p_224922_4_) throws RealmsServiceException, UnsupportedEncodingException {
      RealmsDescriptionDto lvt_5_1_ = new RealmsDescriptionDto(p_224922_3_, p_224922_4_);
      String lvt_6_1_ = this.func_224926_c("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224922_1_)));
      this.func_224938_a(Request.func_224951_b(lvt_6_1_, field_224949_f.toJson(lvt_5_1_)));
   }

   public void func_224925_a(long p_224925_1_, int p_224925_3_, RealmsWorldOptions p_224925_4_) throws RealmsServiceException, UnsupportedEncodingException {
      String lvt_5_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_224925_1_)).replace("$SLOT_ID", String.valueOf(p_224925_3_)));
      String lvt_6_1_ = p_224925_4_.toJson();
      this.func_224938_a(Request.func_224951_b(lvt_5_1_, lvt_6_1_));
   }

   public boolean func_224927_a(long p_224927_1_, int p_224927_3_) throws RealmsServiceException {
      String lvt_4_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_224927_1_)).replace("$SLOT_ID", String.valueOf(p_224927_3_)));
      String lvt_5_1_ = this.func_224938_a(Request.func_224965_c(lvt_4_1_, ""));
      return Boolean.valueOf(lvt_5_1_);
   }

   public void func_224928_c(long p_224928_1_, String p_224928_3_) throws RealmsServiceException {
      String lvt_4_1_ = this.func_224907_b("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_224928_1_)), "backupId=" + p_224928_3_);
      this.func_224938_a(Request.func_224966_b(lvt_4_1_, "", 40000, 600000));
   }

   public WorldTemplatePaginatedList func_224930_a(int p_224930_1_, int p_224930_2_, RealmsServer.ServerType p_224930_3_) throws RealmsServiceException {
      String lvt_4_1_ = this.func_224907_b("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", p_224930_3_.toString()), String.format("page=%d&pageSize=%d", p_224930_1_, p_224930_2_));
      String lvt_5_1_ = this.func_224938_a(Request.func_224953_a(lvt_4_1_));
      return WorldTemplatePaginatedList.parse(lvt_5_1_);
   }

   public Boolean func_224905_d(long p_224905_1_, String p_224905_3_) throws RealmsServiceException {
      String lvt_4_1_ = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", p_224905_3_).replace("$WORLD_ID", String.valueOf(p_224905_1_));
      String lvt_5_1_ = this.func_224926_c("worlds" + lvt_4_1_);
      return Boolean.valueOf(this.func_224938_a(Request.func_224965_c(lvt_5_1_, "")));
   }

   public Ops func_224906_e(long p_224906_1_, String p_224906_3_) throws RealmsServiceException {
      String lvt_4_1_ = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_224906_1_)).replace("$PROFILE_UUID", p_224906_3_);
      String lvt_5_1_ = this.func_224926_c("ops" + lvt_4_1_);
      return Ops.parse(this.func_224938_a(Request.func_224951_b(lvt_5_1_, "")));
   }

   public Ops func_224929_f(long p_224929_1_, String p_224929_3_) throws RealmsServiceException {
      String lvt_4_1_ = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_224929_1_)).replace("$PROFILE_UUID", p_224929_3_);
      String lvt_5_1_ = this.func_224926_c("ops" + lvt_4_1_);
      return Ops.parse(this.func_224938_a(Request.func_224952_b(lvt_5_1_)));
   }

   public Boolean func_224942_e(long p_224942_1_) throws RealmsServiceException, IOException {
      String lvt_3_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(p_224942_1_)));
      String lvt_4_1_ = this.func_224938_a(Request.func_224965_c(lvt_3_1_, ""));
      return Boolean.valueOf(lvt_4_1_);
   }

   public Boolean func_224932_f(long p_224932_1_) throws RealmsServiceException, IOException {
      String lvt_3_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(p_224932_1_)));
      String lvt_4_1_ = this.func_224938_a(Request.func_224965_c(lvt_3_1_, ""));
      return Boolean.valueOf(lvt_4_1_);
   }

   public Boolean func_224943_a(long p_224943_1_, String p_224943_3_, Integer p_224943_4_, boolean p_224943_5_) throws RealmsServiceException, IOException {
      RealmsWorldResetDto lvt_6_1_ = new RealmsWorldResetDto(p_224943_3_, -1L, p_224943_4_, p_224943_5_);
      String lvt_7_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_224943_1_)));
      String lvt_8_1_ = this.func_224938_a(Request.func_224959_a(lvt_7_1_, field_224949_f.toJson(lvt_6_1_), 30000, 80000));
      return Boolean.valueOf(lvt_8_1_);
   }

   public Boolean func_224924_g(long p_224924_1_, String p_224924_3_) throws RealmsServiceException, IOException {
      RealmsWorldResetDto lvt_4_1_ = new RealmsWorldResetDto((String)null, Long.valueOf(p_224924_3_), -1, false);
      String lvt_5_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_224924_1_)));
      String lvt_6_1_ = this.func_224938_a(Request.func_224959_a(lvt_5_1_, field_224949_f.toJson(lvt_4_1_), 30000, 80000));
      return Boolean.valueOf(lvt_6_1_);
   }

   public Subscription func_224933_g(long p_224933_1_) throws RealmsServiceException, IOException {
      String lvt_3_1_ = this.func_224926_c("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224933_1_)));
      String lvt_4_1_ = this.func_224938_a(Request.func_224953_a(lvt_3_1_));
      return Subscription.parse(lvt_4_1_);
   }

   public int func_224909_j() throws RealmsServiceException {
      String lvt_1_1_ = this.func_224926_c("invites/count/pending");
      String lvt_2_1_ = this.func_224938_a(Request.func_224953_a(lvt_1_1_));
      return Integer.parseInt(lvt_2_1_);
   }

   public PendingInvitesList func_224919_k() throws RealmsServiceException {
      String lvt_1_1_ = this.func_224926_c("invites/pending");
      String lvt_2_1_ = this.func_224938_a(Request.func_224953_a(lvt_1_1_));
      return PendingInvitesList.parse(lvt_2_1_);
   }

   public void func_224901_a(String p_224901_1_) throws RealmsServiceException {
      String lvt_2_1_ = this.func_224926_c("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", p_224901_1_));
      this.func_224938_a(Request.func_224965_c(lvt_2_1_, ""));
   }

   public WorldDownload func_224917_b(long p_224917_1_, int p_224917_3_) throws RealmsServiceException {
      String lvt_4_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(p_224917_1_)).replace("$SLOT_ID", String.valueOf(p_224917_3_)));
      String lvt_5_1_ = this.func_224938_a(Request.func_224953_a(lvt_4_1_));
      return WorldDownload.parse(lvt_5_1_);
   }

   public UploadInfo func_224934_h(long p_224934_1_, String p_224934_3_) throws RealmsServiceException {
      String lvt_4_1_ = this.func_224926_c("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(p_224934_1_)));
      UploadInfo lvt_5_1_ = new UploadInfo();
      if (p_224934_3_ != null) {
         lvt_5_1_.setToken(p_224934_3_);
      }

      GsonBuilder lvt_6_1_ = new GsonBuilder();
      lvt_6_1_.excludeFieldsWithoutExposeAnnotation();
      Gson lvt_7_1_ = lvt_6_1_.create();
      String lvt_8_1_ = lvt_7_1_.toJson(lvt_5_1_);
      return UploadInfo.parse(this.func_224938_a(Request.func_224965_c(lvt_4_1_, lvt_8_1_)));
   }

   public void func_224913_b(String p_224913_1_) throws RealmsServiceException {
      String lvt_2_1_ = this.func_224926_c("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", p_224913_1_));
      this.func_224938_a(Request.func_224965_c(lvt_2_1_, ""));
   }

   public void func_224937_l() throws RealmsServiceException {
      String lvt_1_1_ = this.func_224926_c("mco/tos/agreed");
      this.func_224938_a(Request.func_224951_b(lvt_1_1_, ""));
   }

   public RealmsNews func_224920_m() throws RealmsServiceException, IOException {
      String lvt_1_1_ = this.func_224926_c("mco/v1/news");
      String lvt_2_1_ = this.func_224938_a(Request.func_224960_a(lvt_1_1_, 5000, 10000));
      return RealmsNews.parse(lvt_2_1_);
   }

   public void func_224903_a(PingResult p_224903_1_) throws RealmsServiceException {
      String lvt_2_1_ = this.func_224926_c("regions/ping/stat");
      this.func_224938_a(Request.func_224951_b(lvt_2_1_, field_224949_f.toJson(p_224903_1_)));
   }

   public Boolean func_224914_n() throws RealmsServiceException, IOException {
      String lvt_1_1_ = this.func_224926_c("trial");
      String lvt_2_1_ = this.func_224938_a(Request.func_224953_a(lvt_1_1_));
      return Boolean.valueOf(lvt_2_1_);
   }

   public void func_224916_h(long p_224916_1_) throws RealmsServiceException, IOException {
      String lvt_3_1_ = this.func_224926_c("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224916_1_)));
      this.func_224938_a(Request.func_224952_b(lvt_3_1_));
   }

   private String func_224926_c(String p_224926_1_) {
      return this.func_224907_b(p_224926_1_, (String)null);
   }

   private String func_224907_b(String p_224907_1_, String p_224907_2_) {
      try {
         URI lvt_3_1_ = new URI(field_224944_a.field_224899_e, field_224944_a.field_224898_d, "/" + p_224907_1_, p_224907_2_, (String)null);
         return lvt_3_1_.toASCIIString();
      } catch (URISyntaxException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private String func_224938_a(Request<?> p_224938_1_) throws RealmsServiceException {
      p_224938_1_.func_224962_a("sid", this.field_224947_d);
      p_224938_1_.func_224962_a("user", this.field_224948_e);
      p_224938_1_.func_224962_a("version", Realms.getMinecraftVersionString());

      try {
         int lvt_2_1_ = p_224938_1_.func_224958_b();
         if (lvt_2_1_ == 503) {
            int lvt_3_1_ = p_224938_1_.func_224957_a();
            throw new RetryCallException(lvt_3_1_);
         } else {
            String lvt_3_2_ = p_224938_1_.func_224963_c();
            if (lvt_2_1_ >= 200 && lvt_2_1_ < 300) {
               return lvt_3_2_;
            } else if (lvt_2_1_ == 401) {
               String lvt_4_1_ = p_224938_1_.func_224956_c("WWW-Authenticate");
               field_224946_c.info("Could not authorize you against Realms server: " + lvt_4_1_);
               throw new RealmsServiceException(lvt_2_1_, lvt_4_1_, -1, lvt_4_1_);
            } else if (lvt_3_2_ != null && lvt_3_2_.length() != 0) {
               RealmsError lvt_4_2_ = new RealmsError(lvt_3_2_);
               field_224946_c.error("Realms http code: " + lvt_2_1_ + " -  error code: " + lvt_4_2_.func_224974_b() + " -  message: " + lvt_4_2_.func_224973_a() + " - raw body: " + lvt_3_2_);
               throw new RealmsServiceException(lvt_2_1_, lvt_3_2_, lvt_4_2_);
            } else {
               field_224946_c.error("Realms error code: " + lvt_2_1_ + " message: " + lvt_3_2_);
               throw new RealmsServiceException(lvt_2_1_, lvt_3_2_, lvt_2_1_, "");
            }
         }
      } catch (RealmsHttpException var5) {
         throw new RealmsServiceException(500, "Could not connect to Realms: " + var5.getMessage(), -1, "");
      }
   }

   static {
      field_224944_a = RealmsClient.Environment.PRODUCTION;
      field_224946_c = LogManager.getLogger();
      field_224949_f = new Gson();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum CompatibleVersionResponse {
      COMPATIBLE,
      OUTDATED,
      OTHER;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Environment {
      PRODUCTION("pc.realms.minecraft.net", "https"),
      STAGE("pc-stage.realms.minecraft.net", "https"),
      LOCAL("localhost:8080", "http");

      public String field_224898_d;
      public String field_224899_e;

      private Environment(String p_i51584_3_, String p_i51584_4_) {
         this.field_224898_d = p_i51584_3_;
         this.field_224899_e = p_i51584_4_;
      }
   }
}
