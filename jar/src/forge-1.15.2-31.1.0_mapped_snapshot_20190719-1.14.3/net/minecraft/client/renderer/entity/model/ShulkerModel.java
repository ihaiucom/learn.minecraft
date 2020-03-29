package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerModel<T extends ShulkerEntity> extends SegmentedModel<T> {
   private final ModelRenderer base = new ModelRenderer(64, 64, 0, 28);
   private final ModelRenderer lid = new ModelRenderer(64, 64, 0, 0);
   private final ModelRenderer head = new ModelRenderer(64, 64, 0, 52);

   public ShulkerModel() {
      this.lid.func_228300_a_(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F);
      this.lid.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.base.func_228300_a_(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F);
      this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.head.func_228300_a_(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F);
      this.head.setRotationPoint(0.0F, 12.0F, 0.0F);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float lvt_7_1_ = p_225597_4_ - (float)p_225597_1_.ticksExisted;
      float lvt_8_1_ = (0.5F + p_225597_1_.getClientPeekAmount(lvt_7_1_)) * 3.1415927F;
      float lvt_9_1_ = -1.0F + MathHelper.sin(lvt_8_1_);
      float lvt_10_1_ = 0.0F;
      if (lvt_8_1_ > 3.1415927F) {
         lvt_10_1_ = MathHelper.sin(p_225597_4_ * 0.1F) * 0.7F;
      }

      this.lid.setRotationPoint(0.0F, 16.0F + MathHelper.sin(lvt_8_1_) * 8.0F + lvt_10_1_, 0.0F);
      if (p_225597_1_.getClientPeekAmount(lvt_7_1_) > 0.3F) {
         this.lid.rotateAngleY = lvt_9_1_ * lvt_9_1_ * lvt_9_1_ * lvt_9_1_ * 3.1415927F * 0.125F;
      } else {
         this.lid.rotateAngleY = 0.0F;
      }

      this.head.rotateAngleX = p_225597_6_ * 0.017453292F;
      this.head.rotateAngleY = p_225597_5_ * 0.017453292F;
   }

   public Iterable<ModelRenderer> func_225601_a_() {
      return ImmutableList.of(this.base, this.lid);
   }

   public ModelRenderer getBase() {
      return this.base;
   }

   public ModelRenderer getLid() {
      return this.lid;
   }

   public ModelRenderer getHead() {
      return this.head;
   }
}
