package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.world.Difficulty;

public class CSetDifficultyPacket implements IPacket<IServerPlayNetHandler> {
   private Difficulty field_218774_a;

   public CSetDifficultyPacket() {
   }

   public CSetDifficultyPacket(Difficulty p_i50762_1_) {
      this.field_218774_a = p_i50762_1_;
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.func_217263_a(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_218774_a = Difficulty.byId(p_148837_1_.readUnsignedByte());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.field_218774_a.getId());
   }

   public Difficulty func_218773_b() {
      return this.field_218774_a;
   }
}
