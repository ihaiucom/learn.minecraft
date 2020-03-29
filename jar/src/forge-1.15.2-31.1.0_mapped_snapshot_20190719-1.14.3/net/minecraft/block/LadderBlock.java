package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class LadderBlock extends Block implements IWaterLoggable {
   public static final DirectionProperty FACING;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape LADDER_EAST_AABB;
   protected static final VoxelShape LADDER_WEST_AABB;
   protected static final VoxelShape LADDER_SOUTH_AABB;
   protected static final VoxelShape LADDER_NORTH_AABB;

   protected LadderBlock(Block.Properties p_i48371_1_) {
      super(p_i48371_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((Direction)p_220053_1_.get(FACING)) {
      case NORTH:
         return LADDER_NORTH_AABB;
      case SOUTH:
         return LADDER_SOUTH_AABB;
      case WEST:
         return LADDER_WEST_AABB;
      case EAST:
      default:
         return LADDER_EAST_AABB;
      }
   }

   private boolean canAttachTo(IBlockReader p_196471_1_, BlockPos p_196471_2_, Direction p_196471_3_) {
      BlockState blockstate = p_196471_1_.getBlockState(p_196471_2_);
      return !blockstate.canProvidePower() && blockstate.func_224755_d(p_196471_1_, p_196471_2_, p_196471_3_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Direction direction = (Direction)p_196260_1_.get(FACING);
      return this.canAttachTo(p_196260_2_, p_196260_3_.offset(direction.getOpposite()), direction);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_.getOpposite() == p_196271_1_.get(FACING) && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
            p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
         }

         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate1;
      if (!p_196258_1_.replacingClickedOnBlock()) {
         blockstate1 = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos().offset(p_196258_1_.getFace().getOpposite()));
         if (blockstate1.getBlock() == this && blockstate1.get(FACING) == p_196258_1_.getFace()) {
            return null;
         }
      }

      blockstate1 = this.getDefaultState();
      IWorldReader iworldreader = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      Direction[] var6 = p_196258_1_.getNearestLookingDirections();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction direction = var6[var8];
         if (direction.getAxis().isHorizontal()) {
            blockstate1 = (BlockState)blockstate1.with(FACING, direction.getOpposite());
            if (blockstate1.isValidPosition(iworldreader, blockpos)) {
               return (BlockState)blockstate1.with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
            }
         }
      }

      return null;
   }

   public boolean isLadder(BlockState p_isLadder_1_, IWorldReader p_isLadder_2_, BlockPos p_isLadder_3_, LivingEntity p_isLadder_4_) {
      return true;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, WATERLOGGED);
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      LADDER_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
      LADDER_WEST_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      LADDER_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
      LADDER_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   }
}
