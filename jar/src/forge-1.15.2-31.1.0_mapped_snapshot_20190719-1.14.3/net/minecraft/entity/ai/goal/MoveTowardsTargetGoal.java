package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class MoveTowardsTargetGoal extends Goal {
   private final CreatureEntity creature;
   private LivingEntity field_75429_b;
   private double movePosX;
   private double movePosY;
   private double movePosZ;
   private final double speed;
   private final float maxTargetDistance;

   public MoveTowardsTargetGoal(CreatureEntity p_i1640_1_, double p_i1640_2_, float p_i1640_4_) {
      this.creature = p_i1640_1_;
      this.speed = p_i1640_2_;
      this.maxTargetDistance = p_i1640_4_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean shouldExecute() {
      this.field_75429_b = this.creature.getAttackTarget();
      if (this.field_75429_b == null) {
         return false;
      } else if (this.field_75429_b.getDistanceSq(this.creature) > (double)(this.maxTargetDistance * this.maxTargetDistance)) {
         return false;
      } else {
         Vec3d lvt_1_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.creature, 16, 7, this.field_75429_b.getPositionVec());
         if (lvt_1_1_ == null) {
            return false;
         } else {
            this.movePosX = lvt_1_1_.x;
            this.movePosY = lvt_1_1_.y;
            this.movePosZ = lvt_1_1_.z;
            return true;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.creature.getNavigator().noPath() && this.field_75429_b.isAlive() && this.field_75429_b.getDistanceSq(this.creature) < (double)(this.maxTargetDistance * this.maxTargetDistance);
   }

   public void resetTask() {
      this.field_75429_b = null;
   }

   public void startExecuting() {
      this.creature.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
   }
}
