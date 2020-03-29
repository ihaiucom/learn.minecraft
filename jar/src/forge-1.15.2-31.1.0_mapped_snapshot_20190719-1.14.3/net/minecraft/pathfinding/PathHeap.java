package net.minecraft.pathfinding;

public class PathHeap {
   private PathPoint[] pathPoints = new PathPoint[128];
   private int count;

   public PathPoint addPoint(PathPoint p_75849_1_) {
      if (p_75849_1_.index >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      } else {
         if (this.count == this.pathPoints.length) {
            PathPoint[] lvt_2_1_ = new PathPoint[this.count << 1];
            System.arraycopy(this.pathPoints, 0, lvt_2_1_, 0, this.count);
            this.pathPoints = lvt_2_1_;
         }

         this.pathPoints[this.count] = p_75849_1_;
         p_75849_1_.index = this.count;
         this.sortBack(this.count++);
         return p_75849_1_;
      }
   }

   public void clearPath() {
      this.count = 0;
   }

   public PathPoint dequeue() {
      PathPoint lvt_1_1_ = this.pathPoints[0];
      this.pathPoints[0] = this.pathPoints[--this.count];
      this.pathPoints[this.count] = null;
      if (this.count > 0) {
         this.sortForward(0);
      }

      lvt_1_1_.index = -1;
      return lvt_1_1_;
   }

   public void changeDistance(PathPoint p_75850_1_, float p_75850_2_) {
      float lvt_3_1_ = p_75850_1_.distanceToTarget;
      p_75850_1_.distanceToTarget = p_75850_2_;
      if (p_75850_2_ < lvt_3_1_) {
         this.sortBack(p_75850_1_.index);
      } else {
         this.sortForward(p_75850_1_.index);
      }

   }

   private void sortBack(int p_75847_1_) {
      PathPoint lvt_2_1_ = this.pathPoints[p_75847_1_];

      int lvt_4_1_;
      for(float lvt_3_1_ = lvt_2_1_.distanceToTarget; p_75847_1_ > 0; p_75847_1_ = lvt_4_1_) {
         lvt_4_1_ = p_75847_1_ - 1 >> 1;
         PathPoint lvt_5_1_ = this.pathPoints[lvt_4_1_];
         if (lvt_3_1_ >= lvt_5_1_.distanceToTarget) {
            break;
         }

         this.pathPoints[p_75847_1_] = lvt_5_1_;
         lvt_5_1_.index = p_75847_1_;
      }

      this.pathPoints[p_75847_1_] = lvt_2_1_;
      lvt_2_1_.index = p_75847_1_;
   }

   private void sortForward(int p_75846_1_) {
      PathPoint lvt_2_1_ = this.pathPoints[p_75846_1_];
      float lvt_3_1_ = lvt_2_1_.distanceToTarget;

      while(true) {
         int lvt_4_1_ = 1 + (p_75846_1_ << 1);
         int lvt_5_1_ = lvt_4_1_ + 1;
         if (lvt_4_1_ >= this.count) {
            break;
         }

         PathPoint lvt_6_1_ = this.pathPoints[lvt_4_1_];
         float lvt_7_1_ = lvt_6_1_.distanceToTarget;
         PathPoint lvt_8_2_;
         float lvt_9_2_;
         if (lvt_5_1_ >= this.count) {
            lvt_8_2_ = null;
            lvt_9_2_ = Float.POSITIVE_INFINITY;
         } else {
            lvt_8_2_ = this.pathPoints[lvt_5_1_];
            lvt_9_2_ = lvt_8_2_.distanceToTarget;
         }

         if (lvt_7_1_ < lvt_9_2_) {
            if (lvt_7_1_ >= lvt_3_1_) {
               break;
            }

            this.pathPoints[p_75846_1_] = lvt_6_1_;
            lvt_6_1_.index = p_75846_1_;
            p_75846_1_ = lvt_4_1_;
         } else {
            if (lvt_9_2_ >= lvt_3_1_) {
               break;
            }

            this.pathPoints[p_75846_1_] = lvt_8_2_;
            lvt_8_2_.index = p_75846_1_;
            p_75846_1_ = lvt_5_1_;
         }
      }

      this.pathPoints[p_75846_1_] = lvt_2_1_;
      lvt_2_1_.index = p_75846_1_;
   }

   public boolean isPathEmpty() {
      return this.count == 0;
   }
}
