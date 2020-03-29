package net.minecraftforge.fml.network;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class PacketDistributor<T> {
   public static final PacketDistributor<ServerPlayerEntity> PLAYER;
   public static final PacketDistributor<DimensionType> DIMENSION;
   public static final PacketDistributor<PacketDistributor.TargetPoint> NEAR;
   public static final PacketDistributor<Void> ALL;
   public static final PacketDistributor<Void> SERVER;
   public static final PacketDistributor<Entity> TRACKING_ENTITY;
   public static final PacketDistributor<Entity> TRACKING_ENTITY_AND_SELF;
   public static final PacketDistributor<Chunk> TRACKING_CHUNK;
   public static final PacketDistributor<List<NetworkManager>> NMLIST;
   private final BiFunction<PacketDistributor<T>, Supplier<T>, Consumer<IPacket<?>>> functor;
   private final NetworkDirection direction;

   public PacketDistributor(BiFunction<PacketDistributor<T>, Supplier<T>, Consumer<IPacket<?>>> functor, NetworkDirection direction) {
      this.functor = functor;
      this.direction = direction;
   }

   public PacketDistributor.PacketTarget with(Supplier<T> input) {
      return new PacketDistributor.PacketTarget((Consumer)this.functor.apply(this, input), this);
   }

   public PacketDistributor.PacketTarget noArg() {
      return new PacketDistributor.PacketTarget((Consumer)this.functor.apply(this, () -> {
         return null;
      }), this);
   }

   private Consumer<IPacket<?>> playerConsumer(Supplier<ServerPlayerEntity> entityPlayerMPSupplier) {
      return (p) -> {
         ((ServerPlayerEntity)entityPlayerMPSupplier.get()).connection.netManager.sendPacket(p);
      };
   }

   private Consumer<IPacket<?>> playerListDimConsumer(Supplier<DimensionType> dimensionTypeSupplier) {
      return (p) -> {
         this.getServer().getPlayerList().sendPacketToAllPlayersInDimension(p, (DimensionType)dimensionTypeSupplier.get());
      };
   }

   private Consumer<IPacket<?>> playerListAll(Supplier<Void> voidSupplier) {
      return (p) -> {
         this.getServer().getPlayerList().sendPacketToAllPlayers(p);
      };
   }

   private Consumer<IPacket<?>> clientToServer(Supplier<Void> voidSupplier) {
      return (p) -> {
         Minecraft.getInstance().getConnection().sendPacket(p);
      };
   }

   private Consumer<IPacket<?>> playerListPointConsumer(Supplier<PacketDistributor.TargetPoint> targetPointSupplier) {
      return (p) -> {
         PacketDistributor.TargetPoint tp = (PacketDistributor.TargetPoint)targetPointSupplier.get();
         this.getServer().getPlayerList().sendToAllNearExcept(tp.excluded, tp.x, tp.y, tp.z, tp.r2, tp.dim, p);
      };
   }

   private Consumer<IPacket<?>> trackingEntity(Supplier<Entity> entitySupplier) {
      return (p) -> {
         Entity entity = (Entity)entitySupplier.get();
         ((ServerChunkProvider)entity.getEntityWorld().getChunkProvider()).sendToAllTracking(entity, p);
      };
   }

   private Consumer<IPacket<?>> trackingEntityAndSelf(Supplier<Entity> entitySupplier) {
      return (p) -> {
         Entity entity = (Entity)entitySupplier.get();
         ((ServerChunkProvider)entity.getEntityWorld().getChunkProvider()).sendToTrackingAndSelf(entity, p);
      };
   }

   private Consumer<IPacket<?>> trackingChunk(Supplier<Chunk> chunkPosSupplier) {
      return (p) -> {
         Chunk chunk = (Chunk)chunkPosSupplier.get();
         ((ServerChunkProvider)chunk.getWorld().getChunkProvider()).chunkManager.getTrackingPlayers(chunk.getPos(), false).forEach((e) -> {
            e.connection.sendPacket(p);
         });
      };
   }

   private Consumer<IPacket<?>> networkManagerList(Supplier<List<NetworkManager>> nmListSupplier) {
      return (p) -> {
         ((List)nmListSupplier.get()).forEach((nm) -> {
            nm.sendPacket(p);
         });
      };
   }

   private MinecraftServer getServer() {
      return (MinecraftServer)LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
   }

   static {
      PLAYER = new PacketDistributor(PacketDistributor::playerConsumer, NetworkDirection.PLAY_TO_CLIENT);
      DIMENSION = new PacketDistributor(PacketDistributor::playerListDimConsumer, NetworkDirection.PLAY_TO_CLIENT);
      NEAR = new PacketDistributor(PacketDistributor::playerListPointConsumer, NetworkDirection.PLAY_TO_CLIENT);
      ALL = new PacketDistributor(PacketDistributor::playerListAll, NetworkDirection.PLAY_TO_CLIENT);
      SERVER = new PacketDistributor(PacketDistributor::clientToServer, NetworkDirection.PLAY_TO_SERVER);
      TRACKING_ENTITY = new PacketDistributor(PacketDistributor::trackingEntity, NetworkDirection.PLAY_TO_CLIENT);
      TRACKING_ENTITY_AND_SELF = new PacketDistributor(PacketDistributor::trackingEntityAndSelf, NetworkDirection.PLAY_TO_CLIENT);
      TRACKING_CHUNK = new PacketDistributor(PacketDistributor::trackingChunk, NetworkDirection.PLAY_TO_CLIENT);
      NMLIST = new PacketDistributor(PacketDistributor::networkManagerList, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static class PacketTarget {
      private final Consumer<IPacket<?>> packetConsumer;
      private final PacketDistributor<?> distributor;

      PacketTarget(Consumer<IPacket<?>> packetConsumer, PacketDistributor<?> distributor) {
         this.packetConsumer = packetConsumer;
         this.distributor = distributor;
      }

      public void send(IPacket<?> packet) {
         this.packetConsumer.accept(packet);
      }

      public NetworkDirection getDirection() {
         return this.distributor.direction;
      }
   }

   public static final class TargetPoint {
      private final ServerPlayerEntity excluded;
      private final double x;
      private final double y;
      private final double z;
      private final double r2;
      private final DimensionType dim;

      public TargetPoint(ServerPlayerEntity excluded, double x, double y, double z, double r2, DimensionType dim) {
         this.excluded = excluded;
         this.x = x;
         this.y = y;
         this.z = z;
         this.r2 = r2;
         this.dim = dim;
      }

      public TargetPoint(double x, double y, double z, double r2, DimensionType dim) {
         this.excluded = null;
         this.x = x;
         this.y = y;
         this.z = z;
         this.r2 = r2;
         this.dim = dim;
      }

      public static Supplier<PacketDistributor.TargetPoint> p(double x, double y, double z, double r2, DimensionType dim) {
         PacketDistributor.TargetPoint tp = new PacketDistributor.TargetPoint(x, y, z, r2, dim);
         return () -> {
            return tp;
         };
      }
   }
}
