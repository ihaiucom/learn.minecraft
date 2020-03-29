package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IStatFormatter {
   DecimalFormat DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("########0.00"), (p_223254_0_) -> {
      p_223254_0_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   IStatFormatter DEFAULT;
   IStatFormatter DIVIDE_BY_TEN;
   IStatFormatter DISTANCE;
   IStatFormatter TIME;

   @OnlyIn(Dist.CLIENT)
   String format(int var1);

   static {
      NumberFormat var10000 = NumberFormat.getIntegerInstance(Locale.US);
      DEFAULT = var10000::format;
      DIVIDE_BY_TEN = (p_223256_0_) -> {
         return DECIMAL_FORMAT.format((double)p_223256_0_ * 0.1D);
      };
      DISTANCE = (p_223255_0_) -> {
         double lvt_1_1_ = (double)p_223255_0_ / 100.0D;
         double lvt_3_1_ = lvt_1_1_ / 1000.0D;
         if (lvt_3_1_ > 0.5D) {
            return DECIMAL_FORMAT.format(lvt_3_1_) + " km";
         } else {
            return lvt_1_1_ > 0.5D ? DECIMAL_FORMAT.format(lvt_1_1_) + " m" : p_223255_0_ + " cm";
         }
      };
      TIME = (p_223253_0_) -> {
         double lvt_1_1_ = (double)p_223253_0_ / 20.0D;
         double lvt_3_1_ = lvt_1_1_ / 60.0D;
         double lvt_5_1_ = lvt_3_1_ / 60.0D;
         double lvt_7_1_ = lvt_5_1_ / 24.0D;
         double lvt_9_1_ = lvt_7_1_ / 365.0D;
         if (lvt_9_1_ > 0.5D) {
            return DECIMAL_FORMAT.format(lvt_9_1_) + " y";
         } else if (lvt_7_1_ > 0.5D) {
            return DECIMAL_FORMAT.format(lvt_7_1_) + " d";
         } else if (lvt_5_1_ > 0.5D) {
            return DECIMAL_FORMAT.format(lvt_5_1_) + " h";
         } else {
            return lvt_3_1_ > 0.5D ? DECIMAL_FORMAT.format(lvt_3_1_) + " m" : lvt_1_1_ + " s";
         }
      };
   }
}
