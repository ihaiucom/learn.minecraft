package net.minecraftforge.server.command;

import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.ConnectionType;
import net.minecraftforge.fml.network.NetworkHooks;

public class TextComponentHelper {
   private TextComponentHelper() {
   }

   public static TextComponent createComponentTranslation(ICommandSource source, String translation, Object... args) {
      return (TextComponent)(isVanillaClient(source) ? new StringTextComponent(String.format(LanguageMap.getInstance().translateKey(translation), args)) : new TranslationTextComponent(translation, args));
   }

   private static boolean isVanillaClient(ICommandSource sender) {
      if (sender instanceof ServerPlayerEntity) {
         ServerPlayerEntity playerMP = (ServerPlayerEntity)sender;
         ServerPlayNetHandler channel = playerMP.connection;
         return NetworkHooks.getConnectionType(() -> {
            return channel.netManager;
         }) == ConnectionType.VANILLA;
      } else {
         return false;
      }
   }
}
