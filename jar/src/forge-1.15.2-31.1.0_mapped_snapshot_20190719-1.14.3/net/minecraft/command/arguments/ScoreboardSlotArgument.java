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
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TranslationTextComponent;

public class ScoreboardSlotArgument implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("sidebar", "foo.bar");
   public static final DynamicCommandExceptionType SCOREBOARD_UNKNOWN_DISPLAY_SLOT = new DynamicCommandExceptionType((p_208678_0_) -> {
      return new TranslationTextComponent("argument.scoreboardDisplaySlot.invalid", new Object[]{p_208678_0_});
   });

   private ScoreboardSlotArgument() {
   }

   public static ScoreboardSlotArgument scoreboardSlot() {
      return new ScoreboardSlotArgument();
   }

   public static int getScoreboardSlot(CommandContext<CommandSource> p_197217_0_, String p_197217_1_) {
      return (Integer)p_197217_0_.getArgument(p_197217_1_, Integer.class);
   }

   public Integer parse(StringReader p_parse_1_) throws CommandSyntaxException {
      String lvt_2_1_ = p_parse_1_.readUnquotedString();
      int lvt_3_1_ = Scoreboard.getObjectiveDisplaySlotNumber(lvt_2_1_);
      if (lvt_3_1_ == -1) {
         throw SCOREBOARD_UNKNOWN_DISPLAY_SLOT.create(lvt_2_1_);
      } else {
         return lvt_3_1_;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggest(Scoreboard.getDisplaySlotStrings(), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
