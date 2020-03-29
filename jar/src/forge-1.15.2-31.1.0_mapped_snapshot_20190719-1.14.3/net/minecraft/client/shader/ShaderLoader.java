package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ShaderLoader {
   private final ShaderLoader.ShaderType shaderType;
   private final String shaderFilename;
   private final int shader;
   private int shaderAttachCount;

   private ShaderLoader(ShaderLoader.ShaderType p_i45091_1_, int p_i45091_2_, String p_i45091_3_) {
      this.shaderType = p_i45091_1_;
      this.shader = p_i45091_2_;
      this.shaderFilename = p_i45091_3_;
   }

   public void attachShader(IShaderManager p_148056_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      ++this.shaderAttachCount;
      GlStateManager.func_227704_d_(p_148056_1_.getProgram(), this.shader);
   }

   public void detachShader() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      --this.shaderAttachCount;
      if (this.shaderAttachCount <= 0) {
         GlStateManager.func_227703_d_(this.shader);
         this.shaderType.getLoadedShaders().remove(this.shaderFilename);
      }

   }

   public String getShaderFilename() {
      return this.shaderFilename;
   }

   public static ShaderLoader func_216534_a(ShaderLoader.ShaderType p_216534_0_, String p_216534_1_, InputStream p_216534_2_) throws IOException {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      String lvt_3_1_ = TextureUtil.func_225687_b_(p_216534_2_);
      if (lvt_3_1_ == null) {
         throw new IOException("Could not load program " + p_216534_0_.getShaderName());
      } else {
         int lvt_4_1_ = GlStateManager.func_227711_e_(p_216534_0_.getShaderMode());
         GlStateManager.func_227654_a_(lvt_4_1_, lvt_3_1_);
         GlStateManager.func_227717_f_(lvt_4_1_);
         if (GlStateManager.func_227712_e_(lvt_4_1_, 35713) == 0) {
            String lvt_5_1_ = StringUtils.trim(GlStateManager.func_227733_j_(lvt_4_1_, 32768));
            throw new IOException("Couldn't compile " + p_216534_0_.getShaderName() + " program: " + lvt_5_1_);
         } else {
            ShaderLoader lvt_5_2_ = new ShaderLoader(p_216534_0_, lvt_4_1_, p_216534_1_);
            p_216534_0_.getLoadedShaders().put(p_216534_1_, lvt_5_2_);
            return lvt_5_2_;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ShaderType {
      VERTEX("vertex", ".vsh", 35633),
      FRAGMENT("fragment", ".fsh", 35632);

      private final String shaderName;
      private final String shaderExtension;
      private final int shaderMode;
      private final Map<String, ShaderLoader> loadedShaders = Maps.newHashMap();

      private ShaderType(String p_i45090_3_, String p_i45090_4_, int p_i45090_5_) {
         this.shaderName = p_i45090_3_;
         this.shaderExtension = p_i45090_4_;
         this.shaderMode = p_i45090_5_;
      }

      public String getShaderName() {
         return this.shaderName;
      }

      public String getShaderExtension() {
         return this.shaderExtension;
      }

      private int getShaderMode() {
         return this.shaderMode;
      }

      public Map<String, ShaderLoader> getLoadedShaders() {
         return this.loadedShaders;
      }
   }
}
