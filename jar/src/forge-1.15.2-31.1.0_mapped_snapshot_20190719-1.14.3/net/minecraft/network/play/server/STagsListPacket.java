package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.NetworkTagManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STagsListPacket implements IPacket<IClientPlayNetHandler> {
   private NetworkTagManager tags;

   public STagsListPacket() {
   }

   public STagsListPacket(NetworkTagManager p_i48211_1_) {
      this.tags = p_i48211_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.tags = NetworkTagManager.read(p_148837_1_);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      this.tags.write(p_148840_1_);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleTags(this);
   }

   @OnlyIn(Dist.CLIENT)
   public NetworkTagManager getTags() {
      return this.tags;
   }
}
