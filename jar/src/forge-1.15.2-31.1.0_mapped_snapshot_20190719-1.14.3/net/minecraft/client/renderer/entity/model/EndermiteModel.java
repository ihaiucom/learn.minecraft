package net.minecraft.client.renderer.entity.model;

import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermiteModel<T extends Entity> extends SegmentedModel<T> {
   private static final int[][] BODY_SIZES = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
   private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
   private static final int BODY_COUNT;
   private final ModelRenderer[] bodyParts;

   public EndermiteModel() {
      this.bodyParts = new ModelRenderer[BODY_COUNT];
      float lvt_1_1_ = -3.5F;

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.bodyParts.length; ++lvt_2_1_) {
         this.bodyParts[lvt_2_1_] = new ModelRenderer(this, BODY_TEXS[lvt_2_1_][0], BODY_TEXS[lvt_2_1_][1]);
         this.bodyParts[lvt_2_1_].func_228300_a_((float)BODY_SIZES[lvt_2_1_][0] * -0.5F, 0.0F, (float)BODY_SIZES[lvt_2_1_][2] * -0.5F, (float)BODY_SIZES[lvt_2_1_][0], (float)BODY_SIZES[lvt_2_1_][1], (float)BODY_SIZES[lvt_2_1_][2]);
         this.bodyParts[lvt_2_1_].setRotationPoint(0.0F, (float)(24 - BODY_SIZES[lvt_2_1_][1]), lvt_1_1_);
         if (lvt_2_1_ < this.bodyParts.length - 1) {
            lvt_1_1_ += (float)(BODY_SIZES[lvt_2_1_][2] + BODY_SIZES[lvt_2_1_ + 1][2]) * 0.5F;
         }
      }

   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return Arrays.asList(this.bodyParts);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      for(int lvt_7_1_ = 0; lvt_7_1_ < this.bodyParts.length; ++lvt_7_1_) {
         this.bodyParts[lvt_7_1_].rotateAngleY = MathHelper.cos(p_225597_4_ * 0.9F + (float)lvt_7_1_ * 0.15F * 3.1415927F) * 3.1415927F * 0.01F * (float)(1 + Math.abs(lvt_7_1_ - 2));
         this.bodyParts[lvt_7_1_].rotationPointX = MathHelper.sin(p_225597_4_ * 0.9F + (float)lvt_7_1_ * 0.15F * 3.1415927F) * 3.1415927F * 0.1F * (float)Math.abs(lvt_7_1_ - 2);
      }

   }

   static {
      BODY_COUNT = BODY_SIZES.length;
   }
}
