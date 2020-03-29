package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.LoomContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class LoomBlock extends HorizontalBlock {
   private static final TranslationTextComponent field_220255_a = new TranslationTextComponent("container.loom", new Object[0]);

   protected LoomBlock(Block.Properties p_i49978_1_) {
      super(p_i49978_1_);
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         p_225533_4_.openContainer(p_225533_1_.getContainer(p_225533_2_, p_225533_3_));
         p_225533_4_.addStat(Stats.INTERACT_WITH_LOOM);
         return ActionResultType.SUCCESS;
      }
   }

   public INamedContainerProvider getContainer(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return new SimpleNamedContainerProvider((p_220254_2_, p_220254_3_, p_220254_4_) -> {
         return new LoomContainer(p_220254_2_, p_220254_3_, IWorldPosCallable.of(p_220052_2_, p_220052_3_));
      }, field_220255_a);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING);
   }
}
