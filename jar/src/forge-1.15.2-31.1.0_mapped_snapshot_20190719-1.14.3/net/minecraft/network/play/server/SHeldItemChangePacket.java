package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SHeldItemChangePacket implements IPacket<IClientPlayNetHandler> {
   private int heldItemHotbarIndex;

   public SHeldItemChangePacket() {
   }

   public SHeldItemChangePacket(int p_i46919_1_) {
      this.heldItemHotbarIndex = p_i46919_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.heldItemHotbarIndex = p_148837_1_.readByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.heldItemHotbarIndex);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleHeldItemChange(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getHeldItemHotbarIndex() {
      return this.heldItemHotbarIndex;
   }
}
