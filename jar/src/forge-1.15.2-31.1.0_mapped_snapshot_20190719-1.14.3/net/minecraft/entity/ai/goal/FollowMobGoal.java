package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;

public class FollowMobGoal extends Goal {
   private final MobEntity entity;
   private final Predicate<MobEntity> followPredicate;
   private MobEntity followingEntity;
   private final double speedModifier;
   private final PathNavigator navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private float oldWaterCost;
   private final float areaSize;

   public FollowMobGoal(MobEntity p_i47417_1_, double p_i47417_2_, float p_i47417_4_, float p_i47417_5_) {
      this.entity = p_i47417_1_;
      this.followPredicate = (p_210291_1_) -> {
         return p_210291_1_ != null && p_i47417_1_.getClass() != p_210291_1_.getClass();
      };
      this.speedModifier = p_i47417_2_;
      this.navigation = p_i47417_1_.getNavigator();
      this.stopDistance = p_i47417_4_;
      this.areaSize = p_i47417_5_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if (!(p_i47417_1_.getNavigator() instanceof GroundPathNavigator) && !(p_i47417_1_.getNavigator() instanceof FlyingPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
      }
   }

   public boolean shouldExecute() {
      List<MobEntity> lvt_1_1_ = this.entity.world.getEntitiesWithinAABB(MobEntity.class, this.entity.getBoundingBox().grow((double)this.areaSize), this.followPredicate);
      if (!lvt_1_1_.isEmpty()) {
         Iterator var2 = lvt_1_1_.iterator();

         while(var2.hasNext()) {
            MobEntity lvt_3_1_ = (MobEntity)var2.next();
            if (!lvt_3_1_.isInvisible()) {
               this.followingEntity = lvt_3_1_;
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldContinueExecuting() {
      return this.followingEntity != null && !this.navigation.noPath() && this.entity.getDistanceSq(this.followingEntity) > (double)(this.stopDistance * this.stopDistance);
   }

   public void startExecuting() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.entity.getPathPriority(PathNodeType.WATER);
      this.entity.setPathPriority(PathNodeType.WATER, 0.0F);
   }

   public void resetTask() {
      this.followingEntity = null;
      this.navigation.clearPath();
      this.entity.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
   }

   public void tick() {
      if (this.followingEntity != null && !this.entity.getLeashed()) {
         this.entity.getLookController().setLookPositionWithEntity(this.followingEntity, 10.0F, (float)this.entity.getVerticalFaceSpeed());
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            double lvt_1_1_ = this.entity.func_226277_ct_() - this.followingEntity.func_226277_ct_();
            double lvt_3_1_ = this.entity.func_226278_cu_() - this.followingEntity.func_226278_cu_();
            double lvt_5_1_ = this.entity.func_226281_cx_() - this.followingEntity.func_226281_cx_();
            double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;
            if (lvt_7_1_ > (double)(this.stopDistance * this.stopDistance)) {
               this.navigation.tryMoveToEntityLiving(this.followingEntity, this.speedModifier);
            } else {
               this.navigation.clearPath();
               LookController lvt_9_1_ = this.followingEntity.getLookController();
               if (lvt_7_1_ <= (double)this.stopDistance || lvt_9_1_.getLookPosX() == this.entity.func_226277_ct_() && lvt_9_1_.getLookPosY() == this.entity.func_226278_cu_() && lvt_9_1_.getLookPosZ() == this.entity.func_226281_cx_()) {
                  double lvt_10_1_ = this.followingEntity.func_226277_ct_() - this.entity.func_226277_ct_();
                  double lvt_12_1_ = this.followingEntity.func_226281_cx_() - this.entity.func_226281_cx_();
                  this.navigation.tryMoveToXYZ(this.entity.func_226277_ct_() - lvt_10_1_, this.entity.func_226278_cu_(), this.entity.func_226281_cx_() - lvt_12_1_, this.speedModifier);
               }

            }
         }
      }
   }
}
