package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndRodBlock extends DirectionalBlock {
   protected static final VoxelShape END_ROD_VERTICAL_AABB = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape END_ROD_NS_AABB = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape END_ROD_EW_AABB = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   protected EndRodBlock(Block.Properties p_i48404_1_) {
      super(p_i48404_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.UP));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return (BlockState)p_185471_1_.with(FACING, p_185471_2_.mirror((Direction)p_185471_1_.get(FACING)));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch(((Direction)p_220053_1_.get(FACING)).getAxis()) {
      case X:
      default:
         return END_ROD_EW_AABB;
      case Z:
         return END_ROD_NS_AABB;
      case Y:
         return END_ROD_VERTICAL_AABB;
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction lvt_2_1_ = p_196258_1_.getFace();
      BlockState lvt_3_1_ = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos().offset(lvt_2_1_.getOpposite()));
      return lvt_3_1_.getBlock() == this && lvt_3_1_.get(FACING) == lvt_2_1_ ? (BlockState)this.getDefaultState().with(FACING, lvt_2_1_.getOpposite()) : (BlockState)this.getDefaultState().with(FACING, lvt_2_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      Direction lvt_5_1_ = (Direction)p_180655_1_.get(FACING);
      double lvt_6_1_ = (double)p_180655_3_.getX() + 0.55D - (double)(p_180655_4_.nextFloat() * 0.1F);
      double lvt_8_1_ = (double)p_180655_3_.getY() + 0.55D - (double)(p_180655_4_.nextFloat() * 0.1F);
      double lvt_10_1_ = (double)p_180655_3_.getZ() + 0.55D - (double)(p_180655_4_.nextFloat() * 0.1F);
      double lvt_12_1_ = (double)(0.4F - (p_180655_4_.nextFloat() + p_180655_4_.nextFloat()) * 0.4F);
      if (p_180655_4_.nextInt(5) == 0) {
         p_180655_2_.addParticle(ParticleTypes.END_ROD, lvt_6_1_ + (double)lvt_5_1_.getXOffset() * lvt_12_1_, lvt_8_1_ + (double)lvt_5_1_.getYOffset() * lvt_12_1_, lvt_10_1_ + (double)lvt_5_1_.getZOffset() * lvt_12_1_, p_180655_4_.nextGaussian() * 0.005D, p_180655_4_.nextGaussian() * 0.005D, p_180655_4_.nextGaussian() * 0.005D);
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.NORMAL;
   }
}
