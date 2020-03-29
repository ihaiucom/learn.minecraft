package net.minecraft.util.text;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum TextFormatting {
   BLACK("BLACK", '0', 0, 0),
   DARK_BLUE("DARK_BLUE", '1', 1, 170),
   DARK_GREEN("DARK_GREEN", '2', 2, 43520),
   DARK_AQUA("DARK_AQUA", '3', 3, 43690),
   DARK_RED("DARK_RED", '4', 4, 11141120),
   DARK_PURPLE("DARK_PURPLE", '5', 5, 11141290),
   GOLD("GOLD", '6', 6, 16755200),
   GRAY("GRAY", '7', 7, 11184810),
   DARK_GRAY("DARK_GRAY", '8', 8, 5592405),
   BLUE("BLUE", '9', 9, 5592575),
   GREEN("GREEN", 'a', 10, 5635925),
   AQUA("AQUA", 'b', 11, 5636095),
   RED("RED", 'c', 12, 16733525),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13, 16733695),
   YELLOW("YELLOW", 'e', 14, 16777045),
   WHITE("WHITE", 'f', 15, 16777215),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1, (Integer)null);

   private static final Map<String, TextFormatting> NAME_MAPPING = (Map)Arrays.stream(values()).collect(Collectors.toMap((p_199746_0_) -> {
      return lowercaseAlpha(p_199746_0_.name);
   }, (p_199747_0_) -> {
      return p_199747_0_;
   }));
   private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
   private final String name;
   private final char formattingCode;
   private final boolean fancyStyling;
   private final String controlString;
   private final int colorIndex;
   @Nullable
   private final Integer color;

   private static String lowercaseAlpha(String p_175745_0_) {
      return p_175745_0_.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
   }

   private TextFormatting(String p_i49745_3_, char p_i49745_4_, int p_i49745_5_, @Nullable Integer p_i49745_6_) {
      this(p_i49745_3_, p_i49745_4_, false, p_i49745_5_, p_i49745_6_);
   }

   private TextFormatting(String p_i46292_3_, char p_i46292_4_, boolean p_i46292_5_) {
      this(p_i46292_3_, p_i46292_4_, p_i46292_5_, -1, (Integer)null);
   }

   private TextFormatting(String p_i49746_3_, char p_i49746_4_, boolean p_i49746_5_, int p_i49746_6_, @Nullable Integer p_i49746_7_) {
      this.name = p_i49746_3_;
      this.formattingCode = p_i49746_4_;
      this.fancyStyling = p_i49746_5_;
      this.colorIndex = p_i49746_6_;
      this.color = p_i49746_7_;
      this.controlString = "§" + p_i49746_4_;
   }

   @OnlyIn(Dist.CLIENT)
   public static String getFormatString(String p_211164_0_) {
      StringBuilder lvt_1_1_ = new StringBuilder();
      int lvt_2_1_ = -1;
      int lvt_3_1_ = p_211164_0_.length();

      while((lvt_2_1_ = p_211164_0_.indexOf(167, lvt_2_1_ + 1)) != -1) {
         if (lvt_2_1_ < lvt_3_1_ - 1) {
            TextFormatting lvt_4_1_ = fromFormattingCode(p_211164_0_.charAt(lvt_2_1_ + 1));
            if (lvt_4_1_ != null) {
               if (lvt_4_1_.isNormalStyle()) {
                  lvt_1_1_.setLength(0);
               }

               if (lvt_4_1_ != RESET) {
                  lvt_1_1_.append(lvt_4_1_);
               }
            }
         }
      }

      return lvt_1_1_.toString();
   }

   public int getColorIndex() {
      return this.colorIndex;
   }

   public boolean isFancyStyling() {
      return this.fancyStyling;
   }

   public boolean isColor() {
      return !this.fancyStyling && this != RESET;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Integer getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNormalStyle() {
      return !this.fancyStyling;
   }

   public String getFriendlyName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public String toString() {
      return this.controlString;
   }

   @Nullable
   public static String getTextWithoutFormattingCodes(@Nullable String p_110646_0_) {
      return p_110646_0_ == null ? null : FORMATTING_CODE_PATTERN.matcher(p_110646_0_).replaceAll("");
   }

   @Nullable
   public static TextFormatting getValueByName(@Nullable String p_96300_0_) {
      return p_96300_0_ == null ? null : (TextFormatting)NAME_MAPPING.get(lowercaseAlpha(p_96300_0_));
   }

   @Nullable
   public static TextFormatting fromColorIndex(int p_175744_0_) {
      if (p_175744_0_ < 0) {
         return RESET;
      } else {
         TextFormatting[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            TextFormatting lvt_4_1_ = var1[var3];
            if (lvt_4_1_.getColorIndex() == p_175744_0_) {
               return lvt_4_1_;
            }
         }

         return null;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static TextFormatting fromFormattingCode(char p_211165_0_) {
      char lvt_1_1_ = Character.toString(p_211165_0_).toLowerCase(Locale.ROOT).charAt(0);
      TextFormatting[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TextFormatting lvt_5_1_ = var2[var4];
         if (lvt_5_1_.formattingCode == lvt_1_1_) {
            return lvt_5_1_;
         }
      }

      return null;
   }

   public static Collection<String> getValidValues(boolean p_96296_0_, boolean p_96296_1_) {
      List<String> lvt_2_1_ = Lists.newArrayList();
      TextFormatting[] var3 = values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TextFormatting lvt_6_1_ = var3[var5];
         if ((!lvt_6_1_.isColor() || p_96296_0_) && (!lvt_6_1_.isFancyStyling() || p_96296_1_)) {
            lvt_2_1_.add(lvt_6_1_.getFriendlyName());
         }
      }

      return lvt_2_1_;
   }
}
