package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CInputPacket implements IPacket<IServerPlayNetHandler> {
   private float strafeSpeed;
   private float forwardSpeed;
   private boolean jumping;
   private boolean field_229754_d_;

   public CInputPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CInputPacket(float p_i46868_1_, float p_i46868_2_, boolean p_i46868_3_, boolean p_i46868_4_) {
      this.strafeSpeed = p_i46868_1_;
      this.forwardSpeed = p_i46868_2_;
      this.jumping = p_i46868_3_;
      this.field_229754_d_ = p_i46868_4_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.strafeSpeed = p_148837_1_.readFloat();
      this.forwardSpeed = p_148837_1_.readFloat();
      byte lvt_2_1_ = p_148837_1_.readByte();
      this.jumping = (lvt_2_1_ & 1) > 0;
      this.field_229754_d_ = (lvt_2_1_ & 2) > 0;
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat(this.strafeSpeed);
      p_148840_1_.writeFloat(this.forwardSpeed);
      byte lvt_2_1_ = 0;
      if (this.jumping) {
         lvt_2_1_ = (byte)(lvt_2_1_ | 1);
      }

      if (this.field_229754_d_) {
         lvt_2_1_ = (byte)(lvt_2_1_ | 2);
      }

      p_148840_1_.writeByte(lvt_2_1_);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processInput(this);
   }

   public float getStrafeSpeed() {
      return this.strafeSpeed;
   }

   public float getForwardSpeed() {
      return this.forwardSpeed;
   }

   public boolean isJumping() {
      return this.jumping;
   }

   public boolean func_229755_e_() {
      return this.field_229754_d_;
   }
}
