package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedModel<T extends LivingEntity> extends AgeableModel<T> implements IHasArm, IHasHead {
   public ModelRenderer bipedHead;
   public ModelRenderer bipedHeadwear;
   public ModelRenderer bipedBody;
   public ModelRenderer bipedRightArm;
   public ModelRenderer bipedLeftArm;
   public ModelRenderer bipedRightLeg;
   public ModelRenderer bipedLeftLeg;
   public BipedModel.ArmPose leftArmPose;
   public BipedModel.ArmPose rightArmPose;
   public boolean field_228270_o_;
   public float swimAnimation;
   private float remainingItemUseTime;

   public BipedModel(float p_i1148_1_) {
      this(RenderType::func_228640_c_, p_i1148_1_, 0.0F, 64, 32);
   }

   protected BipedModel(float p_i1149_1_, float p_i1149_2_, int p_i1149_3_, int p_i1149_4_) {
      this(RenderType::func_228640_c_, p_i1149_1_, p_i1149_2_, p_i1149_3_, p_i1149_4_);
   }

   public BipedModel(Function<ResourceLocation, RenderType> p_i225946_1_, float p_i225946_2_, float p_i225946_3_, int p_i225946_4_, int p_i225946_5_) {
      super(p_i225946_1_, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
      this.leftArmPose = BipedModel.ArmPose.EMPTY;
      this.rightArmPose = BipedModel.ArmPose.EMPTY;
      this.textureWidth = p_i225946_4_;
      this.textureHeight = p_i225946_5_;
      this.bipedHead = new ModelRenderer(this, 0, 0);
      this.bipedHead.func_228301_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i225946_2_);
      this.bipedHead.setRotationPoint(0.0F, 0.0F + p_i225946_3_, 0.0F);
      this.bipedHeadwear = new ModelRenderer(this, 32, 0);
      this.bipedHeadwear.func_228301_a_(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i225946_2_ + 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + p_i225946_3_, 0.0F);
      this.bipedBody = new ModelRenderer(this, 16, 16);
      this.bipedBody.func_228301_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_i225946_2_);
      this.bipedBody.setRotationPoint(0.0F, 0.0F + p_i225946_3_, 0.0F);
      this.bipedRightArm = new ModelRenderer(this, 40, 16);
      this.bipedRightArm.func_228301_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i225946_2_);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i225946_3_, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 40, 16);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.func_228301_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i225946_2_);
      this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + p_i225946_3_, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 0, 16);
      this.bipedRightLeg.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i225946_2_);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i225946_3_, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i225946_2_);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + p_i225946_3_, 0.0F);
   }

   protected Iterable<ModelRenderer> func_225602_a_() {
      return ImmutableList.of(this.bipedHead);
   }

   protected Iterable<ModelRenderer> func_225600_b_() {
      return ImmutableList.of(this.bipedBody, this.bipedRightArm, this.bipedLeftArm, this.bipedRightLeg, this.bipedLeftLeg, this.bipedHeadwear);
   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      this.swimAnimation = p_212843_1_.getSwimAnimation(p_212843_4_);
      this.remainingItemUseTime = (float)p_212843_1_.getItemInUseMaxCount();
      super.setLivingAnimations(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      boolean lvt_7_1_ = p_225597_1_.getTicksElytraFlying() > 4;
      boolean lvt_8_1_ = p_225597_1_.func_213314_bj();
      this.bipedHead.rotateAngleY = p_225597_5_ * 0.017453292F;
      if (lvt_7_1_) {
         this.bipedHead.rotateAngleX = -0.7853982F;
      } else if (this.swimAnimation > 0.0F) {
         if (lvt_8_1_) {
            this.bipedHead.rotateAngleX = this.func_205060_a(this.bipedHead.rotateAngleX, -0.7853982F, this.swimAnimation);
         } else {
            this.bipedHead.rotateAngleX = this.func_205060_a(this.bipedHead.rotateAngleX, p_225597_6_ * 0.017453292F, this.swimAnimation);
         }
      } else {
         this.bipedHead.rotateAngleX = p_225597_6_ * 0.017453292F;
      }

      this.bipedBody.rotateAngleY = 0.0F;
      this.bipedRightArm.rotationPointZ = 0.0F;
      this.bipedRightArm.rotationPointX = -5.0F;
      this.bipedLeftArm.rotationPointZ = 0.0F;
      this.bipedLeftArm.rotationPointX = 5.0F;
      float lvt_9_1_ = 1.0F;
      if (lvt_7_1_) {
         lvt_9_1_ = (float)p_225597_1_.getMotion().lengthSquared();
         lvt_9_1_ /= 0.2F;
         lvt_9_1_ *= lvt_9_1_ * lvt_9_1_;
      }

      if (lvt_9_1_ < 1.0F) {
         lvt_9_1_ = 1.0F;
      }

      this.bipedRightArm.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F + 3.1415927F) * 2.0F * p_225597_3_ * 0.5F / lvt_9_1_;
      this.bipedLeftArm.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F) * 2.0F * p_225597_3_ * 0.5F / lvt_9_1_;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightLeg.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F) * 1.4F * p_225597_3_ / lvt_9_1_;
      this.bipedLeftLeg.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F + 3.1415927F) * 1.4F * p_225597_3_ / lvt_9_1_;
      this.bipedRightLeg.rotateAngleY = 0.0F;
      this.bipedLeftLeg.rotateAngleY = 0.0F;
      this.bipedRightLeg.rotateAngleZ = 0.0F;
      this.bipedLeftLeg.rotateAngleZ = 0.0F;
      ModelRenderer var10000;
      if (this.isSitting) {
         var10000 = this.bipedRightArm;
         var10000.rotateAngleX += -0.62831855F;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleX += -0.62831855F;
         this.bipedRightLeg.rotateAngleX = -1.4137167F;
         this.bipedRightLeg.rotateAngleY = 0.31415927F;
         this.bipedRightLeg.rotateAngleZ = 0.07853982F;
         this.bipedLeftLeg.rotateAngleX = -1.4137167F;
         this.bipedLeftLeg.rotateAngleY = -0.31415927F;
         this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
      }

      this.bipedRightArm.rotateAngleY = 0.0F;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      switch(this.leftArmPose) {
      case EMPTY:
         this.bipedLeftArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedLeftArm.rotateAngleY = 0.5235988F;
         break;
      case ITEM:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.31415927F;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      switch(this.rightArmPose) {
      case EMPTY:
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedRightArm.rotateAngleY = -0.5235988F;
         break;
      case ITEM:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.31415927F;
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case THROW_SPEAR:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 3.1415927F;
         this.bipedRightArm.rotateAngleY = 0.0F;
      }

      if (this.leftArmPose == BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BOW_AND_ARROW) {
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 3.1415927F;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      float lvt_12_2_;
      float lvt_13_3_;
      float lvt_14_2_;
      if (this.swingProgress > 0.0F) {
         HandSide lvt_10_1_ = this.func_217147_a(p_225597_1_);
         ModelRenderer lvt_11_1_ = this.getArmForSide(lvt_10_1_);
         lvt_12_2_ = this.swingProgress;
         this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(lvt_12_2_) * 6.2831855F) * 0.2F;
         if (lvt_10_1_ == HandSide.LEFT) {
            var10000 = this.bipedBody;
            var10000.rotateAngleY *= -1.0F;
         }

         this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         var10000 = this.bipedRightArm;
         var10000.rotateAngleY += this.bipedBody.rotateAngleY;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleY += this.bipedBody.rotateAngleY;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleX += this.bipedBody.rotateAngleY;
         lvt_12_2_ = 1.0F - this.swingProgress;
         lvt_12_2_ *= lvt_12_2_;
         lvt_12_2_ *= lvt_12_2_;
         lvt_12_2_ = 1.0F - lvt_12_2_;
         lvt_13_3_ = MathHelper.sin(lvt_12_2_ * 3.1415927F);
         lvt_14_2_ = MathHelper.sin(this.swingProgress * 3.1415927F) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
         lvt_11_1_.rotateAngleX = (float)((double)lvt_11_1_.rotateAngleX - ((double)lvt_13_3_ * 1.2D + (double)lvt_14_2_));
         lvt_11_1_.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
         lvt_11_1_.rotateAngleZ += MathHelper.sin(this.swingProgress * 3.1415927F) * -0.4F;
      }

      if (this.field_228270_o_) {
         this.bipedBody.rotateAngleX = 0.5F;
         var10000 = this.bipedRightArm;
         var10000.rotateAngleX += 0.4F;
         var10000 = this.bipedLeftArm;
         var10000.rotateAngleX += 0.4F;
         this.bipedRightLeg.rotationPointZ = 4.0F;
         this.bipedLeftLeg.rotationPointZ = 4.0F;
         this.bipedRightLeg.rotationPointY = 12.2F;
         this.bipedLeftLeg.rotationPointY = 12.2F;
         this.bipedHead.rotationPointY = 4.2F;
         this.bipedBody.rotationPointY = 3.2F;
         this.bipedLeftArm.rotationPointY = 5.2F;
         this.bipedRightArm.rotationPointY = 5.2F;
      } else {
         this.bipedBody.rotateAngleX = 0.0F;
         this.bipedRightLeg.rotationPointZ = 0.1F;
         this.bipedLeftLeg.rotationPointZ = 0.1F;
         this.bipedRightLeg.rotationPointY = 12.0F;
         this.bipedLeftLeg.rotationPointY = 12.0F;
         this.bipedHead.rotationPointY = 0.0F;
         this.bipedBody.rotationPointY = 0.0F;
         this.bipedLeftArm.rotationPointY = 2.0F;
         this.bipedRightArm.rotationPointY = 2.0F;
      }

      var10000 = this.bipedRightArm;
      var10000.rotateAngleZ += MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleZ -= MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedRightArm;
      var10000.rotateAngleX += MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleX -= MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
      if (this.rightArmPose == BipedModel.ArmPose.BOW_AND_ARROW) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
         this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
      } else if (this.leftArmPose == BipedModel.ArmPose.BOW_AND_ARROW && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY - 0.4F;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
         this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX;
      }

      float lvt_10_2_ = (float)CrossbowItem.getChargeTime(p_225597_1_.getActiveItemStack());
      float lvt_11_4_;
      if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
         this.bipedRightArm.rotateAngleY = -0.8F;
         this.bipedRightArm.rotateAngleX = -0.97079635F;
         this.bipedLeftArm.rotateAngleX = -0.97079635F;
         lvt_11_4_ = MathHelper.clamp(this.remainingItemUseTime, 0.0F, lvt_10_2_);
         this.bipedLeftArm.rotateAngleY = MathHelper.lerp(lvt_11_4_ / lvt_10_2_, 0.4F, 0.85F);
         this.bipedLeftArm.rotateAngleX = MathHelper.lerp(lvt_11_4_ / lvt_10_2_, this.bipedLeftArm.rotateAngleX, -1.5707964F);
      } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
         this.bipedLeftArm.rotateAngleY = 0.8F;
         this.bipedRightArm.rotateAngleX = -0.97079635F;
         this.bipedLeftArm.rotateAngleX = -0.97079635F;
         lvt_11_4_ = MathHelper.clamp(this.remainingItemUseTime, 0.0F, lvt_10_2_);
         this.bipedRightArm.rotateAngleY = MathHelper.lerp(lvt_11_4_ / lvt_10_2_, -0.4F, -0.85F);
         this.bipedRightArm.rotateAngleX = MathHelper.lerp(lvt_11_4_ / lvt_10_2_, this.bipedRightArm.rotateAngleX, -1.5707964F);
      }

      if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_HOLD && this.swingProgress <= 0.0F) {
         this.bipedRightArm.rotateAngleY = -0.3F + this.bipedHead.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.6F + this.bipedHead.rotateAngleY;
         this.bipedRightArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX + 0.1F;
         this.bipedLeftArm.rotateAngleX = -1.5F + this.bipedHead.rotateAngleX;
      } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_HOLD) {
         this.bipedRightArm.rotateAngleY = -0.6F + this.bipedHead.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.3F + this.bipedHead.rotateAngleY;
         this.bipedRightArm.rotateAngleX = -1.5F + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = -1.5707964F + this.bipedHead.rotateAngleX + 0.1F;
      }

      if (this.swimAnimation > 0.0F) {
         lvt_11_4_ = p_225597_2_ % 26.0F;
         lvt_12_2_ = this.swingProgress > 0.0F ? 0.0F : this.swimAnimation;
         if (lvt_11_4_ < 14.0F) {
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, 0.0F, this.swimAnimation);
            this.bipedRightArm.rotateAngleX = MathHelper.lerp(lvt_12_2_, this.bipedRightArm.rotateAngleX, 0.0F);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, 3.1415927F, this.swimAnimation);
            this.bipedRightArm.rotateAngleY = MathHelper.lerp(lvt_12_2_, this.bipedRightArm.rotateAngleY, 3.1415927F);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 3.1415927F + 1.8707964F * this.func_203068_a(lvt_11_4_) / this.func_203068_a(14.0F), this.swimAnimation);
            this.bipedRightArm.rotateAngleZ = MathHelper.lerp(lvt_12_2_, this.bipedRightArm.rotateAngleZ, 3.1415927F - 1.8707964F * this.func_203068_a(lvt_11_4_) / this.func_203068_a(14.0F));
         } else if (lvt_11_4_ >= 14.0F && lvt_11_4_ < 22.0F) {
            lvt_13_3_ = (lvt_11_4_ - 14.0F) / 8.0F;
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, 1.5707964F * lvt_13_3_, this.swimAnimation);
            this.bipedRightArm.rotateAngleX = MathHelper.lerp(lvt_12_2_, this.bipedRightArm.rotateAngleX, 1.5707964F * lvt_13_3_);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, 3.1415927F, this.swimAnimation);
            this.bipedRightArm.rotateAngleY = MathHelper.lerp(lvt_12_2_, this.bipedRightArm.rotateAngleY, 3.1415927F);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 5.012389F - 1.8707964F * lvt_13_3_, this.swimAnimation);
            this.bipedRightArm.rotateAngleZ = MathHelper.lerp(lvt_12_2_, this.bipedRightArm.rotateAngleZ, 1.2707963F + 1.8707964F * lvt_13_3_);
         } else if (lvt_11_4_ >= 22.0F && lvt_11_4_ < 26.0F) {
            lvt_13_3_ = (lvt_11_4_ - 22.0F) / 4.0F;
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, 1.5707964F - 1.5707964F * lvt_13_3_, this.swimAnimation);
            this.bipedRightArm.rotateAngleX = MathHelper.lerp(lvt_12_2_, this.bipedRightArm.rotateAngleX, 1.5707964F - 1.5707964F * lvt_13_3_);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, 3.1415927F, this.swimAnimation);
            this.bipedRightArm.rotateAngleY = MathHelper.lerp(lvt_12_2_, this.bipedRightArm.rotateAngleY, 3.1415927F);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 3.1415927F, this.swimAnimation);
            this.bipedRightArm.rotateAngleZ = MathHelper.lerp(lvt_12_2_, this.bipedRightArm.rotateAngleZ, 3.1415927F);
         }

         lvt_13_3_ = 0.3F;
         lvt_14_2_ = 0.33333334F;
         this.bipedLeftLeg.rotateAngleX = MathHelper.lerp(this.swimAnimation, this.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos(p_225597_2_ * 0.33333334F + 3.1415927F));
         this.bipedRightLeg.rotateAngleX = MathHelper.lerp(this.swimAnimation, this.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(p_225597_2_ * 0.33333334F));
      }

      this.bipedHeadwear.copyModelAngles(this.bipedHead);
   }

   protected float func_205060_a(float p_205060_1_, float p_205060_2_, float p_205060_3_) {
      float lvt_4_1_ = (p_205060_2_ - p_205060_1_) % 6.2831855F;
      if (lvt_4_1_ < -3.1415927F) {
         lvt_4_1_ += 6.2831855F;
      }

      if (lvt_4_1_ >= 3.1415927F) {
         lvt_4_1_ -= 6.2831855F;
      }

      return p_205060_1_ + p_205060_3_ * lvt_4_1_;
   }

   private float func_203068_a(float p_203068_1_) {
      return -65.0F * p_203068_1_ + p_203068_1_ * p_203068_1_;
   }

   public void func_217148_a(BipedModel<T> p_217148_1_) {
      super.setModelAttributes(p_217148_1_);
      p_217148_1_.leftArmPose = this.leftArmPose;
      p_217148_1_.rightArmPose = this.rightArmPose;
      p_217148_1_.field_228270_o_ = this.field_228270_o_;
   }

   public void setVisible(boolean p_178719_1_) {
      this.bipedHead.showModel = p_178719_1_;
      this.bipedHeadwear.showModel = p_178719_1_;
      this.bipedBody.showModel = p_178719_1_;
      this.bipedRightArm.showModel = p_178719_1_;
      this.bipedLeftArm.showModel = p_178719_1_;
      this.bipedRightLeg.showModel = p_178719_1_;
      this.bipedLeftLeg.showModel = p_178719_1_;
   }

   public void func_225599_a_(HandSide p_225599_1_, MatrixStack p_225599_2_) {
      this.getArmForSide(p_225599_1_).func_228307_a_(p_225599_2_);
   }

   protected ModelRenderer getArmForSide(HandSide p_187074_1_) {
      return p_187074_1_ == HandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
   }

   public ModelRenderer func_205072_a() {
      return this.bipedHead;
   }

   protected HandSide func_217147_a(T p_217147_1_) {
      HandSide lvt_2_1_ = p_217147_1_.getPrimaryHand();
      return p_217147_1_.swingingHand == Hand.MAIN_HAND ? lvt_2_1_ : lvt_2_1_.opposite();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ArmPose {
      EMPTY,
      ITEM,
      BLOCK,
      BOW_AND_ARROW,
      THROW_SPEAR,
      CROSSBOW_CHARGE,
      CROSSBOW_HOLD;
   }
}
