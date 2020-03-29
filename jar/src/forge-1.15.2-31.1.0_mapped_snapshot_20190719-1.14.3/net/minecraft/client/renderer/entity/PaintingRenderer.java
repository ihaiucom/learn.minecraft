package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.PaintingSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaintingRenderer extends EntityRenderer<PaintingEntity> {
   public PaintingRenderer(EntityRendererManager p_i46150_1_) {
      super(p_i46150_1_);
   }

   public void func_225623_a_(PaintingEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.func_227860_a_();
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F - p_225623_2_));
      PaintingType lvt_7_1_ = p_225623_1_.art;
      float lvt_8_1_ = 0.0625F;
      p_225623_4_.func_227862_a_(0.0625F, 0.0625F, 0.0625F);
      IVertexBuilder lvt_9_1_ = p_225623_5_.getBuffer(RenderType.func_228634_a_(this.getEntityTexture(p_225623_1_)));
      PaintingSpriteUploader lvt_10_1_ = Minecraft.getInstance().getPaintingSpriteUploader();
      this.func_229122_a_(p_225623_4_, lvt_9_1_, p_225623_1_, lvt_7_1_.getWidth(), lvt_7_1_.getHeight(), lvt_10_1_.getSpriteForPainting(lvt_7_1_), lvt_10_1_.func_215286_b());
      p_225623_4_.func_227865_b_();
      super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public ResourceLocation getEntityTexture(PaintingEntity p_110775_1_) {
      return Minecraft.getInstance().getPaintingSpriteUploader().func_215286_b().func_229241_m_().func_229223_g_();
   }

   private void func_229122_a_(MatrixStack p_229122_1_, IVertexBuilder p_229122_2_, PaintingEntity p_229122_3_, int p_229122_4_, int p_229122_5_, TextureAtlasSprite p_229122_6_, TextureAtlasSprite p_229122_7_) {
      MatrixStack.Entry lvt_8_1_ = p_229122_1_.func_227866_c_();
      Matrix4f lvt_9_1_ = lvt_8_1_.func_227870_a_();
      Matrix3f lvt_10_1_ = lvt_8_1_.func_227872_b_();
      float lvt_11_1_ = (float)(-p_229122_4_) / 2.0F;
      float lvt_12_1_ = (float)(-p_229122_5_) / 2.0F;
      float lvt_13_1_ = 0.5F;
      float lvt_14_1_ = p_229122_7_.getMinU();
      float lvt_15_1_ = p_229122_7_.getMaxU();
      float lvt_16_1_ = p_229122_7_.getMinV();
      float lvt_17_1_ = p_229122_7_.getMaxV();
      float lvt_18_1_ = p_229122_7_.getMinU();
      float lvt_19_1_ = p_229122_7_.getMaxU();
      float lvt_20_1_ = p_229122_7_.getMinV();
      float lvt_21_1_ = p_229122_7_.getInterpolatedV(1.0D);
      float lvt_22_1_ = p_229122_7_.getMinU();
      float lvt_23_1_ = p_229122_7_.getInterpolatedU(1.0D);
      float lvt_24_1_ = p_229122_7_.getMinV();
      float lvt_25_1_ = p_229122_7_.getMaxV();
      int lvt_26_1_ = p_229122_4_ / 16;
      int lvt_27_1_ = p_229122_5_ / 16;
      double lvt_28_1_ = 16.0D / (double)lvt_26_1_;
      double lvt_30_1_ = 16.0D / (double)lvt_27_1_;

      for(int lvt_32_1_ = 0; lvt_32_1_ < lvt_26_1_; ++lvt_32_1_) {
         for(int lvt_33_1_ = 0; lvt_33_1_ < lvt_27_1_; ++lvt_33_1_) {
            float lvt_34_1_ = lvt_11_1_ + (float)((lvt_32_1_ + 1) * 16);
            float lvt_35_1_ = lvt_11_1_ + (float)(lvt_32_1_ * 16);
            float lvt_36_1_ = lvt_12_1_ + (float)((lvt_33_1_ + 1) * 16);
            float lvt_37_1_ = lvt_12_1_ + (float)(lvt_33_1_ * 16);
            int lvt_38_1_ = MathHelper.floor(p_229122_3_.func_226277_ct_());
            int lvt_39_1_ = MathHelper.floor(p_229122_3_.func_226278_cu_() + (double)((lvt_36_1_ + lvt_37_1_) / 2.0F / 16.0F));
            int lvt_40_1_ = MathHelper.floor(p_229122_3_.func_226281_cx_());
            Direction lvt_41_1_ = p_229122_3_.getHorizontalFacing();
            if (lvt_41_1_ == Direction.NORTH) {
               lvt_38_1_ = MathHelper.floor(p_229122_3_.func_226277_ct_() + (double)((lvt_34_1_ + lvt_35_1_) / 2.0F / 16.0F));
            }

            if (lvt_41_1_ == Direction.WEST) {
               lvt_40_1_ = MathHelper.floor(p_229122_3_.func_226281_cx_() - (double)((lvt_34_1_ + lvt_35_1_) / 2.0F / 16.0F));
            }

            if (lvt_41_1_ == Direction.SOUTH) {
               lvt_38_1_ = MathHelper.floor(p_229122_3_.func_226277_ct_() - (double)((lvt_34_1_ + lvt_35_1_) / 2.0F / 16.0F));
            }

            if (lvt_41_1_ == Direction.EAST) {
               lvt_40_1_ = MathHelper.floor(p_229122_3_.func_226281_cx_() + (double)((lvt_34_1_ + lvt_35_1_) / 2.0F / 16.0F));
            }

            int lvt_42_1_ = WorldRenderer.func_228421_a_(p_229122_3_.world, new BlockPos(lvt_38_1_, lvt_39_1_, lvt_40_1_));
            float lvt_43_1_ = p_229122_6_.getInterpolatedU(lvt_28_1_ * (double)(lvt_26_1_ - lvt_32_1_));
            float lvt_44_1_ = p_229122_6_.getInterpolatedU(lvt_28_1_ * (double)(lvt_26_1_ - (lvt_32_1_ + 1)));
            float lvt_45_1_ = p_229122_6_.getInterpolatedV(lvt_30_1_ * (double)(lvt_27_1_ - lvt_33_1_));
            float lvt_46_1_ = p_229122_6_.getInterpolatedV(lvt_30_1_ * (double)(lvt_27_1_ - (lvt_33_1_ + 1)));
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_37_1_, lvt_44_1_, lvt_45_1_, -0.5F, 0, 0, -1, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_37_1_, lvt_43_1_, lvt_45_1_, -0.5F, 0, 0, -1, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_36_1_, lvt_43_1_, lvt_46_1_, -0.5F, 0, 0, -1, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_36_1_, lvt_44_1_, lvt_46_1_, -0.5F, 0, 0, -1, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_36_1_, lvt_14_1_, lvt_16_1_, 0.5F, 0, 0, 1, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_36_1_, lvt_15_1_, lvt_16_1_, 0.5F, 0, 0, 1, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_37_1_, lvt_15_1_, lvt_17_1_, 0.5F, 0, 0, 1, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_37_1_, lvt_14_1_, lvt_17_1_, 0.5F, 0, 0, 1, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_36_1_, lvt_18_1_, lvt_20_1_, -0.5F, 0, 1, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_36_1_, lvt_19_1_, lvt_20_1_, -0.5F, 0, 1, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_36_1_, lvt_19_1_, lvt_21_1_, 0.5F, 0, 1, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_36_1_, lvt_18_1_, lvt_21_1_, 0.5F, 0, 1, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_37_1_, lvt_18_1_, lvt_20_1_, 0.5F, 0, -1, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_37_1_, lvt_19_1_, lvt_20_1_, 0.5F, 0, -1, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_37_1_, lvt_19_1_, lvt_21_1_, -0.5F, 0, -1, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_37_1_, lvt_18_1_, lvt_21_1_, -0.5F, 0, -1, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_36_1_, lvt_23_1_, lvt_24_1_, 0.5F, -1, 0, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_37_1_, lvt_23_1_, lvt_25_1_, 0.5F, -1, 0, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_37_1_, lvt_22_1_, lvt_25_1_, -0.5F, -1, 0, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_34_1_, lvt_36_1_, lvt_22_1_, lvt_24_1_, -0.5F, -1, 0, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_36_1_, lvt_23_1_, lvt_24_1_, -0.5F, 1, 0, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_37_1_, lvt_23_1_, lvt_25_1_, -0.5F, 1, 0, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_37_1_, lvt_22_1_, lvt_25_1_, 0.5F, 1, 0, 0, lvt_42_1_);
            this.func_229121_a_(lvt_9_1_, lvt_10_1_, p_229122_2_, lvt_35_1_, lvt_36_1_, lvt_22_1_, lvt_24_1_, 0.5F, 1, 0, 0, lvt_42_1_);
         }
      }

   }

   private void func_229121_a_(Matrix4f p_229121_1_, Matrix3f p_229121_2_, IVertexBuilder p_229121_3_, float p_229121_4_, float p_229121_5_, float p_229121_6_, float p_229121_7_, float p_229121_8_, int p_229121_9_, int p_229121_10_, int p_229121_11_, int p_229121_12_) {
      p_229121_3_.func_227888_a_(p_229121_1_, p_229121_4_, p_229121_5_, p_229121_8_).func_225586_a_(255, 255, 255, 255).func_225583_a_(p_229121_6_, p_229121_7_).func_227891_b_(OverlayTexture.field_229196_a_).func_227886_a_(p_229121_12_).func_227887_a_(p_229121_2_, (float)p_229121_9_, (float)p_229121_10_, (float)p_229121_11_).endVertex();
   }
}
