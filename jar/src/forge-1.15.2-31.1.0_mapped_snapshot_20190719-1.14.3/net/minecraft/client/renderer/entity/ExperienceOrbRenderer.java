package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrbEntity> {
   private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
   private static final RenderType field_229101_e_;

   public ExperienceOrbRenderer(EntityRendererManager p_i46178_1_) {
      super(p_i46178_1_);
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   protected int func_225624_a_(ExperienceOrbEntity p_225624_1_, float p_225624_2_) {
      return MathHelper.clamp(super.func_225624_a_(p_225624_1_, p_225624_2_) + 7, 0, 15);
   }

   public void func_225623_a_(ExperienceOrbEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.func_227860_a_();
      int lvt_7_1_ = p_225623_1_.getTextureByXP();
      float lvt_8_1_ = (float)(lvt_7_1_ % 4 * 16 + 0) / 64.0F;
      float lvt_9_1_ = (float)(lvt_7_1_ % 4 * 16 + 16) / 64.0F;
      float lvt_10_1_ = (float)(lvt_7_1_ / 4 * 16 + 0) / 64.0F;
      float lvt_11_1_ = (float)(lvt_7_1_ / 4 * 16 + 16) / 64.0F;
      float lvt_12_1_ = 1.0F;
      float lvt_13_1_ = 0.5F;
      float lvt_14_1_ = 0.25F;
      float lvt_15_1_ = 255.0F;
      float lvt_16_1_ = ((float)p_225623_1_.xpColor + p_225623_3_) / 2.0F;
      int lvt_17_1_ = (int)((MathHelper.sin(lvt_16_1_ + 0.0F) + 1.0F) * 0.5F * 255.0F);
      int lvt_18_1_ = true;
      int lvt_19_1_ = (int)((MathHelper.sin(lvt_16_1_ + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
      p_225623_4_.func_227861_a_(0.0D, 0.10000000149011612D, 0.0D);
      p_225623_4_.func_227863_a_(this.renderManager.func_229098_b_());
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F));
      float lvt_20_1_ = 0.3F;
      p_225623_4_.func_227862_a_(0.3F, 0.3F, 0.3F);
      IVertexBuilder lvt_21_1_ = p_225623_5_.getBuffer(field_229101_e_);
      MatrixStack.Entry lvt_22_1_ = p_225623_4_.func_227866_c_();
      Matrix4f lvt_23_1_ = lvt_22_1_.func_227870_a_();
      Matrix3f lvt_24_1_ = lvt_22_1_.func_227872_b_();
      func_229102_a_(lvt_21_1_, lvt_23_1_, lvt_24_1_, -0.5F, -0.25F, lvt_17_1_, 255, lvt_19_1_, lvt_8_1_, lvt_11_1_, p_225623_6_);
      func_229102_a_(lvt_21_1_, lvt_23_1_, lvt_24_1_, 0.5F, -0.25F, lvt_17_1_, 255, lvt_19_1_, lvt_9_1_, lvt_11_1_, p_225623_6_);
      func_229102_a_(lvt_21_1_, lvt_23_1_, lvt_24_1_, 0.5F, 0.75F, lvt_17_1_, 255, lvt_19_1_, lvt_9_1_, lvt_10_1_, p_225623_6_);
      func_229102_a_(lvt_21_1_, lvt_23_1_, lvt_24_1_, -0.5F, 0.75F, lvt_17_1_, 255, lvt_19_1_, lvt_8_1_, lvt_10_1_, p_225623_6_);
      p_225623_4_.func_227865_b_();
      super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   private static void func_229102_a_(IVertexBuilder p_229102_0_, Matrix4f p_229102_1_, Matrix3f p_229102_2_, float p_229102_3_, float p_229102_4_, int p_229102_5_, int p_229102_6_, int p_229102_7_, float p_229102_8_, float p_229102_9_, int p_229102_10_) {
      p_229102_0_.func_227888_a_(p_229102_1_, p_229102_3_, p_229102_4_, 0.0F).func_225586_a_(p_229102_5_, p_229102_6_, p_229102_7_, 128).func_225583_a_(p_229102_8_, p_229102_9_).func_227891_b_(OverlayTexture.field_229196_a_).func_227886_a_(p_229102_10_).func_227887_a_(p_229102_2_, 0.0F, 1.0F, 0.0F).endVertex();
   }

   public ResourceLocation getEntityTexture(ExperienceOrbEntity p_110775_1_) {
      return EXPERIENCE_ORB_TEXTURES;
   }

   static {
      field_229101_e_ = RenderType.func_228644_e_(EXPERIENCE_ORB_TEXTURES);
   }
}
