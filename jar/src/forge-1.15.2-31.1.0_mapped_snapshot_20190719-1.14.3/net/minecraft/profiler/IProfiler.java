package net.minecraft.profiler;

import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProfiler {
   void startTick();

   void endTick();

   void startSection(String var1);

   void startSection(Supplier<String> var1);

   void endSection();

   void endStartSection(String var1);

   @OnlyIn(Dist.CLIENT)
   void endStartSection(Supplier<String> var1);

   void func_230035_c_(String var1);

   void func_230036_c_(Supplier<String> var1);
}
