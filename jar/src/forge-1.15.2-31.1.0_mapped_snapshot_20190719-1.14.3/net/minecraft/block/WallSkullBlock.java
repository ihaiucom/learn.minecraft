package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
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

public class WallSkullBlock extends AbstractSkullBlock {
   public static final DirectionProperty FACING;
   private static final Map<Direction, VoxelShape> SHAPES;

   protected WallSkullBlock(SkullBlock.ISkullType p_i48299_1_, Block.Properties p_i48299_2_) {
      super(p_i48299_1_, p_i48299_2_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH));
   }

   public String getTranslationKey() {
      return this.asItem().getTranslationKey();
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (VoxelShape)SHAPES.get(p_220053_1_.get(FACING));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState lvt_2_1_ = this.getDefaultState();
      IBlockReader lvt_3_1_ = p_196258_1_.getWorld();
      BlockPos lvt_4_1_ = p_196258_1_.getPos();
      Direction[] lvt_5_1_ = p_196258_1_.getNearestLookingDirections();
      Direction[] var6 = lvt_5_1_;
      int var7 = lvt_5_1_.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction lvt_9_1_ = var6[var8];
         if (lvt_9_1_.getAxis().isHorizontal()) {
            Direction lvt_10_1_ = lvt_9_1_.getOpposite();
            lvt_2_1_ = (BlockState)lvt_2_1_.with(FACING, lvt_10_1_);
            if (!lvt_3_1_.getBlockState(lvt_4_1_.offset(lvt_9_1_)).isReplaceable(p_196258_1_)) {
               return lvt_2_1_;
            }
         }
      }

      return null;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(4.0D, 4.0D, 8.0D, 12.0D, 12.0D, 16.0D), Direction.SOUTH, Block.makeCuboidShape(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 8.0D), Direction.EAST, Block.makeCuboidShape(0.0D, 4.0D, 4.0D, 8.0D, 12.0D, 12.0D), Direction.WEST, Block.makeCuboidShape(8.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D)));
   }
}
