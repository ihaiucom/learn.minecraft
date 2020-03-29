package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.network.PacketBuffer;

public interface IRangeArgument<T extends MinMaxBounds<?>> extends ArgumentType<T> {
   static IRangeArgument.IntRange intRange() {
      return new IRangeArgument.IntRange();
   }

   public abstract static class Serializer<T extends IRangeArgument<?>> implements IArgumentSerializer<T> {
      public void write(T p_197072_1_, PacketBuffer p_197072_2_) {
      }

      public void write(T p_212244_1_, JsonObject p_212244_2_) {
      }
   }

   public static class FloatRange implements IRangeArgument<MinMaxBounds.FloatBound> {
      private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

      public MinMaxBounds.FloatBound parse(StringReader p_parse_1_) throws CommandSyntaxException {
         return MinMaxBounds.FloatBound.fromReader(p_parse_1_);
      }

      public Collection<String> getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
         return this.parse(p_parse_1_);
      }

      public static class Serializer extends IRangeArgument.Serializer<IRangeArgument.FloatRange> {
         public IRangeArgument.FloatRange read(PacketBuffer p_197071_1_) {
            return new IRangeArgument.FloatRange();
         }

         // $FF: synthetic method
         public ArgumentType read(PacketBuffer p_197071_1_) {
            return this.read(p_197071_1_);
         }
      }
   }

   public static class IntRange implements IRangeArgument<MinMaxBounds.IntBound> {
      private static final Collection<String> EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

      public static MinMaxBounds.IntBound getIntRange(CommandContext<CommandSource> p_211372_0_, String p_211372_1_) {
         return (MinMaxBounds.IntBound)p_211372_0_.getArgument(p_211372_1_, MinMaxBounds.IntBound.class);
      }

      public MinMaxBounds.IntBound parse(StringReader p_parse_1_) throws CommandSyntaxException {
         return MinMaxBounds.IntBound.fromReader(p_parse_1_);
      }

      public Collection<String> getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
         return this.parse(p_parse_1_);
      }

      public static class Serializer extends IRangeArgument.Serializer<IRangeArgument.IntRange> {
         public IRangeArgument.IntRange read(PacketBuffer p_197071_1_) {
            return new IRangeArgument.IntRange();
         }

         // $FF: synthetic method
         public ArgumentType read(PacketBuffer p_197071_1_) {
            return this.read(p_197071_1_);
         }
      }
   }
}
