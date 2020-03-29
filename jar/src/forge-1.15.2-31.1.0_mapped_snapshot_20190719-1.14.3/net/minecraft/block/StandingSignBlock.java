package net.minecraft.block;

import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class StandingSignBlock extends AbstractSignBlock {
   public static final IntegerProperty ROTATION;

   public StandingSignBlock(Block.Properties p_i225764_1_, WoodType p_i225764_2_) {
      super(p_i225764_1_, p_i225764_2_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(ROTATION, 0)).with(WATERLOGGED, false));
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.down()).getMaterial().isSolid();
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IFluidState lvt_2_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      return (BlockState)((BlockState)this.getDefaultState().with(ROTATION, MathHelper.floor((double)((180.0F + p_196258_1_.getPlacementYaw()) * 16.0F / 360.0F) + 0.5D) & 15)).with(WATERLOGGED, lvt_2_1_.getFluid() == Fluids.WATER);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == Direction.DOWN && !this.isValidPosition(p_196271_1_, p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(ROTATION, p_185499_2_.rotate((Integer)p_185499_1_.get(ROTATION), 16));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return (BlockState)p_185471_1_.with(ROTATION, p_185471_2_.mirrorRotation((Integer)p_185471_1_.get(ROTATION), 16));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(ROTATION, WATERLOGGED);
   }

   static {
      ROTATION = BlockStateProperties.ROTATION_0_15;
   }
}
