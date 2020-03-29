package net.minecraft.entity.ai.controller;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;

public class BodyController {
   private final MobEntity mob;
   private int rotationTickCounter;
   private float prevRenderYawHead;

   public BodyController(MobEntity p_i50334_1_) {
      this.mob = p_i50334_1_;
   }

   public void updateRenderAngles() {
      if (this.func_220662_f()) {
         this.mob.renderYawOffset = this.mob.rotationYaw;
         this.func_220664_c();
         this.prevRenderYawHead = this.mob.rotationYawHead;
         this.rotationTickCounter = 0;
      } else {
         if (this.func_220661_e()) {
            if (Math.abs(this.mob.rotationYawHead - this.prevRenderYawHead) > 15.0F) {
               this.rotationTickCounter = 0;
               this.prevRenderYawHead = this.mob.rotationYawHead;
               this.func_220663_b();
            } else {
               ++this.rotationTickCounter;
               if (this.rotationTickCounter > 10) {
                  this.func_220665_d();
               }
            }
         }

      }
   }

   private void func_220663_b() {
      this.mob.renderYawOffset = MathHelper.func_219800_b(this.mob.renderYawOffset, this.mob.rotationYawHead, (float)this.mob.getHorizontalFaceSpeed());
   }

   private void func_220664_c() {
      this.mob.rotationYawHead = MathHelper.func_219800_b(this.mob.rotationYawHead, this.mob.renderYawOffset, (float)this.mob.getHorizontalFaceSpeed());
   }

   private void func_220665_d() {
      int lvt_1_1_ = this.rotationTickCounter - 10;
      float lvt_2_1_ = MathHelper.clamp((float)lvt_1_1_ / 10.0F, 0.0F, 1.0F);
      float lvt_3_1_ = (float)this.mob.getHorizontalFaceSpeed() * (1.0F - lvt_2_1_);
      this.mob.renderYawOffset = MathHelper.func_219800_b(this.mob.renderYawOffset, this.mob.rotationYawHead, lvt_3_1_);
   }

   private boolean func_220661_e() {
      return this.mob.getPassengers().isEmpty() || !(this.mob.getPassengers().get(0) instanceof MobEntity);
   }

   private boolean func_220662_f() {
      double lvt_1_1_ = this.mob.func_226277_ct_() - this.mob.prevPosX;
      double lvt_3_1_ = this.mob.func_226281_cx_() - this.mob.prevPosZ;
      return lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ > 2.500000277905201E-7D;
   }
}
