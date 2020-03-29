package net.minecraftforge.fml;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.commons.lang3.text.ExtendedMessageFormat;
import org.apache.commons.lang3.text.FormatFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeI18n {
   private static final Logger LOGGER = LogManager.getLogger();
   private static Map<String, String> i18n;
   private static Map<String, FormatFactory> customFactories = new HashMap();

   private static void parseException(String formatString, StringBuffer stringBuffer, Object objectToParse) {
      Throwable t = (Throwable)objectToParse;
      if (Objects.equals(formatString, "msg")) {
         stringBuffer.append(t.getMessage());
      } else if (Objects.equals(formatString, "cls")) {
         stringBuffer.append(t.getClass());
      }

   }

   private static void parseModInfo(String formatString, StringBuffer stringBuffer, Object modInfo) {
      ModInfo info = (ModInfo)modInfo;
      if (Objects.equals(formatString, "id")) {
         stringBuffer.append(info.getModId());
      } else if (Objects.equals(formatString, "name")) {
         stringBuffer.append(info.getDisplayName());
      }

   }

   public static String getPattern(String patternName) {
      return i18n == null ? patternName : (String)i18n.getOrDefault(patternName, patternName);
   }

   public static void loadLanguageData(Map<String, String> properties) {
      LOGGER.debug(Logging.CORE, "Loading I18N data entries: {}", properties.size());
      i18n = properties;
   }

   public static String parseMessage(String i18nMessage, Object... args) {
      String pattern = getPattern(i18nMessage);

      try {
         return parseFormat(pattern, args);
      } catch (IllegalArgumentException var4) {
         LOGGER.error(Logging.CORE, "Illegal format found `{}`", pattern);
         return pattern;
      }
   }

   public static String parseFormat(String format, Object... args) {
      ExtendedMessageFormat extendedMessageFormat = new ExtendedMessageFormat(format, customFactories);
      return extendedMessageFormat.format(args);
   }

   static {
      customFactories.put("modinfo", (name, formatString, locale) -> {
         return new ForgeI18n.CustomReadOnlyFormat((stringBuffer, objectToParse) -> {
            parseModInfo(formatString, stringBuffer, objectToParse);
         });
      });
      customFactories.put("lower", (name, formatString, locale) -> {
         return new ForgeI18n.CustomReadOnlyFormat((stringBuffer, objectToParse) -> {
            stringBuffer.append(StringUtils.toLowerCase(String.valueOf(objectToParse)));
         });
      });
      customFactories.put("upper", (name, formatString, locale) -> {
         return new ForgeI18n.CustomReadOnlyFormat((stringBuffer, objectToParse) -> {
            stringBuffer.append(StringUtils.toUpperCase(String.valueOf(objectToParse)));
         });
      });
      customFactories.put("exc", (name, formatString, locale) -> {
         return new ForgeI18n.CustomReadOnlyFormat((stringBuffer, objectToParse) -> {
            parseException(formatString, stringBuffer, objectToParse);
         });
      });
      customFactories.put("vr", (name, formatString, locale) -> {
         return new ForgeI18n.CustomReadOnlyFormat((stringBuffer, o) -> {
            MavenVersionStringHelper.parseVersionRange(formatString, stringBuffer, o);
         });
      });
      customFactories.put("i18n", (name, formatString, locale) -> {
         return new ForgeI18n.CustomReadOnlyFormat((stringBuffer, o) -> {
            stringBuffer.append(parseMessage(formatString, o));
         });
      });
      customFactories.put("ornull", (name, formatString, locale) -> {
         return new ForgeI18n.CustomReadOnlyFormat((stringBuffer, o) -> {
            stringBuffer.append(Objects.equals(String.valueOf(o), "null") ? parseMessage(formatString) : String.valueOf(o));
         });
      });
   }

   public static class CustomReadOnlyFormat extends Format {
      private final BiConsumer<StringBuffer, Object> formatter;

      CustomReadOnlyFormat(BiConsumer<StringBuffer, Object> formatter) {
         this.formatter = formatter;
      }

      public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
         this.formatter.accept(toAppendTo, obj);
         return toAppendTo;
      }

      public Object parseObject(String source, ParsePosition pos) {
         throw new UnsupportedOperationException("Parsing is not supported");
      }
   }
}
