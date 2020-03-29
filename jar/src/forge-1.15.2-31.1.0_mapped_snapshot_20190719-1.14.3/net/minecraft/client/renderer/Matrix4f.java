package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class Matrix4f {
   protected float field_226575_a_;
   protected float field_226576_b_;
   protected float field_226577_c_;
   protected float field_226578_d_;
   protected float field_226579_e_;
   protected float field_226580_f_;
   protected float field_226581_g_;
   protected float field_226582_h_;
   protected float field_226583_i_;
   protected float field_226584_j_;
   protected float field_226585_k_;
   protected float field_226586_l_;
   protected float field_226587_m_;
   protected float field_226588_n_;
   protected float field_226589_o_;
   protected float field_226590_p_;

   public Matrix4f() {
   }

   public Matrix4f(Matrix4f p_i48105_1_) {
      this.field_226575_a_ = p_i48105_1_.field_226575_a_;
      this.field_226576_b_ = p_i48105_1_.field_226576_b_;
      this.field_226577_c_ = p_i48105_1_.field_226577_c_;
      this.field_226578_d_ = p_i48105_1_.field_226578_d_;
      this.field_226579_e_ = p_i48105_1_.field_226579_e_;
      this.field_226580_f_ = p_i48105_1_.field_226580_f_;
      this.field_226581_g_ = p_i48105_1_.field_226581_g_;
      this.field_226582_h_ = p_i48105_1_.field_226582_h_;
      this.field_226583_i_ = p_i48105_1_.field_226583_i_;
      this.field_226584_j_ = p_i48105_1_.field_226584_j_;
      this.field_226585_k_ = p_i48105_1_.field_226585_k_;
      this.field_226586_l_ = p_i48105_1_.field_226586_l_;
      this.field_226587_m_ = p_i48105_1_.field_226587_m_;
      this.field_226588_n_ = p_i48105_1_.field_226588_n_;
      this.field_226589_o_ = p_i48105_1_.field_226589_o_;
      this.field_226590_p_ = p_i48105_1_.field_226590_p_;
   }

   public Matrix4f(Quaternion p_i48104_1_) {
      float f = p_i48104_1_.getX();
      float f1 = p_i48104_1_.getY();
      float f2 = p_i48104_1_.getZ();
      float f3 = p_i48104_1_.getW();
      float f4 = 2.0F * f * f;
      float f5 = 2.0F * f1 * f1;
      float f6 = 2.0F * f2 * f2;
      this.field_226575_a_ = 1.0F - f5 - f6;
      this.field_226580_f_ = 1.0F - f6 - f4;
      this.field_226585_k_ = 1.0F - f4 - f5;
      this.field_226590_p_ = 1.0F;
      float f7 = f * f1;
      float f8 = f1 * f2;
      float f9 = f2 * f;
      float f10 = f * f3;
      float f11 = f1 * f3;
      float f12 = f2 * f3;
      this.field_226579_e_ = 2.0F * (f7 + f12);
      this.field_226576_b_ = 2.0F * (f7 - f12);
      this.field_226583_i_ = 2.0F * (f9 - f11);
      this.field_226577_c_ = 2.0F * (f9 + f11);
      this.field_226584_j_ = 2.0F * (f8 + f10);
      this.field_226581_g_ = 2.0F * (f8 - f10);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Matrix4f matrix4f = (Matrix4f)p_equals_1_;
         return Float.compare(matrix4f.field_226575_a_, this.field_226575_a_) == 0 && Float.compare(matrix4f.field_226576_b_, this.field_226576_b_) == 0 && Float.compare(matrix4f.field_226577_c_, this.field_226577_c_) == 0 && Float.compare(matrix4f.field_226578_d_, this.field_226578_d_) == 0 && Float.compare(matrix4f.field_226579_e_, this.field_226579_e_) == 0 && Float.compare(matrix4f.field_226580_f_, this.field_226580_f_) == 0 && Float.compare(matrix4f.field_226581_g_, this.field_226581_g_) == 0 && Float.compare(matrix4f.field_226582_h_, this.field_226582_h_) == 0 && Float.compare(matrix4f.field_226583_i_, this.field_226583_i_) == 0 && Float.compare(matrix4f.field_226584_j_, this.field_226584_j_) == 0 && Float.compare(matrix4f.field_226585_k_, this.field_226585_k_) == 0 && Float.compare(matrix4f.field_226586_l_, this.field_226586_l_) == 0 && Float.compare(matrix4f.field_226587_m_, this.field_226587_m_) == 0 && Float.compare(matrix4f.field_226588_n_, this.field_226588_n_) == 0 && Float.compare(matrix4f.field_226589_o_, this.field_226589_o_) == 0 && Float.compare(matrix4f.field_226590_p_, this.field_226590_p_) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.field_226575_a_ != 0.0F ? Float.floatToIntBits(this.field_226575_a_) : 0;
      i = 31 * i + (this.field_226576_b_ != 0.0F ? Float.floatToIntBits(this.field_226576_b_) : 0);
      i = 31 * i + (this.field_226577_c_ != 0.0F ? Float.floatToIntBits(this.field_226577_c_) : 0);
      i = 31 * i + (this.field_226578_d_ != 0.0F ? Float.floatToIntBits(this.field_226578_d_) : 0);
      i = 31 * i + (this.field_226579_e_ != 0.0F ? Float.floatToIntBits(this.field_226579_e_) : 0);
      i = 31 * i + (this.field_226580_f_ != 0.0F ? Float.floatToIntBits(this.field_226580_f_) : 0);
      i = 31 * i + (this.field_226581_g_ != 0.0F ? Float.floatToIntBits(this.field_226581_g_) : 0);
      i = 31 * i + (this.field_226582_h_ != 0.0F ? Float.floatToIntBits(this.field_226582_h_) : 0);
      i = 31 * i + (this.field_226583_i_ != 0.0F ? Float.floatToIntBits(this.field_226583_i_) : 0);
      i = 31 * i + (this.field_226584_j_ != 0.0F ? Float.floatToIntBits(this.field_226584_j_) : 0);
      i = 31 * i + (this.field_226585_k_ != 0.0F ? Float.floatToIntBits(this.field_226585_k_) : 0);
      i = 31 * i + (this.field_226586_l_ != 0.0F ? Float.floatToIntBits(this.field_226586_l_) : 0);
      i = 31 * i + (this.field_226587_m_ != 0.0F ? Float.floatToIntBits(this.field_226587_m_) : 0);
      i = 31 * i + (this.field_226588_n_ != 0.0F ? Float.floatToIntBits(this.field_226588_n_) : 0);
      i = 31 * i + (this.field_226589_o_ != 0.0F ? Float.floatToIntBits(this.field_226589_o_) : 0);
      i = 31 * i + (this.field_226590_p_ != 0.0F ? Float.floatToIntBits(this.field_226590_p_) : 0);
      return i;
   }

   private static int func_226594_a_(int p_226594_0_, int p_226594_1_) {
      return p_226594_1_ * 4 + p_226594_0_;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Matrix4f:\n");
      stringbuilder.append(this.field_226575_a_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226576_b_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226577_c_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226578_d_);
      stringbuilder.append("\n");
      stringbuilder.append(this.field_226579_e_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226580_f_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226581_g_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226582_h_);
      stringbuilder.append("\n");
      stringbuilder.append(this.field_226583_i_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226584_j_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226585_k_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226586_l_);
      stringbuilder.append("\n");
      stringbuilder.append(this.field_226587_m_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226588_n_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226589_o_);
      stringbuilder.append(" ");
      stringbuilder.append(this.field_226590_p_);
      stringbuilder.append("\n");
      return stringbuilder.toString();
   }

   public void write(FloatBuffer p_195879_1_) {
      p_195879_1_.put(func_226594_a_(0, 0), this.field_226575_a_);
      p_195879_1_.put(func_226594_a_(0, 1), this.field_226576_b_);
      p_195879_1_.put(func_226594_a_(0, 2), this.field_226577_c_);
      p_195879_1_.put(func_226594_a_(0, 3), this.field_226578_d_);
      p_195879_1_.put(func_226594_a_(1, 0), this.field_226579_e_);
      p_195879_1_.put(func_226594_a_(1, 1), this.field_226580_f_);
      p_195879_1_.put(func_226594_a_(1, 2), this.field_226581_g_);
      p_195879_1_.put(func_226594_a_(1, 3), this.field_226582_h_);
      p_195879_1_.put(func_226594_a_(2, 0), this.field_226583_i_);
      p_195879_1_.put(func_226594_a_(2, 1), this.field_226584_j_);
      p_195879_1_.put(func_226594_a_(2, 2), this.field_226585_k_);
      p_195879_1_.put(func_226594_a_(2, 3), this.field_226586_l_);
      p_195879_1_.put(func_226594_a_(3, 0), this.field_226587_m_);
      p_195879_1_.put(func_226594_a_(3, 1), this.field_226588_n_);
      p_195879_1_.put(func_226594_a_(3, 2), this.field_226589_o_);
      p_195879_1_.put(func_226594_a_(3, 3), this.field_226590_p_);
   }

   public void func_226591_a_() {
      this.field_226575_a_ = 1.0F;
      this.field_226576_b_ = 0.0F;
      this.field_226577_c_ = 0.0F;
      this.field_226578_d_ = 0.0F;
      this.field_226579_e_ = 0.0F;
      this.field_226580_f_ = 1.0F;
      this.field_226581_g_ = 0.0F;
      this.field_226582_h_ = 0.0F;
      this.field_226583_i_ = 0.0F;
      this.field_226584_j_ = 0.0F;
      this.field_226585_k_ = 1.0F;
      this.field_226586_l_ = 0.0F;
      this.field_226587_m_ = 0.0F;
      this.field_226588_n_ = 0.0F;
      this.field_226589_o_ = 0.0F;
      this.field_226590_p_ = 1.0F;
   }

   public float func_226598_b_() {
      float f = this.field_226575_a_ * this.field_226580_f_ - this.field_226576_b_ * this.field_226579_e_;
      float f1 = this.field_226575_a_ * this.field_226581_g_ - this.field_226577_c_ * this.field_226579_e_;
      float f2 = this.field_226575_a_ * this.field_226582_h_ - this.field_226578_d_ * this.field_226579_e_;
      float f3 = this.field_226576_b_ * this.field_226581_g_ - this.field_226577_c_ * this.field_226580_f_;
      float f4 = this.field_226576_b_ * this.field_226582_h_ - this.field_226578_d_ * this.field_226580_f_;
      float f5 = this.field_226577_c_ * this.field_226582_h_ - this.field_226578_d_ * this.field_226581_g_;
      float f6 = this.field_226583_i_ * this.field_226588_n_ - this.field_226584_j_ * this.field_226587_m_;
      float f7 = this.field_226583_i_ * this.field_226589_o_ - this.field_226585_k_ * this.field_226587_m_;
      float f8 = this.field_226583_i_ * this.field_226590_p_ - this.field_226586_l_ * this.field_226587_m_;
      float f9 = this.field_226584_j_ * this.field_226589_o_ - this.field_226585_k_ * this.field_226588_n_;
      float f10 = this.field_226584_j_ * this.field_226590_p_ - this.field_226586_l_ * this.field_226588_n_;
      float f11 = this.field_226585_k_ * this.field_226590_p_ - this.field_226586_l_ * this.field_226589_o_;
      float f12 = this.field_226580_f_ * f11 - this.field_226581_g_ * f10 + this.field_226582_h_ * f9;
      float f13 = -this.field_226579_e_ * f11 + this.field_226581_g_ * f8 - this.field_226582_h_ * f7;
      float f14 = this.field_226579_e_ * f10 - this.field_226580_f_ * f8 + this.field_226582_h_ * f6;
      float f15 = -this.field_226579_e_ * f9 + this.field_226580_f_ * f7 - this.field_226581_g_ * f6;
      float f16 = -this.field_226576_b_ * f11 + this.field_226577_c_ * f10 - this.field_226578_d_ * f9;
      float f17 = this.field_226575_a_ * f11 - this.field_226577_c_ * f8 + this.field_226578_d_ * f7;
      float f18 = -this.field_226575_a_ * f10 + this.field_226576_b_ * f8 - this.field_226578_d_ * f6;
      float f19 = this.field_226575_a_ * f9 - this.field_226576_b_ * f7 + this.field_226577_c_ * f6;
      float f20 = this.field_226588_n_ * f5 - this.field_226589_o_ * f4 + this.field_226590_p_ * f3;
      float f21 = -this.field_226587_m_ * f5 + this.field_226589_o_ * f2 - this.field_226590_p_ * f1;
      float f22 = this.field_226587_m_ * f4 - this.field_226588_n_ * f2 + this.field_226590_p_ * f;
      float f23 = -this.field_226587_m_ * f3 + this.field_226588_n_ * f1 - this.field_226589_o_ * f;
      float f24 = -this.field_226584_j_ * f5 + this.field_226585_k_ * f4 - this.field_226586_l_ * f3;
      float f25 = this.field_226583_i_ * f5 - this.field_226585_k_ * f2 + this.field_226586_l_ * f1;
      float f26 = -this.field_226583_i_ * f4 + this.field_226584_j_ * f2 - this.field_226586_l_ * f;
      float f27 = this.field_226583_i_ * f3 - this.field_226584_j_ * f1 + this.field_226585_k_ * f;
      this.field_226575_a_ = f12;
      this.field_226579_e_ = f13;
      this.field_226583_i_ = f14;
      this.field_226587_m_ = f15;
      this.field_226576_b_ = f16;
      this.field_226580_f_ = f17;
      this.field_226584_j_ = f18;
      this.field_226588_n_ = f19;
      this.field_226577_c_ = f20;
      this.field_226581_g_ = f21;
      this.field_226585_k_ = f22;
      this.field_226589_o_ = f23;
      this.field_226578_d_ = f24;
      this.field_226582_h_ = f25;
      this.field_226586_l_ = f26;
      this.field_226590_p_ = f27;
      return f * f11 - f1 * f10 + f2 * f9 + f3 * f8 - f4 * f7 + f5 * f6;
   }

   public void func_226602_e_() {
      float f = this.field_226579_e_;
      this.field_226579_e_ = this.field_226576_b_;
      this.field_226576_b_ = f;
      f = this.field_226583_i_;
      this.field_226583_i_ = this.field_226577_c_;
      this.field_226577_c_ = f;
      f = this.field_226584_j_;
      this.field_226584_j_ = this.field_226581_g_;
      this.field_226581_g_ = f;
      f = this.field_226587_m_;
      this.field_226587_m_ = this.field_226578_d_;
      this.field_226578_d_ = f;
      f = this.field_226588_n_;
      this.field_226588_n_ = this.field_226582_h_;
      this.field_226582_h_ = f;
      f = this.field_226589_o_;
      this.field_226589_o_ = this.field_226586_l_;
      this.field_226586_l_ = f;
   }

   public boolean func_226600_c_() {
      float f = this.func_226598_b_();
      if (Math.abs(f) > 1.0E-6F) {
         this.func_226592_a_(f);
         return true;
      } else {
         return false;
      }
   }

   public void func_226595_a_(Matrix4f p_226595_1_) {
      float f = this.field_226575_a_ * p_226595_1_.field_226575_a_ + this.field_226576_b_ * p_226595_1_.field_226579_e_ + this.field_226577_c_ * p_226595_1_.field_226583_i_ + this.field_226578_d_ * p_226595_1_.field_226587_m_;
      float f1 = this.field_226575_a_ * p_226595_1_.field_226576_b_ + this.field_226576_b_ * p_226595_1_.field_226580_f_ + this.field_226577_c_ * p_226595_1_.field_226584_j_ + this.field_226578_d_ * p_226595_1_.field_226588_n_;
      float f2 = this.field_226575_a_ * p_226595_1_.field_226577_c_ + this.field_226576_b_ * p_226595_1_.field_226581_g_ + this.field_226577_c_ * p_226595_1_.field_226585_k_ + this.field_226578_d_ * p_226595_1_.field_226589_o_;
      float f3 = this.field_226575_a_ * p_226595_1_.field_226578_d_ + this.field_226576_b_ * p_226595_1_.field_226582_h_ + this.field_226577_c_ * p_226595_1_.field_226586_l_ + this.field_226578_d_ * p_226595_1_.field_226590_p_;
      float f4 = this.field_226579_e_ * p_226595_1_.field_226575_a_ + this.field_226580_f_ * p_226595_1_.field_226579_e_ + this.field_226581_g_ * p_226595_1_.field_226583_i_ + this.field_226582_h_ * p_226595_1_.field_226587_m_;
      float f5 = this.field_226579_e_ * p_226595_1_.field_226576_b_ + this.field_226580_f_ * p_226595_1_.field_226580_f_ + this.field_226581_g_ * p_226595_1_.field_226584_j_ + this.field_226582_h_ * p_226595_1_.field_226588_n_;
      float f6 = this.field_226579_e_ * p_226595_1_.field_226577_c_ + this.field_226580_f_ * p_226595_1_.field_226581_g_ + this.field_226581_g_ * p_226595_1_.field_226585_k_ + this.field_226582_h_ * p_226595_1_.field_226589_o_;
      float f7 = this.field_226579_e_ * p_226595_1_.field_226578_d_ + this.field_226580_f_ * p_226595_1_.field_226582_h_ + this.field_226581_g_ * p_226595_1_.field_226586_l_ + this.field_226582_h_ * p_226595_1_.field_226590_p_;
      float f8 = this.field_226583_i_ * p_226595_1_.field_226575_a_ + this.field_226584_j_ * p_226595_1_.field_226579_e_ + this.field_226585_k_ * p_226595_1_.field_226583_i_ + this.field_226586_l_ * p_226595_1_.field_226587_m_;
      float f9 = this.field_226583_i_ * p_226595_1_.field_226576_b_ + this.field_226584_j_ * p_226595_1_.field_226580_f_ + this.field_226585_k_ * p_226595_1_.field_226584_j_ + this.field_226586_l_ * p_226595_1_.field_226588_n_;
      float f10 = this.field_226583_i_ * p_226595_1_.field_226577_c_ + this.field_226584_j_ * p_226595_1_.field_226581_g_ + this.field_226585_k_ * p_226595_1_.field_226585_k_ + this.field_226586_l_ * p_226595_1_.field_226589_o_;
      float f11 = this.field_226583_i_ * p_226595_1_.field_226578_d_ + this.field_226584_j_ * p_226595_1_.field_226582_h_ + this.field_226585_k_ * p_226595_1_.field_226586_l_ + this.field_226586_l_ * p_226595_1_.field_226590_p_;
      float f12 = this.field_226587_m_ * p_226595_1_.field_226575_a_ + this.field_226588_n_ * p_226595_1_.field_226579_e_ + this.field_226589_o_ * p_226595_1_.field_226583_i_ + this.field_226590_p_ * p_226595_1_.field_226587_m_;
      float f13 = this.field_226587_m_ * p_226595_1_.field_226576_b_ + this.field_226588_n_ * p_226595_1_.field_226580_f_ + this.field_226589_o_ * p_226595_1_.field_226584_j_ + this.field_226590_p_ * p_226595_1_.field_226588_n_;
      float f14 = this.field_226587_m_ * p_226595_1_.field_226577_c_ + this.field_226588_n_ * p_226595_1_.field_226581_g_ + this.field_226589_o_ * p_226595_1_.field_226585_k_ + this.field_226590_p_ * p_226595_1_.field_226589_o_;
      float f15 = this.field_226587_m_ * p_226595_1_.field_226578_d_ + this.field_226588_n_ * p_226595_1_.field_226582_h_ + this.field_226589_o_ * p_226595_1_.field_226586_l_ + this.field_226590_p_ * p_226595_1_.field_226590_p_;
      this.field_226575_a_ = f;
      this.field_226576_b_ = f1;
      this.field_226577_c_ = f2;
      this.field_226578_d_ = f3;
      this.field_226579_e_ = f4;
      this.field_226580_f_ = f5;
      this.field_226581_g_ = f6;
      this.field_226582_h_ = f7;
      this.field_226583_i_ = f8;
      this.field_226584_j_ = f9;
      this.field_226585_k_ = f10;
      this.field_226586_l_ = f11;
      this.field_226587_m_ = f12;
      this.field_226588_n_ = f13;
      this.field_226589_o_ = f14;
      this.field_226590_p_ = f15;
   }

   public void func_226596_a_(Quaternion p_226596_1_) {
      this.func_226595_a_(new Matrix4f(p_226596_1_));
   }

   public void func_226592_a_(float p_226592_1_) {
      this.field_226575_a_ *= p_226592_1_;
      this.field_226576_b_ *= p_226592_1_;
      this.field_226577_c_ *= p_226592_1_;
      this.field_226578_d_ *= p_226592_1_;
      this.field_226579_e_ *= p_226592_1_;
      this.field_226580_f_ *= p_226592_1_;
      this.field_226581_g_ *= p_226592_1_;
      this.field_226582_h_ *= p_226592_1_;
      this.field_226583_i_ *= p_226592_1_;
      this.field_226584_j_ *= p_226592_1_;
      this.field_226585_k_ *= p_226592_1_;
      this.field_226586_l_ *= p_226592_1_;
      this.field_226587_m_ *= p_226592_1_;
      this.field_226588_n_ *= p_226592_1_;
      this.field_226589_o_ *= p_226592_1_;
      this.field_226590_p_ *= p_226592_1_;
   }

   public static Matrix4f perspective(double p_195876_0_, float p_195876_2_, float p_195876_3_, float p_195876_4_) {
      float f = (float)(1.0D / Math.tan(p_195876_0_ * 0.01745329238474369D / 2.0D));
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.field_226575_a_ = f / p_195876_2_;
      matrix4f.field_226580_f_ = f;
      matrix4f.field_226585_k_ = (p_195876_4_ + p_195876_3_) / (p_195876_3_ - p_195876_4_);
      matrix4f.field_226589_o_ = -1.0F;
      matrix4f.field_226586_l_ = 2.0F * p_195876_4_ * p_195876_3_ / (p_195876_3_ - p_195876_4_);
      return matrix4f;
   }

   public static Matrix4f orthographic(float p_195877_0_, float p_195877_1_, float p_195877_2_, float p_195877_3_) {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.field_226575_a_ = 2.0F / p_195877_0_;
      matrix4f.field_226580_f_ = 2.0F / p_195877_1_;
      float f = p_195877_3_ - p_195877_2_;
      matrix4f.field_226585_k_ = -2.0F / f;
      matrix4f.field_226590_p_ = 1.0F;
      matrix4f.field_226578_d_ = -1.0F;
      matrix4f.field_226582_h_ = -1.0F;
      matrix4f.field_226586_l_ = -(p_195877_3_ + p_195877_2_) / f;
      return matrix4f;
   }

   public void func_226597_a_(Vector3f p_226597_1_) {
      this.field_226578_d_ += p_226597_1_.getX();
      this.field_226582_h_ += p_226597_1_.getY();
      this.field_226586_l_ += p_226597_1_.getZ();
   }

   public Matrix4f func_226601_d_() {
      return new Matrix4f(this);
   }

   public static Matrix4f func_226593_a_(float p_226593_0_, float p_226593_1_, float p_226593_2_) {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.field_226575_a_ = p_226593_0_;
      matrix4f.field_226580_f_ = p_226593_1_;
      matrix4f.field_226585_k_ = p_226593_2_;
      matrix4f.field_226590_p_ = 1.0F;
      return matrix4f;
   }

   public static Matrix4f func_226599_b_(float p_226599_0_, float p_226599_1_, float p_226599_2_) {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.field_226575_a_ = 1.0F;
      matrix4f.field_226580_f_ = 1.0F;
      matrix4f.field_226585_k_ = 1.0F;
      matrix4f.field_226590_p_ = 1.0F;
      matrix4f.field_226578_d_ = p_226599_0_;
      matrix4f.field_226582_h_ = p_226599_1_;
      matrix4f.field_226586_l_ = p_226599_2_;
      return matrix4f;
   }

   public Matrix4f(float[] p_i230103_1_) {
      this.field_226575_a_ = p_i230103_1_[0];
      this.field_226576_b_ = p_i230103_1_[1];
      this.field_226577_c_ = p_i230103_1_[2];
      this.field_226578_d_ = p_i230103_1_[3];
      this.field_226579_e_ = p_i230103_1_[4];
      this.field_226580_f_ = p_i230103_1_[5];
      this.field_226581_g_ = p_i230103_1_[6];
      this.field_226582_h_ = p_i230103_1_[7];
      this.field_226583_i_ = p_i230103_1_[8];
      this.field_226584_j_ = p_i230103_1_[9];
      this.field_226585_k_ = p_i230103_1_[10];
      this.field_226586_l_ = p_i230103_1_[11];
      this.field_226587_m_ = p_i230103_1_[12];
      this.field_226588_n_ = p_i230103_1_[13];
      this.field_226589_o_ = p_i230103_1_[14];
      this.field_226590_p_ = p_i230103_1_[15];
   }

   public void set(Matrix4f p_set_1_) {
      this.field_226575_a_ = p_set_1_.field_226575_a_;
      this.field_226576_b_ = p_set_1_.field_226576_b_;
      this.field_226577_c_ = p_set_1_.field_226577_c_;
      this.field_226578_d_ = p_set_1_.field_226578_d_;
      this.field_226579_e_ = p_set_1_.field_226579_e_;
      this.field_226580_f_ = p_set_1_.field_226580_f_;
      this.field_226581_g_ = p_set_1_.field_226581_g_;
      this.field_226582_h_ = p_set_1_.field_226582_h_;
      this.field_226583_i_ = p_set_1_.field_226583_i_;
      this.field_226584_j_ = p_set_1_.field_226584_j_;
      this.field_226585_k_ = p_set_1_.field_226585_k_;
      this.field_226586_l_ = p_set_1_.field_226586_l_;
      this.field_226587_m_ = p_set_1_.field_226587_m_;
      this.field_226588_n_ = p_set_1_.field_226588_n_;
      this.field_226589_o_ = p_set_1_.field_226589_o_;
      this.field_226590_p_ = p_set_1_.field_226590_p_;
   }

   public void add(Matrix4f p_add_1_) {
      this.field_226575_a_ += p_add_1_.field_226575_a_;
      this.field_226576_b_ += p_add_1_.field_226576_b_;
      this.field_226577_c_ += p_add_1_.field_226577_c_;
      this.field_226578_d_ += p_add_1_.field_226578_d_;
      this.field_226579_e_ += p_add_1_.field_226579_e_;
      this.field_226580_f_ += p_add_1_.field_226580_f_;
      this.field_226581_g_ += p_add_1_.field_226581_g_;
      this.field_226582_h_ += p_add_1_.field_226582_h_;
      this.field_226583_i_ += p_add_1_.field_226583_i_;
      this.field_226584_j_ += p_add_1_.field_226584_j_;
      this.field_226585_k_ += p_add_1_.field_226585_k_;
      this.field_226586_l_ += p_add_1_.field_226586_l_;
      this.field_226587_m_ += p_add_1_.field_226587_m_;
      this.field_226588_n_ += p_add_1_.field_226588_n_;
      this.field_226589_o_ += p_add_1_.field_226589_o_;
      this.field_226590_p_ += p_add_1_.field_226590_p_;
   }

   public void multiplyBackward(Matrix4f p_multiplyBackward_1_) {
      Matrix4f copy = p_multiplyBackward_1_.func_226601_d_();
      copy.func_226595_a_(this);
      this.set(copy);
   }

   public void setTranslation(float p_setTranslation_1_, float p_setTranslation_2_, float p_setTranslation_3_) {
      this.field_226575_a_ = 1.0F;
      this.field_226580_f_ = 1.0F;
      this.field_226585_k_ = 1.0F;
      this.field_226590_p_ = 1.0F;
      this.field_226578_d_ = p_setTranslation_1_;
      this.field_226582_h_ = p_setTranslation_2_;
      this.field_226586_l_ = p_setTranslation_3_;
   }
}
