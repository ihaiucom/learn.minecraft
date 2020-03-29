package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.MobEntity;

public class SwimGoal extends Goal {
   private final MobEntity entity;

   public SwimGoal(MobEntity p_i1624_1_) {
      this.entity = p_i1624_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP));
      p_i1624_1_.getNavigator().setCanSwim(true);
   }

   public boolean shouldExecute() {
      double lvt_1_1_ = (double)this.entity.getEyeHeight() < 0.4D ? 0.2D : 0.4D;
      return this.entity.isInWater() && this.entity.getSubmergedHeight() > lvt_1_1_ || this.entity.isInLava();
   }

   public void tick() {
      if (this.entity.getRNG().nextFloat() < 0.8F) {
         this.entity.getJumpController().setJumping();
      }

   }
}
