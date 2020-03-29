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
public class SilverfishModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] field_78171_a = new ModelRenderer[7];
   private final ModelRenderer[] field_78169_b;
   private final ImmutableList<ModelRenderer> field_228295_f_;
   private final float[] zPlacement = new float[7];
   private static final int[][] SILVERFISH_BOX_LENGTH = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] SILVERFISH_TEXTURE_POSITIONS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public SilverfishModel() {
      float lvt_1_1_ = -3.5F;

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_78171_a.length; ++lvt_2_1_) {
         this.field_78171_a[lvt_2_1_] = new ModelRenderer(this, SILVERFISH_TEXTURE_POSITIONS[lvt_2_1_][0], SILVERFISH_TEXTURE_POSITIONS[lvt_2_1_][1]);
         this.field_78171_a[lvt_2_1_].func_228300_a_((float)SILVERFISH_BOX_LENGTH[lvt_2_1_][0] * -0.5F, 0.0F, (float)SILVERFISH_BOX_LENGTH[lvt_2_1_][2] * -0.5F, (float)SILVERFISH_BOX_LENGTH[lvt_2_1_][0], (float)SILVERFISH_BOX_LENGTH[lvt_2_1_][1], (float)SILVERFISH_BOX_LENGTH[lvt_2_1_][2]);
         this.field_78171_a[lvt_2_1_].setRotationPoint(0.0F, (float)(24 - SILVERFISH_BOX_LENGTH[lvt_2_1_][1]), lvt_1_1_);
         this.zPlacement[lvt_2_1_] = lvt_1_1_;
         if (lvt_2_1_ < this.field_78171_a.length - 1) {
            lvt_1_1_ += (float)(SILVERFISH_BOX_LENGTH[lvt_2_1_][2] + SILVERFISH_BOX_LENGTH[lvt_2_1_ + 1][2]) * 0.5F;
         }
      }

      this.field_78169_b = new ModelRenderer[3];
      this.field_78169_b[0] = new ModelRenderer(this, 20, 0);
      this.field_78169_b[0].func_228300_a_(-5.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[2][2] * -0.5F, 10.0F, 8.0F, (float)SILVERFISH_BOX_LENGTH[2][2]);
      this.field_78169_b[0].setRotationPoint(0.0F, 16.0F, this.zPlacement[2]);
      this.field_78169_b[1] = new ModelRenderer(this, 20, 11);
      this.field_78169_b[1].func_228300_a_(-3.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6.0F, 4.0F, (float)SILVERFISH_BOX_LENGTH[4][2]);
      this.field_78169_b[1].setRotationPoint(0.0F, 20.0F, this.zPlacement[4]);
      this.field_78169_b[2] = new ModelRenderer(this, 20, 18);
      this.field_78169_b[2].func_228300_a_(-3.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6.0F, 5.0F, (float)SILVERFISH_BOX_LENGTH[1][2]);
      this.field_78169_b[2].setRotationPoint(0.0F, 19.0F, this.zPlacement[1]);
      Builder<ModelRenderer> lvt_2_2_ = ImmutableList.builder();
      lvt_2_2_.addAll(Arrays.asList(this.field_78171_a));
      lvt_2_2_.addAll(Arrays.asList(this.field_78169_b));
      this.field_228295_f_ = lvt_2_2_.build();
   }

   public ImmutableList<ModelRenderer> func_225601_a_() {
      return this.field_228295_f_;
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      for(int lvt_7_1_ = 0; lvt_7_1_ < this.field_78171_a.length; ++lvt_7_1_) {
         this.field_78171_a[lvt_7_1_].rotateAngleY = MathHelper.cos(p_225597_4_ * 0.9F + (float)lvt_7_1_ * 0.15F * 3.1415927F) * 3.1415927F * 0.05F * (float)(1 + Math.abs(lvt_7_1_ - 2));
         this.field_78171_a[lvt_7_1_].rotationPointX = MathHelper.sin(p_225597_4_ * 0.9F + (float)lvt_7_1_ * 0.15F * 3.1415927F) * 3.1415927F * 0.2F * (float)Math.abs(lvt_7_1_ - 2);
      }

      this.field_78169_b[0].rotateAngleY = this.field_78171_a[2].rotateAngleY;
      this.field_78169_b[1].rotateAngleY = this.field_78171_a[4].rotateAngleY;
      this.field_78169_b[1].rotationPointX = this.field_78171_a[4].rotationPointX;
      this.field_78169_b[2].rotateAngleY = this.field_78171_a[1].rotateAngleY;
      this.field_78169_b[2].rotationPointX = this.field_78171_a[1].rotationPointX;
   }

   // $FF: synthetic method
   public Iterable func_225601_a_() {
      return this.func_225601_a_();
   }
}
