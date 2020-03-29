package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.server.management.IPBanList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BanIpCommand {
   public static final Pattern IP_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
   private static final SimpleCommandExceptionType IP_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.banip.invalid", new Object[0]));
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.banip.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198220_0_) {
      p_198220_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban-ip").requires((p_198222_0_) -> {
         return p_198222_0_.getServer().getPlayerList().getBannedIPs().isLanServer() && p_198222_0_.hasPermissionLevel(3);
      })).then(((RequiredArgumentBuilder)Commands.argument("target", StringArgumentType.word()).executes((p_198219_0_) -> {
         return banUsernameOrIp((CommandSource)p_198219_0_.getSource(), StringArgumentType.getString(p_198219_0_, "target"), (ITextComponent)null);
      })).then(Commands.argument("reason", MessageArgument.message()).executes((p_198221_0_) -> {
         return banUsernameOrIp((CommandSource)p_198221_0_.getSource(), StringArgumentType.getString(p_198221_0_, "target"), MessageArgument.getMessage(p_198221_0_, "reason"));
      }))));
   }

   private static int banUsernameOrIp(CommandSource p_198223_0_, String p_198223_1_, @Nullable ITextComponent p_198223_2_) throws CommandSyntaxException {
      Matcher lvt_3_1_ = IP_PATTERN.matcher(p_198223_1_);
      if (lvt_3_1_.matches()) {
         return banIpAddress(p_198223_0_, p_198223_1_, p_198223_2_);
      } else {
         ServerPlayerEntity lvt_4_1_ = p_198223_0_.getServer().getPlayerList().getPlayerByUsername(p_198223_1_);
         if (lvt_4_1_ != null) {
            return banIpAddress(p_198223_0_, lvt_4_1_.getPlayerIP(), p_198223_2_);
         } else {
            throw IP_INVALID.create();
         }
      }
   }

   private static int banIpAddress(CommandSource p_198224_0_, String p_198224_1_, @Nullable ITextComponent p_198224_2_) throws CommandSyntaxException {
      IPBanList lvt_3_1_ = p_198224_0_.getServer().getPlayerList().getBannedIPs();
      if (lvt_3_1_.isBanned(p_198224_1_)) {
         throw FAILED_EXCEPTION.create();
      } else {
         List<ServerPlayerEntity> lvt_4_1_ = p_198224_0_.getServer().getPlayerList().getPlayersMatchingAddress(p_198224_1_);
         IPBanEntry lvt_5_1_ = new IPBanEntry(p_198224_1_, (Date)null, p_198224_0_.getName(), (Date)null, p_198224_2_ == null ? null : p_198224_2_.getString());
         lvt_3_1_.addEntry(lvt_5_1_);
         p_198224_0_.sendFeedback(new TranslationTextComponent("commands.banip.success", new Object[]{p_198224_1_, lvt_5_1_.getBanReason()}), true);
         if (!lvt_4_1_.isEmpty()) {
            p_198224_0_.sendFeedback(new TranslationTextComponent("commands.banip.info", new Object[]{lvt_4_1_.size(), EntitySelector.joinNames(lvt_4_1_)}), true);
         }

         Iterator var6 = lvt_4_1_.iterator();

         while(var6.hasNext()) {
            ServerPlayerEntity lvt_7_1_ = (ServerPlayerEntity)var6.next();
            lvt_7_1_.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.ip_banned", new Object[0]));
         }

         return lvt_4_1_.size();
      }
   }
}
