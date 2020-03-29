package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GLAllocation {
   public static synchronized ByteBuffer createDirectByteBuffer(int p_74524_0_) {
      return ByteBuffer.allocateDirect(p_74524_0_).order(ByteOrder.nativeOrder());
   }

   public static FloatBuffer createDirectFloatBuffer(int p_74529_0_) {
      return createDirectByteBuffer(p_74529_0_ << 2).asFloatBuffer();
   }
}
