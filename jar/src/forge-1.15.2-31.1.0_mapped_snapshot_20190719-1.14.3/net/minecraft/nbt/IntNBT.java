package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class IntNBT extends NumberNBT {
   public static final INBTType<IntNBT> field_229691_a_ = new INBTType<IntNBT>() {
      public IntNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(96L);
         return IntNBT.func_229692_a_(p_225649_1_.readInt());
      }

      public String func_225648_a_() {
         return "INT";
      }

      public String func_225650_b_() {
         return "TAG_Int";
      }

      public boolean func_225651_c_() {
         return true;
      }

      // $FF: synthetic method
      public INBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         return this.func_225649_b_(p_225649_1_, p_225649_2_, p_225649_3_);
      }
   };
   private final int data;

   private IntNBT(int p_i45133_1_) {
      this.data = p_i45133_1_;
   }

   public static IntNBT func_229692_a_(int p_229692_0_) {
      return p_229692_0_ >= -128 && p_229692_0_ <= 1024 ? IntNBT.Cache.field_229693_a_[p_229692_0_ + 128] : new IntNBT(p_229692_0_);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeInt(this.data);
   }

   public byte getId() {
      return 3;
   }

   public INBTType<IntNBT> func_225647_b_() {
      return field_229691_a_;
   }

   public String toString() {
      return String.valueOf(this.data);
   }

   public IntNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof IntNBT && this.data == ((IntNBT)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      return (new StringTextComponent(String.valueOf(this.data))).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getLong() {
      return (long)this.data;
   }

   public int getInt() {
      return this.data;
   }

   public short getShort() {
      return (short)(this.data & '\uffff');
   }

   public byte getByte() {
      return (byte)(this.data & 255);
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
   IntNBT(int p_i226077_1_, Object p_i226077_2_) {
      this(p_i226077_1_);
   }

   static class Cache {
      static final IntNBT[] field_229693_a_ = new IntNBT[1153];

      static {
         for(int lvt_0_1_ = 0; lvt_0_1_ < field_229693_a_.length; ++lvt_0_1_) {
            field_229693_a_[lvt_0_1_] = new IntNBT(-128 + lvt_0_1_);
         }

      }
   }
}
