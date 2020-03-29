package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack implements IResourcePack {
   public static Path basePath;
   private static final Logger LOGGER = LogManager.getLogger();
   public static Class<?> baseClass;
   private static final Map<ResourcePackType, FileSystem> field_217810_e = (Map)Util.make(Maps.newHashMap(), (p_lambda$static$0_0_) -> {
      Class var1 = VanillaPack.class;
      synchronized(VanillaPack.class) {
         ResourcePackType[] var2 = ResourcePackType.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ResourcePackType resourcepacktype = var2[var4];
            URL url = VanillaPack.class.getResource("/" + resourcepacktype.getDirectoryName() + "/.mcassetsroot");

            try {
               URI uri = url.toURI();
               if ("jar".equals(uri.getScheme())) {
                  FileSystem filesystem;
                  try {
                     filesystem = FileSystems.getFileSystem(uri);
                  } catch (FileSystemNotFoundException var11) {
                     filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                  }

                  p_lambda$static$0_0_.put(resourcepacktype, filesystem);
               }
            } catch (URISyntaxException | IOException var12) {
               LOGGER.error("Couldn't get a list of all vanilla resources", var12);
            }
         }

      }
   });
   public final Set<String> resourceNamespaces;

   public VanillaPack(String... p_i47912_1_) {
      this.resourceNamespaces = ImmutableSet.copyOf(p_i47912_1_);
   }

   public InputStream getRootResourceStream(String p_195763_1_) throws IOException {
      if (!p_195763_1_.contains("/") && !p_195763_1_.contains("\\")) {
         if (basePath != null) {
            Path path = basePath.resolve(p_195763_1_);
            if (Files.exists(path, new LinkOption[0])) {
               return Files.newInputStream(path);
            }
         }

         return this.getInputStreamVanilla(p_195763_1_);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   public InputStream getResourceStream(ResourcePackType p_195761_1_, ResourceLocation p_195761_2_) throws IOException {
      InputStream inputstream = this.getInputStreamVanilla(p_195761_1_, p_195761_2_);
      if (inputstream != null) {
         return inputstream;
      } else {
         throw new FileNotFoundException(p_195761_2_.getPath());
      }
   }

   public Collection<ResourceLocation> func_225637_a_(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_) {
      Set<ResourceLocation> set = Sets.newHashSet();
      URI uri;
      if (basePath != null) {
         try {
            func_229867_a_(set, p_225637_4_, p_225637_2_, basePath.resolve(p_225637_1_.getDirectoryName()), p_225637_3_, p_225637_5_);
         } catch (IOException var15) {
         }

         if (p_225637_1_ == ResourcePackType.CLIENT_RESOURCES) {
            Enumeration enumeration = null;

            try {
               enumeration = baseClass.getClassLoader().getResources(p_225637_1_.getDirectoryName() + "/");
            } catch (IOException var14) {
            }

            while(enumeration != null && enumeration.hasMoreElements()) {
               try {
                  uri = ((URL)enumeration.nextElement()).toURI();
                  if ("file".equals(uri.getScheme())) {
                     func_229867_a_(set, p_225637_4_, p_225637_2_, Paths.get(uri), p_225637_3_, p_225637_5_);
                  }
               } catch (URISyntaxException | IOException var13) {
               }
            }
         }
      }

      try {
         URL url1 = VanillaPack.class.getResource("/" + p_225637_1_.getDirectoryName() + "/.mcassetsroot");
         if (url1 == null) {
            LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
            return set;
         }

         uri = url1.toURI();
         if ("file".equals(uri.getScheme())) {
            URL url = new URL(url1.toString().substring(0, url1.toString().length() - ".mcassetsroot".length()));
            Path path = Paths.get(url.toURI());
            func_229867_a_(set, p_225637_4_, p_225637_2_, path, p_225637_3_, p_225637_5_);
         } else if ("jar".equals(uri.getScheme())) {
            Path path1 = ((FileSystem)field_217810_e.get(p_225637_1_)).getPath("/" + p_225637_1_.getDirectoryName());
            func_229867_a_(set, p_225637_4_, "minecraft", path1, p_225637_3_, p_225637_5_);
         } else {
            LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", uri);
         }
      } catch (FileNotFoundException | NoSuchFileException var11) {
      } catch (URISyntaxException | IOException var12) {
         LOGGER.error("Couldn't get a list of all vanilla resources", var12);
      }

      return set;
   }

   private static void func_229867_a_(Collection<ResourceLocation> p_229867_0_, int p_229867_1_, String p_229867_2_, Path p_229867_3_, String p_229867_4_, Predicate<String> p_229867_5_) throws IOException {
      Path path = p_229867_3_.resolve(p_229867_2_);
      Stream<Path> stream = Files.walk(path.resolve(p_229867_4_), p_229867_1_, new FileVisitOption[0]);
      Throwable var8 = null;

      try {
         stream.filter((p_lambda$func_229867_a_$1_1_) -> {
            return !p_lambda$func_229867_a_$1_1_.endsWith(".mcmeta") && Files.isRegularFile(p_lambda$func_229867_a_$1_1_, new LinkOption[0]) && p_229867_5_.test(p_lambda$func_229867_a_$1_1_.getFileName().toString());
         }).map((p_lambda$func_229867_a_$2_2_) -> {
            return new ResourceLocation(p_229867_2_, path.relativize(p_lambda$func_229867_a_$2_2_).toString().replaceAll("\\\\", "/"));
         }).forEach(p_229867_0_::add);
      } catch (Throwable var17) {
         var8 = var17;
         throw var17;
      } finally {
         if (stream != null) {
            if (var8 != null) {
               try {
                  stream.close();
               } catch (Throwable var16) {
                  var8.addSuppressed(var16);
               }
            } else {
               stream.close();
            }
         }

      }

   }

   @Nullable
   protected InputStream getInputStreamVanilla(ResourcePackType p_195782_1_, ResourceLocation p_195782_2_) {
      String s = func_223458_d(p_195782_1_, p_195782_2_);
      if (basePath != null) {
         Path path = basePath.resolve(p_195782_1_.getDirectoryName() + "/" + p_195782_2_.getNamespace() + "/" + p_195782_2_.getPath());
         if (Files.exists(path, new LinkOption[0])) {
            try {
               return Files.newInputStream(path);
            } catch (IOException var7) {
            }
         }
      }

      try {
         URL url = VanillaPack.class.getResource(s);
         return func_223459_a(s, url) ? this.getExtraInputStream(p_195782_1_, s) : null;
      } catch (IOException var6) {
         return VanillaPack.class.getResourceAsStream(s);
      }
   }

   private static String func_223458_d(ResourcePackType p_223458_0_, ResourceLocation p_223458_1_) {
      return "/" + p_223458_0_.getDirectoryName() + "/" + p_223458_1_.getNamespace() + "/" + p_223458_1_.getPath();
   }

   private static boolean func_223459_a(String p_223459_0_, @Nullable URL p_223459_1_) throws IOException {
      return p_223459_1_ != null && (p_223459_1_.getProtocol().equals("jar") || FolderPack.validatePath(new File(p_223459_1_.getFile()), p_223459_0_));
   }

   @Nullable
   protected InputStream getInputStreamVanilla(String p_200010_1_) {
      return this.getExtraInputStream(ResourcePackType.SERVER_DATA, "/" + p_200010_1_);
   }

   public boolean resourceExists(ResourcePackType p_195764_1_, ResourceLocation p_195764_2_) {
      String s = func_223458_d(p_195764_1_, p_195764_2_);
      if (basePath != null) {
         Path path = basePath.resolve(p_195764_1_.getDirectoryName() + "/" + p_195764_2_.getNamespace() + "/" + p_195764_2_.getPath());
         if (Files.exists(path, new LinkOption[0])) {
            return true;
         }
      }

      try {
         URL url = VanillaPack.class.getResource(s);
         return func_223459_a(s, url);
      } catch (IOException var5) {
         return false;
      }
   }

   public Set<String> getResourceNamespaces(ResourcePackType p_195759_1_) {
      return this.resourceNamespaces;
   }

   @Nullable
   public <T> T getMetadata(IMetadataSectionSerializer<T> p_195760_1_) throws IOException {
      try {
         InputStream inputstream = this.getRootResourceStream("pack.mcmeta");
         Throwable var3 = null;

         Object var5;
         try {
            Object object = ResourcePack.getResourceMetadata(p_195760_1_, inputstream);
            var5 = object;
         } catch (Throwable var15) {
            var3 = var15;
            throw var15;
         } finally {
            if (inputstream != null) {
               if (var3 != null) {
                  try {
                     inputstream.close();
                  } catch (Throwable var14) {
                     var3.addSuppressed(var14);
                  }
               } else {
                  inputstream.close();
               }
            }

         }

         return var5;
      } catch (RuntimeException | FileNotFoundException var17) {
         return null;
      }
   }

   public String getName() {
      return "Default";
   }

   public void close() {
   }

   private InputStream getExtraInputStream(ResourcePackType p_getExtraInputStream_1_, String p_getExtraInputStream_2_) {
      try {
         FileSystem fs = (FileSystem)field_217810_e.get(p_getExtraInputStream_1_);
         return fs != null ? Files.newInputStream(fs.getPath(p_getExtraInputStream_2_)) : VanillaPack.class.getResourceAsStream(p_getExtraInputStream_2_);
      } catch (IOException var4) {
         return VanillaPack.class.getResourceAsStream(p_getExtraInputStream_2_);
      }
   }
}
