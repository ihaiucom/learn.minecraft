package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBrokenWorldScreen extends RealmsScreen {
   private static final Logger field_224071_a = LogManager.getLogger();
   private final RealmsScreen field_224072_b;
   private final RealmsMainScreen field_224073_c;
   private RealmsServer field_224074_d;
   private final long field_224075_e;
   private String field_224076_f = getLocalizedString("mco.brokenworld.title");
   private final String field_224077_g = getLocalizedString("mco.brokenworld.message.line1") + "\\n" + getLocalizedString("mco.brokenworld.message.line2");
   private int field_224078_h;
   private int field_224079_i;
   private final int field_224080_j = 80;
   private final int field_224081_k = 5;
   private static final List<Integer> field_224082_l = Arrays.asList(1, 2, 3);
   private static final List<Integer> field_224083_m = Arrays.asList(4, 5, 6);
   private static final List<Integer> field_224084_n = Arrays.asList(7, 8, 9);
   private static final List<Integer> field_224085_o = Arrays.asList(10, 11, 12);
   private final List<Integer> field_224086_p = Lists.newArrayList();
   private int field_224087_q;

   public RealmsBrokenWorldScreen(RealmsScreen p_i51776_1_, RealmsMainScreen p_i51776_2_, long p_i51776_3_) {
      this.field_224072_b = p_i51776_1_;
      this.field_224073_c = p_i51776_2_;
      this.field_224075_e = p_i51776_3_;
   }

   public void func_224052_a(String p_224052_1_) {
      this.field_224076_f = p_224052_1_;
   }

   public void init() {
      this.field_224078_h = this.width() / 2 - 150;
      this.field_224079_i = this.width() / 2 + 190;
      this.buttonsAdd(new RealmsButton(0, this.field_224079_i - 80 + 8, RealmsConstants.func_225109_a(13) - 5, 70, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsBrokenWorldScreen.this.func_224060_e();
         }
      });
      if (this.field_224074_d == null) {
         this.func_224068_a(this.field_224075_e);
      } else {
         this.func_224058_a();
      }

      this.setKeyboardHandlerSendRepeatsToGui(true);
   }

   public void func_224058_a() {
      Iterator var1 = this.field_224074_d.slots.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<Integer, RealmsWorldOptions> lvt_2_1_ = (Entry)var1.next();
         RealmsWorldOptions lvt_3_1_ = (RealmsWorldOptions)lvt_2_1_.getValue();
         boolean lvt_4_1_ = (Integer)lvt_2_1_.getKey() != this.field_224074_d.activeSlot || this.field_224074_d.worldType.equals(RealmsServer.ServerType.MINIGAME);
         Object lvt_5_2_;
         if (lvt_4_1_) {
            lvt_5_2_ = new RealmsBrokenWorldScreen.PlayButton((Integer)field_224082_l.get((Integer)lvt_2_1_.getKey() - 1), this.func_224065_a((Integer)lvt_2_1_.getKey()), getLocalizedString("mco.brokenworld.play"));
         } else {
            lvt_5_2_ = new RealmsBrokenWorldScreen.DownloadButton((Integer)field_224084_n.get((Integer)lvt_2_1_.getKey() - 1), this.func_224065_a((Integer)lvt_2_1_.getKey()), getLocalizedString("mco.brokenworld.download"));
         }

         if (this.field_224086_p.contains(lvt_2_1_.getKey())) {
            ((RealmsButton)lvt_5_2_).active(false);
            ((RealmsButton)lvt_5_2_).setMessage(getLocalizedString("mco.brokenworld.downloaded"));
         }

         this.buttonsAdd((AbstractRealmsButton)lvt_5_2_);
         this.buttonsAdd(new RealmsButton((Integer)field_224083_m.get((Integer)lvt_2_1_.getKey() - 1), this.func_224065_a((Integer)lvt_2_1_.getKey()), RealmsConstants.func_225109_a(10), 80, 20, getLocalizedString("mco.brokenworld.reset")) {
            public void onPress() {
               int lvt_1_1_ = RealmsBrokenWorldScreen.field_224083_m.indexOf(this.id()) + 1;
               RealmsResetWorldScreen lvt_2_1_ = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.field_224074_d, RealmsBrokenWorldScreen.this);
               if (lvt_1_1_ != RealmsBrokenWorldScreen.this.field_224074_d.activeSlot || RealmsBrokenWorldScreen.this.field_224074_d.worldType.equals(RealmsServer.ServerType.MINIGAME)) {
                  lvt_2_1_.func_224445_b(lvt_1_1_);
               }

               lvt_2_1_.func_224444_a(14);
               Realms.setScreen(lvt_2_1_);
            }
         });
      }

   }

   public void tick() {
      ++this.field_224087_q;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.field_224076_f, this.width() / 2, 17, 16777215);
      String[] lvt_4_1_ = this.field_224077_g.split("\\\\n");

      for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.length; ++lvt_5_1_) {
         this.drawCenteredString(lvt_4_1_[lvt_5_1_], this.width() / 2, RealmsConstants.func_225109_a(-1) + 3 + lvt_5_1_ * 12, 10526880);
      }

      if (this.field_224074_d != null) {
         Iterator var7 = this.field_224074_d.slots.entrySet().iterator();

         while(true) {
            while(var7.hasNext()) {
               Entry<Integer, RealmsWorldOptions> lvt_6_1_ = (Entry)var7.next();
               if (((RealmsWorldOptions)lvt_6_1_.getValue()).templateImage != null && ((RealmsWorldOptions)lvt_6_1_.getValue()).templateId != -1L) {
                  this.func_224053_a(this.func_224065_a((Integer)lvt_6_1_.getKey()), RealmsConstants.func_225109_a(1) + 5, p_render_1_, p_render_2_, this.field_224074_d.activeSlot == (Integer)lvt_6_1_.getKey() && !this.func_224069_f(), ((RealmsWorldOptions)lvt_6_1_.getValue()).getSlotName((Integer)lvt_6_1_.getKey()), (Integer)lvt_6_1_.getKey(), ((RealmsWorldOptions)lvt_6_1_.getValue()).templateId, ((RealmsWorldOptions)lvt_6_1_.getValue()).templateImage, ((RealmsWorldOptions)lvt_6_1_.getValue()).empty);
               } else {
                  this.func_224053_a(this.func_224065_a((Integer)lvt_6_1_.getKey()), RealmsConstants.func_225109_a(1) + 5, p_render_1_, p_render_2_, this.field_224074_d.activeSlot == (Integer)lvt_6_1_.getKey() && !this.func_224069_f(), ((RealmsWorldOptions)lvt_6_1_.getValue()).getSlotName((Integer)lvt_6_1_.getKey()), (Integer)lvt_6_1_.getKey(), -1L, (String)null, ((RealmsWorldOptions)lvt_6_1_.getValue()).empty);
               }
            }

            return;
         }
      }
   }

   private int func_224065_a(int p_224065_1_) {
      return this.field_224078_h + (p_224065_1_ - 1) * 110;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.func_224060_e();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   private void func_224060_e() {
      Realms.setScreen(this.field_224072_b);
   }

   private void func_224068_a(long p_224068_1_) {
      (new Thread(() -> {
         RealmsClient lvt_3_1_ = RealmsClient.func_224911_a();

         try {
            this.field_224074_d = lvt_3_1_.func_224935_a(p_224068_1_);
            this.func_224058_a();
         } catch (RealmsServiceException var5) {
            field_224071_a.error("Couldn't get own world");
            Realms.setScreen(new RealmsGenericErrorScreen(var5.getMessage(), this.field_224072_b));
         } catch (IOException var6) {
            field_224071_a.error("Couldn't parse response getting own world");
         }

      })).start();
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (!p_confirmResult_1_) {
         Realms.setScreen(this);
      } else {
         if (p_confirmResult_2_ != 13 && p_confirmResult_2_ != 14) {
            if (field_224084_n.contains(p_confirmResult_2_)) {
               this.func_224066_b(field_224084_n.indexOf(p_confirmResult_2_) + 1);
            } else if (field_224085_o.contains(p_confirmResult_2_)) {
               this.field_224086_p.add(field_224085_o.indexOf(p_confirmResult_2_) + 1);
               this.childrenClear();
               this.func_224058_a();
            }
         } else {
            (new Thread(() -> {
               RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
               if (this.field_224074_d.state.equals(RealmsServer.Status.CLOSED)) {
                  RealmsTasks.OpenServerTask lvt_2_1_ = new RealmsTasks.OpenServerTask(this.field_224074_d, this, this.field_224072_b, true);
                  RealmsLongRunningMcoTaskScreen lvt_3_1_ = new RealmsLongRunningMcoTaskScreen(this, lvt_2_1_);
                  lvt_3_1_.func_224233_a();
                  Realms.setScreen(lvt_3_1_);
               } else {
                  try {
                     this.field_224073_c.func_223942_f().func_223911_a(lvt_1_1_.func_224935_a(this.field_224075_e), this);
                  } catch (RealmsServiceException var4) {
                     field_224071_a.error("Couldn't get own world");
                     Realms.setScreen(this.field_224072_b);
                  } catch (IOException var5) {
                     field_224071_a.error("Couldn't parse response getting own world");
                     Realms.setScreen(this.field_224072_b);
                  }
               }

            })).start();
         }

      }
   }

   private void func_224066_b(int p_224066_1_) {
      RealmsClient lvt_2_1_ = RealmsClient.func_224911_a();

      try {
         WorldDownload lvt_3_1_ = lvt_2_1_.func_224917_b(this.field_224074_d.id, p_224066_1_);
         RealmsDownloadLatestWorldScreen lvt_4_1_ = new RealmsDownloadLatestWorldScreen(this, lvt_3_1_, this.field_224074_d.name + " (" + ((RealmsWorldOptions)this.field_224074_d.slots.get(p_224066_1_)).getSlotName(p_224066_1_) + ")");
         lvt_4_1_.func_224167_a((Integer)field_224085_o.get(p_224066_1_ - 1));
         Realms.setScreen(lvt_4_1_);
      } catch (RealmsServiceException var5) {
         field_224071_a.error("Couldn't download world data");
         Realms.setScreen(new RealmsGenericErrorScreen(var5, this));
      }

   }

   private boolean func_224069_f() {
      return this.field_224074_d != null && this.field_224074_d.worldType.equals(RealmsServer.ServerType.MINIGAME);
   }

   private void func_224053_a(int p_224053_1_, int p_224053_2_, int p_224053_3_, int p_224053_4_, boolean p_224053_5_, String p_224053_6_, int p_224053_7_, long p_224053_8_, String p_224053_10_, boolean p_224053_11_) {
      if (p_224053_11_) {
         bind("realms:textures/gui/realms/empty_frame.png");
      } else if (p_224053_10_ != null && p_224053_8_ != -1L) {
         RealmsTextureManager.func_225202_a(String.valueOf(p_224053_8_), p_224053_10_);
      } else if (p_224053_7_ == 1) {
         bind("textures/gui/title/background/panorama_0.png");
      } else if (p_224053_7_ == 2) {
         bind("textures/gui/title/background/panorama_2.png");
      } else if (p_224053_7_ == 3) {
         bind("textures/gui/title/background/panorama_3.png");
      } else {
         RealmsTextureManager.func_225202_a(String.valueOf(this.field_224074_d.minigameId), this.field_224074_d.minigameImage);
      }

      if (!p_224053_5_) {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else if (p_224053_5_) {
         float lvt_12_1_ = 0.9F + 0.1F * RealmsMth.cos((float)this.field_224087_q * 0.2F);
         RenderSystem.color4f(lvt_12_1_, lvt_12_1_, lvt_12_1_, 1.0F);
      }

      RealmsScreen.blit(p_224053_1_ + 3, p_224053_2_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      bind("realms:textures/gui/realms/slot_frame.png");
      if (p_224053_5_) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      RealmsScreen.blit(p_224053_1_, p_224053_2_, 0.0F, 0.0F, 80, 80, 80, 80);
      this.drawCenteredString(p_224053_6_, p_224053_1_ + 40, p_224053_2_ + 66, 16777215);
   }

   private void func_224056_c(int p_224056_1_) {
      RealmsTasks.SwitchSlotTask lvt_2_1_ = new RealmsTasks.SwitchSlotTask(this.field_224074_d.id, p_224056_1_, this, 13);
      RealmsLongRunningMcoTaskScreen lvt_3_1_ = new RealmsLongRunningMcoTaskScreen(this.field_224072_b, lvt_2_1_);
      lvt_3_1_.func_224233_a();
      Realms.setScreen(lvt_3_1_);
   }

   @OnlyIn(Dist.CLIENT)
   class DownloadButton extends RealmsButton {
      public DownloadButton(int p_i51634_2_, int p_i51634_3_, String p_i51634_4_) {
         super(p_i51634_2_, p_i51634_3_, RealmsConstants.func_225109_a(8), 80, 20, p_i51634_4_);
      }

      public void onPress() {
         String lvt_1_1_ = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
         String lvt_2_1_ = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(RealmsBrokenWorldScreen.this, RealmsLongConfirmationScreen.Type.Info, lvt_1_1_, lvt_2_1_, true, this.id()));
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PlayButton extends RealmsButton {
      public PlayButton(int p_i51633_2_, int p_i51633_3_, String p_i51633_4_) {
         super(p_i51633_2_, p_i51633_3_, RealmsConstants.func_225109_a(8), 80, 20, p_i51633_4_);
      }

      public void onPress() {
         int lvt_1_1_ = RealmsBrokenWorldScreen.field_224082_l.indexOf(this.id()) + 1;
         if (((RealmsWorldOptions)RealmsBrokenWorldScreen.this.field_224074_d.slots.get(lvt_1_1_)).empty) {
            RealmsResetWorldScreen lvt_2_1_ = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.field_224074_d, RealmsBrokenWorldScreen.this, RealmsScreen.getLocalizedString("mco.configure.world.switch.slot"), RealmsScreen.getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, RealmsScreen.getLocalizedString("gui.cancel"));
            lvt_2_1_.func_224445_b(lvt_1_1_);
            lvt_2_1_.func_224432_a(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
            lvt_2_1_.func_224444_a(14);
            Realms.setScreen(lvt_2_1_);
         } else {
            RealmsBrokenWorldScreen.this.func_224056_c(lvt_1_1_);
         }

      }
   }
}
