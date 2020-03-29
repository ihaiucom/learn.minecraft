package net.minecraft.util.math.shapes;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class VoxelShape {
   protected final VoxelShapePart part;
   @Nullable
   private VoxelShape[] projectionCache;

   VoxelShape(VoxelShapePart p_i47680_1_) {
      this.part = p_i47680_1_;
   }

   public double getStart(Direction.Axis p_197762_1_) {
      int lvt_2_1_ = this.part.getStart(p_197762_1_);
      return lvt_2_1_ >= this.part.getSize(p_197762_1_) ? Double.POSITIVE_INFINITY : this.getValueUnchecked(p_197762_1_, lvt_2_1_);
   }

   public double getEnd(Direction.Axis p_197758_1_) {
      int lvt_2_1_ = this.part.getEnd(p_197758_1_);
      return lvt_2_1_ <= 0 ? Double.NEGATIVE_INFINITY : this.getValueUnchecked(p_197758_1_, lvt_2_1_);
   }

   public AxisAlignedBB getBoundingBox() {
      if (this.isEmpty()) {
         throw (UnsupportedOperationException)Util.func_229757_c_(new UnsupportedOperationException("No bounds for empty shape."));
      } else {
         return new AxisAlignedBB(this.getStart(Direction.Axis.X), this.getStart(Direction.Axis.Y), this.getStart(Direction.Axis.Z), this.getEnd(Direction.Axis.X), this.getEnd(Direction.Axis.Y), this.getEnd(Direction.Axis.Z));
      }
   }

   protected double getValueUnchecked(Direction.Axis p_197759_1_, int p_197759_2_) {
      return this.getValues(p_197759_1_).getDouble(p_197759_2_);
   }

   protected abstract DoubleList getValues(Direction.Axis var1);

   public boolean isEmpty() {
      return this.part.isEmpty();
   }

   public VoxelShape withOffset(double p_197751_1_, double p_197751_3_, double p_197751_5_) {
      return (VoxelShape)(this.isEmpty() ? VoxelShapes.empty() : new VoxelShapeArray(this.part, new OffsetDoubleList(this.getValues(Direction.Axis.X), p_197751_1_), new OffsetDoubleList(this.getValues(Direction.Axis.Y), p_197751_3_), new OffsetDoubleList(this.getValues(Direction.Axis.Z), p_197751_5_)));
   }

   public VoxelShape simplify() {
      VoxelShape[] lvt_1_1_ = new VoxelShape[]{VoxelShapes.empty()};
      this.forEachBox((p_197763_1_, p_197763_3_, p_197763_5_, p_197763_7_, p_197763_9_, p_197763_11_) -> {
         lvt_1_1_[0] = VoxelShapes.combine(lvt_1_1_[0], VoxelShapes.create(p_197763_1_, p_197763_3_, p_197763_5_, p_197763_7_, p_197763_9_, p_197763_11_), IBooleanFunction.OR);
      });
      return lvt_1_1_[0];
   }

   @OnlyIn(Dist.CLIENT)
   public void forEachEdge(VoxelShapes.ILineConsumer p_197754_1_) {
      this.part.forEachEdge((p_197750_2_, p_197750_3_, p_197750_4_, p_197750_5_, p_197750_6_, p_197750_7_) -> {
         p_197754_1_.consume(this.getValueUnchecked(Direction.Axis.X, p_197750_2_), this.getValueUnchecked(Direction.Axis.Y, p_197750_3_), this.getValueUnchecked(Direction.Axis.Z, p_197750_4_), this.getValueUnchecked(Direction.Axis.X, p_197750_5_), this.getValueUnchecked(Direction.Axis.Y, p_197750_6_), this.getValueUnchecked(Direction.Axis.Z, p_197750_7_));
      }, true);
   }

   public void forEachBox(VoxelShapes.ILineConsumer p_197755_1_) {
      DoubleList lvt_2_1_ = this.getValues(Direction.Axis.X);
      DoubleList lvt_3_1_ = this.getValues(Direction.Axis.Y);
      DoubleList lvt_4_1_ = this.getValues(Direction.Axis.Z);
      this.part.forEachBox((p_224789_4_, p_224789_5_, p_224789_6_, p_224789_7_, p_224789_8_, p_224789_9_) -> {
         p_197755_1_.consume(lvt_2_1_.getDouble(p_224789_4_), lvt_3_1_.getDouble(p_224789_5_), lvt_4_1_.getDouble(p_224789_6_), lvt_2_1_.getDouble(p_224789_7_), lvt_3_1_.getDouble(p_224789_8_), lvt_4_1_.getDouble(p_224789_9_));
      }, true);
   }

   public List<AxisAlignedBB> toBoundingBoxList() {
      List<AxisAlignedBB> lvt_1_1_ = Lists.newArrayList();
      this.forEachBox((p_203431_1_, p_203431_3_, p_203431_5_, p_203431_7_, p_203431_9_, p_203431_11_) -> {
         lvt_1_1_.add(new AxisAlignedBB(p_203431_1_, p_203431_3_, p_203431_5_, p_203431_7_, p_203431_9_, p_203431_11_));
      });
      return lvt_1_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public double min(Direction.Axis p_197764_1_, double p_197764_2_, double p_197764_4_) {
      Direction.Axis lvt_6_1_ = AxisRotation.FORWARD.rotate(p_197764_1_);
      Direction.Axis lvt_7_1_ = AxisRotation.BACKWARD.rotate(p_197764_1_);
      int lvt_8_1_ = this.getClosestIndex(lvt_6_1_, p_197764_2_);
      int lvt_9_1_ = this.getClosestIndex(lvt_7_1_, p_197764_4_);
      int lvt_10_1_ = this.part.firstFilled(p_197764_1_, lvt_8_1_, lvt_9_1_);
      return lvt_10_1_ >= this.part.getSize(p_197764_1_) ? Double.POSITIVE_INFINITY : this.getValueUnchecked(p_197764_1_, lvt_10_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public double max(Direction.Axis p_197760_1_, double p_197760_2_, double p_197760_4_) {
      Direction.Axis lvt_6_1_ = AxisRotation.FORWARD.rotate(p_197760_1_);
      Direction.Axis lvt_7_1_ = AxisRotation.BACKWARD.rotate(p_197760_1_);
      int lvt_8_1_ = this.getClosestIndex(lvt_6_1_, p_197760_2_);
      int lvt_9_1_ = this.getClosestIndex(lvt_7_1_, p_197760_4_);
      int lvt_10_1_ = this.part.lastFilled(p_197760_1_, lvt_8_1_, lvt_9_1_);
      return lvt_10_1_ <= 0 ? Double.NEGATIVE_INFINITY : this.getValueUnchecked(p_197760_1_, lvt_10_1_);
   }

   protected int getClosestIndex(Direction.Axis p_197749_1_, double p_197749_2_) {
      return MathHelper.binarySearch(0, this.part.getSize(p_197749_1_) + 1, (p_197761_4_) -> {
         if (p_197761_4_ < 0) {
            return false;
         } else if (p_197761_4_ > this.part.getSize(p_197749_1_)) {
            return true;
         } else {
            return p_197749_2_ < this.getValueUnchecked(p_197749_1_, p_197761_4_);
         }
      }) - 1;
   }

   protected boolean contains(double p_211542_1_, double p_211542_3_, double p_211542_5_) {
      return this.part.contains(this.getClosestIndex(Direction.Axis.X, p_211542_1_), this.getClosestIndex(Direction.Axis.Y, p_211542_3_), this.getClosestIndex(Direction.Axis.Z, p_211542_5_));
   }

   @Nullable
   public BlockRayTraceResult rayTrace(Vec3d p_212433_1_, Vec3d p_212433_2_, BlockPos p_212433_3_) {
      if (this.isEmpty()) {
         return null;
      } else {
         Vec3d lvt_4_1_ = p_212433_2_.subtract(p_212433_1_);
         if (lvt_4_1_.lengthSquared() < 1.0E-7D) {
            return null;
         } else {
            Vec3d lvt_5_1_ = p_212433_1_.add(lvt_4_1_.scale(0.001D));
            return this.contains(lvt_5_1_.x - (double)p_212433_3_.getX(), lvt_5_1_.y - (double)p_212433_3_.getY(), lvt_5_1_.z - (double)p_212433_3_.getZ()) ? new BlockRayTraceResult(lvt_5_1_, Direction.getFacingFromVector(lvt_4_1_.x, lvt_4_1_.y, lvt_4_1_.z).getOpposite(), p_212433_3_, true) : AxisAlignedBB.rayTrace(this.toBoundingBoxList(), p_212433_1_, p_212433_2_, p_212433_3_);
         }
      }
   }

   public VoxelShape project(Direction p_212434_1_) {
      if (!this.isEmpty() && this != VoxelShapes.fullCube()) {
         VoxelShape lvt_2_1_;
         if (this.projectionCache != null) {
            lvt_2_1_ = this.projectionCache[p_212434_1_.ordinal()];
            if (lvt_2_1_ != null) {
               return lvt_2_1_;
            }
         } else {
            this.projectionCache = new VoxelShape[6];
         }

         lvt_2_1_ = this.doProject(p_212434_1_);
         this.projectionCache[p_212434_1_.ordinal()] = lvt_2_1_;
         return lvt_2_1_;
      } else {
         return this;
      }
   }

   private VoxelShape doProject(Direction p_222863_1_) {
      Direction.Axis lvt_2_1_ = p_222863_1_.getAxis();
      Direction.AxisDirection lvt_3_1_ = p_222863_1_.getAxisDirection();
      DoubleList lvt_4_1_ = this.getValues(lvt_2_1_);
      if (lvt_4_1_.size() == 2 && DoubleMath.fuzzyEquals(lvt_4_1_.getDouble(0), 0.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(lvt_4_1_.getDouble(1), 1.0D, 1.0E-7D)) {
         return this;
      } else {
         int lvt_5_1_ = this.getClosestIndex(lvt_2_1_, lvt_3_1_ == Direction.AxisDirection.POSITIVE ? 0.9999999D : 1.0E-7D);
         return new SplitVoxelShape(this, lvt_2_1_, lvt_5_1_);
      }
   }

   public double getAllowedOffset(Direction.Axis p_212430_1_, AxisAlignedBB p_212430_2_, double p_212430_3_) {
      return this.getAllowedOffset(AxisRotation.from(p_212430_1_, Direction.Axis.X), p_212430_2_, p_212430_3_);
   }

   protected double getAllowedOffset(AxisRotation p_212431_1_, AxisAlignedBB p_212431_2_, double p_212431_3_) {
      if (this.isEmpty()) {
         return p_212431_3_;
      } else if (Math.abs(p_212431_3_) < 1.0E-7D) {
         return 0.0D;
      } else {
         AxisRotation lvt_5_1_ = p_212431_1_.reverse();
         Direction.Axis lvt_6_1_ = lvt_5_1_.rotate(Direction.Axis.X);
         Direction.Axis lvt_7_1_ = lvt_5_1_.rotate(Direction.Axis.Y);
         Direction.Axis lvt_8_1_ = lvt_5_1_.rotate(Direction.Axis.Z);
         double lvt_9_1_ = p_212431_2_.getMax(lvt_6_1_);
         double lvt_11_1_ = p_212431_2_.getMin(lvt_6_1_);
         int lvt_13_1_ = this.getClosestIndex(lvt_6_1_, lvt_11_1_ + 1.0E-7D);
         int lvt_14_1_ = this.getClosestIndex(lvt_6_1_, lvt_9_1_ - 1.0E-7D);
         int lvt_15_1_ = Math.max(0, this.getClosestIndex(lvt_7_1_, p_212431_2_.getMin(lvt_7_1_) + 1.0E-7D));
         int lvt_16_1_ = Math.min(this.part.getSize(lvt_7_1_), this.getClosestIndex(lvt_7_1_, p_212431_2_.getMax(lvt_7_1_) - 1.0E-7D) + 1);
         int lvt_17_1_ = Math.max(0, this.getClosestIndex(lvt_8_1_, p_212431_2_.getMin(lvt_8_1_) + 1.0E-7D));
         int lvt_18_1_ = Math.min(this.part.getSize(lvt_8_1_), this.getClosestIndex(lvt_8_1_, p_212431_2_.getMax(lvt_8_1_) - 1.0E-7D) + 1);
         int lvt_19_1_ = this.part.getSize(lvt_6_1_);
         int lvt_20_2_;
         int lvt_21_2_;
         int lvt_22_2_;
         double lvt_23_2_;
         if (p_212431_3_ > 0.0D) {
            for(lvt_20_2_ = lvt_14_1_ + 1; lvt_20_2_ < lvt_19_1_; ++lvt_20_2_) {
               for(lvt_21_2_ = lvt_15_1_; lvt_21_2_ < lvt_16_1_; ++lvt_21_2_) {
                  for(lvt_22_2_ = lvt_17_1_; lvt_22_2_ < lvt_18_1_; ++lvt_22_2_) {
                     if (this.part.containsWithRotation(lvt_5_1_, lvt_20_2_, lvt_21_2_, lvt_22_2_)) {
                        lvt_23_2_ = this.getValueUnchecked(lvt_6_1_, lvt_20_2_) - lvt_9_1_;
                        if (lvt_23_2_ >= -1.0E-7D) {
                           p_212431_3_ = Math.min(p_212431_3_, lvt_23_2_);
                        }

                        return p_212431_3_;
                     }
                  }
               }
            }
         } else if (p_212431_3_ < 0.0D) {
            for(lvt_20_2_ = lvt_13_1_ - 1; lvt_20_2_ >= 0; --lvt_20_2_) {
               for(lvt_21_2_ = lvt_15_1_; lvt_21_2_ < lvt_16_1_; ++lvt_21_2_) {
                  for(lvt_22_2_ = lvt_17_1_; lvt_22_2_ < lvt_18_1_; ++lvt_22_2_) {
                     if (this.part.containsWithRotation(lvt_5_1_, lvt_20_2_, lvt_21_2_, lvt_22_2_)) {
                        lvt_23_2_ = this.getValueUnchecked(lvt_6_1_, lvt_20_2_ + 1) - lvt_11_1_;
                        if (lvt_23_2_ <= 1.0E-7D) {
                           p_212431_3_ = Math.max(p_212431_3_, lvt_23_2_);
                        }

                        return p_212431_3_;
                     }
                  }
               }
            }
         }

         return p_212431_3_;
      }
   }

   public String toString() {
      return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
   }
}
