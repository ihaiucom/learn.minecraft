package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChargingPlayerPhase extends Phase {
   private static final Logger LOGGER = LogManager.getLogger();
   private Vec3d targetLocation;
   private int timeSinceCharge;

   public ChargingPlayerPhase(EnderDragonEntity p_i46793_1_) {
      super(p_i46793_1_);
   }

   public void serverTick() {
      if (this.targetLocation == null) {
         LOGGER.warn("Aborting charge player as no target was set.");
         this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      } else if (this.timeSinceCharge > 0 && this.timeSinceCharge++ >= 10) {
         this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      } else {
         double lvt_1_1_ = this.targetLocation.squareDistanceTo(this.dragon.func_226277_ct_(), this.dragon.func_226278_cu_(), this.dragon.func_226281_cx_());
         if (lvt_1_1_ < 100.0D || lvt_1_1_ > 22500.0D || this.dragon.collidedHorizontally || this.dragon.collidedVertically) {
            ++this.timeSinceCharge;
         }

      }
   }

   public void initPhase() {
      this.targetLocation = null;
      this.timeSinceCharge = 0;
   }

   public void setTarget(Vec3d p_188668_1_) {
      this.targetLocation = p_188668_1_;
   }

   public float getMaxRiseOrFall() {
      return 3.0F;
   }

   @Nullable
   public Vec3d getTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<ChargingPlayerPhase> getType() {
      return PhaseType.CHARGING_PLAYER;
   }
}
