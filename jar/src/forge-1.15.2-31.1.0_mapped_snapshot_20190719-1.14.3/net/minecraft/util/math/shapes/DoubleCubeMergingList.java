package net.minecraft.util.math.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class DoubleCubeMergingList implements IDoubleListMerger {
   private final DoubleRangeList field_212436_a;
   private final int firstSize;
   private final int secondSize;
   private final int gcd;

   DoubleCubeMergingList(int p_i47687_1_, int p_i47687_2_) {
      this.field_212436_a = new DoubleRangeList((int)VoxelShapes.lcm(p_i47687_1_, p_i47687_2_));
      this.firstSize = p_i47687_1_;
      this.secondSize = p_i47687_2_;
      this.gcd = IntMath.gcd(p_i47687_1_, p_i47687_2_);
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer p_197855_1_) {
      int lvt_2_1_ = this.firstSize / this.gcd;
      int lvt_3_1_ = this.secondSize / this.gcd;

      for(int lvt_4_1_ = 0; lvt_4_1_ <= this.field_212436_a.size(); ++lvt_4_1_) {
         if (!p_197855_1_.merge(lvt_4_1_ / lvt_3_1_, lvt_4_1_ / lvt_2_1_, lvt_4_1_)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList func_212435_a() {
      return this.field_212436_a;
   }
}
