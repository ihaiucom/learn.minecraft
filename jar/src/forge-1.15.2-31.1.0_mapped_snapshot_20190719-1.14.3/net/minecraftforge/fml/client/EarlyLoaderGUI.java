package net.minecraftforge.fml.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.fml.loading.progress.StartupMessageManager.Message;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.system.MemoryUtil;

public class EarlyLoaderGUI {
   private final MainWindow window;
   private boolean handledElsewhere;
   private static final float[] memorycolour = new float[]{0.0F, 0.0F, 0.0F};

   public EarlyLoaderGUI(MainWindow window) {
      this.window = window;
      RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.clear(16384, Minecraft.IS_RUNNING_ON_MAC);
      window.update();
   }

   public void handleElsewhere() {
      this.handledElsewhere = true;
   }

   void renderFromGUI() {
      this.renderMessages();
   }

   void renderTick() {
      if (!this.handledElsewhere) {
         int guiScale = this.window.calcGuiScale(0, false);
         this.window.setGuiScale((double)guiScale);
         RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.clear(16384, Minecraft.IS_RUNNING_ON_MAC);
         this.renderMessages();
         this.window.update();
      }
   }

   private void renderMessages() {
      List<Pair<Integer, Message>> messages = StartupMessageManager.getMessages();

      for(int i = 0; i < messages.size(); ++i) {
         Pair<Integer, Message> pair = (Pair)messages.get(i);
         float fade = MathHelper.clamp((4000.0F - (float)(Integer)pair.getLeft() - (float)(i - 4) * 1000.0F) / 5000.0F, 0.0F, 1.0F);
         if (fade >= 0.01F) {
            Message msg = (Message)pair.getRight();
            this.renderMessage(msg.getText(), msg.getTypeColour(), (this.window.getScaledHeight() - 15) / 10 - i + 1, fade);
         }
      }

      this.renderMemoryInfo();
   }

   private void renderMemoryInfo() {
      MemoryUsage heapusage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
      MemoryUsage offheapusage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
      float pctmemory = (float)heapusage.getUsed() / (float)heapusage.getMax();
      String memory = String.format("Memory Heap: %d / %d MB (%.1f%%)  OffHeap: %d MB", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, (double)pctmemory * 100.0D, offheapusage.getUsed() >> 20);
      int i = MathHelper.hsvToRGB((1.0F - (float)Math.pow((double)pctmemory, 1.5D)) / 3.0F, 1.0F, 0.5F);
      memorycolour[2] = (float)(i & 255) / 255.0F;
      memorycolour[1] = (float)(i >> 8 & 255) / 255.0F;
      memorycolour[0] = (float)(i >> 16 & 255) / 255.0F;
      this.renderMessage(memory, memorycolour, 1, 1.0F);
   }

   void renderMessage(String message, float[] colour, int line, float alpha) {
      GlStateManager.func_227770_y_(32884);
      ByteBuffer charBuffer = MemoryUtil.memAlloc(message.length() * 270);
      int quads = STBEasyFont.stb_easy_font_print(0.0F, 0.0F, message, (ByteBuffer)null, charBuffer);
      GL14.glVertexPointer(2, 5126, 16, charBuffer);
      RenderSystem.enableBlend();
      GL14.glBlendColor(0.0F, 0.0F, 0.0F, alpha);
      RenderSystem.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
      RenderSystem.color3f(colour[0], colour[1], colour[2]);
      RenderSystem.pushMatrix();
      RenderSystem.translatef(10.0F, (float)(line * 10), 0.0F);
      RenderSystem.scalef(1.0F, 1.0F, 0.0F);
      RenderSystem.drawArrays(7, 0, quads * 4);
      RenderSystem.popMatrix();
      MemoryUtil.memFree(charBuffer);
   }
}
