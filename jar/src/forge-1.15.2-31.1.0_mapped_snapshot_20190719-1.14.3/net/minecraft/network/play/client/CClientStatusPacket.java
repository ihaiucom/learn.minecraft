package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CClientStatusPacket implements IPacket<IServerPlayNetHandler> {
   private CClientStatusPacket.State status;

   public CClientStatusPacket() {
   }

   public CClientStatusPacket(CClientStatusPacket.State p_i46886_1_) {
      this.status = p_i46886_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.status = (CClientStatusPacket.State)p_148837_1_.readEnumValue(CClientStatusPacket.State.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.status);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processClientStatus(this);
   }

   public CClientStatusPacket.State getStatus() {
      return this.status;
   }

   public static enum State {
      PERFORM_RESPAWN,
      REQUEST_STATS;
   }
}
