package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class KelpBlock extends Block implements ILiquidContainer {
   private final KelpTopBlock top;

   protected KelpBlock(KelpTopBlock p_i49501_1_, Block.Properties p_i49501_2_) {
      super(p_i49501_2_);
      this.top = p_i49501_1_;
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.isValidPosition(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      }

      super.func_225534_a_(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == Direction.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      if (p_196271_2_ == Direction.UP) {
         Block block = p_196271_3_.getBlock();
         if (block != this && block != this.top) {
            return this.top.randomAge(p_196271_4_);
         }
      }

      p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.down();
      BlockState blockstate = p_196260_2_.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      return block != Blocks.MAGMA_BLOCK && (block == this || blockstate.func_224755_d(p_196260_2_, blockpos, Direction.UP));
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(Blocks.KELP);
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
      return false;
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, IFluidState p_204509_4_) {
      return false;
   }
}
