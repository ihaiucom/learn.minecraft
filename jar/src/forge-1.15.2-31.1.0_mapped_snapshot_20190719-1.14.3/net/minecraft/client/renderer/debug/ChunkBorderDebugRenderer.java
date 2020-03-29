package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkBorderDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public ChunkBorderDebugRenderer(Minecraft p_i47134_1_) {
      this.minecraft = p_i47134_1_;
   }

   public void func_225619_a_(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      RenderSystem.enableDepthTest();
      RenderSystem.shadeModel(7425);
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      Entity lvt_9_1_ = this.minecraft.gameRenderer.getActiveRenderInfo().getRenderViewEntity();
      Tessellator lvt_10_1_ = Tessellator.getInstance();
      BufferBuilder lvt_11_1_ = lvt_10_1_.getBuffer();
      double lvt_12_1_ = 0.0D - p_225619_5_;
      double lvt_14_1_ = 256.0D - p_225619_5_;
      RenderSystem.disableTexture();
      RenderSystem.disableBlend();
      double lvt_16_1_ = (double)(lvt_9_1_.chunkCoordX << 4) - p_225619_3_;
      double lvt_18_1_ = (double)(lvt_9_1_.chunkCoordZ << 4) - p_225619_7_;
      RenderSystem.lineWidth(1.0F);
      lvt_11_1_.begin(3, DefaultVertexFormats.POSITION_COLOR);

      int lvt_20_6_;
      int lvt_21_3_;
      for(lvt_20_6_ = -16; lvt_20_6_ <= 32; lvt_20_6_ += 16) {
         for(lvt_21_3_ = -16; lvt_21_3_ <= 32; lvt_21_3_ += 16) {
            lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_12_1_, lvt_18_1_ + (double)lvt_21_3_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
            lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_12_1_, lvt_18_1_ + (double)lvt_21_3_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
            lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_14_1_, lvt_18_1_ + (double)lvt_21_3_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
            lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_14_1_, lvt_18_1_ + (double)lvt_21_3_).func_227885_a_(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
         }
      }

      for(lvt_20_6_ = 2; lvt_20_6_ < 16; lvt_20_6_ += 2) {
         lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_12_1_, lvt_18_1_).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_12_1_, lvt_18_1_).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_14_1_, lvt_18_1_).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_14_1_, lvt_18_1_).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_12_1_, lvt_18_1_ + 16.0D).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_12_1_, lvt_18_1_ + 16.0D).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_14_1_, lvt_18_1_ + 16.0D).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_14_1_, lvt_18_1_ + 16.0D).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      for(lvt_20_6_ = 2; lvt_20_6_ < 16; lvt_20_6_ += 2) {
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_12_1_, lvt_18_1_ + (double)lvt_20_6_).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_12_1_, lvt_18_1_ + (double)lvt_20_6_).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_14_1_, lvt_18_1_ + (double)lvt_20_6_).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_14_1_, lvt_18_1_ + (double)lvt_20_6_).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + 16.0D, lvt_12_1_, lvt_18_1_ + (double)lvt_20_6_).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + 16.0D, lvt_12_1_, lvt_18_1_ + (double)lvt_20_6_).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + 16.0D, lvt_14_1_, lvt_18_1_ + (double)lvt_20_6_).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + 16.0D, lvt_14_1_, lvt_18_1_ + (double)lvt_20_6_).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      double lvt_21_4_;
      for(lvt_20_6_ = 0; lvt_20_6_ <= 256; lvt_20_6_ += 2) {
         lvt_21_4_ = (double)lvt_20_6_ - p_225619_5_;
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_ + 16.0D).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + 16.0D, lvt_21_4_, lvt_18_1_ + 16.0D).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + 16.0D, lvt_21_4_, lvt_18_1_).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_).func_227885_a_(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_).func_227885_a_(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      lvt_10_1_.draw();
      RenderSystem.lineWidth(2.0F);
      lvt_11_1_.begin(3, DefaultVertexFormats.POSITION_COLOR);

      for(lvt_20_6_ = 0; lvt_20_6_ <= 16; lvt_20_6_ += 16) {
         for(lvt_21_3_ = 0; lvt_21_3_ <= 16; lvt_21_3_ += 16) {
            lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_12_1_, lvt_18_1_ + (double)lvt_21_3_).func_227885_a_(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
            lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_12_1_, lvt_18_1_ + (double)lvt_21_3_).func_227885_a_(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_14_1_, lvt_18_1_ + (double)lvt_21_3_).func_227885_a_(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            lvt_11_1_.func_225582_a_(lvt_16_1_ + (double)lvt_20_6_, lvt_14_1_, lvt_18_1_ + (double)lvt_21_3_).func_227885_a_(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         }
      }

      for(lvt_20_6_ = 0; lvt_20_6_ <= 256; lvt_20_6_ += 16) {
         lvt_21_4_ = (double)lvt_20_6_ - p_225619_5_;
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_).func_227885_a_(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_).func_227885_a_(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_ + 16.0D).func_227885_a_(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + 16.0D, lvt_21_4_, lvt_18_1_ + 16.0D).func_227885_a_(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_ + 16.0D, lvt_21_4_, lvt_18_1_).func_227885_a_(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_).func_227885_a_(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         lvt_11_1_.func_225582_a_(lvt_16_1_, lvt_21_4_, lvt_18_1_).func_227885_a_(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
      }

      lvt_10_1_.draw();
      RenderSystem.lineWidth(1.0F);
      RenderSystem.enableBlend();
      RenderSystem.enableTexture();
      RenderSystem.shadeModel(7424);
   }
}
