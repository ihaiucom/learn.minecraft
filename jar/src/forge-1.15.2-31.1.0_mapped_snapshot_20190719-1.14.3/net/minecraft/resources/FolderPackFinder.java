package net.minecraft.resources;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;

public class FolderPackFinder implements IPackFinder {
   private static final FileFilter FILE_FILTER = (p_195731_0_) -> {
      boolean lvt_1_1_ = p_195731_0_.isFile() && p_195731_0_.getName().endsWith(".zip");
      boolean lvt_2_1_ = p_195731_0_.isDirectory() && (new File(p_195731_0_, "pack.mcmeta")).isFile();
      return lvt_1_1_ || lvt_2_1_;
   };
   private final File folder;

   public FolderPackFinder(File p_i47911_1_) {
      this.folder = p_i47911_1_;
   }

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> p_195730_1_, ResourcePackInfo.IFactory<T> p_195730_2_) {
      if (!this.folder.isDirectory()) {
         this.folder.mkdirs();
      }

      File[] lvt_3_1_ = this.folder.listFiles(FILE_FILTER);
      if (lvt_3_1_ != null) {
         File[] var4 = lvt_3_1_;
         int var5 = lvt_3_1_.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            File lvt_7_1_ = var4[var6];
            String lvt_8_1_ = "file/" + lvt_7_1_.getName();
            T lvt_9_1_ = ResourcePackInfo.createResourcePack(lvt_8_1_, false, this.makePackSupplier(lvt_7_1_), p_195730_2_, ResourcePackInfo.Priority.TOP);
            if (lvt_9_1_ != null) {
               p_195730_1_.put(lvt_8_1_, lvt_9_1_);
            }
         }

      }
   }

   private Supplier<IResourcePack> makePackSupplier(File p_195733_1_) {
      return p_195733_1_.isDirectory() ? () -> {
         return new FolderPack(p_195733_1_);
      } : () -> {
         return new FilePack(p_195733_1_);
      };
   }
}
