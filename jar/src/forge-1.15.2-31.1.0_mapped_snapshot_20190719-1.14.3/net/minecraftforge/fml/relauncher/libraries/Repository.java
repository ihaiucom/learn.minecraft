package net.minecraftforge.fml.relauncher.libraries;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Repository {
   private static final Logger LOGGER = LogManager.getLogger();
   static final Map<String, Repository> cache = new LinkedHashMap();
   private final String name;
   private final File root;

   public static Repository create(File root) throws IOException {
      return create(root, root.getCanonicalPath());
   }

   public static Repository create(File root, String name) {
      return (Repository)cache.computeIfAbsent(name, (f) -> {
         return new Repository(root, name);
      });
   }

   public static Repository replace(File root, String name) {
      return (Repository)cache.put(name, new Repository(root, name));
   }

   public static Repository get(String name) {
      return (Repository)cache.get(name);
   }

   public static Artifact resolveAll(Artifact artifact) {
      Artifact ret = null;
      Iterator var2 = cache.values().iterator();

      while(true) {
         Artifact tmp;
         do {
            if (!var2.hasNext()) {
               return ret;
            }

            Repository repo = (Repository)var2.next();
            tmp = repo.resolve(artifact);
         } while(tmp == null);

         if (!artifact.isSnapshot()) {
            return tmp;
         }

         ret = ret != null && ret.compareTo(tmp) >= 0 ? ret : tmp;
      }
   }

   protected Repository(File root) throws IOException {
      this(root, root.getCanonicalPath());
   }

   protected Repository(File root, String name) {
      this.root = root;
      this.name = name;
      if (name == null) {
         throw new IllegalArgumentException("Invalid Repository Name, for " + root);
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public boolean equals(Object o) {
      return o instanceof Repository && ((Repository)o).name.equals(this.name);
   }

   public Artifact resolve(Artifact artifact) {
      if (!artifact.isSnapshot()) {
         return this.getFile(artifact.getPath()).exists() ? artifact : null;
      } else {
         File meta = this.getFile(artifact.getFolder() + File.separatorChar + "maven-metadata.json");
         if (!meta.exists()) {
            return null;
         } else {
            SnapshotJson json = SnapshotJson.create(this.getFile(artifact.getFolder() + File.separatorChar + "maven-metadata.json"));
            if (json.getLatest() == null) {
               return null;
            } else {
               Artifact ret;
               for(ret = new Artifact(artifact, this, json.getLatest()); json.getLatest() != null && !this.getFile(ret.getPath()).exists(); ret = new Artifact(artifact, this, json.getLatest())) {
                  if (!json.remove(json.getLatest())) {
                     throw new IllegalStateException("Something went wrong, Latest (" + json.getLatest() + ") did not point to an entry in the json list: " + meta.getAbsolutePath());
                  }
               }

               return this.getFile(ret.getPath()).exists() ? ret : null;
            }
         }
      }
   }

   public File getFile(String path) {
      return new File(this.root, path);
   }

   public File archive(Artifact artifact, File file, byte[] manifest) {
      File target = artifact.getFile();

      try {
         if (target.exists()) {
            LOGGER.debug("Maven file already exists for {}({}) at {}, deleting duplicate.", file.getName(), artifact.toString(), target.getAbsolutePath());
            file.delete();
         } else {
            LOGGER.debug("Moving file {}({}) to maven repo at {}.", file.getName(), artifact.toString(), target.getAbsolutePath());
            Files.move(file, target);
            if (artifact.isSnapshot()) {
               SnapshotJson json = SnapshotJson.create(artifact.getSnapshotMeta());
               json.add(new SnapshotJson.Entry(artifact.getTimestamp(), Files.hash(target, Hashing.md5()).toString()));
               json.write(artifact.getSnapshotMeta());
            }

            if (!LibraryManager.DISABLE_EXTERNAL_MANIFEST) {
               File meta_target = new File(target.getAbsolutePath() + ".meta");
               Files.write(manifest, meta_target);
            }
         }

         return target;
      } catch (IOException var6) {
         LOGGER.error(LOGGER.getMessageFactory().newMessage("Error moving file {} to {}", new Object[]{file, target.getAbsolutePath()}), var6);
         return file;
      }
   }

   public void filterLegacy(List<File> list) {
   }
}
