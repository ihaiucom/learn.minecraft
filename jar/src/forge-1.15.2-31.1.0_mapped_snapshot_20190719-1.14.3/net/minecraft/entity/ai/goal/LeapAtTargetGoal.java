package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.Vec3d;

public class LeapAtTargetGoal extends Goal {
   private final MobEntity leaper;
   private LivingEntity leapTarget;
   private final float leapMotionY;

   public LeapAtTargetGoal(MobEntity p_i1630_1_, float p_i1630_2_) {
      this.leaper = p_i1630_1_;
      this.leapMotionY = p_i1630_2_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean shouldExecute() {
      if (this.leaper.isBeingRidden()) {
         return false;
      } else {
         this.leapTarget = this.leaper.getAttackTarget();
         if (this.leapTarget == null) {
            return false;
         } else {
            double lvt_1_1_ = this.leaper.getDistanceSq(this.leapTarget);
            if (lvt_1_1_ >= 4.0D && lvt_1_1_ <= 16.0D) {
               if (!this.leaper.onGround) {
                  return false;
               } else {
                  return this.leaper.getRNG().nextInt(5) == 0;
               }
            } else {
               return false;
            }
         }
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.leaper.onGround;
   }

   public void startExecuting() {
      Vec3d lvt_1_1_ = this.leaper.getMotion();
      Vec3d lvt_2_1_ = new Vec3d(this.leapTarget.func_226277_ct_() - this.leaper.func_226277_ct_(), 0.0D, this.leapTarget.func_226281_cx_() - this.leaper.func_226281_cx_());
      if (lvt_2_1_.lengthSquared() > 1.0E-7D) {
         lvt_2_1_ = lvt_2_1_.normalize().scale(0.4D).add(lvt_1_1_.scale(0.2D));
      }

      this.leaper.setMotion(lvt_2_1_.x, (double)this.leapMotionY, lvt_2_1_.z);
   }
}
