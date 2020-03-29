package net.minecraft.client.gui.widget;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextFieldWidget extends Widget implements IRenderable, IGuiEventListener {
   private final FontRenderer fontRenderer;
   private String text;
   private int maxStringLength;
   private int cursorCounter;
   private boolean enableBackgroundDrawing;
   private boolean canLoseFocus;
   private boolean isEnabled;
   private boolean field_212956_h;
   private int lineScrollOffset;
   private int cursorPosition;
   private int selectionEnd;
   private int enabledColor;
   private int disabledColor;
   private String suggestion;
   private Consumer<String> guiResponder;
   private Predicate<String> validator;
   private BiFunction<String, Integer, String> textFormatter;

   public TextFieldWidget(FontRenderer p_i51137_1_, int p_i51137_2_, int p_i51137_3_, int p_i51137_4_, int p_i51137_5_, String p_i51137_6_) {
      this(p_i51137_1_, p_i51137_2_, p_i51137_3_, p_i51137_4_, p_i51137_5_, (TextFieldWidget)null, p_i51137_6_);
   }

   public TextFieldWidget(FontRenderer p_i51138_1_, int p_i51138_2_, int p_i51138_3_, int p_i51138_4_, int p_i51138_5_, @Nullable TextFieldWidget p_i51138_6_, String p_i51138_7_) {
      super(p_i51138_2_, p_i51138_3_, p_i51138_4_, p_i51138_5_, p_i51138_7_);
      this.text = "";
      this.maxStringLength = 32;
      this.enableBackgroundDrawing = true;
      this.canLoseFocus = true;
      this.isEnabled = true;
      this.enabledColor = 14737632;
      this.disabledColor = 7368816;
      this.validator = Predicates.alwaysTrue();
      this.textFormatter = (p_195610_0_, p_195610_1_) -> {
         return p_195610_0_;
      };
      this.fontRenderer = p_i51138_1_;
      if (p_i51138_6_ != null) {
         this.setText(p_i51138_6_.getText());
      }

   }

   public void func_212954_a(Consumer<String> p_212954_1_) {
      this.guiResponder = p_212954_1_;
   }

   public void setTextFormatter(BiFunction<String, Integer, String> p_195607_1_) {
      this.textFormatter = p_195607_1_;
   }

   public void tick() {
      ++this.cursorCounter;
   }

   protected String getNarrationMessage() {
      String lvt_1_1_ = this.getMessage();
      return lvt_1_1_.isEmpty() ? "" : I18n.format("gui.narrate.editBox", lvt_1_1_, this.text);
   }

   public void setText(String p_146180_1_) {
      if (this.validator.test(p_146180_1_)) {
         if (p_146180_1_.length() > this.maxStringLength) {
            this.text = p_146180_1_.substring(0, this.maxStringLength);
         } else {
            this.text = p_146180_1_;
         }

         this.setCursorPositionEnd();
         this.setSelectionPos(this.cursorPosition);
         this.func_212951_d(p_146180_1_);
      }
   }

   public String getText() {
      return this.text;
   }

   public String getSelectedText() {
      int lvt_1_1_ = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int lvt_2_1_ = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      return this.text.substring(lvt_1_1_, lvt_2_1_);
   }

   public void setValidator(Predicate<String> p_200675_1_) {
      this.validator = p_200675_1_;
   }

   public void writeText(String p_146191_1_) {
      String lvt_2_1_ = "";
      String lvt_3_1_ = SharedConstants.filterAllowedCharacters(p_146191_1_);
      int lvt_4_1_ = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int lvt_5_1_ = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      int lvt_6_1_ = this.maxStringLength - this.text.length() - (lvt_4_1_ - lvt_5_1_);
      if (!this.text.isEmpty()) {
         lvt_2_1_ = lvt_2_1_ + this.text.substring(0, lvt_4_1_);
      }

      int lvt_7_2_;
      if (lvt_6_1_ < lvt_3_1_.length()) {
         lvt_2_1_ = lvt_2_1_ + lvt_3_1_.substring(0, lvt_6_1_);
         lvt_7_2_ = lvt_6_1_;
      } else {
         lvt_2_1_ = lvt_2_1_ + lvt_3_1_;
         lvt_7_2_ = lvt_3_1_.length();
      }

      if (!this.text.isEmpty() && lvt_5_1_ < this.text.length()) {
         lvt_2_1_ = lvt_2_1_ + this.text.substring(lvt_5_1_);
      }

      if (this.validator.test(lvt_2_1_)) {
         this.text = lvt_2_1_;
         this.func_212422_f(lvt_4_1_ + lvt_7_2_);
         this.setSelectionPos(this.cursorPosition);
         this.func_212951_d(this.text);
      }
   }

   private void func_212951_d(String p_212951_1_) {
      if (this.guiResponder != null) {
         this.guiResponder.accept(p_212951_1_);
      }

      this.nextNarration = Util.milliTime() + 500L;
   }

   private void delete(int p_212950_1_) {
      if (Screen.hasControlDown()) {
         this.deleteWords(p_212950_1_);
      } else {
         this.deleteFromCursor(p_212950_1_);
      }

   }

   public void deleteWords(int p_146177_1_) {
      if (!this.text.isEmpty()) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(p_146177_1_) - this.cursorPosition);
         }
      }
   }

   public void deleteFromCursor(int p_146175_1_) {
      if (!this.text.isEmpty()) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            boolean lvt_2_1_ = p_146175_1_ < 0;
            int lvt_3_1_ = lvt_2_1_ ? this.cursorPosition + p_146175_1_ : this.cursorPosition;
            int lvt_4_1_ = lvt_2_1_ ? this.cursorPosition : this.cursorPosition + p_146175_1_;
            String lvt_5_1_ = "";
            if (lvt_3_1_ >= 0) {
               lvt_5_1_ = this.text.substring(0, lvt_3_1_);
            }

            if (lvt_4_1_ < this.text.length()) {
               lvt_5_1_ = lvt_5_1_ + this.text.substring(lvt_4_1_);
            }

            if (this.validator.test(lvt_5_1_)) {
               this.text = lvt_5_1_;
               if (lvt_2_1_) {
                  this.moveCursorBy(p_146175_1_);
               }

               this.func_212951_d(this.text);
            }
         }
      }
   }

   public int getNthWordFromCursor(int p_146187_1_) {
      return this.getNthWordFromPos(p_146187_1_, this.getCursorPosition());
   }

   private int getNthWordFromPos(int p_146183_1_, int p_146183_2_) {
      return this.getNthWordFromPosWS(p_146183_1_, p_146183_2_, true);
   }

   private int getNthWordFromPosWS(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
      int lvt_4_1_ = p_146197_2_;
      boolean lvt_5_1_ = p_146197_1_ < 0;
      int lvt_6_1_ = Math.abs(p_146197_1_);

      for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_; ++lvt_7_1_) {
         if (!lvt_5_1_) {
            int lvt_8_1_ = this.text.length();
            lvt_4_1_ = this.text.indexOf(32, lvt_4_1_);
            if (lvt_4_1_ == -1) {
               lvt_4_1_ = lvt_8_1_;
            } else {
               while(p_146197_3_ && lvt_4_1_ < lvt_8_1_ && this.text.charAt(lvt_4_1_) == ' ') {
                  ++lvt_4_1_;
               }
            }
         } else {
            while(p_146197_3_ && lvt_4_1_ > 0 && this.text.charAt(lvt_4_1_ - 1) == ' ') {
               --lvt_4_1_;
            }

            while(lvt_4_1_ > 0 && this.text.charAt(lvt_4_1_ - 1) != ' ') {
               --lvt_4_1_;
            }
         }
      }

      return lvt_4_1_;
   }

   public void moveCursorBy(int p_146182_1_) {
      this.setCursorPosition(this.cursorPosition + p_146182_1_);
   }

   public void setCursorPosition(int p_146190_1_) {
      this.func_212422_f(p_146190_1_);
      if (!this.field_212956_h) {
         this.setSelectionPos(this.cursorPosition);
      }

      this.func_212951_d(this.text);
   }

   public void func_212422_f(int p_212422_1_) {
      this.cursorPosition = MathHelper.clamp(p_212422_1_, 0, this.text.length());
   }

   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (!this.func_212955_f()) {
         return false;
      } else {
         this.field_212956_h = Screen.hasShiftDown();
         if (Screen.isSelectAll(p_keyPressed_1_)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
         } else if (Screen.isCopy(p_keyPressed_1_)) {
            Minecraft.getInstance().keyboardListener.setClipboardString(this.getSelectedText());
            return true;
         } else if (Screen.isPaste(p_keyPressed_1_)) {
            if (this.isEnabled) {
               this.writeText(Minecraft.getInstance().keyboardListener.getClipboardString());
            }

            return true;
         } else if (Screen.isCut(p_keyPressed_1_)) {
            Minecraft.getInstance().keyboardListener.setClipboardString(this.getSelectedText());
            if (this.isEnabled) {
               this.writeText("");
            }

            return true;
         } else {
            switch(p_keyPressed_1_) {
            case 259:
               if (this.isEnabled) {
                  this.field_212956_h = false;
                  this.delete(-1);
                  this.field_212956_h = Screen.hasShiftDown();
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               return false;
            case 261:
               if (this.isEnabled) {
                  this.field_212956_h = false;
                  this.delete(1);
                  this.field_212956_h = Screen.hasShiftDown();
               }

               return true;
            case 262:
               if (Screen.hasControlDown()) {
                  this.setCursorPosition(this.getNthWordFromCursor(1));
               } else {
                  this.moveCursorBy(1);
               }

               return true;
            case 263:
               if (Screen.hasControlDown()) {
                  this.setCursorPosition(this.getNthWordFromCursor(-1));
               } else {
                  this.moveCursorBy(-1);
               }

               return true;
            case 268:
               this.setCursorPositionZero();
               return true;
            case 269:
               this.setCursorPositionEnd();
               return true;
            }
         }
      }
   }

   public boolean func_212955_f() {
      return this.getVisible() && this.isFocused() && this.isEnabled();
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (!this.func_212955_f()) {
         return false;
      } else if (SharedConstants.isAllowedCharacter(p_charTyped_1_)) {
         if (this.isEnabled) {
            this.writeText(Character.toString(p_charTyped_1_));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (!this.getVisible()) {
         return false;
      } else {
         boolean lvt_6_1_ = p_mouseClicked_1_ >= (double)this.x && p_mouseClicked_1_ < (double)(this.x + this.width) && p_mouseClicked_3_ >= (double)this.y && p_mouseClicked_3_ < (double)(this.y + this.height);
         if (this.canLoseFocus) {
            this.setFocused2(lvt_6_1_);
         }

         if (this.isFocused() && lvt_6_1_ && p_mouseClicked_5_ == 0) {
            int lvt_7_1_ = MathHelper.floor(p_mouseClicked_1_) - this.x;
            if (this.enableBackgroundDrawing) {
               lvt_7_1_ -= 4;
            }

            String lvt_8_1_ = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getAdjustedWidth());
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(lvt_8_1_, lvt_7_1_).length() + this.lineScrollOffset);
            return true;
         } else {
            return false;
         }
      }
   }

   public void setFocused2(boolean p_146195_1_) {
      super.setFocused(p_146195_1_);
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      if (this.getVisible()) {
         if (this.getEnableBackgroundDrawing()) {
            fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
            fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
         }

         int lvt_4_1_ = this.isEnabled ? this.enabledColor : this.disabledColor;
         int lvt_5_1_ = this.cursorPosition - this.lineScrollOffset;
         int lvt_6_1_ = this.selectionEnd - this.lineScrollOffset;
         String lvt_7_1_ = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getAdjustedWidth());
         boolean lvt_8_1_ = lvt_5_1_ >= 0 && lvt_5_1_ <= lvt_7_1_.length();
         boolean lvt_9_1_ = this.isFocused() && this.cursorCounter / 6 % 2 == 0 && lvt_8_1_;
         int lvt_10_1_ = this.enableBackgroundDrawing ? this.x + 4 : this.x;
         int lvt_11_1_ = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
         int lvt_12_1_ = lvt_10_1_;
         if (lvt_6_1_ > lvt_7_1_.length()) {
            lvt_6_1_ = lvt_7_1_.length();
         }

         if (!lvt_7_1_.isEmpty()) {
            String lvt_13_1_ = lvt_8_1_ ? lvt_7_1_.substring(0, lvt_5_1_) : lvt_7_1_;
            lvt_12_1_ = this.fontRenderer.drawStringWithShadow((String)this.textFormatter.apply(lvt_13_1_, this.lineScrollOffset), (float)lvt_10_1_, (float)lvt_11_1_, lvt_4_1_);
         }

         boolean lvt_13_2_ = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
         int lvt_14_1_ = lvt_12_1_;
         if (!lvt_8_1_) {
            lvt_14_1_ = lvt_5_1_ > 0 ? lvt_10_1_ + this.width : lvt_10_1_;
         } else if (lvt_13_2_) {
            lvt_14_1_ = lvt_12_1_ - 1;
            --lvt_12_1_;
         }

         if (!lvt_7_1_.isEmpty() && lvt_8_1_ && lvt_5_1_ < lvt_7_1_.length()) {
            this.fontRenderer.drawStringWithShadow((String)this.textFormatter.apply(lvt_7_1_.substring(lvt_5_1_), this.cursorPosition), (float)lvt_12_1_, (float)lvt_11_1_, lvt_4_1_);
         }

         if (!lvt_13_2_ && this.suggestion != null) {
            this.fontRenderer.drawStringWithShadow(this.suggestion, (float)(lvt_14_1_ - 1), (float)lvt_11_1_, -8355712);
         }

         int var10002;
         int var10003;
         if (lvt_9_1_) {
            if (lvt_13_2_) {
               int var10001 = lvt_11_1_ - 1;
               var10002 = lvt_14_1_ + 1;
               var10003 = lvt_11_1_ + 1;
               this.fontRenderer.getClass();
               AbstractGui.fill(lvt_14_1_, var10001, var10002, var10003 + 9, -3092272);
            } else {
               this.fontRenderer.drawStringWithShadow("_", (float)lvt_14_1_, (float)lvt_11_1_, lvt_4_1_);
            }
         }

         if (lvt_6_1_ != lvt_5_1_) {
            int lvt_15_1_ = lvt_10_1_ + this.fontRenderer.getStringWidth(lvt_7_1_.substring(0, lvt_6_1_));
            var10002 = lvt_11_1_ - 1;
            var10003 = lvt_15_1_ - 1;
            int var10004 = lvt_11_1_ + 1;
            this.fontRenderer.getClass();
            this.drawSelectionBox(lvt_14_1_, var10002, var10003, var10004 + 9);
         }

      }
   }

   private void drawSelectionBox(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {
      int lvt_5_2_;
      if (p_146188_1_ < p_146188_3_) {
         lvt_5_2_ = p_146188_1_;
         p_146188_1_ = p_146188_3_;
         p_146188_3_ = lvt_5_2_;
      }

      if (p_146188_2_ < p_146188_4_) {
         lvt_5_2_ = p_146188_2_;
         p_146188_2_ = p_146188_4_;
         p_146188_4_ = lvt_5_2_;
      }

      if (p_146188_3_ > this.x + this.width) {
         p_146188_3_ = this.x + this.width;
      }

      if (p_146188_1_ > this.x + this.width) {
         p_146188_1_ = this.x + this.width;
      }

      Tessellator lvt_5_3_ = Tessellator.getInstance();
      BufferBuilder lvt_6_1_ = lvt_5_3_.getBuffer();
      RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      lvt_6_1_.begin(7, DefaultVertexFormats.POSITION);
      lvt_6_1_.func_225582_a_((double)p_146188_1_, (double)p_146188_4_, 0.0D).endVertex();
      lvt_6_1_.func_225582_a_((double)p_146188_3_, (double)p_146188_4_, 0.0D).endVertex();
      lvt_6_1_.func_225582_a_((double)p_146188_3_, (double)p_146188_2_, 0.0D).endVertex();
      lvt_6_1_.func_225582_a_((double)p_146188_1_, (double)p_146188_2_, 0.0D).endVertex();
      lvt_5_3_.draw();
      RenderSystem.disableColorLogicOp();
      RenderSystem.enableTexture();
   }

   public void setMaxStringLength(int p_146203_1_) {
      this.maxStringLength = p_146203_1_;
      if (this.text.length() > p_146203_1_) {
         this.text = this.text.substring(0, p_146203_1_);
         this.func_212951_d(this.text);
      }

   }

   private int getMaxStringLength() {
      return this.maxStringLength;
   }

   public int getCursorPosition() {
      return this.cursorPosition;
   }

   private boolean getEnableBackgroundDrawing() {
      return this.enableBackgroundDrawing;
   }

   public void setEnableBackgroundDrawing(boolean p_146185_1_) {
      this.enableBackgroundDrawing = p_146185_1_;
   }

   public void setTextColor(int p_146193_1_) {
      this.enabledColor = p_146193_1_;
   }

   public void setDisabledTextColour(int p_146204_1_) {
      this.disabledColor = p_146204_1_;
   }

   public boolean changeFocus(boolean p_changeFocus_1_) {
      return this.visible && this.isEnabled ? super.changeFocus(p_changeFocus_1_) : false;
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return this.visible && p_isMouseOver_1_ >= (double)this.x && p_isMouseOver_1_ < (double)(this.x + this.width) && p_isMouseOver_3_ >= (double)this.y && p_isMouseOver_3_ < (double)(this.y + this.height);
   }

   protected void onFocusedChanged(boolean p_onFocusedChanged_1_) {
      if (p_onFocusedChanged_1_) {
         this.cursorCounter = 0;
      }

   }

   private boolean isEnabled() {
      return this.isEnabled;
   }

   public void setEnabled(boolean p_146184_1_) {
      this.isEnabled = p_146184_1_;
   }

   public int getAdjustedWidth() {
      return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
   }

   public void setSelectionPos(int p_146199_1_) {
      int lvt_2_1_ = this.text.length();
      this.selectionEnd = MathHelper.clamp(p_146199_1_, 0, lvt_2_1_);
      if (this.fontRenderer != null) {
         if (this.lineScrollOffset > lvt_2_1_) {
            this.lineScrollOffset = lvt_2_1_;
         }

         int lvt_3_1_ = this.getAdjustedWidth();
         String lvt_4_1_ = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), lvt_3_1_);
         int lvt_5_1_ = lvt_4_1_.length() + this.lineScrollOffset;
         if (this.selectionEnd == this.lineScrollOffset) {
            this.lineScrollOffset -= this.fontRenderer.trimStringToWidth(this.text, lvt_3_1_, true).length();
         }

         if (this.selectionEnd > lvt_5_1_) {
            this.lineScrollOffset += this.selectionEnd - lvt_5_1_;
         } else if (this.selectionEnd <= this.lineScrollOffset) {
            this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;
         }

         this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, lvt_2_1_);
      }

   }

   public void setCanLoseFocus(boolean p_146205_1_) {
      this.canLoseFocus = p_146205_1_;
   }

   public boolean getVisible() {
      return this.visible;
   }

   public void setVisible(boolean p_146189_1_) {
      this.visible = p_146189_1_;
   }

   public void setSuggestion(@Nullable String p_195612_1_) {
      this.suggestion = p_195612_1_;
   }

   public int func_195611_j(int p_195611_1_) {
      return p_195611_1_ > this.text.length() ? this.x : this.x + this.fontRenderer.getStringWidth(this.text.substring(0, p_195611_1_));
   }

   public void setX(int p_212952_1_) {
      this.x = p_212952_1_;
   }
}
