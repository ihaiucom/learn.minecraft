package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;

public class CAnimateHandPacket implements IPacket<IServerPlayNetHandler> {
   private Hand hand;

   public CAnimateHandPacket() {
   }

   public CAnimateHandPacket(Hand p_i46860_1_) {
      this.hand = p_i46860_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.hand = (Hand)p_148837_1_.readEnumValue(Hand.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.hand);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAnimation(this);
   }

   public Hand getHand() {
      return this.hand;
   }
}
