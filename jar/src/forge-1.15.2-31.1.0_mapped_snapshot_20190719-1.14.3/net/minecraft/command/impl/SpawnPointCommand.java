package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class SpawnPointCommand {
   public static void register(CommandDispatcher<CommandSource> p_198695_0_) {
      p_198695_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawnpoint").requires((p_198699_0_) -> {
         return p_198699_0_.hasPermissionLevel(2);
      })).executes((p_198697_0_) -> {
         return setSpawnPoint((CommandSource)p_198697_0_.getSource(), Collections.singleton(((CommandSource)p_198697_0_.getSource()).asPlayer()), new BlockPos(((CommandSource)p_198697_0_.getSource()).getPos()));
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((p_198694_0_) -> {
         return setSpawnPoint((CommandSource)p_198694_0_.getSource(), EntityArgument.getPlayers(p_198694_0_, "targets"), new BlockPos(((CommandSource)p_198694_0_.getSource()).getPos()));
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_198698_0_) -> {
         return setSpawnPoint((CommandSource)p_198698_0_.getSource(), EntityArgument.getPlayers(p_198698_0_, "targets"), BlockPosArgument.getBlockPos(p_198698_0_, "pos"));
      }))));
   }

   private static int setSpawnPoint(CommandSource p_198696_0_, Collection<ServerPlayerEntity> p_198696_1_, BlockPos p_198696_2_) {
      Iterator var3 = p_198696_1_.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity lvt_4_1_ = (ServerPlayerEntity)var3.next();
         lvt_4_1_.func_226560_a_(p_198696_2_, true, false);
      }

      if (p_198696_1_.size() == 1) {
         p_198696_0_.sendFeedback(new TranslationTextComponent("commands.spawnpoint.success.single", new Object[]{p_198696_2_.getX(), p_198696_2_.getY(), p_198696_2_.getZ(), ((ServerPlayerEntity)p_198696_1_.iterator().next()).getDisplayName()}), true);
      } else {
         p_198696_0_.sendFeedback(new TranslationTextComponent("commands.spawnpoint.success.multiple", new Object[]{p_198696_2_.getX(), p_198696_2_.getY(), p_198696_2_.getZ(), p_198696_1_.size()}), true);
      }

      return p_198696_1_.size();
   }
}
