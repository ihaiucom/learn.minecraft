package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.Util;

public class NibbleArray {
   @Nullable
   protected byte[] data;

   public NibbleArray() {
   }

   public NibbleArray(byte[] p_i45646_1_) {
      this.data = p_i45646_1_;
      if (p_i45646_1_.length != 2048) {
         throw (IllegalArgumentException)Util.func_229757_c_(new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + p_i45646_1_.length));
      }
   }

   protected NibbleArray(int p_i49951_1_) {
      this.data = new byte[p_i49951_1_];
   }

   public int get(int p_76582_1_, int p_76582_2_, int p_76582_3_) {
      return this.getFromIndex(this.getCoordinateIndex(p_76582_1_, p_76582_2_, p_76582_3_));
   }

   public void set(int p_76581_1_, int p_76581_2_, int p_76581_3_, int p_76581_4_) {
      this.setIndex(this.getCoordinateIndex(p_76581_1_, p_76581_2_, p_76581_3_), p_76581_4_);
   }

   protected int getCoordinateIndex(int p_177483_1_, int p_177483_2_, int p_177483_3_) {
      return p_177483_2_ << 8 | p_177483_3_ << 4 | p_177483_1_;
   }

   private int getFromIndex(int p_177480_1_) {
      if (this.data == null) {
         return 0;
      } else {
         int lvt_2_1_ = this.getNibbleIndex(p_177480_1_);
         return this.isLowerNibble(p_177480_1_) ? this.data[lvt_2_1_] & 15 : this.data[lvt_2_1_] >> 4 & 15;
      }
   }

   private void setIndex(int p_177482_1_, int p_177482_2_) {
      if (this.data == null) {
         this.data = new byte[2048];
      }

      int lvt_3_1_ = this.getNibbleIndex(p_177482_1_);
      if (this.isLowerNibble(p_177482_1_)) {
         this.data[lvt_3_1_] = (byte)(this.data[lvt_3_1_] & 240 | p_177482_2_ & 15);
      } else {
         this.data[lvt_3_1_] = (byte)(this.data[lvt_3_1_] & 15 | (p_177482_2_ & 15) << 4);
      }

   }

   private boolean isLowerNibble(int p_177479_1_) {
      return (p_177479_1_ & 1) == 0;
   }

   private int getNibbleIndex(int p_177478_1_) {
      return p_177478_1_ >> 1;
   }

   public byte[] getData() {
      if (this.data == null) {
         this.data = new byte[2048];
      }

      return this.data;
   }

   public NibbleArray copy() {
      return this.data == null ? new NibbleArray() : new NibbleArray((byte[])this.data.clone());
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder();

      for(int lvt_2_1_ = 0; lvt_2_1_ < 4096; ++lvt_2_1_) {
         lvt_1_1_.append(Integer.toHexString(this.getFromIndex(lvt_2_1_)));
         if ((lvt_2_1_ & 15) == 15) {
            lvt_1_1_.append("\n");
         }

         if ((lvt_2_1_ & 255) == 255) {
            lvt_1_1_.append("\n");
         }
      }

      return lvt_1_1_.toString();
   }

   public boolean isEmpty() {
      return this.data == null;
   }
}
