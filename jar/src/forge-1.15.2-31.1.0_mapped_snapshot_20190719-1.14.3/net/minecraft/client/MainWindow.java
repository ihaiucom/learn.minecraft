package net.minecraft.client;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.renderer.MonitorHandler;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.UndeclaredException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@OnlyIn(Dist.CLIENT)
public final class MainWindow implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final GLFWErrorCallback loggingErrorCallback = GLFWErrorCallback.create(this::logGlError);
   private final IWindowEventListener field_227797_c_;
   private final MonitorHandler monitorHandler;
   private final long handle;
   private int prevWindowX;
   private int prevWindowY;
   private int prevWindowWidth;
   private int prevWindowHeight;
   private Optional<VideoMode> videoMode;
   private boolean fullscreen;
   private boolean lastFullscreen;
   private int windowX;
   private int windowY;
   private int width;
   private int height;
   private int framebufferWidth;
   private int framebufferHeight;
   private int scaledWidth;
   private int scaledHeight;
   private double guiScaleFactor;
   private String renderPhase = "";
   private boolean videoModeChanged;
   private int framerateLimit;
   private boolean vsync;

   public MainWindow(IWindowEventListener p_i51170_1_, MonitorHandler p_i51170_2_, ScreenSize p_i51170_3_, @Nullable String p_i51170_4_, String p_i51170_5_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      this.monitorHandler = p_i51170_2_;
      this.func_227803_u_();
      this.func_227799_a_("Pre startup");
      this.field_227797_c_ = p_i51170_1_;
      Optional<VideoMode> lvt_6_1_ = VideoMode.parseFromSettings(p_i51170_4_);
      if (lvt_6_1_.isPresent()) {
         this.videoMode = lvt_6_1_;
      } else if (p_i51170_3_.fullscreenWidth.isPresent() && p_i51170_3_.fullscreenHeight.isPresent()) {
         this.videoMode = Optional.of(new VideoMode(p_i51170_3_.fullscreenWidth.getAsInt(), p_i51170_3_.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
      } else {
         this.videoMode = Optional.empty();
      }

      this.lastFullscreen = this.fullscreen = p_i51170_3_.fullscreen;
      Monitor lvt_7_1_ = p_i51170_2_.func_216512_a(GLFW.glfwGetPrimaryMonitor());
      this.prevWindowWidth = this.width = p_i51170_3_.width > 0 ? p_i51170_3_.width : 1;
      this.prevWindowHeight = this.height = p_i51170_3_.height > 0 ? p_i51170_3_.height : 1;
      GLFW.glfwDefaultWindowHints();
      GLFW.glfwWindowHint(139265, 196609);
      GLFW.glfwWindowHint(139275, 221185);
      GLFW.glfwWindowHint(139266, 2);
      GLFW.glfwWindowHint(139267, 0);
      GLFW.glfwWindowHint(139272, 0);
      this.handle = GLFW.glfwCreateWindow(this.width, this.height, p_i51170_5_, this.fullscreen && lvt_7_1_ != null ? lvt_7_1_.getMonitorPointer() : 0L, 0L);
      if (lvt_7_1_ != null) {
         VideoMode lvt_8_1_ = lvt_7_1_.getVideoModeOrDefault(this.fullscreen ? this.videoMode : Optional.empty());
         this.prevWindowX = this.windowX = lvt_7_1_.getVirtualPosX() + lvt_8_1_.getWidth() / 2 - this.width / 2;
         this.prevWindowY = this.windowY = lvt_7_1_.getVirtualPosY() + lvt_8_1_.getHeight() / 2 - this.height / 2;
      } else {
         int[] lvt_8_2_ = new int[1];
         int[] lvt_9_1_ = new int[1];
         GLFW.glfwGetWindowPos(this.handle, lvt_8_2_, lvt_9_1_);
         this.prevWindowX = this.windowX = lvt_8_2_[0];
         this.prevWindowY = this.windowY = lvt_9_1_[0];
      }

      GLFW.glfwMakeContextCurrent(this.handle);
      GL.createCapabilities();
      this.updateVideoMode();
      this.updateFramebufferSize();
      GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeUpdate);
      GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPosUpdate);
      GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeUpdate);
      GLFW.glfwSetWindowFocusCallback(this.handle, this::onWindowFocusUpdate);
   }

   public int func_227798_a_() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GLX._getRefreshRate(this);
   }

   public boolean func_227800_b_() {
      return GLX._shouldClose(this);
   }

   public static void checkGlfwError(BiConsumer<Integer, String> p_211162_0_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      MemoryStack lvt_1_1_ = MemoryStack.stackPush();
      Throwable var2 = null;

      try {
         PointerBuffer lvt_3_1_ = lvt_1_1_.mallocPointer(1);
         int lvt_4_1_ = GLFW.glfwGetError(lvt_3_1_);
         if (lvt_4_1_ != 0) {
            long lvt_5_1_ = lvt_3_1_.get();
            String lvt_7_1_ = lvt_5_1_ == 0L ? "" : MemoryUtil.memUTF8(lvt_5_1_);
            p_211162_0_.accept(lvt_4_1_, lvt_7_1_);
         }
      } catch (Throwable var15) {
         var2 = var15;
         throw var15;
      } finally {
         if (lvt_1_1_ != null) {
            if (var2 != null) {
               try {
                  lvt_1_1_.close();
               } catch (Throwable var14) {
                  var2.addSuppressed(var14);
               }
            } else {
               lvt_1_1_.close();
            }
         }

      }

   }

   public void setWindowIcon(InputStream p_216529_1_, InputStream p_216529_2_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);

      try {
         MemoryStack lvt_3_1_ = MemoryStack.stackPush();
         Throwable var4 = null;

         try {
            if (p_216529_1_ == null) {
               throw new FileNotFoundException("icons/icon_16x16.png");
            }

            if (p_216529_2_ == null) {
               throw new FileNotFoundException("icons/icon_32x32.png");
            }

            IntBuffer lvt_5_1_ = lvt_3_1_.mallocInt(1);
            IntBuffer lvt_6_1_ = lvt_3_1_.mallocInt(1);
            IntBuffer lvt_7_1_ = lvt_3_1_.mallocInt(1);
            Buffer lvt_8_1_ = GLFWImage.mallocStack(2, lvt_3_1_);
            ByteBuffer lvt_9_1_ = this.loadIcon(p_216529_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
            if (lvt_9_1_ == null) {
               throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }

            lvt_8_1_.position(0);
            lvt_8_1_.width(lvt_5_1_.get(0));
            lvt_8_1_.height(lvt_6_1_.get(0));
            lvt_8_1_.pixels(lvt_9_1_);
            ByteBuffer lvt_10_1_ = this.loadIcon(p_216529_2_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
            if (lvt_10_1_ == null) {
               throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }

            lvt_8_1_.position(1);
            lvt_8_1_.width(lvt_5_1_.get(0));
            lvt_8_1_.height(lvt_6_1_.get(0));
            lvt_8_1_.pixels(lvt_10_1_);
            lvt_8_1_.position(0);
            GLFW.glfwSetWindowIcon(this.handle, lvt_8_1_);
            STBImage.stbi_image_free(lvt_9_1_);
            STBImage.stbi_image_free(lvt_10_1_);
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if (lvt_3_1_ != null) {
               if (var4 != null) {
                  try {
                     lvt_3_1_.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  lvt_3_1_.close();
               }
            }

         }
      } catch (IOException var21) {
         LOGGER.error("Couldn't set icon", var21);
      }

   }

   @Nullable
   private ByteBuffer loadIcon(InputStream p_198111_1_, IntBuffer p_198111_2_, IntBuffer p_198111_3_, IntBuffer p_198111_4_) throws IOException {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      ByteBuffer lvt_5_1_ = null;

      ByteBuffer var6;
      try {
         lvt_5_1_ = TextureUtil.func_225684_a_(p_198111_1_);
         lvt_5_1_.rewind();
         var6 = STBImage.stbi_load_from_memory(lvt_5_1_, p_198111_2_, p_198111_3_, p_198111_4_, 0);
      } finally {
         if (lvt_5_1_ != null) {
            MemoryUtil.memFree(lvt_5_1_);
         }

      }

      return var6;
   }

   public void func_227799_a_(String p_227799_1_) {
      this.renderPhase = p_227799_1_;
   }

   private void func_227803_u_() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLFW.glfwSetErrorCallback(MainWindow::throwExceptionForGlError);
   }

   private static void throwExceptionForGlError(int p_208034_0_, long p_208034_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      String lvt_3_1_ = "GLFW error " + p_208034_0_ + ": " + MemoryUtil.memUTF8(p_208034_1_);
      TinyFileDialogs.tinyfd_messageBox("Minecraft", lvt_3_1_ + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).", "ok", "error", false);
      throw new MainWindow.GlException(lvt_3_1_);
   }

   public void logGlError(int p_198084_1_, long p_198084_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      String lvt_4_1_ = MemoryUtil.memUTF8(p_198084_2_);
      LOGGER.error("########## GL ERROR ##########");
      LOGGER.error("@ {}", this.renderPhase);
      LOGGER.error("{}: {}", p_198084_1_, lvt_4_1_);
   }

   public void func_227801_c_() {
      GLFWErrorCallback lvt_1_1_ = GLFW.glfwSetErrorCallback(this.loggingErrorCallback);
      if (lvt_1_1_ != null) {
         lvt_1_1_.free();
      }

   }

   public void setVsync(boolean p_216523_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.vsync = p_216523_1_;
      GLFW.glfwSwapInterval(p_216523_1_ ? 1 : 0);
   }

   public void close() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      Callbacks.glfwFreeCallbacks(this.handle);
      this.loggingErrorCallback.close();
      GLFW.glfwDestroyWindow(this.handle);
      GLFW.glfwTerminate();
   }

   private void onWindowPosUpdate(long p_198080_1_, int p_198080_3_, int p_198080_4_) {
      this.windowX = p_198080_3_;
      this.windowY = p_198080_4_;
   }

   private void onFramebufferSizeUpdate(long p_198102_1_, int p_198102_3_, int p_198102_4_) {
      if (p_198102_1_ == this.handle) {
         int lvt_5_1_ = this.getFramebufferWidth();
         int lvt_6_1_ = this.getFramebufferHeight();
         if (p_198102_3_ != 0 && p_198102_4_ != 0) {
            this.framebufferWidth = p_198102_3_;
            this.framebufferHeight = p_198102_4_;
            if (this.getFramebufferWidth() != lvt_5_1_ || this.getFramebufferHeight() != lvt_6_1_) {
               this.field_227797_c_.updateWindowSize();
            }

         }
      }
   }

   private void updateFramebufferSize() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      int[] lvt_1_1_ = new int[1];
      int[] lvt_2_1_ = new int[1];
      GLFW.glfwGetFramebufferSize(this.handle, lvt_1_1_, lvt_2_1_);
      this.framebufferWidth = lvt_1_1_[0];
      this.framebufferHeight = lvt_2_1_[0];
   }

   private void onWindowSizeUpdate(long p_198089_1_, int p_198089_3_, int p_198089_4_) {
      this.width = p_198089_3_;
      this.height = p_198089_4_;
   }

   private void onWindowFocusUpdate(long p_198095_1_, boolean p_198095_3_) {
      if (p_198095_1_ == this.handle) {
         this.field_227797_c_.setGameFocused(p_198095_3_);
      }

   }

   public void setFramerateLimit(int p_216526_1_) {
      this.framerateLimit = p_216526_1_;
   }

   public int getLimitFramerate() {
      return this.framerateLimit;
   }

   public void func_227802_e_() {
      RenderSystem.flipFrame(this.handle);
      if (this.fullscreen != this.lastFullscreen) {
         this.lastFullscreen = this.fullscreen;
         this.toggleFullscreen(this.vsync);
      }

   }

   public Optional<VideoMode> getVideoMode() {
      return this.videoMode;
   }

   public void func_224797_a(Optional<VideoMode> p_224797_1_) {
      boolean lvt_2_1_ = !p_224797_1_.equals(this.videoMode);
      this.videoMode = p_224797_1_;
      if (lvt_2_1_) {
         this.videoModeChanged = true;
      }

   }

   public void update() {
      if (this.fullscreen && this.videoModeChanged) {
         this.videoModeChanged = false;
         this.updateVideoMode();
         this.field_227797_c_.updateWindowSize();
      }

   }

   private void updateVideoMode() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      boolean lvt_1_1_ = GLFW.glfwGetWindowMonitor(this.handle) != 0L;
      if (this.fullscreen) {
         Monitor lvt_2_1_ = this.monitorHandler.func_216515_a(this);
         if (lvt_2_1_ == null) {
            LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
            this.fullscreen = false;
         } else {
            VideoMode lvt_3_1_ = lvt_2_1_.getVideoModeOrDefault(this.videoMode);
            if (!lvt_1_1_) {
               this.prevWindowX = this.windowX;
               this.prevWindowY = this.windowY;
               this.prevWindowWidth = this.width;
               this.prevWindowHeight = this.height;
            }

            this.windowX = 0;
            this.windowY = 0;
            this.width = lvt_3_1_.getWidth();
            this.height = lvt_3_1_.getHeight();
            GLFW.glfwSetWindowMonitor(this.handle, lvt_2_1_.getMonitorPointer(), this.windowX, this.windowY, this.width, this.height, lvt_3_1_.getRefreshRate());
         }
      } else {
         this.windowX = this.prevWindowX;
         this.windowY = this.prevWindowY;
         this.width = this.prevWindowWidth;
         this.height = this.prevWindowHeight;
         GLFW.glfwSetWindowMonitor(this.handle, 0L, this.windowX, this.windowY, this.width, this.height, -1);
      }

   }

   public void toggleFullscreen() {
      this.fullscreen = !this.fullscreen;
   }

   private void toggleFullscreen(boolean p_216527_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);

      try {
         this.updateVideoMode();
         this.field_227797_c_.updateWindowSize();
         this.setVsync(p_216527_1_);
         this.func_227802_e_();
      } catch (Exception var3) {
         LOGGER.error("Couldn't toggle fullscreen", var3);
      }

   }

   public int calcGuiScale(int p_216521_1_, boolean p_216521_2_) {
      int lvt_3_1_;
      for(lvt_3_1_ = 1; lvt_3_1_ != p_216521_1_ && lvt_3_1_ < this.framebufferWidth && lvt_3_1_ < this.framebufferHeight && this.framebufferWidth / (lvt_3_1_ + 1) >= 320 && this.framebufferHeight / (lvt_3_1_ + 1) >= 240; ++lvt_3_1_) {
      }

      if (p_216521_2_ && lvt_3_1_ % 2 != 0) {
         ++lvt_3_1_;
      }

      return lvt_3_1_;
   }

   public void setGuiScale(double p_216525_1_) {
      this.guiScaleFactor = p_216525_1_;
      int lvt_3_1_ = (int)((double)this.framebufferWidth / p_216525_1_);
      this.scaledWidth = (double)this.framebufferWidth / p_216525_1_ > (double)lvt_3_1_ ? lvt_3_1_ + 1 : lvt_3_1_;
      int lvt_4_1_ = (int)((double)this.framebufferHeight / p_216525_1_);
      this.scaledHeight = (double)this.framebufferHeight / p_216525_1_ > (double)lvt_4_1_ ? lvt_4_1_ + 1 : lvt_4_1_;
   }

   public void func_230148_b_(String p_230148_1_) {
      GLFW.glfwSetWindowTitle(this.handle, p_230148_1_);
   }

   public long getHandle() {
      return this.handle;
   }

   public boolean isFullscreen() {
      return this.fullscreen;
   }

   public int getFramebufferWidth() {
      return this.framebufferWidth;
   }

   public int getFramebufferHeight() {
      return this.framebufferHeight;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getScaledWidth() {
      return this.scaledWidth;
   }

   public int getScaledHeight() {
      return this.scaledHeight;
   }

   public int getWindowX() {
      return this.windowX;
   }

   public int getWindowY() {
      return this.windowY;
   }

   public double getGuiScaleFactor() {
      return this.guiScaleFactor;
   }

   @Nullable
   public Monitor func_224796_s() {
      return this.monitorHandler.func_216515_a(this);
   }

   public void func_224798_d(boolean p_224798_1_) {
      InputMappings.func_224791_a(this.handle, p_224798_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class GlException extends UndeclaredException {
      private GlException(String p_i225902_1_) {
         super(p_i225902_1_);
      }

      // $FF: synthetic method
      GlException(String p_i225903_1_, Object p_i225903_2_) {
         this(p_i225903_1_);
      }
   }
}
