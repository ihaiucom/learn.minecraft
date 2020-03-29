package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
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

public class CakeBlock extends Block {
   public static final IntegerProperty BITES;
   protected static final VoxelShape[] SHAPES;

   protected CakeBlock(Block.Properties p_i48434_1_) {
      super(p_i48434_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(BITES, 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPES[(Integer)p_220053_1_.get(BITES)];
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         ItemStack lvt_7_1_ = p_225533_4_.getHeldItem(p_225533_5_);
         if (this.func_226911_a_(p_225533_2_, p_225533_3_, p_225533_1_, p_225533_4_) == ActionResultType.SUCCESS) {
            return ActionResultType.SUCCESS;
         }

         if (lvt_7_1_.isEmpty()) {
            return ActionResultType.CONSUME;
         }
      }

      return this.func_226911_a_(p_225533_2_, p_225533_3_, p_225533_1_, p_225533_4_);
   }

   private ActionResultType func_226911_a_(IWorld p_226911_1_, BlockPos p_226911_2_, BlockState p_226911_3_, PlayerEntity p_226911_4_) {
      if (!p_226911_4_.canEat(false)) {
         return ActionResultType.PASS;
      } else {
         p_226911_4_.addStat(Stats.EAT_CAKE_SLICE);
         p_226911_4_.getFoodStats().addStats(2, 0.1F);
         int lvt_5_1_ = (Integer)p_226911_3_.get(BITES);
         if (lvt_5_1_ < 6) {
            p_226911_1_.setBlockState(p_226911_2_, (BlockState)p_226911_3_.with(BITES, lvt_5_1_ + 1), 3);
         } else {
            p_226911_1_.removeBlock(p_226911_2_, false);
         }

         return ActionResultType.SUCCESS;
      }
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == Direction.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.down()).getMaterial().isSolid();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(BITES);
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return (7 - (Integer)p_180641_1_.get(BITES)) * 2;
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      BITES = BlockStateProperties.BITES_0_6;
      SHAPES = new VoxelShape[]{Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};
   }
}
