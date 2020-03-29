package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class NonOverlappingMerger extends AbstractDoubleList implements IDoubleListMerger {
   private final DoubleList list1;
   private final DoubleList list2;
   private final boolean field_199640_c;

   public NonOverlappingMerger(DoubleList p_i48187_1_, DoubleList p_i48187_2_, boolean p_i48187_3_) {
      this.list1 = p_i48187_1_;
      this.list2 = p_i48187_2_;
      this.field_199640_c = p_i48187_3_;
   }

   public int size() {
      return this.list1.size() + this.list2.size();
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer p_197855_1_) {
      return this.field_199640_c ? this.func_199637_b((p_199636_1_, p_199636_2_, p_199636_3_) -> {
         return p_197855_1_.merge(p_199636_2_, p_199636_1_, p_199636_3_);
      }) : this.func_199637_b(p_197855_1_);
   }

   private boolean func_199637_b(IDoubleListMerger.IConsumer p_199637_1_) {
      int lvt_2_1_ = this.list1.size() - 1;

      int lvt_3_2_;
      for(lvt_3_2_ = 0; lvt_3_2_ < lvt_2_1_; ++lvt_3_2_) {
         if (!p_199637_1_.merge(lvt_3_2_, -1, lvt_3_2_)) {
            return false;
         }
      }

      if (!p_199637_1_.merge(lvt_2_1_, -1, lvt_2_1_)) {
         return false;
      } else {
         for(lvt_3_2_ = 0; lvt_3_2_ < this.list2.size(); ++lvt_3_2_) {
            if (!p_199637_1_.merge(lvt_2_1_, lvt_3_2_, lvt_2_1_ + 1 + lvt_3_2_)) {
               return false;
            }
         }

         return true;
      }
   }

   public double getDouble(int p_getDouble_1_) {
      return p_getDouble_1_ < this.list1.size() ? this.list1.getDouble(p_getDouble_1_) : this.list2.getDouble(p_getDouble_1_ - this.list1.size());
   }

   public DoubleList func_212435_a() {
      return this;
   }
}
