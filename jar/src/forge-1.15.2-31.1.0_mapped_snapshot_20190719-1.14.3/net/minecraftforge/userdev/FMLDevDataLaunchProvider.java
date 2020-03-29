package net.minecraftforge.userdev;

import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import java.util.concurrent.Callable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.LogMarkers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FMLDevDataLaunchProvider extends FMLDevServerLaunchProvider implements ILaunchHandlerService {
   private static final Logger LOGGER = LogManager.getLogger();

   public String name() {
      return "fmldevdata";
   }

   public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader) {
      return () -> {
         LOGGER.debug(LogMarkers.CORE, "Launching minecraft in {} with arguments {}", launchClassLoader, arguments);
         super.beforeStart(launchClassLoader);
         launchClassLoader.addTargetPackageFilter(this.getPackagePredicate());
         Thread.currentThread().setContextClassLoader(launchClassLoader.getInstance());
         Class.forName("net.minecraft.data.Main", true, launchClassLoader.getInstance()).getMethod("main", String[].class).invoke((Object)null, arguments);
         return null;
      };
   }

   public Dist getDist() {
      return Dist.CLIENT;
   }

   protected String getNaming() {
      return "mcp";
   }
}
