package net.minecraftforge.fml.relauncher.libraries;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnapshotJson implements Comparable<SnapshotJson> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DateFormat TIMESTAMP = new SimpleDateFormat("yyyyMMdd.hhmmss");
   public static final String META_JSON_FILE = "maven-metadata.json";
   private static final Gson GSON = (new GsonBuilder()).create();
   private static final Comparator<SnapshotJson.Entry> SORTER = (o1, o2) -> {
      return o2.timestamp.compareTo(o1.timestamp);
   };
   private String latest;
   private List<SnapshotJson.Entry> versions;

   public static SnapshotJson create(File target) {
      if (!target.exists()) {
         return new SnapshotJson();
      } else {
         try {
            String json = Files.asCharSource(target, StandardCharsets.UTF_8).read();
            SnapshotJson obj = (SnapshotJson)GSON.fromJson(json, SnapshotJson.class);
            obj.updateLatest();
            return obj;
         } catch (JsonSyntaxException var3) {
            LOGGER.info("Failed to parse snapshot json file " + target + ".", var3);
         } catch (IOException var4) {
            LOGGER.info("Failed to read snapshot json file " + target + ".", var4);
         }

         return new SnapshotJson();
      }
   }

   public String getLatest() {
      return this.latest;
   }

   public void add(SnapshotJson.Entry data) {
      if (this.versions == null) {
         this.versions = new ArrayList();
      }

      this.versions.add(data);
      this.updateLatest();
   }

   public void merge(SnapshotJson o) {
      if (o.versions != null) {
         if (this.versions == null) {
            this.versions = new ArrayList(o.versions);
         } else {
            o.versions.stream().filter((e) -> {
               return this.versions.stream().anyMatch((e2) -> {
                  return e.timestamp.equals(e2.timestamp);
               });
            }).forEach((e) -> {
               this.versions.add(e);
            });
         }

         this.updateLatest();
      }

   }

   public boolean remove(String timestamp) {
      if (this.versions == null) {
         return false;
      } else {
         if (this.versions.removeIf((e) -> {
            return e.timestamp.equals(timestamp);
         })) {
            this.updateLatest();
         }

         return false;
      }
   }

   public String updateLatest() {
      if (this.versions == null) {
         this.latest = null;
         return null;
      } else {
         Collections.sort(this.versions, SORTER);
         return this.latest = this.versions.isEmpty() ? null : ((SnapshotJson.Entry)this.versions.get(0)).timestamp;
      }
   }

   public void write(File target) throws IOException {
      Files.write(GSON.toJson(this), target, StandardCharsets.UTF_8);
   }

   public int compareTo(SnapshotJson o) {
      return o == null ? 1 : (o.latest == null ? (this.latest == null ? 0 : 1) : (this.latest == null ? -1 : o.latest.compareTo(this.latest)));
   }

   public static class Entry {
      private String timestamp;
      private String md5;

      public Entry(String timestamp, String md5) {
         this.timestamp = timestamp;
         this.md5 = md5;
      }

      public String getTimestamp() {
         return this.timestamp;
      }

      public String getMd5() {
         return this.md5;
      }
   }
}
