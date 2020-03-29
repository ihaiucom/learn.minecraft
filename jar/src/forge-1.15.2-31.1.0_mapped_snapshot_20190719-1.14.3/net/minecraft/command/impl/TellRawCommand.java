package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextComponentUtils;

public class TellRawCommand {
   public static void register(CommandDispatcher<CommandSource> p_198818_0_) {
      p_198818_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tellraw").requires((p_198820_0_) -> {
         return p_198820_0_.hasPermissionLevel(2);
      })).then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", ComponentArgument.component()).executes((p_198819_0_) -> {
         int lvt_1_1_ = 0;

         for(Iterator var2 = EntityArgument.getPlayers(p_198819_0_, "targets").iterator(); var2.hasNext(); ++lvt_1_1_) {
            ServerPlayerEntity lvt_3_1_ = (ServerPlayerEntity)var2.next();
            lvt_3_1_.sendMessage(TextComponentUtils.updateForEntity((CommandSource)p_198819_0_.getSource(), ComponentArgument.getComponent(p_198819_0_, "message"), lvt_3_1_, 0));
         }

         return lvt_1_1_;
      }))));
   }
}
