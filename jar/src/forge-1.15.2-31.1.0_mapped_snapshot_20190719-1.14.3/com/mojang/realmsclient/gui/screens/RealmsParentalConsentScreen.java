package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Iterator;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsParentalConsentScreen extends RealmsScreen {
   private final RealmsScreen field_224260_a;

   public RealmsParentalConsentScreen(RealmsScreen p_i51762_1_) {
      this.field_224260_a = p_i51762_1_;
   }

   public void init() {
      Realms.narrateNow(getLocalizedString("mco.account.privacyinfo"));
      String lvt_1_1_ = getLocalizedString("mco.account.update");
      String lvt_2_1_ = getLocalizedString("gui.back");
      int lvt_3_1_ = Math.max(this.fontWidth(lvt_1_1_), this.fontWidth(lvt_2_1_)) + 30;
      String lvt_4_1_ = getLocalizedString("mco.account.privacy.info");
      int lvt_5_1_ = (int)((double)this.fontWidth(lvt_4_1_) * 1.2D);
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 - lvt_5_1_ / 2, RealmsConstants.func_225109_a(11), lvt_5_1_, 20, lvt_4_1_) {
         public void onPress() {
            RealmsUtil.func_225190_c("https://minecraft.net/privacy/gdpr/");
         }
      });
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 - (lvt_3_1_ + 5), RealmsConstants.func_225109_a(13), lvt_3_1_, 20, lvt_1_1_) {
         public void onPress() {
            RealmsUtil.func_225190_c("https://minecraft.net/update-account");
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 5, RealmsConstants.func_225109_a(13), lvt_3_1_, 20, lvt_2_1_) {
         public void onPress() {
            Realms.setScreen(RealmsParentalConsentScreen.this.field_224260_a);
         }
      });
   }

   public void tick() {
      super.tick();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      List<String> lvt_4_1_ = this.getLocalizedStringWithLineWidth("mco.account.privacyinfo", (int)Math.round((double)this.width() * 0.9D));
      int lvt_5_1_ = 15;

      for(Iterator var6 = lvt_4_1_.iterator(); var6.hasNext(); lvt_5_1_ += 15) {
         String lvt_7_1_ = (String)var6.next();
         this.drawCenteredString(lvt_7_1_, this.width() / 2, lvt_5_1_, 16777215);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
