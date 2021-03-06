package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class OwnerHurtByTargetGoal extends TargetGoal {
   private final TameableEntity tameable;
   private LivingEntity attacker;
   private int timestamp;

   public OwnerHurtByTargetGoal(TameableEntity p_i1667_1_) {
      super(p_i1667_1_, false);
      this.tameable = p_i1667_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean shouldExecute() {
      if (this.tameable.isTamed() && !this.tameable.isSitting()) {
         LivingEntity lvt_1_1_ = this.tameable.getOwner();
         if (lvt_1_1_ == null) {
            return false;
         } else {
            this.attacker = lvt_1_1_.getRevengeTarget();
            int lvt_2_1_ = lvt_1_1_.getRevengeTimer();
            return lvt_2_1_ != this.timestamp && this.isSuitableTarget(this.attacker, EntityPredicate.DEFAULT) && this.tameable.shouldAttackEntity(this.attacker, lvt_1_1_);
         }
      } else {
         return false;
      }
   }

   public void startExecuting() {
      this.goalOwner.setAttackTarget(this.attacker);
      LivingEntity lvt_1_1_ = this.tameable.getOwner();
      if (lvt_1_1_ != null) {
         this.timestamp = lvt_1_1_.getRevengeTimer();
      }

      super.startExecuting();
   }
}
