package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SalmonModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer bodyFront;
   private final ModelRenderer bodyRear;
   private final ModelRenderer head;
   private final ModelRenderer finRight;
   private final ModelRenderer finLeft;

   public SalmonModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int lvt_1_1_ = true;
      this.bodyFront = new ModelRenderer(this, 0, 0);
      this.bodyFront.func_228300_a_(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
      this.bodyFront.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.bodyRear = new ModelRenderer(this, 0, 13);
      this.bodyRear.func_228300_a_(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
      this.bodyRear.setRotationPoint(0.0F, 20.0F, 8.0F);
      this.head = new ModelRenderer(this, 22, 0);
      this.head.func_228300_a_(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F);
      this.head.setRotationPoint(0.0F, 20.0F, 0.0F);
      ModelRenderer lvt_2_1_ = new ModelRenderer(this, 20, 10);
      lvt_2_1_.func_228300_a_(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F);
      lvt_2_1_.setRotationPoint(0.0F, 0.0F, 8.0F);
      this.bodyRear.addChild(lvt_2_1_);
      ModelRenderer lvt_3_1_ = new ModelRenderer(this, 2, 1);
      lvt_3_1_.func_228300_a_(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F);
      lvt_3_1_.setRotationPoint(0.0F, -4.5F, 5.0F);
      this.bodyFront.addChild(lvt_3_1_);
      ModelRenderer lvt_4_1_ = new ModelRenderer(this, 0, 2);
      lvt_4_1_.func_228300_a_(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F);
      lvt_4_1_.setRotationPoint(0.0F, -4.5F, -1.0F);
      this.bodyRear.addChild(lvt_4_1_);
      this.finRight = new ModelRenderer(this, -4, 0);
      this.finRight.func_228300_a_(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.finRight.setRotationPoint(-1.5F, 21.5F, 0.0F);
      this.finRight.rotateAngleZ = -0.7853982F;
      this.finLeft = new ModelRenderer(this, 0, 0);
      this.finLeft.func_228300_a_(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.finLeft.setRotationPoint(1.5F, 21.5F, 0.0F);
      this.finLeft.rotateAngleZ = 0.7853982F;
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return ImmutableList.of(this.bodyFront, this.bodyRear, this.head, this.finRight, this.finLeft);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float lvt_7_1_ = 1.0F;
      float lvt_8_1_ = 1.0F;
      if (!p_225597_1_.isInWater()) {
         lvt_7_1_ = 1.3F;
         lvt_8_1_ = 1.7F;
      }

      this.bodyRear.rotateAngleY = -lvt_7_1_ * 0.25F * MathHelper.sin(lvt_8_1_ * 0.6F * p_225597_4_);
   }
}
