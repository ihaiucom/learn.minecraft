package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadingTexture extends SimpleTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   @Nullable
   private final File cacheFile;
   private final String imageUrl;
   private final boolean field_229154_h_;
   @Nullable
   private final Runnable field_229155_i_;
   @Nullable
   private CompletableFuture<?> field_229156_j_;
   private boolean textureUploaded;

   public DownloadingTexture(@Nullable File p_i226043_1_, String p_i226043_2_, ResourceLocation p_i226043_3_, boolean p_i226043_4_, @Nullable Runnable p_i226043_5_) {
      super(p_i226043_3_);
      this.cacheFile = p_i226043_1_;
      this.imageUrl = p_i226043_2_;
      this.field_229154_h_ = p_i226043_4_;
      this.field_229155_i_ = p_i226043_5_;
   }

   private void setImage(NativeImage p_195417_1_) {
      if (this.field_229155_i_ != null) {
         this.field_229155_i_.run();
      }

      Minecraft.getInstance().execute(() -> {
         this.textureUploaded = true;
         if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
               this.func_229160_b_(p_195417_1_);
            });
         } else {
            this.func_229160_b_(p_195417_1_);
         }

      });
   }

   private void func_229160_b_(NativeImage p_229160_1_) {
      TextureUtil.func_225680_a_(this.getGlTextureId(), p_229160_1_.getWidth(), p_229160_1_.getHeight());
      p_229160_1_.uploadTextureSub(0, 0, 0, true);
   }

   public void loadTexture(IResourceManager p_195413_1_) throws IOException {
      Minecraft.getInstance().execute(() -> {
         if (!this.textureUploaded) {
            try {
               super.loadTexture(p_195413_1_);
            } catch (IOException var3) {
               LOGGER.warn("Failed to load texture: {}", this.textureLocation, var3);
            }

            this.textureUploaded = true;
         }

      });
      if (this.field_229156_j_ == null) {
         NativeImage lvt_2_2_;
         if (this.cacheFile != null && this.cacheFile.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", this.cacheFile);
            FileInputStream lvt_3_1_ = new FileInputStream(this.cacheFile);
            lvt_2_2_ = this.func_229159_a_(lvt_3_1_);
         } else {
            lvt_2_2_ = null;
         }

         if (lvt_2_2_ != null) {
            this.setImage(lvt_2_2_);
         } else {
            this.field_229156_j_ = CompletableFuture.runAsync(() -> {
               HttpURLConnection lvt_1_1_ = null;
               LOGGER.debug("Downloading http texture from {} to {}", this.imageUrl, this.cacheFile);

               try {
                  lvt_1_1_ = (HttpURLConnection)(new URL(this.imageUrl)).openConnection(Minecraft.getInstance().getProxy());
                  lvt_1_1_.setDoInput(true);
                  lvt_1_1_.setDoOutput(false);
                  lvt_1_1_.connect();
                  if (lvt_1_1_.getResponseCode() / 100 != 2) {
                     return;
                  }

                  Object lvt_2_2_;
                  if (this.cacheFile != null) {
                     FileUtils.copyInputStreamToFile(lvt_1_1_.getInputStream(), this.cacheFile);
                     lvt_2_2_ = new FileInputStream(this.cacheFile);
                  } else {
                     lvt_2_2_ = lvt_1_1_.getInputStream();
                  }

                  Minecraft.getInstance().execute(() -> {
                     NativeImage lvt_2_1_ = this.func_229159_a_(lvt_2_2_);
                     if (lvt_2_1_ != null) {
                        this.setImage(lvt_2_1_);
                     }

                  });
               } catch (Exception var6) {
                  LOGGER.error("Couldn't download http texture", var6);
               } finally {
                  if (lvt_1_1_ != null) {
                     lvt_1_1_.disconnect();
                  }

               }

            }, Util.getServerExecutor());
         }
      }
   }

   @Nullable
   private NativeImage func_229159_a_(InputStream p_229159_1_) {
      NativeImage lvt_2_1_ = null;

      try {
         lvt_2_1_ = NativeImage.read(p_229159_1_);
         if (this.field_229154_h_) {
            lvt_2_1_ = func_229163_c_(lvt_2_1_);
         }
      } catch (IOException var4) {
         LOGGER.warn("Error while loading the skin texture", var4);
      }

      return lvt_2_1_;
   }

   private static NativeImage func_229163_c_(NativeImage p_229163_0_) {
      boolean lvt_1_1_ = p_229163_0_.getHeight() == 32;
      if (lvt_1_1_) {
         NativeImage lvt_2_1_ = new NativeImage(64, 64, true);
         lvt_2_1_.copyImageData(p_229163_0_);
         p_229163_0_.close();
         p_229163_0_ = lvt_2_1_;
         lvt_2_1_.fillAreaRGBA(0, 32, 64, 32, 0);
         lvt_2_1_.copyAreaRGBA(4, 16, 16, 32, 4, 4, true, false);
         lvt_2_1_.copyAreaRGBA(8, 16, 16, 32, 4, 4, true, false);
         lvt_2_1_.copyAreaRGBA(0, 20, 24, 32, 4, 12, true, false);
         lvt_2_1_.copyAreaRGBA(4, 20, 16, 32, 4, 12, true, false);
         lvt_2_1_.copyAreaRGBA(8, 20, 8, 32, 4, 12, true, false);
         lvt_2_1_.copyAreaRGBA(12, 20, 16, 32, 4, 12, true, false);
         lvt_2_1_.copyAreaRGBA(44, 16, -8, 32, 4, 4, true, false);
         lvt_2_1_.copyAreaRGBA(48, 16, -8, 32, 4, 4, true, false);
         lvt_2_1_.copyAreaRGBA(40, 20, 0, 32, 4, 12, true, false);
         lvt_2_1_.copyAreaRGBA(44, 20, -8, 32, 4, 12, true, false);
         lvt_2_1_.copyAreaRGBA(48, 20, -16, 32, 4, 12, true, false);
         lvt_2_1_.copyAreaRGBA(52, 20, -8, 32, 4, 12, true, false);
      }

      func_229161_b_(p_229163_0_, 0, 0, 32, 16);
      if (lvt_1_1_) {
         func_229158_a_(p_229163_0_, 32, 0, 64, 32);
      }

      func_229161_b_(p_229163_0_, 0, 16, 64, 32);
      func_229161_b_(p_229163_0_, 16, 48, 48, 64);
      return p_229163_0_;
   }

   private static void func_229158_a_(NativeImage p_229158_0_, int p_229158_1_, int p_229158_2_, int p_229158_3_, int p_229158_4_) {
      int lvt_5_2_;
      int lvt_6_2_;
      for(lvt_5_2_ = p_229158_1_; lvt_5_2_ < p_229158_3_; ++lvt_5_2_) {
         for(lvt_6_2_ = p_229158_2_; lvt_6_2_ < p_229158_4_; ++lvt_6_2_) {
            int lvt_7_1_ = p_229158_0_.getPixelRGBA(lvt_5_2_, lvt_6_2_);
            if ((lvt_7_1_ >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(lvt_5_2_ = p_229158_1_; lvt_5_2_ < p_229158_3_; ++lvt_5_2_) {
         for(lvt_6_2_ = p_229158_2_; lvt_6_2_ < p_229158_4_; ++lvt_6_2_) {
            p_229158_0_.setPixelRGBA(lvt_5_2_, lvt_6_2_, p_229158_0_.getPixelRGBA(lvt_5_2_, lvt_6_2_) & 16777215);
         }
      }

   }

   private static void func_229161_b_(NativeImage p_229161_0_, int p_229161_1_, int p_229161_2_, int p_229161_3_, int p_229161_4_) {
      for(int lvt_5_1_ = p_229161_1_; lvt_5_1_ < p_229161_3_; ++lvt_5_1_) {
         for(int lvt_6_1_ = p_229161_2_; lvt_6_1_ < p_229161_4_; ++lvt_6_1_) {
            p_229161_0_.setPixelRGBA(lvt_5_1_, lvt_6_1_, p_229161_0_.getPixelRGBA(lvt_5_1_, lvt_6_1_) | -16777216);
         }
      }

   }
}
