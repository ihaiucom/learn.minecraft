package net.minecraftforge.fml.relauncher.libraries;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

public class LibraryManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final boolean DISABLE_EXTERNAL_MANIFEST = Boolean.parseBoolean(System.getProperty("forge.disable_external_manifest", "false"));
   public static final boolean ENABLE_AUTO_MOD_MOVEMENT = Boolean.parseBoolean(System.getProperty("forge.enable_auto_mod_movement", "false"));
   private static final String LIBRARY_DIRECTORY_OVERRIDE = System.getProperty("forge.lib_folder", (String)null);
   private static final List<String> skipContainedDeps = Arrays.asList(System.getProperty("fml.skipContainedDeps", "").split(","));
   private static final FilenameFilter MOD_FILENAME_FILTER = (dir, name) -> {
      return name.endsWith(".jar") || name.endsWith(".zip");
   };
   private static final Comparator<File> FILE_NAME_SORTER_INSENSITVE = (o1, o2) -> {
      return o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH));
   };
   public static final Name MODSIDE = new Name("ModSide");
   private static final Name MODCONTAINSDEPS = new Name("ContainedDeps");
   private static final Name MAVEN_ARTIFACT = new Name("Maven-Artifact");
   private static final Name TIMESTAMP = new Name("Timestamp");
   private static final Name MD5 = new Name("MD5");
   private static Repository libraries_dir = null;
   private static Set<File> processed = new HashSet();

   public static void setup(File minecraftHome) {
      File libDir = findLibraryFolder(minecraftHome);
      LOGGER.debug("Determined Minecraft Libraries Root: {}", libDir);
      Repository old = Repository.replace(libDir, "libraries");
      if (old != null) {
         LOGGER.debug("  Overwriting Previous: {}", old);
      }

      libraries_dir = Repository.get("libraries");
      File mods = new File(minecraftHome, "mods");
      File mods_ver = new File(mods, MCPVersion.getMCVersion());
      ModList memory = null;
      if (!ENABLE_AUTO_MOD_MOVEMENT) {
         Repository repo = new LinkRepository(new File(mods, "memory_repo"));
         memory = new MemoryModList(repo);
         ModList.cache.put("MEMORY", memory);
         Repository.cache.put("MEMORY", repo);
      }

      File[] var14 = new File[]{mods, mods_ver};
      int var7 = var14.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         File dir = var14[var8];
         cleanDirectory(dir, (ModList)(ENABLE_AUTO_MOD_MOVEMENT ? ModList.create(new File(dir, "mod_list.json"), minecraftHome) : memory), mods_ver, mods);
      }

      Iterator var15 = ModList.getKnownLists(minecraftHome).iterator();

      while(var15.hasNext()) {
         ModList list = (ModList)var15.next();
         Repository repo = list.getRepository() == null ? libraries_dir : list.getRepository();
         List<Artifact> artifacts = list.getArtifacts();

         for(int i = 0; i < artifacts.size(); ++i) {
            Artifact artifact = (Artifact)artifacts.get(i);
            Artifact resolved = repo.resolve(artifact);
            if (resolved != null) {
               File target = repo.getFile(resolved.getPath());
               if (target.exists()) {
                  extractPacked(target, list, mods_ver, mods);
               }
            }
         }
      }

   }

   private static File findLibraryFolder(File minecraftHome) {
      if (LIBRARY_DIRECTORY_OVERRIDE != null) {
         LOGGER.error("System variable set to override Library Directory: {}", LIBRARY_DIRECTORY_OVERRIDE);
         return new File(LIBRARY_DIRECTORY_OVERRIDE);
      } else {
         CodeSource source = ArtifactVersion.class.getProtectionDomain().getCodeSource();
         if (source == null) {
            LOGGER.error("Unable to determine codesource for {}. Using default libraries directory.", ArtifactVersion.class.getName());
            return new File(minecraftHome, "libraries");
         } else {
            try {
               File apache = new File(source.getLocation().toURI());
               if (apache.isFile()) {
                  apache = apache.getParentFile();
               }

               apache = apache.getParentFile();
               String comp = apache.getAbsolutePath().toLowerCase(Locale.ENGLISH).replace('\\', '/');
               if (!comp.endsWith("/")) {
                  comp = comp + '/';
               }

               if (!comp.endsWith("/org/apache/maven/maven-artifact/")) {
                  LOGGER.error("Apache Maven library folder was not in the format expected. Using default libraries directory.");
                  LOGGER.error("Full: {}", new File(source.getLocation().toURI()));
                  LOGGER.error("Trimmed: {}", comp);
                  return new File(minecraftHome, "libraries");
               } else {
                  return apache.getParentFile().getParentFile().getParentFile().getParentFile();
               }
            } catch (URISyntaxException var4) {
               LOGGER.error(LOGGER.getMessageFactory().newMessage("Unable to determine file for {}. Using default libraries directory.", new Object[]{ArtifactVersion.class.getName()}), var4);
               return new File(minecraftHome, "libraries");
            }
         }
      }
   }

   private static void cleanDirectory(File dir, ModList modlist, File... modDirs) {
      if (dir.exists()) {
         LOGGER.debug("Cleaning up mods folder: {}", dir);
         File[] var3 = dir.listFiles((f) -> {
            return f.isFile() && f.getName().endsWith(".jar");
         });
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File file = var3[var5];
            Pair<Artifact, byte[]> ret = extractPacked(file, modlist, modDirs);
            if (ret != null) {
               Artifact artifact = (Artifact)ret.getLeft();
               Repository repo = modlist.getRepository() == null ? libraries_dir : modlist.getRepository();
               File moved = repo.archive(artifact, file, (byte[])ret.getRight());
               processed.add(moved);
            }
         }

         try {
            if (modlist.changed()) {
               modlist.save();
            }
         } catch (IOException var11) {
            LOGGER.error(LOGGER.getMessageFactory().newMessage("Error updating modlist file {}", new Object[]{modlist.getName()}), var11);
         }

      }
   }

   private static Pair<Artifact, byte[]> extractPacked(File file, ModList modlist, File... modDirs) {
      if (processed.contains(file)) {
         LOGGER.debug("File already proccessed {}, Skipping", file.getAbsolutePath());
         return null;
      } else {
         JarFile jar = null;

         try {
            jar = new JarFile(file);
            LOGGER.debug("Examining file: {}", file.getName());
            processed.add(file);
            Pair var4 = extractPacked(jar, modlist, modDirs);
            return var4;
         } catch (IOException var14) {
            LOGGER.error("Unable to read the jar file {} - ignoring", file.getName(), var14);
         } finally {
            try {
               if (jar != null) {
                  jar.close();
               }
            } catch (IOException var13) {
            }

         }

         return null;
      }
   }

   private static Pair<Artifact, byte[]> extractPacked(JarFile jar, ModList modlist, File... modDirs) throws IOException {
      if (jar.getManifest() == null) {
         return null;
      } else {
         JarEntry manifest_entry = jar.getJarEntry("META-INF/MANIFEST.MF");
         if (manifest_entry == null) {
            manifest_entry = (JarEntry)jar.stream().filter((e) -> {
               return "META-INF/MANIFEST.MF".equals(e.getName().toUpperCase(Locale.ENGLISH));
            }).findFirst().get();
         }

         Attributes attrs = jar.getManifest().getMainAttributes();
         String modSide = attrs.getValue(MODSIDE);
         if (modSide != null && !"BOTH".equals(modSide) && !FMLEnvironment.dist.name().equals(modSide)) {
            return null;
         } else {
            if (attrs.containsKey(MODCONTAINSDEPS)) {
               String[] var6 = attrs.getValue(MODCONTAINSDEPS).split(" ");
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String dep = var6[var8];
                  if (!dep.endsWith(".jar")) {
                     LOGGER.error("Contained Dep is not a jar file: {}", dep);
                     throw new IllegalStateException("Invalid contained dep, Must be jar: " + dep);
                  }

                  if (jar.getJarEntry(dep) == null && jar.getJarEntry("META-INF/libraries/" + dep) != null) {
                     dep = "META-INF/libraries/" + dep;
                  }

                  JarEntry depEntry = jar.getJarEntry(dep);
                  if (depEntry == null) {
                     LOGGER.error("Contained Dep is not in the jar: {}", dep);
                     throw new IllegalStateException("Invalid contained dep, Missing from jar: " + dep);
                  }

                  String depEndName = (new File(dep)).getName();
                  if (!skipContainedDeps.contains(dep) && !skipContainedDeps.contains(depEndName)) {
                     Attributes meta = null;
                     byte[] data = null;
                     byte[] manifest_data = null;
                     JarEntry metaEntry = jar.getJarEntry(dep + ".meta");
                     if (metaEntry != null) {
                        manifest_data = readAll(jar.getInputStream(metaEntry));
                        meta = (new Manifest(new ByteArrayInputStream(manifest_data))).getMainAttributes();
                     } else {
                        data = readAll(jar.getInputStream(depEntry));
                        ZipInputStream zi = new ZipInputStream(new ByteArrayInputStream(data));
                        Throwable var17 = null;

                        try {
                           ZipEntry ze = null;

                           while((ze = zi.getNextEntry()) != null) {
                              if (ze.getName().equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                                 manifest_data = readAll(zi);
                                 meta = (new Manifest(new ByteArrayInputStream(manifest_data))).getMainAttributes();
                                 break;
                              }
                           }
                        } catch (Throwable var152) {
                           var17 = var152;
                           throw var152;
                        } finally {
                           if (zi != null) {
                              if (var17 != null) {
                                 try {
                                    zi.close();
                                 } catch (Throwable var136) {
                                    var17.addSuppressed(var136);
                                 }
                              } else {
                                 zi.close();
                              }
                           }

                        }
                     }

                     File target;
                     FileOutputStream out;
                     Throwable var162;
                     Object in;
                     Throwable var166;
                     if (meta != null && meta.containsKey(MAVEN_ARTIFACT)) {
                        try {
                           Artifact artifact = readArtifact(modlist.getRepository(), meta);
                           target = artifact.getFile();
                           Pair child;
                           if (target.exists()) {
                              LOGGER.debug("Found existing ContainedDep {}({}) from {} extracted to {}, skipping extraction", dep, artifact.toString(), target.getCanonicalPath(), jar.getName());
                              if (!ENABLE_AUTO_MOD_MOVEMENT) {
                                 child = extractPacked(target, modlist, modDirs);
                                 if (child == null && metaEntry != null) {
                                    modlist.add(artifact);
                                 }
                              }
                           } else {
                              LOGGER.debug("Extracting ContainedDep {}({}) from {} to {}", dep, artifact.toString(), jar.getName(), target.getCanonicalPath());
                              Files.createParentDirs(target);
                              out = new FileOutputStream(target);
                              var162 = null;

                              try {
                                 in = data == null ? jar.getInputStream(depEntry) : new ByteArrayInputStream(data);
                                 var166 = null;

                                 try {
                                    ByteStreams.copy((InputStream)in, out);
                                 } catch (Throwable var142) {
                                    var166 = var142;
                                    throw var142;
                                 } finally {
                                    if (in != null) {
                                       if (var166 != null) {
                                          try {
                                             ((InputStream)in).close();
                                          } catch (Throwable var138) {
                                             var166.addSuppressed(var138);
                                          }
                                       } else {
                                          ((InputStream)in).close();
                                       }
                                    }

                                 }
                              } catch (Throwable var144) {
                                 var162 = var144;
                                 throw var144;
                              } finally {
                                 if (out != null) {
                                    if (var162 != null) {
                                       try {
                                          out.close();
                                       } catch (Throwable var137) {
                                          var162.addSuppressed(var137);
                                       }
                                    } else {
                                       out.close();
                                    }
                                 }

                              }

                              LOGGER.debug("Extracted ContainedDep {}({}) from {} to {}", dep, artifact.toString(), jar.getName(), target.getCanonicalPath());
                              if (artifact.isSnapshot()) {
                                 SnapshotJson json = SnapshotJson.create(artifact.getSnapshotMeta());
                                 json.add(new SnapshotJson.Entry(artifact.getTimestamp(), meta.getValue(MD5)));
                                 json.write(artifact.getSnapshotMeta());
                              }

                              if (!DISABLE_EXTERNAL_MANIFEST) {
                                 File meta_target = new File(target.getAbsolutePath() + ".meta");
                                 Files.write(manifest_data, meta_target);
                              }

                              child = extractPacked(target, modlist, modDirs);
                              if (child == null && metaEntry != null) {
                                 modlist.add(artifact);
                              }
                           }
                        } catch (NumberFormatException var146) {
                           LOGGER.error(LOGGER.getMessageFactory().newMessage("An error occurred extracting dependency. Invalid Timestamp: {}", new Object[]{meta.getValue(TIMESTAMP)}), var146);
                        } catch (IOException var147) {
                           LOGGER.error("An error occurred extracting dependency", var147);
                        }
                     } else {
                        boolean found = false;
                        File[] var157 = modDirs;
                        int var159 = modDirs.length;

                        for(int var19 = 0; var19 < var159; ++var19) {
                           File dir = var157[var19];
                           File target = new File(dir, depEndName);
                           if (target.exists()) {
                              LOGGER.debug("Found existing ContainDep extracted to {}, skipping extraction", target.getCanonicalPath());
                              found = true;
                           }
                        }

                        if (!found) {
                           target = new File(modDirs[0], depEndName);
                           LOGGER.debug("Extracting ContainedDep {} from {} to {}", dep, jar.getName(), target.getCanonicalPath());

                           try {
                              Files.createParentDirs(target);
                              out = new FileOutputStream(target);
                              var162 = null;

                              try {
                                 in = data == null ? jar.getInputStream(depEntry) : new ByteArrayInputStream(data);
                                 var166 = null;

                                 try {
                                    ByteStreams.copy((InputStream)in, out);
                                 } catch (Throwable var141) {
                                    var166 = var141;
                                    throw var141;
                                 } finally {
                                    if (in != null) {
                                       if (var166 != null) {
                                          try {
                                             ((InputStream)in).close();
                                          } catch (Throwable var140) {
                                             var166.addSuppressed(var140);
                                          }
                                       } else {
                                          ((InputStream)in).close();
                                       }
                                    }

                                 }
                              } catch (Throwable var149) {
                                 var162 = var149;
                                 throw var149;
                              } finally {
                                 if (out != null) {
                                    if (var162 != null) {
                                       try {
                                          out.close();
                                       } catch (Throwable var139) {
                                          var162.addSuppressed(var139);
                                       }
                                    } else {
                                       out.close();
                                    }
                                 }

                              }

                              LOGGER.debug("Extracted ContainedDep {} from {} to {}", dep, jar.getName(), target.getCanonicalPath());
                              extractPacked(target, modlist, modDirs);
                           } catch (IOException var151) {
                              LOGGER.error("An error occurred extracting dependency", var151);
                           }
                        }
                     }
                  } else {
                     LOGGER.error("Skipping dep at request: {}", dep);
                  }
               }
            }

            if (attrs.containsKey(MAVEN_ARTIFACT)) {
               Artifact artifact = readArtifact(modlist.getRepository(), attrs);
               modlist.add(artifact);
               return Pair.of(artifact, readAll(jar.getInputStream(manifest_entry)));
            } else {
               return null;
            }
         }
      }
   }

   private static Artifact readArtifact(Repository repo, Attributes meta) {
      String timestamp = meta.getValue(TIMESTAMP);
      if (timestamp != null) {
         timestamp = SnapshotJson.TIMESTAMP.format(new Date(Long.parseLong(timestamp)));
      }

      return new Artifact(repo, meta.getValue(MAVEN_ARTIFACT), timestamp);
   }

   private static byte[] readAll(InputStream in) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int read = true;
      byte[] data = new byte[16384];

      int read;
      while((read = in.read(data, 0, data.length)) != -1) {
         out.write(data, 0, read);
      }

      out.flush();
      return out.toByteArray();
   }

   public static List<Artifact> flattenLists(File mcDir) {
      List<Artifact> merged = new ArrayList();
      Iterator var2 = ModList.getBasicLists(mcDir).iterator();

      while(var2.hasNext()) {
         ModList list = (ModList)var2.next();
         Iterator var4 = list.flatten().iterator();

         while(var4.hasNext()) {
            Artifact art = (Artifact)var4.next();
            Stream var10000 = merged.stream();
            art.getClass();
            Optional<Artifact> old = var10000.filter(art::matchesID).findFirst();
            if (!old.isPresent()) {
               merged.add(art);
            } else if (((Artifact)old.get()).getVersion().compareTo(art.getVersion()) < 0) {
               merged.add(merged.indexOf(old.get()), art);
               merged.remove(old.get());
            }
         }
      }

      return merged;
   }

   public static List<File> gatherLegacyCanidates(File mcDir) {
      List<File> list = new ArrayList();
      Map<String, String> args = Collections.emptyMap();
      String extraMods = (String)args.get("--mods");
      String[] var4;
      int var5;
      int var6;
      String mod;
      File file;
      if (extraMods != null) {
         LOGGER.info("Found mods from the command line:");
         var4 = extraMods.split(",");
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            mod = var4[var6];
            file = new File(mcDir, mod);
            if (!file.exists()) {
               LOGGER.info("  Failed to find mod file {} ({})", mod, file.getAbsolutePath());
            } else if (!list.contains(file)) {
               LOGGER.debug("  Adding {} ({}) to the mod list", mod, file.getAbsolutePath());
               list.add(file);
            } else if (!list.contains(file)) {
               LOGGER.debug("  Duplicte command line mod detected {} ({})", mod, file.getAbsolutePath());
            }
         }
      }

      var4 = new String[]{"mods", "mods" + File.separatorChar + MCPVersion.getMCVersion()};
      var5 = var4.length;

      for(var6 = 0; var6 < var5; ++var6) {
         mod = var4[var6];
         file = new File(mcDir, mod);
         if (file.isDirectory() && file.exists()) {
            LOGGER.info("Searching {} for mods", file.getAbsolutePath());
            File[] var9 = file.listFiles(MOD_FILENAME_FILTER);
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               File f = var9[var11];
               if (!list.contains(f)) {
                  LOGGER.debug("  Adding {} to the mod list", f.getName());
                  list.add(f);
               }
            }
         }
      }

      ModList memory = (ModList)ModList.cache.get("MEMORY");
      if (!ENABLE_AUTO_MOD_MOVEMENT && memory != null && memory.getRepository() != null) {
         memory.getRepository().filterLegacy(list);
      }

      list.sort(FILE_NAME_SORTER_INSENSITVE);
      return list;
   }

   public static Repository getDefaultRepo() {
      return libraries_dir;
   }
}
