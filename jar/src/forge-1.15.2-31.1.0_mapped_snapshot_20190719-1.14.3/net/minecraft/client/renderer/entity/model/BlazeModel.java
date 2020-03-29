package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlazeModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] blazeSticks;
   private final ModelRenderer blazeHead = new ModelRenderer(this, 0, 0);
   private final ImmutableList<ModelRenderer> field_228242_f_;

   public BlazeModel() {
      this.blazeHead.func_228300_a_(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.blazeSticks = new ModelRenderer[12];

      for(int lvt_1_1_ = 0; lvt_1_1_ < this.blazeSticks.length; ++lvt_1_1_) {
         this.blazeSticks[lvt_1_1_] = new ModelRenderer(this, 0, 16);
         this.blazeSticks[lvt_1_1_].func_228300_a_(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);
      }

      Builder<ModelRenderer> lvt_1_2_ = ImmutableList.builder();
      lvt_1_2_.add(this.blazeHead);
      lvt_1_2_.addAll(Arrays.asList(this.blazeSticks));
      this.field_228242_f_ = lvt_1_2_.build();
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return this.field_228242_f_;
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float lvt_7_1_ = p_225597_4_ * 3.1415927F * -0.1F;

      int lvt_8_3_;
      for(lvt_8_3_ = 0; lvt_8_3_ < 4; ++lvt_8_3_) {
         this.blazeSticks[lvt_8_3_].rotationPointY = -2.0F + MathHelper.cos(((float)(lvt_8_3_ * 2) + p_225597_4_) * 0.25F);
         this.blazeSticks[lvt_8_3_].rotationPointX = MathHelper.cos(lvt_7_1_) * 9.0F;
         this.blazeSticks[lvt_8_3_].rotationPointZ = MathHelper.sin(lvt_7_1_) * 9.0F;
         ++lvt_7_1_;
      }

      lvt_7_1_ = 0.7853982F + p_225597_4_ * 3.1415927F * 0.03F;

      for(lvt_8_3_ = 4; lvt_8_3_ < 8; ++lvt_8_3_) {
         this.blazeSticks[lvt_8_3_].rotationPointY = 2.0F + MathHelper.cos(((float)(lvt_8_3_ * 2) + p_225597_4_) * 0.25F);
         this.blazeSticks[lvt_8_3_].rotationPointX = MathHelper.cos(lvt_7_1_) * 7.0F;
         this.blazeSticks[lvt_8_3_].rotationPointZ = MathHelper.sin(lvt_7_1_) * 7.0F;
         ++lvt_7_1_;
      }

      lvt_7_1_ = 0.47123894F + p_225597_4_ * 3.1415927F * -0.05F;

      for(lvt_8_3_ = 8; lvt_8_3_ < 12; ++lvt_8_3_) {
         this.blazeSticks[lvt_8_3_].rotationPointY = 11.0F + MathHelper.cos(((float)lvt_8_3_ * 1.5F + p_225597_4_) * 0.5F);
         this.blazeSticks[lvt_8_3_].rotationPointX = MathHelper.cos(lvt_7_1_) * 5.0F;
         this.blazeSticks[lvt_8_3_].rotationPointZ = MathHelper.sin(lvt_7_1_) * 5.0F;
         ++lvt_7_1_;
      }

      this.blazeHead.rotateAngleY = p_225597_5_ * 0.017453292F;
      this.blazeHead.rotateAngleX = p_225597_6_ * 0.017453292F;
   }
}
