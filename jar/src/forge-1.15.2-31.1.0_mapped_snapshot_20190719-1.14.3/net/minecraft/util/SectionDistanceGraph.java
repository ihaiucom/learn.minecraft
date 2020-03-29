package net.minecraft.util;

import net.minecraft.util.math.SectionPos;
import net.minecraft.world.lighting.LevelBasedGraph;

public abstract class SectionDistanceGraph extends LevelBasedGraph {
   protected SectionDistanceGraph(int p_i50706_1_, int p_i50706_2_, int p_i50706_3_) {
      super(p_i50706_1_, p_i50706_2_, p_i50706_3_);
   }

   protected boolean isRoot(long p_215485_1_) {
      return p_215485_1_ == Long.MAX_VALUE;
   }

   protected void notifyNeighbors(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      for(int lvt_5_1_ = -1; lvt_5_1_ <= 1; ++lvt_5_1_) {
         for(int lvt_6_1_ = -1; lvt_6_1_ <= 1; ++lvt_6_1_) {
            for(int lvt_7_1_ = -1; lvt_7_1_ <= 1; ++lvt_7_1_) {
               long lvt_8_1_ = SectionPos.withOffset(p_215478_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
               if (lvt_8_1_ != p_215478_1_) {
                  this.propagateLevel(p_215478_1_, lvt_8_1_, p_215478_3_, p_215478_4_);
               }
            }
         }
      }

   }

   protected int computeLevel(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      int lvt_6_1_ = p_215477_5_;

      for(int lvt_7_1_ = -1; lvt_7_1_ <= 1; ++lvt_7_1_) {
         for(int lvt_8_1_ = -1; lvt_8_1_ <= 1; ++lvt_8_1_) {
            for(int lvt_9_1_ = -1; lvt_9_1_ <= 1; ++lvt_9_1_) {
               long lvt_10_1_ = SectionPos.withOffset(p_215477_1_, lvt_7_1_, lvt_8_1_, lvt_9_1_);
               if (lvt_10_1_ == p_215477_1_) {
                  lvt_10_1_ = Long.MAX_VALUE;
               }

               if (lvt_10_1_ != p_215477_3_) {
                  int lvt_12_1_ = this.getEdgeLevel(lvt_10_1_, p_215477_1_, this.getLevel(lvt_10_1_));
                  if (lvt_6_1_ > lvt_12_1_) {
                     lvt_6_1_ = lvt_12_1_;
                  }

                  if (lvt_6_1_ == 0) {
                     return lvt_6_1_;
                  }
               }
            }
         }
      }

      return lvt_6_1_;
   }

   protected int getEdgeLevel(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      return p_215480_1_ == Long.MAX_VALUE ? this.getSourceLevel(p_215480_3_) : p_215480_5_ + 1;
   }

   protected abstract int getSourceLevel(long var1);

   public void updateSourceLevel(long p_215515_1_, int p_215515_3_, boolean p_215515_4_) {
      this.scheduleUpdate(Long.MAX_VALUE, p_215515_1_, p_215515_3_, p_215515_4_);
   }
}
