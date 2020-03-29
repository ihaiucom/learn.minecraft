package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrafePlayerPhase extends Phase {
   private static final Logger LOGGER = LogManager.getLogger();
   private int fireballCharge;
   private Path currentPath;
   private Vec3d targetLocation;
   private LivingEntity attackTarget;
   private boolean holdingPatternClockwise;

   public StrafePlayerPhase(EnderDragonEntity p_i46784_1_) {
      super(p_i46784_1_);
   }

   public void serverTick() {
      if (this.attackTarget == null) {
         LOGGER.warn("Skipping player strafe phase because no player was found");
         this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      } else {
         double lvt_1_2_;
         double lvt_3_2_;
         double lvt_9_2_;
         if (this.currentPath != null && this.currentPath.isFinished()) {
            lvt_1_2_ = this.attackTarget.func_226277_ct_();
            lvt_3_2_ = this.attackTarget.func_226281_cx_();
            double lvt_5_1_ = lvt_1_2_ - this.dragon.func_226277_ct_();
            double lvt_7_1_ = lvt_3_2_ - this.dragon.func_226281_cx_();
            lvt_9_2_ = (double)MathHelper.sqrt(lvt_5_1_ * lvt_5_1_ + lvt_7_1_ * lvt_7_1_);
            double lvt_11_1_ = Math.min(0.4000000059604645D + lvt_9_2_ / 80.0D - 1.0D, 10.0D);
            this.targetLocation = new Vec3d(lvt_1_2_, this.attackTarget.func_226278_cu_() + lvt_11_1_, lvt_3_2_);
         }

         lvt_1_2_ = this.targetLocation == null ? 0.0D : this.targetLocation.squareDistanceTo(this.dragon.func_226277_ct_(), this.dragon.func_226278_cu_(), this.dragon.func_226281_cx_());
         if (lvt_1_2_ < 100.0D || lvt_1_2_ > 22500.0D) {
            this.findNewTarget();
         }

         lvt_3_2_ = 64.0D;
         if (this.attackTarget.getDistanceSq(this.dragon) < 4096.0D) {
            if (this.dragon.canEntityBeSeen(this.attackTarget)) {
               ++this.fireballCharge;
               Vec3d lvt_5_2_ = (new Vec3d(this.attackTarget.func_226277_ct_() - this.dragon.func_226277_ct_(), 0.0D, this.attackTarget.func_226281_cx_() - this.dragon.func_226281_cx_())).normalize();
               Vec3d lvt_6_1_ = (new Vec3d((double)MathHelper.sin(this.dragon.rotationYaw * 0.017453292F), 0.0D, (double)(-MathHelper.cos(this.dragon.rotationYaw * 0.017453292F)))).normalize();
               float lvt_7_2_ = (float)lvt_6_1_.dotProduct(lvt_5_2_);
               float lvt_8_1_ = (float)(Math.acos((double)lvt_7_2_) * 57.2957763671875D);
               lvt_8_1_ += 0.5F;
               if (this.fireballCharge >= 5 && lvt_8_1_ >= 0.0F && lvt_8_1_ < 10.0F) {
                  lvt_9_2_ = 1.0D;
                  Vec3d lvt_11_2_ = this.dragon.getLook(1.0F);
                  double lvt_12_1_ = this.dragon.field_70986_h.func_226277_ct_() - lvt_11_2_.x * 1.0D;
                  double lvt_14_1_ = this.dragon.field_70986_h.func_226283_e_(0.5D) + 0.5D;
                  double lvt_16_1_ = this.dragon.field_70986_h.func_226281_cx_() - lvt_11_2_.z * 1.0D;
                  double lvt_18_1_ = this.attackTarget.func_226277_ct_() - lvt_12_1_;
                  double lvt_20_1_ = this.attackTarget.func_226283_e_(0.5D) - lvt_14_1_;
                  double lvt_22_1_ = this.attackTarget.func_226281_cx_() - lvt_16_1_;
                  this.dragon.world.playEvent((PlayerEntity)null, 1017, new BlockPos(this.dragon), 0);
                  DragonFireballEntity lvt_24_1_ = new DragonFireballEntity(this.dragon.world, this.dragon, lvt_18_1_, lvt_20_1_, lvt_22_1_);
                  lvt_24_1_.setLocationAndAngles(lvt_12_1_, lvt_14_1_, lvt_16_1_, 0.0F, 0.0F);
                  this.dragon.world.addEntity(lvt_24_1_);
                  this.fireballCharge = 0;
                  if (this.currentPath != null) {
                     while(!this.currentPath.isFinished()) {
                        this.currentPath.incrementPathIndex();
                     }
                  }

                  this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
               }
            } else if (this.fireballCharge > 0) {
               --this.fireballCharge;
            }
         } else if (this.fireballCharge > 0) {
            --this.fireballCharge;
         }

      }
   }

   private void findNewTarget() {
      if (this.currentPath == null || this.currentPath.isFinished()) {
         int lvt_1_1_ = this.dragon.initPathPoints();
         int lvt_2_1_ = lvt_1_1_;
         if (this.dragon.getRNG().nextInt(8) == 0) {
            this.holdingPatternClockwise = !this.holdingPatternClockwise;
            lvt_2_1_ = lvt_1_1_ + 6;
         }

         if (this.holdingPatternClockwise) {
            ++lvt_2_1_;
         } else {
            --lvt_2_1_;
         }

         if (this.dragon.getFightManager() != null && this.dragon.getFightManager().getNumAliveCrystals() > 0) {
            lvt_2_1_ %= 12;
            if (lvt_2_1_ < 0) {
               lvt_2_1_ += 12;
            }
         } else {
            lvt_2_1_ -= 12;
            lvt_2_1_ &= 7;
            lvt_2_1_ += 12;
         }

         this.currentPath = this.dragon.findPath(lvt_1_1_, lvt_2_1_, (PathPoint)null);
         if (this.currentPath != null) {
            this.currentPath.incrementPathIndex();
         }
      }

      this.navigateToNextPathNode();
   }

   private void navigateToNextPathNode() {
      if (this.currentPath != null && !this.currentPath.isFinished()) {
         Vec3d lvt_1_1_ = this.currentPath.getCurrentPos();
         this.currentPath.incrementPathIndex();
         double lvt_2_1_ = lvt_1_1_.x;
         double lvt_6_1_ = lvt_1_1_.z;

         double lvt_4_1_;
         do {
            lvt_4_1_ = lvt_1_1_.y + (double)(this.dragon.getRNG().nextFloat() * 20.0F);
         } while(lvt_4_1_ < lvt_1_1_.y);

         this.targetLocation = new Vec3d(lvt_2_1_, lvt_4_1_, lvt_6_1_);
      }

   }

   public void initPhase() {
      this.fireballCharge = 0;
      this.targetLocation = null;
      this.currentPath = null;
      this.attackTarget = null;
   }

   public void setTarget(LivingEntity p_188686_1_) {
      this.attackTarget = p_188686_1_;
      int lvt_2_1_ = this.dragon.initPathPoints();
      int lvt_3_1_ = this.dragon.getNearestPpIdx(this.attackTarget.func_226277_ct_(), this.attackTarget.func_226278_cu_(), this.attackTarget.func_226281_cx_());
      int lvt_4_1_ = MathHelper.floor(this.attackTarget.func_226277_ct_());
      int lvt_5_1_ = MathHelper.floor(this.attackTarget.func_226281_cx_());
      double lvt_6_1_ = (double)lvt_4_1_ - this.dragon.func_226277_ct_();
      double lvt_8_1_ = (double)lvt_5_1_ - this.dragon.func_226281_cx_();
      double lvt_10_1_ = (double)MathHelper.sqrt(lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_);
      double lvt_12_1_ = Math.min(0.4000000059604645D + lvt_10_1_ / 80.0D - 1.0D, 10.0D);
      int lvt_14_1_ = MathHelper.floor(this.attackTarget.func_226278_cu_() + lvt_12_1_);
      PathPoint lvt_15_1_ = new PathPoint(lvt_4_1_, lvt_14_1_, lvt_5_1_);
      this.currentPath = this.dragon.findPath(lvt_2_1_, lvt_3_1_, lvt_15_1_);
      if (this.currentPath != null) {
         this.currentPath.incrementPathIndex();
         this.navigateToNextPathNode();
      }

   }

   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<StrafePlayerPhase> getType() {
      return PhaseType.STRAFE_PLAYER;
   }
}
