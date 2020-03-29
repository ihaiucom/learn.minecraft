package net.minecraftforge.fml.relauncher.libraries;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LinkRepository extends Repository {
   private static final Logger LOGGER = LogManager.getLogger();
   private Map<String, File> artifact_to_file = new HashMap();
   private Map<String, File> filesystem = new HashMap();
   private Map<String, Artifact> snapshots = new HashMap();
   private Set<File> known = new HashSet();

   LinkRepository(File root) {
      super(root, "MEMORY");
   }

   public File archive(Artifact artifact, File file, byte[] manifest) {
      String key = artifact.toString();
      this.known.add(file);
      if (this.artifact_to_file.containsKey(key)) {
         LOGGER.debug("Maven file already exists for {}({}) at {}, ignoring duplicate.", file.getName(), artifact.toString(), ((File)this.artifact_to_file.get(key)).getAbsolutePath());
         if (artifact.isSnapshot()) {
            Artifact old = (Artifact)this.snapshots.get(key);
            if (old == null || old.compareVersion(artifact) < 0) {
               LOGGER.debug("Overriding Snapshot {} -> {}", old == null ? "null" : old.getTimestamp(), artifact.getTimestamp());
               this.snapshots.put(key, artifact);
               this.artifact_to_file.put(key, file);
               this.filesystem.put(artifact.getPath(), file);
            }
         }
      } else {
         LOGGER.debug("Making maven link for {} in memory to {}.", key, file.getAbsolutePath());
         this.artifact_to_file.put(key, file);
         this.filesystem.put(artifact.getPath(), file);
         if (artifact.isSnapshot()) {
            this.snapshots.put(key, artifact);
         }
      }

      return file;
   }

   public void filterLegacy(List<File> list) {
      list.removeIf((e) -> {
         return this.known.contains(e);
      });
   }

   public Artifact resolve(Artifact artifact) {
      String key = artifact.toString();
      File file = (File)this.artifact_to_file.get(key);
      return file != null && file.exists() ? new Artifact(artifact, this, artifact.isSnapshot() ? artifact.getTimestamp() : null) : super.resolve(artifact);
   }

   public File getFile(String path) {
      return this.filesystem.containsKey(path) ? (File)this.filesystem.get(path) : super.getFile(path);
   }
}
