package net.minecraftforge.fml.network;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.unsafe.UnsafeHacks;
import org.apache.commons.lang3.tuple.Pair;

public enum NetworkDirection {
   PLAY_TO_SERVER(NetworkEvent.ClientCustomPayloadEvent::new, LogicalSide.CLIENT, CCustomPayloadPacket.class, 1),
   PLAY_TO_CLIENT(NetworkEvent.ServerCustomPayloadEvent::new, LogicalSide.SERVER, SCustomPayloadPlayPacket.class, 0),
   LOGIN_TO_SERVER(NetworkEvent.ClientCustomPayloadLoginEvent::new, LogicalSide.CLIENT, CCustomPayloadLoginPacket.class, 3),
   LOGIN_TO_CLIENT(NetworkEvent.ServerCustomPayloadLoginEvent::new, LogicalSide.SERVER, SCustomPayloadLoginPacket.class, 2);

   private final BiFunction<ICustomPacket<?>, Supplier<NetworkEvent.Context>, NetworkEvent> eventSupplier;
   private final LogicalSide logicalSide;
   private final Class<? extends IPacket> packetClass;
   private final int otherWay;
   private static final Reference2ReferenceArrayMap<Class<? extends IPacket>, NetworkDirection> packetLookup = (Reference2ReferenceArrayMap)Stream.of(values()).collect(Collectors.toMap(NetworkDirection::getPacketClass, Function.identity(), (m1, m2) -> {
      return m1;
   }, Reference2ReferenceArrayMap::new));

   private NetworkDirection(BiFunction<ICustomPacket<?>, Supplier<NetworkEvent.Context>, NetworkEvent> eventSupplier, LogicalSide logicalSide, Class<? extends IPacket> clazz, int i) {
      this.eventSupplier = eventSupplier;
      this.logicalSide = logicalSide;
      this.packetClass = clazz;
      this.otherWay = i;
   }

   private Class<? extends IPacket> getPacketClass() {
      return this.packetClass;
   }

   public static <T extends ICustomPacket<?>> NetworkDirection directionFor(Class<T> customPacket) {
      return (NetworkDirection)packetLookup.get(customPacket);
   }

   public NetworkDirection reply() {
      return values()[this.otherWay];
   }

   public NetworkEvent getEvent(ICustomPacket<?> buffer, Supplier<NetworkEvent.Context> manager) {
      return (NetworkEvent)this.eventSupplier.apply(buffer, manager);
   }

   public LogicalSide getOriginationSide() {
      return this.logicalSide;
   }

   public LogicalSide getReceptionSide() {
      return this.reply().logicalSide;
   }

   public <T extends IPacket<?>> ICustomPacket<T> buildPacket(Pair<PacketBuffer, Integer> packetData, ResourceLocation channelName) {
      ICustomPacket<T> packet = (ICustomPacket)UnsafeHacks.newInstance(this.getPacketClass());
      packet.setName(channelName);
      packet.setData((PacketBuffer)packetData.getLeft());
      packet.setIndex((Integer)packetData.getRight());
      return packet;
   }
}
