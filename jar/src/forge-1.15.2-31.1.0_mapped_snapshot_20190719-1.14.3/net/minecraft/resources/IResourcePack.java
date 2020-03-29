package net.minecraft.resources;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeResourcePack;

public interface IResourcePack extends Closeable, IForgeResourcePack {
   @OnlyIn(Dist.CLIENT)
   InputStream getRootResourceStream(String var1) throws IOException;

   InputStream getResourceStream(ResourcePackType var1, ResourceLocation var2) throws IOException;

   Collection<ResourceLocation> func_225637_a_(ResourcePackType var1, String var2, String var3, int var4, Predicate<String> var5);

   boolean resourceExists(ResourcePackType var1, ResourceLocation var2);

   Set<String> getResourceNamespaces(ResourcePackType var1);

   @Nullable
   <T> T getMetadata(IMetadataSectionSerializer<T> var1) throws IOException;

   String getName();
}
