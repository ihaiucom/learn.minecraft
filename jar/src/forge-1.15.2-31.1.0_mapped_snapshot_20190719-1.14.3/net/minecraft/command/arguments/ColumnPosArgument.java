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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.text.TranslationTextComponent;

public class ColumnPosArgument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
   public static final SimpleCommandExceptionType field_212604_a = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos2d.incomplete", new Object[0]));

   public static ColumnPosArgument columnPos() {
      return new ColumnPosArgument();
   }

   public static ColumnPos func_218101_a(CommandContext<CommandSource> p_218101_0_, String p_218101_1_) {
      BlockPos lvt_2_1_ = ((ILocationArgument)p_218101_0_.getArgument(p_218101_1_, ILocationArgument.class)).getBlockPos((CommandSource)p_218101_0_.getSource());
      return new ColumnPos(lvt_2_1_.getX(), lvt_2_1_.getZ());
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int lvt_2_1_ = p_parse_1_.getCursor();
      if (!p_parse_1_.canRead()) {
         throw field_212604_a.createWithContext(p_parse_1_);
      } else {
         LocationPart lvt_3_1_ = LocationPart.parseInt(p_parse_1_);
         if (p_parse_1_.canRead() && p_parse_1_.peek() == ' ') {
            p_parse_1_.skip();
            LocationPart lvt_4_1_ = LocationPart.parseInt(p_parse_1_);
            return new LocationInput(lvt_3_1_, new LocationPart(true, 0.0D), lvt_4_1_);
         } else {
            p_parse_1_.setCursor(lvt_2_1_);
            throw field_212604_a.createWithContext(p_parse_1_);
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
            lvt_4_2_ = ((ISuggestionProvider)p_listSuggestions_1_.getSource()).func_217294_q();
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
