package net.minecraft.util;

import java.util.function.Supplier;

public class LazyValue<T> {
   private Supplier<T> supplier;
   private T value;

   public LazyValue(Supplier<T> p_i48587_1_) {
      this.supplier = p_i48587_1_;
   }

   public T getValue() {
      Supplier<T> lvt_1_1_ = this.supplier;
      if (lvt_1_1_ != null) {
         this.value = lvt_1_1_.get();
         this.supplier = null;
      }

      return this.value;
   }
}
