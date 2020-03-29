package net.minecraft.util;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FileUtil {
   private static final Pattern field_214996_a = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
   private static final Pattern field_214997_b = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

   @OnlyIn(Dist.CLIENT)
   public static String func_214992_a(Path p_214992_0_, String p_214992_1_, String p_214992_2_) throws IOException {
      char[] var3 = SharedConstants.ILLEGAL_FILE_CHARACTERS;
      int lvt_4_1_ = var3.length;

      for(int var5 = 0; var5 < lvt_4_1_; ++var5) {
         char lvt_6_1_ = var3[var5];
         p_214992_1_ = p_214992_1_.replace(lvt_6_1_, '_');
      }

      p_214992_1_ = p_214992_1_.replaceAll("[./\"]", "_");
      if (field_214997_b.matcher(p_214992_1_).matches()) {
         p_214992_1_ = "_" + p_214992_1_ + "_";
      }

      Matcher lvt_3_1_ = field_214996_a.matcher(p_214992_1_);
      lvt_4_1_ = 0;
      if (lvt_3_1_.matches()) {
         p_214992_1_ = lvt_3_1_.group("name");
         lvt_4_1_ = Integer.parseInt(lvt_3_1_.group("count"));
      }

      if (p_214992_1_.length() > 255 - p_214992_2_.length()) {
         p_214992_1_ = p_214992_1_.substring(0, 255 - p_214992_2_.length());
      }

      while(true) {
         String lvt_5_1_ = p_214992_1_;
         if (lvt_4_1_ != 0) {
            String lvt_6_2_ = " (" + lvt_4_1_ + ")";
            int lvt_7_1_ = 255 - lvt_6_2_.length();
            if (p_214992_1_.length() > lvt_7_1_) {
               lvt_5_1_ = p_214992_1_.substring(0, lvt_7_1_);
            }

            lvt_5_1_ = lvt_5_1_ + lvt_6_2_;
         }

         lvt_5_1_ = lvt_5_1_ + p_214992_2_;
         Path lvt_6_3_ = p_214992_0_.resolve(lvt_5_1_);

         try {
            Path lvt_7_2_ = Files.createDirectory(lvt_6_3_);
            Files.deleteIfExists(lvt_7_2_);
            return p_214992_0_.relativize(lvt_7_2_).toString();
         } catch (FileAlreadyExistsException var8) {
            ++lvt_4_1_;
         }
      }
   }

   public static boolean func_214995_a(Path p_214995_0_) {
      Path lvt_1_1_ = p_214995_0_.normalize();
      return lvt_1_1_.equals(p_214995_0_);
   }

   public static boolean func_214994_b(Path p_214994_0_) {
      Iterator var1 = p_214994_0_.iterator();

      Path lvt_2_1_;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         lvt_2_1_ = (Path)var1.next();
      } while(!field_214997_b.matcher(lvt_2_1_.toString()).matches());

      return false;
   }

   public static Path func_214993_b(Path p_214993_0_, String p_214993_1_, String p_214993_2_) {
      String lvt_3_1_ = p_214993_1_ + p_214993_2_;
      Path lvt_4_1_ = Paths.get(lvt_3_1_);
      if (lvt_4_1_.endsWith(p_214993_2_)) {
         throw new InvalidPathException(lvt_3_1_, "empty resource name");
      } else {
         return p_214993_0_.resolve(lvt_4_1_);
      }
   }
}
