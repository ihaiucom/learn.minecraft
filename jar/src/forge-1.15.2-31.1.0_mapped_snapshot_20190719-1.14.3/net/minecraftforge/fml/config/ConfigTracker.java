package net.minecraftforge.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ConfigTracker {
   private static final Logger LOGGER = LogManager.getLogger();
   static final Marker CONFIG = MarkerManager.getMarker("CONFIG");
   public static final ConfigTracker INSTANCE = new ConfigTracker();
   private final ConcurrentHashMap<String, ModConfig> fileMap = new ConcurrentHashMap();
   private final EnumMap<ModConfig.Type, Set<ModConfig>> configSets = new EnumMap(ModConfig.Type.class);
   private ConcurrentHashMap<String, Map<ModConfig.Type, ModConfig>> configsByMod = new ConcurrentHashMap();

   private ConfigTracker() {
      this.configSets.put(ModConfig.Type.CLIENT, Collections.synchronizedSet(new LinkedHashSet()));
      this.configSets.put(ModConfig.Type.COMMON, Collections.synchronizedSet(new LinkedHashSet()));
      this.configSets.put(ModConfig.Type.SERVER, Collections.synchronizedSet(new LinkedHashSet()));
   }

   void trackConfig(ModConfig config) {
      if (this.fileMap.containsKey(config.getFileName())) {
         LOGGER.error(CONFIG, "Detected config file conflict {} between {} and {}", config.getFileName(), ((ModConfig)this.fileMap.get(config.getFileName())).getModId(), config.getModId());
         throw new RuntimeException("Config conflict detected!");
      } else {
         this.fileMap.put(config.getFileName(), config);
         ((Set)this.configSets.get(config.getType())).add(config);
         ((Map)this.configsByMod.computeIfAbsent(config.getModId(), (k) -> {
            return new EnumMap(ModConfig.Type.class);
         })).put(config.getType(), config);
         LOGGER.debug(CONFIG, "Config file {} for {} tracking", config.getFileName(), config.getModId());
      }
   }

   public void loadConfigs(ModConfig.Type type, Path configBasePath) {
      LOGGER.debug(CONFIG, "Loading configs type {}", type);
      ((Set)this.configSets.get(type)).forEach((config) -> {
         this.openConfig(config, configBasePath);
      });
   }

   public List<Pair<String, FMLHandshakeMessages.S2CConfigData>> syncConfigs(boolean isLocal) {
      Map<String, byte[]> configData = (Map)((Set)this.configSets.get(ModConfig.Type.SERVER)).stream().collect(Collectors.toMap(ModConfig::getFileName, (mc) -> {
         try {
            return Files.readAllBytes(mc.getFullPath());
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      }));
      return (List)configData.entrySet().stream().map((e) -> {
         return Pair.of("Config " + (String)e.getKey(), new FMLHandshakeMessages.S2CConfigData((String)e.getKey(), (byte[])e.getValue()));
      }).collect(Collectors.toList());
   }

   private void openConfig(ModConfig config, Path configBasePath) {
      LOGGER.debug(CONFIG, "Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
      CommentedFileConfig configData = (CommentedFileConfig)config.getHandler().reader(configBasePath).apply(config);
      config.setConfigData(configData);
      config.fireEvent(new ModConfig.Loading(config));
      config.save();
   }

   public void receiveSyncedConfig(FMLHandshakeMessages.S2CConfigData s2CConfigData, Supplier<NetworkEvent.Context> contextSupplier) {
      if (!Minecraft.getInstance().isIntegratedServerRunning()) {
         Optional.ofNullable(this.fileMap.get(s2CConfigData.getFileName())).ifPresent((mc) -> {
            mc.setConfigData((CommentedConfig)TomlFormat.instance().createParser().parse(new ByteArrayInputStream(s2CConfigData.getBytes())));
            mc.fireEvent(new ModConfig.Reloading(mc));
         });
      }

   }

   public void loadDefaultServerConfigs() {
      ((Set)this.configSets.get(ModConfig.Type.SERVER)).forEach((modConfig) -> {
         CommentedConfig commentedConfig = CommentedConfig.inMemory();
         modConfig.getSpec().correct(commentedConfig);
         modConfig.setConfigData(commentedConfig);
         modConfig.fireEvent(new ModConfig.Loading(modConfig));
      });
   }

   public String getConfigFileName(String modId, ModConfig.Type type) {
      return (String)Optional.ofNullable(((Map)this.configsByMod.getOrDefault(modId, Collections.emptyMap())).getOrDefault(type, (Object)null)).map(ModConfig::getFullPath).map(Object::toString).orElse((Object)null);
   }
}
