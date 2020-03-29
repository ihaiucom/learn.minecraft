package net.minecraft.entity.ai.controller;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.MathHelper;

public class FlyingMovementController extends MovementController {
   private final int field_226323_i_;
   private final boolean field_226324_j_;

   public FlyingMovementController(MobEntity p_i225710_1_, int p_i225710_2_, boolean p_i225710_3_) {
      super(p_i225710_1_);
      this.field_226323_i_ = p_i225710_2_;
      this.field_226324_j_ = p_i225710_3_;
   }

   public void tick() {
      if (this.action == MovementController.Action.MOVE_TO) {
         this.action = MovementController.Action.WAIT;
         this.mob.setNoGravity(true);
         double lvt_1_1_ = this.posX - this.mob.func_226277_ct_();
         double lvt_3_1_ = this.posY - this.mob.func_226278_cu_();
         double lvt_5_1_ = this.posZ - this.mob.func_226281_cx_();
         double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;
         if (lvt_7_1_ < 2.500000277905201E-7D) {
            this.mob.setMoveVertical(0.0F);
            this.mob.setMoveForward(0.0F);
            return;
         }

         float lvt_9_1_ = (float)(MathHelper.atan2(lvt_5_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
         this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, lvt_9_1_, 90.0F);
         float lvt_10_2_;
         if (this.mob.onGround) {
            lvt_10_2_ = (float)(this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
         } else {
            lvt_10_2_ = (float)(this.speed * this.mob.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getValue());
         }

         this.mob.setAIMoveSpeed(lvt_10_2_);
         double lvt_11_1_ = (double)MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_);
         float lvt_13_1_ = (float)(-(MathHelper.atan2(lvt_3_1_, lvt_11_1_) * 57.2957763671875D));
         this.mob.rotationPitch = this.limitAngle(this.mob.rotationPitch, lvt_13_1_, (float)this.field_226323_i_);
         this.mob.setMoveVertical(lvt_3_1_ > 0.0D ? lvt_10_2_ : -lvt_10_2_);
      } else {
         if (!this.field_226324_j_) {
            this.mob.setNoGravity(false);
         }

         this.mob.setMoveVertical(0.0F);
         this.mob.setMoveForward(0.0F);
      }

   }
}
