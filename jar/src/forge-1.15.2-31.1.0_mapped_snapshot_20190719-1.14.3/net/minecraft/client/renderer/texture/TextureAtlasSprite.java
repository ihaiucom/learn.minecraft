package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SpriteAwareVertexBuilder;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeTextureAtlasSprite;

@OnlyIn(Dist.CLIENT)
public class TextureAtlasSprite implements AutoCloseable, IForgeTextureAtlasSprite {
   private final AtlasTexture field_229225_b_;
   private final TextureAtlasSprite.Info field_229226_c_;
   private final AnimationMetadataSection animationMetadata;
   protected final NativeImage[] frames;
   private final int[] framesX;
   private final int[] framesY;
   @Nullable
   private final TextureAtlasSprite.InterpolationData field_229227_g_;
   private final int x;
   private final int y;
   private final float minU;
   private final float maxU;
   private final float minV;
   private final float maxV;
   private int frameCounter;
   private int tickCounter;

   protected TextureAtlasSprite(AtlasTexture p_i226049_1_, TextureAtlasSprite.Info p_i226049_2_, int p_i226049_3_, int p_i226049_4_, int p_i226049_5_, int p_i226049_6_, int p_i226049_7_, NativeImage p_i226049_8_) {
      this.field_229225_b_ = p_i226049_1_;
      AnimationMetadataSection animationmetadatasection = p_i226049_2_.field_229247_d_;
      int i = p_i226049_2_.field_229245_b_;
      int j = p_i226049_2_.field_229246_c_;
      this.x = p_i226049_6_;
      this.y = p_i226049_7_;
      this.minU = (float)p_i226049_6_ / (float)p_i226049_4_;
      this.maxU = (float)(p_i226049_6_ + i) / (float)p_i226049_4_;
      this.minV = (float)p_i226049_7_ / (float)p_i226049_5_;
      this.maxV = (float)(p_i226049_7_ + j) / (float)p_i226049_5_;
      int k = p_i226049_8_.getWidth() / animationmetadatasection.func_229302_b_(i);
      int l = p_i226049_8_.getHeight() / animationmetadatasection.func_229301_a_(j);
      int j1;
      int k1;
      int l1;
      if (animationmetadatasection.getFrameCount() > 0) {
         int i1 = (Integer)animationmetadatasection.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
         this.framesX = new int[i1];
         this.framesY = new int[i1];
         Arrays.fill(this.framesX, -1);
         Arrays.fill(this.framesY, -1);

         for(Iterator var15 = animationmetadatasection.getFrameIndexSet().iterator(); var15.hasNext(); this.framesY[j1] = k1) {
            j1 = (Integer)var15.next();
            if (j1 >= k * l) {
               throw new RuntimeException("invalid frameindex " + j1);
            }

            k1 = j1 / k;
            l1 = j1 % k;
            this.framesX[j1] = l1;
         }
      } else {
         List<AnimationFrame> list = Lists.newArrayList();
         int i2 = k * l;
         this.framesX = new int[i2];
         this.framesY = new int[i2];

         for(j1 = 0; j1 < l; ++j1) {
            for(k1 = 0; k1 < k; ++k1) {
               l1 = j1 * k + k1;
               this.framesX[l1] = k1;
               this.framesY[l1] = j1;
               list.add(new AnimationFrame(l1, -1));
            }
         }

         animationmetadatasection = new AnimationMetadataSection(list, i, j, animationmetadatasection.getFrameTime(), animationmetadatasection.isInterpolate());
      }

      this.field_229226_c_ = new TextureAtlasSprite.Info(p_i226049_2_.field_229244_a_, i, j, animationmetadatasection);
      this.animationMetadata = animationmetadatasection;

      CrashReport crashreport1;
      CrashReportCategory crashreportcategory1;
      try {
         try {
            this.frames = MipmapGenerator.func_229173_a_(p_i226049_8_, p_i226049_3_);
         } catch (Throwable var19) {
            crashreport1 = CrashReport.makeCrashReport(var19, "Generating mipmaps for frame");
            crashreportcategory1 = crashreport1.makeCategory("Frame being iterated");
            crashreportcategory1.addDetail("First frame", () -> {
               StringBuilder stringbuilder = new StringBuilder();
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(p_i226049_8_.getWidth()).append("x").append(p_i226049_8_.getHeight());
               return stringbuilder.toString();
            });
            throw new ReportedException(crashreport1);
         }
      } catch (Throwable var20) {
         crashreport1 = CrashReport.makeCrashReport(var20, "Applying mipmap");
         crashreportcategory1 = crashreport1.makeCategory("Sprite being mipmapped");
         crashreportcategory1.addDetail("Sprite name", () -> {
            return this.getName().toString();
         });
         crashreportcategory1.addDetail("Sprite size", () -> {
            return this.getWidth() + " x " + this.getHeight();
         });
         crashreportcategory1.addDetail("Sprite frames", () -> {
            return this.getFrameCount() + " frames";
         });
         crashreportcategory1.addDetail("Mipmap levels", (Object)p_i226049_3_);
         throw new ReportedException(crashreport1);
      }

      if (animationmetadatasection.isInterpolate()) {
         this.field_229227_g_ = new TextureAtlasSprite.InterpolationData(p_i226049_2_, p_i226049_3_);
      } else {
         this.field_229227_g_ = null;
      }

   }

