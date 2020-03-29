package net.minecraftforge.fml.network;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

public class PacketDispatcher {
   BiConsumer<ResourceLocation, PacketBuffer> packetSink;

   PacketDispatcher(BiConsumer<ResourceLocation, PacketBuffer> packetSink) {
      this.packetSink = packetSink;
   }

   private PacketDispatcher() {
   }

   public void sendPacket(ResourceLocation resourceLocation, PacketBuffer buffer) {
      this.packetSink.accept(resourceLocation, buffer);
   }

   // $FF: synthetic method
   PacketDispatcher(Object x0) {
      this();
   }

   static class NetworkManagerDispatcher extends PacketDispatcher {
      private final NetworkManager manager;
      private final int packetIndex;
      private final BiFunction<Pair<PacketBuffer, Integer>, ResourceLocation, ICustomPacket<?>> customPacketSupplier;

      NetworkManagerDispatcher(NetworkManager manager, int packetIndex, BiFunction<Pair<PacketBuffer, Integer>, ResourceLocation, ICustomPacket<?>> customPacketSupplier) {
         super((<undefinedtype>)null);
         this.packetSink = this::dispatchPacket;
         this.manager = manager;
         this.packetIndex = packetIndex;
         this.customPacketSupplier = customPacketSupplier;
      }

      private void dispatchPacket(ResourceLocation resourceLocation, PacketBuffer buffer) {
         ICustomPacket<?> packet = (ICustomPacket)this.customPacketSupplier.apply(Pair.of(buffer, this.packetIndex), resourceLocation);
         this.manager.sendPacket(packet.getThis());
      }
   }
}
