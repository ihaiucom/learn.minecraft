package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Unit;
import net.minecraftforge.resource.IResourceType;

/** @deprecated */
@Deprecated
public interface IResourceManagerReloadListener extends IFutureReloadListener {
   default CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      return p_215226_1_.markCompleteAwaitingOthers(Unit.INSTANCE).thenRunAsync(() -> {
         this.onResourceManagerReload(p_215226_2_);
      }, p_215226_6_);
   }

   void onResourceManagerReload(IResourceManager var1);

   @Nullable
   default IResourceType getResourceType() {
      return null;
   }
}
