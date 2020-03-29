package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HayBlock extends RotatedPillarBlock {
   public HayBlock(Block.Properties p_i48380_1_) {
      super(p_i48380_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AXIS, Direction.Axis.Y));
   }

   public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      p_180658_3_.func_225503_b_(p_180658_4_, 0.2F);
   }
}
