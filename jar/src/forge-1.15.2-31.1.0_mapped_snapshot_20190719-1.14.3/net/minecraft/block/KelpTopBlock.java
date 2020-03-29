package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

public class KelpTopBlock extends Block implements ILiquidContainer {
   public static final IntegerProperty AGE;
   protected static final VoxelShape SHAPE;

   protected KelpTopBlock(Block.Properties p_i48781_1_) {
      super(p_i48781_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IFluidState ifluidstate = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      return ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8 ? this.randomAge(p_196258_1_.getWorld()) : null;
   }

   public BlockState randomAge(IWorld p_209906_1_) {
      return (BlockState)this.getDefaultState().with(AGE, p_209906_1_.getRandom().nextInt(25));
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.isValidPosition(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      } else {
         BlockPos blockpos = p_225534_3_.up();
         BlockState blockstate = p_225534_2_.getBlockState(blockpos);
         if (blockstate.getBlock() == Blocks.WATER && (Integer)p_225534_1_.get(AGE) < 25 && ForgeHooks.onCropsGrowPre(p_225534_2_, blockpos, p_225534_1_, p_225534_4_.nextDouble() < 0.14D)) {
            p_225534_2_.setBlockState(blockpos, (BlockState)p_225534_1_.cycle(AGE));
            ForgeHooks.onCropsGrowPost(p_225534_2_, blockpos, p_225534_1_);
         }
      }

   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.down();
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      if (block == Blocks.MAGMA_BLOCK) {
         return false;
      } else {
         return block == this || block == Blocks.KELP_PLANT || blockstate.func_224755_d(p_196260_2_, blockpos, Direction.UP);
      }
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         if (p_196271_2_ == Direction.DOWN) {
            return Blocks.AIR.getDefaultState();
         }

         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      if (p_196271_2_ == Direction.UP && p_196271_3_.getBlock() == this) {
         return Blocks.KELP_PLANT.getDefaultState();
      } else {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
      return false;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, IFluidState p_204509_4_) {
      return false;
   }

   static {
      AGE = BlockStateProperties.AGE_0_25;
      SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   }
}
