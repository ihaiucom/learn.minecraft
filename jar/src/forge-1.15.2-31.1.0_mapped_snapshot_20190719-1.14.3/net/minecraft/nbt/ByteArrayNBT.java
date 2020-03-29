package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayNBT extends CollectionNBT<ByteNBT> {
   public static final INBTType<ByteArrayNBT> field_229667_a_ = new INBTType<ByteArrayNBT>() {
      public ByteArrayNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(192L);
         int lvt_4_1_ = p_225649_1_.readInt();
         p_225649_3_.read(8L * (long)lvt_4_1_);
         byte[] lvt_5_1_ = new byte[lvt_4_1_];
         p_225649_1_.readFully(lvt_5_1_);
         return new ByteArrayNBT(lvt_5_1_);
      }

      public String func_225648_a_() {
         return "BYTE[]";
      }

      public String func_225650_b_() {
         return "TAG_Byte_Array";
      }

      // $FF: synthetic method
      public INBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         return this.func_225649_b_(p_225649_1_, p_225649_2_, p_225649_3_);
      }
   };
   private byte[] data;

   public ByteArrayNBT(byte[] p_i45128_1_) {
      this.data = p_i45128_1_;
   }

   public ByteArrayNBT(List<Byte> p_i47529_1_) {
      this(toArray(p_i47529_1_));
   }

   private static byte[] toArray(List<Byte> p_193589_0_) {
      byte[] lvt_1_1_ = new byte[p_193589_0_.size()];

      for(int lvt_2_1_ = 0; lvt_2_1_ < p_193589_0_.size(); ++lvt_2_1_) {
         Byte lvt_3_1_ = (Byte)p_193589_0_.get(lvt_2_1_);
         lvt_1_1_[lvt_2_1_] = lvt_3_1_ == null ? 0 : lvt_3_1_;
      }

      return lvt_1_1_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.data.length);
      p_74734_1_.write(this.data);
   }

   public byte getId() {
      return 7;
   }

   public INBTType<ByteArrayNBT> func_225647_b_() {
      return field_229667_a_;
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder("[B;");

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.data.length; ++lvt_2_1_) {
         if (lvt_2_1_ != 0) {
            lvt_1_1_.append(',');
         }

         lvt_1_1_.append(this.data[lvt_2_1_]).append('B');
      }

      return lvt_1_1_.append(']').toString();
   }

   public INBT copy() {
      byte[] lvt_1_1_ = new byte[this.data.length];
      System.arraycopy(this.data, 0, lvt_1_1_, 0, this.data.length);
      return new ByteArrayNBT(lvt_1_1_);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof ByteArrayNBT && Arrays.equals(this.data, ((ByteArrayNBT)p_equals_1_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent lvt_3_1_ = (new StringTextComponent("B")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
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

   public byte[] getByteArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public ByteNBT get(int p_get_1_) {
      return ByteNBT.func_229671_a_(this.data[p_get_1_]);
   }

   public ByteNBT set(int p_set_1_, ByteNBT p_set_2_) {
      byte lvt_3_1_ = this.data[p_set_1_];
      this.data[p_set_1_] = p_set_2_.getByte();
      return ByteNBT.func_229671_a_(lvt_3_1_);
   }

   public void add(int p_add_1_, ByteNBT p_add_2_) {
      this.data = ArrayUtils.add(this.data, p_add_1_, p_add_2_.getByte());
   }

   public boolean func_218659_a(int p_218659_1_, INBT p_218659_2_) {
      if (p_218659_2_ instanceof NumberNBT) {
         this.data[p_218659_1_] = ((NumberNBT)p_218659_2_).getByte();
         return true;
      } else {
         return false;
      }
   }

   public boolean func_218660_b(int p_218660_1_, INBT p_218660_2_) {
      if (p_218660_2_ instanceof NumberNBT) {
         this.data = ArrayUtils.add(this.data, p_218660_1_, ((NumberNBT)p_218660_2_).getByte());
         return true;
      } else {
         return false;
      }
   }

   public ByteNBT remove(int p_remove_1_) {
      byte lvt_2_1_ = this.data[p_remove_1_];
      this.data = ArrayUtils.remove(this.data, p_remove_1_);
      return ByteNBT.func_229671_a_(lvt_2_1_);
   }

   public void clear() {
      this.data = new byte[0];
   }

   // $FF: synthetic method
   public INBT remove(int p_remove_1_) {
      return this.remove(p_remove_1_);
   }

   // $FF: synthetic method
   public void add(int p_add_1_, INBT p_add_2_) {
      this.add(p_add_1_, (ByteNBT)p_add_2_);
   }

   // $FF: synthetic method
   public INBT set(int p_set_1_, INBT p_set_2_) {
      return this.set(p_set_1_, (ByteNBT)p_set_2_);
   }

   // $FF: synthetic method
   public Object remove(int p_remove_1_) {
      return this.remove(p_remove_1_);
   }

   // $FF: synthetic method
   public void add(int p_add_1_, Object p_add_2_) {
      this.add(p_add_1_, (ByteNBT)p_add_2_);
   }

   // $FF: synthetic method
   public Object set(int p_set_1_, Object p_set_2_) {
      return this.set(p_set_1_, (ByteNBT)p_set_2_);
   }

   // $FF: synthetic method
   public Object get(int p_get_1_) {
      return this.get(p_get_1_);
   }
}
