package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsTextureManager {
   private static final Map<String, RealmsTextureManager.RealmsTexture> field_225209_a = Maps.newHashMap();
   private static final Map<String, Boolean> field_225210_b = Maps.newHashMap();
   private static final Map<String, String> field_225211_c = Maps.newHashMap();
   private static final Logger field_225212_d = LogManager.getLogger();

   public static void func_225202_a(String p_225202_0_, String p_225202_1_) {
      if (p_225202_1_ == null) {
         RealmsScreen.bind("textures/gui/presets/isles.png");
      } else {
         int lvt_2_1_ = func_225203_b(p_225202_0_, p_225202_1_);
         RenderSystem.bindTexture(lvt_2_1_);
      }
   }

   public static void func_225205_a(String p_225205_0_, Runnable p_225205_1_) {
      RenderSystem.pushTextureAttributes();

      try {
         func_225200_a(p_225205_0_);
         p_225205_1_.run();
      } finally {
         RenderSystem.popAttributes();
      }

   }

   private static void func_225204_a(UUID p_225204_0_) {
      RealmsScreen.bind((p_225204_0_.hashCode() & 1) == 1 ? "minecraft:textures/entity/alex.png" : "minecraft:textures/entity/steve.png");
   }

   private static void func_225200_a(final String p_225200_0_) {
      UUID lvt_1_1_ = UUIDTypeAdapter.fromString(p_225200_0_);
      if (field_225209_a.containsKey(p_225200_0_)) {
         RenderSystem.bindTexture(((RealmsTextureManager.RealmsTexture)field_225209_a.get(p_225200_0_)).field_225198_b);
      } else if (field_225210_b.containsKey(p_225200_0_)) {
         if (!(Boolean)field_225210_b.get(p_225200_0_)) {
            func_225204_a(lvt_1_1_);
         } else if (field_225211_c.containsKey(p_225200_0_)) {
            int lvt_2_1_ = func_225203_b(p_225200_0_, (String)field_225211_c.get(p_225200_0_));
            RenderSystem.bindTexture(lvt_2_1_);
         } else {
            func_225204_a(lvt_1_1_);
         }

      } else {
         field_225210_b.put(p_225200_0_, false);
         func_225204_a(lvt_1_1_);
         Thread lvt_2_2_ = new Thread("Realms Texture Downloader") {
            public void run() {
               Map<Type, MinecraftProfileTexture> lvt_1_1_ = RealmsUtil.func_225191_b(p_225200_0_);
               if (lvt_1_1_.containsKey(Type.SKIN)) {
                  MinecraftProfileTexture lvt_2_1_ = (MinecraftProfileTexture)lvt_1_1_.get(Type.SKIN);
                  String lvt_3_1_ = lvt_2_1_.getUrl();
                  HttpURLConnection lvt_4_1_ = null;
                  RealmsTextureManager.field_225212_d.debug("Downloading http texture from {}", lvt_3_1_);

                  try {
                     lvt_4_1_ = (HttpURLConnection)(new URL(lvt_3_1_)).openConnection(Realms.getProxy());
                     lvt_4_1_.setDoInput(true);
                     lvt_4_1_.setDoOutput(false);
                     lvt_4_1_.connect();
                     if (lvt_4_1_.getResponseCode() / 100 != 2) {
                        RealmsTextureManager.field_225210_b.remove(p_225200_0_);
                        return;
                     }

                     BufferedImage lvt_5_2_;
                     try {
                        lvt_5_2_ = ImageIO.read(lvt_4_1_.getInputStream());
                     } catch (Exception var17) {
                        RealmsTextureManager.field_225210_b.remove(p_225200_0_);
                        return;
                     } finally {
                        IOUtils.closeQuietly(lvt_4_1_.getInputStream());
                     }

                     lvt_5_2_ = (new SkinProcessor()).func_225228_a(lvt_5_2_);
                     ByteArrayOutputStream lvt_6_2_ = new ByteArrayOutputStream();
                     ImageIO.write(lvt_5_2_, "png", lvt_6_2_);
                     RealmsTextureManager.field_225211_c.put(p_225200_0_, (new Base64()).encodeToString(lvt_6_2_.toByteArray()));
                     RealmsTextureManager.field_225210_b.put(p_225200_0_, true);
                  } catch (Exception var19) {
                     RealmsTextureManager.field_225212_d.error("Couldn't download http texture", var19);
                     RealmsTextureManager.field_225210_b.remove(p_225200_0_);
                  } finally {
                     if (lvt_4_1_ != null) {
                        lvt_4_1_.disconnect();
                     }

                  }

               } else {
                  RealmsTextureManager.field_225210_b.put(p_225200_0_, true);
               }
            }
         };
         lvt_2_2_.setDaemon(true);
         lvt_2_2_.start();
      }
   }

   private static int func_225203_b(String p_225203_0_, String p_225203_1_) {
      int lvt_2_2_;
      if (field_225209_a.containsKey(p_225203_0_)) {
         RealmsTextureManager.RealmsTexture lvt_3_1_ = (RealmsTextureManager.RealmsTexture)field_225209_a.get(p_225203_0_);
         if (lvt_3_1_.field_225197_a.equals(p_225203_1_)) {
            return lvt_3_1_.field_225198_b;
         }

         RenderSystem.deleteTexture(lvt_3_1_.field_225198_b);
         lvt_2_2_ = lvt_3_1_.field_225198_b;
      } else {
         lvt_2_2_ = GlStateManager.func_227622_J_();
      }

      IntBuffer lvt_3_2_ = null;
      int lvt_4_1_ = 0;
      int lvt_5_1_ = 0;

      try {
         ByteArrayInputStream lvt_7_1_ = new ByteArrayInputStream((new Base64()).decode(p_225203_1_));

         BufferedImage lvt_6_2_;
         try {
            lvt_6_2_ = ImageIO.read(lvt_7_1_);
         } finally {
            IOUtils.closeQuietly(lvt_7_1_);
         }

         lvt_4_1_ = lvt_6_2_.getWidth();
         lvt_5_1_ = lvt_6_2_.getHeight();
         int[] lvt_8_1_ = new int[lvt_4_1_ * lvt_5_1_];
         lvt_6_2_.getRGB(0, 0, lvt_4_1_, lvt_5_1_, lvt_8_1_, 0, lvt_4_1_);
         lvt_3_2_ = ByteBuffer.allocateDirect(4 * lvt_4_1_ * lvt_5_1_).order(ByteOrder.nativeOrder()).asIntBuffer();
         lvt_3_2_.put(lvt_8_1_);
         lvt_3_2_.flip();
      } catch (IOException var12) {
         var12.printStackTrace();
      }

      RenderSystem.activeTexture(33984);
      RenderSystem.bindTexture(lvt_2_2_);
      TextureUtil.func_225685_a_(lvt_3_2_, lvt_4_1_, lvt_5_1_);
      field_225209_a.put(p_225203_0_, new RealmsTextureManager.RealmsTexture(p_225203_1_, lvt_2_2_));
      return lvt_2_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class RealmsTexture {
      String field_225197_a;
      int field_225198_b;

      public RealmsTexture(String p_i51693_1_, int p_i51693_2_) {
         this.field_225197_a = p_i51693_1_;
         this.field_225198_b = p_i51693_2_;
      }
   }
}
