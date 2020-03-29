package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SCloseWindowPacket implements IPacket<IClientPlayNetHandler> {
   private int windowId;

   public SCloseWindowPacket() {
   }

   public SCloseWindowPacket(int p_i46957_1_) {
      this.windowId = p_i46957_1_;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCloseWindow(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readUnsignedByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
   }
}
