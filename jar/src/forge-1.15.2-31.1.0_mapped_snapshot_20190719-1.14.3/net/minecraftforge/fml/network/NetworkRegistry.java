package net.minecraftforge.fml.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkRegistry {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker NETREGISTRY = MarkerManager.getMarker("NETREGISTRY");
   private static Map<ResourceLocation, NetworkInstance> instances = Collections.synchronizedMap(new HashMap());
   public static String ABSENT = new String("ABSENT \ud83e\udd14");
   public static String ACCEPTVANILLA = new String("ALLOWVANILLA \ud83d\udc93\ud83d\udc93\ud83d\udc93");
   private static boolean lock = false;

   public static List<String> getServerNonVanillaNetworkMods() {
      return listRejectedVanillaMods(NetworkInstance::tryClientVersionOnServer);
   }

   public static List<String> getClientNonVanillaNetworkMods() {
      return listRejectedVanillaMods(NetworkInstance::tryServerVersionOnClient);
   }

   public static boolean acceptsVanillaClientConnections() {
      return instances.isEmpty() || getServerNonVanillaNetworkMods().isEmpty();
   }

   public static boolean canConnectToVanillaServer() {
      return instances.isEmpty() || getClientNonVanillaNetworkMods().isEmpty();
   }

   public static SimpleChannel newSimpleChannel(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
      return new SimpleChannel(createInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
   }

   public static EventNetworkChannel newEventChannel(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
      return new EventNetworkChannel(createInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
   }

   private static NetworkInstance createInstance(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
      if (lock) {
         LOGGER.error(NETREGISTRY, "Attempted to register channel {} even though registry phase is over", name);
         throw new IllegalArgumentException("Registration of network channels is locked");
      } else if (instances.containsKey(name)) {
         LOGGER.error(NETREGISTRY, "NetworkDirection channel {} already registered.", name);
         throw new IllegalArgumentException("NetworkDirection Channel {" + name + "} already registered");
      } else {
         NetworkInstance networkInstance = new NetworkInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
         instances.put(name, networkInstance);
         return networkInstance;
      }
   }

   static Optional<NetworkInstance> findTarget(ResourceLocation resourceLocation) {
      return Optional.ofNullable(instances.get(resourceLocation));
   }

   static Map<ResourceLocation, String> buildChannelVersions() {
      return (Map)instances.entrySet().stream().collect(Collectors.toMap(Entry::getKey, (e) -> {
         return ((NetworkInstance)e.getValue()).getNetworkProtocolVersion();
      }));
   }

   static Map<ResourceLocation, Pair<String, Boolean>> buildChannelVersionsForListPing() {
      return (Map)instances.entrySet().stream().map((p) -> {
         return Pair.of(p.getKey(), Pair.of(((NetworkInstance)p.getValue()).getNetworkProtocolVersion(), ((NetworkInstance)p.getValue()).tryClientVersionOnServer(ABSENT)));
      }).filter((p) -> {
         return !((ResourceLocation)p.getLeft()).getNamespace().equals("fml");
      }).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
   }

   static List<String> listRejectedVanillaMods(BiFunction<NetworkInstance, String, Boolean> testFunction) {
      List<Pair<ResourceLocation, Boolean>> results = (List)instances.values().stream().map((ni) -> {
         String incomingVersion = ACCEPTVANILLA;
         boolean test = (Boolean)testFunction.apply(ni, incomingVersion);
         LOGGER.debug(NETREGISTRY, "Channel '{}' : Vanilla acceptance test: {}", ni.getChannelName(), test ? "ACCEPTED" : "REJECTED");
         return Pair.of(ni.getChannelName(), test);
      }).filter((p) -> {
         return !(Boolean)p.getRight();
      }).collect(Collectors.toList());
      if (!results.isEmpty()) {
         LOGGER.error(NETREGISTRY, "Channels [{}] rejected vanilla connections", results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.joining(",")));
         return (List)results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.toList());
      } else {
         LOGGER.debug(NETREGISTRY, "Accepting channel list from vanilla");
         return Collections.emptyList();
      }
   }

   static boolean validateClientChannels(Map<ResourceLocation, String> channels) {
      return validateChannels(channels, "server", NetworkInstance::tryServerVersionOnClient);
   }

   static boolean validateServerChannels(Map<ResourceLocation, String> channels) {
      return validateChannels(channels, "client", NetworkInstance::tryClientVersionOnServer);
   }

   private static boolean validateChannels(Map<ResourceLocation, String> incoming, String originName, BiFunction<NetworkInstance, String, Boolean> testFunction) {
      List<Pair<ResourceLocation, Boolean>> results = (List)instances.values().stream().map((ni) -> {
         String incomingVersion = (String)incoming.getOrDefault(ni.getChannelName(), ABSENT);
         boolean test = (Boolean)testFunction.apply(ni, incomingVersion);
         LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' from {} : {}", ni.getChannelName(), incomingVersion, originName, test ? "ACCEPTED" : "REJECTED");
         return Pair.of(ni.getChannelName(), test);
      }).filter((p) -> {
         return !(Boolean)p.getRight();
      }).collect(Collectors.toList());
      if (!results.isEmpty()) {
         LOGGER.error(NETREGISTRY, "Channels [{}] rejected their {} side version number", results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.joining(",")), originName);
         return false;
      } else {
         LOGGER.debug(NETREGISTRY, "Accepting channel list from {}", originName);
         return true;
      }
   }

   static List<NetworkRegistry.LoginPayload> gatherLoginPayloads(NetworkDirection direction, boolean isLocal) {
      if (direction != NetworkDirection.LOGIN_TO_CLIENT) {
         return Collections.emptyList();
      } else {
         List<NetworkRegistry.LoginPayload> gatheredPayloads = new ArrayList();
         instances.values().forEach((ni) -> {
            ni.dispatchGatherLogin(gatheredPayloads, isLocal);
         });
         return gatheredPayloads;
      }
   }

   public static boolean checkListPingCompatibilityForClient(Map<ResourceLocation, Pair<String, Boolean>> incoming) {
      Set<ResourceLocation> handled = new HashSet();
      List<Pair<ResourceLocation, Boolean>> results = (List)instances.values().stream().filter((p) -> {
         return !p.getChannelName().getNamespace().equals("fml");
      }).map((ni) -> {
         Pair<String, Boolean> incomingVersion = (Pair)incoming.getOrDefault(ni.getChannelName(), Pair.of(ABSENT, true));
         boolean test = ni.tryServerVersionOnClient((String)incomingVersion.getLeft());
         handled.add(ni.getChannelName());
         LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' during listping : {}", ni.getChannelName(), incomingVersion, test ? "ACCEPTED" : "REJECTED");
         return Pair.of(ni.getChannelName(), test);
      }).filter((p) -> {
         return !(Boolean)p.getRight();
      }).collect(Collectors.toList());
      List<ResourceLocation> missingButRequired = (List)incoming.entrySet().stream().filter((p) -> {
         return !((ResourceLocation)p.getKey()).getNamespace().equals("fml");
      }).filter((p) -> {
         return !(Boolean)((Pair)p.getValue()).getRight();
      }).filter((p) -> {
         return !handled.contains(p.getKey());
      }).map(Entry::getKey).collect(Collectors.toList());
      if (!results.isEmpty()) {
         LOGGER.error(NETREGISTRY, "Channels [{}] rejected their server side version number during listping", results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.joining(",")));
         return false;
      } else if (!missingButRequired.isEmpty()) {
         LOGGER.error(NETREGISTRY, "The server is likely to require channel [{}] to be present, yet we don't have it", missingButRequired);
         return false;
      } else {
         LOGGER.debug(NETREGISTRY, "Accepting channel list during listping");
         return true;
      }
   }

   public boolean isLocked() {
      return lock;
   }

   public static void lock() {
      lock = true;
   }

   public static class ChannelBuilder {
      private ResourceLocation channelName;
      private Supplier<String> networkProtocolVersion;
      private Predicate<String> clientAcceptedVersions;
      private Predicate<String> serverAcceptedVersions;

      public static NetworkRegistry.ChannelBuilder named(ResourceLocation channelName) {
         NetworkRegistry.ChannelBuilder builder = new NetworkRegistry.ChannelBuilder();
         builder.channelName = channelName;
         return builder;
      }

      public NetworkRegistry.ChannelBuilder networkProtocolVersion(Supplier<String> networkProtocolVersion) {
         this.networkProtocolVersion = networkProtocolVersion;
         return this;
      }

      public NetworkRegistry.ChannelBuilder clientAcceptedVersions(Predicate<String> clientAcceptedVersions) {
         this.clientAcceptedVersions = clientAcceptedVersions;
         return this;
      }

      public NetworkRegistry.ChannelBuilder serverAcceptedVersions(Predicate<String> serverAcceptedVersions) {
         this.serverAcceptedVersions = serverAcceptedVersions;
         return this;
      }

      private NetworkInstance createNetworkInstance() {
         return NetworkRegistry.createInstance(this.channelName, this.networkProtocolVersion, this.clientAcceptedVersions, this.serverAcceptedVersions);
      }

      public SimpleChannel simpleChannel() {
         return new SimpleChannel(this.createNetworkInstance());
      }

      public EventNetworkChannel eventNetworkChannel() {
         return new EventNetworkChannel(this.createNetworkInstance());
      }
   }

   public static class LoginPayload {
      private final PacketBuffer data;
      private final ResourceLocation channelName;
      private final String messageContext;

      public LoginPayload(PacketBuffer buffer, ResourceLocation channelName, String messageContext) {
         this.data = buffer;
         this.channelName = channelName;
         this.messageContext = messageContext;
      }

      public PacketBuffer getData() {
         return this.data;
      }

      public ResourceLocation getChannelName() {
         return this.channelName;
      }

      public String getMessageContext() {
         return this.messageContext;
      }
   }
}
