package net.minecraftforge.common;

import net.minecraft.crash.CrashReport;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MinecraftForge {
   public static final IEventBus EVENT_BUS = BusBuilder.builder().startShutdown().build();
   static final ForgeInternalHandler INTERNAL_HANDLER = new ForgeInternalHandler();
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker FORGE = MarkerManager.getMarker("FORGE");

   public static void initialize() {
      LOGGER.info(FORGE, "MinecraftForge v{} Initialized", ForgeVersion.getVersion());
      UsernameCache.load();
      ForgeHooks.initTools();
      new CrashReport("ThisIsFake", new Exception("Not real"));
   }
}
