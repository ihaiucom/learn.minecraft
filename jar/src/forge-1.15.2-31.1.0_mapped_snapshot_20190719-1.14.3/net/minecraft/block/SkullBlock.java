package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class SkullBlock extends AbstractSkullBlock {
   public static final IntegerProperty ROTATION;
   protected static final VoxelShape SHAPE;

   protected SkullBlock(SkullBlock.ISkullType p_i48332_1_, Block.Properties p_i48332_2_) {
      super(p_i48332_1_, p_i48332_2_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(ROTATION, 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public VoxelShape getRenderShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return VoxelShapes.empty();
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(ROTATION, MathHelper.floor((double)(p_196258_1_.getPlacementYaw() * 16.0F / 360.0F) + 0.5D) & 15);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(ROTATION, p_185499_2_.rotate((Integer)p_185499_1_.get(ROTATION), 16));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return (BlockState)p_185471_1_.with(ROTATION, p_185471_2_.mirrorRotation((Integer)p_185471_1_.get(ROTATION), 16));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(ROTATION);
   }

   static {
      ROTATION = BlockStateProperties.ROTATION_0_15;
      SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);
   }

   public static enum Types implements SkullBlock.ISkullType {
      SKELETON,
      WITHER_SKELETON,
      PLAYER,
      ZOMBIE,
      CREEPER,
      DRAGON;
   }

   public interface ISkullType {
   }
}
