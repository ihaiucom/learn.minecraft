package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaModel<T extends PandaEntity> extends QuadrupedModel<T> {
   private float field_217164_l;
   private float field_217165_m;
   private float field_217166_n;

   public PandaModel(int p_i51063_1_, float p_i51063_2_) {
      super(p_i51063_1_, p_i51063_2_, true, 23.0F, 4.8F, 2.7F, 3.0F, 49);
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.headModel = new ModelRenderer(this, 0, 6);
      this.headModel.func_228300_a_(-6.5F, -5.0F, -4.0F, 13.0F, 10.0F, 9.0F);
      this.headModel.setRotationPoint(0.0F, 11.5F, -17.0F);
      this.headModel.setTextureOffset(45, 16).func_228300_a_(-3.5F, 0.0F, -6.0F, 7.0F, 5.0F, 2.0F);
      this.headModel.setTextureOffset(52, 25).func_228300_a_(-8.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
      this.headModel.setTextureOffset(52, 25).func_228300_a_(3.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
      this.field_78148_b = new ModelRenderer(this, 0, 25);
      this.field_78148_b.func_228300_a_(-9.5F, -13.0F, -6.5F, 19.0F, 26.0F, 13.0F);
      this.field_78148_b.setRotationPoint(0.0F, 10.0F, 0.0F);
      int lvt_3_1_ = true;
      int lvt_4_1_ = true;
      this.field_78149_c = new ModelRenderer(this, 40, 0);
      this.field_78149_c.func_228300_a_(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.field_78149_c.setRotationPoint(-5.5F, 15.0F, 9.0F);
      this.field_78146_d = new ModelRenderer(this, 40, 0);
      this.field_78146_d.func_228300_a_(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.field_78146_d.setRotationPoint(5.5F, 15.0F, 9.0F);
      this.field_78147_e = new ModelRenderer(this, 40, 0);
      this.field_78147_e.func_228300_a_(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.field_78147_e.setRotationPoint(-5.5F, 15.0F, -9.0F);
      this.field_78144_f = new ModelRenderer(this, 40, 0);
      this.field_78144_f.func_228300_a_(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.field_78144_f.setRotationPoint(5.5F, 15.0F, -9.0F);
   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      super.setLivingAnimations(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
      this.field_217164_l = p_212843_1_.func_213561_v(p_212843_4_);
      this.field_217165_m = p_212843_1_.func_213583_w(p_212843_4_);
      this.field_217166_n = p_212843_1_.isChild() ? 0.0F : p_212843_1_.func_213591_x(p_212843_4_);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.func_225597_a_(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      boolean lvt_7_1_ = p_225597_1_.func_213544_dV() > 0;
      boolean lvt_8_1_ = p_225597_1_.func_213539_dW();
      int lvt_9_1_ = p_225597_1_.func_213585_ee();
      boolean lvt_10_1_ = p_225597_1_.func_213578_dZ();
      boolean lvt_11_1_ = p_225597_1_.func_213566_eo();
      if (lvt_7_1_) {
         this.headModel.rotateAngleY = 0.35F * MathHelper.sin(0.6F * p_225597_4_);
         this.headModel.rotateAngleZ = 0.35F * MathHelper.sin(0.6F * p_225597_4_);
         this.field_78147_e.rotateAngleX = -0.75F * MathHelper.sin(0.3F * p_225597_4_);
         this.field_78144_f.rotateAngleX = 0.75F * MathHelper.sin(0.3F * p_225597_4_);
      } else {
         this.headModel.rotateAngleZ = 0.0F;
      }

      if (lvt_8_1_) {
         if (lvt_9_1_ < 15) {
            this.headModel.rotateAngleX = -0.7853982F * (float)lvt_9_1_ / 14.0F;
         } else if (lvt_9_1_ < 20) {
            float lvt_12_1_ = (float)((lvt_9_1_ - 15) / 5);
            this.headModel.rotateAngleX = -0.7853982F + 0.7853982F * lvt_12_1_;
         }
      }

      if (this.field_217164_l > 0.0F) {
         this.field_78148_b.rotateAngleX = ModelUtils.func_228283_a_(this.field_78148_b.rotateAngleX, 1.7407963F, this.field_217164_l);
         this.headModel.rotateAngleX = ModelUtils.func_228283_a_(this.headModel.rotateAngleX, 1.5707964F, this.field_217164_l);
         this.field_78147_e.rotateAngleZ = -0.27079642F;
         this.field_78144_f.rotateAngleZ = 0.27079642F;
         this.field_78149_c.rotateAngleZ = 0.5707964F;
         this.field_78146_d.rotateAngleZ = -0.5707964F;
         if (lvt_10_1_) {
            this.headModel.rotateAngleX = 1.5707964F + 0.2F * MathHelper.sin(p_225597_4_ * 0.6F);
            this.field_78147_e.rotateAngleX = -0.4F - 0.2F * MathHelper.sin(p_225597_4_ * 0.6F);
            this.field_78144_f.rotateAngleX = -0.4F - 0.2F * MathHelper.sin(p_225597_4_ * 0.6F);
         }

         if (lvt_11_1_) {
            this.headModel.rotateAngleX = 2.1707964F;
            this.field_78147_e.rotateAngleX = -0.9F;
            this.field_78144_f.rotateAngleX = -0.9F;
         }
      } else {
         this.field_78149_c.rotateAngleZ = 0.0F;
         this.field_78146_d.rotateAngleZ = 0.0F;
         this.field_78147_e.rotateAngleZ = 0.0F;
         this.field_78144_f.rotateAngleZ = 0.0F;
      }

      if (this.field_217165_m > 0.0F) {
         this.field_78149_c.rotateAngleX = -0.6F * MathHelper.sin(p_225597_4_ * 0.15F);
         this.field_78146_d.rotateAngleX = 0.6F * MathHelper.sin(p_225597_4_ * 0.15F);
         this.field_78147_e.rotateAngleX = 0.3F * MathHelper.sin(p_225597_4_ * 0.25F);
         this.field_78144_f.rotateAngleX = -0.3F * MathHelper.sin(p_225597_4_ * 0.25F);
         this.headModel.rotateAngleX = ModelUtils.func_228283_a_(this.headModel.rotateAngleX, 1.5707964F, this.field_217165_m);
      }

      if (this.field_217166_n > 0.0F) {
         this.headModel.rotateAngleX = ModelUtils.func_228283_a_(this.headModel.rotateAngleX, 2.0561945F, this.field_217166_n);
         this.field_78149_c.rotateAngleX = -0.5F * MathHelper.sin(p_225597_4_ * 0.5F);
         this.field_78146_d.rotateAngleX = 0.5F * MathHelper.sin(p_225597_4_ * 0.5F);
         this.field_78147_e.rotateAngleX = 0.5F * MathHelper.sin(p_225597_4_ * 0.5F);
         this.field_78144_f.rotateAngleX = -0.5F * MathHelper.sin(p_225597_4_ * 0.5F);
      }

   }
}
