package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsConstants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
   private static final Logger field_224547_a = LogManager.getLogger();
   private final RealmsResetWorldScreen field_224548_b;
   private final long field_224549_c;
   private final int field_224550_d;
   private RealmsButton field_224551_e;
   private final DateFormat field_224552_f = new SimpleDateFormat();
   private List<RealmsLevelSummary> field_224553_g = Lists.newArrayList();
   private int field_224554_h = -1;
   private RealmsSelectFileToUploadScreen.WorldSelectionList field_224555_i;
   private String field_224556_j;
   private String field_224557_k;
   private final String[] field_224558_l = new String[4];
   private RealmsLabel field_224559_m;
   private RealmsLabel field_224560_n;
   private RealmsLabel field_224561_o;

   public RealmsSelectFileToUploadScreen(long p_i51754_1_, int p_i51754_3_, RealmsResetWorldScreen p_i51754_4_) {
      this.field_224548_b = p_i51754_4_;
      this.field_224549_c = p_i51754_1_;
      this.field_224550_d = p_i51754_3_;
   }

   private void func_224541_a() throws Exception {
      RealmsAnvilLevelStorageSource lvt_1_1_ = this.getLevelStorageSource();
      this.field_224553_g = lvt_1_1_.getLevelList();
      Collections.sort(this.field_224553_g);
      Iterator var2 = this.field_224553_g.iterator();

      while(var2.hasNext()) {
         RealmsLevelSummary lvt_3_1_ = (RealmsLevelSummary)var2.next();
         this.field_224555_i.func_223881_a(lvt_3_1_);
      }

   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.field_224555_i = new RealmsSelectFileToUploadScreen.WorldSelectionList();

      try {
         this.func_224541_a();
      } catch (Exception var2) {
         field_224547_a.error("Couldn't load level list", var2);
         Realms.setScreen(new RealmsGenericErrorScreen("Unable to load worlds", var2.getMessage(), this.field_224548_b));
         return;
      }

      this.field_224556_j = getLocalizedString("selectWorld.world");
      this.field_224557_k = getLocalizedString("selectWorld.conversion");
      this.field_224558_l[Realms.survivalId()] = getLocalizedString("gameMode.survival");
      this.field_224558_l[Realms.creativeId()] = getLocalizedString("gameMode.creative");
      this.field_224558_l[Realms.adventureId()] = getLocalizedString("gameMode.adventure");
      this.field_224558_l[Realms.spectatorId()] = getLocalizedString("gameMode.spectator");
      this.addWidget(this.field_224555_i);
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 6, this.height() - 32, 153, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsSelectFileToUploadScreen.this.field_224548_b);
         }
      });
      this.buttonsAdd(this.field_224551_e = new RealmsButton(2, this.width() / 2 - 154, this.height() - 32, 153, 20, getLocalizedString("mco.upload.button.name")) {
         public void onPress() {
            RealmsSelectFileToUploadScreen.this.func_224544_b();
         }
      });
      this.field_224551_e.active(this.field_224554_h >= 0 && this.field_224554_h < this.field_224553_g.size());
      this.addWidget(this.field_224559_m = new RealmsLabel(getLocalizedString("mco.upload.select.world.title"), this.width() / 2, 13, 16777215));
      this.addWidget(this.field_224560_n = new RealmsLabel(getLocalizedString("mco.upload.select.world.subtitle"), this.width() / 2, RealmsConstants.func_225109_a(-1), 10526880));
      if (this.field_224553_g.isEmpty()) {
         this.addWidget(this.field_224561_o = new RealmsLabel(getLocalizedString("mco.upload.select.world.none"), this.width() / 2, this.height() / 2 - 20, 16777215));
      } else {
         this.field_224561_o = null;
      }

      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   private void func_224544_b() {
      if (this.field_224554_h != -1 && !((RealmsLevelSummary)this.field_224553_g.get(this.field_224554_h)).isHardcore()) {
         RealmsLevelSummary lvt_1_1_ = (RealmsLevelSummary)this.field_224553_g.get(this.field_224554_h);
         Realms.setScreen(new RealmsUploadScreen(this.field_224549_c, this.field_224550_d, this.field_224548_b, lvt_1_1_));
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.field_224555_i.render(p_render_1_, p_render_2_, p_render_3_);
      this.field_224559_m.render(this);
      this.field_224560_n.render(this);
      if (this.field_224561_o != null) {
         this.field_224561_o.render(this);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.field_224548_b);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void tick() {
      super.tick();
   }

   private String func_224532_a(RealmsLevelSummary p_224532_1_) {
      return this.field_224558_l[p_224532_1_.getGameMode()];
   }

   private String func_224533_b(RealmsLevelSummary p_224533_1_) {
      return this.field_224552_f.format(new Date(p_224533_1_.getLastPlayed()));
   }

   @OnlyIn(Dist.CLIENT)
   class WorldSelectionEntry extends RealmListEntry {
      final RealmsLevelSummary field_223759_a;

      public WorldSelectionEntry(RealmsLevelSummary p_i51738_2_) {
         this.field_223759_a = p_i51738_2_;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.func_223758_a(this.field_223759_a, p_render_1_, p_render_3_, p_render_2_, p_render_5_, Tezzelator.instance, p_render_6_, p_render_7_);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         RealmsSelectFileToUploadScreen.this.field_224555_i.selectItem(RealmsSelectFileToUploadScreen.this.field_224553_g.indexOf(this.field_223759_a));
         return true;
      }

      protected void func_223758_a(RealmsLevelSummary p_223758_1_, int p_223758_2_, int p_223758_3_, int p_223758_4_, int p_223758_5_, Tezzelator p_223758_6_, int p_223758_7_, int p_223758_8_) {
         String lvt_9_1_ = p_223758_1_.getLevelName();
         if (lvt_9_1_ == null || lvt_9_1_.isEmpty()) {
            lvt_9_1_ = RealmsSelectFileToUploadScreen.this.field_224556_j + " " + (p_223758_2_ + 1);
         }

         String lvt_10_1_ = p_223758_1_.getLevelId();
         lvt_10_1_ = lvt_10_1_ + " (" + RealmsSelectFileToUploadScreen.this.func_224533_b(p_223758_1_);
         lvt_10_1_ = lvt_10_1_ + ")";
         String lvt_11_1_ = "";
         if (p_223758_1_.isRequiresConversion()) {
            lvt_11_1_ = RealmsSelectFileToUploadScreen.this.field_224557_k + " " + lvt_11_1_;
         } else {
            lvt_11_1_ = RealmsSelectFileToUploadScreen.this.func_224532_a(p_223758_1_);
            if (p_223758_1_.isHardcore()) {
               lvt_11_1_ = ChatFormatting.DARK_RED + RealmsScreen.getLocalizedString("mco.upload.hardcore") + ChatFormatting.RESET;
            }

            if (p_223758_1_.hasCheats()) {
               lvt_11_1_ = lvt_11_1_ + ", " + RealmsScreen.getLocalizedString("selectWorld.cheats");
            }
         }

         RealmsSelectFileToUploadScreen.this.drawString(lvt_9_1_, p_223758_3_ + 2, p_223758_4_ + 1, 16777215);
         RealmsSelectFileToUploadScreen.this.drawString(lvt_10_1_, p_223758_3_ + 2, p_223758_4_ + 12, 8421504);
         RealmsSelectFileToUploadScreen.this.drawString(lvt_11_1_, p_223758_3_ + 2, p_223758_4_ + 12 + 10, 8421504);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldSelectionList extends RealmsObjectSelectionList {
      public WorldSelectionList() {
         super(RealmsSelectFileToUploadScreen.this.width(), RealmsSelectFileToUploadScreen.this.height(), RealmsConstants.func_225109_a(0), RealmsSelectFileToUploadScreen.this.height() - 40, 36);
      }

      public void func_223881_a(RealmsLevelSummary p_223881_1_) {
         this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldSelectionEntry(p_223881_1_));
      }

      public int getItemCount() {
         return RealmsSelectFileToUploadScreen.this.field_224553_g.size();
      }

      public int getMaxPosition() {
         return RealmsSelectFileToUploadScreen.this.field_224553_g.size() * 36;
      }

      public boolean isFocused() {
         return RealmsSelectFileToUploadScreen.this.isFocused(this);
      }

      public void renderBackground() {
         RealmsSelectFileToUploadScreen.this.renderBackground();
      }

      public void selectItem(int p_selectItem_1_) {
         this.setSelected(p_selectItem_1_);
         if (p_selectItem_1_ != -1) {
            RealmsLevelSummary lvt_2_1_ = (RealmsLevelSummary)RealmsSelectFileToUploadScreen.this.field_224553_g.get(p_selectItem_1_);
            String lvt_3_1_ = RealmsScreen.getLocalizedString("narrator.select.list.position", p_selectItem_1_ + 1, RealmsSelectFileToUploadScreen.this.field_224553_g.size());
            String lvt_4_1_ = Realms.joinNarrations(Arrays.asList(lvt_2_1_.getLevelName(), RealmsSelectFileToUploadScreen.this.func_224533_b(lvt_2_1_), RealmsSelectFileToUploadScreen.this.func_224532_a(lvt_2_1_), lvt_3_1_));
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", lvt_4_1_));
         }

         RealmsSelectFileToUploadScreen.this.field_224554_h = p_selectItem_1_;
         RealmsSelectFileToUploadScreen.this.field_224551_e.active(RealmsSelectFileToUploadScreen.this.field_224554_h >= 0 && RealmsSelectFileToUploadScreen.this.field_224554_h < this.getItemCount() && !((RealmsLevelSummary)RealmsSelectFileToUploadScreen.this.field_224553_g.get(RealmsSelectFileToUploadScreen.this.field_224554_h)).isHardcore());
      }
   }
}
