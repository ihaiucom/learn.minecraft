package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTimeManager {
   private final long[] field_228729_a_;
   private int field_228730_b_;
   private int field_228731_c_;

   public RenderTimeManager(int p_i225998_1_) {
      this.field_228729_a_ = new long[p_i225998_1_];
   }

   public long func_228732_a_(long p_228732_1_) {
      if (this.field_228730_b_ < this.field_228729_a_.length) {
         ++this.field_228730_b_;
      }

      this.field_228729_a_[this.field_228731_c_] = p_228732_1_;
      this.field_228731_c_ = (this.field_228731_c_ + 1) % this.field_228729_a_.length;
      long lvt_3_1_ = Long.MAX_VALUE;
      long lvt_5_1_ = Long.MIN_VALUE;
      long lvt_7_1_ = 0L;

      for(int lvt_9_1_ = 0; lvt_9_1_ < this.field_228730_b_; ++lvt_9_1_) {
         long lvt_10_1_ = this.field_228729_a_[lvt_9_1_];
         lvt_7_1_ += lvt_10_1_;
         lvt_3_1_ = Math.min(lvt_3_1_, lvt_10_1_);
         lvt_5_1_ = Math.max(lvt_5_1_, lvt_10_1_);
      }

      if (this.field_228730_b_ > 2) {
         lvt_7_1_ -= lvt_3_1_ + lvt_5_1_;
         return lvt_7_1_ / (long)(this.field_228730_b_ - 2);
      } else {
         return lvt_7_1_ > 0L ? (long)this.field_228730_b_ / lvt_7_1_ : 0L;
      }
   }
}
