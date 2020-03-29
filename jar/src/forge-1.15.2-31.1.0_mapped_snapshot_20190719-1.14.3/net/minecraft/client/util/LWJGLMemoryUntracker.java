package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.Pointer;

@OnlyIn(Dist.CLIENT)
public class LWJGLMemoryUntracker {
   @Nullable
   private static final MethodHandle HANDLE = (MethodHandle)GLX.make(() -> {
      try {
         Lookup lvt_0_1_ = MethodHandles.lookup();
         Class<?> lvt_1_1_ = Class.forName("org.lwjgl.system.MemoryManage$DebugAllocator");
         Method lvt_2_1_ = lvt_1_1_.getDeclaredMethod("untrack", Long.TYPE);
         lvt_2_1_.setAccessible(true);
         Field lvt_3_1_ = Class.forName("org.lwjgl.system.MemoryUtil$LazyInit").getDeclaredField("ALLOCATOR");
         lvt_3_1_.setAccessible(true);
         Object lvt_4_1_ = lvt_3_1_.get((Object)null);
         return lvt_1_1_.isInstance(lvt_4_1_) ? lvt_0_1_.unreflect(lvt_2_1_) : null;
      } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException var5) {
         throw new RuntimeException(var5);
      }
   });

   public static void untrack(long p_197933_0_) {
      if (HANDLE != null) {
         try {
            HANDLE.invoke(p_197933_0_);
         } catch (Throwable var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public static void untrack(Pointer p_211545_0_) {
      untrack(p_211545_0_.address());
   }
}
