package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TranslationTextComponent;

public class TriggerCommand {
   private static final SimpleCommandExceptionType NOT_PRIMED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.trigger.failed.unprimed", new Object[0]));
   private static final SimpleCommandExceptionType NOT_A_TRIGGER = new SimpleCommandExceptionType(new TranslationTextComponent("commands.trigger.failed.invalid", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198852_0_) {
      p_198852_0_.register((LiteralArgumentBuilder)Commands.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_198853_0_, p_198853_1_) -> {
         return suggestTriggers((CommandSource)p_198853_0_.getSource(), p_198853_1_);
      }).executes((p_198854_0_) -> {
         return incrementTrigger((CommandSource)p_198854_0_.getSource(), checkValidTrigger(((CommandSource)p_198854_0_.getSource()).asPlayer(), ObjectiveArgument.getObjective(p_198854_0_, "objective")));
      })).then(Commands.literal("add").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_198849_0_) -> {
         return addToTrigger((CommandSource)p_198849_0_.getSource(), checkValidTrigger(((CommandSource)p_198849_0_.getSource()).asPlayer(), ObjectiveArgument.getObjective(p_198849_0_, "objective")), IntegerArgumentType.getInteger(p_198849_0_, "value"));
      })))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_198855_0_) -> {
         return setTrigger((CommandSource)p_198855_0_.getSource(), checkValidTrigger(((CommandSource)p_198855_0_.getSource()).asPlayer(), ObjectiveArgument.getObjective(p_198855_0_, "objective")), IntegerArgumentType.getInteger(p_198855_0_, "value"));
      })))));
   }

   public static CompletableFuture<Suggestions> suggestTriggers(CommandSource p_198850_0_, SuggestionsBuilder p_198850_1_) {
      Entity lvt_2_1_ = p_198850_0_.getEntity();
      List<String> lvt_3_1_ = Lists.newArrayList();
      if (lvt_2_1_ != null) {
         Scoreboard lvt_4_1_ = p_198850_0_.getServer().getScoreboard();
         String lvt_5_1_ = lvt_2_1_.getScoreboardName();
         Iterator var6 = lvt_4_1_.getScoreObjectives().iterator();

         while(var6.hasNext()) {
            ScoreObjective lvt_7_1_ = (ScoreObjective)var6.next();
            if (lvt_7_1_.getCriteria() == ScoreCriteria.TRIGGER && lvt_4_1_.entityHasObjective(lvt_5_1_, lvt_7_1_)) {
               Score lvt_8_1_ = lvt_4_1_.getOrCreateScore(lvt_5_1_, lvt_7_1_);
               if (!lvt_8_1_.isLocked()) {
                  lvt_3_1_.add(lvt_7_1_.getName());
               }
            }
         }
      }

      return ISuggestionProvider.suggest((Iterable)lvt_3_1_, p_198850_1_);
   }

   private static int addToTrigger(CommandSource p_201479_0_, Score p_201479_1_, int p_201479_2_) {
      p_201479_1_.increaseScore(p_201479_2_);
      p_201479_0_.sendFeedback(new TranslationTextComponent("commands.trigger.add.success", new Object[]{p_201479_1_.getObjective().func_197890_e(), p_201479_2_}), true);
      return p_201479_1_.getScorePoints();
   }

   private static int setTrigger(CommandSource p_201478_0_, Score p_201478_1_, int p_201478_2_) {
      p_201478_1_.setScorePoints(p_201478_2_);
      p_201478_0_.sendFeedback(new TranslationTextComponent("commands.trigger.set.success", new Object[]{p_201478_1_.getObjective().func_197890_e(), p_201478_2_}), true);
      return p_201478_2_;
   }

   private static int incrementTrigger(CommandSource p_201477_0_, Score p_201477_1_) {
      p_201477_1_.increaseScore(1);
      p_201477_0_.sendFeedback(new TranslationTextComponent("commands.trigger.simple.success", new Object[]{p_201477_1_.getObjective().func_197890_e()}), true);
      return p_201477_1_.getScorePoints();
   }

   private static Score checkValidTrigger(ServerPlayerEntity p_198848_0_, ScoreObjective p_198848_1_) throws CommandSyntaxException {
      if (p_198848_1_.getCriteria() != ScoreCriteria.TRIGGER) {
         throw NOT_A_TRIGGER.create();
      } else {
         Scoreboard lvt_2_1_ = p_198848_0_.getWorldScoreboard();
         String lvt_3_1_ = p_198848_0_.getScoreboardName();
         if (!lvt_2_1_.entityHasObjective(lvt_3_1_, p_198848_1_)) {
            throw NOT_PRIMED.create();
         } else {
            Score lvt_4_1_ = lvt_2_1_.getOrCreateScore(lvt_3_1_, p_198848_1_);
            if (lvt_4_1_.isLocked()) {
               throw NOT_PRIMED.create();
            } else {
               lvt_4_1_.setLocked(true);
               return lvt_4_1_;
            }
         }
      }
   }
}
