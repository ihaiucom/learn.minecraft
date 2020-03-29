package net.minecraftforge.fml.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class ExtendedButton extends Button {
   public ExtendedButton(int xPos, int yPos, int width, int height, String displayString, Button.IPressable handler) {
      super(xPos, yPos, width, height, displayString, handler);
   }

   public void renderButton(int mouseX, int mouseY, float partial) {
      if (this.visible) {
         Minecraft mc = Minecraft.getInstance();
         this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int k = this.getYImage(this.isHovered);
         GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, (float)this.getBlitOffset());
         this.renderBg(mc, mouseX, mouseY);
         int color = this.getFGColor();
         if (this.isHovered && this.packedFGColor == -1) {
            color = 16777120;
         }

         String buttonText = this.getMessage();
         int strWidth = mc.fontRenderer.getStringWidth(buttonText);
         int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
         if (strWidth > this.width - 6 && strWidth > ellipsisWidth) {
            buttonText = mc.fontRenderer.trimStringToWidth(buttonText, this.width - 6 - ellipsisWidth).trim() + "...";
         }

         this.drawCenteredString(mc.fontRenderer, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
      }

   }
}
