package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RavagerModel extends SegmentedModel<RavagerEntity> {
   private final ModelRenderer field_217168_a;
   private final ModelRenderer field_217169_b;
   private final ModelRenderer field_217170_f;
   private final ModelRenderer field_217171_g;
   private final ModelRenderer field_217172_h;
   private final ModelRenderer field_217173_i;
   private final ModelRenderer field_217174_j;
   private final ModelRenderer field_217175_k;

   public RavagerModel() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      int lvt_1_1_ = true;
      float lvt_2_1_ = 0.0F;
      this.field_217175_k = new ModelRenderer(this);
      this.field_217175_k.setRotationPoint(0.0F, -7.0F, -1.5F);
      this.field_217175_k.setTextureOffset(68, 73).func_228301_a_(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F, 0.0F);
      this.field_217168_a = new ModelRenderer(this);
      this.field_217168_a.setRotationPoint(0.0F, 16.0F, -17.0F);
      this.field_217168_a.setTextureOffset(0, 0).func_228301_a_(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F, 0.0F);
      this.field_217168_a.setTextureOffset(0, 0).func_228301_a_(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F, 0.0F);
      ModelRenderer lvt_3_1_ = new ModelRenderer(this);
      lvt_3_1_.setRotationPoint(-10.0F, -14.0F, -8.0F);
      lvt_3_1_.setTextureOffset(74, 55).func_228301_a_(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
      lvt_3_1_.rotateAngleX = 1.0995574F;
      this.field_217168_a.addChild(lvt_3_1_);
      ModelRenderer lvt_4_1_ = new ModelRenderer(this);
      lvt_4_1_.mirror = true;
      lvt_4_1_.setRotationPoint(8.0F, -14.0F, -8.0F);
      lvt_4_1_.setTextureOffset(74, 55).func_228301_a_(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
      lvt_4_1_.rotateAngleX = 1.0995574F;
      this.field_217168_a.addChild(lvt_4_1_);
      this.field_217169_b = new ModelRenderer(this);
      this.field_217169_b.setRotationPoint(0.0F, -2.0F, 2.0F);
      this.field_217169_b.setTextureOffset(0, 36).func_228301_a_(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F, 0.0F);
      this.field_217168_a.addChild(this.field_217169_b);
      this.field_217175_k.addChild(this.field_217168_a);
      this.field_217170_f = new ModelRenderer(this);
      this.field_217170_f.setTextureOffset(0, 55).func_228301_a_(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F, 0.0F);
      this.field_217170_f.setTextureOffset(0, 91).func_228301_a_(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F, 0.0F);
      this.field_217170_f.setRotationPoint(0.0F, 1.0F, 2.0F);
      this.field_217171_g = new ModelRenderer(this, 96, 0);
      this.field_217171_g.func_228301_a_(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.field_217171_g.setRotationPoint(-8.0F, -13.0F, 18.0F);
      this.field_217172_h = new ModelRenderer(this, 96, 0);
      this.field_217172_h.mirror = true;
      this.field_217172_h.func_228301_a_(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.field_217172_h.setRotationPoint(8.0F, -13.0F, 18.0F);
      this.field_217173_i = new ModelRenderer(this, 64, 0);
      this.field_217173_i.func_228301_a_(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.field_217173_i.setRotationPoint(-8.0F, -13.0F, -5.0F);
      this.field_217174_j = new ModelRenderer(this, 64, 0);
      this.field_217174_j.mirror = true;
      this.field_217174_j.func_228301_a_(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.field_217174_j.setRotationPoint(8.0F, -13.0F, -5.0F);
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return ImmutableList.of(this.field_217175_k, this.field_217170_f, this.field_217171_g, this.field_217172_h, this.field_217173_i, this.field_217174_j);
   }

   public void func_225597_a_(RavagerEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.field_217168_a.rotateAngleX = p_225597_6_ * 0.017453292F;
      this.field_217168_a.rotateAngleY = p_225597_5_ * 0.017453292F;
      this.field_217170_f.rotateAngleX = 1.5707964F;
      float lvt_7_1_ = 0.4F * p_225597_3_;
      this.field_217171_g.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F) * lvt_7_1_;
      this.field_217172_h.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F + 3.1415927F) * lvt_7_1_;
      this.field_217173_i.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F + 3.1415927F) * lvt_7_1_;
      this.field_217174_j.rotateAngleX = MathHelper.cos(p_225597_2_ * 0.6662F) * lvt_7_1_;
   }

   public void setLivingAnimations(RavagerEntity p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      super.setLivingAnimations(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
      int lvt_5_1_ = p_212843_1_.func_213684_dX();
      int lvt_6_1_ = p_212843_1_.func_213687_eg();
      int lvt_7_1_ = true;
      int lvt_8_1_ = p_212843_1_.func_213683_l();
      int lvt_9_1_ = true;
      float lvt_10_1_;
      float lvt_11_1_;
      float lvt_13_3_;
      if (lvt_8_1_ > 0) {
         lvt_10_1_ = this.func_217167_a((float)lvt_8_1_ - p_212843_4_, 10.0F);
         lvt_11_1_ = (1.0F + lvt_10_1_) * 0.5F;
         float lvt_12_1_ = lvt_11_1_ * lvt_11_1_ * lvt_11_1_ * 12.0F;
         lvt_13_3_ = lvt_12_1_ * MathHelper.sin(this.field_217175_k.rotateAngleX);
         this.field_217175_k.rotationPointZ = -6.5F + lvt_12_1_;
         this.field_217175_k.rotationPointY = -7.0F - lvt_13_3_;
         float lvt_14_1_ = MathHelper.sin(((float)lvt_8_1_ - p_212843_4_) / 10.0F * 3.1415927F * 0.25F);
         this.field_217169_b.rotateAngleX = 1.5707964F * lvt_14_1_;
         if (lvt_8_1_ > 5) {
            this.field_217169_b.rotateAngleX = MathHelper.sin(((float)(-4 + lvt_8_1_) - p_212843_4_) / 4.0F) * 3.1415927F * 0.4F;
         } else {
            this.field_217169_b.rotateAngleX = 0.15707964F * MathHelper.sin(3.1415927F * ((float)lvt_8_1_ - p_212843_4_) / 10.0F);
         }
      } else {
         lvt_10_1_ = -1.0F;
         lvt_11_1_ = -1.0F * MathHelper.sin(this.field_217175_k.rotateAngleX);
         this.field_217175_k.rotationPointX = 0.0F;
         this.field_217175_k.rotationPointY = -7.0F - lvt_11_1_;
         this.field_217175_k.rotationPointZ = 5.5F;
         boolean lvt_12_2_ = lvt_5_1_ > 0;
         this.field_217175_k.rotateAngleX = lvt_12_2_ ? 0.21991149F : 0.0F;
         this.field_217169_b.rotateAngleX = 3.1415927F * (lvt_12_2_ ? 0.05F : 0.01F);
         if (lvt_12_2_) {
            double lvt_13_2_ = (double)lvt_5_1_ / 40.0D;
            this.field_217175_k.rotationPointX = (float)Math.sin(lvt_13_2_ * 10.0D) * 3.0F;
         } else if (lvt_6_1_ > 0) {
            lvt_13_3_ = MathHelper.sin(((float)(20 - lvt_6_1_) - p_212843_4_) / 20.0F * 3.1415927F * 0.25F);
            this.field_217169_b.rotateAngleX = 1.5707964F * lvt_13_3_;
         }
      }

   }

   private float func_217167_a(float p_217167_1_, float p_217167_2_) {
      return (Math.abs(p_217167_1_ % p_217167_2_ - p_217167_2_ * 0.5F) - p_217167_2_ * 0.25F) / (p_217167_2_ * 0.25F);
   }
}
