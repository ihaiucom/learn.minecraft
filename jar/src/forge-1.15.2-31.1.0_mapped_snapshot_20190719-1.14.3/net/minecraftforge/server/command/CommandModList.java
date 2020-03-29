package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

public class CommandModList {
   static ArgumentBuilder<CommandSource, ?> register() {
      return ((LiteralArgumentBuilder)Commands.literal("mods").requires((cs) -> {
         return cs.hasPermissionLevel(0);
      })).executes((ctx) -> {
         ((CommandSource)ctx.getSource()).sendFeedback(new TranslationTextComponent("commands.forge.mods.list", new Object[]{ModList.get().applyForEachModFile((modFile) -> {
            return String.format("%s %s : %s (%s) - %d", modFile.getLocator().name().replace(' ', '_'), modFile.getFileName(), ((IModInfo)modFile.getModInfos().get(0)).getModId(), ((IModInfo)modFile.getModInfos().get(0)).getVersion(), modFile.getModInfos().size());
         }).collect(Collectors.joining("\n• ", "• ", ""))}), true);
         return 0;
      });
   }
}
