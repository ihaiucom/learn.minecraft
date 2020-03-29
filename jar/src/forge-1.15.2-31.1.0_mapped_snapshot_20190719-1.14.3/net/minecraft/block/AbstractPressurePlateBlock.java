package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractPressurePlateBlock extends Block {
   protected static final VoxelShape PRESSED_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
   protected static final VoxelShape UNPRESSED_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
   protected static final AxisAlignedBB PRESSURE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

   protected AbstractPressurePlateBlock(Block.Properties p_i48445_1_) {
      super(p_i48445_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.getRedstoneStrength(p_220053_1_) > 0 ? PRESSED_AABB : UNPRESSED_AABB;
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 20;
   }

   public boolean canSpawnInBlock() {
      return true;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == Direction.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos lvt_4_1_ = p_196260_3_.down();
      return func_220064_c(p_196260_2_, lvt_4_1_) || func_220055_a(p_196260_2_, lvt_4_1_, Direction.UP);
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      int lvt_5_1_ = this.getRedstoneStrength(p_225534_1_);
      if (lvt_5_1_ > 0) {
         this.updateState(p_225534_2_, p_225534_3_, p_225534_1_, lvt_5_1_);
      }

   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isRemote) {
         int lvt_5_1_ = this.getRedstoneStrength(p_196262_1_);
         if (lvt_5_1_ == 0) {
            this.updateState(p_196262_2_, p_196262_3_, p_196262_1_, lvt_5_1_);
         }

      }
   }

   protected void updateState(World p_180666_1_, BlockPos p_180666_2_, BlockState p_180666_3_, int p_180666_4_) {
      int lvt_5_1_ = this.computeRedstoneStrength(p_180666_1_, p_180666_2_);
      boolean lvt_6_1_ = p_180666_4_ > 0;
      boolean lvt_7_1_ = lvt_5_1_ > 0;
      if (p_180666_4_ != lvt_5_1_) {
         BlockState lvt_8_1_ = this.setRedstoneStrength(p_180666_3_, lvt_5_1_);
         p_180666_1_.setBlockState(p_180666_2_, lvt_8_1_, 2);
         this.updateNeighbors(p_180666_1_, p_180666_2_);
         p_180666_1_.func_225319_b(p_180666_2_, p_180666_3_, lvt_8_1_);
      }

      if (!lvt_7_1_ && lvt_6_1_) {
         this.playClickOffSound(p_180666_1_, p_180666_2_);
      } else if (lvt_7_1_ && !lvt_6_1_) {
         this.playClickOnSound(p_180666_1_, p_180666_2_);
      }

      if (lvt_7_1_) {
         p_180666_1_.getPendingBlockTicks().scheduleTick(new BlockPos(p_180666_2_), this, this.tickRate(p_180666_1_));
      }

   }

   protected abstract void playClickOnSound(IWorld var1, BlockPos var2);

   protected abstract void playClickOffSound(IWorld var1, BlockPos var2);

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         if (this.getRedstoneStrength(p_196243_1_) > 0) {
            this.updateNeighbors(p_196243_2_, p_196243_3_);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   protected void updateNeighbors(World p_176578_1_, BlockPos p_176578_2_) {
      p_176578_1_.notifyNeighborsOfStateChange(p_176578_2_, this);
      p_176578_1_.notifyNeighborsOfStateChange(p_176578_2_.down(), this);
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return this.getRedstoneStrength(p_180656_1_);
   }

   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_4_ == Direction.UP ? this.getRedstoneStrength(p_176211_1_) : 0;
   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return true;
   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   protected abstract int computeRedstoneStrength(World var1, BlockPos var2);

   protected abstract int getRedstoneStrength(BlockState var1);

   protected abstract BlockState setRedstoneStrength(BlockState var1, int var2);
}
