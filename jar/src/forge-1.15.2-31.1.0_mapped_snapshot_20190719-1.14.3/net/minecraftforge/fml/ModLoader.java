package net.minecraftforge.fml;

import com.google.common.collect.ImmutableList;
import cpw.mods.modlauncher.TransformingClassLoader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.registry.Bootstrap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.InvalidModIdentifier;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider.IModLanguageLoader;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModLoader {
   private static final Logger LOGGER = LogManager.getLogger();
   private static ModLoader INSTANCE;
   private final TransformingClassLoader launchClassLoader;
   private final LoadingModList loadingModList;
   private final List<ModLoadingException> loadingExceptions;
   private final List<ModLoadingWarning> loadingWarnings;
   private GatherDataEvent.DataGeneratorConfig dataGeneratorConfig;
   private ExistingFileHelper existingFileHelper;
   private final Optional<Consumer<String>> statusConsumer = net.minecraftforge.fml.loading.progress.StartupMessageManager.modLoaderConsumer();

   private ModLoader() {
      INSTANCE = this;
      this.launchClassLoader = FMLLoader.getLaunchClassLoader();
      this.loadingModList = FMLLoader.getLoadingModList();
      this.loadingExceptions = (List)FMLLoader.getLoadingModList().getErrors().stream().flatMap(ModLoadingException::fromEarlyException).collect(Collectors.toList());
      this.loadingWarnings = (List)FMLLoader.getLoadingModList().getBrokenFiles().stream().map((file) -> {
         return new ModLoadingWarning((IModInfo)null, ModLoadingStage.VALIDATE, (String)InvalidModIdentifier.identifyJarProblem(file.getFilePath()).orElse("fml.modloading.brokenfile"), new Object[]{file.getFileName()});
      }).collect(Collectors.toList());
      LOGGER.debug(Logging.CORE, "Loading Network data for FML net version: {}", FMLNetworkConstants.init());
      CrashReportExtender.registerCrashCallable("ModLauncher", FMLLoader::getLauncherInfo);
      CrashReportExtender.registerCrashCallable("ModLauncher launch target", FMLLoader::launcherHandlerName);
      CrashReportExtender.registerCrashCallable("ModLauncher naming", FMLLoader::getNaming);
      CrashReportExtender.registerCrashCallable("ModLauncher services", this::computeModLauncherServiceList);
      CrashReportExtender.registerCrashCallable("FML", ForgeVersion::getSpec);
      CrashReportExtender.registerCrashCallable("Forge", () -> {
         return ForgeVersion.getGroup() + ":" + ForgeVersion.getVersion();
      });
      CrashReportExtender.registerCrashCallable("FML Language Providers", this::computeLanguageList);
   }

   private String computeLanguageList() {
      return "\n" + (String)FMLLoader.getLanguageLoadingProvider().applyForEach((lp) -> {
         return lp.name() + "@" + lp.getClass().getPackage().getImplementationVersion();
      }).collect(Collectors.joining("\n\t\t", "\t\t", ""));
   }

   private String computeModLauncherServiceList() {
      List<Map<String, String>> mods = FMLLoader.modLauncherModList();
      return "\n" + (String)mods.stream().map((mod) -> {
         return (String)mod.getOrDefault("file", "nofile") + " " + (String)mod.getOrDefault("name", "missing") + " " + (String)mod.getOrDefault("type", "NOTYPE") + " " + (String)mod.getOrDefault("description", "");
      }).collect(Collectors.joining("\n\t\t", "\t\t", ""));
   }

   public static ModLoader get() {
      return INSTANCE == null ? (INSTANCE = new ModLoader()) : INSTANCE;
   }

   public void loadMods(Executor mainThreadExecutor, Consumer<Consumer<Supplier<Event>>> preSidedRunnable, Consumer<Consumer<Supplier<Event>>> postSidedRunnable) {
      this.statusConsumer.ifPresent((c) -> {
         c.accept("Loading mod config");
      });
      DistExecutor.runWhenOn(Dist.CLIENT, () -> {
         return () -> {
            ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, FMLPaths.CONFIGDIR.get());
         };
      });
      ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, FMLPaths.CONFIGDIR.get());
      this.statusConsumer.ifPresent((c) -> {
         c.accept("Mod setup: SETUP");
      });
      this.dispatchAndHandleError(LifecycleEventProvider.SETUP, mainThreadExecutor, (Runnable)null);
      this.statusConsumer.ifPresent((c) -> {
         c.accept("Mod setup: SIDED SETUP");
      });
      mainThreadExecutor.execute(() -> {
         preSidedRunnable.accept((c) -> {
            ModList.get().forEachModContainer((mi, mc) -> {
               mc.acceptEvent((Event)c.get());
            });
         });
      });
      this.dispatchAndHandleError(LifecycleEventProvider.SIDED_SETUP, mainThreadExecutor, (Runnable)null);
      mainThreadExecutor.execute(() -> {
         postSidedRunnable.accept((c) -> {
            ModList.get().forEachModContainer((mi, mc) -> {
               mc.acceptEvent((Event)c.get());
            });
         });
      });
      this.statusConsumer.ifPresent((c) -> {
         c.accept("Mod setup complete");
      });
   }

   public void gatherAndInitializeMods(Runnable ticker) {
      this.statusConsumer.ifPresent((c) -> {
         c.accept("Loading mods");
      });
      ModList modList = ModList.of((List)this.loadingModList.getModFiles().stream().map(ModFileInfo::getFile).collect(Collectors.toList()), this.loadingModList.getMods());
      if (!this.loadingExceptions.isEmpty()) {
         LOGGER.fatal(Logging.CORE, "Error during pre-loading phase", (Throwable)this.loadingExceptions.get(0));
         modList.setLoadedMods(Collections.emptyList());
         throw new LoadingFailedException(this.loadingExceptions);
      } else {
         this.statusConsumer.ifPresent((c) -> {
            c.accept("Building Mod List");
         });
         List<ModContainer> modContainers = (List)this.loadingModList.getModFiles().stream().map(ModFileInfo::getFile).map((mf) -> {
            return this.buildMods(mf, this.launchClassLoader);
         }).flatMap(Collection::stream).collect(Collectors.toList());
         if (!this.loadingExceptions.isEmpty()) {
            LOGGER.fatal(Logging.CORE, "Failed to initialize mod containers", (Throwable)this.loadingExceptions.get(0));
            modList.setLoadedMods(Collections.emptyList());
            throw new LoadingFailedException(this.loadingExceptions);
         } else {
            modList.setLoadedMods(modContainers);
            this.statusConsumer.ifPresent((c) -> {
               c.accept(String.format("Constructing %d mods", modList.size()));
            });
            this.dispatchAndHandleError(LifecycleEventProvider.CONSTRUCT, Runnable::run, ticker);
            this.statusConsumer.ifPresent((c) -> {
               c.accept("Creating registries");
            });
            GameData.fireCreateRegistryEvents(LifecycleEventProvider.CREATE_REGISTRIES, (event) -> {
               this.dispatchAndHandleError(event, Runnable::run, ticker);
            });
            ObjectHolderRegistry.findObjectHolders();
            CapabilityManager.INSTANCE.injectCapabilities(modList.getAllScanData());
            this.statusConsumer.ifPresent((c) -> {
               c.accept("Populating registries");
            });
            GameData.fireRegistryEvents((rl) -> {
               return true;
            }, LifecycleEventProvider.LOAD_REGISTRIES, (event) -> {
               this.dispatchAndHandleError(event, Runnable::run, ticker);
            });
            this.statusConsumer.ifPresent((c) -> {
               c.accept("Early mod loading complete");
            });
         }
      }
   }

   private void dispatchAndHandleError(LifecycleEventProvider event, Executor executor, Runnable ticker) {
      if (!this.loadingExceptions.isEmpty()) {
         LOGGER.error(Logging.LOADING, "Skipping lifecycle event {}, {} errors found.", event, this.loadingExceptions.size());
      } else {
         event.dispatch(this::accumulateErrors, executor, ticker);
      }

      if (!this.loadingExceptions.isEmpty()) {
         LOGGER.fatal(Logging.LOADING, "Failed to complete lifecycle event {}, {} errors found", event, this.loadingExceptions.size());
         throw new LoadingFailedException(this.loadingExceptions);
      }
   }

   private void accumulateErrors(List<ModLoadingException> errors) {
      this.loadingExceptions.addAll(errors);
   }

   private List<ModContainer> buildMods(ModFile modFile, TransformingClassLoader modClassLoader) {
      Map<String, IModInfo> modInfoMap = (Map)modFile.getModFileInfo().getMods().stream().collect(Collectors.toMap(IModInfo::getModId, Function.identity()));
      LOGGER.debug(Logging.LOADING, "ModContainer is {}", ModContainer.class.getClassLoader());
      List<ModContainer> containers = (List)modFile.getScanResult().getTargets().entrySet().stream().map((e) -> {
         return this.buildModContainerFromTOML(modFile, modClassLoader, modInfoMap, e);
      }).filter((e) -> {
         return e != null;
      }).collect(Collectors.toList());
      if (containers.size() != modInfoMap.size()) {
         LOGGER.fatal(Logging.LOADING, "File {} constructed {} mods: {}, but had {} mods specified: {}", modFile.getFilePath(), containers.size(), containers.stream().map((c) -> {
            return c != null ? c.getModId() : "(null)";
         }).collect(Collectors.toList()), modInfoMap.size(), modInfoMap.values().stream().map(IModInfo::getModId).collect(Collectors.toList()));
         this.loadingExceptions.add(new ModLoadingException((IModInfo)null, ModLoadingStage.CONSTRUCT, "fml.modloading.missingclasses", (Throwable)null, new Object[]{modFile.getFilePath()}));
      }

      return containers;
   }

   private ModContainer buildModContainerFromTOML(ModFile modFile, TransformingClassLoader modClassLoader, Map<String, IModInfo> modInfoMap, Entry<String, ? extends IModLanguageLoader> idToProviderEntry) {
      try {
         String modId = (String)idToProviderEntry.getKey();
         IModLanguageLoader languageLoader = (IModLanguageLoader)idToProviderEntry.getValue();
         IModInfo info = (IModInfo)Optional.ofNullable(modInfoMap.get(modId)).orElseThrow(() -> {
            return new ModLoadingException((IModInfo)null, ModLoadingStage.CONSTRUCT, "fml.modloading.missingmetadata", (Throwable)null, new Object[]{modId});
         });
         return (ModContainer)languageLoader.loadMod(info, modClassLoader, modFile.getScanResult());
      } catch (ModLoadingException var8) {
         this.loadingExceptions.add(var8);
         return null;
      }
   }

   public void postEvent(Event e) {
      ModList.get().forEachModContainer((id, mc) -> {
         mc.acceptEvent(e);
      });
   }

   public void finishMods(Executor mainThreadExecutor) {
      this.statusConsumer.ifPresent((c) -> {
         c.accept("Mod setup: ENQUEUE IMC");
      });
      this.dispatchAndHandleError(LifecycleEventProvider.ENQUEUE_IMC, mainThreadExecutor, (Runnable)null);
      this.statusConsumer.ifPresent((c) -> {
         c.accept("Mod setup: PROCESS IMC");
      });
      this.dispatchAndHandleError(LifecycleEventProvider.PROCESS_IMC, mainThreadExecutor, (Runnable)null);
      this.statusConsumer.ifPresent((c) -> {
         c.accept("Mod setup: Final completion");
      });
      this.dispatchAndHandleError(LifecycleEventProvider.COMPLETE, mainThreadExecutor, (Runnable)null);
      this.statusConsumer.ifPresent((c) -> {
         c.accept("Freezing data");
      });
      GameData.freezeData();
      NetworkRegistry.lock();
      this.statusConsumer.ifPresent((c) -> {
         c.accept(String.format("Mod loading complete - %d mods loaded", ModList.get().size()));
      });
   }

   public List<ModLoadingWarning> getWarnings() {
      return ImmutableList.copyOf(this.loadingWarnings);
   }

   public void addWarning(ModLoadingWarning warning) {
      this.loadingWarnings.add(warning);
   }

   public void runDataGenerator(Set<String> mods, Path path, Collection<Path> inputs, Collection<Path> existingPacks, boolean serverGenerators, boolean clientGenerators, boolean devToolGenerators, boolean reportsGenerator, boolean structureValidator) {
      if (!mods.contains("minecraft") || mods.size() != 1) {
         LOGGER.info("Initializing Data Gatherer for mods {}", mods);
         Bootstrap.register();
         this.dataGeneratorConfig = new GatherDataEvent.DataGeneratorConfig(mods, path, inputs, serverGenerators, clientGenerators, devToolGenerators, reportsGenerator, structureValidator);
         this.existingFileHelper = new ExistingFileHelper(existingPacks, structureValidator);
         this.gatherAndInitializeMods((Runnable)null);
         this.dispatchAndHandleError(LifecycleEventProvider.GATHERDATA, Runnable::run, (Runnable)null);
         this.dataGeneratorConfig.runAll();
      }
   }

   public Function<ModContainer, ModLifecycleEvent> getDataGeneratorEvent() {
      return (mc) -> {
         return new GatherDataEvent(mc, this.dataGeneratorConfig.makeGenerator((p) -> {
            return this.dataGeneratorConfig.getMods().size() == 1 ? p : p.resolve(mc.getModId());
         }, this.dataGeneratorConfig.getMods().contains(mc.getModId())), this.dataGeneratorConfig, this.existingFileHelper);
      };
   }
}
