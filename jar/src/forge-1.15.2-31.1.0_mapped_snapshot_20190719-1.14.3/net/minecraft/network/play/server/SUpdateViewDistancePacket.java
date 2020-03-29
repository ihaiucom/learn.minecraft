package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateViewDistancePacket implements IPacket<IClientPlayNetHandler> {
   private int field_218759_a;

   public SUpdateViewDistancePacket() {
   }

   public SUpdateViewDistancePacket(int p_i50765_1_) {
      this.field_218759_a = p_i50765_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_218759_a = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_218759_a);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.func_217270_a(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218758_b() {
      return this.field_218759_a;
   }
}
