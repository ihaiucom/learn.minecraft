package net.minecraft.util;

import java.util.function.IntConsumer;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class BitArray {
   private final long[] longArray;
   private final int bitsPerEntry;
   private final long maxEntryValue;
   private final int arraySize;

   public BitArray(int p_i46832_1_, int p_i46832_2_) {
      this(p_i46832_1_, p_i46832_2_, new long[MathHelper.roundUp(p_i46832_2_ * p_i46832_1_, 64) / 64]);
   }

   public BitArray(int p_i47901_1_, int p_i47901_2_, long[] p_i47901_3_) {
      Validate.inclusiveBetween(1L, 32L, (long)p_i47901_1_);
      this.arraySize = p_i47901_2_;
      this.bitsPerEntry = p_i47901_1_;
      this.longArray = p_i47901_3_;
      this.maxEntryValue = (1L << p_i47901_1_) - 1L;
      int lvt_4_1_ = MathHelper.roundUp(p_i47901_2_ * p_i47901_1_, 64) / 64;
      if (p_i47901_3_.length != lvt_4_1_) {
         throw (RuntimeException)Util.func_229757_c_(new RuntimeException("Invalid length given for storage, got: " + p_i47901_3_.length + " but expected: " + lvt_4_1_));
      }
   }

   public int func_219789_a(int p_219789_1_, int p_219789_2_) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)p_219789_1_);
      Validate.inclusiveBetween(0L, this.maxEntryValue, (long)p_219789_2_);
      int lvt_3_1_ = p_219789_1_ * this.bitsPerEntry;
      int lvt_4_1_ = lvt_3_1_ >> 6;
      int lvt_5_1_ = (p_219789_1_ + 1) * this.bitsPerEntry - 1 >> 6;
      int lvt_6_1_ = lvt_3_1_ ^ lvt_4_1_ << 6;
      int lvt_7_1_ = 0;
      int lvt_7_1_ = lvt_7_1_ | (int)(this.longArray[lvt_4_1_] >>> lvt_6_1_ & this.maxEntryValue);
      this.longArray[lvt_4_1_] = this.longArray[lvt_4_1_] & ~(this.maxEntryValue << lvt_6_1_) | ((long)p_219789_2_ & this.maxEntryValue) << lvt_6_1_;
      if (lvt_4_1_ != lvt_5_1_) {
         int lvt_8_1_ = 64 - lvt_6_1_;
         int lvt_9_1_ = this.bitsPerEntry - lvt_8_1_;
         lvt_7_1_ |= (int)(this.longArray[lvt_5_1_] << lvt_8_1_ & this.maxEntryValue);
         this.longArray[lvt_5_1_] = this.longArray[lvt_5_1_] >>> lvt_9_1_ << lvt_9_1_ | ((long)p_219789_2_ & this.maxEntryValue) >> lvt_8_1_;
      }

      return lvt_7_1_;
   }

   public void setAt(int p_188141_1_, int p_188141_2_) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)p_188141_1_);
      Validate.inclusiveBetween(0L, this.maxEntryValue, (long)p_188141_2_);
      int lvt_3_1_ = p_188141_1_ * this.bitsPerEntry;
      int lvt_4_1_ = lvt_3_1_ >> 6;
      int lvt_5_1_ = (p_188141_1_ + 1) * this.bitsPerEntry - 1 >> 6;
      int lvt_6_1_ = lvt_3_1_ ^ lvt_4_1_ << 6;
      this.longArray[lvt_4_1_] = this.longArray[lvt_4_1_] & ~(this.maxEntryValue << lvt_6_1_) | ((long)p_188141_2_ & this.maxEntryValue) << lvt_6_1_;
      if (lvt_4_1_ != lvt_5_1_) {
         int lvt_7_1_ = 64 - lvt_6_1_;
         int lvt_8_1_ = this.bitsPerEntry - lvt_7_1_;
         this.longArray[lvt_5_1_] = this.longArray[lvt_5_1_] >>> lvt_8_1_ << lvt_8_1_ | ((long)p_188141_2_ & this.maxEntryValue) >> lvt_7_1_;
      }

   }

   public int getAt(int p_188142_1_) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)p_188142_1_);
      int lvt_2_1_ = p_188142_1_ * this.bitsPerEntry;
      int lvt_3_1_ = lvt_2_1_ >> 6;
      int lvt_4_1_ = (p_188142_1_ + 1) * this.bitsPerEntry - 1 >> 6;
      int lvt_5_1_ = lvt_2_1_ ^ lvt_3_1_ << 6;
      if (lvt_3_1_ == lvt_4_1_) {
         return (int)(this.longArray[lvt_3_1_] >>> lvt_5_1_ & this.maxEntryValue);
      } else {
         int lvt_6_1_ = 64 - lvt_5_1_;
         return (int)((this.longArray[lvt_3_1_] >>> lvt_5_1_ | this.longArray[lvt_4_1_] << lvt_6_1_) & this.maxEntryValue);
      }
   }

   public long[] getBackingLongArray() {
      return this.longArray;
   }

   public int size() {
      return this.arraySize;
   }

   public int bitsPerEntry() {
      return this.bitsPerEntry;
   }

   public void func_225421_a(IntConsumer p_225421_1_) {
      int lvt_2_1_ = this.longArray.length;
      if (lvt_2_1_ != 0) {
         int lvt_3_1_ = 0;
         long lvt_4_1_ = this.longArray[0];
         long lvt_6_1_ = lvt_2_1_ > 1 ? this.longArray[1] : 0L;

         for(int lvt_8_1_ = 0; lvt_8_1_ < this.arraySize; ++lvt_8_1_) {
            int lvt_9_1_ = lvt_8_1_ * this.bitsPerEntry;
            int lvt_10_1_ = lvt_9_1_ >> 6;
            int lvt_11_1_ = (lvt_8_1_ + 1) * this.bitsPerEntry - 1 >> 6;
            int lvt_12_1_ = lvt_9_1_ ^ lvt_10_1_ << 6;
            if (lvt_10_1_ != lvt_3_1_) {
               lvt_4_1_ = lvt_6_1_;
               lvt_6_1_ = lvt_10_1_ + 1 < lvt_2_1_ ? this.longArray[lvt_10_1_ + 1] : 0L;
               lvt_3_1_ = lvt_10_1_;
            }

            if (lvt_10_1_ == lvt_11_1_) {
               p_225421_1_.accept((int)(lvt_4_1_ >>> lvt_12_1_ & this.maxEntryValue));
            } else {
               int lvt_13_1_ = 64 - lvt_12_1_;
               p_225421_1_.accept((int)((lvt_4_1_ >>> lvt_12_1_ | lvt_6_1_ << lvt_13_1_) & this.maxEntryValue));
            }
         }

      }
   }
}
