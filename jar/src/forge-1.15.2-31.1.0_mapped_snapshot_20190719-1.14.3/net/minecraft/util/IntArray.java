package net.minecraft.util;

public class IntArray implements IIntArray {
   private final int[] field_221479_a;

   public IntArray(int p_i50063_1_) {
      this.field_221479_a = new int[p_i50063_1_];
   }

   public int get(int p_221476_1_) {
      return this.field_221479_a[p_221476_1_];
   }

   public void set(int p_221477_1_, int p_221477_2_) {
      this.field_221479_a[p_221477_1_] = p_221477_2_;
   }

   public int size() {
      return this.field_221479_a.length;
   }
}
