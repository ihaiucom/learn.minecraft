package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SConfirmTransactionPacket implements IPacket<IClientPlayNetHandler> {
   private int windowId;
   private short actionNumber;
   private boolean accepted;

   public SConfirmTransactionPacket() {
   }

   public SConfirmTransactionPacket(int p_i46958_1_, short p_i46958_2_, boolean p_i46958_3_) {
      this.windowId = p_i46958_1_;
      this.actionNumber = p_i46958_2_;
      this.accepted = p_i46958_3_;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleConfirmTransaction(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readUnsignedByte();
      this.actionNumber = p_148837_1_.readShort();
      this.accepted = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeShort(this.actionNumber);
      p_148840_1_.writeBoolean(this.accepted);
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @OnlyIn(Dist.CLIENT)
   public short getActionNumber() {
      return this.actionNumber;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean wasAccepted() {
      return this.accepted;
   }
}
