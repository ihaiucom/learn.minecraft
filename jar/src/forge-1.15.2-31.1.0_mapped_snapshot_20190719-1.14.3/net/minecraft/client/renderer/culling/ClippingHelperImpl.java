package net.minecraft.client.renderer.culling;

import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClippingHelperImpl {
   private final Vector4f[] field_228948_a_ = new Vector4f[6];
   private double field_228949_b_;
   private double field_228950_c_;
   private double field_228951_d_;

   public ClippingHelperImpl(Matrix4f p_i226026_1_, Matrix4f p_i226026_2_) {
      this.func_228956_a_(p_i226026_1_, p_i226026_2_);
   }

   public void func_228952_a_(double p_228952_1_, double p_228952_3_, double p_228952_5_) {
      this.field_228949_b_ = p_228952_1_;
      this.field_228950_c_ = p_228952_3_;
      this.field_228951_d_ = p_228952_5_;
   }

   private void func_228956_a_(Matrix4f p_228956_1_, Matrix4f p_228956_2_) {
      Matrix4f lvt_3_1_ = p_228956_2_.func_226601_d_();
      lvt_3_1_.func_226595_a_(p_228956_1_);
      lvt_3_1_.func_226602_e_();
      this.func_228955_a_(lvt_3_1_, -1, 0, 0, 0);
      this.func_228955_a_(lvt_3_1_, 1, 0, 0, 1);
      this.func_228955_a_(lvt_3_1_, 0, -1, 0, 2);
      this.func_228955_a_(lvt_3_1_, 0, 1, 0, 3);
      this.func_228955_a_(lvt_3_1_, 0, 0, -1, 4);
      this.func_228955_a_(lvt_3_1_, 0, 0, 1, 5);
   }

   private void func_228955_a_(Matrix4f p_228955_1_, int p_228955_2_, int p_228955_3_, int p_228955_4_, int p_228955_5_) {
      Vector4f lvt_6_1_ = new Vector4f((float)p_228955_2_, (float)p_228955_3_, (float)p_228955_4_, 1.0F);
      lvt_6_1_.func_229372_a_(p_228955_1_);
      lvt_6_1_.func_229374_e_();
      this.field_228948_a_[p_228955_5_] = lvt_6_1_;
   }

   public boolean func_228957_a_(AxisAlignedBB p_228957_1_) {
      return this.func_228953_a_(p_228957_1_.minX, p_228957_1_.minY, p_228957_1_.minZ, p_228957_1_.maxX, p_228957_1_.maxY, p_228957_1_.maxZ);
   }

   private boolean func_228953_a_(double p_228953_1_, double p_228953_3_, double p_228953_5_, double p_228953_7_, double p_228953_9_, double p_228953_11_) {
      float lvt_13_1_ = (float)(p_228953_1_ - this.field_228949_b_);
      float lvt_14_1_ = (float)(p_228953_3_ - this.field_228950_c_);
      float lvt_15_1_ = (float)(p_228953_5_ - this.field_228951_d_);
      float lvt_16_1_ = (float)(p_228953_7_ - this.field_228949_b_);
      float lvt_17_1_ = (float)(p_228953_9_ - this.field_228950_c_);
      float lvt_18_1_ = (float)(p_228953_11_ - this.field_228951_d_);
      return this.func_228954_a_(lvt_13_1_, lvt_14_1_, lvt_15_1_, lvt_16_1_, lvt_17_1_, lvt_18_1_);
   }

   private boolean func_228954_a_(float p_228954_1_, float p_228954_2_, float p_228954_3_, float p_228954_4_, float p_228954_5_, float p_228954_6_) {
      for(int lvt_7_1_ = 0; lvt_7_1_ < 6; ++lvt_7_1_) {
         Vector4f lvt_8_1_ = this.field_228948_a_[lvt_7_1_];
         if (lvt_8_1_.func_229373_a_(new Vector4f(p_228954_1_, p_228954_2_, p_228954_3_, 1.0F)) <= 0.0F && lvt_8_1_.func_229373_a_(new Vector4f(p_228954_4_, p_228954_2_, p_228954_3_, 1.0F)) <= 0.0F && lvt_8_1_.func_229373_a_(new Vector4f(p_228954_1_, p_228954_5_, p_228954_3_, 1.0F)) <= 0.0F && lvt_8_1_.func_229373_a_(new Vector4f(p_228954_4_, p_228954_5_, p_228954_3_, 1.0F)) <= 0.0F && lvt_8_1_.func_229373_a_(new Vector4f(p_228954_1_, p_228954_2_, p_228954_6_, 1.0F)) <= 0.0F && lvt_8_1_.func_229373_a_(new Vector4f(p_228954_4_, p_228954_2_, p_228954_6_, 1.0F)) <= 0.0F && lvt_8_1_.func_229373_a_(new Vector4f(p_228954_1_, p_228954_5_, p_228954_6_, 1.0F)) <= 0.0F && lvt_8_1_.func_229373_a_(new Vector4f(p_228954_4_, p_228954_5_, p_228954_6_, 1.0F)) <= 0.0F) {
            return false;
         }
      }

      return true;
   }
}
