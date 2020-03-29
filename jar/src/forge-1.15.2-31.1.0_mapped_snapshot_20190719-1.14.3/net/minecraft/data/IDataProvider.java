package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;

public interface IDataProvider {
   HashFunction HASH_FUNCTION = Hashing.sha1();

   void act(DirectoryCache var1) throws IOException;

   String getName();

   static void save(Gson p_218426_0_, DirectoryCache p_218426_1_, JsonElement p_218426_2_, Path p_218426_3_) throws IOException {
      String lvt_4_1_ = p_218426_0_.toJson(p_218426_2_);
      String lvt_5_1_ = HASH_FUNCTION.hashUnencodedChars(lvt_4_1_).toString();
      if (!Objects.equals(p_218426_1_.getPreviousHash(p_218426_3_), lvt_5_1_) || !Files.exists(p_218426_3_, new LinkOption[0])) {
         Files.createDirectories(p_218426_3_.getParent());
         BufferedWriter lvt_6_1_ = Files.newBufferedWriter(p_218426_3_);
         Throwable var7 = null;

         try {
            lvt_6_1_.write(lvt_4_1_);
         } catch (Throwable var16) {
            var7 = var16;
            throw var16;
         } finally {
            if (lvt_6_1_ != null) {
               if (var7 != null) {
                  try {
                     lvt_6_1_.close();
                  } catch (Throwable var15) {
                     var7.addSuppressed(var15);
                  }
               } else {
                  lvt_6_1_.close();
               }
            }

         }
      }

      p_218426_1_.func_208316_a(p_218426_3_, lvt_5_1_);
   }
}
