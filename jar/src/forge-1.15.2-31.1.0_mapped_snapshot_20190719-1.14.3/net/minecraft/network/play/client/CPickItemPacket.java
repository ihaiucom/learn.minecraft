package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPickItemPacket implements IPacket<IServerPlayNetHandler> {
   private int pickIndex;

   public CPickItemPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPickItemPacket(int p_i49547_1_) {
      this.pickIndex = p_i49547_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.pickIndex = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.pickIndex);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processPickItem(this);
   }

   public int getPickIndex() {
      return this.pickIndex;
   }
}
