package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;

public class BreatheAirGoal extends Goal {
   private final CreatureEntity field_205142_a;

   public BreatheAirGoal(CreatureEntity p_i48940_1_) {
      this.field_205142_a = p_i48940_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean shouldExecute() {
      return this.field_205142_a.getAir() < 140;
   }

   public boolean shouldContinueExecuting() {
      return this.shouldExecute();
   }

   public boolean isPreemptible() {
      return false;
   }

   public void startExecuting() {
      this.navigate();
   }

   private void navigate() {
      Iterable<BlockPos> lvt_1_1_ = BlockPos.getAllInBoxMutable(MathHelper.floor(this.field_205142_a.func_226277_ct_() - 1.0D), MathHelper.floor(this.field_205142_a.func_226278_cu_()), MathHelper.floor(this.field_205142_a.func_226281_cx_() - 1.0D), MathHelper.floor(this.field_205142_a.func_226277_ct_() + 1.0D), MathHelper.floor(this.field_205142_a.func_226278_cu_() + 8.0D), MathHelper.floor(this.field_205142_a.func_226281_cx_() + 1.0D));
      BlockPos lvt_2_1_ = null;
      Iterator var3 = lvt_1_1_.iterator();

      while(var3.hasNext()) {
         BlockPos lvt_4_1_ = (BlockPos)var3.next();
         if (this.canBreatheAt(this.field_205142_a.world, lvt_4_1_)) {
            lvt_2_1_ = lvt_4_1_;
            break;
         }
      }

      if (lvt_2_1_ == null) {
         lvt_2_1_ = new BlockPos(this.field_205142_a.func_226277_ct_(), this.field_205142_a.func_226278_cu_() + 8.0D, this.field_205142_a.func_226281_cx_());
      }

      this.field_205142_a.getNavigator().tryMoveToXYZ((double)lvt_2_1_.getX(), (double)(lvt_2_1_.getY() + 1), (double)lvt_2_1_.getZ(), 1.0D);
   }

   public void tick() {
      this.navigate();
      this.field_205142_a.moveRelative(0.02F, new Vec3d((double)this.field_205142_a.moveStrafing, (double)this.field_205142_a.moveVertical, (double)this.field_205142_a.moveForward));
      this.field_205142_a.move(MoverType.SELF, this.field_205142_a.getMotion());
   }

   private boolean canBreatheAt(IWorldReader p_205140_1_, BlockPos p_205140_2_) {
      BlockState lvt_3_1_ = p_205140_1_.getBlockState(p_205140_2_);
      return (p_205140_1_.getFluidState(p_205140_2_).isEmpty() || lvt_3_1_.getBlock() == Blocks.BUBBLE_COLUMN) && lvt_3_1_.allowsMovement(p_205140_1_, p_205140_2_, PathType.LAND);
   }
}
