package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpiderModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer field_78209_a;
   private final ModelRenderer field_78207_b;
   private final ModelRenderer field_78208_c;
   private final ModelRenderer field_78205_d;
   private final ModelRenderer field_78206_e;
   private final ModelRenderer field_78203_f;
   private final ModelRenderer field_78204_g;
   private final ModelRenderer field_78212_h;
   private final ModelRenderer field_78213_i;
   private final ModelRenderer field_78210_j;
   private final ModelRenderer field_78211_k;

   public SpiderModel() {
      float lvt_1_1_ = 0.0F;
      int lvt_2_1_ = true;
      this.field_78209_a = new ModelRenderer(this, 32, 4);
      this.field_78209_a.func_228301_a_(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, 0.0F);
      this.field_78209_a.setRotationPoint(0.0F, 15.0F, -3.0F);
      this.field_78207_b = new ModelRenderer(this, 0, 0);
      this.field_78207_b.func_228301_a_(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F);
      this.field_78207_b.setRotationPoint(0.0F, 15.0F, 0.0F);
      this.field_78208_c = new ModelRenderer(this, 0, 12);
      this.field_78208_c.func_228301_a_(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F, 0.0F);
      this.field_78208_c.setRotationPoint(0.0F, 15.0F, 9.0F);
      this.field_78205_d = new ModelRenderer(this, 18, 0);
      this.field_78205_d.func_228301_a_(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.field_78205_d.setRotationPoint(-4.0F, 15.0F, 2.0F);
      this.field_78206_e = new ModelRenderer(this, 18, 0);
      this.field_78206_e.func_228301_a_(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.field_78206_e.setRotationPoint(4.0F, 15.0F, 2.0F);
      this.field_78203_f = new ModelRenderer(this, 18, 0);
      this.field_78203_f.func_228301_a_(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.field_78203_f.setRotationPoint(-4.0F, 15.0F, 1.0F);
      this.field_78204_g = new ModelRenderer(this, 18, 0);
      this.field_78204_g.func_228301_a_(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.field_78204_g.setRotationPoint(4.0F, 15.0F, 1.0F);
      this.field_78212_h = new ModelRenderer(this, 18, 0);
      this.field_78212_h.func_228301_a_(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.field_78212_h.setRotationPoint(-4.0F, 15.0F, 0.0F);
      this.field_78213_i = new ModelRenderer(this, 18, 0);
      this.field_78213_i.func_228301_a_(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.field_78213_i.setRotationPoint(4.0F, 15.0F, 0.0F);
      this.field_78210_j = new ModelRenderer(this, 18, 0);
      this.field_78210_j.func_228301_a_(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.field_78210_j.setRotationPoint(-4.0F, 15.0F, -1.0F);
      this.field_78211_k = new ModelRenderer(this, 18, 0);
      this.field_78211_k.func_228301_a_(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.field_78211_k.setRotationPoint(4.0F, 15.0F, -1.0F);
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return ImmutableList.of(this.field_78209_a, this.field_78207_b, this.field_78208_c, this.field_78205_d, this.field_78206_e, this.field_78203_f, this.field_78204_g, this.field_78212_h, this.field_78213_i, this.field_78210_j, this.field_78211_k);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.field_78209_a.rotateAngleY = p_225597_5_ * 0.017453292F;
      this.field_78209_a.rotateAngleX = p_225597_6_ * 0.017453292F;
      float lvt_7_1_ = 0.7853982F;
      this.field_78205_d.rotateAngleZ = -0.7853982F;
      this.field_78206_e.rotateAngleZ = 0.7853982F;
      this.field_78203_f.rotateAngleZ = -0.58119464F;
      this.field_78204_g.rotateAngleZ = 0.58119464F;
      this.field_78212_h.rotateAngleZ = -0.58119464F;
      this.field_78213_i.rotateAngleZ = 0.58119464F;
      this.field_78210_j.rotateAngleZ = -0.7853982F;
      this.field_78211_k.rotateAngleZ = 0.7853982F;
      float lvt_8_1_ = -0.0F;
      float lvt_9_1_ = 0.3926991F;
      this.field_78205_d.rotateAngleY = 0.7853982F;
      this.field_78206_e.rotateAngleY = -0.7853982F;
      this.field_78203_f.rotateAngleY = 0.3926991F;
      this.field_78204_g.rotateAngleY = -0.3926991F;
      this.field_78212_h.rotateAngleY = -0.3926991F;
      this.field_78213_i.rotateAngleY = 0.3926991F;
      this.field_78210_j.rotateAngleY = -0.7853982F;
      this.field_78211_k.rotateAngleY = 0.7853982F;
      float lvt_10_1_ = -(MathHelper.cos(p_225597_2_ * 0.6662F * 2.0F + 0.0F) * 0.4F) * p_225597_3_;
      float lvt_11_1_ = -(MathHelper.cos(p_225597_2_ * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * p_225597_3_;
      float lvt_12_1_ = -(MathHelper.cos(p_225597_2_ * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * p_225597_3_;
      float lvt_13_1_ = -(MathHelper.cos(p_225597_2_ * 0.6662F * 2.0F + 4.712389F) * 0.4F) * p_225597_3_;
      float lvt_14_1_ = Math.abs(MathHelper.sin(p_225597_2_ * 0.6662F + 0.0F) * 0.4F) * p_225597_3_;
      float lvt_15_1_ = Math.abs(MathHelper.sin(p_225597_2_ * 0.6662F + 3.1415927F) * 0.4F) * p_225597_3_;
      float lvt_16_1_ = Math.abs(MathHelper.sin(p_225597_2_ * 0.6662F + 1.5707964F) * 0.4F) * p_225597_3_;
      float lvt_17_1_ = Math.abs(MathHelper.sin(p_225597_2_ * 0.6662F + 4.712389F) * 0.4F) * p_225597_3_;
      ModelRenderer var10000 = this.field_78205_d;
      var10000.rotateAngleY += lvt_10_1_;
      var10000 = this.field_78206_e;
      var10000.rotateAngleY += -lvt_10_1_;
      var10000 = this.field_78203_f;
      var10000.rotateAngleY += lvt_11_1_;
      var10000 = this.field_78204_g;
      var10000.rotateAngleY += -lvt_11_1_;
      var10000 = this.field_78212_h;
      var10000.rotateAngleY += lvt_12_1_;
      var10000 = this.field_78213_i;
      var10000.rotateAngleY += -lvt_12_1_;
      var10000 = this.field_78210_j;
      var10000.rotateAngleY += lvt_13_1_;
      var10000 = this.field_78211_k;
      var10000.rotateAngleY += -lvt_13_1_;
      var10000 = this.field_78205_d;
      var10000.rotateAngleZ += lvt_14_1_;
      var10000 = this.field_78206_e;
      var10000.rotateAngleZ += -lvt_14_1_;
      var10000 = this.field_78203_f;
      var10000.rotateAngleZ += lvt_15_1_;
      var10000 = this.field_78204_g;
      var10000.rotateAngleZ += -lvt_15_1_;
      var10000 = this.field_78212_h;
      var10000.rotateAngleZ += lvt_16_1_;
      var10000 = this.field_78213_i;
      var10000.rotateAngleZ += -lvt_16_1_;
      var10000 = this.field_78210_j;
      var10000.rotateAngleZ += lvt_17_1_;
      var10000 = this.field_78211_k;
      var10000.rotateAngleZ += -lvt_17_1_;
   }
}
