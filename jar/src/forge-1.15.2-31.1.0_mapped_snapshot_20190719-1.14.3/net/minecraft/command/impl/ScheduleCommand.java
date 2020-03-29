package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.TimedFunction;
import net.minecraft.command.TimedFunctionTag;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.command.arguments.FunctionArgument;
import net.minecraft.command.arguments.TimeArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ScheduleCommand {
   private static final SimpleCommandExceptionType field_218913_a = new SimpleCommandExceptionType(new TranslationTextComponent("commands.schedule.same_tick", new Object[0]));
   private static final DynamicCommandExceptionType field_229811_b_ = new DynamicCommandExceptionType((p_229818_0_) -> {
      return new TranslationTextComponent("commands.schedule.cleared.failure", new Object[]{p_229818_0_});
   });
   private static final SuggestionProvider<CommandSource> field_229812_c_ = (p_229814_0_, p_229814_1_) -> {
      return ISuggestionProvider.suggest((Iterable)((CommandSource)p_229814_0_.getSource()).getWorld().getWorldInfo().getScheduledEvents().func_227574_a_(), p_229814_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_218909_0_) {
      p_218909_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("schedule").requires((p_229815_0_) -> {
         return p_229815_0_.hasPermissionLevel(2);
      })).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.func_200021_a()).suggests(FunctionCommand.FUNCTION_SUGGESTER).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("time", TimeArgument.func_218091_a()).executes((p_229823_0_) -> {
         return func_229816_a_((CommandSource)p_229823_0_.getSource(), FunctionArgument.func_218110_b(p_229823_0_, "function"), IntegerArgumentType.getInteger(p_229823_0_, "time"), true);
      })).then(Commands.literal("append").executes((p_229822_0_) -> {
         return func_229816_a_((CommandSource)p_229822_0_.getSource(), FunctionArgument.func_218110_b(p_229822_0_, "function"), IntegerArgumentType.getInteger(p_229822_0_, "time"), false);
      }))).then(Commands.literal("replace").executes((p_229821_0_) -> {
         return func_229816_a_((CommandSource)p_229821_0_.getSource(), FunctionArgument.func_218110_b(p_229821_0_, "function"), IntegerArgumentType.getInteger(p_229821_0_, "time"), true);
      })))))).then(Commands.literal("clear").then(Commands.argument("function", StringArgumentType.greedyString()).suggests(field_229812_c_).executes((p_229813_0_) -> {
         return func_229817_a_((CommandSource)p_229813_0_.getSource(), StringArgumentType.getString(p_229813_0_, "function"));
      }))));
   }

   private static int func_229816_a_(CommandSource p_229816_0_, Either<FunctionObject, Tag<FunctionObject>> p_229816_1_, int p_229816_2_, boolean p_229816_3_) throws CommandSyntaxException {
      if (p_229816_2_ == 0) {
         throw field_218913_a.create();
      } else {
         long lvt_4_1_ = p_229816_0_.getWorld().getGameTime() + (long)p_229816_2_;
         TimerCallbackManager<MinecraftServer> lvt_6_1_ = p_229816_0_.getWorld().getWorldInfo().getScheduledEvents();
         p_229816_1_.ifLeft((p_229820_6_) -> {
            ResourceLocation lvt_7_1_ = p_229820_6_.getId();
            String lvt_8_1_ = lvt_7_1_.toString();
            if (p_229816_3_) {
               lvt_6_1_.func_227575_a_(lvt_8_1_);
            }

            lvt_6_1_.func_227576_a_(lvt_8_1_, lvt_4_1_, new TimedFunction(lvt_7_1_));
            p_229816_0_.sendFeedback(new TranslationTextComponent("commands.schedule.created.function", new Object[]{lvt_7_1_, p_229816_2_, lvt_4_1_}), true);
         }).ifRight((p_229819_6_) -> {
            ResourceLocation lvt_7_1_ = p_229819_6_.getId();
            String lvt_8_1_ = "#" + lvt_7_1_.toString();
            if (p_229816_3_) {
               lvt_6_1_.func_227575_a_(lvt_8_1_);
            }

            lvt_6_1_.func_227576_a_(lvt_8_1_, lvt_4_1_, new TimedFunctionTag(lvt_7_1_));
            p_229816_0_.sendFeedback(new TranslationTextComponent("commands.schedule.created.tag", new Object[]{lvt_7_1_, p_229816_2_, lvt_4_1_}), true);
         });
         return (int)Math.floorMod(lvt_4_1_, 2147483647L);
      }
   }

   private static int func_229817_a_(CommandSource p_229817_0_, String p_229817_1_) throws CommandSyntaxException {
      int lvt_2_1_ = p_229817_0_.getWorld().getWorldInfo().getScheduledEvents().func_227575_a_(p_229817_1_);
      if (lvt_2_1_ == 0) {
         throw field_229811_b_.create(p_229817_1_);
      } else {
         p_229817_0_.sendFeedback(new TranslationTextComponent("commands.schedule.cleared.success", new Object[]{lvt_2_1_, p_229817_1_}), true);
         return lvt_2_1_;
      }
   }
}
