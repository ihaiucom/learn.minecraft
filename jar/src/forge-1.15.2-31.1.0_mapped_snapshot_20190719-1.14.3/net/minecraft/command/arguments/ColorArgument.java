package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ColorArgument implements ArgumentType<TextFormatting> {
   private static final Collection<String> EXAMPLES = Arrays.asList("red", "green");
   public static final DynamicCommandExceptionType COLOR_INVALID = new DynamicCommandExceptionType((p_208659_0_) -> {
      return new TranslationTextComponent("argument.color.invalid", new Object[]{p_208659_0_});
   });

   private ColorArgument() {
   }

   public static ColorArgument color() {
      return new ColorArgument();
   }

   public static TextFormatting getColor(CommandContext<CommandSource> p_197064_0_, String p_197064_1_) {
      return (TextFormatting)p_197064_0_.getArgument(p_197064_1_, TextFormatting.class);
   }

   public TextFormatting parse(StringReader p_parse_1_) throws CommandSyntaxException {
      String lvt_2_1_ = p_parse_1_.readUnquotedString();
      TextFormatting lvt_3_1_ = TextFormatting.getValueByName(lvt_2_1_);
      if (lvt_3_1_ != null && !lvt_3_1_.isFancyStyling()) {
         return lvt_3_1_;
      } else {
         throw COLOR_INVALID.create(lvt_2_1_);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggest((Iterable)TextFormatting.getValidValues(true, false), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
