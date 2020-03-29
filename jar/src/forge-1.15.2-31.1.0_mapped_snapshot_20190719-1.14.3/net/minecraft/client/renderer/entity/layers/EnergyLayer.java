package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IChargeableMob;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EnergyLayer<T extends Entity & IChargeableMob, M extends EntityModel<T>> extends LayerRenderer<T, M> {
   public EnergyLayer(IEntityRenderer<T, M> p_i226038_1_) {
      super(p_i226038_1_);
   }

   public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (((IChargeableMob)p_225628_4_).func_225509_J__()) {
         float lvt_11_1_ = (float)p_225628_4_.ticksExisted + p_225628_7_;
         EntityModel<T> lvt_12_1_ = this.func_225635_b_();
         lvt_12_1_.setLivingAnimations(p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_);
         this.getEntityModel().setModelAttributes(lvt_12_1_);
         IVertexBuilder lvt_13_1_ = p_225628_2_.getBuffer(RenderType.func_228636_a_(this.func_225633_a_(), this.func_225634_a_(lvt_11_1_), lvt_11_1_ * 0.01F));
         lvt_12_1_.func_225597_a_(p_225628_4_, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
         lvt_12_1_.func_225598_a_(p_225628_1_, lvt_13_1_, p_225628_3_, OverlayTexture.field_229196_a_, 0.5F, 0.5F, 0.5F, 1.0F);
      }
   }

   protected abstract float func_225634_a_(float var1);

   protected abstract ResourceLocation func_225633_a_();

   protected abstract EntityModel<T> func_225635_b_();
}
