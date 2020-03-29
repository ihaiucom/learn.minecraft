package net.minecraftforge.fml.client;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.BrandingControl;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.SidedProvider;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.gui.screen.LoadingErrorScreen;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.packs.DelegatableResourcePack;
import net.minecraftforge.fml.packs.DelegatingResourcePack;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.fml.server.LanguageHook;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientModLoader {
   private static final Logger LOGGER = LogManager.getLogger();
   private static boolean loading;
   private static Minecraft mc;
   private static LoadingFailedException error;
   private static EarlyLoaderGUI earlyLoaderGUI;

   public static void begin(Minecraft minecraft, ResourcePackList<ClientResourcePackInfo> defaultResourcePacks, IReloadableResourceManager mcResourceManager, DownloadingPackFinder metadataSerializer) {
      Runtime.getRuntime().addShutdownHook(new Thread(LogManager::shutdown));
      loading = true;
      mc = minecraft;
      SidedProvider.setClient(() -> {
         return minecraft;
      });
      LogicalSidedProvider.setClient(() -> {
         return minecraft;
      });
      LanguageHook.loadForgeAndMCLangs();
      earlyLoaderGUI = new EarlyLoaderGUI(minecraft.func_228018_at_());
      createRunnableWithCatch(() -> {
         ModLoader var10000 = ModLoader.get();
         EarlyLoaderGUI var10001 = earlyLoaderGUI;
         var10000.gatherAndInitializeMods(var10001::renderTick);
      }).run();
      ResourcePackLoader.loadResourcePacks(defaultResourcePacks, ClientModLoader::buildPackFinder);
      mcResourceManager.addReloadListener(ClientModLoader::onreload);
      mcResourceManager.addReloadListener(BrandingControl.resourceManagerReloadListener());
      ModelLoaderRegistry.init();
   }

   private static CompletableFuture<Void> onreload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler prepareProfiler, IProfiler executeProfiler, Executor asyncExecutor, Executor syncExecutor) {
      CompletableFuture var10000 = CompletableFuture.runAsync(createRunnableWithCatch(() -> {
         startModLoading(syncExecutor);
      }), asyncExecutor);
      stage.getClass();
      return var10000.thenCompose(stage::markCompleteAwaitingOthers).thenRunAsync(() -> {
         finishModLoading(syncExecutor);
      }, asyncExecutor);
   }

   private static Runnable createRunnableWithCatch(Runnable r) {
      return () -> {
         try {
            r.run();
         } catch (LoadingFailedException var2) {
            MinecraftForge.EVENT_BUS.shutdown();
            if (error == null) {
               error = var2;
            }
         }

      };
   }

   private static void startModLoading(Executor executor) {
      earlyLoaderGUI.handleElsewhere();
      createRunnableWithCatch(() -> {
         ModLoader.get().loadMods(executor, ClientModLoader::preSidedRunnable, ClientModLoader::postSidedRunnable);
      }).run();
   }

   private static void postSidedRunnable(Consumer<Supplier<Event>> perModContainerEventProcessor) {
      RenderingRegistry.loadEntityRenderers(mc.getRenderManager());
      ModelLoaderRegistry.initComplete();
   }

   private static void preSidedRunnable(Consumer<Supplier<Event>> perModContainerEventProcessor) {
      perModContainerEventProcessor.accept(ModelRegistryEvent::new);
   }

   private static void finishModLoading(Executor executor) {
      createRunnableWithCatch(() -> {
         ModLoader.get().finishMods(executor);
      }).run();
      loading = false;
      executor.execute(() -> {
         mc.gameSettings.loadOptions();
      });
   }

   public static VersionChecker.Status checkForUpdates() {
      return VersionChecker.Status.UP_TO_DATE;
   }

   public static boolean completeModLoading() {
      RenderSystem.disableTexture();
      RenderSystem.enableTexture();
      List<ModLoadingWarning> warnings = ModLoader.get().getWarnings();
      boolean showWarnings = true;

      try {
         showWarnings = (Boolean)ForgeConfig.CLIENT.showLoadWarnings.get();
      } catch (NullPointerException var3) {
      }

      if (!showWarnings) {
         if (!warnings.isEmpty()) {
            LOGGER.warn(LogMarkers.LOADING, "Mods loaded with {} warning(s)", warnings.size());
            warnings.forEach((warning) -> {
               LOGGER.warn(LogMarkers.LOADING, warning.formatToString());
            });
         }

         warnings = Collections.emptyList();
      }

      if (error == null) {
         MinecraftForge.EVENT_BUS.start();
      }

      if (error == null && warnings.isEmpty()) {
         ClientHooks.logMissingTextureErrors();
         return false;
      } else {
         mc.displayGuiScreen(new LoadingErrorScreen(error, warnings));
         return true;
      }
   }

   public static void renderProgressText() {
      earlyLoaderGUI.renderFromGUI();
   }

   public static boolean isLoading() {
      return loading;
   }

   private static <T extends ResourcePackInfo> ResourcePackLoader.IPackInfoFinder<T> buildPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ? super T> packSetter) {
      return (packList, factory) -> {
         clientPackFinder(modResourcePacks, packSetter, packList, factory);
      };
   }

   private static <T extends ResourcePackInfo> void clientPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ? super T> packSetter, Map<String, T> packList, ResourcePackInfo.IFactory<? extends T> factory) {
      List<DelegatableResourcePack> hiddenPacks = new ArrayList();
      Iterator var5 = modResourcePacks.entrySet().iterator();

      while(var5.hasNext()) {
         Entry<ModFile, ? extends ModFileResourcePack> e = (Entry)var5.next();
         IModInfo mod = (IModInfo)((ModFile)e.getKey()).getModInfos().get(0);
         if (!Objects.equals(mod.getModId(), "minecraft")) {
            String name = "mod:" + mod.getModId();
            T packInfo = ResourcePackInfo.createResourcePack(name, false, e::getValue, factory, ResourcePackInfo.Priority.BOTTOM);
            if (packInfo == null) {
               ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", new Object[]{e.getKey()}));
            } else {
               packSetter.accept(e.getValue(), packInfo);
               LOGGER.debug(Logging.CORE, "Generating PackInfo named {} for mod file {}", name, ((ModFile)e.getKey()).getFilePath());
               if (mod.getOwningFile().showAsResourcePack()) {
                  packList.put(name, packInfo);
               } else {
                  hiddenPacks.add(e.getValue());
               }
            }
         }
      }

      T packInfo = ResourcePackInfo.createResourcePack("mod_resources", true, () -> {
         return new DelegatingResourcePack("mod_resources", "Mod Resources", new PackMetadataSection(new TranslationTextComponent("fml.resources.modresources", new Object[]{hiddenPacks.size()}), 5), hiddenPacks);
      }, factory, ResourcePackInfo.Priority.BOTTOM);
      packList.put("mod_resources", packInfo);
   }
}
