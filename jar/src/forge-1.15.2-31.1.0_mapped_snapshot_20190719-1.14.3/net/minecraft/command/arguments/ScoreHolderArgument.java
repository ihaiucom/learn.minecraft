package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.text.TranslationTextComponent;

public class ScoreHolderArgument implements ArgumentType<ScoreHolderArgument.INameProvider> {
   public static final SuggestionProvider<CommandSource> SUGGEST_ENTITY_SELECTOR = (p_201323_0_, p_201323_1_) -> {
      StringReader lvt_2_1_ = new StringReader(p_201323_1_.getInput());
      lvt_2_1_.setCursor(p_201323_1_.getStart());
      EntitySelectorParser lvt_3_1_ = new EntitySelectorParser(lvt_2_1_);

      try {
         lvt_3_1_.parse();
      } catch (CommandSyntaxException var5) {
      }

      return lvt_3_1_.fillSuggestions(p_201323_1_, (p_201949_1_) -> {
         ISuggestionProvider.suggest((Iterable)((CommandSource)p_201323_0_.getSource()).getPlayerNames(), p_201949_1_);
      });
   };
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
   private static final SimpleCommandExceptionType EMPTY_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("argument.scoreHolder.empty", new Object[0]));
   private final boolean allowMultiple;

   public ScoreHolderArgument(boolean p_i47968_1_) {
      this.allowMultiple = p_i47968_1_;
   }

   public static String getSingleScoreHolderNoObjectives(CommandContext<CommandSource> p_197211_0_, String p_197211_1_) throws CommandSyntaxException {
      return (String)getScoreHolderNoObjectives(p_197211_0_, p_197211_1_).iterator().next();
   }

   public static Collection<String> getScoreHolderNoObjectives(CommandContext<CommandSource> p_197213_0_, String p_197213_1_) throws CommandSyntaxException {
      return getScoreHolder(p_197213_0_, p_197213_1_, Collections::emptyList);
   }

   public static Collection<String> getScoreHolder(CommandContext<CommandSource> p_211707_0_, String p_211707_1_) throws CommandSyntaxException {
      ServerScoreboard var10002 = ((CommandSource)p_211707_0_.getSource()).getServer().getScoreboard();
      var10002.getClass();
      return getScoreHolder(p_211707_0_, p_211707_1_, var10002::getObjectiveNames);
   }

   public static Collection<String> getScoreHolder(CommandContext<CommandSource> p_197210_0_, String p_197210_1_, Supplier<Collection<String>> p_197210_2_) throws CommandSyntaxException {
      Collection<String> lvt_3_1_ = ((ScoreHolderArgument.INameProvider)p_197210_0_.getArgument(p_197210_1_, ScoreHolderArgument.INameProvider.class)).getNames((CommandSource)p_197210_0_.getSource(), p_197210_2_);
      if (lvt_3_1_.isEmpty()) {
         throw EntityArgument.ENTITY_NOT_FOUND.create();
      } else {
         return lvt_3_1_;
      }
   }

   public static ScoreHolderArgument scoreHolder() {
      return new ScoreHolderArgument(false);
   }

   public static ScoreHolderArgument scoreHolders() {
      return new ScoreHolderArgument(true);
   }

   public ScoreHolderArgument.INameProvider parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '@') {
         EntitySelectorParser lvt_2_1_ = new EntitySelectorParser(p_parse_1_);
         EntitySelector lvt_3_1_ = lvt_2_1_.parse();
         if (!this.allowMultiple && lvt_3_1_.getLimit() > 1) {
            throw EntityArgument.TOO_MANY_ENTITIES.create();
         } else {
            return new ScoreHolderArgument.NameProvider(lvt_3_1_);
         }
      } else {
         int lvt_2_2_ = p_parse_1_.getCursor();

         while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
            p_parse_1_.skip();
         }

         String lvt_3_2_ = p_parse_1_.getString().substring(lvt_2_2_, p_parse_1_.getCursor());
         if (lvt_3_2_.equals("*")) {
            return (p_197208_0_, p_197208_1_) -> {
               Collection<String> lvt_2_1_ = (Collection)p_197208_1_.get();
               if (lvt_2_1_.isEmpty()) {
                  throw EMPTY_EXCEPTION.create();
               } else {
                  return lvt_2_1_;
               }
            };
         } else {
            Collection<String> lvt_4_1_ = Collections.singleton(lvt_3_2_);
            return (p_197212_1_, p_197212_2_) -> {
               return lvt_4_1_;
            };
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }

   public static class Serializer implements IArgumentSerializer<ScoreHolderArgument> {
      public void write(ScoreHolderArgument p_197072_1_, PacketBuffer p_197072_2_) {
         byte lvt_3_1_ = 0;
         if (p_197072_1_.allowMultiple) {
            lvt_3_1_ = (byte)(lvt_3_1_ | 1);
         }

         p_197072_2_.writeByte(lvt_3_1_);
      }

      public ScoreHolderArgument read(PacketBuffer p_197071_1_) {
         byte lvt_2_1_ = p_197071_1_.readByte();
         boolean lvt_3_1_ = (lvt_2_1_ & 1) != 0;
         return new ScoreHolderArgument(lvt_3_1_);
      }

      public void write(ScoreHolderArgument p_212244_1_, JsonObject p_212244_2_) {
         p_212244_2_.addProperty("amount", p_212244_1_.allowMultiple ? "multiple" : "single");
      }

      // $FF: synthetic method
      public ArgumentType read(PacketBuffer p_197071_1_) {
         return this.read(p_197071_1_);
      }
   }

   public static class NameProvider implements ScoreHolderArgument.INameProvider {
      private final EntitySelector selector;

      public NameProvider(EntitySelector p_i47977_1_) {
         this.selector = p_i47977_1_;
      }

      public Collection<String> getNames(CommandSource p_getNames_1_, Supplier<Collection<String>> p_getNames_2_) throws CommandSyntaxException {
         List<? extends Entity> lvt_3_1_ = this.selector.select(p_getNames_1_);
         if (lvt_3_1_.isEmpty()) {
            throw EntityArgument.ENTITY_NOT_FOUND.create();
         } else {
            List<String> lvt_4_1_ = Lists.newArrayList();
            Iterator var5 = lvt_3_1_.iterator();

            while(var5.hasNext()) {
               Entity lvt_6_1_ = (Entity)var5.next();
               lvt_4_1_.add(lvt_6_1_.getScoreboardName());
            }

            return lvt_4_1_;
         }
      }
   }

   @FunctionalInterface
   public interface INameProvider {
      Collection<String> getNames(CommandSource var1, Supplier<Collection<String>> var2) throws CommandSyntaxException;
   }
}
