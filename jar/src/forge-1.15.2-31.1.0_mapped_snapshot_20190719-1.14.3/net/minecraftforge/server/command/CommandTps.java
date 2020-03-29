package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;

class CommandTps {
   private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType((dim) -> {
      return new TranslationTextComponent("commands.forge.tps.invalid", new Object[]{dim, StreamSupport.stream(DimensionType.getAll().spliterator(), false).map((d) -> {
         return DimensionType.getKey(d).toString();
      }).sorted().collect(Collectors.joining(", "))});
   });
   private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");

   static ArgumentBuilder<CommandSource, ?> register() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tps").requires((cs) -> {
         return cs.hasPermissionLevel(0);
      })).then(Commands.argument("dim", DimensionArgument.getDimension()).executes((ctx) -> {
         return sendTime((CommandSource)ctx.getSource(), DimensionArgument.func_212592_a(ctx, "dim"));
      }))).executes((ctx) -> {
         Iterator var1 = DimensionType.getAll().iterator();

         while(var1.hasNext()) {
            DimensionType dim = (DimensionType)var1.next();
            sendTime((CommandSource)ctx.getSource(), dim);
         }

         double meanTickTime = (double)mean(((CommandSource)ctx.getSource()).getServer().tickTimeArray) * 1.0E-6D;
         double meanTPS = Math.min(1000.0D / meanTickTime, 20.0D);
         ((CommandSource)ctx.getSource()).sendFeedback(new TranslationTextComponent("commands.forge.tps.summary.all", new Object[]{TIME_FORMATTER.format(meanTickTime), TIME_FORMATTER.format(meanTPS)}), true);
         return 0;
      });
   }

   private static int sendTime(CommandSource cs, DimensionType dim) throws CommandSyntaxException {
      long[] times = cs.getServer().getTickTime(dim);
      if (times == null) {
         throw INVALID_DIMENSION.create(DimensionType.getKey(dim));
      } else {
         double worldTickTime = (double)mean(times) * 1.0E-6D;
         double worldTPS = Math.min(1000.0D / worldTickTime, 20.0D);
         cs.sendFeedback(new TranslationTextComponent("commands.forge.tps.summary.named", new Object[]{dim.getId(), DimensionType.getKey(dim), TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS)}), true);
         return 1;
      }
   }

   private static long mean(long[] values) {
      long sum = 0L;
      long[] var3 = values;
      int var4 = values.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         long v = var3[var5];
         sum += v;
      }

      return sum / (long)values.length;
   }
}
