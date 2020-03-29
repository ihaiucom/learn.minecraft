package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ArrowRenderer<T extends AbstractArrowEntity> extends EntityRenderer<T> {
   public ArrowRenderer(EntityRendererManager p_i46193_1_) {
      super(p_i46193_1_);
   }

   public void func_225623_a_(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.func_227860_a_();
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(MathHelper.lerp(p_225623_3_, p_225623_1_.prevRotationYaw, p_225623_1_.rotationYaw) - 90.0F));
      p_225623_4_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(MathHelper.lerp(p_225623_3_, p_225623_1_.prevRotationPitch, p_225623_1_.rotationPitch)));
      int lvt_7_1_ = false;
      float lvt_8_1_ = 0.0F;
      float lvt_9_1_ = 0.5F;
      float lvt_10_1_ = 0.0F;
      float lvt_11_1_ = 0.15625F;
      float lvt_12_1_ = 0.0F;
      float lvt_13_1_ = 0.15625F;
      float lvt_14_1_ = 0.15625F;
      float lvt_15_1_ = 0.3125F;
      float lvt_16_1_ = 0.05625F;
      float lvt_17_1_ = (float)p_225623_1_.arrowShake - p_225623_3_;
      if (lvt_17_1_ > 0.0F) {
         float lvt_18_1_ = -MathHelper.sin(lvt_17_1_ * 3.0F) * lvt_17_1_;
         p_225623_4_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(lvt_18_1_));
      }

      p_225623_4_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(45.0F));
      p_225623_4_.func_227862_a_(0.05625F, 0.05625F, 0.05625F);
      p_225623_4_.func_227861_a_(-4.0D, 0.0D, 0.0D);
      IVertexBuilder lvt_18_2_ = p_225623_5_.getBuffer(RenderType.func_228638_b_(this.getEntityTexture(p_225623_1_)));
      MatrixStack.Entry lvt_19_1_ = p_225623_4_.func_227866_c_();
      Matrix4f lvt_20_1_ = lvt_19_1_.func_227870_a_();
      Matrix3f lvt_21_1_ = lvt_19_1_.func_227872_b_();
      this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, p_225623_6_);
      this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, p_225623_6_);
      this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, p_225623_6_);
      this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, p_225623_6_);
      this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, p_225623_6_);
      this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, p_225623_6_);
      this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, p_225623_6_);
      this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, p_225623_6_);

      for(int lvt_22_1_ = 0; lvt_22_1_ < 4; ++lvt_22_1_) {
         p_225623_4_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(90.0F));
         this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, p_225623_6_);
         this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, p_225623_6_);
         this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, p_225623_6_);
         this.func_229039_a_(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, p_225623_6_);
      }

      p_225623_4_.func_227865_b_();
      super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public void func_229039_a_(Matrix4f p_229039_1_, Matrix3f p_229039_2_, IVertexBuilder p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
      p_229039_3_.func_227888_a_(p_229039_1_, (float)p_229039_4_, (float)p_229039_5_, (float)p_229039_6_).func_225586_a_(255, 255, 255, 255).func_225583_a_(p_229039_7_, p_229039_8_).func_227891_b_(OverlayTexture.field_229196_a_).func_227886_a_(p_229039_12_).func_227887_a_(p_229039_2_, (float)p_229039_9_, (float)p_229039_11_, (float)p_229039_10_).endVertex();
   }
}
