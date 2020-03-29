package net.minecraft.command.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.management.BanEntry;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TranslationTextComponent;

public class BanListCommand {
   public static void register(CommandDispatcher<CommandSource> p_198229_0_) {
      p_198229_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("banlist").requires((p_198233_0_) -> {
         return (p_198233_0_.getServer().getPlayerList().getBannedPlayers().isLanServer() || p_198233_0_.getServer().getPlayerList().getBannedIPs().isLanServer()) && p_198233_0_.hasPermissionLevel(3);
      })).executes((p_198231_0_) -> {
         PlayerList lvt_1_1_ = ((CommandSource)p_198231_0_.getSource()).getServer().getPlayerList();
         return sendBanList((CommandSource)p_198231_0_.getSource(), Lists.newArrayList(Iterables.concat(lvt_1_1_.getBannedPlayers().getEntries(), lvt_1_1_.getBannedIPs().getEntries())));
      })).then(Commands.literal("ips").executes((p_198228_0_) -> {
         return sendBanList((CommandSource)p_198228_0_.getSource(), ((CommandSource)p_198228_0_.getSource()).getServer().getPlayerList().getBannedIPs().getEntries());
      }))).then(Commands.literal("players").executes((p_198232_0_) -> {
         return sendBanList((CommandSource)p_198232_0_.getSource(), ((CommandSource)p_198232_0_.getSource()).getServer().getPlayerList().getBannedPlayers().getEntries());
      })));
   }

   private static int sendBanList(CommandSource p_198230_0_, Collection<? extends BanEntry<?>> p_198230_1_) {
      if (p_198230_1_.isEmpty()) {
         p_198230_0_.sendFeedback(new TranslationTextComponent("commands.banlist.none", new Object[0]), false);
      } else {
         p_198230_0_.sendFeedback(new TranslationTextComponent("commands.banlist.list", new Object[]{p_198230_1_.size()}), false);
         Iterator var2 = p_198230_1_.iterator();

         while(var2.hasNext()) {
            BanEntry<?> lvt_3_1_ = (BanEntry)var2.next();
            p_198230_0_.sendFeedback(new TranslationTextComponent("commands.banlist.entry", new Object[]{lvt_3_1_.getDisplayName(), lvt_3_1_.getBannedBy(), lvt_3_1_.getBanReason()}), false);
         }
      }

      return p_198230_1_.size();
   }
}
