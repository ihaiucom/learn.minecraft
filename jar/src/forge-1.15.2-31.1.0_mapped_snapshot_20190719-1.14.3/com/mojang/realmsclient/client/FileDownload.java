package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FileDownload {
   private static final Logger field_224843_a = LogManager.getLogger();
   private volatile boolean field_224844_b;
   private volatile boolean field_224845_c;
   private volatile boolean field_224846_d;
   private volatile boolean field_224847_e;
   private volatile File field_224848_f;
   private volatile File field_224849_g;
   private volatile HttpGet field_224850_h;
   private Thread field_224851_i;
   private final RequestConfig field_224852_j = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
   private static final String[] field_224853_k = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public long func_224827_a(String p_224827_1_) {
      CloseableHttpClient lvt_2_1_ = null;
      HttpGet lvt_3_1_ = null;

      long var5;
      try {
         lvt_3_1_ = new HttpGet(p_224827_1_);
         lvt_2_1_ = HttpClientBuilder.create().setDefaultRequestConfig(this.field_224852_j).build();
         CloseableHttpResponse lvt_4_1_ = lvt_2_1_.execute(lvt_3_1_);
         var5 = Long.parseLong(lvt_4_1_.getFirstHeader("Content-Length").getValue());
         return var5;
      } catch (Throwable var16) {
         field_224843_a.error("Unable to get content length for download");
         var5 = 0L;
      } finally {
         if (lvt_3_1_ != null) {
            lvt_3_1_.releaseConnection();
         }

         if (lvt_2_1_ != null) {
            try {
               lvt_2_1_.close();
            } catch (IOException var15) {
               field_224843_a.error("Could not close http client", var15);
            }
         }

      }

      return var5;
   }

   public void func_224830_a(WorldDownload p_224830_1_, String p_224830_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_224830_3_, RealmsAnvilLevelStorageSource p_224830_4_) {
      if (this.field_224851_i == null) {
         this.field_224851_i = new Thread(() -> {
            CloseableHttpClient lvt_5_1_ = null;
            boolean var90 = false;

            label1408: {
               CloseableHttpResponse lvt_6_6_;
               FileOutputStream lvt_7_6_;
               FileDownload.DownloadCountingOutputStream lvt_9_4_;
               FileDownload.ResourcePackProgressListener lvt_8_4_;
               label1402: {
                  try {
                     var90 = true;
                     this.field_224848_f = File.createTempFile("backup", ".tar.gz");
                     this.field_224850_h = new HttpGet(p_224830_1_.downloadLink);
                     lvt_5_1_ = HttpClientBuilder.create().setDefaultRequestConfig(this.field_224852_j).build();
                     lvt_6_6_ = lvt_5_1_.execute(this.field_224850_h);
                     p_224830_3_.field_225140_b = Long.parseLong(lvt_6_6_.getFirstHeader("Content-Length").getValue());
                     if (lvt_6_6_.getStatusLine().getStatusCode() != 200) {
                        this.field_224846_d = true;
                        this.field_224850_h.abort();
                        var90 = false;
                        break label1408;
                     }

                     lvt_7_6_ = new FileOutputStream(this.field_224848_f);
                     FileDownload.ProgressListener lvt_8_2_ = new FileDownload.ProgressListener(p_224830_2_.trim(), this.field_224848_f, p_224830_4_, p_224830_3_, p_224830_1_);
                     lvt_9_4_ = new FileDownload.DownloadCountingOutputStream(lvt_7_6_);
                     lvt_9_4_.func_224804_a(lvt_8_2_);
                     IOUtils.copy(lvt_6_6_.getEntity().getContent(), lvt_9_4_);
                     var90 = false;
                     break label1402;
                  } catch (Exception var103) {
                     field_224843_a.error("Caught exception while downloading: " + var103.getMessage());
                     this.field_224846_d = true;
                     var90 = false;
                  } finally {
                     if (var90) {
                        this.field_224850_h.releaseConnection();
                        if (this.field_224848_f != null) {
                           this.field_224848_f.delete();
                        }

                        if (!this.field_224846_d) {
                           if (!p_224830_1_.resourcePackUrl.isEmpty() && !p_224830_1_.resourcePackHash.isEmpty()) {
                              try {
                                 this.field_224848_f = File.createTempFile("resources", ".tar.gz");
                                 this.field_224850_h = new HttpGet(p_224830_1_.resourcePackUrl);
                                 HttpResponse lvt_15_1_ = lvt_5_1_.execute(this.field_224850_h);
                                 p_224830_3_.field_225140_b = Long.parseLong(lvt_15_1_.getFirstHeader("Content-Length").getValue());
                                 if (lvt_15_1_.getStatusLine().getStatusCode() != 200) {
                                    this.field_224846_d = true;
                                    this.field_224850_h.abort();
                                    return;
                                 }

                                 OutputStream lvt_16_1_ = new FileOutputStream(this.field_224848_f);
                                 FileDownload.ResourcePackProgressListener lvt_17_1_ = new FileDownload.ResourcePackProgressListener(this.field_224848_f, p_224830_3_, p_224830_1_);
                                 FileDownload.DownloadCountingOutputStream lvt_18_1_ = new FileDownload.DownloadCountingOutputStream(lvt_16_1_);
                                 lvt_18_1_.func_224804_a(lvt_17_1_);
                                 IOUtils.copy(lvt_15_1_.getEntity().getContent(), lvt_18_1_);
                              } catch (Exception var95) {
                                 field_224843_a.error("Caught exception while downloading: " + var95.getMessage());
                                 this.field_224846_d = true;
                              } finally {
                                 this.field_224850_h.releaseConnection();
                                 if (this.field_224848_f != null) {
                                    this.field_224848_f.delete();
                                 }

                              }
                           } else {
                              this.field_224845_c = true;
                           }
                        }

                        if (lvt_5_1_ != null) {
                           try {
                              lvt_5_1_.close();
                           } catch (IOException var91) {
                              field_224843_a.error("Failed to close Realms download client");
                           }
                        }

                     }
                  }

                  this.field_224850_h.releaseConnection();
                  if (this.field_224848_f != null) {
                     this.field_224848_f.delete();
                  }

                  if (!this.field_224846_d) {
                     if (!p_224830_1_.resourcePackUrl.isEmpty() && !p_224830_1_.resourcePackHash.isEmpty()) {
                        try {
                           this.field_224848_f = File.createTempFile("resources", ".tar.gz");
                           this.field_224850_h = new HttpGet(p_224830_1_.resourcePackUrl);
                           lvt_6_6_ = lvt_5_1_.execute(this.field_224850_h);
                           p_224830_3_.field_225140_b = Long.parseLong(lvt_6_6_.getFirstHeader("Content-Length").getValue());
                           if (lvt_6_6_.getStatusLine().getStatusCode() != 200) {
                              this.field_224846_d = true;
                              this.field_224850_h.abort();
                              return;
                           }

                           lvt_7_6_ = new FileOutputStream(this.field_224848_f);
                           lvt_8_4_ = new FileDownload.ResourcePackProgressListener(this.field_224848_f, p_224830_3_, p_224830_1_);
                           lvt_9_4_ = new FileDownload.DownloadCountingOutputStream(lvt_7_6_);
                           lvt_9_4_.func_224804_a(lvt_8_4_);
                           IOUtils.copy(lvt_6_6_.getEntity().getContent(), lvt_9_4_);
                        } catch (Exception var99) {
                           field_224843_a.error("Caught exception while downloading: " + var99.getMessage());
                           this.field_224846_d = true;
                        } finally {
                           this.field_224850_h.releaseConnection();
                           if (this.field_224848_f != null) {
                              this.field_224848_f.delete();
                           }

                        }
                     } else {
                        this.field_224845_c = true;
                     }
                  }

                  if (lvt_5_1_ != null) {
                     try {
                        lvt_5_1_.close();
                     } catch (IOException var93) {
                        field_224843_a.error("Failed to close Realms download client");
                     }

                     return;
                  }

                  return;
               }

               this.field_224850_h.releaseConnection();
               if (this.field_224848_f != null) {
                  this.field_224848_f.delete();
               }

               if (!this.field_224846_d) {
                  if (!p_224830_1_.resourcePackUrl.isEmpty() && !p_224830_1_.resourcePackHash.isEmpty()) {
                     try {
                        this.field_224848_f = File.createTempFile("resources", ".tar.gz");
                        this.field_224850_h = new HttpGet(p_224830_1_.resourcePackUrl);
                        lvt_6_6_ = lvt_5_1_.execute(this.field_224850_h);
                        p_224830_3_.field_225140_b = Long.parseLong(lvt_6_6_.getFirstHeader("Content-Length").getValue());
                        if (lvt_6_6_.getStatusLine().getStatusCode() != 200) {
                           this.field_224846_d = true;
                           this.field_224850_h.abort();
                           return;
                        }

                        lvt_7_6_ = new FileOutputStream(this.field_224848_f);
                        lvt_8_4_ = new FileDownload.ResourcePackProgressListener(this.field_224848_f, p_224830_3_, p_224830_1_);
                        lvt_9_4_ = new FileDownload.DownloadCountingOutputStream(lvt_7_6_);
                        lvt_9_4_.func_224804_a(lvt_8_4_);
                        IOUtils.copy(lvt_6_6_.getEntity().getContent(), lvt_9_4_);
                     } catch (Exception var101) {
                        field_224843_a.error("Caught exception while downloading: " + var101.getMessage());
                        this.field_224846_d = true;
                     } finally {
                        this.field_224850_h.releaseConnection();
                        if (this.field_224848_f != null) {
                           this.field_224848_f.delete();
                        }

                     }
                  } else {
                     this.field_224845_c = true;
                  }
               }

               if (lvt_5_1_ != null) {
                  try {
                     lvt_5_1_.close();
                  } catch (IOException var94) {
                     field_224843_a.error("Failed to close Realms download client");
                  }
               }

               return;
            }

            this.field_224850_h.releaseConnection();
            if (this.field_224848_f != null) {
               this.field_224848_f.delete();
            }

            if (!this.field_224846_d) {
               if (!p_224830_1_.resourcePackUrl.isEmpty() && !p_224830_1_.resourcePackHash.isEmpty()) {
                  try {
                     this.field_224848_f = File.createTempFile("resources", ".tar.gz");
                     this.field_224850_h = new HttpGet(p_224830_1_.resourcePackUrl);
                     HttpResponse lvt_7_1_ = lvt_5_1_.execute(this.field_224850_h);
                     p_224830_3_.field_225140_b = Long.parseLong(lvt_7_1_.getFirstHeader("Content-Length").getValue());
                     if (lvt_7_1_.getStatusLine().getStatusCode() != 200) {
                        this.field_224846_d = true;
                        this.field_224850_h.abort();
                        return;
                     }

                     OutputStream lvt_8_1_ = new FileOutputStream(this.field_224848_f);
                     FileDownload.ResourcePackProgressListener lvt_9_1_ = new FileDownload.ResourcePackProgressListener(this.field_224848_f, p_224830_3_, p_224830_1_);
                     FileDownload.DownloadCountingOutputStream lvt_10_1_ = new FileDownload.DownloadCountingOutputStream(lvt_8_1_);
                     lvt_10_1_.func_224804_a(lvt_9_1_);
                     IOUtils.copy(lvt_7_1_.getEntity().getContent(), lvt_10_1_);
                  } catch (Exception var97) {
                     field_224843_a.error("Caught exception while downloading: " + var97.getMessage());
                     this.field_224846_d = true;
                  } finally {
                     this.field_224850_h.releaseConnection();
                     if (this.field_224848_f != null) {
                        this.field_224848_f.delete();
                     }

                  }
               } else {
                  this.field_224845_c = true;
               }
            }

            if (lvt_5_1_ != null) {
               try {
                  lvt_5_1_.close();
               } catch (IOException var92) {
                  field_224843_a.error("Failed to close Realms download client");
               }
            }

         });
         this.field_224851_i.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(field_224843_a));
         this.field_224851_i.start();
      }
   }

   public void func_224834_a() {
      if (this.field_224850_h != null) {
         this.field_224850_h.abort();
      }

      if (this.field_224848_f != null) {
         this.field_224848_f.delete();
      }

      this.field_224844_b = true;
   }

   public boolean func_224835_b() {
      return this.field_224845_c;
   }

   public boolean func_224836_c() {
      return this.field_224846_d;
   }

   public boolean func_224837_d() {
      return this.field_224847_e;
   }

   public static String func_224828_b(String p_224828_0_) {
      p_224828_0_ = p_224828_0_.replaceAll("[\\./\"]", "_");
      String[] var1 = field_224853_k;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String lvt_4_1_ = var1[var3];
         if (p_224828_0_.equalsIgnoreCase(lvt_4_1_)) {
            p_224828_0_ = "_" + p_224828_0_ + "_";
         }
      }

      return p_224828_0_;
   }

   private void func_224831_a(String p_224831_1_, File p_224831_2_, RealmsAnvilLevelStorageSource p_224831_3_) throws IOException {
      Pattern lvt_4_1_ = Pattern.compile(".*-([0-9]+)$");
      int lvt_6_1_ = 1;
      char[] var7 = RealmsSharedConstants.ILLEGAL_FILE_CHARACTERS;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         char lvt_10_1_ = var7[var9];
         p_224831_1_ = p_224831_1_.replace(lvt_10_1_, '_');
      }

      if (StringUtils.isEmpty(p_224831_1_)) {
         p_224831_1_ = "Realm";
      }

      p_224831_1_ = func_224828_b(p_224831_1_);

      try {
         Iterator var24 = p_224831_3_.getLevelList().iterator();

         while(var24.hasNext()) {
            RealmsLevelSummary lvt_8_1_ = (RealmsLevelSummary)var24.next();
            if (lvt_8_1_.getLevelId().toLowerCase(Locale.ROOT).startsWith(p_224831_1_.toLowerCase(Locale.ROOT))) {
               Matcher lvt_9_1_ = lvt_4_1_.matcher(lvt_8_1_.getLevelId());
               if (lvt_9_1_.matches()) {
                  if (Integer.valueOf(lvt_9_1_.group(1)) > lvt_6_1_) {
                     lvt_6_1_ = Integer.valueOf(lvt_9_1_.group(1));
                  }
               } else {
                  ++lvt_6_1_;
               }
            }
         }
      } catch (Exception var23) {
         field_224843_a.error("Error getting level list", var23);
         this.field_224846_d = true;
         return;
      }

      String lvt_5_2_;
      if (p_224831_3_.isNewLevelIdAcceptable(p_224831_1_) && lvt_6_1_ <= 1) {
         lvt_5_2_ = p_224831_1_;
      } else {
         lvt_5_2_ = p_224831_1_ + (lvt_6_1_ == 1 ? "" : "-" + lvt_6_1_);
         if (!p_224831_3_.isNewLevelIdAcceptable(lvt_5_2_)) {
            boolean lvt_7_2_ = false;

            while(!lvt_7_2_) {
               ++lvt_6_1_;
               lvt_5_2_ = p_224831_1_ + (lvt_6_1_ == 1 ? "" : "-" + lvt_6_1_);
               if (p_224831_3_.isNewLevelIdAcceptable(lvt_5_2_)) {
                  lvt_7_2_ = true;
               }
            }
         }
      }

      TarArchiveInputStream lvt_7_3_ = null;
      File lvt_8_2_ = new File(Realms.getGameDirectoryPath(), "saves");
      boolean var20 = false;

      File lvt_10_4_;
      label301: {
         try {
            var20 = true;
            lvt_8_2_.mkdir();
            lvt_7_3_ = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(p_224831_2_))));

            for(TarArchiveEntry lvt_9_2_ = lvt_7_3_.getNextTarEntry(); lvt_9_2_ != null; lvt_9_2_ = lvt_7_3_.getNextTarEntry()) {
               lvt_10_4_ = new File(lvt_8_2_, lvt_9_2_.getName().replace("world", lvt_5_2_));
               if (lvt_9_2_.isDirectory()) {
                  lvt_10_4_.mkdirs();
               } else {
                  lvt_10_4_.createNewFile();
                  byte[] lvt_11_1_ = new byte[1024];
                  BufferedOutputStream lvt_12_1_ = new BufferedOutputStream(new FileOutputStream(lvt_10_4_));
                  boolean var13 = false;

                  int lvt_13_1_;
                  while((lvt_13_1_ = lvt_7_3_.read(lvt_11_1_)) != -1) {
                     lvt_12_1_.write(lvt_11_1_, 0, lvt_13_1_);
                  }

                  lvt_12_1_.close();
                  Object var32 = null;
               }
            }

            var20 = false;
            break label301;
         } catch (Exception var21) {
            field_224843_a.error("Error extracting world", var21);
            this.field_224846_d = true;
            var20 = false;
         } finally {
            if (var20) {
               if (lvt_7_3_ != null) {
                  lvt_7_3_.close();
               }

               if (p_224831_2_ != null) {
                  p_224831_2_.delete();
               }

               p_224831_3_.renameLevel(lvt_5_2_, lvt_5_2_.trim());
               File lvt_16_1_ = new File(lvt_8_2_, lvt_5_2_ + File.separator + "level.dat");
               Realms.deletePlayerTag(lvt_16_1_);
               this.field_224849_g = new File(lvt_8_2_, lvt_5_2_ + File.separator + "resources.zip");
            }
         }

         if (lvt_7_3_ != null) {
            lvt_7_3_.close();
         }

         if (p_224831_2_ != null) {
            p_224831_2_.delete();
         }

         p_224831_3_.renameLevel(lvt_5_2_, lvt_5_2_.trim());
         lvt_10_4_ = new File(lvt_8_2_, lvt_5_2_ + File.separator + "level.dat");
         Realms.deletePlayerTag(lvt_10_4_);
         this.field_224849_g = new File(lvt_8_2_, lvt_5_2_ + File.separator + "resources.zip");
         return;
      }

      if (lvt_7_3_ != null) {
         lvt_7_3_.close();
      }

      if (p_224831_2_ != null) {
         p_224831_2_.delete();
      }

      p_224831_3_.renameLevel(lvt_5_2_, lvt_5_2_.trim());
      lvt_10_4_ = new File(lvt_8_2_, lvt_5_2_ + File.separator + "level.dat");
      Realms.deletePlayerTag(lvt_10_4_);
      this.field_224849_g = new File(lvt_8_2_, lvt_5_2_ + File.separator + "resources.zip");
   }

   @OnlyIn(Dist.CLIENT)
   class DownloadCountingOutputStream extends CountingOutputStream {
      private ActionListener field_224806_b;

      public DownloadCountingOutputStream(OutputStream p_i51649_2_) {
         super(p_i51649_2_);
      }

      public void func_224804_a(ActionListener p_224804_1_) {
         this.field_224806_b = p_224804_1_;
      }

      protected void afterWrite(int p_afterWrite_1_) throws IOException {
         super.afterWrite(p_afterWrite_1_);
         if (this.field_224806_b != null) {
            this.field_224806_b.actionPerformed(new ActionEvent(this, 0, (String)null));
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ResourcePackProgressListener implements ActionListener {
      private final File field_224819_b;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus field_224820_c;
      private final WorldDownload field_224821_d;

      private ResourcePackProgressListener(File p_i51645_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51645_3_, WorldDownload p_i51645_4_) {
         this.field_224819_b = p_i51645_2_;
         this.field_224820_c = p_i51645_3_;
         this.field_224821_d = p_i51645_4_;
      }

      public void actionPerformed(ActionEvent p_actionPerformed_1_) {
         this.field_224820_c.field_225139_a = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();
         if (this.field_224820_c.field_225139_a >= this.field_224820_c.field_225140_b && !FileDownload.this.field_224844_b) {
            try {
               String lvt_2_1_ = Hashing.sha1().hashBytes(Files.toByteArray(this.field_224819_b)).toString();
               if (lvt_2_1_.equals(this.field_224821_d.resourcePackHash)) {
                  FileUtils.copyFile(this.field_224819_b, FileDownload.this.field_224849_g);
                  FileDownload.this.field_224845_c = true;
               } else {
                  FileDownload.field_224843_a.error("Resourcepack had wrong hash (expected " + this.field_224821_d.resourcePackHash + ", found " + lvt_2_1_ + "). Deleting it.");
                  FileUtils.deleteQuietly(this.field_224819_b);
                  FileDownload.this.field_224846_d = true;
               }
            } catch (IOException var3) {
               FileDownload.field_224843_a.error("Error copying resourcepack file", var3.getMessage());
               FileDownload.this.field_224846_d = true;
            }
         }

      }

      // $FF: synthetic method
      ResourcePackProgressListener(File p_i51646_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51646_3_, WorldDownload p_i51646_4_, Object p_i51646_5_) {
         this(p_i51646_2_, p_i51646_3_, p_i51646_4_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ProgressListener implements ActionListener {
      private final String field_224813_b;
      private final File field_224814_c;
      private final RealmsAnvilLevelStorageSource field_224815_d;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus field_224816_e;
      private final WorldDownload field_224817_f;

      private ProgressListener(String p_i51647_2_, File p_i51647_3_, RealmsAnvilLevelStorageSource p_i51647_4_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51647_5_, WorldDownload p_i51647_6_) {
         this.field_224813_b = p_i51647_2_;
         this.field_224814_c = p_i51647_3_;
         this.field_224815_d = p_i51647_4_;
         this.field_224816_e = p_i51647_5_;
         this.field_224817_f = p_i51647_6_;
      }

      public void actionPerformed(ActionEvent p_actionPerformed_1_) {
         this.field_224816_e.field_225139_a = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();
         if (this.field_224816_e.field_225139_a >= this.field_224816_e.field_225140_b && !FileDownload.this.field_224844_b && !FileDownload.this.field_224846_d) {
            try {
               FileDownload.this.field_224847_e = true;
               FileDownload.this.func_224831_a(this.field_224813_b, this.field_224814_c, this.field_224815_d);
            } catch (IOException var3) {
               FileDownload.field_224843_a.error("Error extracting archive", var3);
               FileDownload.this.field_224846_d = true;
            }
         }

      }

      // $FF: synthetic method
      ProgressListener(String p_i51648_2_, File p_i51648_3_, RealmsAnvilLevelStorageSource p_i51648_4_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51648_5_, WorldDownload p_i51648_6_, Object p_i51648_7_) {
         this(p_i51648_2_, p_i51648_3_, p_i51648_4_, p_i51648_5_, p_i51648_6_);
      }
   }
}
