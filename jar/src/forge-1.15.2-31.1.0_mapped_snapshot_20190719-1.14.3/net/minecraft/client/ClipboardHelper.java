package net.minecraft.client;

import com.google.common.base.Charsets;
import java.nio.ByteBuffer;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class ClipboardHelper {
   private final ByteBuffer field_216490_a = BufferUtils.createByteBuffer(8192);

   public String func_216487_a(long p_216487_1_, GLFWErrorCallbackI p_216487_3_) {
      GLFWErrorCallback lvt_4_1_ = GLFW.glfwSetErrorCallback(p_216487_3_);
      String lvt_5_1_ = GLFW.glfwGetClipboardString(p_216487_1_);
      lvt_5_1_ = lvt_5_1_ != null ? SharedConstants.func_215070_b(lvt_5_1_) : "";
      GLFWErrorCallback lvt_6_1_ = GLFW.glfwSetErrorCallback(lvt_4_1_);
      if (lvt_6_1_ != null) {
         lvt_6_1_.free();
      }

      return lvt_5_1_;
   }

   private static void func_230147_a_(long p_230147_0_, ByteBuffer p_230147_2_, byte[] p_230147_3_) {
      p_230147_2_.clear();
      p_230147_2_.put(p_230147_3_);
      p_230147_2_.put((byte)0);
      p_230147_2_.flip();
      GLFW.glfwSetClipboardString(p_230147_0_, p_230147_2_);
   }

   public void func_216489_a(long p_216489_1_, String p_216489_3_) {
      byte[] lvt_4_1_ = p_216489_3_.getBytes(Charsets.UTF_8);
      int lvt_5_1_ = lvt_4_1_.length + 1;
      if (lvt_5_1_ < this.field_216490_a.capacity()) {
         func_230147_a_(p_216489_1_, this.field_216490_a, lvt_4_1_);
      } else {
         ByteBuffer lvt_6_1_ = MemoryUtil.memAlloc(lvt_5_1_);

         try {
            func_230147_a_(p_216489_1_, lvt_6_1_, lvt_4_1_);
         } finally {
            MemoryUtil.memFree(lvt_6_1_);
         }
      }

   }
}
