package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonModel<T extends MobEntity & IRangedAttackMob> extends BipedModel<T> {
   public SkeletonModel() {
      this(0.0F, false);
   }

   public SkeletonModel(float p_i46303_1_, boolean p_i46303_2_) {
      super(p_i46303_1_);
      if (!p_i46303_2_) {
         this.bipedRightArm = new ModelRenderer(this, 40, 16);
         this.bipedRightArm.func_228301_a_(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, p_i46303_1_);
         this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
         this.bipedLeftArm = new ModelRenderer(this, 40, 16);
         this.bipedLeftArm.mirror = true;
         this.bipedLeftArm.func_228301_a_(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, p_i46303_1_);
         this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
         this.bipedRightLeg = new ModelRenderer(this, 0, 16);
         this.bipedRightLeg.func_228301_a_(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, p_i46303_1_);
         this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
         this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
         this.bipedLeftLeg.mirror = true;
         this.bipedLeftLeg.func_228301_a_(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, p_i46303_1_);
         this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
      }

   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      this.rightArmPose = BipedModel.ArmPose.EMPTY;
      this.leftArmPose = BipedModel.ArmPose.EMPTY;
      ItemStack itemstack = p_212843_1_.getHeldItem(Hand.MAIN_HAND);
      if (itemstack.getItem() instanceof BowItem && p_212843_1_.isAggressive()) {
         if (p_212843_1_.getPrimaryHand() == HandSide.RIGHT) {
            this.rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
         } else {
            this.leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
         }
      }

      super.setLivingAnimations((LivingEntity)p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.func_225597_a_((LivingEntity)p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      ItemStack itemstack = p_225597_1_.getHeldItemMainhand();
      if (p_225597_1_.isAggressive() && (itemstack.isEmpty() || !(itemstack.getItem() instanceof BowItem))) {
         float f = MathHelper.sin(this.swingProgress * 3.1415927F);
         float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * 3.1415927F);
         this.bipedRightArm.rotateAngleZ = 0.0F;
         this.bipedLeftArm.rotateAngleZ = 0.0F;
         this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
         this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
         this.bipedRightArm.rotateAngleX = -1.5707964F;
         this.bipedLeftArm.rotateAngleX = -1.5707964F;
         ModelRenderer var10000 = this.bipedRightArm;
         var10000.rotateAngleX -= f * 1.2F - f1 * 0.4F;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleX -= f * 1.2F - f1 * 0.4F;
         var10000 = this.bipedRightArm;
         var10000.rotateAngleZ += MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleZ -= MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
         var10000 = this.bipedRightArm;
         var10000.rotateAngleX += MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleX -= MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
      }

   }

   public void func_225599_a_(HandSide p_225599_1_, MatrixStack p_225599_2_) {
      float f = p_225599_1_ == HandSide.RIGHT ? 1.0F : -1.0F;
      ModelRenderer modelrenderer = this.getArmForSide(p_225599_1_);
      modelrenderer.rotationPointX += f;
      modelrenderer.func_228307_a_(p_225599_2_);
      modelrenderer.rotationPointX -= f;
   }
}
