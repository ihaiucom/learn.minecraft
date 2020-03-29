package net.minecraftforge.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.StringUtils;

public class ModConfig {
   private final ModConfig.Type type;
   private final ForgeConfigSpec spec;
   private final String fileName;
   private final ModContainer container;
   private final ConfigFileTypeHandler configHandler;
   private CommentedConfig configData;
   private Callable<Void> saveHandler;

   public ModConfig(ModConfig.Type type, ForgeConfigSpec spec, ModContainer container, String fileName) {
      this.type = type;
      this.spec = spec;
      this.fileName = fileName;
      this.container = container;
      this.configHandler = ConfigFileTypeHandler.TOML;
      ConfigTracker.INSTANCE.trackConfig(this);
   }

   public ModConfig(ModConfig.Type type, ForgeConfigSpec spec, ModContainer activeContainer) {
      this(type, spec, activeContainer, defaultConfigName(type, activeContainer.getModId()));
   }

   private static String defaultConfigName(ModConfig.Type type, String modId) {
      return String.format("%s-%s.toml", modId, type.extension());
   }

   public ModConfig.Type getType() {
      return this.type;
   }

   public String getFileName() {
      return this.fileName;
   }

   public ConfigFileTypeHandler getHandler() {
      return this.configHandler;
   }

   public ForgeConfigSpec getSpec() {
      return this.spec;
   }

   public String getModId() {
      return this.container.getModId();
   }

   public CommentedConfig getConfigData() {
      return this.configData;
   }

   void setConfigData(CommentedConfig configData) {
      this.configData = configData;
      this.spec.setConfig(this.configData);
   }

   void fireEvent(ModConfig.ModConfigEvent configEvent) {
      this.container.dispatchConfigEvent(configEvent);
   }

   public void save() {
      ((CommentedFileConfig)this.configData).save();
   }

   public Path getFullPath() {
      return ((CommentedFileConfig)this.configData).getNioPath();
   }

   public static class Reloading extends ModConfig.ModConfigEvent {
      Reloading(ModConfig config) {
         super(config);
      }
   }

   public static class Loading extends ModConfig.ModConfigEvent {
      Loading(ModConfig config) {
         super(config);
      }
   }

   public static class ModConfigEvent extends Event {
      private final ModConfig config;

      ModConfigEvent(ModConfig config) {
         this.config = config;
      }

      public ModConfig getConfig() {
         return this.config;
      }
   }

   public static enum Type {
      COMMON,
      CLIENT,
      SERVER;

      public String extension() {
         return StringUtils.toLowerCase(this.name());
      }
   }
}
