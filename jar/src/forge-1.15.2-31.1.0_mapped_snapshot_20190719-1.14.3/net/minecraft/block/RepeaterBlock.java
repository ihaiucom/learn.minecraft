package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RepeaterBlock extends RedstoneDiodeBlock {
   public static final BooleanProperty LOCKED;
   public static final IntegerProperty DELAY;

   protected RepeaterBlock(Block.Properties p_i48340_1_) {
      super(p_i48340_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(DELAY, 1)).with(LOCKED, false)).with(POWERED, false));
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (!p_225533_4_.abilities.allowEdit) {
         return ActionResultType.PASS;
      } else {
         p_225533_2_.setBlockState(p_225533_3_, (BlockState)p_225533_1_.cycle(DELAY), 3);
         return ActionResultType.SUCCESS;
      }
   }

   protected int getDelay(BlockState p_196346_1_) {
      return (Integer)p_196346_1_.get(DELAY) * 2;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState lvt_2_1_ = super.getStateForPlacement(p_196258_1_);
      return (BlockState)lvt_2_1_.with(LOCKED, this.isLocked(p_196258_1_.getWorld(), p_196258_1_.getPos(), lvt_2_1_));
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_4_.isRemote() && p_196271_2_.getAxis() != ((Direction)p_196271_1_.get(HORIZONTAL_FACING)).getAxis() ? (BlockState)p_196271_1_.with(LOCKED, this.isLocked(p_196271_4_, p_196271_5_, p_196271_1_)) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isLocked(IWorldReader p_176405_1_, BlockPos p_176405_2_, BlockState p_176405_3_) {
      return this.getPowerOnSides(p_176405_1_, p_176405_2_, p_176405_3_) > 0;
   }

   protected boolean isAlternateInput(BlockState p_185545_1_) {
      return isDiode(p_185545_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if ((Boolean)p_180655_1_.get(POWERED)) {
         Direction lvt_5_1_ = (Direction)p_180655_1_.get(HORIZONTAL_FACING);
         double lvt_6_1_ = (double)((float)p_180655_3_.getX() + 0.5F) + (double)(p_180655_4_.nextFloat() - 0.5F) * 0.2D;
         double lvt_8_1_ = (double)((float)p_180655_3_.getY() + 0.4F) + (double)(p_180655_4_.nextFloat() - 0.5F) * 0.2D;
         double lvt_10_1_ = (double)((float)p_180655_3_.getZ() + 0.5F) + (double)(p_180655_4_.nextFloat() - 0.5F) * 0.2D;
         float lvt_12_1_ = -5.0F;
         if (p_180655_4_.nextBoolean()) {
            lvt_12_1_ = (float)((Integer)p_180655_1_.get(DELAY) * 2 - 1);
         }

         lvt_12_1_ /= 16.0F;
         double lvt_13_1_ = (double)(lvt_12_1_ * (float)lvt_5_1_.getXOffset());
         double lvt_15_1_ = (double)(lvt_12_1_ * (float)lvt_5_1_.getZOffset());
         p_180655_2_.addParticle(RedstoneParticleData.REDSTONE_DUST, lvt_6_1_ + lvt_13_1_, lvt_8_1_, lvt_10_1_ + lvt_15_1_, 0.0D, 0.0D, 0.0D);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, DELAY, LOCKED, POWERED);
   }

   static {
      LOCKED = BlockStateProperties.LOCKED;
      DELAY = BlockStateProperties.DELAY_1_4;
   }
}
