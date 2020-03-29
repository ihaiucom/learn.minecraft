package net.minecraft.command.impl;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.PotionArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.TranslationTextComponent;

public class EffectCommand {
   private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.effect.give.failed", new Object[0]));
   private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.effect.clear.everything.failed", new Object[0]));
   private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.effect.clear.specific.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198353_0_) {
      p_198353_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("effect").requires((p_198359_0_) -> {
         return p_198359_0_.hasPermissionLevel(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("clear").executes((p_198352_0_) -> {
         return clearAllEffects((CommandSource)p_198352_0_.getSource(), ImmutableList.of(((CommandSource)p_198352_0_.getSource()).assertIsEntity()));
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).executes((p_198356_0_) -> {
         return clearAllEffects((CommandSource)p_198356_0_.getSource(), EntityArgument.getEntities(p_198356_0_, "targets"));
      })).then(Commands.argument("effect", PotionArgument.mobEffect()).executes((p_198351_0_) -> {
         return clearEffect((CommandSource)p_198351_0_.getSource(), EntityArgument.getEntities(p_198351_0_, "targets"), PotionArgument.getMobEffect(p_198351_0_, "effect"));
      }))))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("effect", PotionArgument.mobEffect()).executes((p_198357_0_) -> {
         return addEffect((CommandSource)p_198357_0_.getSource(), EntityArgument.getEntities(p_198357_0_, "targets"), PotionArgument.getMobEffect(p_198357_0_, "effect"), (Integer)null, 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((p_198350_0_) -> {
         return addEffect((CommandSource)p_198350_0_.getSource(), EntityArgument.getEntities(p_198350_0_, "targets"), PotionArgument.getMobEffect(p_198350_0_, "effect"), IntegerArgumentType.getInteger(p_198350_0_, "seconds"), 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((p_198358_0_) -> {
         return addEffect((CommandSource)p_198358_0_.getSource(), EntityArgument.getEntities(p_198358_0_, "targets"), PotionArgument.getMobEffect(p_198358_0_, "effect"), IntegerArgumentType.getInteger(p_198358_0_, "seconds"), IntegerArgumentType.getInteger(p_198358_0_, "amplifier"), true);
      })).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((p_229759_0_) -> {
         return addEffect((CommandSource)p_229759_0_.getSource(), EntityArgument.getEntities(p_229759_0_, "targets"), PotionArgument.getMobEffect(p_229759_0_, "effect"), IntegerArgumentType.getInteger(p_229759_0_, "seconds"), IntegerArgumentType.getInteger(p_229759_0_, "amplifier"), !BoolArgumentType.getBool(p_229759_0_, "hideParticles"));
      }))))))));
   }

   private static int addEffect(CommandSource p_198360_0_, Collection<? extends Entity> p_198360_1_, Effect p_198360_2_, @Nullable Integer p_198360_3_, int p_198360_4_, boolean p_198360_5_) throws CommandSyntaxException {
      int lvt_6_1_ = 0;
      int lvt_7_4_;
      if (p_198360_3_ != null) {
         if (p_198360_2_.isInstant()) {
            lvt_7_4_ = p_198360_3_;
         } else {
            lvt_7_4_ = p_198360_3_ * 20;
         }
      } else if (p_198360_2_.isInstant()) {
         lvt_7_4_ = 1;
      } else {
         lvt_7_4_ = 600;
      }

      Iterator var8 = p_198360_1_.iterator();

      while(var8.hasNext()) {
         Entity lvt_9_1_ = (Entity)var8.next();
         if (lvt_9_1_ instanceof LivingEntity) {
            EffectInstance lvt_10_1_ = new EffectInstance(p_198360_2_, lvt_7_4_, p_198360_4_, false, p_198360_5_);
            if (((LivingEntity)lvt_9_1_).addPotionEffect(lvt_10_1_)) {
               ++lvt_6_1_;
            }
         }
      }

      if (lvt_6_1_ == 0) {
         throw GIVE_FAILED_EXCEPTION.create();
      } else {
         if (p_198360_1_.size() == 1) {
            p_198360_0_.sendFeedback(new TranslationTextComponent("commands.effect.give.success.single", new Object[]{p_198360_2_.getDisplayName(), ((Entity)p_198360_1_.iterator().next()).getDisplayName(), lvt_7_4_ / 20}), true);
         } else {
            p_198360_0_.sendFeedback(new TranslationTextComponent("commands.effect.give.success.multiple", new Object[]{p_198360_2_.getDisplayName(), p_198360_1_.size(), lvt_7_4_ / 20}), true);
         }

         return lvt_6_1_;
      }
   }

   private static int clearAllEffects(CommandSource p_198354_0_, Collection<? extends Entity> p_198354_1_) throws CommandSyntaxException {
      int lvt_2_1_ = 0;
      Iterator var3 = p_198354_1_.iterator();

      while(var3.hasNext()) {
         Entity lvt_4_1_ = (Entity)var3.next();
         if (lvt_4_1_ instanceof LivingEntity && ((LivingEntity)lvt_4_1_).clearActivePotions()) {
            ++lvt_2_1_;
         }
      }

      if (lvt_2_1_ == 0) {
         throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
      } else {
         if (p_198354_1_.size() == 1) {
            p_198354_0_.sendFeedback(new TranslationTextComponent("commands.effect.clear.everything.success.single", new Object[]{((Entity)p_198354_1_.iterator().next()).getDisplayName()}), true);
         } else {
            p_198354_0_.sendFeedback(new TranslationTextComponent("commands.effect.clear.everything.success.multiple", new Object[]{p_198354_1_.size()}), true);
         }

         return lvt_2_1_;
      }
   }

   private static int clearEffect(CommandSource p_198355_0_, Collection<? extends Entity> p_198355_1_, Effect p_198355_2_) throws CommandSyntaxException {
      int lvt_3_1_ = 0;
      Iterator var4 = p_198355_1_.iterator();

      while(var4.hasNext()) {
         Entity lvt_5_1_ = (Entity)var4.next();
         if (lvt_5_1_ instanceof LivingEntity && ((LivingEntity)lvt_5_1_).removePotionEffect(p_198355_2_)) {
            ++lvt_3_1_;
         }
      }

      if (lvt_3_1_ == 0) {
         throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
      } else {
         if (p_198355_1_.size() == 1) {
            p_198355_0_.sendFeedback(new TranslationTextComponent("commands.effect.clear.specific.success.single", new Object[]{p_198355_2_.getDisplayName(), ((Entity)p_198355_1_.iterator().next()).getDisplayName()}), true);
         } else {
            p_198355_0_.sendFeedback(new TranslationTextComponent("commands.effect.clear.specific.success.multiple", new Object[]{p_198355_2_.getDisplayName(), p_198355_1_.size()}), true);
         }

         return lvt_3_1_;
      }
   }
}
