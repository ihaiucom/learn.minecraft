package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CResourcePackStatusPacket implements IPacket<IServerPlayNetHandler> {
   private CResourcePackStatusPacket.Action action;

   public CResourcePackStatusPacket() {
   }

   public CResourcePackStatusPacket(CResourcePackStatusPacket.Action p_i47156_1_) {
      this.action = p_i47156_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.action = (CResourcePackStatusPacket.Action)p_148837_1_.readEnumValue(CResourcePackStatusPacket.Action.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.action);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleResourcePackStatus(this);
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;
   }
}
