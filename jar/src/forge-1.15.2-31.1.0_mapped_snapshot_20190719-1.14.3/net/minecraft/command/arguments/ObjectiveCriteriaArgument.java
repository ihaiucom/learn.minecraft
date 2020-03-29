package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ObjectiveCriteriaArgument implements ArgumentType<ScoreCriteria> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
   public static final DynamicCommandExceptionType OBJECTIVE_INVALID_CRITERIA = new DynamicCommandExceptionType((p_208672_0_) -> {
      return new TranslationTextComponent("argument.criteria.invalid", new Object[]{p_208672_0_});
   });

   private ObjectiveCriteriaArgument() {
   }

   public static ObjectiveCriteriaArgument objectiveCriteria() {
      return new ObjectiveCriteriaArgument();
   }

   public static ScoreCriteria getObjectiveCriteria(CommandContext<CommandSource> p_197161_0_, String p_197161_1_) {
      return (ScoreCriteria)p_197161_0_.getArgument(p_197161_1_, ScoreCriteria.class);
   }

   public ScoreCriteria parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int lvt_2_1_ = p_parse_1_.getCursor();

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         p_parse_1_.skip();
      }

      String lvt_3_1_ = p_parse_1_.getString().substring(lvt_2_1_, p_parse_1_.getCursor());
      return (ScoreCriteria)ScoreCriteria.func_216390_a(lvt_3_1_).orElseThrow(() -> {
         p_parse_1_.setCursor(lvt_2_1_);
         return OBJECTIVE_INVALID_CRITERIA.create(lvt_3_1_);
      });
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      List<String> lvt_3_1_ = Lists.newArrayList(ScoreCriteria.INSTANCES.keySet());
      Iterator var4 = Registry.STATS.iterator();

      while(var4.hasNext()) {
         StatType<?> lvt_5_1_ = (StatType)var4.next();
         Iterator var6 = lvt_5_1_.getRegistry().iterator();

         while(var6.hasNext()) {
            Object lvt_7_1_ = var6.next();
            String lvt_8_1_ = this.makeStatName(lvt_5_1_, lvt_7_1_);
            lvt_3_1_.add(lvt_8_1_);
         }
      }

      return ISuggestionProvider.suggest((Iterable)lvt_3_1_, p_listSuggestions_2_);
   }

   public <T> String makeStatName(StatType<T> p_199815_1_, Object p_199815_2_) {
      return Stat.buildName(p_199815_1_, p_199815_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
