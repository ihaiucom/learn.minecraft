package net.minecraft.entity.boss.dragon.phase;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class LandingPhase extends Phase {
   private Vec3d targetLocation;

   public LandingPhase(EnderDragonEntity p_i46788_1_) {
      super(p_i46788_1_);
   }

   public void clientTick() {
      Vec3d lvt_1_1_ = this.dragon.getHeadLookVec(1.0F).normalize();
      lvt_1_1_.rotateYaw(-0.7853982F);
      double lvt_2_1_ = this.dragon.field_70986_h.func_226277_ct_();
      double lvt_4_1_ = this.dragon.field_70986_h.func_226283_e_(0.5D);
      double lvt_6_1_ = this.dragon.field_70986_h.func_226281_cx_();

      for(int lvt_8_1_ = 0; lvt_8_1_ < 8; ++lvt_8_1_) {
         Random lvt_9_1_ = this.dragon.getRNG();
         double lvt_10_1_ = lvt_2_1_ + lvt_9_1_.nextGaussian() / 2.0D;
         double lvt_12_1_ = lvt_4_1_ + lvt_9_1_.nextGaussian() / 2.0D;
         double lvt_14_1_ = lvt_6_1_ + lvt_9_1_.nextGaussian() / 2.0D;
         Vec3d lvt_16_1_ = this.dragon.getMotion();
         this.dragon.world.addParticle(ParticleTypes.DRAGON_BREATH, lvt_10_1_, lvt_12_1_, lvt_14_1_, -lvt_1_1_.x * 0.07999999821186066D + lvt_16_1_.x, -lvt_1_1_.y * 0.30000001192092896D + lvt_16_1_.y, -lvt_1_1_.z * 0.07999999821186066D + lvt_16_1_.z);
         lvt_1_1_.rotateYaw(0.19634955F);
      }

   }

   public void serverTick() {
      if (this.targetLocation == null) {
         this.targetLocation = new Vec3d(this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION));
      }

      if (this.targetLocation.squareDistanceTo(this.dragon.func_226277_ct_(), this.dragon.func_226278_cu_(), this.dragon.func_226281_cx_()) < 1.0D) {
         ((FlamingSittingPhase)this.dragon.getPhaseManager().getPhase(PhaseType.SITTING_FLAMING)).resetFlameCount();
         this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
      }

   }

   public float getMaxRiseOrFall() {
      return 1.5F;
   }

   public float getYawFactor() {
      float lvt_1_1_ = MathHelper.sqrt(Entity.func_213296_b(this.dragon.getMotion())) + 1.0F;
      float lvt_2_1_ = Math.min(lvt_1_1_, 40.0F);
      return lvt_2_1_ / lvt_1_1_;
   }

   public void initPhase() {
      this.targetLocation = null;
   }

   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<LandingPhase> getType() {
      return PhaseType.LANDING;
   }
}
