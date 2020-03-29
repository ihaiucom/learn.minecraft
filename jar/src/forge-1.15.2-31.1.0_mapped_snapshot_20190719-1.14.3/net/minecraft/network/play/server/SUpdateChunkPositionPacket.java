package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateChunkPositionPacket implements IPacket<IClientPlayNetHandler> {
   private int field_218756_a;
   private int field_218757_b;

   public SUpdateChunkPositionPacket() {
   }

   public SUpdateChunkPositionPacket(int p_i50766_1_, int p_i50766_2_) {
      this.field_218756_a = p_i50766_1_;
      this.field_218757_b = p_i50766_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_218756_a = p_148837_1_.readVarInt();
      this.field_218757_b = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_218756_a);
      p_148840_1_.writeVarInt(this.field_218757_b);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.func_217267_a(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218755_b() {
      return this.field_218756_a;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218754_c() {
      return this.field_218757_b;
   }
}
