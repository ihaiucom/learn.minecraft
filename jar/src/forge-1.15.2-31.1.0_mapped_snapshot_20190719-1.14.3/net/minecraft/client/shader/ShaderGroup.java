package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.util.JSONException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;

@OnlyIn(Dist.CLIENT)
public class ShaderGroup implements AutoCloseable {
   private final Framebuffer mainFramebuffer;
   private final IResourceManager resourceManager;
   private final String shaderGroupName;
   private final List<Shader> listShaders = Lists.newArrayList();
   private final Map<String, Framebuffer> mapFramebuffers = Maps.newHashMap();
   private final List<Framebuffer> listFramebuffers = Lists.newArrayList();
   private Matrix4f projectionMatrix;
   private int mainFramebufferWidth;
   private int mainFramebufferHeight;
   private float time;
   private float lastStamp;

   public ShaderGroup(TextureManager p_i1050_1_, IResourceManager p_i1050_2_, Framebuffer p_i1050_3_, ResourceLocation p_i1050_4_) throws IOException, JsonSyntaxException {
      this.resourceManager = p_i1050_2_;
      this.mainFramebuffer = p_i1050_3_;
      this.time = 0.0F;
      this.lastStamp = 0.0F;
      this.mainFramebufferWidth = p_i1050_3_.framebufferWidth;
      this.mainFramebufferHeight = p_i1050_3_.framebufferHeight;
      this.shaderGroupName = p_i1050_4_.toString();
      this.resetProjectionMatrix();
      this.parseGroup(p_i1050_1_, p_i1050_4_);
   }

