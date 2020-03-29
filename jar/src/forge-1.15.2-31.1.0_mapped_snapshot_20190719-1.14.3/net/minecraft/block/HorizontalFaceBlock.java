package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class HorizontalFaceBlock extends HorizontalBlock {
   public static final EnumProperty<AttachFace> FACE;

   protected HorizontalFaceBlock(Block.Properties p_i48402_1_) {
      super(p_i48402_1_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return func_220185_b(p_196260_2_, p_196260_3_, getFacing(p_196260_1_).getOpposite());
   }

   public static boolean func_220185_b(IWorldReader p_220185_0_, BlockPos p_220185_1_, Direction p_220185_2_) {
      BlockPos lvt_3_1_ = p_220185_1_.offset(p_220185_2_);
      return p_220185_0_.getBlockState(lvt_3_1_).func_224755_d(p_220185_0_, lvt_3_1_, p_220185_2_.getOpposite());
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction[] var2 = p_196258_1_.getNearestLookingDirections();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction lvt_5_1_ = var2[var4];
         BlockState lvt_6_2_;
         if (lvt_5_1_.getAxis() == Direction.Axis.Y) {
            lvt_6_2_ = (BlockState)((BlockState)this.getDefaultState().with(FACE, lvt_5_1_ == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)).with(HORIZONTAL_FACING, p_196258_1_.getPlacementHorizontalFacing());
         } else {
            lvt_6_2_ = (BlockState)((BlockState)this.getDefaultState().with(FACE, AttachFace.WALL)).with(HORIZONTAL_FACING, lvt_5_1_.getOpposite());
         }

         if (lvt_6_2_.isValidPosition(p_196258_1_.getWorld(), p_196258_1_.getPos())) {
            return lvt_6_2_;
         }
      }

      return null;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return getFacing(p_196271_1_).getOpposite() == p_196271_2_ && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected static Direction getFacing(BlockState p_196365_0_) {
      switch((AttachFace)p_196365_0_.get(FACE)) {
      case CEILING:
         return Direction.DOWN;
      case FLOOR:
         return Direction.UP;
      default:
         return (Direction)p_196365_0_.get(HORIZONTAL_FACING);
      }
   }

   static {
      FACE = BlockStateProperties.FACE;
   }
}
