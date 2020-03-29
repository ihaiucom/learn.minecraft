package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectatorGui extends AbstractGui implements ISpectatorMenuRecipient {
   private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation SPECTATOR_WIDGETS = new ResourceLocation("textures/gui/spectator_widgets.png");
   private final Minecraft mc;
   private long lastSelectionTime;
   private SpectatorMenu menu;

   public SpectatorGui(Minecraft p_i45527_1_) {
      this.mc = p_i45527_1_;
   }

   public void onHotbarSelected(int p_175260_1_) {
      this.lastSelectionTime = Util.milliTime();
      if (this.menu != null) {
         this.menu.selectSlot(p_175260_1_);
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }

   private float getHotbarAlpha() {
      long lvt_1_1_ = this.lastSelectionTime - Util.milliTime() + 5000L;
      return MathHelper.clamp((float)lvt_1_1_ / 2000.0F, 0.0F, 1.0F);
   }

   public void renderTooltip(float p_195622_1_) {
      if (this.menu != null) {
         float lvt_2_1_ = this.getHotbarAlpha();
         if (lvt_2_1_ <= 0.0F) {
            this.menu.exit();
         } else {
            int lvt_3_1_ = this.mc.func_228018_at_().getScaledWidth() / 2;
            int lvt_4_1_ = this.getBlitOffset();
            this.setBlitOffset(-90);
            int lvt_5_1_ = MathHelper.floor((float)this.mc.func_228018_at_().getScaledHeight() - 22.0F * lvt_2_1_);
            SpectatorDetails lvt_6_1_ = this.menu.getCurrentPage();
            this.func_214456_a(lvt_2_1_, lvt_3_1_, lvt_5_1_, lvt_6_1_);
            this.setBlitOffset(lvt_4_1_);
         }
      }
   }

   protected void func_214456_a(float p_214456_1_, int p_214456_2_, int p_214456_3_, SpectatorDetails p_214456_4_) {
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, p_214456_1_);
      this.mc.getTextureManager().bindTexture(WIDGETS);
      this.blit(p_214456_2_ - 91, p_214456_3_, 0, 0, 182, 22);
      if (p_214456_4_.getSelectedSlot() >= 0) {
         this.blit(p_214456_2_ - 91 - 1 + p_214456_4_.getSelectedSlot() * 20, p_214456_3_ - 1, 0, 22, 24, 22);
      }

      for(int lvt_5_1_ = 0; lvt_5_1_ < 9; ++lvt_5_1_) {
         this.renderSlot(lvt_5_1_, this.mc.func_228018_at_().getScaledWidth() / 2 - 90 + lvt_5_1_ * 20 + 2, (float)(p_214456_3_ + 3), p_214456_1_, p_214456_4_.getObject(lvt_5_1_));
      }

      RenderSystem.disableRescaleNormal();
      RenderSystem.disableBlend();
   }

   private void renderSlot(int p_175266_1_, int p_175266_2_, float p_175266_3_, float p_175266_4_, ISpectatorMenuObject p_175266_5_) {
      this.mc.getTextureManager().bindTexture(SPECTATOR_WIDGETS);
      if (p_175266_5_ != SpectatorMenu.EMPTY_SLOT) {
         int lvt_6_1_ = (int)(p_175266_4_ * 255.0F);
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)p_175266_2_, p_175266_3_, 0.0F);
         float lvt_7_1_ = p_175266_5_.isEnabled() ? 1.0F : 0.25F;
         RenderSystem.color4f(lvt_7_1_, lvt_7_1_, lvt_7_1_, p_175266_4_);
         p_175266_5_.renderIcon(lvt_7_1_, lvt_6_1_);
         RenderSystem.popMatrix();
         String lvt_8_1_ = String.valueOf(this.mc.gameSettings.keyBindsHotbar[p_175266_1_].getLocalizedName());
         if (lvt_6_1_ > 3 && p_175266_5_.isEnabled()) {
            this.mc.fontRenderer.drawStringWithShadow(lvt_8_1_, (float)(p_175266_2_ + 19 - 2 - this.mc.fontRenderer.getStringWidth(lvt_8_1_)), p_175266_3_ + 6.0F + 3.0F, 16777215 + (lvt_6_1_ << 24));
         }
      }

   }

   public void renderSelectedItem() {
      int lvt_1_1_ = (int)(this.getHotbarAlpha() * 255.0F);
      if (lvt_1_1_ > 3 && this.menu != null) {
         ISpectatorMenuObject lvt_2_1_ = this.menu.getSelectedItem();
         String lvt_3_1_ = lvt_2_1_ == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt().getFormattedText() : lvt_2_1_.getSpectatorName().getFormattedText();
         if (lvt_3_1_ != null) {
            int lvt_4_1_ = (this.mc.func_228018_at_().getScaledWidth() - this.mc.fontRenderer.getStringWidth(lvt_3_1_)) / 2;
            int lvt_5_1_ = this.mc.func_228018_at_().getScaledHeight() - 35;
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.mc.fontRenderer.drawStringWithShadow(lvt_3_1_, (float)lvt_4_1_, (float)lvt_5_1_, 16777215 + (lvt_1_1_ << 24));
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
         }
      }

   }

   public void onSpectatorMenuClosed(SpectatorMenu p_175257_1_) {
      this.menu = null;
      this.lastSelectionTime = 0L;
   }

   public boolean isMenuActive() {
      return this.menu != null;
   }

   public void onMouseScroll(double p_195621_1_) {
      int lvt_3_1_;
      for(lvt_3_1_ = this.menu.getSelectedSlot() + (int)p_195621_1_; lvt_3_1_ >= 0 && lvt_3_1_ <= 8 && (this.menu.getItem(lvt_3_1_) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(lvt_3_1_).isEnabled()); lvt_3_1_ = (int)((double)lvt_3_1_ + p_195621_1_)) {
      }

      if (lvt_3_1_ >= 0 && lvt_3_1_ <= 8) {
         this.menu.selectSlot(lvt_3_1_);
         this.lastSelectionTime = Util.milliTime();
      }

   }

   public void onMiddleClick() {
      this.lastSelectionTime = Util.milliTime();
      if (this.isMenuActive()) {
         int lvt_1_1_ = this.menu.getSelectedSlot();
         if (lvt_1_1_ != -1) {
            this.menu.selectSlot(lvt_1_1_);
         }
      } else {
         this.menu = new SpectatorMenu(this);
      }

   }
}
