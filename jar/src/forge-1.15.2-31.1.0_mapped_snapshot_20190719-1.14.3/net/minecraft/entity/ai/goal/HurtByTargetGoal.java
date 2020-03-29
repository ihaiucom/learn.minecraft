package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class HurtByTargetGoal extends TargetGoal {
   private static final EntityPredicate field_220795_a = (new EntityPredicate()).setLineOfSiteRequired().setUseInvisibilityCheck();
   private boolean entityCallsForHelp;
   private int revengeTimerOld;
   private final Class<?>[] excludedReinforcementTypes;
   private Class<?>[] field_220797_i;

   public HurtByTargetGoal(CreatureEntity p_i50317_1_, Class<?>... p_i50317_2_) {
      super(p_i50317_1_, true);
      this.excludedReinforcementTypes = p_i50317_2_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean shouldExecute() {
      int lvt_1_1_ = this.goalOwner.getRevengeTimer();
      LivingEntity lvt_2_1_ = this.goalOwner.getRevengeTarget();
      if (lvt_1_1_ != this.revengeTimerOld && lvt_2_1_ != null) {
         Class[] var3 = this.excludedReinforcementTypes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Class<?> lvt_6_1_ = var3[var5];
            if (lvt_6_1_.isAssignableFrom(lvt_2_1_.getClass())) {
               return false;
            }
         }

         return this.isSuitableTarget(lvt_2_1_, field_220795_a);
      } else {
         return false;
      }
   }

   public HurtByTargetGoal setCallsForHelp(Class<?>... p_220794_1_) {
      this.entityCallsForHelp = true;
      this.field_220797_i = p_220794_1_;
      return this;
   }

   public void startExecuting() {
      this.goalOwner.setAttackTarget(this.goalOwner.getRevengeTarget());
      this.target = this.goalOwner.getAttackTarget();
      this.revengeTimerOld = this.goalOwner.getRevengeTimer();
      this.unseenMemoryTicks = 300;
      if (this.entityCallsForHelp) {
         this.alertOthers();
      }

      super.startExecuting();
   }

   protected void alertOthers() {
      double lvt_1_1_ = this.getTargetDistance();
      List<MobEntity> lvt_3_1_ = this.goalOwner.world.func_225317_b(this.goalOwner.getClass(), (new AxisAlignedBB(this.goalOwner.func_226277_ct_(), this.goalOwner.func_226278_cu_(), this.goalOwner.func_226281_cx_(), this.goalOwner.func_226277_ct_() + 1.0D, this.goalOwner.func_226278_cu_() + 1.0D, this.goalOwner.func_226281_cx_() + 1.0D)).grow(lvt_1_1_, 10.0D, lvt_1_1_));
      Iterator var4 = lvt_3_1_.iterator();

      while(true) {
         MobEntity lvt_5_1_;
         boolean lvt_6_1_;
         do {
            do {
               do {
                  do {
                     do {
                        if (!var4.hasNext()) {
                           return;
                        }

                        lvt_5_1_ = (MobEntity)var4.next();
                     } while(this.goalOwner == lvt_5_1_);
                  } while(lvt_5_1_.getAttackTarget() != null);
               } while(this.goalOwner instanceof TameableEntity && ((TameableEntity)this.goalOwner).getOwner() != ((TameableEntity)lvt_5_1_).getOwner());
            } while(lvt_5_1_.isOnSameTeam(this.goalOwner.getRevengeTarget()));

            if (this.field_220797_i == null) {
               break;
            }

            lvt_6_1_ = false;
            Class[] var7 = this.field_220797_i;
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Class<?> lvt_10_1_ = var7[var9];
               if (lvt_5_1_.getClass() == lvt_10_1_) {
                  lvt_6_1_ = true;
                  break;
               }
            }
         } while(lvt_6_1_);

         this.setAttackTarget(lvt_5_1_, this.goalOwner.getRevengeTarget());
      }
   }

   protected void setAttackTarget(MobEntity p_220793_1_, LivingEntity p_220793_2_) {
      p_220793_1_.setAttackTarget(p_220793_2_);
   }
}
