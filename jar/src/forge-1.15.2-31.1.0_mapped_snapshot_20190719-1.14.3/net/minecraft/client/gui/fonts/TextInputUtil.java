package net.minecraft.client.gui.fonts;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextInputUtil {
   private final Minecraft field_216900_a;
   private final FontRenderer field_216901_b;
   private final Supplier<String> field_216902_c;
   private final Consumer<String> field_216903_d;
   private final int field_216904_e;
   private int field_216905_f;
   private int field_216906_g;

   public TextInputUtil(Minecraft p_i51124_1_, Supplier<String> p_i51124_2_, Consumer<String> p_i51124_3_, int p_i51124_4_) {
      this.field_216900_a = p_i51124_1_;
      this.field_216901_b = p_i51124_1_.fontRenderer;
      this.field_216902_c = p_i51124_2_;
      this.field_216903_d = p_i51124_3_;
      this.field_216904_e = p_i51124_4_;
      this.func_216899_b();
   }

   public boolean func_216894_a(char p_216894_1_) {
      if (SharedConstants.isAllowedCharacter(p_216894_1_)) {
         this.func_216892_a(Character.toString(p_216894_1_));
      }

      return true;
   }

   private void func_216892_a(String p_216892_1_) {
      if (this.field_216906_g != this.field_216905_f) {
         this.func_216893_f();
      }

      String lvt_2_1_ = (String)this.field_216902_c.get();
      this.field_216905_f = MathHelper.clamp(this.field_216905_f, 0, lvt_2_1_.length());
      String lvt_3_1_ = (new StringBuilder(lvt_2_1_)).insert(this.field_216905_f, p_216892_1_).toString();
      if (this.field_216901_b.getStringWidth(lvt_3_1_) <= this.field_216904_e) {
         this.field_216903_d.accept(lvt_3_1_);
         this.field_216906_g = this.field_216905_f = Math.min(lvt_3_1_.length(), this.field_216905_f + p_216892_1_.length());
      }

   }

   public boolean func_216897_a(int p_216897_1_) {
      String lvt_2_1_ = (String)this.field_216902_c.get();
      if (Screen.isSelectAll(p_216897_1_)) {
         this.field_216906_g = 0;
         this.field_216905_f = lvt_2_1_.length();
         return true;
      } else if (Screen.isCopy(p_216897_1_)) {
         this.field_216900_a.keyboardListener.setClipboardString(this.func_216895_e());
         return true;
      } else if (Screen.isPaste(p_216897_1_)) {
         this.func_216892_a(SharedConstants.filterAllowedCharacters(TextFormatting.getTextWithoutFormattingCodes(this.field_216900_a.keyboardListener.getClipboardString().replaceAll("\\r", ""))));
         this.field_216906_g = this.field_216905_f;
         return true;
      } else if (Screen.isCut(p_216897_1_)) {
         this.field_216900_a.keyboardListener.setClipboardString(this.func_216895_e());
         this.func_216893_f();
         return true;
      } else if (p_216897_1_ == 259) {
         if (!lvt_2_1_.isEmpty()) {
            if (this.field_216906_g != this.field_216905_f) {
               this.func_216893_f();
            } else if (this.field_216905_f > 0) {
               lvt_2_1_ = (new StringBuilder(lvt_2_1_)).deleteCharAt(Math.max(0, this.field_216905_f - 1)).toString();
               this.field_216906_g = this.field_216905_f = Math.max(0, this.field_216905_f - 1);
               this.field_216903_d.accept(lvt_2_1_);
            }
         }

         return true;
      } else if (p_216897_1_ == 261) {
         if (!lvt_2_1_.isEmpty()) {
            if (this.field_216906_g != this.field_216905_f) {
               this.func_216893_f();
            } else if (this.field_216905_f < lvt_2_1_.length()) {
               lvt_2_1_ = (new StringBuilder(lvt_2_1_)).deleteCharAt(Math.max(0, this.field_216905_f)).toString();
               this.field_216903_d.accept(lvt_2_1_);
            }
         }

         return true;
      } else {
         int lvt_3_2_;
         if (p_216897_1_ == 263) {
            lvt_3_2_ = this.field_216901_b.getBidiFlag() ? 1 : -1;
            if (Screen.hasControlDown()) {
               this.field_216905_f = this.field_216901_b.func_216863_a(lvt_2_1_, lvt_3_2_, this.field_216905_f, true);
            } else {
               this.field_216905_f = Math.max(0, Math.min(lvt_2_1_.length(), this.field_216905_f + lvt_3_2_));
            }

            if (!Screen.hasShiftDown()) {
               this.field_216906_g = this.field_216905_f;
            }

            return true;
         } else if (p_216897_1_ == 262) {
            lvt_3_2_ = this.field_216901_b.getBidiFlag() ? -1 : 1;
            if (Screen.hasControlDown()) {
               this.field_216905_f = this.field_216901_b.func_216863_a(lvt_2_1_, lvt_3_2_, this.field_216905_f, true);
            } else {
               this.field_216905_f = Math.max(0, Math.min(lvt_2_1_.length(), this.field_216905_f + lvt_3_2_));
            }

            if (!Screen.hasShiftDown()) {
               this.field_216906_g = this.field_216905_f;
            }

            return true;
         } else if (p_216897_1_ == 268) {
            this.field_216905_f = 0;
            if (!Screen.hasShiftDown()) {
               this.field_216906_g = this.field_216905_f;
            }

            return true;
         } else if (p_216897_1_ == 269) {
            this.field_216905_f = ((String)this.field_216902_c.get()).length();
            if (!Screen.hasShiftDown()) {
               this.field_216906_g = this.field_216905_f;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private String func_216895_e() {
      String lvt_1_1_ = (String)this.field_216902_c.get();
      int lvt_2_1_ = Math.min(this.field_216905_f, this.field_216906_g);
      int lvt_3_1_ = Math.max(this.field_216905_f, this.field_216906_g);
      return lvt_1_1_.substring(lvt_2_1_, lvt_3_1_);
   }

   private void func_216893_f() {
      if (this.field_216906_g != this.field_216905_f) {
         String lvt_1_1_ = (String)this.field_216902_c.get();
         int lvt_2_1_ = Math.min(this.field_216905_f, this.field_216906_g);
         int lvt_3_1_ = Math.max(this.field_216905_f, this.field_216906_g);
         String lvt_4_1_ = lvt_1_1_.substring(0, lvt_2_1_) + lvt_1_1_.substring(lvt_3_1_);
         this.field_216905_f = lvt_2_1_;
         this.field_216906_g = this.field_216905_f;
         this.field_216903_d.accept(lvt_4_1_);
      }
   }

   public void func_216899_b() {
      this.field_216906_g = this.field_216905_f = ((String)this.field_216902_c.get()).length();
   }

   public int func_216896_c() {
      return this.field_216905_f;
   }

   public int func_216898_d() {
      return this.field_216906_g;
   }
}