   private void uploadFrames(int p_195659_1_) {
      int i = this.framesX[p_195659_1_] * this.field_229226_c_.field_229245_b_;
      int j = this.framesY[p_195659_1_] * this.field_229226_c_.field_229246_c_;
      this.uploadFrames(i, j, this.frames);
   }

   private void uploadFrames(int p_195667_1_, int p_195667_2_, NativeImage[] p_195667_3_) {
      for(int i = 0; i < this.frames.length && this.field_229226_c_.field_229245_b_ >> i > 0 && this.field_229226_c_.field_229246_c_ >> i > 0; ++i) {
         p_195667_3_[i].func_227788_a_(i, this.x >> i, this.y >> i, p_195667_1_ >> i, p_195667_2_ >> i, this.field_229226_c_.field_229245_b_ >> i, this.field_229226_c_.field_229246_c_ >> i, this.frames.length > 1, false);
      }

   }

   public int getWidth() {
      return this.field_229226_c_.field_229245_b_;
   }

   public int getHeight() {
      return this.field_229226_c_.field_229246_c_;
   }

   public float getMinU() {
      return this.minU;
   }

   public float getMaxU() {
      return this.maxU;
   }

   public float getInterpolatedU(double p_94214_1_) {
      float f = this.maxU - this.minU;
      return this.minU + f * (float)p_94214_1_ / 16.0F;
   }

   public float getMinV() {
      return this.minV;
   }

   public float getMaxV() {
      return this.maxV;
   }

   public float getInterpolatedV(double p_94207_1_) {
      float f = this.maxV - this.minV;
      return this.minV + f * (float)p_94207_1_ / 16.0F;
   }

   public ResourceLocation getName() {
      return this.field_229226_c_.field_229244_a_;
   }

   public AtlasTexture func_229241_m_() {
      return this.field_229225_b_;
   }

   public int getFrameCount() {
      return this.framesX.length;
   }

   public void close() {
      NativeImage[] var1 = this.frames;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         NativeImage nativeimage = var1[var3];
         if (nativeimage != null) {
            nativeimage.close();
         }
      }

