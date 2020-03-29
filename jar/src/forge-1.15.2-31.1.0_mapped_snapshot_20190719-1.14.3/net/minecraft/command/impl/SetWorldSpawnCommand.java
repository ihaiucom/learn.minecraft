package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class SetWorldSpawnCommand {
   public static void register(CommandDispatcher<CommandSource> p_198702_0_) {
      p_198702_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setworldspawn").requires((p_198704_0_) -> {
         return p_198704_0_.hasPermissionLevel(2);
      })).executes((p_198700_0_) -> {
         return setSpawn((CommandSource)p_198700_0_.getSource(), new BlockPos(((CommandSource)p_198700_0_.getSource()).getPos()));
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_198703_0_) -> {
         return setSpawn((CommandSource)p_198703_0_.getSource(), BlockPosArgument.getBlockPos(p_198703_0_, "pos"));
      })));
   }

   private static int setSpawn(CommandSource p_198701_0_, BlockPos p_198701_1_) {
      p_198701_0_.getWorld().setSpawnPoint(p_198701_1_);
      p_198701_0_.getServer().getPlayerList().sendPacketToAllPlayers(new SSpawnPositionPacket(p_198701_1_));
      p_198701_0_.sendFeedback(new TranslationTextComponent("commands.setworldspawn.success", new Object[]{p_198701_1_.getX(), p_198701_1_.getY(), p_198701_1_.getZ()}), true);
      return 1;
   }
}
