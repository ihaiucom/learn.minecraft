package net.minecraft.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class FilePack extends ResourcePack {
   public static final Splitter PATH_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
   private ZipFile zipFile;

   public FilePack(File p_i47915_1_) {
      super(p_i47915_1_);
   }

   private ZipFile getResourcePackZipFile() throws IOException {
      if (this.zipFile == null) {
         this.zipFile = new ZipFile(this.file);
      }

      return this.zipFile;
   }

   protected InputStream getInputStream(String p_195766_1_) throws IOException {
      ZipFile lvt_2_1_ = this.getResourcePackZipFile();
      ZipEntry lvt_3_1_ = lvt_2_1_.getEntry(p_195766_1_);
      if (lvt_3_1_ == null) {
         throw new ResourcePackFileNotFoundException(this.file, p_195766_1_);
      } else {
         return lvt_2_1_.getInputStream(lvt_3_1_);
      }
   }

   public boolean resourceExists(String p_195768_1_) {
      try {
         return this.getResourcePackZipFile().getEntry(p_195768_1_) != null;
      } catch (IOException var3) {
         return false;
      }
   }

   public Set<String> getResourceNamespaces(ResourcePackType p_195759_1_) {
      ZipFile lvt_2_2_;
      try {
         lvt_2_2_ = this.getResourcePackZipFile();
      } catch (IOException var9) {
         return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> lvt_3_2_ = lvt_2_2_.entries();
      HashSet lvt_4_1_ = Sets.newHashSet();

      while(lvt_3_2_.hasMoreElements()) {
         ZipEntry lvt_5_1_ = (ZipEntry)lvt_3_2_.nextElement();
         String lvt_6_1_ = lvt_5_1_.getName();
         if (lvt_6_1_.startsWith(p_195759_1_.getDirectoryName() + "/")) {
            List<String> lvt_7_1_ = Lists.newArrayList(PATH_SPLITTER.split(lvt_6_1_));
            if (lvt_7_1_.size() > 1) {
               String lvt_8_1_ = (String)lvt_7_1_.get(1);
               if (lvt_8_1_.equals(lvt_8_1_.toLowerCase(Locale.ROOT))) {
                  lvt_4_1_.add(lvt_8_1_);
               } else {
                  this.onIgnoreNonLowercaseNamespace(lvt_8_1_);
               }
            }
         }
      }

      return lvt_4_1_;
   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public void close() {
      if (this.zipFile != null) {
         IOUtils.closeQuietly(this.zipFile);
         this.zipFile = null;
      }

   }

   public Collection<ResourceLocation> func_225637_a_(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_) {
      ZipFile lvt_6_2_;
      try {
         lvt_6_2_ = this.getResourcePackZipFile();
      } catch (IOException var15) {
         return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> lvt_7_2_ = lvt_6_2_.entries();
      List<ResourceLocation> lvt_8_1_ = Lists.newArrayList();
      String lvt_9_1_ = p_225637_1_.getDirectoryName() + "/" + p_225637_2_ + "/";
      String lvt_10_1_ = lvt_9_1_ + p_225637_3_ + "/";

      while(lvt_7_2_.hasMoreElements()) {
         ZipEntry lvt_11_1_ = (ZipEntry)lvt_7_2_.nextElement();
         if (!lvt_11_1_.isDirectory()) {
            String lvt_12_1_ = lvt_11_1_.getName();
            if (!lvt_12_1_.endsWith(".mcmeta") && lvt_12_1_.startsWith(lvt_10_1_)) {
               String lvt_13_1_ = lvt_12_1_.substring(lvt_9_1_.length());
               String[] lvt_14_1_ = lvt_13_1_.split("/");
               if (lvt_14_1_.length >= p_225637_4_ + 1 && p_225637_5_.test(lvt_14_1_[lvt_14_1_.length - 1])) {
                  lvt_8_1_.add(new ResourceLocation(p_225637_2_, lvt_13_1_));
               }
            }
         }
      }

      return lvt_8_1_;
   }
}
