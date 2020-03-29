package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SOpenSignMenuPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos signPosition;

   public SOpenSignMenuPacket() {
   }

   public SOpenSignMenuPacket(BlockPos p_i46934_1_) {
      this.signPosition = p_i46934_1_;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSignEditorOpen(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.signPosition = p_148837_1_.readBlockPos();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.signPosition);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getSignPosition() {
      return this.signPosition;
   }
}
