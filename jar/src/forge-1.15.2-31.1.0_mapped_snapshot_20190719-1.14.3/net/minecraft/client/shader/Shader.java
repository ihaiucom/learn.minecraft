package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Shader implements AutoCloseable {
   private final ShaderInstance manager;
   public final Framebuffer framebufferIn;
   public final Framebuffer framebufferOut;
   private final List<Object> listAuxFramebuffers = Lists.newArrayList();
   private final List<String> listAuxNames = Lists.newArrayList();
   private final List<Integer> listAuxWidths = Lists.newArrayList();
   private final List<Integer> listAuxHeights = Lists.newArrayList();
   private Matrix4f projectionMatrix;

   public Shader(IResourceManager p_i45089_1_, String p_i45089_2_, Framebuffer p_i45089_3_, Framebuffer p_i45089_4_) throws IOException {
      this.manager = new ShaderInstance(p_i45089_1_, p_i45089_2_);
      this.framebufferIn = p_i45089_3_;
      this.framebufferOut = p_i45089_4_;
   }

   public void close() {
      this.manager.close();
   }

   public void addAuxFramebuffer(String p_148041_1_, Object p_148041_2_, int p_148041_3_, int p_148041_4_) {
      this.listAuxNames.add(this.listAuxNames.size(), p_148041_1_);
      this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), p_148041_2_);
      this.listAuxWidths.add(this.listAuxWidths.size(), p_148041_3_);
      this.listAuxHeights.add(this.listAuxHeights.size(), p_148041_4_);
   }

   public void setProjectionMatrix(Matrix4f p_195654_1_) {
      this.projectionMatrix = p_195654_1_;
   }

   public void render(float p_148042_1_) {
      this.framebufferIn.unbindFramebuffer();
      float lvt_2_1_ = (float)this.framebufferOut.framebufferTextureWidth;
      float lvt_3_1_ = (float)this.framebufferOut.framebufferTextureHeight;
      RenderSystem.viewport(0, 0, (int)lvt_2_1_, (int)lvt_3_1_);
      this.manager.func_216537_a("DiffuseSampler", this.framebufferIn);

      for(int lvt_4_1_ = 0; lvt_4_1_ < this.listAuxFramebuffers.size(); ++lvt_4_1_) {
         this.manager.func_216537_a((String)this.listAuxNames.get(lvt_4_1_), this.listAuxFramebuffers.get(lvt_4_1_));
         this.manager.getShaderUniform("AuxSize" + lvt_4_1_).set((float)(Integer)this.listAuxWidths.get(lvt_4_1_), (float)(Integer)this.listAuxHeights.get(lvt_4_1_));
      }

      this.manager.getShaderUniform("ProjMat").set(this.projectionMatrix);
      this.manager.getShaderUniform("InSize").set((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
      this.manager.getShaderUniform("OutSize").set(lvt_2_1_, lvt_3_1_);
      this.manager.getShaderUniform("Time").set(p_148042_1_);
      Minecraft lvt_4_2_ = Minecraft.getInstance();
      this.manager.getShaderUniform("ScreenSize").set((float)lvt_4_2_.func_228018_at_().getFramebufferWidth(), (float)lvt_4_2_.func_228018_at_().getFramebufferHeight());
      this.manager.func_216535_f();
      this.framebufferOut.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
      this.framebufferOut.bindFramebuffer(false);
      RenderSystem.depthMask(false);
      BufferBuilder lvt_5_1_ = Tessellator.getInstance().getBuffer();
      lvt_5_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
      lvt_5_1_.func_225582_a_(0.0D, 0.0D, 500.0D).func_225586_a_(255, 255, 255, 255).endVertex();
      lvt_5_1_.func_225582_a_((double)lvt_2_1_, 0.0D, 500.0D).func_225586_a_(255, 255, 255, 255).endVertex();
      lvt_5_1_.func_225582_a_((double)lvt_2_1_, (double)lvt_3_1_, 500.0D).func_225586_a_(255, 255, 255, 255).endVertex();
      lvt_5_1_.func_225582_a_(0.0D, (double)lvt_3_1_, 500.0D).func_225586_a_(255, 255, 255, 255).endVertex();
      lvt_5_1_.finishDrawing();
      WorldVertexBufferUploader.draw(lvt_5_1_);
      RenderSystem.depthMask(true);
      this.manager.func_216544_e();
      this.framebufferOut.unbindFramebuffer();
      this.framebufferIn.unbindFramebufferTexture();
      Iterator var6 = this.listAuxFramebuffers.iterator();

      while(var6.hasNext()) {
         Object lvt_7_1_ = var6.next();
         if (lvt_7_1_ instanceof Framebuffer) {
            ((Framebuffer)lvt_7_1_).unbindFramebufferTexture();
         }
      }

   }

   public ShaderInstance getShaderManager() {
      return this.manager;
   }
}
