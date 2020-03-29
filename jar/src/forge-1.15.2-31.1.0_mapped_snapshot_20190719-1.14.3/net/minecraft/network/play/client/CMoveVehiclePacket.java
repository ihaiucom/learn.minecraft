package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CMoveVehiclePacket implements IPacket<IServerPlayNetHandler> {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;

   public CMoveVehiclePacket() {
   }

   public CMoveVehiclePacket(Entity p_i46874_1_) {
      this.x = p_i46874_1_.func_226277_ct_();
      this.y = p_i46874_1_.func_226278_cu_();
      this.z = p_i46874_1_.func_226281_cx_();
      this.yaw = p_i46874_1_.rotationYaw;
      this.pitch = p_i46874_1_.rotationPitch;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yaw = p_148837_1_.readFloat();
      this.pitch = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeFloat(this.yaw);
      p_148840_1_.writeFloat(this.pitch);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processVehicleMove(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }
}
