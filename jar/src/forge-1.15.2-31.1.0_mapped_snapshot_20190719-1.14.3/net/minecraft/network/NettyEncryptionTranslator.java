package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {
   private final Cipher cipher;
   private byte[] inputBuffer = new byte[0];
   private byte[] outputBuffer = new byte[0];

   protected NettyEncryptionTranslator(Cipher p_i45140_1_) {
      this.cipher = p_i45140_1_;
   }

   private byte[] bufToBytes(ByteBuf p_150502_1_) {
      int lvt_2_1_ = p_150502_1_.readableBytes();
      if (this.inputBuffer.length < lvt_2_1_) {
         this.inputBuffer = new byte[lvt_2_1_];
      }

      p_150502_1_.readBytes(this.inputBuffer, 0, lvt_2_1_);
      return this.inputBuffer;
   }

   protected ByteBuf decipher(ChannelHandlerContext p_150503_1_, ByteBuf p_150503_2_) throws ShortBufferException {
      int lvt_3_1_ = p_150503_2_.readableBytes();
      byte[] lvt_4_1_ = this.bufToBytes(p_150503_2_);
      ByteBuf lvt_5_1_ = p_150503_1_.alloc().heapBuffer(this.cipher.getOutputSize(lvt_3_1_));
      lvt_5_1_.writerIndex(this.cipher.update(lvt_4_1_, 0, lvt_3_1_, lvt_5_1_.array(), lvt_5_1_.arrayOffset()));
      return lvt_5_1_;
   }

   protected void cipher(ByteBuf p_150504_1_, ByteBuf p_150504_2_) throws ShortBufferException {
      int lvt_3_1_ = p_150504_1_.readableBytes();
      byte[] lvt_4_1_ = this.bufToBytes(p_150504_1_);
      int lvt_5_1_ = this.cipher.getOutputSize(lvt_3_1_);
      if (this.outputBuffer.length < lvt_5_1_) {
         this.outputBuffer = new byte[lvt_5_1_];
      }

      p_150504_2_.writeBytes(this.outputBuffer, 0, this.cipher.update(lvt_4_1_, 0, lvt_3_1_, this.outputBuffer));
   }
}
