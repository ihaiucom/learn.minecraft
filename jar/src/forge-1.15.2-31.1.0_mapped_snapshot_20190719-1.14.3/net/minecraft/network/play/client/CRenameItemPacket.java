package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CRenameItemPacket implements IPacket<IServerPlayNetHandler> {
   private String name;

   public CRenameItemPacket() {
   }

   public CRenameItemPacket(String p_i49546_1_) {
      this.name = p_i49546_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.name = p_148837_1_.readString(32767);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(this.name);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processRenameItem(this);
   }

   public String getName() {
      return this.name;
   }
}
