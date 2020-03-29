package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RangedCrossbowAttackGoal<T extends MonsterEntity & IRangedAttackMob & ICrossbowUser> extends Goal {
   private final T field_220748_a;
   private RangedCrossbowAttackGoal.CrossbowState field_220749_b;
   private final double field_220750_c;
   private final float field_220751_d;
   private int field_220752_e;
   private int field_220753_f;

   public RangedCrossbowAttackGoal(T p_i50322_1_, double p_i50322_2_, float p_i50322_4_) {
      this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
      this.field_220748_a = p_i50322_1_;
      this.field_220750_c = p_i50322_2_;
      this.field_220751_d = p_i50322_4_ * p_i50322_4_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean shouldExecute() {
      return this.func_220746_h() && this.func_220745_g();
   }

   private boolean func_220745_g() {
      return this.field_220748_a.isHolding(Items.CROSSBOW);
   }

   public boolean shouldContinueExecuting() {
      return this.func_220746_h() && (this.shouldExecute() || !this.field_220748_a.getNavigator().noPath()) && this.func_220745_g();
   }

   private boolean func_220746_h() {
      return this.field_220748_a.getAttackTarget() != null && this.field_220748_a.getAttackTarget().isAlive();
   }

   public void resetTask() {
      super.resetTask();
      this.field_220748_a.setAggroed(false);
      this.field_220748_a.setAttackTarget((LivingEntity)null);
      this.field_220752_e = 0;
      if (this.field_220748_a.isHandActive()) {
         this.field_220748_a.resetActiveHand();
         ((ICrossbowUser)this.field_220748_a).setCharging(false);
         CrossbowItem.setCharged(this.field_220748_a.getActiveItemStack(), false);
      }

   }

   public void tick() {
      LivingEntity lvt_1_1_ = this.field_220748_a.getAttackTarget();
      if (lvt_1_1_ != null) {
         boolean lvt_2_1_ = this.field_220748_a.getEntitySenses().canSee(lvt_1_1_);
         boolean lvt_3_1_ = this.field_220752_e > 0;
         if (lvt_2_1_ != lvt_3_1_) {
            this.field_220752_e = 0;
         }

         if (lvt_2_1_) {
            ++this.field_220752_e;
         } else {
            --this.field_220752_e;
         }

         double lvt_4_1_ = this.field_220748_a.getDistanceSq(lvt_1_1_);
         boolean lvt_6_1_ = (lvt_4_1_ > (double)this.field_220751_d || this.field_220752_e < 5) && this.field_220753_f == 0;
         if (lvt_6_1_) {
            this.field_220748_a.getNavigator().tryMoveToEntityLiving(lvt_1_1_, this.func_220747_j() ? this.field_220750_c : this.field_220750_c * 0.5D);
         } else {
            this.field_220748_a.getNavigator().clearPath();
         }

         this.field_220748_a.getLookController().setLookPositionWithEntity(lvt_1_1_, 30.0F, 30.0F);
         if (this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED) {
            if (!lvt_6_1_) {
               this.field_220748_a.setActiveHand(ProjectileHelper.getHandWith(this.field_220748_a, Items.CROSSBOW));
               this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.CHARGING;
               ((ICrossbowUser)this.field_220748_a).setCharging(true);
            }
         } else if (this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.CHARGING) {
            if (!this.field_220748_a.isHandActive()) {
               this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
            }

            int lvt_7_1_ = this.field_220748_a.getItemInUseMaxCount();
            ItemStack lvt_8_1_ = this.field_220748_a.getActiveItemStack();
            if (lvt_7_1_ >= CrossbowItem.getChargeTime(lvt_8_1_)) {
               this.field_220748_a.stopActiveHand();
               this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.CHARGED;
               this.field_220753_f = 20 + this.field_220748_a.getRNG().nextInt(20);
               ((ICrossbowUser)this.field_220748_a).setCharging(false);
            }
         } else if (this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.CHARGED) {
            --this.field_220753_f;
            if (this.field_220753_f == 0) {
               this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
            }
         } else if (this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && lvt_2_1_) {
            ((IRangedAttackMob)this.field_220748_a).attackEntityWithRangedAttack(lvt_1_1_, 1.0F);
            ItemStack lvt_7_2_ = this.field_220748_a.getHeldItem(ProjectileHelper.getHandWith(this.field_220748_a, Items.CROSSBOW));
            CrossbowItem.setCharged(lvt_7_2_, false);
            this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
         }

      }
   }

   private boolean func_220747_j() {
      return this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   }

   static enum CrossbowState {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;
   }
}
