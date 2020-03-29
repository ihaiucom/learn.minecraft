package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitherModel<T extends WitherEntity> extends SegmentedModel<T> {
   private final ModelRenderer[] upperBodyParts;
   private final ModelRenderer[] heads;
   private final ImmutableList<ModelRenderer> field_228297_f_;

   public WitherModel(float p_i46302_1_) {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.upperBodyParts = new ModelRenderer[3];
      this.upperBodyParts[0] = new ModelRenderer(this, 0, 16);
      this.upperBodyParts[0].func_228301_a_(-10.0F, 3.9F, -0.5F, 20.0F, 3.0F, 3.0F, p_i46302_1_);
      this.upperBodyParts[1] = (new ModelRenderer(this)).setTextureSize(this.textureWidth, this.textureHeight);
      this.upperBodyParts[1].setRotationPoint(-2.0F, 6.9F, -0.5F);
      this.upperBodyParts[1].setTextureOffset(0, 22).func_228301_a_(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, p_i46302_1_);
      this.upperBodyParts[1].setTextureOffset(24, 22).func_228301_a_(-4.0F, 1.5F, 0.5F, 11.0F, 2.0F, 2.0F, p_i46302_1_);
      this.upperBodyParts[1].setTextureOffset(24, 22).func_228301_a_(-4.0F, 4.0F, 0.5F, 11.0F, 2.0F, 2.0F, p_i46302_1_);
      this.upperBodyParts[1].setTextureOffset(24, 22).func_228301_a_(-4.0F, 6.5F, 0.5F, 11.0F, 2.0F, 2.0F, p_i46302_1_);
      this.upperBodyParts[2] = new ModelRenderer(this, 12, 22);
      this.upperBodyParts[2].func_228301_a_(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F, p_i46302_1_);
      this.heads = new ModelRenderer[3];
      this.heads[0] = new ModelRenderer(this, 0, 0);
      this.heads[0].func_228301_a_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i46302_1_);
      this.heads[1] = new ModelRenderer(this, 32, 0);
      this.heads[1].func_228301_a_(-4.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, p_i46302_1_);
      this.heads[1].rotationPointX = -8.0F;
      this.heads[1].rotationPointY = 4.0F;
      this.heads[2] = new ModelRenderer(this, 32, 0);
      this.heads[2].func_228301_a_(-4.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, p_i46302_1_);
      this.heads[2].rotationPointX = 10.0F;
      this.heads[2].rotationPointY = 4.0F;
      Builder<ModelRenderer> lvt_2_1_ = ImmutableList.builder();
      lvt_2_1_.addAll(Arrays.asList(this.heads));
      lvt_2_1_.addAll(Arrays.asList(this.upperBodyParts));
      this.field_228297_f_ = lvt_2_1_.build();
   }

   public ImmutableList<ModelRenderer> func_225601_a_() {
      return this.field_228297_f_;
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float lvt_7_1_ = MathHelper.cos(p_225597_4_ * 0.1F);
      this.upperBodyParts[1].rotateAngleX = (0.065F + 0.05F * lvt_7_1_) * 3.1415927F;
      this.upperBodyParts[2].setRotationPoint(-2.0F, 6.9F + MathHelper.cos(this.upperBodyParts[1].rotateAngleX) * 10.0F, -0.5F + MathHelper.sin(this.upperBodyParts[1].rotateAngleX) * 10.0F);
      this.upperBodyParts[2].rotateAngleX = (0.265F + 0.1F * lvt_7_1_) * 3.1415927F;
      this.heads[0].rotateAngleY = p_225597_5_ * 0.017453292F;
      this.heads[0].rotateAngleX = p_225597_6_ * 0.017453292F;
   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      for(int lvt_5_1_ = 1; lvt_5_1_ < 3; ++lvt_5_1_) {
         this.heads[lvt_5_1_].rotateAngleY = (p_212843_1_.getHeadYRotation(lvt_5_1_ - 1) - p_212843_1_.renderYawOffset) * 0.017453292F;
         this.heads[lvt_5_1_].rotateAngleX = p_212843_1_.getHeadXRotation(lvt_5_1_ - 1) * 0.017453292F;
      }

   }

   // $FF: synthetic method
   public Iterable func_225601_a_() {
      return this.func_225601_a_();
   }
}
