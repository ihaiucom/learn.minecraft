package net.minecraft.nbt;

import java.util.AbstractList;

public abstract class CollectionNBT<T extends INBT> extends AbstractList<T> implements INBT {
   public abstract T set(int var1, T var2);

   public abstract void add(int var1, T var2);

   public abstract T remove(int var1);

   public abstract boolean func_218659_a(int var1, INBT var2);

   public abstract boolean func_218660_b(int var1, INBT var2);

   // $FF: synthetic method
   public Object remove(int p_remove_1_) {
      return this.remove(p_remove_1_);
   }

   // $FF: synthetic method
   public void add(int p_add_1_, Object p_add_2_) {
      this.add(p_add_1_, (INBT)p_add_2_);
   }

   // $FF: synthetic method
   public Object set(int p_set_1_, Object p_set_2_) {
      return this.set(p_set_1_, (INBT)p_set_2_);
   }
}
