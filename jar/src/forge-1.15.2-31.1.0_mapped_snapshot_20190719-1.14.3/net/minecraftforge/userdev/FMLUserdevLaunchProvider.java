package net.minecraftforge.userdev;

import cpw.mods.modlauncher.api.IEnvironment;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.fml.loading.LogMarkers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class FMLUserdevLaunchProvider extends FMLCommonLaunchHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private Path forgeJar;
   private Path mcJars;
   private static final String FORGE_VERSION_CLASS = "net/minecraftforge/versions/forge/ForgeVersion.class";

   public Path getForgePath(String mcVersion, String forgeVersion, String forgeGroup) {
      URL forgePath = this.getClass().getClassLoader().getResource("net/minecraftforge/versions/forge/ForgeVersion.class");
      if (forgePath == null) {
         LOGGER.fatal(LogMarkers.CORE, "Unable to locate forge on the classpath");
         throw new RuntimeException("Unable to locate forge on the classpath");
      } else {
         this.forgeJar = LibraryFinder.findJarPathFor("net/minecraftforge/versions/forge/ForgeVersion.class", "forge", forgePath);
         return this.forgeJar;
      }
   }

   public void setup(IEnvironment environment, Map<String, ?> arguments) {
      if (!this.forgeJar.getFileName().toString().endsWith(".jar")) {
         LOGGER.fatal(LogMarkers.CORE, "Userdev Launcher attempted to be used with non-jar version of Forge: {}", this.forgeJar);
         throw new RuntimeException("Userdev Launcher can only be used with dev-jar version of Forge");
      } else {
         List<String> mavenRoots = new ArrayList((List)arguments.get("mavenRoots"));
         String forgeGroup = (String)arguments.get("forgeGroup");
         int dirs = forgeGroup.split("\\.").length + 2;
         Path fjroot = this.forgeJar;

         do {
            fjroot = fjroot.getParent();
         } while(dirs-- > 0);

         String fjpath = fjroot.toString();
         mavenRoots.add(fjpath);
         LOGGER.debug(LogMarkers.CORE, "Injecting maven path {}", fjpath);
         this.processModClassesEnvironmentVariable(arguments);
         arguments.put("mavenRoots", mavenRoots);
      }
   }

   protected void validatePaths(Path forgePath, Path[] mcPaths, String forgeVersion, String mcVersion, String mcpVersion) {
   }

   public Path[] getMCPaths(String mcVersion, String mcpVersion, String forgeVersion, String forgeGroup) {
      URL mcDataPath = this.getClass().getClassLoader().getResource("assets/minecraft/lang/en_us.json");
      if (mcDataPath == null) {
         LOGGER.fatal(LogMarkers.CORE, "Unable to locate minecraft data on the classpath");
         throw new RuntimeException("Unable to locate minecraft data on the classpath");
      } else {
         this.mcJars = LibraryFinder.findJarPathFor("en_us.json", "mcdata", mcDataPath);
         return new Path[]{this.mcJars};
      }
   }

   protected String getNaming() {
      return "mcp";
   }
}
