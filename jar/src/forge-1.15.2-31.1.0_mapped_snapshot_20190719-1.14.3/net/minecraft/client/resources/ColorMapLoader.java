package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorMapLoader {
   @Deprecated
   public static int[] loadColors(IResourceManager p_217820_0_, ResourceLocation p_217820_1_) throws IOException {
      IResource lvt_2_1_ = p_217820_0_.getResource(p_217820_1_);
      Throwable var3 = null;

      Object var6;
      try {
         NativeImage lvt_4_1_ = NativeImage.read(lvt_2_1_.getInputStream());
         Throwable var5 = null;

         try {
            var6 = lvt_4_1_.makePixelArray();
         } catch (Throwable var29) {
            var6 = var29;
            var5 = var29;
            throw var29;
         } finally {
            if (lvt_4_1_ != null) {
               if (var5 != null) {
                  try {
                     lvt_4_1_.close();
                  } catch (Throwable var28) {
                     var5.addSuppressed(var28);
                  }
               } else {
                  lvt_4_1_.close();
               }
            }

         }
      } catch (Throwable var31) {
         var3 = var31;
         throw var31;
      } finally {
         if (lvt_2_1_ != null) {
            if (var3 != null) {
               try {
                  lvt_2_1_.close();
               } catch (Throwable var27) {
                  var3.addSuppressed(var27);
               }
            } else {
               lvt_2_1_.close();
            }
         }

      }

      return (int[])var6;
   }
}
