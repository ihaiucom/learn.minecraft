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
import net.minecraft.server.management.BanList;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class PardonCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.pardon.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198547_0_) {
      p_198547_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pardon").requires((p_198551_0_) -> {
         return p_198551_0_.getServer().getPlayerList().getBannedIPs().isLanServer() && p_198551_0_.hasPermissionLevel(3);
      })).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198549_0_, p_198549_1_) -> {
         return ISuggestionProvider.suggest(((CommandSource)p_198549_0_.getSource()).getServer().getPlayerList().getBannedPlayers().getKeys(), p_198549_1_);
      }).executes((p_198550_0_) -> {
         return unbanPlayers((CommandSource)p_198550_0_.getSource(), GameProfileArgument.getGameProfiles(p_198550_0_, "targets"));
      })));
   }

   private static int unbanPlayers(CommandSource p_198548_0_, Collection<GameProfile> p_198548_1_) throws CommandSyntaxException {
      BanList lvt_2_1_ = p_198548_0_.getServer().getPlayerList().getBannedPlayers();
      int lvt_3_1_ = 0;
      Iterator var4 = p_198548_1_.iterator();

      while(var4.hasNext()) {
         GameProfile lvt_5_1_ = (GameProfile)var4.next();
         if (lvt_2_1_.isBanned(lvt_5_1_)) {
            lvt_2_1_.removeEntry(lvt_5_1_);
            ++lvt_3_1_;
            p_198548_0_.sendFeedback(new TranslationTextComponent("commands.pardon.success", new Object[]{TextComponentUtils.getDisplayName(lvt_5_1_)}), true);
         }
      }

      if (lvt_3_1_ == 0) {
         throw FAILED_EXCEPTION.create();
      } else {
         return lvt_3_1_;
      }
   }
}