      if (this.field_229227_g_ != null) {
         this.field_229227_g_.close();
      }

   }

   public String toString() {
      int i = this.framesX.length;
      return "TextureAtlasSprite{name='" + this.field_229226_c_.field_229244_a_ + '\'' + ", frameCount=" + i + ", x=" + this.x + ", y=" + this.y + ", height=" + this.field_229226_c_.field_229246_c_ + ", width=" + this.field_229226_c_.field_229245_b_ + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
   }

   public boolean isPixelTransparent(int p_195662_1_, int p_195662_2_, int p_195662_3_) {
      return (this.frames[0].getPixelRGBA(p_195662_2_ + this.framesX[p_195662_1_] * this.field_229226_c_.field_229245_b_, p_195662_3_ + this.framesY[p_195662_1_] * this.field_229226_c_.field_229246_c_) >> 24 & 255) == 0;
   }

   public void uploadMipmaps() {
      this.uploadFrames(0);
   }

   private float func_229228_a_() {
      float f = (float)this.field_229226_c_.field_229245_b_ / (this.maxU - this.minU);
      float f1 = (float)this.field_229226_c_.field_229246_c_ / (this.maxV - this.minV);
      return Math.max(f1, f);
   }

   public float func_229242_p_() {
      return 4.0F / this.func_229228_a_();
   }

   public void updateAnimation() {
      ++this.tickCounter;
      if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
         int i = this.animationMetadata.getFrameIndex(this.frameCounter);
         int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
         this.frameCounter = (this.frameCounter + 1) % j;
         this.tickCounter = 0;
         int k = this.animationMetadata.getFrameIndex(this.frameCounter);
         if (i != k && k >= 0 && k < this.getFrameCount()) {
            this.uploadFrames(k);
         }
      } else if (this.field_229227_g_ != null) {
         if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
               this.field_229227_g_.func_229257_a_();
            });
         } else {
            this.field_229227_g_.func_229257_a_();
         }
      }

   }

   public boolean hasAnimationMetadata() {
      return this.animationMetadata.getFrameCount() > 1;
   }

   public IVertexBuilder func_229230_a_(IVertexBuilder p_229230_1_) {
      return new SpriteAwareVertexBuilder(p_229230_1_, this);
   }

   public int getPixelRGBA(int p_getPixelRGBA_1_, int p_getPixelRGBA_2_, int p_getPixelRGBA_3_) {
      return this.frames[0].getPixelRGBA(p_getPixelRGBA_2_ + this.framesX[p_getPixelRGBA_1_] * this.getWidth(), p_getPixelRGBA_3_ + this.framesY[p_getPixelRGBA_1_] * this.getHeight());
   }

   @OnlyIn(Dist.CLIENT)
   final class InterpolationData implements AutoCloseable {
      private final NativeImage[] field_229256_b_;

      private InterpolationData(TextureAtlasSprite.Info p_i226051_2_, int p_i226051_3_) {
         this.field_229256_b_ = new NativeImage[p_i226051_3_ + 1];

         for(int i = 0; i < this.field_229256_b_.length; ++i) {
            int j = p_i226051_2_.field_229245_b_ >> i;
            int k = p_i226051_2_.field_229246_c_ >> i;
            if (this.field_229256_b_[i] == null) {
               this.field_229256_b_[i] = new NativeImage(j, k, false);
            }
         }

      }

      private void func_229257_a_() {
         double d0 = 1.0D - (double)TextureAtlasSprite.this.tickCounter / (double)TextureAtlasSprite.this.animationMetadata.getFrameTimeSingle(TextureAtlasSprite.this.frameCounter);
         int i = TextureAtlasSprite.this.animationMetadata.getFrameIndex(TextureAtlasSprite.this.frameCounter);
         int j = TextureAtlasSprite.this.animationMetadata.getFrameCount() == 0 ? TextureAtlasSprite.this.getFrameCount() : TextureAtlasSprite.this.animationMetadata.getFrameCount();
         int k = TextureAtlasSprite.this.animationMetadata.getFrameIndex((TextureAtlasSprite.this.frameCounter + 1) % j);
         if (i != k && k >= 0 && k < TextureAtlasSprite.this.getFrameCount()) {
            for(int l = 0; l < this.field_229256_b_.length; ++l) {
               int i1 = TextureAtlasSprite.this.field_229226_c_.field_229245_b_ >> l;
               int j1 = TextureAtlasSprite.this.field_229226_c_.field_229246_c_ >> l;

               for(int k1 = 0; k1 < j1; ++k1) {
                  for(int l1 = 0; l1 < i1; ++l1) {
                     int i2 = this.func_229259_a_(i, l, l1, k1);
                     int j2 = this.func_229259_a_(k, l, l1, k1);
                     int k2 = this.func_229258_a_(d0, i2 >> 16 & 255, j2 >> 16 & 255);
                     int l2 = this.func_229258_a_(d0, i2 >> 8 & 255, j2 >> 8 & 255);
                     int i3 = this.func_229258_a_(d0, i2 & 255, j2 & 255);
                     this.field_229256_b_[l].setPixelRGBA(l1, k1, i2 & -16777216 | k2 << 16 | l2 << 8 | i3);
                  }
               }
            }

            TextureAtlasSprite.this.uploadFrames(0, 0, this.field_229256_b_);
         }

      }

      private int func_229259_a_(int p_229259_1_, int p_229259_2_, int p_229259_3_, int p_229259_4_) {
         return TextureAtlasSprite.this.frames[p_229259_2_].getPixelRGBA(p_229259_3_ + (TextureAtlasSprite.this.framesX[p_229259_1_] * TextureAtlasSprite.this.field_229226_c_.field_229245_b_ >> p_229259_2_), p_229259_4_ + (TextureAtlasSprite.this.framesY[p_229259_1_] * TextureAtlasSprite.this.field_229226_c_.field_229246_c_ >> p_229259_2_));
      }

      private int func_229258_a_(double p_229258_1_, int p_229258_3_, int p_229258_4_) {
         return (int)(p_229258_1_ * (double)p_229258_3_ + (1.0D - p_229258_1_) * (double)p_229258_4_);
      }

      public void close() {
         NativeImage[] var1 = this.field_229256_b_;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            NativeImage nativeimage = var1[var3];
            if (nativeimage != null) {
               nativeimage.close();
            }
         }

      }

      // $FF: synthetic method
      InterpolationData(TextureAtlasSprite.Info p_i226052_2_, int p_i226052_3_, Object p_i226052_4_) {
         this(p_i226052_2_, p_i226052_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Info {
      private final ResourceLocation field_229244_a_;
      private final int field_229245_b_;
      private final int field_229246_c_;
      private final AnimationMetadataSection field_229247_d_;

      public Info(ResourceLocation p_i226050_1_, int p_i226050_2_, int p_i226050_3_, AnimationMetadataSection p_i226050_4_) {
         this.field_229244_a_ = p_i226050_1_;
         this.field_229245_b_ = p_i226050_2_;
         this.field_229246_c_ = p_i226050_3_;
         this.field_229247_d_ = p_i226050_4_;
      }

      public ResourceLocation func_229248_a_() {
         return this.field_229244_a_;
      }

      public int func_229250_b_() {
         return this.field_229245_b_;
      }

      public int func_229252_c_() {
         return this.field_229246_c_;
      }
   }
}
