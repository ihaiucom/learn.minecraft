package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class SlabBlock extends Block implements IWaterLoggable {
   public static final EnumProperty<SlabType> TYPE;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape BOTTOM_SHAPE;
   protected static final VoxelShape TOP_SHAPE;

   public SlabBlock(Block.Properties p_i48331_1_) {
      super(p_i48331_1_);
      this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(TYPE, SlabType.BOTTOM)).with(WATERLOGGED, false));
   }

   public boolean func_220074_n(BlockState p_220074_1_) {
      return p_220074_1_.get(TYPE) != SlabType.DOUBLE;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(TYPE, WATERLOGGED);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      SlabType lvt_5_1_ = (SlabType)p_220053_1_.get(TYPE);
      switch(lvt_5_1_) {
      case DOUBLE:
         return VoxelShapes.fullCube();
      case TOP:
         return TOP_SHAPE;
      default:
         return BOTTOM_SHAPE;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockPos lvt_2_1_ = p_196258_1_.getPos();
      BlockState lvt_3_1_ = p_196258_1_.getWorld().getBlockState(lvt_2_1_);
      if (lvt_3_1_.getBlock() == this) {
         return (BlockState)((BlockState)lvt_3_1_.with(TYPE, SlabType.DOUBLE)).with(WATERLOGGED, false);
      } else {
         IFluidState lvt_4_1_ = p_196258_1_.getWorld().getFluidState(lvt_2_1_);
         BlockState lvt_5_1_ = (BlockState)((BlockState)this.getDefaultState().with(TYPE, SlabType.BOTTOM)).with(WATERLOGGED, lvt_4_1_.getFluid() == Fluids.WATER);
         Direction lvt_6_1_ = p_196258_1_.getFace();
         return lvt_6_1_ != Direction.DOWN && (lvt_6_1_ == Direction.UP || p_196258_1_.getHitVec().y - (double)lvt_2_1_.getY() <= 0.5D) ? lvt_5_1_ : (BlockState)lvt_5_1_.with(TYPE, SlabType.TOP);
      }
   }

   public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      ItemStack lvt_3_1_ = p_196253_2_.getItem();
      SlabType lvt_4_1_ = (SlabType)p_196253_1_.get(TYPE);
      if (lvt_4_1_ != SlabType.DOUBLE && lvt_3_1_.getItem() == this.asItem()) {
         if (p_196253_2_.replacingClickedOnBlock()) {
            boolean lvt_5_1_ = p_196253_2_.getHitVec().y - (double)p_196253_2_.getPos().getY() > 0.5D;
            Direction lvt_6_1_ = p_196253_2_.getFace();
            if (lvt_4_1_ == SlabType.BOTTOM) {
               return lvt_6_1_ == Direction.UP || lvt_5_1_ && lvt_6_1_.getAxis().isHorizontal();
            } else {
               return lvt_6_1_ == Direction.DOWN || !lvt_5_1_ && lvt_6_1_.getAxis().isHorizontal();
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public boolean receiveFluid(IWorld p_204509_1_, BlockPos p_204509_2_, BlockState p_204509_3_, IFluidState p_204509_4_) {
      return p_204509_3_.get(TYPE) != SlabType.DOUBLE ? IWaterLoggable.super.receiveFluid(p_204509_1_, p_204509_2_, p_204509_3_, p_204509_4_) : false;
   }

   public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
      return p_204510_3_.get(TYPE) != SlabType.DOUBLE ? IWaterLoggable.super.canContainFluid(p_204510_1_, p_204510_2_, p_204510_3_, p_204510_4_) : false;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return false;
      case WATER:
         return p_196266_2_.getFluidState(p_196266_3_).isTagged(FluidTags.WATER);
      case AIR:
         return false;
      default:
         return false;
      }
   }

   static {
      TYPE = BlockStateProperties.SLAB_TYPE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      BOTTOM_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
      TOP_SHAPE = Block.makeCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   }
}
