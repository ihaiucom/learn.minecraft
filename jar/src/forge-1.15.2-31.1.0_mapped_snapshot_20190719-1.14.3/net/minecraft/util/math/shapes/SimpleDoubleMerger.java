package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class SimpleDoubleMerger implements IDoubleListMerger {
   private final DoubleList list;

   public SimpleDoubleMerger(DoubleList p_i49559_1_) {
      this.list = p_i49559_1_;
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer p_197855_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ <= this.list.size(); ++lvt_2_1_) {
         if (!p_197855_1_.merge(lvt_2_1_, lvt_2_1_, lvt_2_1_)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList func_212435_a() {
      return this.list;
   }
}
