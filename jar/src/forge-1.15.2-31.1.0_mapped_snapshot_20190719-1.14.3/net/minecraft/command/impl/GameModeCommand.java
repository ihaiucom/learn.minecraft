package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;

public class GameModeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198482_0_) {
      LiteralArgumentBuilder<CommandSource> lvt_1_1_ = (LiteralArgumentBuilder)Commands.literal("gamemode").requires((p_198485_0_) -> {
         return p_198485_0_.hasPermissionLevel(2);
      });
      GameType[] var2 = GameType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType lvt_5_1_ = var2[var4];
         if (lvt_5_1_ != GameType.NOT_SET) {
            lvt_1_1_.then(((LiteralArgumentBuilder)Commands.literal(lvt_5_1_.getName()).executes((p_198483_1_) -> {
               return setGameMode(p_198483_1_, Collections.singleton(((CommandSource)p_198483_1_.getSource()).asPlayer()), lvt_5_1_);
            })).then(Commands.argument("target", EntityArgument.players()).executes((p_198486_1_) -> {
               return setGameMode(p_198486_1_, EntityArgument.getPlayers(p_198486_1_, "target"), lvt_5_1_);
            })));
         }
      }

      p_198482_0_.register(lvt_1_1_);
   }

   private static void sendGameModeFeedback(CommandSource p_208517_0_, ServerPlayerEntity p_208517_1_, GameType p_208517_2_) {
      ITextComponent lvt_3_1_ = new TranslationTextComponent("gameMode." + p_208517_2_.getName(), new Object[0]);
      if (p_208517_0_.getEntity() == p_208517_1_) {
         p_208517_0_.sendFeedback(new TranslationTextComponent("commands.gamemode.success.self", new Object[]{lvt_3_1_}), true);
      } else {
         if (p_208517_0_.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
            p_208517_1_.sendMessage(new TranslationTextComponent("gameMode.changed", new Object[]{lvt_3_1_}));
         }

         p_208517_0_.sendFeedback(new TranslationTextComponent("commands.gamemode.success.other", new Object[]{p_208517_1_.getDisplayName(), lvt_3_1_}), true);
      }

   }

   private static int setGameMode(CommandContext<CommandSource> p_198484_0_, Collection<ServerPlayerEntity> p_198484_1_, GameType p_198484_2_) {
      int lvt_3_1_ = 0;
      Iterator var4 = p_198484_1_.iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity lvt_5_1_ = (ServerPlayerEntity)var4.next();
         if (lvt_5_1_.interactionManager.getGameType() != p_198484_2_) {
            lvt_5_1_.setGameType(p_198484_2_);
            sendGameModeFeedback((CommandSource)p_198484_0_.getSource(), lvt_5_1_, p_198484_2_);
            ++lvt_3_1_;
         }
      }

      return lvt_3_1_;
   }
}
