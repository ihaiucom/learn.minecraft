package net.minecraft.world.chunk.storage;

public class NibbleArrayReader {
   public final byte[] data;
   private final int depthBits;
   private final int depthBitsPlusFour;

   public NibbleArrayReader(byte[] p_i1998_1_, int p_i1998_2_) {
      this.data = p_i1998_1_;
      this.depthBits = p_i1998_2_;
      this.depthBitsPlusFour = p_i1998_2_ + 4;
   }

   public int get(int p_76686_1_, int p_76686_2_, int p_76686_3_) {
      int lvt_4_1_ = p_76686_1_ << this.depthBitsPlusFour | p_76686_3_ << this.depthBits | p_76686_2_;
      int lvt_5_1_ = lvt_4_1_ >> 1;
      int lvt_6_1_ = lvt_4_1_ & 1;
      return lvt_6_1_ == 0 ? this.data[lvt_5_1_] & 15 : this.data[lvt_5_1_] >> 4 & 15;
   }
}
