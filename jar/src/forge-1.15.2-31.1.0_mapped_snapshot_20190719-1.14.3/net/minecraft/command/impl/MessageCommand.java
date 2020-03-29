package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class MessageCommand {
   public static void register(CommandDispatcher<CommandSource> p_198537_0_) {
      LiteralCommandNode<CommandSource> lvt_1_1_ = p_198537_0_.register((LiteralArgumentBuilder)Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((p_198539_0_) -> {
         return sendPrivateMessage((CommandSource)p_198539_0_.getSource(), EntityArgument.getPlayers(p_198539_0_, "targets"), MessageArgument.getMessage(p_198539_0_, "message"));
      }))));
      p_198537_0_.register((LiteralArgumentBuilder)Commands.literal("tell").redirect(lvt_1_1_));
      p_198537_0_.register((LiteralArgumentBuilder)Commands.literal("w").redirect(lvt_1_1_));
   }

   private static int sendPrivateMessage(CommandSource p_198538_0_, Collection<ServerPlayerEntity> p_198538_1_, ITextComponent p_198538_2_) {
      Iterator var3 = p_198538_1_.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity lvt_4_1_ = (ServerPlayerEntity)var3.next();
         lvt_4_1_.sendMessage((new TranslationTextComponent("commands.message.display.incoming", new Object[]{p_198538_0_.getDisplayName(), p_198538_2_.deepCopy()})).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
         p_198538_0_.sendFeedback((new TranslationTextComponent("commands.message.display.outgoing", new Object[]{lvt_4_1_.getDisplayName(), p_198538_2_.deepCopy()})).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), false);
      }

      return p_198538_1_.size();
   }
}
