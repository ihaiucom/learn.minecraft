package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class DeadCoralWallFanBlock extends CoralFanBlock {
   public static final DirectionProperty FACING;
   private static final Map<Direction, VoxelShape> SHAPES;

   protected DeadCoralWallFanBlock(Block.Properties p_i49776_1_) {
      super(p_i49776_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, true));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (VoxelShape)SHAPES.get(p_220053_1_.get(FACING));
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

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return p_196271_2_.getOpposite() == p_196271_1_.get(FACING) && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : p_196271_1_;
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Direction lvt_4_1_ = (Direction)p_196260_1_.get(FACING);
      BlockPos lvt_5_1_ = p_196260_3_.offset(lvt_4_1_.getOpposite());
      BlockState lvt_6_1_ = p_196260_2_.getBlockState(lvt_5_1_);
      return lvt_6_1_.func_224755_d(p_196260_2_, lvt_5_1_, lvt_4_1_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState lvt_2_1_ = super.getStateForPlacement(p_196258_1_);
      IWorldReader lvt_3_1_ = p_196258_1_.getWorld();
      BlockPos lvt_4_1_ = p_196258_1_.getPos();
      Direction[] lvt_5_1_ = p_196258_1_.getNearestLookingDirections();
      Direction[] var6 = lvt_5_1_;
      int var7 = lvt_5_1_.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction lvt_9_1_ = var6[var8];
         if (lvt_9_1_.getAxis().isHorizontal()) {
            lvt_2_1_ = (BlockState)lvt_2_1_.with(FACING, lvt_9_1_.getOpposite());
            if (lvt_2_1_.isValidPosition(lvt_3_1_, lvt_4_1_)) {
               return lvt_2_1_;
            }
         }
      }

      return null;
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(0.0D, 4.0D, 5.0D, 16.0D, 12.0D, 16.0D), Direction.SOUTH, Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 11.0D), Direction.WEST, Block.makeCuboidShape(5.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D), Direction.EAST, Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 11.0D, 12.0D, 16.0D)));
   }
}
