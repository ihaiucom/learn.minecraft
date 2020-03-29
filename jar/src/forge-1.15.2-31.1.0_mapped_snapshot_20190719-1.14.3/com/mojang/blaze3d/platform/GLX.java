package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlDebugTextUtils;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import oshi.SystemInfo;
import oshi.hardware.Processor;

@OnlyIn(Dist.CLIENT)
public class GLX {
   private static final Logger LOGGER = LogManager.getLogger();
   private static String capsString = "";
   private static String cpuInfo;
   private static final Map<Integer, String> LOOKUP_MAP = (Map)make(Maps.newHashMap(), (p_229878_0_) -> {
      p_229878_0_.put(0, "No error");
      p_229878_0_.put(1280, "Enum parameter is invalid for this function");
      p_229878_0_.put(1281, "Parameter is invalid for this function");
      p_229878_0_.put(1282, "Current state is invalid for this function");
      p_229878_0_.put(1283, "Stack overflow");
      p_229878_0_.put(1284, "Stack underflow");
      p_229878_0_.put(1285, "Out of memory");
      p_229878_0_.put(1286, "Operation on incomplete framebuffer");
      p_229878_0_.put(1286, "Operation on incomplete framebuffer");
   });

   public static String getOpenGLVersionString() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GLFW.glfwGetCurrentContext() == 0L ? "NO CONTEXT" : GlStateManager.func_227610_C_(7937) + " GL version " + GlStateManager.func_227610_C_(7938) + ", " + GlStateManager.func_227610_C_(7936);
   }

   public static int _getRefreshRate(MainWindow p__getRefreshRate_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      long lvt_1_1_ = GLFW.glfwGetWindowMonitor(p__getRefreshRate_0_.getHandle());
      if (lvt_1_1_ == 0L) {
         lvt_1_1_ = GLFW.glfwGetPrimaryMonitor();
      }

      GLFWVidMode lvt_3_1_ = lvt_1_1_ == 0L ? null : GLFW.glfwGetVideoMode(lvt_1_1_);
      return lvt_3_1_ == null ? 0 : lvt_3_1_.refreshRate();
   }

   public static String _getLWJGLVersion() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return Version.getVersion();
   }

   public static LongSupplier _initGlfw() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      MainWindow.checkGlfwError((p_229879_0_, p_229879_1_) -> {
         throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", p_229879_0_, p_229879_1_));
      });
      List<String> lvt_0_1_ = Lists.newArrayList();
      GLFWErrorCallback lvt_1_1_ = GLFW.glfwSetErrorCallback((p_229880_1_, p_229880_2_) -> {
         lvt_0_1_.add(String.format("GLFW error during init: [0x%X]%s", p_229880_1_, p_229880_2_));
      });
      if (!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(lvt_0_1_));
      } else {
         LongSupplier lvt_2_2_ = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9D);
         };
         Iterator var3 = lvt_0_1_.iterator();

         while(var3.hasNext()) {
            String lvt_4_1_ = (String)var3.next();
            LOGGER.error("GLFW error collected during initialization: {}", lvt_4_1_);
         }

         RenderSystem.setErrorCallback(lvt_1_1_);
         return lvt_2_2_;
      }
   }

   public static void _setGlfwErrorCallback(GLFWErrorCallbackI p__setGlfwErrorCallback_0_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLFWErrorCallback lvt_1_1_ = GLFW.glfwSetErrorCallback(p__setGlfwErrorCallback_0_);
      if (lvt_1_1_ != null) {
         lvt_1_1_.free();
      }

   }

   public static boolean _shouldClose(MainWindow p__shouldClose_0_) {
      return GLFW.glfwWindowShouldClose(p__shouldClose_0_.getHandle());
   }

   public static void _setupNvFogDistance() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (GL.getCapabilities().GL_NV_fog_distance) {
         GlStateManager.func_227742_m_(34138, 34139);
      }

   }

   public static void _init(int p__init_0_, boolean p__init_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLCapabilities lvt_2_1_ = GL.getCapabilities();
      capsString = "Using framebuffer using " + GlStateManager.func_227666_a_(lvt_2_1_);

      try {
         Processor[] lvt_3_1_ = (new SystemInfo()).getHardware().getProcessors();
         cpuInfo = String.format("%dx %s", lvt_3_1_.length, lvt_3_1_[0]).replaceAll("\\s+", " ");
      } catch (Throwable var4) {
      }

      GlDebugTextUtils.setDebugVerbosity(p__init_0_, p__init_1_);
   }

   public static String _getCapsString() {
      return capsString;
   }

   public static String _getCpuInfo() {
      return cpuInfo == null ? "<unknown>" : cpuInfo;
   }

   public static void _renderCrosshair(int p__renderCrosshair_0_, boolean p__renderCrosshair_1_, boolean p__renderCrosshair_2_, boolean p__renderCrosshair_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager.func_227621_I_();
      GlStateManager.func_227667_a_(false);
      Tessellator lvt_4_1_ = RenderSystem.renderThreadTesselator();
      BufferBuilder lvt_5_1_ = lvt_4_1_.getBuffer();
      GL11.glLineWidth(4.0F);
      lvt_5_1_.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (p__renderCrosshair_1_) {
         lvt_5_1_.func_225582_a_(0.0D, 0.0D, 0.0D).func_225586_a_(0, 0, 0, 255).endVertex();
         lvt_5_1_.func_225582_a_((double)p__renderCrosshair_0_, 0.0D, 0.0D).func_225586_a_(0, 0, 0, 255).endVertex();
      }

      if (p__renderCrosshair_2_) {
         lvt_5_1_.func_225582_a_(0.0D, 0.0D, 0.0D).func_225586_a_(0, 0, 0, 255).endVertex();
         lvt_5_1_.func_225582_a_(0.0D, (double)p__renderCrosshair_0_, 0.0D).func_225586_a_(0, 0, 0, 255).endVertex();
      }

      if (p__renderCrosshair_3_) {
         lvt_5_1_.func_225582_a_(0.0D, 0.0D, 0.0D).func_225586_a_(0, 0, 0, 255).endVertex();
         lvt_5_1_.func_225582_a_(0.0D, 0.0D, (double)p__renderCrosshair_0_).func_225586_a_(0, 0, 0, 255).endVertex();
      }

      lvt_4_1_.draw();
      GL11.glLineWidth(2.0F);
      lvt_5_1_.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (p__renderCrosshair_1_) {
         lvt_5_1_.func_225582_a_(0.0D, 0.0D, 0.0D).func_225586_a_(255, 0, 0, 255).endVertex();
         lvt_5_1_.func_225582_a_((double)p__renderCrosshair_0_, 0.0D, 0.0D).func_225586_a_(255, 0, 0, 255).endVertex();
      }

      if (p__renderCrosshair_2_) {
         lvt_5_1_.func_225582_a_(0.0D, 0.0D, 0.0D).func_225586_a_(0, 255, 0, 255).endVertex();
         lvt_5_1_.func_225582_a_(0.0D, (double)p__renderCrosshair_0_, 0.0D).func_225586_a_(0, 255, 0, 255).endVertex();
      }

      if (p__renderCrosshair_3_) {
         lvt_5_1_.func_225582_a_(0.0D, 0.0D, 0.0D).func_225586_a_(127, 127, 255, 255).endVertex();
         lvt_5_1_.func_225582_a_(0.0D, 0.0D, (double)p__renderCrosshair_0_).func_225586_a_(127, 127, 255, 255).endVertex();
      }

      lvt_4_1_.draw();
      GL11.glLineWidth(1.0F);
      GlStateManager.func_227667_a_(true);
      GlStateManager.func_227619_H_();
   }

   public static String getErrorString(int p_getErrorString_0_) {
      return (String)LOOKUP_MAP.get(p_getErrorString_0_);
   }

   public static <T> T make(Supplier<T> p_make_0_) {
      return p_make_0_.get();
   }

   public static <T> T make(T p_make_0_, Consumer<T> p_make_1_) {
      p_make_1_.accept(p_make_0_);
      return p_make_0_;
   }
}
