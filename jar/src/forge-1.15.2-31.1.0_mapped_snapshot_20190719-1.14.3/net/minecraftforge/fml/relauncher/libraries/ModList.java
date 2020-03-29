package net.minecraftforge.fml.relauncher.libraries;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModList {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setLenient().create();
   static final Map<String, ModList> cache = new HashMap();
   private final File path;
   private final ModList.JsonModList mod_list;
   private final Repository repo;
   private final ModList parent;
   private final List<Artifact> artifacts = new ArrayList();
   private final List<Artifact> artifacts_imm;
   private final Map<String, Artifact> art_map;
   private boolean changed;

   public static ModList create(File json, File mcdir) {
      try {
         String key = json.getCanonicalFile().getAbsolutePath();
         return (ModList)cache.computeIfAbsent(key, (k) -> {
            return new ModList(json, mcdir);
         });
      } catch (IOException var3) {
         LOGGER.error(LOGGER.getMessageFactory().newMessage("Unable to load ModList json at {}.", new Object[]{json.getAbsoluteFile()}), var3);
         return new ModList(json, mcdir);
      }
   }

   public static List<ModList> getKnownLists(File mcdir) {
      String[] var1 = new String[]{"mods/mod_list.json", "mods/" + MCPVersion.getMCVersion() + "/mod_list.json"};
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String list = var1[var3];
         if (list != null) {
            File listFile = getFile(mcdir, list);
            if (listFile != null && listFile.exists()) {
               create(listFile, mcdir);
            }
         }
      }

      return ImmutableList.copyOf(cache.values());
   }

   public static List<ModList> getBasicLists(File mcdir) {
      List<ModList> lst = new ArrayList();
      ModList memory = (ModList)cache.get("MEMORY");
      if (memory != null) {
         lst.add(memory);
      }

      String[] var3 = new String[]{"mods/mod_list.json", "mods/" + MCPVersion.getMCVersion() + "/mod_list.json"};
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String list = var3[var5];
         if (list != null) {
            File listFile = getFile(mcdir, list);
            if (listFile != null && listFile.exists()) {
               lst.add(create(listFile, mcdir));
            }
         }
      }

      return lst;
   }

   protected ModList(Repository repo) {
      this.artifacts_imm = Collections.unmodifiableList(this.artifacts);
      this.art_map = new HashMap();
      this.changed = false;
      this.path = null;
      this.mod_list = new ModList.JsonModList();
      this.repo = repo;
      this.parent = null;
   }

   private ModList(File path, File mcdir) {
      this.artifacts_imm = Collections.unmodifiableList(this.artifacts);
      this.art_map = new HashMap();
      this.changed = false;
      this.path = path;
      ModList.JsonModList temp_list = null;
      if (this.path.exists()) {
         try {
            String json = Files.asCharSource(path, StandardCharsets.UTF_8).read();
            temp_list = (ModList.JsonModList)GSON.fromJson(json, ModList.JsonModList.class);
         } catch (JsonSyntaxException var9) {
            LOGGER.info(LOGGER.getMessageFactory().newMessage("Failed to parse modList json file {}.", new Object[]{path}), var9);
         } catch (IOException var10) {
            LOGGER.info(LOGGER.getMessageFactory().newMessage("Failed to read modList json file {}.", new Object[]{path}), var10);
         }
      }

      this.mod_list = temp_list == null ? new ModList.JsonModList() : temp_list;
      Repository temp = null;
      File repoFile;
      if (this.mod_list.repositoryRoot != null) {
         try {
            repoFile = getFile(mcdir, this.mod_list.repositoryRoot);
            if (repoFile != null) {
               temp = Repository.create(repoFile);
            }
         } catch (IOException var8) {
            LOGGER.info(LOGGER.getMessageFactory().newMessage("Failed to create repository for modlist at {}.", new Object[]{this.mod_list.repositoryRoot}), var8);
         }
      }

      this.repo = temp;
      repoFile = this.mod_list.parentList == null ? null : getFile(mcdir, this.mod_list.parentList);
      this.parent = repoFile != null && repoFile.exists() ? create(repoFile, mcdir) : null;
      if (this.mod_list.modRef != null) {
         Iterator var6 = this.mod_list.modRef.iterator();

         while(var6.hasNext()) {
            String ref = (String)var6.next();
            this.add(new Artifact(this.getRepository(), ref, (String)null));
         }

         this.changed = false;
      }

   }

   public Repository getRepository() {
      return this.repo;
   }

   public void add(Artifact artifact) {
      Artifact old = (Artifact)this.art_map.get(artifact.toString());
      if (old != null) {
         this.artifacts.add(this.artifacts.indexOf(old), artifact);
         this.artifacts.remove(old);
      } else {
         this.artifacts.add(artifact);
      }

      this.art_map.put(artifact.toString(), artifact);
      this.changed = true;
   }

   public List<Artifact> getArtifacts() {
      return this.artifacts_imm;
   }

   public boolean changed() {
      return this.changed;
   }

   public void save() throws IOException {
      this.mod_list.modRef = (List)this.artifacts.stream().map((a) -> {
         return a.toString();
      }).collect(Collectors.toList());
      Files.write(GSON.toJson(this.mod_list), this.path, StandardCharsets.UTF_8);
   }

   private static File getFile(File root, String path) {
      try {
         return path.startsWith("absolute:") ? (new File(path.substring(9))).getCanonicalFile() : (new File(root, path)).getCanonicalFile();
      } catch (IOException var3) {
         LOGGER.info(LOGGER.getMessageFactory().newMessage("Unable to canonicalize path {} relative to {}", new Object[]{path, root.getAbsolutePath()}));
         return null;
      }
   }

   public List<Artifact> flatten() {
      List<Artifact> lst = this.parent == null ? new ArrayList() : this.parent.flatten();
      Iterator var2 = this.artifacts.iterator();

      while(var2.hasNext()) {
         Artifact art = (Artifact)var2.next();
         Stream var10000 = ((List)lst).stream();
         art.getClass();
         Optional<Artifact> old = var10000.filter(art::matchesID).findFirst();
         if (!old.isPresent()) {
            ((List)lst).add(art);
         } else if (((Artifact)old.get()).getVersion().compareTo(art.getVersion()) < 0) {
            ((List)lst).add(((List)lst).indexOf(old.get()), art);
            ((List)lst).remove(old.get());
         }
      }

      return (List)lst;
   }

   public Object getName() {
      return this.path.getAbsolutePath();
   }

   private static class JsonModList {
      public String repositoryRoot;
      public List<String> modRef;
      public String parentList;

      private JsonModList() {
      }

      // $FF: synthetic method
      JsonModList(Object x0) {
         this();
      }
   }
}
