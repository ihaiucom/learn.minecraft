package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class LandingApproachPhase extends Phase {
   private static final EntityPredicate field_221118_b = (new EntityPredicate()).setDistance(128.0D);
   private Path currentPath;
   private Vec3d targetLocation;

   public LandingApproachPhase(EnderDragonEntity p_i46789_1_) {
      super(p_i46789_1_);
   }

   public PhaseType<LandingApproachPhase> getType() {
      return PhaseType.LANDING_APPROACH;
   }

   public void initPhase() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   public void serverTick() {
      double lvt_1_1_ = this.targetLocation == null ? 0.0D : this.targetLocation.squareDistanceTo(this.dragon.func_226277_ct_(), this.dragon.func_226278_cu_(), this.dragon.func_226281_cx_());
      if (lvt_1_1_ < 100.0D || lvt_1_1_ > 22500.0D || this.dragon.collidedHorizontally || this.dragon.collidedVertically) {
         this.findNewTarget();
      }

   }

   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      if (this.currentPath == null || this.currentPath.isFinished()) {
         int lvt_1_1_ = this.dragon.initPathPoints();
         BlockPos lvt_2_1_ = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         PlayerEntity lvt_3_1_ = this.dragon.world.getClosestPlayer(field_221118_b, (double)lvt_2_1_.getX(), (double)lvt_2_1_.getY(), (double)lvt_2_1_.getZ());
         int lvt_4_2_;
         if (lvt_3_1_ != null) {
            Vec3d lvt_5_1_ = (new Vec3d(lvt_3_1_.func_226277_ct_(), 0.0D, lvt_3_1_.func_226281_cx_())).normalize();
            lvt_4_2_ = this.dragon.getNearestPpIdx(-lvt_5_1_.x * 40.0D, 105.0D, -lvt_5_1_.z * 40.0D);
         } else {
            lvt_4_2_ = this.dragon.getNearestPpIdx(40.0D, (double)lvt_2_1_.getY(), 0.0D);
         }

         PathPoint lvt_5_2_ = new PathPoint(lvt_2_1_.getX(), lvt_2_1_.getY(), lvt_2_1_.getZ());
         this.currentPath = this.dragon.findPath(lvt_1_1_, lvt_4_2_, lvt_5_2_);
         if (this.currentPath != null) {
            this.currentPath.incrementPathIndex();
         }
      }

      this.navigateToNextPathNode();
      if (this.currentPath != null && this.currentPath.isFinished()) {
         this.dragon.getPhaseManager().setPhase(PhaseType.LANDING);
      }

   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isFinished()) {
         Vec3d lvt_1_1_ = this.currentPath.getCurrentPos();
         this.currentPath.incrementPathIndex();
         double lvt_2_1_ = lvt_1_1_.x;
         double lvt_4_1_ = lvt_1_1_.z;

         double lvt_6_1_;
         do {
            lvt_6_1_ = lvt_1_1_.y + (double)(this.dragon.getRNG().nextFloat() * 20.0F);
         } while(lvt_6_1_ < lvt_1_1_.y);

         this.targetLocation = new Vec3d(lvt_2_1_, lvt_6_1_, lvt_4_1_);
      }

   }
}
