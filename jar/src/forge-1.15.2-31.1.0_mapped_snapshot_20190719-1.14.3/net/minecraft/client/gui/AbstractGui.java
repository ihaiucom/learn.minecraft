package net.minecraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGui {
   public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
   private int blitOffset;

   protected void hLine(int p_hLine_1_, int p_hLine_2_, int p_hLine_3_, int p_hLine_4_) {
      if (p_hLine_2_ < p_hLine_1_) {
         int lvt_5_1_ = p_hLine_1_;
         p_hLine_1_ = p_hLine_2_;
         p_hLine_2_ = lvt_5_1_;
      }

      fill(p_hLine_1_, p_hLine_3_, p_hLine_2_ + 1, p_hLine_3_ + 1, p_hLine_4_);
   }

   protected void vLine(int p_vLine_1_, int p_vLine_2_, int p_vLine_3_, int p_vLine_4_) {
      if (p_vLine_3_ < p_vLine_2_) {
         int lvt_5_1_ = p_vLine_2_;
         p_vLine_2_ = p_vLine_3_;
         p_vLine_3_ = lvt_5_1_;
      }

      fill(p_vLine_1_, p_vLine_2_ + 1, p_vLine_1_ + 1, p_vLine_3_, p_vLine_4_);
   }

   public static void fill(int p_fill_0_, int p_fill_1_, int p_fill_2_, int p_fill_3_, int p_fill_4_) {
      fill(TransformationMatrix.func_227983_a_().func_227988_c_(), p_fill_0_, p_fill_1_, p_fill_2_, p_fill_3_, p_fill_4_);
   }

   public static void fill(Matrix4f p_fill_0_, int p_fill_1_, int p_fill_2_, int p_fill_3_, int p_fill_4_, int p_fill_5_) {
      int lvt_6_2_;
      if (p_fill_1_ < p_fill_3_) {
         lvt_6_2_ = p_fill_1_;
         p_fill_1_ = p_fill_3_;
         p_fill_3_ = lvt_6_2_;
      }

      if (p_fill_2_ < p_fill_4_) {
         lvt_6_2_ = p_fill_2_;
         p_fill_2_ = p_fill_4_;
         p_fill_4_ = lvt_6_2_;
      }

      float lvt_6_3_ = (float)(p_fill_5_ >> 24 & 255) / 255.0F;
      float lvt_7_1_ = (float)(p_fill_5_ >> 16 & 255) / 255.0F;
      float lvt_8_1_ = (float)(p_fill_5_ >> 8 & 255) / 255.0F;
      float lvt_9_1_ = (float)(p_fill_5_ & 255) / 255.0F;
      BufferBuilder lvt_10_1_ = Tessellator.getInstance().getBuffer();
      RenderSystem.enableBlend();
      RenderSystem.disableTexture();
      RenderSystem.defaultBlendFunc();
      lvt_10_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
      lvt_10_1_.func_227888_a_(p_fill_0_, (float)p_fill_1_, (float)p_fill_4_, 0.0F).func_227885_a_(lvt_7_1_, lvt_8_1_, lvt_9_1_, lvt_6_3_).endVertex();
      lvt_10_1_.func_227888_a_(p_fill_0_, (float)p_fill_3_, (float)p_fill_4_, 0.0F).func_227885_a_(lvt_7_1_, lvt_8_1_, lvt_9_1_, lvt_6_3_).endVertex();
      lvt_10_1_.func_227888_a_(p_fill_0_, (float)p_fill_3_, (float)p_fill_2_, 0.0F).func_227885_a_(lvt_7_1_, lvt_8_1_, lvt_9_1_, lvt_6_3_).endVertex();
      lvt_10_1_.func_227888_a_(p_fill_0_, (float)p_fill_1_, (float)p_fill_2_, 0.0F).func_227885_a_(lvt_7_1_, lvt_8_1_, lvt_9_1_, lvt_6_3_).endVertex();
      lvt_10_1_.finishDrawing();
      WorldVertexBufferUploader.draw(lvt_10_1_);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   protected void fillGradient(int p_fillGradient_1_, int p_fillGradient_2_, int p_fillGradient_3_, int p_fillGradient_4_, int p_fillGradient_5_, int p_fillGradient_6_) {
      float lvt_7_1_ = (float)(p_fillGradient_5_ >> 24 & 255) / 255.0F;
      float lvt_8_1_ = (float)(p_fillGradient_5_ >> 16 & 255) / 255.0F;
      float lvt_9_1_ = (float)(p_fillGradient_5_ >> 8 & 255) / 255.0F;
      float lvt_10_1_ = (float)(p_fillGradient_5_ & 255) / 255.0F;
      float lvt_11_1_ = (float)(p_fillGradient_6_ >> 24 & 255) / 255.0F;
      float lvt_12_1_ = (float)(p_fillGradient_6_ >> 16 & 255) / 255.0F;
      float lvt_13_1_ = (float)(p_fillGradient_6_ >> 8 & 255) / 255.0F;
      float lvt_14_1_ = (float)(p_fillGradient_6_ & 255) / 255.0F;
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.disableAlphaTest();
      RenderSystem.defaultBlendFunc();
      RenderSystem.shadeModel(7425);
      Tessellator lvt_15_1_ = Tessellator.getInstance();
      BufferBuilder lvt_16_1_ = lvt_15_1_.getBuffer();
      lvt_16_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
      lvt_16_1_.func_225582_a_((double)p_fillGradient_3_, (double)p_fillGradient_2_, (double)this.blitOffset).func_227885_a_(lvt_8_1_, lvt_9_1_, lvt_10_1_, lvt_7_1_).endVertex();
      lvt_16_1_.func_225582_a_((double)p_fillGradient_1_, (double)p_fillGradient_2_, (double)this.blitOffset).func_227885_a_(lvt_8_1_, lvt_9_1_, lvt_10_1_, lvt_7_1_).endVertex();
      lvt_16_1_.func_225582_a_((double)p_fillGradient_1_, (double)p_fillGradient_4_, (double)this.blitOffset).func_227885_a_(lvt_12_1_, lvt_13_1_, lvt_14_1_, lvt_11_1_).endVertex();
      lvt_16_1_.func_225582_a_((double)p_fillGradient_3_, (double)p_fillGradient_4_, (double)this.blitOffset).func_227885_a_(lvt_12_1_, lvt_13_1_, lvt_14_1_, lvt_11_1_).endVertex();
      lvt_15_1_.draw();
      RenderSystem.shadeModel(7424);
      RenderSystem.disableBlend();
      RenderSystem.enableAlphaTest();
      RenderSystem.enableTexture();
   }

   public void drawCenteredString(FontRenderer p_drawCenteredString_1_, String p_drawCenteredString_2_, int p_drawCenteredString_3_, int p_drawCenteredString_4_, int p_drawCenteredString_5_) {
      p_drawCenteredString_1_.drawStringWithShadow(p_drawCenteredString_2_, (float)(p_drawCenteredString_3_ - p_drawCenteredString_1_.getStringWidth(p_drawCenteredString_2_) / 2), (float)p_drawCenteredString_4_, p_drawCenteredString_5_);
   }

   public void drawRightAlignedString(FontRenderer p_drawRightAlignedString_1_, String p_drawRightAlignedString_2_, int p_drawRightAlignedString_3_, int p_drawRightAlignedString_4_, int p_drawRightAlignedString_5_) {
      p_drawRightAlignedString_1_.drawStringWithShadow(p_drawRightAlignedString_2_, (float)(p_drawRightAlignedString_3_ - p_drawRightAlignedString_1_.getStringWidth(p_drawRightAlignedString_2_)), (float)p_drawRightAlignedString_4_, p_drawRightAlignedString_5_);
   }

   public void drawString(FontRenderer p_drawString_1_, String p_drawString_2_, int p_drawString_3_, int p_drawString_4_, int p_drawString_5_) {
      p_drawString_1_.drawStringWithShadow(p_drawString_2_, (float)p_drawString_3_, (float)p_drawString_4_, p_drawString_5_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, TextureAtlasSprite p_blit_5_) {
      innerBlit(p_blit_0_, p_blit_0_ + p_blit_3_, p_blit_1_, p_blit_1_ + p_blit_4_, p_blit_2_, p_blit_5_.getMinU(), p_blit_5_.getMaxU(), p_blit_5_.getMinV(), p_blit_5_.getMaxV());
   }

   public void blit(int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_) {
      blit(p_blit_1_, p_blit_2_, this.blitOffset, (float)p_blit_3_, (float)p_blit_4_, p_blit_5_, p_blit_6_, 256, 256);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, float p_blit_3_, float p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_) {
      innerBlit(p_blit_0_, p_blit_0_ + p_blit_5_, p_blit_1_, p_blit_1_ + p_blit_6_, p_blit_2_, p_blit_5_, p_blit_6_, p_blit_3_, p_blit_4_, p_blit_8_, p_blit_7_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, int p_blit_3_, float p_blit_4_, float p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_, int p_blit_9_) {
      innerBlit(p_blit_0_, p_blit_0_ + p_blit_2_, p_blit_1_, p_blit_1_ + p_blit_3_, 0, p_blit_6_, p_blit_7_, p_blit_4_, p_blit_5_, p_blit_8_, p_blit_9_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, float p_blit_2_, float p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_) {
      blit(p_blit_0_, p_blit_1_, p_blit_4_, p_blit_5_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_);
   }

   private static void innerBlit(int p_innerBlit_0_, int p_innerBlit_1_, int p_innerBlit_2_, int p_innerBlit_3_, int p_innerBlit_4_, int p_innerBlit_5_, int p_innerBlit_6_, float p_innerBlit_7_, float p_innerBlit_8_, int p_innerBlit_9_, int p_innerBlit_10_) {
      innerBlit(p_innerBlit_0_, p_innerBlit_1_, p_innerBlit_2_, p_innerBlit_3_, p_innerBlit_4_, (p_innerBlit_7_ + 0.0F) / (float)p_innerBlit_9_, (p_innerBlit_7_ + (float)p_innerBlit_5_) / (float)p_innerBlit_9_, (p_innerBlit_8_ + 0.0F) / (float)p_innerBlit_10_, (p_innerBlit_8_ + (float)p_innerBlit_6_) / (float)p_innerBlit_10_);
   }

   protected static void innerBlit(int p_innerBlit_0_, int p_innerBlit_1_, int p_innerBlit_2_, int p_innerBlit_3_, int p_innerBlit_4_, float p_innerBlit_5_, float p_innerBlit_6_, float p_innerBlit_7_, float p_innerBlit_8_) {
      BufferBuilder lvt_9_1_ = Tessellator.getInstance().getBuffer();
      lvt_9_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
      lvt_9_1_.func_225582_a_((double)p_innerBlit_0_, (double)p_innerBlit_3_, (double)p_innerBlit_4_).func_225583_a_(p_innerBlit_5_, p_innerBlit_8_).endVertex();
      lvt_9_1_.func_225582_a_((double)p_innerBlit_1_, (double)p_innerBlit_3_, (double)p_innerBlit_4_).func_225583_a_(p_innerBlit_6_, p_innerBlit_8_).endVertex();
      lvt_9_1_.func_225582_a_((double)p_innerBlit_1_, (double)p_innerBlit_2_, (double)p_innerBlit_4_).func_225583_a_(p_innerBlit_6_, p_innerBlit_7_).endVertex();
      lvt_9_1_.func_225582_a_((double)p_innerBlit_0_, (double)p_innerBlit_2_, (double)p_innerBlit_4_).func_225583_a_(p_innerBlit_5_, p_innerBlit_7_).endVertex();
      lvt_9_1_.finishDrawing();
      RenderSystem.enableAlphaTest();
      WorldVertexBufferUploader.draw(lvt_9_1_);
   }

   public int getBlitOffset() {
      return this.blitOffset;
   }

   public void setBlitOffset(int p_setBlitOffset_1_) {
      this.blitOffset = p_setBlitOffset_1_;
   }
}
