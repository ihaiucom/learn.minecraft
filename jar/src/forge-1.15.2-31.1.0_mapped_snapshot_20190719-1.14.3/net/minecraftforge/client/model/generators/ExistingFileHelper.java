package net.minecraftforge.client.model.generators;

import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;

public class ExistingFileHelper {
   private final SimpleReloadableResourceManager clientResources;
   private final SimpleReloadableResourceManager serverData;
   private final boolean enable;

   public ExistingFileHelper(Collection<Path> existingPacks, boolean enable) {
      this.clientResources = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES, Thread.currentThread());
      this.serverData = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA, Thread.currentThread());
      this.clientResources.addResourcePack(new VanillaPack(new String[]{"minecraft", "realms"}));
      this.serverData.addResourcePack(new VanillaPack(new String[]{"minecraft"}));
      Iterator var3 = existingPacks.iterator();

      while(var3.hasNext()) {
         Path existing = (Path)var3.next();
         File file = existing.toFile();
         IResourcePack pack = file.isDirectory() ? new FolderPack(file) : new FilePack(file);
         this.clientResources.addResourcePack((IResourcePack)pack);
         this.serverData.addResourcePack((IResourcePack)pack);
      }

      this.enable = enable;
   }

   private IResourceManager getManager(ResourcePackType type) {
      return type == ResourcePackType.CLIENT_RESOURCES ? this.clientResources : this.serverData;
   }

   private ResourceLocation getLocation(ResourceLocation base, String suffix, String prefix) {
      return new ResourceLocation(base.getNamespace(), prefix + "/" + base.getPath() + suffix);
   }

   public boolean exists(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) {
      return !this.enable ? true : this.getManager(type).hasResource(this.getLocation(loc, pathSuffix, pathPrefix));
   }

   @VisibleForTesting
   public IResource getResource(ResourceLocation loc, ResourcePackType type, String pathSuffix, String pathPrefix) throws IOException {
      return this.getManager(type).getResource(this.getLocation(loc, pathSuffix, pathPrefix));
   }

   public boolean isEnabled() {
      return this.enable;
   }
}
