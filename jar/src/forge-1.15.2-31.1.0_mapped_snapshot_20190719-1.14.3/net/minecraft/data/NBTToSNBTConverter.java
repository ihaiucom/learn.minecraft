package net.minecraft.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTToSNBTConverter implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;

   public NBTToSNBTConverter(DataGenerator p_i48258_1_) {
      this.generator = p_i48258_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      Path lvt_2_1_ = this.generator.getOutputFolder();
      Iterator var3 = this.generator.getInputFolders().iterator();

      while(var3.hasNext()) {
         Path lvt_4_1_ = (Path)var3.next();
         Files.walk(lvt_4_1_).filter((p_200416_0_) -> {
            return p_200416_0_.toString().endsWith(".nbt");
         }).forEach((p_200415_3_) -> {
            func_229443_a_(p_200415_3_, this.getFileName(lvt_4_1_, p_200415_3_), lvt_2_1_);
         });
      }

   }

   public String getName() {
      return "NBT to SNBT";
   }

   private String getFileName(Path p_200417_1_, Path p_200417_2_) {
      String lvt_3_1_ = p_200417_1_.relativize(p_200417_2_).toString().replaceAll("\\\\", "/");
      return lvt_3_1_.substring(0, lvt_3_1_.length() - ".nbt".length());
   }

   @Nullable
   public static Path func_229443_a_(Path p_229443_0_, String p_229443_1_, Path p_229443_2_) {
      try {
         CompoundNBT lvt_3_1_ = CompressedStreamTools.readCompressed(Files.newInputStream(p_229443_0_));
         ITextComponent lvt_4_1_ = lvt_3_1_.toFormattedComponent("    ", 0);
         String lvt_5_1_ = lvt_4_1_.getString() + "\n";
         Path lvt_6_1_ = p_229443_2_.resolve(p_229443_1_ + ".snbt");
         Files.createDirectories(lvt_6_1_.getParent());
         BufferedWriter lvt_7_1_ = Files.newBufferedWriter(lvt_6_1_);
         Throwable var8 = null;

         try {
            lvt_7_1_.write(lvt_5_1_);
         } catch (Throwable var18) {
            var8 = var18;
            throw var18;
         } finally {
            if (lvt_7_1_ != null) {
               if (var8 != null) {
                  try {
                     lvt_7_1_.close();
                  } catch (Throwable var17) {
                     var8.addSuppressed(var17);
                  }
               } else {
                  lvt_7_1_.close();
               }
            }

         }

         LOGGER.info("Converted {} from NBT to SNBT", p_229443_1_);
         return lvt_6_1_;
      } catch (IOException var20) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", p_229443_1_, p_229443_0_, var20);
         return null;
      }
   }
}
