package net.minecraft.util.math.shapes;

import java.util.BitSet;
import net.minecraft.util.Direction;

public final class BitSetVoxelShapePart extends VoxelShapePart {
   private final BitSet bitSet;
   private int startX;
   private int startY;
   private int startZ;
   private int endX;
   private int endY;
   private int endZ;

   public BitSetVoxelShapePart(int p_i47690_1_, int p_i47690_2_, int p_i47690_3_) {
      this(p_i47690_1_, p_i47690_2_, p_i47690_3_, p_i47690_1_, p_i47690_2_, p_i47690_3_, 0, 0, 0);
   }

   public BitSetVoxelShapePart(int p_i48183_1_, int p_i48183_2_, int p_i48183_3_, int p_i48183_4_, int p_i48183_5_, int p_i48183_6_, int p_i48183_7_, int p_i48183_8_, int p_i48183_9_) {
      super(p_i48183_1_, p_i48183_2_, p_i48183_3_);
      this.bitSet = new BitSet(p_i48183_1_ * p_i48183_2_ * p_i48183_3_);
      this.startX = p_i48183_4_;
      this.startY = p_i48183_5_;
      this.startZ = p_i48183_6_;
      this.endX = p_i48183_7_;
      this.endY = p_i48183_8_;
      this.endZ = p_i48183_9_;
   }

   public BitSetVoxelShapePart(VoxelShapePart p_i47692_1_) {
      super(p_i47692_1_.xSize, p_i47692_1_.ySize, p_i47692_1_.zSize);
      if (p_i47692_1_ instanceof BitSetVoxelShapePart) {
         this.bitSet = (BitSet)((BitSetVoxelShapePart)p_i47692_1_).bitSet.clone();
      } else {
         this.bitSet = new BitSet(this.xSize * this.ySize * this.zSize);

         for(int lvt_2_1_ = 0; lvt_2_1_ < this.xSize; ++lvt_2_1_) {
            for(int lvt_3_1_ = 0; lvt_3_1_ < this.ySize; ++lvt_3_1_) {
               for(int lvt_4_1_ = 0; lvt_4_1_ < this.zSize; ++lvt_4_1_) {
                  if (p_i47692_1_.isFilled(lvt_2_1_, lvt_3_1_, lvt_4_1_)) {
                     this.bitSet.set(this.getIndex(lvt_2_1_, lvt_3_1_, lvt_4_1_));
                  }
               }
            }
         }
      }

      this.startX = p_i47692_1_.getStart(Direction.Axis.X);
      this.startY = p_i47692_1_.getStart(Direction.Axis.Y);
      this.startZ = p_i47692_1_.getStart(Direction.Axis.Z);
      this.endX = p_i47692_1_.getEnd(Direction.Axis.X);
      this.endY = p_i47692_1_.getEnd(Direction.Axis.Y);
      this.endZ = p_i47692_1_.getEnd(Direction.Axis.Z);
   }

   protected int getIndex(int p_197848_1_, int p_197848_2_, int p_197848_3_) {
      return (p_197848_1_ * this.ySize + p_197848_2_) * this.zSize + p_197848_3_;
   }

   public boolean isFilled(int p_197835_1_, int p_197835_2_, int p_197835_3_) {
      return this.bitSet.get(this.getIndex(p_197835_1_, p_197835_2_, p_197835_3_));
   }

   public void setFilled(int p_199625_1_, int p_199625_2_, int p_199625_3_, boolean p_199625_4_, boolean p_199625_5_) {
      this.bitSet.set(this.getIndex(p_199625_1_, p_199625_2_, p_199625_3_), p_199625_5_);
      if (p_199625_4_ && p_199625_5_) {
         this.startX = Math.min(this.startX, p_199625_1_);
         this.startY = Math.min(this.startY, p_199625_2_);
         this.startZ = Math.min(this.startZ, p_199625_3_);
         this.endX = Math.max(this.endX, p_199625_1_ + 1);
         this.endY = Math.max(this.endY, p_199625_2_ + 1);
         this.endZ = Math.max(this.endZ, p_199625_3_ + 1);
      }

   }

   public boolean isEmpty() {
      return this.bitSet.isEmpty();
   }

