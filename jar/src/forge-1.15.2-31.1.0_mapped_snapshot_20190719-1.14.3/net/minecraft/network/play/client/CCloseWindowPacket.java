package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCloseWindowPacket implements IPacket<IServerPlayNetHandler> {
   private int windowId;

   public CCloseWindowPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CCloseWindowPacket(int p_i46881_1_) {
      this.windowId = p_i46881_1_;
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processCloseWindow(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
   }
}
