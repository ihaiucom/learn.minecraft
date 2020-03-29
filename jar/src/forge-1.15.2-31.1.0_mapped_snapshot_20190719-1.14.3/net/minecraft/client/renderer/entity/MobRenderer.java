package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class MobRenderer<T extends MobEntity, M extends EntityModel<T>> extends LivingRenderer<T, M> {
   public MobRenderer(EntityRendererManager p_i50961_1_, M p_i50961_2_, float p_i50961_3_) {
      super(p_i50961_1_, p_i50961_2_, p_i50961_3_);
   }

   protected boolean canRenderName(T p_177070_1_) {
      return super.canRenderName((LivingEntity)p_177070_1_) && (p_177070_1_.getAlwaysRenderNameTagForRender() || p_177070_1_.hasCustomName() && p_177070_1_ == this.renderManager.pointedEntity);
   }

   public boolean func_225626_a_(T p_225626_1_, ClippingHelperImpl p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      if (super.func_225626_a_(p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_)) {
         return true;
      } else {
         Entity lvt_9_1_ = p_225626_1_.getLeashHolder();
         return lvt_9_1_ != null ? p_225626_2_.func_228957_a_(lvt_9_1_.getRenderBoundingBox()) : false;
      }
   }

   public void func_225623_a_(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      super.func_225623_a_((LivingEntity)p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      Entity lvt_7_1_ = p_225623_1_.getLeashHolder();
      if (lvt_7_1_ != null) {
         this.func_229118_a_(p_225623_1_, p_225623_3_, p_225623_4_, p_225623_5_, lvt_7_1_);
      }
   }

   private <E extends Entity> void func_229118_a_(T p_229118_1_, float p_229118_2_, MatrixStack p_229118_3_, IRenderTypeBuffer p_229118_4_, E p_229118_5_) {
      p_229118_3_.func_227860_a_();
      double lvt_6_1_ = (double)(MathHelper.lerp(p_229118_2_ * 0.5F, p_229118_5_.rotationYaw, p_229118_5_.prevRotationYaw) * 0.017453292F);
      double lvt_8_1_ = (double)(MathHelper.lerp(p_229118_2_ * 0.5F, p_229118_5_.rotationPitch, p_229118_5_.prevRotationPitch) * 0.017453292F);
      double lvt_10_1_ = Math.cos(lvt_6_1_);
      double lvt_12_1_ = Math.sin(lvt_6_1_);
      double lvt_14_1_ = Math.sin(lvt_8_1_);
      if (p_229118_5_ instanceof HangingEntity) {
         lvt_10_1_ = 0.0D;
         lvt_12_1_ = 0.0D;
         lvt_14_1_ = -1.0D;
      }

      double lvt_16_1_ = Math.cos(lvt_8_1_);
      double lvt_18_1_ = MathHelper.lerp((double)p_229118_2_, p_229118_5_.prevPosX, p_229118_5_.func_226277_ct_()) - lvt_10_1_ * 0.7D - lvt_12_1_ * 0.5D * lvt_16_1_;
      double lvt_20_1_ = MathHelper.lerp((double)p_229118_2_, p_229118_5_.prevPosY + (double)p_229118_5_.getEyeHeight() * 0.7D, p_229118_5_.func_226278_cu_() + (double)p_229118_5_.getEyeHeight() * 0.7D) - lvt_14_1_ * 0.5D - 0.25D;
      double lvt_22_1_ = MathHelper.lerp((double)p_229118_2_, p_229118_5_.prevPosZ, p_229118_5_.func_226281_cx_()) - lvt_12_1_ * 0.7D + lvt_10_1_ * 0.5D * lvt_16_1_;
      double lvt_24_1_ = (double)(MathHelper.lerp(p_229118_2_, p_229118_1_.renderYawOffset, p_229118_1_.prevRenderYawOffset) * 0.017453292F) + 1.5707963267948966D;
      lvt_10_1_ = Math.cos(lvt_24_1_) * (double)p_229118_1_.getWidth() * 0.4D;
      lvt_12_1_ = Math.sin(lvt_24_1_) * (double)p_229118_1_.getWidth() * 0.4D;
      double lvt_26_1_ = MathHelper.lerp((double)p_229118_2_, p_229118_1_.prevPosX, p_229118_1_.func_226277_ct_()) + lvt_10_1_;
      double lvt_28_1_ = MathHelper.lerp((double)p_229118_2_, p_229118_1_.prevPosY, p_229118_1_.func_226278_cu_());
      double lvt_30_1_ = MathHelper.lerp((double)p_229118_2_, p_229118_1_.prevPosZ, p_229118_1_.func_226281_cx_()) + lvt_12_1_;
      p_229118_3_.func_227861_a_(lvt_10_1_, -(1.6D - (double)p_229118_1_.getHeight()) * 0.5D, lvt_12_1_);
      float lvt_32_1_ = (float)(lvt_18_1_ - lvt_26_1_);
      float lvt_33_1_ = (float)(lvt_20_1_ - lvt_28_1_);
      float lvt_34_1_ = (float)(lvt_22_1_ - lvt_30_1_);
      float lvt_35_1_ = 0.025F;
      IVertexBuilder lvt_36_1_ = p_229118_4_.getBuffer(RenderType.func_228649_h_());
      Matrix4f lvt_37_1_ = p_229118_3_.func_227866_c_().func_227870_a_();
      float lvt_38_1_ = MathHelper.func_226165_i_(lvt_32_1_ * lvt_32_1_ + lvt_34_1_ * lvt_34_1_) * 0.025F / 2.0F;
      float lvt_39_1_ = lvt_34_1_ * lvt_38_1_;
      float lvt_40_1_ = lvt_32_1_ * lvt_38_1_;
      int lvt_41_1_ = this.func_225624_a_(p_229118_1_, p_229118_2_);
      int lvt_42_1_ = this.renderManager.getRenderer(p_229118_5_).func_225624_a_(p_229118_5_, p_229118_2_);
      int lvt_43_1_ = p_229118_1_.world.func_226658_a_(LightType.SKY, new BlockPos(p_229118_1_.getEyePosition(p_229118_2_)));
      int lvt_44_1_ = p_229118_1_.world.func_226658_a_(LightType.SKY, new BlockPos(p_229118_5_.getEyePosition(p_229118_2_)));
      func_229119_a_(lvt_36_1_, lvt_37_1_, lvt_32_1_, lvt_33_1_, lvt_34_1_, lvt_41_1_, lvt_42_1_, lvt_43_1_, lvt_44_1_, 0.025F, 0.025F, lvt_39_1_, lvt_40_1_);
      func_229119_a_(lvt_36_1_, lvt_37_1_, lvt_32_1_, lvt_33_1_, lvt_34_1_, lvt_41_1_, lvt_42_1_, lvt_43_1_, lvt_44_1_, 0.025F, 0.0F, lvt_39_1_, lvt_40_1_);
      p_229118_3_.func_227865_b_();
   }

   public static void func_229119_a_(IVertexBuilder p_229119_0_, Matrix4f p_229119_1_, float p_229119_2_, float p_229119_3_, float p_229119_4_, int p_229119_5_, int p_229119_6_, int p_229119_7_, int p_229119_8_, float p_229119_9_, float p_229119_10_, float p_229119_11_, float p_229119_12_) {
      int lvt_13_1_ = true;

      for(int lvt_14_1_ = 0; lvt_14_1_ < 24; ++lvt_14_1_) {
         float lvt_15_1_ = (float)lvt_14_1_ / 23.0F;
         int lvt_16_1_ = (int)MathHelper.lerp(lvt_15_1_, (float)p_229119_5_, (float)p_229119_6_);
         int lvt_17_1_ = (int)MathHelper.lerp(lvt_15_1_, (float)p_229119_7_, (float)p_229119_8_);
         int lvt_18_1_ = LightTexture.func_228451_a_(lvt_16_1_, lvt_17_1_);
         func_229120_a_(p_229119_0_, p_229119_1_, lvt_18_1_, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, lvt_14_1_, false, p_229119_11_, p_229119_12_);
         func_229120_a_(p_229119_0_, p_229119_1_, lvt_18_1_, p_229119_2_, p_229119_3_, p_229119_4_, p_229119_9_, p_229119_10_, 24, lvt_14_1_ + 1, true, p_229119_11_, p_229119_12_);
      }

   }

   public static void func_229120_a_(IVertexBuilder p_229120_0_, Matrix4f p_229120_1_, int p_229120_2_, float p_229120_3_, float p_229120_4_, float p_229120_5_, float p_229120_6_, float p_229120_7_, int p_229120_8_, int p_229120_9_, boolean p_229120_10_, float p_229120_11_, float p_229120_12_) {
      float lvt_13_1_ = 0.5F;
      float lvt_14_1_ = 0.4F;
      float lvt_15_1_ = 0.3F;
      if (p_229120_9_ % 2 == 0) {
         lvt_13_1_ *= 0.7F;
         lvt_14_1_ *= 0.7F;
         lvt_15_1_ *= 0.7F;
      }

      float lvt_16_1_ = (float)p_229120_9_ / (float)p_229120_8_;
      float lvt_17_1_ = p_229120_3_ * lvt_16_1_;
      float lvt_18_1_ = p_229120_4_ * (lvt_16_1_ * lvt_16_1_ + lvt_16_1_) * 0.5F + ((float)p_229120_8_ - (float)p_229120_9_) / ((float)p_229120_8_ * 0.75F) + 0.125F;
      float lvt_19_1_ = p_229120_5_ * lvt_16_1_;
      if (!p_229120_10_) {
         p_229120_0_.func_227888_a_(p_229120_1_, lvt_17_1_ + p_229120_11_, lvt_18_1_ + p_229120_6_ - p_229120_7_, lvt_19_1_ - p_229120_12_).func_227885_a_(lvt_13_1_, lvt_14_1_, lvt_15_1_, 1.0F).func_227886_a_(p_229120_2_).endVertex();
      }

      p_229120_0_.func_227888_a_(p_229120_1_, lvt_17_1_ - p_229120_11_, lvt_18_1_ + p_229120_7_, lvt_19_1_ + p_229120_12_).func_227885_a_(lvt_13_1_, lvt_14_1_, lvt_15_1_, 1.0F).func_227886_a_(p_229120_2_).endVertex();
      if (p_229120_10_) {
         p_229120_0_.func_227888_a_(p_229120_1_, lvt_17_1_ + p_229120_11_, lvt_18_1_ + p_229120_6_ - p_229120_7_, lvt_19_1_ - p_229120_12_).func_227885_a_(lvt_13_1_, lvt_14_1_, lvt_15_1_, 1.0F).func_227886_a_(p_229120_2_).endVertex();
      }

   }

   // $FF: synthetic method
   protected boolean canRenderName(LivingEntity p_177070_1_) {
      return this.canRenderName((MobEntity)p_177070_1_);
   }
}
