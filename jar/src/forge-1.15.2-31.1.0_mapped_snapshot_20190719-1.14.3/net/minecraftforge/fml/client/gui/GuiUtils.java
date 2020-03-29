package net.minecraftforge.fml.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;

public class GuiUtils {
   public static final String UNDO_CHAR = "↶";
   public static final String RESET_CHAR = "☄";
   public static final String VALID = "✔";
   public static final String INVALID = "✕";
   public static int[] colorCodes = new int[]{0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 5635925, 5636095, 16733525, 16733695, 16777045, 16777215, 0, 42, 10752, 10794, 2752512, 2752554, 2763264, 2763306, 1381653, 1381695, 1392405, 1392447, 4134165, 4134207, 4144917, 4144959};
   @Nonnull
   private static ItemStack cachedTooltipStack;

   public static int getColorCode(char c, boolean isLighter) {
      return colorCodes[isLighter ? "0123456789abcdef".indexOf(c) : "0123456789abcdef".indexOf(c) + 16];
   }

   public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
      drawContinuousTexturedBox(x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
   }

   public static void drawContinuousTexturedBox(ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int borderSize, float zLevel) {
      drawContinuousTexturedBox(res, x, y, u, v, width, height, textureWidth, textureHeight, borderSize, borderSize, borderSize, borderSize, zLevel);
   }

