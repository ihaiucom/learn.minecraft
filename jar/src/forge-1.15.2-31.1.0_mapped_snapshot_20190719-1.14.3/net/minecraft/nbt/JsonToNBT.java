package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.util.text.TranslationTextComponent;

public class JsonToNBT {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(new TranslationTextComponent("argument.nbt.trailing", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(new TranslationTextComponent("argument.nbt.expected.key", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.nbt.expected.value", new Object[0]));
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((p_208775_0_, p_208775_1_) -> {
      return new TranslationTextComponent("argument.nbt.list.mixed", new Object[]{p_208775_0_, p_208775_1_});
   });
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((p_208774_0_, p_208774_1_) -> {
      return new TranslationTextComponent("argument.nbt.array.mixed", new Object[]{p_208774_0_, p_208774_1_});
   });
   public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((p_208773_0_) -> {
      return new TranslationTextComponent("argument.nbt.array.invalid", new Object[]{p_208773_0_});
   });
   private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
   private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
   private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
   private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
   private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
   private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
   private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
   private final StringReader reader;

   public static CompoundNBT getTagFromJson(String p_180713_0_) throws CommandSyntaxException {
      return (new JsonToNBT(new StringReader(p_180713_0_))).readSingleStruct();
   }

   @VisibleForTesting
   CompoundNBT readSingleStruct() throws CommandSyntaxException {
      CompoundNBT lvt_1_1_ = this.readStruct();
      this.reader.skipWhitespace();
      if (this.reader.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext(this.reader);
      } else {
         return lvt_1_1_;
      }
   }

   public JsonToNBT(StringReader p_i47948_1_) {
      this.reader = p_i47948_1_;
   }

   protected String readKey() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
      } else {
         return this.reader.readString();
      }
   }

   protected INBT readTypedValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      int lvt_1_1_ = this.reader.getCursor();
      if (StringReader.isQuotedStringStart(this.reader.peek())) {
         return StringNBT.func_229705_a_(this.reader.readQuotedString());
      } else {
         String lvt_2_1_ = this.reader.readUnquotedString();
         if (lvt_2_1_.isEmpty()) {
            this.reader.setCursor(lvt_1_1_);
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
         } else {
            return this.type(lvt_2_1_);
         }
      }
   }

   private INBT type(String p_193596_1_) {
      try {
         if (FLOAT_PATTERN.matcher(p_193596_1_).matches()) {
            return FloatNBT.func_229689_a_(Float.parseFloat(p_193596_1_.substring(0, p_193596_1_.length() - 1)));
         }

         if (BYTE_PATTERN.matcher(p_193596_1_).matches()) {
            return ByteNBT.func_229671_a_(Byte.parseByte(p_193596_1_.substring(0, p_193596_1_.length() - 1)));
         }

         if (LONG_PATTERN.matcher(p_193596_1_).matches()) {
            return LongNBT.func_229698_a_(Long.parseLong(p_193596_1_.substring(0, p_193596_1_.length() - 1)));
         }

         if (SHORT_PATTERN.matcher(p_193596_1_).matches()) {
            return ShortNBT.func_229701_a_(Short.parseShort(p_193596_1_.substring(0, p_193596_1_.length() - 1)));
         }

         if (INT_PATTERN.matcher(p_193596_1_).matches()) {
            return IntNBT.func_229692_a_(Integer.parseInt(p_193596_1_));
         }

         if (DOUBLE_PATTERN.matcher(p_193596_1_).matches()) {
            return DoubleNBT.func_229684_a_(Double.parseDouble(p_193596_1_.substring(0, p_193596_1_.length() - 1)));
         }

         if (DOUBLE_PATTERN_NOSUFFIX.matcher(p_193596_1_).matches()) {
            return DoubleNBT.func_229684_a_(Double.parseDouble(p_193596_1_));
         }

         if ("true".equalsIgnoreCase(p_193596_1_)) {
            return ByteNBT.field_229670_c_;
         }

         if ("false".equalsIgnoreCase(p_193596_1_)) {
            return ByteNBT.field_229669_b_;
         }
      } catch (NumberFormatException var3) {
      }

      return StringNBT.func_229705_a_(p_193596_1_);
   }

   public INBT readValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         char lvt_1_1_ = this.reader.peek();
         if (lvt_1_1_ == '{') {
            return this.readStruct();
         } else {
            return lvt_1_1_ == '[' ? this.readList() : this.readTypedValue();
         }
      }
   }

   protected INBT readList() throws CommandSyntaxException {
      return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
   }

   public CompoundNBT readStruct() throws CommandSyntaxException {
      this.expect('{');
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      this.reader.skipWhitespace();

      while(this.reader.canRead() && this.reader.peek() != '}') {
         int lvt_2_1_ = this.reader.getCursor();
         String lvt_3_1_ = this.readKey();
         if (lvt_3_1_.isEmpty()) {
            this.reader.setCursor(lvt_2_1_);
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }

         this.expect(':');
         lvt_1_1_.put(lvt_3_1_, this.readValue());
         if (!this.hasElementSeparator()) {
            break;
         }

         if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }
      }

      this.expect('}');
      return lvt_1_1_;
   }

   private INBT readListTag() throws CommandSyntaxException {
      this.expect('[');
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         ListNBT lvt_1_1_ = new ListNBT();
         INBTType lvt_2_1_ = null;

         while(this.reader.peek() != ']') {
            int lvt_3_1_ = this.reader.getCursor();
            INBT lvt_4_1_ = this.readValue();
            INBTType<?> lvt_5_1_ = lvt_4_1_.func_225647_b_();
            if (lvt_2_1_ == null) {
               lvt_2_1_ = lvt_5_1_;
            } else if (lvt_5_1_ != lvt_2_1_) {
               this.reader.setCursor(lvt_3_1_);
               throw ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, lvt_5_1_.func_225650_b_(), lvt_2_1_.func_225650_b_());
            }

            lvt_1_1_.add(lvt_4_1_);
            if (!this.hasElementSeparator()) {
               break;
            }

            if (!this.reader.canRead()) {
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            }
         }

         this.expect(']');
         return lvt_1_1_;
      }
   }

   private INBT readArrayTag() throws CommandSyntaxException {
      this.expect('[');
      int lvt_1_1_ = this.reader.getCursor();
      char lvt_2_1_ = this.reader.read();
      this.reader.read();
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else if (lvt_2_1_ == 'B') {
         return new ByteArrayNBT(this.func_229706_a_(ByteArrayNBT.field_229667_a_, ByteNBT.field_229668_a_));
      } else if (lvt_2_1_ == 'L') {
         return new LongArrayNBT(this.func_229706_a_(LongArrayNBT.field_229696_a_, LongNBT.field_229697_a_));
      } else if (lvt_2_1_ == 'I') {
         return new IntArrayNBT(this.func_229706_a_(IntArrayNBT.field_229690_a_, IntNBT.field_229691_a_));
      } else {
         this.reader.setCursor(lvt_1_1_);
         throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(lvt_2_1_));
      }
   }

   private <T extends Number> List<T> func_229706_a_(INBTType<?> p_229706_1_, INBTType<?> p_229706_2_) throws CommandSyntaxException {
      ArrayList lvt_3_1_ = Lists.newArrayList();

      while(true) {
         if (this.reader.peek() != ']') {
            int lvt_4_1_ = this.reader.getCursor();
            INBT lvt_5_1_ = this.readValue();
            INBTType<?> lvt_6_1_ = lvt_5_1_.func_225647_b_();
            if (lvt_6_1_ != p_229706_2_) {
               this.reader.setCursor(lvt_4_1_);
               throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, lvt_6_1_.func_225650_b_(), p_229706_1_.func_225650_b_());
            }

            if (p_229706_2_ == ByteNBT.field_229668_a_) {
               lvt_3_1_.add(((NumberNBT)lvt_5_1_).getByte());
            } else if (p_229706_2_ == LongNBT.field_229697_a_) {
               lvt_3_1_.add(((NumberNBT)lvt_5_1_).getLong());
            } else {
               lvt_3_1_.add(((NumberNBT)lvt_5_1_).getInt());
            }

            if (this.hasElementSeparator()) {
               if (!this.reader.canRead()) {
                  throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
               }
               continue;
            }
         }

         this.expect(']');
         return lvt_3_1_;
      }
   }

   private boolean hasElementSeparator() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == ',') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   private void expect(char p_193604_1_) throws CommandSyntaxException {
      this.reader.skipWhitespace();
      this.reader.expect(p_193604_1_);
   }
}
