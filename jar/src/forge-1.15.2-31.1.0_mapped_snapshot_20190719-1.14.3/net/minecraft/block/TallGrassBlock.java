package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IShearable;

public class TallGrassBlock extends BushBlock implements IGrowable, IShearable {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   protected TallGrassBlock(Block.Properties p_i48310_1_) {
      super(p_i48310_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return true;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      DoublePlantBlock doubleplantblock = (DoublePlantBlock)((DoublePlantBlock)(this == Blocks.FERN ? Blocks.LARGE_FERN : Blocks.TALL_GRASS));
      if (doubleplantblock.getDefaultState().isValidPosition(p_225535_1_, p_225535_3_) && p_225535_1_.isAirBlock(p_225535_3_.up())) {
         doubleplantblock.placeAt(p_225535_1_, p_225535_3_, 2);
      }

   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XYZ;
   }
}
