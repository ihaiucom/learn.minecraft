package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateBeaconPacket implements IPacket<IServerPlayNetHandler> {
   private int primaryEffect;
   private int secondaryEffect;

   public CUpdateBeaconPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateBeaconPacket(int p_i49544_1_, int p_i49544_2_) {
      this.primaryEffect = p_i49544_1_;
      this.secondaryEffect = p_i49544_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.primaryEffect = p_148837_1_.readVarInt();
      this.secondaryEffect = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.primaryEffect);
      p_148840_1_.writeVarInt(this.secondaryEffect);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processUpdateBeacon(this);
   }

   public int getPrimaryEffect() {
      return this.primaryEffect;
   }

   public int getSecondaryEffect() {
      return this.secondaryEffect;
   }
}
