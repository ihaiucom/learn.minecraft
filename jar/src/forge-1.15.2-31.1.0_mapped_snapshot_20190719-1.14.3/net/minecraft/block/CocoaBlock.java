package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

public class CocoaBlock extends HorizontalBlock implements IGrowable {
   public static final IntegerProperty AGE;
   protected static final VoxelShape[] COCOA_EAST_AABB;
   protected static final VoxelShape[] COCOA_WEST_AABB;
   protected static final VoxelShape[] COCOA_NORTH_AABB;
   protected static final VoxelShape[] COCOA_SOUTH_AABB;

   public CocoaBlock(Block.Properties p_i48426_1_) {
      super(p_i48426_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(AGE, 0));
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      int i = (Integer)p_225534_1_.get(AGE);
      if (i < 2 && ForgeHooks.onCropsGrowPre(p_225534_2_, p_225534_3_, p_225534_1_, p_225534_2_.rand.nextInt(5) == 0)) {
         p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(AGE, i + 1), 2);
         ForgeHooks.onCropsGrowPost(p_225534_2_, p_225534_3_, p_225534_1_);
      }

   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Block block = p_196260_2_.getBlockState(p_196260_3_.offset((Direction)p_196260_1_.get(HORIZONTAL_FACING))).getBlock();
      return block.isIn(BlockTags.JUNGLE_LOGS);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      int i = (Integer)p_220053_1_.get(AGE);
      switch((Direction)p_220053_1_.get(HORIZONTAL_FACING)) {
      case SOUTH:
         return COCOA_SOUTH_AABB[i];
      case NORTH:
      default:
         return COCOA_NORTH_AABB[i];
      case WEST:
         return COCOA_WEST_AABB[i];
      case EAST:
         return COCOA_EAST_AABB[i];
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = this.getDefaultState();
      IWorldReader iworldreader = p_196258_1_.getWorld();
      BlockPos blockpos = p_196258_1_.getPos();
      Direction[] var5 = p_196258_1_.getNearestLookingDirections();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction = var5[var7];
         if (direction.getAxis().isHorizontal()) {
            blockstate = (BlockState)blockstate.with(HORIZONTAL_FACING, direction);
            if (blockstate.isValidPosition(iworldreader, blockpos)) {
               return blockstate;
            }
         }
      }

      return null;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == p_196271_1_.get(HORIZONTAL_FACING) && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return (Integer)p_176473_3_.get(AGE) < 2;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      p_225535_1_.setBlockState(p_225535_3_, (BlockState)p_225535_4_.with(AGE, (Integer)p_225535_4_.get(AGE) + 1), 2);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, AGE);
   }

   static {
      AGE = BlockStateProperties.AGE_0_2;
      COCOA_EAST_AABB = new VoxelShape[]{Block.makeCuboidShape(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D), Block.makeCuboidShape(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D), Block.makeCuboidShape(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)};
      COCOA_WEST_AABB = new VoxelShape[]{Block.makeCuboidShape(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D), Block.makeCuboidShape(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D), Block.makeCuboidShape(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)};
      COCOA_NORTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D), Block.makeCuboidShape(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D), Block.makeCuboidShape(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)};
      COCOA_SOUTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D), Block.makeCuboidShape(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D), Block.makeCuboidShape(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)};
   }
}
