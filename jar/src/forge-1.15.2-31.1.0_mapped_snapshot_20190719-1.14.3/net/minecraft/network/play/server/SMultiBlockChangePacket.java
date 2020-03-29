package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SMultiBlockChangePacket implements IPacket<IClientPlayNetHandler> {
   private ChunkPos chunkPos;
   private SMultiBlockChangePacket.UpdateData[] changedBlocks;

   public SMultiBlockChangePacket() {
   }

   public SMultiBlockChangePacket(int p_i46959_1_, short[] p_i46959_2_, Chunk p_i46959_3_) {
      this.chunkPos = p_i46959_3_.getPos();
      this.changedBlocks = new SMultiBlockChangePacket.UpdateData[p_i46959_1_];

      for(int lvt_4_1_ = 0; lvt_4_1_ < this.changedBlocks.length; ++lvt_4_1_) {
         this.changedBlocks[lvt_4_1_] = new SMultiBlockChangePacket.UpdateData(p_i46959_2_[lvt_4_1_], p_i46959_3_);
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.chunkPos = new ChunkPos(p_148837_1_.readInt(), p_148837_1_.readInt());
      this.changedBlocks = new SMultiBlockChangePacket.UpdateData[p_148837_1_.readVarInt()];

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.changedBlocks.length; ++lvt_2_1_) {
         this.changedBlocks[lvt_2_1_] = new SMultiBlockChangePacket.UpdateData(p_148837_1_.readShort(), (BlockState)Block.BLOCK_STATE_IDS.getByValue(p_148837_1_.readVarInt()));
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.chunkPos.x);
      p_148840_1_.writeInt(this.chunkPos.z);
      p_148840_1_.writeVarInt(this.changedBlocks.length);
      SMultiBlockChangePacket.UpdateData[] var2 = this.changedBlocks;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SMultiBlockChangePacket.UpdateData lvt_5_1_ = var2[var4];
         p_148840_1_.writeShort(lvt_5_1_.getOffset());
         p_148840_1_.writeVarInt(Block.getStateId(lvt_5_1_.getBlockState()));
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleMultiBlockChange(this);
   }

   @OnlyIn(Dist.CLIENT)
   public SMultiBlockChangePacket.UpdateData[] getChangedBlocks() {
      return this.changedBlocks;
   }

   public class UpdateData {
      private final short offset;
      private final BlockState blockState;

      public UpdateData(short p_i46544_2_, BlockState p_i46544_3_) {
         this.offset = p_i46544_2_;
         this.blockState = p_i46544_3_;
      }

      public UpdateData(short p_i46545_2_, Chunk p_i46545_3_) {
         this.offset = p_i46545_2_;
         this.blockState = p_i46545_3_.getBlockState(this.getPos());
      }

      public BlockPos getPos() {
         return new BlockPos(SMultiBlockChangePacket.this.chunkPos.getBlock(this.offset >> 12 & 15, this.offset & 255, this.offset >> 8 & 15));
      }

      public short getOffset() {
         return this.offset;
      }

      public BlockState getBlockState() {
         return this.blockState;
      }
   }
}
