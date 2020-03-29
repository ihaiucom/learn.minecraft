package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.TimeArgument;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class TimeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198823_0_) {
      p_198823_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("time").requires((p_198828_0_) -> {
         return p_198828_0_.hasPermissionLevel(2);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("set").then(Commands.literal("day").executes((p_198832_0_) -> {
         return setTime((CommandSource)p_198832_0_.getSource(), 1000);
      }))).then(Commands.literal("noon").executes((p_198825_0_) -> {
         return setTime((CommandSource)p_198825_0_.getSource(), 6000);
      }))).then(Commands.literal("night").executes((p_198822_0_) -> {
         return setTime((CommandSource)p_198822_0_.getSource(), 13000);
      }))).then(Commands.literal("midnight").executes((p_200563_0_) -> {
         return setTime((CommandSource)p_200563_0_.getSource(), 18000);
      }))).then(Commands.argument("time", TimeArgument.func_218091_a()).executes((p_200564_0_) -> {
         return setTime((CommandSource)p_200564_0_.getSource(), IntegerArgumentType.getInteger(p_200564_0_, "time"));
      })))).then(Commands.literal("add").then(Commands.argument("time", TimeArgument.func_218091_a()).executes((p_198830_0_) -> {
         return addTime((CommandSource)p_198830_0_.getSource(), IntegerArgumentType.getInteger(p_198830_0_, "time"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("query").then(Commands.literal("daytime").executes((p_198827_0_) -> {
         return sendQueryResults((CommandSource)p_198827_0_.getSource(), getDayTime(((CommandSource)p_198827_0_.getSource()).getWorld()));
      }))).then(Commands.literal("gametime").executes((p_198821_0_) -> {
         return sendQueryResults((CommandSource)p_198821_0_.getSource(), (int)(((CommandSource)p_198821_0_.getSource()).getWorld().getGameTime() % 2147483647L));
      }))).then(Commands.literal("day").executes((p_198831_0_) -> {
         return sendQueryResults((CommandSource)p_198831_0_.getSource(), (int)(((CommandSource)p_198831_0_.getSource()).getWorld().getDayTime() / 24000L % 2147483647L));
      }))));
   }

   private static int getDayTime(ServerWorld p_198833_0_) {
      return (int)(p_198833_0_.getDayTime() % 24000L);
   }

   private static int sendQueryResults(CommandSource p_198824_0_, int p_198824_1_) {
      p_198824_0_.sendFeedback(new TranslationTextComponent("commands.time.query", new Object[]{p_198824_1_}), false);
      return p_198824_1_;
   }

   public static int setTime(CommandSource p_198829_0_, int p_198829_1_) {
      Iterator var2 = p_198829_0_.getServer().getWorlds().iterator();

      while(var2.hasNext()) {
         ServerWorld lvt_3_1_ = (ServerWorld)var2.next();
         lvt_3_1_.setDayTime((long)p_198829_1_);
      }

      p_198829_0_.sendFeedback(new TranslationTextComponent("commands.time.set", new Object[]{p_198829_1_}), true);
      return getDayTime(p_198829_0_.getWorld());
   }

   public static int addTime(CommandSource p_198826_0_, int p_198826_1_) {
      Iterator var2 = p_198826_0_.getServer().getWorlds().iterator();

      while(var2.hasNext()) {
         ServerWorld lvt_3_1_ = (ServerWorld)var2.next();
         lvt_3_1_.setDayTime(lvt_3_1_.getDayTime() + (long)p_198826_1_);
      }

      int lvt_2_1_ = getDayTime(p_198826_0_.getWorld());
      p_198826_0_.sendFeedback(new TranslationTextComponent("commands.time.set", new Object[]{lvt_2_1_}), true);
      return lvt_2_1_;
   }
}
