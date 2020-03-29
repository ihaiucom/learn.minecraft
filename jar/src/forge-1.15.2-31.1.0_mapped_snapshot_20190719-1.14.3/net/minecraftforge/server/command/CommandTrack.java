package net.minecraftforge.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.server.timings.ForgeTimings;
import net.minecraftforge.server.timings.TimeTracker;

class CommandTrack {
   private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#####0.00");

   static ArgumentBuilder<CommandSource, ?> register() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("track").then(CommandTrack.StartTrackingCommand.register())).then(CommandTrack.ResetTrackingCommand.register())).then(CommandTrack.TrackResultsEntity.register())).then(CommandTrack.TrackResultsTileEntity.register())).then(CommandTrack.StartTrackingCommand.register());
   }

   private static class TrackResultsTileEntity {
      static ArgumentBuilder<CommandSource, ?> register() {
         return Commands.literal("te").executes((ctx) -> {
            return CommandTrack.TrackResults.execute((CommandSource)ctx.getSource(), TimeTracker.TILE_ENTITY_UPDATE, (data) -> {
               TileEntity te = (TileEntity)data.getObject().get();
               if (te == null) {
                  return new TranslationTextComponent("commands.forge.tracking.invalid", new Object[0]);
               } else {
                  BlockPos pos = te.getPos();
                  double averageTimings = data.getAverageTimings();
                  String tickTime = (averageTimings > 1000.0D ? CommandTrack.TIME_FORMAT.format(averageTimings / 1000.0D) : CommandTrack.TIME_FORMAT.format(averageTimings)) + (averageTimings < 1000.0D ? "μs" : "ms");
                  return new TranslationTextComponent("commands.forge.tracking.timing_entry", new Object[]{te.getType().getRegistryName(), DimensionType.getKey(te.getWorld().dimension.getType()), pos.getX(), pos.getY(), pos.getZ(), tickTime});
               }
            });
         });
      }
   }

   private static class TrackResultsEntity {
      static ArgumentBuilder<CommandSource, ?> register() {
         return Commands.literal("entity").executes((ctx) -> {
            return CommandTrack.TrackResults.execute((CommandSource)ctx.getSource(), TimeTracker.ENTITY_UPDATE, (data) -> {
               Entity entity = (Entity)data.getObject().get();
               if (entity == null) {
                  return new TranslationTextComponent("commands.forge.tracking.invalid", new Object[0]);
               } else {
                  BlockPos pos = entity.getPosition();
                  double averageTimings = data.getAverageTimings();
                  String tickTime = (averageTimings > 1000.0D ? CommandTrack.TIME_FORMAT.format(averageTimings / 1000.0D) : CommandTrack.TIME_FORMAT.format(averageTimings)) + (averageTimings < 1000.0D ? "μs" : "ms");
                  return new TranslationTextComponent("commands.forge.tracking.timing_entry", new Object[]{entity.getType().getRegistryName(), DimensionType.getKey(entity.world.dimension.getType()), pos.getX(), pos.getY(), pos.getZ(), tickTime});
               }
            });
         });
      }
   }

   private static class TrackResults {
      private static <T> List<ForgeTimings<T>> getSortedTimings(TimeTracker<T> tracker) {
         ArrayList<ForgeTimings<T>> list = new ArrayList();
         list.addAll(tracker.getTimingData());
         list.sort(Comparator.comparingDouble(ForgeTimings::getAverageTimings));
         Collections.reverse(list);
         return list;
      }

      private static <T> int execute(CommandSource source, TimeTracker<T> tracker, Function<ForgeTimings<T>, ITextComponent> toString) throws CommandException {
         List<ForgeTimings<T>> timingsList = getSortedTimings(tracker);
         if (timingsList.isEmpty()) {
            source.sendFeedback(new TranslationTextComponent("commands.forge.tracking.no_data", new Object[0]), true);
         } else {
            timingsList.stream().filter((timings) -> {
               return timings.getObject().get() != null;
            }).limit(10L).forEach((timings) -> {
               source.sendFeedback((ITextComponent)toString.apply(timings), true);
            });
         }

         return 0;
      }
   }

   private static class ResetTrackingCommand {
      static ArgumentBuilder<CommandSource, ?> register() {
         return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reset").requires((cs) -> {
            return cs.hasPermissionLevel(2);
         })).then(Commands.literal("te").executes((ctx) -> {
            TimeTracker.TILE_ENTITY_UPDATE.reset();
            ((CommandSource)ctx.getSource()).sendFeedback(new TranslationTextComponent("commands.forge.tracking.te.reset", new Object[0]), true);
            return 0;
         }))).then(Commands.literal("entity").executes((ctx) -> {
            TimeTracker.ENTITY_UPDATE.reset();
            ((CommandSource)ctx.getSource()).sendFeedback(new TranslationTextComponent("commands.forge.tracking.entity.reset", new Object[0]), true);
            return 0;
         }));
      }
   }

   private static class StartTrackingCommand {
      static ArgumentBuilder<CommandSource, ?> register() {
         return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("start").requires((cs) -> {
            return cs.hasPermissionLevel(2);
         })).then(Commands.literal("te").then(Commands.argument("duration", IntegerArgumentType.integer(1)).executes((ctx) -> {
            int duration = IntegerArgumentType.getInteger(ctx, "duration");
            TimeTracker.TILE_ENTITY_UPDATE.reset();
            TimeTracker.TILE_ENTITY_UPDATE.enable(duration);
            ((CommandSource)ctx.getSource()).sendFeedback(new TranslationTextComponent("commands.forge.tracking.te.enabled", new Object[]{duration}), true);
            return 0;
         })))).then(Commands.literal("entity").then(Commands.argument("duration", IntegerArgumentType.integer(1)).executes((ctx) -> {
            int duration = IntegerArgumentType.getInteger(ctx, "duration");
            TimeTracker.ENTITY_UPDATE.reset();
            TimeTracker.ENTITY_UPDATE.enable(duration);
            ((CommandSource)ctx.getSource()).sendFeedback(new TranslationTextComponent("commands.forge.tracking.entity.enabled", new Object[]{duration}), true);
            return 0;
         })));
      }
   }
}
