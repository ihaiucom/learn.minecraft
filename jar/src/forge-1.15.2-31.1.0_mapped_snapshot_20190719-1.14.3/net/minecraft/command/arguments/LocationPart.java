package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.text.TranslationTextComponent;

public class LocationPart {
   public static final SimpleCommandExceptionType EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.missing.double", new Object[0]));
   public static final SimpleCommandExceptionType EXPECTED_INT = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.missing.int", new Object[0]));
   private final boolean relative;
   private final double value;

   public LocationPart(boolean p_i47963_1_, double p_i47963_2_) {
      this.relative = p_i47963_1_;
      this.value = p_i47963_2_;
   }

   public double get(double p_197306_1_) {
      return this.relative ? this.value + p_197306_1_ : this.value;
   }

   public static LocationPart parseDouble(StringReader p_197308_0_, boolean p_197308_1_) throws CommandSyntaxException {
      if (p_197308_0_.canRead() && p_197308_0_.peek() == '^') {
         throw Vec3Argument.POS_MIXED_TYPES.createWithContext(p_197308_0_);
      } else if (!p_197308_0_.canRead()) {
         throw EXPECTED_DOUBLE.createWithContext(p_197308_0_);
      } else {
         boolean lvt_2_1_ = isRelative(p_197308_0_);
         int lvt_3_1_ = p_197308_0_.getCursor();
         double lvt_4_1_ = p_197308_0_.canRead() && p_197308_0_.peek() != ' ' ? p_197308_0_.readDouble() : 0.0D;
         String lvt_6_1_ = p_197308_0_.getString().substring(lvt_3_1_, p_197308_0_.getCursor());
         if (lvt_2_1_ && lvt_6_1_.isEmpty()) {
            return new LocationPart(true, 0.0D);
         } else {
            if (!lvt_6_1_.contains(".") && !lvt_2_1_ && p_197308_1_) {
               lvt_4_1_ += 0.5D;
            }

            return new LocationPart(lvt_2_1_, lvt_4_1_);
         }
      }
   }

   public static LocationPart parseInt(StringReader p_197307_0_) throws CommandSyntaxException {
      if (p_197307_0_.canRead() && p_197307_0_.peek() == '^') {
         throw Vec3Argument.POS_MIXED_TYPES.createWithContext(p_197307_0_);
      } else if (!p_197307_0_.canRead()) {
         throw EXPECTED_INT.createWithContext(p_197307_0_);
      } else {
         boolean lvt_1_1_ = isRelative(p_197307_0_);
         double lvt_2_2_;
         if (p_197307_0_.canRead() && p_197307_0_.peek() != ' ') {
            lvt_2_2_ = lvt_1_1_ ? p_197307_0_.readDouble() : (double)p_197307_0_.readInt();
         } else {
            lvt_2_2_ = 0.0D;
         }

         return new LocationPart(lvt_1_1_, lvt_2_2_);
      }
   }

   private static boolean isRelative(StringReader p_197309_0_) {
      boolean lvt_1_2_;
      if (p_197309_0_.peek() == '~') {
         lvt_1_2_ = true;
         p_197309_0_.skip();
      } else {
         lvt_1_2_ = false;
      }

      return lvt_1_2_;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof LocationPart)) {
         return false;
      } else {
         LocationPart lvt_2_1_ = (LocationPart)p_equals_1_;
         if (this.relative != lvt_2_1_.relative) {
            return false;
         } else {
            return Double.compare(lvt_2_1_.value, this.value) == 0;
         }
      }
   }

   public int hashCode() {
      int lvt_1_1_ = this.relative ? 1 : 0;
      long lvt_2_1_ = Double.doubleToLongBits(this.value);
      lvt_1_1_ = 31 * lvt_1_1_ + (int)(lvt_2_1_ ^ lvt_2_1_ >>> 32);
      return lvt_1_1_;
   }

   public boolean isRelative() {
      return this.relative;
   }
}
