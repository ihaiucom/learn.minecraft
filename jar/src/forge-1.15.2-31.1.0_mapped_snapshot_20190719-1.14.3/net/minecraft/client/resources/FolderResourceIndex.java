package net.minecraft.client.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FolderResourceIndex extends ResourceIndex {
   private final File baseDir;

   public FolderResourceIndex(File p_i46540_1_) {
      this.baseDir = p_i46540_1_;
   }

   public File getFile(ResourceLocation p_188547_1_) {
      return new File(this.baseDir, p_188547_1_.toString().replace(':', '/'));
   }

   public File func_225638_a_(String p_225638_1_) {
      return new File(this.baseDir, p_225638_1_);
   }

   public Collection<ResourceLocation> func_225639_a_(String p_225639_1_, String p_225639_2_, int p_225639_3_, Predicate<String> p_225639_4_) {
      Path lvt_5_1_ = this.baseDir.toPath().resolve(p_225639_2_);

      try {
         Stream<Path> lvt_6_1_ = Files.walk(lvt_5_1_.resolve(p_225639_1_), p_225639_3_, new FileVisitOption[0]);
         Throwable var7 = null;

         Collection var8;
         try {
            var8 = (Collection)lvt_6_1_.filter((p_211686_0_) -> {
               return Files.isRegularFile(p_211686_0_, new LinkOption[0]);
            }).filter((p_211687_0_) -> {
               return !p_211687_0_.endsWith(".mcmeta");
            }).filter((p_229275_1_) -> {
               return p_225639_4_.test(p_229275_1_.getFileName().toString());
            }).map((p_229274_2_) -> {
               return new ResourceLocation(p_225639_2_, lvt_5_1_.relativize(p_229274_2_).toString().replaceAll("\\\\", "/"));
            }).collect(Collectors.toList());
         } catch (Throwable var19) {
            var7 = var19;
            throw var19;
         } finally {
            if (lvt_6_1_ != null) {
               if (var7 != null) {
                  try {
                     lvt_6_1_.close();
                  } catch (Throwable var18) {
                     var7.addSuppressed(var18);
                  }
               } else {
                  lvt_6_1_.close();
               }
            }

         }

         return var8;
      } catch (NoSuchFileException var21) {
      } catch (IOException var22) {
         LOGGER.warn("Unable to getFiles on {}", p_225639_1_, var22);
      }

      return Collections.emptyList();
   }
}
