package net.minecraft.command.impl;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class HelpCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.help.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198510_0_) {
      p_198510_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("help").executes((p_198511_1_) -> {
         Map<CommandNode<CommandSource>, String> lvt_2_1_ = p_198510_0_.getSmartUsage(p_198510_0_.getRoot(), p_198511_1_.getSource());
         Iterator var3 = lvt_2_1_.values().iterator();

         while(var3.hasNext()) {
            String lvt_4_1_ = (String)var3.next();
            ((CommandSource)p_198511_1_.getSource()).sendFeedback(new StringTextComponent("/" + lvt_4_1_), false);
         }

         return lvt_2_1_.size();
      })).then(Commands.argument("command", StringArgumentType.greedyString()).executes((p_198512_1_) -> {
         ParseResults<CommandSource> lvt_2_1_ = p_198510_0_.parse(StringArgumentType.getString(p_198512_1_, "command"), p_198512_1_.getSource());
         if (lvt_2_1_.getContext().getNodes().isEmpty()) {
            throw FAILED_EXCEPTION.create();
         } else {
            Map<CommandNode<CommandSource>, String> lvt_3_1_ = p_198510_0_.getSmartUsage(((ParsedCommandNode)Iterables.getLast(lvt_2_1_.getContext().getNodes())).getNode(), p_198512_1_.getSource());
            Iterator var4 = lvt_3_1_.values().iterator();

            while(var4.hasNext()) {
               String lvt_5_1_ = (String)var4.next();
               ((CommandSource)p_198512_1_.getSource()).sendFeedback(new StringTextComponent("/" + lvt_2_1_.getReader().getString() + " " + lvt_5_1_), false);
            }

            return lvt_3_1_.size();
         }
      })));
   }
}
