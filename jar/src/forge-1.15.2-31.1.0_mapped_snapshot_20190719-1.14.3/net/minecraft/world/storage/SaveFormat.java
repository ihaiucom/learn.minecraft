package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileUtil;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.StartupQuery;
import net.minecraftforge.fml.WorldPersistenceHooks;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormat {
   private static final Logger field_215785_a = LogManager.getLogger();
   private static final DateTimeFormatter BACKUP_DATE_FORMAT;
   private final Path savesDir;
   private final Path backupsDir;
   private final DataFixer field_215788_e;

   public SaveFormat(Path p_i51277_1_, Path p_i51277_2_, DataFixer p_i51277_3_) {
      this.field_215788_e = p_i51277_3_;

      try {
         Files.createDirectories(Files.exists(p_i51277_1_, new LinkOption[0]) ? p_i51277_1_.toRealPath() : p_i51277_1_);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }

      this.savesDir = p_i51277_1_;
      this.backupsDir = p_i51277_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return "Anvil";
   }

   @OnlyIn(Dist.CLIENT)
   public List<WorldSummary> getSaveList() throws AnvilConverterException {
      if (!Files.isDirectory(this.savesDir, new LinkOption[0])) {
         throw new AnvilConverterException((new TranslationTextComponent("selectWorld.load_folder_access", new Object[0])).getString());
      } else {
         List<WorldSummary> list = Lists.newArrayList();
         File[] afile = this.savesDir.toFile().listFiles();
         File[] var3 = afile;
         int var4 = afile.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File file1 = var3[var5];
            if (file1.isDirectory()) {
               String s = file1.getName();
               WorldInfo worldinfo = this.getWorldInfo(s);
               if (worldinfo != null && (worldinfo.getSaveVersion() == 19132 || worldinfo.getSaveVersion() == 19133)) {
                  boolean flag = worldinfo.getSaveVersion() != this.func_215782_e();
                  String s1 = worldinfo.getWorldName();
                  if (StringUtils.isEmpty(s1)) {
                     s1 = s;
                  }

                  long i = 0L;
                  list.add(new WorldSummary(worldinfo, s, s1, 0L, flag));
               }
            }
         }

         return list;
      }
   }

   private int func_215782_e() {
      return 19133;
   }

   public SaveHandler getSaveLoader(String p_197715_1_, @Nullable MinecraftServer p_197715_2_) {
      return func_215783_a(this.savesDir, this.field_215788_e, p_197715_1_, p_197715_2_);
   }

   protected static SaveHandler func_215783_a(Path p_215783_0_, DataFixer p_215783_1_, String p_215783_2_, @Nullable MinecraftServer p_215783_3_) {
      return new SaveHandler(p_215783_0_.toFile(), p_215783_2_, p_215783_3_, p_215783_1_);
   }

   public boolean isOldMapFormat(String p_75801_1_) {
      WorldInfo worldinfo = this.getWorldInfo(p_75801_1_);
      return worldinfo != null && worldinfo.getSaveVersion() != this.func_215782_e();
   }

   public boolean convertMapFormat(String p_75805_1_, IProgressUpdate p_75805_2_) {
      return AnvilSaveConverter.func_215792_a(this.savesDir, this.field_215788_e, p_75805_1_, p_75805_2_);
   }

   @Nullable
   public WorldInfo getWorldInfo(String p_75803_1_) {
      return func_215779_a(this.savesDir, this.field_215788_e, p_75803_1_);
   }

   @Nullable
   protected static WorldInfo func_215779_a(Path p_215779_0_, DataFixer p_215779_1_, String p_215779_2_) {
      File file1 = new File(p_215779_0_.toFile(), p_215779_2_);
      if (!file1.exists()) {
         return null;
      } else {
         File file2 = new File(file1, "level.dat");
         if (file2.exists()) {
            WorldInfo worldinfo = func_215780_a(file2, p_215779_1_);
            if (worldinfo != null) {
               return worldinfo;
            }
         }

         file2 = new File(file1, "level.dat_old");
         return file2.exists() ? func_215780_a(file2, p_215779_1_) : null;
      }
   }

   @Nullable
   public static WorldInfo func_215780_a(File p_215780_0_, DataFixer p_215780_1_) {
      return getWorldData(p_215780_0_, p_215780_1_, (SaveHandler)null);
   }

   @Nullable
   public static WorldInfo getWorldData(File p_getWorldData_0_, DataFixer p_getWorldData_1_, @Nullable SaveHandler p_getWorldData_2_) {
      try {
         CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(new FileInputStream(p_getWorldData_0_));
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
         CompoundNBT compoundnbt2 = compoundnbt1.contains("Player", 10) ? compoundnbt1.getCompound("Player") : null;
         compoundnbt1.remove("Player");
         int i = compoundnbt1.contains("DataVersion", 99) ? compoundnbt1.getInt("DataVersion") : -1;
         WorldInfo ret = new WorldInfo(NBTUtil.update(p_getWorldData_1_, DefaultTypeReferences.LEVEL, compoundnbt1, i), p_getWorldData_1_, i, compoundnbt2);
         if (p_getWorldData_2_ != null) {
            WorldPersistenceHooks.handleWorldDataLoad(p_getWorldData_2_, ret, compoundnbt);
         }

         return ret;
      } catch (StartupQuery.AbortedException var8) {
         throw var8;
      } catch (Exception var9) {
         field_215785_a.error("Exception reading {}", p_getWorldData_0_, var9);
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void renameWorld(String p_75806_1_, String p_75806_2_) {
      File file1 = new File(this.savesDir.toFile(), p_75806_1_);
      if (file1.exists()) {
         File file2 = new File(file1, "level.dat");
         if (file2.exists()) {
            try {
               CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(new FileInputStream(file2));
               CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
               compoundnbt1.putString("LevelName", p_75806_2_);
               CompressedStreamTools.writeCompressed(compoundnbt, new FileOutputStream(file2));
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNewLevelIdAcceptable(String p_207742_1_) {
      try {
         Path path = this.savesDir.resolve(p_207742_1_);
         Files.createDirectory(path);
         Files.deleteIfExists(path);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean deleteWorldDirectory(String p_75802_1_) {
      File file1 = new File(this.savesDir.toFile(), p_75802_1_);
      if (!file1.exists()) {
         return true;
      } else {
         field_215785_a.info("Deleting level {}", p_75802_1_);

         for(int i = 1; i <= 5; ++i) {
            field_215785_a.info("Attempt {}...", i);
            if (func_215784_a(file1.listFiles())) {
               break;
            }

            field_215785_a.warn("Unsuccessful in deleting contents.");
            if (i < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
               }
            }
         }

         return file1.delete();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static boolean func_215784_a(File[] p_215784_0_) {
      File[] var1 = p_215784_0_;
      int var2 = p_215784_0_.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         File file1 = var1[var3];
         field_215785_a.debug("Deleting {}", file1);
         if (file1.isDirectory() && !func_215784_a(file1.listFiles())) {
            field_215785_a.warn("Couldn't delete directory {}", file1);
            return false;
         }

         if (!file1.delete()) {
            field_215785_a.warn("Couldn't delete file {}", file1);
            return false;
         }
      }

      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canLoadWorld(String p_90033_1_) {
      return Files.isDirectory(this.savesDir.resolve(p_90033_1_), new LinkOption[0]);
   }

   @OnlyIn(Dist.CLIENT)
   public Path func_215781_c() {
      return this.savesDir;
   }

   public File getFile(String p_186352_1_, String p_186352_2_) {
      return this.savesDir.resolve(p_186352_1_).resolve(p_186352_2_).toFile();
   }

   @OnlyIn(Dist.CLIENT)
   private Path getWorldFolder(String p_197714_1_) {
      return this.savesDir.resolve(p_197714_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public Path getBackupsFolder() {
      return this.backupsDir;
   }

   @OnlyIn(Dist.CLIENT)
   public long createBackup(String p_197713_1_) throws IOException {
      final Path path = this.getWorldFolder(p_197713_1_);
      String s = LocalDateTime.now().format(BACKUP_DATE_FORMAT) + "_" + p_197713_1_;
      Path path1 = this.getBackupsFolder();

      try {
         Files.createDirectories(Files.exists(path1, new LinkOption[0]) ? path1.toRealPath() : path1);
      } catch (IOException var18) {
         throw new RuntimeException(var18);
      }

      Path path2 = path1.resolve(FileUtil.func_214992_a(path1, s, ".zip"));
      final ZipOutputStream zipoutputstream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path2)));
      Throwable var7 = null;

      try {
         final Path path3 = Paths.get(p_197713_1_);
         Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path p_visitFile_1_, BasicFileAttributes p_visitFile_2_) throws IOException {
               String s1 = path3.resolve(path.relativize(p_visitFile_1_)).toString().replace('\\', '/');
               ZipEntry zipentry = new ZipEntry(s1);
               zipoutputstream.putNextEntry(zipentry);
               com.google.common.io.Files.asByteSource(p_visitFile_1_.toFile()).copyTo(zipoutputstream);
               zipoutputstream.closeEntry();
               return FileVisitResult.CONTINUE;
            }
         });
      } catch (Throwable var17) {
         var7 = var17;
         throw var17;
      } finally {
         if (zipoutputstream != null) {
            if (var7 != null) {
               try {
                  zipoutputstream.close();
               } catch (Throwable var16) {
                  var7.addSuppressed(var16);
               }
            } else {
               zipoutputstream.close();
            }
         }

      }

      return Files.size(path2);
   }

   static {
      BACKUP_DATE_FORMAT = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
   }
}
