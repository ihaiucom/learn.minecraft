package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public final class IndirectMerger implements IDoubleListMerger {
   private final DoubleArrayList field_197856_a;
   private final IntArrayList list1;
   private final IntArrayList list2;

   protected IndirectMerger(DoubleList p_i47685_1_, DoubleList p_i47685_2_, boolean p_i47685_3_, boolean p_i47685_4_) {
      int lvt_5_1_ = 0;
      int lvt_6_1_ = 0;
      double lvt_7_1_ = Double.NaN;
      int lvt_9_1_ = p_i47685_1_.size();
      int lvt_10_1_ = p_i47685_2_.size();
      int lvt_11_1_ = lvt_9_1_ + lvt_10_1_;
      this.field_197856_a = new DoubleArrayList(lvt_11_1_);
      this.list1 = new IntArrayList(lvt_11_1_);
      this.list2 = new IntArrayList(lvt_11_1_);

      while(true) {
         boolean lvt_12_1_;
         boolean lvt_13_1_;
         boolean lvt_14_1_;
         double lvt_15_1_;
         do {
            do {
               lvt_12_1_ = lvt_5_1_ < lvt_9_1_;
               lvt_13_1_ = lvt_6_1_ < lvt_10_1_;
               if (!lvt_12_1_ && !lvt_13_1_) {
                  if (this.field_197856_a.isEmpty()) {
                     this.field_197856_a.add(Math.min(p_i47685_1_.getDouble(lvt_9_1_ - 1), p_i47685_2_.getDouble(lvt_10_1_ - 1)));
                  }

                  return;
               }

               lvt_14_1_ = lvt_12_1_ && (!lvt_13_1_ || p_i47685_1_.getDouble(lvt_5_1_) < p_i47685_2_.getDouble(lvt_6_1_) + 1.0E-7D);
               lvt_15_1_ = lvt_14_1_ ? p_i47685_1_.getDouble(lvt_5_1_++) : p_i47685_2_.getDouble(lvt_6_1_++);
            } while((lvt_5_1_ == 0 || !lvt_12_1_) && !lvt_14_1_ && !p_i47685_4_);
         } while((lvt_6_1_ == 0 || !lvt_13_1_) && lvt_14_1_ && !p_i47685_3_);

         if (lvt_7_1_ < lvt_15_1_ - 1.0E-7D) {
            this.list1.add(lvt_5_1_ - 1);
            this.list2.add(lvt_6_1_ - 1);
            this.field_197856_a.add(lvt_15_1_);
            lvt_7_1_ = lvt_15_1_;
         } else if (!this.field_197856_a.isEmpty()) {
            this.list1.set(this.list1.size() - 1, lvt_5_1_ - 1);
            this.list2.set(this.list2.size() - 1, lvt_6_1_ - 1);
         }
      }
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer p_197855_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_197856_a.size() - 1; ++lvt_2_1_) {
         if (!p_197855_1_.merge(this.list1.getInt(lvt_2_1_), this.list2.getInt(lvt_2_1_), lvt_2_1_)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList func_212435_a() {
      return this.field_197856_a;
   }
}
