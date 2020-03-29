package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class MeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198364_0_) {
      p_198364_0_.register((LiteralArgumentBuilder)Commands.literal("me").then(Commands.argument("action", StringArgumentType.greedyString()).executes((p_198365_0_) -> {
         ((CommandSource)p_198365_0_.getSource()).getServer().getPlayerList().sendMessage(new TranslationTextComponent("chat.type.emote", new Object[]{((CommandSource)p_198365_0_.getSource()).getDisplayName(), StringArgumentType.getString(p_198365_0_, "action")}));
         return 1;
      })));
   }
}
