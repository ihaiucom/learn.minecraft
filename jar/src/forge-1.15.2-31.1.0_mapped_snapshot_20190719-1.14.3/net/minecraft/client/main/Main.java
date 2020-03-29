package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.blaze3d.Empty3i;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.util.UndeclaredException;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Main {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void main(String[] p_main_0_) {
      OptionParser lvt_1_1_ = new OptionParser();
      lvt_1_1_.allowsUnrecognizedOptions();
      lvt_1_1_.accepts("demo");
      lvt_1_1_.accepts("fullscreen");
      lvt_1_1_.accepts("checkGlErrors");
      OptionSpec<String> lvt_2_1_ = lvt_1_1_.accepts("server").withRequiredArg();
      OptionSpec<Integer> lvt_3_1_ = lvt_1_1_.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
      OptionSpec<File> lvt_4_1_ = lvt_1_1_.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      OptionSpec<File> lvt_5_1_ = lvt_1_1_.accepts("assetsDir").withRequiredArg().ofType(File.class);
      OptionSpec<File> lvt_6_1_ = lvt_1_1_.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      OptionSpec<String> lvt_7_1_ = lvt_1_1_.accepts("proxyHost").withRequiredArg();
      OptionSpec<Integer> lvt_8_1_ = lvt_1_1_.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      OptionSpec<String> lvt_9_1_ = lvt_1_1_.accepts("proxyUser").withRequiredArg();
      OptionSpec<String> lvt_10_1_ = lvt_1_1_.accepts("proxyPass").withRequiredArg();
      OptionSpec<String> lvt_11_1_ = lvt_1_1_.accepts("username").withRequiredArg().defaultsTo("Player" + Util.milliTime() % 1000L, new String[0]);
      OptionSpec<String> lvt_12_1_ = lvt_1_1_.accepts("uuid").withRequiredArg();
      OptionSpec<String> lvt_13_1_ = lvt_1_1_.accepts("accessToken").withRequiredArg().required();
      OptionSpec<String> lvt_14_1_ = lvt_1_1_.accepts("version").withRequiredArg().required();
      OptionSpec<Integer> lvt_15_1_ = lvt_1_1_.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      OptionSpec<Integer> lvt_16_1_ = lvt_1_1_.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      OptionSpec<Integer> lvt_17_1_ = lvt_1_1_.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      OptionSpec<Integer> lvt_18_1_ = lvt_1_1_.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      OptionSpec<String> lvt_19_1_ = lvt_1_1_.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      OptionSpec<String> lvt_20_1_ = lvt_1_1_.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      OptionSpec<String> lvt_21_1_ = lvt_1_1_.accepts("assetIndex").withRequiredArg();
      OptionSpec<String> lvt_22_1_ = lvt_1_1_.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      OptionSpec<String> lvt_23_1_ = lvt_1_1_.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      OptionSpec<String> lvt_24_1_ = lvt_1_1_.nonOptions();
      OptionSet lvt_25_1_ = lvt_1_1_.parse(p_main_0_);
      List<String> lvt_26_1_ = lvt_25_1_.valuesOf(lvt_24_1_);
      if (!lvt_26_1_.isEmpty()) {
         System.out.println("Completely ignored arguments: " + lvt_26_1_);
      }

      String lvt_27_1_ = (String)getValue(lvt_25_1_, lvt_7_1_);
      Proxy lvt_28_1_ = Proxy.NO_PROXY;
      if (lvt_27_1_ != null) {
         try {
            lvt_28_1_ = new Proxy(Type.SOCKS, new InetSocketAddress(lvt_27_1_, (Integer)getValue(lvt_25_1_, lvt_8_1_)));
         } catch (Exception var68) {
         }
      }

      final String lvt_29_1_ = (String)getValue(lvt_25_1_, lvt_9_1_);
      final String lvt_30_1_ = (String)getValue(lvt_25_1_, lvt_10_1_);
      if (!lvt_28_1_.equals(Proxy.NO_PROXY) && isNotEmpty(lvt_29_1_) && isNotEmpty(lvt_30_1_)) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(lvt_29_1_, lvt_30_1_.toCharArray());
            }
         });
      }

      int lvt_31_1_ = (Integer)getValue(lvt_25_1_, lvt_15_1_);
      int lvt_32_1_ = (Integer)getValue(lvt_25_1_, lvt_16_1_);
      OptionalInt lvt_33_1_ = func_224732_a((Integer)getValue(lvt_25_1_, lvt_17_1_));
      OptionalInt lvt_34_1_ = func_224732_a((Integer)getValue(lvt_25_1_, lvt_18_1_));
      boolean lvt_35_1_ = lvt_25_1_.has("fullscreen");
      boolean lvt_36_1_ = lvt_25_1_.has("demo");
      String lvt_37_1_ = (String)getValue(lvt_25_1_, lvt_14_1_);
      Gson lvt_38_1_ = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap lvt_39_1_ = (PropertyMap)JSONUtils.fromJson(lvt_38_1_, (String)getValue(lvt_25_1_, lvt_19_1_), PropertyMap.class);
      PropertyMap lvt_40_1_ = (PropertyMap)JSONUtils.fromJson(lvt_38_1_, (String)getValue(lvt_25_1_, lvt_20_1_), PropertyMap.class);
      String lvt_41_1_ = (String)getValue(lvt_25_1_, lvt_23_1_);
      File lvt_42_1_ = (File)getValue(lvt_25_1_, lvt_4_1_);
      File lvt_43_1_ = lvt_25_1_.has(lvt_5_1_) ? (File)getValue(lvt_25_1_, lvt_5_1_) : new File(lvt_42_1_, "assets/");
      File lvt_44_1_ = lvt_25_1_.has(lvt_6_1_) ? (File)getValue(lvt_25_1_, lvt_6_1_) : new File(lvt_42_1_, "resourcepacks/");
      String lvt_45_1_ = lvt_25_1_.has(lvt_12_1_) ? (String)lvt_12_1_.value(lvt_25_1_) : PlayerEntity.getOfflineUUID((String)lvt_11_1_.value(lvt_25_1_)).toString();
      String lvt_46_1_ = lvt_25_1_.has(lvt_21_1_) ? (String)lvt_21_1_.value(lvt_25_1_) : null;
      String lvt_47_1_ = (String)getValue(lvt_25_1_, lvt_2_1_);
      Integer lvt_48_1_ = (Integer)getValue(lvt_25_1_, lvt_3_1_);
      CrashReport.func_230188_h_();
      Session lvt_49_1_ = new Session((String)lvt_11_1_.value(lvt_25_1_), lvt_45_1_, (String)lvt_13_1_.value(lvt_25_1_), (String)lvt_22_1_.value(lvt_25_1_));
      GameConfiguration lvt_50_1_ = new GameConfiguration(new GameConfiguration.UserInformation(lvt_49_1_, lvt_39_1_, lvt_40_1_, lvt_28_1_), new ScreenSize(lvt_31_1_, lvt_32_1_, lvt_33_1_, lvt_34_1_, lvt_35_1_), new GameConfiguration.FolderInformation(lvt_42_1_, lvt_44_1_, lvt_43_1_, lvt_46_1_), new GameConfiguration.GameInformation(lvt_36_1_, lvt_37_1_, lvt_41_1_), new GameConfiguration.ServerInformation(lvt_47_1_, lvt_48_1_));
      Thread lvt_51_1_ = new Thread("Client Shutdown Thread") {
         public void run() {
            Minecraft lvt_1_1_ = Minecraft.getInstance();
            if (lvt_1_1_ != null) {
               IntegratedServer lvt_2_1_ = lvt_1_1_.getIntegratedServer();
               if (lvt_2_1_ != null) {
                  lvt_2_1_.initiateShutdown(true);
               }

            }
         }
      };
      lvt_51_1_.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      Runtime.getRuntime().addShutdownHook(lvt_51_1_);
      new Empty3i();

      final Minecraft lvt_53_2_;
      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         RenderSystem.beginInitialization();
         lvt_53_2_ = new Minecraft(lvt_50_1_);
         RenderSystem.finishInitialization();
      } catch (UndeclaredException var66) {
         LOGGER.warn("Failed to create window: ", var66);
         return;
      } catch (Throwable var67) {
         CrashReport lvt_55_1_ = CrashReport.makeCrashReport(var67, "Initializing game");
         lvt_55_1_.makeCategory("Initialization");
         Minecraft.func_228009_a_((LanguageManager)null, lvt_50_1_.gameInfo.version, (GameSettings)null, lvt_55_1_);
         Minecraft.displayCrashReport(lvt_55_1_);
         return;
      }

      Thread lvt_54_4_;
      if (lvt_53_2_.func_228017_as_()) {
         lvt_54_4_ = new Thread("Game thread") {
            public void run() {
               try {
                  RenderSystem.initGameThread(true);
                  lvt_53_2_.run();
               } catch (Throwable var2) {
                  Main.LOGGER.error("Exception in client thread", var2);
               }

            }
         };
         lvt_54_4_.start();

         while(true) {
            if (lvt_53_2_.func_228025_l_()) {
               continue;
            }
         }
      } else {
         lvt_54_4_ = null;

         try {
            RenderSystem.initGameThread(false);
            lvt_53_2_.run();
         } catch (Throwable var65) {
            LOGGER.error("Unhandled game exception", var65);
         }
      }

      try {
         lvt_53_2_.shutdown();
         if (lvt_54_4_ != null) {
            lvt_54_4_.join();
         }
      } catch (InterruptedException var63) {
         LOGGER.error("Exception during client thread shutdown", var63);
      } finally {
         lvt_53_2_.shutdownMinecraftApplet();
      }

   }

   private static OptionalInt func_224732_a(@Nullable Integer p_224732_0_) {
      return p_224732_0_ != null ? OptionalInt.of(p_224732_0_) : OptionalInt.empty();
   }

   @Nullable
   private static <T> T getValue(OptionSet p_206236_0_, OptionSpec<T> p_206236_1_) {
      try {
         return p_206236_0_.valueOf(p_206236_1_);
      } catch (Throwable var5) {
         if (p_206236_1_ instanceof ArgumentAcceptingOptionSpec) {
            ArgumentAcceptingOptionSpec<T> lvt_3_1_ = (ArgumentAcceptingOptionSpec)p_206236_1_;
            List<T> lvt_4_1_ = lvt_3_1_.defaultValues();
            if (!lvt_4_1_.isEmpty()) {
               return lvt_4_1_.get(0);
            }
         }

         throw var5;
      }
   }

   private static boolean isNotEmpty(@Nullable String p_110121_0_) {
      return p_110121_0_ != null && !p_110121_0_.isEmpty();
   }

   static {
      System.setProperty("java.awt.headless", "true");
   }
}
