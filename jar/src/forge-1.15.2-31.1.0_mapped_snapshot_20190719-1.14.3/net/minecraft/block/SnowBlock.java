package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.server.ServerWorld;

public class SnowBlock extends Block {
   public static final IntegerProperty LAYERS;
   protected static final VoxelShape[] SHAPES;

   protected SnowBlock(Block.Properties p_i48328_1_) {
      super(p_i48328_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LAYERS, 1));
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return (Integer)p_196266_1_.get(LAYERS) < 5;
      case WATER:
         return false;
      case AIR:
         return false;
      default:
         return false;
      }
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPES[(Integer)p_220053_1_.get(LAYERS)];
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return SHAPES[(Integer)p_220071_1_.get(LAYERS) - 1];
   }

   public boolean func_220074_n(BlockState p_220074_1_) {
      return true;
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockState lvt_4_1_ = p_196260_2_.getBlockState(p_196260_3_.down());
      Block lvt_5_1_ = lvt_4_1_.getBlock();
      if (lvt_5_1_ != Blocks.ICE && lvt_5_1_ != Blocks.PACKED_ICE && lvt_5_1_ != Blocks.BARRIER) {
         if (lvt_5_1_ != Blocks.field_226907_mc_ && lvt_5_1_ != Blocks.SOUL_SAND) {
            return Block.doesSideFillSquare(lvt_4_1_.getCollisionShape(p_196260_2_, p_196260_3_.down()), Direction.UP) || lvt_5_1_ == this && (Integer)lvt_4_1_.get(LAYERS) == 8;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_2_.func_226658_a_(LightType.BLOCK, p_225534_3_) > 11) {
         spawnDrops(p_225534_1_, p_225534_2_, p_225534_3_);
         p_225534_2_.removeBlock(p_225534_3_, false);
      }

   }

   public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      int lvt_3_1_ = (Integer)p_196253_1_.get(LAYERS);
      if (p_196253_2_.getItem().getItem() == this.asItem() && lvt_3_1_ < 8) {
         if (p_196253_2_.replacingClickedOnBlock()) {
            return p_196253_2_.getFace() == Direction.UP;
         } else {
            return true;
         }
      } else {
         return lvt_3_1_ == 1;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState lvt_2_1_ = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos());
      if (lvt_2_1_.getBlock() == this) {
         int lvt_3_1_ = (Integer)lvt_2_1_.get(LAYERS);
         return (BlockState)lvt_2_1_.with(LAYERS, Math.min(8, lvt_3_1_ + 1));
      } else {
         return super.getStateForPlacement(p_196258_1_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LAYERS);
   }

   static {
      LAYERS = BlockStateProperties.LAYERS_1_8;
      SHAPES = new VoxelShape[]{VoxelShapes.empty(), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
   }
}
