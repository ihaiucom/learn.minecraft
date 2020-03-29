package net.minecraftforge.fml.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.loading.AdvancedLogMessageAdapter;
import net.minecraftforge.fml.util.ThreeConsumer;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class FMLHandshakeHandler {
   static final Marker FMLHSMARKER;
   private static final Logger LOGGER;
   private static final FMLLoginWrapper loginWrapper;
   private List<NetworkRegistry.LoginPayload> messageList;
   private List<Integer> sentMessages = new ArrayList();
   private final NetworkDirection direction;
   private final NetworkManager manager;
   private int packetPosition;
   private Map<ResourceLocation, ForgeRegistry.Snapshot> registrySnapshots;
   private Set<ResourceLocation> registriesToReceive;
   private Map<ResourceLocation, String> registryHashes;

   static void registerHandshake(NetworkManager manager, NetworkDirection direction) {
      manager.channel().attr(FMLNetworkConstants.FML_HANDSHAKE_HANDLER).compareAndSet((Object)null, new FMLHandshakeHandler(manager, direction));
   }

   static boolean tickLogin(NetworkManager networkManager) {
      return ((FMLHandshakeHandler)networkManager.channel().attr(FMLNetworkConstants.FML_HANDSHAKE_HANDLER).get()).tickServer();
   }

   private FMLHandshakeHandler(NetworkManager networkManager, NetworkDirection side) {
      this.direction = side;
      this.manager = networkManager;
      if (networkManager.isLocalChannel()) {
         this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, true);
         LOGGER.debug(FMLHSMARKER, "Starting local connection.");
      } else if (NetworkHooks.getConnectionType(() -> {
         return this.manager;
      }) == ConnectionType.VANILLA) {
         this.messageList = Collections.emptyList();
         LOGGER.debug(FMLHSMARKER, "Starting new vanilla network connection.");
      } else {
         this.messageList = NetworkRegistry.gatherLoginPayloads(this.direction, false);
         LOGGER.debug(FMLHSMARKER, "Starting new modded network connection. Found {} messages to dispatch.", this.messageList.size());
      }

   }

   public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> biConsumerFor(ThreeConsumer<FMLHandshakeHandler, ? super MSG, ? super Supplier<NetworkEvent.Context>> consumer) {
      return (m, c) -> {
         ThreeConsumer.bindArgs(consumer, m, c).accept(getHandshake(c));
      };
   }

   public static <MSG extends IntSupplier> BiConsumer<MSG, Supplier<NetworkEvent.Context>> indexFirst(ThreeConsumer<FMLHandshakeHandler, MSG, Supplier<NetworkEvent.Context>> next) {
      BiConsumer<MSG, Supplier<NetworkEvent.Context>> loginIndexedMessageSupplierBiConsumer = biConsumerFor(FMLHandshakeHandler::handleIndexedMessage);
      return loginIndexedMessageSupplierBiConsumer.andThen(biConsumerFor(next));
   }

   private static FMLHandshakeHandler getHandshake(Supplier<NetworkEvent.Context> contextSupplier) {
      return (FMLHandshakeHandler)((NetworkEvent.Context)contextSupplier.get()).attr(FMLNetworkConstants.FML_HANDSHAKE_HANDLER).get();
   }

   void handleServerModListOnClient(FMLHandshakeMessages.S2CModList serverModList, Supplier<NetworkEvent.Context> c) {
      LOGGER.debug(FMLHSMARKER, "Logging into server with mod list [{}]", String.join(", ", serverModList.getModList()));
      boolean accepted = NetworkRegistry.validateClientChannels(serverModList.getChannels());
      ((NetworkEvent.Context)c.get()).setPacketHandled(true);
      if (!accepted) {
         LOGGER.error(FMLHSMARKER, "Terminating connection with server, mismatched mod list");
         ((NetworkEvent.Context)c.get()).getNetworkManager().closeChannel(new StringTextComponent("Connection closed - mismatched mod channel list"));
      } else {
         FMLNetworkConstants.handshakeChannel.reply(new FMLHandshakeMessages.C2SModListReply(), (NetworkEvent.Context)c.get());
         LOGGER.debug(FMLHSMARKER, "Accepted server connection");
         ((NetworkEvent.Context)c.get()).getNetworkManager().channel().attr(FMLNetworkConstants.FML_NETVERSION).set("FML2");
         this.registriesToReceive = new HashSet(serverModList.getRegistries());
         this.registrySnapshots = Maps.newHashMap();
         LOGGER.debug(ForgeRegistry.REGISTRIES, "Expecting {} registries: {}", new org.apache.logging.log4j.util.Supplier[]{() -> {
            return this.registriesToReceive.size();
         }, () -> {
            return this.registriesToReceive;
         }});
      }
   }

   <MSG extends IntSupplier> void handleIndexedMessage(MSG message, Supplier<NetworkEvent.Context> c) {
      LOGGER.debug(FMLHSMARKER, "Received client indexed reply {} of type {}", message.getAsInt(), message.getClass().getName());
      boolean removed = this.sentMessages.removeIf((i) -> {
         return i == message.getAsInt();
      });
      if (!removed) {
         LOGGER.error(FMLHSMARKER, "Recieved unexpected index {} in client reply", message.getAsInt());
      }

   }

   void handleClientModListOnServer(FMLHandshakeMessages.C2SModListReply clientModList, Supplier<NetworkEvent.Context> c) {
      LOGGER.debug(FMLHSMARKER, "Received client connection with modlist [{}]", String.join(", ", clientModList.getModList()));
      boolean accepted = NetworkRegistry.validateServerChannels(clientModList.getChannels());
      ((NetworkEvent.Context)c.get()).setPacketHandled(true);
      if (!accepted) {
         LOGGER.error(FMLHSMARKER, "Terminating connection with client, mismatched mod list");
         ((NetworkEvent.Context)c.get()).getNetworkManager().closeChannel(new StringTextComponent("Connection closed - mismatched mod channel list"));
      } else {
         LOGGER.debug(FMLHSMARKER, "Accepted client connection mod list");
      }
   }

   void handleRegistryMessage(FMLHandshakeMessages.S2CRegistry registryPacket, Supplier<NetworkEvent.Context> contextSupplier) {
      LOGGER.debug(FMLHSMARKER, "Received registry packet for {}", registryPacket.getRegistryName());
      this.registriesToReceive.remove(registryPacket.getRegistryName());
      this.registrySnapshots.put(registryPacket.getRegistryName(), registryPacket.getSnapshot());
      boolean continueHandshake = true;
      if (this.registriesToReceive.isEmpty()) {
         continueHandshake = this.handleRegistryLoading(contextSupplier);
      }

      ((NetworkEvent.Context)contextSupplier.get()).setPacketHandled(true);
      if (!continueHandshake) {
         LOGGER.error(FMLHSMARKER, "Connection closed, not continuing handshake");
      } else {
         FMLNetworkConstants.handshakeChannel.reply(new FMLHandshakeMessages.C2SAcknowledge(), (NetworkEvent.Context)contextSupplier.get());
      }

   }

   private boolean handleRegistryLoading(Supplier<NetworkEvent.Context> contextSupplier) {
      AtomicBoolean successfulConnection = new AtomicBoolean(false);
      CountDownLatch block = new CountDownLatch(1);
      ((NetworkEvent.Context)contextSupplier.get()).enqueueWork(() -> {
         LOGGER.debug(FMLHSMARKER, "Injecting registry snapshot from server.");
         Multimap<ResourceLocation, ResourceLocation> missingData = GameData.injectSnapshot(this.registrySnapshots, false, false);
         LOGGER.debug(FMLHSMARKER, "Snapshot injected.");
         if (!missingData.isEmpty()) {
            LOGGER.error(FMLHSMARKER, "Missing registry data for network connection:\n{}", new AdvancedLogMessageAdapter((sb) -> {
               missingData.forEach((reg, entry) -> {
                  sb.append("\t").append(reg).append(": ").append(entry).append('\n');
               });
            }));
         }

         successfulConnection.set(missingData.isEmpty());
         block.countDown();
      });
      LOGGER.debug(FMLHSMARKER, "Waiting for registries to load.");

      try {
         block.await();
      } catch (InterruptedException var5) {
         Thread.interrupted();
      }

      if (successfulConnection.get()) {
         LOGGER.debug(FMLHSMARKER, "Registry load complete, continuing handshake.");
      } else {
         LOGGER.error(FMLHSMARKER, "Failed to load registry, closing connection.");
         this.manager.closeChannel(new StringTextComponent("Failed to synchronize registry data from server, closing connection"));
      }

      return successfulConnection.get();
   }

   void handleClientAck(FMLHandshakeMessages.C2SAcknowledge msg, Supplier<NetworkEvent.Context> contextSupplier) {
      LOGGER.debug(FMLHSMARKER, "Received acknowledgement from client");
      ((NetworkEvent.Context)contextSupplier.get()).setPacketHandled(true);
   }

   void handleConfigSync(FMLHandshakeMessages.S2CConfigData msg, Supplier<NetworkEvent.Context> contextSupplier) {
      LOGGER.debug(FMLHSMARKER, "Received config sync from server");
      ConfigTracker.INSTANCE.receiveSyncedConfig(msg, contextSupplier);
      ((NetworkEvent.Context)contextSupplier.get()).setPacketHandled(true);
      FMLNetworkConstants.handshakeChannel.reply(new FMLHandshakeMessages.C2SAcknowledge(), (NetworkEvent.Context)contextSupplier.get());
   }

   public boolean tickServer() {
      if (this.packetPosition < this.messageList.size()) {
         NetworkRegistry.LoginPayload message = (NetworkRegistry.LoginPayload)this.messageList.get(this.packetPosition);
         LOGGER.debug(FMLHSMARKER, "Sending ticking packet info '{}' to '{}' sequence {}", message.getMessageContext(), message.getChannelName(), this.packetPosition);
         this.sentMessages.add(this.packetPosition);
         loginWrapper.sendServerToClientLoginPacket(message.getChannelName(), message.getData(), this.packetPosition, this.manager);
         ++this.packetPosition;
      }

      if (this.sentMessages.isEmpty() && this.packetPosition >= this.messageList.size() - 1) {
         this.manager.channel().attr(FMLNetworkConstants.FML_HANDSHAKE_HANDLER).set((Object)null);
         LOGGER.debug(FMLHSMARKER, "Handshake complete!");
         return true;
      } else {
         return false;
      }
   }

   static {
      FMLHSMARKER = MarkerManager.getMarker("FMLHANDSHAKE").setParents(new Marker[]{FMLNetworkConstants.NETWORK});
      LOGGER = LogManager.getLogger();
      loginWrapper = new FMLLoginWrapper();
   }
}
