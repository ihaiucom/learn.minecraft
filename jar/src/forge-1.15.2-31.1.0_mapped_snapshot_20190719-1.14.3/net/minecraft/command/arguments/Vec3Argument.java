package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

public class Vec3Argument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType POS_INCOMPLETE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos3d.incomplete", new Object[0]));
   public static final SimpleCommandExceptionType POS_MIXED_TYPES = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.mixed", new Object[0]));
   private final boolean centerIntegers;

   public Vec3Argument(boolean p_i47964_1_) {
      this.centerIntegers = p_i47964_1_;
   }

   public static Vec3Argument vec3() {
      return new Vec3Argument(true);
   }

   public static Vec3Argument vec3(boolean p_197303_0_) {
      return new Vec3Argument(p_197303_0_);
   }

   public static Vec3d getVec3(CommandContext<CommandSource> p_197300_0_, String p_197300_1_) throws CommandSyntaxException {
      return ((ILocationArgument)p_197300_0_.getArgument(p_197300_1_, ILocationArgument.class)).getPosition((CommandSource)p_197300_0_.getSource());
   }

   public static ILocationArgument getLocation(CommandContext<CommandSource> p_200385_0_, String p_200385_1_) {
      return (ILocationArgument)p_200385_0_.getArgument(p_200385_1_, ILocationArgument.class);
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return (ILocationArgument)(p_parse_1_.canRead() && p_parse_1_.peek() == '^' ? LocalLocationArgument.parse(p_parse_1_) : LocationInput.parseDouble(p_parse_1_, this.centerIntegers));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (!(p_listSuggestions_1_.getSource() instanceof ISuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String lvt_3_1_ = p_listSuggestions_2_.getRemaining();
         Object lvt_4_2_;
         if (!lvt_3_1_.isEmpty() && lvt_3_1_.charAt(0) == '^') {
            lvt_4_2_ = Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_LOCAL);
         } else {
            lvt_4_2_ = ((ISuggestionProvider)p_listSuggestions_1_.getSource()).func_217293_r();
         }

         return ISuggestionProvider.func_209000_a(lvt_3_1_, (Collection)lvt_4_2_, p_listSuggestions_2_, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
