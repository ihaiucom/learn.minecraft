package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class TrapDoorBlock extends HorizontalBlock implements IWaterLoggable {
   public static final BooleanProperty OPEN;
   public static final EnumProperty<Half> HALF;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape EAST_OPEN_AABB;
   protected static final VoxelShape WEST_OPEN_AABB;
   protected static final VoxelShape SOUTH_OPEN_AABB;
   protected static final VoxelShape NORTH_OPEN_AABB;
   protected static final VoxelShape BOTTOM_AABB;
   protected static final VoxelShape TOP_AABB;

   protected TrapDoorBlock(Block.Properties p_i48307_1_) {
      super(p_i48307_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(OPEN, false)).with(HALF, Half.BOTTOM)).with(POWERED, false)).with(WATERLOGGED, false));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      if (!(Boolean)p_220053_1_.get(OPEN)) {
         return p_220053_1_.get(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
      } else {
         switch((Direction)p_220053_1_.get(HORIZONTAL_FACING)) {
         case NORTH:
         default:
            return NORTH_OPEN_AABB;
         case SOUTH:
            return SOUTH_OPEN_AABB;
         case WEST:
            return WEST_OPEN_AABB;
         case EAST:
            return EAST_OPEN_AABB;
         }
      }
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return (Boolean)p_196266_1_.get(OPEN);
      case WATER:
         return (Boolean)p_196266_1_.get(WATERLOGGED);
      case AIR:
         return (Boolean)p_196266_1_.get(OPEN);
      default:
         return false;
      }
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (this.material == Material.IRON) {
         return ActionResultType.PASS;
      } else {
         p_225533_1_ = (BlockState)p_225533_1_.cycle(OPEN);
         p_225533_2_.setBlockState(p_225533_3_, p_225533_1_, 2);
         if ((Boolean)p_225533_1_.get(WATERLOGGED)) {
            p_225533_2_.getPendingFluidTicks().scheduleTick(p_225533_3_, Fluids.WATER, Fluids.WATER.getTickRate(p_225533_2_));
         }

         this.playSound(p_225533_4_, p_225533_2_, p_225533_3_, (Boolean)p_225533_1_.get(OPEN));
         return ActionResultType.SUCCESS;
      }
   }

   protected void playSound(@Nullable PlayerEntity p_185731_1_, World p_185731_2_, BlockPos p_185731_3_, boolean p_185731_4_) {
      int i;
      if (p_185731_4_) {
         i = this.material == Material.IRON ? 1037 : 1007;
         p_185731_2_.playEvent(p_185731_1_, i, p_185731_3_, 0);
      } else {
         i = this.material == Material.IRON ? 1036 : 1013;
         p_185731_2_.playEvent(p_185731_1_, i, p_185731_3_, 0);
      }

   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isRemote) {
         boolean flag = p_220069_2_.isBlockPowered(p_220069_3_);
         if (flag != (Boolean)p_220069_1_.get(POWERED)) {
            if ((Boolean)p_220069_1_.get(OPEN) != flag) {
               p_220069_1_ = (BlockState)p_220069_1_.with(OPEN, flag);
               this.playSound((PlayerEntity)null, p_220069_2_, p_220069_3_, flag);
            }

            p_220069_2_.setBlockState(p_220069_3_, (BlockState)p_220069_1_.with(POWERED, flag), 2);
            if ((Boolean)p_220069_1_.get(WATERLOGGED)) {
               p_220069_2_.getPendingFluidTicks().scheduleTick(p_220069_3_, Fluids.WATER, Fluids.WATER.getTickRate(p_220069_2_));
            }
         }
      }

   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = this.getDefaultState();
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      Direction direction = p_196258_1_.getFace();
      if (!p_196258_1_.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
         blockstate = (BlockState)((BlockState)blockstate.with(HORIZONTAL_FACING, direction)).with(HALF, p_196258_1_.getHitVec().y - (double)p_196258_1_.getPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
      } else {
         blockstate = (BlockState)((BlockState)blockstate.with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite())).with(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
      }

      if (p_196258_1_.getWorld().isBlockPowered(p_196258_1_.getPos())) {
         blockstate = (BlockState)((BlockState)blockstate.with(OPEN, true)).with(POWERED, true);
      }

      return (BlockState)blockstate.with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, OPEN, HALF, POWERED, WATERLOGGED);
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isLadder(BlockState p_isLadder_1_, IWorldReader p_isLadder_2_, BlockPos p_isLadder_3_, LivingEntity p_isLadder_4_) {
      if ((Boolean)p_isLadder_1_.get(OPEN)) {
         BlockState down = p_isLadder_2_.getBlockState(p_isLadder_3_.down());
         if (down.getBlock() == Blocks.LADDER) {
            return down.get(LadderBlock.FACING) == p_isLadder_1_.get(HORIZONTAL_FACING);
         }
      }

      return false;
   }

   public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return false;
   }

   static {
      OPEN = BlockStateProperties.OPEN;
      HALF = BlockStateProperties.HALF;
      POWERED = BlockStateProperties.POWERED;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      EAST_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
      WEST_OPEN_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      SOUTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
      NORTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
      BOTTOM_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
      TOP_AABB = Block.makeCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   }
}
