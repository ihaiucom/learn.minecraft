package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SOpenHorseWindowPacket implements IPacket<IClientPlayNetHandler> {
   private int field_218705_a;
   private int field_218706_b;
   private int field_218707_c;

   public SOpenHorseWindowPacket() {
   }

   public SOpenHorseWindowPacket(int p_i50776_1_, int p_i50776_2_, int p_i50776_3_) {
      this.field_218705_a = p_i50776_1_;
      this.field_218706_b = p_i50776_2_;
      this.field_218707_c = p_i50776_3_;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.func_217271_a(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_218705_a = p_148837_1_.readUnsignedByte();
      this.field_218706_b = p_148837_1_.readVarInt();
      this.field_218707_c = p_148837_1_.readInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.field_218705_a);
      p_148840_1_.writeVarInt(this.field_218706_b);
      p_148840_1_.writeInt(this.field_218707_c);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218704_b() {
      return this.field_218705_a;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218702_c() {
      return this.field_218706_b;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218703_d() {
      return this.field_218707_c;
   }
}
