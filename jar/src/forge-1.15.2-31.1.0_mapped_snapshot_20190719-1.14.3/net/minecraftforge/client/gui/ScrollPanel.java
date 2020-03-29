package net.minecraftforge.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;

public abstract class ScrollPanel extends FocusableGui implements IRenderable {
   private final Minecraft client;
   protected final int width;
   protected final int height;
   protected final int top;
   protected final int bottom;
   protected final int right;
   protected final int left;
   private boolean scrolling;
   protected float scrollDistance;
   protected boolean captureMouse = true;
   protected final int border = 4;
   private final int barWidth = 6;
   private final int barLeft;

   public ScrollPanel(Minecraft client, int width, int height, int top, int left) {
      this.client = client;
      this.width = width;
      this.height = height;
      this.top = top;
      this.left = left;
      this.bottom = height + this.top;
      this.right = width + this.left;
      this.barLeft = this.left + this.width - 6;
   }

   protected abstract int getContentHeight();

   protected void drawBackground() {
   }

   protected abstract void drawPanel(int var1, int var2, Tessellator var3, int var4, int var5);

   protected boolean clickPanel(double mouseX, double mouseY, int button) {
      return false;
   }

   private int getMaxScroll() {
      int var10000 = this.getContentHeight();
      int var10001 = this.height;
      this.getClass();
      return var10000 - (var10001 - 4);
   }

   private void applyScrollLimits() {
      int max = this.getMaxScroll();
      if (max < 0) {
         max /= 2;
      }

      if (this.scrollDistance < 0.0F) {
         this.scrollDistance = 0.0F;
      }

      if (this.scrollDistance > (float)max) {
         this.scrollDistance = (float)max;
      }

   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
      if (scroll != 0.0D) {
         this.scrollDistance = (float)((double)this.scrollDistance + -scroll * (double)this.getScrollAmount());
         this.applyScrollLimits();
         return true;
      } else {
         return false;
      }
   }

   protected int getScrollAmount() {
      return 20;
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return mouseX >= (double)this.left && mouseX <= (double)(this.left + this.width) && mouseY >= (double)this.top && mouseY <= (double)this.bottom;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (super.mouseClicked(mouseX, mouseY, button)) {
         return true;
      } else {
         this.scrolling = button == 0 && mouseX >= (double)this.barLeft && mouseX < (double)(this.barLeft + 6);
         if (this.scrolling) {
            return true;
         } else {
            int mouseListY = (int)mouseY - this.top - this.getContentHeight() + (int)this.scrollDistance - 4;
            return mouseX >= (double)this.left && mouseX <= (double)this.right && mouseListY < 0 ? this.clickPanel(mouseX - (double)this.left, mouseY - (double)this.top + (double)((int)this.scrollDistance) - 4.0D, button) : false;
         }
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         return true;
      } else {
         boolean ret = this.scrolling;
         this.scrolling = false;
         return ret;
      }
   }

