package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;

public class LookAtGoal extends Goal {
   protected final MobEntity entity;
   protected Entity closestEntity;
   protected final float maxDistance;
   private int lookTime;
   protected final float chance;
   protected final Class<? extends LivingEntity> watchedClass;
   protected final EntityPredicate field_220716_e;

   public LookAtGoal(MobEntity p_i1631_1_, Class<? extends LivingEntity> p_i1631_2_, float p_i1631_3_) {
      this(p_i1631_1_, p_i1631_2_, p_i1631_3_, 0.02F);
   }

   public LookAtGoal(MobEntity p_i1632_1_, Class<? extends LivingEntity> p_i1632_2_, float p_i1632_3_, float p_i1632_4_) {
      this.entity = p_i1632_1_;
      this.watchedClass = p_i1632_2_;
      this.maxDistance = p_i1632_3_;
      this.chance = p_i1632_4_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
      if (p_i1632_2_ == PlayerEntity.class) {
         this.field_220716_e = (new EntityPredicate()).setDistance((double)p_i1632_3_).allowFriendlyFire().allowInvulnerable().setSkipAttackChecks().setCustomPredicate((p_220715_1_) -> {
            return EntityPredicates.notRiding(p_i1632_1_).test(p_220715_1_);
         });
      } else {
         this.field_220716_e = (new EntityPredicate()).setDistance((double)p_i1632_3_).allowFriendlyFire().allowInvulnerable().setSkipAttackChecks();
      }

   }

   public boolean shouldExecute() {
      if (this.entity.getRNG().nextFloat() >= this.chance) {
         return false;
      } else {
         if (this.entity.getAttackTarget() != null) {
            this.closestEntity = this.entity.getAttackTarget();
         }

         if (this.watchedClass == PlayerEntity.class) {
            this.closestEntity = this.entity.world.getClosestPlayer(this.field_220716_e, this.entity, this.entity.func_226277_ct_(), this.entity.func_226280_cw_(), this.entity.func_226281_cx_());
         } else {
            this.closestEntity = this.entity.world.func_225318_b(this.watchedClass, this.field_220716_e, this.entity, this.entity.func_226277_ct_(), this.entity.func_226280_cw_(), this.entity.func_226281_cx_(), this.entity.getBoundingBox().grow((double)this.maxDistance, 3.0D, (double)this.maxDistance));
         }

         return this.closestEntity != null;
      }
   }

   public boolean shouldContinueExecuting() {
      if (!this.closestEntity.isAlive()) {
         return false;
      } else if (this.entity.getDistanceSq(this.closestEntity) > (double)(this.maxDistance * this.maxDistance)) {
         return false;
      } else {
         return this.lookTime > 0;
      }
   }

   public void startExecuting() {
      this.lookTime = 40 + this.entity.getRNG().nextInt(40);
   }

   public void resetTask() {
      this.closestEntity = null;
   }

   public void tick() {
      this.entity.getLookController().func_220679_a(this.closestEntity.func_226277_ct_(), this.closestEntity.func_226280_cw_(), this.closestEntity.func_226281_cx_());
      --this.lookTime;
   }
}
