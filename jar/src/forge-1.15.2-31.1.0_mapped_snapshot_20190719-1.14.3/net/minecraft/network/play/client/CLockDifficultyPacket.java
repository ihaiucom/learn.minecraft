package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CLockDifficultyPacket implements IPacket<IServerPlayNetHandler> {
   private boolean field_218777_a;

   public CLockDifficultyPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CLockDifficultyPacket(boolean p_i50760_1_) {
      this.field_218777_a = p_i50760_1_;
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.func_217261_a(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_218777_a = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBoolean(this.field_218777_a);
   }

   public boolean func_218776_b() {
      return this.field_218777_a;
   }
}
