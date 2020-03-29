package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;

public class PanicGoal extends Goal {
   protected final CreatureEntity creature;
   protected final double speed;
   protected double randPosX;
   protected double randPosY;
   protected double randPosZ;

   public PanicGoal(CreatureEntity p_i1645_1_, double p_i1645_2_) {
      this.creature = p_i1645_1_;
      this.speed = p_i1645_2_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean shouldExecute() {
      if (this.creature.getRevengeTarget() == null && !this.creature.isBurning()) {
         return false;
      } else {
         if (this.creature.isBurning()) {
            BlockPos lvt_1_1_ = this.getRandPos(this.creature.world, this.creature, 5, 4);
            if (lvt_1_1_ != null) {
               this.randPosX = (double)lvt_1_1_.getX();
               this.randPosY = (double)lvt_1_1_.getY();
               this.randPosZ = (double)lvt_1_1_.getZ();
               return true;
            }
         }

         return this.findRandomPosition();
      }
   }

   protected boolean findRandomPosition() {
      Vec3d lvt_1_1_ = RandomPositionGenerator.findRandomTarget(this.creature, 5, 4);
      if (lvt_1_1_ == null) {
         return false;
      } else {
         this.randPosX = lvt_1_1_.x;
         this.randPosY = lvt_1_1_.y;
         this.randPosZ = lvt_1_1_.z;
         return true;
      }
   }

   public void startExecuting() {
      this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
   }

   public boolean shouldContinueExecuting() {
      return !this.creature.getNavigator().noPath();
   }

   @Nullable
   protected BlockPos getRandPos(IBlockReader p_188497_1_, Entity p_188497_2_, int p_188497_3_, int p_188497_4_) {
      BlockPos lvt_5_1_ = new BlockPos(p_188497_2_);
      int lvt_6_1_ = lvt_5_1_.getX();
      int lvt_7_1_ = lvt_5_1_.getY();
      int lvt_8_1_ = lvt_5_1_.getZ();
      float lvt_9_1_ = (float)(p_188497_3_ * p_188497_3_ * p_188497_4_ * 2);
      BlockPos lvt_10_1_ = null;
      BlockPos.Mutable lvt_11_1_ = new BlockPos.Mutable();

      for(int lvt_12_1_ = lvt_6_1_ - p_188497_3_; lvt_12_1_ <= lvt_6_1_ + p_188497_3_; ++lvt_12_1_) {
         for(int lvt_13_1_ = lvt_7_1_ - p_188497_4_; lvt_13_1_ <= lvt_7_1_ + p_188497_4_; ++lvt_13_1_) {
            for(int lvt_14_1_ = lvt_8_1_ - p_188497_3_; lvt_14_1_ <= lvt_8_1_ + p_188497_3_; ++lvt_14_1_) {
               lvt_11_1_.setPos(lvt_12_1_, lvt_13_1_, lvt_14_1_);
               if (p_188497_1_.getFluidState(lvt_11_1_).isTagged(FluidTags.WATER)) {
                  float lvt_15_1_ = (float)((lvt_12_1_ - lvt_6_1_) * (lvt_12_1_ - lvt_6_1_) + (lvt_13_1_ - lvt_7_1_) * (lvt_13_1_ - lvt_7_1_) + (lvt_14_1_ - lvt_8_1_) * (lvt_14_1_ - lvt_8_1_));
                  if (lvt_15_1_ < lvt_9_1_) {
                     lvt_9_1_ = lvt_15_1_;
                     lvt_10_1_ = new BlockPos(lvt_11_1_);
                  }
               }
            }
         }
      }

      return lvt_10_1_;
   }
}
