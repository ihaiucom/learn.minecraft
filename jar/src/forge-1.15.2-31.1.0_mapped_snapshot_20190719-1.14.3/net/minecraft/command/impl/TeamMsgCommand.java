package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class TeamMsgCommand {
   private static final SimpleCommandExceptionType field_218919_a = new SimpleCommandExceptionType(new TranslationTextComponent("commands.teammsg.failed.noteam", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_218915_0_) {
      LiteralCommandNode<CommandSource> lvt_1_1_ = p_218915_0_.register((LiteralArgumentBuilder)Commands.literal("teammsg").then(Commands.argument("message", MessageArgument.message()).executes((p_218916_0_) -> {
         return func_218917_a((CommandSource)p_218916_0_.getSource(), MessageArgument.getMessage(p_218916_0_, "message"));
      })));
      p_218915_0_.register((LiteralArgumentBuilder)Commands.literal("tm").redirect(lvt_1_1_));
   }

   private static int func_218917_a(CommandSource p_218917_0_, ITextComponent p_218917_1_) throws CommandSyntaxException {
      Entity lvt_2_1_ = p_218917_0_.assertIsEntity();
      ScorePlayerTeam lvt_3_1_ = (ScorePlayerTeam)lvt_2_1_.getTeam();
      if (lvt_3_1_ == null) {
         throw field_218919_a.create();
      } else {
         Consumer<Style> lvt_4_1_ = (p_218918_0_) -> {
            p_218918_0_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.type.team.hover", new Object[0]))).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
         };
         ITextComponent lvt_5_1_ = lvt_3_1_.getCommandName().applyTextStyle(lvt_4_1_);
         Iterator var6 = lvt_5_1_.getSiblings().iterator();

         while(var6.hasNext()) {
            ITextComponent lvt_7_1_ = (ITextComponent)var6.next();
            lvt_7_1_.applyTextStyle(lvt_4_1_);
         }

         List<ServerPlayerEntity> lvt_6_1_ = p_218917_0_.getServer().getPlayerList().getPlayers();
         Iterator var10 = lvt_6_1_.iterator();

         while(var10.hasNext()) {
            ServerPlayerEntity lvt_8_1_ = (ServerPlayerEntity)var10.next();
            if (lvt_8_1_ == lvt_2_1_) {
               lvt_8_1_.sendMessage(new TranslationTextComponent("chat.type.team.sent", new Object[]{lvt_5_1_, p_218917_0_.getDisplayName(), p_218917_1_.deepCopy()}));
            } else if (lvt_8_1_.getTeam() == lvt_3_1_) {
               lvt_8_1_.sendMessage(new TranslationTextComponent("chat.type.team.text", new Object[]{lvt_5_1_, p_218917_0_.getDisplayName(), p_218917_1_.deepCopy()}));
            }
         }

         return lvt_6_1_.size();
      }
   }
}
