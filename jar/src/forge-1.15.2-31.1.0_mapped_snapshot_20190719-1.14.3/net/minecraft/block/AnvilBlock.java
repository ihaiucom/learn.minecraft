package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AnvilBlock extends FallingBlock {
   public static final DirectionProperty FACING;
   private static final VoxelShape PART_BASE;
   private static final VoxelShape PART_LOWER_X;
   private static final VoxelShape PART_MID_X;
   private static final VoxelShape PART_UPPER_X;
   private static final VoxelShape PART_LOWER_Z;
   private static final VoxelShape PART_MID_Z;
   private static final VoxelShape PART_UPPER_Z;
   private static final VoxelShape X_AXIS_AABB;
   private static final VoxelShape Z_AXIS_AABB;
   private static final TranslationTextComponent field_220273_k;

   public AnvilBlock(Block.Properties p_i48450_1_) {
      super(p_i48450_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().rotateY());
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         p_225533_4_.openContainer(p_225533_1_.getContainer(p_225533_2_, p_225533_3_));
         p_225533_4_.addStat(Stats.field_226145_aA_);
         return ActionResultType.SUCCESS;
      }
   }

   @Nullable
   public INamedContainerProvider getContainer(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return new SimpleNamedContainerProvider((p_220272_2_, p_220272_3_, p_220272_4_) -> {
         return new RepairContainer(p_220272_2_, p_220272_3_, IWorldPosCallable.of(p_220052_2_, p_220052_3_));
      }, field_220273_k);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      Direction lvt_5_1_ = (Direction)p_220053_1_.get(FACING);
      return lvt_5_1_.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
   }

   protected void onStartFalling(FallingBlockEntity p_149829_1_) {
      p_149829_1_.setHurtEntities(true);
   }

   public void onEndFalling(World p_176502_1_, BlockPos p_176502_2_, BlockState p_176502_3_, BlockState p_176502_4_) {
      p_176502_1_.playEvent(1031, p_176502_2_, 0);
   }

   public void onBroken(World p_190974_1_, BlockPos p_190974_2_) {
      p_190974_1_.playEvent(1029, p_190974_2_, 0);
   }

   @Nullable
   public static BlockState damage(BlockState p_196433_0_) {
      Block lvt_1_1_ = p_196433_0_.getBlock();
      if (lvt_1_1_ == Blocks.ANVIL) {
         return (BlockState)Blocks.CHIPPED_ANVIL.getDefaultState().with(FACING, p_196433_0_.get(FACING));
      } else {
         return lvt_1_1_ == Blocks.CHIPPED_ANVIL ? (BlockState)Blocks.DAMAGED_ANVIL.getDefaultState().with(FACING, p_196433_0_.get(FACING)) : null;
      }
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      PART_BASE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
      PART_LOWER_X = Block.makeCuboidShape(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
      PART_MID_X = Block.makeCuboidShape(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
      PART_UPPER_X = Block.makeCuboidShape(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
      PART_LOWER_Z = Block.makeCuboidShape(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
      PART_MID_Z = Block.makeCuboidShape(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
      PART_UPPER_Z = Block.makeCuboidShape(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
      X_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER_X, PART_MID_X, PART_UPPER_X);
      Z_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER_Z, PART_MID_Z, PART_UPPER_Z);
      field_220273_k = new TranslationTextComponent("container.repair", new Object[0]);
   }
}
