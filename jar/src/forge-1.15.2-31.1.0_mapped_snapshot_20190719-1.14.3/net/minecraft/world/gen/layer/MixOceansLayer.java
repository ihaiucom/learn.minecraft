package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum MixOceansLayer implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   public int apply(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_) {
      int lvt_6_1_ = p_215723_2_.getValue(this.func_215721_a(p_215723_4_), this.func_215722_b(p_215723_5_));
      int lvt_7_1_ = p_215723_3_.getValue(this.func_215721_a(p_215723_4_), this.func_215722_b(p_215723_5_));
      if (!LayerUtil.isOcean(lvt_6_1_)) {
         return lvt_6_1_;
      } else {
         int lvt_8_1_ = true;
         int lvt_9_1_ = true;

         for(int lvt_10_1_ = -8; lvt_10_1_ <= 8; lvt_10_1_ += 4) {
            for(int lvt_11_1_ = -8; lvt_11_1_ <= 8; lvt_11_1_ += 4) {
               int lvt_12_1_ = p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + lvt_10_1_), this.func_215722_b(p_215723_5_ + lvt_11_1_));
               if (!LayerUtil.isOcean(lvt_12_1_)) {
                  if (lvt_7_1_ == LayerUtil.WARM_OCEAN) {
                     return LayerUtil.LUKEWARM_OCEAN;
                  }

                  if (lvt_7_1_ == LayerUtil.FROZEN_OCEAN) {
                     return LayerUtil.COLD_OCEAN;
                  }
               }
            }
         }

         if (lvt_6_1_ == LayerUtil.DEEP_OCEAN) {
            if (lvt_7_1_ == LayerUtil.LUKEWARM_OCEAN) {
               return LayerUtil.DEEP_LUKEWARM_OCEAN;
            }

            if (lvt_7_1_ == LayerUtil.OCEAN) {
               return LayerUtil.DEEP_OCEAN;
            }

            if (lvt_7_1_ == LayerUtil.COLD_OCEAN) {
               return LayerUtil.DEEP_COLD_OCEAN;
            }

            if (lvt_7_1_ == LayerUtil.FROZEN_OCEAN) {
               return LayerUtil.DEEP_FROZEN_OCEAN;
            }
         }

         return lvt_7_1_;
      }
   }
}
