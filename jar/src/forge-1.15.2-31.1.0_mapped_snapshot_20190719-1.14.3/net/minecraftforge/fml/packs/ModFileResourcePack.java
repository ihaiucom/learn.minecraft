package net.minecraftforge.fml.packs;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

public class ModFileResourcePack extends DelegatableResourcePack {
   private final ModFile modFile;
   private ResourcePackInfo packInfo;

   public ModFileResourcePack(ModFile modFile) {
      super(new File("dummy"));
      this.modFile = modFile;
   }

   public ModFile getModFile() {
      return this.modFile;
   }

   public String getName() {
      return this.modFile.getFileName();
   }

   public InputStream getInputStream(String name) throws IOException {
      Path path = this.modFile.getLocator().findPath(this.modFile, new String[]{name});
      return Files.newInputStream(path, StandardOpenOption.READ);
   }

   public boolean resourceExists(String name) {
      return Files.exists(this.modFile.getLocator().findPath(this.modFile, new String[]{name}), new LinkOption[0]);
   }

   public Collection<ResourceLocation> func_225637_a_(ResourcePackType type, String resourceNamespace, String pathIn, int maxDepth, Predicate<String> filter) {
      try {
         Path root = this.modFile.getLocator().findPath(this.modFile, new String[]{type.getDirectoryName()}).toAbsolutePath();
         Path inputPath = root.getFileSystem().getPath(pathIn);
         return (Collection)Files.walk(root).map((path) -> {
            return root.relativize(path.toAbsolutePath());
         }).filter((path) -> {
            return path.getNameCount() > 1 && path.getNameCount() - 1 <= maxDepth;
         }).filter((path) -> {
            return !path.toString().endsWith(".mcmeta");
         }).filter((path) -> {
            return path.subpath(1, path.getNameCount()).startsWith(inputPath);
         }).filter((path) -> {
            return filter.test(path.getFileName().toString());
         }).map((path) -> {
            return new ResourceLocation(path.getName(0).toString(), Joiner.on('/').join(path.subpath(1, Math.min(maxDepth, path.getNameCount()))));
         }).collect(Collectors.toList());
      } catch (IOException var8) {
         return Collections.emptyList();
      }
   }

   public Set<String> getResourceNamespaces(ResourcePackType type) {
      try {
         Path root = this.modFile.getLocator().findPath(this.modFile, new String[]{type.getDirectoryName()}).toAbsolutePath();
         return (Set)Files.walk(root, 1, new FileVisitOption[0]).map((path) -> {
            return root.relativize(path.toAbsolutePath());
         }).filter((path) -> {
            return path.getNameCount() > 0;
         }).map((p) -> {
            return p.toString().replaceAll("/$", "");
         }).filter((s) -> {
            return !s.isEmpty();
         }).collect(Collectors.toSet());
      } catch (IOException var3) {
         return Collections.emptySet();
      }
   }

   public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
      return location.getPath().startsWith("lang/") ? super.getResourceStream(ResourcePackType.CLIENT_RESOURCES, location) : super.getResourceStream(type, location);
   }

   public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
      return location.getPath().startsWith("lang/") ? super.resourceExists(ResourcePackType.CLIENT_RESOURCES, location) : super.resourceExists(type, location);
   }

   public void close() throws IOException {
   }

   <T extends ResourcePackInfo> void setPackInfo(T packInfo) {
      this.packInfo = packInfo;
   }

   <T extends ResourcePackInfo> T getPackInfo() {
      return this.packInfo;
   }
}
