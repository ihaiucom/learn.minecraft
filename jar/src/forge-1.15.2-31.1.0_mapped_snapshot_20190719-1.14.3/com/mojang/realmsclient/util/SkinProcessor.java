package com.mojang.realmsclient.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinProcessor {
   private int[] field_225230_a;
   private int field_225231_b;
   private int field_225232_c;

   @Nullable
   public BufferedImage func_225228_a(BufferedImage p_225228_1_) {
      if (p_225228_1_ == null) {
         return null;
      } else {
         this.field_225231_b = 64;
         this.field_225232_c = 64;
         BufferedImage lvt_2_1_ = new BufferedImage(this.field_225231_b, this.field_225232_c, 2);
         Graphics lvt_3_1_ = lvt_2_1_.getGraphics();
         lvt_3_1_.drawImage(p_225228_1_, 0, 0, (ImageObserver)null);
         boolean lvt_4_1_ = p_225228_1_.getHeight() == 32;
         if (lvt_4_1_) {
            lvt_3_1_.setColor(new Color(0, 0, 0, 0));
            lvt_3_1_.fillRect(0, 32, 64, 32);
            lvt_3_1_.drawImage(lvt_2_1_, 24, 48, 20, 52, 4, 16, 8, 20, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 28, 48, 24, 52, 8, 16, 12, 20, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 20, 52, 16, 64, 8, 20, 12, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 24, 52, 20, 64, 4, 20, 8, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 28, 52, 24, 64, 0, 20, 4, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 32, 52, 28, 64, 12, 20, 16, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 40, 48, 36, 52, 44, 16, 48, 20, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 44, 48, 40, 52, 48, 16, 52, 20, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 36, 52, 32, 64, 48, 20, 52, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 40, 52, 36, 64, 44, 20, 48, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 44, 52, 40, 64, 40, 20, 44, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 48, 52, 44, 64, 52, 20, 56, 32, (ImageObserver)null);
         }

         lvt_3_1_.dispose();
         this.field_225230_a = ((DataBufferInt)lvt_2_1_.getRaster().getDataBuffer()).getData();
         this.func_225229_b(0, 0, 32, 16);
         if (lvt_4_1_) {
            this.func_225227_a(32, 0, 64, 32);
         }

         this.func_225229_b(0, 16, 64, 32);
         this.func_225229_b(16, 48, 48, 64);
         return lvt_2_1_;
      }
   }

   private void func_225227_a(int p_225227_1_, int p_225227_2_, int p_225227_3_, int p_225227_4_) {
      int lvt_5_2_;
      int lvt_6_2_;
      for(lvt_5_2_ = p_225227_1_; lvt_5_2_ < p_225227_3_; ++lvt_5_2_) {
         for(lvt_6_2_ = p_225227_2_; lvt_6_2_ < p_225227_4_; ++lvt_6_2_) {
            int lvt_7_1_ = this.field_225230_a[lvt_5_2_ + lvt_6_2_ * this.field_225231_b];
            if ((lvt_7_1_ >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(lvt_5_2_ = p_225227_1_; lvt_5_2_ < p_225227_3_; ++lvt_5_2_) {
         for(lvt_6_2_ = p_225227_2_; lvt_6_2_ < p_225227_4_; ++lvt_6_2_) {
            int[] var10000 = this.field_225230_a;
            int var10001 = lvt_5_2_ + lvt_6_2_ * this.field_225231_b;
            var10000[var10001] &= 16777215;
         }
      }

   }

   private void func_225229_b(int p_225229_1_, int p_225229_2_, int p_225229_3_, int p_225229_4_) {
      for(int lvt_5_1_ = p_225229_1_; lvt_5_1_ < p_225229_3_; ++lvt_5_1_) {
         for(int lvt_6_1_ = p_225229_2_; lvt_6_1_ < p_225229_4_; ++lvt_6_1_) {
            int[] var10000 = this.field_225230_a;
            int var10001 = lvt_5_1_ + lvt_6_1_ * this.field_225231_b;
            var10000[var10001] |= -16777216;
         }
      }

   }
}
