package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseModel<T extends AbstractHorseEntity> extends AgeableModel<T> {
   protected final ModelRenderer field_217127_a;
   protected final ModelRenderer field_217128_b;
   private final ModelRenderer field_228262_f_;
   private final ModelRenderer field_228263_g_;
   private final ModelRenderer field_228264_h_;
   private final ModelRenderer field_228265_i_;
   private final ModelRenderer field_228266_j_;
   private final ModelRenderer field_228267_k_;
   private final ModelRenderer field_228268_l_;
   private final ModelRenderer field_228269_m_;
   private final ModelRenderer field_217133_j;
   private final ModelRenderer[] field_217134_k;
   private final ModelRenderer[] field_217135_l;

   public HorseModel(float p_i51065_1_) {
      super(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.field_217127_a = new ModelRenderer(this, 0, 32);
      this.field_217127_a.func_228301_a_(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, 0.05F);
      this.field_217127_a.setRotationPoint(0.0F, 11.0F, 5.0F);
      this.field_217128_b = new ModelRenderer(this, 0, 35);
      this.field_217128_b.func_228300_a_(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F);
      this.field_217128_b.rotateAngleX = 0.5235988F;
      ModelRenderer lvt_2_1_ = new ModelRenderer(this, 0, 13);
      lvt_2_1_.func_228301_a_(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, p_i51065_1_);
      ModelRenderer lvt_3_1_ = new ModelRenderer(this, 56, 36);
      lvt_3_1_.func_228301_a_(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, p_i51065_1_);
      ModelRenderer lvt_4_1_ = new ModelRenderer(this, 0, 25);
      lvt_4_1_.func_228301_a_(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, p_i51065_1_);
      this.field_217128_b.addChild(lvt_2_1_);
      this.field_217128_b.addChild(lvt_3_1_);
      this.field_217128_b.addChild(lvt_4_1_);
      this.func_199047_a(this.field_217128_b);
      this.field_228262_f_ = new ModelRenderer(this, 48, 21);
      this.field_228262_f_.mirror = true;
      this.field_228262_f_.func_228301_a_(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, p_i51065_1_);
      this.field_228262_f_.setRotationPoint(4.0F, 14.0F, 7.0F);
      this.field_228263_g_ = new ModelRenderer(this, 48, 21);
      this.field_228263_g_.func_228301_a_(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, p_i51065_1_);
      this.field_228263_g_.setRotationPoint(-4.0F, 14.0F, 7.0F);
      this.field_228264_h_ = new ModelRenderer(this, 48, 21);
      this.field_228264_h_.mirror = true;
      this.field_228264_h_.func_228301_a_(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, p_i51065_1_);
      this.field_228264_h_.setRotationPoint(4.0F, 6.0F, -12.0F);
      this.field_228265_i_ = new ModelRenderer(this, 48, 21);
      this.field_228265_i_.func_228301_a_(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, p_i51065_1_);
      this.field_228265_i_.setRotationPoint(-4.0F, 6.0F, -12.0F);
      float lvt_5_1_ = 5.5F;
      this.field_228266_j_ = new ModelRenderer(this, 48, 21);
      this.field_228266_j_.mirror = true;
      this.field_228266_j_.func_228302_a_(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, p_i51065_1_, p_i51065_1_ + 5.5F, p_i51065_1_);
      this.field_228266_j_.setRotationPoint(4.0F, 14.0F, 7.0F);
      this.field_228267_k_ = new ModelRenderer(this, 48, 21);
      this.field_228267_k_.func_228302_a_(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, p_i51065_1_, p_i51065_1_ + 5.5F, p_i51065_1_);
      this.field_228267_k_.setRotationPoint(-4.0F, 14.0F, 7.0F);
      this.field_228268_l_ = new ModelRenderer(this, 48, 21);
      this.field_228268_l_.mirror = true;
      this.field_228268_l_.func_228302_a_(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, p_i51065_1_, p_i51065_1_ + 5.5F, p_i51065_1_);
      this.field_228268_l_.setRotationPoint(4.0F, 6.0F, -12.0F);
      this.field_228269_m_ = new ModelRenderer(this, 48, 21);
      this.field_228269_m_.func_228302_a_(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, p_i51065_1_, p_i51065_1_ + 5.5F, p_i51065_1_);
      this.field_228269_m_.setRotationPoint(-4.0F, 6.0F, -12.0F);
      this.field_217133_j = new ModelRenderer(this, 42, 36);
      this.field_217133_j.func_228301_a_(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, p_i51065_1_);
      this.field_217133_j.setRotationPoint(0.0F, -5.0F, 2.0F);
      this.field_217133_j.rotateAngleX = 0.5235988F;
      this.field_217127_a.addChild(this.field_217133_j);
      ModelRenderer lvt_6_1_ = new ModelRenderer(this, 26, 0);
      lvt_6_1_.func_228301_a_(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, 0.5F);
      this.field_217127_a.addChild(lvt_6_1_);
      ModelRenderer lvt_7_1_ = new ModelRenderer(this, 29, 5);
      lvt_7_1_.func_228301_a_(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, p_i51065_1_);
      this.field_217128_b.addChild(lvt_7_1_);
      ModelRenderer lvt_8_1_ = new ModelRenderer(this, 29, 5);
      lvt_8_1_.func_228301_a_(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, p_i51065_1_);
      this.field_217128_b.addChild(lvt_8_1_);
      ModelRenderer lvt_9_1_ = new ModelRenderer(this, 32, 2);
      lvt_9_1_.func_228301_a_(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, p_i51065_1_);
      lvt_9_1_.rotateAngleX = -0.5235988F;
      this.field_217128_b.addChild(lvt_9_1_);
      ModelRenderer lvt_10_1_ = new ModelRenderer(this, 32, 2);
      lvt_10_1_.func_228301_a_(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, p_i51065_1_);
      lvt_10_1_.rotateAngleX = -0.5235988F;
      this.field_217128_b.addChild(lvt_10_1_);
      ModelRenderer lvt_11_1_ = new ModelRenderer(this, 1, 1);
      lvt_11_1_.func_228301_a_(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, 0.2F);
      this.field_217128_b.addChild(lvt_11_1_);
      ModelRenderer lvt_12_1_ = new ModelRenderer(this, 19, 0);
      lvt_12_1_.func_228301_a_(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, 0.2F);
      this.field_217128_b.addChild(lvt_12_1_);
      this.field_217134_k = new ModelRenderer[]{lvt_6_1_, lvt_7_1_, lvt_8_1_, lvt_11_1_, lvt_12_1_};
      this.field_217135_l = new ModelRenderer[]{lvt_9_1_, lvt_10_1_};
   }

   protected void func_199047_a(ModelRenderer p_199047_1_) {
      ModelRenderer lvt_2_1_ = new ModelRenderer(this, 19, 16);
      lvt_2_1_.func_228301_a_(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
      ModelRenderer lvt_3_1_ = new ModelRenderer(this, 19, 16);
      lvt_3_1_.func_228301_a_(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
      p_199047_1_.addChild(lvt_2_1_);
      p_199047_1_.addChild(lvt_3_1_);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      boolean lvt_7_1_ = p_225597_1_.isHorseSaddled();
      boolean lvt_8_1_ = p_225597_1_.isBeingRidden();
      ModelRenderer[] var9 = this.field_217134_k;
      int var10 = var9.length;

      int var11;
      ModelRenderer lvt_12_2_;
      for(var11 = 0; var11 < var10; ++var11) {
         lvt_12_2_ = var9[var11];
         lvt_12_2_.showModel = lvt_7_1_;
      }

      var9 = this.field_217135_l;
      var10 = var9.length;

      for(var11 = 0; var11 < var10; ++var11) {
         lvt_12_2_ = var9[var11];
         lvt_12_2_.showModel = lvt_8_1_ && lvt_7_1_;
      }

      this.field_217127_a.rotationPointY = 11.0F;
   }

   public Iterable<ModelRenderer> func_225602_a_() {
      return ImmutableList.of(this.field_217128_b);
   }

   protected Iterable<ModelRenderer> func_225600_b_() {
      return ImmutableList.of(this.field_217127_a, this.field_228262_f_, this.field_228263_g_, this.field_228264_h_, this.field_228265_i_, this.field_228266_j_, this.field_228267_k_, this.field_228268_l_, this.field_228269_m_);
   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      super.setLivingAnimations(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
      float lvt_5_1_ = MathHelper.func_226167_j_(p_212843_1_.prevRenderYawOffset, p_212843_1_.renderYawOffset, p_212843_4_);
      float lvt_6_1_ = MathHelper.func_226167_j_(p_212843_1_.prevRotationYawHead, p_212843_1_.rotationYawHead, p_212843_4_);
      float lvt_7_1_ = MathHelper.lerp(p_212843_4_, p_212843_1_.prevRotationPitch, p_212843_1_.rotationPitch);
      float lvt_8_1_ = lvt_6_1_ - lvt_5_1_;
      float lvt_9_1_ = lvt_7_1_ * 0.017453292F;
      if (lvt_8_1_ > 20.0F) {
         lvt_8_1_ = 20.0F;
      }

      if (lvt_8_1_ < -20.0F) {
         lvt_8_1_ = -20.0F;
      }

      if (p_212843_3_ > 0.2F) {
         lvt_9_1_ += MathHelper.cos(p_212843_2_ * 0.4F) * 0.15F * p_212843_3_;
      }

      float lvt_10_1_ = p_212843_1_.getGrassEatingAmount(p_212843_4_);
      float lvt_11_1_ = p_212843_1_.getRearingAmount(p_212843_4_);
      float lvt_12_1_ = 1.0F - lvt_11_1_;
      float lvt_13_1_ = p_212843_1_.getMouthOpennessAngle(p_212843_4_);
      boolean lvt_14_1_ = p_212843_1_.tailCounter != 0;
      float lvt_15_1_ = (float)p_212843_1_.ticksExisted + p_212843_4_;
      this.field_217128_b.rotationPointY = 4.0F;
      this.field_217128_b.rotationPointZ = -12.0F;
      this.field_217127_a.rotateAngleX = 0.0F;
      this.field_217128_b.rotateAngleX = 0.5235988F + lvt_9_1_;
      this.field_217128_b.rotateAngleY = lvt_8_1_ * 0.017453292F;
      float lvt_16_1_ = p_212843_1_.isInWater() ? 0.2F : 1.0F;
      float lvt_17_1_ = MathHelper.cos(lvt_16_1_ * p_212843_2_ * 0.6662F + 3.1415927F);
      float lvt_18_1_ = lvt_17_1_ * 0.8F * p_212843_3_;
      float lvt_19_1_ = (1.0F - Math.max(lvt_11_1_, lvt_10_1_)) * (0.5235988F + lvt_9_1_ + lvt_13_1_ * MathHelper.sin(lvt_15_1_) * 0.05F);
      this.field_217128_b.rotateAngleX = lvt_11_1_ * (0.2617994F + lvt_9_1_) + lvt_10_1_ * (2.1816616F + MathHelper.sin(lvt_15_1_) * 0.05F) + lvt_19_1_;
      this.field_217128_b.rotateAngleY = lvt_11_1_ * lvt_8_1_ * 0.017453292F + (1.0F - Math.max(lvt_11_1_, lvt_10_1_)) * this.field_217128_b.rotateAngleY;
      this.field_217128_b.rotationPointY = lvt_11_1_ * -4.0F + lvt_10_1_ * 11.0F + (1.0F - Math.max(lvt_11_1_, lvt_10_1_)) * this.field_217128_b.rotationPointY;
      this.field_217128_b.rotationPointZ = lvt_11_1_ * -4.0F + lvt_10_1_ * -12.0F + (1.0F - Math.max(lvt_11_1_, lvt_10_1_)) * this.field_217128_b.rotationPointZ;
      this.field_217127_a.rotateAngleX = lvt_11_1_ * -0.7853982F + lvt_12_1_ * this.field_217127_a.rotateAngleX;
      float lvt_20_1_ = 0.2617994F * lvt_11_1_;
      float lvt_21_1_ = MathHelper.cos(lvt_15_1_ * 0.6F + 3.1415927F);
      this.field_228264_h_.rotationPointY = 2.0F * lvt_11_1_ + 14.0F * lvt_12_1_;
      this.field_228264_h_.rotationPointZ = -6.0F * lvt_11_1_ - 10.0F * lvt_12_1_;
      this.field_228265_i_.rotationPointY = this.field_228264_h_.rotationPointY;
      this.field_228265_i_.rotationPointZ = this.field_228264_h_.rotationPointZ;
      float lvt_22_1_ = (-1.0471976F + lvt_21_1_) * lvt_11_1_ + lvt_18_1_ * lvt_12_1_;
      float lvt_23_1_ = (-1.0471976F - lvt_21_1_) * lvt_11_1_ - lvt_18_1_ * lvt_12_1_;
      this.field_228262_f_.rotateAngleX = lvt_20_1_ - lvt_17_1_ * 0.5F * p_212843_3_ * lvt_12_1_;
      this.field_228263_g_.rotateAngleX = lvt_20_1_ + lvt_17_1_ * 0.5F * p_212843_3_ * lvt_12_1_;
      this.field_228264_h_.rotateAngleX = lvt_22_1_;
      this.field_228265_i_.rotateAngleX = lvt_23_1_;
      this.field_217133_j.rotateAngleX = 0.5235988F + p_212843_3_ * 0.75F;
      this.field_217133_j.rotationPointY = -5.0F + p_212843_3_;
      this.field_217133_j.rotationPointZ = 2.0F + p_212843_3_ * 2.0F;
      if (lvt_14_1_) {
         this.field_217133_j.rotateAngleY = MathHelper.cos(lvt_15_1_ * 0.7F);
      } else {
         this.field_217133_j.rotateAngleY = 0.0F;
      }

      this.field_228266_j_.rotationPointY = this.field_228262_f_.rotationPointY;
      this.field_228266_j_.rotationPointZ = this.field_228262_f_.rotationPointZ;
      this.field_228266_j_.rotateAngleX = this.field_228262_f_.rotateAngleX;
      this.field_228267_k_.rotationPointY = this.field_228263_g_.rotationPointY;
      this.field_228267_k_.rotationPointZ = this.field_228263_g_.rotationPointZ;
      this.field_228267_k_.rotateAngleX = this.field_228263_g_.rotateAngleX;
      this.field_228268_l_.rotationPointY = this.field_228264_h_.rotationPointY;
      this.field_228268_l_.rotationPointZ = this.field_228264_h_.rotationPointZ;
      this.field_228268_l_.rotateAngleX = this.field_228264_h_.rotateAngleX;
      this.field_228269_m_.rotationPointY = this.field_228265_i_.rotationPointY;
      this.field_228269_m_.rotationPointZ = this.field_228265_i_.rotationPointZ;
      this.field_228269_m_.rotateAngleX = this.field_228265_i_.rotateAngleX;
      boolean lvt_24_1_ = p_212843_1_.isChild();
      this.field_228262_f_.showModel = !lvt_24_1_;
      this.field_228263_g_.showModel = !lvt_24_1_;
      this.field_228264_h_.showModel = !lvt_24_1_;
      this.field_228265_i_.showModel = !lvt_24_1_;
      this.field_228266_j_.showModel = lvt_24_1_;
      this.field_228267_k_.showModel = lvt_24_1_;
      this.field_228268_l_.showModel = lvt_24_1_;
      this.field_228269_m_.showModel = lvt_24_1_;
      this.field_217127_a.rotationPointY = lvt_24_1_ ? 10.8F : 0.0F;
   }
}
