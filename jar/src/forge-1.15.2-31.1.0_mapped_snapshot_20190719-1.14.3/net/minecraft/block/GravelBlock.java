package net.minecraft.block;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GravelBlock extends FallingBlock {
   public GravelBlock(Block.Properties p_i48384_1_) {
      super(p_i48384_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState p_189876_1_) {
      return -8356741;
   }
}
