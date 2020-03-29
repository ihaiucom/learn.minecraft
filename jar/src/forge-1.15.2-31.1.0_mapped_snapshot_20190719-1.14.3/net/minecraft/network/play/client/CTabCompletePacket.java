package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CTabCompletePacket implements IPacket<IServerPlayNetHandler> {
   private int transactionId;
   private String command;

   public CTabCompletePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CTabCompletePacket(int p_i47928_1_, String p_i47928_2_) {
      this.transactionId = p_i47928_1_;
      this.command = p_i47928_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.transactionId = p_148837_1_.readVarInt();
      this.command = p_148837_1_.readString(32500);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.transactionId);
      p_148840_1_.writeString(this.command, 32500);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processTabComplete(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public String getCommand() {
      return this.command;
   }
}
