package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.palette.IPalette;
import net.minecraft.util.palette.PaletteIdentity;
import net.minecraft.util.palette.PalettedContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkSection {
   private static final IPalette<BlockState> REGISTRY_PALETTE;
   private final int yBase;
   private short blockRefCount;
   private short blockTickRefCount;
   private short fluidRefCount;
   private final PalettedContainer<BlockState> data;

   public ChunkSection(int p_i49943_1_) {
      this(p_i49943_1_, (short)0, (short)0, (short)0);
   }

   public ChunkSection(int p_i49944_1_, short p_i49944_2_, short p_i49944_3_, short p_i49944_4_) {
      this.yBase = p_i49944_1_;
      this.blockRefCount = p_i49944_2_;
      this.blockTickRefCount = p_i49944_3_;
      this.fluidRefCount = p_i49944_4_;
      this.data = new PalettedContainer(REGISTRY_PALETTE, Block.BLOCK_STATE_IDS, NBTUtil::readBlockState, NBTUtil::writeBlockState, Blocks.AIR.getDefaultState());
   }

   public BlockState getBlockState(int p_177485_1_, int p_177485_2_, int p_177485_3_) {
      return (BlockState)this.data.get(p_177485_1_, p_177485_2_, p_177485_3_);
   }

   public IFluidState getFluidState(int p_206914_1_, int p_206914_2_, int p_206914_3_) {
      return ((BlockState)this.data.get(p_206914_1_, p_206914_2_, p_206914_3_)).getFluidState();
   }

   public void lock() {
      this.data.lock();
   }

   public void unlock() {
      this.data.unlock();
   }

   public BlockState setBlockState(int p_222629_1_, int p_222629_2_, int p_222629_3_, BlockState p_222629_4_) {
      return this.setBlockState(p_222629_1_, p_222629_2_, p_222629_3_, p_222629_4_, true);
   }

   public BlockState setBlockState(int p_177484_1_, int p_177484_2_, int p_177484_3_, BlockState p_177484_4_, boolean p_177484_5_) {
      BlockState lvt_6_2_;
      if (p_177484_5_) {
         lvt_6_2_ = (BlockState)this.data.func_222641_a(p_177484_1_, p_177484_2_, p_177484_3_, p_177484_4_);
      } else {
         lvt_6_2_ = (BlockState)this.data.func_222639_b(p_177484_1_, p_177484_2_, p_177484_3_, p_177484_4_);
      }

      IFluidState lvt_7_1_ = lvt_6_2_.getFluidState();
      IFluidState lvt_8_1_ = p_177484_4_.getFluidState();
      if (!lvt_6_2_.isAir()) {
         --this.blockRefCount;
         if (lvt_6_2_.ticksRandomly()) {
            --this.blockTickRefCount;
         }
      }

      if (!lvt_7_1_.isEmpty()) {
         --this.fluidRefCount;
      }

      if (!p_177484_4_.isAir()) {
         ++this.blockRefCount;
         if (p_177484_4_.ticksRandomly()) {
            ++this.blockTickRefCount;
         }
      }

      if (!lvt_8_1_.isEmpty()) {
         ++this.fluidRefCount;
      }

      return lvt_6_2_;
   }

   public boolean isEmpty() {
      return this.blockRefCount == 0;
   }

   public static boolean isEmpty(@Nullable ChunkSection p_222628_0_) {
      return p_222628_0_ == Chunk.EMPTY_SECTION || p_222628_0_.isEmpty();
   }

   public boolean needsRandomTickAny() {
      return this.needsRandomTick() || this.needsRandomTickFluid();
   }

   public boolean needsRandomTick() {
      return this.blockTickRefCount > 0;
   }

   public boolean needsRandomTickFluid() {
      return this.fluidRefCount > 0;
   }

   public int getYLocation() {
      return this.yBase;
   }

   public void recalculateRefCounts() {
      this.blockRefCount = 0;
      this.blockTickRefCount = 0;
      this.fluidRefCount = 0;
      this.data.func_225497_a((p_225496_1_, p_225496_2_) -> {
         IFluidState lvt_3_1_ = p_225496_1_.getFluidState();
         if (!p_225496_1_.isAir()) {
            this.blockRefCount = (short)(this.blockRefCount + p_225496_2_);
            if (p_225496_1_.ticksRandomly()) {
               this.blockTickRefCount = (short)(this.blockTickRefCount + p_225496_2_);
            }
         }

         if (!lvt_3_1_.isEmpty()) {
            this.blockRefCount = (short)(this.blockRefCount + p_225496_2_);
            if (lvt_3_1_.ticksRandomly()) {
               this.fluidRefCount = (short)(this.fluidRefCount + p_225496_2_);
            }
         }

      });
   }

   public PalettedContainer<BlockState> getData() {
      return this.data;
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_222634_1_) {
      this.blockRefCount = p_222634_1_.readShort();
      this.data.read(p_222634_1_);
   }

   public void write(PacketBuffer p_222630_1_) {
      p_222630_1_.writeShort(this.blockRefCount);
      this.data.write(p_222630_1_);
   }

   public int getSize() {
      return 2 + this.data.getSerializedSize();
   }

   public boolean contains(BlockState p_222636_1_) {
      return this.data.contains(p_222636_1_);
   }

   static {
      REGISTRY_PALETTE = new PaletteIdentity(Block.BLOCK_STATE_IDS, Blocks.AIR.getDefaultState());
   }
}
