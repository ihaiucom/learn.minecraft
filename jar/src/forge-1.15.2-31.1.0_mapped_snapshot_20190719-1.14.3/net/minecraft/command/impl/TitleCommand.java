package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class TitleCommand {
   public static void register(CommandDispatcher<CommandSource> p_198839_0_) {
      p_198839_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("title").requires((p_198847_0_) -> {
         return p_198847_0_.hasPermissionLevel(2);
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("clear").executes((p_198838_0_) -> {
         return clear((CommandSource)p_198838_0_.getSource(), EntityArgument.getPlayers(p_198838_0_, "targets"));
      }))).then(Commands.literal("reset").executes((p_198841_0_) -> {
         return reset((CommandSource)p_198841_0_.getSource(), EntityArgument.getPlayers(p_198841_0_, "targets"));
      }))).then(Commands.literal("title").then(Commands.argument("title", ComponentArgument.component()).executes((p_198837_0_) -> {
         return show((CommandSource)p_198837_0_.getSource(), EntityArgument.getPlayers(p_198837_0_, "targets"), ComponentArgument.getComponent(p_198837_0_, "title"), STitlePacket.Type.TITLE);
      })))).then(Commands.literal("subtitle").then(Commands.argument("title", ComponentArgument.component()).executes((p_198842_0_) -> {
         return show((CommandSource)p_198842_0_.getSource(), EntityArgument.getPlayers(p_198842_0_, "targets"), ComponentArgument.getComponent(p_198842_0_, "title"), STitlePacket.Type.SUBTITLE);
      })))).then(Commands.literal("actionbar").then(Commands.argument("title", ComponentArgument.component()).executes((p_198836_0_) -> {
         return show((CommandSource)p_198836_0_.getSource(), EntityArgument.getPlayers(p_198836_0_, "targets"), ComponentArgument.getComponent(p_198836_0_, "title"), STitlePacket.Type.ACTIONBAR);
      })))).then(Commands.literal("times").then(Commands.argument("fadeIn", IntegerArgumentType.integer(0)).then(Commands.argument("stay", IntegerArgumentType.integer(0)).then(Commands.argument("fadeOut", IntegerArgumentType.integer(0)).executes((p_198843_0_) -> {
         return setTimes((CommandSource)p_198843_0_.getSource(), EntityArgument.getPlayers(p_198843_0_, "targets"), IntegerArgumentType.getInteger(p_198843_0_, "fadeIn"), IntegerArgumentType.getInteger(p_198843_0_, "stay"), IntegerArgumentType.getInteger(p_198843_0_, "fadeOut"));
      })))))));
   }

   private static int clear(CommandSource p_198840_0_, Collection<ServerPlayerEntity> p_198840_1_) {
      STitlePacket lvt_2_1_ = new STitlePacket(STitlePacket.Type.CLEAR, (ITextComponent)null);
      Iterator var3 = p_198840_1_.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity lvt_4_1_ = (ServerPlayerEntity)var3.next();
         lvt_4_1_.connection.sendPacket(lvt_2_1_);
      }

      if (p_198840_1_.size() == 1) {
         p_198840_0_.sendFeedback(new TranslationTextComponent("commands.title.cleared.single", new Object[]{((ServerPlayerEntity)p_198840_1_.iterator().next()).getDisplayName()}), true);
      } else {
         p_198840_0_.sendFeedback(new TranslationTextComponent("commands.title.cleared.multiple", new Object[]{p_198840_1_.size()}), true);
      }

      return p_198840_1_.size();
   }

   private static int reset(CommandSource p_198844_0_, Collection<ServerPlayerEntity> p_198844_1_) {
      STitlePacket lvt_2_1_ = new STitlePacket(STitlePacket.Type.RESET, (ITextComponent)null);
      Iterator var3 = p_198844_1_.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity lvt_4_1_ = (ServerPlayerEntity)var3.next();
         lvt_4_1_.connection.sendPacket(lvt_2_1_);
      }

      if (p_198844_1_.size() == 1) {
         p_198844_0_.sendFeedback(new TranslationTextComponent("commands.title.reset.single", new Object[]{((ServerPlayerEntity)p_198844_1_.iterator().next()).getDisplayName()}), true);
      } else {
         p_198844_0_.sendFeedback(new TranslationTextComponent("commands.title.reset.multiple", new Object[]{p_198844_1_.size()}), true);
      }

      return p_198844_1_.size();
   }

   private static int show(CommandSource p_198846_0_, Collection<ServerPlayerEntity> p_198846_1_, ITextComponent p_198846_2_, STitlePacket.Type p_198846_3_) throws CommandSyntaxException {
      Iterator var4 = p_198846_1_.iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity lvt_5_1_ = (ServerPlayerEntity)var4.next();
         lvt_5_1_.connection.sendPacket(new STitlePacket(p_198846_3_, TextComponentUtils.updateForEntity(p_198846_0_, p_198846_2_, lvt_5_1_, 0)));
      }

      if (p_198846_1_.size() == 1) {
         p_198846_0_.sendFeedback(new TranslationTextComponent("commands.title.show." + p_198846_3_.name().toLowerCase(Locale.ROOT) + ".single", new Object[]{((ServerPlayerEntity)p_198846_1_.iterator().next()).getDisplayName()}), true);
      } else {
         p_198846_0_.sendFeedback(new TranslationTextComponent("commands.title.show." + p_198846_3_.name().toLowerCase(Locale.ROOT) + ".multiple", new Object[]{p_198846_1_.size()}), true);
      }

      return p_198846_1_.size();
   }

   private static int setTimes(CommandSource p_198845_0_, Collection<ServerPlayerEntity> p_198845_1_, int p_198845_2_, int p_198845_3_, int p_198845_4_) {
      STitlePacket lvt_5_1_ = new STitlePacket(p_198845_2_, p_198845_3_, p_198845_4_);
      Iterator var6 = p_198845_1_.iterator();

      while(var6.hasNext()) {
         ServerPlayerEntity lvt_7_1_ = (ServerPlayerEntity)var6.next();
         lvt_7_1_.connection.sendPacket(lvt_5_1_);
      }

      if (p_198845_1_.size() == 1) {
         p_198845_0_.sendFeedback(new TranslationTextComponent("commands.title.times.single", new Object[]{((ServerPlayerEntity)p_198845_1_.iterator().next()).getDisplayName()}), true);
      } else {
         p_198845_0_.sendFeedback(new TranslationTextComponent("commands.title.times.multiple", new Object[]{p_198845_1_.size()}), true);
      }

      return p_198845_1_.size();
   }
}
