package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
   public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
   @Deprecated
   private final int x;
   @Deprecated
   private final int y;
   @Deprecated
   private final int z;

   public Vec3i(int p_i46007_1_, int p_i46007_2_, int p_i46007_3_) {
      this.x = p_i46007_1_;
      this.y = p_i46007_2_;
      this.z = p_i46007_3_;
   }

   public Vec3i(double p_i46008_1_, double p_i46008_3_, double p_i46008_5_) {
      this(MathHelper.floor(p_i46008_1_), MathHelper.floor(p_i46008_3_), MathHelper.floor(p_i46008_5_));
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Vec3i)) {
         return false;
      } else {
         Vec3i lvt_2_1_ = (Vec3i)p_equals_1_;
         if (this.getX() != lvt_2_1_.getX()) {
            return false;
         } else if (this.getY() != lvt_2_1_.getY()) {
            return false;
         } else {
            return this.getZ() == lvt_2_1_.getZ();
         }
      }
   }

   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i p_compareTo_1_) {
      if (this.getY() == p_compareTo_1_.getY()) {
         return this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ();
      } else {
         return this.getY() - p_compareTo_1_.getY();
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public Vec3i down() {
      return this.down(1);
   }

   public Vec3i down(int p_177979_1_) {
      return this.offset(Direction.DOWN, p_177979_1_);
   }

   public Vec3i offset(Direction p_177967_1_, int p_177967_2_) {
      return p_177967_2_ == 0 ? this : new Vec3i(this.getX() + p_177967_1_.getXOffset() * p_177967_2_, this.getY() + p_177967_1_.getYOffset() * p_177967_2_, this.getZ() + p_177967_1_.getZOffset() * p_177967_2_);
   }

   public Vec3i crossProduct(Vec3i p_177955_1_) {
      return new Vec3i(this.getY() * p_177955_1_.getZ() - this.getZ() * p_177955_1_.getY(), this.getZ() * p_177955_1_.getX() - this.getX() * p_177955_1_.getZ(), this.getX() * p_177955_1_.getY() - this.getY() * p_177955_1_.getX());
   }

   public boolean withinDistance(Vec3i p_218141_1_, double p_218141_2_) {
      return this.distanceSq((double)p_218141_1_.getX(), (double)p_218141_1_.getY(), (double)p_218141_1_.getZ(), false) < p_218141_2_ * p_218141_2_;
   }

   public boolean withinDistance(IPosition p_218137_1_, double p_218137_2_) {
      return this.distanceSq(p_218137_1_.getX(), p_218137_1_.getY(), p_218137_1_.getZ(), true) < p_218137_2_ * p_218137_2_;
   }

   public double distanceSq(Vec3i p_177951_1_) {
      return this.distanceSq((double)p_177951_1_.getX(), (double)p_177951_1_.getY(), (double)p_177951_1_.getZ(), true);
   }

   public double distanceSq(IPosition p_218138_1_, boolean p_218138_2_) {
      return this.distanceSq(p_218138_1_.getX(), p_218138_1_.getY(), p_218138_1_.getZ(), p_218138_2_);
   }

   public double distanceSq(double p_218140_1_, double p_218140_3_, double p_218140_5_, boolean p_218140_7_) {
      double lvt_8_1_ = p_218140_7_ ? 0.5D : 0.0D;
      double lvt_10_1_ = (double)this.getX() + lvt_8_1_ - p_218140_1_;
      double lvt_12_1_ = (double)this.getY() + lvt_8_1_ - p_218140_3_;
      double lvt_14_1_ = (double)this.getZ() + lvt_8_1_ - p_218140_5_;
      return lvt_10_1_ * lvt_10_1_ + lvt_12_1_ * lvt_12_1_ + lvt_14_1_ * lvt_14_1_;
   }

   public int manhattanDistance(Vec3i p_218139_1_) {
      float lvt_2_1_ = (float)Math.abs(p_218139_1_.getX() - this.getX());
      float lvt_3_1_ = (float)Math.abs(p_218139_1_.getY() - this.getY());
      float lvt_4_1_ = (float)Math.abs(p_218139_1_.getZ() - this.getZ());
      return (int)(lvt_2_1_ + lvt_3_1_ + lvt_4_1_);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   @OnlyIn(Dist.CLIENT)
   public String func_229422_x_() {
      return "" + this.getX() + ", " + this.getY() + ", " + this.getZ();
   }

   // $FF: synthetic method
   public int compareTo(Object p_compareTo_1_) {
      return this.compareTo((Vec3i)p_compareTo_1_);
   }
}
