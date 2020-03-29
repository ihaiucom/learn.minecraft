package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.command.arguments.ObjectiveCriteriaArgument;
import net.minecraft.command.arguments.OperationArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.ScoreboardSlotArgument;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class ScoreboardCommand {
   private static final SimpleCommandExceptionType OBJECTIVE_ALREADY_EXISTS_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.scoreboard.objectives.add.duplicate", new Object[0]));
   private static final SimpleCommandExceptionType DISPLAY_ALREADY_CLEAR_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.scoreboard.objectives.display.alreadyEmpty", new Object[0]));
   private static final SimpleCommandExceptionType DISPLAY_ALREADY_SET_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.scoreboard.objectives.display.alreadySet", new Object[0]));
   private static final SimpleCommandExceptionType ENABLE_TRIGGER_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.scoreboard.players.enable.failed", new Object[0]));
   private static final SimpleCommandExceptionType ENABLE_TRIGGER_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.scoreboard.players.enable.invalid", new Object[0]));
   private static final Dynamic2CommandExceptionType SCOREBOARD_PLAYER_NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((p_208907_0_, p_208907_1_) -> {
      return new TranslationTextComponent("commands.scoreboard.players.get.null", new Object[]{p_208907_0_, p_208907_1_});
   });

   public static void register(CommandDispatcher<CommandSource> p_198647_0_) {
      p_198647_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("scoreboard").requires((p_198650_0_) -> {
         return p_198650_0_.hasPermissionLevel(2);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("objectives").then(Commands.literal("list").executes((p_198640_0_) -> {
         return listObjectives((CommandSource)p_198640_0_.getSource());
      }))).then(Commands.literal("add").then(Commands.argument("objective", StringArgumentType.word()).then(((RequiredArgumentBuilder)Commands.argument("criteria", ObjectiveCriteriaArgument.objectiveCriteria()).executes((p_198636_0_) -> {
         return addObjective((CommandSource)p_198636_0_.getSource(), StringArgumentType.getString(p_198636_0_, "objective"), ObjectiveCriteriaArgument.getObjectiveCriteria(p_198636_0_, "criteria"), new StringTextComponent(StringArgumentType.getString(p_198636_0_, "objective")));
      })).then(Commands.argument("displayName", ComponentArgument.component()).executes((p_198649_0_) -> {
         return addObjective((CommandSource)p_198649_0_.getSource(), StringArgumentType.getString(p_198649_0_, "objective"), ObjectiveCriteriaArgument.getObjectiveCriteria(p_198649_0_, "criteria"), ComponentArgument.getComponent(p_198649_0_, "displayName"));
      })))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.literal("displayname").then(Commands.argument("displayName", ComponentArgument.component()).executes((p_211750_0_) -> {
         return setDisplayName((CommandSource)p_211750_0_.getSource(), ObjectiveArgument.getObjective(p_211750_0_, "objective"), ComponentArgument.getComponent(p_211750_0_, "displayName"));
      })))).then(createRenderTypeArgument())))).then(Commands.literal("remove").then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_198646_0_) -> {
         return removeObjective((CommandSource)p_198646_0_.getSource(), ObjectiveArgument.getObjective(p_198646_0_, "objective"));
      })))).then(Commands.literal("setdisplay").then(((RequiredArgumentBuilder)Commands.argument("slot", ScoreboardSlotArgument.scoreboardSlot()).executes((p_198652_0_) -> {
         return clearObjectiveDisplaySlot((CommandSource)p_198652_0_.getSource(), ScoreboardSlotArgument.getScoreboardSlot(p_198652_0_, "slot"));
      })).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_198639_0_) -> {
         return setObjectiveDisplaySlot((CommandSource)p_198639_0_.getSource(), ScoreboardSlotArgument.getScoreboardSlot(p_198639_0_, "slot"), ObjectiveArgument.getObjective(p_198639_0_, "objective"));
      })))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("players").then(((LiteralArgumentBuilder)Commands.literal("list").executes((p_198642_0_) -> {
         return listPlayers((CommandSource)p_198642_0_.getSource());
      })).then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).executes((p_198631_0_) -> {
         return listPlayerScores((CommandSource)p_198631_0_.getSource(), ScoreHolderArgument.getSingleScoreHolderNoObjectives(p_198631_0_, "target"));
      })))).then(Commands.literal("set").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer()).executes((p_198655_0_) -> {
         return setPlayerScore((CommandSource)p_198655_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198655_0_, "targets"), ObjectiveArgument.getWritableObjective(p_198655_0_, "objective"), IntegerArgumentType.getInteger(p_198655_0_, "score"));
      })))))).then(Commands.literal("get").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_198660_0_) -> {
         return getPlayerScore((CommandSource)p_198660_0_.getSource(), ScoreHolderArgument.getSingleScoreHolderNoObjectives(p_198660_0_, "target"), ObjectiveArgument.getObjective(p_198660_0_, "objective"));
      }))))).then(Commands.literal("add").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((p_198645_0_) -> {
         return addToPlayerScore((CommandSource)p_198645_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198645_0_, "targets"), ObjectiveArgument.getWritableObjective(p_198645_0_, "objective"), IntegerArgumentType.getInteger(p_198645_0_, "score"));
      })))))).then(Commands.literal("remove").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((p_198648_0_) -> {
         return removeFromPlayerScore((CommandSource)p_198648_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198648_0_, "targets"), ObjectiveArgument.getWritableObjective(p_198648_0_, "objective"), IntegerArgumentType.getInteger(p_198648_0_, "score"));
      })))))).then(Commands.literal("reset").then(((RequiredArgumentBuilder)Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).executes((p_198635_0_) -> {
         return resetPlayerAllScores((CommandSource)p_198635_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198635_0_, "targets"));
      })).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_198630_0_) -> {
         return resetPlayerScore((CommandSource)p_198630_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198630_0_, "targets"), ObjectiveArgument.getObjective(p_198630_0_, "objective"));
      }))))).then(Commands.literal("enable").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_198638_0_, p_198638_1_) -> {
         return suggestTriggers((CommandSource)p_198638_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198638_0_, "targets"), p_198638_1_);
      }).executes((p_198628_0_) -> {
         return enableTrigger((CommandSource)p_198628_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198628_0_, "targets"), ObjectiveArgument.getObjective(p_198628_0_, "objective"));
      }))))).then(Commands.literal("operation").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.argument("operation", OperationArgument.operation()).then(Commands.argument("source", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("sourceObjective", ObjectiveArgument.objective()).executes((p_198657_0_) -> {
         return applyScoreOperation((CommandSource)p_198657_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198657_0_, "targets"), ObjectiveArgument.getWritableObjective(p_198657_0_, "targetObjective"), OperationArgument.getOperation(p_198657_0_, "operation"), ScoreHolderArgument.getScoreHolder(p_198657_0_, "source"), ObjectiveArgument.getObjective(p_198657_0_, "sourceObjective"));
      })))))))));
   }

   private static LiteralArgumentBuilder<CommandSource> createRenderTypeArgument() {
      LiteralArgumentBuilder<CommandSource> lvt_0_1_ = Commands.literal("rendertype");
      ScoreCriteria.RenderType[] var1 = ScoreCriteria.RenderType.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ScoreCriteria.RenderType lvt_4_1_ = var1[var3];
         lvt_0_1_.then(Commands.literal(lvt_4_1_.getId()).executes((p_211912_1_) -> {
            return setRenderType((CommandSource)p_211912_1_.getSource(), ObjectiveArgument.getObjective(p_211912_1_, "objective"), lvt_4_1_);
         }));
      }

      return lvt_0_1_;
   }

   private static CompletableFuture<Suggestions> suggestTriggers(CommandSource p_198641_0_, Collection<String> p_198641_1_, SuggestionsBuilder p_198641_2_) {
      List<String> lvt_3_1_ = Lists.newArrayList();
      Scoreboard lvt_4_1_ = p_198641_0_.getServer().getScoreboard();
      Iterator var5 = lvt_4_1_.getScoreObjectives().iterator();

      while(true) {
         ScoreObjective lvt_6_1_;
         do {
            if (!var5.hasNext()) {
               return ISuggestionProvider.suggest((Iterable)lvt_3_1_, p_198641_2_);
            }

            lvt_6_1_ = (ScoreObjective)var5.next();
         } while(lvt_6_1_.getCriteria() != ScoreCriteria.TRIGGER);

         boolean lvt_7_1_ = false;
         Iterator var8 = p_198641_1_.iterator();

         label32: {
            String lvt_9_1_;
            do {
               if (!var8.hasNext()) {
                  break label32;
               }

               lvt_9_1_ = (String)var8.next();
            } while(lvt_4_1_.entityHasObjective(lvt_9_1_, lvt_6_1_) && !lvt_4_1_.getOrCreateScore(lvt_9_1_, lvt_6_1_).isLocked());

            lvt_7_1_ = true;
         }

         if (lvt_7_1_) {
            lvt_3_1_.add(lvt_6_1_.getName());
         }
      }
   }

   private static int getPlayerScore(CommandSource p_198634_0_, String p_198634_1_, ScoreObjective p_198634_2_) throws CommandSyntaxException {
      Scoreboard lvt_3_1_ = p_198634_0_.getServer().getScoreboard();
      if (!lvt_3_1_.entityHasObjective(p_198634_1_, p_198634_2_)) {
         throw SCOREBOARD_PLAYER_NOT_FOUND_EXCEPTION.create(p_198634_2_.getName(), p_198634_1_);
      } else {
         Score lvt_4_1_ = lvt_3_1_.getOrCreateScore(p_198634_1_, p_198634_2_);
         p_198634_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.get.success", new Object[]{p_198634_1_, lvt_4_1_.getScorePoints(), p_198634_2_.func_197890_e()}), false);
         return lvt_4_1_.getScorePoints();
      }
   }

   private static int applyScoreOperation(CommandSource p_198658_0_, Collection<String> p_198658_1_, ScoreObjective p_198658_2_, OperationArgument.IOperation p_198658_3_, Collection<String> p_198658_4_, ScoreObjective p_198658_5_) throws CommandSyntaxException {
      Scoreboard lvt_6_1_ = p_198658_0_.getServer().getScoreboard();
      int lvt_7_1_ = 0;

      Score lvt_10_1_;
      for(Iterator var8 = p_198658_1_.iterator(); var8.hasNext(); lvt_7_1_ += lvt_10_1_.getScorePoints()) {
         String lvt_9_1_ = (String)var8.next();
         lvt_10_1_ = lvt_6_1_.getOrCreateScore(lvt_9_1_, p_198658_2_);
         Iterator var11 = p_198658_4_.iterator();

         while(var11.hasNext()) {
            String lvt_12_1_ = (String)var11.next();
            Score lvt_13_1_ = lvt_6_1_.getOrCreateScore(lvt_12_1_, p_198658_5_);
            p_198658_3_.apply(lvt_10_1_, lvt_13_1_);
         }
      }

      if (p_198658_1_.size() == 1) {
         p_198658_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.operation.success.single", new Object[]{p_198658_2_.func_197890_e(), p_198658_1_.iterator().next(), lvt_7_1_}), true);
      } else {
         p_198658_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.operation.success.multiple", new Object[]{p_198658_2_.func_197890_e(), p_198658_1_.size()}), true);
      }

      return lvt_7_1_;
   }

   private static int enableTrigger(CommandSource p_198644_0_, Collection<String> p_198644_1_, ScoreObjective p_198644_2_) throws CommandSyntaxException {
      if (p_198644_2_.getCriteria() != ScoreCriteria.TRIGGER) {
         throw ENABLE_TRIGGER_INVALID.create();
      } else {
         Scoreboard lvt_3_1_ = p_198644_0_.getServer().getScoreboard();
         int lvt_4_1_ = 0;
         Iterator var5 = p_198644_1_.iterator();

         while(var5.hasNext()) {
            String lvt_6_1_ = (String)var5.next();
            Score lvt_7_1_ = lvt_3_1_.getOrCreateScore(lvt_6_1_, p_198644_2_);
            if (lvt_7_1_.isLocked()) {
               lvt_7_1_.setLocked(false);
               ++lvt_4_1_;
            }
         }

         if (lvt_4_1_ == 0) {
            throw ENABLE_TRIGGER_FAILED.create();
         } else {
            if (p_198644_1_.size() == 1) {
               p_198644_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.enable.success.single", new Object[]{p_198644_2_.func_197890_e(), p_198644_1_.iterator().next()}), true);
            } else {
               p_198644_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.enable.success.multiple", new Object[]{p_198644_2_.func_197890_e(), p_198644_1_.size()}), true);
            }

            return lvt_4_1_;
         }
      }
   }

   private static int resetPlayerAllScores(CommandSource p_198654_0_, Collection<String> p_198654_1_) {
      Scoreboard lvt_2_1_ = p_198654_0_.getServer().getScoreboard();
      Iterator var3 = p_198654_1_.iterator();

      while(var3.hasNext()) {
         String lvt_4_1_ = (String)var3.next();
         lvt_2_1_.removeObjectiveFromEntity(lvt_4_1_, (ScoreObjective)null);
      }

      if (p_198654_1_.size() == 1) {
         p_198654_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.reset.all.single", new Object[]{p_198654_1_.iterator().next()}), true);
      } else {
         p_198654_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.reset.all.multiple", new Object[]{p_198654_1_.size()}), true);
      }

      return p_198654_1_.size();
   }

   private static int resetPlayerScore(CommandSource p_198656_0_, Collection<String> p_198656_1_, ScoreObjective p_198656_2_) {
      Scoreboard lvt_3_1_ = p_198656_0_.getServer().getScoreboard();
      Iterator var4 = p_198656_1_.iterator();

      while(var4.hasNext()) {
         String lvt_5_1_ = (String)var4.next();
         lvt_3_1_.removeObjectiveFromEntity(lvt_5_1_, p_198656_2_);
      }

      if (p_198656_1_.size() == 1) {
         p_198656_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.reset.specific.single", new Object[]{p_198656_2_.func_197890_e(), p_198656_1_.iterator().next()}), true);
      } else {
         p_198656_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.reset.specific.multiple", new Object[]{p_198656_2_.func_197890_e(), p_198656_1_.size()}), true);
      }

      return p_198656_1_.size();
   }

   private static int setPlayerScore(CommandSource p_198653_0_, Collection<String> p_198653_1_, ScoreObjective p_198653_2_, int p_198653_3_) {
      Scoreboard lvt_4_1_ = p_198653_0_.getServer().getScoreboard();
      Iterator var5 = p_198653_1_.iterator();

      while(var5.hasNext()) {
         String lvt_6_1_ = (String)var5.next();
         Score lvt_7_1_ = lvt_4_1_.getOrCreateScore(lvt_6_1_, p_198653_2_);
         lvt_7_1_.setScorePoints(p_198653_3_);
      }

      if (p_198653_1_.size() == 1) {
         p_198653_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.set.success.single", new Object[]{p_198653_2_.func_197890_e(), p_198653_1_.iterator().next(), p_198653_3_}), true);
      } else {
         p_198653_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.set.success.multiple", new Object[]{p_198653_2_.func_197890_e(), p_198653_1_.size(), p_198653_3_}), true);
      }

      return p_198653_3_ * p_198653_1_.size();
   }

   private static int addToPlayerScore(CommandSource p_198633_0_, Collection<String> p_198633_1_, ScoreObjective p_198633_2_, int p_198633_3_) {
      Scoreboard lvt_4_1_ = p_198633_0_.getServer().getScoreboard();
      int lvt_5_1_ = 0;

      Score lvt_8_1_;
      for(Iterator var6 = p_198633_1_.iterator(); var6.hasNext(); lvt_5_1_ += lvt_8_1_.getScorePoints()) {
         String lvt_7_1_ = (String)var6.next();
         lvt_8_1_ = lvt_4_1_.getOrCreateScore(lvt_7_1_, p_198633_2_);
         lvt_8_1_.setScorePoints(lvt_8_1_.getScorePoints() + p_198633_3_);
      }

      if (p_198633_1_.size() == 1) {
         p_198633_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.add.success.single", new Object[]{p_198633_3_, p_198633_2_.func_197890_e(), p_198633_1_.iterator().next(), lvt_5_1_}), true);
      } else {
         p_198633_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.add.success.multiple", new Object[]{p_198633_3_, p_198633_2_.func_197890_e(), p_198633_1_.size()}), true);
      }

      return lvt_5_1_;
   }

   private static int removeFromPlayerScore(CommandSource p_198651_0_, Collection<String> p_198651_1_, ScoreObjective p_198651_2_, int p_198651_3_) {
      Scoreboard lvt_4_1_ = p_198651_0_.getServer().getScoreboard();
      int lvt_5_1_ = 0;

      Score lvt_8_1_;
      for(Iterator var6 = p_198651_1_.iterator(); var6.hasNext(); lvt_5_1_ += lvt_8_1_.getScorePoints()) {
         String lvt_7_1_ = (String)var6.next();
         lvt_8_1_ = lvt_4_1_.getOrCreateScore(lvt_7_1_, p_198651_2_);
         lvt_8_1_.setScorePoints(lvt_8_1_.getScorePoints() - p_198651_3_);
      }

      if (p_198651_1_.size() == 1) {
         p_198651_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.remove.success.single", new Object[]{p_198651_3_, p_198651_2_.func_197890_e(), p_198651_1_.iterator().next(), lvt_5_1_}), true);
      } else {
         p_198651_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.remove.success.multiple", new Object[]{p_198651_3_, p_198651_2_.func_197890_e(), p_198651_1_.size()}), true);
      }

      return lvt_5_1_;
   }

   private static int listPlayers(CommandSource p_198661_0_) {
      Collection<String> lvt_1_1_ = p_198661_0_.getServer().getScoreboard().getObjectiveNames();
      if (lvt_1_1_.isEmpty()) {
         p_198661_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.list.empty", new Object[0]), false);
      } else {
         p_198661_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.list.success", new Object[]{lvt_1_1_.size(), TextComponentUtils.makeGreenSortedList(lvt_1_1_)}), false);
      }

      return lvt_1_1_.size();
   }

   private static int listPlayerScores(CommandSource p_198643_0_, String p_198643_1_) {
      Map<ScoreObjective, Score> lvt_2_1_ = p_198643_0_.getServer().getScoreboard().getObjectivesForEntity(p_198643_1_);
      if (lvt_2_1_.isEmpty()) {
         p_198643_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.list.entity.empty", new Object[]{p_198643_1_}), false);
      } else {
         p_198643_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.list.entity.success", new Object[]{p_198643_1_, lvt_2_1_.size()}), false);
         Iterator var3 = lvt_2_1_.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<ScoreObjective, Score> lvt_4_1_ = (Entry)var3.next();
            p_198643_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.players.list.entity.entry", new Object[]{((ScoreObjective)lvt_4_1_.getKey()).func_197890_e(), ((Score)lvt_4_1_.getValue()).getScorePoints()}), false);
         }
      }

      return lvt_2_1_.size();
   }

   private static int clearObjectiveDisplaySlot(CommandSource p_198632_0_, int p_198632_1_) throws CommandSyntaxException {
      Scoreboard lvt_2_1_ = p_198632_0_.getServer().getScoreboard();
      if (lvt_2_1_.getObjectiveInDisplaySlot(p_198632_1_) == null) {
         throw DISPLAY_ALREADY_CLEAR_EXCEPTION.create();
      } else {
         lvt_2_1_.setObjectiveInDisplaySlot(p_198632_1_, (ScoreObjective)null);
         p_198632_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.objectives.display.cleared", new Object[]{Scoreboard.getDisplaySlotStrings()[p_198632_1_]}), true);
         return 0;
      }
   }

   private static int setObjectiveDisplaySlot(CommandSource p_198659_0_, int p_198659_1_, ScoreObjective p_198659_2_) throws CommandSyntaxException {
      Scoreboard lvt_3_1_ = p_198659_0_.getServer().getScoreboard();
      if (lvt_3_1_.getObjectiveInDisplaySlot(p_198659_1_) == p_198659_2_) {
         throw DISPLAY_ALREADY_SET_EXCEPTION.create();
      } else {
         lvt_3_1_.setObjectiveInDisplaySlot(p_198659_1_, p_198659_2_);
         p_198659_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.objectives.display.set", new Object[]{Scoreboard.getDisplaySlotStrings()[p_198659_1_], p_198659_2_.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSource p_211749_0_, ScoreObjective p_211749_1_, ITextComponent p_211749_2_) {
      if (!p_211749_1_.getDisplayName().equals(p_211749_2_)) {
         p_211749_1_.setDisplayName(p_211749_2_);
         p_211749_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.objectives.modify.displayname", new Object[]{p_211749_1_.getName(), p_211749_1_.func_197890_e()}), true);
      }

      return 0;
   }

   private static int setRenderType(CommandSource p_211910_0_, ScoreObjective p_211910_1_, ScoreCriteria.RenderType p_211910_2_) {
      if (p_211910_1_.getRenderType() != p_211910_2_) {
         p_211910_1_.setRenderType(p_211910_2_);
         p_211910_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.objectives.modify.rendertype", new Object[]{p_211910_1_.func_197890_e()}), true);
      }

      return 0;
   }

   private static int removeObjective(CommandSource p_198637_0_, ScoreObjective p_198637_1_) {
      Scoreboard lvt_2_1_ = p_198637_0_.getServer().getScoreboard();
      lvt_2_1_.removeObjective(p_198637_1_);
      p_198637_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.objectives.remove.success", new Object[]{p_198637_1_.func_197890_e()}), true);
      return lvt_2_1_.getScoreObjectives().size();
   }

   private static int addObjective(CommandSource p_198629_0_, String p_198629_1_, ScoreCriteria p_198629_2_, ITextComponent p_198629_3_) throws CommandSyntaxException {
      Scoreboard lvt_4_1_ = p_198629_0_.getServer().getScoreboard();
      if (lvt_4_1_.getObjective(p_198629_1_) != null) {
         throw OBJECTIVE_ALREADY_EXISTS_EXCEPTION.create();
      } else if (p_198629_1_.length() > 16) {
         throw ObjectiveArgument.OBJECTIVE_NAME_TOO_LONG.create(16);
      } else {
         lvt_4_1_.addObjective(p_198629_1_, p_198629_2_, p_198629_3_, p_198629_2_.getRenderType());
         ScoreObjective lvt_5_1_ = lvt_4_1_.getObjective(p_198629_1_);
         p_198629_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.objectives.add.success", new Object[]{lvt_5_1_.func_197890_e()}), true);
         return lvt_4_1_.getScoreObjectives().size();
      }
   }

   private static int listObjectives(CommandSource p_198662_0_) {
      Collection<ScoreObjective> lvt_1_1_ = p_198662_0_.getServer().getScoreboard().getScoreObjectives();
      if (lvt_1_1_.isEmpty()) {
         p_198662_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.objectives.list.empty", new Object[0]), false);
      } else {
         p_198662_0_.sendFeedback(new TranslationTextComponent("commands.scoreboard.objectives.list.success", new Object[]{lvt_1_1_.size(), TextComponentUtils.makeList(lvt_1_1_, ScoreObjective::func_197890_e)}), false);
      }

      return lvt_1_1_.size();
   }
}
