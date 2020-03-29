package net.minecraft.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelUtils {
   public static float func_228283_a_(float p_228283_0_, float p_228283_1_, float p_228283_2_) {
      float lvt_3_1_;
      for(lvt_3_1_ = p_228283_1_ - p_228283_0_; lvt_3_1_ < -3.1415927F; lvt_3_1_ += 6.2831855F) {
      }

      while(lvt_3_1_ >= 3.1415927F) {
         lvt_3_1_ -= 6.2831855F;
      }

      return p_228283_0_ + p_228283_2_ * lvt_3_1_;
   }
}
