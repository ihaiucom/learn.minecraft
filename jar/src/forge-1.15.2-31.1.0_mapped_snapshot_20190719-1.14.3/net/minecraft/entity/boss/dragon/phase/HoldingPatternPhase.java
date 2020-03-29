package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class HoldingPatternPhase extends Phase {
   private static final EntityPredicate field_221117_b = (new EntityPredicate()).setDistance(64.0D);
   private Path currentPath;
   private Vec3d targetLocation;
   private boolean clockwise;

   public HoldingPatternPhase(EnderDragonEntity p_i46791_1_) {
      super(p_i46791_1_);
   }

   public PhaseType<HoldingPatternPhase> getType() {
      return PhaseType.HOLDING_PATTERN;
   }

   public void serverTick() {
      double lvt_1_1_ = this.targetLocation == null ? 0.0D : this.targetLocation.squareDistanceTo(this.dragon.func_226277_ct_(), this.dragon.func_226278_cu_(), this.dragon.func_226281_cx_());
      if (lvt_1_1_ < 100.0D || lvt_1_1_ > 22500.0D || this.dragon.collidedHorizontally || this.dragon.collidedVertically) {
         this.findNewTarget();
      }

   }

   public void initPhase() {
      this.currentPath = null;
      this.targetLocation = null;
   }

   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   private void findNewTarget() {
      int lvt_2_2_;
      if (this.currentPath != null && this.currentPath.isFinished()) {
         BlockPos lvt_1_1_ = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
         lvt_2_2_ = this.dragon.getFightManager() == null ? 0 : this.dragon.getFightManager().getNumAliveCrystals();
         if (this.dragon.getRNG().nextInt(lvt_2_2_ + 3) == 0) {
            this.dragon.getPhaseManager().setPhase(PhaseType.LANDING_APPROACH);
            return;
         }

         double lvt_3_1_ = 64.0D;
         PlayerEntity lvt_5_1_ = this.dragon.world.getClosestPlayer(field_221117_b, (double)lvt_1_1_.getX(), (double)lvt_1_1_.getY(), (double)lvt_1_1_.getZ());
         if (lvt_5_1_ != null) {
            lvt_3_1_ = lvt_1_1_.distanceSq(lvt_5_1_.getPositionVec(), true) / 512.0D;
         }

         if (lvt_5_1_ != null && !lvt_5_1_.abilities.disableDamage && (this.dragon.getRNG().nextInt(MathHelper.abs((int)lvt_3_1_) + 2) == 0 || this.dragon.getRNG().nextInt(lvt_2_2_ + 2) == 0)) {
            this.strafePlayer(lvt_5_1_);
            return;
         }
      }

      if (this.currentPath == null || this.currentPath.isFinished()) {
         int lvt_1_2_ = this.dragon.initPathPoints();
         lvt_2_2_ = lvt_1_2_;
         if (this.dragon.getRNG().nextInt(8) == 0) {
            this.clockwise = !this.clockwise;
            lvt_2_2_ = lvt_1_2_ + 6;
         }

         if (this.clockwise) {
            ++lvt_2_2_;
         } else {
            --lvt_2_2_;
         }

         if (this.dragon.getFightManager() != null && this.dragon.getFightManager().getNumAliveCrystals() >= 0) {
            lvt_2_2_ %= 12;
            if (lvt_2_2_ < 0) {
               lvt_2_2_ += 12;
            }
         } else {
            lvt_2_2_ -= 12;
            lvt_2_2_ &= 7;
            lvt_2_2_ += 12;
         }

         this.currentPath = this.dragon.findPath(lvt_1_2_, lvt_2_2_, (PathPoint)null);
         if (this.currentPath != null) {
            this.currentPath.incrementPathIndex();
         }
      }

      this.navigateToNextPathNode();
   }

   private void strafePlayer(PlayerEntity p_188674_1_) {
      this.dragon.getPhaseManager().setPhase(PhaseType.STRAFE_PLAYER);
      ((StrafePlayerPhase)this.dragon.getPhaseManager().getPhase(PhaseType.STRAFE_PLAYER)).setTarget(p_188674_1_);
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

   public void onCrystalDestroyed(EnderCrystalEntity p_188655_1_, BlockPos p_188655_2_, DamageSource p_188655_3_, @Nullable PlayerEntity p_188655_4_) {
      if (p_188655_4_ != null && !p_188655_4_.abilities.disableDamage) {
         this.strafePlayer(p_188655_4_);
      }

   }
}
