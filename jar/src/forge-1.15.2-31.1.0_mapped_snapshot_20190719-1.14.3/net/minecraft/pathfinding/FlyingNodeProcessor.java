package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class FlyingNodeProcessor extends WalkNodeProcessor {
   public void func_225578_a_(Region p_225578_1_, MobEntity p_225578_2_) {
      super.func_225578_a_(p_225578_1_, p_225578_2_);
      this.avoidsWater = p_225578_2_.getPathPriority(PathNodeType.WATER);
   }

   public void postProcess() {
      this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
      super.postProcess();
   }

   public PathPoint getStart() {
      int lvt_1_2_;
      if (this.getCanSwim() && this.entity.isInWater()) {
         lvt_1_2_ = MathHelper.floor(this.entity.func_226278_cu_());
         BlockPos.Mutable lvt_2_1_ = new BlockPos.Mutable(this.entity.func_226277_ct_(), (double)lvt_1_2_, this.entity.func_226281_cx_());

         for(Block lvt_3_1_ = this.blockaccess.getBlockState(lvt_2_1_).getBlock(); lvt_3_1_ == Blocks.WATER; lvt_3_1_ = this.blockaccess.getBlockState(lvt_2_1_).getBlock()) {
            ++lvt_1_2_;
            lvt_2_1_.setPos(this.entity.func_226277_ct_(), (double)lvt_1_2_, this.entity.func_226281_cx_());
         }
      } else {
         lvt_1_2_ = MathHelper.floor(this.entity.func_226278_cu_() + 0.5D);
      }

      BlockPos lvt_2_2_ = new BlockPos(this.entity);
      PathNodeType lvt_3_2_ = this.getPathNodeType(this.entity, lvt_2_2_.getX(), lvt_1_2_, lvt_2_2_.getZ());
      if (this.entity.getPathPriority(lvt_3_2_) < 0.0F) {
         Set<BlockPos> lvt_4_1_ = Sets.newHashSet();
         lvt_4_1_.add(new BlockPos(this.entity.getBoundingBox().minX, (double)lvt_1_2_, this.entity.getBoundingBox().minZ));
         lvt_4_1_.add(new BlockPos(this.entity.getBoundingBox().minX, (double)lvt_1_2_, this.entity.getBoundingBox().maxZ));
         lvt_4_1_.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)lvt_1_2_, this.entity.getBoundingBox().minZ));
         lvt_4_1_.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)lvt_1_2_, this.entity.getBoundingBox().maxZ));
         Iterator var5 = lvt_4_1_.iterator();

         while(var5.hasNext()) {
            BlockPos lvt_6_1_ = (BlockPos)var5.next();
            PathNodeType lvt_7_1_ = this.getPathNodeType(this.entity, lvt_6_1_);
            if (this.entity.getPathPriority(lvt_7_1_) >= 0.0F) {
               return super.openPoint(lvt_6_1_.getX(), lvt_6_1_.getY(), lvt_6_1_.getZ());
            }
         }
      }

      return super.openPoint(lvt_2_2_.getX(), lvt_1_2_, lvt_2_2_.getZ());
   }

   public FlaggedPathPoint func_224768_a(double p_224768_1_, double p_224768_3_, double p_224768_5_) {
      return new FlaggedPathPoint(super.openPoint(MathHelper.floor(p_224768_1_), MathHelper.floor(p_224768_3_), MathHelper.floor(p_224768_5_)));
   }

   public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int lvt_3_1_ = 0;
      PathPoint lvt_4_1_ = this.openPoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z + 1);
      if (this.func_227477_b_(lvt_4_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_4_1_;
      }

      PathPoint lvt_5_1_ = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z);
      if (this.func_227477_b_(lvt_5_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_5_1_;
      }

      PathPoint lvt_6_1_ = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z);
      if (this.func_227477_b_(lvt_6_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_6_1_;
      }

      PathPoint lvt_7_1_ = this.openPoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z - 1);
      if (this.func_227477_b_(lvt_7_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_7_1_;
      }

      PathPoint lvt_8_1_ = this.openPoint(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.func_227477_b_(lvt_8_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_8_1_;
      }

      PathPoint lvt_9_1_ = this.openPoint(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z);
      if (this.func_227477_b_(lvt_9_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_9_1_;
      }

      PathPoint lvt_10_1_ = this.openPoint(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(lvt_10_1_) && this.func_227476_a_(lvt_4_1_) && this.func_227476_a_(lvt_8_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_10_1_;
      }

      PathPoint lvt_11_1_ = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.func_227477_b_(lvt_11_1_) && this.func_227476_a_(lvt_5_1_) && this.func_227476_a_(lvt_8_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_11_1_;
      }

      PathPoint lvt_12_1_ = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z);
      if (this.func_227477_b_(lvt_12_1_) && this.func_227476_a_(lvt_6_1_) && this.func_227476_a_(lvt_8_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_12_1_;
      }

      PathPoint lvt_13_1_ = this.openPoint(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(lvt_13_1_) && this.func_227476_a_(lvt_7_1_) && this.func_227476_a_(lvt_8_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_13_1_;
      }

      PathPoint lvt_14_1_ = this.openPoint(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(lvt_14_1_) && this.func_227476_a_(lvt_4_1_) && this.func_227476_a_(lvt_9_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_14_1_;
      }

      PathPoint lvt_15_1_ = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z);
      if (this.func_227477_b_(lvt_15_1_) && this.func_227476_a_(lvt_5_1_) && this.func_227476_a_(lvt_9_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_15_1_;
      }

      PathPoint lvt_16_1_ = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z);
      if (this.func_227477_b_(lvt_16_1_) && this.func_227476_a_(lvt_6_1_) && this.func_227476_a_(lvt_9_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_16_1_;
      }

      PathPoint lvt_17_1_ = this.openPoint(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(lvt_17_1_) && this.func_227476_a_(lvt_7_1_) && this.func_227476_a_(lvt_9_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_17_1_;
      }

      PathPoint lvt_18_1_ = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z - 1);
      if (this.func_227477_b_(lvt_18_1_) && this.func_227476_a_(lvt_7_1_) && this.func_227476_a_(lvt_6_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_18_1_;
      }

      PathPoint lvt_19_1_ = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z + 1);
      if (this.func_227477_b_(lvt_19_1_) && this.func_227476_a_(lvt_4_1_) && this.func_227476_a_(lvt_6_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_19_1_;
      }

      PathPoint lvt_20_1_ = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z - 1);
      if (this.func_227477_b_(lvt_20_1_) && this.func_227476_a_(lvt_7_1_) && this.func_227476_a_(lvt_5_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_20_1_;
      }

      PathPoint lvt_21_1_ = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z + 1);
      if (this.func_227477_b_(lvt_21_1_) && this.func_227476_a_(lvt_4_1_) && this.func_227476_a_(lvt_5_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_21_1_;
      }

      PathPoint lvt_22_1_ = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(lvt_22_1_) && this.func_227476_a_(lvt_18_1_) && this.func_227476_a_(lvt_13_1_) && this.func_227476_a_(lvt_12_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_22_1_;
      }

      PathPoint lvt_23_1_ = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(lvt_23_1_) && this.func_227476_a_(lvt_19_1_) && this.func_227476_a_(lvt_10_1_) && this.func_227476_a_(lvt_12_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_23_1_;
      }

      PathPoint lvt_24_1_ = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(lvt_24_1_) && this.func_227476_a_(lvt_20_1_) && this.func_227476_a_(lvt_13_1_) && this.func_227476_a_(lvt_11_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_24_1_;
      }

      PathPoint lvt_25_1_ = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(lvt_25_1_) && this.func_227476_a_(lvt_21_1_) && this.func_227476_a_(lvt_10_1_) && this.func_227476_a_(lvt_11_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_25_1_;
      }

      PathPoint lvt_26_1_ = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(lvt_26_1_) && this.func_227476_a_(lvt_18_1_) && this.func_227476_a_(lvt_17_1_) && this.func_227476_a_(lvt_16_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_26_1_;
      }

      PathPoint lvt_27_1_ = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(lvt_27_1_) && this.func_227476_a_(lvt_19_1_) && this.func_227476_a_(lvt_14_1_) && this.func_227476_a_(lvt_16_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_27_1_;
      }

      PathPoint lvt_28_1_ = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z - 1);
      if (this.func_227477_b_(lvt_28_1_) && this.func_227476_a_(lvt_20_1_) && this.func_227476_a_(lvt_17_1_) && this.func_227476_a_(lvt_15_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_28_1_;
      }

      PathPoint lvt_29_1_ = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z + 1);
      if (this.func_227477_b_(lvt_29_1_) && this.func_227476_a_(lvt_21_1_) && this.func_227476_a_(lvt_14_1_) && this.func_227476_a_(lvt_15_1_)) {
         p_222859_1_[lvt_3_1_++] = lvt_29_1_;
      }

      return lvt_3_1_;
   }

   private boolean func_227476_a_(@Nullable PathPoint p_227476_1_) {
      return p_227476_1_ != null && p_227476_1_.costMalus >= 0.0F;
   }

   private boolean func_227477_b_(@Nullable PathPoint p_227477_1_) {
      return p_227477_1_ != null && !p_227477_1_.visited;
   }

   @Nullable
   protected PathPoint openPoint(int p_176159_1_, int p_176159_2_, int p_176159_3_) {
      PathPoint lvt_4_1_ = null;
      PathNodeType lvt_5_1_ = this.getPathNodeType(this.entity, p_176159_1_, p_176159_2_, p_176159_3_);
      float lvt_6_1_ = this.entity.getPathPriority(lvt_5_1_);
      if (lvt_6_1_ >= 0.0F) {
         lvt_4_1_ = super.openPoint(p_176159_1_, p_176159_2_, p_176159_3_);
         lvt_4_1_.nodeType = lvt_5_1_;
         lvt_4_1_.costMalus = Math.max(lvt_4_1_.costMalus, lvt_6_1_);
         if (lvt_5_1_ == PathNodeType.WALKABLE) {
            ++lvt_4_1_.costMalus;
         }
      }

      return lvt_5_1_ != PathNodeType.OPEN && lvt_5_1_ != PathNodeType.WALKABLE ? lvt_4_1_ : lvt_4_1_;
   }

   public PathNodeType getPathNodeType(IBlockReader p_186319_1_, int p_186319_2_, int p_186319_3_, int p_186319_4_, MobEntity p_186319_5_, int p_186319_6_, int p_186319_7_, int p_186319_8_, boolean p_186319_9_, boolean p_186319_10_) {
      EnumSet<PathNodeType> lvt_11_1_ = EnumSet.noneOf(PathNodeType.class);
      PathNodeType lvt_12_1_ = PathNodeType.BLOCKED;
      BlockPos lvt_13_1_ = new BlockPos(p_186319_5_);
      lvt_12_1_ = this.getPathNodeType(p_186319_1_, p_186319_2_, p_186319_3_, p_186319_4_, p_186319_6_, p_186319_7_, p_186319_8_, p_186319_9_, p_186319_10_, lvt_11_1_, lvt_12_1_, lvt_13_1_);
      if (lvt_11_1_.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      } else {
         PathNodeType lvt_14_1_ = PathNodeType.BLOCKED;
         Iterator var15 = lvt_11_1_.iterator();

         while(var15.hasNext()) {
            PathNodeType lvt_16_1_ = (PathNodeType)var15.next();
            if (p_186319_5_.getPathPriority(lvt_16_1_) < 0.0F) {
               return lvt_16_1_;
            }

            if (p_186319_5_.getPathPriority(lvt_16_1_) >= p_186319_5_.getPathPriority(lvt_14_1_)) {
               lvt_14_1_ = lvt_16_1_;
            }
         }

         if (lvt_12_1_ == PathNodeType.OPEN && p_186319_5_.getPathPriority(lvt_14_1_) == 0.0F) {
            return PathNodeType.OPEN;
         } else {
            return lvt_14_1_;
         }
      }
   }

   public PathNodeType getPathNodeType(IBlockReader p_186330_1_, int p_186330_2_, int p_186330_3_, int p_186330_4_) {
      PathNodeType lvt_5_1_ = getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_);
      if (lvt_5_1_ == PathNodeType.OPEN && p_186330_3_ >= 1) {
         Block lvt_6_1_ = p_186330_1_.getBlockState(new BlockPos(p_186330_2_, p_186330_3_ - 1, p_186330_4_)).getBlock();
         PathNodeType lvt_7_1_ = getPathNodeTypeRaw(p_186330_1_, p_186330_2_, p_186330_3_ - 1, p_186330_4_);
         if (lvt_7_1_ != PathNodeType.DAMAGE_FIRE && lvt_6_1_ != Blocks.MAGMA_BLOCK && lvt_7_1_ != PathNodeType.LAVA && lvt_6_1_ != Blocks.CAMPFIRE) {
            if (lvt_7_1_ == PathNodeType.DAMAGE_CACTUS) {
               lvt_5_1_ = PathNodeType.DAMAGE_CACTUS;
            } else if (lvt_7_1_ == PathNodeType.DAMAGE_OTHER) {
               lvt_5_1_ = PathNodeType.DAMAGE_OTHER;
            } else if (lvt_7_1_ == PathNodeType.COCOA) {
               lvt_5_1_ = PathNodeType.COCOA;
            } else if (lvt_7_1_ == PathNodeType.FENCE) {
               lvt_5_1_ = PathNodeType.FENCE;
            } else {
               lvt_5_1_ = lvt_7_1_ != PathNodeType.WALKABLE && lvt_7_1_ != PathNodeType.OPEN && lvt_7_1_ != PathNodeType.WATER ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            }
         } else {
            lvt_5_1_ = PathNodeType.DAMAGE_FIRE;
         }
      }

      if (lvt_5_1_ == PathNodeType.WALKABLE || lvt_5_1_ == PathNodeType.OPEN) {
         lvt_5_1_ = checkNeighborBlocks(p_186330_1_, p_186330_2_, p_186330_3_, p_186330_4_, lvt_5_1_);
      }

      return lvt_5_1_;
   }

   private PathNodeType getPathNodeType(MobEntity p_192559_1_, BlockPos p_192559_2_) {
      return this.getPathNodeType(p_192559_1_, p_192559_2_.getX(), p_192559_2_.getY(), p_192559_2_.getZ());
   }

   private PathNodeType getPathNodeType(MobEntity p_192558_1_, int p_192558_2_, int p_192558_3_, int p_192558_4_) {
      return this.getPathNodeType(this.blockaccess, p_192558_2_, p_192558_3_, p_192558_4_, p_192558_1_, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
   }
}
