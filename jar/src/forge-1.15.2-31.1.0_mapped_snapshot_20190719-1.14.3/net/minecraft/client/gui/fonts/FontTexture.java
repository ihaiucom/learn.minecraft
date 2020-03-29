package net.minecraft.client.gui.fonts;

import java.io.Closeable;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontTexture extends Texture implements Closeable {
   private final ResourceLocation textureLocation;
   private final RenderType field_228158_e_;
   private final RenderType field_228159_f_;
   private final boolean colored;
   private final FontTexture.Entry entry;

   public FontTexture(ResourceLocation p_i49770_1_, boolean p_i49770_2_) {
      this.textureLocation = p_i49770_1_;
      this.colored = p_i49770_2_;
      this.entry = new FontTexture.Entry(0, 0, 256, 256);
      TextureUtil.func_225682_a_(p_i49770_2_ ? NativeImage.PixelFormatGLCode.RGBA : NativeImage.PixelFormatGLCode.INTENSITY, this.getGlTextureId(), 256, 256);
      this.field_228158_e_ = RenderType.func_228658_l_(p_i49770_1_);
      this.field_228159_f_ = RenderType.func_228660_m_(p_i49770_1_);
   }

   public void loadTexture(IResourceManager p_195413_1_) {
   }

   public void close() {
      this.deleteGlTexture();
   }

   @Nullable
   public TexturedGlyph createTexturedGlyph(IGlyphInfo p_211131_1_) {
      if (p_211131_1_.isColored() != this.colored) {
         return null;
      } else {
         FontTexture.Entry lvt_2_1_ = this.entry.func_211224_a(p_211131_1_);
         if (lvt_2_1_ != null) {
            this.func_229148_d_();
            p_211131_1_.uploadGlyph(lvt_2_1_.xOffset, lvt_2_1_.yOffset);
            float lvt_3_1_ = 256.0F;
            float lvt_4_1_ = 256.0F;
            float lvt_5_1_ = 0.01F;
            return new TexturedGlyph(this.field_228158_e_, this.field_228159_f_, ((float)lvt_2_1_.xOffset + 0.01F) / 256.0F, ((float)lvt_2_1_.xOffset - 0.01F + (float)p_211131_1_.getWidth()) / 256.0F, ((float)lvt_2_1_.yOffset + 0.01F) / 256.0F, ((float)lvt_2_1_.yOffset - 0.01F + (float)p_211131_1_.getHeight()) / 256.0F, p_211131_1_.func_211198_f(), p_211131_1_.func_211199_g(), p_211131_1_.func_211200_h(), p_211131_1_.func_211204_i());
         } else {
            return null;
         }
      }
   }

   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   @OnlyIn(Dist.CLIENT)
   static class Entry {
      private final int xOffset;
      private final int yOffset;
      private final int field_211227_c;
      private final int field_211228_d;
      private FontTexture.Entry field_211229_e;
      private FontTexture.Entry field_211230_f;
      private boolean field_211231_g;

      private Entry(int p_i49711_1_, int p_i49711_2_, int p_i49711_3_, int p_i49711_4_) {
         this.xOffset = p_i49711_1_;
         this.yOffset = p_i49711_2_;
         this.field_211227_c = p_i49711_3_;
         this.field_211228_d = p_i49711_4_;
      }

      @Nullable
      FontTexture.Entry func_211224_a(IGlyphInfo p_211224_1_) {
         if (this.field_211229_e != null && this.field_211230_f != null) {
            FontTexture.Entry lvt_2_1_ = this.field_211229_e.func_211224_a(p_211224_1_);
            if (lvt_2_1_ == null) {
               lvt_2_1_ = this.field_211230_f.func_211224_a(p_211224_1_);
            }

            return lvt_2_1_;
         } else if (this.field_211231_g) {
            return null;
         } else {
            int lvt_2_2_ = p_211224_1_.getWidth();
            int lvt_3_1_ = p_211224_1_.getHeight();
            if (lvt_2_2_ <= this.field_211227_c && lvt_3_1_ <= this.field_211228_d) {
               if (lvt_2_2_ == this.field_211227_c && lvt_3_1_ == this.field_211228_d) {
                  this.field_211231_g = true;
                  return this;
               } else {
                  int lvt_4_1_ = this.field_211227_c - lvt_2_2_;
                  int lvt_5_1_ = this.field_211228_d - lvt_3_1_;
                  if (lvt_4_1_ > lvt_5_1_) {
                     this.field_211229_e = new FontTexture.Entry(this.xOffset, this.yOffset, lvt_2_2_, this.field_211228_d);
                     this.field_211230_f = new FontTexture.Entry(this.xOffset + lvt_2_2_ + 1, this.yOffset, this.field_211227_c - lvt_2_2_ - 1, this.field_211228_d);
                  } else {
                     this.field_211229_e = new FontTexture.Entry(this.xOffset, this.yOffset, this.field_211227_c, lvt_3_1_);
                     this.field_211230_f = new FontTexture.Entry(this.xOffset, this.yOffset + lvt_3_1_ + 1, this.field_211227_c, this.field_211228_d - lvt_3_1_ - 1);
                  }

                  return this.field_211229_e.func_211224_a(p_211224_1_);
               }
            } else {
               return null;
            }
         }
      }

      // $FF: synthetic method
      Entry(int p_i49712_1_, int p_i49712_2_, int p_i49712_3_, int p_i49712_4_, Object p_i49712_5_) {
         this(p_i49712_1_, p_i49712_2_, p_i49712_3_, p_i49712_4_);
      }
   }
}
