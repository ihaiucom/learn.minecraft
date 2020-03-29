package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SheepWoolModel<T extends SheepEntity> extends QuadrupedModel<T> {
   private float headRotationAngleX;

   public SheepWoolModel() {
      super(12, 0.0F, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
      this.headModel = new ModelRenderer(this, 0, 0);
      this.headModel.func_228301_a_(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, 0.6F);
      this.headModel.setRotationPoint(0.0F, 6.0F, -8.0F);
      this.field_78148_b = new ModelRenderer(this, 28, 8);
      this.field_78148_b.func_228301_a_(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, 1.75F);
      this.field_78148_b.setRotationPoint(0.0F, 5.0F, 2.0F);
      float lvt_1_1_ = 0.5F;
      this.field_78149_c = new ModelRenderer(this, 0, 16);
      this.field_78149_c.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
      this.field_78149_c.setRotationPoint(-3.0F, 12.0F, 7.0F);
      this.field_78146_d = new ModelRenderer(this, 0, 16);
      this.field_78146_d.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
      this.field_78146_d.setRotationPoint(3.0F, 12.0F, 7.0F);
      this.field_78147_e = new ModelRenderer(this, 0, 16);
      this.field_78147_e.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
      this.field_78147_e.setRotationPoint(-3.0F, 12.0F, -5.0F);
      this.field_78144_f = new ModelRenderer(this, 0, 16);
      this.field_78144_f.func_228301_a_(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.5F);
      this.field_78144_f.setRotationPoint(3.0F, 12.0F, -5.0F);
   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      super.setLivingAnimations(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
      this.headModel.rotationPointY = 6.0F + p_212843_1_.getHeadRotationPointY(p_212843_4_) * 9.0F;
      this.headRotationAngleX = p_212843_1_.getHeadRotationAngleX(p_212843_4_);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.func_225597_a_(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      this.headModel.rotateAngleX = this.headRotationAngleX;
   }
}
