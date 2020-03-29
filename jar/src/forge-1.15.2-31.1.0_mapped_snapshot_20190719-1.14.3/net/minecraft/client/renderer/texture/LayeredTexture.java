package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LayeredTexture extends Texture {
   private static final Logger LOGGER = LogManager.getLogger();
   public final List<String> layeredTextureNames;

   public LayeredTexture(String... p_i1274_1_) {
      this.layeredTextureNames = Lists.newArrayList(p_i1274_1_);
      if (this.layeredTextureNames.isEmpty()) {
         throw new IllegalStateException("Layered texture with no layers.");
      }
   }

   public void loadTexture(IResourceManager p_195413_1_) throws IOException {
      Iterator<String> iterator = this.layeredTextureNames.iterator();
      String s = (String)iterator.next();

      try {
         IResource iresource = p_195413_1_.getResource(new ResourceLocation(s));
         Throwable var5 = null;

         try {
            NativeImage nativeimage = MinecraftForgeClient.getImageLayer(new ResourceLocation(s), p_195413_1_);

            while(true) {
               String s1;
               do {
                  if (!iterator.hasNext()) {
                     if (!RenderSystem.isOnRenderThreadOrInit()) {
                        RenderSystem.recordRenderCall(() -> {
                           this.func_229167_a_(nativeimage);
                        });
                     } else {
                        this.func_229167_a_(nativeimage);
                     }

                     return;
                  }

                  s1 = (String)iterator.next();
               } while(s1 == null);

               IResource iresource1 = p_195413_1_.getResource(new ResourceLocation(s1));
               Throwable var9 = null;

               try {
                  NativeImage nativeimage1 = NativeImage.read(iresource1.getInputStream());
                  Throwable var11 = null;

                  try {
                     for(int i = 0; i < nativeimage1.getHeight(); ++i) {
                        for(int j = 0; j < nativeimage1.getWidth(); ++j) {
                           nativeimage.blendPixel(j, i, nativeimage1.getPixelRGBA(j, i));
                        }
                     }
                  } catch (Throwable var59) {
                     var11 = var59;
                     throw var59;
                  } finally {
                     if (nativeimage1 != null) {
                        if (var11 != null) {
                           try {
                              nativeimage1.close();
                           } catch (Throwable var58) {
                              var11.addSuppressed(var58);
                           }
                        } else {
                           nativeimage1.close();
                        }
                     }

                  }
               } catch (Throwable var61) {
                  var9 = var61;
                  throw var61;
               } finally {
                  if (iresource1 != null) {
                     if (var9 != null) {
                        try {
                           iresource1.close();
                        } catch (Throwable var57) {
                           var9.addSuppressed(var57);
                        }
                     } else {
                        iresource1.close();
                     }
                  }

               }
            }
         } catch (Throwable var63) {
            var5 = var63;
            throw var63;
         } finally {
            if (iresource != null) {
               if (var5 != null) {
                  try {
                     iresource.close();
                  } catch (Throwable var56) {
                     var5.addSuppressed(var56);
                  }
               } else {
                  iresource.close();
               }
            }

         }
      } catch (IOException var65) {
         LOGGER.error("Couldn't load layered image", var65);
      }
   }

   private void func_229167_a_(NativeImage p_229167_1_) {
      TextureUtil.func_225680_a_(this.getGlTextureId(), p_229167_1_.getWidth(), p_229167_1_.getHeight());
      p_229167_1_.uploadTextureSub(0, 0, 0, true);
   }
}
