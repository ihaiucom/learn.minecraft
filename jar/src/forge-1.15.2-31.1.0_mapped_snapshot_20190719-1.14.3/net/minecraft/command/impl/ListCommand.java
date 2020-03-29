package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class ListCommand {
   public static void register(CommandDispatcher<CommandSource> p_198522_0_) {
      p_198522_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes((p_198523_0_) -> {
         return listNames((CommandSource)p_198523_0_.getSource());
      })).then(Commands.literal("uuids").executes((p_208202_0_) -> {
         return listUUIDs((CommandSource)p_208202_0_.getSource());
      })));
   }

   private static int listNames(CommandSource p_198524_0_) {
      return listPlayers(p_198524_0_, PlayerEntity::getDisplayName);
   }

   private static int listUUIDs(CommandSource p_208201_0_) {
      return listPlayers(p_208201_0_, PlayerEntity::getDisplayNameAndUUID);
   }

   private static int listPlayers(CommandSource p_208200_0_, Function<ServerPlayerEntity, ITextComponent> p_208200_1_) {
      PlayerList lvt_2_1_ = p_208200_0_.getServer().getPlayerList();
      List<ServerPlayerEntity> lvt_3_1_ = lvt_2_1_.getPlayers();
      ITextComponent lvt_4_1_ = TextComponentUtils.makeList(lvt_3_1_, p_208200_1_);
      p_208200_0_.sendFeedback(new TranslationTextComponent("commands.list.players", new Object[]{lvt_3_1_.size(), lvt_2_1_.getMaxPlayers(), lvt_4_1_}), false);
      return lvt_3_1_.size();
   }
}
