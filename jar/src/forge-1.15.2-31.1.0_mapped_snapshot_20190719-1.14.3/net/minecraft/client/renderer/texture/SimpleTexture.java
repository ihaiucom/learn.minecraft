package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SimpleTexture extends Texture {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final ResourceLocation textureLocation;

   public SimpleTexture(ResourceLocation p_i1275_1_) {
      this.textureLocation = p_i1275_1_;
   }

   public void loadTexture(IResourceManager p_195413_1_) throws IOException {
      SimpleTexture.TextureData lvt_2_1_ = this.func_215246_b(p_195413_1_);
      lvt_2_1_.func_217801_c();
      TextureMetadataSection lvt_5_1_ = lvt_2_1_.func_217798_a();
      boolean lvt_3_2_;
      boolean lvt_4_2_;
      if (lvt_5_1_ != null) {
         lvt_3_2_ = lvt_5_1_.getTextureBlur();
         lvt_4_2_ = lvt_5_1_.getTextureClamp();
      } else {
         lvt_3_2_ = false;
         lvt_4_2_ = false;
      }

      NativeImage lvt_6_1_ = lvt_2_1_.func_217800_b();
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            this.func_229207_a_(lvt_6_1_, lvt_3_2_, lvt_4_2_);
         });
      } else {
         this.func_229207_a_(lvt_6_1_, lvt_3_2_, lvt_4_2_);
      }

   }

   private void func_229207_a_(NativeImage p_229207_1_, boolean p_229207_2_, boolean p_229207_3_) {
      TextureUtil.func_225681_a_(this.getGlTextureId(), 0, p_229207_1_.getWidth(), p_229207_1_.getHeight());
      p_229207_1_.func_227789_a_(0, 0, 0, 0, 0, p_229207_1_.getWidth(), p_229207_1_.getHeight(), p_229207_2_, p_229207_3_, false, true);
   }

   protected SimpleTexture.TextureData func_215246_b(IResourceManager p_215246_1_) {
      return SimpleTexture.TextureData.func_217799_a(p_215246_1_, this.textureLocation);
   }

   @OnlyIn(Dist.CLIENT)
   public static class TextureData implements Closeable {
      @Nullable
      private final TextureMetadataSection field_217802_a;
      @Nullable
      private final NativeImage field_217803_b;
      @Nullable
      private final IOException field_217804_c;

      public TextureData(IOException p_i50473_1_) {
         this.field_217804_c = p_i50473_1_;
         this.field_217802_a = null;
         this.field_217803_b = null;
      }

      public TextureData(@Nullable TextureMetadataSection p_i50474_1_, NativeImage p_i50474_2_) {
         this.field_217804_c = null;
         this.field_217802_a = p_i50474_1_;
         this.field_217803_b = p_i50474_2_;
      }

      public static SimpleTexture.TextureData func_217799_a(IResourceManager p_217799_0_, ResourceLocation p_217799_1_) {
         try {
            IResource lvt_2_1_ = p_217799_0_.getResource(p_217799_1_);
            Throwable var3 = null;

            SimpleTexture.TextureData var6;
            try {
               NativeImage lvt_4_1_ = NativeImage.read(lvt_2_1_.getInputStream());
               TextureMetadataSection lvt_5_1_ = null;

               try {
                  lvt_5_1_ = (TextureMetadataSection)lvt_2_1_.getMetadata(TextureMetadataSection.SERIALIZER);
               } catch (RuntimeException var17) {
                  SimpleTexture.LOGGER.warn("Failed reading metadata of: {}", p_217799_1_, var17);
               }

               var6 = new SimpleTexture.TextureData(lvt_5_1_, lvt_4_1_);
            } catch (Throwable var18) {
               var3 = var18;
               throw var18;
            } finally {
               if (lvt_2_1_ != null) {
                  if (var3 != null) {
                     try {
                        lvt_2_1_.close();
                     } catch (Throwable var16) {
                        var3.addSuppressed(var16);
                     }
                  } else {
                     lvt_2_1_.close();
                  }
               }

            }

            return var6;
         } catch (IOException var20) {
            return new SimpleTexture.TextureData(var20);
         }
      }

      @Nullable
      public TextureMetadataSection func_217798_a() {
         return this.field_217802_a;
      }

      public NativeImage func_217800_b() throws IOException {
         if (this.field_217804_c != null) {
            throw this.field_217804_c;
         } else {
            return this.field_217803_b;
         }
      }

      public void close() {
         if (this.field_217803_b != null) {
            this.field_217803_b.close();
         }

      }

      public void func_217801_c() throws IOException {
         if (this.field_217804_c != null) {
            throw this.field_217804_c;
         }
      }
   }
}
