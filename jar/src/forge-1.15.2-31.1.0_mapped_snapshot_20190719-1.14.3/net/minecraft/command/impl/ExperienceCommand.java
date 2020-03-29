package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class ExperienceCommand {
   private static final SimpleCommandExceptionType SET_POINTS_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.experience.set.points.invalid", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198437_0_) {
      LiteralCommandNode<CommandSource> lvt_1_1_ = p_198437_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("experience").requires((p_198442_0_) -> {
         return p_198442_0_.hasPermissionLevel(2);
      })).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer()).executes((p_198445_0_) -> {
         return addExperience((CommandSource)p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"), IntegerArgumentType.getInteger(p_198445_0_, "amount"), ExperienceCommand.Type.POINTS);
      })).then(Commands.literal("points").executes((p_198447_0_) -> {
         return addExperience((CommandSource)p_198447_0_.getSource(), EntityArgument.getPlayers(p_198447_0_, "targets"), IntegerArgumentType.getInteger(p_198447_0_, "amount"), ExperienceCommand.Type.POINTS);
      }))).then(Commands.literal("levels").executes((p_198436_0_) -> {
         return addExperience((CommandSource)p_198436_0_.getSource(), EntityArgument.getPlayers(p_198436_0_, "targets"), IntegerArgumentType.getInteger(p_198436_0_, "amount"), ExperienceCommand.Type.LEVELS);
      })))))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer(0)).executes((p_198439_0_) -> {
         return setExperience((CommandSource)p_198439_0_.getSource(), EntityArgument.getPlayers(p_198439_0_, "targets"), IntegerArgumentType.getInteger(p_198439_0_, "amount"), ExperienceCommand.Type.POINTS);
      })).then(Commands.literal("points").executes((p_198444_0_) -> {
         return setExperience((CommandSource)p_198444_0_.getSource(), EntityArgument.getPlayers(p_198444_0_, "targets"), IntegerArgumentType.getInteger(p_198444_0_, "amount"), ExperienceCommand.Type.POINTS);
      }))).then(Commands.literal("levels").executes((p_198440_0_) -> {
         return setExperience((CommandSource)p_198440_0_.getSource(), EntityArgument.getPlayers(p_198440_0_, "targets"), IntegerArgumentType.getInteger(p_198440_0_, "amount"), ExperienceCommand.Type.LEVELS);
      })))))).then(Commands.literal("query").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.player()).then(Commands.literal("points").executes((p_198435_0_) -> {
         return queryExperience((CommandSource)p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"), ExperienceCommand.Type.POINTS);
      }))).then(Commands.literal("levels").executes((p_198446_0_) -> {
         return queryExperience((CommandSource)p_198446_0_.getSource(), EntityArgument.getPlayer(p_198446_0_, "targets"), ExperienceCommand.Type.LEVELS);
      })))));
      p_198437_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("xp").requires((p_198441_0_) -> {
         return p_198441_0_.hasPermissionLevel(2);
      })).redirect(lvt_1_1_));
   }

   private static int queryExperience(CommandSource p_198443_0_, ServerPlayerEntity p_198443_1_, ExperienceCommand.Type p_198443_2_) {
      int lvt_3_1_ = p_198443_2_.xpGetter.applyAsInt(p_198443_1_);
      p_198443_0_.sendFeedback(new TranslationTextComponent("commands.experience.query." + p_198443_2_.name, new Object[]{p_198443_1_.getDisplayName(), lvt_3_1_}), false);
      return lvt_3_1_;
   }

   private static int addExperience(CommandSource p_198448_0_, Collection<? extends ServerPlayerEntity> p_198448_1_, int p_198448_2_, ExperienceCommand.Type p_198448_3_) {
      Iterator var4 = p_198448_1_.iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity lvt_5_1_ = (ServerPlayerEntity)var4.next();
         p_198448_3_.xpAdder.accept(lvt_5_1_, p_198448_2_);
      }

      if (p_198448_1_.size() == 1) {
         p_198448_0_.sendFeedback(new TranslationTextComponent("commands.experience.add." + p_198448_3_.name + ".success.single", new Object[]{p_198448_2_, ((ServerPlayerEntity)p_198448_1_.iterator().next()).getDisplayName()}), true);
      } else {
         p_198448_0_.sendFeedback(new TranslationTextComponent("commands.experience.add." + p_198448_3_.name + ".success.multiple", new Object[]{p_198448_2_, p_198448_1_.size()}), true);
      }

      return p_198448_1_.size();
   }

   private static int setExperience(CommandSource p_198438_0_, Collection<? extends ServerPlayerEntity> p_198438_1_, int p_198438_2_, ExperienceCommand.Type p_198438_3_) throws CommandSyntaxException {
      int lvt_4_1_ = 0;
      Iterator var5 = p_198438_1_.iterator();

      while(var5.hasNext()) {
         ServerPlayerEntity lvt_6_1_ = (ServerPlayerEntity)var5.next();
         if (p_198438_3_.xpSetter.test(lvt_6_1_, p_198438_2_)) {
            ++lvt_4_1_;
         }
      }

      if (lvt_4_1_ == 0) {
         throw SET_POINTS_INVALID_EXCEPTION.create();
      } else {
         if (p_198438_1_.size() == 1) {
            p_198438_0_.sendFeedback(new TranslationTextComponent("commands.experience.set." + p_198438_3_.name + ".success.single", new Object[]{p_198438_2_, ((ServerPlayerEntity)p_198438_1_.iterator().next()).getDisplayName()}), true);
         } else {
            p_198438_0_.sendFeedback(new TranslationTextComponent("commands.experience.set." + p_198438_3_.name + ".success.multiple", new Object[]{p_198438_2_, p_198438_1_.size()}), true);
         }

         return p_198438_1_.size();
      }
   }

   static enum Type {
      POINTS("points", PlayerEntity::giveExperiencePoints, (p_198424_0_, p_198424_1_) -> {
         if (p_198424_1_ >= p_198424_0_.xpBarCap()) {
            return false;
         } else {
            p_198424_0_.func_195394_a(p_198424_1_);
            return true;
         }
      }, (p_198422_0_) -> {
         return MathHelper.floor(p_198422_0_.experience * (float)p_198422_0_.xpBarCap());
      }),
      LEVELS("levels", ServerPlayerEntity::addExperienceLevel, (p_198425_0_, p_198425_1_) -> {
         p_198425_0_.func_195399_b(p_198425_1_);
         return true;
      }, (p_198427_0_) -> {
         return p_198427_0_.experienceLevel;
      });

      public final BiConsumer<ServerPlayerEntity, Integer> xpAdder;
      public final BiPredicate<ServerPlayerEntity, Integer> xpSetter;
      public final String name;
      private final ToIntFunction<ServerPlayerEntity> xpGetter;

      private Type(String p_i48027_3_, BiConsumer<ServerPlayerEntity, Integer> p_i48027_4_, BiPredicate<ServerPlayerEntity, Integer> p_i48027_5_, ToIntFunction<ServerPlayerEntity> p_i48027_6_) {
         this.xpAdder = p_i48027_4_;
         this.name = p_i48027_3_;
         this.xpSetter = p_i48027_5_;
         this.xpGetter = p_i48027_6_;
      }
   }
}
