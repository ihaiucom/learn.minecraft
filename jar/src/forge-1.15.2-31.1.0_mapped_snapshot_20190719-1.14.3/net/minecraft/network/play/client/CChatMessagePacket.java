package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CChatMessagePacket implements IPacket<IServerPlayNetHandler> {
   private String message;

   public CChatMessagePacket() {
   }

   public CChatMessagePacket(String p_i46887_1_) {
      if (p_i46887_1_.length() > 256) {
         p_i46887_1_ = p_i46887_1_.substring(0, 256);
      }

      this.message = p_i46887_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.message = p_148837_1_.readString(256);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(this.message);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processChatMessage(this);
   }

   public String getMessage() {
      return this.message;
   }
}