   public int getStart(Direction.Axis p_199623_1_) {
      return p_199623_1_.getCoordinate(this.startX, this.startY, this.startZ);
   }

   public int getEnd(Direction.Axis p_199624_1_) {
      return p_199624_1_.getCoordinate(this.endX, this.endY, this.endZ);
   }

   protected boolean isZAxisLineFull(int p_197833_1_, int p_197833_2_, int p_197833_3_, int p_197833_4_) {
      if (p_197833_3_ >= 0 && p_197833_4_ >= 0 && p_197833_1_ >= 0) {
         if (p_197833_3_ < this.xSize && p_197833_4_ < this.ySize && p_197833_2_ <= this.zSize) {
            return this.bitSet.nextClearBit(this.getIndex(p_197833_3_, p_197833_4_, p_197833_1_)) >= this.getIndex(p_197833_3_, p_197833_4_, p_197833_2_);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void setZAxisLine(int p_197834_1_, int p_197834_2_, int p_197834_3_, int p_197834_4_, boolean p_197834_5_) {
      this.bitSet.set(this.getIndex(p_197834_3_, p_197834_4_, p_197834_1_), this.getIndex(p_197834_3_, p_197834_4_, p_197834_2_), p_197834_5_);
   }

   static BitSetVoxelShapePart func_197852_a(VoxelShapePart p_197852_0_, VoxelShapePart p_197852_1_, IDoubleListMerger p_197852_2_, IDoubleListMerger p_197852_3_, IDoubleListMerger p_197852_4_, IBooleanFunction p_197852_5_) {
      BitSetVoxelShapePart lvt_6_1_ = new BitSetVoxelShapePart(p_197852_2_.func_212435_a().size() - 1, p_197852_3_.func_212435_a().size() - 1, p_197852_4_.func_212435_a().size() - 1);
      int[] lvt_7_1_ = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      p_197852_2_.forMergedIndexes((p_199628_7_, p_199628_8_, p_199628_9_) -> {
         boolean[] lvt_10_1_ = new boolean[]{false};
         boolean lvt_11_1_ = p_197852_3_.forMergedIndexes((p_199627_10_, p_199627_11_, p_199627_12_) -> {
            boolean[] lvt_13_1_ = new boolean[]{false};
            boolean lvt_14_1_ = p_197852_4_.forMergedIndexes((p_199629_12_, p_199629_13_, p_199629_14_) -> {
               boolean lvt_15_1_ = p_197852_5_.apply(p_197852_0_.contains(p_199628_7_, p_199627_10_, p_199629_12_), p_197852_1_.contains(p_199628_8_, p_199627_11_, p_199629_13_));
               if (lvt_15_1_) {
                  lvt_6_1_.bitSet.set(lvt_6_1_.getIndex(p_199628_9_, p_199627_12_, p_199629_14_));
                  lvt_7_1_[2] = Math.min(lvt_7_1_[2], p_199629_14_);
                  lvt_7_1_[5] = Math.max(lvt_7_1_[5], p_199629_14_);
                  lvt_13_1_[0] = true;
               }

               return true;
            });
            if (lvt_13_1_[0]) {
               lvt_7_1_[1] = Math.min(lvt_7_1_[1], p_199627_12_);
               lvt_7_1_[4] = Math.max(lvt_7_1_[4], p_199627_12_);
               lvt_10_1_[0] = true;
            }

            return lvt_14_1_;
         });
         if (lvt_10_1_[0]) {
            lvt_7_1_[0] = Math.min(lvt_7_1_[0], p_199628_9_);
            lvt_7_1_[3] = Math.max(lvt_7_1_[3], p_199628_9_);
         }

         return lvt_11_1_;
      });
      lvt_6_1_.startX = lvt_7_1_[0];
      lvt_6_1_.startY = lvt_7_1_[1];
      lvt_6_1_.startZ = lvt_7_1_[2];
      lvt_6_1_.endX = lvt_7_1_[3] + 1;
      lvt_6_1_.endY = lvt_7_1_[4] + 1;
      lvt_6_1_.endZ = lvt_7_1_[5] + 1;
      return lvt_6_1_;
   }
}
