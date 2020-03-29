package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;

public class DefaultGameModeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198340_0_) {
      LiteralArgumentBuilder<CommandSource> lvt_1_1_ = (LiteralArgumentBuilder)Commands.literal("defaultgamemode").requires((p_198342_0_) -> {
         return p_198342_0_.hasPermissionLevel(2);
      });
      GameType[] var2 = GameType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType lvt_5_1_ = var2[var4];
         if (lvt_5_1_ != GameType.NOT_SET) {
            lvt_1_1_.then(Commands.literal(lvt_5_1_.getName()).executes((p_198343_1_) -> {
               return setGameType((CommandSource)p_198343_1_.getSource(), lvt_5_1_);
            }));
         }
      }

      p_198340_0_.register(lvt_1_1_);
   }

   private static int setGameType(CommandSource p_198341_0_, GameType p_198341_1_) {
      int lvt_2_1_ = 0;
      MinecraftServer lvt_3_1_ = p_198341_0_.getServer();
      lvt_3_1_.setGameType(p_198341_1_);
      if (lvt_3_1_.getForceGamemode()) {
         Iterator var4 = lvt_3_1_.getPlayerList().getPlayers().iterator();

         while(var4.hasNext()) {
            ServerPlayerEntity lvt_5_1_ = (ServerPlayerEntity)var4.next();
            if (lvt_5_1_.interactionManager.getGameType() != p_198341_1_) {
               lvt_5_1_.setGameType(p_198341_1_);
               ++lvt_2_1_;
            }
         }
      }

      p_198341_0_.sendFeedback(new TranslationTextComponent("commands.defaultgamemode.success", new Object[]{p_198341_1_.getDisplayName()}), true);
      return lvt_2_1_;
   }
}
