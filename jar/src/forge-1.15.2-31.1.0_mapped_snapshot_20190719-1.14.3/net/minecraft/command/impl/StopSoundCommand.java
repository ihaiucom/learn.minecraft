package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;

public class StopSoundCommand {
   public static void register(CommandDispatcher<CommandSource> p_198730_0_) {
      RequiredArgumentBuilder<CommandSource, EntitySelector> lvt_1_1_ = (RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((p_198729_0_) -> {
         return stopSound((CommandSource)p_198729_0_.getSource(), EntityArgument.getPlayers(p_198729_0_, "targets"), (SoundCategory)null, (ResourceLocation)null);
      })).then(Commands.literal("*").then(Commands.argument("sound", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((p_198732_0_) -> {
         return stopSound((CommandSource)p_198732_0_.getSource(), EntityArgument.getPlayers(p_198732_0_, "targets"), (SoundCategory)null, ResourceLocationArgument.getResourceLocation(p_198732_0_, "sound"));
      })));
      SoundCategory[] var2 = SoundCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundCategory lvt_5_1_ = var2[var4];
         lvt_1_1_.then(((LiteralArgumentBuilder)Commands.literal(lvt_5_1_.getName()).executes((p_198731_1_) -> {
            return stopSound((CommandSource)p_198731_1_.getSource(), EntityArgument.getPlayers(p_198731_1_, "targets"), lvt_5_1_, (ResourceLocation)null);
         })).then(Commands.argument("sound", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((p_198728_1_) -> {
            return stopSound((CommandSource)p_198728_1_.getSource(), EntityArgument.getPlayers(p_198728_1_, "targets"), lvt_5_1_, ResourceLocationArgument.getResourceLocation(p_198728_1_, "sound"));
         })));
      }

      p_198730_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stopsound").requires((p_198734_0_) -> {
         return p_198734_0_.hasPermissionLevel(2);
      })).then(lvt_1_1_));
   }

   private static int stopSound(CommandSource p_198733_0_, Collection<ServerPlayerEntity> p_198733_1_, @Nullable SoundCategory p_198733_2_, @Nullable ResourceLocation p_198733_3_) {
      SStopSoundPacket lvt_4_1_ = new SStopSoundPacket(p_198733_3_, p_198733_2_);
      Iterator var5 = p_198733_1_.iterator();

      while(var5.hasNext()) {
         ServerPlayerEntity lvt_6_1_ = (ServerPlayerEntity)var5.next();
         lvt_6_1_.connection.sendPacket(lvt_4_1_);
      }

      if (p_198733_2_ != null) {
         if (p_198733_3_ != null) {
            p_198733_0_.sendFeedback(new TranslationTextComponent("commands.stopsound.success.source.sound", new Object[]{p_198733_3_, p_198733_2_.getName()}), true);
         } else {
            p_198733_0_.sendFeedback(new TranslationTextComponent("commands.stopsound.success.source.any", new Object[]{p_198733_2_.getName()}), true);
         }
      } else if (p_198733_3_ != null) {
         p_198733_0_.sendFeedback(new TranslationTextComponent("commands.stopsound.success.sourceless.sound", new Object[]{p_198733_3_}), true);
      } else {
         p_198733_0_.sendFeedback(new TranslationTextComponent("commands.stopsound.success.sourceless.any", new Object[0]), true);
      }

      return p_198733_1_.size();
   }
}
