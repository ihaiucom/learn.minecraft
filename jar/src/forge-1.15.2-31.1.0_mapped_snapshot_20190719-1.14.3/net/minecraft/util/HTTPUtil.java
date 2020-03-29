package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HTTPUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ListeningExecutorService DOWNLOADER_EXECUTOR;

   @OnlyIn(Dist.CLIENT)
   public static CompletableFuture<?> downloadResourcePack(File p_180192_0_, String p_180192_1_, Map<String, String> p_180192_2_, int p_180192_3_, @Nullable IProgressUpdate p_180192_4_, Proxy p_180192_5_) {
      return CompletableFuture.supplyAsync(() -> {
         HttpURLConnection lvt_6_1_ = null;
         InputStream lvt_7_1_ = null;
         OutputStream lvt_8_1_ = null;
         if (p_180192_4_ != null) {
            p_180192_4_.resetProgressAndMessage(new TranslationTextComponent("resourcepack.downloading", new Object[0]));
            p_180192_4_.displayLoadingString(new TranslationTextComponent("resourcepack.requesting", new Object[0]));
         }

         try {
            byte[] lvt_9_1_ = new byte[4096];
            URL lvt_10_1_ = new URL(p_180192_1_);
            lvt_6_1_ = (HttpURLConnection)lvt_10_1_.openConnection(p_180192_5_);
            lvt_6_1_.setInstanceFollowRedirects(true);
            float lvt_11_1_ = 0.0F;
            float lvt_12_1_ = (float)p_180192_2_.entrySet().size();
            Iterator var13 = p_180192_2_.entrySet().iterator();

            while(var13.hasNext()) {
               Entry<String, String> lvt_14_1_ = (Entry)var13.next();
               lvt_6_1_.setRequestProperty((String)lvt_14_1_.getKey(), (String)lvt_14_1_.getValue());
               if (p_180192_4_ != null) {
                  p_180192_4_.setLoadingProgress((int)(++lvt_11_1_ / lvt_12_1_ * 100.0F));
               }
            }

            lvt_7_1_ = lvt_6_1_.getInputStream();
            lvt_12_1_ = (float)lvt_6_1_.getContentLength();
            int lvt_13_1_ = lvt_6_1_.getContentLength();
            if (p_180192_4_ != null) {
               p_180192_4_.displayLoadingString(new TranslationTextComponent("resourcepack.progress", new Object[]{String.format(Locale.ROOT, "%.2f", lvt_12_1_ / 1000.0F / 1000.0F)}));
            }

            if (p_180192_0_.exists()) {
               long lvt_14_2_ = p_180192_0_.length();
               if (lvt_14_2_ == (long)lvt_13_1_) {
                  if (p_180192_4_ != null) {
                     p_180192_4_.setDoneWorking();
                  }

                  Object var16 = null;
                  return var16;
               }

               LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", p_180192_0_, lvt_13_1_, lvt_14_2_);
               FileUtils.deleteQuietly(p_180192_0_);
            } else if (p_180192_0_.getParentFile() != null) {
               p_180192_0_.getParentFile().mkdirs();
            }

            lvt_8_1_ = new DataOutputStream(new FileOutputStream(p_180192_0_));
            if (p_180192_3_ > 0 && lvt_12_1_ > (float)p_180192_3_) {
               if (p_180192_4_ != null) {
                  p_180192_4_.setDoneWorking();
               }

               throw new IOException("Filesize is bigger than maximum allowed (file is " + lvt_11_1_ + ", limit is " + p_180192_3_ + ")");
            } else {
               int lvt_14_3_;
               while((lvt_14_3_ = lvt_7_1_.read(lvt_9_1_)) >= 0) {
                  lvt_11_1_ += (float)lvt_14_3_;
                  if (p_180192_4_ != null) {
                     p_180192_4_.setLoadingProgress((int)(lvt_11_1_ / lvt_12_1_ * 100.0F));
                  }

                  if (p_180192_3_ > 0 && lvt_11_1_ > (float)p_180192_3_) {
                     if (p_180192_4_ != null) {
                        p_180192_4_.setDoneWorking();
                     }

                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + lvt_11_1_ + ", limit was " + p_180192_3_ + ")");
                  }

                  if (Thread.interrupted()) {
                     LOGGER.error("INTERRUPTED");
                     if (p_180192_4_ != null) {
                        p_180192_4_.setDoneWorking();
                     }

                     Object var15 = null;
                     return var15;
                  }

                  lvt_8_1_.write(lvt_9_1_, 0, lvt_14_3_);
               }

               if (p_180192_4_ != null) {
                  p_180192_4_.setDoneWorking();
               }

               return null;
            }
         } catch (Throwable var22) {
            var22.printStackTrace();
            if (lvt_6_1_ != null) {
               InputStream lvt_10_2_ = lvt_6_1_.getErrorStream();

               try {
                  LOGGER.error(IOUtils.toString(lvt_10_2_));
               } catch (IOException var21) {
                  var21.printStackTrace();
               }
            }

            if (p_180192_4_ != null) {
               p_180192_4_.setDoneWorking();
            }

            return null;
         } finally {
            IOUtils.closeQuietly(lvt_7_1_);
            IOUtils.closeQuietly(lvt_8_1_);
         }
      }, DOWNLOADER_EXECUTOR);
   }

   public static int getSuitableLanPort() {
      try {
         ServerSocket lvt_0_1_ = new ServerSocket(0);
         Throwable var1 = null;

         int var2;
         try {
            var2 = lvt_0_1_.getLocalPort();
         } catch (Throwable var12) {
            var1 = var12;
            throw var12;
         } finally {
            if (lvt_0_1_ != null) {
               if (var1 != null) {
                  try {
                     lvt_0_1_.close();
                  } catch (Throwable var11) {
                     var1.addSuppressed(var11);
                  }
               } else {
                  lvt_0_1_.close();
               }
            }

         }

         return var2;
      } catch (IOException var14) {
         return 25564;
      }
   }

   static {
      DOWNLOADER_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).setNameFormat("Downloader %d").build()));
   }
}
