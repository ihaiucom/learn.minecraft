package net.minecraft.entity.ai.controller;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LookController {
   protected final MobEntity mob;
   protected float deltaLookYaw;
   protected float deltaLookPitch;
   protected boolean isLooking;
   protected double posX;
   protected double posY;
   protected double posZ;

   public LookController(MobEntity p_i1613_1_) {
      this.mob = p_i1613_1_;
   }

   public void func_220674_a(Vec3d p_220674_1_) {
      this.func_220679_a(p_220674_1_.x, p_220674_1_.y, p_220674_1_.z);
   }

   public void setLookPositionWithEntity(Entity p_75651_1_, float p_75651_2_, float p_75651_3_) {
      this.setLookPosition(p_75651_1_.func_226277_ct_(), func_220676_b(p_75651_1_), p_75651_1_.func_226281_cx_(), p_75651_2_, p_75651_3_);
   }

   public void func_220679_a(double p_220679_1_, double p_220679_3_, double p_220679_5_) {
      this.setLookPosition(p_220679_1_, p_220679_3_, p_220679_5_, (float)this.mob.func_213396_dB(), (float)this.mob.getVerticalFaceSpeed());
   }

   public void setLookPosition(double p_75650_1_, double p_75650_3_, double p_75650_5_, float p_75650_7_, float p_75650_8_) {
      this.posX = p_75650_1_;
      this.posY = p_75650_3_;
      this.posZ = p_75650_5_;
      this.deltaLookYaw = p_75650_7_;
      this.deltaLookPitch = p_75650_8_;
      this.isLooking = true;
   }

   public void tick() {
      if (this.func_220680_b()) {
         this.mob.rotationPitch = 0.0F;
      }

      if (this.isLooking) {
         this.isLooking = false;
         this.mob.rotationYawHead = this.func_220675_a(this.mob.rotationYawHead, this.func_220678_h(), this.deltaLookYaw);
         this.mob.rotationPitch = this.func_220675_a(this.mob.rotationPitch, this.func_220677_g(), this.deltaLookPitch);
      } else {
         this.mob.rotationYawHead = this.func_220675_a(this.mob.rotationYawHead, this.mob.renderYawOffset, 10.0F);
      }

      if (!this.mob.getNavigator().noPath()) {
         this.mob.rotationYawHead = MathHelper.func_219800_b(this.mob.rotationYawHead, this.mob.renderYawOffset, (float)this.mob.getHorizontalFaceSpeed());
      }

   }

   protected boolean func_220680_b() {
      return true;
   }

   public boolean getIsLooking() {
      return this.isLooking;
   }

   public double getLookPosX() {
      return this.posX;
   }

   public double getLookPosY() {
      return this.posY;
   }

   public double getLookPosZ() {
      return this.posZ;
   }

   protected float func_220677_g() {
      double lvt_1_1_ = this.posX - this.mob.func_226277_ct_();
      double lvt_3_1_ = this.posY - this.mob.func_226280_cw_();
      double lvt_5_1_ = this.posZ - this.mob.func_226281_cx_();
      double lvt_7_1_ = (double)MathHelper.sqrt(lvt_1_1_ * lvt_1_1_ + lvt_5_1_ * lvt_5_1_);
      return (float)(-(MathHelper.atan2(lvt_3_1_, lvt_7_1_) * 57.2957763671875D));
   }

   protected float func_220678_h() {
      double lvt_1_1_ = this.posX - this.mob.func_226277_ct_();
      double lvt_3_1_ = this.posZ - this.mob.func_226281_cx_();
      return (float)(MathHelper.atan2(lvt_3_1_, lvt_1_1_) * 57.2957763671875D) - 90.0F;
   }

   protected float func_220675_a(float p_220675_1_, float p_220675_2_, float p_220675_3_) {
      float lvt_4_1_ = MathHelper.wrapSubtractDegrees(p_220675_1_, p_220675_2_);
      float lvt_5_1_ = MathHelper.clamp(lvt_4_1_, -p_220675_3_, p_220675_3_);
      return p_220675_1_ + lvt_5_1_;
   }

   private static double func_220676_b(Entity p_220676_0_) {
      return p_220676_0_ instanceof LivingEntity ? p_220676_0_.func_226280_cw_() : (p_220676_0_.getBoundingBox().minY + p_220676_0_.getBoundingBox().maxY) / 2.0D;
   }
}
