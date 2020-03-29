package net.minecraft.client.renderer.entity.model;

import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] field_78154_a = new ModelRenderer[6];

   public MinecartModel() {
      this.field_78154_a[0] = new ModelRenderer(this, 0, 10);
      this.field_78154_a[1] = new ModelRenderer(this, 0, 0);
      this.field_78154_a[2] = new ModelRenderer(this, 0, 0);
      this.field_78154_a[3] = new ModelRenderer(this, 0, 0);
      this.field_78154_a[4] = new ModelRenderer(this, 0, 0);
      this.field_78154_a[5] = new ModelRenderer(this, 44, 10);
      int lvt_1_1_ = true;
      int lvt_2_1_ = true;
      int lvt_3_1_ = true;
      int lvt_4_1_ = true;
      this.field_78154_a[0].func_228301_a_(-10.0F, -8.0F, -1.0F, 20.0F, 16.0F, 2.0F, 0.0F);
      this.field_78154_a[0].setRotationPoint(0.0F, 4.0F, 0.0F);
      this.field_78154_a[5].func_228301_a_(-9.0F, -7.0F, -1.0F, 18.0F, 14.0F, 1.0F, 0.0F);
      this.field_78154_a[5].setRotationPoint(0.0F, 4.0F, 0.0F);
      this.field_78154_a[1].func_228301_a_(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.field_78154_a[1].setRotationPoint(-9.0F, 4.0F, 0.0F);
      this.field_78154_a[2].func_228301_a_(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.field_78154_a[2].setRotationPoint(9.0F, 4.0F, 0.0F);
      this.field_78154_a[3].func_228301_a_(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.field_78154_a[3].setRotationPoint(0.0F, 4.0F, -7.0F);
      this.field_78154_a[4].func_228301_a_(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.field_78154_a[4].setRotationPoint(0.0F, 4.0F, 7.0F);
      this.field_78154_a[0].rotateAngleX = 1.5707964F;
      this.field_78154_a[1].rotateAngleY = 4.712389F;
      this.field_78154_a[2].rotateAngleY = 1.5707964F;
      this.field_78154_a[3].rotateAngleY = 3.1415927F;
      this.field_78154_a[5].rotateAngleX = -1.5707964F;
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.field_78154_a[5].rotationPointY = 4.0F - p_225597_4_;
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return Arrays.asList(this.field_78154_a);
   }
}
