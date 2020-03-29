package net.minecraft.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IReloadableResourceManager extends IResourceManager {
   CompletableFuture<Unit> reloadResourcesAndThen(Executor var1, Executor var2, List<IResourcePack> var3, CompletableFuture<Unit> var4);

   @OnlyIn(Dist.CLIENT)
   IAsyncReloader reloadResources(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<IResourcePack> var4);

   void addReloadListener(IFutureReloadListener var1);
}
