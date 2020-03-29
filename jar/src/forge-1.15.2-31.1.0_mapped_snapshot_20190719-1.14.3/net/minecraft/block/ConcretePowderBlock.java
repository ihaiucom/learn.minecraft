package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConcretePowderBlock extends FallingBlock {
   private final BlockState solidifiedState;

   public ConcretePowderBlock(Block p_i48423_1_, Block.Properties p_i48423_2_) {
      super(p_i48423_2_);
      this.solidifiedState = p_i48423_1_.getDefaultState();
   }

   public void onEndFalling(World p_176502_1_, BlockPos p_176502_2_, BlockState p_176502_3_, BlockState p_176502_4_) {
      if (func_230137_b_(p_176502_1_, p_176502_2_, p_176502_4_)) {
         p_176502_1_.setBlockState(p_176502_2_, this.solidifiedState, 3);
      }

   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader lvt_2_1_ = p_196258_1_.getWorld();
      BlockPos lvt_3_1_ = p_196258_1_.getPos();
      BlockState lvt_4_1_ = lvt_2_1_.getBlockState(lvt_3_1_);
      return func_230137_b_(lvt_2_1_, lvt_3_1_, lvt_4_1_) ? this.solidifiedState : super.getStateForPlacement(p_196258_1_);
   }

   private static boolean func_230137_b_(IBlockReader p_230137_0_, BlockPos p_230137_1_, BlockState p_230137_2_) {
      return causesSolidify(p_230137_2_) || isTouchingLiquid(p_230137_0_, p_230137_1_);
   }

   private static boolean isTouchingLiquid(IBlockReader p_196441_0_, BlockPos p_196441_1_) {
      boolean lvt_2_1_ = false;
      BlockPos.Mutable lvt_3_1_ = new BlockPos.Mutable(p_196441_1_);
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction lvt_7_1_ = var4[var6];
         BlockState lvt_8_1_ = p_196441_0_.getBlockState(lvt_3_1_);
         if (lvt_7_1_ != Direction.DOWN || causesSolidify(lvt_8_1_)) {
            lvt_3_1_.setPos((Vec3i)p_196441_1_).move(lvt_7_1_);
            lvt_8_1_ = p_196441_0_.getBlockState(lvt_3_1_);
            if (causesSolidify(lvt_8_1_) && !lvt_8_1_.func_224755_d(p_196441_0_, p_196441_1_, lvt_7_1_.getOpposite())) {
               lvt_2_1_ = true;
               break;
            }
         }
      }

      return lvt_2_1_;
   }

   private static boolean causesSolidify(BlockState p_212566_0_) {
      return p_212566_0_.getFluidState().isTagged(FluidTags.WATER);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return isTouchingLiquid(p_196271_4_, p_196271_5_) ? this.solidifiedState : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }
}
