package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CConfirmTeleportPacket implements IPacket<IServerPlayNetHandler> {
   private int telportId;

   public CConfirmTeleportPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CConfirmTeleportPacket(int p_i46889_1_) {
      this.telportId = p_i46889_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.telportId = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.telportId);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processConfirmTeleport(this);
   }

   public int getTeleportId() {
      return this.telportId;
   }
}
