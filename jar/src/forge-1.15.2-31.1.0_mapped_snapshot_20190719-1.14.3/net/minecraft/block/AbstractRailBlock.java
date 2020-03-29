package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class AbstractRailBlock extends Block {
   protected static final VoxelShape FLAT_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape ASCENDING_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean disableCorners;

   public static boolean isRail(World p_208488_0_, BlockPos p_208488_1_) {
      return isRail(p_208488_0_.getBlockState(p_208488_1_));
   }

   public static boolean isRail(BlockState p_208487_0_) {
      return p_208487_0_.isIn(BlockTags.RAILS);
   }

   protected AbstractRailBlock(boolean p_i48444_1_, Block.Properties p_i48444_2_) {
      super(p_i48444_2_);
      this.disableCorners = p_i48444_1_;
   }

   public boolean areCornersDisabled() {
      return this.disableCorners;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      RailShape railshape = p_220053_1_.getBlock() == this ? this.getRailDirection(p_220053_1_, p_220053_2_, p_220053_3_, (AbstractMinecartEntity)null) : null;
      return railshape != null && railshape.isAscending() ? ASCENDING_AABB : FLAT_AABB;
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return func_220064_c(p_196260_2_, p_196260_3_.down());
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock()) {
         p_220082_1_ = this.getUpdatedState(p_220082_2_, p_220082_3_, p_220082_1_, true);
         if (this.disableCorners) {
            p_220082_1_.neighborChanged(p_220082_2_, p_220082_3_, this, p_220082_3_, p_220082_5_);
         }
      }

   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isRemote) {
         RailShape railshape = this.getRailDirection(p_220069_1_, p_220069_2_, p_220069_3_, (AbstractMinecartEntity)null);
         boolean flag = false;
         BlockPos blockpos = p_220069_3_.down();
         if (!func_220064_c(p_220069_2_, blockpos)) {
            flag = true;
         }

         BlockPos blockpos1 = p_220069_3_.east();
         if (railshape == RailShape.ASCENDING_EAST && !func_220064_c(p_220069_2_, blockpos1)) {
            flag = true;
         } else {
            BlockPos blockpos2 = p_220069_3_.west();
            if (railshape == RailShape.ASCENDING_WEST && !func_220064_c(p_220069_2_, blockpos2)) {
               flag = true;
            } else {
               BlockPos blockpos3 = p_220069_3_.north();
               if (railshape == RailShape.ASCENDING_NORTH && !func_220064_c(p_220069_2_, blockpos3)) {
                  flag = true;
               } else {
                  BlockPos blockpos4 = p_220069_3_.south();
                  if (railshape == RailShape.ASCENDING_SOUTH && !func_220064_c(p_220069_2_, blockpos4)) {
                     flag = true;
                  }
               }
            }
         }

         if (flag && !p_220069_2_.isAirBlock(p_220069_3_)) {
            if (!p_220069_6_) {
               spawnDrops(p_220069_1_, p_220069_2_, p_220069_3_);
            }

            p_220069_2_.removeBlock(p_220069_3_, p_220069_6_);
         } else {
            this.updateState(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_);
         }
      }

   }

   protected void updateState(BlockState p_189541_1_, World p_189541_2_, BlockPos p_189541_3_, Block p_189541_4_) {
   }

   protected BlockState getUpdatedState(World p_208489_1_, BlockPos p_208489_2_, BlockState p_208489_3_, boolean p_208489_4_) {
      if (p_208489_1_.isRemote) {
         return p_208489_3_;
      } else {
         RailShape railshape = (RailShape)p_208489_3_.get(this.getShapeProperty());
         return (new RailState(p_208489_1_, p_208489_2_, p_208489_3_)).func_226941_a_(p_208489_1_.isBlockPowered(p_208489_2_), p_208489_4_, railshape).getNewState();
      }
   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.NORMAL;
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_) {
         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
         if (this.getRailDirection(p_196243_1_, p_196243_2_, p_196243_3_, (AbstractMinecartEntity)null).isAscending()) {
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.up(), this);
         }

         if (this.disableCorners) {
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_, this);
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.down(), this);
         }
      }

   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = super.getDefaultState();
      Direction direction = p_196258_1_.getPlacementHorizontalFacing();
      boolean flag = direction == Direction.EAST || direction == Direction.WEST;
      return (BlockState)blockstate.with(this.getShapeProperty(), flag ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
   }

   public abstract IProperty<RailShape> getShapeProperty();

   public boolean isFlexibleRail(BlockState p_isFlexibleRail_1_, IBlockReader p_isFlexibleRail_2_, BlockPos p_isFlexibleRail_3_) {
      return !this.disableCorners;
   }

   public boolean canMakeSlopes(BlockState p_canMakeSlopes_1_, IBlockReader p_canMakeSlopes_2_, BlockPos p_canMakeSlopes_3_) {
      return true;
   }

   public RailShape getRailDirection(BlockState p_getRailDirection_1_, IBlockReader p_getRailDirection_2_, BlockPos p_getRailDirection_3_, @Nullable AbstractMinecartEntity p_getRailDirection_4_) {
      return (RailShape)p_getRailDirection_1_.get(this.getShapeProperty());
   }

   public float getRailMaxSpeed(BlockState p_getRailMaxSpeed_1_, World p_getRailMaxSpeed_2_, BlockPos p_getRailMaxSpeed_3_, AbstractMinecartEntity p_getRailMaxSpeed_4_) {
      return 0.4F;
   }

   public void onMinecartPass(BlockState p_onMinecartPass_1_, World p_onMinecartPass_2_, BlockPos p_onMinecartPass_3_, AbstractMinecartEntity p_onMinecartPass_4_) {
   }
}
