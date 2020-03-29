package net.minecraft.client.resources;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ForgeI18n;

@OnlyIn(Dist.CLIENT)
public class I18n {
   private static Locale i18nLocale;

   static void setLocale(Locale p_135051_0_) {
      i18nLocale = p_135051_0_;
      ForgeI18n.loadLanguageData(i18nLocale.properties);
   }

   public static String format(String p_135052_0_, Object... p_135052_1_) {
      return i18nLocale.formatMessage(p_135052_0_, p_135052_1_);
   }

   public static boolean hasKey(String p_188566_0_) {
      return i18nLocale.hasKey(p_188566_0_);
   }
}
