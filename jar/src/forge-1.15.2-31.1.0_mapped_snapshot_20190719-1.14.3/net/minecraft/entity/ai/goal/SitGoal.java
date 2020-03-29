package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class SitGoal extends Goal {
   private final TameableEntity tameable;
   private boolean isSitting;

   public SitGoal(TameableEntity p_i1654_1_) {
      this.tameable = p_i1654_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean shouldContinueExecuting() {
      return this.isSitting;
   }

   public boolean shouldExecute() {
      if (!this.tameable.isTamed()) {
         return false;
      } else if (this.tameable.isInWaterOrBubbleColumn()) {
         return false;
      } else if (!this.tameable.onGround) {
         return false;
      } else {
         LivingEntity lvt_1_1_ = this.tameable.getOwner();
         if (lvt_1_1_ == null) {
            return true;
         } else {
            return this.tameable.getDistanceSq(lvt_1_1_) < 144.0D && lvt_1_1_.getRevengeTarget() != null ? false : this.isSitting;
         }
      }
   }

   public void startExecuting() {
      this.tameable.getNavigator().clearPath();
      this.tameable.setSitting(true);
   }

   public void resetTask() {
      this.tameable.setSitting(false);
   }

   public void setSitting(boolean p_75270_1_) {
      this.isSitting = p_75270_1_;
   }
}
