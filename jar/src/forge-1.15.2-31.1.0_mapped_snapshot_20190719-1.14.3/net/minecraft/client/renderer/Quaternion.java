package net.minecraft.client.renderer;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class Quaternion {
   public static final Quaternion field_227060_a_ = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private float field_227061_b_;
   private float field_227062_c_;
   private float field_227063_d_;
   private float field_227064_e_;

   public Quaternion(float p_i48100_1_, float p_i48100_2_, float p_i48100_3_, float p_i48100_4_) {
      this.field_227061_b_ = p_i48100_1_;
      this.field_227062_c_ = p_i48100_2_;
      this.field_227063_d_ = p_i48100_3_;
      this.field_227064_e_ = p_i48100_4_;
   }

   public Quaternion(Vector3f p_i48101_1_, float p_i48101_2_, boolean p_i48101_3_) {
      if (p_i48101_3_) {
         p_i48101_2_ *= 0.017453292F;
      }

      float lvt_4_1_ = func_214903_b(p_i48101_2_ / 2.0F);
      this.field_227061_b_ = p_i48101_1_.getX() * lvt_4_1_;
      this.field_227062_c_ = p_i48101_1_.getY() * lvt_4_1_;
      this.field_227063_d_ = p_i48101_1_.getZ() * lvt_4_1_;
      this.field_227064_e_ = func_214904_a(p_i48101_2_ / 2.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion(float p_i48102_1_, float p_i48102_2_, float p_i48102_3_, boolean p_i48102_4_) {
      if (p_i48102_4_) {
         p_i48102_1_ *= 0.017453292F;
         p_i48102_2_ *= 0.017453292F;
         p_i48102_3_ *= 0.017453292F;
      }

      float lvt_5_1_ = func_214903_b(0.5F * p_i48102_1_);
      float lvt_6_1_ = func_214904_a(0.5F * p_i48102_1_);
      float lvt_7_1_ = func_214903_b(0.5F * p_i48102_2_);
      float lvt_8_1_ = func_214904_a(0.5F * p_i48102_2_);
      float lvt_9_1_ = func_214903_b(0.5F * p_i48102_3_);
      float lvt_10_1_ = func_214904_a(0.5F * p_i48102_3_);
      this.field_227061_b_ = lvt_5_1_ * lvt_8_1_ * lvt_10_1_ + lvt_6_1_ * lvt_7_1_ * lvt_9_1_;
      this.field_227062_c_ = lvt_6_1_ * lvt_7_1_ * lvt_10_1_ - lvt_5_1_ * lvt_8_1_ * lvt_9_1_;
      this.field_227063_d_ = lvt_5_1_ * lvt_7_1_ * lvt_10_1_ + lvt_6_1_ * lvt_8_1_ * lvt_9_1_;
      this.field_227064_e_ = lvt_6_1_ * lvt_8_1_ * lvt_10_1_ - lvt_5_1_ * lvt_7_1_ * lvt_9_1_;
   }

   public Quaternion(Quaternion p_i48103_1_) {
      this.field_227061_b_ = p_i48103_1_.field_227061_b_;
      this.field_227062_c_ = p_i48103_1_.field_227062_c_;
      this.field_227063_d_ = p_i48103_1_.field_227063_d_;
      this.field_227064_e_ = p_i48103_1_.field_227064_e_;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Quaternion lvt_2_1_ = (Quaternion)p_equals_1_;
         if (Float.compare(lvt_2_1_.field_227061_b_, this.field_227061_b_) != 0) {
            return false;
         } else if (Float.compare(lvt_2_1_.field_227062_c_, this.field_227062_c_) != 0) {
            return false;
         } else if (Float.compare(lvt_2_1_.field_227063_d_, this.field_227063_d_) != 0) {
            return false;
         } else {
            return Float.compare(lvt_2_1_.field_227064_e_, this.field_227064_e_) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int lvt_1_1_ = Float.floatToIntBits(this.field_227061_b_);
      lvt_1_1_ = 31 * lvt_1_1_ + Float.floatToIntBits(this.field_227062_c_);
      lvt_1_1_ = 31 * lvt_1_1_ + Float.floatToIntBits(this.field_227063_d_);
      lvt_1_1_ = 31 * lvt_1_1_ + Float.floatToIntBits(this.field_227064_e_);
      return lvt_1_1_;
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder();
      lvt_1_1_.append("Quaternion[").append(this.getW()).append(" + ");
      lvt_1_1_.append(this.getX()).append("i + ");
      lvt_1_1_.append(this.getY()).append("j + ");
      lvt_1_1_.append(this.getZ()).append("k]");
      return lvt_1_1_.toString();
   }

   public float getX() {
      return this.field_227061_b_;
   }

   public float getY() {
      return this.field_227062_c_;
   }

   public float getZ() {
      return this.field_227063_d_;
   }

   public float getW() {
      return this.field_227064_e_;
   }

   public void multiply(Quaternion p_195890_1_) {
      float lvt_2_1_ = this.getX();
      float lvt_3_1_ = this.getY();
      float lvt_4_1_ = this.getZ();
      float lvt_5_1_ = this.getW();
      float lvt_6_1_ = p_195890_1_.getX();
      float lvt_7_1_ = p_195890_1_.getY();
      float lvt_8_1_ = p_195890_1_.getZ();
      float lvt_9_1_ = p_195890_1_.getW();
      this.field_227061_b_ = lvt_5_1_ * lvt_6_1_ + lvt_2_1_ * lvt_9_1_ + lvt_3_1_ * lvt_8_1_ - lvt_4_1_ * lvt_7_1_;
      this.field_227062_c_ = lvt_5_1_ * lvt_7_1_ - lvt_2_1_ * lvt_8_1_ + lvt_3_1_ * lvt_9_1_ + lvt_4_1_ * lvt_6_1_;
      this.field_227063_d_ = lvt_5_1_ * lvt_8_1_ + lvt_2_1_ * lvt_7_1_ - lvt_3_1_ * lvt_6_1_ + lvt_4_1_ * lvt_9_1_;
      this.field_227064_e_ = lvt_5_1_ * lvt_9_1_ - lvt_2_1_ * lvt_6_1_ - lvt_3_1_ * lvt_7_1_ - lvt_4_1_ * lvt_8_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_227065_a_(float p_227065_1_) {
      this.field_227061_b_ *= p_227065_1_;
      this.field_227062_c_ *= p_227065_1_;
      this.field_227063_d_ *= p_227065_1_;
      this.field_227064_e_ *= p_227065_1_;
   }

   public void conjugate() {
      this.field_227061_b_ = -this.field_227061_b_;
      this.field_227062_c_ = -this.field_227062_c_;
      this.field_227063_d_ = -this.field_227063_d_;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_227066_a_(float p_227066_1_, float p_227066_2_, float p_227066_3_, float p_227066_4_) {
      this.field_227061_b_ = p_227066_1_;
      this.field_227062_c_ = p_227066_2_;
      this.field_227063_d_ = p_227066_3_;
      this.field_227064_e_ = p_227066_4_;
   }

   private static float func_214904_a(float p_214904_0_) {
      return (float)Math.cos((double)p_214904_0_);
   }

   private static float func_214903_b(float p_214903_0_) {
      return (float)Math.sin((double)p_214903_0_);
   }

   @OnlyIn(Dist.CLIENT)
   public void func_227067_f_() {
      float lvt_1_1_ = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
      if (lvt_1_1_ > 1.0E-6F) {
         float lvt_2_1_ = MathHelper.func_226165_i_(lvt_1_1_);
         this.field_227061_b_ *= lvt_2_1_;
         this.field_227062_c_ *= lvt_2_1_;
         this.field_227063_d_ *= lvt_2_1_;
         this.field_227064_e_ *= lvt_2_1_;
      } else {
         this.field_227061_b_ = 0.0F;
         this.field_227062_c_ = 0.0F;
         this.field_227063_d_ = 0.0F;
         this.field_227064_e_ = 0.0F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion func_227068_g_() {
      return new Quaternion(this);
   }
}
