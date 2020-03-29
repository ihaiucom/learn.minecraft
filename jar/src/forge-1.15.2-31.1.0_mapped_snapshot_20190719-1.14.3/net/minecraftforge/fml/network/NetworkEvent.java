package net.minecraftforge.fml.network;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSidedProvider;

public class NetworkEvent extends Event {
   private final PacketBuffer payload;
   private final Supplier<NetworkEvent.Context> source;
   private final int loginIndex;

   private NetworkEvent(ICustomPacket<?> payload, Supplier<NetworkEvent.Context> source) {
      this.payload = payload.getInternalData();
      this.source = source;
      this.loginIndex = payload.getIndex();
   }

   private NetworkEvent(PacketBuffer payload, Supplier<NetworkEvent.Context> source, int loginIndex) {
      this.payload = payload;
      this.source = source;
      this.loginIndex = loginIndex;
   }

   public NetworkEvent(Supplier<NetworkEvent.Context> source) {
      this.source = source;
      this.payload = null;
      this.loginIndex = -1;
   }

   public PacketBuffer getPayload() {
      return this.payload;
   }

   public Supplier<NetworkEvent.Context> getSource() {
      return this.source;
   }

   public int getLoginIndex() {
      return this.loginIndex;
   }

   // $FF: synthetic method
   NetworkEvent(ICustomPacket x0, Supplier x1, Object x2) {
      this(x0, x1);
   }

   // $FF: synthetic method
   NetworkEvent(PacketBuffer x0, Supplier x1, int x2, Object x3) {
      this(x0, x1, x2);
   }

   public static class Context {
      private final NetworkManager networkManager;
      private final NetworkDirection networkDirection;
      private final PacketDispatcher packetDispatcher;
      private boolean packetHandled;

      Context(NetworkManager netHandler, NetworkDirection networkDirection, int index) {
         NetworkDirection var10007 = networkDirection.reply();
         var10007.getClass();
         this(netHandler, networkDirection, new PacketDispatcher.NetworkManagerDispatcher(netHandler, index, var10007::buildPacket));
      }

      Context(NetworkManager networkManager, NetworkDirection networkDirection, PacketDispatcher dispatcher) {
         this.networkManager = networkManager;
         this.networkDirection = networkDirection;
         this.packetDispatcher = dispatcher;
      }

      public NetworkDirection getDirection() {
         return this.networkDirection;
      }

      public PacketDispatcher getPacketDispatcher() {
         return this.packetDispatcher;
      }

      public <T> Attribute<T> attr(AttributeKey<T> key) {
         return this.networkManager.channel().attr(key);
      }

      public void setPacketHandled(boolean packetHandled) {
         this.packetHandled = packetHandled;
      }

      public boolean getPacketHandled() {
         return this.packetHandled;
      }

      public CompletableFuture<Void> enqueueWork(Runnable runnable) {
         ThreadTaskExecutor<?> executor = (ThreadTaskExecutor)LogicalSidedProvider.WORKQUEUE.get(this.getDirection().getReceptionSide());
         if (!executor.isOnExecutionThread()) {
            return executor.deferTask(runnable);
         } else {
            runnable.run();
            return CompletableFuture.completedFuture((Object)null);
         }
      }

      @Nullable
      public ServerPlayerEntity getSender() {
         INetHandler netHandler = this.networkManager.getNetHandler();
         if (netHandler instanceof ServerPlayNetHandler) {
            ServerPlayNetHandler netHandlerPlayServer = (ServerPlayNetHandler)netHandler;
            return netHandlerPlayServer.player;
         } else {
            return null;
         }
      }

      public NetworkManager getNetworkManager() {
         return this.networkManager;
      }
   }

   public static class ChannelRegistrationChangeEvent extends NetworkEvent {
      private final NetworkEvent.RegistrationChangeType changeType;

      ChannelRegistrationChangeEvent(Supplier<NetworkEvent.Context> source, NetworkEvent.RegistrationChangeType changeType) {
         super(source);
         this.changeType = changeType;
      }

      public NetworkEvent.RegistrationChangeType getRegistrationChangeType() {
         return this.changeType;
      }
   }

   public static enum RegistrationChangeType {
      REGISTER,
      UNREGISTER;
   }

   public static class LoginPayloadEvent extends NetworkEvent {
      LoginPayloadEvent(PacketBuffer payload, Supplier<NetworkEvent.Context> source, int loginIndex) {
         super(payload, source, loginIndex, null);
      }
   }

   public static class GatherLoginPayloadsEvent extends Event {
      private final List<NetworkRegistry.LoginPayload> collected;
      private final boolean isLocal;

      public GatherLoginPayloadsEvent(List<NetworkRegistry.LoginPayload> loginPayloadList, boolean isLocal) {
         this.collected = loginPayloadList;
         this.isLocal = isLocal;
      }

      public void add(PacketBuffer buffer, ResourceLocation channelName, String context) {
         this.collected.add(new NetworkRegistry.LoginPayload(buffer, channelName, context));
      }

      public boolean isLocal() {
         return this.isLocal;
      }
   }

   public static class ClientCustomPayloadLoginEvent extends NetworkEvent.ClientCustomPayloadEvent {
      ClientCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<NetworkEvent.Context> source) {
         super(payload, source);
      }
   }

   public static class ServerCustomPayloadLoginEvent extends NetworkEvent.ServerCustomPayloadEvent {
      ServerCustomPayloadLoginEvent(ICustomPacket<?> payload, Supplier<NetworkEvent.Context> source) {
         super(payload, source);
      }
   }

   public static class ClientCustomPayloadEvent extends NetworkEvent {
      ClientCustomPayloadEvent(ICustomPacket<?> payload, Supplier<NetworkEvent.Context> source) {
         super(payload, source, null);
      }
   }

   public static class ServerCustomPayloadEvent extends NetworkEvent {
      ServerCustomPayloadEvent(ICustomPacket<?> payload, Supplier<NetworkEvent.Context> source) {
         super(payload, source, null);
      }
   }
}
