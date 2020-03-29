package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Random;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhastModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] field_78127_b = new ModelRenderer[9];
   private final ImmutableList<ModelRenderer> field_228260_b_;

   public GhastModel() {
      Builder<ModelRenderer> lvt_1_1_ = ImmutableList.builder();
      ModelRenderer lvt_2_1_ = new ModelRenderer(this, 0, 0);
      lvt_2_1_.func_228300_a_(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
      lvt_2_1_.rotationPointY = 17.6F;
      lvt_1_1_.add(lvt_2_1_);
      Random lvt_3_1_ = new Random(1660L);

      for(int lvt_4_1_ = 0; lvt_4_1_ < this.field_78127_b.length; ++lvt_4_1_) {
         this.field_78127_b[lvt_4_1_] = new ModelRenderer(this, 0, 0);
         float lvt_5_1_ = (((float)(lvt_4_1_ % 3) - (float)(lvt_4_1_ / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float lvt_6_1_ = ((float)(lvt_4_1_ / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int lvt_7_1_ = lvt_3_1_.nextInt(7) + 8;
         this.field_78127_b[lvt_4_1_].func_228300_a_(-1.0F, 0.0F, -1.0F, 2.0F, (float)lvt_7_1_, 2.0F);
         this.field_78127_b[lvt_4_1_].rotationPointX = lvt_5_1_;
         this.field_78127_b[lvt_4_1_].rotationPointZ = lvt_6_1_;
         this.field_78127_b[lvt_4_1_].rotationPointY = 24.6F;
         lvt_1_1_.add(this.field_78127_b[lvt_4_1_]);
      }

      this.field_228260_b_ = lvt_1_1_.build();
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      for(int lvt_7_1_ = 0; lvt_7_1_ < this.field_78127_b.length; ++lvt_7_1_) {
         this.field_78127_b[lvt_7_1_].rotateAngleX = 0.2F * MathHelper.sin(p_225597_4_ * 0.3F + (float)lvt_7_1_) + 0.4F;
      }

   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return this.field_228260_b_;
   }
}
