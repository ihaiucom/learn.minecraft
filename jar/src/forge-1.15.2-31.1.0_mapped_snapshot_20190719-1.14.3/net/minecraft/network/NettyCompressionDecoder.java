package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;

public class NettyCompressionDecoder extends ByteToMessageDecoder {
   private final Inflater inflater;
   private int threshold;

   public NettyCompressionDecoder(int p_i46006_1_) {
      this.threshold = p_i46006_1_;
      this.inflater = new Inflater();
   }

   protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_) throws Exception {
      if (p_decode_2_.readableBytes() != 0) {
         PacketBuffer lvt_4_1_ = new PacketBuffer(p_decode_2_);
         int lvt_5_1_ = lvt_4_1_.readVarInt();
         if (lvt_5_1_ == 0) {
            p_decode_3_.add(lvt_4_1_.readBytes(lvt_4_1_.readableBytes()));
         } else {
            if (lvt_5_1_ < this.threshold) {
               throw new DecoderException("Badly compressed packet - size of " + lvt_5_1_ + " is below server threshold of " + this.threshold);
            }

            if (lvt_5_1_ > 2097152) {
               throw new DecoderException("Badly compressed packet - size of " + lvt_5_1_ + " is larger than protocol maximum of " + 2097152);
            }

            byte[] lvt_6_1_ = new byte[lvt_4_1_.readableBytes()];
            lvt_4_1_.readBytes(lvt_6_1_);
            this.inflater.setInput(lvt_6_1_);
            byte[] lvt_7_1_ = new byte[lvt_5_1_];
            this.inflater.inflate(lvt_7_1_);
            p_decode_3_.add(Unpooled.wrappedBuffer(lvt_7_1_));
            this.inflater.reset();
         }

      }
   }

   public void setCompressionThreshold(int p_179303_1_) {
      this.threshold = p_179303_1_;
   }
}
