package net.minecraft.pathfinding;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PathPoint {
   public final int x;
   public final int y;
   public final int z;
   private final int hash;
   public int index = -1;
   public float totalPathDistance;
   public float distanceToNext;
   public float distanceToTarget;
   public PathPoint previous;
   public boolean visited;
   public float field_222861_j;
   public float costMalus;
   public PathNodeType nodeType;

   public PathPoint(int p_i2135_1_, int p_i2135_2_, int p_i2135_3_) {
      this.nodeType = PathNodeType.BLOCKED;
      this.x = p_i2135_1_;
      this.y = p_i2135_2_;
      this.z = p_i2135_3_;
      this.hash = makeHash(p_i2135_1_, p_i2135_2_, p_i2135_3_);
   }

   public PathPoint cloneMove(int p_186283_1_, int p_186283_2_, int p_186283_3_) {
      PathPoint lvt_4_1_ = new PathPoint(p_186283_1_, p_186283_2_, p_186283_3_);
      lvt_4_1_.index = this.index;
      lvt_4_1_.totalPathDistance = this.totalPathDistance;
      lvt_4_1_.distanceToNext = this.distanceToNext;
      lvt_4_1_.distanceToTarget = this.distanceToTarget;
      lvt_4_1_.previous = this.previous;
      lvt_4_1_.visited = this.visited;
      lvt_4_1_.field_222861_j = this.field_222861_j;
      lvt_4_1_.costMalus = this.costMalus;
      lvt_4_1_.nodeType = this.nodeType;
      return lvt_4_1_;
   }

   public static int makeHash(int p_75830_0_, int p_75830_1_, int p_75830_2_) {
      return p_75830_1_ & 255 | (p_75830_0_ & 32767) << 8 | (p_75830_2_ & 32767) << 24 | (p_75830_0_ < 0 ? Integer.MIN_VALUE : 0) | (p_75830_2_ < 0 ? '耀' : 0);
   }

   public float distanceTo(PathPoint p_75829_1_) {
      float lvt_2_1_ = (float)(p_75829_1_.x - this.x);
      float lvt_3_1_ = (float)(p_75829_1_.y - this.y);
      float lvt_4_1_ = (float)(p_75829_1_.z - this.z);
      return MathHelper.sqrt(lvt_2_1_ * lvt_2_1_ + lvt_3_1_ * lvt_3_1_ + lvt_4_1_ * lvt_4_1_);
   }

   public float distanceToSquared(PathPoint p_75832_1_) {
      float lvt_2_1_ = (float)(p_75832_1_.x - this.x);
      float lvt_3_1_ = (float)(p_75832_1_.y - this.y);
      float lvt_4_1_ = (float)(p_75832_1_.z - this.z);
      return lvt_2_1_ * lvt_2_1_ + lvt_3_1_ * lvt_3_1_ + lvt_4_1_ * lvt_4_1_;
   }

   public float func_224757_c(PathPoint p_224757_1_) {
      float lvt_2_1_ = (float)Math.abs(p_224757_1_.x - this.x);
      float lvt_3_1_ = (float)Math.abs(p_224757_1_.y - this.y);
      float lvt_4_1_ = (float)Math.abs(p_224757_1_.z - this.z);
      return lvt_2_1_ + lvt_3_1_ + lvt_4_1_;
   }

   public float func_224758_c(BlockPos p_224758_1_) {
      float lvt_2_1_ = (float)Math.abs(p_224758_1_.getX() - this.x);
      float lvt_3_1_ = (float)Math.abs(p_224758_1_.getY() - this.y);
      float lvt_4_1_ = (float)Math.abs(p_224758_1_.getZ() - this.z);
      return lvt_2_1_ + lvt_3_1_ + lvt_4_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos func_224759_a() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof PathPoint)) {
         return false;
      } else {
         PathPoint lvt_2_1_ = (PathPoint)p_equals_1_;
         return this.hash == lvt_2_1_.hash && this.x == lvt_2_1_.x && this.y == lvt_2_1_.y && this.z == lvt_2_1_.z;
      }
   }

   public int hashCode() {
      return this.hash;
   }

   public boolean isAssigned() {
      return this.index >= 0;
   }

   public String toString() {
      return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
   }

   @OnlyIn(Dist.CLIENT)
   public static PathPoint createFromBuffer(PacketBuffer p_186282_0_) {
      PathPoint lvt_1_1_ = new PathPoint(p_186282_0_.readInt(), p_186282_0_.readInt(), p_186282_0_.readInt());
      lvt_1_1_.field_222861_j = p_186282_0_.readFloat();
      lvt_1_1_.costMalus = p_186282_0_.readFloat();
      lvt_1_1_.visited = p_186282_0_.readBoolean();
      lvt_1_1_.nodeType = PathNodeType.values()[p_186282_0_.readInt()];
      lvt_1_1_.distanceToTarget = p_186282_0_.readFloat();
      return lvt_1_1_;
   }
}
