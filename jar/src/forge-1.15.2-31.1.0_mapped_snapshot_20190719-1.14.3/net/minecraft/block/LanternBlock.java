package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
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

public class LanternBlock extends Block {
   public static final BooleanProperty HANGING;
   protected static final VoxelShape field_220279_b;
   protected static final VoxelShape field_220280_c;

   public LanternBlock(Block.Properties p_i49980_1_) {
      super(p_i49980_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HANGING, false));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction[] var2 = p_196258_1_.getNearestLookingDirections();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction lvt_5_1_ = var2[var4];
         if (lvt_5_1_.getAxis() == Direction.Axis.Y) {
            BlockState lvt_6_1_ = (BlockState)this.getDefaultState().with(HANGING, lvt_5_1_ == Direction.UP);
            if (lvt_6_1_.isValidPosition(p_196258_1_.getWorld(), p_196258_1_.getPos())) {
               return lvt_6_1_;
            }
         }
      }

      return null;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (Boolean)p_220053_1_.get(HANGING) ? field_220280_c : field_220279_b;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HANGING);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Direction lvt_4_1_ = func_220277_j(p_196260_1_).getOpposite();
      return Block.func_220055_a(p_196260_2_, p_196260_3_.offset(lvt_4_1_), lvt_4_1_.getOpposite());
   }

   protected static Direction func_220277_j(BlockState p_220277_0_) {
      return (Boolean)p_220277_0_.get(HANGING) ? Direction.DOWN : Direction.UP;
   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return func_220277_j(p_196271_1_).getOpposite() == p_196271_2_ && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      HANGING = BlockStateProperties.HANGING;
      field_220279_b = VoxelShapes.or(Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D), Block.makeCuboidShape(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D));
      field_220280_c = VoxelShapes.or(Block.makeCuboidShape(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D), Block.makeCuboidShape(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D));
   }
}
