package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class TeamCommand {
   private static final SimpleCommandExceptionType DUPLICATE_TEAM_NAME = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.add.duplicate", new Object[0]));
   private static final DynamicCommandExceptionType TEAM_NAME_TOO_LONG = new DynamicCommandExceptionType((p_208916_0_) -> {
      return new TranslationTextComponent("commands.team.add.longName", new Object[]{p_208916_0_});
   });
   private static final SimpleCommandExceptionType EMPTY_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.empty.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType NAME_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.name.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType COLOR_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.color.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType FRIENDLY_FIRE_ALREADY_ON = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.friendlyfire.alreadyEnabled", new Object[0]));
   private static final SimpleCommandExceptionType FRIENDLY_FIRE_ALREADY_OFF = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.friendlyfire.alreadyDisabled", new Object[0]));
   private static final SimpleCommandExceptionType SEE_FRIENDLY_INVISIBLES_ALREADY_ON = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyEnabled", new Object[0]));
   private static final SimpleCommandExceptionType SEE_FRIENDLY_INVISIBLES_ALREADY_OFF = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyDisabled", new Object[0]));
   private static final SimpleCommandExceptionType NAMETAG_VISIBILITY_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.nametagVisibility.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType DEATH_MESSAGE_VISIBILITY_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.deathMessageVisibility.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType COLLISION_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.collisionRule.unchanged", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198771_0_) {
      p_198771_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("team").requires((p_198780_0_) -> {
         return p_198780_0_.hasPermissionLevel(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("list").executes((p_198760_0_) -> {
         return listTeams((CommandSource)p_198760_0_.getSource());
      })).then(Commands.argument("team", TeamArgument.team()).executes((p_198763_0_) -> {
         return listMembers((CommandSource)p_198763_0_.getSource(), TeamArgument.getTeam(p_198763_0_, "team"));
      })))).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("team", StringArgumentType.word()).executes((p_198767_0_) -> {
         return addTeam((CommandSource)p_198767_0_.getSource(), StringArgumentType.getString(p_198767_0_, "team"));
      })).then(Commands.argument("displayName", ComponentArgument.component()).executes((p_198779_0_) -> {
         return addTeam((CommandSource)p_198779_0_.getSource(), StringArgumentType.getString(p_198779_0_, "team"), ComponentArgument.getComponent(p_198779_0_, "displayName"));
      }))))).then(Commands.literal("remove").then(Commands.argument("team", TeamArgument.team()).executes((p_198773_0_) -> {
         return removeTeam((CommandSource)p_198773_0_.getSource(), TeamArgument.getTeam(p_198773_0_, "team"));
      })))).then(Commands.literal("empty").then(Commands.argument("team", TeamArgument.team()).executes((p_198785_0_) -> {
         return emptyTeam((CommandSource)p_198785_0_.getSource(), TeamArgument.getTeam(p_198785_0_, "team"));
      })))).then(Commands.literal("join").then(((RequiredArgumentBuilder)Commands.argument("team", TeamArgument.team()).executes((p_198758_0_) -> {
         return joinTeam((CommandSource)p_198758_0_.getSource(), TeamArgument.getTeam(p_198758_0_, "team"), Collections.singleton(((CommandSource)p_198758_0_.getSource()).assertIsEntity().getScoreboardName()));
      })).then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).executes((p_198755_0_) -> {
         return joinTeam((CommandSource)p_198755_0_.getSource(), TeamArgument.getTeam(p_198755_0_, "team"), ScoreHolderArgument.getScoreHolder(p_198755_0_, "members"));
      }))))).then(Commands.literal("leave").then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).executes((p_198765_0_) -> {
         return leaveFromTeams((CommandSource)p_198765_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198765_0_, "members"));
      })))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("team", TeamArgument.team()).then(Commands.literal("displayName").then(Commands.argument("displayName", ComponentArgument.component()).executes((p_211919_0_) -> {
         return setDisplayName((CommandSource)p_211919_0_.getSource(), TeamArgument.getTeam(p_211919_0_, "team"), ComponentArgument.getComponent(p_211919_0_, "displayName"));
      })))).then(Commands.literal("color").then(Commands.argument("value", ColorArgument.color()).executes((p_198762_0_) -> {
         return setColor((CommandSource)p_198762_0_.getSource(), TeamArgument.getTeam(p_198762_0_, "team"), ColorArgument.getColor(p_198762_0_, "value"));
      })))).then(Commands.literal("friendlyFire").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_198775_0_) -> {
         return setAllowFriendlyFire((CommandSource)p_198775_0_.getSource(), TeamArgument.getTeam(p_198775_0_, "team"), BoolArgumentType.getBool(p_198775_0_, "allowed"));
      })))).then(Commands.literal("seeFriendlyInvisibles").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_198770_0_) -> {
         return setCanSeeFriendlyInvisibles((CommandSource)p_198770_0_.getSource(), TeamArgument.getTeam(p_198770_0_, "team"), BoolArgumentType.getBool(p_198770_0_, "allowed"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("nametagVisibility").then(Commands.literal("never").executes((p_198778_0_) -> {
         return setNameTagVisibility((CommandSource)p_198778_0_.getSource(), TeamArgument.getTeam(p_198778_0_, "team"), Team.Visible.NEVER);
      }))).then(Commands.literal("hideForOtherTeams").executes((p_198764_0_) -> {
         return setNameTagVisibility((CommandSource)p_198764_0_.getSource(), TeamArgument.getTeam(p_198764_0_, "team"), Team.Visible.HIDE_FOR_OTHER_TEAMS);
      }))).then(Commands.literal("hideForOwnTeam").executes((p_198766_0_) -> {
         return setNameTagVisibility((CommandSource)p_198766_0_.getSource(), TeamArgument.getTeam(p_198766_0_, "team"), Team.Visible.HIDE_FOR_OWN_TEAM);
      }))).then(Commands.literal("always").executes((p_198759_0_) -> {
         return setNameTagVisibility((CommandSource)p_198759_0_.getSource(), TeamArgument.getTeam(p_198759_0_, "team"), Team.Visible.ALWAYS);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deathMessageVisibility").then(Commands.literal("never").executes((p_198789_0_) -> {
         return setDeathMessageVisibility((CommandSource)p_198789_0_.getSource(), TeamArgument.getTeam(p_198789_0_, "team"), Team.Visible.NEVER);
      }))).then(Commands.literal("hideForOtherTeams").executes((p_198791_0_) -> {
         return setDeathMessageVisibility((CommandSource)p_198791_0_.getSource(), TeamArgument.getTeam(p_198791_0_, "team"), Team.Visible.HIDE_FOR_OTHER_TEAMS);
      }))).then(Commands.literal("hideForOwnTeam").executes((p_198769_0_) -> {
         return setDeathMessageVisibility((CommandSource)p_198769_0_.getSource(), TeamArgument.getTeam(p_198769_0_, "team"), Team.Visible.HIDE_FOR_OWN_TEAM);
      }))).then(Commands.literal("always").executes((p_198774_0_) -> {
         return setDeathMessageVisibility((CommandSource)p_198774_0_.getSource(), TeamArgument.getTeam(p_198774_0_, "team"), Team.Visible.ALWAYS);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("collisionRule").then(Commands.literal("never").executes((p_198761_0_) -> {
         return setCollisionRule((CommandSource)p_198761_0_.getSource(), TeamArgument.getTeam(p_198761_0_, "team"), Team.CollisionRule.NEVER);
      }))).then(Commands.literal("pushOwnTeam").executes((p_198756_0_) -> {
         return setCollisionRule((CommandSource)p_198756_0_.getSource(), TeamArgument.getTeam(p_198756_0_, "team"), Team.CollisionRule.PUSH_OWN_TEAM);
      }))).then(Commands.literal("pushOtherTeams").executes((p_198754_0_) -> {
         return setCollisionRule((CommandSource)p_198754_0_.getSource(), TeamArgument.getTeam(p_198754_0_, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS);
      }))).then(Commands.literal("always").executes((p_198790_0_) -> {
         return setCollisionRule((CommandSource)p_198790_0_.getSource(), TeamArgument.getTeam(p_198790_0_, "team"), Team.CollisionRule.ALWAYS);
      })))).then(Commands.literal("prefix").then(Commands.argument("prefix", ComponentArgument.component()).executes((p_207514_0_) -> {
         return setPrefix((CommandSource)p_207514_0_.getSource(), TeamArgument.getTeam(p_207514_0_, "team"), ComponentArgument.getComponent(p_207514_0_, "prefix"));
      })))).then(Commands.literal("suffix").then(Commands.argument("suffix", ComponentArgument.component()).executes((p_207516_0_) -> {
         return setSuffix((CommandSource)p_207516_0_.getSource(), TeamArgument.getTeam(p_207516_0_, "team"), ComponentArgument.getComponent(p_207516_0_, "suffix"));
      }))))));
   }

   private static int leaveFromTeams(CommandSource p_198786_0_, Collection<String> p_198786_1_) {
      Scoreboard lvt_2_1_ = p_198786_0_.getServer().getScoreboard();
      Iterator var3 = p_198786_1_.iterator();

      while(var3.hasNext()) {
         String lvt_4_1_ = (String)var3.next();
         lvt_2_1_.removePlayerFromTeams(lvt_4_1_);
      }

      if (p_198786_1_.size() == 1) {
         p_198786_0_.sendFeedback(new TranslationTextComponent("commands.team.leave.success.single", new Object[]{p_198786_1_.iterator().next()}), true);
      } else {
         p_198786_0_.sendFeedback(new TranslationTextComponent("commands.team.leave.success.multiple", new Object[]{p_198786_1_.size()}), true);
      }

      return p_198786_1_.size();
   }

   private static int joinTeam(CommandSource p_198768_0_, ScorePlayerTeam p_198768_1_, Collection<String> p_198768_2_) {
      Scoreboard lvt_3_1_ = p_198768_0_.getServer().getScoreboard();
      Iterator var4 = p_198768_2_.iterator();

      while(var4.hasNext()) {
         String lvt_5_1_ = (String)var4.next();
         lvt_3_1_.addPlayerToTeam(lvt_5_1_, p_198768_1_);
      }

      if (p_198768_2_.size() == 1) {
         p_198768_0_.sendFeedback(new TranslationTextComponent("commands.team.join.success.single", new Object[]{p_198768_2_.iterator().next(), p_198768_1_.getCommandName()}), true);
      } else {
         p_198768_0_.sendFeedback(new TranslationTextComponent("commands.team.join.success.multiple", new Object[]{p_198768_2_.size(), p_198768_1_.getCommandName()}), true);
      }

      return p_198768_2_.size();
   }

   private static int setNameTagVisibility(CommandSource p_198777_0_, ScorePlayerTeam p_198777_1_, Team.Visible p_198777_2_) throws CommandSyntaxException {
      if (p_198777_1_.getNameTagVisibility() == p_198777_2_) {
         throw NAMETAG_VISIBILITY_NO_CHANGE.create();
      } else {
         p_198777_1_.setNameTagVisibility(p_198777_2_);
         p_198777_0_.sendFeedback(new TranslationTextComponent("commands.team.option.nametagVisibility.success", new Object[]{p_198777_1_.getCommandName(), p_198777_2_.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setDeathMessageVisibility(CommandSource p_198776_0_, ScorePlayerTeam p_198776_1_, Team.Visible p_198776_2_) throws CommandSyntaxException {
      if (p_198776_1_.getDeathMessageVisibility() == p_198776_2_) {
         throw DEATH_MESSAGE_VISIBILITY_NO_CHANGE.create();
      } else {
         p_198776_1_.setDeathMessageVisibility(p_198776_2_);
         p_198776_0_.sendFeedback(new TranslationTextComponent("commands.team.option.deathMessageVisibility.success", new Object[]{p_198776_1_.getCommandName(), p_198776_2_.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setCollisionRule(CommandSource p_198787_0_, ScorePlayerTeam p_198787_1_, Team.CollisionRule p_198787_2_) throws CommandSyntaxException {
      if (p_198787_1_.getCollisionRule() == p_198787_2_) {
         throw COLLISION_NO_CHANGE.create();
      } else {
         p_198787_1_.setCollisionRule(p_198787_2_);
         p_198787_0_.sendFeedback(new TranslationTextComponent("commands.team.option.collisionRule.success", new Object[]{p_198787_1_.getCommandName(), p_198787_2_.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setCanSeeFriendlyInvisibles(CommandSource p_198783_0_, ScorePlayerTeam p_198783_1_, boolean p_198783_2_) throws CommandSyntaxException {
      if (p_198783_1_.getSeeFriendlyInvisiblesEnabled() == p_198783_2_) {
         if (p_198783_2_) {
            throw SEE_FRIENDLY_INVISIBLES_ALREADY_ON.create();
         } else {
            throw SEE_FRIENDLY_INVISIBLES_ALREADY_OFF.create();
         }
      } else {
         p_198783_1_.setSeeFriendlyInvisiblesEnabled(p_198783_2_);
         p_198783_0_.sendFeedback(new TranslationTextComponent("commands.team.option.seeFriendlyInvisibles." + (p_198783_2_ ? "enabled" : "disabled"), new Object[]{p_198783_1_.getCommandName()}), true);
         return 0;
      }
   }

   private static int setAllowFriendlyFire(CommandSource p_198781_0_, ScorePlayerTeam p_198781_1_, boolean p_198781_2_) throws CommandSyntaxException {
      if (p_198781_1_.getAllowFriendlyFire() == p_198781_2_) {
         if (p_198781_2_) {
            throw FRIENDLY_FIRE_ALREADY_ON.create();
         } else {
            throw FRIENDLY_FIRE_ALREADY_OFF.create();
         }
      } else {
         p_198781_1_.setAllowFriendlyFire(p_198781_2_);
         p_198781_0_.sendFeedback(new TranslationTextComponent("commands.team.option.friendlyfire." + (p_198781_2_ ? "enabled" : "disabled"), new Object[]{p_198781_1_.getCommandName()}), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSource p_211920_0_, ScorePlayerTeam p_211920_1_, ITextComponent p_211920_2_) throws CommandSyntaxException {
      if (p_211920_1_.getDisplayName().equals(p_211920_2_)) {
         throw NAME_NO_CHANGE.create();
      } else {
         p_211920_1_.setDisplayName(p_211920_2_);
         p_211920_0_.sendFeedback(new TranslationTextComponent("commands.team.option.name.success", new Object[]{p_211920_1_.getCommandName()}), true);
         return 0;
      }
   }

   private static int setColor(CommandSource p_198757_0_, ScorePlayerTeam p_198757_1_, TextFormatting p_198757_2_) throws CommandSyntaxException {
      if (p_198757_1_.getColor() == p_198757_2_) {
         throw COLOR_NO_CHANGE.create();
      } else {
         p_198757_1_.setColor(p_198757_2_);
         p_198757_0_.sendFeedback(new TranslationTextComponent("commands.team.option.color.success", new Object[]{p_198757_1_.getCommandName(), p_198757_2_.getFriendlyName()}), true);
         return 0;
      }
   }

   private static int emptyTeam(CommandSource p_198788_0_, ScorePlayerTeam p_198788_1_) throws CommandSyntaxException {
      Scoreboard lvt_2_1_ = p_198788_0_.getServer().getScoreboard();
      Collection<String> lvt_3_1_ = Lists.newArrayList(p_198788_1_.getMembershipCollection());
      if (lvt_3_1_.isEmpty()) {
         throw EMPTY_NO_CHANGE.create();
      } else {
         Iterator var4 = lvt_3_1_.iterator();

         while(var4.hasNext()) {
            String lvt_5_1_ = (String)var4.next();
            lvt_2_1_.removePlayerFromTeam(lvt_5_1_, p_198788_1_);
         }

         p_198788_0_.sendFeedback(new TranslationTextComponent("commands.team.empty.success", new Object[]{lvt_3_1_.size(), p_198788_1_.getCommandName()}), true);
         return lvt_3_1_.size();
      }
   }

   private static int removeTeam(CommandSource p_198784_0_, ScorePlayerTeam p_198784_1_) {
      Scoreboard lvt_2_1_ = p_198784_0_.getServer().getScoreboard();
      lvt_2_1_.removeTeam(p_198784_1_);
      p_198784_0_.sendFeedback(new TranslationTextComponent("commands.team.remove.success", new Object[]{p_198784_1_.getCommandName()}), true);
      return lvt_2_1_.getTeams().size();
   }

   private static int addTeam(CommandSource p_211916_0_, String p_211916_1_) throws CommandSyntaxException {
      return addTeam(p_211916_0_, p_211916_1_, new StringTextComponent(p_211916_1_));
   }

   private static int addTeam(CommandSource p_211917_0_, String p_211917_1_, ITextComponent p_211917_2_) throws CommandSyntaxException {
      Scoreboard lvt_3_1_ = p_211917_0_.getServer().getScoreboard();
      if (lvt_3_1_.getTeam(p_211917_1_) != null) {
         throw DUPLICATE_TEAM_NAME.create();
      } else if (p_211917_1_.length() > 16) {
         throw TEAM_NAME_TOO_LONG.create(16);
      } else {
         ScorePlayerTeam lvt_4_1_ = lvt_3_1_.createTeam(p_211917_1_);
         lvt_4_1_.setDisplayName(p_211917_2_);
         p_211917_0_.sendFeedback(new TranslationTextComponent("commands.team.add.success", new Object[]{lvt_4_1_.getCommandName()}), true);
         return lvt_3_1_.getTeams().size();
      }
   }

   private static int listMembers(CommandSource p_198782_0_, ScorePlayerTeam p_198782_1_) {
      Collection<String> lvt_2_1_ = p_198782_1_.getMembershipCollection();
      if (lvt_2_1_.isEmpty()) {
         p_198782_0_.sendFeedback(new TranslationTextComponent("commands.team.list.members.empty", new Object[]{p_198782_1_.getCommandName()}), false);
      } else {
         p_198782_0_.sendFeedback(new TranslationTextComponent("commands.team.list.members.success", new Object[]{p_198782_1_.getCommandName(), lvt_2_1_.size(), TextComponentUtils.makeGreenSortedList(lvt_2_1_)}), false);
      }

      return lvt_2_1_.size();
   }

   private static int listTeams(CommandSource p_198792_0_) {
      Collection<ScorePlayerTeam> lvt_1_1_ = p_198792_0_.getServer().getScoreboard().getTeams();
      if (lvt_1_1_.isEmpty()) {
         p_198792_0_.sendFeedback(new TranslationTextComponent("commands.team.list.teams.empty", new Object[0]), false);
      } else {
         p_198792_0_.sendFeedback(new TranslationTextComponent("commands.team.list.teams.success", new Object[]{lvt_1_1_.size(), TextComponentUtils.makeList(lvt_1_1_, ScorePlayerTeam::getCommandName)}), false);
      }

      return lvt_1_1_.size();
   }

   private static int setPrefix(CommandSource p_207515_0_, ScorePlayerTeam p_207515_1_, ITextComponent p_207515_2_) {
      p_207515_1_.setPrefix(p_207515_2_);
      p_207515_0_.sendFeedback(new TranslationTextComponent("commands.team.option.prefix.success", new Object[]{p_207515_2_}), false);
      return 1;
   }

   private static int setSuffix(CommandSource p_207517_0_, ScorePlayerTeam p_207517_1_, ITextComponent p_207517_2_) {
      p_207517_1_.setSuffix(p_207517_2_);
      p_207517_0_.sendFeedback(new TranslationTextComponent("commands.team.option.suffix.success", new Object[]{p_207517_2_}), false);
      return 1;
   }
}
