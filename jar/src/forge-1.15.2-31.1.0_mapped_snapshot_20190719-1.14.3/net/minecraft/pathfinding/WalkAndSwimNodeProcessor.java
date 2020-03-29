package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class WalkAndSwimNodeProcessor extends WalkNodeProcessor {
   private float field_203247_k;
   private float field_203248_l;

   public void func_225578_a_(Region p_225578_1_, MobEntity p_225578_2_) {
      super.func_225578_a_(p_225578_1_, p_225578_2_);
      p_225578_2_.setPathPriority(PathNodeType.WATER, 0.0F);
      this.field_203247_k = p_225578_2_.getPathPriority(PathNodeType.WALKABLE);
      p_225578_2_.setPathPriority(PathNodeType.WALKABLE, 6.0F);
      this.field_203248_l = p_225578_2_.getPathPriority(PathNodeType.WATER_BORDER);
      p_225578_2_.setPathPriority(PathNodeType.WATER_BORDER, 4.0F);
   }

   public void postProcess() {
      this.entity.setPathPriority(PathNodeType.WALKABLE, this.field_203247_k);
      this.entity.setPathPriority(PathNodeType.WATER_BORDER, this.field_203248_l);
      super.postProcess();
   }

   public PathPoint getStart() {
      return this.openPoint(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getBoundingBox().minZ));
   }

   public FlaggedPathPoint func_224768_a(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new FlaggedPathPoint(this.openPoint(MathHelper.floor(p_224768_1_), MathHelper.floor(p_224768_3_ + 0.5D), MathHelper.floor(p_224768_5_)));
   }

   public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int lvt_3_1_ = 0;
      int lvt_4_1_ = true;
      BlockPos lvt_5_1_ = new BlockPos(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z);
      double lvt_6_1_ = this.func_203246_a(lvt_5_1_);
      PathPoint lvt_8_1_ = this.func_203245_a(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z + 1, 1, lvt_6_1_);
      PathPoint lvt_9_1_ = this.func_203245_a(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z, 1, lvt_6_1_);
      PathPoint lvt_10_1_ = this.func_203245_a(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z, 1, lvt_6_1_);
      PathPoint lvt_11_1_ = this.func_203245_a(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z - 1, 1, lvt_6_1_);
      PathPoint lvt_12_1_ = this.func_203245_a(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z, 0, lvt_6_1_);
      PathPoint lvt_13_1_ = this.func_203245_a(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z, 1, lvt_6_1_);
      if (lvt_8_1_ != null && !lvt_8_1_.visited) {
         p_222859_1_[lvt_3_1_++] = lvt_8_1_;
      }

      if (lvt_9_1_ != null && !lvt_9_1_.visited) {
         p_222859_1_[lvt_3_1_++] = lvt_9_1_;
      }

      if (lvt_10_1_ != null && !lvt_10_1_.visited) {
         p_222859_1_[lvt_3_1_++] = lvt_10_1_;
      }

      if (lvt_11_1_ != null && !lvt_11_1_.visited) {
         p_222859_1_[lvt_3_1_++] = lvt_11_1_;
      }

      if (lvt_12_1_ != null && !lvt_12_1_.visited) {
         p_222859_1_[lvt_3_1_++] = lvt_12_1_;
      }

      if (lvt_13_1_ != null && !lvt_13_1_.visited) {
         p_222859_1_[lvt_3_1_++] = lvt_13_1_;
      }

      boolean lvt_14_1_ = lvt_11_1_ == null || lvt_11_1_.nodeType == PathNodeType.OPEN || lvt_11_1_.costMalus != 0.0F;
      boolean lvt_15_1_ = lvt_8_1_ == null || lvt_8_1_.nodeType == PathNodeType.OPEN || lvt_8_1_.costMalus != 0.0F;
      boolean lvt_16_1_ = lvt_10_1_ == null || lvt_10_1_.nodeType == PathNodeType.OPEN || lvt_10_1_.costMalus != 0.0F;
      boolean lvt_17_1_ = lvt_9_1_ == null || lvt_9_1_.nodeType == PathNodeType.OPEN || lvt_9_1_.costMalus != 0.0F;
      PathPoint lvt_18_4_;
      if (lvt_14_1_ && lvt_17_1_) {
         lvt_18_4_ = this.func_203245_a(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z - 1, 1, lvt_6_1_);
         if (lvt_18_4_ != null && !lvt_18_4_.visited) {
            p_222859_1_[lvt_3_1_++] = lvt_18_4_;
         }
      }

      if (lvt_14_1_ && lvt_16_1_) {
         lvt_18_4_ = this.func_203245_a(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z - 1, 1, lvt_6_1_);
         if (lvt_18_4_ != null && !lvt_18_4_.visited) {
            p_222859_1_[lvt_3_1_++] = lvt_18_4_;
         }
      }

      if (lvt_15_1_ && lvt_17_1_) {
         lvt_18_4_ = this.func_203245_a(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z + 1, 1, lvt_6_1_);
         if (lvt_18_4_ != null && !lvt_18_4_.visited) {
            p_222859_1_[lvt_3_1_++] = lvt_18_4_;
         }
      }

      if (lvt_15_1_ && lvt_16_1_) {
         lvt_18_4_ = this.func_203245_a(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z + 1, 1, lvt_6_1_);
         if (lvt_18_4_ != null && !lvt_18_4_.visited) {
            p_222859_1_[lvt_3_1_++] = lvt_18_4_;
         }
      }

      return lvt_3_1_;
   }

   private double func_203246_a(BlockPos p_203246_1_) {
      if (!this.entity.isInWater()) {
         BlockPos lvt_2_1_ = p_203246_1_.down();
         VoxelShape lvt_3_1_ = this.blockaccess.getBlockState(lvt_2_1_).getCollisionShape(this.blockaccess, lvt_2_1_);
         return (double)lvt_2_1_.getY() + (lvt_3_1_.isEmpty() ? 0.0D : lvt_3_1_.getEnd(Direction.Axis.Y));
      } else {
         return (double)p_203246_1_.getY() + 0.5D;
      }
   }

   @Nullable
   private PathPoint func_203245_a(int p_203245_1_, int p_203245_2_, int p_203245_3_, int p_203245_4_, double p_203245_5_) {
      PathPoint lvt_7_1_ = null;
      BlockPos lvt_8_1_ = new BlockPos(p_203245_1_, p_203245_2_, p_203245_3_);
      double lvt_9_1_ = this.func_203246_a(lvt_8_1_);
      if (lvt_9_1_ - p_203245_5_ > 1.125D) {
         return null;
      } else {
         PathNodeType lvt_11_1_ = this.getPathNodeType(this.blockaccess, p_203245_1_, p_203245_2_, p_203245_3_, this.entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, false, false);
         float lvt_12_1_ = this.entity.getPathPriority(lvt_11_1_);
         double lvt_13_1_ = (double)this.entity.getWidth() / 2.0D;
         if (lvt_12_1_ >= 0.0F) {
            lvt_7_1_ = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
            lvt_7_1_.nodeType = lvt_11_1_;
            lvt_7_1_.costMalus = Math.max(lvt_7_1_.costMalus, lvt_12_1_);
         }

         if (lvt_11_1_ != PathNodeType.WATER && lvt_11_1_ != PathNodeType.WALKABLE) {
            if (lvt_7_1_ == null && p_203245_4_ > 0 && lvt_11_1_ != PathNodeType.FENCE && lvt_11_1_ != PathNodeType.TRAPDOOR) {
               lvt_7_1_ = this.func_203245_a(p_203245_1_, p_203245_2_ + 1, p_203245_3_, p_203245_4_ - 1, p_203245_5_);
            }

            if (lvt_11_1_ == PathNodeType.OPEN) {
               AxisAlignedBB lvt_15_1_ = new AxisAlignedBB((double)p_203245_1_ - lvt_13_1_ + 0.5D, (double)p_203245_2_ + 0.001D, (double)p_203245_3_ - lvt_13_1_ + 0.5D, (double)p_203245_1_ + lvt_13_1_ + 0.5D, (double)((float)p_203245_2_ + this.entity.getHeight()), (double)p_203245_3_ + lvt_13_1_ + 0.5D);
               if (!this.entity.world.func_226665_a__(this.entity, lvt_15_1_)) {
                  return null;
               }

               PathNodeType lvt_16_1_ = this.getPathNodeType(this.blockaccess, p_203245_1_, p_203245_2_ - 1, p_203245_3_, this.entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, false, false);
               if (lvt_16_1_ == PathNodeType.BLOCKED) {
                  lvt_7_1_ = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
                  lvt_7_1_.nodeType = PathNodeType.WALKABLE;
                  lvt_7_1_.costMalus = Math.max(lvt_7_1_.costMalus, lvt_12_1_);
                  return lvt_7_1_;
               }

               if (lvt_16_1_ == PathNodeType.WATER) {
                  lvt_7_1_ = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
                  lvt_7_1_.nodeType = PathNodeType.WATER;
                  lvt_7_1_.costMalus = Math.max(lvt_7_1_.costMalus, lvt_12_1_);
                  return lvt_7_1_;
               }

               int var17 = 0;

               while(p_203245_2_ > 0 && lvt_11_1_ == PathNodeType.OPEN) {
                  --p_203245_2_;
                  if (var17++ >= this.entity.getMaxFallHeight()) {
                     return null;
                  }

                  lvt_11_1_ = this.getPathNodeType(this.blockaccess, p_203245_1_, p_203245_2_, p_203245_3_, this.entity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, false, false);
                  lvt_12_1_ = this.entity.getPathPriority(lvt_11_1_);
                  if (lvt_11_1_ != PathNodeType.OPEN && lvt_12_1_ >= 0.0F) {
                     lvt_7_1_ = this.openPoint(p_203245_1_, p_203245_2_, p_203245_3_);
                     lvt_7_1_.nodeType = lvt_11_1_;
                     lvt_7_1_.costMalus = Math.max(lvt_7_1_.costMalus, lvt_12_1_);
                     break;
                  }

                  if (lvt_12_1_ < 0.0F) {
                     return null;
                  }
               }
            }

            return lvt_7_1_;
         } else {
            if (p_203245_2_ < this.entity.world.getSeaLevel() - 10 && lvt_7_1_ != null) {
               ++lvt_7_1_.costMalus;
            }

            return lvt_7_1_;
         }
      }
   }

   protected PathNodeType func_215744_a(IBlockReader p_215744_1_, boolean p_215744_2_, boolean p_215744_3_, BlockPos p_215744_4_, PathNodeType p_215744_5_) {
      if (p_215744_5_ == PathNodeType.RAIL && !(p_215744_1_.getBlockState(p_215744_4_).getBlock() instanceof AbstractRailBlock) && !(p_215744_1_.getBlockState(p_215744_4_.down()).getBlock() instanceof AbstractRailBlock)) {
         p_215744_5_ = PathNodeType.FENCE;
      }

      if (p_215744_5_ == PathNodeType.DOOR_OPEN || p_215744_5_ == PathNodeType.DOOR_WOOD_CLOSED || p_215744_5_ == PathNodeType.DOOR_IRON_CLOSED) {
         p_215744_5_ = PathNodeType.BLOCKED;
      }

      if (p_215744_5_ == PathNodeType.LEAVES) {
         p_215744_5_ = PathNodeType.BLOCKED;
      }

      return p_215744_5_;
   }

   public PathNodeType getPathNodeType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      PathNodeType lvt_5_1_ = getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_);
      if (lvt_5_1_ == PathNodeType.WATER) {
         Direction[] var11 = Direction.values();
         int var12 = var11.length;

         for(int var8 = 0; var8 < var12; ++var8) {
            Direction lvt_9_1_ = var11[var8];
            PathNodeType lvt_10_1_ = getPathNodeTypeRaw(p_186330_1_, p_186330_2_ + lvt_9_1_.getXOffset(), p_186330_3_ + lvt_9_1_.getYOffset(), p_186330_4_ + lvt_9_1_.getZOffset());
            if (lvt_10_1_ == PathNodeType.BLOCKED) {
               return PathNodeType.WATER_BORDER;
            }
         }

         return PathNodeType.WATER;
      } else {
         if (lvt_5_1_ == PathNodeType.OPEN && p_186330_3_ >= 1) {
            Block lvt_6_1_ = p_186330_1_.getBlockState(new BlockPos(p_186330_2_, p_186330_3_ - 1, p_186330_4_)).getBlock();
            PathNodeType lvt_7_1_ = getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_ - 1, p_186330_4_);
            if (lvt_7_1_ != PathNodeType.WALKABLE && lvt_7_1_ != PathNodeType.OPEN && lvt_7_1_ != PathNodeType.LAVA) {
               lvt_5_1_ = PathNodeType.WALKABLE;
            } else {
               lvt_5_1_ = PathNodeType.OPEN;
            }

            if (lvt_7_1_ == PathNodeType.DAMAGE_FIRE || lvt_6_1_ == Blocks.MAGMA_BLOCK || lvt_6_1_ == Blocks.CAMPFIRE) {
               lvt_5_1_ = PathNodeType.DAMAGE_FIRE;
            }

            if (lvt_7_1_ == PathNodeType.DAMAGE_CACTUS) {
               lvt_5_1_ = PathNodeType.DAMAGE_CACTUS;
            }

            if (lvt_7_1_ == PathNodeType.DAMAGE_OTHER) {
               lvt_5_1_ = PathNodeType.DAMAGE_OTHER;
            }
         }

         if (lvt_5_1_ == PathNodeType.WALKABLE) {
            lvt_5_1_ = checkNeighborBlocks(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_, lvt_5_1_);
         }

         return lvt_5_1_;
      }
   }
}
