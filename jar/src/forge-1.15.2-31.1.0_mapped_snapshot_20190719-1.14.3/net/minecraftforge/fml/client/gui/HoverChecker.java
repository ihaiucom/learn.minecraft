package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.widget.Widget;

public class HoverChecker {
   private int top;
   private int bottom;
   private int left;
   private int right;
   private int threshold;
   private Widget widget;
   private long hoverStart;

   public HoverChecker(int top, int bottom, int left, int right, int threshold) {
      this.top = top;
      this.bottom = bottom;
      this.left = left;
      this.right = right;
      this.threshold = threshold;
      this.hoverStart = -1L;
   }

   public HoverChecker(Widget widget, int threshold) {
      this.widget = widget;
      this.threshold = threshold;
   }

   public void updateBounds(int top, int bottom, int left, int right) {
      this.top = top;
      this.bottom = bottom;
      this.left = left;
      this.right = right;
   }

   public boolean checkHover(int mouseX, int mouseY) {
      return this.checkHover(mouseX, mouseY, true);
   }

   public boolean checkHover(int mouseX, int mouseY, boolean canHover) {
      if (this.widget != null) {
         this.top = this.widget.y;
         this.bottom = this.widget.y + this.widget.getHeight();
         this.left = this.widget.x;
         this.right = this.widget.x + this.widget.getWidth();
         canHover = canHover && this.widget.visible;
      }

      if (canHover && this.hoverStart == -1L && mouseY >= this.top && mouseY <= this.bottom && mouseX >= this.left && mouseX <= this.right) {
         this.hoverStart = System.currentTimeMillis();
      } else if (!canHover || mouseY < this.top || mouseY > this.bottom || mouseX < this.left || mouseX > this.right) {
         this.resetHoverTimer();
      }

      return canHover && this.hoverStart != -1L && System.currentTimeMillis() - this.hoverStart >= (long)this.threshold;
   }

   public void resetHoverTimer() {
      this.hoverStart = -1L;
   }
}
