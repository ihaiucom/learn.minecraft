package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SnowyDirtBlock extends Block {
   public static final BooleanProperty SNOWY;

   protected SnowyDirtBlock(Block.Properties p_i48327_1_) {
      super(p_i48327_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(SNOWY, false));
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ != Direction.UP) {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         Block lvt_7_1_ = p_196271_3_.getBlock();
         return (BlockState)p_196271_1_.with(SNOWY, lvt_7_1_ == Blocks.SNOW_BLOCK || lvt_7_1_ == Blocks.SNOW);
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Block lvt_2_1_ = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos().up()).getBlock();
      return (BlockState)this.getDefaultState().with(SNOWY, lvt_2_1_ == Blocks.SNOW_BLOCK || lvt_2_1_ == Blocks.SNOW);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(SNOWY);
   }

   static {
      SNOWY = BlockStateProperties.SNOWY;
   }
}
