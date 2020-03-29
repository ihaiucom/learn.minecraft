package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class MinMaxBounds<T extends Number> {
   public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.empty", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.range.swapped", new Object[0]));
   protected final T min;
   protected final T max;

   protected MinMaxBounds(@Nullable T p_i49720_1_, @Nullable T p_i49720_2_) {
      this.min = p_i49720_1_;
      this.max = p_i49720_2_;
   }

   @Nullable
   public T getMin() {
      return this.min;
   }

   @Nullable
   public T getMax() {
      return this.max;
   }

   public boolean isUnbounded() {
      return this.min == null && this.max == null;
   }

   public JsonElement serialize() {
      if (this.isUnbounded()) {
         return JsonNull.INSTANCE;
      } else if (this.min != null && this.min.equals(this.max)) {
         return new JsonPrimitive(this.min);
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.min != null) {
            lvt_1_1_.addProperty("min", this.min);
         }

         if (this.max != null) {
            lvt_1_1_.addProperty("max", this.max);
         }

         return lvt_1_1_;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R fromJson(@Nullable JsonElement p_211331_0_, R p_211331_1_, BiFunction<JsonElement, String, T> p_211331_2_, MinMaxBounds.IBoundFactory<T, R> p_211331_3_) {
      if (p_211331_0_ != null && !p_211331_0_.isJsonNull()) {
         if (JSONUtils.isNumber(p_211331_0_)) {
            T lvt_4_1_ = (Number)p_211331_2_.apply(p_211331_0_, "value");
            return p_211331_3_.create(lvt_4_1_, lvt_4_1_);
         } else {
            JsonObject lvt_4_2_ = JSONUtils.getJsonObject(p_211331_0_, "value");
            T lvt_5_1_ = lvt_4_2_.has("min") ? (Number)p_211331_2_.apply(lvt_4_2_.get("min"), "min") : null;
            T lvt_6_1_ = lvt_4_2_.has("max") ? (Number)p_211331_2_.apply(lvt_4_2_.get("max"), "max") : null;
            return p_211331_3_.create(lvt_5_1_, lvt_6_1_);
         }
      } else {
         return p_211331_1_;
      }
   }

   protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader p_211337_0_, MinMaxBounds.IBoundReader<T, R> p_211337_1_, Function<String, T> p_211337_2_, Supplier<DynamicCommandExceptionType> p_211337_3_, Function<T, T> p_211337_4_) throws CommandSyntaxException {
      if (!p_211337_0_.canRead()) {
         throw ERROR_EMPTY.createWithContext(p_211337_0_);
      } else {
         int lvt_5_1_ = p_211337_0_.getCursor();

         try {
            T lvt_6_1_ = (Number)optionallyFormat(readNumber(p_211337_0_, p_211337_2_, p_211337_3_), p_211337_4_);
            Number lvt_7_2_;
            if (p_211337_0_.canRead(2) && p_211337_0_.peek() == '.' && p_211337_0_.peek(1) == '.') {
               p_211337_0_.skip();
               p_211337_0_.skip();
               lvt_7_2_ = (Number)optionallyFormat(readNumber(p_211337_0_, p_211337_2_, p_211337_3_), p_211337_4_);
               if (lvt_6_1_ == null && lvt_7_2_ == null) {
                  throw ERROR_EMPTY.createWithContext(p_211337_0_);
               }
            } else {
               lvt_7_2_ = lvt_6_1_;
            }

            if (lvt_6_1_ == null && lvt_7_2_ == null) {
               throw ERROR_EMPTY.createWithContext(p_211337_0_);
            } else {
               return p_211337_1_.create(p_211337_0_, lvt_6_1_, lvt_7_2_);
            }
         } catch (CommandSyntaxException var8) {
            p_211337_0_.setCursor(lvt_5_1_);
            throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), lvt_5_1_);
         }
      }
   }

   @Nullable
   private static <T extends Number> T readNumber(StringReader p_196975_0_, Function<String, T> p_196975_1_, Supplier<DynamicCommandExceptionType> p_196975_2_) throws CommandSyntaxException {
      int lvt_3_1_ = p_196975_0_.getCursor();

      while(p_196975_0_.canRead() && isAllowedInputChat(p_196975_0_)) {
         p_196975_0_.skip();
      }

      String lvt_4_1_ = p_196975_0_.getString().substring(lvt_3_1_, p_196975_0_.getCursor());
      if (lvt_4_1_.isEmpty()) {
         return null;
      } else {
         try {
            return (Number)p_196975_1_.apply(lvt_4_1_);
         } catch (NumberFormatException var6) {
            throw ((DynamicCommandExceptionType)p_196975_2_.get()).createWithContext(p_196975_0_, lvt_4_1_);
         }
      }
   }

   private static boolean isAllowedInputChat(StringReader p_196970_0_) {
      char lvt_1_1_ = p_196970_0_.peek();
      if ((lvt_1_1_ < '0' || lvt_1_1_ > '9') && lvt_1_1_ != '-') {
         if (lvt_1_1_ != '.') {
            return false;
         } else {
            return !p_196970_0_.canRead(2) || p_196970_0_.peek(1) != '.';
         }
      } else {
         return true;
      }
   }

   @Nullable
   private static <T> T optionallyFormat(@Nullable T p_196972_0_, Function<T, T> p_196972_1_) {
      return p_196972_0_ == null ? null : p_196972_1_.apply(p_196972_0_);
   }

   @FunctionalInterface
   public interface IBoundReader<T extends Number, R extends MinMaxBounds<T>> {
      R create(StringReader var1, @Nullable T var2, @Nullable T var3) throws CommandSyntaxException;
   }

   @FunctionalInterface
   public interface IBoundFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(@Nullable T var1, @Nullable T var2);
   }

   public static class FloatBound extends MinMaxBounds<Float> {
      public static final MinMaxBounds.FloatBound UNBOUNDED = new MinMaxBounds.FloatBound((Float)null, (Float)null);
      private final Double minSquared;
      private final Double maxSquared;

      private static MinMaxBounds.FloatBound create(StringReader p_211352_0_, @Nullable Float p_211352_1_, @Nullable Float p_211352_2_) throws CommandSyntaxException {
         if (p_211352_1_ != null && p_211352_2_ != null && p_211352_1_ > p_211352_2_) {
            throw ERROR_SWAPPED.createWithContext(p_211352_0_);
         } else {
            return new MinMaxBounds.FloatBound(p_211352_1_, p_211352_2_);
         }
      }

      @Nullable
      private static Double square(@Nullable Float p_211350_0_) {
         return p_211350_0_ == null ? null : p_211350_0_.doubleValue() * p_211350_0_.doubleValue();
      }

      private FloatBound(@Nullable Float p_i49717_1_, @Nullable Float p_i49717_2_) {
         super(p_i49717_1_, p_i49717_2_);
         this.minSquared = square(p_i49717_1_);
         this.maxSquared = square(p_i49717_2_);
      }

      public static MinMaxBounds.FloatBound atLeast(float p_211355_0_) {
         return new MinMaxBounds.FloatBound(p_211355_0_, (Float)null);
      }

      public boolean test(float p_211354_1_) {
         if (this.min != null && (Float)this.min > p_211354_1_) {
            return false;
         } else {
            return this.max == null || (Float)this.max >= p_211354_1_;
         }
      }

      public boolean testSquared(double p_211351_1_) {
         if (this.minSquared != null && this.minSquared > p_211351_1_) {
            return false;
         } else {
            return this.maxSquared == null || this.maxSquared >= p_211351_1_;
         }
      }

      public static MinMaxBounds.FloatBound fromJson(@Nullable JsonElement p_211356_0_) {
         return (MinMaxBounds.FloatBound)fromJson(p_211356_0_, UNBOUNDED, JSONUtils::getFloat, MinMaxBounds.FloatBound::new);
      }

      public static MinMaxBounds.FloatBound fromReader(StringReader p_211357_0_) throws CommandSyntaxException {
         return fromReader(p_211357_0_, (p_211358_0_) -> {
            return p_211358_0_;
         });
      }

      public static MinMaxBounds.FloatBound fromReader(StringReader p_211353_0_, Function<Float, Float> p_211353_1_) throws CommandSyntaxException {
         return (MinMaxBounds.FloatBound)fromReader(p_211353_0_, MinMaxBounds.FloatBound::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, p_211353_1_);
      }
   }

   public static class IntBound extends MinMaxBounds<Integer> {
      public static final MinMaxBounds.IntBound UNBOUNDED = new MinMaxBounds.IntBound((Integer)null, (Integer)null);
      private final Long minSquared;
      private final Long maxSquared;

      private static MinMaxBounds.IntBound create(StringReader p_211338_0_, @Nullable Integer p_211338_1_, @Nullable Integer p_211338_2_) throws CommandSyntaxException {
         if (p_211338_1_ != null && p_211338_2_ != null && p_211338_1_ > p_211338_2_) {
            throw ERROR_SWAPPED.createWithContext(p_211338_0_);
         } else {
            return new MinMaxBounds.IntBound(p_211338_1_, p_211338_2_);
         }
      }

      @Nullable
      private static Long square(@Nullable Integer p_211343_0_) {
         return p_211343_0_ == null ? null : p_211343_0_.longValue() * p_211343_0_.longValue();
      }

      private IntBound(@Nullable Integer p_i49716_1_, @Nullable Integer p_i49716_2_) {
         super(p_i49716_1_, p_i49716_2_);
         this.minSquared = square(p_i49716_1_);
         this.maxSquared = square(p_i49716_2_);
      }

      public static MinMaxBounds.IntBound exactly(int p_211345_0_) {
         return new MinMaxBounds.IntBound(p_211345_0_, p_211345_0_);
      }

      public static MinMaxBounds.IntBound atLeast(int p_211340_0_) {
         return new MinMaxBounds.IntBound(p_211340_0_, (Integer)null);
      }

      public boolean test(int p_211339_1_) {
         if (this.min != null && (Integer)this.min > p_211339_1_) {
            return false;
         } else {
            return this.max == null || (Integer)this.max >= p_211339_1_;
         }
      }

      public static MinMaxBounds.IntBound fromJson(@Nullable JsonElement p_211344_0_) {
         return (MinMaxBounds.IntBound)fromJson(p_211344_0_, UNBOUNDED, JSONUtils::getInt, MinMaxBounds.IntBound::new);
      }

      public static MinMaxBounds.IntBound fromReader(StringReader p_211342_0_) throws CommandSyntaxException {
         return fromReader(p_211342_0_, (p_211346_0_) -> {
            return p_211346_0_;
         });
      }

      public static MinMaxBounds.IntBound fromReader(StringReader p_211341_0_, Function<Integer, Integer> p_211341_1_) throws CommandSyntaxException {
         return (MinMaxBounds.IntBound)fromReader(p_211341_0_, MinMaxBounds.IntBound::create, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, p_211341_1_);
      }
   }
}
