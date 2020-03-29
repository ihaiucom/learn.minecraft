package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
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
public class TextureGlyphProvider implements IGlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final NativeImage texture;
   private final Char2ObjectMap<TextureGlyphProvider.GlyphInfo> glyphInfos;

   public TextureGlyphProvider(NativeImage p_i49767_1_, Char2ObjectMap<TextureGlyphProvider.GlyphInfo> p_i49767_2_) {
      this.texture = p_i49767_1_;
      this.glyphInfos = p_i49767_2_;
   }

   public void close() {
      this.texture.close();
   }

   @Nullable
   public IGlyphInfo func_212248_a(char p_212248_1_) {
      return (IGlyphInfo)this.glyphInfos.get(p_212248_1_);
   }

   @OnlyIn(Dist.CLIENT)
   static final class GlyphInfo implements IGlyphInfo {
      private final float field_211582_a;
      private final NativeImage texture;
      private final int unpackSkipPixels;
      private final int unpackSkipRows;
      private final int width;
      private final int height;
      private final int advanceWidth;
      private final int ascent;

      private GlyphInfo(float p_i49748_1_, NativeImage p_i49748_2_, int p_i49748_3_, int p_i49748_4_, int p_i49748_5_, int p_i49748_6_, int p_i49748_7_, int p_i49748_8_) {
         this.field_211582_a = p_i49748_1_;
         this.texture = p_i49748_2_;
         this.unpackSkipPixels = p_i49748_3_;
         this.unpackSkipRows = p_i49748_4_;
         this.width = p_i49748_5_;
         this.height = p_i49748_6_;
         this.advanceWidth = p_i49748_7_;
         this.ascent = p_i49748_8_;
      }

      public float getOversample() {
         return 1.0F / this.field_211582_a;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)this.advanceWidth;
      }

      public float getBearingY() {
         return IGlyphInfo.super.getBearingY() + 7.0F - (float)this.ascent;
      }

      public void uploadGlyph(int p_211573_1_, int p_211573_2_) {
         this.texture.func_227788_a_(0, p_211573_1_, p_211573_2_, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false, false);
      }

      public boolean isColored() {
         return this.texture.getFormat().getPixelSize() > 1;
      }

      // $FF: synthetic method
      GlyphInfo(float p_i49749_1_, NativeImage p_i49749_2_, int p_i49749_3_, int p_i49749_4_, int p_i49749_5_, int p_i49749_6_, int p_i49749_7_, int p_i49749_8_, Object p_i49749_9_) {
         this(p_i49749_1_, p_i49749_2_, p_i49749_3_, p_i49749_4_, p_i49749_5_, p_i49749_6_, p_i49749_7_, p_i49749_8_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation file;
      private final List<String> chars;
      private final int height;
      private final int ascent;

      public Factory(ResourceLocation p_i49750_1_, int p_i49750_2_, int p_i49750_3_, List<String> p_i49750_4_) {
         this.file = new ResourceLocation(p_i49750_1_.getNamespace(), "textures/" + p_i49750_1_.getPath());
         this.chars = p_i49750_4_;
         this.height = p_i49750_2_;
         this.ascent = p_i49750_3_;
      }

      public static TextureGlyphProvider.Factory deserialize(JsonObject p_211633_0_) {
         int lvt_1_1_ = JSONUtils.getInt(p_211633_0_, "height", 8);
         int lvt_2_1_ = JSONUtils.getInt(p_211633_0_, "ascent");
         if (lvt_2_1_ > lvt_1_1_) {
            throw new JsonParseException("Ascent " + lvt_2_1_ + " higher than height " + lvt_1_1_);
         } else {
            List<String> lvt_3_1_ = Lists.newArrayList();
            JsonArray lvt_4_1_ = JSONUtils.getJsonArray(p_211633_0_, "chars");

            for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.size(); ++lvt_5_1_) {
               String lvt_6_1_ = JSONUtils.getString(lvt_4_1_.get(lvt_5_1_), "chars[" + lvt_5_1_ + "]");
               if (lvt_5_1_ > 0) {
                  int lvt_7_1_ = lvt_6_1_.length();
                  int lvt_8_1_ = ((String)lvt_3_1_.get(0)).length();
                  if (lvt_7_1_ != lvt_8_1_) {
                     throw new JsonParseException("Elements of chars have to be the same length (found: " + lvt_7_1_ + ", expected: " + lvt_8_1_ + "), pad with space or \\u0000");
                  }
               }

               lvt_3_1_.add(lvt_6_1_);
            }

            if (!lvt_3_1_.isEmpty() && !((String)lvt_3_1_.get(0)).isEmpty()) {
               return new TextureGlyphProvider.Factory(new ResourceLocation(JSONUtils.getString(p_211633_0_, "file")), lvt_1_1_, lvt_2_1_, lvt_3_1_);
            } else {
               throw new JsonParseException("Expected to find data in chars, found none.");
            }
         }
      }

      @Nullable
      public IGlyphProvider create(IResourceManager p_211246_1_) {
         try {
            IResource lvt_2_1_ = p_211246_1_.getResource(this.file);
            Throwable var3 = null;

            try {
               NativeImage lvt_4_1_ = NativeImage.read(NativeImage.PixelFormat.RGBA, lvt_2_1_.getInputStream());
               int lvt_5_1_ = lvt_4_1_.getWidth();
               int lvt_6_1_ = lvt_4_1_.getHeight();
               int lvt_7_1_ = lvt_5_1_ / ((String)this.chars.get(0)).length();
               int lvt_8_1_ = lvt_6_1_ / this.chars.size();
               float lvt_9_1_ = (float)this.height / (float)lvt_8_1_;
               Char2ObjectMap<TextureGlyphProvider.GlyphInfo> lvt_10_1_ = new Char2ObjectOpenHashMap();

               for(int lvt_11_1_ = 0; lvt_11_1_ < this.chars.size(); ++lvt_11_1_) {
                  String lvt_12_1_ = (String)this.chars.get(lvt_11_1_);

                  for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_12_1_.length(); ++lvt_13_1_) {
                     char lvt_14_1_ = lvt_12_1_.charAt(lvt_13_1_);
                     if (lvt_14_1_ != 0 && lvt_14_1_ != ' ') {
                        int lvt_15_1_ = this.getCharacterWidth(lvt_4_1_, lvt_7_1_, lvt_8_1_, lvt_13_1_, lvt_11_1_);
                        TextureGlyphProvider.GlyphInfo lvt_16_1_ = (TextureGlyphProvider.GlyphInfo)lvt_10_1_.put(lvt_14_1_, new TextureGlyphProvider.GlyphInfo(lvt_9_1_, lvt_4_1_, lvt_13_1_ * lvt_7_1_, lvt_11_1_ * lvt_8_1_, lvt_7_1_, lvt_8_1_, (int)(0.5D + (double)((float)lvt_15_1_ * lvt_9_1_)) + 1, this.ascent));
                        if (lvt_16_1_ != null) {
                           TextureGlyphProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(lvt_14_1_), this.file);
                        }
                     }
                  }
               }

               TextureGlyphProvider var28 = new TextureGlyphProvider(lvt_4_1_, lvt_10_1_);
               return var28;
            } catch (Throwable var25) {
               var3 = var25;
               throw var25;
            } finally {
               if (lvt_2_1_ != null) {
                  if (var3 != null) {
                     try {
                        lvt_2_1_.close();
                     } catch (Throwable var24) {
                        var3.addSuppressed(var24);
                     }
                  } else {
                     lvt_2_1_.close();
                  }
               }

            }
         } catch (IOException var27) {
            throw new RuntimeException(var27.getMessage());
         }
      }

      private int getCharacterWidth(NativeImage p_211632_1_, int p_211632_2_, int p_211632_3_, int p_211632_4_, int p_211632_5_) {
         int lvt_6_1_;
         for(lvt_6_1_ = p_211632_2_ - 1; lvt_6_1_ >= 0; --lvt_6_1_) {
            int lvt_7_1_ = p_211632_4_ * p_211632_2_ + lvt_6_1_;

            for(int lvt_8_1_ = 0; lvt_8_1_ < p_211632_3_; ++lvt_8_1_) {
               int lvt_9_1_ = p_211632_5_ * p_211632_3_ + lvt_8_1_;
               if (p_211632_1_.getPixelLuminanceOrAlpha(lvt_7_1_, lvt_9_1_) != 0) {
                  return lvt_6_1_ + 1;
               }
            }
         }

         return lvt_6_1_ + 1;
      }
   }
}
