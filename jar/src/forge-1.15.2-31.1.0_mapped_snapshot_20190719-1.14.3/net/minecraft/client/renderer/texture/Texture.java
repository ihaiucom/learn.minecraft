package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Texture {
   protected int glTextureId = -1;
   protected boolean blur;
   protected boolean mipmap;
   private boolean lastBlur;
   private boolean lastMipmap;

   public void setBlurMipmapDirect(boolean p_174937_1_, boolean p_174937_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.blur = p_174937_1_;
      this.mipmap = p_174937_2_;
      int i;
      short j;
      if (p_174937_1_) {
         i = p_174937_2_ ? 9987 : 9729;
         j = 9729;
      } else {
         i = p_174937_2_ ? 9986 : 9728;
         j = 9728;
      }

      GlStateManager.func_227677_b_(3553, 10241, i);
      GlStateManager.func_227677_b_(3553, 10240, j);
   }

   public void setBlurMipmap(boolean p_setBlurMipmap_1_, boolean p_setBlurMipmap_2_) {
      this.lastBlur = this.blur;
      this.lastMipmap = this.mipmap;
      this.setBlurMipmapDirect(p_setBlurMipmap_1_, p_setBlurMipmap_2_);
   }

   public void restoreLastBlurMipmap() {
      this.setBlurMipmapDirect(this.lastBlur, this.lastMipmap);
   }

   public int getGlTextureId() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (this.glTextureId == -1) {
         this.glTextureId = TextureUtil.func_225678_a_();
      }

      return this.glTextureId;
   }

   public void deleteGlTexture() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            if (this.glTextureId != -1) {
               TextureUtil.func_225679_a_(this.glTextureId);
               this.glTextureId = -1;
            }

         });
      } else if (this.glTextureId != -1) {
         TextureUtil.func_225679_a_(this.glTextureId);
         this.glTextureId = -1;
      }

   }

   public abstract void loadTexture(IResourceManager var1) throws IOException;

   public void func_229148_d_() {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            GlStateManager.func_227760_t_(this.getGlTextureId());
         });
      } else {
         GlStateManager.func_227760_t_(this.getGlTextureId());
      }

   }

   public void func_215244_a(TextureManager p_215244_1_, IResourceManager p_215244_2_, ResourceLocation p_215244_3_, Executor p_215244_4_) {
      p_215244_1_.func_229263_a_(p_215244_3_, this);
   }
}
