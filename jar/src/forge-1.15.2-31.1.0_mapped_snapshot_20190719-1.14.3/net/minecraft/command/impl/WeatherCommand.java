package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class WeatherCommand {
   public static void register(CommandDispatcher<CommandSource> p_198862_0_) {
      p_198862_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("weather").requires((p_198868_0_) -> {
         return p_198868_0_.hasPermissionLevel(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("clear").executes((p_198861_0_) -> {
         return setClear((CommandSource)p_198861_0_.getSource(), 6000);
      })).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_198864_0_) -> {
         return setClear((CommandSource)p_198864_0_.getSource(), IntegerArgumentType.getInteger(p_198864_0_, "duration") * 20);
      })))).then(((LiteralArgumentBuilder)Commands.literal("rain").executes((p_198860_0_) -> {
         return setRain((CommandSource)p_198860_0_.getSource(), 6000);
      })).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_198866_0_) -> {
         return setRain((CommandSource)p_198866_0_.getSource(), IntegerArgumentType.getInteger(p_198866_0_, "duration") * 20);
      })))).then(((LiteralArgumentBuilder)Commands.literal("thunder").executes((p_198859_0_) -> {
         return setThunder((CommandSource)p_198859_0_.getSource(), 6000);
      })).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((p_198867_0_) -> {
         return setThunder((CommandSource)p_198867_0_.getSource(), IntegerArgumentType.getInteger(p_198867_0_, "duration") * 20);
      }))));
   }

   private static int setClear(CommandSource p_198869_0_, int p_198869_1_) {
      p_198869_0_.getWorld().getWorldInfo().setClearWeatherTime(p_198869_1_);
      p_198869_0_.getWorld().getWorldInfo().setRainTime(0);
      p_198869_0_.getWorld().getWorldInfo().setThunderTime(0);
      p_198869_0_.getWorld().getWorldInfo().setRaining(false);
      p_198869_0_.getWorld().getWorldInfo().setThundering(false);
      p_198869_0_.sendFeedback(new TranslationTextComponent("commands.weather.set.clear", new Object[0]), true);
      return p_198869_1_;
   }

   private static int setRain(CommandSource p_198865_0_, int p_198865_1_) {
      p_198865_0_.getWorld().getWorldInfo().setClearWeatherTime(0);
      p_198865_0_.getWorld().getWorldInfo().setRainTime(p_198865_1_);
      p_198865_0_.getWorld().getWorldInfo().setThunderTime(p_198865_1_);
      p_198865_0_.getWorld().getWorldInfo().setRaining(true);
      p_198865_0_.getWorld().getWorldInfo().setThundering(false);
      p_198865_0_.sendFeedback(new TranslationTextComponent("commands.weather.set.rain", new Object[0]), true);
      return p_198865_1_;
   }

   private static int setThunder(CommandSource p_198863_0_, int p_198863_1_) {
      p_198863_0_.getWorld().getWorldInfo().setClearWeatherTime(0);
      p_198863_0_.getWorld().getWorldInfo().setRainTime(p_198863_1_);
      p_198863_0_.getWorld().getWorldInfo().setThunderTime(p_198863_1_);
      p_198863_0_.getWorld().getWorldInfo().setRaining(true);
      p_198863_0_.getWorld().getWorldInfo().setThundering(true);
      p_198863_0_.sendFeedback(new TranslationTextComponent("commands.weather.set.thunder", new Object[0]), true);
      return p_198863_1_;
   }
}
