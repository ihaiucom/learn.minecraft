package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsUploadScreen extends RealmsScreen {
   private static final Logger field_224696_a = LogManager.getLogger();
   private final RealmsResetWorldScreen field_224697_b;
   private final RealmsLevelSummary field_224698_c;
   private final long field_224699_d;
   private final int field_224700_e;
   private final UploadStatus field_224701_f;
   private final RateLimiter field_224702_g;
   private volatile String field_224703_h;
   private volatile String field_224704_i;
   private volatile String field_224705_j;
   private volatile boolean field_224706_k;
   private volatile boolean field_224707_l;
   private volatile boolean field_224708_m = true;
   private volatile boolean field_224709_n;
   private RealmsButton field_224710_o;
   private RealmsButton field_224711_p;
   private int field_224712_q;
   private static final String[] field_224713_r = new String[]{"", ".", ". .", ". . ."};
   private int field_224714_s;
   private Long field_224715_t;
   private Long field_224716_u;
   private long field_224717_v;
   private static final ReentrantLock field_224718_w = new ReentrantLock();

   public RealmsUploadScreen(long p_i51747_1_, int p_i51747_3_, RealmsResetWorldScreen p_i51747_4_, RealmsLevelSummary p_i51747_5_) {
      this.field_224699_d = p_i51747_1_;
      this.field_224700_e = p_i51747_3_;
      this.field_224697_b = p_i51747_4_;
      this.field_224698_c = p_i51747_5_;
      this.field_224701_f = new UploadStatus();
      this.field_224702_g = RateLimiter.create(0.10000000149011612D);
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.field_224710_o = new RealmsButton(1, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsUploadScreen.this.func_224679_c();
         }
      };
      this.buttonsAdd(this.field_224711_p = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            RealmsUploadScreen.this.func_224695_d();
         }
      });
      if (!this.field_224709_n) {
         if (this.field_224697_b.field_224455_a == -1) {
            this.func_224682_h();
         } else {
            this.field_224697_b.func_224446_a(this);
         }
      }

   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_1_ && !this.field_224709_n) {
         this.field_224709_n = true;
         Realms.setScreen(this);
         this.func_224682_h();
      }

   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   private void func_224679_c() {
      this.field_224697_b.confirmResult(true, 0);
   }

   private void func_224695_d() {
      this.field_224706_k = true;
      Realms.setScreen(this.field_224697_b);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         if (this.field_224708_m) {
            this.func_224695_d();
         } else {
            this.func_224679_c();
         }

         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      if (!this.field_224707_l && this.field_224701_f.field_224978_a != 0L && this.field_224701_f.field_224978_a == this.field_224701_f.field_224979_b) {
         this.field_224704_i = getLocalizedString("mco.upload.verifying");
         this.field_224711_p.active(false);
      }

      this.drawCenteredString(this.field_224704_i, this.width() / 2, 50, 16777215);
      if (this.field_224708_m) {
         this.func_224678_e();
      }

      if (this.field_224701_f.field_224978_a != 0L && !this.field_224706_k) {
         this.func_224681_f();
         this.func_224664_g();
      }

      if (this.field_224703_h != null) {
         String[] lvt_4_1_ = this.field_224703_h.split("\\\\n");

         for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.length; ++lvt_5_1_) {
            this.drawCenteredString(lvt_4_1_[lvt_5_1_], this.width() / 2, 110 + 12 * lvt_5_1_, 16711680);
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private void func_224678_e() {
      int lvt_1_1_ = this.fontWidth(this.field_224704_i);
      if (this.field_224712_q % 10 == 0) {
         ++this.field_224714_s;
      }

      this.drawString(field_224713_r[this.field_224714_s % field_224713_r.length], this.width() / 2 + lvt_1_1_ / 2 + 5, 50, 16777215);
   }

   private void func_224681_f() {
      double lvt_1_1_ = this.field_224701_f.field_224978_a.doubleValue() / this.field_224701_f.field_224979_b.doubleValue() * 100.0D;
      if (lvt_1_1_ > 100.0D) {
         lvt_1_1_ = 100.0D;
      }

      this.field_224705_j = String.format(Locale.ROOT, "%.1f", lvt_1_1_);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      double lvt_3_1_ = (double)(this.width() / 2 - 100);
      double lvt_5_1_ = 0.5D;
      Tezzelator lvt_7_1_ = Tezzelator.instance;
      lvt_7_1_.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
      lvt_7_1_.vertex(lvt_3_1_ - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      lvt_7_1_.vertex(lvt_3_1_ + 200.0D * lvt_1_1_ / 100.0D + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      lvt_7_1_.vertex(lvt_3_1_ + 200.0D * lvt_1_1_ / 100.0D + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      lvt_7_1_.vertex(lvt_3_1_ - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      lvt_7_1_.vertex(lvt_3_1_, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      lvt_7_1_.vertex(lvt_3_1_ + 200.0D * lvt_1_1_ / 100.0D, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      lvt_7_1_.vertex(lvt_3_1_ + 200.0D * lvt_1_1_ / 100.0D, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      lvt_7_1_.vertex(lvt_3_1_, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      lvt_7_1_.end();
      RenderSystem.enableTexture();
      this.drawCenteredString(this.field_224705_j + " %", this.width() / 2, 84, 16777215);
   }

   private void func_224664_g() {
      if (this.field_224712_q % 20 == 0) {
         if (this.field_224715_t != null) {
            long lvt_1_1_ = System.currentTimeMillis() - this.field_224716_u;
            if (lvt_1_1_ == 0L) {
               lvt_1_1_ = 1L;
            }

            this.field_224717_v = 1000L * (this.field_224701_f.field_224978_a - this.field_224715_t) / lvt_1_1_;
            this.func_224673_c(this.field_224717_v);
         }

         this.field_224715_t = this.field_224701_f.field_224978_a;
         this.field_224716_u = System.currentTimeMillis();
      } else {
         this.func_224673_c(this.field_224717_v);
      }

   }

   private void func_224673_c(long p_224673_1_) {
      if (p_224673_1_ > 0L) {
         int lvt_3_1_ = this.fontWidth(this.field_224705_j);
         String lvt_4_1_ = "(" + func_224671_a(p_224673_1_) + ")";
         this.drawString(lvt_4_1_, this.width() / 2 + lvt_3_1_ / 2 + 15, 84, 16777215);
      }

   }

   public static String func_224671_a(long p_224671_0_) {
      int lvt_2_1_ = true;
      if (p_224671_0_ < 1024L) {
         return p_224671_0_ + " B";
      } else {
         int lvt_3_1_ = (int)(Math.log((double)p_224671_0_) / Math.log(1024.0D));
         String lvt_4_1_ = "KMGTPE".charAt(lvt_3_1_ - 1) + "";
         return String.format(Locale.ROOT, "%.1f %sB/s", (double)p_224671_0_ / Math.pow(1024.0D, (double)lvt_3_1_), lvt_4_1_);
      }
   }

   public void tick() {
      super.tick();
      ++this.field_224712_q;
      if (this.field_224704_i != null && this.field_224702_g.tryAcquire(1)) {
         List<String> lvt_1_1_ = Lists.newArrayList();
         lvt_1_1_.add(this.field_224704_i);
         if (this.field_224705_j != null) {
            lvt_1_1_.add(this.field_224705_j + "%");
         }

         if (this.field_224703_h != null) {
            lvt_1_1_.add(this.field_224703_h);
         }

         Realms.narrateNow(String.join(System.lineSeparator(), lvt_1_1_));
      }

   }

   public static RealmsUploadScreen.Unit func_224665_b(long p_224665_0_) {
      if (p_224665_0_ < 1024L) {
         return RealmsUploadScreen.Unit.B;
      } else {
         int lvt_2_1_ = (int)(Math.log((double)p_224665_0_) / Math.log(1024.0D));
         String lvt_3_1_ = "KMGTPE".charAt(lvt_2_1_ - 1) + "";

         try {
            return RealmsUploadScreen.Unit.valueOf(lvt_3_1_ + "B");
         } catch (Exception var5) {
            return RealmsUploadScreen.Unit.GB;
         }
      }
   }

   public static double func_224691_a(long p_224691_0_, RealmsUploadScreen.Unit p_224691_2_) {
      return p_224691_2_.equals(RealmsUploadScreen.Unit.B) ? (double)p_224691_0_ : (double)p_224691_0_ / Math.pow(1024.0D, (double)p_224691_2_.ordinal());
   }

   public static String func_224667_b(long p_224667_0_, RealmsUploadScreen.Unit p_224667_2_) {
      return String.format("%." + (p_224667_2_.equals(RealmsUploadScreen.Unit.GB) ? "1" : "0") + "f %s", func_224691_a(p_224667_0_, p_224667_2_), p_224667_2_.name());
   }

   private void func_224682_h() {
      this.field_224709_n = true;
      (new Thread(() -> {
         File lvt_1_1_ = null;
         RealmsClient lvt_2_1_ = RealmsClient.func_224911_a();
         long lvt_3_1_ = this.field_224699_d;

         try {
            if (!field_224718_w.tryLock(1L, TimeUnit.SECONDS)) {
               return;
            }

            this.field_224704_i = getLocalizedString("mco.upload.preparing");
            UploadInfo lvt_5_1_ = null;
            int lvt_6_1_ = 0;

            while(lvt_6_1_ < 20) {
               try {
                  if (this.field_224706_k) {
                     this.func_224676_i();
                     return;
                  }

                  lvt_5_1_ = lvt_2_1_.func_224934_h(lvt_3_1_, UploadTokenCache.func_225235_a(lvt_3_1_));
                  break;
               } catch (RetryCallException var20) {
                  Thread.sleep((long)(var20.field_224985_e * 1000));
                  ++lvt_6_1_;
               }
            }

            if (lvt_5_1_ == null) {
               this.field_224704_i = getLocalizedString("mco.upload.close.failure");
               return;
            }

            UploadTokenCache.func_225234_a(lvt_3_1_, lvt_5_1_.getToken());
            if (!lvt_5_1_.isWorldClosed()) {
               this.field_224704_i = getLocalizedString("mco.upload.close.failure");
               return;
            }

            if (this.field_224706_k) {
               this.func_224676_i();
               return;
            }

            File lvt_6_2_ = new File(Realms.getGameDirectoryPath(), "saves");
            lvt_1_1_ = this.func_224675_b(new File(lvt_6_2_, this.field_224698_c.getLevelId()));
            if (this.field_224706_k) {
               this.func_224676_i();
               return;
            }

            if (this.func_224692_a(lvt_1_1_)) {
               this.field_224704_i = getLocalizedString("mco.upload.uploading", new Object[]{this.field_224698_c.getLevelName()});
               FileUpload lvt_7_3_ = new FileUpload(lvt_1_1_, this.field_224699_d, this.field_224700_e, lvt_5_1_, Realms.getSessionId(), Realms.getName(), Realms.getMinecraftVersionString(), this.field_224701_f);
               lvt_7_3_.func_224874_a((p_227992_3_) -> {
                  if (p_227992_3_.field_225179_a >= 200 && p_227992_3_.field_225179_a < 300) {
                     this.field_224707_l = true;
                     this.field_224704_i = getLocalizedString("mco.upload.done");
                     this.field_224710_o.setMessage(getLocalizedString("gui.done"));
                     UploadTokenCache.func_225233_b(lvt_3_1_);
                  } else if (p_227992_3_.field_225179_a == 400 && p_227992_3_.field_225180_b != null) {
                     this.field_224703_h = getLocalizedString("mco.upload.failed", new Object[]{p_227992_3_.field_225180_b});
                  } else {
                     this.field_224703_h = getLocalizedString("mco.upload.failed", new Object[]{p_227992_3_.field_225179_a});
                  }

               });

               while(!lvt_7_3_.func_224881_b()) {
                  if (this.field_224706_k) {
                     lvt_7_3_.func_224878_a();
                     this.func_224676_i();
                     return;
                  }

                  try {
                     Thread.sleep(500L);
                  } catch (InterruptedException var19) {
                     field_224696_a.error("Failed to check Realms file upload status");
                  }
               }

               return;
            }

            long lvt_7_2_ = lvt_1_1_.length();
            RealmsUploadScreen.Unit lvt_9_1_ = func_224665_b(lvt_7_2_);
            RealmsUploadScreen.Unit lvt_10_1_ = func_224665_b(5368709120L);
            if (func_224667_b(lvt_7_2_, lvt_9_1_).equals(func_224667_b(5368709120L, lvt_10_1_)) && lvt_9_1_ != RealmsUploadScreen.Unit.B) {
               RealmsUploadScreen.Unit lvt_11_1_ = RealmsUploadScreen.Unit.values()[lvt_9_1_.ordinal() - 1];
               this.field_224703_h = getLocalizedString("mco.upload.size.failure.line1", new Object[]{this.field_224698_c.getLevelName()}) + "\\n" + getLocalizedString("mco.upload.size.failure.line2", new Object[]{func_224667_b(lvt_7_2_, lvt_11_1_), func_224667_b(5368709120L, lvt_11_1_)});
               return;
            }

            this.field_224703_h = getLocalizedString("mco.upload.size.failure.line1", new Object[]{this.field_224698_c.getLevelName()}) + "\\n" + getLocalizedString("mco.upload.size.failure.line2", new Object[]{func_224667_b(lvt_7_2_, lvt_9_1_), func_224667_b(5368709120L, lvt_10_1_)});
         } catch (IOException var21) {
            this.field_224703_h = getLocalizedString("mco.upload.failed", new Object[]{var21.getMessage()});
            return;
         } catch (RealmsServiceException var22) {
            this.field_224703_h = getLocalizedString("mco.upload.failed", new Object[]{var22.toString()});
            return;
         } catch (InterruptedException var23) {
            field_224696_a.error("Could not acquire upload lock");
            return;
         } finally {
            this.field_224707_l = true;
            if (field_224718_w.isHeldByCurrentThread()) {
               field_224718_w.unlock();
               this.field_224708_m = false;
               this.childrenClear();
               this.buttonsAdd(this.field_224710_o);
               if (lvt_1_1_ != null) {
                  field_224696_a.debug("Deleting file " + lvt_1_1_.getAbsolutePath());
                  lvt_1_1_.delete();
               }

            }

            return;
         }

      })).start();
   }

   private void func_224676_i() {
      this.field_224704_i = getLocalizedString("mco.upload.cancelled");
      field_224696_a.debug("Upload was cancelled");
   }

   private boolean func_224692_a(File p_224692_1_) {
      return p_224692_1_.length() < 5368709120L;
   }

   private File func_224675_b(File p_224675_1_) throws IOException {
      TarArchiveOutputStream lvt_2_1_ = null;

      File var4;
      try {
         File lvt_3_1_ = File.createTempFile("realms-upload-file", ".tar.gz");
         lvt_2_1_ = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(lvt_3_1_)));
         lvt_2_1_.setLongFileMode(3);
         this.func_224669_a(lvt_2_1_, p_224675_1_.getAbsolutePath(), "world", true);
         lvt_2_1_.finish();
         var4 = lvt_3_1_;
      } finally {
         if (lvt_2_1_ != null) {
            lvt_2_1_.close();
         }

      }

      return var4;
   }

   private void func_224669_a(TarArchiveOutputStream p_224669_1_, String p_224669_2_, String p_224669_3_, boolean p_224669_4_) throws IOException {
      if (!this.field_224706_k) {
         File lvt_5_1_ = new File(p_224669_2_);
         String lvt_6_1_ = p_224669_4_ ? p_224669_3_ : p_224669_3_ + lvt_5_1_.getName();
         TarArchiveEntry lvt_7_1_ = new TarArchiveEntry(lvt_5_1_, lvt_6_1_);
         p_224669_1_.putArchiveEntry(lvt_7_1_);
         if (lvt_5_1_.isFile()) {
            IOUtils.copy(new FileInputStream(lvt_5_1_), p_224669_1_);
            p_224669_1_.closeArchiveEntry();
         } else {
            p_224669_1_.closeArchiveEntry();
            File[] lvt_8_1_ = lvt_5_1_.listFiles();
            if (lvt_8_1_ != null) {
               File[] var9 = lvt_8_1_;
               int var10 = lvt_8_1_.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  File lvt_12_1_ = var9[var11];
                  this.func_224669_a(p_224669_1_, lvt_12_1_.getAbsolutePath(), lvt_6_1_ + "/", false);
               }
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum Unit {
      B,
      KB,
      MB,
      GB;
   }
}
