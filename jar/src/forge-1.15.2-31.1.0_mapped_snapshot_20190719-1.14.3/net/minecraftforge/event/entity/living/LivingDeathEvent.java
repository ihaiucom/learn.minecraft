package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class LivingDeathEvent extends LivingEvent {
   private final DamageSource source;

   public LivingDeathEvent(LivingEntity entity, DamageSource source) {
      super(entity);
      this.source = source;
   }

   public DamageSource getSource() {
      return this.source;
   }
}
