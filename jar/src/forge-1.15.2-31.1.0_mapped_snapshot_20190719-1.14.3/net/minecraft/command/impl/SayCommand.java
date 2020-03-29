package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SayCommand {
   public static void register(CommandDispatcher<CommandSource> p_198625_0_) {
      p_198625_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("say").requires((p_198627_0_) -> {
         return p_198627_0_.hasPermissionLevel(2);
      })).then(Commands.argument("message", MessageArgument.message()).executes((p_198626_0_) -> {
         ITextComponent lvt_1_1_ = MessageArgument.getMessage(p_198626_0_, "message");
         ((CommandSource)p_198626_0_.getSource()).getServer().getPlayerList().sendMessage(new TranslationTextComponent("chat.type.announcement", new Object[]{((CommandSource)p_198626_0_.getSource()).getDisplayName(), lvt_1_1_}));
         return 1;
      })));
   }
}
