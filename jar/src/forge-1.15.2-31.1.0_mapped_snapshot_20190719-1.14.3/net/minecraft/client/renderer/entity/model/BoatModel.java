package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoatModel extends SegmentedModel<BoatEntity> {
   private final ModelRenderer[] paddles = new ModelRenderer[2];
   private final ModelRenderer noWater;
   private final ImmutableList<ModelRenderer> field_228243_f_;

   public BoatModel() {
      ModelRenderer[] lvt_1_1_ = new ModelRenderer[]{(new ModelRenderer(this, 0, 0)).setTextureSize(128, 64), (new ModelRenderer(this, 0, 19)).setTextureSize(128, 64), (new ModelRenderer(this, 0, 27)).setTextureSize(128, 64), (new ModelRenderer(this, 0, 35)).setTextureSize(128, 64), (new ModelRenderer(this, 0, 43)).setTextureSize(128, 64)};
      int lvt_2_1_ = true;
      int lvt_3_1_ = true;
      int lvt_4_1_ = true;
      int lvt_5_1_ = true;
      int lvt_6_1_ = true;
      lvt_1_1_[0].func_228301_a_(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
      lvt_1_1_[0].setRotationPoint(0.0F, 3.0F, 1.0F);
      lvt_1_1_[1].func_228301_a_(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, 0.0F);
      lvt_1_1_[1].setRotationPoint(-15.0F, 4.0F, 4.0F);
      lvt_1_1_[2].func_228301_a_(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F, 0.0F);
      lvt_1_1_[2].setRotationPoint(15.0F, 4.0F, 0.0F);
      lvt_1_1_[3].func_228301_a_(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
      lvt_1_1_[3].setRotationPoint(0.0F, 4.0F, -9.0F);
      lvt_1_1_[4].func_228301_a_(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, 0.0F);
      lvt_1_1_[4].setRotationPoint(0.0F, 4.0F, 9.0F);
      lvt_1_1_[0].rotateAngleX = 1.5707964F;
      lvt_1_1_[1].rotateAngleY = 4.712389F;
      lvt_1_1_[2].rotateAngleY = 1.5707964F;
      lvt_1_1_[3].rotateAngleY = 3.1415927F;
      this.paddles[0] = this.makePaddle(true);
      this.paddles[0].setRotationPoint(3.0F, -5.0F, 9.0F);
      this.paddles[1] = this.makePaddle(false);
      this.paddles[1].setRotationPoint(3.0F, -5.0F, -9.0F);
      this.paddles[1].rotateAngleY = 3.1415927F;
      this.paddles[0].rotateAngleZ = 0.19634955F;
      this.paddles[1].rotateAngleZ = 0.19634955F;
      this.noWater = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
      this.noWater.func_228301_a_(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, 0.0F);
      this.noWater.setRotationPoint(0.0F, -3.0F, 1.0F);
      this.noWater.rotateAngleX = 1.5707964F;
      Builder<ModelRenderer> lvt_7_1_ = ImmutableList.builder();
      lvt_7_1_.addAll(Arrays.asList(lvt_1_1_));
      lvt_7_1_.addAll(Arrays.asList(this.paddles));
      this.field_228243_f_ = lvt_7_1_.build();
   }

   public void func_225597_a_(BoatEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.func_228244_a_(p_225597_1_, 0, p_225597_2_);
      this.func_228244_a_(p_225597_1_, 1, p_225597_2_);
   }

   public ImmutableList<ModelRenderer> func_225601_a_() {
      return this.field_228243_f_;
   }

   public ModelRenderer func_228245_c_() {
      return this.noWater;
   }

   protected ModelRenderer makePaddle(boolean p_187056_1_) {
      ModelRenderer lvt_2_1_ = (new ModelRenderer(this, 62, p_187056_1_ ? 0 : 20)).setTextureSize(128, 64);
      int lvt_3_1_ = true;
      int lvt_4_1_ = true;
      int lvt_5_1_ = true;
      float lvt_6_1_ = -5.0F;
      lvt_2_1_.func_228300_a_(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F);
      lvt_2_1_.func_228300_a_(p_187056_1_ ? -1.001F : 0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F);
      return lvt_2_1_;
   }

   protected void func_228244_a_(BoatEntity p_228244_1_, int p_228244_2_, float p_228244_3_) {
      float lvt_4_1_ = p_228244_1_.getRowingTime(p_228244_2_, p_228244_3_);
      ModelRenderer lvt_5_1_ = this.paddles[p_228244_2_];
      lvt_5_1_.rotateAngleX = (float)MathHelper.clampedLerp(-1.0471975803375244D, -0.2617993950843811D, (double)((MathHelper.sin(-lvt_4_1_) + 1.0F) / 2.0F));
      lvt_5_1_.rotateAngleY = (float)MathHelper.clampedLerp(-0.7853981852531433D, 0.7853981852531433D, (double)((MathHelper.sin(-lvt_4_1_ + 1.0F) + 1.0F) / 2.0F));
      if (p_228244_2_ == 1) {
         lvt_5_1_.rotateAngleY = 3.1415927F - lvt_5_1_.rotateAngleY;
      }

   }

   // $FF: synthetic method
   public Iterable func_225601_a_() {
      return this.func_225601_a_();
   }
}
