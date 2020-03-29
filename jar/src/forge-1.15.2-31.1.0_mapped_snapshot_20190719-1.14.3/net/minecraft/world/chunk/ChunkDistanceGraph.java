package net.minecraft.world.chunk;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.lighting.LevelBasedGraph;

public abstract class ChunkDistanceGraph extends LevelBasedGraph {
   protected ChunkDistanceGraph(int p_i50712_1_, int p_i50712_2_, int p_i50712_3_) {
      super(p_i50712_1_, p_i50712_2_, p_i50712_3_);
   }

   protected boolean isRoot(long p_215485_1_) {
      return p_215485_1_ == ChunkPos.SENTINEL;
   }

   protected void notifyNeighbors(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      ChunkPos lvt_5_1_ = new ChunkPos(p_215478_1_);
      int lvt_6_1_ = lvt_5_1_.x;
      int lvt_7_1_ = lvt_5_1_.z;

      for(int lvt_8_1_ = -1; lvt_8_1_ <= 1; ++lvt_8_1_) {
         for(int lvt_9_1_ = -1; lvt_9_1_ <= 1; ++lvt_9_1_) {
            long lvt_10_1_ = ChunkPos.asLong(lvt_6_1_ + lvt_8_1_, lvt_7_1_ + lvt_9_1_);
            if (lvt_10_1_ != p_215478_1_) {
               this.propagateLevel(p_215478_1_, lvt_10_1_, p_215478_3_, p_215478_4_);
            }
         }
      }

   }

   protected int computeLevel(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      int lvt_6_1_ = p_215477_5_;
      ChunkPos lvt_7_1_ = new ChunkPos(p_215477_1_);
      int lvt_8_1_ = lvt_7_1_.x;
      int lvt_9_1_ = lvt_7_1_.z;

      for(int lvt_10_1_ = -1; lvt_10_1_ <= 1; ++lvt_10_1_) {
         for(int lvt_11_1_ = -1; lvt_11_1_ <= 1; ++lvt_11_1_) {
            long lvt_12_1_ = ChunkPos.asLong(lvt_8_1_ + lvt_10_1_, lvt_9_1_ + lvt_11_1_);
            if (lvt_12_1_ == p_215477_1_) {
               lvt_12_1_ = ChunkPos.SENTINEL;
            }

            if (lvt_12_1_ != p_215477_3_) {
               int lvt_14_1_ = this.getEdgeLevel(lvt_12_1_, p_215477_1_, this.getLevel(lvt_12_1_));
               if (lvt_6_1_ > lvt_14_1_) {
                  lvt_6_1_ = lvt_14_1_;
               }

               if (lvt_6_1_ == 0) {
                  return lvt_6_1_;
               }
            }
         }
      }

      return lvt_6_1_;
   }

   protected int getEdgeLevel(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      return p_215480_1_ == ChunkPos.SENTINEL ? this.getSourceLevel(p_215480_3_) : p_215480_5_ + 1;
   }

   protected abstract int getSourceLevel(long var1);

   public void updateSourceLevel(long p_215491_1_, int p_215491_3_, boolean p_215491_4_) {
      this.scheduleUpdate(ChunkPos.SENTINEL, p_215491_1_, p_215491_3_, p_215491_4_);
   }
}
