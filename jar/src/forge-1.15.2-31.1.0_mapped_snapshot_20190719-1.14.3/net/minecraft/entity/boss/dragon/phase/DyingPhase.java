package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class DyingPhase extends Phase {
   private Vec3d targetLocation;
   private int time;

   public DyingPhase(EnderDragonEntity p_i46792_1_) {
      super(p_i46792_1_);
   }

   public void clientTick() {
      if (this.time++ % 10 == 0) {
         float lvt_1_1_ = (this.dragon.getRNG().nextFloat() - 0.5F) * 8.0F;
         float lvt_2_1_ = (this.dragon.getRNG().nextFloat() - 0.5F) * 4.0F;
         float lvt_3_1_ = (this.dragon.getRNG().nextFloat() - 0.5F) * 8.0F;
         this.dragon.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.dragon.func_226277_ct_() + (double)lvt_1_1_, this.dragon.func_226278_cu_() + 2.0D + (double)lvt_2_1_, this.dragon.func_226281_cx_() + (double)lvt_3_1_, 0.0D, 0.0D, 0.0D);
      }

   }

   public void serverTick() {
      ++this.time;
      if (this.targetLocation == null) {
         BlockPos lvt_1_1_ = this.dragon.world.getHeight(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION);
         this.targetLocation = new Vec3d((double)lvt_1_1_.getX(), (double)lvt_1_1_.getY(), (double)lvt_1_1_.getZ());
      }

      double lvt_1_2_ = this.targetLocation.squareDistanceTo(this.dragon.func_226277_ct_(), this.dragon.func_226278_cu_(), this.dragon.func_226281_cx_());
      if (lvt_1_2_ >= 100.0D && lvt_1_2_ <= 22500.0D && !this.dragon.collidedHorizontally && !this.dragon.collidedVertically) {
         this.dragon.setHealth(1.0F);
      } else {
         this.dragon.setHealth(0.0F);
      }

   }

   public void initPhase() {
      this.targetLocation = null;
      this.time = 0;
   }

   public float getMaxRiseOrFall() {
      return 3.0F;
   }

   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<DyingPhase> getType() {
      return PhaseType.DYING;
   }
}
