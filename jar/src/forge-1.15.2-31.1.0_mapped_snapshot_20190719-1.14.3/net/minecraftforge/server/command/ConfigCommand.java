package net.minecraftforge.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.io.File;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("config").then(ConfigCommand.ShowFile.register()));
   }

   public static class ShowFile {
      static ArgumentBuilder<CommandSource, ?> register() {
         return ((LiteralArgumentBuilder)Commands.literal("showfile").requires((cs) -> {
            return cs.hasPermissionLevel(0);
         })).then(Commands.argument("mod", ModIdArgument.modIdArgument()).then(Commands.argument("type", EnumArgument.enumArgument(ModConfig.Type.class)).executes(ConfigCommand.ShowFile::showFile)));
      }

      private static int showFile(CommandContext<CommandSource> context) {
         String modId = (String)context.getArgument("mod", String.class);
         ModConfig.Type type = (ModConfig.Type)context.getArgument("type", ModConfig.Type.class);
         String configFileName = ConfigTracker.INSTANCE.getConfigFileName(modId, type);
         if (configFileName != null) {
            File f = new File(configFileName);
            ((CommandSource)context.getSource()).sendFeedback(new TranslationTextComponent("commands.config.getwithtype", new Object[]{modId, type, (new StringTextComponent(f.getName())).applyTextStyle(TextFormatting.UNDERLINE).applyTextStyle((style) -> {
               style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getAbsolutePath()));
            })}), true);
         } else {
            ((CommandSource)context.getSource()).sendFeedback(new TranslationTextComponent("commands.config.noconfig", new Object[]{modId, type}), true);
         }

         return 0;
      }
   }
}
