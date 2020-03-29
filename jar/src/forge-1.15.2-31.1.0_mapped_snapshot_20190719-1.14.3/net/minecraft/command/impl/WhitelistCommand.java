package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.WhiteList;
import net.minecraft.server.management.WhitelistEntry;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class WhitelistCommand {
   private static final SimpleCommandExceptionType ALREADY_ON = new SimpleCommandExceptionType(new TranslationTextComponent("commands.whitelist.alreadyOn", new Object[0]));
   private static final SimpleCommandExceptionType ALREADY_OFF = new SimpleCommandExceptionType(new TranslationTextComponent("commands.whitelist.alreadyOff", new Object[0]));
   private static final SimpleCommandExceptionType PLAYER_ALREADY_WHITELISTED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.whitelist.add.failed", new Object[0]));
   private static final SimpleCommandExceptionType PLAYER_NOT_WHITELISTED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.whitelist.remove.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198873_0_) {
      p_198873_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("whitelist").requires((p_198877_0_) -> {
         return p_198877_0_.hasPermissionLevel(3);
      })).then(Commands.literal("on").executes((p_198872_0_) -> {
         return enableWhiteList((CommandSource)p_198872_0_.getSource());
      }))).then(Commands.literal("off").executes((p_198874_0_) -> {
         return disableWhiteList((CommandSource)p_198874_0_.getSource());
      }))).then(Commands.literal("list").executes((p_198878_0_) -> {
         return listWhitelistedPlayers((CommandSource)p_198878_0_.getSource());
      }))).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198879_0_, p_198879_1_) -> {
         PlayerList lvt_2_1_ = ((CommandSource)p_198879_0_.getSource()).getServer().getPlayerList();
         return ISuggestionProvider.suggest(lvt_2_1_.getPlayers().stream().filter((p_198871_1_) -> {
            return !lvt_2_1_.getWhitelistedPlayers().isWhitelisted(p_198871_1_.getGameProfile());
         }).map((p_200567_0_) -> {
            return p_200567_0_.getGameProfile().getName();
         }), p_198879_1_);
      }).executes((p_198875_0_) -> {
         return addPlayers((CommandSource)p_198875_0_.getSource(), GameProfileArgument.getGameProfiles(p_198875_0_, "targets"));
      })))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198881_0_, p_198881_1_) -> {
         return ISuggestionProvider.suggest(((CommandSource)p_198881_0_.getSource()).getServer().getPlayerList().getWhitelistedPlayerNames(), p_198881_1_);
      }).executes((p_198870_0_) -> {
         return removePlayers((CommandSource)p_198870_0_.getSource(), GameProfileArgument.getGameProfiles(p_198870_0_, "targets"));
      })))).then(Commands.literal("reload").executes((p_198882_0_) -> {
         return reload((CommandSource)p_198882_0_.getSource());
      })));
   }

   private static int reload(CommandSource p_198883_0_) {
      p_198883_0_.getServer().getPlayerList().reloadWhitelist();
      p_198883_0_.sendFeedback(new TranslationTextComponent("commands.whitelist.reloaded", new Object[0]), true);
      p_198883_0_.getServer().kickPlayersNotWhitelisted(p_198883_0_);
      return 1;
   }

   private static int addPlayers(CommandSource p_198880_0_, Collection<GameProfile> p_198880_1_) throws CommandSyntaxException {
      WhiteList lvt_2_1_ = p_198880_0_.getServer().getPlayerList().getWhitelistedPlayers();
      int lvt_3_1_ = 0;
      Iterator var4 = p_198880_1_.iterator();

      while(var4.hasNext()) {
         GameProfile lvt_5_1_ = (GameProfile)var4.next();
         if (!lvt_2_1_.isWhitelisted(lvt_5_1_)) {
            WhitelistEntry lvt_6_1_ = new WhitelistEntry(lvt_5_1_);
            lvt_2_1_.addEntry(lvt_6_1_);
            p_198880_0_.sendFeedback(new TranslationTextComponent("commands.whitelist.add.success", new Object[]{TextComponentUtils.getDisplayName(lvt_5_1_)}), true);
            ++lvt_3_1_;
         }
      }

      if (lvt_3_1_ == 0) {
         throw PLAYER_ALREADY_WHITELISTED.create();
      } else {
         return lvt_3_1_;
      }
   }

   private static int removePlayers(CommandSource p_198876_0_, Collection<GameProfile> p_198876_1_) throws CommandSyntaxException {
      WhiteList lvt_2_1_ = p_198876_0_.getServer().getPlayerList().getWhitelistedPlayers();
      int lvt_3_1_ = 0;
      Iterator var4 = p_198876_1_.iterator();

      while(var4.hasNext()) {
         GameProfile lvt_5_1_ = (GameProfile)var4.next();
         if (lvt_2_1_.isWhitelisted(lvt_5_1_)) {
            WhitelistEntry lvt_6_1_ = new WhitelistEntry(lvt_5_1_);
            lvt_2_1_.removeEntry(lvt_6_1_);
            p_198876_0_.sendFeedback(new TranslationTextComponent("commands.whitelist.remove.success", new Object[]{TextComponentUtils.getDisplayName(lvt_5_1_)}), true);
            ++lvt_3_1_;
         }
      }

      if (lvt_3_1_ == 0) {
         throw PLAYER_NOT_WHITELISTED.create();
      } else {
         p_198876_0_.getServer().kickPlayersNotWhitelisted(p_198876_0_);
         return lvt_3_1_;
      }
   }

   private static int enableWhiteList(CommandSource p_198884_0_) throws CommandSyntaxException {
      PlayerList lvt_1_1_ = p_198884_0_.getServer().getPlayerList();
      if (lvt_1_1_.isWhiteListEnabled()) {
         throw ALREADY_ON.create();
      } else {
         lvt_1_1_.setWhiteListEnabled(true);
         p_198884_0_.sendFeedback(new TranslationTextComponent("commands.whitelist.enabled", new Object[0]), true);
         p_198884_0_.getServer().kickPlayersNotWhitelisted(p_198884_0_);
         return 1;
      }
   }

   private static int disableWhiteList(CommandSource p_198885_0_) throws CommandSyntaxException {
      PlayerList lvt_1_1_ = p_198885_0_.getServer().getPlayerList();
      if (!lvt_1_1_.isWhiteListEnabled()) {
         throw ALREADY_OFF.create();
      } else {
         lvt_1_1_.setWhiteListEnabled(false);
         p_198885_0_.sendFeedback(new TranslationTextComponent("commands.whitelist.disabled", new Object[0]), true);
         return 1;
      }
   }

   private static int listWhitelistedPlayers(CommandSource p_198886_0_) {
      String[] lvt_1_1_ = p_198886_0_.getServer().getPlayerList().getWhitelistedPlayerNames();
      if (lvt_1_1_.length == 0) {
         p_198886_0_.sendFeedback(new TranslationTextComponent("commands.whitelist.none", new Object[0]), false);
      } else {
         p_198886_0_.sendFeedback(new TranslationTextComponent("commands.whitelist.list", new Object[]{lvt_1_1_.length, String.join(", ", lvt_1_1_)}), false);
      }

      return lvt_1_1_.length;
   }
}
