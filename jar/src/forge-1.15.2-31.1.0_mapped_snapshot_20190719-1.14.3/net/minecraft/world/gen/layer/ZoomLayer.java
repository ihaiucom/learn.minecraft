package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public enum ZoomLayer implements IAreaTransformer1 {
   NORMAL,
   FUZZY {
      protected int func_202715_a(IExtendedNoiseRandom<?> p_202715_1_, int p_202715_2_, int p_202715_3_, int p_202715_4_, int p_202715_5_) {
         return p_202715_1_.func_215714_a(p_202715_2_, p_202715_3_, p_202715_4_, p_202715_5_);
      }
   };

   private ZoomLayer() {
   }

   public int func_215721_a(int p_215721_1_) {
      return p_215721_1_ >> 1;
   }

   public int func_215722_b(int p_215722_1_) {
      return p_215722_1_ >> 1;
   }

   public int func_215728_a(IExtendedNoiseRandom<?> p_215728_1_, IArea p_215728_2_, int p_215728_3_, int p_215728_4_) {
      int lvt_5_1_ = p_215728_2_.getValue(this.func_215721_a(p_215728_3_), this.func_215722_b(p_215728_4_));
      p_215728_1_.setPosition((long)(p_215728_3_ >> 1 << 1), (long)(p_215728_4_ >> 1 << 1));
      int lvt_6_1_ = p_215728_3_ & 1;
      int lvt_7_1_ = p_215728_4_ & 1;
      if (lvt_6_1_ == 0 && lvt_7_1_ == 0) {
         return lvt_5_1_;
      } else {
         int lvt_8_1_ = p_215728_2_.getValue(this.func_215721_a(p_215728_3_), this.func_215722_b(p_215728_4_ + 1));
         int lvt_9_1_ = p_215728_1_.func_215715_a(lvt_5_1_, lvt_8_1_);
         if (lvt_6_1_ == 0 && lvt_7_1_ == 1) {
            return lvt_9_1_;
         } else {
            int lvt_10_1_ = p_215728_2_.getValue(this.func_215721_a(p_215728_3_ + 1), this.func_215722_b(p_215728_4_));
            int lvt_11_1_ = p_215728_1_.func_215715_a(lvt_5_1_, lvt_10_1_);
            if (lvt_6_1_ == 1 && lvt_7_1_ == 0) {
               return lvt_11_1_;
            } else {
               int lvt_12_1_ = p_215728_2_.getValue(this.func_215721_a(p_215728_3_ + 1), this.func_215722_b(p_215728_4_ + 1));
               return this.func_202715_a(p_215728_1_, lvt_5_1_, lvt_10_1_, lvt_8_1_, lvt_12_1_);
            }
         }
      }
   }

   protected int func_202715_a(IExtendedNoiseRandom<?> p_202715_1_, int p_202715_2_, int p_202715_3_, int p_202715_4_, int p_202715_5_) {
      if (p_202715_3_ == p_202715_4_ && p_202715_4_ == p_202715_5_) {
         return p_202715_3_;
      } else if (p_202715_2_ == p_202715_3_ && p_202715_2_ == p_202715_4_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_3_ && p_202715_2_ == p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_4_ && p_202715_2_ == p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_3_ && p_202715_4_ != p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_4_ && p_202715_3_ != p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_5_ && p_202715_3_ != p_202715_4_) {
         return p_202715_2_;
      } else if (p_202715_3_ == p_202715_4_ && p_202715_2_ != p_202715_5_) {
         return p_202715_3_;
      } else if (p_202715_3_ == p_202715_5_ && p_202715_2_ != p_202715_4_) {
         return p_202715_3_;
      } else {
         return p_202715_4_ == p_202715_5_ && p_202715_2_ != p_202715_3_ ? p_202715_4_ : p_202715_1_.func_215714_a(p_202715_2_, p_202715_3_, p_202715_4_, p_202715_5_);
      }
   }

   // $FF: synthetic method
   ZoomLayer(Object p_i48841_3_) {
      this();
   }
}
