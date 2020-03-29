package net.minecraft.client.gui.fonts.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class TrueTypeGlyphProviderFactory implements IGlyphProviderFactory {
   private static final Logger RANDOM = LogManager.getLogger();
   private final ResourceLocation file;
   private final float size;
   private final float oversample;
   private final float shiftX;
   private final float shiftY;
   private final String chars;

   public TrueTypeGlyphProviderFactory(ResourceLocation p_i49753_1_, float p_i49753_2_, float p_i49753_3_, float p_i49753_4_, float p_i49753_5_, String p_i49753_6_) {
      this.file = p_i49753_1_;
      this.size = p_i49753_2_;
      this.oversample = p_i49753_3_;
      this.shiftX = p_i49753_4_;
      this.shiftY = p_i49753_5_;
      this.chars = p_i49753_6_;
   }

   public static IGlyphProviderFactory deserialize(JsonObject p_211624_0_) {
      float lvt_1_1_ = 0.0F;
      float lvt_2_1_ = 0.0F;
      if (p_211624_0_.has("shift")) {
         JsonArray lvt_3_1_ = p_211624_0_.getAsJsonArray("shift");
         if (lvt_3_1_.size() != 2) {
            throw new JsonParseException("Expected 2 elements in 'shift', found " + lvt_3_1_.size());
         }

         lvt_1_1_ = JSONUtils.getFloat(lvt_3_1_.get(0), "shift[0]");
         lvt_2_1_ = JSONUtils.getFloat(lvt_3_1_.get(1), "shift[1]");
      }

      StringBuilder lvt_3_2_ = new StringBuilder();
      if (p_211624_0_.has("skip")) {
         JsonElement lvt_4_1_ = p_211624_0_.get("skip");
         if (lvt_4_1_.isJsonArray()) {
            JsonArray lvt_5_1_ = JSONUtils.getJsonArray(lvt_4_1_, "skip");

            for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.size(); ++lvt_6_1_) {
               lvt_3_2_.append(JSONUtils.getString(lvt_5_1_.get(lvt_6_1_), "skip[" + lvt_6_1_ + "]"));
            }
         } else {
            lvt_3_2_.append(JSONUtils.getString(lvt_4_1_, "skip"));
         }
      }

      return new TrueTypeGlyphProviderFactory(new ResourceLocation(JSONUtils.getString(p_211624_0_, "file")), JSONUtils.getFloat(p_211624_0_, "size", 11.0F), JSONUtils.getFloat(p_211624_0_, "oversample", 1.0F), lvt_1_1_, lvt_2_1_, lvt_3_2_.toString());
   }

   @Nullable
   public IGlyphProvider create(IResourceManager p_211246_1_) {
      STBTTFontinfo lvt_2_1_ = null;
      ByteBuffer lvt_3_1_ = null;

      try {
         IResource lvt_4_1_ = p_211246_1_.getResource(new ResourceLocation(this.file.getNamespace(), "font/" + this.file.getPath()));
         Throwable var5 = null;

         TrueTypeGlyphProvider var6;
         try {
            RANDOM.debug("Loading font {}", this.file);
            lvt_2_1_ = STBTTFontinfo.malloc();
            lvt_3_1_ = TextureUtil.func_225684_a_(lvt_4_1_.getInputStream());
            lvt_3_1_.flip();
            RANDOM.debug("Reading font {}", this.file);
            if (!STBTruetype.stbtt_InitFont(lvt_2_1_, lvt_3_1_)) {
               throw new IOException("Invalid ttf");
            }

            var6 = new TrueTypeGlyphProvider(lvt_3_1_, lvt_2_1_, this.size, this.oversample, this.shiftX, this.shiftY, this.chars);
         } catch (Throwable var16) {
            var5 = var16;
            throw var16;
         } finally {
            if (lvt_4_1_ != null) {
               if (var5 != null) {
                  try {
                     lvt_4_1_.close();
                  } catch (Throwable var15) {
                     var5.addSuppressed(var15);
                  }
               } else {
                  lvt_4_1_.close();
               }
            }

         }

         return var6;
      } catch (Exception var18) {
         RANDOM.error("Couldn't load truetype font {}", this.file, var18);
         if (lvt_2_1_ != null) {
            lvt_2_1_.free();
         }

         MemoryUtil.memFree(lvt_3_1_);
         return null;
      }
   }
}
