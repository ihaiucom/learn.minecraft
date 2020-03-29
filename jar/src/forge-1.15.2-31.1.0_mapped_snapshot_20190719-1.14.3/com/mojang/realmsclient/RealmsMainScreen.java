package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.KeyCombo;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
   private static final Logger field_224012_a = LogManager.getLogger();
   private static boolean field_224013_b;
   private final RateLimiter field_224014_c;
   private boolean field_224015_d;
   private static List<ResourceLocation> field_227918_e_ = ImmutableList.of();
   private static final RealmsDataFetcher field_224017_f = new RealmsDataFetcher();
   private static int field_224018_g = -1;
   private final RealmsScreen field_224019_h;
   private volatile RealmsMainScreen.ServerList field_224020_i;
   private long field_224021_j = -1L;
   private RealmsButton field_224022_k;
   private RealmsButton field_224023_l;
   private RealmsButton field_224024_m;
   private RealmsButton field_224025_n;
   private RealmsButton field_224026_o;
   private String field_224027_p;
   private List<RealmsServer> field_224028_q = Lists.newArrayList();
   private volatile int field_224029_r;
   private int field_224030_s;
   private static volatile boolean field_224031_t;
   private static volatile boolean field_224032_u;
   private static volatile boolean field_224033_v;
   private boolean field_224034_w;
   private boolean field_224035_x;
   private boolean field_224036_y;
   private volatile boolean field_224037_z;
   private volatile boolean field_223993_A;
   private volatile boolean field_223994_B;
   private volatile boolean field_223995_C;
   private volatile String field_223996_D;
   private int field_223997_E;
   private int field_223998_F;
   private boolean field_223999_G;
   private static RealmsScreen field_224000_H;
   private static boolean field_224001_I;
   private List<KeyCombo> field_224002_J;
   private int field_224003_K;
   private ReentrantLock field_224004_L = new ReentrantLock();
   private boolean field_224005_M;
   private RealmsMainScreen.InfoButton field_224006_N;
   private RealmsMainScreen.PendingInvitesButton field_224007_O;
   private RealmsMainScreen.NewsButton field_224008_P;
   private RealmsButton field_224009_Q;
   private RealmsButton field_224010_R;
   private RealmsButton field_224011_S;

   public RealmsMainScreen(RealmsScreen p_i51792_1_) {
      this.field_224019_h = p_i51792_1_;
      this.field_224014_c = RateLimiter.create(0.01666666753590107D);
   }

   public boolean func_223928_a() {
      if (this.func_223968_l() && this.field_224034_w) {
         if (this.field_224037_z && !this.field_223993_A) {
            return true;
         } else {
            Iterator var1 = this.field_224028_q.iterator();

            RealmsServer lvt_2_1_;
            do {
               if (!var1.hasNext()) {
                  return true;
               }

               lvt_2_1_ = (RealmsServer)var1.next();
            } while(!lvt_2_1_.ownerUUID.equals(Realms.getUUID()));

            return false;
         }
      } else {
         return false;
      }
   }

   public boolean func_223990_b() {
      if (this.func_223968_l() && this.field_224034_w) {
         if (this.field_224035_x) {
            return true;
         } else {
            return this.field_224037_z && !this.field_223993_A && this.field_224028_q.isEmpty() ? true : this.field_224028_q.isEmpty();
         }
      } else {
         return false;
      }
   }

   public void init() {
      this.field_224002_J = Lists.newArrayList(new KeyCombo[]{new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
         field_224013_b = !field_224013_b;
      }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
         if (RealmsClient.field_224944_a.equals(RealmsClient.Environment.STAGE)) {
            this.func_223973_x();
         } else {
            this.func_223884_v();
         }

      }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
         if (RealmsClient.field_224944_a.equals(RealmsClient.Environment.LOCAL)) {
            this.func_223973_x();
         } else {
            this.func_223962_w();
         }

      })});
      if (field_224000_H != null) {
         Realms.setScreen(field_224000_H);
      } else {
         this.field_224004_L = new ReentrantLock();
         if (field_224033_v && !this.func_223968_l()) {
            this.func_223975_u();
         }

         this.func_223895_s();
         this.func_223965_t();
         if (!this.field_224015_d) {
            Realms.setConnectedToRealms(false);
         }

         this.setKeyboardHandlerSendRepeatsToGui(true);
         if (this.func_223968_l()) {
            field_224017_f.func_225087_d();
         }

         this.field_223994_B = false;
         this.func_223970_d();
      }
   }

   private boolean func_223968_l() {
      return field_224032_u && field_224031_t;
   }

   public void func_223901_c() {
      this.buttonsAdd(this.field_224025_n = new RealmsButton(1, this.width() / 2 - 190, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.configure")) {
         public void onPress() {
            RealmsMainScreen.this.func_223966_f(RealmsMainScreen.this.func_223967_a(RealmsMainScreen.this.field_224021_j));
         }
      });
      this.buttonsAdd(this.field_224022_k = new RealmsButton(3, this.width() / 2 - 93, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.play")) {
         public void onPress() {
            RealmsMainScreen.this.func_223914_p();
         }
      });
      this.buttonsAdd(this.field_224023_l = new RealmsButton(2, this.width() / 2 + 4, this.height() - 32, 90, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            if (!RealmsMainScreen.this.field_224036_y) {
               Realms.setScreen(RealmsMainScreen.this.field_224019_h);
            }

         }
      });
      this.buttonsAdd(this.field_224024_m = new RealmsButton(0, this.width() / 2 + 100, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.expiredRenew")) {
         public void onPress() {
            RealmsMainScreen.this.func_223930_q();
         }
      });
      this.buttonsAdd(this.field_224026_o = new RealmsButton(7, this.width() / 2 - 202, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.leave")) {
         public void onPress() {
            RealmsMainScreen.this.func_223906_g(RealmsMainScreen.this.func_223967_a(RealmsMainScreen.this.field_224021_j));
         }
      });
      this.buttonsAdd(this.field_224007_O = new RealmsMainScreen.PendingInvitesButton());
      this.buttonsAdd(this.field_224008_P = new RealmsMainScreen.NewsButton());
      this.buttonsAdd(this.field_224006_N = new RealmsMainScreen.InfoButton());
      this.buttonsAdd(this.field_224011_S = new RealmsMainScreen.CloseButton());
      this.buttonsAdd(this.field_224009_Q = new RealmsButton(6, this.width() / 2 + 52, this.func_223932_C() + 137 - 20, 98, 20, getLocalizedString("mco.selectServer.trial")) {
         public void onPress() {
            RealmsMainScreen.this.func_223988_r();
         }
      });
      this.buttonsAdd(this.field_224010_R = new RealmsButton(5, this.width() / 2 + 52, this.func_223932_C() + 160 - 20, 98, 20, getLocalizedString("mco.selectServer.buy")) {
         public void onPress() {
            RealmsUtil.func_225190_c("https://aka.ms/BuyJavaRealms");
         }
      });
      RealmsServer lvt_1_1_ = this.func_223967_a(this.field_224021_j);
      this.func_223915_a(lvt_1_1_);
   }

   private void func_223915_a(RealmsServer p_223915_1_) {
      this.field_224022_k.active(this.func_223897_b(p_223915_1_) && !this.func_223990_b());
      this.field_224024_m.setVisible(this.func_223920_c(p_223915_1_));
      this.field_224025_n.setVisible(this.func_223941_d(p_223915_1_));
      this.field_224026_o.setVisible(this.func_223959_e(p_223915_1_));
      boolean lvt_2_1_ = this.func_223990_b() && this.field_224037_z && !this.field_223993_A;
      this.field_224009_Q.setVisible(lvt_2_1_);
      this.field_224009_Q.active(lvt_2_1_);
      this.field_224010_R.setVisible(this.func_223990_b());
      this.field_224011_S.setVisible(this.func_223990_b() && this.field_224035_x);
      this.field_224024_m.active(!this.func_223990_b());
      this.field_224025_n.active(!this.func_223990_b());
      this.field_224026_o.active(!this.func_223990_b());
      this.field_224008_P.active(true);
      this.field_224007_O.active(true);
      this.field_224023_l.active(true);
      this.field_224006_N.active(!this.func_223990_b());
   }

   private boolean func_223977_m() {
      return (!this.func_223990_b() || this.field_224035_x) && this.func_223968_l() && this.field_224034_w;
   }

   private boolean func_223897_b(RealmsServer p_223897_1_) {
      return p_223897_1_ != null && !p_223897_1_.expired && p_223897_1_.state == RealmsServer.Status.OPEN;
   }

   private boolean func_223920_c(RealmsServer p_223920_1_) {
      return p_223920_1_ != null && p_223920_1_.expired && this.func_223885_h(p_223920_1_);
   }

   private boolean func_223941_d(RealmsServer p_223941_1_) {
      return p_223941_1_ != null && this.func_223885_h(p_223941_1_);
   }

   private boolean func_223959_e(RealmsServer p_223959_1_) {
      return p_223959_1_ != null && !this.func_223885_h(p_223959_1_);
   }

   public void func_223970_d() {
      if (this.func_223968_l() && this.field_224034_w) {
         this.func_223901_c();
      }

      this.field_224020_i = new RealmsMainScreen.ServerList();
      if (field_224018_g != -1) {
         this.field_224020_i.scroll(field_224018_g);
      }

      this.addWidget(this.field_224020_i);
      this.focusOn(this.field_224020_i);
   }

   public void tick() {
      this.tickButtons();
      this.field_224036_y = false;
      ++this.field_224030_s;
      --this.field_224003_K;
      if (this.field_224003_K < 0) {
         this.field_224003_K = 0;
      }

      if (this.func_223968_l()) {
         field_224017_f.func_225086_b();
         Iterator var4;
         RealmsServer lvt_5_3_;
         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.SERVER_LIST)) {
            List<RealmsServer> lvt_1_1_ = field_224017_f.func_225078_e();
            this.field_224020_i.clear();
            boolean lvt_2_1_ = !this.field_224034_w;
            if (lvt_2_1_) {
               this.field_224034_w = true;
            }

            if (lvt_1_1_ != null) {
               boolean lvt_3_1_ = false;
               var4 = lvt_1_1_.iterator();

               while(var4.hasNext()) {
                  lvt_5_3_ = (RealmsServer)var4.next();
                  if (this.func_223991_i(lvt_5_3_)) {
                     lvt_3_1_ = true;
                  }
               }

               this.field_224028_q = lvt_1_1_;
               if (this.func_223928_a()) {
                  this.field_224020_i.addEntry(new RealmsMainScreen.TrialServerEntry());
               }

               var4 = this.field_224028_q.iterator();

               while(var4.hasNext()) {
                  lvt_5_3_ = (RealmsServer)var4.next();
                  this.field_224020_i.addEntry(new RealmsMainScreen.ServerEntry(lvt_5_3_));
               }

               if (!field_224001_I && lvt_3_1_) {
                  field_224001_I = true;
                  this.func_223944_n();
               }
            }

            if (lvt_2_1_) {
               this.func_223901_c();
            }
         }

         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.field_224029_r = field_224017_f.func_225081_f();
            if (this.field_224029_r > 0 && this.field_224014_c.tryAcquire(1)) {
               Realms.narrateNow(getLocalizedString("mco.configure.world.invite.narration", new Object[]{this.field_224029_r}));
            }
         }

         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.field_223993_A) {
            boolean lvt_1_2_ = field_224017_f.func_225071_g();
            if (lvt_1_2_ != this.field_224037_z && this.func_223990_b()) {
               this.field_224037_z = lvt_1_2_;
               this.field_223994_B = false;
            } else {
               this.field_224037_z = lvt_1_2_;
            }
         }

         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.LIVE_STATS)) {
            RealmsServerPlayerLists lvt_1_3_ = field_224017_f.func_225079_h();
            Iterator var8 = lvt_1_3_.servers.iterator();

            label87:
            while(true) {
               while(true) {
                  if (!var8.hasNext()) {
                     break label87;
                  }

                  RealmsServerPlayerList lvt_3_2_ = (RealmsServerPlayerList)var8.next();
                  var4 = this.field_224028_q.iterator();

                  while(var4.hasNext()) {
                     lvt_5_3_ = (RealmsServer)var4.next();
                     if (lvt_5_3_.id == lvt_3_2_.serverId) {
                        lvt_5_3_.updateServerPing(lvt_3_2_);
                        break;
                     }
                  }
               }
            }
         }

         if (field_224017_f.func_225083_a(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.field_223995_C = field_224017_f.func_225059_i();
            this.field_223996_D = field_224017_f.func_225063_j();
         }

         field_224017_f.func_225072_c();
         if (this.func_223990_b()) {
            ++this.field_223998_F;
         }

         if (this.field_224006_N != null) {
            this.field_224006_N.setVisible(this.func_223977_m());
         }

      }
   }

   private void func_223921_a(String p_223921_1_) {
      Realms.setClipboard(p_223921_1_);
      RealmsUtil.func_225190_c(p_223921_1_);
   }

   private void func_223944_n() {
      (new Thread(() -> {
         List<RegionPingResult> lvt_1_1_ = Ping.func_224864_a();
         RealmsClient lvt_2_1_ = RealmsClient.func_224911_a();
         PingResult lvt_3_1_ = new PingResult();
         lvt_3_1_.pingResults = lvt_1_1_;
         lvt_3_1_.worldIds = this.func_223952_o();

         try {
            lvt_2_1_.func_224903_a(lvt_3_1_);
         } catch (Throwable var5) {
            field_224012_a.warn("Could not send ping result to Realms: ", var5);
         }

      })).start();
   }

   private List<Long> func_223952_o() {
      List<Long> lvt_1_1_ = Lists.newArrayList();
      Iterator var2 = this.field_224028_q.iterator();

      while(var2.hasNext()) {
         RealmsServer lvt_3_1_ = (RealmsServer)var2.next();
         if (this.func_223991_i(lvt_3_1_)) {
            lvt_1_1_.add(lvt_3_1_.id);
         }
      }

      return lvt_1_1_;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
      this.func_223939_y();
   }

   private void func_223914_p() {
      RealmsServer lvt_1_1_ = this.func_223967_a(this.field_224021_j);
      if (lvt_1_1_ != null) {
         this.func_223911_a(lvt_1_1_, this);
      }
   }

   private void func_223930_q() {
      RealmsServer lvt_1_1_ = this.func_223967_a(this.field_224021_j);
      if (lvt_1_1_ != null) {
         String lvt_2_1_ = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + lvt_1_1_.remoteSubscriptionId + "&profileId=" + Realms.getUUID() + "&ref=" + (lvt_1_1_.expiredTrial ? "expiredTrial" : "expiredRealm");
         this.func_223921_a(lvt_2_1_);
      }
   }

   private void func_223988_r() {
      if (this.field_224037_z && !this.field_223993_A) {
         RealmsUtil.func_225190_c("https://aka.ms/startjavarealmstrial");
         Realms.setScreen(this.field_224019_h);
      }
   }

   private void func_223895_s() {
      if (!field_224033_v) {
         field_224033_v = true;
         (new Thread("MCO Compatability Checker #1") {
            public void run() {
               RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();

               try {
                  RealmsClient.CompatibleVersionResponse lvt_2_1_ = lvt_1_1_.func_224939_i();
                  if (lvt_2_1_.equals(RealmsClient.CompatibleVersionResponse.OUTDATED)) {
                     RealmsMainScreen.field_224000_H = new RealmsClientOutdatedScreen(RealmsMainScreen.this.field_224019_h, true);
                     Realms.setScreen(RealmsMainScreen.field_224000_H);
                  } else if (lvt_2_1_.equals(RealmsClient.CompatibleVersionResponse.OTHER)) {
                     RealmsMainScreen.field_224000_H = new RealmsClientOutdatedScreen(RealmsMainScreen.this.field_224019_h, false);
                     Realms.setScreen(RealmsMainScreen.field_224000_H);
                  } else {
                     RealmsMainScreen.this.func_223975_u();
                  }
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.field_224033_v = false;
                  RealmsMainScreen.field_224012_a.error("Couldn't connect to realms: ", var3.toString());
                  if (var3.field_224981_a == 401) {
                     RealmsMainScreen.field_224000_H = new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.error.invalid.session.title"), RealmsScreen.getLocalizedString("mco.error.invalid.session.message"), RealmsMainScreen.this.field_224019_h);
                     Realms.setScreen(RealmsMainScreen.field_224000_H);
                  } else {
                     Realms.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.field_224019_h));
                  }
               } catch (IOException var4) {
                  RealmsMainScreen.field_224033_v = false;
                  RealmsMainScreen.field_224012_a.error("Couldn't connect to realms: ", var4.getMessage());
                  Realms.setScreen(new RealmsGenericErrorScreen(var4.getMessage(), RealmsMainScreen.this.field_224019_h));
               }
            }
         }).start();
      }

   }

   private void func_223965_t() {
   }

   private void func_223975_u() {
      (new Thread("MCO Compatability Checker #1") {
         public void run() {
            RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();

            try {
               Boolean lvt_2_1_ = lvt_1_1_.func_224918_g();
               if (lvt_2_1_) {
                  RealmsMainScreen.field_224012_a.info("Realms is available for this user");
                  RealmsMainScreen.field_224031_t = true;
               } else {
                  RealmsMainScreen.field_224012_a.info("Realms is not available for this user");
                  RealmsMainScreen.field_224031_t = false;
                  Realms.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.field_224019_h));
               }

               RealmsMainScreen.field_224032_u = true;
            } catch (RealmsServiceException var3) {
               RealmsMainScreen.field_224012_a.error("Couldn't connect to realms: ", var3.toString());
               Realms.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.field_224019_h));
            } catch (IOException var4) {
               RealmsMainScreen.field_224012_a.error("Couldn't connect to realms: ", var4.getMessage());
               Realms.setScreen(new RealmsGenericErrorScreen(var4.getMessage(), RealmsMainScreen.this.field_224019_h));
            }

         }
      }).start();
   }

   private void func_223884_v() {
      if (!RealmsClient.field_224944_a.equals(RealmsClient.Environment.STAGE)) {
         (new Thread("MCO Stage Availability Checker #1") {
            public void run() {
               RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();

               try {
                  Boolean lvt_2_1_ = lvt_1_1_.func_224931_h();
                  if (lvt_2_1_) {
                     RealmsClient.func_224940_b();
                     RealmsMainScreen.field_224012_a.info("Switched to stage");
                     RealmsMainScreen.field_224017_f.func_225087_d();
                  }
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.field_224012_a.error("Couldn't connect to Realms: " + var3);
               } catch (IOException var4) {
                  RealmsMainScreen.field_224012_a.error("Couldn't parse response connecting to Realms: " + var4.getMessage());
               }

            }
         }).start();
      }

   }

   private void func_223962_w() {
      if (!RealmsClient.field_224944_a.equals(RealmsClient.Environment.LOCAL)) {
         (new Thread("MCO Local Availability Checker #1") {
            public void run() {
               RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();

               try {
                  Boolean lvt_2_1_ = lvt_1_1_.func_224931_h();
                  if (lvt_2_1_) {
                     RealmsClient.func_224941_d();
                     RealmsMainScreen.field_224012_a.info("Switched to local");
                     RealmsMainScreen.field_224017_f.func_225087_d();
                  }
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.field_224012_a.error("Couldn't connect to Realms: " + var3);
               } catch (IOException var4) {
                  RealmsMainScreen.field_224012_a.error("Couldn't parse response connecting to Realms: " + var4.getMessage());
               }

            }
         }).start();
      }

   }

   private void func_223973_x() {
      RealmsClient.func_224921_c();
      field_224017_f.func_225087_d();
   }

   private void func_223939_y() {
      field_224017_f.func_225070_k();
   }

   private void func_223966_f(RealmsServer p_223966_1_) {
      if (Realms.getUUID().equals(p_223966_1_.ownerUUID) || field_224013_b) {
         this.func_223949_z();
         Minecraft lvt_2_1_ = Minecraft.getInstance();
         lvt_2_1_.execute(() -> {
            lvt_2_1_.displayGuiScreen((new RealmsConfigureWorldScreen(this, p_223966_1_.id)).getProxy());
         });
      }

   }

   private void func_223906_g(@Nullable RealmsServer p_223906_1_) {
      if (p_223906_1_ != null && !Realms.getUUID().equals(p_223906_1_.ownerUUID)) {
         this.func_223949_z();
         String lvt_2_1_ = getLocalizedString("mco.configure.world.leave.question.line1");
         String lvt_3_1_ = getLocalizedString("mco.configure.world.leave.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, lvt_2_1_, lvt_3_1_, true, 4));
      }

   }

   private void func_223949_z() {
      field_224018_g = this.field_224020_i.getScroll();
   }

   private RealmsServer func_223967_a(long p_223967_1_) {
      Iterator var3 = this.field_224028_q.iterator();

      RealmsServer lvt_4_1_;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         lvt_4_1_ = (RealmsServer)var3.next();
      } while(lvt_4_1_.id != p_223967_1_);

      return lvt_4_1_;
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 4) {
         if (p_confirmResult_1_) {
            (new Thread("Realms-leave-server") {
               public void run() {
                  try {
                     RealmsServer lvt_1_1_ = RealmsMainScreen.this.func_223967_a(RealmsMainScreen.this.field_224021_j);
                     if (lvt_1_1_ != null) {
                        RealmsClient lvt_2_1_ = RealmsClient.func_224911_a();
                        lvt_2_1_.func_224912_c(lvt_1_1_.id);
                        RealmsMainScreen.field_224017_f.func_225085_a(lvt_1_1_);
                        RealmsMainScreen.this.field_224028_q.remove(lvt_1_1_);
                        RealmsMainScreen.this.field_224020_i.children().removeIf((p_230230_1_) -> {
                           return p_230230_1_ instanceof RealmsMainScreen.ServerEntry && ((RealmsMainScreen.ServerEntry)p_230230_1_).field_223734_a.id == RealmsMainScreen.this.field_224021_j;
                        });
                        RealmsMainScreen.this.field_224020_i.setSelected(-1);
                        RealmsMainScreen.this.func_223915_a((RealmsServer)null);
                        RealmsMainScreen.this.field_224021_j = -1L;
                        RealmsMainScreen.this.field_224022_k.active(false);
                     }
                  } catch (RealmsServiceException var3) {
                     RealmsMainScreen.field_224012_a.error("Couldn't configure world");
                     Realms.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this));
                  }

               }
            }).start();
         }

         Realms.setScreen(this);
      }

   }

   public void func_223978_e() {
      this.field_224021_j = -1L;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      switch(p_keyPressed_1_) {
      case 256:
         this.field_224002_J.forEach(KeyCombo::func_224800_a);
         this.func_223955_A();
         return true;
      default:
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_223955_A() {
      if (this.func_223990_b() && this.field_224035_x) {
         this.field_224035_x = false;
      } else {
         Realms.setScreen(this.field_224019_h);
      }

   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      this.field_224002_J.forEach((p_227920_1_) -> {
         p_227920_1_.func_224799_a(p_charTyped_1_);
      });
      return true;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.field_224005_M = false;
      this.field_224027_p = null;
      this.renderBackground();
      this.field_224020_i.render(p_render_1_, p_render_2_, p_render_3_);
      this.func_223883_a(this.width() / 2 - 50, 7);
      if (RealmsClient.field_224944_a.equals(RealmsClient.Environment.STAGE)) {
         this.func_223888_E();
      }

      if (RealmsClient.field_224944_a.equals(RealmsClient.Environment.LOCAL)) {
         this.func_223964_D();
      }

      if (this.func_223990_b()) {
         this.func_223980_b(p_render_1_, p_render_2_);
      } else {
         if (this.field_223994_B) {
            this.func_223915_a((RealmsServer)null);
            if (!this.hasWidget(this.field_224020_i)) {
               this.addWidget(this.field_224020_i);
            }

            RealmsServer lvt_4_1_ = this.func_223967_a(this.field_224021_j);
            this.field_224022_k.active(this.func_223897_b(lvt_4_1_));
         }

         this.field_223994_B = false;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.field_224027_p != null) {
         this.func_223922_a(this.field_224027_p, p_render_1_, p_render_2_);
      }

      if (this.field_224037_z && !this.field_223993_A && this.func_223990_b()) {
         RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         int lvt_4_2_ = true;
         int lvt_5_1_ = true;
         int lvt_6_1_ = 0;
         if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
            lvt_6_1_ = 8;
         }

         RealmsScreen.blit(this.field_224009_Q.func_214457_x() + this.field_224009_Q.getWidth() - 8 - 4, this.field_224009_Q.func_223291_y_() + this.field_224009_Q.getHeight() / 2 - 4, 0.0F, (float)lvt_6_1_, 8, 8, 8, 16);
         RenderSystem.popMatrix();
      }

   }

   private void func_223883_a(int p_223883_1_, int p_223883_2_) {
      RealmsScreen.bind("realms:textures/gui/title/realms.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.scalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(p_223883_1_ * 2, p_223883_2_ * 2 - 5, 0.0F, 0.0F, 200, 50, 200, 50);
      RenderSystem.popMatrix();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.func_223979_a(p_mouseClicked_1_, p_mouseClicked_3_) && this.field_224035_x) {
         this.field_224035_x = false;
         this.field_224036_y = true;
         return true;
      } else {
         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   private boolean func_223979_a(double p_223979_1_, double p_223979_3_) {
      int lvt_5_1_ = this.func_223989_B();
      int lvt_6_1_ = this.func_223932_C();
      return p_223979_1_ < (double)(lvt_5_1_ - 5) || p_223979_1_ > (double)(lvt_5_1_ + 315) || p_223979_3_ < (double)(lvt_6_1_ - 5) || p_223979_3_ > (double)(lvt_6_1_ + 171);
   }

   private void func_223980_b(int p_223980_1_, int p_223980_2_) {
      int lvt_3_1_ = this.func_223989_B();
      int lvt_4_1_ = this.func_223932_C();
      String lvt_5_1_ = getLocalizedString("mco.selectServer.popup");
      List<String> lvt_6_1_ = this.fontSplit(lvt_5_1_, 100);
      if (!this.field_223994_B) {
         this.field_223997_E = 0;
         this.field_223998_F = 0;
         this.field_223999_G = true;
         this.func_223915_a((RealmsServer)null);
         if (this.hasWidget(this.field_224020_i)) {
            this.removeWidget(this.field_224020_i);
         }

         Realms.narrateNow(lvt_5_1_);
      }

      if (this.field_224034_w) {
         this.field_223994_B = true;
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.7F);
      RenderSystem.enableBlend();
      RealmsScreen.bind("realms:textures/gui/realms/darken.png");
      RenderSystem.pushMatrix();
      int lvt_7_1_ = false;
      int lvt_8_1_ = true;
      RealmsScreen.blit(0, 32, 0.0F, 0.0F, this.width(), this.height() - 40 - 32, 310, 166);
      RenderSystem.popMatrix();
      RenderSystem.disableBlend();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RealmsScreen.bind("realms:textures/gui/realms/popup.png");
      RenderSystem.pushMatrix();
      RealmsScreen.blit(lvt_3_1_, lvt_4_1_, 0.0F, 0.0F, 310, 166, 310, 166);
      RenderSystem.popMatrix();
      if (!field_227918_e_.isEmpty()) {
         RealmsScreen.bind(((ResourceLocation)field_227918_e_.get(this.field_223997_E)).toString());
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(lvt_3_1_ + 7, lvt_4_1_ + 7, 0.0F, 0.0F, 195, 152, 195, 152);
         RenderSystem.popMatrix();
         if (this.field_223998_F % 95 < 5) {
            if (!this.field_223999_G) {
               this.field_223997_E = (this.field_223997_E + 1) % field_227918_e_.size();
               this.field_223999_G = true;
            }
         } else {
            this.field_223999_G = false;
         }
      }

      int lvt_9_1_ = 0;
      Iterator var10 = lvt_6_1_.iterator();

      while(var10.hasNext()) {
         String lvt_11_1_ = (String)var10.next();
         int var10002 = this.width() / 2 + 52;
         ++lvt_9_1_;
         this.drawString(lvt_11_1_, var10002, lvt_4_1_ + 10 * lvt_9_1_ - 3, 8421504, false);
      }

   }

   private int func_223989_B() {
      return (this.width() - 310) / 2;
   }

   private int func_223932_C() {
      return this.height() / 2 - 80;
   }

   private void func_223960_a(int p_223960_1_, int p_223960_2_, int p_223960_3_, int p_223960_4_, boolean p_223960_5_, boolean p_223960_6_) {
      int lvt_7_1_ = this.field_224029_r;
      boolean lvt_8_1_ = this.func_223931_b((double)p_223960_1_, (double)p_223960_2_);
      boolean lvt_9_1_ = p_223960_6_ && p_223960_5_;
      if (lvt_9_1_) {
         float lvt_10_1_ = 0.25F + (1.0F + RealmsMth.sin((float)this.field_224030_s * 0.5F)) * 0.25F;
         int lvt_11_1_ = -16777216 | (int)(lvt_10_1_ * 64.0F) << 16 | (int)(lvt_10_1_ * 64.0F) << 8 | (int)(lvt_10_1_ * 64.0F) << 0;
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ - 2, p_223960_3_ + 18, p_223960_4_ + 18, lvt_11_1_, lvt_11_1_);
         lvt_11_1_ = -16777216 | (int)(lvt_10_1_ * 255.0F) << 16 | (int)(lvt_10_1_ * 255.0F) << 8 | (int)(lvt_10_1_ * 255.0F) << 0;
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ - 2, p_223960_3_ + 18, p_223960_4_ - 1, lvt_11_1_, lvt_11_1_);
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ - 2, p_223960_3_ - 1, p_223960_4_ + 18, lvt_11_1_, lvt_11_1_);
         this.fillGradient(p_223960_3_ + 17, p_223960_4_ - 2, p_223960_3_ + 18, p_223960_4_ + 18, lvt_11_1_, lvt_11_1_);
         this.fillGradient(p_223960_3_ - 2, p_223960_4_ + 17, p_223960_3_ + 18, p_223960_4_ + 18, lvt_11_1_, lvt_11_1_);
      }

      RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      boolean lvt_10_2_ = p_223960_6_ && p_223960_5_;
      RealmsScreen.blit(p_223960_3_, p_223960_4_ - 6, lvt_10_2_ ? 16.0F : 0.0F, 0.0F, 15, 25, 31, 25);
      RenderSystem.popMatrix();
      boolean lvt_11_2_ = p_223960_6_ && lvt_7_1_ != 0;
      int lvt_12_2_;
      if (lvt_11_2_) {
         lvt_12_2_ = (Math.min(lvt_7_1_, 6) - 1) * 8;
         int lvt_13_1_ = (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.field_224030_s) * 0.57F), RealmsMth.cos((float)this.field_224030_s * 0.35F))) * -6.0F);
         RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(p_223960_3_ + 4, p_223960_4_ + 4 + lvt_13_1_, (float)lvt_12_2_, lvt_8_1_ ? 8.0F : 0.0F, 8, 8, 48, 16);
         RenderSystem.popMatrix();
      }

      lvt_12_2_ = p_223960_1_ + 12;
      boolean lvt_14_1_ = p_223960_6_ && lvt_8_1_;
      if (lvt_14_1_) {
         String lvt_15_1_ = getLocalizedString(lvt_7_1_ == 0 ? "mco.invites.nopending" : "mco.invites.pending");
         int lvt_16_1_ = this.fontWidth(lvt_15_1_);
         this.fillGradient(lvt_12_2_ - 3, p_223960_2_ - 3, lvt_12_2_ + lvt_16_1_ + 3, p_223960_2_ + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(lvt_15_1_, lvt_12_2_, p_223960_2_, -1);
      }

   }

   private boolean func_223931_b(double p_223931_1_, double p_223931_3_) {
      int lvt_5_1_ = this.width() / 2 + 50;
      int lvt_6_1_ = this.width() / 2 + 66;
      int lvt_7_1_ = 11;
      int lvt_8_1_ = 23;
      if (this.field_224029_r != 0) {
         lvt_5_1_ -= 3;
         lvt_6_1_ += 3;
         lvt_7_1_ -= 5;
         lvt_8_1_ += 5;
      }

      return (double)lvt_5_1_ <= p_223931_1_ && p_223931_1_ <= (double)lvt_6_1_ && (double)lvt_7_1_ <= p_223931_3_ && p_223931_3_ <= (double)lvt_8_1_;
   }

   public void func_223911_a(RealmsServer p_223911_1_, RealmsScreen p_223911_2_) {
      if (p_223911_1_ != null) {
         try {
            if (!this.field_224004_L.tryLock(1L, TimeUnit.SECONDS)) {
               return;
            }

            if (this.field_224004_L.getHoldCount() > 1) {
               return;
            }
         } catch (InterruptedException var4) {
            return;
         }

         this.field_224015_d = true;
         this.func_223950_b(p_223911_1_, p_223911_2_);
      }

   }

   private void func_223950_b(RealmsServer p_223950_1_, RealmsScreen p_223950_2_) {
      RealmsLongRunningMcoTaskScreen lvt_3_1_ = new RealmsLongRunningMcoTaskScreen(p_223950_2_, new RealmsTasks.RealmsGetServerDetailsTask(this, p_223950_2_, p_223950_1_, this.field_224004_L));
      lvt_3_1_.func_224233_a();
      Realms.setScreen(lvt_3_1_);
   }

   private boolean func_223885_h(RealmsServer p_223885_1_) {
      return p_223885_1_.ownerUUID != null && p_223885_1_.ownerUUID.equals(Realms.getUUID());
   }

   private boolean func_223991_i(RealmsServer p_223991_1_) {
      return p_223991_1_.ownerUUID != null && p_223991_1_.ownerUUID.equals(Realms.getUUID()) && !p_223991_1_.expired;
   }

   private void func_223907_a(int p_223907_1_, int p_223907_2_, int p_223907_3_, int p_223907_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223907_1_, p_223907_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_223907_3_ >= p_223907_1_ && p_223907_3_ <= p_223907_1_ + 9 && p_223907_4_ >= p_223907_2_ && p_223907_4_ <= p_223907_2_ + 27 && p_223907_4_ < this.height() - 40 && p_223907_4_ > 32 && !this.func_223990_b()) {
         this.field_224027_p = getLocalizedString("mco.selectServer.expired");
      }

   }

   private void func_223909_a(int p_223909_1_, int p_223909_2_, int p_223909_3_, int p_223909_4_, int p_223909_5_) {
      RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      if (this.field_224030_s % 20 < 10) {
         RealmsScreen.blit(p_223909_1_, p_223909_2_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         RealmsScreen.blit(p_223909_1_, p_223909_2_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      RenderSystem.popMatrix();
      if (p_223909_3_ >= p_223909_1_ && p_223909_3_ <= p_223909_1_ + 9 && p_223909_4_ >= p_223909_2_ && p_223909_4_ <= p_223909_2_ + 27 && p_223909_4_ < this.height() - 40 && p_223909_4_ > 32 && !this.func_223990_b()) {
         if (p_223909_5_ <= 0) {
            this.field_224027_p = getLocalizedString("mco.selectServer.expires.soon");
         } else if (p_223909_5_ == 1) {
            this.field_224027_p = getLocalizedString("mco.selectServer.expires.day");
         } else {
            this.field_224027_p = getLocalizedString("mco.selectServer.expires.days", new Object[]{p_223909_5_});
         }
      }

   }

   private void func_223987_b(int p_223987_1_, int p_223987_2_, int p_223987_3_, int p_223987_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223987_1_, p_223987_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_223987_3_ >= p_223987_1_ && p_223987_3_ <= p_223987_1_ + 9 && p_223987_4_ >= p_223987_2_ && p_223987_4_ <= p_223987_2_ + 27 && p_223987_4_ < this.height() - 40 && p_223987_4_ > 32 && !this.func_223990_b()) {
         this.field_224027_p = getLocalizedString("mco.selectServer.open");
      }

   }

   private void func_223912_c(int p_223912_1_, int p_223912_2_, int p_223912_3_, int p_223912_4_) {
      RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223912_1_, p_223912_2_, 0.0F, 0.0F, 10, 28, 10, 28);
      RenderSystem.popMatrix();
      if (p_223912_3_ >= p_223912_1_ && p_223912_3_ <= p_223912_1_ + 9 && p_223912_4_ >= p_223912_2_ && p_223912_4_ <= p_223912_2_ + 27 && p_223912_4_ < this.height() - 40 && p_223912_4_ > 32 && !this.func_223990_b()) {
         this.field_224027_p = getLocalizedString("mco.selectServer.closed");
      }

   }

   private void func_223945_d(int p_223945_1_, int p_223945_2_, int p_223945_3_, int p_223945_4_) {
      boolean lvt_5_1_ = false;
      if (p_223945_3_ >= p_223945_1_ && p_223945_3_ <= p_223945_1_ + 28 && p_223945_4_ >= p_223945_2_ && p_223945_4_ <= p_223945_2_ + 28 && p_223945_4_ < this.height() - 40 && p_223945_4_ > 32 && !this.func_223990_b()) {
         lvt_5_1_ = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/leave_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223945_1_, p_223945_2_, lvt_5_1_ ? 28.0F : 0.0F, 0.0F, 28, 28, 56, 28);
      RenderSystem.popMatrix();
      if (lvt_5_1_) {
         this.field_224027_p = getLocalizedString("mco.selectServer.leave");
      }

   }

   private void func_223916_e(int p_223916_1_, int p_223916_2_, int p_223916_3_, int p_223916_4_) {
      boolean lvt_5_1_ = false;
      if (p_223916_3_ >= p_223916_1_ && p_223916_3_ <= p_223916_1_ + 28 && p_223916_4_ >= p_223916_2_ && p_223916_4_ <= p_223916_2_ + 28 && p_223916_4_ < this.height() - 40 && p_223916_4_ > 32 && !this.func_223990_b()) {
         lvt_5_1_ = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/configure_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223916_1_, p_223916_2_, lvt_5_1_ ? 28.0F : 0.0F, 0.0F, 28, 28, 56, 28);
      RenderSystem.popMatrix();
      if (lvt_5_1_) {
         this.field_224027_p = getLocalizedString("mco.selectServer.configure");
      }

   }

   protected void func_223922_a(String p_223922_1_, int p_223922_2_, int p_223922_3_) {
      if (p_223922_1_ != null) {
         int lvt_4_1_ = 0;
         int lvt_5_1_ = 0;
         String[] var6 = p_223922_1_.split("\n");
         int lvt_7_1_ = var6.length;

         int lvt_10_1_;
         for(int var8 = 0; var8 < lvt_7_1_; ++var8) {
            String lvt_9_1_ = var6[var8];
            lvt_10_1_ = this.fontWidth(lvt_9_1_);
            if (lvt_10_1_ > lvt_5_1_) {
               lvt_5_1_ = lvt_10_1_;
            }
         }

         int lvt_6_1_ = p_223922_2_ - lvt_5_1_ - 5;
         lvt_7_1_ = p_223922_3_;
         if (lvt_6_1_ < 0) {
            lvt_6_1_ = p_223922_2_ + 12;
         }

         String[] var13 = p_223922_1_.split("\n");
         int var14 = var13.length;

         for(lvt_10_1_ = 0; lvt_10_1_ < var14; ++lvt_10_1_) {
            String lvt_11_1_ = var13[lvt_10_1_];
            this.fillGradient(lvt_6_1_ - 3, lvt_7_1_ - (lvt_4_1_ == 0 ? 3 : 0) + lvt_4_1_, lvt_6_1_ + lvt_5_1_ + 3, lvt_7_1_ + 8 + 3 + lvt_4_1_, -1073741824, -1073741824);
            this.fontDrawShadow(lvt_11_1_, lvt_6_1_, lvt_7_1_ + lvt_4_1_, 16777215);
            lvt_4_1_ += 10;
         }

      }
   }

   private void func_223933_a(int p_223933_1_, int p_223933_2_, int p_223933_3_, int p_223933_4_, boolean p_223933_5_) {
      boolean lvt_6_1_ = false;
      if (p_223933_1_ >= p_223933_3_ && p_223933_1_ <= p_223933_3_ + 20 && p_223933_2_ >= p_223933_4_ && p_223933_2_ <= p_223933_4_ + 20) {
         lvt_6_1_ = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/questionmark.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(p_223933_3_, p_223933_4_, p_223933_5_ ? 20.0F : 0.0F, 0.0F, 20, 20, 40, 20);
      RenderSystem.popMatrix();
      if (lvt_6_1_) {
         this.field_224027_p = getLocalizedString("mco.selectServer.info");
      }

   }

   private void func_223982_a(int p_223982_1_, int p_223982_2_, boolean p_223982_3_, int p_223982_4_, int p_223982_5_, boolean p_223982_6_, boolean p_223982_7_) {
      boolean lvt_8_1_ = false;
      if (p_223982_1_ >= p_223982_4_ && p_223982_1_ <= p_223982_4_ + 20 && p_223982_2_ >= p_223982_5_ && p_223982_2_ <= p_223982_5_ + 20) {
         lvt_8_1_ = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/news_icon.png");
      if (p_223982_7_) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         RenderSystem.color4f(0.5F, 0.5F, 0.5F, 1.0F);
      }

      RenderSystem.pushMatrix();
      boolean lvt_9_1_ = p_223982_7_ && p_223982_6_;
      RealmsScreen.blit(p_223982_4_, p_223982_5_, lvt_9_1_ ? 20.0F : 0.0F, 0.0F, 20, 20, 40, 20);
      RenderSystem.popMatrix();
      if (lvt_8_1_ && p_223982_7_) {
         this.field_224027_p = getLocalizedString("mco.news");
      }

      if (p_223982_3_ && p_223982_7_) {
         int lvt_10_1_ = lvt_8_1_ ? 0 : (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.field_224030_s) * 0.57F), RealmsMth.cos((float)this.field_224030_s * 0.35F))) * -6.0F);
         RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(p_223982_4_ + 10, p_223982_5_ + 2 + lvt_10_1_, 40.0F, 0.0F, 8, 8, 48, 16);
         RenderSystem.popMatrix();
      }

   }

   private void func_223964_D() {
      String lvt_1_1_ = "LOCAL!";
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
      RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(1.5F, 1.5F, 1.5F);
      this.drawString("LOCAL!", 0, 0, 8388479);
      RenderSystem.popMatrix();
   }

   private void func_223888_E() {
      String lvt_1_1_ = "STAGE!";
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
      RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(1.5F, 1.5F, 1.5F);
      this.drawString("STAGE!", 0, 0, -256);
      RenderSystem.popMatrix();
   }

   public RealmsMainScreen func_223942_f() {
      return new RealmsMainScreen(this.field_224019_h);
   }

   public static void func_227932_a_(IResourceManager p_227932_0_) {
      Collection<ResourceLocation> lvt_1_1_ = p_227932_0_.getAllResourceLocations("textures/gui/images", (p_227934_0_) -> {
         return p_227934_0_.endsWith(".png");
      });
      field_227918_e_ = (List)lvt_1_1_.stream().filter((p_227931_0_) -> {
         return p_227931_0_.getNamespace().equals("realms");
      }).collect(ImmutableList.toImmutableList());
   }

   @OnlyIn(Dist.CLIENT)
   class CloseButton extends RealmsButton {
      public CloseButton() {
         super(11, RealmsMainScreen.this.func_223989_B() + 4, RealmsMainScreen.this.func_223932_C() + 4, 12, 12, RealmsScreen.getLocalizedString("mco.selectServer.close"));
      }

      public void tick() {
         super.tick();
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsScreen.bind("realms:textures/gui/realms/cross_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(this.func_214457_x(), this.func_223291_y_(), 0.0F, this.getProxy().isHovered() ? 12.0F : 0.0F, 12, 12, 12, 24);
         RenderSystem.popMatrix();
         if (this.getProxy().isMouseOver((double)p_renderButton_1_, (double)p_renderButton_2_)) {
            RealmsMainScreen.this.field_224027_p = this.getProxy().getMessage();
         }

      }

      public void onPress() {
         RealmsMainScreen.this.func_223955_A();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class InfoButton extends RealmsButton {
      public InfoButton() {
         super(10, RealmsMainScreen.this.width() - 37, 6, 20, 20, RealmsScreen.getLocalizedString("mco.selectServer.info"));
      }

      public void tick() {
         super.tick();
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsMainScreen.this.func_223933_a(p_renderButton_1_, p_renderButton_2_, this.func_214457_x(), this.func_223291_y_(), this.getProxy().isHovered());
      }

      public void onPress() {
         RealmsMainScreen.this.field_224035_x = !RealmsMainScreen.this.field_224035_x;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class NewsButton extends RealmsButton {
      public NewsButton() {
         super(9, RealmsMainScreen.this.width() - 62, 6, 20, 20, "");
      }

      public void tick() {
         this.setMessage(Realms.getLocalizedString("mco.news"));
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void onPress() {
         if (RealmsMainScreen.this.field_223996_D != null) {
            RealmsUtil.func_225190_c(RealmsMainScreen.this.field_223996_D);
            if (RealmsMainScreen.this.field_223995_C) {
               RealmsPersistence.RealmsPersistenceData lvt_1_1_ = RealmsPersistence.func_225188_a();
               lvt_1_1_.field_225186_b = false;
               RealmsMainScreen.this.field_223995_C = false;
               RealmsPersistence.func_225187_a(lvt_1_1_);
            }

         }
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsMainScreen.this.func_223982_a(p_renderButton_1_, p_renderButton_2_, RealmsMainScreen.this.field_223995_C, this.func_214457_x(), this.func_223291_y_(), this.getProxy().isHovered(), this.active());
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PendingInvitesButton extends RealmsButton {
      public PendingInvitesButton() {
         super(8, RealmsMainScreen.this.width() / 2 + 47, 6, 22, 22, "");
      }

      public void tick() {
         this.setMessage(Realms.getLocalizedString(RealmsMainScreen.this.field_224029_r == 0 ? "mco.invites.nopending" : "mco.invites.pending"));
      }

      public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

      public void onPress() {
         RealmsPendingInvitesScreen lvt_1_1_ = new RealmsPendingInvitesScreen(RealmsMainScreen.this.field_224019_h);
         Realms.setScreen(lvt_1_1_);
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         RealmsMainScreen.this.func_223960_a(p_renderButton_1_, p_renderButton_2_, this.func_214457_x(), this.func_223291_y_(), this.getProxy().isHovered(), this.active());
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerEntry extends RealmListEntry {
      final RealmsServer field_223734_a;

      public ServerEntry(RealmsServer p_i51666_2_) {
         this.field_223734_a = p_i51666_2_;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223731_a(this.field_223734_a, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (this.field_223734_a.state == RealmsServer.Status.UNINITIALIZED) {
            RealmsMainScreen.this.field_224021_j = -1L;
            Realms.setScreen(new RealmsCreateRealmScreen(this.field_223734_a, RealmsMainScreen.this));
         } else {
            RealmsMainScreen.this.field_224021_j = this.field_223734_a.id;
         }

         return true;
      }

      private void func_223731_a(RealmsServer p_223731_1_, int p_223731_2_, int p_223731_3_, int p_223731_4_, int p_223731_5_) {
         this.func_223733_b(p_223731_1_, p_223731_2_ + 36, p_223731_3_, p_223731_4_, p_223731_5_);
      }

      private void func_223733_b(RealmsServer p_223733_1_, int p_223733_2_, int p_223733_3_, int p_223733_4_, int p_223733_5_) {
         if (p_223733_1_.state == RealmsServer.Status.UNINITIALIZED) {
            RealmsScreen.bind("realms:textures/gui/realms/world_icon.png");
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableAlphaTest();
            RenderSystem.pushMatrix();
            RealmsScreen.blit(p_223733_2_ + 10, p_223733_3_ + 6, 0.0F, 0.0F, 40, 20, 40, 20);
            RenderSystem.popMatrix();
            float lvt_6_1_ = 0.5F + (1.0F + RealmsMth.sin((float)RealmsMainScreen.this.field_224030_s * 0.25F)) * 0.25F;
            int lvt_7_1_ = -16777216 | (int)(127.0F * lvt_6_1_) << 16 | (int)(255.0F * lvt_6_1_) << 8 | (int)(127.0F * lvt_6_1_);
            RealmsMainScreen.this.drawCenteredString(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized"), p_223733_2_ + 10 + 40 + 75, p_223733_3_ + 12, lvt_7_1_);
         } else {
            int lvt_6_2_ = true;
            int lvt_7_2_ = true;
            if (p_223733_1_.expired) {
               RealmsMainScreen.this.func_223907_a(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            } else if (p_223733_1_.state == RealmsServer.Status.CLOSED) {
               RealmsMainScreen.this.func_223912_c(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            } else if (RealmsMainScreen.this.func_223885_h(p_223733_1_) && p_223733_1_.daysLeft < 7) {
               RealmsMainScreen.this.func_223909_a(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_, p_223733_1_.daysLeft);
            } else if (p_223733_1_.state == RealmsServer.Status.OPEN) {
               RealmsMainScreen.this.func_223987_b(p_223733_2_ + 225 - 14, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            }

            if (!RealmsMainScreen.this.func_223885_h(p_223733_1_) && !RealmsMainScreen.field_224013_b) {
               RealmsMainScreen.this.func_223945_d(p_223733_2_ + 225, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            } else {
               RealmsMainScreen.this.func_223916_e(p_223733_2_ + 225, p_223733_3_ + 2, p_223733_4_, p_223733_5_);
            }

            if (!"0".equals(p_223733_1_.serverPing.nrOfPlayers)) {
               String lvt_8_1_ = ChatFormatting.GRAY + "" + p_223733_1_.serverPing.nrOfPlayers;
               RealmsMainScreen.this.drawString(lvt_8_1_, p_223733_2_ + 207 - RealmsMainScreen.this.fontWidth(lvt_8_1_), p_223733_3_ + 3, 8421504);
               if (p_223733_4_ >= p_223733_2_ + 207 - RealmsMainScreen.this.fontWidth(lvt_8_1_) && p_223733_4_ <= p_223733_2_ + 207 && p_223733_5_ >= p_223733_3_ + 1 && p_223733_5_ <= p_223733_3_ + 10 && p_223733_5_ < RealmsMainScreen.this.height() - 40 && p_223733_5_ > 32 && !RealmsMainScreen.this.func_223990_b()) {
                  RealmsMainScreen.this.field_224027_p = p_223733_1_.serverPing.playerList;
               }
            }

            String lvt_9_1_;
            if (RealmsMainScreen.this.func_223885_h(p_223733_1_) && p_223733_1_.expired) {
               boolean lvt_8_2_ = false;
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.enableBlend();
               RealmsScreen.bind("minecraft:textures/gui/widgets.png");
               RenderSystem.pushMatrix();
               RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
               lvt_9_1_ = RealmsScreen.getLocalizedString("mco.selectServer.expiredList");
               String lvt_10_1_ = RealmsScreen.getLocalizedString("mco.selectServer.expiredRenew");
               if (p_223733_1_.expiredTrial) {
                  lvt_9_1_ = RealmsScreen.getLocalizedString("mco.selectServer.expiredTrial");
                  lvt_10_1_ = RealmsScreen.getLocalizedString("mco.selectServer.expiredSubscribe");
               }

               int lvt_11_1_ = RealmsMainScreen.this.fontWidth(lvt_10_1_) + 17;
               int lvt_12_1_ = true;
               int lvt_13_1_ = p_223733_2_ + RealmsMainScreen.this.fontWidth(lvt_9_1_) + 8;
               int lvt_14_1_ = p_223733_3_ + 13;
               if (p_223733_4_ >= lvt_13_1_ && p_223733_4_ < lvt_13_1_ + lvt_11_1_ && p_223733_5_ > lvt_14_1_ && p_223733_5_ <= lvt_14_1_ + 16 & p_223733_5_ < RealmsMainScreen.this.height() - 40 && p_223733_5_ > 32 && !RealmsMainScreen.this.func_223990_b()) {
                  lvt_8_2_ = true;
                  RealmsMainScreen.this.field_224005_M = true;
               }

               int lvt_15_1_ = lvt_8_2_ ? 2 : 1;
               RealmsScreen.blit(lvt_13_1_, lvt_14_1_, 0.0F, (float)(46 + lvt_15_1_ * 20), lvt_11_1_ / 2, 8, 256, 256);
               RealmsScreen.blit(lvt_13_1_ + lvt_11_1_ / 2, lvt_14_1_, (float)(200 - lvt_11_1_ / 2), (float)(46 + lvt_15_1_ * 20), lvt_11_1_ / 2, 8, 256, 256);
               RealmsScreen.blit(lvt_13_1_, lvt_14_1_ + 8, 0.0F, (float)(46 + lvt_15_1_ * 20 + 12), lvt_11_1_ / 2, 8, 256, 256);
               RealmsScreen.blit(lvt_13_1_ + lvt_11_1_ / 2, lvt_14_1_ + 8, (float)(200 - lvt_11_1_ / 2), (float)(46 + lvt_15_1_ * 20 + 12), lvt_11_1_ / 2, 8, 256, 256);
               RenderSystem.popMatrix();
               RenderSystem.disableBlend();
               int lvt_16_1_ = p_223733_3_ + 11 + 5;
               int lvt_17_1_ = lvt_8_2_ ? 16777120 : 16777215;
               RealmsMainScreen.this.drawString(lvt_9_1_, p_223733_2_ + 2, lvt_16_1_ + 1, 15553363);
               RealmsMainScreen.this.drawCenteredString(lvt_10_1_, lvt_13_1_ + lvt_11_1_ / 2, lvt_16_1_ + 1, lvt_17_1_);
            } else {
               if (p_223733_1_.worldType.equals(RealmsServer.ServerType.MINIGAME)) {
                  int lvt_8_3_ = 13413468;
                  lvt_9_1_ = RealmsScreen.getLocalizedString("mco.selectServer.minigame") + " ";
                  int lvt_10_2_ = RealmsMainScreen.this.fontWidth(lvt_9_1_);
                  RealmsMainScreen.this.drawString(lvt_9_1_, p_223733_2_ + 2, p_223733_3_ + 12, 13413468);
                  RealmsMainScreen.this.drawString(p_223733_1_.getMinigameName(), p_223733_2_ + 2 + lvt_10_2_, p_223733_3_ + 12, 8421504);
               } else {
                  RealmsMainScreen.this.drawString(p_223733_1_.getDescription(), p_223733_2_ + 2, p_223733_3_ + 12, 8421504);
               }

               if (!RealmsMainScreen.this.func_223885_h(p_223733_1_)) {
                  RealmsMainScreen.this.drawString(p_223733_1_.owner, p_223733_2_ + 2, p_223733_3_ + 12 + 11, 8421504);
               }
            }

            RealmsMainScreen.this.drawString(p_223733_1_.getName(), p_223733_2_ + 2, p_223733_3_ + 1, 16777215);
            RealmsTextureManager.func_225205_a(p_223733_1_.ownerUUID, () -> {
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               RealmsScreen.blit(p_223733_2_ - 36, p_223733_3_, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
               RealmsScreen.blit(p_223733_2_ - 36, p_223733_3_, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
            });
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class TrialServerEntry extends RealmListEntry {
      public TrialServerEntry() {
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223736_a(p_render_1_, p_render_3_, p_render_2_, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         RealmsMainScreen.this.field_224035_x = true;
         return true;
      }

      private void func_223736_a(int p_223736_1_, int p_223736_2_, int p_223736_3_, int p_223736_4_, int p_223736_5_) {
         int lvt_6_1_ = p_223736_3_ + 8;
         int lvt_7_1_ = 0;
         String lvt_8_1_ = RealmsScreen.getLocalizedString("mco.trial.message.line1") + "\\n" + RealmsScreen.getLocalizedString("mco.trial.message.line2");
         boolean lvt_9_1_ = false;
         if (p_223736_2_ <= p_223736_4_ && p_223736_4_ <= RealmsMainScreen.this.field_224020_i.getScroll() && p_223736_3_ <= p_223736_5_ && p_223736_5_ <= p_223736_3_ + 32) {
            lvt_9_1_ = true;
         }

         int lvt_10_1_ = 8388479;
         if (lvt_9_1_ && !RealmsMainScreen.this.func_223990_b()) {
            lvt_10_1_ = 6077788;
         }

         String[] var11 = lvt_8_1_.split("\\\\n");
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            String lvt_14_1_ = var11[var13];
            RealmsMainScreen.this.drawCenteredString(lvt_14_1_, RealmsMainScreen.this.width() / 2, lvt_6_1_ + lvt_7_1_, lvt_10_1_);
            lvt_7_1_ += 10;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerList extends RealmsObjectSelectionList<RealmListEntry> {
      public ServerList() {
         super(RealmsMainScreen.this.width(), RealmsMainScreen.this.height(), 32, RealmsMainScreen.this.height() - 40, 36);
      }

      public boolean isFocused() {
         return RealmsMainScreen.this.isFocused(this);
      }

      public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
         if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 32 && p_keyPressed_1_ != 335) {
            return false;
         } else {
            RealmListEntry lvt_4_1_ = this.getSelected();
            return lvt_4_1_ == null ? super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : lvt_4_1_.mouseClicked(0.0D, 0.0D, 0);
         }
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         if (p_mouseClicked_5_ == 0 && p_mouseClicked_1_ < (double)this.getScrollbarPosition() && p_mouseClicked_3_ >= (double)this.y0() && p_mouseClicked_3_ <= (double)this.y1()) {
            int lvt_6_1_ = RealmsMainScreen.this.field_224020_i.getRowLeft();
            int lvt_7_1_ = this.getScrollbarPosition();
            int lvt_8_1_ = (int)Math.floor(p_mouseClicked_3_ - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
            int lvt_9_1_ = lvt_8_1_ / this.itemHeight();
            if (p_mouseClicked_1_ >= (double)lvt_6_1_ && p_mouseClicked_1_ <= (double)lvt_7_1_ && lvt_9_1_ >= 0 && lvt_8_1_ >= 0 && lvt_9_1_ < this.getItemCount()) {
               this.itemClicked(lvt_8_1_, lvt_9_1_, p_mouseClicked_1_, p_mouseClicked_3_, this.width());
               RealmsMainScreen.this.field_224003_K = RealmsMainScreen.this.field_224003_K + 7;
               this.selectItem(lvt_9_1_);
            }

            return true;
         } else {
            return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
         }
      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            RealmsServer lvt_2_3_;
            if (RealmsMainScreen.this.func_223928_a()) {
               if (p_selectItem_1_ == 0) {
                  Realms.narrateNow(RealmsScreen.getLocalizedString("mco.trial.message.line1"), RealmsScreen.getLocalizedString("mco.trial.message.line2"));
                  lvt_2_3_ = null;
               } else {
                  if (p_selectItem_1_ - 1 >= RealmsMainScreen.this.field_224028_q.size()) {
                     RealmsMainScreen.this.field_224021_j = -1L;
                     return;
                  }

                  lvt_2_3_ = (RealmsServer)RealmsMainScreen.this.field_224028_q.get(p_selectItem_1_ - 1);
               }
            } else {
               if (p_selectItem_1_ >= RealmsMainScreen.this.field_224028_q.size()) {
                  RealmsMainScreen.this.field_224021_j = -1L;
                  return;
               }

               lvt_2_3_ = (RealmsServer)RealmsMainScreen.this.field_224028_q.get(p_selectItem_1_);
            }

            RealmsMainScreen.this.func_223915_a(lvt_2_3_);
            if (lvt_2_3_ == null) {
               RealmsMainScreen.this.field_224021_j = -1L;
            } else if (lvt_2_3_.state == RealmsServer.Status.UNINITIALIZED) {
               Realms.narrateNow(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized") + RealmsScreen.getLocalizedString("mco.gui.button"));
               RealmsMainScreen.this.field_224021_j = -1L;
            } else {
               RealmsMainScreen.this.field_224021_j = lvt_2_3_.id;
               if (RealmsMainScreen.this.field_224003_K >= 10 && RealmsMainScreen.this.field_224022_k.active()) {
                  RealmsMainScreen.this.func_223911_a(RealmsMainScreen.this.func_223967_a(RealmsMainScreen.this.field_224021_j), RealmsMainScreen.this);
               }

               Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", lvt_2_3_.name));
            }
         }
      }

      public void itemClicked(int p_itemClicked_1_, int p_itemClicked_2_, double p_itemClicked_3_, double p_itemClicked_5_, int p_itemClicked_7_) {
         if (RealmsMainScreen.this.func_223928_a()) {
            if (p_itemClicked_2_ == 0) {
               RealmsMainScreen.this.field_224035_x = true;
               return;
            }

            --p_itemClicked_2_;
         }

         if (p_itemClicked_2_ < RealmsMainScreen.this.field_224028_q.size()) {
            RealmsServer lvt_8_1_ = (RealmsServer)RealmsMainScreen.this.field_224028_q.get(p_itemClicked_2_);
            if (lvt_8_1_ != null) {
               if (lvt_8_1_.state == RealmsServer.Status.UNINITIALIZED) {
                  RealmsMainScreen.this.field_224021_j = -1L;
                  Realms.setScreen(new RealmsCreateRealmScreen(lvt_8_1_, RealmsMainScreen.this));
               } else {
                  RealmsMainScreen.this.field_224021_j = lvt_8_1_.id;
               }

               if (RealmsMainScreen.this.field_224027_p != null && RealmsMainScreen.this.field_224027_p.equals(RealmsScreen.getLocalizedString("mco.selectServer.configure"))) {
                  RealmsMainScreen.this.field_224021_j = lvt_8_1_.id;
                  RealmsMainScreen.this.func_223966_f(lvt_8_1_);
               } else if (RealmsMainScreen.this.field_224027_p != null && RealmsMainScreen.this.field_224027_p.equals(RealmsScreen.getLocalizedString("mco.selectServer.leave"))) {
                  RealmsMainScreen.this.field_224021_j = lvt_8_1_.id;
                  RealmsMainScreen.this.func_223906_g(lvt_8_1_);
               } else if (RealmsMainScreen.this.func_223885_h(lvt_8_1_) && lvt_8_1_.expired && RealmsMainScreen.this.field_224005_M) {
                  RealmsMainScreen.this.func_223930_q();
               }

            }
         }
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 300;
      }
   }
}
