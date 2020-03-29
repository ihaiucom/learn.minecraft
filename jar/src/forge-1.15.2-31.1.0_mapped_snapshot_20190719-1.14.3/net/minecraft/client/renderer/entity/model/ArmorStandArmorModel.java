package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandArmorModel extends BipedModel<ArmorStandEntity> {
   public ArmorStandArmorModel(float p_i46307_1_) {
      this(p_i46307_1_, 64, 32);
   }

   protected ArmorStandArmorModel(float p_i46308_1_, int p_i46308_2_, int p_i46308_3_) {
      super(p_i46308_1_, 0.0F, p_i46308_2_, p_i46308_3_);
   }

   public void func_225597_a_(ArmorStandEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.bipedHead.rotateAngleX = 0.017453292F * p_225597_1_.getHeadRotation().getX();
      this.bipedHead.rotateAngleY = 0.017453292F * p_225597_1_.getHeadRotation().getY();
      this.bipedHead.rotateAngleZ = 0.017453292F * p_225597_1_.getHeadRotation().getZ();
      this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
      this.bipedBody.rotateAngleX = 0.017453292F * p_225597_1_.getBodyRotation().getX();
      this.bipedBody.rotateAngleY = 0.017453292F * p_225597_1_.getBodyRotation().getY();
      this.bipedBody.rotateAngleZ = 0.017453292F * p_225597_1_.getBodyRotation().getZ();
      this.bipedLeftArm.rotateAngleX = 0.017453292F * p_225597_1_.getLeftArmRotation().getX();
      this.bipedLeftArm.rotateAngleY = 0.017453292F * p_225597_1_.getLeftArmRotation().getY();
      this.bipedLeftArm.rotateAngleZ = 0.017453292F * p_225597_1_.getLeftArmRotation().getZ();
      this.bipedRightArm.rotateAngleX = 0.017453292F * p_225597_1_.getRightArmRotation().getX();
      this.bipedRightArm.rotateAngleY = 0.017453292F * p_225597_1_.getRightArmRotation().getY();
      this.bipedRightArm.rotateAngleZ = 0.017453292F * p_225597_1_.getRightArmRotation().getZ();
      this.bipedLeftLeg.rotateAngleX = 0.017453292F * p_225597_1_.getLeftLegRotation().getX();
      this.bipedLeftLeg.rotateAngleY = 0.017453292F * p_225597_1_.getLeftLegRotation().getY();
      this.bipedLeftLeg.rotateAngleZ = 0.017453292F * p_225597_1_.getLeftLegRotation().getZ();
      this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
      this.bipedRightLeg.rotateAngleX = 0.017453292F * p_225597_1_.getRightLegRotation().getX();
      this.bipedRightLeg.rotateAngleY = 0.017453292F * p_225597_1_.getRightLegRotation().getY();
      this.bipedRightLeg.rotateAngleZ = 0.017453292F * p_225597_1_.getRightLegRotation().getZ();
      this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
      this.bipedHeadwear.copyModelAngles(this.bipedHead);
   }
}
