package net.minecraft.util.math;

public class ColumnPos {
   public final int x;
   public final int z;

   public ColumnPos(int p_i50710_1_, int p_i50710_2_) {
      this.x = p_i50710_1_;
      this.z = p_i50710_2_;
   }

   public ColumnPos(BlockPos p_i50711_1_) {
      this.x = p_i50711_1_.getX();
      this.z = p_i50711_1_.getZ();
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public int hashCode() {
      int lvt_1_1_ = 1664525 * this.x + 1013904223;
      int lvt_2_1_ = 1664525 * (this.z ^ -559038737) + 1013904223;
      return lvt_1_1_ ^ lvt_2_1_;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ColumnPos)) {
         return false;
      } else {
         ColumnPos lvt_2_1_ = (ColumnPos)p_equals_1_;
         return this.x == lvt_2_1_.x && this.z == lvt_2_1_.z;
      }
   }
}
