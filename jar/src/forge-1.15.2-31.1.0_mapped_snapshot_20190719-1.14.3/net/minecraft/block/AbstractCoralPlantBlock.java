package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class AbstractCoralPlantBlock extends Block implements IWaterLoggable {
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape field_212559_a;

   protected AbstractCoralPlantBlock(Block.Properties p_i49810_1_) {
      super(p_i49810_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(WATERLOGGED, true));
   }

   protected void updateIfDry(BlockState p_212558_1_, IWorld p_212558_2_, BlockPos p_212558_3_) {
      if (!isInWater(p_212558_1_, p_212558_2_, p_212558_3_)) {
         p_212558_2_.getPendingBlockTicks().scheduleTick(p_212558_3_, this, 60 + p_212558_2_.getRandom().nextInt(40));
      }

   }

   protected static boolean isInWater(BlockState p_212557_0_, IBlockReader p_212557_1_, BlockPos p_212557_2_) {
      if ((Boolean)p_212557_0_.get(WATERLOGGED)) {
         return true;
      } else {
         Direction[] var3 = Direction.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Direction lvt_6_1_ = var3[var5];
            if (p_212557_1_.getFluidState(p_212557_2_.offset(lvt_6_1_)).isTagged(FluidTags.WATER)) {
               return true;
            }
         }

         return false;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IFluidState lvt_2_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      return (BlockState)this.getDefaultState().with(WATERLOGGED, lvt_2_1_.isTagged(FluidTags.WATER) && lvt_2_1_.getLevel() == 8);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return field_212559_a;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return p_196271_2_ == Direction.DOWN && !this.isValidPosition(p_196271_1_, p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos lvt_4_1_ = p_196260_3_.down();
      return p_196260_2_.getBlockState(lvt_4_1_).func_224755_d(p_196260_2_, lvt_4_1_, Direction.UP);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(WATERLOGGED);
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      field_212559_a = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
   }
}
