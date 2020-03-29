package net.minecraftforge.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

public interface IModelLoader<T extends IModelGeometry<T>> extends ISelectiveResourceReloadListener {
   default IResourceType getResourceType() {
      return VanillaResourceType.MODELS;
   }

   void onResourceManagerReload(IResourceManager var1);

   default void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
      if (resourcePredicate.test(this.getResourceType())) {
         this.onResourceManagerReload(resourceManager);
      }

   }

   T read(JsonDeserializationContext var1, JsonObject var2);
}