   private void parseGroup(TextureManager p_152765_1_, ResourceLocation p_152765_2_) throws IOException, JsonSyntaxException {
      IResource iresource = null;

      try {
         iresource = this.resourceManager.getResource(p_152765_2_);
         JsonObject jsonobject = JSONUtils.fromJson((Reader)(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8)));
         int j;
         Iterator var7;
         JsonElement jsonelement1;
         JSONException jsonexception2;
         JsonArray jsonarray1;
         if (JSONUtils.isJsonArray(jsonobject, "targets")) {
            jsonarray1 = jsonobject.getAsJsonArray("targets");
            j = 0;

            for(var7 = jsonarray1.iterator(); var7.hasNext(); ++j) {
               jsonelement1 = (JsonElement)var7.next();

               try {
                  this.initTarget(jsonelement1);
               } catch (Exception var17) {
                  jsonexception2 = JSONException.forException(var17);
                  jsonexception2.prependJsonKey("targets[" + j + "]");
                  throw jsonexception2;
               }
            }
         }

         if (JSONUtils.isJsonArray(jsonobject, "passes")) {
            jsonarray1 = jsonobject.getAsJsonArray("passes");
            j = 0;

            for(var7 = jsonarray1.iterator(); var7.hasNext(); ++j) {
               jsonelement1 = (JsonElement)var7.next();

               try {
                  this.parsePass(p_152765_1_, jsonelement1);
               } catch (Exception var16) {
                  jsonexception2 = JSONException.forException(var16);
                  jsonexception2.prependJsonKey("passes[" + j + "]");
                  throw jsonexception2;
               }
            }
         }
      } catch (Exception var18) {
         JSONException jsonexception = JSONException.forException(var18);
         jsonexception.setFilenameAndFlush(p_152765_2_.getPath());
         throw jsonexception;
      } finally {
         IOUtils.closeQuietly(iresource);
      }

   }

   private void initTarget(JsonElement p_148027_1_) throws JSONException {
      if (JSONUtils.isString(p_148027_1_)) {
         this.addFramebuffer(p_148027_1_.getAsString(), this.mainFramebufferWidth, this.mainFramebufferHeight);
      } else {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_148027_1_, "target");
         String s = JSONUtils.getString(jsonobject, "name");
         int i = JSONUtils.getInt(jsonobject, "width", this.mainFramebufferWidth);
         int j = JSONUtils.getInt(jsonobject, "height", this.mainFramebufferHeight);
         if (this.mapFramebuffers.containsKey(s)) {
            throw new JSONException(s + " is already defined");
         }

         this.addFramebuffer(s, i, j);
      }

   }

   private void parsePass(TextureManager p_152764_1_, JsonElement p_152764_2_) throws IOException {
      JsonObject jsonobject = JSONUtils.getJsonObject(p_152764_2_, "pass");
      String s = JSONUtils.getString(jsonobject, "name");
      String s1 = JSONUtils.getString(jsonobject, "intarget");
      String s2 = JSONUtils.getString(jsonobject, "outtarget");
      Framebuffer framebuffer = this.getFramebuffer(s1);
      Framebuffer framebuffer1 = this.getFramebuffer(s2);
      if (framebuffer == null) {
         throw new JSONException("Input target '" + s1 + "' does not exist");
      } else if (framebuffer1 == null) {
         throw new JSONException("Output target '" + s2 + "' does not exist");
      } else {
         Shader shader = this.addShader(s, framebuffer, framebuffer1);
         JsonArray jsonarray = JSONUtils.getJsonArray(jsonobject, "auxtargets", (JsonArray)null);
         if (jsonarray != null) {
            int i = 0;

            for(Iterator var12 = jsonarray.iterator(); var12.hasNext(); ++i) {
               JsonElement jsonelement = (JsonElement)var12.next();

               try {
                  JsonObject jsonobject1 = JSONUtils.getJsonObject(jsonelement, "auxtarget");
                  String s4 = JSONUtils.getString(jsonobject1, "name");
                  String s3 = JSONUtils.getString(jsonobject1, "id");
                  Framebuffer framebuffer2 = this.getFramebuffer(s3);
                  if (framebuffer2 == null) {
                     ResourceLocation rl = ResourceLocation.tryCreate(s3);
                     ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "textures/effect/" + rl.getPath() + ".png");
                     IResource iresource = null;

                     try {
                        iresource = this.resourceManager.getResource(resourcelocation);
                     } catch (FileNotFoundException var30) {
                        throw new JSONException("Render target or texture '" + s3 + "' does not exist");
                     } finally {
                        IOUtils.closeQuietly(iresource);
                     }

                     p_152764_1_.bindTexture(resourcelocation);
                     Texture lvt_20_2_ = p_152764_1_.func_229267_b_(resourcelocation);
                     int lvt_21_1_ = JSONUtils.getInt(jsonobject1, "width");
                     int lvt_22_1_ = JSONUtils.getInt(jsonobject1, "height");
                     boolean var24 = JSONUtils.getBoolean(jsonobject1, "bilinear");
                     if (var24) {
                        RenderSystem.texParameter(3553, 10241, 9729);
                        RenderSystem.texParameter(3553, 10240, 9729);
                     } else {
                        RenderSystem.texParameter(3553, 10241, 9728);
                        RenderSystem.texParameter(3553, 10240, 9728);
                     }

                     shader.addAuxFramebuffer(s4, lvt_20_2_.getGlTextureId(), lvt_21_1_, lvt_22_1_);
                  } else {
                     shader.addAuxFramebuffer(s4, framebuffer2, framebuffer2.framebufferTextureWidth, framebuffer2.framebufferTextureHeight);
                  }
               } catch (Exception var32) {
                  JSONException jsonexception = JSONException.forException(var32);
                  jsonexception.prependJsonKey("auxtargets[" + i + "]");
                  throw jsonexception;
               }
            }
         }

         JsonArray jsonarray1 = JSONUtils.getJsonArray(jsonobject, "uniforms", (JsonArray)null);
         if (jsonarray1 != null) {
            int l = 0;

            for(Iterator var35 = jsonarray1.iterator(); var35.hasNext(); ++l) {
               JsonElement jsonelement1 = (JsonElement)var35.next();

               try {
                  this.initUniform(jsonelement1);
               } catch (Exception var29) {
                  JSONException jsonexception1 = JSONException.forException(var29);
                  jsonexception1.prependJsonKey("uniforms[" + l + "]");
                  throw jsonexception1;
               }
            }
         }

      }
   }

   private void initUniform(JsonElement p_148028_1_) throws JSONException {
      JsonObject jsonobject = JSONUtils.getJsonObject(p_148028_1_, "uniform");
      String s = JSONUtils.getString(jsonobject, "name");
      ShaderUniform shaderuniform = ((Shader)this.listShaders.get(this.listShaders.size() - 1)).getShaderManager().func_216539_a(s);
      if (shaderuniform == null) {
         throw new JSONException("Uniform '" + s + "' does not exist");
      } else {
         float[] afloat = new float[4];
         int i = 0;

         for(Iterator var7 = JSONUtils.getJsonArray(jsonobject, "values").iterator(); var7.hasNext(); ++i) {
            JsonElement jsonelement = (JsonElement)var7.next();

            try {
               afloat[i] = JSONUtils.getFloat(jsonelement, "value");
            } catch (Exception var11) {
               JSONException jsonexception = JSONException.forException(var11);
               jsonexception.prependJsonKey("values[" + i + "]");
               throw jsonexception;
            }
         }

         switch(i) {
         case 0:
         default:
            break;
         case 1:
            shaderuniform.set(afloat[0]);
            break;
         case 2:
            shaderuniform.set(afloat[0], afloat[1]);
            break;
         case 3:
            shaderuniform.set(afloat[0], afloat[1], afloat[2]);
            break;
         case 4:
            shaderuniform.set(afloat[0], afloat[1], afloat[2], afloat[3]);
         }

      }
   }

   public Framebuffer getFramebufferRaw(String p_177066_1_) {
      return (Framebuffer)this.mapFramebuffers.get(p_177066_1_);
   }

   public void addFramebuffer(String p_148020_1_, int p_148020_2_, int p_148020_3_) {
      Framebuffer framebuffer = new Framebuffer(p_148020_2_, p_148020_3_, true, Minecraft.IS_RUNNING_ON_MAC);
      framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.mapFramebuffers.put(p_148020_1_, framebuffer);
      if (p_148020_2_ == this.mainFramebufferWidth && p_148020_3_ == this.mainFramebufferHeight) {
         this.listFramebuffers.add(framebuffer);
      }

   }

   public void close() {
      Iterator var1 = this.mapFramebuffers.values().iterator();

      while(var1.hasNext()) {
         Framebuffer framebuffer = (Framebuffer)var1.next();
         framebuffer.deleteFramebuffer();
      }

      var1 = this.listShaders.iterator();

      while(var1.hasNext()) {
         Shader shader = (Shader)var1.next();
         shader.close();
      }

      this.listShaders.clear();
   }

   public Shader addShader(String p_148023_1_, Framebuffer p_148023_2_, Framebuffer p_148023_3_) throws IOException {
      Shader shader = new Shader(this.resourceManager, p_148023_1_, p_148023_2_, p_148023_3_);
      this.listShaders.add(this.listShaders.size(), shader);
      return shader;
   }

   private void resetProjectionMatrix() {
      this.projectionMatrix = Matrix4f.orthographic((float)this.mainFramebuffer.framebufferTextureWidth, (float)this.mainFramebuffer.framebufferTextureHeight, 0.1F, 1000.0F);
   }

   public void createBindFramebuffers(int p_148026_1_, int p_148026_2_) {
      this.mainFramebufferWidth = this.mainFramebuffer.framebufferTextureWidth;
      this.mainFramebufferHeight = this.mainFramebuffer.framebufferTextureHeight;
      this.resetProjectionMatrix();
      Iterator var3 = this.listShaders.iterator();

      while(var3.hasNext()) {
         Shader shader = (Shader)var3.next();
         shader.setProjectionMatrix(this.projectionMatrix);
      }

      var3 = this.listFramebuffers.iterator();

      while(var3.hasNext()) {
         Framebuffer framebuffer = (Framebuffer)var3.next();
         framebuffer.func_216491_a(p_148026_1_, p_148026_2_, Minecraft.IS_RUNNING_ON_MAC);
      }

   }

   public void render(float p_148018_1_) {
      if (p_148018_1_ < this.lastStamp) {
         this.time += 1.0F - this.lastStamp;
         this.time += p_148018_1_;
      } else {
         this.time += p_148018_1_ - this.lastStamp;
      }

      for(this.lastStamp = p_148018_1_; this.time > 20.0F; this.time -= 20.0F) {
      }

      Iterator var2 = this.listShaders.iterator();

      while(var2.hasNext()) {
         Shader shader = (Shader)var2.next();
         shader.render(this.time / 20.0F);
      }

   }

   public final String getShaderGroupName() {
      return this.shaderGroupName;
   }

   private Framebuffer getFramebuffer(String p_148017_1_) {
      if (p_148017_1_ == null) {
         return null;
      } else {
         return p_148017_1_.equals("minecraft:main") ? this.mainFramebuffer : (Framebuffer)this.mapFramebuffers.get(p_148017_1_);
      }
   }
}
