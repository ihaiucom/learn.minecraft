package net.minecraft.block;

import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;

public class GlazedTerracottaBlock extends HorizontalBlock {
   public GlazedTerracottaBlock(Block.Properties p_i48390_1_) {
      super(p_i48390_1_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.PUSH_ONLY;
   }
}
