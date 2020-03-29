package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;

public class FarmlandBlock extends Block {
   public static final IntegerProperty MOISTURE;
   protected static final VoxelShape SHAPE;

   protected FarmlandBlock(Block.Properties p_i48400_1_) {
      super(p_i48400_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(MOISTURE, 0));
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ == Direction.UP && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.up());
      return !blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return !this.getDefaultState().isValidPosition(p_196258_1_.getWorld(), p_196258_1_.getPos()) ? Blocks.DIRT.getDefaultState() : super.getStateForPlacement(p_196258_1_);
   }

   public boolean func_220074_n(BlockState p_220074_1_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.isValidPosition(p_225534_2_, p_225534_3_)) {
         turnToDirt(p_225534_1_, p_225534_2_, p_225534_3_);
      } else {
         int i = (Integer)p_225534_1_.get(MOISTURE);
         if (!hasWater(p_225534_2_, p_225534_3_) && !p_225534_2_.isRainingAt(p_225534_3_.up())) {
            if (i > 0) {
               p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(MOISTURE, i - 1), 2);
            } else if (!this.hasCrops(p_225534_2_, p_225534_3_)) {
               turnToDirt(p_225534_1_, p_225534_2_, p_225534_3_);
            }
         } else if (i < 7) {
            p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(MOISTURE, 7), 2);
         }
      }

   }

   public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      if (!p_180658_1_.isRemote && ForgeHooks.onFarmlandTrample(p_180658_1_, p_180658_2_, Blocks.DIRT.getDefaultState(), p_180658_4_, p_180658_3_)) {
         turnToDirt(p_180658_1_.getBlockState(p_180658_2_), p_180658_1_, p_180658_2_);
      }

      super.onFallenUpon(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_);
   }

   public static void turnToDirt(BlockState p_199610_0_, World p_199610_1_, BlockPos p_199610_2_) {
      p_199610_1_.setBlockState(p_199610_2_, nudgeEntitiesWithNewState(p_199610_0_, Blocks.DIRT.getDefaultState(), p_199610_1_, p_199610_2_));
   }

   private boolean hasCrops(IBlockReader p_176529_1_, BlockPos p_176529_2_) {
      BlockState state = p_176529_1_.getBlockState(p_176529_2_.up());
      return state.getBlock() instanceof IPlantable && this.canSustainPlant(state, p_176529_1_, p_176529_2_, Direction.UP, (IPlantable)state.getBlock());
   }

   private static boolean hasWater(IWorldReader p_176530_0_, BlockPos p_176530_1_) {
      Iterator var2 = BlockPos.getAllInBoxMutable(p_176530_1_.add(-4, 0, -4), p_176530_1_.add(4, 1, 4)).iterator();

      BlockPos blockpos;
      do {
         if (!var2.hasNext()) {
            return FarmlandWaterManager.hasBlockWaterTicket(p_176530_0_, p_176530_1_);
         }

         blockpos = (BlockPos)var2.next();
      } while(!p_176530_0_.getFluidState(blockpos).isTagged(FluidTags.WATER));

      return true;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(MOISTURE);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_229870_f_(BlockState p_229870_1_, IBlockReader p_229870_2_, BlockPos p_229870_3_) {
      return true;
   }

   static {
      MOISTURE = BlockStateProperties.MOISTURE_0_7;
      SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
   }
}
