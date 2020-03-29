package net.minecraft.resources;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FolderPack extends ResourcePack {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final boolean OS_WINDOWS;
   private static final CharMatcher BACKSLASH_MATCHER;

   public FolderPack(File p_i47914_1_) {
      super(p_i47914_1_);
   }

   public static boolean validatePath(File p_195777_0_, String p_195777_1_) throws IOException {
      String lvt_2_1_ = p_195777_0_.getCanonicalPath();
      if (OS_WINDOWS) {
         lvt_2_1_ = BACKSLASH_MATCHER.replaceFrom(lvt_2_1_, '/');
      }

      return lvt_2_1_.endsWith(p_195777_1_);
   }

   protected InputStream getInputStream(String p_195766_1_) throws IOException {
      File lvt_2_1_ = this.getFile(p_195766_1_);
      if (lvt_2_1_ == null) {
         throw new ResourcePackFileNotFoundException(this.file, p_195766_1_);
      } else {
         return new FileInputStream(lvt_2_1_);
      }
   }

   protected boolean resourceExists(String p_195768_1_) {
      return this.getFile(p_195768_1_) != null;
   }

   @Nullable
   private File getFile(String p_195776_1_) {
      try {
         File lvt_2_1_ = new File(this.file, p_195776_1_);
         if (lvt_2_1_.isFile() && validatePath(lvt_2_1_, p_195776_1_)) {
            return lvt_2_1_;
         }
      } catch (IOException var3) {
      }

      return null;
   }

   public Set<String> getResourceNamespaces(ResourcePackType p_195759_1_) {
      Set<String> lvt_2_1_ = Sets.newHashSet();
      File lvt_3_1_ = new File(this.file, p_195759_1_.getDirectoryName());
      File[] lvt_4_1_ = lvt_3_1_.listFiles(DirectoryFileFilter.DIRECTORY);
      if (lvt_4_1_ != null) {
         File[] var5 = lvt_4_1_;
         int var6 = lvt_4_1_.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File lvt_8_1_ = var5[var7];
            String lvt_9_1_ = getRelativeString(lvt_3_1_, lvt_8_1_);
            if (lvt_9_1_.equals(lvt_9_1_.toLowerCase(Locale.ROOT))) {
               lvt_2_1_.add(lvt_9_1_.substring(0, lvt_9_1_.length() - 1));
            } else {
               this.onIgnoreNonLowercaseNamespace(lvt_9_1_);
            }
         }
      }

      return lvt_2_1_;
   }

   public void close() throws IOException {
   }

   public Collection<ResourceLocation> func_225637_a_(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_) {
      File lvt_6_1_ = new File(this.file, p_225637_1_.getDirectoryName());
      List<ResourceLocation> lvt_7_1_ = Lists.newArrayList();
      this.func_199546_a(new File(new File(lvt_6_1_, p_225637_2_), p_225637_3_), p_225637_4_, p_225637_2_, lvt_7_1_, p_225637_3_ + "/", p_225637_5_);
      return lvt_7_1_;
   }

   private void func_199546_a(File p_199546_1_, int p_199546_2_, String p_199546_3_, List<ResourceLocation> p_199546_4_, String p_199546_5_, Predicate<String> p_199546_6_) {
      File[] lvt_7_1_ = p_199546_1_.listFiles();
      if (lvt_7_1_ != null) {
         File[] var8 = lvt_7_1_;
         int var9 = lvt_7_1_.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            File lvt_11_1_ = var8[var10];
            if (lvt_11_1_.isDirectory()) {
               if (p_199546_2_ > 0) {
                  this.func_199546_a(lvt_11_1_, p_199546_2_ - 1, p_199546_3_, p_199546_4_, p_199546_5_ + lvt_11_1_.getName() + "/", p_199546_6_);
               }
            } else if (!lvt_11_1_.getName().endsWith(".mcmeta") && p_199546_6_.test(lvt_11_1_.getName())) {
               try {
                  p_199546_4_.add(new ResourceLocation(p_199546_3_, p_199546_5_ + lvt_11_1_.getName()));
               } catch (ResourceLocationException var13) {
                  LOGGER.error(var13.getMessage());
               }
            }
         }
      }

   }

   static {
      OS_WINDOWS = Util.getOSType() == Util.OS.WINDOWS;
      BACKSLASH_MATCHER = CharMatcher.is('\\');
   }
}
