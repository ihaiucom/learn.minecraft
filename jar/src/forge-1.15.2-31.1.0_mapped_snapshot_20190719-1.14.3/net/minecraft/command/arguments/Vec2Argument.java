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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

public class Vec2Argument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
   public static final SimpleCommandExceptionType VEC2_INCOMPLETE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos2d.incomplete", new Object[0]));
   private final boolean centerIntegers;

   public Vec2Argument(boolean p_i47965_1_) {
      this.centerIntegers = p_i47965_1_;
   }

   public static Vec2Argument vec2() {
      return new Vec2Argument(true);
   }

   public static Vec2f getVec2f(CommandContext<CommandSource> p_197295_0_, String p_197295_1_) throws CommandSyntaxException {
      Vec3d lvt_2_1_ = ((ILocationArgument)p_197295_0_.getArgument(p_197295_1_, ILocationArgument.class)).getPosition((CommandSource)p_197295_0_.getSource());
      return new Vec2f((float)lvt_2_1_.x, (float)lvt_2_1_.z);
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int lvt_2_1_ = p_parse_1_.getCursor();
      if (!p_parse_1_.canRead()) {
         throw VEC2_INCOMPLETE.createWithContext(p_parse_1_);
      } else {
         LocationPart lvt_3_1_ = LocationPart.parseDouble(p_parse_1_, this.centerIntegers);
         if (p_parse_1_.canRead() && p_parse_1_.peek() == ' ') {
            p_parse_1_.skip();
            LocationPart lvt_4_1_ = LocationPart.parseDouble(p_parse_1_, this.centerIntegers);
            return new LocationInput(lvt_3_1_, new LocationPart(true, 0.0D), lvt_4_1_);
         } else {
            p_parse_1_.setCursor(lvt_2_1_);
            throw VEC2_INCOMPLETE.createWithContext(p_parse_1_);
         }
      }
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

         return ISuggestionProvider.func_211269_a(lvt_3_1_, (Collection)lvt_4_2_, p_listSuggestions_2_, Commands.func_212590_a(this::parse));
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
