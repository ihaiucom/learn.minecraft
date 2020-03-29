package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PolarBearModel<T extends PolarBearEntity> extends QuadrupedModel<T> {
   public PolarBearModel() {
      super(12, 0.0F, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.headModel = new ModelRenderer(this, 0, 0);
      this.headModel.func_228301_a_(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F, 0.0F);
      this.headModel.setRotationPoint(0.0F, 10.0F, -16.0F);
      this.headModel.setTextureOffset(0, 44).func_228301_a_(-2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F, 0.0F);
      this.headModel.setTextureOffset(26, 0).func_228301_a_(-4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      ModelRenderer lvt_1_1_ = this.headModel.setTextureOffset(26, 0);
      lvt_1_1_.mirror = true;
      lvt_1_1_.func_228301_a_(2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      this.field_78148_b = new ModelRenderer(this);
      this.field_78148_b.setTextureOffset(0, 19).func_228301_a_(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F, 0.0F);
      this.field_78148_b.setTextureOffset(39, 0).func_228301_a_(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F, 0.0F);
      this.field_78148_b.setRotationPoint(-2.0F, 9.0F, 12.0F);
      int lvt_2_1_ = true;
      this.field_78149_c = new ModelRenderer(this, 50, 22);
      this.field_78149_c.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
      this.field_78149_c.setRotationPoint(-3.5F, 14.0F, 6.0F);
      this.field_78146_d = new ModelRenderer(this, 50, 22);
      this.field_78146_d.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
      this.field_78146_d.setRotationPoint(3.5F, 14.0F, 6.0F);
      this.field_78147_e = new ModelRenderer(this, 50, 40);
      this.field_78147_e.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
      this.field_78147_e.setRotationPoint(-2.5F, 14.0F, -7.0F);
      this.field_78144_f = new ModelRenderer(this, 50, 40);
      this.field_78144_f.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
      this.field_78144_f.setRotationPoint(2.5F, 14.0F, -7.0F);
      --this.field_78149_c.rotationPointX;
      ++this.field_78146_d.rotationPointX;
      ModelRenderer var10000 = this.field_78149_c;
      var10000.rotationPointZ += 0.0F;
      var10000 = this.field_78146_d;
      var10000.rotationPointZ += 0.0F;
      --this.field_78147_e.rotationPointX;
      ++this.field_78144_f.rotationPointX;
      --this.field_78147_e.rotationPointZ;
      --this.field_78144_f.rotationPointZ;
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.func_225597_a_(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      float lvt_7_1_ = p_225597_4_ - (float)p_225597_1_.ticksExisted;
      float lvt_8_1_ = p_225597_1_.getStandingAnimationScale(lvt_7_1_);
      lvt_8_1_ *= lvt_8_1_;
      float lvt_9_1_ = 1.0F - lvt_8_1_;
      this.field_78148_b.rotateAngleX = 1.5707964F - lvt_8_1_ * 3.1415927F * 0.35F;
      this.field_78148_b.rotationPointY = 9.0F * lvt_9_1_ + 11.0F * lvt_8_1_;
      this.field_78147_e.rotationPointY = 14.0F * lvt_9_1_ - 6.0F * lvt_8_1_;
      this.field_78147_e.rotationPointZ = -8.0F * lvt_9_1_ - 4.0F * lvt_8_1_;
      ModelRenderer var10000 = this.field_78147_e;
      var10000.rotateAngleX -= lvt_8_1_ * 3.1415927F * 0.45F;
      this.field_78144_f.rotationPointY = this.field_78147_e.rotationPointY;
      this.field_78144_f.rotationPointZ = this.field_78147_e.rotationPointZ;
      var10000 = this.field_78144_f;
      var10000.rotateAngleX -= lvt_8_1_ * 3.1415927F * 0.45F;
      if (this.isChild) {
         this.headModel.rotationPointY = 10.0F * lvt_9_1_ - 9.0F * lvt_8_1_;
         this.headModel.rotationPointZ = -16.0F * lvt_9_1_ - 7.0F * lvt_8_1_;
      } else {
         this.headModel.rotationPointY = 10.0F * lvt_9_1_ - 14.0F * lvt_8_1_;
         this.headModel.rotationPointZ = -16.0F * lvt_9_1_ - 3.0F * lvt_8_1_;
      }

      var10000 = this.headModel;
      var10000.rotateAngleX += lvt_8_1_ * 3.1415927F * 0.15F;
   }
}
