package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FileUpload {
   private static final Logger field_224883_a = LogManager.getLogger();
   private final File field_224884_b;
   private final long field_224885_c;
   private final int field_224886_d;
   private final UploadInfo field_224887_e;
   private final String field_224888_f;
   private final String field_224889_g;
   private final String field_224890_h;
   private final UploadStatus field_224891_i;
   private final AtomicBoolean field_224892_j = new AtomicBoolean(false);
   private CompletableFuture<UploadResult> field_224893_k;
   private final RequestConfig field_224894_l;

   public FileUpload(File p_i51791_1_, long p_i51791_2_, int p_i51791_4_, UploadInfo p_i51791_5_, String p_i51791_6_, String p_i51791_7_, String p_i51791_8_, UploadStatus p_i51791_9_) {
      this.field_224894_l = RequestConfig.custom().setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L)).setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L)).build();
      this.field_224884_b = p_i51791_1_;
      this.field_224885_c = p_i51791_2_;
      this.field_224886_d = p_i51791_4_;
      this.field_224887_e = p_i51791_5_;
      this.field_224888_f = p_i51791_6_;
      this.field_224889_g = p_i51791_7_;
      this.field_224890_h = p_i51791_8_;
      this.field_224891_i = p_i51791_9_;
   }

   public void func_224874_a(Consumer<UploadResult> p_224874_1_) {
      if (this.field_224893_k == null) {
         this.field_224893_k = CompletableFuture.supplyAsync(() -> {
            return this.func_224879_a(0);
         });
         this.field_224893_k.thenAccept(p_224874_1_);
      }
   }

   public void func_224878_a() {
      this.field_224892_j.set(true);
      if (this.field_224893_k != null) {
         this.field_224893_k.cancel(false);
         this.field_224893_k = null;
      }

   }

   private UploadResult func_224879_a(int p_224879_1_) {
      UploadResult.Builder lvt_2_1_ = new UploadResult.Builder();
      if (this.field_224892_j.get()) {
         return lvt_2_1_.func_225174_a();
      } else {
         this.field_224891_i.field_224979_b = this.field_224884_b.length();
         HttpPost lvt_3_1_ = new HttpPost("http://" + this.field_224887_e.getUploadEndpoint() + ":" + this.field_224887_e.getPort() + "/upload" + "/" + this.field_224885_c + "/" + this.field_224886_d);
         CloseableHttpClient lvt_4_1_ = HttpClientBuilder.create().setDefaultRequestConfig(this.field_224894_l).build();

         try {
            this.func_224872_a(lvt_3_1_);
            HttpResponse lvt_5_1_ = lvt_4_1_.execute(lvt_3_1_);
            long lvt_6_1_ = this.func_224880_a(lvt_5_1_);
            if (this.func_224882_a(lvt_6_1_, p_224879_1_)) {
               UploadResult var8 = this.func_224876_b(lvt_6_1_, p_224879_1_);
               return var8;
            }

            this.func_224875_a(lvt_5_1_, lvt_2_1_);
         } catch (Exception var12) {
            if (!this.field_224892_j.get()) {
               field_224883_a.error("Caught exception while uploading: ", var12);
            }
         } finally {
            this.func_224877_a(lvt_3_1_, lvt_4_1_);
         }

         return lvt_2_1_.func_225174_a();
      }
   }

   private void func_224877_a(HttpPost p_224877_1_, CloseableHttpClient p_224877_2_) {
      p_224877_1_.releaseConnection();
      if (p_224877_2_ != null) {
         try {
            p_224877_2_.close();
         } catch (IOException var4) {
            field_224883_a.error("Failed to close Realms upload client");
         }
      }

   }

   private void func_224872_a(HttpPost p_224872_1_) throws FileNotFoundException {
      p_224872_1_.setHeader("Cookie", "sid=" + this.field_224888_f + ";token=" + this.field_224887_e.getToken() + ";user=" + this.field_224889_g + ";version=" + this.field_224890_h);
      FileUpload.CustomInputStreamEntity lvt_2_1_ = new FileUpload.CustomInputStreamEntity(new FileInputStream(this.field_224884_b), this.field_224884_b.length(), this.field_224891_i);
      lvt_2_1_.setContentType("application/octet-stream");
      p_224872_1_.setEntity(lvt_2_1_);
   }

   private void func_224875_a(HttpResponse p_224875_1_, UploadResult.Builder p_224875_2_) throws IOException {
      int lvt_3_1_ = p_224875_1_.getStatusLine().getStatusCode();
      if (lvt_3_1_ == 401) {
         field_224883_a.debug("Realms server returned 401: " + p_224875_1_.getFirstHeader("WWW-Authenticate"));
      }

      p_224875_2_.func_225175_a(lvt_3_1_);
      if (p_224875_1_.getEntity() != null) {
         String lvt_4_1_ = EntityUtils.toString(p_224875_1_.getEntity(), "UTF-8");
         if (lvt_4_1_ != null) {
            try {
               JsonParser lvt_5_1_ = new JsonParser();
               JsonElement lvt_6_1_ = lvt_5_1_.parse(lvt_4_1_).getAsJsonObject().get("errorMsg");
               Optional<String> lvt_7_1_ = Optional.ofNullable(lvt_6_1_).map(JsonElement::getAsString);
               p_224875_2_.func_225176_a((String)lvt_7_1_.orElse((Object)null));
            } catch (Exception var8) {
            }
         }
      }

   }

   private boolean func_224882_a(long p_224882_1_, int p_224882_3_) {
      return p_224882_1_ > 0L && p_224882_3_ + 1 < 5;
   }

   private UploadResult func_224876_b(long p_224876_1_, int p_224876_3_) throws InterruptedException {
      Thread.sleep(Duration.ofSeconds(p_224876_1_).toMillis());
      return this.func_224879_a(p_224876_3_ + 1);
   }

   private long func_224880_a(HttpResponse p_224880_1_) {
      return (Long)Optional.ofNullable(p_224880_1_.getFirstHeader("Retry-After")).map(Header::getValue).map(Long::valueOf).orElse(0L);
   }

   public boolean func_224881_b() {
      return this.field_224893_k.isDone() || this.field_224893_k.isCancelled();
   }

   @OnlyIn(Dist.CLIENT)
   static class CustomInputStreamEntity extends InputStreamEntity {
      private final long field_224869_a;
      private final InputStream field_224870_b;
      private final UploadStatus field_224871_c;

      public CustomInputStreamEntity(InputStream p_i51622_1_, long p_i51622_2_, UploadStatus p_i51622_4_) {
         super(p_i51622_1_);
         this.field_224870_b = p_i51622_1_;
         this.field_224869_a = p_i51622_2_;
         this.field_224871_c = p_i51622_4_;
      }

      public void writeTo(OutputStream p_writeTo_1_) throws IOException {
         Args.notNull(p_writeTo_1_, "Output stream");
         InputStream lvt_2_1_ = this.field_224870_b;

         try {
            byte[] lvt_3_1_ = new byte[4096];
            int lvt_4_2_;
            if (this.field_224869_a < 0L) {
               while((lvt_4_2_ = lvt_2_1_.read(lvt_3_1_)) != -1) {
                  p_writeTo_1_.write(lvt_3_1_, 0, lvt_4_2_);
                  UploadStatus var11 = this.field_224871_c;
                  var11.field_224978_a = var11.field_224978_a + (long)lvt_4_2_;
               }
            } else {
               long lvt_5_1_ = this.field_224869_a;

               while(lvt_5_1_ > 0L) {
                  lvt_4_2_ = lvt_2_1_.read(lvt_3_1_, 0, (int)Math.min(4096L, lvt_5_1_));
                  if (lvt_4_2_ == -1) {
                     break;
                  }

                  p_writeTo_1_.write(lvt_3_1_, 0, lvt_4_2_);
                  UploadStatus var7 = this.field_224871_c;
                  var7.field_224978_a = var7.field_224978_a + (long)lvt_4_2_;
                  lvt_5_1_ -= (long)lvt_4_2_;
                  p_writeTo_1_.flush();
               }
            }
         } finally {
            lvt_2_1_.close();
         }

      }
   }
}
