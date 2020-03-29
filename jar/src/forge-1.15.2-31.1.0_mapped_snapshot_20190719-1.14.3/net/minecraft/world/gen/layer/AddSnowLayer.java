package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum AddSnowLayer implements IC1Transformer {
   INSTANCE;

   public int apply(INoiseRandom p_202716_1_, int p_202716_2_) {
      if (LayerUtil.isShallowOcean(p_202716_2_)) {
         return p_202716_2_;
      } else {
         int lvt_3_1_ = p_202716_1_.random(6);
         if (lvt_3_1_ == 0) {
            return 4;
         } else {
            return lvt_3_1_ == 1 ? 3 : 1;
         }
      }
   }
}
