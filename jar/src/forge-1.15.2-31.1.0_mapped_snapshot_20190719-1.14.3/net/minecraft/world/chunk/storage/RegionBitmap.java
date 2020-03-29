package net.minecraft.world.chunk.storage;

import java.util.BitSet;

public class RegionBitmap {
   private final BitSet field_227118_a_ = new BitSet();

   public void func_227120_a_(int p_227120_1_, int p_227120_2_) {
      this.field_227118_a_.set(p_227120_1_, p_227120_1_ + p_227120_2_);
   }

   public void func_227121_b_(int p_227121_1_, int p_227121_2_) {
      this.field_227118_a_.clear(p_227121_1_, p_227121_1_ + p_227121_2_);
   }

   public int func_227119_a_(int p_227119_1_) {
      int lvt_2_1_ = 0;

      while(true) {
         int lvt_3_1_ = this.field_227118_a_.nextClearBit(lvt_2_1_);
         int lvt_4_1_ = this.field_227118_a_.nextSetBit(lvt_3_1_);
         if (lvt_4_1_ == -1 || lvt_4_1_ - lvt_3_1_ >= p_227119_1_) {
            this.func_227120_a_(lvt_3_1_, p_227119_1_);
            return lvt_3_1_;
         }

         lvt_2_1_ = lvt_4_1_;
      }
   }
}
