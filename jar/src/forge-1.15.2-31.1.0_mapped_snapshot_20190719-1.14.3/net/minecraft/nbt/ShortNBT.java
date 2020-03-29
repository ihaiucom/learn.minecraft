package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ShortNBT extends NumberNBT {
   public static final INBTType<ShortNBT> field_229700_a_ = new INBTType<ShortNBT>() {
      public ShortNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(80L);
         return ShortNBT.func_229701_a_(p_225649_1_.readShort());
      }

      public String func_225648_a_() {
         return "SHORT";
      }

      public String func_225650_b_() {
         return "TAG_Short";
      }

      public boolean func_225651_c_() {
         return true;
      }

      // $FF: synthetic method
      public INBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         return this.func_225649_b_(p_225649_1_, p_225649_2_, p_225649_3_);
      }
   };
   private final short data;

   private ShortNBT(short p_i45135_1_) {
      this.data = p_i45135_1_;
   }

   public static ShortNBT func_229701_a_(short p_229701_0_) {
      return p_229701_0_ >= -128 && p_229701_0_ <= 1024 ? ShortNBT.Cache.field_229702_a_[p_229701_0_ + 128] : new ShortNBT(p_229701_0_);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeShort(this.data);
   }

   public byte getId() {
      return 2;
   }

   public INBTType<ShortNBT> func_225647_b_() {
      return field_229700_a_;
   }

   public String toString() {
      return this.data + "s";
   }

   public ShortNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof ShortNBT && this.data == ((ShortNBT)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent lvt_3_1_ = (new StringTextComponent("s")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new StringTextComponent(String.valueOf(this.data))).appendSibling(lvt_3_1_).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getLong() {
      return (long)this.data;
   }

   public int getInt() {
      return this.data;
   }

   public short getShort() {
      return this.data;
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
   ShortNBT(short p_i226081_1_, Object p_i226081_2_) {
      this(p_i226081_1_);
   }

   static class Cache {
      static final ShortNBT[] field_229702_a_ = new ShortNBT[1153];

      static {
         for(int lvt_0_1_ = 0; lvt_0_1_ < field_229702_a_.length; ++lvt_0_1_) {
            field_229702_a_[lvt_0_1_] = new ShortNBT((short)(-128 + lvt_0_1_));
         }

      }
   }
}
