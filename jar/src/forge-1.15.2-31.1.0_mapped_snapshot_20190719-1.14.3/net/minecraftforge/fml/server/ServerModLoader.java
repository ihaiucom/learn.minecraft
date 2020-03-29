package net.minecraftforge.fml.server;

import java.util.List;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.SidedProvider;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.network.FMLStatusPing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerModLoader {
   private static final Logger LOGGER = LogManager.getLogger();
   private static DedicatedServer server;
   private static boolean hasErrors = false;

   public static void begin(DedicatedServer dedicatedServer) {
      server = dedicatedServer;
      SidedProvider.setServer(() -> {
         return dedicatedServer;
      });
      LogicalSidedProvider.setServer(() -> {
         return dedicatedServer;
      });
      LanguageHook.loadForgeAndMCLangs();

      try {
         ModLoader.get().gatherAndInitializeMods((Runnable)null);
         ModLoader.get().loadMods(Runnable::run, (a) -> {
         }, (a) -> {
         });
      } catch (LoadingFailedException var2) {
         hasErrors = true;
         throw var2;
      }
   }

   public static void end() {
      try {
         ModLoader.get().finishMods(Runnable::run);
      } catch (LoadingFailedException var1) {
         hasErrors = true;
         throw var1;
      }

      List<ModLoadingWarning> warnings = ModLoader.get().getWarnings();
      if (!warnings.isEmpty()) {
         LOGGER.warn(LogMarkers.LOADING, "Mods loaded with {} warnings", warnings.size());
         warnings.forEach((warning) -> {
            LOGGER.warn(LogMarkers.LOADING, warning.formatToString());
         });
      }

      MinecraftForge.EVENT_BUS.start();
      server.getServerStatusResponse().setForgeData(new FMLStatusPing());
   }

   public static boolean hasErrors() {
      return hasErrors;
   }
}
