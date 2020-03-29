package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

public class CoralBlock extends Block {
   private final Block deadBlock;

   public CoralBlock(Block p_i48893_1_, Block.Properties p_i48893_2_) {
      super(p_i48893_2_);
      this.deadBlock = p_i48893_1_;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!this.canLive(p_225534_2_, p_225534_3_)) {
         p_225534_2_.setBlockState(p_225534_3_, this.deadBlock.getDefaultState(), 2);
      }

   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!this.canLive(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 60 + p_196271_4_.getRandom().nextInt(40));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected boolean canLive(IBlockReader p_203943_1_, BlockPos p_203943_2_) {
      Direction[] var3 = Direction.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction lvt_6_1_ = var3[var5];
         IFluidState lvt_7_1_ = p_203943_1_.getFluidState(p_203943_2_.offset(lvt_6_1_));
         if (lvt_7_1_.isTagged(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      if (!this.canLive(p_196258_1_.getWorld(), p_196258_1_.getPos())) {
         p_196258_1_.getWorld().getPendingBlockTicks().scheduleTick(p_196258_1_.getPos(), this, 60 + p_196258_1_.getWorld().getRandom().nextInt(40));
      }

      return this.getDefaultState();
   }
}
