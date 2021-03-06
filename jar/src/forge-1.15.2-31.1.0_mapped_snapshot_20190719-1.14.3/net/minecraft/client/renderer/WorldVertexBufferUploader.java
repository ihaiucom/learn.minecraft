package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class WorldVertexBufferUploader {
   public static void draw(BufferBuilder p_181679_0_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            Pair<BufferBuilder.DrawState, ByteBuffer> lvt_1_1_ = p_181679_0_.func_227832_f_();
            BufferBuilder.DrawState lvt_2_1_ = (BufferBuilder.DrawState)lvt_1_1_.getFirst();
            func_227844_a_((ByteBuffer)lvt_1_1_.getSecond(), lvt_2_1_.func_227840_c_(), lvt_2_1_.func_227838_a_(), lvt_2_1_.func_227839_b_());
         });
      } else {
         Pair<BufferBuilder.DrawState, ByteBuffer> lvt_1_1_ = p_181679_0_.func_227832_f_();
         BufferBuilder.DrawState lvt_2_1_ = (BufferBuilder.DrawState)lvt_1_1_.getFirst();
         func_227844_a_((ByteBuffer)lvt_1_1_.getSecond(), lvt_2_1_.func_227840_c_(), lvt_2_1_.func_227838_a_(), lvt_2_1_.func_227839_b_());
      }

   }

   private static void func_227844_a_(ByteBuffer p_227844_0_, int p_227844_1_, VertexFormat p_227844_2_, int p_227844_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      p_227844_0_.clear();
      if (p_227844_3_ > 0) {
         p_227844_2_.func_227892_a_(MemoryUtil.memAddress(p_227844_0_));
         GlStateManager.func_227719_f_(p_227844_1_, 0, p_227844_3_);
         p_227844_2_.func_227895_d_();
      }
   }
}
