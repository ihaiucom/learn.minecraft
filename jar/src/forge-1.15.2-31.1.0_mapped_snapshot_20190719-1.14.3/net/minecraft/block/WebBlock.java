package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class WebBlock extends Block implements IShearable {
   public WebBlock(Block.Properties p_i48296_1_) {
      super(p_i48296_1_);
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      p_196262_4_.setMotionMultiplier(p_196262_1_, new Vec3d(0.25D, 0.05000000074505806D, 0.25D));
   }
}
