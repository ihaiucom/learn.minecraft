package net.minecraft.util;

public abstract class IntReferenceHolder {
   private int lastKnownValue;

   public static IntReferenceHolder create(final IIntArray p_221493_0_, final int p_221493_1_) {
      return new IntReferenceHolder() {
         public int get() {
            return p_221493_0_.get(p_221493_1_);
         }

         public void set(int p_221494_1_) {
            p_221493_0_.set(p_221493_1_, p_221494_1_);
         }
      };
   }

   public static IntReferenceHolder create(final int[] p_221497_0_, final int p_221497_1_) {
      return new IntReferenceHolder() {
         public int get() {
            return p_221497_0_[p_221497_1_];
         }

         public void set(int p_221494_1_) {
            p_221497_0_[p_221497_1_] = p_221494_1_;
         }
      };
   }

   public static IntReferenceHolder single() {
      return new IntReferenceHolder() {
         private int value;

         public int get() {
            return this.value;
         }

         public void set(int p_221494_1_) {
            this.value = p_221494_1_;
         }
      };
   }

   public abstract int get();

   public abstract void set(int var1);

   public boolean isDirty() {
      int lvt_1_1_ = this.get();
      boolean lvt_2_1_ = lvt_1_1_ != this.lastKnownValue;
      this.lastKnownValue = lvt_1_1_;
      return lvt_2_1_;
   }
}
