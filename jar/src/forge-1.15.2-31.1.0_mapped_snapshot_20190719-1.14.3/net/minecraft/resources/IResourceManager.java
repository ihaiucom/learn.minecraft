package net.minecraft.resources;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;

public interface IResourceManager {
   Set<String> getResourceNamespaces();

   IResource getResource(ResourceLocation var1) throws IOException;

   boolean hasResource(ResourceLocation var1);

   List<IResource> getAllResources(ResourceLocation var1) throws IOException;

   Collection<ResourceLocation> getAllResourceLocations(String var1, Predicate<String> var2);
}
