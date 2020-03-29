package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class SwimNodeProcessor extends NodeProcessor {
   private final boolean field_205202_j;

   public SwimNodeProcessor(boolean p_i48927_1_) {
      this.field_205202_j = p_i48927_1_;
   }

   public PathPoint getStart() {
      return super.openPoint(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getBoundingBox().minZ));
   }

   public FlaggedPathPoint func_224768_a(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new FlaggedPathPoint(super.openPoint(MathHelper.floor(p_224768_1_ - (double)(this.entity.getWidth() / 2.0F)), MathHelper.floor(p_224768_3_ + 0.5D), MathHelper.floor(p_224768_5_ - (double)(this.entity.getWidth() / 2.0F))));
   }

   public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int lvt_3_1_ = 0;
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction lvt_7_1_ = var4[var6];
         PathPoint lvt_8_1_ = this.getWaterNode(p_222859_2_.x + lvt_7_1_.getXOffset(), p_222859_2_.y + lvt_7_1_.getYOffset(), p_222859_2_.z + lvt_7_1_.getZOffset());
         if (lvt_8_1_ != null && !lvt_8_1_.visited) {
            p_222859_1_[lvt_3_1_++] = lvt_8_1_;
         }
      }

      return lvt_3_1_;
   }

   public PathNodeType getPathNodeType(IBlockReader p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, MobEntity p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_) {
      return this.getPathNodeType(p_186319_1_, p_186319_2_, p_186319_3_, p_186319_4_);
   }

   public PathNodeType getPathNodeType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      BlockPos lvt_5_1_ = new BlockPos(p_186330_2_, p_186330_3_, p_186330_4_);
      IFluidState lvt_6_1_ = p_186330_1_.getFluidState(lvt_5_1_);
      BlockState lvt_7_1_ = p_186330_1_.getBlockState(lvt_5_1_);
      if (lvt_6_1_.isEmpty() && lvt_7_1_.allowsMovement(p_186330_1_, lvt_5_1_.down(), PathType.WATER) && lvt_7_1_.isAir()) {
         return PathNodeType.BREACH;
      } else {
         return lvt_6_1_.isTagged(FluidTags.WATER) && lvt_7_1_.allowsMovement(p_186330_1_, lvt_5_1_, PathType.WATER) ? PathNodeType.WATER : PathNodeType.BLOCKED;
      }
   }

   @Nullable
   private PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
      PathNodeType lvt_4_1_ = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
      return (!this.field_205202_j || lvt_4_1_ != PathNodeType.BREACH) && lvt_4_1_ != PathNodeType.WATER ? null : this.openPoint(p_186328_1_, p_186328_2_, p_186328_3_);
   }

   @Nullable
   protected PathPoint openPoint(int p_176159_1_, int p_176159_2_, int p_176159_3_) {
      PathPoint lvt_4_1_ = null;
      PathNodeType lvt_5_1_ = this.getPathNodeType(this.entity.world, p_176159_1_, p_176159_2_, p_176159_3_);
      float lvt_6_1_ = this.entity.getPathPriority(lvt_5_1_);
      if (lvt_6_1_ >= 0.0F) {
         lvt_4_1_ = super.openPoint(p_176159_1_, p_176159_2_, p_176159_3_);
         lvt_4_1_.nodeType = lvt_5_1_;
         lvt_4_1_.costMalus = Math.max(lvt_4_1_.costMalus, lvt_6_1_);
         if (this.blockaccess.getFluidState(new BlockPos(p_176159_1_, p_176159_2_, p_176159_3_)).isEmpty()) {
            lvt_4_1_.costMalus += 8.0F;
         }
      }

      return lvt_5_1_ == PathNodeType.OPEN ? lvt_4_1_ : lvt_4_1_;
   }

   private PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
      BlockPos.Mutable lvt_4_1_ = new BlockPos.Mutable();

      for(int lvt_5_1_ = p_186327_1_; lvt_5_1_ < p_186327_1_ + this.entitySizeX; ++lvt_5_1_) {
         for(int lvt_6_1_ = p_186327_2_; lvt_6_1_ < p_186327_2_ + this.entitySizeY; ++lvt_6_1_) {
            for(int lvt_7_1_ = p_186327_3_; lvt_7_1_ < p_186327_3_ + this.entitySizeZ; ++lvt_7_1_) {
               IFluidState lvt_8_1_ = this.blockaccess.getFluidState(lvt_4_1_.setPos(lvt_5_1_, lvt_6_1_, lvt_7_1_));
               BlockState lvt_9_1_ = this.blockaccess.getBlockState(lvt_4_1_.setPos(lvt_5_1_, lvt_6_1_, lvt_7_1_));
               if (lvt_8_1_.isEmpty() && lvt_9_1_.allowsMovement(this.blockaccess, lvt_4_1_.down(), PathType.WATER) && lvt_9_1_.isAir()) {
                  return PathNodeType.BREACH;
               }

               if (!lvt_8_1_.isTagged(FluidTags.WATER)) {
                  return PathNodeType.BLOCKED;
               }
            }
         }
      }

      BlockState lvt_5_2_ = this.blockaccess.getBlockState(lvt_4_1_);
      if (lvt_5_2_.allowsMovement(this.blockaccess, lvt_4_1_, PathType.WATER)) {
         return PathNodeType.WATER;
      } else {
         return PathNodeType.BLOCKED;
      }
   }
}
