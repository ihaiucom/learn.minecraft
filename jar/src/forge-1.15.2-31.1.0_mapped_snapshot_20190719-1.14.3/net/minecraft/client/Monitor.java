package net.minecraft.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.VideoMode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@OnlyIn(Dist.CLIENT)
public final class Monitor {
   private final long monitorPointer;
   private final List<VideoMode> videoModes;
   private VideoMode defaultVideoMode;
   private int virtualPosX;
   private int virtualPosY;

   public Monitor(long p_i51795_1_) {
      this.monitorPointer = p_i51795_1_;
      this.videoModes = Lists.newArrayList();
      this.setup();
   }

   public void setup() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      this.videoModes.clear();
      Buffer lvt_1_1_ = GLFW.glfwGetVideoModes(this.monitorPointer);

      for(int lvt_2_1_ = lvt_1_1_.limit() - 1; lvt_2_1_ >= 0; --lvt_2_1_) {
         lvt_1_1_.position(lvt_2_1_);
         VideoMode lvt_3_1_ = new VideoMode(lvt_1_1_);
         if (lvt_3_1_.getRedBits() >= 8 && lvt_3_1_.getGreenBits() >= 8 && lvt_3_1_.getBlueBits() >= 8) {
            this.videoModes.add(lvt_3_1_);
         }
      }

      int[] lvt_2_2_ = new int[1];
      int[] lvt_3_2_ = new int[1];
      GLFW.glfwGetMonitorPos(this.monitorPointer, lvt_2_2_, lvt_3_2_);
      this.virtualPosX = lvt_2_2_[0];
      this.virtualPosY = lvt_3_2_[0];
      GLFWVidMode lvt_4_1_ = GLFW.glfwGetVideoMode(this.monitorPointer);
      this.defaultVideoMode = new VideoMode(lvt_4_1_);
   }

   public VideoMode getVideoModeOrDefault(Optional<VideoMode> p_197992_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      if (p_197992_1_.isPresent()) {
         VideoMode lvt_2_1_ = (VideoMode)p_197992_1_.get();
         Iterator var3 = this.videoModes.iterator();

         while(var3.hasNext()) {
            VideoMode lvt_4_1_ = (VideoMode)var3.next();
            if (lvt_4_1_.equals(lvt_2_1_)) {
               return lvt_4_1_;
            }
         }
      }

      return this.getDefaultVideoMode();
   }

   public int func_224794_a(VideoMode p_224794_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return this.videoModes.indexOf(p_224794_1_);
   }

   public VideoMode getDefaultVideoMode() {
      return this.defaultVideoMode;
   }

   public int getVirtualPosX() {
      return this.virtualPosX;
   }

   public int getVirtualPosY() {
      return this.virtualPosY;
   }

   public VideoMode getVideoModeFromIndex(int p_197991_1_) {
      return (VideoMode)this.videoModes.get(p_197991_1_);
   }

   public int getVideoModeCount() {
      return this.videoModes.size();
   }

   public long getMonitorPointer() {
      return this.monitorPointer;
   }

   public String toString() {
      return String.format("Monitor[%s %sx%s %s]", this.monitorPointer, this.virtualPosX, this.virtualPosY, this.defaultVideoMode);
   }
}
