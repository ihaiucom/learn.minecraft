package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ByteNBT extends NumberNBT {
   public static final INBTType<ByteNBT> field_229668_a_ = new INBTType<ByteNBT>() {
      public ByteNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(72L);
         return ByteNBT.func_229671_a_(p_225649_1_.readByte());
      }

      public String func_225648_a_() {
         return "BYTE";
      }

      public String func_225650_b_() {
         return "TAG_Byte";
      }

      public boolean func_225651_c_() {
         return true;
      }

      // $FF: synthetic method
      public INBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         return this.func_225649_b_(p_225649_1_, p_225649_2_, p_225649_3_);
      }
   };
   public static final ByteNBT field_229669_b_ = func_229671_a_((byte)0);
   public static final ByteNBT field_229670_c_ = func_229671_a_((byte)1);
   private final byte data;

   private ByteNBT(byte p_i45129_1_) {
      this.data = p_i45129_1_;
   }

   public static ByteNBT func_229671_a_(byte p_229671_0_) {
      return ByteNBT.Cache.field_229673_a_[128 + p_229671_0_];
   }

   public static ByteNBT func_229672_a_(boolean p_229672_0_) {
      return p_229672_0_ ? field_229670_c_ : field_229669_b_;
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeByte(this.data);
   }

   public byte getId() {
      return 1;
   }

   public INBTType<ByteNBT> func_225647_b_() {
      return field_229668_a_;
   }

   public String toString() {
      return this.data + "b";
   }

   public ByteNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof ByteNBT && this.data == ((ByteNBT)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent lvt_3_1_ = (new StringTextComponent("b")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new StringTextComponent(String.valueOf(this.data))).appendSibling(lvt_3_1_).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getLong() {
      return (long)this.data;
   }

   public int getInt() {
      return this.data;
   }

   public short getShort() {
      return (short)this.data;
   }

   public byte getByte() {
      return this.data;
   }

   public double getDouble() {
      return (double)this.data;
   }

   public float getFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }

   // $FF: synthetic method
   public INBT copy() {
      return this.copy();
   }

   // $FF: synthetic method
   ByteNBT(byte p_i226074_1_, Object p_i226074_2_) {
      this(p_i226074_1_);
   }

   static class Cache {
      private static final ByteNBT[] field_229673_a_ = new ByteNBT[256];

      static {
         for(int lvt_0_1_ = 0; lvt_0_1_ < field_229673_a_.length; ++lvt_0_1_) {
            field_229673_a_[lvt_0_1_] = new ByteNBT((byte)(lvt_0_1_ - 128));
         }

      }
   }
}
