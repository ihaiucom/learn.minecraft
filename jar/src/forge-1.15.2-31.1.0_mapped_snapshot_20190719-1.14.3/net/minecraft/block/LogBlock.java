package net.minecraft.block;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class LogBlock extends RotatedPillarBlock {
   private final MaterialColor verticalColor;

   public LogBlock(MaterialColor p_i48367_1_, Block.Properties p_i48367_2_) {
      super(p_i48367_2_);
      this.verticalColor = p_i48367_1_;
   }

   public MaterialColor getMaterialColor(BlockState p_180659_1_, IBlockReader p_180659_2_, BlockPos p_180659_3_) {
      return p_180659_1_.get(AXIS) == Direction.Axis.Y ? this.verticalColor : this.materialColor;
   }
}
