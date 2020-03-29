package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllagerModel<T extends AbstractIllagerEntity> extends SegmentedModel<T> implements IHasArm, IHasHead {
   private final ModelRenderer head;
   private final ModelRenderer hat;
   private final ModelRenderer body;
   private final ModelRenderer arms;
   private final ModelRenderer field_217143_g;
   private final ModelRenderer field_217144_h;
   private final ModelRenderer rightArm;
   private final ModelRenderer leftArm;
   private float field_217145_m;

   public IllagerModel(float p_i47227_1_, float p_i47227_2_, int p_i47227_3_, int p_i47227_4_) {
      this.head = (new ModelRenderer(this)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.head.setRotationPoint(0.0F, 0.0F + p_i47227_2_, 0.0F);
      this.head.setTextureOffset(0, 0).func_228301_a_(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i47227_1_);
      this.hat = (new ModelRenderer(this, 32, 0)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.hat.func_228301_a_(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, p_i47227_1_ + 0.45F);
      this.head.addChild(this.hat);
      this.hat.showModel = false;
      ModelRenderer lvt_5_1_ = (new ModelRenderer(this)).setTextureSize(p_i47227_3_, p_i47227_4_);
      lvt_5_1_.setRotationPoint(0.0F, p_i47227_2_ - 2.0F, 0.0F);
      lvt_5_1_.setTextureOffset(24, 0).func_228301_a_(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, p_i47227_1_);
      this.head.addChild(lvt_5_1_);
      this.body = (new ModelRenderer(this)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.body.setRotationPoint(0.0F, 0.0F + p_i47227_2_, 0.0F);
      this.body.setTextureOffset(16, 20).func_228301_a_(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, p_i47227_1_);
      this.body.setTextureOffset(0, 38).func_228301_a_(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, p_i47227_1_ + 0.5F);
      this.arms = (new ModelRenderer(this)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.arms.setRotationPoint(0.0F, 0.0F + p_i47227_2_ + 2.0F, 0.0F);
      this.arms.setTextureOffset(44, 22).func_228301_a_(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, p_i47227_1_);
      ModelRenderer lvt_6_1_ = (new ModelRenderer(this, 44, 22)).setTextureSize(p_i47227_3_, p_i47227_4_);
      lvt_6_1_.mirror = true;
      lvt_6_1_.func_228301_a_(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, p_i47227_1_);
      this.arms.addChild(lvt_6_1_);
      this.arms.setTextureOffset(40, 38).func_228301_a_(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, p_i47227_1_);
      this.field_217143_g = (new ModelRenderer(this, 0, 22)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.field_217143_g.setRotationPoint(-2.0F, 12.0F + p_i47227_2_, 0.0F);
      this.field_217143_g.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i47227_1_);
      this.field_217144_h = (new ModelRenderer(this, 0, 22)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.field_217144_h.mirror = true;
      this.field_217144_h.setRotationPoint(2.0F, 12.0F + p_i47227_2_, 0.0F);
      this.field_217144_h.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i47227_1_);
      this.rightArm = (new ModelRenderer(this, 40, 46)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.rightArm.func_228301_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i47227_1_);
      this.rightArm.setRotationPoint(-5.0F, 2.0F + p_i47227_2_, 0.0F);
      this.leftArm = (new ModelRenderer(this, 40, 46)).setTextureSize(p_i47227_3_, p_i47227_4_);
      this.leftArm.mirror = true;
      this.leftArm.func_228301_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i47227_1_);
      this.leftArm.setRotationPoint(5.0F, 2.0F + p_i47227_2_, 0.0F);
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return ImmutableList.of(this.head, this.body, this.field_217143_g, this.field_217144_h, this.arms, this.rightArm, this.leftArm);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.head.rotateAngleY = p_225597_5_ * 0.017453292F;
      this.head.rotateAngleX = p_225597_6_ * 0.017453292F;
      this.arms.rotationPointY = 3.0F;
      this.arms.rotationPointZ = -1.0F;
      this.arms.rotateAngleX = -0.75F;
      if (this.isSitting) {
         this.rightArm.rotateAngleX = -0.62831855F;
         this.rightArm.rotateAngleY = 0.0F;
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleX = -0.62831855F;
         this.leftArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.field_217143_g.rotateAngleX = -1.4137167F;
         this.field_217143_g.rotateAngleY = 0.31415927F;
         this.field_217143_g.rotateAngleZ = 0.07853982F;
         this.field_217144_h.rotateAngleX = -1.4137167F;
         this.field_217144_h.rotateAngleY = -0.31415927F;
         this.field_217144_h.rotateAngleZ = -0.07853982F;
      } else {
         this.rightArm.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F + 3.1415927F) * 2.0F * p_225597_3_ * 0.5F;
         this.rightArm.rotateAngleY = 0.0F;
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F) * 2.0F * p_225597_3_ * 0.5F;
         this.leftArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.field_217143_g.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F) * 1.4F * p_225597_3_ * 0.5F;
         this.field_217143_g.rotateAngleY = 0.0F;
         this.field_217143_g.rotateAngleZ = 0.0F;
         this.field_217144_h.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F + 3.1415927F) * 1.4F * p_225597_3_ * 0.5F;
         this.field_217144_h.rotateAngleY = 0.0F;
         this.field_217144_h.rotateAngleZ = 0.0F;
      }

      AbstractIllagerEntity.ArmPose lvt_7_1_ = p_225597_1_.getArmPose();
      float lvt_8_2_;
      if (lvt_7_1_ == AbstractIllagerEntity.ArmPose.ATTACKING) {
         lvt_8_2_ = MathHelper.sin(this.swingProgress * 3.1415927F);
         float lvt_9_1_ = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * 3.1415927F);
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.rightArm.rotateAngleY = 0.15707964F;
         this.leftArm.rotateAngleY = -0.15707964F;
         ModelRenderer var10000;
         if (p_225597_1_.getPrimaryHand() == HandSide.RIGHT) {
            this.rightArm.rotateAngleX = -1.8849558F + MathHelper.cos(p_225597_4_ * 0.09F) * 0.15F;
            this.leftArm.rotateAngleX = -0.0F + MathHelper.cos(p_225597_4_ * 0.19F) * 0.5F;
            var10000 = this.rightArm;
            var10000.rotateAngleX += lvt_8_2_ * 2.2F - lvt_9_1_ * 0.4F;
            var10000 = this.leftArm;
            var10000.rotateAngleX += lvt_8_2_ * 1.2F - lvt_9_1_ * 0.4F;
         } else {
            this.rightArm.rotateAngleX = -0.0F + MathHelper.cos(p_225597_4_ * 0.19F) * 0.5F;
            this.leftArm.rotateAngleX = -1.8849558F + MathHelper.cos(p_225597_4_ * 0.09F) * 0.15F;
            var10000 = this.rightArm;
            var10000.rotateAngleX += lvt_8_2_ * 1.2F - lvt_9_1_ * 0.4F;
            var10000 = this.leftArm;
            var10000.rotateAngleX += lvt_8_2_ * 2.2F - lvt_9_1_ * 0.4F;
         }

         var10000 = this.rightArm;
         var10000.rotateAngleZ += MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
         var10000 = this.leftArm;
         var10000.rotateAngleZ -= MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
         var10000 = this.rightArm;
         var10000.rotateAngleX += MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
         var10000 = this.leftArm;
         var10000.rotateAngleX -= MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
      } else if (lvt_7_1_ == AbstractIllagerEntity.ArmPose.SPELLCASTING) {
         this.rightArm.rotationPointZ = 0.0F;
         this.rightArm.rotationPointX = -5.0F;
         this.leftArm.rotationPointZ = 0.0F;
         this.leftArm.rotationPointX = 5.0F;
         this.rightArm.rotateAngleX = MathHelper.cos(p_225597_4_ * 0.6662F) * 0.25F;
         this.leftArm.rotateAngleX = MathHelper.cos(p_225597_4_ * 0.6662F) * 0.25F;
         this.rightArm.rotateAngleZ = 2.3561945F;
         this.leftArm.rotateAngleZ = -2.3561945F;
         this.rightArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleY = 0.0F;
      } else if (lvt_7_1_ == AbstractIllagerEntity.ArmPose.BOW_AND_ARROW) {
         this.rightArm.rotateAngleY = -0.1F + this.head.rotateAngleY;
         this.rightArm.rotateAngleX = -1.5707964F + this.head.rotateAngleX;
         this.leftArm.rotateAngleX = -0.9424779F + this.head.rotateAngleX;
         this.leftArm.rotateAngleY = this.head.rotateAngleY - 0.4F;
         this.leftArm.rotateAngleZ = 1.5707964F;
      } else if (lvt_7_1_ == AbstractIllagerEntity.ArmPose.CROSSBOW_HOLD) {
         this.rightArm.rotateAngleY = -0.3F + this.head.rotateAngleY;
         this.leftArm.rotateAngleY = 0.6F + this.head.rotateAngleY;
         this.rightArm.rotateAngleX = -1.5707964F + this.head.rotateAngleX + 0.1F;
         this.leftArm.rotateAngleX = -1.5F + this.head.rotateAngleX;
      } else if (lvt_7_1_ == AbstractIllagerEntity.ArmPose.CROSSBOW_CHARGE) {
         this.rightArm.rotateAngleY = -0.8F;
         this.rightArm.rotateAngleX = -0.97079635F;
         this.leftArm.rotateAngleX = -0.97079635F;
         lvt_8_2_ = MathHelper.clamp(this.field_217145_m, 0.0F, 25.0F);
         this.leftArm.rotateAngleY = MathHelper.lerp(lvt_8_2_ / 25.0F, 0.4F, 0.85F);
         this.leftArm.rotateAngleX = MathHelper.lerp(lvt_8_2_ / 25.0F, this.leftArm.rotateAngleX, -1.5707964F);
      } else if (lvt_7_1_ == AbstractIllagerEntity.ArmPose.CELEBRATING) {
         this.rightArm.rotationPointZ = 0.0F;
         this.rightArm.rotationPointX = -5.0F;
         this.rightArm.rotateAngleX = MathHelper.cos(p_225597_4_ * 0.6662F) * 0.05F;
         this.rightArm.rotateAngleZ = 2.670354F;
         this.rightArm.rotateAngleY = 0.0F;
         this.leftArm.rotationPointZ = 0.0F;
         this.leftArm.rotationPointX = 5.0F;
         this.leftArm.rotateAngleX = MathHelper.cos(p_225597_4_ * 0.6662F) * 0.05F;
         this.leftArm.rotateAngleZ = -2.3561945F;
         this.leftArm.rotateAngleY = 0.0F;
      }

      boolean lvt_8_3_ = lvt_7_1_ == AbstractIllagerEntity.ArmPose.CROSSED;
      this.arms.showModel = lvt_8_3_;
      this.leftArm.showModel = !lvt_8_3_;
      this.rightArm.showModel = !lvt_8_3_;
   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      this.field_217145_m = (float)p_212843_1_.getItemInUseMaxCount();
      super.setLivingAnimations(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
   }

   private ModelRenderer getArm(HandSide p_191216_1_) {
      return p_191216_1_ == HandSide.LEFT ? this.leftArm : this.rightArm;
   }

   public ModelRenderer func_205062_a() {
      return this.hat;
   }

   public ModelRenderer func_205072_a() {
      return this.head;
   }

   public void func_225599_a_(HandSide p_225599_1_, MatrixStack p_225599_2_) {
      this.getArm(p_225599_1_).func_228307_a_(p_225599_2_);
   }
}
