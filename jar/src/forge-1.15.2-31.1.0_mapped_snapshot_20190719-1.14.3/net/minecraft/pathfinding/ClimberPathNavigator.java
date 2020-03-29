package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClimberPathNavigator extends GroundPathNavigator {
   private BlockPos targetPosition;

   public ClimberPathNavigator(MobEntity p_i45874_1_, World p_i45874_2_) {
      super(p_i45874_1_, p_i45874_2_);
   }

   public Path getPathToPos(BlockPos p_179680_1_, int p_179680_2_) {
      this.targetPosition = p_179680_1_;
      return super.getPathToPos(p_179680_1_, p_179680_2_);
   }

   public Path getPathToEntityLiving(Entity p_75494_1_, int p_75494_2_) {
      this.targetPosition = new BlockPos(p_75494_1_);
      return super.getPathToEntityLiving(p_75494_1_, p_75494_2_);
   }

   public boolean tryMoveToEntityLiving(Entity p_75497_1_, double p_75497_2_) {
      Path lvt_4_1_ = this.getPathToEntityLiving(p_75497_1_, 0);
      if (lvt_4_1_ != null) {
         return this.setPath(lvt_4_1_, p_75497_2_);
      } else {
         this.targetPosition = new BlockPos(p_75497_1_);
         this.speed = p_75497_2_;
         return true;
      }
   }

   public void tick() {
      if (!this.noPath()) {
         super.tick();
      } else {
         if (this.targetPosition != null) {
            if (!this.targetPosition.withinDistance(this.entity.getPositionVec(), (double)this.entity.getWidth()) && (this.entity.func_226278_cu_() <= (double)this.targetPosition.getY() || !(new BlockPos((double)this.targetPosition.getX(), this.entity.func_226278_cu_(), (double)this.targetPosition.getZ())).withinDistance(this.entity.getPositionVec(), (double)this.entity.getWidth()))) {
               this.entity.getMoveHelper().setMoveTo((double)this.targetPosition.getX(), (double)this.targetPosition.getY(), (double)this.targetPosition.getZ(), this.speed);
            } else {
               this.targetPosition = null;
            }
         }

      }
   }
}
