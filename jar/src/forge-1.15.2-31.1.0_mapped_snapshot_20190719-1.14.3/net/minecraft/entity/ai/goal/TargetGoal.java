package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class TargetGoal extends Goal {
   protected final MobEntity goalOwner;
   protected final boolean shouldCheckSight;
   private final boolean nearbyOnly;
   private int targetSearchStatus;
   private int targetSearchDelay;
   private int targetUnseenTicks;
   protected LivingEntity target;
   protected int unseenMemoryTicks;

   public TargetGoal(MobEntity p_i50308_1_, boolean p_i50308_2_) {
      this(p_i50308_1_, p_i50308_2_, false);
   }

   public TargetGoal(MobEntity p_i50309_1_, boolean p_i50309_2_, boolean p_i50309_3_) {
      this.unseenMemoryTicks = 60;
      this.goalOwner = p_i50309_1_;
      this.shouldCheckSight = p_i50309_2_;
      this.nearbyOnly = p_i50309_3_;
   }

   public boolean shouldContinueExecuting() {
      LivingEntity lvt_1_1_ = this.goalOwner.getAttackTarget();
      if (lvt_1_1_ == null) {
         lvt_1_1_ = this.target;
      }

      if (lvt_1_1_ == null) {
         return false;
      } else if (!lvt_1_1_.isAlive()) {
         return false;
      } else {
         Team lvt_2_1_ = this.goalOwner.getTeam();
         Team lvt_3_1_ = lvt_1_1_.getTeam();
         if (lvt_2_1_ != null && lvt_3_1_ == lvt_2_1_) {
            return false;
         } else {
            double lvt_4_1_ = this.getTargetDistance();
            if (this.goalOwner.getDistanceSq(lvt_1_1_) > lvt_4_1_ * lvt_4_1_) {
               return false;
            } else {
               if (this.shouldCheckSight) {
                  if (this.goalOwner.getEntitySenses().canSee(lvt_1_1_)) {
                     this.targetUnseenTicks = 0;
                  } else if (++this.targetUnseenTicks > this.unseenMemoryTicks) {
                     return false;
                  }
               }

               if (lvt_1_1_ instanceof PlayerEntity && ((PlayerEntity)lvt_1_1_).abilities.disableDamage) {
                  return false;
               } else {
                  this.goalOwner.setAttackTarget(lvt_1_1_);
                  return true;
               }
            }
         }
      }
   }

   protected double getTargetDistance() {
      IAttributeInstance lvt_1_1_ = this.goalOwner.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
      return lvt_1_1_ == null ? 16.0D : lvt_1_1_.getValue();
   }

   public void startExecuting() {
      this.targetSearchStatus = 0;
      this.targetSearchDelay = 0;
      this.targetUnseenTicks = 0;
   }

   public void resetTask() {
      this.goalOwner.setAttackTarget((LivingEntity)null);
      this.target = null;
   }

   protected boolean isSuitableTarget(@Nullable LivingEntity p_220777_1_, EntityPredicate p_220777_2_) {
      if (p_220777_1_ == null) {
         return false;
      } else if (!p_220777_2_.canTarget(this.goalOwner, p_220777_1_)) {
         return false;
      } else if (!this.goalOwner.isWithinHomeDistanceFromPosition(new BlockPos(p_220777_1_))) {
         return false;
      } else {
         if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
               this.targetSearchStatus = 0;
            }

            if (this.targetSearchStatus == 0) {
               this.targetSearchStatus = this.canEasilyReach(p_220777_1_) ? 1 : 2;
            }

            if (this.targetSearchStatus == 2) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean canEasilyReach(LivingEntity p_75295_1_) {
      this.targetSearchDelay = 10 + this.goalOwner.getRNG().nextInt(5);
      Path lvt_2_1_ = this.goalOwner.getNavigator().getPathToEntityLiving(p_75295_1_, 0);
      if (lvt_2_1_ == null) {
         return false;
      } else {
         PathPoint lvt_3_1_ = lvt_2_1_.getFinalPathPoint();
         if (lvt_3_1_ == null) {
            return false;
         } else {
            int lvt_4_1_ = lvt_3_1_.x - MathHelper.floor(p_75295_1_.func_226277_ct_());
            int lvt_5_1_ = lvt_3_1_.z - MathHelper.floor(p_75295_1_.func_226281_cx_());
            return (double)(lvt_4_1_ * lvt_4_1_ + lvt_5_1_ * lvt_5_1_) <= 2.25D;
         }
      }
   }

   public TargetGoal setUnseenMemoryTicks(int p_190882_1_) {
      this.unseenMemoryTicks = p_190882_1_;
      return this;
   }
}
