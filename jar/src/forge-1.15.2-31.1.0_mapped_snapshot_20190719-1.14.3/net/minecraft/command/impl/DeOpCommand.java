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
import net.minecraft.util.text.TranslationTextComponent;

public class DeOpCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.deop.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198321_0_) {
      p_198321_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deop").requires((p_198325_0_) -> {
         return p_198325_0_.hasPermissionLevel(3);
      })).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198323_0_, p_198323_1_) -> {
         return ISuggestionProvider.suggest(((CommandSource)p_198323_0_.getSource()).getServer().getPlayerList().getOppedPlayerNames(), p_198323_1_);
      }).executes((p_198324_0_) -> {
         return deopPlayers((CommandSource)p_198324_0_.getSource(), GameProfileArgument.getGameProfiles(p_198324_0_, "targets"));
      })));
   }

   private static int deopPlayers(CommandSource p_198322_0_, Collection<GameProfile> p_198322_1_) throws CommandSyntaxException {
      PlayerList lvt_2_1_ = p_198322_0_.getServer().getPlayerList();
      int lvt_3_1_ = 0;
      Iterator var4 = p_198322_1_.iterator();

      while(var4.hasNext()) {
         GameProfile lvt_5_1_ = (GameProfile)var4.next();
         if (lvt_2_1_.canSendCommands(lvt_5_1_)) {
            lvt_2_1_.removeOp(lvt_5_1_);
            ++lvt_3_1_;
            p_198322_0_.sendFeedback(new TranslationTextComponent("commands.deop.success", new Object[]{((GameProfile)p_198322_1_.iterator().next()).getName()}), true);
         }
      }

      if (lvt_3_1_ == 0) {
         throw FAILED_EXCEPTION.create();
      } else {
         p_198322_0_.getServer().kickPlayersNotWhitelisted(p_198322_0_);
         return lvt_3_1_;
      }
   }
}
