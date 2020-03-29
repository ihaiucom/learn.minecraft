package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSubscriptionInfoScreen extends RealmsScreen {
   private static final Logger field_224579_a = LogManager.getLogger();
   private final RealmsScreen field_224580_b;
   private final RealmsServer field_224581_c;
   private final RealmsScreen field_224582_d;
   private final int field_224583_e = 0;
   private final int field_224584_f = 1;
   private final int field_224585_g = 2;
   private final String field_224586_h;
   private final String field_224587_i;
   private final String field_224588_j;
   private final String field_224589_k;
   private int field_224590_l;
   private String field_224591_m;
   private Subscription.Type field_224592_n;
   private final String field_224593_o = "https://aka.ms/ExtendJavaRealms";

   public RealmsSubscriptionInfoScreen(RealmsScreen p_i51749_1_, RealmsServer p_i51749_2_, RealmsScreen p_i51749_3_) {
      this.field_224580_b = p_i51749_1_;
      this.field_224581_c = p_i51749_2_;
      this.field_224582_d = p_i51749_3_;
      this.field_224586_h = getLocalizedString("mco.configure.world.subscription.title");
      this.field_224587_i = getLocalizedString("mco.configure.world.subscription.start");
      this.field_224588_j = getLocalizedString("mco.configure.world.subscription.timeleft");
      this.field_224589_k = getLocalizedString("mco.configure.world.subscription.recurring.daysleft");
   }

   public void init() {
      this.func_224573_a(this.field_224581_c.id);
      Realms.narrateNow(this.field_224586_h, this.field_224587_i, this.field_224591_m, this.field_224588_j, this.func_224576_a(this.field_224590_l));
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(2, this.width() / 2 - 100, RealmsConstants.func_225109_a(6), getLocalizedString("mco.configure.world.subscription.extend")) {
         public void onPress() {
            String lvt_1_1_ = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + RealmsSubscriptionInfoScreen.this.field_224581_c.remoteSubscriptionId + "&profileId=" + Realms.getUUID();
            Realms.setClipboard(lvt_1_1_);
            RealmsUtil.func_225190_c(lvt_1_1_);
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.func_225109_a(12), getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsSubscriptionInfoScreen.this.field_224580_b);
         }
      });
      if (this.field_224581_c.expired) {
         this.buttonsAdd(new RealmsButton(1, this.width() / 2 - 100, RealmsConstants.func_225109_a(10), getLocalizedString("mco.configure.world.delete.button")) {
            public void onPress() {
               String lvt_1_1_ = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line1");
               String lvt_2_1_ = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line2");
               Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSubscriptionInfoScreen.this, RealmsLongConfirmationScreen.Type.Warning, lvt_1_1_, lvt_2_1_, true, 1));
            }
         });
      }

   }

   private void func_224573_a(long p_224573_1_) {
      RealmsClient lvt_3_1_ = RealmsClient.func_224911_a();

      try {
         Subscription lvt_4_1_ = lvt_3_1_.func_224933_g(p_224573_1_);
         this.field_224590_l = lvt_4_1_.daysLeft;
         this.field_224591_m = this.func_224574_b(lvt_4_1_.startDate);
         this.field_224592_n = lvt_4_1_.type;
      } catch (RealmsServiceException var5) {
         field_224579_a.error("Couldn't get subscription");
         Realms.setScreen(new RealmsGenericErrorScreen(var5, this.field_224580_b));
      } catch (IOException var6) {
         field_224579_a.error("Couldn't parse response subscribing");
      }

   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_2_ == 1 && p_confirmResult_1_) {
         (new Thread("Realms-delete-realm") {
            public void run() {
               try {
                  RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
                  lvt_1_1_.func_224916_h(RealmsSubscriptionInfoScreen.this.field_224581_c.id);
               } catch (RealmsServiceException var2) {
                  RealmsSubscriptionInfoScreen.field_224579_a.error("Couldn't delete world");
                  RealmsSubscriptionInfoScreen.field_224579_a.error(var2);
               } catch (IOException var3) {
                  RealmsSubscriptionInfoScreen.field_224579_a.error("Couldn't delete world");
                  var3.printStackTrace();
               }

               Realms.setScreen(RealmsSubscriptionInfoScreen.this.field_224582_d);
            }
         }).start();
      }

      Realms.setScreen(this);
   }

   private String func_224574_b(long p_224574_1_) {
      Calendar lvt_3_1_ = new GregorianCalendar(TimeZone.getDefault());
      lvt_3_1_.setTimeInMillis(p_224574_1_);
      return DateFormat.getDateTimeInstance().format(lvt_3_1_.getTime());
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         Realms.setScreen(this.field_224580_b);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      int lvt_4_1_ = this.width() / 2 - 100;
      this.drawCenteredString(this.field_224586_h, this.width() / 2, 17, 16777215);
      this.drawString(this.field_224587_i, lvt_4_1_, RealmsConstants.func_225109_a(0), 10526880);
      this.drawString(this.field_224591_m, lvt_4_1_, RealmsConstants.func_225109_a(1), 16777215);
      if (this.field_224592_n == Subscription.Type.NORMAL) {
         this.drawString(this.field_224588_j, lvt_4_1_, RealmsConstants.func_225109_a(3), 10526880);
      } else if (this.field_224592_n == Subscription.Type.RECURRING) {
         this.drawString(this.field_224589_k, lvt_4_1_, RealmsConstants.func_225109_a(3), 10526880);
      }

      this.drawString(this.func_224576_a(this.field_224590_l), lvt_4_1_, RealmsConstants.func_225109_a(4), 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String func_224576_a(int p_224576_1_) {
      if (p_224576_1_ == -1 && this.field_224581_c.expired) {
         return getLocalizedString("mco.configure.world.subscription.expired");
      } else if (p_224576_1_ <= 1) {
         return getLocalizedString("mco.configure.world.subscription.less_than_a_day");
      } else {
         int lvt_2_1_ = p_224576_1_ / 30;
         int lvt_3_1_ = p_224576_1_ % 30;
         StringBuilder lvt_4_1_ = new StringBuilder();
         if (lvt_2_1_ > 0) {
            lvt_4_1_.append(lvt_2_1_).append(" ");
            if (lvt_2_1_ == 1) {
               lvt_4_1_.append(getLocalizedString("mco.configure.world.subscription.month").toLowerCase(Locale.ROOT));
            } else {
               lvt_4_1_.append(getLocalizedString("mco.configure.world.subscription.months").toLowerCase(Locale.ROOT));
            }
         }

         if (lvt_3_1_ > 0) {
            if (lvt_4_1_.length() > 0) {
               lvt_4_1_.append(", ");
            }

            lvt_4_1_.append(lvt_3_1_).append(" ");
            if (lvt_3_1_ == 1) {
               lvt_4_1_.append(getLocalizedString("mco.configure.world.subscription.day").toLowerCase(Locale.ROOT));
            } else {
               lvt_4_1_.append(getLocalizedString("mco.configure.world.subscription.days").toLowerCase(Locale.ROOT));
            }
         }

         return lvt_4_1_.toString();
      }
   }
}
