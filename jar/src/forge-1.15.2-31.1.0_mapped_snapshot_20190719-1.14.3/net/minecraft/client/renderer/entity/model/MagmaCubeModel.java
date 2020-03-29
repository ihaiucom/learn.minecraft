package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagmaCubeModel<T extends SlimeEntity> extends SegmentedModel<T> {
   private final ModelRenderer[] segments = new ModelRenderer[8];
   private final ModelRenderer core;
   private final ImmutableList<ModelRenderer> field_228271_f_;

   public MagmaCubeModel() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < this.segments.length; ++lvt_1_1_) {
         int lvt_2_1_ = 0;
         int lvt_3_1_ = lvt_1_1_;
         if (lvt_1_1_ == 2) {
            lvt_2_1_ = 24;
            lvt_3_1_ = 10;
         } else if (lvt_1_1_ == 3) {
            lvt_2_1_ = 24;
            lvt_3_1_ = 19;
         }

         this.segments[lvt_1_1_] = new ModelRenderer(this, lvt_2_1_, lvt_3_1_);
         this.segments[lvt_1_1_].func_228300_a_(-4.0F, (float)(16 + lvt_1_1_), -4.0F, 8.0F, 1.0F, 8.0F);
      }

      this.core = new ModelRenderer(this, 0, 16);
      this.core.func_228300_a_(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F);
      Builder<ModelRenderer> lvt_1_2_ = ImmutableList.builder();
      lvt_1_2_.add(this.core);
      lvt_1_2_.addAll(Arrays.asList(this.segments));
      this.field_228271_f_ = lvt_1_2_.build();
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      float lvt_5_1_ = MathHelper.lerp(p_212843_4_, p_212843_1_.prevSquishFactor, p_212843_1_.squishFactor);
      if (lvt_5_1_ < 0.0F) {
         lvt_5_1_ = 0.0F;
      }

      for(int lvt_6_1_ = 0; lvt_6_1_ < this.segments.length; ++lvt_6_1_) {
         this.segments[lvt_6_1_].rotationPointY = (float)(-(4 - lvt_6_1_)) * lvt_5_1_ * 1.7F;
      }

   }

   public ImmutableList<ModelRenderer> func_225601_a_() {
      return this.field_228271_f_;
   }

   // $FF: synthetic method
   public Iterable func_225601_a_() {
      return this.func_225601_a_();
   }
}
