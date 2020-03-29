package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

public class RotatedPillarBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS;

   public RotatedPillarBlock(Block.Properties p_i48339_1_) {
      super(p_i48339_1_);
      this.setDefaultState((BlockState)this.getDefaultState().with(AXIS, Direction.Axis.Y));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Direction.Axis)p_185499_1_.get(AXIS)) {
         case X:
            return (BlockState)p_185499_1_.with(AXIS, Direction.Axis.Z);
         case Z:
            return (BlockState)p_185499_1_.with(AXIS, Direction.Axis.X);
         default:
            return p_185499_1_;
         }
      default:
         return p_185499_1_;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AXIS);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(AXIS, p_196258_1_.getFace().getAxis());
   }

   static {
      AXIS = BlockStateProperties.AXIS;
   }
}
