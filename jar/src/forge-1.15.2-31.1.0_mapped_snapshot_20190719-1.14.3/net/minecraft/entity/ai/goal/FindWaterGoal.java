package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FindWaterGoal extends Goal {
   private final CreatureEntity field_205152_a;

   public FindWaterGoal(CreatureEntity p_i48936_1_) {
      this.field_205152_a = p_i48936_1_;
   }

   public boolean shouldExecute() {
      return this.field_205152_a.onGround && !this.field_205152_a.world.getFluidState(new BlockPos(this.field_205152_a)).isTagged(FluidTags.WATER);
   }

   public void startExecuting() {
      BlockPos lvt_1_1_ = null;
      Iterable<BlockPos> lvt_2_1_ = BlockPos.getAllInBoxMutable(MathHelper.floor(this.field_205152_a.func_226277_ct_() - 2.0D), MathHelper.floor(this.field_205152_a.func_226278_cu_() - 2.0D), MathHelper.floor(this.field_205152_a.func_226281_cx_() - 2.0D), MathHelper.floor(this.field_205152_a.func_226277_ct_() + 2.0D), MathHelper.floor(this.field_205152_a.func_226278_cu_()), MathHelper.floor(this.field_205152_a.func_226281_cx_() + 2.0D));
      Iterator var3 = lvt_2_1_.iterator();

      while(var3.hasNext()) {
         BlockPos lvt_4_1_ = (BlockPos)var3.next();
         if (this.field_205152_a.world.getFluidState(lvt_4_1_).isTagged(FluidTags.WATER)) {
            lvt_1_1_ = lvt_4_1_;
            break;
         }
      }

      if (lvt_1_1_ != null) {
         this.field_205152_a.getMoveHelper().setMoveTo((double)lvt_1_1_.getX(), (double)lvt_1_1_.getY(), (double)lvt_1_1_.getZ(), 1.0D);
      }

   }
}
