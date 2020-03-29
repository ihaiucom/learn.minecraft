package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NewChatGui extends AbstractGui {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final List<String> sentMessages = Lists.newArrayList();
   private final List<ChatLine> chatLines = Lists.newArrayList();
   private final List<ChatLine> drawnChatLines = Lists.newArrayList();
   private int scrollPos;
   private boolean isScrolled;

   public NewChatGui(Minecraft p_i1022_1_) {
      this.mc = p_i1022_1_;
   }

   public void render(int p_146230_1_) {
      if (this.func_228091_i_()) {
         int lvt_2_1_ = this.getLineCount();
         int lvt_3_1_ = this.drawnChatLines.size();
         if (lvt_3_1_ > 0) {
            boolean lvt_4_1_ = false;
            if (this.getChatOpen()) {
               lvt_4_1_ = true;
            }

            double lvt_5_1_ = this.getScale();
            int lvt_7_1_ = MathHelper.ceil((double)this.getChatWidth() / lvt_5_1_);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(2.0F, 8.0F, 0.0F);
            RenderSystem.scaled(lvt_5_1_, lvt_5_1_, 1.0D);
            double lvt_8_1_ = this.mc.gameSettings.chatOpacity * 0.8999999761581421D + 0.10000000149011612D;
            double lvt_10_1_ = this.mc.gameSettings.accessibilityTextBackgroundOpacity;
            int lvt_12_1_ = 0;
            Matrix4f lvt_13_1_ = Matrix4f.func_226599_b_(0.0F, 0.0F, -100.0F);

            int lvt_16_1_;
            int lvt_19_1_;
            int lvt_20_1_;
            for(int lvt_14_1_ = 0; lvt_14_1_ + this.scrollPos < this.drawnChatLines.size() && lvt_14_1_ < lvt_2_1_; ++lvt_14_1_) {
               ChatLine lvt_15_1_ = (ChatLine)this.drawnChatLines.get(lvt_14_1_ + this.scrollPos);
               if (lvt_15_1_ != null) {
                  lvt_16_1_ = p_146230_1_ - lvt_15_1_.getUpdatedCounter();
                  if (lvt_16_1_ < 200 || lvt_4_1_) {
                     double lvt_17_1_ = lvt_4_1_ ? 1.0D : func_212915_c(lvt_16_1_);
                     lvt_19_1_ = (int)(255.0D * lvt_17_1_ * lvt_8_1_);
                     lvt_20_1_ = (int)(255.0D * lvt_17_1_ * lvt_10_1_);
                     ++lvt_12_1_;
                     if (lvt_19_1_ > 3) {
                        int lvt_21_1_ = false;
                        int lvt_22_1_ = -lvt_14_1_ * 9;
                        fill(lvt_13_1_, -2, lvt_22_1_ - 9, 0 + lvt_7_1_ + 4, lvt_22_1_, lvt_20_1_ << 24);
                        String lvt_23_1_ = lvt_15_1_.getChatComponent().getFormattedText();
                        RenderSystem.enableBlend();
                        this.mc.fontRenderer.drawStringWithShadow(lvt_23_1_, 0.0F, (float)(lvt_22_1_ - 8), 16777215 + (lvt_19_1_ << 24));
                        RenderSystem.disableAlphaTest();
                        RenderSystem.disableBlend();
                     }
                  }
               }
            }

            if (lvt_4_1_) {
               this.mc.fontRenderer.getClass();
               int lvt_14_2_ = 9;
               RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
               int lvt_15_2_ = lvt_3_1_ * lvt_14_2_ + lvt_3_1_;
               lvt_16_1_ = lvt_12_1_ * lvt_14_2_ + lvt_12_1_;
               int lvt_17_2_ = this.scrollPos * lvt_16_1_ / lvt_3_1_;
               int lvt_18_1_ = lvt_16_1_ * lvt_16_1_ / lvt_15_2_;
               if (lvt_15_2_ != lvt_16_1_) {
                  lvt_19_1_ = lvt_17_2_ > 0 ? 170 : 96;
                  lvt_20_1_ = this.isScrolled ? 13382451 : 3355562;
                  fill(0, -lvt_17_2_, 2, -lvt_17_2_ - lvt_18_1_, lvt_20_1_ + (lvt_19_1_ << 24));
                  fill(2, -lvt_17_2_, 1, -lvt_17_2_ - lvt_18_1_, 13421772 + (lvt_19_1_ << 24));
               }
            }

            RenderSystem.popMatrix();
         }
      }
   }

   private boolean func_228091_i_() {
      return this.mc.gameSettings.chatVisibility != ChatVisibility.HIDDEN;
   }

   private static double func_212915_c(int p_212915_0_) {
      double lvt_1_1_ = (double)p_212915_0_ / 200.0D;
      lvt_1_1_ = 1.0D - lvt_1_1_;
      lvt_1_1_ *= 10.0D;
      lvt_1_1_ = MathHelper.clamp(lvt_1_1_, 0.0D, 1.0D);
      lvt_1_1_ *= lvt_1_1_;
      return lvt_1_1_;
   }

   public void clearChatMessages(boolean p_146231_1_) {
      this.drawnChatLines.clear();
      this.chatLines.clear();
      if (p_146231_1_) {
         this.sentMessages.clear();
      }

   }

   public void printChatMessage(ITextComponent p_146227_1_) {
      this.printChatMessageWithOptionalDeletion(p_146227_1_, 0);
   }

   public void printChatMessageWithOptionalDeletion(ITextComponent p_146234_1_, int p_146234_2_) {
      this.setChatLine(p_146234_1_, p_146234_2_, this.mc.ingameGUI.getTicks(), false);
      LOGGER.info("[CHAT] {}", p_146234_1_.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
   }

   private void setChatLine(ITextComponent p_146237_1_, int p_146237_2_, int p_146237_3_, boolean p_146237_4_) {
      if (p_146237_2_ != 0) {
         this.deleteChatLine(p_146237_2_);
      }

      int lvt_5_1_ = MathHelper.floor((double)this.getChatWidth() / this.getScale());
      List<ITextComponent> lvt_6_1_ = RenderComponentsUtil.splitText(p_146237_1_, lvt_5_1_, this.mc.fontRenderer, false, false);
      boolean lvt_7_1_ = this.getChatOpen();

      ITextComponent lvt_9_1_;
      for(Iterator var8 = lvt_6_1_.iterator(); var8.hasNext(); this.drawnChatLines.add(0, new ChatLine(p_146237_3_, lvt_9_1_, p_146237_2_))) {
         lvt_9_1_ = (ITextComponent)var8.next();
         if (lvt_7_1_ && this.scrollPos > 0) {
            this.isScrolled = true;
            this.func_194813_a(1.0D);
         }
      }

      while(this.drawnChatLines.size() > 100) {
         this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
      }

      if (!p_146237_4_) {
         this.chatLines.add(0, new ChatLine(p_146237_3_, p_146237_1_, p_146237_2_));

         while(this.chatLines.size() > 100) {
            this.chatLines.remove(this.chatLines.size() - 1);
         }
      }

   }

   public void refreshChat() {
      this.drawnChatLines.clear();
      this.resetScroll();

      for(int lvt_1_1_ = this.chatLines.size() - 1; lvt_1_1_ >= 0; --lvt_1_1_) {
         ChatLine lvt_2_1_ = (ChatLine)this.chatLines.get(lvt_1_1_);
         this.setChatLine(lvt_2_1_.getChatComponent(), lvt_2_1_.getChatLineID(), lvt_2_1_.getUpdatedCounter(), true);
      }

   }

   public List<String> getSentMessages() {
      return this.sentMessages;
   }

   public void addToSentMessages(String p_146239_1_) {
      if (this.sentMessages.isEmpty() || !((String)this.sentMessages.get(this.sentMessages.size() - 1)).equals(p_146239_1_)) {
         this.sentMessages.add(p_146239_1_);
      }

   }

   public void resetScroll() {
      this.scrollPos = 0;
      this.isScrolled = false;
   }

   public void func_194813_a(double p_194813_1_) {
      this.scrollPos = (int)((double)this.scrollPos + p_194813_1_);
      int lvt_3_1_ = this.drawnChatLines.size();
      if (this.scrollPos > lvt_3_1_ - this.getLineCount()) {
         this.scrollPos = lvt_3_1_ - this.getLineCount();
      }

      if (this.scrollPos <= 0) {
         this.scrollPos = 0;
         this.isScrolled = false;
      }

   }

   @Nullable
   public ITextComponent getTextComponent(double p_194817_1_, double p_194817_3_) {
      if (this.getChatOpen() && !this.mc.gameSettings.hideGUI && this.func_228091_i_()) {
         double lvt_5_1_ = this.getScale();
         double lvt_7_1_ = p_194817_1_ - 2.0D;
         double lvt_9_1_ = (double)this.mc.func_228018_at_().getScaledHeight() - p_194817_3_ - 40.0D;
         lvt_7_1_ = (double)MathHelper.floor(lvt_7_1_ / lvt_5_1_);
         lvt_9_1_ = (double)MathHelper.floor(lvt_9_1_ / lvt_5_1_);
         if (lvt_7_1_ >= 0.0D && lvt_9_1_ >= 0.0D) {
            int lvt_11_1_ = Math.min(this.getLineCount(), this.drawnChatLines.size());
            if (lvt_7_1_ <= (double)MathHelper.floor((double)this.getChatWidth() / this.getScale())) {
               this.mc.fontRenderer.getClass();
               if (lvt_9_1_ < (double)(9 * lvt_11_1_ + lvt_11_1_)) {
                  this.mc.fontRenderer.getClass();
                  int lvt_12_1_ = (int)(lvt_9_1_ / 9.0D + (double)this.scrollPos);
                  if (lvt_12_1_ >= 0 && lvt_12_1_ < this.drawnChatLines.size()) {
                     ChatLine lvt_13_1_ = (ChatLine)this.drawnChatLines.get(lvt_12_1_);
                     int lvt_14_1_ = 0;
                     Iterator var15 = lvt_13_1_.getChatComponent().iterator();

                     while(var15.hasNext()) {
                        ITextComponent lvt_16_1_ = (ITextComponent)var15.next();
                        if (lvt_16_1_ instanceof StringTextComponent) {
                           lvt_14_1_ += this.mc.fontRenderer.getStringWidth(RenderComponentsUtil.removeTextColorsIfConfigured(((StringTextComponent)lvt_16_1_).getText(), false));
                           if ((double)lvt_14_1_ > lvt_7_1_) {
                              return lvt_16_1_;
                           }
                        }
                     }
                  }

                  return null;
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean getChatOpen() {
      return this.mc.currentScreen instanceof ChatScreen;
   }

   public void deleteChatLine(int p_146242_1_) {
      Iterator lvt_2_1_ = this.drawnChatLines.iterator();

      ChatLine lvt_3_2_;
      while(lvt_2_1_.hasNext()) {
         lvt_3_2_ = (ChatLine)lvt_2_1_.next();
         if (lvt_3_2_.getChatLineID() == p_146242_1_) {
            lvt_2_1_.remove();
         }
      }

      lvt_2_1_ = this.chatLines.iterator();

      while(lvt_2_1_.hasNext()) {
         lvt_3_2_ = (ChatLine)lvt_2_1_.next();
         if (lvt_3_2_.getChatLineID() == p_146242_1_) {
            lvt_2_1_.remove();
            break;
         }
      }

   }

   public int getChatWidth() {
      return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
   }

   public int getChatHeight() {
      return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
   }

   public double getScale() {
      return this.mc.gameSettings.chatScale;
   }

   public static int calculateChatboxWidth(double p_194814_0_) {
      int lvt_2_1_ = true;
      int lvt_3_1_ = true;
      return MathHelper.floor(p_194814_0_ * 280.0D + 40.0D);
   }

   public static int calculateChatboxHeight(double p_194816_0_) {
      int lvt_2_1_ = true;
      int lvt_3_1_ = true;
      return MathHelper.floor(p_194816_0_ * 160.0D + 20.0D);
   }

   public int getLineCount() {
      return this.getChatHeight() / 9;
   }
}
