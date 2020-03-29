package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.server.management.IPBanList;
import net.minecraft.util.text.TranslationTextComponent;

public class PardonIpCommand {
   private static final SimpleCommandExceptionType IP_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.pardonip.invalid", new Object[0]));
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.pardonip.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198553_0_) {
      p_198553_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pardon-ip").requires((p_198556_0_) -> {
         return p_198556_0_.getServer().getPlayerList().getBannedIPs().isLanServer() && p_198556_0_.hasPermissionLevel(3);
      })).then(Commands.argument("target", StringArgumentType.word()).suggests((p_198554_0_, p_198554_1_) -> {
         return ISuggestionProvider.suggest(((CommandSource)p_198554_0_.getSource()).getServer().getPlayerList().getBannedIPs().getKeys(), p_198554_1_);
      }).executes((p_198555_0_) -> {
         return unbanIp((CommandSource)p_198555_0_.getSource(), StringArgumentType.getString(p_198555_0_, "target"));
      })));
   }

   private static int unbanIp(CommandSource p_198557_0_, String p_198557_1_) throws CommandSyntaxException {
      Matcher lvt_2_1_ = BanIpCommand.IP_PATTERN.matcher(p_198557_1_);
      if (!lvt_2_1_.matches()) {
         throw IP_INVALID_EXCEPTION.create();
      } else {
         IPBanList lvt_3_1_ = p_198557_0_.getServer().getPlayerList().getBannedIPs();
         if (!lvt_3_1_.isBanned(p_198557_1_)) {
            throw FAILED_EXCEPTION.create();
         } else {
            lvt_3_1_.removeEntry(p_198557_1_);
            p_198557_0_.sendFeedback(new TranslationTextComponent("commands.pardonip.success", new Object[]{p_198557_1_}), true);
            return 1;
         }
      }
   }
}
