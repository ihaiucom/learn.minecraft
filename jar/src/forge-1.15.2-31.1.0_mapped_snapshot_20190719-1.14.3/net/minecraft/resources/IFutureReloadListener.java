package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;

public interface IFutureReloadListener {
   CompletableFuture<Void> reload(IFutureReloadListener.IStage var1, IResourceManager var2, IProfiler var3, IProfiler var4, Executor var5, Executor var6);

   default String func_225594_i_() {
      return this.getClass().getSimpleName();
   }

   public interface IStage {
      <T> CompletableFuture<T> markCompleteAwaitingOthers(T var1);
   }
}
