package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSkyboxCube {
   private final ResourceLocation[] locations = new ResourceLocation[6];

   public RenderSkyboxCube(ResourceLocation p_i49378_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < 6; ++lvt_2_1_) {
         this.locations[lvt_2_1_] = new ResourceLocation(p_i49378_1_.getNamespace(), p_i49378_1_.getPath() + '_' + lvt_2_1_ + ".png");
      }

   }

   public void render(Minecraft p_217616_1_, float p_217616_2_, float p_217616_3_, float p_217616_4_) {
      Tessellator lvt_5_1_ = Tessellator.getInstance();
      BufferBuilder lvt_6_1_ = lvt_5_1_.getBuffer();
      RenderSystem.matrixMode(5889);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      RenderSystem.multMatrix(Matrix4f.perspective(85.0D, (float)p_217616_1_.func_228018_at_().getFramebufferWidth() / (float)p_217616_1_.func_228018_at_().getFramebufferHeight(), 0.05F, 10.0F));
      RenderSystem.matrixMode(5888);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      RenderSystem.enableBlend();
      RenderSystem.disableAlphaTest();
      RenderSystem.disableCull();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      int lvt_7_1_ = true;

      for(int lvt_8_1_ = 0; lvt_8_1_ < 4; ++lvt_8_1_) {
         RenderSystem.pushMatrix();
         float lvt_9_1_ = ((float)(lvt_8_1_ % 2) / 2.0F - 0.5F) / 256.0F;
         float lvt_10_1_ = ((float)(lvt_8_1_ / 2) / 2.0F - 0.5F) / 256.0F;
         float lvt_11_1_ = 0.0F;
         RenderSystem.translatef(lvt_9_1_, lvt_10_1_, 0.0F);
         RenderSystem.rotatef(p_217616_2_, 1.0F, 0.0F, 0.0F);
         RenderSystem.rotatef(p_217616_3_, 0.0F, 1.0F, 0.0F);

         for(int lvt_12_1_ = 0; lvt_12_1_ < 6; ++lvt_12_1_) {
            p_217616_1_.getTextureManager().bindTexture(this.locations[lvt_12_1_]);
            lvt_6_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int lvt_13_1_ = Math.round(255.0F * p_217616_4_) / (lvt_8_1_ + 1);
            if (lvt_12_1_ == 0) {
               lvt_6_1_.func_225582_a_(-1.0D, -1.0D, 1.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(-1.0D, 1.0D, 1.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, 1.0D, 1.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, -1.0D, 1.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
            }

            if (lvt_12_1_ == 1) {
               lvt_6_1_.func_225582_a_(1.0D, -1.0D, 1.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, 1.0D, 1.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, 1.0D, -1.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, -1.0D, -1.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
            }

            if (lvt_12_1_ == 2) {
               lvt_6_1_.func_225582_a_(1.0D, -1.0D, -1.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, 1.0D, -1.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(-1.0D, 1.0D, -1.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(-1.0D, -1.0D, -1.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
            }

            if (lvt_12_1_ == 3) {
               lvt_6_1_.func_225582_a_(-1.0D, -1.0D, -1.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(-1.0D, 1.0D, -1.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(-1.0D, 1.0D, 1.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(-1.0D, -1.0D, 1.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
            }

            if (lvt_12_1_ == 4) {
               lvt_6_1_.func_225582_a_(-1.0D, -1.0D, -1.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(-1.0D, -1.0D, 1.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, -1.0D, 1.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, -1.0D, -1.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
            }

            if (lvt_12_1_ == 5) {
               lvt_6_1_.func_225582_a_(-1.0D, 1.0D, 1.0D).func_225583_a_(0.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(-1.0D, 1.0D, -1.0D).func_225583_a_(0.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, 1.0D, -1.0D).func_225583_a_(1.0F, 1.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
               lvt_6_1_.func_225582_a_(1.0D, 1.0D, 1.0D).func_225583_a_(1.0F, 0.0F).func_225586_a_(255, 255, 255, lvt_13_1_).endVertex();
            }

            lvt_5_1_.draw();
         }

         RenderSystem.popMatrix();
         RenderSystem.colorMask(true, true, true, false);
      }

      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.matrixMode(5889);
      RenderSystem.popMatrix();
      RenderSystem.matrixMode(5888);
      RenderSystem.popMatrix();
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
   }

   public CompletableFuture<Void> loadAsync(TextureManager p_217617_1_, Executor p_217617_2_) {
      CompletableFuture<?>[] lvt_3_1_ = new CompletableFuture[6];

      for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.length; ++lvt_4_1_) {
         lvt_3_1_[lvt_4_1_] = p_217617_1_.loadAsync(this.locations[lvt_4_1_], p_217617_2_);
      }

      return CompletableFuture.allOf(lvt_3_1_);
   }
}
