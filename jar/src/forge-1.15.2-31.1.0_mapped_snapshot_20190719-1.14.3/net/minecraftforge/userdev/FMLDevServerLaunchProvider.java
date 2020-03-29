package net.minecraftforge.userdev;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ILaunchHandlerService;
import cpw.mods.modlauncher.api.ITransformingClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.fml.loading.LogMarkers;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FMLDevServerLaunchProvider extends FMLCommonLaunchHandler implements ILaunchHandlerService {
   private static final Logger LOGGER = LogManager.getLogger();
   private Path compiledClasses;
   private Path resources;

   public String name() {
      return "fmldevserver";
   }

   public Path getForgePath(String mcVersion, String forgeVersion, String forgeGroup) {
      this.compiledClasses = LibraryFinder.findJarPathFor("net/minecraftforge/versions/forge/ForgeVersion.class", "forge");
      this.resources = LibraryFinder.findJarPathFor("assets/minecraft/lang/en_us.json", "mcassets");
      return this.compiledClasses;
   }

   public Path[] getMCPaths(String mcVersion, String mcpVersion, String forgeVersion, String forgeGroup) {
      return new Path[]{this.compiledClasses, this.resources};
   }

   public Callable<Void> launchService(String[] arguments, ITransformingClassLoader launchClassLoader) {
      return () -> {
         LOGGER.debug(LogMarkers.CORE, "Launching minecraft in {} with arguments {}", launchClassLoader, arguments);
         super.beforeStart(launchClassLoader);
         launchClassLoader.addTargetPackageFilter(this.getPackagePredicate());
         Thread.currentThread().setContextClassLoader(launchClassLoader.getInstance());
         Class.forName("net.minecraft.server.MinecraftServer", true, launchClassLoader.getInstance()).getMethod("main", String[].class).invoke((Object)null, arguments);
         return null;
      };
   }

   public void setup(IEnvironment environment, Map<String, ?> arguments) {
      Path forgemodstoml = LibraryFinder.findJarPathFor("META-INF/mods.toml", "forgemodstoml");
      ((List)arguments.computeIfAbsent("explodedTargets", (a) -> {
         return new ArrayList();
      })).add(Pair.of(forgemodstoml, Collections.singletonList(this.compiledClasses)));
      this.processModClassesEnvironmentVariable(arguments);
   }

   public Dist getDist() {
      return Dist.DEDICATED_SERVER;
   }

   protected String getNaming() {
      return "mcp";
   }
}
