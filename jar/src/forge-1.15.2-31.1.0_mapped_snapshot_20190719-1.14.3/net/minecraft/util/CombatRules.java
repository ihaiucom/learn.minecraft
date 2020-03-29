package net.minecraft.util;

import net.minecraft.util.math.MathHelper;

public class CombatRules {
   public static float getDamageAfterAbsorb(float p_189427_0_, float p_189427_1_, float p_189427_2_) {
      float lvt_3_1_ = 2.0F + p_189427_2_ / 4.0F;
      float lvt_4_1_ = MathHelper.clamp(p_189427_1_ - p_189427_0_ / lvt_3_1_, p_189427_1_ * 0.2F, 20.0F);
      return p_189427_0_ * (1.0F - lvt_4_1_ / 25.0F);
   }

   public static float getDamageAfterMagicAbsorb(float p_188401_0_, float p_188401_1_) {
      float lvt_2_1_ = MathHelper.clamp(p_188401_1_, 0.0F, 20.0F);
      return p_188401_0_ * (1.0F - lvt_2_1_ / 25.0F);
   }
}
