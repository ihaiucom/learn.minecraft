package net.minecraft.client.util;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MouseSmoother {
   private double targetValue;
   private double remainingValue;
   private double lastAmount;

   public double smooth(double p_199102_1_, double p_199102_3_) {
      this.targetValue += p_199102_1_;
      double lvt_5_1_ = this.targetValue - this.remainingValue;
      double lvt_7_1_ = MathHelper.lerp(0.5D, this.lastAmount, lvt_5_1_);
      double lvt_9_1_ = Math.signum(lvt_5_1_);
      if (lvt_9_1_ * lvt_5_1_ > lvt_9_1_ * this.lastAmount) {
         lvt_5_1_ = lvt_7_1_;
      }

      this.lastAmount = lvt_7_1_;
      this.remainingValue += lvt_5_1_ * p_199102_3_;
      return lvt_5_1_ * p_199102_3_;
   }

   public void reset() {
      this.targetValue = 0.0D;
      this.remainingValue = 0.0D;
      this.lastAmount = 0.0D;
   }
}
