package net.minecraft.world;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoliageColors {
   private static int[] foliageBuffer = new int[65536];

   public static void setFoliageBiomeColorizer(int[] p_77467_0_) {
      foliageBuffer = p_77467_0_;
   }

   public static int get(double p_77470_0_, double p_77470_2_) {
      p_77470_2_ *= p_77470_0_;
      int lvt_4_1_ = (int)((1.0D - p_77470_0_) * 255.0D);
      int lvt_5_1_ = (int)((1.0D - p_77470_2_) * 255.0D);
      return foliageBuffer[lvt_5_1_ << 8 | lvt_4_1_];
   }

   public static int getSpruce() {
      return 6396257;
   }

   public static int getBirch() {
      return 8431445;
   }

   public static int getDefault() {
      return 4764952;
   }
}
