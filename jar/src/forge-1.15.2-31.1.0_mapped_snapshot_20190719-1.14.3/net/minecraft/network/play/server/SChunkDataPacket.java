package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SChunkDataPacket implements IPacket<IClientPlayNetHandler> {
   private int chunkX;
   private int chunkZ;
   private int availableSections;
   private CompoundNBT heightmapTags;
   @Nullable
   private BiomeContainer field_229738_e_;
   private byte[] buffer;
   private List<CompoundNBT> tileEntityTags;
   private boolean fullChunk;

   public SChunkDataPacket() {
   }

   public SChunkDataPacket(Chunk p_i47124_1_, int p_i47124_2_) {
      ChunkPos lvt_3_1_ = p_i47124_1_.getPos();
      this.chunkX = lvt_3_1_.x;
      this.chunkZ = lvt_3_1_.z;
      this.fullChunk = p_i47124_2_ == 65535;
      this.heightmapTags = new CompoundNBT();
      Iterator var4 = p_i47124_1_.func_217311_f().iterator();

      Entry lvt_5_2_;
      while(var4.hasNext()) {
         lvt_5_2_ = (Entry)var4.next();
         if (((Heightmap.Type)lvt_5_2_.getKey()).func_222681_b()) {
            this.heightmapTags.put(((Heightmap.Type)lvt_5_2_.getKey()).getId(), new LongArrayNBT(((Heightmap)lvt_5_2_.getValue()).getDataArray()));
         }
      }

      if (this.fullChunk) {
         this.field_229738_e_ = p_i47124_1_.func_225549_i_().func_227057_b_();
      }

      this.buffer = new byte[this.func_218709_a(p_i47124_1_, p_i47124_2_)];
      this.availableSections = this.func_218708_a(new PacketBuffer(this.getWriteBuffer()), p_i47124_1_, p_i47124_2_);
      this.tileEntityTags = Lists.newArrayList();
      var4 = p_i47124_1_.getTileEntityMap().entrySet().iterator();

      while(true) {
         TileEntity lvt_7_1_;
         int lvt_8_1_;
         do {
            if (!var4.hasNext()) {
               return;
            }

            lvt_5_2_ = (Entry)var4.next();
            BlockPos lvt_6_1_ = (BlockPos)lvt_5_2_.getKey();
            lvt_7_1_ = (TileEntity)lvt_5_2_.getValue();
            lvt_8_1_ = lvt_6_1_.getY() >> 4;
         } while(!this.isFullChunk() && (p_i47124_2_ & 1 << lvt_8_1_) == 0);

         CompoundNBT lvt_9_1_ = lvt_7_1_.getUpdateTag();
         this.tileEntityTags.add(lvt_9_1_);
      }
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.chunkX = p_148837_1_.readInt();
      this.chunkZ = p_148837_1_.readInt();
      this.fullChunk = p_148837_1_.readBoolean();
      this.availableSections = p_148837_1_.readVarInt();
      this.heightmapTags = p_148837_1_.readCompoundTag();
      if (this.fullChunk) {
         this.field_229738_e_ = new BiomeContainer(p_148837_1_);
      }

      int lvt_2_1_ = p_148837_1_.readVarInt();
      if (lvt_2_1_ > 2097152) {
         throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
      } else {
         this.buffer = new byte[lvt_2_1_];
         p_148837_1_.readBytes(this.buffer);
         int lvt_3_1_ = p_148837_1_.readVarInt();
         this.tileEntityTags = Lists.newArrayList();

         for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_) {
            this.tileEntityTags.add(p_148837_1_.readCompoundTag());
         }

      }
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.chunkX);
      p_148840_1_.writeInt(this.chunkZ);
      p_148840_1_.writeBoolean(this.fullChunk);
      p_148840_1_.writeVarInt(this.availableSections);
      p_148840_1_.writeCompoundTag(this.heightmapTags);
      if (this.field_229738_e_ != null) {
         this.field_229738_e_.func_227056_a_(p_148840_1_);
      }

      p_148840_1_.writeVarInt(this.buffer.length);
      p_148840_1_.writeBytes(this.buffer);
      p_148840_1_.writeVarInt(this.tileEntityTags.size());
      Iterator var2 = this.tileEntityTags.iterator();

      while(var2.hasNext()) {
         CompoundNBT lvt_3_1_ = (CompoundNBT)var2.next();
         p_148840_1_.writeCompoundTag(lvt_3_1_);
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleChunkData(this);
   }

   @OnlyIn(Dist.CLIENT)
   public PacketBuffer getReadBuffer() {
      return new PacketBuffer(Unpooled.wrappedBuffer(this.buffer));
   }

   private ByteBuf getWriteBuffer() {
      ByteBuf lvt_1_1_ = Unpooled.wrappedBuffer(this.buffer);
      lvt_1_1_.writerIndex(0);
      return lvt_1_1_;
   }

   public int func_218708_a(PacketBuffer p_218708_1_, Chunk p_218708_2_, int p_218708_3_) {
      int lvt_4_1_ = 0;
      ChunkSection[] lvt_5_1_ = p_218708_2_.getSections();
      int lvt_6_1_ = 0;

      for(int lvt_7_1_ = lvt_5_1_.length; lvt_6_1_ < lvt_7_1_; ++lvt_6_1_) {
         ChunkSection lvt_8_1_ = lvt_5_1_[lvt_6_1_];
         if (lvt_8_1_ != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !lvt_8_1_.isEmpty()) && (p_218708_3_ & 1 << lvt_6_1_) != 0) {
            lvt_4_1_ |= 1 << lvt_6_1_;
            lvt_8_1_.write(p_218708_1_);
         }
      }

      return lvt_4_1_;
   }

   protected int func_218709_a(Chunk p_218709_1_, int p_218709_2_) {
      int lvt_3_1_ = 0;
      ChunkSection[] lvt_4_1_ = p_218709_1_.getSections();
      int lvt_5_1_ = 0;

      for(int lvt_6_1_ = lvt_4_1_.length; lvt_5_1_ < lvt_6_1_; ++lvt_5_1_) {
         ChunkSection lvt_7_1_ = lvt_4_1_[lvt_5_1_];
         if (lvt_7_1_ != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !lvt_7_1_.isEmpty()) && (p_218709_2_ & 1 << lvt_5_1_) != 0) {
            lvt_3_1_ += lvt_7_1_.getSize();
         }
      }

      return lvt_3_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getChunkX() {
      return this.chunkX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getChunkZ() {
      return this.chunkZ;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAvailableSections() {
      return this.availableSections;
   }

   public boolean isFullChunk() {
      return this.fullChunk;
   }

   @OnlyIn(Dist.CLIENT)
   public CompoundNBT getHeightmapTags() {
      return this.heightmapTags;
   }

   @OnlyIn(Dist.CLIENT)
   public List<CompoundNBT> getTileEntityTags() {
      return this.tileEntityTags;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public BiomeContainer func_229739_i_() {
      return this.field_229738_e_ == null ? null : this.field_229738_e_.func_227057_b_();
   }
}
