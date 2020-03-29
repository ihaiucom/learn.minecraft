package net.minecraftforge.fml.packs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;

public class DelegatingResourcePack extends ResourcePack {
   private final List<DelegatableResourcePack> delegates;
   private final String name;
   private final PackMetadataSection packInfo;

   public DelegatingResourcePack(String id, String name, PackMetadataSection packInfo) {
      this(id, name, packInfo, Collections.emptyList());
   }

   public DelegatingResourcePack(String id, String name, PackMetadataSection packInfo, List<DelegatableResourcePack> packs) {
      super(new File(id));
      this.delegates = new ArrayList();
      this.name = name;
      this.packInfo = packInfo;
      packs.forEach(this::addDelegate);
   }

   public void addDelegate(DelegatableResourcePack pack) {
      synchronized(this.delegates) {
         this.delegates.add(pack);
      }
   }

   public String getName() {
      return this.name;
   }

   public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
      return deserializer.getSectionName().equals("pack") ? this.packInfo : null;
   }

   public Collection<ResourceLocation> func_225637_a_(ResourcePackType type, String pathIn, String pathIn2, int maxDepth, Predicate<String> filter) {
      synchronized(this.delegates) {
         return (Collection)this.delegates.stream().flatMap((r) -> {
            return r.func_225637_a_(type, pathIn, pathIn2, maxDepth, filter).stream();
         }).collect(Collectors.toList());
      }
   }

   public Set<String> getResourceNamespaces(ResourcePackType type) {
      synchronized(this.delegates) {
         return (Set)this.delegates.stream().flatMap((r) -> {
            return r.getResourceNamespaces(type).stream();
         }).collect(Collectors.toSet());
      }
   }

   public void close() throws IOException {
      synchronized(this.delegates) {
         Iterator var2 = this.delegates.iterator();

         while(var2.hasNext()) {
            ResourcePack pack = (ResourcePack)var2.next();
            pack.close();
         }

      }
   }

   protected InputStream getInputStream(String resourcePath) throws IOException {
      if (!resourcePath.equals("pack.png")) {
         synchronized(this.delegates) {
            Iterator var3 = this.delegates.iterator();

            while(var3.hasNext()) {
               DelegatableResourcePack pack = (DelegatableResourcePack)var3.next();
               if (pack.resourceExists(resourcePath)) {
                  return pack.getInputStream(resourcePath);
               }
            }
         }
      }

      throw new ResourcePackFileNotFoundException(this.file, resourcePath);
   }

   protected boolean resourceExists(String resourcePath) {
      synchronized(this.delegates) {
         Iterator var3 = this.delegates.iterator();

         DelegatableResourcePack pack;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            pack = (DelegatableResourcePack)var3.next();
         } while(!pack.resourceExists(resourcePath));

         return true;
      }
   }
}
