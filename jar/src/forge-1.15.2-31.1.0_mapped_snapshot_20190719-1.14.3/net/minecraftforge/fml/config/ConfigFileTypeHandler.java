package net.minecraftforge.fml.config;

import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Function;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigFileTypeHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   static ConfigFileTypeHandler TOML = new ConfigFileTypeHandler();
   private static final Path defaultConfigPath;

   public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
      return (c) -> {
         Path configPath = configBasePath.resolve(c.getFileName());
         CommentedFileConfig configData = (CommentedFileConfig)CommentedFileConfig.builder(configPath).sync().preserveInsertionOrder().autosave().onFileNotFound((newfile, configFormat) -> {
            return this.setupConfigFile(c, newfile, configFormat);
         }).writingMode(WritingMode.REPLACE).build();
         LOGGER.debug(ConfigTracker.CONFIG, "Built TOML config for {}", configPath.toString());
         configData.load();
         LOGGER.debug(ConfigTracker.CONFIG, "Loaded TOML config file {}", configPath.toString());

         try {
            FileWatcher.defaultInstance().addWatch(configPath, new ConfigFileTypeHandler.ConfigWatcher(c, configData, Thread.currentThread().getContextClassLoader()));
            LOGGER.debug(ConfigTracker.CONFIG, "Watching TOML config file {} for changes", configPath.toString());
            return configData;
         } catch (IOException var6) {
            throw new RuntimeException("Couldn't watch config file", var6);
         }
      };
   }

   private boolean setupConfigFile(ModConfig modConfig, Path file, ConfigFormat<?> conf) throws IOException {
      Path p = defaultConfigPath.resolve(modConfig.getFileName());
      if (Files.exists(p, new LinkOption[0])) {
         LOGGER.info(ConfigTracker.CONFIG, "Loading default config file from path {}", p);
         Files.copy(p, file);
      } else {
         Files.createFile(file);
         conf.initEmptyFile(file);
      }

      return true;
   }

   static {
      defaultConfigPath = FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());
   }

   private static class ConfigWatcher implements Runnable {
      private final ModConfig modConfig;
      private final CommentedFileConfig commentedFileConfig;
      private final ClassLoader realClassLoader;

      ConfigWatcher(ModConfig modConfig, CommentedFileConfig commentedFileConfig, ClassLoader classLoader) {
         this.modConfig = modConfig;
         this.commentedFileConfig = commentedFileConfig;
         this.realClassLoader = classLoader;
      }

      public void run() {
         Thread.currentThread().setContextClassLoader(this.realClassLoader);
         if (!this.modConfig.getSpec().isCorrecting()) {
            this.commentedFileConfig.load();
            ConfigFileTypeHandler.LOGGER.debug(ConfigTracker.CONFIG, "Config file {} changed, sending notifies", this.modConfig.getFileName());
            this.modConfig.fireEvent(new ModConfig.Reloading(this.modConfig));
         }

      }
   }
}
