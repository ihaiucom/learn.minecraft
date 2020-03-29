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

public class OpCommand {
   private static final SimpleCommandExceptionType ALREADY_OP = new SimpleCommandExceptionType(new TranslationTextComponent("commands.op.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198541_0_) {
      p_198541_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("op").requires((p_198545_0_) -> {
         return p_198545_0_.hasPermissionLevel(3);
      })).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198543_0_, p_198543_1_) -> {
         PlayerList lvt_2_1_ = ((CommandSource)p_198543_0_.getSource()).getServer().getPlayerList();
         return ISuggestionProvider.suggest(lvt_2_1_.getPlayers().stream().filter((p_198540_1_) -> {
            return !lvt_2_1_.canSendCommands(p_198540_1_.getGameProfile());
         }).map((p_200545_0_) -> {
            return p_200545_0_.getGameProfile().getName();
         }), p_198543_1_);
      }).executes((p_198544_0_) -> {
         return opPlayers((CommandSource)p_198544_0_.getSource(), GameProfileArgument.getGameProfiles(p_198544_0_, "targets"));
      })));
   }

   private static int opPlayers(CommandSource p_198542_0_, Collection<GameProfile> p_198542_1_) throws CommandSyntaxException {
      PlayerList lvt_2_1_ = p_198542_0_.getServer().getPlayerList();
      int lvt_3_1_ = 0;
      Iterator var4 = p_198542_1_.iterator();

      while(var4.hasNext()) {
         GameProfile lvt_5_1_ = (GameProfile)var4.next();
         if (!lvt_2_1_.canSendCommands(lvt_5_1_)) {
            lvt_2_1_.addOp(lvt_5_1_);
            ++lvt_3_1_;
            p_198542_0_.sendFeedback(new TranslationTextComponent("commands.op.success", new Object[]{((GameProfile)p_198542_1_.iterator().next()).getName()}), true);
         }
      }

      if (lvt_3_1_ == 0) {
         throw ALREADY_OP.create();
      } else {
         return lvt_3_1_;
      }
   }
}
