package net.minecraft.world;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrassColors {
   private static int[] grassBuffer = new int[65536];

   public static void setGrassBiomeColorizer(int[] p_77479_0_) {
      grassBuffer = p_77479_0_;
   }

   public static int get(double p_77480_0_, double p_77480_2_) {
      p_77480_2_ *= p_77480_0_;
      int lvt_4_1_ = (int)((1.0D - p_77480_0_) * 255.0D);
      int lvt_5_1_ = (int)((1.0D - p_77480_2_) * 255.0D);
      int lvt_6_1_ = lvt_5_1_ << 8 | lvt_4_1_;
      return lvt_6_1_ > grassBuffer.length ? -65281 : grassBuffer[lvt_6_1_];
   }
}
