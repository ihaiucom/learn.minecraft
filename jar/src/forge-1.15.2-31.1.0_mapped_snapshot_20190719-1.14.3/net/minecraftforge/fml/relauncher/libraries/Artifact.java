package net.minecraftforge.fml.relauncher.libraries;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import org.apache.maven.artifact.versioning.ComparableVersion;

public class Artifact implements Comparable<Artifact> {
   private final Repository repo;
   private final String group;
   private final String artifact;
   private final String classifier;
   private final String extension;
   private final String value;
   private final ComparableVersion version;
   private final String timestamp;
   private final Date date;
   private String filename;
   private String folder;

   public Artifact(Repository repo, String value, String timestamp) {
      this.repo = repo;
      this.value = value;
      int idx = value.indexOf(64);
      if (idx > 0) {
         this.extension = value.substring(idx + 1);
         value = value.substring(0, idx);
      } else {
         this.extension = "jar";
      }

      String[] parts = value.split(":");
      this.group = parts[0];
      this.artifact = parts[1];
      this.version = new ComparableVersion(parts[2]);
      this.classifier = parts.length > 3 ? parts[3] : null;
      this.timestamp = this.isSnapshot() ? timestamp : null;

      try {
         this.date = this.timestamp == null ? null : SnapshotJson.TIMESTAMP.parse(this.timestamp);
      } catch (ParseException var7) {
         throw new RuntimeException(var7);
      }
   }

   public Artifact(Artifact other, Repository repo, String timestamp) {
      this.repo = repo;
      this.group = other.group;
      this.artifact = other.artifact;
      this.classifier = other.classifier;
      this.extension = other.extension;
      this.value = other.value;
      this.version = other.version;
      this.timestamp = timestamp;

      try {
         this.date = this.timestamp == null ? null : SnapshotJson.TIMESTAMP.parse(this.timestamp);
      } catch (ParseException var5) {
         throw new RuntimeException(var5);
      }
   }

   public String toString() {
      return this.value;
   }

   public int hashCode() {
      return this.value.hashCode();
   }

   public String getFilename() {
      if (this.filename == null) {
         StringBuilder sb = new StringBuilder();
         sb.append(this.artifact).append('-').append(this.version);
         if (this.isSnapshot() && this.timestamp != null) {
            sb.append('-').append(this.getTimestamp());
         }

         if (this.classifier != null) {
            sb.append('-').append(this.classifier);
         }

         sb.append('.').append(this.extension);
         this.filename = sb.toString();
      }

      return this.filename;
   }

   public String getFolder() {
      if (this.folder == null) {
         StringBuilder sb = new StringBuilder();
         String[] var2 = this.group.split("\\.");
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String part = var2[var4];
            sb.append(part).append(File.separatorChar);
         }

         sb.append(this.artifact).append(File.separatorChar);
         sb.append(this.version);
         this.folder = sb.toString();
      }

      return this.folder;
   }

   public String getPath() {
      return this.getFolder() + File.separatorChar + this.getFilename();
   }

   public File getFile() {
      return (this.repo != null ? this.repo : LibraryManager.getDefaultRepo()).getFile(this.getPath());
   }

   public File getSnapshotMeta() {
      if (!this.isSnapshot()) {
         throw new IllegalStateException("Attempted to call date suffix on non-snapshot");
      } else {
         return (this.repo != null ? this.repo : LibraryManager.getDefaultRepo()).getFile(this.getFolder() + File.separatorChar + "maven-metadata.json");
      }
   }

   public boolean isSnapshot() {
      return this.version.toString().toLowerCase(Locale.ENGLISH).endsWith("-snapshot");
   }

   public String getTimestamp() {
      if (!this.isSnapshot()) {
         throw new IllegalStateException("Attempted to call date suffix on non-snapshot");
      } else {
         return this.timestamp;
      }
   }

   public ComparableVersion getVersion() {
      return this.version;
   }

   public Repository getRepository() {
      return this.repo;
   }

   public boolean matchesID(Artifact o) {
      if (o == null) {
         return false;
      } else {
         boolean var10000;
         label33: {
            if (this.group.equals(o.group) && this.artifact.equals(o.artifact)) {
               if (o.classifier == null) {
                  if (this.classifier == null) {
                     break label33;
                  }
               } else if (o.classifier.equals(this.classifier)) {
                  break label33;
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   public int compareVersion(Artifact o) {
      int ver = this.version.compareTo(o.version);
      if (ver == 0 && this.isSnapshot()) {
         return this.timestamp == null ? (o.timestamp == null ? 0 : -1) : (o.timestamp == null ? 1 : this.date.compareTo(o.date));
      } else {
         return ver;
      }
   }

   public int compareTo(Artifact o) {
      if (o == null) {
         return 1;
      } else if (!this.group.equals(o.group)) {
         return this.group.compareTo(o.group);
      } else if (!this.artifact.equals(o.artifact)) {
         return this.artifact.compareTo(o.artifact);
      } else if (this.classifier == null && o.classifier != null) {
         return -1;
      } else if (this.classifier != null && o.classifier == null) {
         return 1;
      } else {
         return this.classifier != null && !this.classifier.equals(o.classifier) ? this.classifier.compareTo(o.classifier) : this.compareVersion(o);
      }
   }
}
