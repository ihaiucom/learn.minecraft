package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;

public final class VoxelShapeArray extends VoxelShape {
   private final DoubleList xPoints;
   private final DoubleList yPoints;
   private final DoubleList zPoints;

   protected VoxelShapeArray(VoxelShapePart p_i47693_1_, double[] p_i47693_2_, double[] p_i47693_3_, double[] p_i47693_4_) {
      this(p_i47693_1_, (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(p_i47693_2_, p_i47693_1_.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(p_i47693_3_, p_i47693_1_.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(p_i47693_4_, p_i47693_1_.getZSize() + 1)));
   }

   VoxelShapeArray(VoxelShapePart p_i47694_1_, DoubleList p_i47694_2_, DoubleList p_i47694_3_, DoubleList p_i47694_4_) {
      super(p_i47694_1_);
      int lvt_5_1_ = p_i47694_1_.getXSize() + 1;
      int lvt_6_1_ = p_i47694_1_.getYSize() + 1;
      int lvt_7_1_ = p_i47694_1_.getZSize() + 1;
      if (lvt_5_1_ == p_i47694_2_.size() && lvt_6_1_ == p_i47694_3_.size() && lvt_7_1_ == p_i47694_4_.size()) {
         this.xPoints = p_i47694_2_;
         this.yPoints = p_i47694_3_;
         this.zPoints = p_i47694_4_;
      } else {
         throw (IllegalArgumentException)Util.func_229757_c_(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
      }
   }

   protected DoubleList getValues(Direction.Axis p_197757_1_) {
      switch(p_197757_1_) {
      case X:
         return this.xPoints;
      case Y:
         return this.yPoints;
      case Z:
         return this.zPoints;
      default:
         throw new IllegalArgumentException();
      }
   }
}
