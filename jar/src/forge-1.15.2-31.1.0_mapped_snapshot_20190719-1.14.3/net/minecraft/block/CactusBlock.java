package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class CactusBlock extends Block implements IPlantable {
   public static final IntegerProperty AGE;
   protected static final VoxelShape field_196400_b;
   protected static final VoxelShape field_196401_c;

   protected CactusBlock(Block.Properties p_i48435_1_) {
      super(p_i48435_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_2_.isAreaLoaded(p_225534_3_, 1)) {
         if (!p_225534_1_.isValidPosition(p_225534_2_, p_225534_3_)) {
            p_225534_2_.destroyBlock(p_225534_3_, true);
         } else {
            BlockPos blockpos = p_225534_3_.up();
            if (p_225534_2_.isAirBlock(blockpos)) {
               int i;
               for(i = 1; p_225534_2_.getBlockState(p_225534_3_.down(i)).getBlock() == this; ++i) {
               }

               if (i < 3) {
                  int j = (Integer)p_225534_1_.get(AGE);
                  if (ForgeHooks.onCropsGrowPre(p_225534_2_, blockpos, p_225534_1_, true)) {
                     if (j == 15) {
                        p_225534_2_.setBlockState(blockpos, this.getDefaultState());
                        BlockState blockstate = (BlockState)p_225534_1_.with(AGE, 0);
                        p_225534_2_.setBlockState(p_225534_3_, blockstate, 4);
                        blockstate.neighborChanged(p_225534_2_, blockpos, this, p_225534_3_, false);
                     } else {
                        p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(AGE, j + 1), 4);
                     }

                     ForgeHooks.onCropsGrowPost(p_225534_2_, p_225534_3_, p_225534_1_);
                  }
               }
            }
         }

      }
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return field_196400_b;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return field_196401_c;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

      Direction direction;
      Material material;
      do {
         if (!var4.hasNext()) {
            BlockState soil = p_196260_2_.getBlockState(p_196260_3_.down());
            return soil.canSustainPlant(p_196260_2_, p_196260_3_.down(), Direction.UP, this) && !p_196260_2_.getBlockState(p_196260_3_.up()).getMaterial().isLiquid();
         }

         direction = (Direction)var4.next();
         BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.offset(direction));
         material = blockstate.getMaterial();
      } while(!material.isSolid() && !p_196260_2_.getFluidState(p_196260_3_.offset(direction)).isTagged(FluidTags.LAVA));

      return false;
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      p_196262_4_.attackEntityFrom(DamageSource.CACTUS, 1.0F);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public PlantType getPlantType(IBlockReader p_getPlantType_1_, BlockPos p_getPlantType_2_) {
      return PlantType.Desert;
   }

   public BlockState getPlant(IBlockReader p_getPlant_1_, BlockPos p_getPlant_2_) {
      return this.getDefaultState();
   }

   static {
      AGE = BlockStateProperties.AGE_0_15;
      field_196400_b = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
      field_196401_c = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
   }
}
