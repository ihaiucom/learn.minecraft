package net.minecraft.advancements.criterion;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.text.TranslationTextComponent;

public class MinMaxBoundsWrapped {
   public static final MinMaxBoundsWrapped UNBOUNDED = new MinMaxBoundsWrapped((Float)null, (Float)null);
   public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.ints", new Object[0]));
   private final Float min;
   private final Float max;

   public MinMaxBoundsWrapped(@Nullable Float p_i49328_1_, @Nullable Float p_i49328_2_) {
      this.min = p_i49328_1_;
      this.max = p_i49328_2_;
   }

   @Nullable
   public Float getMin() {
      return this.min;
   }

   @Nullable
   public Float getMax() {
      return this.max;
   }

   public static MinMaxBoundsWrapped func_207921_a(StringReader p_207921_0_, boolean p_207921_1_, Function<Float, Float> p_207921_2_) throws CommandSyntaxException {
      if (!p_207921_0_.canRead()) {
         throw MinMaxBounds.ERROR_EMPTY.createWithContext(p_207921_0_);
      } else {
         int lvt_3_1_ = p_207921_0_.getCursor();
         Float lvt_4_1_ = map(func_207924_b(p_207921_0_, p_207921_1_), p_207921_2_);
         Float lvt_5_2_;
         if (p_207921_0_.canRead(2) && p_207921_0_.peek() == '.' && p_207921_0_.peek(1) == '.') {
            p_207921_0_.skip();
            p_207921_0_.skip();
            lvt_5_2_ = map(func_207924_b(p_207921_0_, p_207921_1_), p_207921_2_);
            if (lvt_4_1_ == null && lvt_5_2_ == null) {
               p_207921_0_.setCursor(lvt_3_1_);
               throw MinMaxBounds.ERROR_EMPTY.createWithContext(p_207921_0_);
            }
         } else {
            if (!p_207921_1_ && p_207921_0_.canRead() && p_207921_0_.peek() == '.') {
               p_207921_0_.setCursor(lvt_3_1_);
               throw ERROR_INTS_ONLY.createWithContext(p_207921_0_);
            }

            lvt_5_2_ = lvt_4_1_;
         }

         if (lvt_4_1_ == null && lvt_5_2_ == null) {
            p_207921_0_.setCursor(lvt_3_1_);
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(p_207921_0_);
         } else {
            return new MinMaxBoundsWrapped(lvt_4_1_, lvt_5_2_);
         }
      }
   }

   @Nullable
   private static Float func_207924_b(StringReader p_207924_0_, boolean p_207924_1_) throws CommandSyntaxException {
      int lvt_2_1_ = p_207924_0_.getCursor();

      while(p_207924_0_.canRead() && func_207920_c(p_207924_0_, p_207924_1_)) {
         p_207924_0_.skip();
      }

      String lvt_3_1_ = p_207924_0_.getString().substring(lvt_2_1_, p_207924_0_.getCursor());
      if (lvt_3_1_.isEmpty()) {
         return null;
      } else {
         try {
            return Float.parseFloat(lvt_3_1_);
         } catch (NumberFormatException var5) {
            if (p_207924_1_) {
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(p_207924_0_, lvt_3_1_);
            } else {
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(p_207924_0_, lvt_3_1_);
            }
         }
      }
   }

   private static boolean func_207920_c(StringReader p_207920_0_, boolean p_207920_1_) {
      char lvt_2_1_ = p_207920_0_.peek();
      if ((lvt_2_1_ < '0' || lvt_2_1_ > '9') && lvt_2_1_ != '-') {
         if (p_207920_1_ && lvt_2_1_ == '.') {
            return !p_207920_0_.canRead(2) || p_207920_0_.peek(1) != '.';
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Nullable
   private static Float map(@Nullable Float p_207922_0_, Function<Float, Float> p_207922_1_) {
      return p_207922_0_ == null ? null : (Float)p_207922_1_.apply(p_207922_0_);
   }
}
