package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class DoubleNBT extends NumberNBT {
   public static final DoubleNBT field_229682_a_ = new DoubleNBT(0.0D);
   public static final INBTType<DoubleNBT> field_229683_b_ = new INBTType<DoubleNBT>() {
      public DoubleNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(128L);
         return DoubleNBT.func_229684_a_(p_225649_1_.readDouble());
      }

      public String func_225648_a_() {
         return "DOUBLE";
      }

      public String func_225650_b_() {
         return "TAG_Double";
      }

      public boolean func_225651_c_() {
         return true;
      }

      // $FF: synthetic method
      public INBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         return this.func_225649_b_(p_225649_1_, p_225649_2_, p_225649_3_);
      }
   };
   private final double data;

   private DoubleNBT(double p_i45130_1_) {
      this.data = p_i45130_1_;
   }

   public static DoubleNBT func_229684_a_(double p_229684_0_) {
      return p_229684_0_ == 0.0D ? field_229682_a_ : new DoubleNBT(p_229684_0_);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeDouble(this.data);
   }

   public byte getId() {
      return 6;
   }

   public INBTType<DoubleNBT> func_225647_b_() {
      return field_229683_b_;
   }

   public String toString() {
      return this.data + "d";
   }

   public DoubleNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof DoubleNBT && this.data == ((DoubleNBT)p_equals_1_).data;
      }
   }

   public int hashCode() {
      long lvt_1_1_ = Double.doubleToLongBits(this.data);
      return (int)(lvt_1_1_ ^ lvt_1_1_ >>> 32);
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent lvt_3_1_ = (new StringTextComponent("d")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new StringTextComponent(String.valueOf(this.data))).appendSibling(lvt_3_1_).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getLong() {
      return (long)Math.floor(this.data);
   }

   public int getInt() {
      return MathHelper.floor(this.data);
   }

   public short getShort() {
      return (short)(MathHelper.floor(this.data) & '\uffff');
   }

   public byte getByte() {
      return (byte)(MathHelper.floor(this.data) & 255);
   }

   public double getDouble() {
      return this.data;
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
}
