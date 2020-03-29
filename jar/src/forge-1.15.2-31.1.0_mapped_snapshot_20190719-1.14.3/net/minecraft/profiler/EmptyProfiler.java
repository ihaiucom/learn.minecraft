package net.minecraft.profiler;

import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmptyProfiler implements IResultableProfiler {
   public static final EmptyProfiler INSTANCE = new EmptyProfiler();

   private EmptyProfiler() {
   }

   public void startTick() {
   }

   public void endTick() {
   }

   public void startSection(String p_76320_1_) {
   }

   public void startSection(Supplier<String> p_194340_1_) {
   }

   public void endSection() {
   }

   public void endStartSection(String p_219895_1_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void endStartSection(Supplier<String> p_194339_1_) {
   }

   public void func_230035_c_(String p_230035_1_) {
   }

   public void func_230036_c_(Supplier<String> p_230036_1_) {
   }

   public IProfileResult getResults() {
      return EmptyProfileResult.field_219926_a;
   }
}
