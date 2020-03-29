package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElytraModel<T extends LivingEntity> extends AgeableModel<T> {
   private final ModelRenderer rightWing;
   private final ModelRenderer leftWing = new ModelRenderer(this, 22, 0);

   public ElytraModel() {
      this.leftWing.func_228301_a_(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
      this.rightWing = new ModelRenderer(this, 22, 0);
      this.rightWing.mirror = true;
      this.rightWing.func_228301_a_(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
   }

   protected Iterable<ModelRenderer> func_225602_a_() {
      return ImmutableList.of();
   }

   protected Iterable<ModelRenderer> func_225600_b_() {
      return ImmutableList.of(this.leftWing, this.rightWing);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float lvt_7_1_ = 0.2617994F;
      float lvt_8_1_ = -0.2617994F;
      float lvt_9_1_ = 0.0F;
      float lvt_10_1_ = 0.0F;
      if (p_225597_1_.isElytraFlying()) {
         float lvt_11_1_ = 1.0F;
         Vec3d lvt_12_1_ = p_225597_1_.getMotion();
         if (lvt_12_1_.y < 0.0D) {
            Vec3d lvt_13_1_ = lvt_12_1_.normalize();
            lvt_11_1_ = 1.0F - (float)Math.pow(-lvt_13_1_.y, 1.5D);
         }

         lvt_7_1_ = lvt_11_1_ * 0.34906584F + (1.0F - lvt_11_1_) * lvt_7_1_;
         lvt_8_1_ = lvt_11_1_ * -1.5707964F + (1.0F - lvt_11_1_) * lvt_8_1_;
      } else if (p_225597_1_.isCrouching()) {
         lvt_7_1_ = 0.6981317F;
         lvt_8_1_ = -0.7853982F;
         lvt_9_1_ = 3.0F;
         lvt_10_1_ = 0.08726646F;
      }

      this.leftWing.rotationPointX = 5.0F;
      this.leftWing.rotationPointY = lvt_9_1_;
      if (p_225597_1_ instanceof AbstractClientPlayerEntity) {
         AbstractClientPlayerEntity lvt_11_2_ = (AbstractClientPlayerEntity)p_225597_1_;
         lvt_11_2_.rotateElytraX = (float)((double)lvt_11_2_.rotateElytraX + (double)(lvt_7_1_ - lvt_11_2_.rotateElytraX) * 0.1D);
         lvt_11_2_.rotateElytraY = (float)((double)lvt_11_2_.rotateElytraY + (double)(lvt_10_1_ - lvt_11_2_.rotateElytraY) * 0.1D);
         lvt_11_2_.rotateElytraZ = (float)((double)lvt_11_2_.rotateElytraZ + (double)(lvt_8_1_ - lvt_11_2_.rotateElytraZ) * 0.1D);
         this.leftWing.rotateAngleX = lvt_11_2_.rotateElytraX;
         this.leftWing.rotateAngleY = lvt_11_2_.rotateElytraY;
         this.leftWing.rotateAngleZ = lvt_11_2_.rotateElytraZ;
      } else {
         this.leftWing.rotateAngleX = lvt_7_1_;
         this.leftWing.rotateAngleZ = lvt_8_1_;
         this.leftWing.rotateAngleY = lvt_10_1_;
      }

      this.rightWing.rotationPointX = -this.leftWing.rotationPointX;
      this.rightWing.rotateAngleY = -this.leftWing.rotateAngleY;
      this.rightWing.rotationPointY = this.leftWing.rotationPointY;
      this.rightWing.rotateAngleX = this.leftWing.rotateAngleX;
      this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;
   }
}
