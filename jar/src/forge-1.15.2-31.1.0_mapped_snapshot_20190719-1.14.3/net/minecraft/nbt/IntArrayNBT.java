package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayNBT extends CollectionNBT<IntNBT> {
   public static final INBTType<IntArrayNBT> field_229690_a_ = new INBTType<IntArrayNBT>() {
      public IntArrayNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(192L);
         int lvt_4_1_ = p_225649_1_.readInt();
         p_225649_3_.read(32L * (long)lvt_4_1_);
         int[] lvt_5_1_ = new int[lvt_4_1_];

         for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_4_1_; ++lvt_6_1_) {
            lvt_5_1_[lvt_6_1_] = p_225649_1_.readInt();
         }

         return new IntArrayNBT(lvt_5_1_);
      }

      public String func_225648_a_() {
         return "INT[]";
      }

      public String func_225650_b_() {
         return "TAG_Int_Array";
      }

      // $FF: synthetic method
      public INBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         return this.func_225649_b_(p_225649_1_, p_225649_2_, p_225649_3_);
      }
   };
   private int[] intArray;

   public IntArrayNBT(int[] p_i45132_1_) {
      this.intArray = p_i45132_1_;
   }

   public IntArrayNBT(List<Integer> p_i47528_1_) {
      this(toArray(p_i47528_1_));
   }

   private static int[] toArray(List<Integer> p_193584_0_) {
      int[] lvt_1_1_ = new int[p_193584_0_.size()];

      for(int lvt_2_1_ = 0; lvt_2_1_ < p_193584_0_.size(); ++lvt_2_1_) {
         Integer lvt_3_1_ = (Integer)p_193584_0_.get(lvt_2_1_);
         lvt_1_1_[lvt_2_1_] = lvt_3_1_ == null ? 0 : lvt_3_1_;
      }

      return lvt_1_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.intArray.length);
      int[] var2 = this.intArray;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int lvt_5_1_ = var2[var4];
         p_74734_1_.writeInt(lvt_5_1_);
      }

   }

   public byte getId() {
      return 11;
   }

   public INBTType<IntArrayNBT> func_225647_b_() {
      return field_229690_a_;
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder("[I;");

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.intArray.length; ++lvt_2_1_) {
         if (lvt_2_1_ != 0) {
            lvt_1_1_.append(',');
         }

         lvt_1_1_.append(this.intArray[lvt_2_1_]);
      }

      return lvt_1_1_.append(']').toString();
   }

   public IntArrayNBT copy() {
      int[] lvt_1_1_ = new int[this.intArray.length];
      System.arraycopy(this.intArray, 0, lvt_1_1_, 0, this.intArray.length);
      return new IntArrayNBT(lvt_1_1_);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof IntArrayNBT && Arrays.equals(this.intArray, ((IntArrayNBT)p_equals_1_).intArray);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.intArray);
   }

   public int[] getIntArray() {
      return this.intArray;
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent lvt_3_1_ = (new StringTextComponent("I")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      ITextComponent lvt_4_1_ = (new StringTextComponent("[")).appendSibling(lvt_3_1_).appendText(";");

      for(int lvt_5_1_ = 0; lvt_5_1_ < this.intArray.length; ++lvt_5_1_) {
         lvt_4_1_.appendText(" ").appendSibling((new StringTextComponent(String.valueOf(this.intArray[lvt_5_1_]))).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (lvt_5_1_ != this.intArray.length - 1) {
            lvt_4_1_.appendText(",");
         }
      }

      lvt_4_1_.appendText("]");
      return lvt_4_1_;
   }

   public int size() {
      return this.intArray.length;
   }

   public IntNBT get(int p_get_1_) {
      return IntNBT.func_229692_a_(this.intArray[p_get_1_]);
   }

   public IntNBT set(int p_set_1_, IntNBT p_set_2_) {
      int lvt_3_1_ = this.intArray[p_set_1_];
      this.intArray[p_set_1_] = p_set_2_.getInt();
      return IntNBT.func_229692_a_(lvt_3_1_);
   }

   public void add(int p_add_1_, IntNBT p_add_2_) {
      this.intArray = ArrayUtils.add(this.intArray, p_add_1_, p_add_2_.getInt());
   }

   public boolean func_218659_a(int p_218659_1_, INBT p_218659_2_) {
      if (p_218659_2_ instanceof NumberNBT) {
         this.intArray[p_218659_1_] = ((NumberNBT)p_218659_2_).getInt();
         return true;
      } else {
         return false;
      }
   }

   public boolean func_218660_b(int p_218660_1_, INBT p_218660_2_) {
      if (p_218660_2_ instanceof NumberNBT) {
         this.intArray = ArrayUtils.add(this.intArray, p_218660_1_, ((NumberNBT)p_218660_2_).getInt());
         return true;
      } else {
         return false;
      }
   }

   public IntNBT remove(int p_remove_1_) {
      int lvt_2_1_ = this.intArray[p_remove_1_];
      this.intArray = ArrayUtils.remove(this.intArray, p_remove_1_);
      return IntNBT.func_229692_a_(lvt_2_1_);
   }

   public void clear() {
      this.intArray = new int[0];
   }

   // $FF: synthetic method
   public INBT remove(int p_remove_1_) {
      return this.remove(p_remove_1_);
   }

   // $FF: synthetic method
   public void add(int p_add_1_, INBT p_add_2_) {
      this.add(p_add_1_, (IntNBT)p_add_2_);
   }

   // $FF: synthetic method
   public INBT set(int p_set_1_, INBT p_set_2_) {
      return this.set(p_set_1_, (IntNBT)p_set_2_);
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
      this.add(p_add_1_, (IntNBT)p_add_2_);
   }

   // $FF: synthetic method
   public Object set(int p_set_1_, Object p_set_2_) {
      return this.set(p_set_1_, (IntNBT)p_set_2_);
   }

   // $FF: synthetic method
   public Object get(int p_get_1_) {
      return this.get(p_get_1_);
   }
}
