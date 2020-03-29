package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SBlockActionPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos blockPosition;
   private int instrument;
   private int pitch;
   private Block block;

   public SBlockActionPacket() {
   }

   public SBlockActionPacket(BlockPos p_i46966_1_, Block p_i46966_2_, int p_i46966_3_, int p_i46966_4_) {
      this.blockPosition = p_i46966_1_;
      this.block = p_i46966_2_;
      this.instrument = p_i46966_3_;
      this.pitch = p_i46966_4_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.blockPosition = p_148837_1_.readBlockPos();
      this.instrument = p_148837_1_.readUnsignedByte();
      this.pitch = p_148837_1_.readUnsignedByte();
      this.block = (Block)Registry.BLOCK.getByValue(p_148837_1_.readVarInt());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.blockPosition);
      p_148840_1_.writeByte(this.instrument);
      p_148840_1_.writeByte(this.pitch);
      p_148840_1_.writeVarInt(Registry.BLOCK.getId(this.block));
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleBlockAction(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   @OnlyIn(Dist.CLIENT)
   public int getData1() {
      return this.instrument;
   }

   @OnlyIn(Dist.CLIENT)
   public int getData2() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public Block getBlockType() {
      return this.block;
   }
}
