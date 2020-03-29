package net.minecraft.util.math;

import java.util.Iterator;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AxisAlignedBB {
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   public AxisAlignedBB(double p_i2300_1_, double p_i2300_3_, double p_i2300_5_, double p_i2300_7_, double p_i2300_9_, double p_i2300_11_) {
      this.minX = Math.min(p_i2300_1_, p_i2300_7_);
      this.minY = Math.min(p_i2300_3_, p_i2300_9_);
      this.minZ = Math.min(p_i2300_5_, p_i2300_11_);
      this.maxX = Math.max(p_i2300_1_, p_i2300_7_);
      this.maxY = Math.max(p_i2300_3_, p_i2300_9_);
      this.maxZ = Math.max(p_i2300_5_, p_i2300_11_);
   }

   public AxisAlignedBB(BlockPos p_i46612_1_) {
      this((double)p_i46612_1_.getX(), (double)p_i46612_1_.getY(), (double)p_i46612_1_.getZ(), (double)(p_i46612_1_.getX() + 1), (double)(p_i46612_1_.getY() + 1), (double)(p_i46612_1_.getZ() + 1));
   }

   public AxisAlignedBB(BlockPos p_i45554_1_, BlockPos p_i45554_2_) {
      this((double)p_i45554_1_.getX(), (double)p_i45554_1_.getY(), (double)p_i45554_1_.getZ(), (double)p_i45554_2_.getX(), (double)p_i45554_2_.getY(), (double)p_i45554_2_.getZ());
   }

   public AxisAlignedBB(Vec3d p_i47144_1_, Vec3d p_i47144_2_) {
      this(p_i47144_1_.x, p_i47144_1_.y, p_i47144_1_.z, p_i47144_2_.x, p_i47144_2_.y, p_i47144_2_.z);
   }

   public static AxisAlignedBB func_216363_a(MutableBoundingBox p_216363_0_) {
      return new AxisAlignedBB((double)p_216363_0_.minX, (double)p_216363_0_.minY, (double)p_216363_0_.minZ, (double)(p_216363_0_.maxX + 1), (double)(p_216363_0_.maxY + 1), (double)(p_216363_0_.maxZ + 1));
   }

   public double getMin(Direction.Axis p_197745_1_) {
      return p_197745_1_.getCoordinate(this.minX, this.minY, this.minZ);
   }

   public double getMax(Direction.Axis p_197742_1_) {
      return p_197742_1_.getCoordinate(this.maxX, this.maxY, this.maxZ);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof AxisAlignedBB)) {
         return false;
      } else {
         AxisAlignedBB lvt_2_1_ = (AxisAlignedBB)p_equals_1_;
         if (Double.compare(lvt_2_1_.minX, this.minX) != 0) {
            return false;
         } else if (Double.compare(lvt_2_1_.minY, this.minY) != 0) {
            return false;
         } else if (Double.compare(lvt_2_1_.minZ, this.minZ) != 0) {
            return false;
         } else if (Double.compare(lvt_2_1_.maxX, this.maxX) != 0) {
            return false;
         } else if (Double.compare(lvt_2_1_.maxY, this.maxY) != 0) {
            return false;
         } else {
            return Double.compare(lvt_2_1_.maxZ, this.maxZ) == 0;
         }
      }
   }

   public int hashCode() {
      long lvt_1_1_ = Double.doubleToLongBits(this.minX);
      int lvt_3_1_ = (int)(lvt_1_1_ ^ lvt_1_1_ >>> 32);
      lvt_1_1_ = Double.doubleToLongBits(this.minY);
      lvt_3_1_ = 31 * lvt_3_1_ + (int)(lvt_1_1_ ^ lvt_1_1_ >>> 32);
      lvt_1_1_ = Double.doubleToLongBits(this.minZ);
      lvt_3_1_ = 31 * lvt_3_1_ + (int)(lvt_1_1_ ^ lvt_1_1_ >>> 32);
      lvt_1_1_ = Double.doubleToLongBits(this.maxX);
      lvt_3_1_ = 31 * lvt_3_1_ + (int)(lvt_1_1_ ^ lvt_1_1_ >>> 32);
      lvt_1_1_ = Double.doubleToLongBits(this.maxY);
      lvt_3_1_ = 31 * lvt_3_1_ + (int)(lvt_1_1_ ^ lvt_1_1_ >>> 32);
      lvt_1_1_ = Double.doubleToLongBits(this.maxZ);
      lvt_3_1_ = 31 * lvt_3_1_ + (int)(lvt_1_1_ ^ lvt_1_1_ >>> 32);
      return lvt_3_1_;
   }

   public AxisAlignedBB contract(double p_191195_1_, double p_191195_3_, double p_191195_5_) {
      double lvt_7_1_ = this.minX;
      double lvt_9_1_ = this.minY;
      double lvt_11_1_ = this.minZ;
      double lvt_13_1_ = this.maxX;
      double lvt_15_1_ = this.maxY;
      double lvt_17_1_ = this.maxZ;
      if (p_191195_1_ < 0.0D) {
         lvt_7_1_ -= p_191195_1_;
      } else if (p_191195_1_ > 0.0D) {
         lvt_13_1_ -= p_191195_1_;
      }

      if (p_191195_3_ < 0.0D) {
         lvt_9_1_ -= p_191195_3_;
      } else if (p_191195_3_ > 0.0D) {
         lvt_15_1_ -= p_191195_3_;
      }

      if (p_191195_5_ < 0.0D) {
         lvt_11_1_ -= p_191195_5_;
      } else if (p_191195_5_ > 0.0D) {
         lvt_17_1_ -= p_191195_5_;
      }

      return new AxisAlignedBB(lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_);
   }

   public AxisAlignedBB expand(Vec3d p_216361_1_) {
      return this.expand(p_216361_1_.x, p_216361_1_.y, p_216361_1_.z);
   }

   public AxisAlignedBB expand(double p_72321_1_, double p_72321_3_, double p_72321_5_) {
      double lvt_7_1_ = this.minX;
      double lvt_9_1_ = this.minY;
      double lvt_11_1_ = this.minZ;
      double lvt_13_1_ = this.maxX;
      double lvt_15_1_ = this.maxY;
      double lvt_17_1_ = this.maxZ;
      if (p_72321_1_ < 0.0D) {
         lvt_7_1_ += p_72321_1_;
      } else if (p_72321_1_ > 0.0D) {
         lvt_13_1_ += p_72321_1_;
      }

      if (p_72321_3_ < 0.0D) {
         lvt_9_1_ += p_72321_3_;
      } else if (p_72321_3_ > 0.0D) {
         lvt_15_1_ += p_72321_3_;
      }

      if (p_72321_5_ < 0.0D) {
         lvt_11_1_ += p_72321_5_;
      } else if (p_72321_5_ > 0.0D) {
         lvt_17_1_ += p_72321_5_;
      }

      return new AxisAlignedBB(lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_);
   }

   public AxisAlignedBB grow(double p_72314_1_, double p_72314_3_, double p_72314_5_) {
      double lvt_7_1_ = this.minX - p_72314_1_;
      double lvt_9_1_ = this.minY - p_72314_3_;
      double lvt_11_1_ = this.minZ - p_72314_5_;
      double lvt_13_1_ = this.maxX + p_72314_1_;
      double lvt_15_1_ = this.maxY + p_72314_3_;
      double lvt_17_1_ = this.maxZ + p_72314_5_;
      return new AxisAlignedBB(lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_);
   }

   public AxisAlignedBB grow(double p_186662_1_) {
      return this.grow(p_186662_1_, p_186662_1_, p_186662_1_);
   }

   public AxisAlignedBB intersect(AxisAlignedBB p_191500_1_) {
      double lvt_2_1_ = Math.max(this.minX, p_191500_1_.minX);
      double lvt_4_1_ = Math.max(this.minY, p_191500_1_.minY);
      double lvt_6_1_ = Math.max(this.minZ, p_191500_1_.minZ);
      double lvt_8_1_ = Math.min(this.maxX, p_191500_1_.maxX);
      double lvt_10_1_ = Math.min(this.maxY, p_191500_1_.maxY);
      double lvt_12_1_ = Math.min(this.maxZ, p_191500_1_.maxZ);
      return new AxisAlignedBB(lvt_2_1_, lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_12_1_);
   }

   public AxisAlignedBB union(AxisAlignedBB p_111270_1_) {
      double lvt_2_1_ = Math.min(this.minX, p_111270_1_.minX);
      double lvt_4_1_ = Math.min(this.minY, p_111270_1_.minY);
      double lvt_6_1_ = Math.min(this.minZ, p_111270_1_.minZ);
      double lvt_8_1_ = Math.max(this.maxX, p_111270_1_.maxX);
      double lvt_10_1_ = Math.max(this.maxY, p_111270_1_.maxY);
      double lvt_12_1_ = Math.max(this.maxZ, p_111270_1_.maxZ);
      return new AxisAlignedBB(lvt_2_1_, lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_12_1_);
   }

   public AxisAlignedBB offset(double p_72317_1_, double p_72317_3_, double p_72317_5_) {
      return new AxisAlignedBB(this.minX + p_72317_1_, this.minY + p_72317_3_, this.minZ + p_72317_5_, this.maxX + p_72317_1_, this.maxY + p_72317_3_, this.maxZ + p_72317_5_);
   }

   public AxisAlignedBB offset(BlockPos p_186670_1_) {
      return new AxisAlignedBB(this.minX + (double)p_186670_1_.getX(), this.minY + (double)p_186670_1_.getY(), this.minZ + (double)p_186670_1_.getZ(), this.maxX + (double)p_186670_1_.getX(), this.maxY + (double)p_186670_1_.getY(), this.maxZ + (double)p_186670_1_.getZ());
   }

   public AxisAlignedBB offset(Vec3d p_191194_1_) {
      return this.offset(p_191194_1_.x, p_191194_1_.y, p_191194_1_.z);
   }

   public boolean intersects(AxisAlignedBB p_72326_1_) {
      return this.intersects(p_72326_1_.minX, p_72326_1_.minY, p_72326_1_.minZ, p_72326_1_.maxX, p_72326_1_.maxY, p_72326_1_.maxZ);
   }

   public boolean intersects(double p_186668_1_, double p_186668_3_, double p_186668_5_, double p_186668_7_, double p_186668_9_, double p_186668_11_) {
      return this.minX < p_186668_7_ && this.maxX > p_186668_1_ && this.minY < p_186668_9_ && this.maxY > p_186668_3_ && this.minZ < p_186668_11_ && this.maxZ > p_186668_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean intersects(Vec3d p_189973_1_, Vec3d p_189973_2_) {
      return this.intersects(Math.min(p_189973_1_.x, p_189973_2_.x), Math.min(p_189973_1_.y, p_189973_2_.y), Math.min(p_189973_1_.z, p_189973_2_.z), Math.max(p_189973_1_.x, p_189973_2_.x), Math.max(p_189973_1_.y, p_189973_2_.y), Math.max(p_189973_1_.z, p_189973_2_.z));
   }

   public boolean contains(Vec3d p_72318_1_) {
      return this.contains(p_72318_1_.x, p_72318_1_.y, p_72318_1_.z);
   }

   public boolean contains(double p_197744_1_, double p_197744_3_, double p_197744_5_) {
      return p_197744_1_ >= this.minX && p_197744_1_ < this.maxX && p_197744_3_ >= this.minY && p_197744_3_ < this.maxY && p_197744_5_ >= this.minZ && p_197744_5_ < this.maxZ;
   }

   public double getAverageEdgeLength() {
      double lvt_1_1_ = this.getXSize();
      double lvt_3_1_ = this.getYSize();
      double lvt_5_1_ = this.getZSize();
      return (lvt_1_1_ + lvt_3_1_ + lvt_5_1_) / 3.0D;
   }

   public double getXSize() {
      return this.maxX - this.minX;
   }

   public double getYSize() {
      return this.maxY - this.minY;
   }

   public double getZSize() {
      return this.maxZ - this.minZ;
   }

   public AxisAlignedBB shrink(double p_186664_1_) {
      return this.grow(-p_186664_1_);
   }

   public Optional<Vec3d> rayTrace(Vec3d p_216365_1_, Vec3d p_216365_2_) {
      double[] lvt_3_1_ = new double[]{1.0D};
      double lvt_4_1_ = p_216365_2_.x - p_216365_1_.x;
      double lvt_6_1_ = p_216365_2_.y - p_216365_1_.y;
      double lvt_8_1_ = p_216365_2_.z - p_216365_1_.z;
      Direction lvt_10_1_ = func_197741_a(this, p_216365_1_, lvt_3_1_, (Direction)null, lvt_4_1_, lvt_6_1_, lvt_8_1_);
      if (lvt_10_1_ == null) {
         return Optional.empty();
      } else {
         double lvt_11_1_ = lvt_3_1_[0];
         return Optional.of(p_216365_1_.add(lvt_11_1_ * lvt_4_1_, lvt_11_1_ * lvt_6_1_, lvt_11_1_ * lvt_8_1_));
      }
   }

   @Nullable
   public static BlockRayTraceResult rayTrace(Iterable<AxisAlignedBB> p_197743_0_, Vec3d p_197743_1_, Vec3d p_197743_2_, BlockPos p_197743_3_) {
      double[] lvt_4_1_ = new double[]{1.0D};
      Direction lvt_5_1_ = null;
      double lvt_6_1_ = p_197743_2_.x - p_197743_1_.x;
      double lvt_8_1_ = p_197743_2_.y - p_197743_1_.y;
      double lvt_10_1_ = p_197743_2_.z - p_197743_1_.z;

      AxisAlignedBB lvt_13_1_;
      for(Iterator var12 = p_197743_0_.iterator(); var12.hasNext(); lvt_5_1_ = func_197741_a(lvt_13_1_.offset(p_197743_3_), p_197743_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_8_1_, lvt_10_1_)) {
         lvt_13_1_ = (AxisAlignedBB)var12.next();
      }

      if (lvt_5_1_ == null) {
         return null;
      } else {
         double lvt_12_1_ = lvt_4_1_[0];
         return new BlockRayTraceResult(p_197743_1_.add(lvt_12_1_ * lvt_6_1_, lvt_12_1_ * lvt_8_1_, lvt_12_1_ * lvt_10_1_), lvt_5_1_, p_197743_3_, false);
      }
   }

   @Nullable
   private static Direction func_197741_a(AxisAlignedBB p_197741_0_, Vec3d p_197741_1_, double[] p_197741_2_, @Nullable Direction p_197741_3_, double p_197741_4_, double p_197741_6_, double p_197741_8_) {
      if (p_197741_4_ > 1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_4_, p_197741_6_, p_197741_8_, p_197741_0_.minX, p_197741_0_.minY, p_197741_0_.maxY, p_197741_0_.minZ, p_197741_0_.maxZ, Direction.WEST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);
      } else if (p_197741_4_ < -1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_4_, p_197741_6_, p_197741_8_, p_197741_0_.maxX, p_197741_0_.minY, p_197741_0_.maxY, p_197741_0_.minZ, p_197741_0_.maxZ, Direction.EAST, p_197741_1_.x, p_197741_1_.y, p_197741_1_.z);
      }

      if (p_197741_6_ > 1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_6_, p_197741_8_, p_197741_4_, p_197741_0_.minY, p_197741_0_.minZ, p_197741_0_.maxZ, p_197741_0_.minX, p_197741_0_.maxX, Direction.DOWN, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);
      } else if (p_197741_6_ < -1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_6_, p_197741_8_, p_197741_4_, p_197741_0_.maxY, p_197741_0_.minZ, p_197741_0_.maxZ, p_197741_0_.minX, p_197741_0_.maxX, Direction.UP, p_197741_1_.y, p_197741_1_.z, p_197741_1_.x);
      }

      if (p_197741_8_ > 1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_8_, p_197741_4_, p_197741_6_, p_197741_0_.minZ, p_197741_0_.minX, p_197741_0_.maxX, p_197741_0_.minY, p_197741_0_.maxY, Direction.NORTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);
      } else if (p_197741_8_ < -1.0E-7D) {
         p_197741_3_ = func_197740_a(p_197741_2_, p_197741_3_, p_197741_8_, p_197741_4_, p_197741_6_, p_197741_0_.maxZ, p_197741_0_.minX, p_197741_0_.maxX, p_197741_0_.minY, p_197741_0_.maxY, Direction.SOUTH, p_197741_1_.z, p_197741_1_.x, p_197741_1_.y);
      }

      return p_197741_3_;
   }

   @Nullable
   private static Direction func_197740_a(double[] p_197740_0_, @Nullable Direction p_197740_1_, double p_197740_2_, double p_197740_4_, double p_197740_6_, double p_197740_8_, double p_197740_10_, double p_197740_12_, double p_197740_14_, double p_197740_16_, Direction p_197740_18_, double p_197740_19_, double p_197740_21_, double p_197740_23_) {
      double lvt_25_1_ = (p_197740_8_ - p_197740_19_) / p_197740_2_;
      double lvt_27_1_ = p_197740_21_ + lvt_25_1_ * p_197740_4_;
      double lvt_29_1_ = p_197740_23_ + lvt_25_1_ * p_197740_6_;
      if (0.0D < lvt_25_1_ && lvt_25_1_ < p_197740_0_[0] && p_197740_10_ - 1.0E-7D < lvt_27_1_ && lvt_27_1_ < p_197740_12_ + 1.0E-7D && p_197740_14_ - 1.0E-7D < lvt_29_1_ && lvt_29_1_ < p_197740_16_ + 1.0E-7D) {
         p_197740_0_[0] = lvt_25_1_;
         return p_197740_18_;
      } else {
         return p_197740_1_;
      }
   }

   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasNaN() {
      return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
   }

   public Vec3d getCenter() {
      return new Vec3d(MathHelper.lerp(0.5D, this.minX, this.maxX), MathHelper.lerp(0.5D, this.minY, this.maxY), MathHelper.lerp(0.5D, this.minZ, this.maxZ));
   }
}
