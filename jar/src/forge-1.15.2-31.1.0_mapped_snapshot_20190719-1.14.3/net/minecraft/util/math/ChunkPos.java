package net.minecraft.util.math;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class ChunkPos {
   public static final long SENTINEL = asLong(1875016, 1875016);
   public final int x;
   public final int z;

   public ChunkPos(int p_i1947_1_, int p_i1947_2_) {
      this.x = p_i1947_1_;
      this.z = p_i1947_2_;
   }

   public ChunkPos(BlockPos p_i46717_1_) {
      this.x = p_i46717_1_.getX() >> 4;
      this.z = p_i46717_1_.getZ() >> 4;
   }

   public ChunkPos(long p_i48713_1_) {
      this.x = (int)p_i48713_1_;
      this.z = (int)(p_i48713_1_ >> 32);
   }

   public long asLong() {
      return asLong(this.x, this.z);
   }

   public static long asLong(int p_77272_0_, int p_77272_1_) {
      return (long)p_77272_0_ & 4294967295L | ((long)p_77272_1_ & 4294967295L) << 32;
   }

   public static int getX(long p_212578_0_) {
      return (int)(p_212578_0_ & 4294967295L);
   }

   public static int getZ(long p_212579_0_) {
      return (int)(p_212579_0_ >>> 32 & 4294967295L);
   }

   public int hashCode() {
      int lvt_1_1_ = 1664525 * this.x + 1013904223;
      int lvt_2_1_ = 1664525 * (this.z ^ -559038737) + 1013904223;
      return lvt_1_1_ ^ lvt_2_1_;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ChunkPos)) {
         return false;
      } else {
         ChunkPos lvt_2_1_ = (ChunkPos)p_equals_1_;
         return this.x == lvt_2_1_.x && this.z == lvt_2_1_.z;
      }
   }

   public int getXStart() {
      return this.x << 4;
   }

   public int getZStart() {
      return this.z << 4;
   }

   public int getXEnd() {
      return (this.x << 4) + 15;
   }

   public int getZEnd() {
      return (this.z << 4) + 15;
   }

   public int getRegionCoordX() {
      return this.x >> 5;
   }

   public int getRegionCoordZ() {
      return this.z >> 5;
   }

   public int getRegionPositionX() {
      return this.x & 31;
   }

   public int getRegionPositionZ() {
      return this.z & 31;
   }

   public BlockPos getBlock(int p_180331_1_, int p_180331_2_, int p_180331_3_) {
      return new BlockPos((this.x << 4) + p_180331_1_, p_180331_2_, (this.z << 4) + p_180331_3_);
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.x << 4, 0, this.z << 4);
   }

   public int func_226661_a_(ChunkPos p_226661_1_) {
      return Math.max(Math.abs(this.x - p_226661_1_.x), Math.abs(this.z - p_226661_1_.z));
   }

   public static Stream<ChunkPos> getAllInBox(ChunkPos p_222243_0_, int p_222243_1_) {
      return getAllInBox(new ChunkPos(p_222243_0_.x - p_222243_1_, p_222243_0_.z - p_222243_1_), new ChunkPos(p_222243_0_.x + p_222243_1_, p_222243_0_.z + p_222243_1_));
   }

   public static Stream<ChunkPos> getAllInBox(final ChunkPos p_222239_0_, final ChunkPos p_222239_1_) {
      int lvt_2_1_ = Math.abs(p_222239_0_.x - p_222239_1_.x) + 1;
      int lvt_3_1_ = Math.abs(p_222239_0_.z - p_222239_1_.z) + 1;
      final int lvt_4_1_ = p_222239_0_.x < p_222239_1_.x ? 1 : -1;
      final int lvt_5_1_ = p_222239_0_.z < p_222239_1_.z ? 1 : -1;
      return StreamSupport.stream(new AbstractSpliterator<ChunkPos>((long)(lvt_2_1_ * lvt_3_1_), 64) {
         @Nullable
         private ChunkPos field_222237_e;

         public boolean tryAdvance(Consumer<? super ChunkPos> p_tryAdvance_1_) {
            if (this.field_222237_e == null) {
               this.field_222237_e = p_222239_0_;
            } else {
               int lvt_2_1_ = this.field_222237_e.x;
               int lvt_3_1_ = this.field_222237_e.z;
               if (lvt_2_1_ == p_222239_1_.x) {
                  if (lvt_3_1_ == p_222239_1_.z) {
                     return false;
                  }

                  this.field_222237_e = new ChunkPos(p_222239_0_.x, lvt_3_1_ + lvt_5_1_);
               } else {
                  this.field_222237_e = new ChunkPos(lvt_2_1_ + lvt_4_1_, lvt_3_1_);
               }
            }

            p_tryAdvance_1_.accept(this.field_222237_e);
            return true;
         }
      }, false);
   }
}
