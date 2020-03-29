package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class InteractDoorGoal extends Goal {
   protected MobEntity entity;
   protected BlockPos doorPosition;
   protected boolean doorInteract;
   private boolean hasStoppedDoorInteraction;
   private float entityPositionX;
   private float entityPositionZ;

   public InteractDoorGoal(MobEntity p_i1621_1_) {
      this.doorPosition = BlockPos.ZERO;
      this.entity = p_i1621_1_;
      if (!(p_i1621_1_.getNavigator() instanceof GroundPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   protected boolean canDestroy() {
      if (!this.doorInteract) {
         return false;
      } else {
         BlockState lvt_1_1_ = this.entity.world.getBlockState(this.doorPosition);
         if (!(lvt_1_1_.getBlock() instanceof DoorBlock)) {
            this.doorInteract = false;
            return false;
         } else {
            return (Boolean)lvt_1_1_.get(DoorBlock.OPEN);
         }
      }
   }

   protected void toggleDoor(boolean p_195921_1_) {
      if (this.doorInteract) {
         BlockState lvt_2_1_ = this.entity.world.getBlockState(this.doorPosition);
         if (lvt_2_1_.getBlock() instanceof DoorBlock) {
            ((DoorBlock)lvt_2_1_.getBlock()).toggleDoor(this.entity.world, this.doorPosition, p_195921_1_);
         }
      }

   }

   public boolean shouldExecute() {
      if (!this.entity.collidedHorizontally) {
         return false;
      } else {
         GroundPathNavigator lvt_1_1_ = (GroundPathNavigator)this.entity.getNavigator();
         Path lvt_2_1_ = lvt_1_1_.getPath();
         if (lvt_2_1_ != null && !lvt_2_1_.isFinished() && lvt_1_1_.getEnterDoors()) {
            for(int lvt_3_1_ = 0; lvt_3_1_ < Math.min(lvt_2_1_.getCurrentPathIndex() + 2, lvt_2_1_.getCurrentPathLength()); ++lvt_3_1_) {
               PathPoint lvt_4_1_ = lvt_2_1_.getPathPointFromIndex(lvt_3_1_);
               this.doorPosition = new BlockPos(lvt_4_1_.x, lvt_4_1_.y + 1, lvt_4_1_.z);
               if (this.entity.getDistanceSq((double)this.doorPosition.getX(), this.entity.func_226278_cu_(), (double)this.doorPosition.getZ()) <= 2.25D) {
                  this.doorInteract = func_220695_a(this.entity.world, this.doorPosition);
                  if (this.doorInteract) {
                     return true;
                  }
               }
            }

            this.doorPosition = (new BlockPos(this.entity)).up();
            this.doorInteract = func_220695_a(this.entity.world, this.doorPosition);
            return this.doorInteract;
         } else {
            return false;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.hasStoppedDoorInteraction;
   }

   public void startExecuting() {
      this.hasStoppedDoorInteraction = false;
      this.entityPositionX = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.entity.func_226277_ct_());
      this.entityPositionZ = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.entity.func_226281_cx_());
   }

   public void tick() {
      float lvt_1_1_ = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.entity.func_226277_ct_());
      float lvt_2_1_ = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.entity.func_226281_cx_());
      float lvt_3_1_ = this.entityPositionX * lvt_1_1_ + this.entityPositionZ * lvt_2_1_;
      if (lvt_3_1_ < 0.0F) {
         this.hasStoppedDoorInteraction = true;
      }

   }

   public static boolean func_220695_a(World p_220695_0_, BlockPos p_220695_1_) {
      BlockState lvt_2_1_ = p_220695_0_.getBlockState(p_220695_1_);
      return lvt_2_1_.getBlock() instanceof DoorBlock && lvt_2_1_.getMaterial() == Material.WOOD;
   }
}
