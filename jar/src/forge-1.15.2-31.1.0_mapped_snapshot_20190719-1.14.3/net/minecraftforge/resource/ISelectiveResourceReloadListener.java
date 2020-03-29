package net.minecraftforge.resource;

import java.util.function.Predicate;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

public interface ISelectiveResourceReloadListener extends IResourceManagerReloadListener {
   default void onResourceManagerReload(IResourceManager resourceManager) {
      this.onResourceManagerReload(resourceManager, SelectiveReloadStateHandler.INSTANCE.get());
   }

   void onResourceManagerReload(IResourceManager var1, Predicate<IResourceType> var2);
}
