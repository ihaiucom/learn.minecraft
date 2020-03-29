package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;

public class SaveAllCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.save.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198611_0_) {
      p_198611_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-all").requires((p_198615_0_) -> {
         return p_198615_0_.hasPermissionLevel(4);
      })).executes((p_198610_0_) -> {
         return saveAll((CommandSource)p_198610_0_.getSource(), false);
      })).then(Commands.literal("flush").executes((p_198613_0_) -> {
         return saveAll((CommandSource)p_198613_0_.getSource(), true);
      })));
   }

   private static int saveAll(CommandSource p_198614_0_, boolean p_198614_1_) throws CommandSyntaxException {
      p_198614_0_.sendFeedback(new TranslationTextComponent("commands.save.saving", new Object[0]), false);
      MinecraftServer lvt_2_1_ = p_198614_0_.getServer();
      lvt_2_1_.getPlayerList().saveAllPlayerData();
      boolean lvt_3_1_ = lvt_2_1_.save(true, p_198614_1_, true);
      if (!lvt_3_1_) {
         throw FAILED_EXCEPTION.create();
      } else {
         p_198614_0_.sendFeedback(new TranslationTextComponent("commands.save.success", new Object[0]), true);
         return 1;
      }
   }
}
