package net.minecraft.block;

import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.IPlantable;

public class BushBlock extends Block implements IPlantable {
   protected BushBlock(Block.Properties p_i48437_1_) {
      super(p_i48437_1_);
   }

   protected boolean isValidGround(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      Block block = p_200014_1_.getBlock();
      return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.FARMLAND;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.down();
      return p_196260_1_.getBlock() == this ? p_196260_2_.getBlockState(blockpos).canSustainPlant(p_196260_2_, blockpos, Direction.UP, this) : this.isValidGround(p_196260_2_.getBlockState(blockpos), p_196260_2_, blockpos);
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return true;
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return p_196266_4_ == PathType.AIR && !this.blocksMovement ? true : super.allowsMovement(p_196266_1_, p_196266_2_, p_196266_3_, p_196266_4_);
   }

   public BlockState getPlant(IBlockReader p_getPlant_1_, BlockPos p_getPlant_2_) {
      BlockState state = p_getPlant_1_.getBlockState(p_getPlant_2_);
      return state.getBlock() != this ? this.getDefaultState() : state;
   }
}
