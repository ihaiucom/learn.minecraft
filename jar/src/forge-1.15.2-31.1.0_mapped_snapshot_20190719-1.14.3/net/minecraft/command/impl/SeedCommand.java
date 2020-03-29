package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class SeedCommand {
   public static void register(CommandDispatcher<CommandSource> p_198671_0_) {
      p_198671_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("seed").requires((p_198673_0_) -> {
         return p_198673_0_.getServer().isSinglePlayer() || p_198673_0_.hasPermissionLevel(2);
      })).executes((p_198672_0_) -> {
         long lvt_1_1_ = ((CommandSource)p_198672_0_.getSource()).getWorld().getSeed();
         ITextComponent lvt_3_1_ = TextComponentUtils.wrapInSquareBrackets((new StringTextComponent(String.valueOf(lvt_1_1_))).applyTextStyle((p_211752_2_) -> {
            p_211752_2_.setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(lvt_1_1_))).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.copy.click", new Object[0]))).setInsertion(String.valueOf(lvt_1_1_));
         }));
         ((CommandSource)p_198672_0_.getSource()).sendFeedback(new TranslationTextComponent("commands.seed.success", new Object[]{lvt_3_1_}), false);
         return (int)lvt_1_1_;
      }));
   }
}
