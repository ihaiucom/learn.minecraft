package net.minecraftforge.fml;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModList {
   private static Logger LOGGER = LogManager.getLogger();
   private static ModList INSTANCE;
   private final List<ModFileInfo> modFiles;
   private final List<ModInfo> sortedList;
   private final Map<String, ModFileInfo> fileById;
   private List<ModContainer> mods;
   private Map<String, ModContainer> indexedMods;
   private ForkJoinPool modLoadingThreadPool;
   private List<ModFileScanData> modFileScanData;
   static LifecycleEventProvider.EventHandler<LifecycleEventProvider.LifecycleEvent, Consumer<List<ModLoadingException>>, Executor, Runnable> inlineDispatcher = (event, errors, executor, ticker) -> {
      get().dispatchSynchronousEvent(event, errors, executor);
   };
   static LifecycleEventProvider.EventHandler<LifecycleEventProvider.LifecycleEvent, Consumer<List<ModLoadingException>>, Executor, Runnable> parallelDispatcher = (event, errors, executor, ticker) -> {
      get().dispatchParallelEvent(event, errors, executor, ticker);
   };

   private ModList(List<ModFile> modFiles, List<ModInfo> sortedList) {
      Stream var10001 = modFiles.stream().map(ModFile::getModFileInfo);
      ModFileInfo.class.getClass();
      this.modFiles = (List)var10001.map(ModFileInfo.class::cast).collect(Collectors.toList());
      var10001 = sortedList.stream();
      ModInfo.class.getClass();
      this.sortedList = (List)var10001.map(ModInfo.class::cast).collect(Collectors.toList());
      var10001 = this.modFiles.stream().map(ModFileInfo::getMods).flatMap(Collection::stream);
      ModInfo.class.getClass();
      this.fileById = (Map)var10001.map(ModInfo.class::cast).collect(Collectors.toMap(ModInfo::getModId, ModInfo::getOwningFile));
      int loadingThreadCount = FMLConfig.loadingThreadCount();
      LOGGER.debug(Logging.LOADING, "Using {} threads for parallel mod-loading", loadingThreadCount);
      this.modLoadingThreadPool = new ForkJoinPool(loadingThreadCount, ModList::newForkJoinWorkerThread, (UncaughtExceptionHandler)null, false);
      CrashReportExtender.registerCrashCallable("Mod List", this::crashReport);
   }

   private String getModContainerState(String modId) {
      return (String)this.getModContainerById(modId).map(ModContainer::getCurrentState).map(Object::toString).orElse("NONE");
   }

   private String fileToLine(ModFile mf) {
      return mf.getFileName() + " " + ((IModInfo)mf.getModInfos().get(0)).getDisplayName() + " " + (String)mf.getModInfos().stream().map((mi) -> {
         return mi.getModId() + "@" + mi.getVersion() + " " + this.getModContainerState(mi.getModId());
      }).collect(Collectors.joining(", ", "{", "}"));
   }

   private String crashReport() {
      return "\n" + (String)this.applyForEachModFile(this::fileToLine).collect(Collectors.joining("\n\t\t", "\t\t", ""));
   }

   public static ModList of(List<ModFile> modFiles, List<ModInfo> sortedList) {
      INSTANCE = new ModList(modFiles, sortedList);
      return INSTANCE;
   }

   public static ModList get() {
      return INSTANCE;
   }

   private static ForkJoinWorkerThread newForkJoinWorkerThread(ForkJoinPool pool) {
      ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
      thread.setName("modloading-worker-" + thread.getPoolIndex());
      thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
      return thread;
   }

   public List<ModFileInfo> getModFiles() {
      return this.modFiles;
   }

   public ModFileInfo getModFileById(String modid) {
      return (ModFileInfo)this.fileById.get(modid);
   }

   private void dispatchSynchronousEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent, Consumer<List<ModLoadingException>> errorHandler, Executor executor) {
      LOGGER.debug(Logging.LOADING, "Dispatching synchronous event {}", lifecycleEvent);
      FMLLoader.getLanguageLoadingProvider().forEach((lp) -> {
         lp.consumeLifecycleEvent(() -> {
            return lifecycleEvent;
         });
      });
      this.mods.forEach((m) -> {
         m.transitionState(lifecycleEvent, errorHandler);
      });
      FMLLoader.getLanguageLoadingProvider().forEach((lp) -> {
         lp.consumeLifecycleEvent(() -> {
            return lifecycleEvent;
         });
      });
   }

   private void dispatchParallelEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent, Consumer<List<ModLoadingException>> errorHandler, Executor executor, Runnable ticker) {
      LOGGER.debug(Logging.LOADING, "Dispatching parallel event {}", lifecycleEvent);
      FMLLoader.getLanguageLoadingProvider().forEach((lp) -> {
         lp.consumeLifecycleEvent(() -> {
            return lifecycleEvent;
         });
      });
      DeferredWorkQueue.clear();

      try {
         ForkJoinTask parallelTask = this.modLoadingThreadPool.submit(() -> {
            this.mods.parallelStream().forEach((m) -> {
               m.transitionState(lifecycleEvent, errorHandler);
            });
         });

         while(ticker != null && !parallelTask.isDone()) {
            executor.execute(ticker);
         }

         parallelTask.get();
      } catch (ExecutionException | InterruptedException var6) {
         LOGGER.error(Logging.LOADING, "Encountered an exception during parallel processing - sleeping 10 seconds to wait for jobs to finish", var6);
         errorHandler.accept(Collections.singletonList(new ModList.UncaughtModLoadingException(lifecycleEvent.fromStage(), var6)));
         this.modLoadingThreadPool.awaitQuiescence(10L, TimeUnit.SECONDS);
         if (!this.modLoadingThreadPool.isQuiescent()) {
            LOGGER.fatal(Logging.LOADING, "The parallel pool has failed to quiesce correctly, forcing a shutdown. There is something really wrong here");
            this.modLoadingThreadPool.shutdownNow();
            throw new RuntimeException("Forge played \"STOP IT NOW MODS!\" - it was \"NOT VERY EFFECTIVE\"");
         }
      }

      DeferredWorkQueue.runTasks(lifecycleEvent.fromStage(), errorHandler, executor);
      FMLLoader.getLanguageLoadingProvider().forEach((lp) -> {
         lp.consumeLifecycleEvent(() -> {
            return lifecycleEvent;
         });
      });
   }

   void setLoadedMods(List<ModContainer> modContainers) {
      this.mods = modContainers;
      this.indexedMods = (Map)modContainers.stream().collect(Collectors.toMap(ModContainer::getModId, Function.identity()));
   }

   public <T> Optional<T> getModObjectById(String modId) {
      return this.getModContainerById(modId).map(ModContainer::getMod).map((o) -> {
         return o;
      });
   }

   public Optional<? extends ModContainer> getModContainerById(String modId) {
      return Optional.ofNullable(this.indexedMods.get(modId));
   }

   public Optional<? extends ModContainer> getModContainerByObject(Object obj) {
      return this.mods.stream().filter((mc) -> {
         return mc.getMod() == obj;
      }).findFirst();
   }

   public List<ModInfo> getMods() {
      return this.sortedList;
   }

   public boolean isLoaded(String modTarget) {
      return this.indexedMods.containsKey(modTarget);
   }

   public int size() {
      return this.mods.size();
   }

   public List<ModFileScanData> getAllScanData() {
      if (this.modFileScanData == null) {
         this.modFileScanData = (List)this.sortedList.stream().map(ModInfo::getOwningFile).filter(Objects::nonNull).map(ModFileInfo::getFile).map(ModFile::getScanResult).collect(Collectors.toList());
      }

      return this.modFileScanData;
   }

   public void forEachModFile(Consumer<ModFile> fileConsumer) {
      this.modFiles.stream().map(ModFileInfo::getFile).forEach(fileConsumer);
   }

   public <T> Stream<T> applyForEachModFile(Function<ModFile, T> function) {
      return this.modFiles.stream().map(ModFileInfo::getFile).map(function);
   }

   public void forEachModContainer(BiConsumer<String, ModContainer> modContainerConsumer) {
      this.indexedMods.forEach(modContainerConsumer);
   }

   public <T> Stream<T> applyForEachModContainer(Function<ModContainer, T> function) {
      return this.indexedMods.values().stream().map(function);
   }

   private static class UncaughtModLoadingException extends ModLoadingException {
      public UncaughtModLoadingException(ModLoadingStage stage, Throwable originalException) {
         super((IModInfo)null, stage, "fml.modloading.uncaughterror", originalException);
      }
   }
}
