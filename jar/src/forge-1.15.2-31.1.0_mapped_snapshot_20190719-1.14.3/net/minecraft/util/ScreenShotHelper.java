package net.minecraft.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ScreenshotEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenShotHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   public static void saveScreenshot(File p_148260_0_, int p_148260_1_, int p_148260_2_, Framebuffer p_148260_3_, Consumer<ITextComponent> p_148260_4_) {
      saveScreenshot(p_148260_0_, (String)null, p_148260_1_, p_148260_2_, p_148260_3_, p_148260_4_);
   }

   public static void saveScreenshot(File p_148259_0_, @Nullable String p_148259_1_, int p_148259_2_, int p_148259_3_, Framebuffer p_148259_4_, Consumer<ITextComponent> p_148259_5_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            func_228051_b_(p_148259_0_, p_148259_1_, p_148259_2_, p_148259_3_, p_148259_4_, p_148259_5_);
         });
      } else {
         func_228051_b_(p_148259_0_, p_148259_1_, p_148259_2_, p_148259_3_, p_148259_4_, p_148259_5_);
      }

   }

   private static void func_228051_b_(File p_228051_0_, @Nullable String p_228051_1_, int p_228051_2_, int p_228051_3_, Framebuffer p_228051_4_, Consumer<ITextComponent> p_228051_5_) {
      NativeImage nativeimage = createScreenshot(p_228051_2_, p_228051_3_, p_228051_4_);
      File file1 = new File(p_228051_0_, "screenshots");
      file1.mkdir();
      File file2;
      if (p_228051_1_ == null) {
         file2 = getTimestampedPNGFileForDirectory(file1);
      } else {
         file2 = new File(file1, p_228051_1_);
      }

      ScreenshotEvent event = ForgeHooksClient.onScreenshot(nativeimage, file2);
      if (event.isCanceled()) {
         p_228051_5_.accept(event.getCancelMessage());
      } else {
         File target = event.getScreenshotFile();
         SimpleResource.RESOURCE_IO_EXECUTOR.execute(() -> {
            try {
               nativeimage.write(target);
               ITextComponent itextcomponent = (new StringTextComponent(target.getName())).applyTextStyle(TextFormatting.UNDERLINE).applyTextStyle((p_lambda$null$1_1_) -> {
                  p_lambda$null$1_1_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, target.getAbsolutePath()));
               });
               if (event.getResultMessage() != null) {
                  p_228051_5_.accept(event.getResultMessage());
               } else {
                  p_228051_5_.accept(new TranslationTextComponent("screenshot.success", new Object[]{itextcomponent}));
               }
            } catch (Exception var8) {
               LOGGER.warn("Couldn't save screenshot", var8);
               p_228051_5_.accept(new TranslationTextComponent("screenshot.failure", new Object[]{var8.getMessage()}));
            } finally {
               nativeimage.close();
            }

         });
      }
   }

   public static NativeImage createScreenshot(int p_198052_0_, int p_198052_1_, Framebuffer p_198052_2_) {
      p_198052_0_ = p_198052_2_.framebufferTextureWidth;
      p_198052_1_ = p_198052_2_.framebufferTextureHeight;
      NativeImage nativeimage = new NativeImage(p_198052_0_, p_198052_1_, false);
      RenderSystem.bindTexture(p_198052_2_.framebufferTexture);
      nativeimage.downloadFromTexture(0, true);
      nativeimage.flip();
      return nativeimage;
   }

   private static File getTimestampedPNGFileForDirectory(File p_74290_0_) {
      String s = DATE_FORMAT.format(new Date());
      int i = 1;

      while(true) {
         File file1 = new File(p_74290_0_, s + (i == 1 ? "" : "_" + i) + ".png");
         if (!file1.exists()) {
            return file1;
         }

         ++i;
      }
   }
}