   public static void drawContinuousTexturedBox(ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
      Minecraft.getInstance().getTextureManager().bindTexture(res);
      drawContinuousTexturedBox(x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
   }

   public static void drawContinuousTexturedBox(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(770, 771, 1, 0);
      int fillerWidth = textureWidth - leftBorder - rightBorder;
      int fillerHeight = textureHeight - topBorder - bottomBorder;
      int canvasWidth = width - leftBorder - rightBorder;
      int canvasHeight = height - topBorder - bottomBorder;
      int xPasses = canvasWidth / fillerWidth;
      int remainderWidth = canvasWidth % fillerWidth;
      int yPasses = canvasHeight / fillerHeight;
      int remainderHeight = canvasHeight % fillerHeight;
      drawTexturedModalRect(x, y, u, v, leftBorder, topBorder, zLevel);
      drawTexturedModalRect(x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel);
      drawTexturedModalRect(x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel);
      drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel);

      int i;
      for(i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); ++i) {
         drawTexturedModalRect(x + leftBorder + i * fillerWidth, y, u + leftBorder, v, i == xPasses ? remainderWidth : fillerWidth, topBorder, zLevel);
         drawTexturedModalRect(x + leftBorder + i * fillerWidth, y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, i == xPasses ? remainderWidth : fillerWidth, bottomBorder, zLevel);

         for(int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); ++j) {
            drawTexturedModalRect(x + leftBorder + i * fillerWidth, y + topBorder + j * fillerHeight, u + leftBorder, v + topBorder, i == xPasses ? remainderWidth : fillerWidth, j == yPasses ? remainderHeight : fillerHeight, zLevel);
         }
      }

      for(i = 0; i < yPasses + (remainderHeight > 0 ? 1 : 0); ++i) {
         drawTexturedModalRect(x, y + topBorder + i * fillerHeight, u, v + topBorder, leftBorder, i == yPasses ? remainderHeight : fillerHeight, zLevel);
         drawTexturedModalRect(x + leftBorder + canvasWidth, y + topBorder + i * fillerHeight, u + leftBorder + fillerWidth, v + topBorder, rightBorder, i == yPasses ? remainderHeight : fillerHeight, zLevel);
      }

   }

   public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, float zLevel) {
      float uScale = 0.00390625F;
      float vScale = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder wr = tessellator.getBuffer();
      wr.begin(7, DefaultVertexFormats.POSITION_TEX);
      wr.func_225582_a_((double)x, (double)(y + height), (double)zLevel).func_225583_a_((float)u * 0.00390625F, (float)(v + height) * 0.00390625F).endVertex();
      wr.func_225582_a_((double)(x + width), (double)(y + height), (double)zLevel).func_225583_a_((float)(u + width) * 0.00390625F, (float)(v + height) * 0.00390625F).endVertex();
      wr.func_225582_a_((double)(x + width), (double)y, (double)zLevel).func_225583_a_((float)(u + width) * 0.00390625F, (float)v * 0.00390625F).endVertex();
      wr.func_225582_a_((double)x, (double)y, (double)zLevel).func_225583_a_((float)u * 0.00390625F, (float)v * 0.00390625F).endVertex();
      tessellator.draw();
   }

   public static void preItemToolTip(@Nonnull ItemStack stack) {
      cachedTooltipStack = stack;
   }

   public static void postItemToolTip() {
      cachedTooltipStack = ItemStack.EMPTY;
   }

   public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
      drawHoveringText(cachedTooltipStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
   }

   public static void drawHoveringText(@Nonnull ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
      if (!((List)textLines).isEmpty()) {
         RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, (List)textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
         if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
         }

         mouseX = event.getX();
         mouseY = event.getY();
         screenWidth = event.getScreenWidth();
         screenHeight = event.getScreenHeight();
         maxTextWidth = event.getMaxWidth();
         font = event.getFontRenderer();
         RenderSystem.disableRescaleNormal();
         RenderSystem.disableDepthTest();
         int tooltipTextWidth = 0;
         Iterator var10 = ((List)textLines).iterator();

         int tooltipX;
         while(var10.hasNext()) {
            String textLine = (String)var10.next();
            tooltipX = font.getStringWidth(textLine);
            if (tooltipX > tooltipTextWidth) {
               tooltipTextWidth = tooltipX;
            }
         }

         boolean needsWrap = false;
         int titleLinesCount = 1;
         tooltipX = mouseX + 12;
         if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) {
               if (mouseX > screenWidth / 2) {
                  tooltipTextWidth = mouseX - 12 - 8;
               } else {
                  tooltipTextWidth = screenWidth - 16 - mouseX;
               }

               needsWrap = true;
            }
         }

         if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
         }

         int tooltipY;
         if (needsWrap) {
            tooltipY = 0;
            List<String> wrappedTextLines = new ArrayList();
            int i = 0;

            while(true) {
               if (i >= ((List)textLines).size()) {
                  tooltipTextWidth = tooltipY;
                  textLines = wrappedTextLines;
                  if (mouseX > screenWidth / 2) {
                     tooltipX = mouseX - 16 - tooltipY;
                  } else {
                     tooltipX = mouseX + 12;
                  }
                  break;
               }

               String textLine = (String)((List)textLines).get(i);
               List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
               if (i == 0) {
                  titleLinesCount = wrappedLine.size();
               }

               String line;
               for(Iterator var18 = wrappedLine.iterator(); var18.hasNext(); wrappedTextLines.add(line)) {
                  line = (String)var18.next();
                  int lineWidth = font.getStringWidth(line);
                  if (lineWidth > tooltipY) {
                     tooltipY = lineWidth;
                  }
               }

               ++i;
            }
         }

         tooltipY = mouseY - 12;
         int tooltipHeight = 8;
         if (((List)textLines).size() > 1) {
            tooltipHeight += (((List)textLines).size() - 1) * 10;
            if (((List)textLines).size() > titleLinesCount) {
               tooltipHeight += 2;
            }
         }

         if (tooltipY < 4) {
            tooltipY = 4;
         } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
            tooltipY = screenHeight - tooltipHeight - 4;
         }

         int zLevel = true;
         int backgroundColor = -267386864;
         int borderColorStart = 1347420415;
         int borderColorEnd = (borderColorStart & 16711422) >> 1 | borderColorStart & -16777216;
         RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, (List)textLines, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
         MinecraftForge.EVENT_BUS.post(colorEvent);
         backgroundColor = colorEvent.getBackground();
         borderColorStart = colorEvent.getBorderStart();
         borderColorEnd = colorEvent.getBorderEnd();
         drawGradientRect(300, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
         drawGradientRect(300, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
         drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
         drawGradientRect(300, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
         drawGradientRect(300, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
         drawGradientRect(300, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
         drawGradientRect(300, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
         drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
         drawGradientRect(300, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);
         MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, (List)textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
         IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.func_228455_a_(Tessellator.getInstance().getBuffer());
         MatrixStack textStack = new MatrixStack();
         textStack.func_227861_a_(0.0D, 0.0D, 300.0D);
         Matrix4f textLocation = textStack.func_227866_c_().func_227870_a_();

         for(int lineNumber = 0; lineNumber < ((List)textLines).size(); ++lineNumber) {
            String line = (String)((List)textLines).get(lineNumber);
            if (line != null) {
               font.func_228079_a_(line, (float)tooltipX, (float)tooltipY, -1, true, textLocation, renderType, false, 0, 15728880);
            }

            if (lineNumber + 1 == titleLinesCount) {
               tooltipY += 2;
            }

            tooltipY += 10;
         }

         renderType.func_228461_a_();
         MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, (List)textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
         RenderSystem.enableDepthTest();
         RenderSystem.enableRescaleNormal();
      }

   }

   public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
      float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
      float startRed = (float)(startColor >> 16 & 255) / 255.0F;
      float startGreen = (float)(startColor >> 8 & 255) / 255.0F;
      float startBlue = (float)(startColor & 255) / 255.0F;
      float endAlpha = (float)(endColor >> 24 & 255) / 255.0F;
      float endRed = (float)(endColor >> 16 & 255) / 255.0F;
      float endGreen = (float)(endColor >> 8 & 255) / 255.0F;
      float endBlue = (float)(endColor & 255) / 255.0F;
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.disableAlphaTest();
      RenderSystem.defaultBlendFunc();
      RenderSystem.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      buffer.func_225582_a_((double)right, (double)top, (double)zLevel).func_227885_a_(startRed, startGreen, startBlue, startAlpha).endVertex();
      buffer.func_225582_a_((double)left, (double)top, (double)zLevel).func_227885_a_(startRed, startGreen, startBlue, startAlpha).endVertex();
      buffer.func_225582_a_((double)left, (double)bottom, (double)zLevel).func_227885_a_(endRed, endGreen, endBlue, endAlpha).endVertex();
      buffer.func_225582_a_((double)right, (double)bottom, (double)zLevel).func_227885_a_(endRed, endGreen, endBlue, endAlpha).endVertex();
      tessellator.draw();
      RenderSystem.shadeModel(7424);
      RenderSystem.disableBlend();
      RenderSystem.enableAlphaTest();
      RenderSystem.enableTexture();
   }

   public static void drawInscribedRect(int x, int y, int boundsWidth, int boundsHeight, int rectWidth, int rectHeight) {
      drawInscribedRect(x, y, boundsWidth, boundsHeight, rectWidth, rectHeight, true, true);
   }

   public static void drawInscribedRect(int x, int y, int boundsWidth, int boundsHeight, int rectWidth, int rectHeight, boolean centerX, boolean centerY) {
      int h;
      if (rectWidth * boundsHeight > rectHeight * boundsWidth) {
         h = boundsHeight;
         boundsHeight = (int)((double)boundsWidth * ((double)rectHeight / (double)rectWidth));
         if (centerY) {
            y += (h - boundsHeight) / 2;
         }
      } else {
         h = boundsWidth;
         boundsWidth = (int)((double)boundsHeight * ((double)rectWidth / (double)rectHeight));
         if (centerX) {
            x += (h - boundsWidth) / 2;
         }
      }

      AbstractGui.blit(x, y, boundsWidth, boundsHeight, 0.0F, 0.0F, rectWidth, rectHeight, rectWidth, rectHeight);
   }

   static {
      cachedTooltipStack = ItemStack.EMPTY;
   }
}
