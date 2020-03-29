package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieVillagerModel<T extends ZombieEntity> extends BipedModel<T> implements IHeadToggle {
   private ModelRenderer field_217150_a;

   public ZombieVillagerModel(float p_i51058_1_, boolean p_i51058_2_) {
      super(p_i51058_1_, 0.0F, 64, p_i51058_2_ ? 32 : 64);
      if (p_i51058_2_) {
         this.bipedHead = new ModelRenderer(this, 0, 0);
         this.bipedHead.func_228301_a_(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i51058_1_);
         this.bipedBody = new ModelRenderer(this, 16, 16);
         this.bipedBody.func_228301_a_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_i51058_1_ + 0.1F);
         this.bipedRightLeg = new ModelRenderer(this, 0, 16);
         this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
         this.bipedRightLeg.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_ + 0.1F);
         this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
         this.bipedLeftLeg.mirror = true;
         this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
         this.bipedLeftLeg.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_ + 0.1F);
      } else {
         this.bipedHead = new ModelRenderer(this, 0, 0);
         this.bipedHead.setTextureOffset(0, 0).func_228301_a_(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i51058_1_);
         this.bipedHead.setTextureOffset(24, 0).func_228301_a_(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, p_i51058_1_);
         this.bipedHeadwear = new ModelRenderer(this, 32, 0);
         this.bipedHeadwear.func_228301_a_(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i51058_1_ + 0.5F);
         this.field_217150_a = new ModelRenderer(this);
         this.field_217150_a.setTextureOffset(30, 47).func_228301_a_(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, p_i51058_1_);
         this.field_217150_a.rotateAngleX = -1.5707964F;
         this.bipedHeadwear.addChild(this.field_217150_a);
         this.bipedBody = new ModelRenderer(this, 16, 20);
         this.bipedBody.func_228301_a_(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, p_i51058_1_);
         this.bipedBody.setTextureOffset(0, 38).func_228301_a_(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, p_i51058_1_ + 0.05F);
         this.bipedRightArm = new ModelRenderer(this, 44, 22);
         this.bipedRightArm.func_228301_a_(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
         this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
         this.bipedLeftArm = new ModelRenderer(this, 44, 22);
         this.bipedLeftArm.mirror = true;
         this.bipedLeftArm.func_228301_a_(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
         this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
         this.bipedRightLeg = new ModelRenderer(this, 0, 22);
         this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
         this.bipedRightLeg.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
         this.bipedLeftLeg = new ModelRenderer(this, 0, 22);
         this.bipedLeftLeg.mirror = true;
         this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
         this.bipedLeftLeg.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
      }

   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.func_225597_a_((LivingEntity)p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      float lvt_7_1_ = MathHelper.sin(this.swingProgress * 3.1415927F);
      float lvt_8_1_ = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * 3.1415927F);
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightArm.rotateAngleY = -(0.1F - lvt_7_1_ * 0.6F);
      this.bipedLeftArm.rotateAngleY = 0.1F - lvt_7_1_ * 0.6F;
      float lvt_9_1_ = -3.1415927F / (p_225597_1_.isAggressive() ? 1.5F : 2.25F);
      this.bipedRightArm.rotateAngleX = lvt_9_1_;
      this.bipedLeftArm.rotateAngleX = lvt_9_1_;
      ModelRenderer var10000 = this.bipedRightArm;
      var10000.rotateAngleX += lvt_7_1_ * 1.2F - lvt_8_1_ * 0.4F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleX += lvt_7_1_ * 1.2F - lvt_8_1_ * 0.4F;
      var10000 = this.bipedRightArm;
      var10000.rotateAngleZ += MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleZ -= MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedRightArm;
      var10000.rotateAngleX += MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleX -= MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
   }

   public void func_217146_a(boolean p_217146_1_) {
      this.bipedHead.showModel = p_217146_1_;
      this.bipedHeadwear.showModel = p_217146_1_;
      this.field_217150_a.showModel = p_217146_1_;
   }
}
