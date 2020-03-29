package net.minecraft.entity;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPredicate {
   public static final EntityPredicate DEFAULT = new EntityPredicate();
   private double distance = -1.0D;
   private boolean allowInvulnerable;
   private boolean friendlyFire;
   private boolean requireLineOfSight;
   private boolean skipAttackChecks;
   private boolean useVisibilityModifier = true;
   private Predicate<LivingEntity> customPredicate;

   public EntityPredicate setDistance(double p_221013_1_) {
      this.distance = p_221013_1_;
      return this;
   }

   public EntityPredicate allowInvulnerable() {
      this.allowInvulnerable = true;
      return this;
   }

   public EntityPredicate allowFriendlyFire() {
      this.friendlyFire = true;
      return this;
   }

   public EntityPredicate setLineOfSiteRequired() {
      this.requireLineOfSight = true;
      return this;
   }

   public EntityPredicate setSkipAttackChecks() {
      this.skipAttackChecks = true;
      return this;
   }

   public EntityPredicate setUseInvisibilityCheck() {
      this.useVisibilityModifier = false;
      return this;
   }

   public EntityPredicate setCustomPredicate(@Nullable Predicate<LivingEntity> p_221012_1_) {
      this.customPredicate = p_221012_1_;
      return this;
   }

   public boolean canTarget(@Nullable LivingEntity p_221015_1_, LivingEntity p_221015_2_) {
      if (p_221015_1_ == p_221015_2_) {
         return false;
      } else if (p_221015_2_.isSpectator()) {
         return false;
      } else if (!p_221015_2_.isAlive()) {
         return false;
      } else if (!this.allowInvulnerable && p_221015_2_.isInvulnerable()) {
         return false;
      } else if (this.customPredicate != null && !this.customPredicate.test(p_221015_2_)) {
         return false;
      } else {
         if (p_221015_1_ != null) {
            if (!this.skipAttackChecks) {
               if (!p_221015_1_.canAttack(p_221015_2_)) {
                  return false;
               }

               if (!p_221015_1_.canAttack(p_221015_2_.getType())) {
                  return false;
               }
            }

            if (!this.friendlyFire && p_221015_1_.isOnSameTeam(p_221015_2_)) {
               return false;
            }

            if (this.distance > 0.0D) {
               double lvt_3_1_ = this.useVisibilityModifier ? p_221015_2_.getVisibilityMultiplier(p_221015_1_) : 1.0D;
               double lvt_5_1_ = this.distance * lvt_3_1_;
               double lvt_7_1_ = p_221015_1_.getDistanceSq(p_221015_2_.func_226277_ct_(), p_221015_2_.func_226278_cu_(), p_221015_2_.func_226281_cx_());
               if (lvt_7_1_ > lvt_5_1_ * lvt_5_1_) {
                  return false;
               }
            }

            if (!this.requireLineOfSight && p_221015_1_ instanceof MobEntity && !((MobEntity)p_221015_1_).getEntitySenses().canSee(p_221015_2_)) {
               return false;
            }
         }

         return true;
      }
   }
}
