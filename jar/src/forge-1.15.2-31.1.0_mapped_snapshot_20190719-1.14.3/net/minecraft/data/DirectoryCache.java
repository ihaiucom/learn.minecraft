package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryCache {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Path outputFolder;
   private final Path cacheFile;
   private int hits;
   private final Map<Path, String> staleFiles = Maps.newHashMap();
   private final Map<Path, String> createdFiles = Maps.newTreeMap();
   private final Set<Path> field_218457_g = Sets.newHashSet();

   public DirectoryCache(Path p_i49352_1_, String p_i49352_2_) throws IOException {
      this.outputFolder = p_i49352_1_;
      Path path = p_i49352_1_.resolve(".cache");
      Files.createDirectories(path);
      this.cacheFile = path.resolve(p_i49352_2_);
      this.getFiles().forEach((p_lambda$new$0_1_) -> {
         String s = (String)this.staleFiles.put(p_lambda$new$0_1_, "");
      });
      if (Files.isReadable(this.cacheFile)) {
         IOUtils.readLines(Files.newInputStream(this.cacheFile), Charsets.UTF_8).forEach((p_lambda$new$1_2_) -> {
            int i = p_lambda$new$1_2_.indexOf(32);
            this.staleFiles.put(p_i49352_1_.resolve(p_lambda$new$1_2_.substring(i + 1)), p_lambda$new$1_2_.substring(0, i));
         });
      }

   }

   public void writeCache() throws IOException {
      this.func_209400_b();

      BufferedWriter writer;
      try {
         writer = Files.newBufferedWriter(this.cacheFile);
      } catch (IOException var3) {
         LOGGER.warn("Unable write cachefile {}: {}", this.cacheFile, var3.toString());
         return;
      }

      IOUtils.writeLines((Collection)this.createdFiles.entrySet().stream().map((p_lambda$writeCache$2_1_) -> {
         return (String)p_lambda$writeCache$2_1_.getValue() + ' ' + this.outputFolder.relativize((Path)p_lambda$writeCache$2_1_.getKey());
      }).collect(Collectors.toList()), System.lineSeparator(), writer);
      writer.close();
      LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", this.hits, this.createdFiles.size() - this.hits, this.staleFiles.size());
   }

   @Nullable
   public String getPreviousHash(Path p_208323_1_) {
      return (String)this.staleFiles.get(p_208323_1_);
   }

   public void func_208316_a(Path p_208316_1_, String p_208316_2_) {
      this.createdFiles.put(p_208316_1_, p_208316_2_);
      if (Objects.equals(this.staleFiles.remove(p_208316_1_), p_208316_2_)) {
         ++this.hits;
      }

   }

   public boolean func_208320_b(Path p_208320_1_) {
      return this.staleFiles.containsKey(p_208320_1_);
   }

   public void func_218456_c(Path p_218456_1_) {
      this.field_218457_g.add(p_218456_1_);
   }

   private void func_209400_b() throws IOException {
      this.getFiles().forEach((p_lambda$func_209400_b$3_1_) -> {
         if (this.func_208320_b(p_lambda$func_209400_b$3_1_) && !this.field_218457_g.contains(p_lambda$func_209400_b$3_1_)) {
            try {
               Files.delete(p_lambda$func_209400_b$3_1_);
            } catch (IOException var3) {
               LOGGER.debug("Unable to delete: {} ({})", p_lambda$func_209400_b$3_1_, var3.toString());
            }
         }

      });
   }

   private Stream<Path> getFiles() throws IOException {
      return Files.walk(this.outputFolder).filter((p_lambda$getFiles$4_1_) -> {
         return !Objects.equals(this.cacheFile, p_lambda$getFiles$4_1_) && !Files.isDirectory(p_lambda$getFiles$4_1_, new LinkOption[0]);
      });
   }
}
