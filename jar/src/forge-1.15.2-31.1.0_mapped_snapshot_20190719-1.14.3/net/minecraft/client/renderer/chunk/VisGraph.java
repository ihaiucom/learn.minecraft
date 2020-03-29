package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VisGraph {
   private static final int DX = (int)Math.pow(16.0D, 0.0D);
   private static final int DZ = (int)Math.pow(16.0D, 1.0D);
   private static final int DY = (int)Math.pow(16.0D, 2.0D);
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BitSet bitSet = new BitSet(4096);
   private static final int[] INDEX_OF_EDGES = (int[])Util.make(new int[1352], (p_209264_0_) -> {
      int lvt_1_1_ = false;
      int lvt_2_1_ = true;
      int lvt_3_1_ = 0;

      for(int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_) {
         for(int lvt_5_1_ = 0; lvt_5_1_ < 16; ++lvt_5_1_) {
            for(int lvt_6_1_ = 0; lvt_6_1_ < 16; ++lvt_6_1_) {
               if (lvt_4_1_ == 0 || lvt_4_1_ == 15 || lvt_5_1_ == 0 || lvt_5_1_ == 15 || lvt_6_1_ == 0 || lvt_6_1_ == 15) {
                  p_209264_0_[lvt_3_1_++] = getIndex(lvt_4_1_, lvt_5_1_, lvt_6_1_);
               }
            }
         }
      }

   });
   private int empty = 4096;

   public void setOpaqueCube(BlockPos p_178606_1_) {
      this.bitSet.set(getIndex(p_178606_1_), true);
      --this.empty;
   }

   private static int getIndex(BlockPos p_178608_0_) {
      return getIndex(p_178608_0_.getX() & 15, p_178608_0_.getY() & 15, p_178608_0_.getZ() & 15);
   }

   private static int getIndex(int p_178605_0_, int p_178605_1_, int p_178605_2_) {
      return p_178605_0_ << 0 | p_178605_1_ << 8 | p_178605_2_ << 4;
   }

   public SetVisibility computeVisibility() {
      SetVisibility lvt_1_1_ = new SetVisibility();
      if (4096 - this.empty < 256) {
         lvt_1_1_.setAllVisible(true);
      } else if (this.empty == 0) {
         lvt_1_1_.setAllVisible(false);
      } else {
         int[] var2 = INDEX_OF_EDGES;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int lvt_5_1_ = var2[var4];
            if (!this.bitSet.get(lvt_5_1_)) {
               lvt_1_1_.setManyVisible(this.floodFill(lvt_5_1_));
            }
         }
      }

      return lvt_1_1_;
   }

   public Set<Direction> getVisibleFacings(BlockPos p_178609_1_) {
      return this.floodFill(getIndex(p_178609_1_));
   }

   private Set<Direction> floodFill(int p_178604_1_) {
      Set<Direction> lvt_2_1_ = EnumSet.noneOf(Direction.class);
      IntPriorityQueue lvt_3_1_ = new IntArrayFIFOQueue();
      lvt_3_1_.enqueue(p_178604_1_);
      this.bitSet.set(p_178604_1_, true);

      while(!lvt_3_1_.isEmpty()) {
         int lvt_4_1_ = lvt_3_1_.dequeueInt();
         this.addEdges(lvt_4_1_, lvt_2_1_);
         Direction[] var5 = DIRECTIONS;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction lvt_8_1_ = var5[var7];
            int lvt_9_1_ = this.getNeighborIndexAtFace(lvt_4_1_, lvt_8_1_);
            if (lvt_9_1_ >= 0 && !this.bitSet.get(lvt_9_1_)) {
               this.bitSet.set(lvt_9_1_, true);
               lvt_3_1_.enqueue(lvt_9_1_);
            }
         }
      }

      return lvt_2_1_;
   }

   private void addEdges(int p_178610_1_, Set<Direction> p_178610_2_) {
      int lvt_3_1_ = p_178610_1_ >> 0 & 15;
      if (lvt_3_1_ == 0) {
         p_178610_2_.add(Direction.WEST);
      } else if (lvt_3_1_ == 15) {
         p_178610_2_.add(Direction.EAST);
      }

      int lvt_4_1_ = p_178610_1_ >> 8 & 15;
      if (lvt_4_1_ == 0) {
         p_178610_2_.add(Direction.DOWN);
      } else if (lvt_4_1_ == 15) {
         p_178610_2_.add(Direction.UP);
      }

      int lvt_5_1_ = p_178610_1_ >> 4 & 15;
      if (lvt_5_1_ == 0) {
         p_178610_2_.add(Direction.NORTH);
      } else if (lvt_5_1_ == 15) {
         p_178610_2_.add(Direction.SOUTH);
      }

   }

   private int getNeighborIndexAtFace(int p_178603_1_, Direction p_178603_2_) {
      switch(p_178603_2_) {
      case DOWN:
         if ((p_178603_1_ >> 8 & 15) == 0) {
            return -1;
         }

         return p_178603_1_ - DY;
      case UP:
         if ((p_178603_1_ >> 8 & 15) == 15) {
            return -1;
         }

         return p_178603_1_ + DY;
      case NORTH:
         if ((p_178603_1_ >> 4 & 15) == 0) {
            return -1;
         }

         return p_178603_1_ - DZ;
      case SOUTH:
         if ((p_178603_1_ >> 4 & 15) == 15) {
            return -1;
         }

         return p_178603_1_ + DZ;
      case WEST:
         if ((p_178603_1_ >> 0 & 15) == 0) {
            return -1;
         }

         return p_178603_1_ - DX;
      case EAST:
         if ((p_178603_1_ >> 0 & 15) == 15) {
            return -1;
         }

         return p_178603_1_ + DX;
      default:
         return -1;
      }
   }
}
