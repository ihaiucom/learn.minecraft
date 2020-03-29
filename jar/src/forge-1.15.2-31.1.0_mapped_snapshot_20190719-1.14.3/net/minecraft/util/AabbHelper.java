package net.minecraft.util;

import net.minecraft.util.math.AxisAlignedBB;

public class AabbHelper {
   public static AxisAlignedBB func_227019_a_(AxisAlignedBB p_227019_0_, Direction p_227019_1_, double p_227019_2_) {
      double lvt_4_1_ = p_227019_2_ * (double)p_227019_1_.getAxisDirection().getOffset();
      double lvt_6_1_ = Math.min(lvt_4_1_, 0.0D);
      double lvt_8_1_ = Math.max(lvt_4_1_, 0.0D);
      switch(p_227019_1_) {
      case WEST:
         return new AxisAlignedBB(p_227019_0_.minX + lvt_6_1_, p_227019_0_.minY, p_227019_0_.minZ, p_227019_0_.minX + lvt_8_1_, p_227019_0_.maxY, p_227019_0_.maxZ);
      case EAST:
         return new AxisAlignedBB(p_227019_0_.maxX + lvt_6_1_, p_227019_0_.minY, p_227019_0_.minZ, p_227019_0_.maxX + lvt_8_1_, p_227019_0_.maxY, p_227019_0_.maxZ);
      case DOWN:
         return new AxisAlignedBB(p_227019_0_.minX, p_227019_0_.minY + lvt_6_1_, p_227019_0_.minZ, p_227019_0_.maxX, p_227019_0_.minY + lvt_8_1_, p_227019_0_.maxZ);
      case UP:
      default:
         return new AxisAlignedBB(p_227019_0_.minX, p_227019_0_.maxY + lvt_6_1_, p_227019_0_.minZ, p_227019_0_.maxX, p_227019_0_.maxY + lvt_8_1_, p_227019_0_.maxZ);
      case NORTH:
         return new AxisAlignedBB(p_227019_0_.minX, p_227019_0_.minY, p_227019_0_.minZ + lvt_6_1_, p_227019_0_.maxX, p_227019_0_.maxY, p_227019_0_.minZ + lvt_8_1_);
      case SOUTH:
         return new AxisAlignedBB(p_227019_0_.minX, p_227019_0_.minY, p_227019_0_.maxZ + lvt_6_1_, p_227019_0_.maxX, p_227019_0_.maxY, p_227019_0_.maxZ + lvt_8_1_);
      }
   }
}
