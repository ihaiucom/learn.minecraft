package net.minecraftforge.fml.client;

import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class ConfigGuiHandler {
   public static Optional<BiFunction<Minecraft, Screen, Screen>> getGuiFactoryFor(ModInfo selectedMod) {
      return ModList.get().getModContainerById(selectedMod.getModId()).flatMap((mc) -> {
         return mc.getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY);
      });
   }
}
