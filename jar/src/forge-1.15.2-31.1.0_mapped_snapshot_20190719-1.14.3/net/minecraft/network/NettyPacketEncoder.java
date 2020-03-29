package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NettyPacketEncoder extends MessageToByteEncoder<IPacket<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker RECEIVED_PACKET_MARKER;
   private final PacketDirection direction;

   public NettyPacketEncoder(PacketDirection p_i45998_1_) {
      this.direction = p_i45998_1_;
   }

   protected void encode(ChannelHandlerContext p_encode_1_, IPacket<?> p_encode_2_, ByteBuf p_encode_3_) throws Exception {
      ProtocolType lvt_4_1_ = (ProtocolType)p_encode_1_.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get();
      if (lvt_4_1_ == null) {
         throw new RuntimeException("ConnectionProtocol unknown: " + p_encode_2_);
      } else {
         Integer lvt_5_1_ = lvt_4_1_.getPacketId(this.direction, p_encode_2_);
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(RECEIVED_PACKET_MARKER, "OUT: [{}:{}] {}", p_encode_1_.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get(), lvt_5_1_, p_encode_2_.getClass().getName());
         }

         if (lvt_5_1_ == null) {
            throw new IOException("Can't serialize unregistered packet");
         } else {
            PacketBuffer lvt_6_1_ = new PacketBuffer(p_encode_3_);
            lvt_6_1_.writeVarInt(lvt_5_1_);

            try {
               p_encode_2_.writePacketData(lvt_6_1_);
            } catch (Throwable var8) {
               LOGGER.error(var8);
               if (p_encode_2_.shouldSkipErrors()) {
                  throw new SkipableEncoderException(var8);
               } else {
                  throw var8;
               }
            }
         }
      }
   }

   // $FF: synthetic method
   protected void encode(ChannelHandlerContext p_encode_1_, Object p_encode_2_, ByteBuf p_encode_3_) throws Exception {
      this.encode(p_encode_1_, (IPacket)p_encode_2_, p_encode_3_);
   }

   static {
      RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_SENT", NetworkManager.NETWORK_PACKETS_MARKER);
   }
}
