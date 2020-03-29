package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CSteerBoatPacket implements IPacket<IServerPlayNetHandler> {
   private boolean left;
   private boolean right;

   public CSteerBoatPacket() {
   }

   public CSteerBoatPacket(boolean p_i46873_1_, boolean p_i46873_2_) {
      this.left = p_i46873_1_;
      this.right = p_i46873_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.left = p_148837_1_.readBoolean();
      this.right = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBoolean(this.left);
      p_148840_1_.writeBoolean(this.right);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processSteerBoat(this);
   }

   public boolean getLeft() {
      return this.left;
   }

   public boolean getRight() {
      return this.right;
   }
}
