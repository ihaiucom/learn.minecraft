package net.minecraftforge.fml.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FMLLoginWrapper {
   private static final Logger LOGGER = LogManager.getLogger();
   static final ResourceLocation WRAPPER = new ResourceLocation("fml:loginwrapper");
   private EventNetworkChannel wrapperChannel;

   FMLLoginWrapper() {
      this.wrapperChannel = NetworkRegistry.ChannelBuilder.named(WRAPPER).clientAcceptedVersions((a) -> {
         return true;
      }).serverAcceptedVersions((a) -> {
         return true;
      }).networkProtocolVersion(() -> {
         return "FML2";
      }).eventNetworkChannel();
      this.wrapperChannel.addListener(this::wrapperReceived);
   }

   private <T extends NetworkEvent> void wrapperReceived(T packet) {
      NetworkEvent.Context wrappedContext = (NetworkEvent.Context)packet.getSource().get();
      PacketBuffer payload = packet.getPayload();
      ResourceLocation targetNetworkReceiver = FMLNetworkConstants.FML_HANDSHAKE_RESOURCE;
      PacketBuffer data = null;
      int loginSequence;
      if (payload != null) {
         targetNetworkReceiver = payload.readResourceLocation();
         loginSequence = payload.readVarInt();
         data = new PacketBuffer(payload.readBytes(loginSequence));
      }

      loginSequence = packet.getLoginIndex();
      LOGGER.debug(FMLHandshakeHandler.FMLHSMARKER, "Recieved login wrapper packet event for channel {} with index {}", targetNetworkReceiver, loginSequence);
      NetworkEvent.Context context = new NetworkEvent.Context(wrappedContext.getNetworkManager(), wrappedContext.getDirection(), new PacketDispatcher((rl, buf) -> {
         LOGGER.debug(FMLHandshakeHandler.FMLHSMARKER, "Dispatching wrapped packet reply for channel {} with index {}", rl, loginSequence);
         wrappedContext.getPacketDispatcher().sendPacket(WRAPPER, this.wrapPacket(rl, buf));
      }));
      NetworkEvent.LoginPayloadEvent loginPayloadEvent = new NetworkEvent.LoginPayloadEvent(data, () -> {
         return context;
      }, loginSequence);
      NetworkRegistry.findTarget(targetNetworkReceiver).ifPresent((ni) -> {
         ni.dispatchLoginPacket(loginPayloadEvent);
         wrappedContext.setPacketHandled(context.getPacketHandled());
      });
   }

   private PacketBuffer wrapPacket(ResourceLocation rl, PacketBuffer buf) {
      PacketBuffer pb = new PacketBuffer(Unpooled.buffer(buf.capacity()));
      pb.writeResourceLocation(rl);
      pb.writeVarInt(buf.readableBytes());
      pb.writeBytes((ByteBuf)buf);
      return pb;
   }

   void sendServerToClientLoginPacket(ResourceLocation resourceLocation, PacketBuffer buffer, int index, NetworkManager manager) {
      PacketBuffer pb = this.wrapPacket(resourceLocation, buffer);
      manager.sendPacket(NetworkDirection.LOGIN_TO_CLIENT.buildPacket(Pair.of(pb, index), WRAPPER).getThis());
   }
}
