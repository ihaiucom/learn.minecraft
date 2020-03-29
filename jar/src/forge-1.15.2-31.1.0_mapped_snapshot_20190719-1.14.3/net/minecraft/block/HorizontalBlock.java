package net.minecraft.block;

import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public abstract class HorizontalBlock extends Block {
   public static final DirectionProperty HORIZONTAL_FACING;

   protected HorizontalBlock(Block.Properties p_i48377_1_) {
      super(p_i48377_1_);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(HORIZONTAL_FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(HORIZONTAL_FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(HORIZONTAL_FACING)));
   }

   static {
      HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
   }
}
