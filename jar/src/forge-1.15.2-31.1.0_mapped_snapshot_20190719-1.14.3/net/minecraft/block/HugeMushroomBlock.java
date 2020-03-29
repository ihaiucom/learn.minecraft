package net.minecraft.block;

import java.util.Map;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class HugeMushroomBlock extends Block {
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   public static final BooleanProperty DOWN;
   private static final Map<Direction, BooleanProperty> field_196462_B;

   public HugeMushroomBlock(Block.Properties p_i49982_1_) {
      super(p_i49982_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(NORTH, true)).with(EAST, true)).with(SOUTH, true)).with(WEST, true)).with(UP, true)).with(DOWN, true));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader lvt_2_1_ = p_196258_1_.getWorld();
      BlockPos lvt_3_1_ = p_196258_1_.getPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, this != lvt_2_1_.getBlockState(lvt_3_1_.down()).getBlock())).with(UP, this != lvt_2_1_.getBlockState(lvt_3_1_.up()).getBlock())).with(NORTH, this != lvt_2_1_.getBlockState(lvt_3_1_.north()).getBlock())).with(EAST, this != lvt_2_1_.getBlockState(lvt_3_1_.east()).getBlock())).with(SOUTH, this != lvt_2_1_.getBlockState(lvt_3_1_.south()).getBlock())).with(WEST, this != lvt_2_1_.getBlockState(lvt_3_1_.west()).getBlock());
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_3_.getBlock() == this ? (BlockState)p_196271_1_.with((IProperty)field_196462_B.get(p_196271_2_), false) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with((IProperty)field_196462_B.get(p_185499_2_.rotate(Direction.NORTH)), p_185499_1_.get(NORTH))).with((IProperty)field_196462_B.get(p_185499_2_.rotate(Direction.SOUTH)), p_185499_1_.get(SOUTH))).with((IProperty)field_196462_B.get(p_185499_2_.rotate(Direction.EAST)), p_185499_1_.get(EAST))).with((IProperty)field_196462_B.get(p_185499_2_.rotate(Direction.WEST)), p_185499_1_.get(WEST))).with((IProperty)field_196462_B.get(p_185499_2_.rotate(Direction.UP)), p_185499_1_.get(UP))).with((IProperty)field_196462_B.get(p_185499_2_.rotate(Direction.DOWN)), p_185499_1_.get(DOWN));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)p_185471_1_.with((IProperty)field_196462_B.get(p_185471_2_.mirror(Direction.NORTH)), p_185471_1_.get(NORTH))).with((IProperty)field_196462_B.get(p_185471_2_.mirror(Direction.SOUTH)), p_185471_1_.get(SOUTH))).with((IProperty)field_196462_B.get(p_185471_2_.mirror(Direction.EAST)), p_185471_1_.get(EAST))).with((IProperty)field_196462_B.get(p_185471_2_.mirror(Direction.WEST)), p_185471_1_.get(WEST))).with((IProperty)field_196462_B.get(p_185471_2_.mirror(Direction.UP)), p_185471_1_.get(UP))).with((IProperty)field_196462_B.get(p_185471_2_.mirror(Direction.DOWN)), p_185471_1_.get(DOWN));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }

   static {
      NORTH = SixWayBlock.NORTH;
      EAST = SixWayBlock.EAST;
      SOUTH = SixWayBlock.SOUTH;
      WEST = SixWayBlock.WEST;
      UP = SixWayBlock.UP;
      DOWN = SixWayBlock.DOWN;
      field_196462_B = SixWayBlock.FACING_TO_PROPERTY_MAP;
   }
}
