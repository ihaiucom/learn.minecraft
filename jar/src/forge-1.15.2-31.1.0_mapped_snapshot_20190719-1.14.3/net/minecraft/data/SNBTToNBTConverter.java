package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SNBTToNBTConverter implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;
   private final List<SNBTToNBTConverter.ITransformer> field_225370_d = Lists.newArrayList();

   public SNBTToNBTConverter(DataGenerator p_i48257_1_) {
      this.generator = p_i48257_1_;
   }

   public SNBTToNBTConverter func_225369_a(SNBTToNBTConverter.ITransformer p_225369_1_) {
      this.field_225370_d.add(p_225369_1_);
      return this;
   }

   private CompoundNBT func_225368_a(String p_225368_1_, CompoundNBT p_225368_2_) {
      CompoundNBT lvt_3_1_ = p_225368_2_;

      SNBTToNBTConverter.ITransformer lvt_5_1_;
      for(Iterator var4 = this.field_225370_d.iterator(); var4.hasNext(); lvt_3_1_ = lvt_5_1_.func_225371_a(p_225368_1_, lvt_3_1_)) {
         lvt_5_1_ = (SNBTToNBTConverter.ITransformer)var4.next();
      }

      return lvt_3_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      Path lvt_2_1_ = this.generator.getOutputFolder();
      List<CompletableFuture<SNBTToNBTConverter.TaskResult>> lvt_3_1_ = Lists.newArrayList();
      Iterator var4 = this.generator.getInputFolders().iterator();

      while(var4.hasNext()) {
         Path lvt_5_1_ = (Path)var4.next();
         Files.walk(lvt_5_1_).filter((p_200422_0_) -> {
            return p_200422_0_.toString().endsWith(".snbt");
         }).forEach((p_229447_3_) -> {
            lvt_3_1_.add(CompletableFuture.supplyAsync(() -> {
               return this.func_229446_a_(p_229447_3_, this.getFileName(lvt_5_1_, p_229447_3_));
            }, Util.getServerExecutor()));
         });
      }

      ((List)Util.gather(lvt_3_1_).join()).stream().filter(Objects::nonNull).forEach((p_229445_3_) -> {
         this.func_229444_a_(p_200398_1_, p_229445_3_, lvt_2_1_);
      });
   }

   public String getName() {
      return "SNBT -> NBT";
   }

   private String getFileName(Path p_200423_1_, Path p_200423_2_) {
      String lvt_3_1_ = p_200423_1_.relativize(p_200423_2_).toString().replaceAll("\\\\", "/");
      return lvt_3_1_.substring(0, lvt_3_1_.length() - ".snbt".length());
   }

   @Nullable
   private SNBTToNBTConverter.TaskResult func_229446_a_(Path p_229446_1_, String p_229446_2_) {
      try {
         BufferedReader lvt_3_1_ = Files.newBufferedReader(p_229446_1_);
         Throwable var4 = null;

         SNBTToNBTConverter.TaskResult var9;
         try {
            String lvt_5_1_ = IOUtils.toString(lvt_3_1_);
            ByteArrayOutputStream lvt_6_1_ = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(this.func_225368_a(p_229446_2_, JsonToNBT.getTagFromJson(lvt_5_1_)), lvt_6_1_);
            byte[] lvt_7_1_ = lvt_6_1_.toByteArray();
            String lvt_8_1_ = HASH_FUNCTION.hashBytes(lvt_7_1_).toString();
            var9 = new SNBTToNBTConverter.TaskResult(p_229446_2_, lvt_7_1_, lvt_8_1_);
         } catch (Throwable var20) {
            var4 = var20;
            throw var20;
         } finally {
            if (lvt_3_1_ != null) {
               if (var4 != null) {
                  try {
                     lvt_3_1_.close();
                  } catch (Throwable var19) {
                     var4.addSuppressed(var19);
                  }
               } else {
                  lvt_3_1_.close();
               }
            }

         }

         return var9;
      } catch (CommandSyntaxException var22) {
         LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", p_229446_2_, p_229446_1_, var22);
      } catch (IOException var23) {
         LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", p_229446_2_, p_229446_1_, var23);
      }

      return null;
   }

   private void func_229444_a_(DirectoryCache p_229444_1_, SNBTToNBTConverter.TaskResult p_229444_2_, Path p_229444_3_) {
      Path lvt_4_1_ = p_229444_3_.resolve(p_229444_2_.field_229449_a_ + ".nbt");

      try {
         if (!Objects.equals(p_229444_1_.getPreviousHash(lvt_4_1_), p_229444_2_.field_229451_c_) || !Files.exists(lvt_4_1_, new LinkOption[0])) {
            Files.createDirectories(lvt_4_1_.getParent());
            OutputStream lvt_5_1_ = Files.newOutputStream(lvt_4_1_);
            Throwable var6 = null;

            try {
               lvt_5_1_.write(p_229444_2_.field_229450_b_);
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (lvt_5_1_ != null) {
                  if (var6 != null) {
                     try {
                        lvt_5_1_.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     lvt_5_1_.close();
                  }
               }

            }
         }

         p_229444_1_.func_208316_a(lvt_4_1_, p_229444_2_.field_229451_c_);
      } catch (IOException var18) {
         LOGGER.error("Couldn't write structure {} at {}", p_229444_2_.field_229449_a_, lvt_4_1_, var18);
      }

   }

   @FunctionalInterface
   public interface ITransformer {
      CompoundNBT func_225371_a(String var1, CompoundNBT var2);
   }

   static class TaskResult {
      private final String field_229449_a_;
      private final byte[] field_229450_b_;
      private final String field_229451_c_;

      public TaskResult(String p_i226063_1_, byte[] p_i226063_2_, String p_i226063_3_) {
         this.field_229449_a_ = p_i226063_1_;
         this.field_229450_b_ = p_i226063_2_;
         this.field_229451_c_ = p_i226063_3_;
      }
   }
}
