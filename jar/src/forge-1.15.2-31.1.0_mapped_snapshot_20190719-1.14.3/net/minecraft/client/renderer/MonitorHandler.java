package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Monitor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;

@OnlyIn(Dist.CLIENT)
public class MonitorHandler {
   private final Long2ObjectMap<Monitor> field_216517_a = new Long2ObjectOpenHashMap();
   private final IMonitorFactory field_216520_d;

   public MonitorHandler(IMonitorFactory p_i51171_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      this.field_216520_d = p_i51171_1_;
      GLFW.glfwSetMonitorCallback(this::func_216516_a);
      PointerBuffer lvt_2_1_ = GLFW.glfwGetMonitors();
      if (lvt_2_1_ != null) {
         for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.limit(); ++lvt_3_1_) {
            long lvt_4_1_ = lvt_2_1_.get(lvt_3_1_);
            this.field_216517_a.put(lvt_4_1_, p_i51171_1_.createMonitor(lvt_4_1_));
         }
      }

   }

   private void func_216516_a(long p_216516_1_, int p_216516_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_216516_3_ == 262145) {
         this.field_216517_a.put(p_216516_1_, this.field_216520_d.createMonitor(p_216516_1_));
      } else if (p_216516_3_ == 262146) {
         this.field_216517_a.remove(p_216516_1_);
      }

   }

   @Nullable
   public Monitor func_216512_a(long p_216512_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return (Monitor)this.field_216517_a.get(p_216512_1_);
   }

   @Nullable
   public Monitor func_216515_a(MainWindow p_216515_1_) {
      long lvt_2_1_ = GLFW.glfwGetWindowMonitor(p_216515_1_.getHandle());
      if (lvt_2_1_ != 0L) {
         return this.func_216512_a(lvt_2_1_);
      } else {
         int lvt_4_1_ = p_216515_1_.getWindowX();
         int lvt_5_1_ = lvt_4_1_ + p_216515_1_.getWidth();
         int lvt_6_1_ = p_216515_1_.getWindowY();
         int lvt_7_1_ = lvt_6_1_ + p_216515_1_.getHeight();
         int lvt_8_1_ = -1;
         Monitor lvt_9_1_ = null;
         ObjectIterator var10 = this.field_216517_a.values().iterator();

         while(var10.hasNext()) {
            Monitor lvt_11_1_ = (Monitor)var10.next();
            int lvt_12_1_ = lvt_11_1_.getVirtualPosX();
            int lvt_13_1_ = lvt_12_1_ + lvt_11_1_.getDefaultVideoMode().getWidth();
            int lvt_14_1_ = lvt_11_1_.getVirtualPosY();
            int lvt_15_1_ = lvt_14_1_ + lvt_11_1_.getDefaultVideoMode().getHeight();
            int lvt_16_1_ = func_216513_a(lvt_4_1_, lvt_12_1_, lvt_13_1_);
            int lvt_17_1_ = func_216513_a(lvt_5_1_, lvt_12_1_, lvt_13_1_);
            int lvt_18_1_ = func_216513_a(lvt_6_1_, lvt_14_1_, lvt_15_1_);
            int lvt_19_1_ = func_216513_a(lvt_7_1_, lvt_14_1_, lvt_15_1_);
            int lvt_20_1_ = Math.max(0, lvt_17_1_ - lvt_16_1_);
            int lvt_21_1_ = Math.max(0, lvt_19_1_ - lvt_18_1_);
            int lvt_22_1_ = lvt_20_1_ * lvt_21_1_;
            if (lvt_22_1_ > lvt_8_1_) {
               lvt_9_1_ = lvt_11_1_;
               lvt_8_1_ = lvt_22_1_;
            }
         }

         return lvt_9_1_;
      }
   }

   public static int func_216513_a(int p_216513_0_, int p_216513_1_, int p_216513_2_) {
      if (p_216513_0_ < p_216513_1_) {
         return p_216513_1_;
      } else {
         return p_216513_0_ > p_216513_2_ ? p_216513_2_ : p_216513_0_;
      }
   }

   public void func_216514_a() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GLFWMonitorCallback lvt_1_1_ = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);
      if (lvt_1_1_ != null) {
         lvt_1_1_.free();
      }

   }
}
