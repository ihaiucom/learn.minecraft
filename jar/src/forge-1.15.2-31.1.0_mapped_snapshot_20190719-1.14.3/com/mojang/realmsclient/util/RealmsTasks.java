package com.mojang.realmsclient.util;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResourcePackScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsConnect;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsTasks {
   private static final Logger field_225184_a = LogManager.getLogger();

   private static void func_225182_b(int p_225182_0_) {
      try {
         Thread.sleep((long)(p_225182_0_ * 1000));
      } catch (InterruptedException var2) {
         field_225184_a.error("", var2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class DownloadTask extends LongRunningTask {
      private final long field_224996_b;
      private final int field_224997_c;
      private final RealmsScreen field_224998_d;
      private final String field_224999_e;

      public DownloadTask(long p_i51716_1_, int p_i51716_3_, String p_i51716_4_, RealmsScreen p_i51716_5_) {
         this.field_224996_b = p_i51716_1_;
         this.field_224997_c = p_i51716_3_;
         this.field_224998_d = p_i51716_5_;
         this.field_224999_e = p_i51716_4_;
      }

      public void run() {
         this.func_224989_b(RealmsScreen.getLocalizedString("mco.download.preparing"));
         RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
         int lvt_2_1_ = 0;

         while(lvt_2_1_ < 25) {
            try {
               if (this.func_224988_a()) {
                  return;
               }

               WorldDownload lvt_3_1_ = lvt_1_1_.func_224917_b(this.field_224996_b, this.field_224997_c);
               RealmsTasks.func_225182_b(1);
               if (this.func_224988_a()) {
                  return;
               }

               Realms.setScreen(new RealmsDownloadLatestWorldScreen(this.field_224998_d, lvt_3_1_, this.field_224999_e));
               return;
            } catch (RetryCallException var4) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.func_225182_b(var4.field_224985_e);
               ++lvt_2_1_;
            } catch (RealmsServiceException var5) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.field_225184_a.error("Couldn't download world data");
               Realms.setScreen(new RealmsGenericErrorScreen(var5, this.field_224998_d));
               return;
            } catch (Exception var6) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.field_225184_a.error("Couldn't download world data", var6);
               this.func_224986_a(var6.getLocalizedMessage());
               return;
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class RestoreTask extends LongRunningTask {
      private final Backup field_225021_b;
      private final long field_225022_c;
      private final RealmsConfigureWorldScreen field_225023_d;

      public RestoreTask(Backup p_i51710_1_, long p_i51710_2_, RealmsConfigureWorldScreen p_i51710_4_) {
         this.field_225021_b = p_i51710_1_;
         this.field_225022_c = p_i51710_2_;
         this.field_225023_d = p_i51710_4_;
      }

      public void run() {
         this.func_224989_b(RealmsScreen.getLocalizedString("mco.backup.restoring"));
         RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
         int lvt_2_1_ = 0;

         while(lvt_2_1_ < 25) {
            try {
               if (this.func_224988_a()) {
                  return;
               }

               lvt_1_1_.func_224928_c(this.field_225022_c, this.field_225021_b.backupId);
               RealmsTasks.func_225182_b(1);
               if (this.func_224988_a()) {
                  return;
               }

               Realms.setScreen(this.field_225023_d.func_224407_b());
               return;
            } catch (RetryCallException var4) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.func_225182_b(var4.field_224985_e);
               ++lvt_2_1_;
            } catch (RealmsServiceException var5) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.field_225184_a.error("Couldn't restore backup", var5);
               Realms.setScreen(new RealmsGenericErrorScreen(var5, this.field_225023_d));
               return;
            } catch (Exception var6) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.field_225184_a.error("Couldn't restore backup", var6);
               this.func_224986_a(var6.getLocalizedMessage());
               return;
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WorldCreationTask extends LongRunningTask {
      private final String field_225034_b;
      private final String field_225035_c;
      private final long field_225036_d;
      private final RealmsScreen field_225037_e;

      public WorldCreationTask(long p_i51706_1_, String p_i51706_3_, String p_i51706_4_, RealmsScreen p_i51706_5_) {
         this.field_225036_d = p_i51706_1_;
         this.field_225034_b = p_i51706_3_;
         this.field_225035_c = p_i51706_4_;
         this.field_225037_e = p_i51706_5_;
      }

      public void run() {
         String lvt_1_1_ = RealmsScreen.getLocalizedString("mco.create.world.wait");
         this.func_224989_b(lvt_1_1_);
         RealmsClient lvt_2_1_ = RealmsClient.func_224911_a();

         try {
            lvt_2_1_.func_224900_a(this.field_225036_d, this.field_225034_b, this.field_225035_c);
            Realms.setScreen(this.field_225037_e);
         } catch (RealmsServiceException var4) {
            RealmsTasks.field_225184_a.error("Couldn't create world");
            this.func_224986_a(var4.toString());
         } catch (UnsupportedEncodingException var5) {
            RealmsTasks.field_225184_a.error("Couldn't create world");
            this.func_224986_a(var5.getLocalizedMessage());
         } catch (IOException var6) {
            RealmsTasks.field_225184_a.error("Could not parse response creating world");
            this.func_224986_a(var6.getLocalizedMessage());
         } catch (Exception var7) {
            RealmsTasks.field_225184_a.error("Could not create world");
            this.func_224986_a(var7.getLocalizedMessage());
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class RealmsConnectTask extends LongRunningTask {
      private final RealmsConnect field_225004_b;
      private final RealmsServerAddress field_225005_c;

      public RealmsConnectTask(RealmsScreen p_i51714_1_, RealmsServerAddress p_i51714_2_) {
         this.field_225005_c = p_i51714_2_;
         this.field_225004_b = new RealmsConnect(p_i51714_1_);
      }

      public void run() {
         this.func_224989_b(RealmsScreen.getLocalizedString("mco.connect.connecting"));
         net.minecraft.realms.RealmsServerAddress lvt_1_1_ = net.minecraft.realms.RealmsServerAddress.parseString(this.field_225005_c.address);
         this.field_225004_b.connect(lvt_1_1_.getHost(), lvt_1_1_.getPort());
      }

      public void func_224992_d() {
         this.field_225004_b.abort();
         Realms.clearResourcePack();
      }

      public void func_224990_b() {
         this.field_225004_b.tick();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class RealmsGetServerDetailsTask extends LongRunningTask {
      private final RealmsServer field_225007_b;
      private final RealmsScreen field_225008_c;
      private final RealmsMainScreen field_225009_d;
      private final ReentrantLock field_225010_e;

      public RealmsGetServerDetailsTask(RealmsMainScreen p_i51713_1_, RealmsScreen p_i51713_2_, RealmsServer p_i51713_3_, ReentrantLock p_i51713_4_) {
         this.field_225008_c = p_i51713_2_;
         this.field_225009_d = p_i51713_1_;
         this.field_225007_b = p_i51713_3_;
         this.field_225010_e = p_i51713_4_;
      }

      public void run() {
         this.func_224989_b(RealmsScreen.getLocalizedString("mco.connect.connecting"));
         RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
         boolean lvt_2_1_ = false;
         boolean lvt_3_1_ = false;
         int lvt_4_1_ = 5;
         RealmsServerAddress lvt_5_1_ = null;
         boolean lvt_6_1_ = false;
         boolean lvt_7_1_ = false;

         for(int lvt_8_1_ = 0; lvt_8_1_ < 40 && !this.func_224988_a(); ++lvt_8_1_) {
            try {
               lvt_5_1_ = lvt_1_1_.func_224904_b(this.field_225007_b.id);
               lvt_2_1_ = true;
            } catch (RetryCallException var10) {
               lvt_4_1_ = var10.field_224985_e;
            } catch (RealmsServiceException var11) {
               if (var11.field_224983_c == 6002) {
                  lvt_6_1_ = true;
               } else if (var11.field_224983_c == 6006) {
                  lvt_7_1_ = true;
               } else {
                  lvt_3_1_ = true;
                  this.func_224986_a(var11.toString());
                  RealmsTasks.field_225184_a.error("Couldn't connect to world", var11);
               }
               break;
            } catch (IOException var12) {
               RealmsTasks.field_225184_a.error("Couldn't parse response connecting to world", var12);
            } catch (Exception var13) {
               lvt_3_1_ = true;
               RealmsTasks.field_225184_a.error("Couldn't connect to world", var13);
               this.func_224986_a(var13.getLocalizedMessage());
               break;
            }

            if (lvt_2_1_) {
               break;
            }

            this.func_225006_a(lvt_4_1_);
         }

         if (lvt_6_1_) {
            Realms.setScreen(new RealmsTermsScreen(this.field_225008_c, this.field_225009_d, this.field_225007_b));
         } else if (lvt_7_1_) {
            if (this.field_225007_b.ownerUUID.equals(Realms.getUUID())) {
               RealmsBrokenWorldScreen lvt_8_2_ = new RealmsBrokenWorldScreen(this.field_225008_c, this.field_225009_d, this.field_225007_b.id);
               if (this.field_225007_b.worldType.equals(RealmsServer.ServerType.MINIGAME)) {
                  lvt_8_2_.func_224052_a(RealmsScreen.getLocalizedString("mco.brokenworld.minigame.title"));
               }

               Realms.setScreen(lvt_8_2_);
            } else {
               Realms.setScreen(new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.title"), RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.error"), this.field_225008_c));
            }
         } else if (!this.func_224988_a() && !lvt_3_1_) {
            if (lvt_2_1_) {
               if (lvt_5_1_.resourcePackUrl != null && lvt_5_1_.resourcePackHash != null) {
                  String lvt_8_3_ = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line1");
                  String lvt_9_5_ = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line2");
                  Realms.setScreen(new RealmsLongConfirmationScreen(new RealmsResourcePackScreen(this.field_225008_c, lvt_5_1_, this.field_225010_e), RealmsLongConfirmationScreen.Type.Info, lvt_8_3_, lvt_9_5_, true, 100));
               } else {
                  RealmsLongRunningMcoTaskScreen lvt_8_4_ = new RealmsLongRunningMcoTaskScreen(this.field_225008_c, new RealmsTasks.RealmsConnectTask(this.field_225008_c, lvt_5_1_));
                  lvt_8_4_.func_224233_a();
                  Realms.setScreen(lvt_8_4_);
               }
            } else {
               this.func_224986_a(RealmsScreen.getLocalizedString("mco.errorMessage.connectionFailure"));
            }
         }

      }

      private void func_225006_a(int p_225006_1_) {
         try {
            Thread.sleep((long)(p_225006_1_ * 1000));
         } catch (InterruptedException var3) {
            RealmsTasks.field_225184_a.warn(var3.getLocalizedMessage());
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ResettingWorldTask extends LongRunningTask {
      private final String field_225013_b;
      private final WorldTemplate field_225014_c;
      private final int field_225015_d;
      private final boolean field_225016_e;
      private final long field_225017_f;
      private final RealmsScreen field_225018_g;
      private int field_225019_h = -1;
      private String field_225020_i = RealmsScreen.getLocalizedString("mco.reset.world.resetting.screen.title");

      public ResettingWorldTask(long p_i51711_1_, RealmsScreen p_i51711_3_, WorldTemplate p_i51711_4_) {
         this.field_225013_b = null;
         this.field_225014_c = p_i51711_4_;
         this.field_225015_d = -1;
         this.field_225016_e = true;
         this.field_225017_f = p_i51711_1_;
         this.field_225018_g = p_i51711_3_;
      }

      public ResettingWorldTask(long p_i51712_1_, RealmsScreen p_i51712_3_, String p_i51712_4_, int p_i51712_5_, boolean p_i51712_6_) {
         this.field_225013_b = p_i51712_4_;
         this.field_225014_c = null;
         this.field_225015_d = p_i51712_5_;
         this.field_225016_e = p_i51712_6_;
         this.field_225017_f = p_i51712_1_;
         this.field_225018_g = p_i51712_3_;
      }

      public void func_225011_a(int p_225011_1_) {
         this.field_225019_h = p_225011_1_;
      }

      public void func_225012_c(String p_225012_1_) {
         this.field_225020_i = p_225012_1_;
      }

      public void run() {
         RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
         this.func_224989_b(this.field_225020_i);
         int lvt_2_1_ = 0;

         while(lvt_2_1_ < 25) {
            try {
               if (this.func_224988_a()) {
                  return;
               }

               if (this.field_225014_c != null) {
                  lvt_1_1_.func_224924_g(this.field_225017_f, this.field_225014_c.id);
               } else {
                  lvt_1_1_.func_224943_a(this.field_225017_f, this.field_225013_b, this.field_225015_d, this.field_225016_e);
               }

               if (this.func_224988_a()) {
                  return;
               }

               if (this.field_225019_h == -1) {
                  Realms.setScreen(this.field_225018_g);
               } else {
                  this.field_225018_g.confirmResult(true, this.field_225019_h);
               }

               return;
            } catch (RetryCallException var4) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.func_225182_b(var4.field_224985_e);
               ++lvt_2_1_;
            } catch (Exception var5) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.field_225184_a.error("Couldn't reset world");
               this.func_224986_a(var5.toString());
               return;
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SwitchMinigameTask extends LongRunningTask {
      private final long field_225024_b;
      private final WorldTemplate field_225025_c;
      private final RealmsConfigureWorldScreen field_225026_d;

      public SwitchMinigameTask(long p_i51709_1_, WorldTemplate p_i51709_3_, RealmsConfigureWorldScreen p_i51709_4_) {
         this.field_225024_b = p_i51709_1_;
         this.field_225025_c = p_i51709_3_;
         this.field_225026_d = p_i51709_4_;
      }

      public void run() {
         RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
         String lvt_2_1_ = RealmsScreen.getLocalizedString("mco.minigame.world.starting.screen.title");
         this.func_224989_b(lvt_2_1_);

         for(int lvt_3_1_ = 0; lvt_3_1_ < 25; ++lvt_3_1_) {
            try {
               if (this.func_224988_a()) {
                  return;
               }

               if (lvt_1_1_.func_224905_d(this.field_225024_b, this.field_225025_c.id)) {
                  Realms.setScreen(this.field_225026_d);
                  break;
               }
            } catch (RetryCallException var5) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.func_225182_b(var5.field_224985_e);
            } catch (Exception var6) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.field_225184_a.error("Couldn't start mini game!");
               this.func_224986_a(var6.toString());
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SwitchSlotTask extends LongRunningTask {
      private final long field_225027_b;
      private final int field_225028_c;
      private final RealmsConfirmResultListener field_225029_d;
      private final int field_225030_e;

      public SwitchSlotTask(long p_i51708_1_, int p_i51708_3_, RealmsConfirmResultListener p_i51708_4_, int p_i51708_5_) {
         this.field_225027_b = p_i51708_1_;
         this.field_225028_c = p_i51708_3_;
         this.field_225029_d = p_i51708_4_;
         this.field_225030_e = p_i51708_5_;
      }

      public void run() {
         RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
         String lvt_2_1_ = RealmsScreen.getLocalizedString("mco.minigame.world.slot.screen.title");
         this.func_224989_b(lvt_2_1_);

         for(int lvt_3_1_ = 0; lvt_3_1_ < 25; ++lvt_3_1_) {
            try {
               if (this.func_224988_a()) {
                  return;
               }

               if (lvt_1_1_.func_224927_a(this.field_225027_b, this.field_225028_c)) {
                  this.field_225029_d.confirmResult(true, this.field_225030_e);
                  break;
               }
            } catch (RetryCallException var5) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.func_225182_b(var5.field_224985_e);
            } catch (Exception var6) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.field_225184_a.error("Couldn't switch world!");
               this.func_224986_a(var6.toString());
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class CloseServerTask extends LongRunningTask {
      private final RealmsServer field_224994_b;
      private final RealmsConfigureWorldScreen field_224995_c;

      public CloseServerTask(RealmsServer p_i51717_1_, RealmsConfigureWorldScreen p_i51717_2_) {
         this.field_224994_b = p_i51717_1_;
         this.field_224995_c = p_i51717_2_;
      }

      public void run() {
         this.func_224989_b(RealmsScreen.getLocalizedString("mco.configure.world.closing"));
         RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();

         for(int lvt_2_1_ = 0; lvt_2_1_ < 25; ++lvt_2_1_) {
            if (this.func_224988_a()) {
               return;
            }

            try {
               boolean lvt_3_1_ = lvt_1_1_.func_224932_f(this.field_224994_b.id);
               if (lvt_3_1_) {
                  this.field_224995_c.func_224398_a();
                  this.field_224994_b.state = RealmsServer.Status.CLOSED;
                  Realms.setScreen(this.field_224995_c);
                  break;
               }
            } catch (RetryCallException var4) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.func_225182_b(var4.field_224985_e);
            } catch (Exception var5) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.field_225184_a.error("Failed to close server", var5);
               this.func_224986_a("Failed to close the server");
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class OpenServerTask extends LongRunningTask {
      private final RealmsServer field_225000_b;
      private final RealmsScreen field_225001_c;
      private final boolean field_225002_d;
      private final RealmsScreen field_225003_e;

      public OpenServerTask(RealmsServer p_i51715_1_, RealmsScreen p_i51715_2_, RealmsScreen p_i51715_3_, boolean p_i51715_4_) {
         this.field_225000_b = p_i51715_1_;
         this.field_225001_c = p_i51715_2_;
         this.field_225002_d = p_i51715_4_;
         this.field_225003_e = p_i51715_3_;
      }

      public void run() {
         this.func_224989_b(RealmsScreen.getLocalizedString("mco.configure.world.opening"));
         RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();

         for(int lvt_2_1_ = 0; lvt_2_1_ < 25; ++lvt_2_1_) {
            if (this.func_224988_a()) {
               return;
            }

            try {
               boolean lvt_3_1_ = lvt_1_1_.func_224942_e(this.field_225000_b.id);
               if (lvt_3_1_) {
                  if (this.field_225001_c instanceof RealmsConfigureWorldScreen) {
                     ((RealmsConfigureWorldScreen)this.field_225001_c).func_224398_a();
                  }

                  this.field_225000_b.state = RealmsServer.Status.OPEN;
                  if (this.field_225002_d) {
                     ((RealmsMainScreen)this.field_225003_e).func_223911_a(this.field_225000_b, this.field_225001_c);
                  } else {
                     Realms.setScreen(this.field_225001_c);
                  }
                  break;
               }
            } catch (RetryCallException var4) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.func_225182_b(var4.field_224985_e);
            } catch (Exception var5) {
               if (this.func_224988_a()) {
                  return;
               }

               RealmsTasks.field_225184_a.error("Failed to open server", var5);
               this.func_224986_a("Failed to open the server");
            }
         }

      }
   }
}
