package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DynamicTexture extends Texture implements AutoCloseable {
   private NativeImage dynamicTextureData;

   public DynamicTexture(NativeImage p_i48124_1_) {
      this.dynamicTextureData = p_i48124_1_;
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            TextureUtil.func_225680_a_(this.getGlTextureId(), this.dynamicTextureData.getWidth(), this.dynamicTextureData.getHeight());
            this.updateDynamicTexture();
         });
      } else {
         TextureUtil.func_225680_a_(this.getGlTextureId(), this.dynamicTextureData.getWidth(), this.dynamicTextureData.getHeight());
         this.updateDynamicTexture();
      }

   }

   public DynamicTexture(int p_i48125_1_, int p_i48125_2_, boolean p_i48125_3_) {
      RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
      this.dynamicTextureData = new NativeImage(p_i48125_1_, p_i48125_2_, p_i48125_3_);
      TextureUtil.func_225680_a_(this.getGlTextureId(), this.dynamicTextureData.getWidth(), this.dynamicTextureData.getHeight());
   }

   public void loadTexture(IResourceManager p_195413_1_) throws IOException {
   }

   public void updateDynamicTexture() {
      this.func_229148_d_();
      this.dynamicTextureData.uploadTextureSub(0, 0, 0, false);
   }

   @Nullable
   public NativeImage getTextureData() {
      return this.dynamicTextureData;
   }

   public void setTextureData(NativeImage p_195415_1_) throws Exception {
      this.dynamicTextureData.close();
      this.dynamicTextureData = p_195415_1_;
   }

   public void close() {
      this.dynamicTextureData.close();
      this.deleteGlTexture();
      this.dynamicTextureData = null;
   }
}
