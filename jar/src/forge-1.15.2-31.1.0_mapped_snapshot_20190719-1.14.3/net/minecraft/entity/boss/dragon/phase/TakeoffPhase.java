package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class TakeoffPhase extends Phase {
   private boolean firstTick;
   private Path currentPath;
   private Vec3d targetLocation;

   public TakeoffPhase(EnderDragonEntity p_i46783_1_) {
      super(p_i46783_1_);
   }

   public void serverTick() {
      if (!this.firstTick && this.currentPath != null) {
         BlockPos lvt_1_1_ = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         if (!lvt_1_1_.withinDistance(this.dragon.getPositionVec(), 10.0D)) {
            this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
         }
      } else {
         this.firstTick = false;
         this.findNewTarget();
      }

   }

   public void initPhase() {
      this.firstTick = true;
      this.currentPath = null;
      this.targetLocation = null;
   }

   private void findNewTarget() {
      int lvt_1_1_ = this.dragon.initPathPoints();
      Vec3d lvt_2_1_ = this.dragon.getHeadLookVec(1.0F);
      int lvt_3_1_ = this.dragon.getNearestPpIdx(-lvt_2_1_.x * 40.0D, 105.0D, -lvt_2_1_.z * 40.0D);
      if (this.dragon.getFightManager() != null && this.dragon.getFightManager().getNumAliveCrystals() > 0) {
         lvt_3_1_ %= 12;
         if (lvt_3_1_ < 0) {
            lvt_3_1_ += 12;
         }
      } else {
         lvt_3_1_ -= 12;
         lvt_3_1_ &= 7;
         lvt_3_1_ += 12;
      }

      this.currentPath = this.dragon.findPath(lvt_1_1_, lvt_3_1_, (PathPoint)null);
      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null) {
         this.currentPath.incrementPathIndex();
         if (!this.currentPath.isFinished()) {
            Vec3d lvt_1_1_ = this.currentPath.getCurrentPos();
            this.currentPath.incrementPathIndex();

            double lvt_2_1_;
            do {
               lvt_2_1_ = lvt_1_1_.y + (double)(this.dragon.getRNG().nextFloat() * 20.0F);
            } while(lvt_2_1_ < lvt_1_1_.y);

            this.targetLocation = new Vec3d(lvt_1_1_.x, lvt_2_1_, lvt_1_1_.z);
         }
      }

   }

   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<TakeoffPhase> getType() {
      return PhaseType.TAKEOFF;
   }
}
