package com.mojang.realmsclient.client;

import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Request<T extends Request<T>> {
   protected HttpURLConnection field_224968_a;
   private boolean field_224970_c;
   protected String field_224969_b;

   public Request(String p_i51788_1_, int p_i51788_2_, int p_i51788_3_) {
      try {
         this.field_224969_b = p_i51788_1_;
         Proxy lvt_4_1_ = RealmsClientConfig.func_224895_a();
         if (lvt_4_1_ != null) {
            this.field_224968_a = (HttpURLConnection)(new URL(p_i51788_1_)).openConnection(lvt_4_1_);
         } else {
            this.field_224968_a = (HttpURLConnection)(new URL(p_i51788_1_)).openConnection();
         }

         this.field_224968_a.setConnectTimeout(p_i51788_2_);
         this.field_224968_a.setReadTimeout(p_i51788_3_);
      } catch (MalformedURLException var5) {
         throw new RealmsHttpException(var5.getMessage(), var5);
      } catch (IOException var6) {
         throw new RealmsHttpException(var6.getMessage(), var6);
      }
   }

   public void func_224962_a(String p_224962_1_, String p_224962_2_) {
      func_224967_a(this.field_224968_a, p_224962_1_, p_224962_2_);
   }

   public static void func_224967_a(HttpURLConnection p_224967_0_, String p_224967_1_, String p_224967_2_) {
      String lvt_3_1_ = p_224967_0_.getRequestProperty("Cookie");
      if (lvt_3_1_ == null) {
         p_224967_0_.setRequestProperty("Cookie", p_224967_1_ + "=" + p_224967_2_);
      } else {
         p_224967_0_.setRequestProperty("Cookie", lvt_3_1_ + ";" + p_224967_1_ + "=" + p_224967_2_);
      }

   }

   public int func_224957_a() {
      return func_224964_a(this.field_224968_a);
   }

   public static int func_224964_a(HttpURLConnection p_224964_0_) {
      String lvt_1_1_ = p_224964_0_.getHeaderField("Retry-After");

      try {
         return Integer.valueOf(lvt_1_1_);
      } catch (Exception var3) {
         return 5;
      }
   }

   public int func_224958_b() {
      try {
         this.func_224955_d();
         return this.field_224968_a.getResponseCode();
      } catch (Exception var2) {
         throw new RealmsHttpException(var2.getMessage(), var2);
      }
   }

   public String func_224963_c() {
      try {
         this.func_224955_d();
         String lvt_1_1_ = null;
         if (this.func_224958_b() >= 400) {
            lvt_1_1_ = this.func_224954_a(this.field_224968_a.getErrorStream());
         } else {
            lvt_1_1_ = this.func_224954_a(this.field_224968_a.getInputStream());
         }

         this.func_224950_f();
         return lvt_1_1_;
      } catch (IOException var2) {
         throw new RealmsHttpException(var2.getMessage(), var2);
      }
   }

   private String func_224954_a(InputStream p_224954_1_) throws IOException {
      if (p_224954_1_ == null) {
         return "";
      } else {
         InputStreamReader lvt_2_1_ = new InputStreamReader(p_224954_1_, "UTF-8");
         StringBuilder lvt_3_1_ = new StringBuilder();

         for(int lvt_4_1_ = lvt_2_1_.read(); lvt_4_1_ != -1; lvt_4_1_ = lvt_2_1_.read()) {
            lvt_3_1_.append((char)lvt_4_1_);
         }

         return lvt_3_1_.toString();
      }
   }

   private void func_224950_f() {
      byte[] lvt_1_1_ = new byte[1024];

      try {
         InputStream lvt_3_2_;
         try {
            int lvt_2_1_ = false;
            lvt_3_2_ = this.field_224968_a.getInputStream();

            while(lvt_3_2_.read(lvt_1_1_) > 0) {
            }

            lvt_3_2_.close();
            return;
         } catch (Exception var10) {
            try {
               lvt_3_2_ = this.field_224968_a.getErrorStream();
               int lvt_4_1_ = false;
               if (lvt_3_2_ != null) {
                  while(lvt_3_2_.read(lvt_1_1_) > 0) {
                  }

                  lvt_3_2_.close();
                  return;
               }
            } catch (IOException var9) {
               return;
            }
         }
      } finally {
         if (this.field_224968_a != null) {
            this.field_224968_a.disconnect();
         }

      }

   }

   protected T func_224955_d() {
      if (this.field_224970_c) {
         return this;
      } else {
         T lvt_1_1_ = this.func_223626_e_();
         this.field_224970_c = true;
         return lvt_1_1_;
      }
   }

   protected abstract T func_223626_e_();

   public static Request<?> func_224953_a(String p_224953_0_) {
      return new Request.Get(p_224953_0_, 5000, 60000);
   }

   public static Request<?> func_224960_a(String p_224960_0_, int p_224960_1_, int p_224960_2_) {
      return new Request.Get(p_224960_0_, p_224960_1_, p_224960_2_);
   }

   public static Request<?> func_224951_b(String p_224951_0_, String p_224951_1_) {
      return new Request.Post(p_224951_0_, p_224951_1_, 5000, 60000);
   }

   public static Request<?> func_224959_a(String p_224959_0_, String p_224959_1_, int p_224959_2_, int p_224959_3_) {
      return new Request.Post(p_224959_0_, p_224959_1_, p_224959_2_, p_224959_3_);
   }

   public static Request<?> func_224952_b(String p_224952_0_) {
      return new Request.Delete(p_224952_0_, 5000, 60000);
   }

   public static Request<?> func_224965_c(String p_224965_0_, String p_224965_1_) {
      return new Request.Put(p_224965_0_, p_224965_1_, 5000, 60000);
   }

   public static Request<?> func_224966_b(String p_224966_0_, String p_224966_1_, int p_224966_2_, int p_224966_3_) {
      return new Request.Put(p_224966_0_, p_224966_1_, p_224966_2_, p_224966_3_);
   }

   public String func_224956_c(String p_224956_1_) {
      return func_224961_a(this.field_224968_a, p_224956_1_);
   }

   public static String func_224961_a(HttpURLConnection p_224961_0_, String p_224961_1_) {
      try {
         return p_224961_0_.getHeaderField(p_224961_1_);
      } catch (Exception var3) {
         return "";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Post extends Request<Request.Post> {
      private final String field_224971_c;

      public Post(String p_i51798_1_, String p_i51798_2_, int p_i51798_3_, int p_i51798_4_) {
         super(p_i51798_1_, p_i51798_3_, p_i51798_4_);
         this.field_224971_c = p_i51798_2_;
      }

      public Request.Post func_223626_e_() {
         try {
            if (this.field_224971_c != null) {
               this.field_224968_a.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.field_224968_a.setDoInput(true);
            this.field_224968_a.setDoOutput(true);
            this.field_224968_a.setUseCaches(false);
            this.field_224968_a.setRequestMethod("POST");
            OutputStream lvt_1_1_ = this.field_224968_a.getOutputStream();
            OutputStreamWriter lvt_2_1_ = new OutputStreamWriter(lvt_1_1_, "UTF-8");
            lvt_2_1_.write(this.field_224971_c);
            lvt_2_1_.close();
            lvt_1_1_.flush();
            return this;
         } catch (Exception var3) {
            throw new RealmsHttpException(var3.getMessage(), var3);
         }
      }

      // $FF: synthetic method
      public Request func_223626_e_() {
         return this.func_223626_e_();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Put extends Request<Request.Put> {
      private final String field_224972_c;

      public Put(String p_i51797_1_, String p_i51797_2_, int p_i51797_3_, int p_i51797_4_) {
         super(p_i51797_1_, p_i51797_3_, p_i51797_4_);
         this.field_224972_c = p_i51797_2_;
      }

      public Request.Put func_223626_e_() {
         try {
            if (this.field_224972_c != null) {
               this.field_224968_a.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.field_224968_a.setDoOutput(true);
            this.field_224968_a.setDoInput(true);
            this.field_224968_a.setRequestMethod("PUT");
            OutputStream lvt_1_1_ = this.field_224968_a.getOutputStream();
            OutputStreamWriter lvt_2_1_ = new OutputStreamWriter(lvt_1_1_, "UTF-8");
            lvt_2_1_.write(this.field_224972_c);
            lvt_2_1_.close();
            lvt_1_1_.flush();
            return this;
         } catch (Exception var3) {
            throw new RealmsHttpException(var3.getMessage(), var3);
         }
      }

      // $FF: synthetic method
      public Request func_223626_e_() {
         return this.func_223626_e_();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Get extends Request<Request.Get> {
      public Get(String p_i51799_1_, int p_i51799_2_, int p_i51799_3_) {
         super(p_i51799_1_, p_i51799_2_, p_i51799_3_);
      }

      public Request.Get func_223626_e_() {
         try {
            this.field_224968_a.setDoInput(true);
            this.field_224968_a.setDoOutput(true);
            this.field_224968_a.setUseCaches(false);
            this.field_224968_a.setRequestMethod("GET");
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }

      // $FF: synthetic method
      public Request func_223626_e_() {
         return this.func_223626_e_();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Delete extends Request<Request.Delete> {
      public Delete(String p_i51800_1_, int p_i51800_2_, int p_i51800_3_) {
         super(p_i51800_1_, p_i51800_2_, p_i51800_3_);
      }

      public Request.Delete func_223626_e_() {
         try {
            this.field_224968_a.setDoOutput(true);
            this.field_224968_a.setRequestMethod("DELETE");
            this.field_224968_a.connect();
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }

      // $FF: synthetic method
      public Request func_223626_e_() {
         return this.func_223626_e_();
      }
   }
}
