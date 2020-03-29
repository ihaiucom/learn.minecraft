package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.IBlockReader;

public class OcelotAttackGoal extends Goal {
   private final IBlockReader world;
   private final MobEntity entity;
   private LivingEntity target;
   private int attackCountdown;

   public OcelotAttackGoal(MobEntity p_i1641_1_) {
      this.entity = p_i1641_1_;
      this.world = p_i1641_1_.world;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean shouldExecute() {
      LivingEntity lvt_1_1_ = this.entity.getAttackTarget();
      if (lvt_1_1_ == null) {
         return false;
      } else {
         this.target = lvt_1_1_;
         return true;
      }
   }

   public boolean shouldContinueExecuting() {
      if (!this.target.isAlive()) {
         return false;
      } else if (this.entity.getDistanceSq(this.target) > 225.0D) {
         return false;
      } else {
         return !this.entity.getNavigator().noPath() || this.shouldExecute();
      }
   }

   public void resetTask() {
      this.target = null;
      this.entity.getNavigator().clearPath();
   }

   public void tick() {
      this.entity.getLookController().setLookPositionWithEntity(this.target, 30.0F, 30.0F);
      double lvt_1_1_ = (double)(this.entity.getWidth() * 2.0F * this.entity.getWidth() * 2.0F);
      double lvt_3_1_ = this.entity.getDistanceSq(this.target.func_226277_ct_(), this.target.func_226278_cu_(), this.target.func_226281_cx_());
      double lvt_5_1_ = 0.8D;
      if (lvt_3_1_ > lvt_1_1_ && lvt_3_1_ < 16.0D) {
         lvt_5_1_ = 1.33D;
      } else if (lvt_3_1_ < 225.0D) {
         lvt_5_1_ = 0.6D;
      }

      this.entity.getNavigator().tryMoveToEntityLiving(this.target, lvt_5_1_);
      this.attackCountdown = Math.max(this.attackCountdown - 1, 0);
      if (lvt_3_1_ <= lvt_1_1_) {
         if (this.attackCountdown <= 0) {
            this.attackCountdown = 20;
            this.entity.attackEntityAsMob(this.target);
         }
      }
   }
}
