package net.minecraftforge.fml.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class UnicodeGlyphButton extends ExtendedButton {
   public String glyph;
   public float glyphScale;

   public UnicodeGlyphButton(int xPos, int yPos, int width, int height, String displayString, String glyph, float glyphScale, Button.IPressable handler) {
      super(xPos, yPos, width, height, displayString, handler);
      this.glyph = glyph;
      this.glyphScale = glyphScale;
   }

   public void render(int mouseX, int mouseY, float partial) {
      if (this.visible) {
         Minecraft mc = Minecraft.getInstance();
         this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int k = this.getYImage(this.isHovered);
         GuiUtils.drawContinuousTexturedBox(Button.WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, (float)this.getBlitOffset());
         this.renderBg(mc, mouseX, mouseY);
         String buttonText = this.getMessage();
         int glyphWidth = (int)((float)mc.fontRenderer.getStringWidth(this.glyph) * this.glyphScale);
         int strWidth = mc.fontRenderer.getStringWidth(buttonText);
         int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
         int totalWidth = strWidth + glyphWidth;
         if (totalWidth > this.width - 6 && totalWidth > ellipsisWidth) {
            buttonText = mc.fontRenderer.trimStringToWidth(buttonText, this.width - 6 - ellipsisWidth).trim() + "...";
         }

         strWidth = mc.fontRenderer.getStringWidth(buttonText);
         int var10000 = glyphWidth + strWidth;
         RenderSystem.pushMatrix();
         RenderSystem.scalef(this.glyphScale, this.glyphScale, 1.0F);
         this.drawCenteredString(mc.fontRenderer, this.glyph, (int)((float)(this.x + this.width / 2 - strWidth / 2) / this.glyphScale - (float)glyphWidth / (2.0F * this.glyphScale) + 2.0F), (int)(((float)this.y + (float)(this.height - 8) / this.glyphScale / 2.0F - 1.0F) / this.glyphScale), this.getFGColor());
         RenderSystem.popMatrix();
         this.drawCenteredString(mc.fontRenderer, buttonText, (int)((float)(this.x + this.width / 2) + (float)glyphWidth / this.glyphScale), this.y + (this.height - 8) / 2, this.getFGColor());
      }

   }
}
