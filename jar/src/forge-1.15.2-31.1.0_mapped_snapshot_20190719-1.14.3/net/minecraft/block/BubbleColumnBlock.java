package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BubbleColumnBlock extends Block implements IBucketPickupHandler {
   public static final BooleanProperty DRAG;

   public BubbleColumnBlock(Block.Properties p_i48783_1_) {
      super(p_i48783_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(DRAG, true));
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      BlockState lvt_5_1_ = p_196262_2_.getBlockState(p_196262_3_.up());
      if (lvt_5_1_.isAir()) {
         p_196262_4_.onEnterBubbleColumnWithAirAbove((Boolean)p_196262_1_.get(DRAG));
         if (!p_196262_2_.isRemote) {
            ServerWorld lvt_6_1_ = (ServerWorld)p_196262_2_;

            for(int lvt_7_1_ = 0; lvt_7_1_ < 2; ++lvt_7_1_) {
               lvt_6_1_.spawnParticle(ParticleTypes.SPLASH, (double)((float)p_196262_3_.getX() + p_196262_2_.rand.nextFloat()), (double)(p_196262_3_.getY() + 1), (double)((float)p_196262_3_.getZ() + p_196262_2_.rand.nextFloat()), 1, 0.0D, 0.0D, 0.0D, 1.0D);
               lvt_6_1_.spawnParticle(ParticleTypes.BUBBLE, (double)((float)p_196262_3_.getX() + p_196262_2_.rand.nextFloat()), (double)(p_196262_3_.getY() + 1), (double)((float)p_196262_3_.getZ() + p_196262_2_.rand.nextFloat()), 1, 0.0D, 0.01D, 0.0D, 0.2D);
            }
         }
      } else {
         p_196262_4_.onEnterBubbleColumn((Boolean)p_196262_1_.get(DRAG));
      }

   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      placeBubbleColumn(p_220082_2_, p_220082_3_.up(), getDrag(p_220082_2_, p_220082_3_.down()));
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      placeBubbleColumn(p_225534_2_, p_225534_3_.up(), getDrag(p_225534_2_, p_225534_3_));
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public static void placeBubbleColumn(IWorld p_203159_0_, BlockPos p_203159_1_, boolean p_203159_2_) {
      if (canHoldBubbleColumn(p_203159_0_, p_203159_1_)) {
         p_203159_0_.setBlockState(p_203159_1_, (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, p_203159_2_), 2);
      }

   }

   public static boolean canHoldBubbleColumn(IWorld p_208072_0_, BlockPos p_208072_1_) {
      IFluidState lvt_2_1_ = p_208072_0_.getFluidState(p_208072_1_);
      return p_208072_0_.getBlockState(p_208072_1_).getBlock() == Blocks.WATER && lvt_2_1_.getLevel() >= 8 && lvt_2_1_.isSource();
   }

   private static boolean getDrag(IBlockReader p_203157_0_, BlockPos p_203157_1_) {
      BlockState lvt_2_1_ = p_203157_0_.getBlockState(p_203157_1_);
      Block lvt_3_1_ = lvt_2_1_.getBlock();
      if (lvt_3_1_ == Blocks.BUBBLE_COLUMN) {
         return (Boolean)lvt_2_1_.get(DRAG);
      } else {
         return lvt_3_1_ != Blocks.SOUL_SAND;
      }
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 5;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      double lvt_5_1_ = (double)p_180655_3_.getX();
      double lvt_7_1_ = (double)p_180655_3_.getY();
      double lvt_9_1_ = (double)p_180655_3_.getZ();
      if ((Boolean)p_180655_1_.get(DRAG)) {
         p_180655_2_.addOptionalParticle(ParticleTypes.CURRENT_DOWN, lvt_5_1_ + 0.5D, lvt_7_1_ + 0.8D, lvt_9_1_, 0.0D, 0.0D, 0.0D);
         if (p_180655_4_.nextInt(200) == 0) {
            p_180655_2_.playSound(lvt_5_1_, lvt_7_1_, lvt_9_1_, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + p_180655_4_.nextFloat() * 0.2F, 0.9F + p_180655_4_.nextFloat() * 0.15F, false);
         }
      } else {
         p_180655_2_.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, lvt_5_1_ + 0.5D, lvt_7_1_, lvt_9_1_ + 0.5D, 0.0D, 0.04D, 0.0D);
         p_180655_2_.addOptionalParticle(ParticleTypes.BUBBLE_COLUMN_UP, lvt_5_1_ + (double)p_180655_4_.nextFloat(), lvt_7_1_ + (double)p_180655_4_.nextFloat(), lvt_9_1_ + (double)p_180655_4_.nextFloat(), 0.0D, 0.04D, 0.0D);
         if (p_180655_4_.nextInt(200) == 0) {
            p_180655_2_.playSound(lvt_5_1_, lvt_7_1_, lvt_9_1_, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + p_180655_4_.nextFloat() * 0.2F, 0.9F + p_180655_4_.nextFloat() * 0.15F, false);
         }
      }

   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         return Blocks.WATER.getDefaultState();
      } else {
         if (p_196271_2_ == Direction.DOWN) {
            p_196271_4_.setBlockState(p_196271_5_, (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with(DRAG, getDrag(p_196271_4_, p_196271_6_)), 2);
         } else if (p_196271_2_ == Direction.UP && p_196271_3_.getBlock() != Blocks.BUBBLE_COLUMN && canHoldBubbleColumn(p_196271_4_, p_196271_6_)) {
            p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, this.tickRate(p_196271_4_));
         }

         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Block lvt_4_1_ = p_196260_2_.getBlockState(p_196260_3_.down()).getBlock();
      return lvt_4_1_ == Blocks.BUBBLE_COLUMN || lvt_4_1_ == Blocks.MAGMA_BLOCK || lvt_4_1_ == Blocks.SOUL_SAND;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.empty();
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.INVISIBLE;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(DRAG);
   }

   public Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, BlockState p_204508_3_) {
      p_204508_1_.setBlockState(p_204508_2_, Blocks.AIR.getDefaultState(), 11);
      return Fluids.WATER;
   }

   static {
      DRAG = BlockStateProperties.DRAG;
   }
}
