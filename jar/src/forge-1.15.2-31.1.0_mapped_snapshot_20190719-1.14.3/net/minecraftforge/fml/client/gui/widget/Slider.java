package net.minecraftforge.fml.client.gui.widget;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class Slider extends ExtendedButton {
   public double sliderValue;
   public String dispString;
   public boolean dragging;
   public boolean showDecimal;
   public double minValue;
   public double maxValue;
   public int precision;
   @Nullable
   public Slider.ISlider parent;
   public String suffix;
   public boolean drawString;

   public Slider(int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, Button.IPressable handler) {
      this(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, handler, (Slider.ISlider)null);
   }

   public Slider(int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, Button.IPressable handler, @Nullable Slider.ISlider par) {
      super(xPos, yPos, width, height, prefix, handler);
      this.sliderValue = 1.0D;
      this.dispString = "";
      this.dragging = false;
      this.showDecimal = true;
      this.minValue = 0.0D;
      this.maxValue = 5.0D;
      this.precision = 1;
      this.parent = null;
      this.suffix = "";
      this.drawString = true;
      this.minValue = minVal;
      this.maxValue = maxVal;
      this.sliderValue = (currentVal - this.minValue) / (this.maxValue - this.minValue);
      this.dispString = prefix;
      this.parent = par;
      this.suffix = suf;
      this.showDecimal = showDec;
      String val;
      if (this.showDecimal) {
         val = Double.toString(this.sliderValue * (this.maxValue - this.minValue) + this.minValue);
         this.precision = Math.min(val.substring(val.indexOf(".") + 1).length(), 4);
      } else {
         val = Integer.toString((int)Math.round(this.sliderValue * (this.maxValue - this.minValue) + this.minValue));
         this.precision = 0;
      }

      this.setMessage(this.dispString + val + this.suffix);
      this.drawString = drawStr;
      if (!this.drawString) {
         this.setMessage("");
      }

   }

   public Slider(int xPos, int yPos, String displayStr, double minVal, double maxVal, double currentVal, Button.IPressable handler, Slider.ISlider par) {
      this(xPos, yPos, 150, 20, displayStr, "", minVal, maxVal, currentVal, true, true, handler, par);
   }

   public int getYImage(boolean par1) {
      return 0;
   }

   protected void renderBg(Minecraft par1Minecraft, int par2, int par3) {
      if (this.visible) {
         if (this.dragging) {
            this.sliderValue = (double)((float)(par2 - (this.x + 4)) / (float)(this.width - 8));
            this.updateSlider();
         }

         GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, this.x + (int)(this.sliderValue * (double)((float)(this.width - 8))), this.y, 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, (float)this.getBlitOffset());
      }

   }

   public void onClick(double mouseX, double mouseY) {
      this.sliderValue = (mouseX - (double)(this.x + 4)) / (double)(this.width - 8);
      this.updateSlider();
      this.dragging = true;
   }

   public void updateSlider() {
      if (this.sliderValue < 0.0D) {
         this.sliderValue = 0.0D;
      }

      if (this.sliderValue > 1.0D) {
         this.sliderValue = 1.0D;
      }

      String val;
      if (this.showDecimal) {
         val = Double.toString(this.sliderValue * (this.maxValue - this.minValue) + this.minValue);
         if (val.substring(val.indexOf(".") + 1).length() > this.precision) {
            val = val.substring(0, val.indexOf(".") + this.precision + 1);
            if (val.endsWith(".")) {
               val = val.substring(0, val.indexOf(".") + this.precision);
            }
         } else {
            while(val.substring(val.indexOf(".") + 1).length() < this.precision) {
               val = val + "0";
            }
         }
      } else {
         val = Integer.toString((int)Math.round(this.sliderValue * (this.maxValue - this.minValue) + this.minValue));
      }

      if (this.drawString) {
         this.setMessage(this.dispString + val + this.suffix);
      }

      if (this.parent != null) {
         this.parent.onChangeSliderValue(this);
      }

   }

   public void onRelease(double mouseX, double mouseY) {
      this.dragging = false;
   }

   public int getValueInt() {
      return (int)Math.round(this.sliderValue * (this.maxValue - this.minValue) + this.minValue);
   }

   public double getValue() {
      return this.sliderValue * (this.maxValue - this.minValue) + this.minValue;
   }

   public void setValue(double d) {
      this.sliderValue = (d - this.minValue) / (this.maxValue - this.minValue);
   }

   public interface ISlider {
      void onChangeSliderValue(Slider var1);
   }
}
