package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractButtonBlock extends HorizontalFaceBlock {
   public static final BooleanProperty POWERED;
   protected static final VoxelShape field_196370_b;
   protected static final VoxelShape field_196371_c;
   protected static final VoxelShape field_196376_y;
   protected static final VoxelShape field_196377_z;
   protected static final VoxelShape AABB_NORTH_OFF;
   protected static final VoxelShape AABB_SOUTH_OFF;
   protected static final VoxelShape AABB_WEST_OFF;
   protected static final VoxelShape AABB_EAST_OFF;
   protected static final VoxelShape field_196372_E;
   protected static final VoxelShape field_196373_F;
   protected static final VoxelShape field_196374_G;
   protected static final VoxelShape field_196375_H;
   protected static final VoxelShape AABB_NORTH_ON;
   protected static final VoxelShape AABB_SOUTH_ON;
   protected static final VoxelShape AABB_WEST_ON;
   protected static final VoxelShape AABB_EAST_ON;
   private final boolean wooden;

   protected AbstractButtonBlock(boolean p_i48436_1_, Block.Properties p_i48436_2_) {
      super(p_i48436_2_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(POWERED, false)).with(FACE, AttachFace.WALL));
      this.wooden = p_i48436_1_;
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return this.wooden ? 30 : 20;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      Direction lvt_5_1_ = (Direction)p_220053_1_.get(HORIZONTAL_FACING);
      boolean lvt_6_1_ = (Boolean)p_220053_1_.get(POWERED);
      switch((AttachFace)p_220053_1_.get(FACE)) {
      case FLOOR:
         if (lvt_5_1_.getAxis() == Direction.Axis.X) {
            return lvt_6_1_ ? field_196374_G : field_196376_y;
         }

         return lvt_6_1_ ? field_196375_H : field_196377_z;
      case WALL:
         switch(lvt_5_1_) {
         case EAST:
            return lvt_6_1_ ? AABB_EAST_ON : AABB_EAST_OFF;
         case WEST:
            return lvt_6_1_ ? AABB_WEST_ON : AABB_WEST_OFF;
         case SOUTH:
            return lvt_6_1_ ? AABB_SOUTH_ON : AABB_SOUTH_OFF;
         case NORTH:
         default:
            return lvt_6_1_ ? AABB_NORTH_ON : AABB_NORTH_OFF;
         }
      case CEILING:
      default:
         if (lvt_5_1_.getAxis() == Direction.Axis.X) {
            return lvt_6_1_ ? field_196372_E : field_196370_b;
         } else {
            return lvt_6_1_ ? field_196373_F : field_196371_c;
         }
      }
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if ((Boolean)p_225533_1_.get(POWERED)) {
         return ActionResultType.CONSUME;
      } else {
         this.func_226910_d_(p_225533_1_, p_225533_2_, p_225533_3_);
         this.playSound(p_225533_4_, p_225533_2_, p_225533_3_, true);
         return ActionResultType.SUCCESS;
      }
   }

   public void func_226910_d_(BlockState p_226910_1_, World p_226910_2_, BlockPos p_226910_3_) {
      p_226910_2_.setBlockState(p_226910_3_, (BlockState)p_226910_1_.with(POWERED, true), 3);
      this.updateNeighbors(p_226910_1_, p_226910_2_, p_226910_3_);
      p_226910_2_.getPendingBlockTicks().scheduleTick(p_226910_3_, this, this.tickRate(p_226910_2_));
   }

   protected void playSound(@Nullable PlayerEntity p_196367_1_, IWorld p_196367_2_, BlockPos p_196367_3_, boolean p_196367_4_) {
      p_196367_2_.playSound(p_196367_4_ ? p_196367_1_ : null, p_196367_3_, this.getSoundEvent(p_196367_4_), SoundCategory.BLOCKS, 0.3F, p_196367_4_ ? 0.6F : 0.5F);
   }

   protected abstract SoundEvent getSoundEvent(boolean var1);

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         if ((Boolean)p_196243_1_.get(POWERED)) {
            this.updateNeighbors(p_196243_1_, p_196243_2_, p_196243_3_);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return (Boolean)p_180656_1_.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return (Boolean)p_176211_1_.get(POWERED) && getFacing(p_176211_1_) == p_176211_4_ ? 15 : 0;
   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return true;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if ((Boolean)p_225534_1_.get(POWERED)) {
         if (this.wooden) {
            this.checkPressed(p_225534_1_, p_225534_2_, p_225534_3_);
         } else {
            p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(POWERED, false), 3);
            this.updateNeighbors(p_225534_1_, p_225534_2_, p_225534_3_);
            this.playSound((PlayerEntity)null, p_225534_2_, p_225534_3_, false);
         }

      }
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isRemote && this.wooden && !(Boolean)p_196262_1_.get(POWERED)) {
         this.checkPressed(p_196262_1_, p_196262_2_, p_196262_3_);
      }
   }

   private void checkPressed(BlockState p_185616_1_, World p_185616_2_, BlockPos p_185616_3_) {
      List<? extends Entity> lvt_4_1_ = p_185616_2_.getEntitiesWithinAABB(AbstractArrowEntity.class, p_185616_1_.getShape(p_185616_2_, p_185616_3_).getBoundingBox().offset(p_185616_3_));
      boolean lvt_5_1_ = !lvt_4_1_.isEmpty();
      boolean lvt_6_1_ = (Boolean)p_185616_1_.get(POWERED);
      if (lvt_5_1_ != lvt_6_1_) {
         p_185616_2_.setBlockState(p_185616_3_, (BlockState)p_185616_1_.with(POWERED, lvt_5_1_), 3);
         this.updateNeighbors(p_185616_1_, p_185616_2_, p_185616_3_);
         this.playSound((PlayerEntity)null, p_185616_2_, p_185616_3_, lvt_5_1_);
      }

      if (lvt_5_1_) {
         p_185616_2_.getPendingBlockTicks().scheduleTick(new BlockPos(p_185616_3_), this, this.tickRate(p_185616_2_));
      }

   }

   private void updateNeighbors(BlockState p_196368_1_, World p_196368_2_, BlockPos p_196368_3_) {
      p_196368_2_.notifyNeighborsOfStateChange(p_196368_3_, this);
      p_196368_2_.notifyNeighborsOfStateChange(p_196368_3_.offset(getFacing(p_196368_1_).getOpposite()), this);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, POWERED, FACE);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
      field_196370_b = Block.makeCuboidShape(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
      field_196371_c = Block.makeCuboidShape(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
      field_196376_y = Block.makeCuboidShape(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
      field_196377_z = Block.makeCuboidShape(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
      AABB_NORTH_OFF = Block.makeCuboidShape(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
      AABB_SOUTH_OFF = Block.makeCuboidShape(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
      AABB_WEST_OFF = Block.makeCuboidShape(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
      AABB_EAST_OFF = Block.makeCuboidShape(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
      field_196372_E = Block.makeCuboidShape(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
      field_196373_F = Block.makeCuboidShape(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
      field_196374_G = Block.makeCuboidShape(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
      field_196375_H = Block.makeCuboidShape(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
      AABB_NORTH_ON = Block.makeCuboidShape(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
      AABB_SOUTH_ON = Block.makeCuboidShape(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
      AABB_WEST_ON = Block.makeCuboidShape(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
      AABB_EAST_ON = Block.makeCuboidShape(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
   }
}
