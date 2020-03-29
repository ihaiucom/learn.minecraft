package net.minecraft.block;

import com.google.common.base.Predicates;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class EndPortalFrameBlock extends Block {
   public static final DirectionProperty FACING;
   public static final BooleanProperty EYE;
   protected static final VoxelShape BASE_SHAPE;
   protected static final VoxelShape EYE_SHAPE;
   protected static final VoxelShape BASE_WITH_EYE_SHAPE;
   private static BlockPattern portalShape;

   public EndPortalFrameBlock(Block.Properties p_i48405_1_) {
      super(p_i48405_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(EYE, false));
   }

   public boolean func_220074_n(BlockState p_220074_1_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (Boolean)p_220053_1_.get(EYE) ? BASE_WITH_EYE_SHAPE : BASE_SHAPE;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)((BlockState)this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite())).with(EYE, false);
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return (Boolean)p_180641_1_.get(EYE) ? 15 : 0;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, EYE);
   }

   public static BlockPattern getOrCreatePortalShape() {
      if (portalShape == null) {
         portalShape = BlockPatternBuilder.start().aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?").where('?', CachedBlockInfo.hasState(BlockStateMatcher.ANY)).where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.END_PORTAL_FRAME).where(EYE, Predicates.equalTo(true)).where(FACING, Predicates.equalTo(Direction.SOUTH)))).where('>', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.END_PORTAL_FRAME).where(EYE, Predicates.equalTo(true)).where(FACING, Predicates.equalTo(Direction.WEST)))).where('v', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.END_PORTAL_FRAME).where(EYE, Predicates.equalTo(true)).where(FACING, Predicates.equalTo(Direction.NORTH)))).where('<', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.END_PORTAL_FRAME).where(EYE, Predicates.equalTo(true)).where(FACING, Predicates.equalTo(Direction.EAST)))).build();
      }

      return portalShape;
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      EYE = BlockStateProperties.EYE;
      BASE_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
      EYE_SHAPE = Block.makeCuboidShape(4.0D, 13.0D, 4.0D, 12.0D, 16.0D, 12.0D);
      BASE_WITH_EYE_SHAPE = VoxelShapes.or(BASE_SHAPE, EYE_SHAPE);
   }
}
