package net.minecraft.client;

import com.mojang.bridge.Bridge;
import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.Language;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.bridge.game.RunningGame;
import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.SessionEventListener;
import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecraftGame implements RunningGame {
   private final Minecraft gameInstance;
   @Nullable
   private final Launcher launcher;
   private SessionEventListener sessionListener;

   public MinecraftGame(Minecraft p_i51163_1_) {
      this.sessionListener = SessionEventListener.NONE;
      this.gameInstance = p_i51163_1_;
      this.launcher = Bridge.getLauncher();
      if (this.launcher != null) {
         this.launcher.registerGame(this);
      }

   }

   public GameVersion getVersion() {
      return SharedConstants.getVersion();
   }

   public Language getSelectedLanguage() {
      return this.gameInstance.getLanguageManager().getCurrentLanguage();
   }

   @Nullable
   public GameSession getCurrentSession() {
      ClientWorld lvt_1_1_ = this.gameInstance.world;
      return lvt_1_1_ == null ? null : new ClientGameSession(lvt_1_1_, this.gameInstance.player, this.gameInstance.player.connection);
   }

   public PerformanceMetrics getPerformanceMetrics() {
      FrameTimer lvt_1_1_ = this.gameInstance.getFrameTimer();
      long lvt_2_1_ = 2147483647L;
      long lvt_4_1_ = -2147483648L;
      long lvt_6_1_ = 0L;
      long[] var8 = lvt_1_1_.getFrames();
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         long lvt_11_1_ = var8[var10];
         lvt_2_1_ = Math.min(lvt_2_1_, lvt_11_1_);
         lvt_4_1_ = Math.max(lvt_4_1_, lvt_11_1_);
         lvt_6_1_ += lvt_11_1_;
      }

      return new MinecraftGame.MinecraftPerformanceMetrics((int)lvt_2_1_, (int)lvt_4_1_, (int)(lvt_6_1_ / (long)lvt_1_1_.getFrames().length), lvt_1_1_.getFrames().length);
   }

   public void setSessionEventListener(SessionEventListener p_setSessionEventListener_1_) {
      this.sessionListener = p_setSessionEventListener_1_;
   }

   public void func_216814_a() {
      this.sessionListener.onStartGameSession(this.getCurrentSession());
   }

   public void func_216815_b() {
      this.sessionListener.onLeaveGameSession(this.getCurrentSession());
   }

   @OnlyIn(Dist.CLIENT)
   static class MinecraftPerformanceMetrics implements PerformanceMetrics {
      private final int minTime;
      private final int maxTime;
      private final int averageTime;
      private final int sampleCount;

      public MinecraftPerformanceMetrics(int p_i51282_1_, int p_i51282_2_, int p_i51282_3_, int p_i51282_4_) {
         this.minTime = p_i51282_1_;
         this.maxTime = p_i51282_2_;
         this.averageTime = p_i51282_3_;
         this.sampleCount = p_i51282_4_;
      }

      public int getMinTime() {
         return this.minTime;
      }

      public int getMaxTime() {
         return this.maxTime;
      }

      public int getAverageTime() {
         return this.averageTime;
      }

      public int getSampleCount() {
         return this.sampleCount;
      }
   }
}
