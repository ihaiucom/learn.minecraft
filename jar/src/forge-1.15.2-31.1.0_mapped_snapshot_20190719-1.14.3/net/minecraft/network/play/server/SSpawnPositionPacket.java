package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnPositionPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos spawnBlockPos;

   public SSpawnPositionPacket() {
   }

   public SSpawnPositionPacket(BlockPos p_i46903_1_) {
      this.spawnBlockPos = p_i46903_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.spawnBlockPos = p_148837_1_.readBlockPos();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.spawnBlockPos);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSpawnPosition(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getSpawnPos() {
      return this.spawnBlockPos;
   }
}
