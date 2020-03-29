package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LeverBlock extends HorizontalFaceBlock {
   public static final BooleanProperty POWERED;
   protected static final VoxelShape LEVER_NORTH_AABB;
   protected static final VoxelShape LEVER_SOUTH_AABB;
   protected static final VoxelShape LEVER_WEST_AABB;
   protected static final VoxelShape LEVER_EAST_AABB;
   protected static final VoxelShape FLOOR_Z_SHAPE;
   protected static final VoxelShape FLOOR_X_SHAPE;
   protected static final VoxelShape CEILING_Z_SHAPE;
   protected static final VoxelShape CEILING_X_SHAPE;

   protected LeverBlock(Block.Properties p_i48369_1_) {
      super(p_i48369_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HORIZONTAL_FACING, Direction.NORTH)).with(POWERED, false)).with(FACE, AttachFace.WALL));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((AttachFace)p_220053_1_.get(FACE)) {
      case FLOOR:
         switch(((Direction)p_220053_1_.get(HORIZONTAL_FACING)).getAxis()) {
         case X:
            return FLOOR_X_SHAPE;
         case Z:
         default:
            return FLOOR_Z_SHAPE;
         }
      case WALL:
         switch((Direction)p_220053_1_.get(HORIZONTAL_FACING)) {
         case EAST:
            return LEVER_EAST_AABB;
         case WEST:
            return LEVER_WEST_AABB;
         case SOUTH:
            return LEVER_SOUTH_AABB;
         case NORTH:
         default:
            return LEVER_NORTH_AABB;
         }
      case CEILING:
      default:
         switch(((Direction)p_220053_1_.get(HORIZONTAL_FACING)).getAxis()) {
         case X:
            return CEILING_X_SHAPE;
         case Z:
         default:
            return CEILING_Z_SHAPE;
         }
      }
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      BlockState lvt_7_1_;
      if (p_225533_2_.isRemote) {
         lvt_7_1_ = (BlockState)p_225533_1_.cycle(POWERED);
         if ((Boolean)lvt_7_1_.get(POWERED)) {
            addParticles(lvt_7_1_, p_225533_2_, p_225533_3_, 1.0F);
         }

         return ActionResultType.SUCCESS;
      } else {
         lvt_7_1_ = this.func_226939_d_(p_225533_1_, p_225533_2_, p_225533_3_);
         float lvt_8_1_ = (Boolean)lvt_7_1_.get(POWERED) ? 0.6F : 0.5F;
         p_225533_2_.playSound((PlayerEntity)null, p_225533_3_, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, lvt_8_1_);
         return ActionResultType.SUCCESS;
      }
   }

   public BlockState func_226939_d_(BlockState p_226939_1_, World p_226939_2_, BlockPos p_226939_3_) {
      p_226939_1_ = (BlockState)p_226939_1_.cycle(POWERED);
      p_226939_2_.setBlockState(p_226939_3_, p_226939_1_, 3);
      this.updateNeighbors(p_226939_1_, p_226939_2_, p_226939_3_);
      return p_226939_1_;
   }

   private static void addParticles(BlockState p_196379_0_, IWorld p_196379_1_, BlockPos p_196379_2_, float p_196379_3_) {
      Direction lvt_4_1_ = ((Direction)p_196379_0_.get(HORIZONTAL_FACING)).getOpposite();
      Direction lvt_5_1_ = getFacing(p_196379_0_).getOpposite();
      double lvt_6_1_ = (double)p_196379_2_.getX() + 0.5D + 0.1D * (double)lvt_4_1_.getXOffset() + 0.2D * (double)lvt_5_1_.getXOffset();
      double lvt_8_1_ = (double)p_196379_2_.getY() + 0.5D + 0.1D * (double)lvt_4_1_.getYOffset() + 0.2D * (double)lvt_5_1_.getYOffset();
      double lvt_10_1_ = (double)p_196379_2_.getZ() + 0.5D + 0.1D * (double)lvt_4_1_.getZOffset() + 0.2D * (double)lvt_5_1_.getZOffset();
      p_196379_1_.addParticle(new RedstoneParticleData(1.0F, 0.0F, 0.0F, p_196379_3_), lvt_6_1_, lvt_8_1_, lvt_10_1_, 0.0D, 0.0D, 0.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if ((Boolean)p_180655_1_.get(POWERED) && p_180655_4_.nextFloat() < 0.25F) {
         addParticles(p_180655_1_, p_180655_2_, p_180655_3_, 0.5F);
      }

   }

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

   private void updateNeighbors(BlockState p_196378_1_, World p_196378_2_, BlockPos p_196378_3_) {
      p_196378_2_.notifyNeighborsOfStateChange(p_196378_3_, this);
      p_196378_2_.notifyNeighborsOfStateChange(p_196378_3_.offset(getFacing(p_196378_1_).getOpposite()), this);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACE, HORIZONTAL_FACING, POWERED);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
      LEVER_NORTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
      LEVER_SOUTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
      LEVER_WEST_AABB = Block.makeCuboidShape(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
      LEVER_EAST_AABB = Block.makeCuboidShape(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
      FLOOR_Z_SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
      FLOOR_X_SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
      CEILING_Z_SHAPE = Block.makeCuboidShape(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
      CEILING_X_SHAPE = Block.makeCuboidShape(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);
   }
}
