package net.minecraftforge.fml.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.registries.ClearableRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkHooks {
   private static final Logger LOGGER = LogManager.getLogger();
   private static Int2ObjectMap<DimensionType> trackingMap = new Int2ObjectOpenHashMap();

   public static String getFMLVersion(String ip) {
      return ip.contains("\u0000") ? (Objects.equals(ip.split("\u0000")[1], "FML2") ? "FML2" : ip.split("\u0000")[1]) : "NONE";
   }

   public static ConnectionType getConnectionType(Supplier<NetworkManager> connection) {
      return ConnectionType.forVersionFlag((String)((NetworkManager)connection.get()).channel().attr(FMLNetworkConstants.FML_NETVERSION).get());
   }

   public static IPacket<?> getEntitySpawningPacket(Entity entity) {
      return FMLNetworkConstants.playChannel.toVanillaPacket(new FMLPlayMessages.SpawnEntity(entity), NetworkDirection.PLAY_TO_CLIENT);
   }

   public static boolean onCustomPayload(ICustomPacket<?> packet, NetworkManager manager) {
      return (Boolean)NetworkRegistry.findTarget(packet.getName()).map((ni) -> {
         return ni.dispatch(packet.getDirection(), packet, manager);
      }).orElse(Boolean.FALSE);
   }

   public static void registerServerLoginChannel(NetworkManager manager, CHandshakePacket packet) {
      manager.channel().attr(FMLNetworkConstants.FML_NETVERSION).set(packet.getFMLVersion());
      FMLHandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_CLIENT);
   }

   public static synchronized void registerClientLoginChannel(NetworkManager manager) {
      manager.channel().attr(FMLNetworkConstants.FML_NETVERSION).set("NONE");
      FMLHandshakeHandler.registerHandshake(manager, NetworkDirection.LOGIN_TO_SERVER);
   }

   public static synchronized void sendMCRegistryPackets(NetworkManager manager, String direction) {
      Set<ResourceLocation> resourceLocations = (Set)NetworkRegistry.buildChannelVersions().keySet().stream().filter((rl) -> {
         return !Objects.equals(rl.getNamespace(), "minecraft");
      }).collect(Collectors.toSet());
      FMLMCRegisterPacketHandler.INSTANCE.addChannels(resourceLocations, manager);
      FMLMCRegisterPacketHandler.INSTANCE.sendRegistry(manager, NetworkDirection.valueOf(direction));
   }

   public static synchronized void sendDimensionDataPacket(NetworkManager manager, ServerPlayerEntity player) {
      if (!player.dimension.isVanilla()) {
         if (!manager.isLocalChannel()) {
            FMLNetworkConstants.playChannel.sendTo(new FMLPlayMessages.DimensionInfoMessage(player.dimension), manager, NetworkDirection.PLAY_TO_CLIENT);
         }
      }
   }

   public static void handleClientLoginSuccess(NetworkManager manager) {
      if (manager != null && manager.channel() != null) {
         if (getConnectionType(() -> {
            return manager;
         }) == ConnectionType.VANILLA) {
            LOGGER.info("Connected to a vanilla server. Catching up missing behaviour.");
            ConfigTracker.INSTANCE.loadDefaultServerConfigs();
         } else {
            LOGGER.info("Connected to a modded server.");
         }

      } else {
         throw new NullPointerException("ARGH! Network Manager is null (" + manager != null ? "CHANNEL" : "MANAGER)");
      }
   }

   public static boolean tickNegotiation(ServerLoginNetHandler netHandlerLoginServer, NetworkManager networkManager, ServerPlayerEntity player) {
      return FMLHandshakeHandler.tickLogin(networkManager);
   }

   public static void openGui(ServerPlayerEntity player, INamedContainerProvider containerSupplier) {
      openGui(player, containerSupplier, (buf) -> {
      });
   }

   public static void openGui(ServerPlayerEntity player, INamedContainerProvider containerSupplier, BlockPos pos) {
      openGui(player, containerSupplier, (buf) -> {
         buf.writeBlockPos(pos);
      });
   }

   public static void openGui(ServerPlayerEntity player, INamedContainerProvider containerSupplier, Consumer<PacketBuffer> extraDataWriter) {
      if (!player.world.isRemote) {
         player.closeContainer();
         player.getNextWindowId();
         int openContainerId = player.currentWindowId;
         PacketBuffer extraData = new PacketBuffer(Unpooled.buffer());
         extraDataWriter.accept(extraData);
         extraData.readerIndex(0);
         PacketBuffer output = new PacketBuffer(Unpooled.buffer());
         output.writeVarInt(extraData.readableBytes());
         output.writeBytes((ByteBuf)extraData);
         if (output.readableBytes() <= 32600 && output.readableBytes() >= 1) {
            Container c = containerSupplier.createMenu(openContainerId, player.inventory, player);
            ContainerType<?> type = c.getType();
            FMLPlayMessages.OpenContainer msg = new FMLPlayMessages.OpenContainer(type, openContainerId, containerSupplier.getDisplayName(), output);
            FMLNetworkConstants.playChannel.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
            player.openContainer = c;
            player.openContainer.addListener(player);
            MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, c));
         } else {
            throw new IllegalArgumentException("Invalid PacketBuffer for openGui, found " + output.readableBytes() + " bytes");
         }
      }
   }

   public static DimensionType getDummyDimType(int dimension) {
      return (DimensionType)trackingMap.computeIfAbsent(dimension, (id) -> {
         return DimensionType.getById(id);
      });
   }

   static void addCachedDimensionType(DimensionType dimensionType, ResourceLocation dimName) {
      trackingMap.put(dimensionType.getId(), dimensionType);
      ClearableRegistry<DimensionType> dimtypereg = (ClearableRegistry)Registry.DIMENSION_TYPE;
      dimtypereg.register(dimensionType.getId(), dimName, dimensionType);
   }
}
