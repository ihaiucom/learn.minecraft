package net.minecraftforge.userdev;

import com.google.common.base.Predicates;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClasspathLocator extends AbstractJarFileLocator {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final String MODS_TOML = "META-INF/mods.toml";
   private static final String MANIFEST = "META-INF/MANIFEST.MF";
   private Set<Path> modCoords;

   public List<IModFile> scanMods() {
      return (List)this.modCoords.stream().map((mc) -> {
         return new ModFile(mc, this);
      }).peek((f) -> {
         FileSystem var10000 = (FileSystem)this.modJars.compute(f, (mf, fs) -> {
            return this.createFileSystem(mf);
         });
      }).collect(Collectors.toList());
   }

   public String name() {
      return "userdev classpath";
   }

   public void initArguments(Map<String, ?> arguments) {
      try {
         this.modCoords = new LinkedHashSet();
         this.locateMods("META-INF/mods.toml", "classpath_mod", Predicates.alwaysTrue());
         this.locateMods("META-INF/MANIFEST.MF", "manifest_jar", (path) -> {
            return this.findManifest(path).map((m) -> {
               return m.getMainAttributes().getValue(ModFile.TYPE);
            }).isPresent();
         });
      } catch (IOException var3) {
         LOGGER.fatal(LogMarkers.CORE, "Error trying to find resources", var3);
         throw new RuntimeException("wha?", var3);
      }
   }

   private void locateMods(String resource, String name, Predicate<Path> filter) throws IOException {
      Enumeration modsTomls = ClassLoader.getSystemClassLoader().getResources(resource);

      while(modsTomls.hasMoreElements()) {
         URL url = (URL)modsTomls.nextElement();
         Path path = LibraryFinder.findJarPathFor(resource, name, url);
         if (!Files.isDirectory(path, new LinkOption[0]) && filter.test(path)) {
            LOGGER.debug(LogMarkers.CORE, "Found classpath mod: {}", path);
            this.modCoords.add(path);
         }
      }

   }
}
