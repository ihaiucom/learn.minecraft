package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.CustomServerBossInfo;
import net.minecraft.server.CustomServerBossInfoManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;

public class BossBarCommand {
   private static final DynamicCommandExceptionType BOSS_BAR_ID_TAKEN = new DynamicCommandExceptionType((p_208783_0_) -> {
      return new TranslationTextComponent("commands.bossbar.create.failed", new Object[]{p_208783_0_});
   });
   private static final DynamicCommandExceptionType NO_BOSSBAR_WITH_ID = new DynamicCommandExceptionType((p_208782_0_) -> {
      return new TranslationTextComponent("commands.bossbar.unknown", new Object[]{p_208782_0_});
   });
   private static final SimpleCommandExceptionType PLAYERS_ALREADY_ON_BOSSBAR = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.players.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ALREADY_NAME_OF_BOSSBAR = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.name.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ALREADY_COLOR_OF_BOSSBAR = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.color.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ALREADY_STYLE_OF_BOSSBAR = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.style.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ALREADY_VALUE_OF_BOSSBAR = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.value.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ALREADY_MAX_OF_BOSSBAR = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.max.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType BOSSBAR_ALREADY_HIDDEN = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.visibility.unchanged.hidden", new Object[0]));
   private static final SimpleCommandExceptionType BOSSBAR_ALREADY_VISIBLE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.bossbar.set.visibility.unchanged.visible", new Object[0]));
   public static final SuggestionProvider<CommandSource> SUGGESTIONS_PROVIDER = (p_201404_0_, p_201404_1_) -> {
      return ISuggestionProvider.suggestIterable(((CommandSource)p_201404_0_.getSource()).getServer().getCustomBossEvents().getIDs(), p_201404_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_201413_0_) {
      p_201413_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("bossbar").requires((p_201423_0_) -> {
         return p_201423_0_.hasPermissionLevel(2);
      })).then(Commands.literal("add").then(Commands.argument("id", ResourceLocationArgument.resourceLocation()).then(Commands.argument("name", ComponentArgument.component()).executes((p_201426_0_) -> {
         return createBossbar((CommandSource)p_201426_0_.getSource(), ResourceLocationArgument.getResourceLocation(p_201426_0_, "id"), ComponentArgument.getComponent(p_201426_0_, "name"));
      }))))).then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(SUGGESTIONS_PROVIDER).executes((p_201429_0_) -> {
         return removeBossbar((CommandSource)p_201429_0_.getSource(), getBossbar(p_201429_0_));
      })))).then(Commands.literal("list").executes((p_201396_0_) -> {
         return listBars((CommandSource)p_201396_0_.getSource());
      }))).then(Commands.literal("set").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(SUGGESTIONS_PROVIDER).then(Commands.literal("name").then(Commands.argument("name", ComponentArgument.component()).executes((p_201401_0_) -> {
         return setName((CommandSource)p_201401_0_.getSource(), getBossbar(p_201401_0_), ComponentArgument.getComponent(p_201401_0_, "name"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("color").then(Commands.literal("pink").executes((p_201409_0_) -> {
         return setColor((CommandSource)p_201409_0_.getSource(), getBossbar(p_201409_0_), BossInfo.Color.PINK);
      }))).then(Commands.literal("blue").executes((p_201422_0_) -> {
         return setColor((CommandSource)p_201422_0_.getSource(), getBossbar(p_201422_0_), BossInfo.Color.BLUE);
      }))).then(Commands.literal("red").executes((p_201417_0_) -> {
         return setColor((CommandSource)p_201417_0_.getSource(), getBossbar(p_201417_0_), BossInfo.Color.RED);
      }))).then(Commands.literal("green").executes((p_201424_0_) -> {
         return setColor((CommandSource)p_201424_0_.getSource(), getBossbar(p_201424_0_), BossInfo.Color.GREEN);
      }))).then(Commands.literal("yellow").executes((p_201393_0_) -> {
         return setColor((CommandSource)p_201393_0_.getSource(), getBossbar(p_201393_0_), BossInfo.Color.YELLOW);
      }))).then(Commands.literal("purple").executes((p_201391_0_) -> {
         return setColor((CommandSource)p_201391_0_.getSource(), getBossbar(p_201391_0_), BossInfo.Color.PURPLE);
      }))).then(Commands.literal("white").executes((p_201406_0_) -> {
         return setColor((CommandSource)p_201406_0_.getSource(), getBossbar(p_201406_0_), BossInfo.Color.WHITE);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("style").then(Commands.literal("progress").executes((p_201399_0_) -> {
         return setStyle((CommandSource)p_201399_0_.getSource(), getBossbar(p_201399_0_), BossInfo.Overlay.PROGRESS);
      }))).then(Commands.literal("notched_6").executes((p_201419_0_) -> {
         return setStyle((CommandSource)p_201419_0_.getSource(), getBossbar(p_201419_0_), BossInfo.Overlay.NOTCHED_6);
      }))).then(Commands.literal("notched_10").executes((p_201412_0_) -> {
         return setStyle((CommandSource)p_201412_0_.getSource(), getBossbar(p_201412_0_), BossInfo.Overlay.NOTCHED_10);
      }))).then(Commands.literal("notched_12").executes((p_201421_0_) -> {
         return setStyle((CommandSource)p_201421_0_.getSource(), getBossbar(p_201421_0_), BossInfo.Overlay.NOTCHED_12);
      }))).then(Commands.literal("notched_20").executes((p_201403_0_) -> {
         return setStyle((CommandSource)p_201403_0_.getSource(), getBossbar(p_201403_0_), BossInfo.Overlay.NOTCHED_20);
      })))).then(Commands.literal("value").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes((p_201408_0_) -> {
         return setValue((CommandSource)p_201408_0_.getSource(), getBossbar(p_201408_0_), IntegerArgumentType.getInteger(p_201408_0_, "value"));
      })))).then(Commands.literal("max").then(Commands.argument("max", IntegerArgumentType.integer(1)).executes((p_201395_0_) -> {
         return setMax((CommandSource)p_201395_0_.getSource(), getBossbar(p_201395_0_), IntegerArgumentType.getInteger(p_201395_0_, "max"));
      })))).then(Commands.literal("visible").then(Commands.argument("visible", BoolArgumentType.bool()).executes((p_201427_0_) -> {
         return setVisibility((CommandSource)p_201427_0_.getSource(), getBossbar(p_201427_0_), BoolArgumentType.getBool(p_201427_0_, "visible"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("players").executes((p_201430_0_) -> {
         return setPlayers((CommandSource)p_201430_0_.getSource(), getBossbar(p_201430_0_), Collections.emptyList());
      })).then(Commands.argument("targets", EntityArgument.players()).executes((p_201411_0_) -> {
         return setPlayers((CommandSource)p_201411_0_.getSource(), getBossbar(p_201411_0_), EntityArgument.getPlayersAllowingNone(p_201411_0_, "targets"));
      })))))).then(Commands.literal("get").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(SUGGESTIONS_PROVIDER).then(Commands.literal("value").executes((p_201418_0_) -> {
         return getValue((CommandSource)p_201418_0_.getSource(), getBossbar(p_201418_0_));
      }))).then(Commands.literal("max").executes((p_201398_0_) -> {
         return getMax((CommandSource)p_201398_0_.getSource(), getBossbar(p_201398_0_));
      }))).then(Commands.literal("visible").executes((p_201392_0_) -> {
         return getVisibility((CommandSource)p_201392_0_.getSource(), getBossbar(p_201392_0_));
      }))).then(Commands.literal("players").executes((p_201388_0_) -> {
         return getPlayers((CommandSource)p_201388_0_.getSource(), getBossbar(p_201388_0_));
      })))));
   }

   private static int getValue(CommandSource p_201414_0_, CustomServerBossInfo p_201414_1_) {
      p_201414_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.get.value", new Object[]{p_201414_1_.getFormattedName(), p_201414_1_.getValue()}), true);
      return p_201414_1_.getValue();
   }

   private static int getMax(CommandSource p_201402_0_, CustomServerBossInfo p_201402_1_) {
      p_201402_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.get.max", new Object[]{p_201402_1_.getFormattedName(), p_201402_1_.getMax()}), true);
      return p_201402_1_.getMax();
   }

   private static int getVisibility(CommandSource p_201389_0_, CustomServerBossInfo p_201389_1_) {
      if (p_201389_1_.isVisible()) {
         p_201389_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.get.visible.visible", new Object[]{p_201389_1_.getFormattedName()}), true);
         return 1;
      } else {
         p_201389_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.get.visible.hidden", new Object[]{p_201389_1_.getFormattedName()}), true);
         return 0;
      }
   }

   private static int getPlayers(CommandSource p_201425_0_, CustomServerBossInfo p_201425_1_) {
      if (p_201425_1_.getPlayers().isEmpty()) {
         p_201425_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.get.players.none", new Object[]{p_201425_1_.getFormattedName()}), true);
      } else {
         p_201425_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.get.players.some", new Object[]{p_201425_1_.getFormattedName(), p_201425_1_.getPlayers().size(), TextComponentUtils.makeList(p_201425_1_.getPlayers(), PlayerEntity::getDisplayName)}), true);
      }

      return p_201425_1_.getPlayers().size();
   }

   private static int setVisibility(CommandSource p_201410_0_, CustomServerBossInfo p_201410_1_, boolean p_201410_2_) throws CommandSyntaxException {
      if (p_201410_1_.isVisible() == p_201410_2_) {
         if (p_201410_2_) {
            throw BOSSBAR_ALREADY_VISIBLE.create();
         } else {
            throw BOSSBAR_ALREADY_HIDDEN.create();
         }
      } else {
         p_201410_1_.setVisible(p_201410_2_);
         if (p_201410_2_) {
            p_201410_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.set.visible.success.visible", new Object[]{p_201410_1_.getFormattedName()}), true);
         } else {
            p_201410_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.set.visible.success.hidden", new Object[]{p_201410_1_.getFormattedName()}), true);
         }

         return 0;
      }
   }

   private static int setValue(CommandSource p_201397_0_, CustomServerBossInfo p_201397_1_, int p_201397_2_) throws CommandSyntaxException {
      if (p_201397_1_.getValue() == p_201397_2_) {
         throw ALREADY_VALUE_OF_BOSSBAR.create();
      } else {
         p_201397_1_.setValue(p_201397_2_);
         p_201397_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.set.value.success", new Object[]{p_201397_1_.getFormattedName(), p_201397_2_}), true);
         return p_201397_2_;
      }
   }

   private static int setMax(CommandSource p_201394_0_, CustomServerBossInfo p_201394_1_, int p_201394_2_) throws CommandSyntaxException {
      if (p_201394_1_.getMax() == p_201394_2_) {
         throw ALREADY_MAX_OF_BOSSBAR.create();
      } else {
         p_201394_1_.setMax(p_201394_2_);
         p_201394_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.set.max.success", new Object[]{p_201394_1_.getFormattedName(), p_201394_2_}), true);
         return p_201394_2_;
      }
   }

   private static int setColor(CommandSource p_201415_0_, CustomServerBossInfo p_201415_1_, BossInfo.Color p_201415_2_) throws CommandSyntaxException {
      if (p_201415_1_.getColor().equals(p_201415_2_)) {
         throw ALREADY_COLOR_OF_BOSSBAR.create();
      } else {
         p_201415_1_.setColor(p_201415_2_);
         p_201415_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.set.color.success", new Object[]{p_201415_1_.getFormattedName()}), true);
         return 0;
      }
   }

   private static int setStyle(CommandSource p_201390_0_, CustomServerBossInfo p_201390_1_, BossInfo.Overlay p_201390_2_) throws CommandSyntaxException {
      if (p_201390_1_.getOverlay().equals(p_201390_2_)) {
         throw ALREADY_STYLE_OF_BOSSBAR.create();
      } else {
         p_201390_1_.setOverlay(p_201390_2_);
         p_201390_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.set.style.success", new Object[]{p_201390_1_.getFormattedName()}), true);
         return 0;
      }
   }

   private static int setName(CommandSource p_201420_0_, CustomServerBossInfo p_201420_1_, ITextComponent p_201420_2_) throws CommandSyntaxException {
      ITextComponent lvt_3_1_ = TextComponentUtils.updateForEntity(p_201420_0_, p_201420_2_, (Entity)null, 0);
      if (p_201420_1_.getName().equals(lvt_3_1_)) {
         throw ALREADY_NAME_OF_BOSSBAR.create();
      } else {
         p_201420_1_.setName(lvt_3_1_);
         p_201420_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.set.name.success", new Object[]{p_201420_1_.getFormattedName()}), true);
         return 0;
      }
   }

   private static int setPlayers(CommandSource p_201405_0_, CustomServerBossInfo p_201405_1_, Collection<ServerPlayerEntity> p_201405_2_) throws CommandSyntaxException {
      boolean lvt_3_1_ = p_201405_1_.setPlayers(p_201405_2_);
      if (!lvt_3_1_) {
         throw PLAYERS_ALREADY_ON_BOSSBAR.create();
      } else {
         if (p_201405_1_.getPlayers().isEmpty()) {
            p_201405_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.set.players.success.none", new Object[]{p_201405_1_.getFormattedName()}), true);
         } else {
            p_201405_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.set.players.success.some", new Object[]{p_201405_1_.getFormattedName(), p_201405_2_.size(), TextComponentUtils.makeList(p_201405_2_, PlayerEntity::getDisplayName)}), true);
         }

         return p_201405_1_.getPlayers().size();
      }
   }

   private static int listBars(CommandSource p_201428_0_) {
      Collection<CustomServerBossInfo> lvt_1_1_ = p_201428_0_.getServer().getCustomBossEvents().getBossbars();
      if (lvt_1_1_.isEmpty()) {
         p_201428_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.list.bars.none", new Object[0]), false);
      } else {
         p_201428_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.list.bars.some", new Object[]{lvt_1_1_.size(), TextComponentUtils.makeList(lvt_1_1_, CustomServerBossInfo::getFormattedName)}), false);
      }

      return lvt_1_1_.size();
   }

   private static int createBossbar(CommandSource p_201400_0_, ResourceLocation p_201400_1_, ITextComponent p_201400_2_) throws CommandSyntaxException {
      CustomServerBossInfoManager lvt_3_1_ = p_201400_0_.getServer().getCustomBossEvents();
      if (lvt_3_1_.get(p_201400_1_) != null) {
         throw BOSS_BAR_ID_TAKEN.create(p_201400_1_.toString());
      } else {
         CustomServerBossInfo lvt_4_1_ = lvt_3_1_.add(p_201400_1_, TextComponentUtils.updateForEntity(p_201400_0_, p_201400_2_, (Entity)null, 0));
         p_201400_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.create.success", new Object[]{lvt_4_1_.getFormattedName()}), true);
         return lvt_3_1_.getBossbars().size();
      }
   }

   private static int removeBossbar(CommandSource p_201407_0_, CustomServerBossInfo p_201407_1_) {
      CustomServerBossInfoManager lvt_2_1_ = p_201407_0_.getServer().getCustomBossEvents();
      p_201407_1_.removeAllPlayers();
      lvt_2_1_.remove(p_201407_1_);
      p_201407_0_.sendFeedback(new TranslationTextComponent("commands.bossbar.remove.success", new Object[]{p_201407_1_.getFormattedName()}), true);
      return lvt_2_1_.getBossbars().size();
   }

   public static CustomServerBossInfo getBossbar(CommandContext<CommandSource> p_201416_0_) throws CommandSyntaxException {
      ResourceLocation lvt_1_1_ = ResourceLocationArgument.getResourceLocation(p_201416_0_, "id");
      CustomServerBossInfo lvt_2_1_ = ((CommandSource)p_201416_0_.getSource()).getServer().getCustomBossEvents().get(lvt_1_1_);
      if (lvt_2_1_ == null) {
         throw NO_BOSSBAR_WITH_ID.create(lvt_1_1_.toString());
      } else {
         return lvt_2_1_;
      }
   }
}