   private int getBarHeight() {
      int barHeight = this.height * this.height / this.getContentHeight();
      if (barHeight < 32) {
         barHeight = 32;
      }

      if (barHeight > this.height - 8) {
         barHeight = this.height - 8;
      }

      return barHeight;
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.scrolling) {
         int maxScroll = this.height - this.getBarHeight();
         double moved = deltaY / (double)maxScroll;
         this.scrollDistance = (float)((double)this.scrollDistance + (double)this.getMaxScroll() * moved);
         this.applyScrollLimits();
         return true;
      } else {
         return false;
      }
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground();
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder worldr = tess.getBuffer();
      double scale = this.client.func_228018_at_().getGuiScaleFactor();
      GL11.glEnable(3089);
      GL11.glScissor((int)((double)this.left * scale), (int)((double)this.client.func_228018_at_().getFramebufferHeight() - (double)this.bottom * scale), (int)((double)this.width * scale), (int)((double)this.height * scale));
      if (this.client.world != null) {
         this.drawGradientRect(this.left, this.top, this.right, this.bottom, -1072689136, -804253680);
      } else {
         RenderSystem.disableLighting();
         RenderSystem.disableFog();
         this.client.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float texScale = 32.0F;
         worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldr.func_225582_a_((double)this.left, (double)this.bottom, 0.0D).func_225583_a_((float)this.left / 32.0F, (float)(this.bottom + (int)this.scrollDistance) / 32.0F).func_225586_a_(32, 32, 32, 255).endVertex();
         worldr.func_225582_a_((double)this.right, (double)this.bottom, 0.0D).func_225583_a_((float)this.right / 32.0F, (float)(this.bottom + (int)this.scrollDistance) / 32.0F).func_225586_a_(32, 32, 32, 255).endVertex();
         worldr.func_225582_a_((double)this.right, (double)this.top, 0.0D).func_225583_a_((float)this.right / 32.0F, (float)(this.top + (int)this.scrollDistance) / 32.0F).func_225586_a_(32, 32, 32, 255).endVertex();
         worldr.func_225582_a_((double)this.left, (double)this.top, 0.0D).func_225583_a_((float)this.left / 32.0F, (float)(this.top + (int)this.scrollDistance) / 32.0F).func_225586_a_(32, 32, 32, 255).endVertex();
         tess.draw();
      }

      int baseY = this.top + 4 - (int)this.scrollDistance;
      this.drawPanel(this.right, baseY, tess, mouseX, mouseY);
      RenderSystem.disableDepthTest();
      int extraHeight = this.getContentHeight() + 4 - this.height;
      if (extraHeight > 0) {
         int barHeight = this.getBarHeight();
         int barTop = (int)this.scrollDistance * (this.height - barHeight) / extraHeight + this.top;
         if (barTop < this.top) {
            barTop = this.top;
         }

         RenderSystem.disableTexture();
         worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldr.func_225582_a_((double)this.barLeft, (double)this.bottom, 0.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(0, 0, 0, 255).endVertex();
         worldr.func_225582_a_((double)(this.barLeft + 6), (double)this.bottom, 0.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(0, 0, 0, 255).endVertex();
         worldr.func_225582_a_((double)(this.barLeft + 6), (double)this.top, 0.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(0, 0, 0, 255).endVertex();
         worldr.func_225582_a_((double)this.barLeft, (double)this.top, 0.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(0, 0, 0, 255).endVertex();
         tess.draw();
         worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldr.func_225582_a_((double)this.barLeft, (double)(barTop + barHeight), 0.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(128, 128, 128, 255).endVertex();
         worldr.func_225582_a_((double)(this.barLeft + 6), (double)(barTop + barHeight), 0.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(128, 128, 128, 255).endVertex();
         worldr.func_225582_a_((double)(this.barLeft + 6), (double)barTop, 0.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(128, 128, 128, 255).endVertex();
         worldr.func_225582_a_((double)this.barLeft, (double)barTop, 0.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(128, 128, 128, 255).endVertex();
         tess.draw();
         worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldr.func_225582_a_((double)this.barLeft, (double)(barTop + barHeight - 1), 0.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(192, 192, 192, 255).endVertex();
         worldr.func_225582_a_((double)(this.barLeft + 6 - 1), (double)(barTop + barHeight - 1), 0.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(192, 192, 192, 255).endVertex();
         worldr.func_225582_a_((double)(this.barLeft + 6 - 1), (double)barTop, 0.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(192, 192, 192, 255).endVertex();
         worldr.func_225582_a_((double)this.barLeft, (double)barTop, 0.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(192, 192, 192, 255).endVertex();
         tess.draw();
      }

      RenderSystem.enableTexture();
      RenderSystem.shadeModel(7424);
      RenderSystem.enableAlphaTest();
      RenderSystem.disableBlend();
      GL11.glDisable(3089);
   }

   protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
      GuiUtils.drawGradientRect(0, left, top, right, bottom, color1, color2);
   }

   public List<? extends IGuiEventListener> children() {
      return Collections.emptyList();
   }
}
