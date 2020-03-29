package net.minecraftforge.client.event;

import java.io.File;
import java.io.IOException;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ScreenshotEvent extends Event {
   public static final ITextComponent DEFAULT_CANCEL_REASON = new StringTextComponent("Screenshot canceled");
   private NativeImage image;
   private File screenshotFile;
   private ITextComponent resultMessage = null;

   public ScreenshotEvent(NativeImage image, File screenshotFile) {
      this.image = image;
      this.screenshotFile = screenshotFile;

      try {
         this.screenshotFile = screenshotFile.getCanonicalFile();
      } catch (IOException var4) {
      }

   }

   public NativeImage getImage() {
      return this.image;
   }

   public File getScreenshotFile() {
      return this.screenshotFile;
   }

   public void setScreenshotFile(File screenshotFile) {
      this.screenshotFile = screenshotFile;
   }

   public ITextComponent getResultMessage() {
      return this.resultMessage;
   }

   public void setResultMessage(ITextComponent resultMessage) {
      this.resultMessage = resultMessage;
   }

   public ITextComponent getCancelMessage() {
      return this.getResultMessage() != null ? this.getResultMessage() : DEFAULT_CANCEL_REASON;
   }
}
