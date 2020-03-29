package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
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

public class WallBannerBlock extends AbstractBannerBlock {
   public static final DirectionProperty HORIZONTAL_FACING;
   private static final Map<Direction, VoxelShape> BANNER_SHAPES;

   public WallBannerBlock(DyeColor p_i48302_1_, Block.Properties p_i48302_2_) {
      super(p_i48302_1_, p_i48302_2_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH));
   }

   public String getTranslationKey() {
      return this.asItem().getTranslationKey();
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.offset(((Direction)p_196260_1_.get(HORIZONTAL_FACING)).getOpposite())).getMaterial().isSolid();
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_ == ((Direction)p_196271_1_.get(HORIZONTAL_FACING)).getOpposite() && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (VoxelShape)BANNER_SHAPES.get(p_220053_1_.get(HORIZONTAL_FACING));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState lvt_2_1_ = this.getDefaultState();
      IWorldReader lvt_3_1_ = p_196258_1_.getWorld();
      BlockPos lvt_4_1_ = p_196258_1_.getPos();
      Direction[] lvt_5_1_ = p_196258_1_.getNearestLookingDirections();
      Direction[] var6 = lvt_5_1_;
      int var7 = lvt_5_1_.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction lvt_9_1_ = var6[var8];
         if (lvt_9_1_.getAxis().isHorizontal()) {
            Direction lvt_10_1_ = lvt_9_1_.getOpposite();
            lvt_2_1_ = (BlockState)lvt_2_1_.with(HORIZONTAL_FACING, lvt_10_1_);
            if (lvt_2_1_.isValidPosition(lvt_3_1_, lvt_4_1_)) {
               return lvt_2_1_;
            }
         }
      }

      return null;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(HORIZONTAL_FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(HORIZONTAL_FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(HORIZONTAL_FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING);
   }

   static {
      HORIZONTAL_FACING = HorizontalBlock.HORIZONTAL_FACING;
      BANNER_SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 12.5D, 16.0D), Direction.SOUTH, Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.5D, 2.0D), Direction.WEST, Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 12.5D, 16.0D), Direction.EAST, Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 12.5D, 16.0D)));
   }
}
