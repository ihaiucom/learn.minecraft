package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JSONBlendingMode {
   private static JSONBlendingMode lastApplied;
   private final int srcColorFactor;
   private final int srcAlphaFactor;
   private final int destColorFactor;
   private final int destAlphaFactor;
   private final int blendFunction;
   private final boolean separateBlend;
   private final boolean opaque;

   private JSONBlendingMode(boolean p_i45084_1_, boolean p_i45084_2_, int p_i45084_3_, int p_i45084_4_, int p_i45084_5_, int p_i45084_6_, int p_i45084_7_) {
      this.separateBlend = p_i45084_1_;
      this.srcColorFactor = p_i45084_3_;
      this.destColorFactor = p_i45084_4_;
      this.srcAlphaFactor = p_i45084_5_;
      this.destAlphaFactor = p_i45084_6_;
      this.opaque = p_i45084_2_;
      this.blendFunction = p_i45084_7_;
   }

   public JSONBlendingMode() {
      this(false, true, 1, 0, 1, 0, 32774);
   }

   public JSONBlendingMode(int p_i45085_1_, int p_i45085_2_, int p_i45085_3_) {
      this(false, false, p_i45085_1_, p_i45085_2_, p_i45085_1_, p_i45085_2_, p_i45085_3_);
   }

   public JSONBlendingMode(int p_i45086_1_, int p_i45086_2_, int p_i45086_3_, int p_i45086_4_, int p_i45086_5_) {
      this(true, false, p_i45086_1_, p_i45086_2_, p_i45086_3_, p_i45086_4_, p_i45086_5_);
   }

   public void apply() {
      if (!this.equals(lastApplied)) {
         if (lastApplied == null || this.opaque != lastApplied.isOpaque()) {
            lastApplied = this;
            if (this.opaque) {
               RenderSystem.disableBlend();
               return;
            }

            RenderSystem.enableBlend();
         }

         RenderSystem.blendEquation(this.blendFunction);
         if (this.separateBlend) {
            RenderSystem.blendFuncSeparate(this.srcColorFactor, this.destColorFactor, this.srcAlphaFactor, this.destAlphaFactor);
         } else {
            RenderSystem.blendFunc(this.srcColorFactor, this.destColorFactor);
         }

      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof JSONBlendingMode)) {
         return false;
      } else {
         JSONBlendingMode lvt_2_1_ = (JSONBlendingMode)p_equals_1_;
         if (this.blendFunction != lvt_2_1_.blendFunction) {
            return false;
         } else if (this.destAlphaFactor != lvt_2_1_.destAlphaFactor) {
            return false;
         } else if (this.destColorFactor != lvt_2_1_.destColorFactor) {
            return false;
         } else if (this.opaque != lvt_2_1_.opaque) {
            return false;
         } else if (this.separateBlend != lvt_2_1_.separateBlend) {
            return false;
         } else if (this.srcAlphaFactor != lvt_2_1_.srcAlphaFactor) {
            return false;
         } else {
            return this.srcColorFactor == lvt_2_1_.srcColorFactor;
         }
      }
   }

   public int hashCode() {
      int lvt_1_1_ = this.srcColorFactor;
      lvt_1_1_ = 31 * lvt_1_1_ + this.srcAlphaFactor;
      lvt_1_1_ = 31 * lvt_1_1_ + this.destColorFactor;
      lvt_1_1_ = 31 * lvt_1_1_ + this.destAlphaFactor;
      lvt_1_1_ = 31 * lvt_1_1_ + this.blendFunction;
      lvt_1_1_ = 31 * lvt_1_1_ + (this.separateBlend ? 1 : 0);
      lvt_1_1_ = 31 * lvt_1_1_ + (this.opaque ? 1 : 0);
      return lvt_1_1_;
   }

   public boolean isOpaque() {
      return this.opaque;
   }

   public static int stringToBlendFunction(String p_148108_0_) {
      String lvt_1_1_ = p_148108_0_.trim().toLowerCase(Locale.ROOT);
      if ("add".equals(lvt_1_1_)) {
         return 32774;
      } else if ("subtract".equals(lvt_1_1_)) {
         return 32778;
      } else if ("reversesubtract".equals(lvt_1_1_)) {
         return 32779;
      } else if ("reverse_subtract".equals(lvt_1_1_)) {
         return 32779;
      } else if ("min".equals(lvt_1_1_)) {
         return 32775;
      } else {
         return "max".equals(lvt_1_1_) ? '耈' : '耆';
      }
   }

   public static int stringToBlendFactor(String p_148107_0_) {
      String lvt_1_1_ = p_148107_0_.trim().toLowerCase(Locale.ROOT);
      lvt_1_1_ = lvt_1_1_.replaceAll("_", "");
      lvt_1_1_ = lvt_1_1_.replaceAll("one", "1");
      lvt_1_1_ = lvt_1_1_.replaceAll("zero", "0");
      lvt_1_1_ = lvt_1_1_.replaceAll("minus", "-");
      if ("0".equals(lvt_1_1_)) {
         return 0;
      } else if ("1".equals(lvt_1_1_)) {
         return 1;
      } else if ("srccolor".equals(lvt_1_1_)) {
         return 768;
      } else if ("1-srccolor".equals(lvt_1_1_)) {
         return 769;
      } else if ("dstcolor".equals(lvt_1_1_)) {
         return 774;
      } else if ("1-dstcolor".equals(lvt_1_1_)) {
         return 775;
      } else if ("srcalpha".equals(lvt_1_1_)) {
         return 770;
      } else if ("1-srcalpha".equals(lvt_1_1_)) {
         return 771;
      } else if ("dstalpha".equals(lvt_1_1_)) {
         return 772;
      } else {
         return "1-dstalpha".equals(lvt_1_1_) ? 773 : -1;
      }
   }
}
