package net.minecraft.block;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SandBlock extends FallingBlock {
   private final int dustColor;

   public SandBlock(int p_i48338_1_, Block.Properties p_i48338_2_) {
      super(p_i48338_2_);
      this.dustColor = p_i48338_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState p_189876_1_) {
      return this.dustColor;
   }
}
