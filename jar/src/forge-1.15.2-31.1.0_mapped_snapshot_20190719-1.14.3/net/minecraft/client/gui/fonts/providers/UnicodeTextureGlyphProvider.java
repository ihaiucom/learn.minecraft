package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UnicodeTextureGlyphProvider implements IGlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final IResourceManager resourceManager;
   private final byte[] sizes;
   private final String template;
   private final Map<ResourceLocation, NativeImage> field_211845_e = Maps.newHashMap();

   public UnicodeTextureGlyphProvider(IResourceManager p_i49737_1_, byte[] p_i49737_2_, String p_i49737_3_) {
      this.resourceManager = p_i49737_1_;
      this.sizes = p_i49737_2_;
      this.template = p_i49737_3_;

      label324:
      for(int lvt_4_1_ = 0; lvt_4_1_ < 256; ++lvt_4_1_) {
         char lvt_5_1_ = (char)(lvt_4_1_ * 256);
         ResourceLocation lvt_6_1_ = this.getTextureFor(lvt_5_1_);

         try {
            IResource lvt_7_1_ = this.resourceManager.getResource(lvt_6_1_);
            Throwable var8 = null;

            try {
               NativeImage lvt_9_1_ = NativeImage.read(NativeImage.PixelFormat.RGBA, lvt_7_1_.getInputStream());
               Throwable var10 = null;

               try {
                  if (lvt_9_1_.getWidth() == 256 && lvt_9_1_.getHeight() == 256) {
                     int lvt_11_1_ = 0;

                     while(true) {
                        if (lvt_11_1_ >= 256) {
                           continue label324;
                        }

                        byte lvt_12_1_ = p_i49737_2_[lvt_5_1_ + lvt_11_1_];
                        if (lvt_12_1_ != 0 && func_212453_a(lvt_12_1_) > func_212454_b(lvt_12_1_)) {
                           p_i49737_2_[lvt_5_1_ + lvt_11_1_] = 0;
                        }

                        ++lvt_11_1_;
                     }
                  }
               } catch (Throwable var39) {
                  var10 = var39;
                  throw var39;
               } finally {
                  if (lvt_9_1_ != null) {
                     if (var10 != null) {
                        try {
                           lvt_9_1_.close();
                        } catch (Throwable var38) {
                           var10.addSuppressed(var38);
                        }
                     } else {
                        lvt_9_1_.close();
                     }
                  }

               }
            } catch (Throwable var41) {
               var8 = var41;
               throw var41;
            } finally {
               if (lvt_7_1_ != null) {
                  if (var8 != null) {
                     try {
                        lvt_7_1_.close();
                     } catch (Throwable var37) {
                        var8.addSuppressed(var37);
                     }
                  } else {
                     lvt_7_1_.close();
                  }
               }

            }
         } catch (IOException var43) {
         }

         Arrays.fill(p_i49737_2_, lvt_5_1_, lvt_5_1_ + 256, (byte)0);
      }

   }

   public void close() {
      this.field_211845_e.values().forEach(NativeImage::close);
   }

   private ResourceLocation getTextureFor(char p_211623_1_) {
      ResourceLocation lvt_2_1_ = new ResourceLocation(String.format(this.template, String.format("%02x", p_211623_1_ / 256)));
      return new ResourceLocation(lvt_2_1_.getNamespace(), "textures/" + lvt_2_1_.getPath());
   }

   @Nullable
   public IGlyphInfo func_212248_a(char p_212248_1_) {
      byte lvt_2_1_ = this.sizes[p_212248_1_];
      if (lvt_2_1_ != 0) {
         NativeImage lvt_3_1_ = (NativeImage)this.field_211845_e.computeIfAbsent(this.getTextureFor(p_212248_1_), this::loadTexture);
         if (lvt_3_1_ != null) {
            int lvt_4_1_ = func_212453_a(lvt_2_1_);
            return new UnicodeTextureGlyphProvider.GlpyhInfo(p_212248_1_ % 16 * 16 + lvt_4_1_, (p_212248_1_ & 255) / 16 * 16, func_212454_b(lvt_2_1_) - lvt_4_1_, 16, lvt_3_1_);
         }
      }

      return null;
   }

   @Nullable
   private NativeImage loadTexture(ResourceLocation p_211255_1_) {
      try {
         IResource lvt_2_1_ = this.resourceManager.getResource(p_211255_1_);
         Throwable var3 = null;

         NativeImage var4;
         try {
            var4 = NativeImage.read(NativeImage.PixelFormat.RGBA, lvt_2_1_.getInputStream());
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (lvt_2_1_ != null) {
               if (var3 != null) {
                  try {
                     lvt_2_1_.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  lvt_2_1_.close();
               }
            }

         }

         return var4;
      } catch (IOException var16) {
         LOGGER.error("Couldn't load texture {}", p_211255_1_, var16);
         return null;
      }
   }

   private static int func_212453_a(byte p_212453_0_) {
      return p_212453_0_ >> 4 & 15;
   }

   private static int func_212454_b(byte p_212454_0_) {
      return (p_212454_0_ & 15) + 1;
   }

   @OnlyIn(Dist.CLIENT)
   static class GlpyhInfo implements IGlyphInfo {
      private final int width;
      private final int height;
      private final int unpackSkipPixels;
      private final int unpackSkipRows;
      private final NativeImage texture;

      private GlpyhInfo(int p_i49758_1_, int p_i49758_2_, int p_i49758_3_, int p_i49758_4_, NativeImage p_i49758_5_) {
         this.width = p_i49758_3_;
         this.height = p_i49758_4_;
         this.unpackSkipPixels = p_i49758_1_;
         this.unpackSkipRows = p_i49758_2_;
         this.texture = p_i49758_5_;
      }

      public float getOversample() {
         return 2.0F;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)(this.width / 2 + 1);
      }

      public void uploadGlyph(int p_211573_1_, int p_211573_2_) {
         this.texture.func_227788_a_(0, p_211573_1_, p_211573_2_, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false, false);
      }

      public boolean isColored() {
         return this.texture.getFormat().getPixelSize() > 1;
      }

      public float getShadowOffset() {
         return 0.5F;
      }

      public float getBoldOffset() {
         return 0.5F;
      }

      // $FF: synthetic method
      GlpyhInfo(int p_i49759_1_, int p_i49759_2_, int p_i49759_3_, int p_i49759_4_, NativeImage p_i49759_5_, Object p_i49759_6_) {
         this(p_i49759_1_, p_i49759_2_, p_i49759_3_, p_i49759_4_, p_i49759_5_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation sizes;
      private final String template;

      public Factory(ResourceLocation p_i49760_1_, String p_i49760_2_) {
         this.sizes = p_i49760_1_;
         this.template = p_i49760_2_;
      }

      public static IGlyphProviderFactory deserialize(JsonObject p_211629_0_) {
         return new UnicodeTextureGlyphProvider.Factory(new ResourceLocation(JSONUtils.getString(p_211629_0_, "sizes")), JSONUtils.getString(p_211629_0_, "template"));
      }

      @Nullable
      public IGlyphProvider create(IResourceManager p_211246_1_) {
         try {
            IResource lvt_2_1_ = Minecraft.getInstance().getResourceManager().getResource(this.sizes);
            Throwable var3 = null;

            UnicodeTextureGlyphProvider var5;
            try {
               byte[] lvt_4_1_ = new byte[65536];
               lvt_2_1_.getInputStream().read(lvt_4_1_);
               var5 = new UnicodeTextureGlyphProvider(p_211246_1_, lvt_4_1_, this.template);
            } catch (Throwable var15) {
               var3 = var15;
               throw var15;
            } finally {
               if (lvt_2_1_ != null) {
                  if (var3 != null) {
                     try {
                        lvt_2_1_.close();
                     } catch (Throwable var14) {
                        var3.addSuppressed(var14);
                     }
                  } else {
                     lvt_2_1_.close();
                  }
               }

            }

            return var5;
         } catch (IOException var17) {
            UnicodeTextureGlyphProvider.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", this.sizes);
            return null;
         }
      }
   }
}
