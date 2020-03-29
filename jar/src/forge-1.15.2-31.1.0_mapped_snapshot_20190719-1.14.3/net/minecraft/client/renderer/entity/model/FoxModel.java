package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxModel<T extends FoxEntity> extends AgeableModel<T> {
   public final ModelRenderer field_217115_a;
   private final ModelRenderer field_217116_b;
   private final ModelRenderer field_217117_f;
   private final ModelRenderer field_217118_g;
   private final ModelRenderer field_217119_h;
   private final ModelRenderer field_217120_i;
   private final ModelRenderer field_217121_j;
   private final ModelRenderer field_217122_k;
   private final ModelRenderer field_217123_l;
   private final ModelRenderer field_217124_m;
   private float field_217125_n;

   public FoxModel() {
      super(true, 8.0F, 3.35F);
      this.textureWidth = 48;
      this.textureHeight = 32;
      this.field_217115_a = new ModelRenderer(this, 1, 5);
      this.field_217115_a.func_228300_a_(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F);
      this.field_217115_a.setRotationPoint(-1.0F, 16.5F, -3.0F);
      this.field_217116_b = new ModelRenderer(this, 8, 1);
      this.field_217116_b.func_228300_a_(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F);
      this.field_217117_f = new ModelRenderer(this, 15, 1);
      this.field_217117_f.func_228300_a_(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F);
      this.field_217118_g = new ModelRenderer(this, 6, 18);
      this.field_217118_g.func_228300_a_(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F);
      this.field_217115_a.addChild(this.field_217116_b);
      this.field_217115_a.addChild(this.field_217117_f);
      this.field_217115_a.addChild(this.field_217118_g);
      this.field_217119_h = new ModelRenderer(this, 24, 15);
      this.field_217119_h.func_228300_a_(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F);
      this.field_217119_h.setRotationPoint(0.0F, 16.0F, -6.0F);
      float lvt_1_1_ = 0.001F;
      this.field_217120_i = new ModelRenderer(this, 13, 24);
      this.field_217120_i.func_228301_a_(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
      this.field_217120_i.setRotationPoint(-5.0F, 17.5F, 7.0F);
      this.field_217121_j = new ModelRenderer(this, 4, 24);
      this.field_217121_j.func_228301_a_(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
      this.field_217121_j.setRotationPoint(-1.0F, 17.5F, 7.0F);
      this.field_217122_k = new ModelRenderer(this, 13, 24);
      this.field_217122_k.func_228301_a_(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
      this.field_217122_k.setRotationPoint(-5.0F, 17.5F, 0.0F);
      this.field_217123_l = new ModelRenderer(this, 4, 24);
      this.field_217123_l.func_228301_a_(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
      this.field_217123_l.setRotationPoint(-1.0F, 17.5F, 0.0F);
      this.field_217124_m = new ModelRenderer(this, 30, 0);
      this.field_217124_m.func_228300_a_(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F);
      this.field_217124_m.setRotationPoint(-4.0F, 15.0F, -1.0F);
      this.field_217119_h.addChild(this.field_217124_m);
   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      this.field_217119_h.rotateAngleX = 1.5707964F;
      this.field_217124_m.rotateAngleX = -0.05235988F;
      this.field_217120_i.rotateAngleX = MathHelper.cos(p_212843_2_ * 0.6662F) * 1.4F * p_212843_3_;
      this.field_217121_j.rotateAngleX = MathHelper.cos(p_212843_2_ * 0.6662F + 3.1415927F) * 1.4F * p_212843_3_;
      this.field_217122_k.rotateAngleX = MathHelper.cos(p_212843_2_ * 0.6662F + 3.1415927F) * 1.4F * p_212843_3_;
      this.field_217123_l.rotateAngleX = MathHelper.cos(p_212843_2_ * 0.6662F) * 1.4F * p_212843_3_;
      this.field_217115_a.setRotationPoint(-1.0F, 16.5F, -3.0F);
      this.field_217115_a.rotateAngleY = 0.0F;
      this.field_217115_a.rotateAngleZ = p_212843_1_.func_213475_v(p_212843_4_);
      this.field_217120_i.showModel = true;
      this.field_217121_j.showModel = true;
      this.field_217122_k.showModel = true;
      this.field_217123_l.showModel = true;
      this.field_217119_h.setRotationPoint(0.0F, 16.0F, -6.0F);
      this.field_217119_h.rotateAngleZ = 0.0F;
      this.field_217120_i.setRotationPoint(-5.0F, 17.5F, 7.0F);
      this.field_217121_j.setRotationPoint(-1.0F, 17.5F, 7.0F);
      if (p_212843_1_.isCrouching()) {
         this.field_217119_h.rotateAngleX = 1.6755161F;
         float lvt_5_1_ = p_212843_1_.func_213503_w(p_212843_4_);
         this.field_217119_h.setRotationPoint(0.0F, 16.0F + p_212843_1_.func_213503_w(p_212843_4_), -6.0F);
         this.field_217115_a.setRotationPoint(-1.0F, 16.5F + lvt_5_1_, -3.0F);
         this.field_217115_a.rotateAngleY = 0.0F;
      } else if (p_212843_1_.isSleeping()) {
         this.field_217119_h.rotateAngleZ = -1.5707964F;
         this.field_217119_h.setRotationPoint(0.0F, 21.0F, -6.0F);
         this.field_217124_m.rotateAngleX = -2.6179938F;
         if (this.isChild) {
            this.field_217124_m.rotateAngleX = -2.1816616F;
            this.field_217119_h.setRotationPoint(0.0F, 21.0F, -2.0F);
         }

         this.field_217115_a.setRotationPoint(1.0F, 19.49F, -3.0F);
         this.field_217115_a.rotateAngleX = 0.0F;
         this.field_217115_a.rotateAngleY = -2.0943952F;
         this.field_217115_a.rotateAngleZ = 0.0F;
         this.field_217120_i.showModel = false;
         this.field_217121_j.showModel = false;
         this.field_217122_k.showModel = false;
         this.field_217123_l.showModel = false;
      } else if (p_212843_1_.isSitting()) {
         this.field_217119_h.rotateAngleX = 0.5235988F;
         this.field_217119_h.setRotationPoint(0.0F, 9.0F, -3.0F);
         this.field_217124_m.rotateAngleX = 0.7853982F;
         this.field_217124_m.setRotationPoint(-4.0F, 15.0F, -2.0F);
         this.field_217115_a.setRotationPoint(-1.0F, 10.0F, -0.25F);
         this.field_217115_a.rotateAngleX = 0.0F;
         this.field_217115_a.rotateAngleY = 0.0F;
         if (this.isChild) {
            this.field_217115_a.setRotationPoint(-1.0F, 13.0F, -3.75F);
         }

         this.field_217120_i.rotateAngleX = -1.3089969F;
         this.field_217120_i.setRotationPoint(-5.0F, 21.5F, 6.75F);
         this.field_217121_j.rotateAngleX = -1.3089969F;
         this.field_217121_j.setRotationPoint(-1.0F, 21.5F, 6.75F);
         this.field_217122_k.rotateAngleX = -0.2617994F;
         this.field_217123_l.rotateAngleX = -0.2617994F;
      }

   }

   protected Iterable<ModelRenderer> func_225602_a_() {
      return ImmutableList.of(this.field_217115_a);
   }

   protected Iterable<ModelRenderer> func_225600_b_() {
      return ImmutableList.of(this.field_217119_h, this.field_217120_i, this.field_217121_j, this.field_217122_k, this.field_217123_l);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      if (!p_225597_1_.isSleeping() && !p_225597_1_.func_213472_dX() && !p_225597_1_.isCrouching()) {
         this.field_217115_a.rotateAngleX = p_225597_6_ * 0.017453292F;
         this.field_217115_a.rotateAngleY = p_225597_5_ * 0.017453292F;
      }

      if (p_225597_1_.isSleeping()) {
         this.field_217115_a.rotateAngleX = 0.0F;
         this.field_217115_a.rotateAngleY = -2.0943952F;
         this.field_217115_a.rotateAngleZ = MathHelper.cos(p_225597_4_ * 0.027F) / 22.0F;
      }

      float lvt_7_2_;
      if (p_225597_1_.isCrouching()) {
         lvt_7_2_ = MathHelper.cos(p_225597_4_) * 0.01F;
         this.field_217119_h.rotateAngleY = lvt_7_2_;
         this.field_217120_i.rotateAngleZ = lvt_7_2_;
         this.field_217121_j.rotateAngleZ = lvt_7_2_;
         this.field_217122_k.rotateAngleZ = lvt_7_2_ / 2.0F;
         this.field_217123_l.rotateAngleZ = lvt_7_2_ / 2.0F;
      }

      if (p_225597_1_.func_213472_dX()) {
         lvt_7_2_ = 0.1F;
         this.field_217125_n += 0.67F;
         this.field_217120_i.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F) * 0.1F;
         this.field_217121_j.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F + 3.1415927F) * 0.1F;
         this.field_217122_k.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F + 3.1415927F) * 0.1F;
         this.field_217123_l.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F) * 0.1F;
      }

   }
}
