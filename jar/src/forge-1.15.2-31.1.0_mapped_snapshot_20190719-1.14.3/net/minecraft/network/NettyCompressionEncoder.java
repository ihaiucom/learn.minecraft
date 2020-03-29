package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   private final byte[] buffer = new byte[8192];
   private final Deflater deflater;
   private int threshold;

   public NettyCompressionEncoder(int p_i46005_1_) {
      this.threshold = p_i46005_1_;
      this.deflater = new Deflater();
   }

   protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf p_encode_2_, ByteBuf p_encode_3_) throws Exception {
      int lvt_4_1_ = p_encode_2_.readableBytes();
      PacketBuffer lvt_5_1_ = new PacketBuffer(p_encode_3_);
      if (lvt_4_1_ < this.threshold) {
         lvt_5_1_.writeVarInt(0);
         lvt_5_1_.writeBytes(p_encode_2_);
      } else {
         byte[] lvt_6_1_ = new byte[lvt_4_1_];
         p_encode_2_.readBytes(lvt_6_1_);
         lvt_5_1_.writeVarInt(lvt_6_1_.length);
         this.deflater.setInput(lvt_6_1_, 0, lvt_4_1_);
         this.deflater.finish();

         while(!this.deflater.finished()) {
            int lvt_7_1_ = this.deflater.deflate(this.buffer);
            lvt_5_1_.writeBytes((byte[])this.buffer, 0, lvt_7_1_);
         }

         this.deflater.reset();
      }

   }

   public void setCompressionThreshold(int p_179299_1_) {
      this.threshold = p_179299_1_;
   }

   // $FF: synthetic method
   protected void encode(ChannelHandlerContext p_encode_1_, Object p_encode_2_, ByteBuf p_encode_3_) throws Exception {
      this.encode(p_encode_1_, (ByteBuf)p_encode_2_, p_encode_3_);
   }
}
