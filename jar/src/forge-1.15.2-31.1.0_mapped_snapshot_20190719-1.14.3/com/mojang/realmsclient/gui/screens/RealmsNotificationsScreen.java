package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsNotificationsScreen extends RealmsScreen {
   private static final RealmsDataFetcher field_224265_a = new RealmsDataFetcher();
   private volatile int field_224266_b;
   private static boolean field_224267_c;
   private static boolean field_224268_d;
   private static boolean field_224269_e;
   private static boolean field_224270_f;
   private static final List<RealmsDataFetcher.Task> field_224271_g;

   public RealmsNotificationsScreen(RealmsScreen p_i51763_1_) {
   }

   public void init() {
      this.func_224261_a();
      this.setKeyboardHandlerSendRepeatsToGui(true);
   }

   public void tick() {
      if ((!Realms.getRealmsNotificationsEnabled() || !Realms.inTitleScreen() || !field_224269_e) && !field_224265_a.func_225065_a()) {
         field_224265_a.func_225070_k();
      } else if (field_224269_e && Realms.getRealmsNotificationsEnabled()) {
         field_224265_a.func_225077_a(field_224271_g);
         if (field_224265_a.func_225083_a(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.field_224266_b = field_224265_a.func_225081_f();
         }

         if (field_224265_a.func_225083_a(RealmsDataFetcher.Task.TRIAL_AVAILABLE)) {
            field_224268_d = field_224265_a.func_225071_g();
         }

         if (field_224265_a.func_225083_a(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            field_224270_f = field_224265_a.func_225059_i();
         }

         field_224265_a.func_225072_c();
      }
   }

   private void func_224261_a() {
      if (!field_224267_c) {
         field_224267_c = true;
         (new Thread("Realms Notification Availability checker #1") {
            public void run() {
               RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();

               try {
                  RealmsClient.CompatibleVersionResponse lvt_2_1_ = lvt_1_1_.func_224939_i();
                  if (!lvt_2_1_.equals(RealmsClient.CompatibleVersionResponse.COMPATIBLE)) {
                     return;
                  }
               } catch (RealmsServiceException var3) {
                  if (var3.field_224981_a != 401) {
                     RealmsNotificationsScreen.field_224267_c = false;
                  }

                  return;
               } catch (IOException var4) {
                  RealmsNotificationsScreen.field_224267_c = false;
                  return;
               }

               RealmsNotificationsScreen.field_224269_e = true;
            }
         }).start();
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (field_224269_e) {
         this.func_224262_a(p_render_1_, p_render_2_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   private void func_224262_a(int p_224262_1_, int p_224262_2_) {
      int lvt_3_1_ = this.field_224266_b;
      int lvt_4_1_ = true;
      int lvt_5_1_ = this.height() / 4 + 48;
      int lvt_6_1_ = this.width() / 2 + 80;
      int lvt_7_1_ = lvt_5_1_ + 48 + 2;
      int lvt_8_1_ = 0;
      if (field_224270_f) {
         RealmsScreen.bind("realms:textures/gui/realms/news_notification_mainscreen.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.4F, 0.4F, 0.4F);
         RealmsScreen.blit((int)((double)(lvt_6_1_ + 2 - lvt_8_1_) * 2.5D), (int)((double)lvt_7_1_ * 2.5D), 0.0F, 0.0F, 40, 40, 40, 40);
         RenderSystem.popMatrix();
         lvt_8_1_ += 14;
      }

      if (lvt_3_1_ != 0) {
         RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RealmsScreen.blit(lvt_6_1_ - lvt_8_1_, lvt_7_1_ - 6, 0.0F, 0.0F, 15, 25, 31, 25);
         RenderSystem.popMatrix();
         lvt_8_1_ += 16;
      }

      if (field_224268_d) {
         RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         int lvt_9_1_ = 0;
         if ((System.currentTimeMillis() / 800L & 1L) == 1L) {
            lvt_9_1_ = 8;
         }

         RealmsScreen.blit(lvt_6_1_ + 4 - lvt_8_1_, lvt_7_1_ + 4, 0.0F, (float)lvt_9_1_, 8, 8, 8, 16);
         RenderSystem.popMatrix();
      }

   }

   public void removed() {
      field_224265_a.func_225070_k();
   }

   static {
      field_224271_g = Arrays.asList(RealmsDataFetcher.Task.PENDING_INVITE, RealmsDataFetcher.Task.TRIAL_AVAILABLE, RealmsDataFetcher.Task.UNREAD_NEWS);
   }
}
