package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DolphinModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer field_205082_b;
   private final ModelRenderer field_205083_c;
   private final ModelRenderer field_205084_d;

   public DolphinModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      float lvt_1_1_ = 18.0F;
      float lvt_2_1_ = -8.0F;
      this.field_205082_b = new ModelRenderer(this, 22, 0);
      this.field_205082_b.func_228300_a_(-4.0F, -7.0F, 0.0F, 8.0F, 7.0F, 13.0F);
      this.field_205082_b.setRotationPoint(0.0F, 22.0F, -5.0F);
      ModelRenderer lvt_3_1_ = new ModelRenderer(this, 51, 0);
      lvt_3_1_.func_228300_a_(-0.5F, 0.0F, 8.0F, 1.0F, 4.0F, 5.0F);
      lvt_3_1_.rotateAngleX = 1.0471976F;
      this.field_205082_b.addChild(lvt_3_1_);
      ModelRenderer lvt_4_1_ = new ModelRenderer(this, 48, 20);
      lvt_4_1_.mirror = true;
      lvt_4_1_.func_228300_a_(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F);
      lvt_4_1_.setRotationPoint(2.0F, -2.0F, 4.0F);
      lvt_4_1_.rotateAngleX = 1.0471976F;
      lvt_4_1_.rotateAngleZ = 2.0943952F;
      this.field_205082_b.addChild(lvt_4_1_);
      ModelRenderer lvt_5_1_ = new ModelRenderer(this, 48, 20);
      lvt_5_1_.func_228300_a_(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F);
      lvt_5_1_.setRotationPoint(-2.0F, -2.0F, 4.0F);
      lvt_5_1_.rotateAngleX = 1.0471976F;
      lvt_5_1_.rotateAngleZ = -2.0943952F;
      this.field_205082_b.addChild(lvt_5_1_);
      this.field_205083_c = new ModelRenderer(this, 0, 19);
      this.field_205083_c.func_228300_a_(-2.0F, -2.5F, 0.0F, 4.0F, 5.0F, 11.0F);
      this.field_205083_c.setRotationPoint(0.0F, -2.5F, 11.0F);
      this.field_205083_c.rotateAngleX = -0.10471976F;
      this.field_205082_b.addChild(this.field_205083_c);
      this.field_205084_d = new ModelRenderer(this, 19, 20);
      this.field_205084_d.func_228300_a_(-5.0F, -0.5F, 0.0F, 10.0F, 1.0F, 6.0F);
      this.field_205084_d.setRotationPoint(0.0F, 0.0F, 9.0F);
      this.field_205084_d.rotateAngleX = 0.0F;
      this.field_205083_c.addChild(this.field_205084_d);
      ModelRenderer lvt_6_1_ = new ModelRenderer(this, 0, 0);
      lvt_6_1_.func_228300_a_(-4.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F);
      lvt_6_1_.setRotationPoint(0.0F, -4.0F, -3.0F);
      ModelRenderer lvt_7_1_ = new ModelRenderer(this, 0, 13);
      lvt_7_1_.func_228300_a_(-1.0F, 2.0F, -7.0F, 2.0F, 2.0F, 4.0F);
      lvt_6_1_.addChild(lvt_7_1_);
      this.field_205082_b.addChild(lvt_6_1_);
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return ImmutableList.of(this.field_205082_b);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.field_205082_b.rotateAngleX = p_225597_6_ * 0.017453292F;
      this.field_205082_b.rotateAngleY = p_225597_5_ * 0.017453292F;
      if (Entity.func_213296_b(p_225597_1_.getMotion()) > 1.0E-7D) {
         ModelRenderer var10000 = this.field_205082_b;
         var10000.rotateAngleX += -0.05F + -0.05F * MathHelper.cos(p_225597_4_ * 0.3F);
         this.field_205083_c.rotateAngleX = -0.1F * MathHelper.cos(p_225597_4_ * 0.3F);
         this.field_205084_d.rotateAngleX = -0.2F * MathHelper.cos(p_225597_4_ * 0.3F);
      }

   }
}
