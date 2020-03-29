package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CKeepAlivePacket implements IPacket<IServerPlayNetHandler> {
   private long key;

   public CKeepAlivePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CKeepAlivePacket(long p_i46876_1_) {
      this.key = p_i46876_1_;
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processKeepAlive(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.key = p_148837_1_.readLong();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeLong(this.key);
   }

   public long getKey() {
      return this.key;
   }
}
