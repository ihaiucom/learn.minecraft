package net.minecraft.client.renderer.texture;

import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MipmapGenerator {
   private static final float[] field_229169_a_ = (float[])Util.make(new float[256], (p_229174_0_) -> {
      for(int lvt_1_1_ = 0; lvt_1_1_ < p_229174_0_.length; ++lvt_1_1_) {
         p_229174_0_[lvt_1_1_] = (float)Math.pow((double)((float)lvt_1_1_ / 255.0F), 2.2D);
      }

   });

   public static NativeImage[] func_229173_a_(NativeImage p_229173_0_, int p_229173_1_) {
      NativeImage[] lvt_2_1_ = new NativeImage[p_229173_1_ + 1];
      lvt_2_1_[0] = p_229173_0_;
      if (p_229173_1_ > 0) {
         boolean lvt_3_1_ = false;

         int lvt_4_2_;
         label51:
         for(lvt_4_2_ = 0; lvt_4_2_ < p_229173_0_.getWidth(); ++lvt_4_2_) {
            for(int lvt_5_1_ = 0; lvt_5_1_ < p_229173_0_.getHeight(); ++lvt_5_1_) {
               if (p_229173_0_.getPixelRGBA(lvt_4_2_, lvt_5_1_) >> 24 == 0) {
                  lvt_3_1_ = true;
                  break label51;
               }
            }
         }

         for(lvt_4_2_ = 1; lvt_4_2_ <= p_229173_1_; ++lvt_4_2_) {
            NativeImage lvt_5_2_ = lvt_2_1_[lvt_4_2_ - 1];
            NativeImage lvt_6_1_ = new NativeImage(lvt_5_2_.getWidth() >> 1, lvt_5_2_.getHeight() >> 1, false);
            int lvt_7_1_ = lvt_6_1_.getWidth();
            int lvt_8_1_ = lvt_6_1_.getHeight();

            for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_7_1_; ++lvt_9_1_) {
               for(int lvt_10_1_ = 0; lvt_10_1_ < lvt_8_1_; ++lvt_10_1_) {
                  lvt_6_1_.setPixelRGBA(lvt_9_1_, lvt_10_1_, func_229172_a_(lvt_5_2_.getPixelRGBA(lvt_9_1_ * 2 + 0, lvt_10_1_ * 2 + 0), lvt_5_2_.getPixelRGBA(lvt_9_1_ * 2 + 1, lvt_10_1_ * 2 + 0), lvt_5_2_.getPixelRGBA(lvt_9_1_ * 2 + 0, lvt_10_1_ * 2 + 1), lvt_5_2_.getPixelRGBA(lvt_9_1_ * 2 + 1, lvt_10_1_ * 2 + 1), lvt_3_1_));
               }
            }

            lvt_2_1_[lvt_4_2_] = lvt_6_1_;
         }
      }

      return lvt_2_1_;
   }

   private static int func_229172_a_(int p_229172_0_, int p_229172_1_, int p_229172_2_, int p_229172_3_, boolean p_229172_4_) {
      if (p_229172_4_) {
         float lvt_5_1_ = 0.0F;
         float lvt_6_1_ = 0.0F;
         float lvt_7_1_ = 0.0F;
         float lvt_8_1_ = 0.0F;
         if (p_229172_0_ >> 24 != 0) {
            lvt_5_1_ += func_229170_a_(p_229172_0_ >> 24);
            lvt_6_1_ += func_229170_a_(p_229172_0_ >> 16);
            lvt_7_1_ += func_229170_a_(p_229172_0_ >> 8);
            lvt_8_1_ += func_229170_a_(p_229172_0_ >> 0);
         }

         if (p_229172_1_ >> 24 != 0) {
            lvt_5_1_ += func_229170_a_(p_229172_1_ >> 24);
            lvt_6_1_ += func_229170_a_(p_229172_1_ >> 16);
            lvt_7_1_ += func_229170_a_(p_229172_1_ >> 8);
            lvt_8_1_ += func_229170_a_(p_229172_1_ >> 0);
         }

         if (p_229172_2_ >> 24 != 0) {
            lvt_5_1_ += func_229170_a_(p_229172_2_ >> 24);
            lvt_6_1_ += func_229170_a_(p_229172_2_ >> 16);
            lvt_7_1_ += func_229170_a_(p_229172_2_ >> 8);
            lvt_8_1_ += func_229170_a_(p_229172_2_ >> 0);
         }

         if (p_229172_3_ >> 24 != 0) {
            lvt_5_1_ += func_229170_a_(p_229172_3_ >> 24);
            lvt_6_1_ += func_229170_a_(p_229172_3_ >> 16);
            lvt_7_1_ += func_229170_a_(p_229172_3_ >> 8);
            lvt_8_1_ += func_229170_a_(p_229172_3_ >> 0);
         }

         lvt_5_1_ /= 4.0F;
         lvt_6_1_ /= 4.0F;
         lvt_7_1_ /= 4.0F;
         lvt_8_1_ /= 4.0F;
         int lvt_9_1_ = (int)(Math.pow((double)lvt_5_1_, 0.45454545454545453D) * 255.0D);
         int lvt_10_1_ = (int)(Math.pow((double)lvt_6_1_, 0.45454545454545453D) * 255.0D);
         int lvt_11_1_ = (int)(Math.pow((double)lvt_7_1_, 0.45454545454545453D) * 255.0D);
         int lvt_12_1_ = (int)(Math.pow((double)lvt_8_1_, 0.45454545454545453D) * 255.0D);
         if (lvt_9_1_ < 96) {
            lvt_9_1_ = 0;
         }

         return lvt_9_1_ << 24 | lvt_10_1_ << 16 | lvt_11_1_ << 8 | lvt_12_1_;
      } else {
         int lvt_5_2_ = func_229171_a_(p_229172_0_, p_229172_1_, p_229172_2_, p_229172_3_, 24);
         int lvt_6_2_ = func_229171_a_(p_229172_0_, p_229172_1_, p_229172_2_, p_229172_3_, 16);
         int lvt_7_2_ = func_229171_a_(p_229172_0_, p_229172_1_, p_229172_2_, p_229172_3_, 8);
         int lvt_8_2_ = func_229171_a_(p_229172_0_, p_229172_1_, p_229172_2_, p_229172_3_, 0);
         return lvt_5_2_ << 24 | lvt_6_2_ << 16 | lvt_7_2_ << 8 | lvt_8_2_;
      }
   }

   private static int func_229171_a_(int p_229171_0_, int p_229171_1_, int p_229171_2_, int p_229171_3_, int p_229171_4_) {
      float lvt_5_1_ = func_229170_a_(p_229171_0_ >> p_229171_4_);
      float lvt_6_1_ = func_229170_a_(p_229171_1_ >> p_229171_4_);
      float lvt_7_1_ = func_229170_a_(p_229171_2_ >> p_229171_4_);
      float lvt_8_1_ = func_229170_a_(p_229171_3_ >> p_229171_4_);
      float lvt_9_1_ = (float)((double)((float)Math.pow((double)(lvt_5_1_ + lvt_6_1_ + lvt_7_1_ + lvt_8_1_) * 0.25D, 0.45454545454545453D)));
      return (int)((double)lvt_9_1_ * 255.0D);
   }

   private static float func_229170_a_(int p_229170_0_) {
      return field_229169_a_[p_229170_0_ & 255];
   }
}
