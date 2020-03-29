package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayNBT extends CollectionNBT<LongNBT> {
   public static final INBTType<LongArrayNBT> field_229696_a_ = new INBTType<LongArrayNBT>() {
      public LongArrayNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(192L);
         int lvt_4_1_ = p_225649_1_.readInt();
         p_225649_3_.read(64L * (long)lvt_4_1_);
         long[] lvt_5_1_ = new long[lvt_4_1_];

         for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_4_1_; ++lvt_6_1_) {
            lvt_5_1_[lvt_6_1_] = p_225649_1_.readLong();
         }

         return new LongArrayNBT(lvt_5_1_);
      }

      public String func_225648_a_() {
         return "LONG[]";
      }

      public String func_225650_b_() {
         return "TAG_Long_Array";
      }

      // $FF: synthetic method
      public INBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         return this.func_225649_b_(p_225649_1_, p_225649_2_, p_225649_3_);
      }
   };
   private long[] data;

   public LongArrayNBT(long[] p_i47524_1_) {
      this.data = p_i47524_1_;
   }

   public LongArrayNBT(LongSet p_i48736_1_) {
      this.data = p_i48736_1_.toLongArray();
   }

   public LongArrayNBT(List<Long> p_i47525_1_) {
      this(toArray(p_i47525_1_));
   }

   private static long[] toArray(List<Long> p_193586_0_) {
      long[] lvt_1_1_ = new long[p_193586_0_.size()];

      for(int lvt_2_1_ = 0; lvt_2_1_ < p_193586_0_.size(); ++lvt_2_1_) {
         Long lvt_3_1_ = (Long)p_193586_0_.get(lvt_2_1_);
         lvt_1_1_[lvt_2_1_] = lvt_3_1_ == null ? 0L : lvt_3_1_;
      }

      return lvt_1_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.data.length);
      long[] var2 = this.data;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long lvt_5_1_ = var2[var4];
         p_74734_1_.writeLong(lvt_5_1_);
      }

   }

   public byte getId() {
      return 12;
   }

   public INBTType<LongArrayNBT> func_225647_b_() {
      return field_229696_a_;
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder("[L;");

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.data.length; ++lvt_2_1_) {
         if (lvt_2_1_ != 0) {
            lvt_1_1_.append(',');
         }

         lvt_1_1_.append(this.data[lvt_2_1_]).append('L');
      }

      return lvt_1_1_.append(']').toString();
   }

   public LongArrayNBT copy() {
      long[] lvt_1_1_ = new long[this.data.length];
      System.arraycopy(this.data, 0, lvt_1_1_, 0, this.data.length);
      return new LongArrayNBT(lvt_1_1_);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof LongArrayNBT && Arrays.equals(this.data, ((LongArrayNBT)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent lvt_3_1_ = (new StringTextComponent("L")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      ITextComponent lvt_4_1_ = (new StringTextComponent("[")).appendSibling(lvt_3_1_).appendText(";");

      for(int lvt_5_1_ = 0; lvt_5_1_ < this.data.length; ++lvt_5_1_) {
         ITextComponent lvt_6_1_ = (new StringTextComponent(String.valueOf(this.data[lvt_5_1_]))).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         lvt_4_1_.appendText(" ").appendSibling(lvt_6_1_).appendSibling(lvt_3_1_);
         if (lvt_5_1_ != this.data.length - 1) {
            lvt_4_1_.appendText(",");
         }
      }

      lvt_4_1_.appendText("]");
      return lvt_4_1_;
   }

   public long[] getAsLongArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public LongNBT get(int p_get_1_) {
      return LongNBT.func_229698_a_(this.data[p_get_1_]);
   }

   public LongNBT set(int p_set_1_, LongNBT p_set_2_) {
      long lvt_3_1_ = this.data[p_set_1_];
      this.data[p_set_1_] = p_set_2_.getLong();
      return LongNBT.func_229698_a_(lvt_3_1_);
   }

   public void add(int p_add_1_, LongNBT p_add_2_) {
      this.data = ArrayUtils.add(this.data, p_add_1_, p_add_2_.getLong());
   }

   public boolean func_218659_a(int p_218659_1_, INBT p_218659_2_) {
      if (p_218659_2_ instanceof NumberNBT) {
         this.data[p_218659_1_] = ((NumberNBT)p_218659_2_).getLong();
         return true;
      } else {
         return false;
      }
   }

   public boolean func_218660_b(int p_218660_1_, INBT p_218660_2_) {
      if (p_218660_2_ instanceof NumberNBT) {
         this.data = ArrayUtils.add(this.data, p_218660_1_, ((NumberNBT)p_218660_2_).getLong());
         return true;
      } else {
         return false;
      }
   }

   public LongNBT remove(int p_remove_1_) {
      long lvt_2_1_ = this.data[p_remove_1_];
      this.data = ArrayUtils.remove(this.data, p_remove_1_);
      return LongNBT.func_229698_a_(lvt_2_1_);
   }

   public void clear() {
      this.data = new long[0];
   }

   // $FF: synthetic method
   public INBT remove(int p_remove_1_) {
      return this.remove(p_remove_1_);
   }

   // $FF: synthetic method
   public void add(int p_add_1_, INBT p_add_2_) {
      this.add(p_add_1_, (LongNBT)p_add_2_);
   }

   // $FF: synthetic method
   public INBT set(int p_set_1_, INBT p_set_2_) {
      return this.set(p_set_1_, (LongNBT)p_set_2_);
   }

   // $FF: synthetic method
   public INBT copy() {
      return this.copy();
   }

   // $FF: synthetic method
   public Object remove(int p_remove_1_) {
      return this.remove(p_remove_1_);
   }

   // $FF: synthetic method
   public void add(int p_add_1_, Object p_add_2_) {
      this.add(p_add_1_, (LongNBT)p_add_2_);
   }

   // $FF: synthetic method
   public Object set(int p_set_1_, Object p_set_2_) {
      return this.set(p_set_1_, (LongNBT)p_set_2_);
   }

   // $FF: synthetic method
   public Object get(int p_get_1_) {
      return this.get(p_get_1_);
   }
}
