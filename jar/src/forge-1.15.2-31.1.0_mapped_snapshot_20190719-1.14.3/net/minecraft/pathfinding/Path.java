package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Path {
   private final List<PathPoint> field_75884_a;
   private PathPoint[] openSet = new PathPoint[0];
   private PathPoint[] closedSet = new PathPoint[0];
   @OnlyIn(Dist.CLIENT)
   private Set<FlaggedPathPoint> field_224772_d;
   private int currentPathIndex;
   private final BlockPos target;
   private final float field_224773_g;
   private final boolean field_224774_h;

   public Path(List<PathPoint> p_i51804_1_, BlockPos p_i51804_2_, boolean p_i51804_3_) {
      this.field_75884_a = p_i51804_1_;
      this.target = p_i51804_2_;
      this.field_224773_g = p_i51804_1_.isEmpty() ? Float.MAX_VALUE : ((PathPoint)this.field_75884_a.get(this.field_75884_a.size() - 1)).func_224758_c(this.target);
      this.field_224774_h = p_i51804_3_;
   }

   public void incrementPathIndex() {
      ++this.currentPathIndex;
   }

   public boolean isFinished() {
      return this.currentPathIndex >= this.field_75884_a.size();
   }

   @Nullable
   public PathPoint getFinalPathPoint() {
      return !this.field_75884_a.isEmpty() ? (PathPoint)this.field_75884_a.get(this.field_75884_a.size() - 1) : null;
   }

   public PathPoint getPathPointFromIndex(int p_75877_1_) {
      return (PathPoint)this.field_75884_a.get(p_75877_1_);
   }

   public List<PathPoint> func_215746_d() {
      return this.field_75884_a;
   }

   public void func_215747_b(int p_215747_1_) {
      if (this.field_75884_a.size() > p_215747_1_) {
         this.field_75884_a.subList(p_215747_1_, this.field_75884_a.size()).clear();
      }

   }

   public void setPoint(int p_186309_1_, PathPoint p_186309_2_) {
      this.field_75884_a.set(p_186309_1_, p_186309_2_);
   }

   public int getCurrentPathLength() {
      return this.field_75884_a.size();
   }

   public int getCurrentPathIndex() {
      return this.currentPathIndex;
   }

   public void setCurrentPathIndex(int p_75872_1_) {
      this.currentPathIndex = p_75872_1_;
   }

   public Vec3d getVectorFromIndex(Entity p_75881_1_, int p_75881_2_) {
      PathPoint lvt_3_1_ = (PathPoint)this.field_75884_a.get(p_75881_2_);
      double lvt_4_1_ = (double)lvt_3_1_.x + (double)((int)(p_75881_1_.getWidth() + 1.0F)) * 0.5D;
      double lvt_6_1_ = (double)lvt_3_1_.y;
      double lvt_8_1_ = (double)lvt_3_1_.z + (double)((int)(p_75881_1_.getWidth() + 1.0F)) * 0.5D;
      return new Vec3d(lvt_4_1_, lvt_6_1_, lvt_8_1_);
   }

   public Vec3d getPosition(Entity p_75878_1_) {
      return this.getVectorFromIndex(p_75878_1_, this.currentPathIndex);
   }

   public Vec3d getCurrentPos() {
      PathPoint lvt_1_1_ = (PathPoint)this.field_75884_a.get(this.currentPathIndex);
      return new Vec3d((double)lvt_1_1_.x, (double)lvt_1_1_.y, (double)lvt_1_1_.z);
   }

   public boolean isSamePath(@Nullable Path p_75876_1_) {
      if (p_75876_1_ == null) {
         return false;
      } else if (p_75876_1_.field_75884_a.size() != this.field_75884_a.size()) {
         return false;
      } else {
         for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_75884_a.size(); ++lvt_2_1_) {
            PathPoint lvt_3_1_ = (PathPoint)this.field_75884_a.get(lvt_2_1_);
            PathPoint lvt_4_1_ = (PathPoint)p_75876_1_.field_75884_a.get(lvt_2_1_);
            if (lvt_3_1_.x != lvt_4_1_.x || lvt_3_1_.y != lvt_4_1_.y || lvt_3_1_.z != lvt_4_1_.z) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean func_224771_h() {
      return this.field_224774_h;
   }

   @OnlyIn(Dist.CLIENT)
   public PathPoint[] getOpenSet() {
      return this.openSet;
   }

   @OnlyIn(Dist.CLIENT)
   public PathPoint[] getClosedSet() {
      return this.closedSet;
   }

   @OnlyIn(Dist.CLIENT)
   public static Path read(PacketBuffer p_186311_0_) {
      boolean lvt_1_1_ = p_186311_0_.readBoolean();
      int lvt_2_1_ = p_186311_0_.readInt();
      int lvt_3_1_ = p_186311_0_.readInt();
      Set<FlaggedPathPoint> lvt_4_1_ = Sets.newHashSet();

      for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_3_1_; ++lvt_5_1_) {
         lvt_4_1_.add(FlaggedPathPoint.func_224760_c(p_186311_0_));
      }

      BlockPos lvt_5_2_ = new BlockPos(p_186311_0_.readInt(), p_186311_0_.readInt(), p_186311_0_.readInt());
      List<PathPoint> lvt_6_1_ = Lists.newArrayList();
      int lvt_7_1_ = p_186311_0_.readInt();

      for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_; ++lvt_8_1_) {
         lvt_6_1_.add(PathPoint.createFromBuffer(p_186311_0_));
      }

      PathPoint[] lvt_8_2_ = new PathPoint[p_186311_0_.readInt()];

      for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_2_.length; ++lvt_9_1_) {
         lvt_8_2_[lvt_9_1_] = PathPoint.createFromBuffer(p_186311_0_);
      }

      PathPoint[] lvt_9_2_ = new PathPoint[p_186311_0_.readInt()];

      for(int lvt_10_1_ = 0; lvt_10_1_ < lvt_9_2_.length; ++lvt_10_1_) {
         lvt_9_2_[lvt_10_1_] = PathPoint.createFromBuffer(p_186311_0_);
      }

      Path lvt_10_2_ = new Path(lvt_6_1_, lvt_5_2_, lvt_1_1_);
      lvt_10_2_.openSet = lvt_8_2_;
      lvt_10_2_.closedSet = lvt_9_2_;
      lvt_10_2_.field_224772_d = lvt_4_1_;
      lvt_10_2_.currentPathIndex = lvt_2_1_;
      return lvt_10_2_;
   }

   public String toString() {
      return "Path(length=" + this.field_75884_a.size() + ")";
   }

   public BlockPos func_224770_k() {
      return this.target;
   }

   public float func_224769_l() {
      return this.field_224773_g;
   }
}
