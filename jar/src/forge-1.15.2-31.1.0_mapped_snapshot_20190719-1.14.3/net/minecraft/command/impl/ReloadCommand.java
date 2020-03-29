package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class ReloadCommand {
   public static void register(CommandDispatcher<CommandSource> p_198597_0_) {
      p_198597_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload").requires((p_198599_0_) -> {
         return p_198599_0_.hasPermissionLevel(2);
      })).executes((p_198598_0_) -> {
         ((CommandSource)p_198598_0_.getSource()).sendFeedback(new TranslationTextComponent("commands.reload.success", new Object[0]), true);
         ((CommandSource)p_198598_0_.getSource()).getServer().reload();
         return 0;
      }));
   }
}
