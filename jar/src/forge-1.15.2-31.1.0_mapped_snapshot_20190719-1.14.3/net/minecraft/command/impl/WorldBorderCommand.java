package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
   private static final SimpleCommandExceptionType CENTER_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.center.failed", new Object[0]));
   private static final SimpleCommandExceptionType SIZE_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.set.failed.nochange", new Object[0]));
   private static final SimpleCommandExceptionType SIZE_TOO_SMALL = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.set.failed.small.", new Object[0]));
   private static final SimpleCommandExceptionType SIZE_TOO_BIG = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.set.failed.big.", new Object[0]));
   private static final SimpleCommandExceptionType WARNING_TIME_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.warning.time.failed", new Object[0]));
   private static final SimpleCommandExceptionType WARNING_DISTANCE_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.warning.distance.failed", new Object[0]));
   private static final SimpleCommandExceptionType DAMAGE_BUFFER_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.damage.buffer.failed", new Object[0]));
   private static final SimpleCommandExceptionType DAMAGE_AMOUNT_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.worldborder.damage.amount.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198894_0_) {
      p_198894_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("worldborder").requires((p_198903_0_) -> {
         return p_198903_0_.hasPermissionLevel(2);
      })).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((p_198908_0_) -> {
         return setSize((CommandSource)p_198908_0_.getSource(), ((CommandSource)p_198908_0_.getSource()).getWorld().getWorldBorder().getDiameter() + (double)FloatArgumentType.getFloat(p_198908_0_, "distance"), 0L);
      })).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198901_0_) -> {
         return setSize((CommandSource)p_198901_0_.getSource(), ((CommandSource)p_198901_0_.getSource()).getWorld().getWorldBorder().getDiameter() + (double)FloatArgumentType.getFloat(p_198901_0_, "distance"), ((CommandSource)p_198901_0_.getSource()).getWorld().getWorldBorder().getTimeUntilTarget() + (long)IntegerArgumentType.getInteger(p_198901_0_, "time") * 1000L);
      }))))).then(Commands.literal("set").then(((RequiredArgumentBuilder)Commands.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((p_198906_0_) -> {
         return setSize((CommandSource)p_198906_0_.getSource(), (double)FloatArgumentType.getFloat(p_198906_0_, "distance"), 0L);
      })).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198909_0_) -> {
         return setSize((CommandSource)p_198909_0_.getSource(), (double)FloatArgumentType.getFloat(p_198909_0_, "distance"), (long)IntegerArgumentType.getInteger(p_198909_0_, "time") * 1000L);
      }))))).then(Commands.literal("center").then(Commands.argument("pos", Vec2Argument.vec2()).executes((p_198893_0_) -> {
         return setCenter((CommandSource)p_198893_0_.getSource(), Vec2Argument.getVec2f(p_198893_0_, "pos"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("damage").then(Commands.literal("amount").then(Commands.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F)).executes((p_198897_0_) -> {
         return setDamageAmount((CommandSource)p_198897_0_.getSource(), FloatArgumentType.getFloat(p_198897_0_, "damagePerBlock"));
      })))).then(Commands.literal("buffer").then(Commands.argument("distance", FloatArgumentType.floatArg(0.0F)).executes((p_198905_0_) -> {
         return setDamageBuffer((CommandSource)p_198905_0_.getSource(), FloatArgumentType.getFloat(p_198905_0_, "distance"));
      }))))).then(Commands.literal("get").executes((p_198900_0_) -> {
         return getSize((CommandSource)p_198900_0_.getSource());
      }))).then(((LiteralArgumentBuilder)Commands.literal("warning").then(Commands.literal("distance").then(Commands.argument("distance", IntegerArgumentType.integer(0)).executes((p_198892_0_) -> {
         return setWarningDistance((CommandSource)p_198892_0_.getSource(), IntegerArgumentType.getInteger(p_198892_0_, "distance"));
      })))).then(Commands.literal("time").then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((p_198907_0_) -> {
         return setWarningTime((CommandSource)p_198907_0_.getSource(), IntegerArgumentType.getInteger(p_198907_0_, "time"));
      })))));
   }

   private static int setDamageBuffer(CommandSource p_198898_0_, float p_198898_1_) throws CommandSyntaxException {
      WorldBorder lvt_2_1_ = p_198898_0_.getWorld().getWorldBorder();
      if (lvt_2_1_.getDamageBuffer() == (double)p_198898_1_) {
         throw DAMAGE_BUFFER_NO_CHANGE.create();
      } else {
         lvt_2_1_.setDamageBuffer((double)p_198898_1_);
         p_198898_0_.sendFeedback(new TranslationTextComponent("commands.worldborder.damage.buffer.success", new Object[]{String.format(Locale.ROOT, "%.2f", p_198898_1_)}), true);
         return (int)p_198898_1_;
      }
   }

   private static int setDamageAmount(CommandSource p_198904_0_, float p_198904_1_) throws CommandSyntaxException {
      WorldBorder lvt_2_1_ = p_198904_0_.getWorld().getWorldBorder();
      if (lvt_2_1_.getDamagePerBlock() == (double)p_198904_1_) {
         throw DAMAGE_AMOUNT_NO_CHANGE.create();
      } else {
         lvt_2_1_.setDamagePerBlock((double)p_198904_1_);
         p_198904_0_.sendFeedback(new TranslationTextComponent("commands.worldborder.damage.amount.success", new Object[]{String.format(Locale.ROOT, "%.2f", p_198904_1_)}), true);
         return (int)p_198904_1_;
      }
   }

   private static int setWarningTime(CommandSource p_198902_0_, int p_198902_1_) throws CommandSyntaxException {
      WorldBorder lvt_2_1_ = p_198902_0_.getWorld().getWorldBorder();
      if (lvt_2_1_.getWarningTime() == p_198902_1_) {
         throw WARNING_TIME_NO_CHANGE.create();
      } else {
         lvt_2_1_.setWarningTime(p_198902_1_);
         p_198902_0_.sendFeedback(new TranslationTextComponent("commands.worldborder.warning.time.success", new Object[]{p_198902_1_}), true);
         return p_198902_1_;
      }
   }

   private static int setWarningDistance(CommandSource p_198899_0_, int p_198899_1_) throws CommandSyntaxException {
      WorldBorder lvt_2_1_ = p_198899_0_.getWorld().getWorldBorder();
      if (lvt_2_1_.getWarningDistance() == p_198899_1_) {
         throw WARNING_DISTANCE_NO_CHANGE.create();
      } else {
         lvt_2_1_.setWarningDistance(p_198899_1_);
         p_198899_0_.sendFeedback(new TranslationTextComponent("commands.worldborder.warning.distance.success", new Object[]{p_198899_1_}), true);
         return p_198899_1_;
      }
   }

   private static int getSize(CommandSource p_198910_0_) {
      double lvt_1_1_ = p_198910_0_.getWorld().getWorldBorder().getDiameter();
      p_198910_0_.sendFeedback(new TranslationTextComponent("commands.worldborder.get", new Object[]{String.format(Locale.ROOT, "%.0f", lvt_1_1_)}), false);
      return MathHelper.floor(lvt_1_1_ + 0.5D);
   }

   private static int setCenter(CommandSource p_198896_0_, Vec2f p_198896_1_) throws CommandSyntaxException {
      WorldBorder lvt_2_1_ = p_198896_0_.getWorld().getWorldBorder();
      if (lvt_2_1_.getCenterX() == (double)p_198896_1_.x && lvt_2_1_.getCenterZ() == (double)p_198896_1_.y) {
         throw CENTER_NO_CHANGE.create();
      } else {
         lvt_2_1_.setCenter((double)p_198896_1_.x, (double)p_198896_1_.y);
         p_198896_0_.sendFeedback(new TranslationTextComponent("commands.worldborder.center.success", new Object[]{String.format(Locale.ROOT, "%.2f", p_198896_1_.x), String.format("%.2f", p_198896_1_.y)}), true);
         return 0;
      }
   }

   private static int setSize(CommandSource p_198895_0_, double p_198895_1_, long p_198895_3_) throws CommandSyntaxException {
      WorldBorder lvt_5_1_ = p_198895_0_.getWorld().getWorldBorder();
      double lvt_6_1_ = lvt_5_1_.getDiameter();
      if (lvt_6_1_ == p_198895_1_) {
         throw SIZE_NO_CHANGE.create();
      } else if (p_198895_1_ < 1.0D) {
         throw SIZE_TOO_SMALL.create();
      } else if (p_198895_1_ > 6.0E7D) {
         throw SIZE_TOO_BIG.create();
      } else {
         if (p_198895_3_ > 0L) {
            lvt_5_1_.setTransition(lvt_6_1_, p_198895_1_, p_198895_3_);
            if (p_198895_1_ > lvt_6_1_) {
               p_198895_0_.sendFeedback(new TranslationTextComponent("commands.worldborder.set.grow", new Object[]{String.format(Locale.ROOT, "%.1f", p_198895_1_), Long.toString(p_198895_3_ / 1000L)}), true);
            } else {
               p_198895_0_.sendFeedback(new TranslationTextComponent("commands.worldborder.set.shrink", new Object[]{String.format(Locale.ROOT, "%.1f", p_198895_1_), Long.toString(p_198895_3_ / 1000L)}), true);
            }
         } else {
            lvt_5_1_.setTransition(p_198895_1_);
            p_198895_0_.sendFeedback(new TranslationTextComponent("commands.worldborder.set.immediate", new Object[]{String.format(Locale.ROOT, "%.1f", p_198895_1_)}), true);
         }

         return (int)(p_198895_1_ - lvt_6_1_);
      }
   }
}
