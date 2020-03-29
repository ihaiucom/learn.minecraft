package net.minecraft.block;

import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class WallBlock extends FourWayBlock {
   public static final BooleanProperty UP;
   private final VoxelShape[] wallShapes;
   private final VoxelShape[] wallCollisionShapes;

   public WallBlock(Block.Properties p_i48301_1_) {
      super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, p_i48301_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(UP, true)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
      this.wallShapes = this.makeShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
      this.wallCollisionShapes = this.makeShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (Boolean)p_220053_1_.get(UP) ? this.wallShapes[this.getIndex(p_220053_1_)] : super.getShape(p_220053_1_, p_220053_2_, p_220053_3_, p_220053_4_);
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return (Boolean)p_220071_1_.get(UP) ? this.wallCollisionShapes[this.getIndex(p_220071_1_)] : super.getCollisionShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   private boolean func_220113_a(BlockState p_220113_1_, boolean p_220113_2_, Direction p_220113_3_) {
      Block lvt_4_1_ = p_220113_1_.getBlock();
      boolean lvt_5_1_ = lvt_4_1_.isIn(BlockTags.WALLS) || lvt_4_1_ instanceof FenceGateBlock && FenceGateBlock.isParallel(p_220113_1_, p_220113_3_);
      return !cannotAttach(lvt_4_1_) && p_220113_2_ || lvt_5_1_;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IWorldReader lvt_2_1_ = p_196258_1_.getWorld();
      BlockPos lvt_3_1_ = p_196258_1_.getPos();
      IFluidState lvt_4_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      BlockPos lvt_5_1_ = lvt_3_1_.north();
      BlockPos lvt_6_1_ = lvt_3_1_.east();
      BlockPos lvt_7_1_ = lvt_3_1_.south();
      BlockPos lvt_8_1_ = lvt_3_1_.west();
      BlockState lvt_9_1_ = lvt_2_1_.getBlockState(lvt_5_1_);
      BlockState lvt_10_1_ = lvt_2_1_.getBlockState(lvt_6_1_);
      BlockState lvt_11_1_ = lvt_2_1_.getBlockState(lvt_7_1_);
      BlockState lvt_12_1_ = lvt_2_1_.getBlockState(lvt_8_1_);
      boolean lvt_13_1_ = this.func_220113_a(lvt_9_1_, lvt_9_1_.func_224755_d(lvt_2_1_, lvt_5_1_, Direction.SOUTH), Direction.SOUTH);
      boolean lvt_14_1_ = this.func_220113_a(lvt_10_1_, lvt_10_1_.func_224755_d(lvt_2_1_, lvt_6_1_, Direction.WEST), Direction.WEST);
      boolean lvt_15_1_ = this.func_220113_a(lvt_11_1_, lvt_11_1_.func_224755_d(lvt_2_1_, lvt_7_1_, Direction.NORTH), Direction.NORTH);
      boolean lvt_16_1_ = this.func_220113_a(lvt_12_1_, lvt_12_1_.func_224755_d(lvt_2_1_, lvt_8_1_, Direction.EAST), Direction.EAST);
      boolean lvt_17_1_ = (!lvt_13_1_ || lvt_14_1_ || !lvt_15_1_ || lvt_16_1_) && (lvt_13_1_ || !lvt_14_1_ || lvt_15_1_ || !lvt_16_1_);
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(UP, lvt_17_1_ || !lvt_2_1_.isAirBlock(lvt_3_1_.up()))).with(NORTH, lvt_13_1_)).with(EAST, lvt_14_1_)).with(SOUTH, lvt_15_1_)).with(WEST, lvt_16_1_)).with(WATERLOGGED, lvt_4_1_.getFluid() == Fluids.WATER);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      if (p_196271_2_ == Direction.DOWN) {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         Direction lvt_7_1_ = p_196271_2_.getOpposite();
         boolean lvt_8_1_ = p_196271_2_ == Direction.NORTH ? this.func_220113_a(p_196271_3_, p_196271_3_.func_224755_d(p_196271_4_, p_196271_6_, lvt_7_1_), lvt_7_1_) : (Boolean)p_196271_1_.get(NORTH);
         boolean lvt_9_1_ = p_196271_2_ == Direction.EAST ? this.func_220113_a(p_196271_3_, p_196271_3_.func_224755_d(p_196271_4_, p_196271_6_, lvt_7_1_), lvt_7_1_) : (Boolean)p_196271_1_.get(EAST);
         boolean lvt_10_1_ = p_196271_2_ == Direction.SOUTH ? this.func_220113_a(p_196271_3_, p_196271_3_.func_224755_d(p_196271_4_, p_196271_6_, lvt_7_1_), lvt_7_1_) : (Boolean)p_196271_1_.get(SOUTH);
         boolean lvt_11_1_ = p_196271_2_ == Direction.WEST ? this.func_220113_a(p_196271_3_, p_196271_3_.func_224755_d(p_196271_4_, p_196271_6_, lvt_7_1_), lvt_7_1_) : (Boolean)p_196271_1_.get(WEST);
         boolean lvt_12_1_ = (!lvt_8_1_ || lvt_9_1_ || !lvt_10_1_ || lvt_11_1_) && (lvt_8_1_ || !lvt_9_1_ || lvt_10_1_ || !lvt_11_1_);
         return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)p_196271_1_.with(UP, lvt_12_1_ || !p_196271_4_.isAirBlock(p_196271_5_.up()))).with(NORTH, lvt_8_1_)).with(EAST, lvt_9_1_)).with(SOUTH, lvt_10_1_)).with(WEST, lvt_11_1_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }

   static {
      UP = BlockStateProperties.UP;
   }
}
