package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;

public class LivingSetAttackTargetEvent extends LivingEvent {
   private final LivingEntity target;

   public LivingSetAttackTargetEvent(LivingEntity entity, LivingEntity target) {
      super(entity);
      this.target = target;
   }

   public LivingEntity getTarget() {
      return this.target;
   }
}
