package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquidModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer field_78202_a;
   private final ModelRenderer[] field_78201_b = new ModelRenderer[8];
   private final ImmutableList<ModelRenderer> field_228296_f_;

   public SquidModel() {
      int lvt_1_1_ = true;
      this.field_78202_a = new ModelRenderer(this, 0, 0);
      this.field_78202_a.func_228300_a_(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F);
      ModelRenderer var10000 = this.field_78202_a;
      var10000.rotationPointY += 8.0F;

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_78201_b.length; ++lvt_2_1_) {
         this.field_78201_b[lvt_2_1_] = new ModelRenderer(this, 48, 0);
         double lvt_3_1_ = (double)lvt_2_1_ * 3.141592653589793D * 2.0D / (double)this.field_78201_b.length;
         float lvt_5_1_ = (float)Math.cos(lvt_3_1_) * 5.0F;
         float lvt_6_1_ = (float)Math.sin(lvt_3_1_) * 5.0F;
         this.field_78201_b[lvt_2_1_].func_228300_a_(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);
         this.field_78201_b[lvt_2_1_].rotationPointX = lvt_5_1_;
         this.field_78201_b[lvt_2_1_].rotationPointZ = lvt_6_1_;
         this.field_78201_b[lvt_2_1_].rotationPointY = 15.0F;
         lvt_3_1_ = (double)lvt_2_1_ * 3.141592653589793D * -2.0D / (double)this.field_78201_b.length + 1.5707963267948966D;
         this.field_78201_b[lvt_2_1_].rotateAngleY = (float)lvt_3_1_;
      }

      Builder<ModelRenderer> lvt_2_2_ = ImmutableList.builder();
      lvt_2_2_.add(this.field_78202_a);
      lvt_2_2_.addAll(Arrays.asList(this.field_78201_b));
      this.field_228296_f_ = lvt_2_2_.build();
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      ModelRenderer[] var7 = this.field_78201_b;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         ModelRenderer lvt_10_1_ = var7[var9];
         lvt_10_1_.rotateAngleX = p_225597_4_;
      }

   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return this.field_228296_f_;
   }
}
