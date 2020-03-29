package net.minecraft.client.gui.fonts.providers;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class TrueTypeGlyphProvider implements IGlyphProvider {
   private final ByteBuffer field_230146_a_;
   private final STBTTFontinfo fontInfo;
   private final float oversample;
   private final CharSet chars = new CharArraySet();
   private final float shiftX;
   private final float shiftY;
   private final float scale;
   private final float ascent;

   public TrueTypeGlyphProvider(ByteBuffer p_i230051_1_, STBTTFontinfo p_i230051_2_, float p_i230051_3_, float p_i230051_4_, float p_i230051_5_, float p_i230051_6_, String p_i230051_7_) {
      this.field_230146_a_ = p_i230051_1_;
      this.fontInfo = p_i230051_2_;
      this.oversample = p_i230051_4_;
      p_i230051_7_.chars().forEach((p_211614_1_) -> {
         this.chars.add((char)(p_211614_1_ & '\uffff'));
      });
      this.shiftX = p_i230051_5_ * p_i230051_4_;
      this.shiftY = p_i230051_6_ * p_i230051_4_;
      this.scale = STBTruetype.stbtt_ScaleForPixelHeight(p_i230051_2_, p_i230051_3_ * p_i230051_4_);
      MemoryStack lvt_8_1_ = MemoryStack.stackPush();
      Throwable var9 = null;

      try {
         IntBuffer lvt_10_1_ = lvt_8_1_.mallocInt(1);
         IntBuffer lvt_11_1_ = lvt_8_1_.mallocInt(1);
         IntBuffer lvt_12_1_ = lvt_8_1_.mallocInt(1);
         STBTruetype.stbtt_GetFontVMetrics(p_i230051_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_);
         this.ascent = (float)lvt_10_1_.get(0) * this.scale;
      } catch (Throwable var20) {
         var9 = var20;
         throw var20;
      } finally {
         if (lvt_8_1_ != null) {
            if (var9 != null) {
               try {
                  lvt_8_1_.close();
               } catch (Throwable var19) {
                  var9.addSuppressed(var19);
               }
            } else {
               lvt_8_1_.close();
            }
         }

      }

   }

   @Nullable
   public TrueTypeGlyphProvider.GlpyhInfo func_212248_a(char p_212248_1_) {
      if (this.chars.contains(p_212248_1_)) {
         return null;
      } else {
         MemoryStack lvt_2_1_ = MemoryStack.stackPush();
         Throwable var3 = null;

         IntBuffer lvt_11_1_;
         try {
            IntBuffer lvt_4_1_ = lvt_2_1_.mallocInt(1);
            IntBuffer lvt_5_1_ = lvt_2_1_.mallocInt(1);
            IntBuffer lvt_6_1_ = lvt_2_1_.mallocInt(1);
            IntBuffer lvt_7_1_ = lvt_2_1_.mallocInt(1);
            int lvt_8_1_ = STBTruetype.stbtt_FindGlyphIndex(this.fontInfo, p_212248_1_);
            if (lvt_8_1_ == 0) {
               Object var26 = null;
               return (TrueTypeGlyphProvider.GlpyhInfo)var26;
            }

            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.fontInfo, lvt_8_1_, this.scale, this.scale, this.shiftX, this.shiftY, lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
            int lvt_9_1_ = lvt_6_1_.get(0) - lvt_4_1_.get(0);
            int lvt_10_1_ = lvt_7_1_.get(0) - lvt_5_1_.get(0);
            if (lvt_9_1_ != 0 && lvt_10_1_ != 0) {
               lvt_11_1_ = lvt_2_1_.mallocInt(1);
               IntBuffer lvt_12_1_ = lvt_2_1_.mallocInt(1);
               STBTruetype.stbtt_GetGlyphHMetrics(this.fontInfo, lvt_8_1_, lvt_11_1_, lvt_12_1_);
               TrueTypeGlyphProvider.GlpyhInfo var13 = new TrueTypeGlyphProvider.GlpyhInfo(lvt_4_1_.get(0), lvt_6_1_.get(0), -lvt_5_1_.get(0), -lvt_7_1_.get(0), (float)lvt_11_1_.get(0) * this.scale, (float)lvt_12_1_.get(0) * this.scale, lvt_8_1_);
               return var13;
            }

            lvt_11_1_ = null;
         } catch (Throwable var24) {
            var3 = var24;
            throw var24;
         } finally {
            if (lvt_2_1_ != null) {
               if (var3 != null) {
                  try {
                     lvt_2_1_.close();
                  } catch (Throwable var23) {
                     var3.addSuppressed(var23);
                  }
               } else {
                  lvt_2_1_.close();
               }
            }

         }

         return lvt_11_1_;
      }
   }

   public void close() {
      this.fontInfo.free();
      MemoryUtil.memFree(this.field_230146_a_);
   }

   // $FF: synthetic method
   @Nullable
   public IGlyphInfo func_212248_a(char p_212248_1_) {
      return this.func_212248_a(p_212248_1_);
   }

   @OnlyIn(Dist.CLIENT)
   class GlpyhInfo implements IGlyphInfo {
      private final int width;
      private final int height;
      private final float field_212464_d;
      private final float field_212465_e;
      private final float advanceWidth;
      private final int glyphIndex;

      private GlpyhInfo(int p_i49751_2_, int p_i49751_3_, int p_i49751_4_, int p_i49751_5_, float p_i49751_6_, float p_i49751_7_, int p_i49751_8_) {
         this.width = p_i49751_3_ - p_i49751_2_;
         this.height = p_i49751_4_ - p_i49751_5_;
         this.advanceWidth = p_i49751_6_ / TrueTypeGlyphProvider.this.oversample;
         this.field_212464_d = (p_i49751_7_ + (float)p_i49751_2_ + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
         this.field_212465_e = (TrueTypeGlyphProvider.this.ascent - (float)p_i49751_4_ + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
         this.glyphIndex = p_i49751_8_;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public float getOversample() {
         return TrueTypeGlyphProvider.this.oversample;
      }

      public float getAdvance() {
         return this.advanceWidth;
      }

      public float getBearingX() {
         return this.field_212464_d;
      }

      public float getBearingY() {
         return this.field_212465_e;
      }

      public void uploadGlyph(int p_211573_1_, int p_211573_2_) {
         NativeImage lvt_3_1_ = new NativeImage(NativeImage.PixelFormat.LUMINANCE, this.width, this.height, false);
         lvt_3_1_.renderGlyph(TrueTypeGlyphProvider.this.fontInfo, this.glyphIndex, this.width, this.height, TrueTypeGlyphProvider.this.scale, TrueTypeGlyphProvider.this.scale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
         lvt_3_1_.func_227788_a_(0, p_211573_1_, p_211573_2_, 0, 0, this.width, this.height, false, true);
      }

      public boolean isColored() {
         return false;
      }

      // $FF: synthetic method
      GlpyhInfo(int p_i49752_2_, int p_i49752_3_, int p_i49752_4_, int p_i49752_5_, float p_i49752_6_, float p_i49752_7_, int p_i49752_8_, Object p_i49752_9_) {
         this(p_i49752_2_, p_i49752_3_, p_i49752_4_, p_i49752_5_, p_i49752_6_, p_i49752_7_, p_i49752_8_);
      }
   }
}
