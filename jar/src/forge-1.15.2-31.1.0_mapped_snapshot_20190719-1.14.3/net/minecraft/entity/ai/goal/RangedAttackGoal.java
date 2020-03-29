package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;

public class RangedAttackGoal extends Goal {
   private final MobEntity field_75322_b;
   private final IRangedAttackMob rangedAttackEntityHost;
   private LivingEntity field_75323_c;
   private int rangedAttackTime;
   private final double entityMoveSpeed;
   private int seeTime;
   private final int attackIntervalMin;
   private final int maxRangedAttackTime;
   private final float attackRadius;
   private final float maxAttackDistance;

   public RangedAttackGoal(IRangedAttackMob p_i1649_1_, double p_i1649_2_, int p_i1649_4_, float p_i1649_5_) {
      this(p_i1649_1_, p_i1649_2_, p_i1649_4_, p_i1649_4_, p_i1649_5_);
   }

   public RangedAttackGoal(IRangedAttackMob p_i1650_1_, double p_i1650_2_, int p_i1650_4_, int p_i1650_5_, float p_i1650_6_) {
      this.rangedAttackTime = -1;
      if (!(p_i1650_1_ instanceof LivingEntity)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      } else {
         this.rangedAttackEntityHost = p_i1650_1_;
         this.field_75322_b = (MobEntity)p_i1650_1_;
         this.entityMoveSpeed = p_i1650_2_;
         this.attackIntervalMin = p_i1650_4_;
         this.maxRangedAttackTime = p_i1650_5_;
         this.attackRadius = p_i1650_6_;
         this.maxAttackDistance = p_i1650_6_ * p_i1650_6_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }
   }

   public boolean shouldExecute() {
      LivingEntity lvt_1_1_ = this.field_75322_b.getAttackTarget();
      if (lvt_1_1_ != null && lvt_1_1_.isAlive()) {
         this.field_75323_c = lvt_1_1_;
         return true;
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return this.shouldExecute() || !this.field_75322_b.getNavigator().noPath();
   }

   public void resetTask() {
      this.field_75323_c = null;
      this.seeTime = 0;
      this.rangedAttackTime = -1;
   }

   public void tick() {
      double lvt_1_1_ = this.field_75322_b.getDistanceSq(this.field_75323_c.func_226277_ct_(), this.field_75323_c.func_226278_cu_(), this.field_75323_c.func_226281_cx_());
      boolean lvt_3_1_ = this.field_75322_b.getEntitySenses().canSee(this.field_75323_c);
      if (lvt_3_1_) {
         ++this.seeTime;
      } else {
         this.seeTime = 0;
      }

      if (lvt_1_1_ <= (double)this.maxAttackDistance && this.seeTime >= 5) {
         this.field_75322_b.getNavigator().clearPath();
      } else {
         this.field_75322_b.getNavigator().tryMoveToEntityLiving(this.field_75323_c, this.entityMoveSpeed);
      }

      this.field_75322_b.getLookController().setLookPositionWithEntity(this.field_75323_c, 30.0F, 30.0F);
      float lvt_4_1_;
      if (--this.rangedAttackTime == 0) {
         if (!lvt_3_1_) {
            return;
         }

         lvt_4_1_ = MathHelper.sqrt(lvt_1_1_) / this.attackRadius;
         float lvt_5_1_ = MathHelper.clamp(lvt_4_1_, 0.1F, 1.0F);
         this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.field_75323_c, lvt_5_1_);
         this.rangedAttackTime = MathHelper.floor(lvt_4_1_ * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
      } else if (this.rangedAttackTime < 0) {
         lvt_4_1_ = MathHelper.sqrt(lvt_1_1_) / this.attackRadius;
         this.rangedAttackTime = MathHelper.floor(lvt_4_1_ * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
      }

   }
}
