package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SeaPickleBlock extends BushBlock implements IGrowable, IWaterLoggable {
   public static final IntegerProperty PICKLES;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape ONE_SHAPE;
   protected static final VoxelShape TWO_SHAPE;
   protected static final VoxelShape THREE_SHAPE;
   protected static final VoxelShape FOUR_SHAPE;

   protected SeaPickleBlock(Block.Properties p_i48924_1_) {
      super(p_i48924_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(PICKLES, 1)).with(WATERLOGGED, true));
   }

   public int getLightValue(BlockState p_149750_1_) {
      return this.isInBadEnvironment(p_149750_1_) ? 0 : super.getLightValue(p_149750_1_) + 3 * (Integer)p_149750_1_.get(PICKLES);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState lvt_2_1_ = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos());
      if (lvt_2_1_.getBlock() == this) {
         return (BlockState)lvt_2_1_.with(PICKLES, Math.min(4, (Integer)lvt_2_1_.get(PICKLES) + 1));
      } else {
         IFluidState lvt_3_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
         boolean lvt_4_1_ = lvt_3_1_.isTagged(FluidTags.WATER) && lvt_3_1_.getLevel() == 8;
         return (BlockState)super.getStateForPlacement(p_196258_1_).with(WATERLOGGED, lvt_4_1_);
      }
   }

   private boolean isInBadEnvironment(BlockState p_204901_1_) {
      return !(Boolean)p_204901_1_.get(WATERLOGGED);
   }

   protected boolean isValidGround(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return !p_200014_1_.getCollisionShape(p_200014_2_, p_200014_3_).project(Direction.UP).isEmpty();
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos lvt_4_1_ = p_196260_3_.down();
      return this.isValidGround(p_196260_2_.getBlockState(lvt_4_1_), p_196260_2_, lvt_4_1_);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
            p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
         }

         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      return p_196253_2_.getItem().getItem() == this.asItem() && (Integer)p_196253_1_.get(PICKLES) < 4 ? true : super.isReplaceable(p_196253_1_, p_196253_2_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((Integer)p_220053_1_.get(PICKLES)) {
      case 1:
      default:
         return ONE_SHAPE;
      case 2:
         return TWO_SHAPE;
      case 3:
         return THREE_SHAPE;
      case 4:
         return FOUR_SHAPE;
      }
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(PICKLES, WATERLOGGED);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return true;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      if (!this.isInBadEnvironment(p_225535_4_) && p_225535_1_.getBlockState(p_225535_3_.down()).isIn(BlockTags.CORAL_BLOCKS)) {
         int lvt_5_1_ = true;
         int lvt_6_1_ = 1;
         int lvt_7_1_ = true;
         int lvt_8_1_ = 0;
         int lvt_9_1_ = p_225535_3_.getX() - 2;
         int lvt_10_1_ = 0;

         for(int lvt_11_1_ = 0; lvt_11_1_ < 5; ++lvt_11_1_) {
            for(int lvt_12_1_ = 0; lvt_12_1_ < lvt_6_1_; ++lvt_12_1_) {
               int lvt_13_1_ = 2 + p_225535_3_.getY() - 1;

               for(int lvt_14_1_ = lvt_13_1_ - 2; lvt_14_1_ < lvt_13_1_; ++lvt_14_1_) {
                  BlockPos lvt_15_1_ = new BlockPos(lvt_9_1_ + lvt_11_1_, lvt_14_1_, p_225535_3_.getZ() - lvt_10_1_ + lvt_12_1_);
                  if (lvt_15_1_ != p_225535_3_ && p_225535_2_.nextInt(6) == 0 && p_225535_1_.getBlockState(lvt_15_1_).getBlock() == Blocks.WATER) {
                     BlockState lvt_16_1_ = p_225535_1_.getBlockState(lvt_15_1_.down());
                     if (lvt_16_1_.isIn(BlockTags.CORAL_BLOCKS)) {
                        p_225535_1_.setBlockState(lvt_15_1_, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(PICKLES, p_225535_2_.nextInt(4) + 1), 3);
                     }
                  }
               }
            }

            if (lvt_8_1_ < 2) {
               lvt_6_1_ += 2;
               ++lvt_10_1_;
            } else {
               lvt_6_1_ -= 2;
               --lvt_10_1_;
            }

            ++lvt_8_1_;
         }

         p_225535_1_.setBlockState(p_225535_3_, (BlockState)p_225535_4_.with(PICKLES, 4), 2);
      }

   }

   static {
      PICKLES = BlockStateProperties.PICKLES_1_4;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      ONE_SHAPE = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
      TWO_SHAPE = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
      THREE_SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
      FOUR_SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);
   }
}
