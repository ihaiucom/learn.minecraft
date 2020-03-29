package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public class IntIdentityHashBiMap<K> implements IObjectIntIterable<K> {
   private static final Object EMPTY = null;
   private K[] values;
   private int[] intKeys;
   private K[] byId;
   private int nextFreeIndex;
   private int mapSize;

   public IntIdentityHashBiMap(int p_i46830_1_) {
      p_i46830_1_ = (int)((float)p_i46830_1_ / 0.8F);
      this.values = (Object[])(new Object[p_i46830_1_]);
      this.intKeys = new int[p_i46830_1_];
      this.byId = (Object[])(new Object[p_i46830_1_]);
   }

   public int getId(@Nullable K p_186815_1_) {
      return this.getValue(this.getIndex(p_186815_1_, this.hashObject(p_186815_1_)));
   }

   @Nullable
   public K getByValue(int p_148745_1_) {
      return p_148745_1_ >= 0 && p_148745_1_ < this.byId.length ? this.byId[p_148745_1_] : null;
   }

   private int getValue(int p_186805_1_) {
      return p_186805_1_ == -1 ? -1 : this.intKeys[p_186805_1_];
   }

   public int add(K p_186808_1_) {
      int lvt_2_1_ = this.nextId();
      this.put(p_186808_1_, lvt_2_1_);
      return lvt_2_1_;
   }

   private int nextId() {
      while(this.nextFreeIndex < this.byId.length && this.byId[this.nextFreeIndex] != null) {
         ++this.nextFreeIndex;
      }

      return this.nextFreeIndex;
   }

   private void grow(int p_186807_1_) {
      K[] lvt_2_1_ = this.values;
      int[] lvt_3_1_ = this.intKeys;
      this.values = (Object[])(new Object[p_186807_1_]);
      this.intKeys = new int[p_186807_1_];
      this.byId = (Object[])(new Object[p_186807_1_]);
      this.nextFreeIndex = 0;
      this.mapSize = 0;

      for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_2_1_.length; ++lvt_4_1_) {
         if (lvt_2_1_[lvt_4_1_] != null) {
            this.put(lvt_2_1_[lvt_4_1_], lvt_3_1_[lvt_4_1_]);
         }
      }

   }

   public void put(K p_186814_1_, int p_186814_2_) {
      int lvt_3_1_ = Math.max(p_186814_2_, this.mapSize + 1);
      int lvt_4_1_;
      if ((float)lvt_3_1_ >= (float)this.values.length * 0.8F) {
         for(lvt_4_1_ = this.values.length << 1; lvt_4_1_ < p_186814_2_; lvt_4_1_ <<= 1) {
         }

         this.grow(lvt_4_1_);
      }

      lvt_4_1_ = this.findEmpty(this.hashObject(p_186814_1_));
      this.values[lvt_4_1_] = p_186814_1_;
      this.intKeys[lvt_4_1_] = p_186814_2_;
      this.byId[p_186814_2_] = p_186814_1_;
      ++this.mapSize;
      if (p_186814_2_ == this.nextFreeIndex) {
         ++this.nextFreeIndex;
      }

   }

   private int hashObject(@Nullable K p_186811_1_) {
      return (MathHelper.hash(System.identityHashCode(p_186811_1_)) & Integer.MAX_VALUE) % this.values.length;
   }

   private int getIndex(@Nullable K p_186816_1_, int p_186816_2_) {
      int lvt_3_2_;
      for(lvt_3_2_ = p_186816_2_; lvt_3_2_ < this.values.length; ++lvt_3_2_) {
         if (this.values[lvt_3_2_] == p_186816_1_) {
            return lvt_3_2_;
         }

         if (this.values[lvt_3_2_] == EMPTY) {
            return -1;
         }
      }

      for(lvt_3_2_ = 0; lvt_3_2_ < p_186816_2_; ++lvt_3_2_) {
         if (this.values[lvt_3_2_] == p_186816_1_) {
            return lvt_3_2_;
         }

         if (this.values[lvt_3_2_] == EMPTY) {
            return -1;
         }
      }

      return -1;
   }

   private int findEmpty(int p_186806_1_) {
      int lvt_2_2_;
      for(lvt_2_2_ = p_186806_1_; lvt_2_2_ < this.values.length; ++lvt_2_2_) {
         if (this.values[lvt_2_2_] == EMPTY) {
            return lvt_2_2_;
         }
      }

      for(lvt_2_2_ = 0; lvt_2_2_ < p_186806_1_; ++lvt_2_2_) {
         if (this.values[lvt_2_2_] == EMPTY) {
            return lvt_2_2_;
         }
      }

      throw new RuntimeException("Overflowed :(");
   }

   public Iterator<K> iterator() {
      return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
   }

   public void clear() {
      Arrays.fill(this.values, (Object)null);
      Arrays.fill(this.byId, (Object)null);
      this.nextFreeIndex = 0;
      this.mapSize = 0;
   }

   public int size() {
      return this.mapSize;
   }
}
