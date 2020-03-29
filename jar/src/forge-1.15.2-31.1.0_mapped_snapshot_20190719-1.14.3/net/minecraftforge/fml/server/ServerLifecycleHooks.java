package net.minecraftforge.fml.server;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.loading.FileUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.network.ConnectionType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ServerLifecycleHooks {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker SERVERHOOKS = MarkerManager.getMarker("SERVERHOOKS");
   private static volatile CountDownLatch exitLatch = null;
   private static MinecraftServer currentServer;
   private static AtomicBoolean allowLogins = new AtomicBoolean(false);

   public static boolean handleServerAboutToStart(MinecraftServer server) {
      currentServer = server;
      LogicalSidedProvider.setServer(() -> {
         return server;
      });
      Path serverConfig = server.getActiveAnvilConverter().getFile(server.getFolderName(), "serverconfig").toPath();
      FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");
      ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, serverConfig);
      ResourcePackLoader.loadResourcePacks(currentServer.getResourcePacks(), ServerLifecycleHooks::buildPackFinder);
      return !MinecraftForge.EVENT_BUS.post(new FMLServerAboutToStartEvent(server));
   }

   public static boolean handleServerStarting(MinecraftServer server) {
      DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> {
         return () -> {
            LanguageHook.loadLanguagesOnServer(server);
         };
      });
      return !MinecraftForge.EVENT_BUS.post(new FMLServerStartingEvent(server));
   }

   public static void handleServerStarted(MinecraftServer server) {
      MinecraftForge.EVENT_BUS.post(new FMLServerStartedEvent(server));
      allowLogins.set(true);
   }

   public static void handleServerStopping(MinecraftServer server) {
      allowLogins.set(false);
      MinecraftForge.EVENT_BUS.post(new FMLServerStoppingEvent(server));
   }

   public static void expectServerStopped() {
      exitLatch = new CountDownLatch(1);
   }

   public static void handleServerStopped(MinecraftServer server) {
      if (!server.isDedicatedServer()) {
         GameData.revertToFrozen();
      }

      MinecraftForge.EVENT_BUS.post(new FMLServerStoppedEvent(server));
      currentServer = null;
      LogicalSidedProvider.setServer((Supplier)null);
      CountDownLatch latch = exitLatch;
      if (latch != null) {
         latch.countDown();
         exitLatch = null;
      }

   }

   public static MinecraftServer getCurrentServer() {
      return currentServer;
   }

   public static boolean handleServerLogin(CHandshakePacket packet, NetworkManager manager) {
      if (!allowLogins.get()) {
         StringTextComponent text = new StringTextComponent("Server is still starting! Please wait before reconnecting.");
         LOGGER.info(SERVERHOOKS, "Disconnecting Player (server is still starting): {}", text.getUnformattedComponentText());
         manager.sendPacket(new SDisconnectLoginPacket(text));
         manager.closeChannel(text);
         return false;
      } else {
         if (packet.getRequestedState() == ProtocolType.LOGIN) {
            ConnectionType connectionType = ConnectionType.forVersionFlag(packet.getFMLVersion());
            int versionNumber = connectionType.getFMLVersionNumber(packet.getFMLVersion());
            if (connectionType == ConnectionType.MODDED && versionNumber != 2) {
               rejectConnection(manager, connectionType, "This modded server is not network compatible with your modded client. Please verify your Forge version closely matches the server. Got net version " + versionNumber + " this server is net version " + 2);
               return false;
            }

            if (connectionType == ConnectionType.VANILLA && !NetworkRegistry.acceptsVanillaClientConnections()) {
               rejectConnection(manager, connectionType, "This server has mods that require Forge to be installed on the client. Contact your server admin for more details.");
               return false;
            }
         }

         if (packet.getRequestedState() == ProtocolType.STATUS) {
            return true;
         } else {
            NetworkHooks.registerServerLoginChannel(manager, packet);
            return true;
         }
      }
   }

   private static void rejectConnection(NetworkManager manager, ConnectionType type, String message) {
      manager.setConnectionState(ProtocolType.LOGIN);
      LOGGER.info(SERVERHOOKS, "Disconnecting {} connection attempt: {}", type, message);
      StringTextComponent text = new StringTextComponent(message);
      manager.sendPacket(new SDisconnectLoginPacket(text));
      manager.closeChannel(text);
   }

   public static void handleExit(int retVal) {
      System.exit(retVal);
   }

   private static <T extends ResourcePackInfo> ResourcePackLoader.IPackInfoFinder<T> buildPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ? super T> packSetter) {
      return (packList, factory) -> {
         serverPackFinder(modResourcePacks, packSetter, packList, factory);
      };
   }

   private static <T extends ResourcePackInfo> void serverPackFinder(Map<ModFile, ? extends ModFileResourcePack> modResourcePacks, BiConsumer<? super ModFileResourcePack, ? super T> packSetter, Map<String, T> packList, ResourcePackInfo.IFactory<? extends T> factory) {
      Iterator var4 = modResourcePacks.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<ModFile, ? extends ModFileResourcePack> e = (Entry)var4.next();
         IModInfo mod = (IModInfo)((ModFile)e.getKey()).getModInfos().get(0);
         if (!Objects.equals(mod.getModId(), "minecraft")) {
            String name = "mod:" + mod.getModId();
            T packInfo = ResourcePackInfo.createResourcePack(name, true, e::getValue, factory, ResourcePackInfo.Priority.TOP);
            if (packInfo == null) {
               ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", new Object[]{e.getKey()}));
            } else {
               packSetter.accept(e.getValue(), packInfo);
               LOGGER.debug(Logging.CORE, "Generating PackInfo named {} for mod file {}", name, ((ModFile)e.getKey()).getFilePath());
               packList.put(name, packInfo);
            }
         }
      }

   }
}
