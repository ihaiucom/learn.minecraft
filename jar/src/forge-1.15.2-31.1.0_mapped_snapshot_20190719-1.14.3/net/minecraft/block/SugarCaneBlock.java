package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class SugarCaneBlock extends Block implements IPlantable {
   public static final IntegerProperty AGE;
   protected static final VoxelShape SHAPE;

   protected SugarCaneBlock(Block.Properties p_i48312_1_) {
      super(p_i48312_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.isValidPosition(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      } else if (p_225534_2_.isAirBlock(p_225534_3_.up())) {
         int i;
         for(i = 1; p_225534_2_.getBlockState(p_225534_3_.down(i)).getBlock() == this; ++i) {
         }

         if (i < 3) {
            int j = (Integer)p_225534_1_.get(AGE);
            if (ForgeHooks.onCropsGrowPre(p_225534_2_, p_225534_3_, p_225534_1_, true)) {
               if (j == 15) {
                  p_225534_2_.setBlockState(p_225534_3_.up(), this.getDefaultState());
                  p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(AGE, 0), 4);
               } else {
                  p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(AGE, j + 1), 4);
               }

               ForgeHooks.onCropsGrowPost(p_225534_2_, p_225534_3_, p_225534_1_);
            }
         }
      }

   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockState soil = p_196260_2_.getBlockState(p_196260_3_.down());
      if (soil.canSustainPlant(p_196260_2_, p_196260_3_.down(), Direction.UP, this)) {
         return true;
      } else {
         Block block = p_196260_2_.getBlockState(p_196260_3_.down()).getBlock();
         if (block == this) {
            return true;
         } else if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.SAND || block == Blocks.RED_SAND) {
            BlockPos blockpos = p_196260_3_.down();
            Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

            BlockState blockstate;
            IFluidState ifluidstate;
            do {
               if (!var7.hasNext()) {
                  return false;
               }

               Direction direction = (Direction)var7.next();
               blockstate = p_196260_2_.getBlockState(blockpos.offset(direction));
               ifluidstate = p_196260_2_.getFluidState(blockpos.offset(direction));
            } while(!ifluidstate.isTagged(FluidTags.WATER) && blockstate.getBlock() != Blocks.FROSTED_ICE);

            return true;
         } else {
            return false;
         }
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public PlantType getPlantType(IBlockReader p_getPlantType_1_, BlockPos p_getPlantType_2_) {
      return PlantType.Beach;
   }

   public BlockState getPlant(IBlockReader p_getPlant_1_, BlockPos p_getPlant_2_) {
      return this.getDefaultState();
   }

   static {
      AGE = BlockStateProperties.AGE_0_15;
      SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   }
}
