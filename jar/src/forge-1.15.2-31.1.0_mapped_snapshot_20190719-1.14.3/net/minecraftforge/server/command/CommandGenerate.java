package net.minecraftforge.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.WorldWorkerManager;

class CommandGenerate {
   static ArgumentBuilder<CommandSource, ?> register() {
      return ((LiteralArgumentBuilder)Commands.literal("generate").requires((cs) -> {
         return cs.hasPermissionLevel(4);
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("count", IntegerArgumentType.integer(1)).then(((RequiredArgumentBuilder)Commands.argument("dim", DimensionArgument.getDimension()).then(Commands.argument("interval", IntegerArgumentType.integer()).executes((ctx) -> {
         return execute((CommandSource)ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"), getInt(ctx, "count"), DimensionArgument.func_212592_a(ctx, "dim"), getInt(ctx, "interval"));
      }))).executes((ctx) -> {
         return execute((CommandSource)ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"), getInt(ctx, "count"), DimensionArgument.func_212592_a(ctx, "dim"), -1);
      }))).executes((ctx) -> {
         return execute((CommandSource)ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"), getInt(ctx, "count"), ((CommandSource)ctx.getSource()).getWorld().dimension.getType(), -1);
      })));
   }

   private static int getInt(CommandContext<CommandSource> ctx, String name) {
      return IntegerArgumentType.getInteger(ctx, name);
   }

   private static int execute(CommandSource source, BlockPos pos, int count, DimensionType dim, int interval) throws CommandException {
      BlockPos chunkpos = new BlockPos(pos.getX() >> 4, 0, pos.getZ() >> 4);
      ChunkGenWorker worker = new ChunkGenWorker(source, chunkpos, count, dim, interval);
      source.sendFeedback(worker.getStartMessage(source), true);
      WorldWorkerManager.addWorker(worker);
      return 0;
   }
}
