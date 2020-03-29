package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.StonecutterContainer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class StonecutterBlock extends Block {
   private static final TranslationTextComponent field_220286_c = new TranslationTextComponent("container.stonecutter", new Object[0]);
   public static final DirectionProperty field_220284_a;
   protected static final VoxelShape field_220285_b;

   public StonecutterBlock(Block.Properties p_i49972_1_) {
      super(p_i49972_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(field_220284_a, Direction.NORTH));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(field_220284_a, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         p_225533_4_.openContainer(p_225533_1_.getContainer(p_225533_2_, p_225533_3_));
         p_225533_4_.addStat(Stats.INTERACT_WITH_STONECUTTER);
         return ActionResultType.SUCCESS;
      }
   }

   @Nullable
   public INamedContainerProvider getContainer(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return new SimpleNamedContainerProvider((p_220283_2_, p_220283_3_, p_220283_4_) -> {
         return new StonecutterContainer(p_220283_2_, p_220283_3_, IWorldPosCallable.of(p_220052_2_, p_220052_3_));
      }, field_220286_c);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return field_220285_b;
   }

   public boolean func_220074_n(BlockState p_220074_1_) {
      return true;
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(field_220284_a, p_185499_2_.rotate((Direction)p_185499_1_.get(field_220284_a)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(field_220284_a)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(field_220284_a);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      field_220284_a = HorizontalBlock.HORIZONTAL_FACING;
      field_220285_b = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   }
}
