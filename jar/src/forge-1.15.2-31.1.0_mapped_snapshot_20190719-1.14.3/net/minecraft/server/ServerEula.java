package net.minecraft.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerEula {
   private static final Logger LOG = LogManager.getLogger();
   private final Path eulaFile;
   private final boolean acceptedEULA;

   public ServerEula(Path p_i50746_1_) {
      this.eulaFile = p_i50746_1_;
      this.acceptedEULA = SharedConstants.developmentMode || this.loadEulaStatus();
   }

   private boolean loadEulaStatus() {
      try {
         InputStream lvt_1_1_ = Files.newInputStream(this.eulaFile);
         Throwable var2 = null;

         boolean var4;
         try {
            Properties lvt_3_1_ = new Properties();
            lvt_3_1_.load(lvt_1_1_);
            var4 = Boolean.parseBoolean(lvt_3_1_.getProperty("eula", "false"));
         } catch (Throwable var14) {
            var2 = var14;
            throw var14;
         } finally {
            if (lvt_1_1_ != null) {
               if (var2 != null) {
                  try {
                     lvt_1_1_.close();
                  } catch (Throwable var13) {
                     var2.addSuppressed(var13);
                  }
               } else {
                  lvt_1_1_.close();
               }
            }

         }

         return var4;
      } catch (Exception var16) {
         LOG.warn("Failed to load {}", this.eulaFile);
         this.createEULAFile();
         return false;
      }
   }

   public boolean hasAcceptedEULA() {
      return this.acceptedEULA;
   }

   private void createEULAFile() {
      if (!SharedConstants.developmentMode) {
         try {
            OutputStream lvt_1_1_ = Files.newOutputStream(this.eulaFile);
            Throwable var2 = null;

            try {
               Properties lvt_3_1_ = new Properties();
               lvt_3_1_.setProperty("eula", "false");
               lvt_3_1_.store(lvt_1_1_, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
            } catch (Throwable var12) {
               var2 = var12;
               throw var12;
            } finally {
               if (lvt_1_1_ != null) {
                  if (var2 != null) {
                     try {
                        lvt_1_1_.close();
                     } catch (Throwable var11) {
                        var2.addSuppressed(var11);
                     }
                  } else {
                     lvt_1_1_.close();
                  }
               }

            }
         } catch (Exception var14) {
            LOG.warn("Failed to save {}", this.eulaFile, var14);
         }

      }
   }
}
