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
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TranslationTextComponent;

public class ObjectiveArgument implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
   private static final DynamicCommandExceptionType OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType((p_208671_0_) -> {
      return new TranslationTextComponent("arguments.objective.notFound", new Object[]{p_208671_0_});
   });
   private static final DynamicCommandExceptionType OBJECTIVE_READ_ONLY = new DynamicCommandExceptionType((p_208669_0_) -> {
      return new TranslationTextComponent("arguments.objective.readonly", new Object[]{p_208669_0_});
   });
   public static final DynamicCommandExceptionType OBJECTIVE_NAME_TOO_LONG = new DynamicCommandExceptionType((p_208670_0_) -> {
      return new TranslationTextComponent("commands.scoreboard.objectives.add.longName", new Object[]{p_208670_0_});
   });

   public static ObjectiveArgument objective() {
      return new ObjectiveArgument();
   }

   public static ScoreObjective getObjective(CommandContext<CommandSource> p_197158_0_, String p_197158_1_) throws CommandSyntaxException {
      String lvt_2_1_ = (String)p_197158_0_.getArgument(p_197158_1_, String.class);
      Scoreboard lvt_3_1_ = ((CommandSource)p_197158_0_.getSource()).getServer().getScoreboard();
      ScoreObjective lvt_4_1_ = lvt_3_1_.getObjective(lvt_2_1_);
      if (lvt_4_1_ == null) {
         throw OBJECTIVE_NOT_FOUND.create(lvt_2_1_);
      } else {
         return lvt_4_1_;
      }
   }

   public static ScoreObjective getWritableObjective(CommandContext<CommandSource> p_197156_0_, String p_197156_1_) throws CommandSyntaxException {
      ScoreObjective lvt_2_1_ = getObjective(p_197156_0_, p_197156_1_);
      if (lvt_2_1_.getCriteria().isReadOnly()) {
         throw OBJECTIVE_READ_ONLY.create(lvt_2_1_.getName());
      } else {
         return lvt_2_1_;
      }
   }

   public String parse(StringReader p_parse_1_) throws CommandSyntaxException {
      String lvt_2_1_ = p_parse_1_.readUnquotedString();
      if (lvt_2_1_.length() > 16) {
         throw OBJECTIVE_NAME_TOO_LONG.create(16);
      } else {
         return lvt_2_1_;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (p_listSuggestions_1_.getSource() instanceof CommandSource) {
         return ISuggestionProvider.suggest((Iterable)((CommandSource)p_listSuggestions_1_.getSource()).getServer().getScoreboard().func_197897_d(), p_listSuggestions_2_);
      } else if (p_listSuggestions_1_.getSource() instanceof ISuggestionProvider) {
         ISuggestionProvider lvt_3_1_ = (ISuggestionProvider)p_listSuggestions_1_.getSource();
         return lvt_3_1_.getSuggestionsFromServer(p_listSuggestions_1_, p_listSuggestions_2_);
      } else {
         return Suggestions.empty();
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
