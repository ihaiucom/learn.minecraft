package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class FloatNBT extends NumberNBT {
   public static final FloatNBT field_229687_a_ = new FloatNBT(0.0F);
   public static final INBTType<FloatNBT> field_229688_b_ = new INBTType<FloatNBT>() {
      public FloatNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(96L);
         return FloatNBT.func_229689_a_(p_225649_1_.readFloat());
      }

      public String func_225648_a_() {
         return "FLOAT";
      }

      public String func_225650_b_() {
         return "TAG_Float";
      }

      public boolean func_225651_c_() {
         return true;
      }

      // $FF: synthetic method
      public INBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         return this.func_225649_b_(p_225649_1_, p_225649_2_, p_225649_3_);
      }
   };
   private final float data;

   private FloatNBT(float p_i45131_1_) {
      this.data = p_i45131_1_;
   }

   public static FloatNBT func_229689_a_(float p_229689_0_) {
      return p_229689_0_ == 0.0F ? field_229687_a_ : new FloatNBT(p_229689_0_);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      p_74734_1_.writeFloat(this.data);
   }

   public byte getId() {
      return 5;
   }

   public INBTType<FloatNBT> func_225647_b_() {
      return field_229688_b_;
   }

   public String toString() {
      return this.data + "f";
   }

   public FloatNBT copy() {
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof FloatNBT && this.data == ((FloatNBT)p_equals_1_).data;
      }
   }

   public int hashCode() {
      return Float.floatToIntBits(this.data);
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      ITextComponent lvt_3_1_ = (new StringTextComponent("f")).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new StringTextComponent(String.valueOf(this.data))).appendSibling(lvt_3_1_).applyTextStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getLong() {
      return (long)this.data;
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
      return (double)this.data;
   }

   public float getFloat() {
      return this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }

   // $FF: synthetic method
   public INBT copy() {
      return this.copy();
   }
}
