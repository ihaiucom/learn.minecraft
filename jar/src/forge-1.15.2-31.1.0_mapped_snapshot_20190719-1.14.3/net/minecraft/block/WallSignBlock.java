package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class WallSignBlock extends AbstractSignBlock {
   public static final DirectionProperty FACING;
   private static final Map<Direction, VoxelShape> SHAPES;

   public WallSignBlock(Block.Properties p_i225766_1_, WoodType p_i225766_2_) {
      super(p_i225766_1_, p_i225766_2_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false));
   }

   public String getTranslationKey() {
      return this.asItem().getTranslationKey();
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (VoxelShape)SHAPES.get(p_220053_1_.get(FACING));
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.offset(((Direction)p_196260_1_.get(FACING)).getOpposite())).getMaterial().isSolid();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState lvt_2_1_ = this.getDefaultState();
      IFluidState lvt_3_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      IWorldReader lvt_4_1_ = p_196258_1_.getWorld();
      BlockPos lvt_5_1_ = p_196258_1_.getPos();
      Direction[] lvt_6_1_ = p_196258_1_.getNearestLookingDirections();
      Direction[] var7 = lvt_6_1_;
      int var8 = lvt_6_1_.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         Direction lvt_10_1_ = var7[var9];
         if (lvt_10_1_.getAxis().isHorizontal()) {
            Direction lvt_11_1_ = lvt_10_1_.getOpposite();
            lvt_2_1_ = (BlockState)lvt_2_1_.with(FACING, lvt_11_1_);
            if (lvt_2_1_.isValidPosition(lvt_4_1_, lvt_5_1_)) {
               return (BlockState)lvt_2_1_.with(WATERLOGGED, lvt_3_1_.getFluid() == Fluids.WATER);
            }
         }
      }

      return null;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_.getOpposite() == p_196271_1_.get(FACING) && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, WATERLOGGED);
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), Direction.SOUTH, Block.makeCuboidShape(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), Direction.EAST, Block.makeCuboidShape(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), Direction.WEST, Block.makeCuboidShape(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));
   }
}
