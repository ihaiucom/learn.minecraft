package net.minecraft.client.renderer.texture;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public final class NativeImage implements AutoCloseable {
   private static final Logger field_227785_a_ = LogManager.getLogger();
   private static final Set<StandardOpenOption> OPEN_OPTIONS;
   private final NativeImage.PixelFormat pixelFormat;
   private final int width;
   private final int height;
   private final boolean stbiPointer;
   private long imagePointer;
   private final long size;

   public NativeImage(int p_i48122_1_, int p_i48122_2_, boolean p_i48122_3_) {
      this(NativeImage.PixelFormat.RGBA, p_i48122_1_, p_i48122_2_, p_i48122_3_);
   }

   public NativeImage(NativeImage.PixelFormat p_i49763_1_, int p_i49763_2_, int p_i49763_3_, boolean p_i49763_4_) {
      this.pixelFormat = p_i49763_1_;
      this.width = p_i49763_2_;
      this.height = p_i49763_3_;
      this.size = (long)p_i49763_2_ * (long)p_i49763_3_ * (long)p_i49763_1_.getPixelSize();
      this.stbiPointer = false;
      if (p_i49763_4_) {
         this.imagePointer = MemoryUtil.nmemCalloc(1L, this.size);
      } else {
         this.imagePointer = MemoryUtil.nmemAlloc(this.size);
      }

   }

   private NativeImage(NativeImage.PixelFormat p_i49764_1_, int p_i49764_2_, int p_i49764_3_, boolean p_i49764_4_, long p_i49764_5_) {
      this.pixelFormat = p_i49764_1_;
      this.width = p_i49764_2_;
      this.height = p_i49764_3_;
      this.stbiPointer = p_i49764_4_;
      this.imagePointer = p_i49764_5_;
      this.size = (long)(p_i49764_2_ * p_i49764_3_ * p_i49764_1_.getPixelSize());
   }

   public String toString() {
      return "NativeImage[" + this.pixelFormat + " " + this.width + "x" + this.height + "@" + this.imagePointer + (this.stbiPointer ? "S" : "N") + "]";
   }

   public static NativeImage read(InputStream p_195713_0_) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, p_195713_0_);
   }

   public static NativeImage read(@Nullable NativeImage.PixelFormat p_211679_0_, InputStream p_211679_1_) throws IOException {
      ByteBuffer bytebuffer = null;

      NativeImage nativeimage;
      try {
         bytebuffer = TextureUtil.func_225684_a_(p_211679_1_);
         bytebuffer.rewind();
         nativeimage = read(p_211679_0_, bytebuffer);
      } finally {
         MemoryUtil.memFree(bytebuffer);
         IOUtils.closeQuietly(p_211679_1_);
      }

      return nativeimage;
   }

   public static NativeImage read(ByteBuffer p_195704_0_) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, p_195704_0_);
   }

   public static NativeImage read(@Nullable NativeImage.PixelFormat p_211677_0_, ByteBuffer p_211677_1_) throws IOException {
      if (p_211677_0_ != null && !p_211677_0_.isSerializable()) {
         throw new UnsupportedOperationException("Don't know how to read format " + p_211677_0_);
      } else if (MemoryUtil.memAddress(p_211677_1_) == 0L) {
         throw new IllegalArgumentException("Invalid buffer");
      } else {
         MemoryStack memorystack = MemoryStack.stackPush();
         Throwable var4 = null;

         NativeImage nativeimage;
         try {
            IntBuffer intbuffer = memorystack.mallocInt(1);
            IntBuffer intbuffer1 = memorystack.mallocInt(1);
            IntBuffer intbuffer2 = memorystack.mallocInt(1);
            ByteBuffer bytebuffer = STBImage.stbi_load_from_memory(p_211677_1_, intbuffer, intbuffer1, intbuffer2, p_211677_0_ == null ? 0 : p_211677_0_.pixelSize);
            if (bytebuffer == null) {
               throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            nativeimage = new NativeImage(p_211677_0_ == null ? NativeImage.PixelFormat.fromChannelCount(intbuffer2.get(0)) : p_211677_0_, intbuffer.get(0), intbuffer1.get(0), true, MemoryUtil.memAddress(bytebuffer));
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (memorystack != null) {
               if (var4 != null) {
                  try {
                     memorystack.close();
                  } catch (Throwable var15) {
                     var4.addSuppressed(var15);
                  }
               } else {
                  memorystack.close();
               }
            }

         }

         return nativeimage;
      }
   }

   private static void setWrapST(boolean p_195707_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (p_195707_0_) {
         GlStateManager.func_227677_b_(3553, 10242, 10496);
         GlStateManager.func_227677_b_(3553, 10243, 10496);
      } else {
         GlStateManager.func_227677_b_(3553, 10242, 10497);
         GlStateManager.func_227677_b_(3553, 10243, 10497);
      }

   }

   private static void setMinMagFilters(boolean p_195705_0_, boolean p_195705_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (p_195705_0_) {
         GlStateManager.func_227677_b_(3553, 10241, p_195705_1_ ? 9987 : 9729);
         GlStateManager.func_227677_b_(3553, 10240, 9729);
      } else {
         GlStateManager.func_227677_b_(3553, 10241, p_195705_1_ ? 9986 : 9728);
         GlStateManager.func_227677_b_(3553, 10240, 9728);
      }

   }

   private void checkImage() {
      if (this.imagePointer == 0L) {
         throw new IllegalStateException("Image is not allocated.");
      }
   }

   public void close() {
      if (this.imagePointer != 0L) {
         if (this.stbiPointer) {
            STBImage.nstbi_image_free(this.imagePointer);
         } else {
            MemoryUtil.nmemFree(this.imagePointer);
         }
      }

      this.imagePointer = 0L;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public NativeImage.PixelFormat getFormat() {
      return this.pixelFormat;
   }

   public int getPixelRGBA(int p_195709_1_, int p_195709_2_) {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.pixelFormat));
      } else if (p_195709_1_ >= 0 && p_195709_2_ >= 0 && p_195709_1_ < this.width && p_195709_2_ < this.height) {
         this.checkImage();
         long i = (long)((p_195709_1_ + p_195709_2_ * this.width) * 4);
         return MemoryUtil.memGetInt(this.imagePointer + i);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_195709_1_, p_195709_2_, this.width, this.height));
      }
   }

   public void setPixelRGBA(int p_195700_1_, int p_195700_2_, int p_195700_3_) {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.pixelFormat));
      } else if (p_195700_1_ >= 0 && p_195700_2_ >= 0 && p_195700_1_ < this.width && p_195700_2_ < this.height) {
         this.checkImage();
         long i = (long)((p_195700_1_ + p_195700_2_ * this.width) * 4);
         MemoryUtil.memPutInt(this.imagePointer + i, p_195700_3_);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_195700_1_, p_195700_2_, this.width, this.height));
      }
   }

   public byte getPixelLuminanceOrAlpha(int p_211675_1_, int p_211675_2_) {
      if (!this.pixelFormat.hasLuminanceOrAlpha()) {
         throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.pixelFormat));
      } else if (p_211675_1_ >= 0 && p_211675_2_ >= 0 && p_211675_1_ < this.width && p_211675_2_ < this.height) {
         int i = (p_211675_1_ + p_211675_2_ * this.width) * this.pixelFormat.getPixelSize() + this.pixelFormat.getOffsetAlphaBits() / 8;
         return MemoryUtil.memGetByte(this.imagePointer + (long)i);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_211675_1_, p_211675_2_, this.width, this.height));
      }
   }

   public void blendPixel(int p_195718_1_, int p_195718_2_, int p_195718_3_) {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
      } else {
         int i = this.getPixelRGBA(p_195718_1_, p_195718_2_);
         float f = (float)func_227786_a_(p_195718_3_) / 255.0F;
         float f1 = (float)func_227795_d_(p_195718_3_) / 255.0F;
         float f2 = (float)func_227793_c_(p_195718_3_) / 255.0F;
         float f3 = (float)func_227791_b_(p_195718_3_) / 255.0F;
         float f4 = (float)func_227786_a_(i) / 255.0F;
         float f5 = (float)func_227795_d_(i) / 255.0F;
         float f6 = (float)func_227793_c_(i) / 255.0F;
         float f7 = (float)func_227791_b_(i) / 255.0F;
         float f8 = 1.0F - f;
         float f9 = f * f + f4 * f8;
         float f10 = f1 * f + f5 * f8;
         float f11 = f2 * f + f6 * f8;
         float f12 = f3 * f + f7 * f8;
         if (f9 > 1.0F) {
            f9 = 1.0F;
         }

         if (f10 > 1.0F) {
            f10 = 1.0F;
         }

         if (f11 > 1.0F) {
            f11 = 1.0F;
         }

         if (f12 > 1.0F) {
            f12 = 1.0F;
         }

         int j = (int)(f9 * 255.0F);
         int k = (int)(f10 * 255.0F);
         int l = (int)(f11 * 255.0F);
         int i1 = (int)(f12 * 255.0F);
         this.setPixelRGBA(p_195718_1_, p_195718_2_, func_227787_a_(j, k, l, i1));
      }
   }

   /** @deprecated */
   @Deprecated
   public int[] makePixelArray() {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
      } else {
         this.checkImage();
         int[] aint = new int[this.getWidth() * this.getHeight()];

         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               int k = this.getPixelRGBA(j, i);
               int l = func_227786_a_(k);
               int i1 = func_227795_d_(k);
               int j1 = func_227793_c_(k);
               int k1 = func_227791_b_(k);
               int l1 = l << 24 | k1 << 16 | j1 << 8 | i1;
               aint[j + i * this.getWidth()] = l1;
            }
         }

         return aint;
      }
   }

   public void uploadTextureSub(int p_195697_1_, int p_195697_2_, int p_195697_3_, boolean p_195697_4_) {
      this.func_227788_a_(p_195697_1_, p_195697_2_, p_195697_3_, 0, 0, this.width, this.height, false, p_195697_4_);
   }

   public void func_227788_a_(int p_227788_1_, int p_227788_2_, int p_227788_3_, int p_227788_4_, int p_227788_5_, int p_227788_6_, int p_227788_7_, boolean p_227788_8_, boolean p_227788_9_) {
      this.func_227789_a_(p_227788_1_, p_227788_2_, p_227788_3_, p_227788_4_, p_227788_5_, p_227788_6_, p_227788_7_, false, false, p_227788_8_, p_227788_9_);
   }

   public void func_227789_a_(int p_227789_1_, int p_227789_2_, int p_227789_3_, int p_227789_4_, int p_227789_5_, int p_227789_6_, int p_227789_7_, boolean p_227789_8_, boolean p_227789_9_, boolean p_227789_10_, boolean p_227789_11_) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            this.func_227792_b_(p_227789_1_, p_227789_2_, p_227789_3_, p_227789_4_, p_227789_5_, p_227789_6_, p_227789_7_, p_227789_8_, p_227789_9_, p_227789_10_, p_227789_11_);
         });
      } else {
         this.func_227792_b_(p_227789_1_, p_227789_2_, p_227789_3_, p_227789_4_, p_227789_5_, p_227789_6_, p_227789_7_, p_227789_8_, p_227789_9_, p_227789_10_, p_227789_11_);
      }

   }

   private void func_227792_b_(int p_227792_1_, int p_227792_2_, int p_227792_3_, int p_227792_4_, int p_227792_5_, int p_227792_6_, int p_227792_7_, boolean p_227792_8_, boolean p_227792_9_, boolean p_227792_10_, boolean p_227792_11_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.checkImage();
      setMinMagFilters(p_227792_8_, p_227792_10_);
      setWrapST(p_227792_9_);
      if (p_227792_6_ == this.getWidth()) {
         GlStateManager.func_227748_o_(3314, 0);
      } else {
         GlStateManager.func_227748_o_(3314, this.getWidth());
      }

      GlStateManager.func_227748_o_(3316, p_227792_4_);
      GlStateManager.func_227748_o_(3315, p_227792_5_);
      this.pixelFormat.setGlUnpackAlignment();
      GlStateManager.func_227646_a_(3553, p_227792_1_, p_227792_2_, p_227792_3_, p_227792_6_, p_227792_7_, this.pixelFormat.getGlFormat(), 5121, this.imagePointer);
      if (p_227792_11_) {
         this.close();
      }

   }

   public void downloadFromTexture(int p_195717_1_, boolean p_195717_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      this.checkImage();
      this.pixelFormat.setGlPackAlignment();
      GlStateManager.func_227649_a_(3553, p_195717_1_, this.pixelFormat.getGlFormat(), 5121, this.imagePointer);
      if (p_195717_2_ && this.pixelFormat.hasAlpha()) {
         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               this.setPixelRGBA(j, i, this.getPixelRGBA(j, i) | 255 << this.pixelFormat.getOffsetAlpha());
            }
         }
      }

   }

   public void write(File p_209271_1_) throws IOException {
      this.write(p_209271_1_.toPath());
   }

   public void renderGlyph(STBTTFontinfo p_211676_1_, int p_211676_2_, int p_211676_3_, int p_211676_4_, float p_211676_5_, float p_211676_6_, float p_211676_7_, float p_211676_8_, int p_211676_9_, int p_211676_10_) {
      if (p_211676_9_ >= 0 && p_211676_9_ + p_211676_3_ <= this.getWidth() && p_211676_10_ >= 0 && p_211676_10_ + p_211676_4_ <= this.getHeight()) {
         if (this.pixelFormat.getPixelSize() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
         } else {
            STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(p_211676_1_.address(), this.imagePointer + (long)p_211676_9_ + (long)(p_211676_10_ * this.getWidth()), p_211676_3_, p_211676_4_, this.getWidth(), p_211676_5_, p_211676_6_, p_211676_7_, p_211676_8_, p_211676_2_);
         }
      } else {
         throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", p_211676_9_, p_211676_10_, p_211676_3_, p_211676_4_, this.getWidth(), this.getHeight()));
      }
   }

   public void write(Path p_209270_1_) throws IOException {
      if (!this.pixelFormat.isSerializable()) {
         throw new UnsupportedOperationException("Don't know how to write format " + this.pixelFormat);
      } else {
         this.checkImage();
         WritableByteChannel writablebytechannel = Files.newByteChannel(p_209270_1_, OPEN_OPTIONS);
         Throwable var3 = null;

         try {
            if (!this.func_227790_a_(writablebytechannel)) {
               throw new IOException("Could not write image to the PNG file \"" + p_209270_1_.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
            }
         } catch (Throwable var12) {
            var3 = var12;
            throw var12;
         } finally {
            if (writablebytechannel != null) {
               if (var3 != null) {
                  try {
                     writablebytechannel.close();
                  } catch (Throwable var11) {
                     var3.addSuppressed(var11);
                  }
               } else {
                  writablebytechannel.close();
               }
            }

         }

      }
   }

   public byte[] func_227796_e_() throws IOException {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      Throwable var3 = null;

      byte[] abyte;
      try {
         WritableByteChannel writablebytechannel = Channels.newChannel(bytearrayoutputstream);
         Throwable var5 = null;

         try {
            if (!this.func_227790_a_(writablebytechannel)) {
               throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
            }

            abyte = bytearrayoutputstream.toByteArray();
         } catch (Throwable var28) {
            var5 = var28;
            throw var28;
         } finally {
            if (writablebytechannel != null) {
               if (var5 != null) {
                  try {
                     writablebytechannel.close();
                  } catch (Throwable var27) {
                     var5.addSuppressed(var27);
                  }
               } else {
                  writablebytechannel.close();
               }
            }

         }
      } catch (Throwable var30) {
         var3 = var30;
         throw var30;
      } finally {
         if (bytearrayoutputstream != null) {
            if (var3 != null) {
               try {
                  bytearrayoutputstream.close();
               } catch (Throwable var26) {
                  var3.addSuppressed(var26);
               }
            } else {
               bytearrayoutputstream.close();
            }
         }

      }

      return abyte;
   }

   private boolean func_227790_a_(WritableByteChannel p_227790_1_) throws IOException {
      NativeImage.WriteCallback nativeimage$writecallback = new NativeImage.WriteCallback(p_227790_1_);

      boolean flag;
      try {
         int i = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.pixelFormat.getPixelSize());
         if (i < this.getHeight()) {
            field_227785_a_.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", this.getHeight(), i);
         }

         if (STBImageWrite.nstbi_write_png_to_func(nativeimage$writecallback.address(), 0L, this.getWidth(), i, this.pixelFormat.getPixelSize(), this.imagePointer, 0) != 0) {
            nativeimage$writecallback.propagateException();
            flag = true;
            boolean var5 = flag;
            return var5;
         }

         flag = false;
      } finally {
         nativeimage$writecallback.free();
      }

      return flag;
   }

   public void copyImageData(NativeImage p_195703_1_) {
      if (p_195703_1_.getFormat() != this.pixelFormat) {
         throw new UnsupportedOperationException("Image formats don't match.");
      } else {
         int i = this.pixelFormat.getPixelSize();
         this.checkImage();
         p_195703_1_.checkImage();
         if (this.width == p_195703_1_.width) {
            MemoryUtil.memCopy(p_195703_1_.imagePointer, this.imagePointer, Math.min(this.size, p_195703_1_.size));
         } else {
            int j = Math.min(this.getWidth(), p_195703_1_.getWidth());
            int k = Math.min(this.getHeight(), p_195703_1_.getHeight());

            for(int l = 0; l < k; ++l) {
               int i1 = l * p_195703_1_.getWidth() * i;
               int j1 = l * this.getWidth() * i;
               MemoryUtil.memCopy(p_195703_1_.imagePointer + (long)i1, this.imagePointer + (long)j1, (long)j);
            }
         }

      }
   }

   public void fillAreaRGBA(int p_195715_1_, int p_195715_2_, int p_195715_3_, int p_195715_4_, int p_195715_5_) {
      for(int i = p_195715_2_; i < p_195715_2_ + p_195715_4_; ++i) {
         for(int j = p_195715_1_; j < p_195715_1_ + p_195715_3_; ++j) {
            this.setPixelRGBA(j, i, p_195715_5_);
         }
      }

   }

   public void copyAreaRGBA(int p_195699_1_, int p_195699_2_, int p_195699_3_, int p_195699_4_, int p_195699_5_, int p_195699_6_, boolean p_195699_7_, boolean p_195699_8_) {
      for(int i = 0; i < p_195699_6_; ++i) {
         for(int j = 0; j < p_195699_5_; ++j) {
            int k = p_195699_7_ ? p_195699_5_ - 1 - j : j;
            int l = p_195699_8_ ? p_195699_6_ - 1 - i : i;
            int i1 = this.getPixelRGBA(p_195699_1_ + j, p_195699_2_ + i);
            this.setPixelRGBA(p_195699_1_ + p_195699_3_ + k, p_195699_2_ + p_195699_4_ + l, i1);
         }
      }

   }

   public void flip() {
      this.checkImage();
      MemoryStack memorystack = MemoryStack.stackPush();
      Throwable var2 = null;

      try {
         int i = this.pixelFormat.getPixelSize();
         int j = this.getWidth() * i;
         long k = memorystack.nmalloc(j);

         for(int l = 0; l < this.getHeight() / 2; ++l) {
            int i1 = l * this.getWidth() * i;
            int j1 = (this.getHeight() - 1 - l) * this.getWidth() * i;
            MemoryUtil.memCopy(this.imagePointer + (long)i1, k, (long)j);
            MemoryUtil.memCopy(this.imagePointer + (long)j1, this.imagePointer + (long)i1, (long)j);
            MemoryUtil.memCopy(k, this.imagePointer + (long)j1, (long)j);
         }
      } catch (Throwable var17) {
         var2 = var17;
         throw var17;
      } finally {
         if (memorystack != null) {
            if (var2 != null) {
               try {
                  memorystack.close();
               } catch (Throwable var16) {
                  var2.addSuppressed(var16);
               }
            } else {
               memorystack.close();
            }
         }

      }

   }

   public void resizeSubRectTo(int p_195708_1_, int p_195708_2_, int p_195708_3_, int p_195708_4_, NativeImage p_195708_5_) {
      this.checkImage();
      if (p_195708_5_.getFormat() != this.pixelFormat) {
         throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
      } else {
         int i = this.pixelFormat.getPixelSize();
         STBImageResize.nstbir_resize_uint8(this.imagePointer + (long)((p_195708_1_ + p_195708_2_ * this.getWidth()) * i), p_195708_3_, p_195708_4_, this.getWidth() * i, p_195708_5_.imagePointer, p_195708_5_.getWidth(), p_195708_5_.getHeight(), 0, i);
      }
   }

   public void untrack() {
      LWJGLMemoryUntracker.untrack(this.imagePointer);
   }

   public static NativeImage func_216511_b(String p_216511_0_) throws IOException {
      byte[] abyte = Base64.getDecoder().decode(p_216511_0_.replaceAll("\n", "").getBytes(Charsets.UTF_8));
      MemoryStack memorystack = MemoryStack.stackPush();
      Throwable var4 = null;

      NativeImage nativeimage;
      try {
         ByteBuffer bytebuffer = memorystack.malloc(abyte.length);
         bytebuffer.put(abyte);
         bytebuffer.rewind();
         nativeimage = read(bytebuffer);
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (memorystack != null) {
            if (var4 != null) {
               try {
                  memorystack.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               memorystack.close();
            }
         }

      }

      return nativeimage;
   }

   public static int func_227786_a_(int p_227786_0_) {
      return p_227786_0_ >> 24 & 255;
   }

   public static int func_227791_b_(int p_227791_0_) {
      return p_227791_0_ >> 0 & 255;
   }

   public static int func_227793_c_(int p_227793_0_) {
      return p_227793_0_ >> 8 & 255;
   }

   public static int func_227795_d_(int p_227795_0_) {
      return p_227795_0_ >> 16 & 255;
   }

   public static int func_227787_a_(int p_227787_0_, int p_227787_1_, int p_227787_2_, int p_227787_3_) {
      return (p_227787_0_ & 255) << 24 | (p_227787_1_ & 255) << 16 | (p_227787_2_ & 255) << 8 | (p_227787_3_ & 255) << 0;
   }

   static {
      OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   }

   @OnlyIn(Dist.CLIENT)
   static class WriteCallback extends STBIWriteCallback {
      private final WritableByteChannel channel;
      @Nullable
      private IOException exception;

      private WriteCallback(WritableByteChannel p_i49388_1_) {
         this.channel = p_i49388_1_;
      }

      public void invoke(long p_invoke_1_, long p_invoke_3_, int p_invoke_5_) {
         ByteBuffer bytebuffer = getData(p_invoke_3_, p_invoke_5_);

         try {
            this.channel.write(bytebuffer);
         } catch (IOException var8) {
            this.exception = var8;
         }

      }

      public void propagateException() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         }
      }

      // $FF: synthetic method
      WriteCallback(WritableByteChannel p_i49389_1_, Object p_i49389_2_) {
         this(p_i49389_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormatGLCode {
      RGBA(6408),
      RGB(6407),
      LUMINANCE_ALPHA(6410),
      LUMINANCE(6409),
      INTENSITY(32841);

      private final int glConstant;

      private PixelFormatGLCode(int p_i49761_3_) {
         this.glConstant = p_i49761_3_;
      }

      int getGlFormat() {
         return this.glConstant;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormat {
      RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
      RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
      LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
      LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

      private final int pixelSize;
      private final int glFormat;
      private final boolean red;
      private final boolean green;
      private final boolean blue;
      private final boolean hasLuminance;
      private final boolean hasAlpha;
      private final int offsetRed;
      private final int offsetGreen;
      private final int offsetBlue;
      private final int offsetLuminance;
      private final int offsetAlpha;
      private final boolean serializable;

      private PixelFormat(int p_i49762_3_, int p_i49762_4_, boolean p_i49762_5_, boolean p_i49762_6_, boolean p_i49762_7_, boolean p_i49762_8_, boolean p_i49762_9_, int p_i49762_10_, int p_i49762_11_, int p_i49762_12_, int p_i49762_13_, int p_i49762_14_, boolean p_i49762_15_) {
         this.pixelSize = p_i49762_3_;
         this.glFormat = p_i49762_4_;
         this.red = p_i49762_5_;
         this.green = p_i49762_6_;
         this.blue = p_i49762_7_;
         this.hasLuminance = p_i49762_8_;
         this.hasAlpha = p_i49762_9_;
         this.offsetRed = p_i49762_10_;
         this.offsetGreen = p_i49762_11_;
         this.offsetBlue = p_i49762_12_;
         this.offsetLuminance = p_i49762_13_;
         this.offsetAlpha = p_i49762_14_;
         this.serializable = p_i49762_15_;
      }

      public int getPixelSize() {
         return this.pixelSize;
      }

      public void setGlPackAlignment() {
         RenderSystem.assertThread(RenderSystem::isOnRenderThread);
         GlStateManager.func_227748_o_(3333, this.getPixelSize());
      }

      public void setGlUnpackAlignment() {
         RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
         GlStateManager.func_227748_o_(3317, this.getPixelSize());
      }

      public int getGlFormat() {
         return this.glFormat;
      }

      public boolean hasAlpha() {
         return this.hasAlpha;
      }

      public int getOffsetAlpha() {
         return this.offsetAlpha;
      }

      public boolean hasLuminanceOrAlpha() {
         return this.hasLuminance || this.hasAlpha;
      }

      public int getOffsetAlphaBits() {
         return this.hasLuminance ? this.offsetLuminance : this.offsetAlpha;
      }

      public boolean isSerializable() {
         return this.serializable;
      }

      private static NativeImage.PixelFormat fromChannelCount(int p_211646_0_) {
         switch(p_211646_0_) {
         case 1:
            return LUMINANCE;
         case 2:
            return LUMINANCE_ALPHA;
         case 3:
            return RGB;
         case 4:
         default:
            return RGBA;
         }
      }
   }
}
